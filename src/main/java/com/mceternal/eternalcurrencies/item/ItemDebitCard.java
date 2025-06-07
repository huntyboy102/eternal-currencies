package com.mceternal.eternalcurrencies.item;

import com.mceternal.eternalcurrencies.api.capability.ICurrencies;
import com.mceternal.eternalcurrencies.api.capability.ReferenceCurrencyHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ItemDebitCard extends Item {

    public static final String KEY_ACCOUNT_ID = "account_uuid";

    public ItemDebitCard(Properties pProperties) {
        super(pProperties.stacksTo(1).fireResistant());
    }

    public static void bindCard(ItemStack stack, UUID holderAddress) {
        stack.getOrCreateTag().putUUID(KEY_ACCOUNT_ID, holderAddress);
    }

    public static void connectUser(ICapabilityProvider provider, UUID holderAddress) {
        provider.getCapability(ICurrencies.CAPABILITY).ifPresent(currencies -> {
            if(currencies instanceof ReferenceCurrencyHolder referenceHolder
                && referenceHolder.getReference() != holderAddress)
                referenceHolder.updateReference(holderAddress);
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(level.isClientSide)
            return InteractionResultHolder.pass(stack);

        if(stack.hasTag()
                && stack.getTag().contains(KEY_ACCOUNT_ID)) {
            UUID accountID = stack.getTag().getUUID(KEY_ACCOUNT_ID);
            LazyOptional<ICurrencies> currencies = player.getCapability(ICurrencies.CAPABILITY);
            if(currencies.isPresent()
                    && currencies.resolve().get() instanceof ReferenceCurrencyHolder referenceHolder) {
                //EternalCurrencies.LOGGER.info("Debit Card account connection. Card account ID: {}, user account ID: {}, Is the same: {}",
                //        accountID, referenceHolder.getReference(), referenceHolder.getReference().equals(accountID));
                if(!referenceHolder.getReference().equals(accountID)) {
                    connectUser(player, accountID);
                    player.sendSystemMessage(Component.translatable("item.eternalcurrencies.debit_card.connected_you_to_account", accountID));
                    return InteractionResultHolder.success(stack);
                } else {
                    player.sendSystemMessage(Component.translatable("item.eternalcurrencies.debit_card.already_connected_to_this_account", accountID));
                    return InteractionResultHolder.pass(stack);
                }
            }
        } else {
            LazyOptional<ICurrencies> currencies = player.getCapability(ICurrencies.CAPABILITY);
            if(currencies.isPresent()
                    && currencies.resolve().get() instanceof ReferenceCurrencyHolder referenceHolder) {
                UUID accountID = referenceHolder.getReference();
                bindCard(stack, accountID);
                player.sendSystemMessage(Component.translatable("item.eternalcurrencies.debit_card.connected_card_to_account", accountID));
                return InteractionResultHolder.success(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltipLines, TooltipFlag pIsAdvanced) {
        if(stack.hasTag() && stack.getTag().contains(KEY_ACCOUNT_ID)) {
            UUID accountID = stack.getTag().getUUID(KEY_ACCOUNT_ID);
            tooltipLines.add(Component.translatable("item.eternalcurrencies.debit_card.tooltip", accountID.toString()));
        }
    }
}
