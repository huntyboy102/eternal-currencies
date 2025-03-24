package com.mceternal.eternalcurrencies.item;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ItemCurrencyCheque extends Item {

    public static final String KEY_CURRENCY_TAG = "currencies";
    public static final String KEY_CURRENCY_TYPE = "currency";
    public static final String KEY_CURRENCY_AMOUNT = "amount";

    public ItemCurrencyCheque(Properties pProperties) {
        super(pProperties);
    }

    public static List<ItemStack> getVariantForEachCurrency() {
        List<ItemStack> variants = new ArrayList<>();
        EternalCurrenciesAPI.getRegisteredCurrencies().forEach((identifier, currenyData) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString(KEY_CURRENCY_TYPE, identifier.toString());
            entry.putLong(KEY_CURRENCY_AMOUNT, currenyData.defaultAmount());

            ListTag entries = new ListTag();
            entries.add(entry);
            CompoundTag root = new CompoundTag();
            root.put(KEY_CURRENCY_TAG, entries);
            EternalCurrencies.LOGGER.info(root.toString());

            ItemStack variant = new ItemStack(EternalCurrenciesItems.CHEQUE.get());
            variant.setTag(root);
            variants.add(variant);
        });
        return variants;
    }

    //TODO add bank block and make sure this works
    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        BlockEntity be = context.getLevel().getBlockEntity(clickedPos);
        ItemStack held = context.getItemInHand();
        Player player = context.getPlayer();
        if(be != null
                && held.hasTag()
                && validateCurrencyRoot(held.getTag())) {
            redeem(held, be, player, context.getLevel());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        redeem(stack, player, level);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    //TODO sound on redeem, possibly specified in CurrencyData
    public static void redeem(ItemStack self, ICapabilityProvider target, Player player, Level level, boolean consumeAll) {
        if(!level.isClientSide && self.hasTag() && validateCurrencyRoot(self.getTag())) {
            int toTurnIn = consumeAll ? self.getCount() : 1;

            EternalCurrenciesAPI.getCurrencies(target).ifPresent(currencies -> {
                ListTag currencyTagList = self.getTag().getList(KEY_CURRENCY_TAG, 10);
                forCurrencyTag(currencyTagList, (currency, amount) -> {
                    currencies.add(currency, amount);
                    player.sendSystemMessage(Component.translatable("message.eternalcurrencies.added_currency",
                            EternalCurrenciesAPI.getCurrencyTranslationComponent(currency, currencies.getCurrency(currency)), amount));
                });
            });
            if(!player.getAbilities().instabuild)
                self.shrink(toTurnIn);
        }
    }

    public static void redeem(ItemStack self, ICapabilityProvider target, Player player, Level level) {
        redeem(self, target, player, level, player.isCrouching());
    }

    public static void redeem(ItemStack self, Player player, Level level) {
        redeem(self, player, player, level);
    }

    public static boolean validateCurrencyRoot(CompoundTag root) {
        return root.contains(KEY_CURRENCY_TAG)
                && root.get(KEY_CURRENCY_TAG) instanceof ListTag;
    }

    public static boolean validateCurrencyTag(CompoundTag tag) {
        return tag != null
                && (tag.contains(KEY_CURRENCY_TYPE)
                    && tag.get(KEY_CURRENCY_TYPE) instanceof StringTag)
                && (tag.contains(KEY_CURRENCY_AMOUNT)
                    && tag.get(KEY_CURRENCY_AMOUNT) instanceof NumericTag);
    }

    public static void forCurrencyTag(ListTag currencyTagList, BiConsumer<ResourceLocation, Long> contentConsumer) {
        currencyTagList.forEach(tag -> {
            if (tag instanceof CompoundTag currencyTag && validateCurrencyTag(currencyTag)) {
                ResourceLocation currency = new ResourceLocation(currencyTag.getString(KEY_CURRENCY_TYPE));
                long amount = currencyTag.getLong(KEY_CURRENCY_AMOUNT);
                if(EternalCurrenciesAPI.getRegisteredCurrencies().containsKey(currency))
                    contentConsumer.accept(currency, amount);
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipLines, TooltipFlag isAdvanced) {
        if(stack.hasTag() && validateCurrencyRoot(stack.getTag())) {
            ListTag currencyRoot = stack.getTag().getList(KEY_CURRENCY_TAG, 10);
            currencyRoot.forEach(tag -> {
                if (tag instanceof CompoundTag entryTag && validateCurrencyTag(entryTag)) {
                    ResourceLocation currency = new ResourceLocation(entryTag.getString(KEY_CURRENCY_TYPE));
                    if(EternalCurrenciesAPI.getRegisteredCurrencies().containsKey(currency))
                        tooltipLines.add(EternalCurrenciesAPI.getCurrencyTranslationComponent(
                                currency,
                                entryTag.getLong(KEY_CURRENCY_AMOUNT)));
                }
            });

        }
    }
}
