package com.mceternal.eternalcurrencies.command;

import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import com.mceternal.eternalcurrencies.api.capability.ICurrencies;
import com.mceternal.eternalcurrencies.api.capability.ReferenceCurrencyHolder;
import com.mceternal.eternalcurrencies.data.CurrencyData;
import com.mceternal.eternalcurrencies.data.CurrencySavedData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class EternalCurrenciesCommands {

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_CURRENCIES = (context, suggestions) ->
            SharedSuggestionProvider.suggest(EternalCurrenciesAPI.getRegisteredCurrencies(context.getSource().getLevel().registryAccess()).keySet()
                    .stream().map(ResourceLocation::toString), suggestions);

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_REFERRABLE_HOLDERS = (context, suggestions) ->
            SharedSuggestionProvider.suggest(CurrencySavedData.getFromServer(context.getSource().getServer()).getAllAddresses()
                    .stream().map(UUID::toString), suggestions);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        //EternalCurrencies.LOGGER.info("Fired Command Registry!");
        //TODO: consider if these should be individual commands, or subcommands.

        LiteralCommandNode<CommandSourceStack> ecCommand = dispatcher.register(Commands.literal("eternalcurrencies")
                //Set Balance
                .then(Commands.literal("setbalance")
                        .requires(command -> command.hasPermission(2))
                        .then(executesWithCurrencyAndPlayer(
                                (context, player) -> setBalance(
                                        ResourceLocationArgument.getId(context, "currency"),
                                        LongArgumentType.getLong(context, "amount"),
                                        player,
                                        context
                                ))
                        ))
                //Add to Balance
                .then(Commands.literal("addcurrency")
                        .requires(command -> command.hasPermission(2))
                        .then(executesWithCurrencyAndPlayer(
                                (context, player) -> addCurrency(
                                        ResourceLocationArgument.getId(context, "currency"),
                                        LongArgumentType.getLong(context, "amount"),
                                        player,
                                        context
                                ))
                        ))
                //Remove from Balance
                .then(Commands.literal("removecurrency")
                        .requires(command -> command.hasPermission(2))
                        .then(executesWithCurrencyAndPlayer(
                                (context, player) -> removeCurrency(
                                        ResourceLocationArgument.getId(context, "currency"),
                                        LongArgumentType.getLong(context, "amount"),
                                        player,
                                        context
                                )
                        )))
                //Get Balance
                .then(Commands.literal("checkbalance")
                        .then(Commands.argument("currency", ResourceLocationArgument.id())
                                .suggests(SUGGEST_CURRENCIES)
                                .executes(context -> getBalance(
                                        ResourceLocationArgument.getId(context, "currency"),
                                        context.getSource().getPlayer(),
                                        context
                                ))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .requires(command -> command.hasPermission(2))
                                        .executes(context -> getBalance(
                                                ResourceLocationArgument.getId(context, "currency"),
                                                EntityArgument.getPlayer(context, "player"),
                                                context
                                        ))
                                )
                        ))
                //Pay
                .then(Commands.literal("pay")
                        .requires(CommandSourceStack::isPlayer)
                        .then(withCurrencyAndAmount(
                                Commands.argument("player", EntityArgument.player())
                                        .executes(context -> pay(
                                                ResourceLocationArgument.getId(context, "currency"),
                                                LongArgumentType.getLong(context, "amount"),
                                                EntityArgument.getPlayer(context, "player"),
                                                context.getSource().getPlayer()
                                        ))
                        )))
                //Debug - list currencies
                .then(Commands.literal("listcurrencies")
                        .requires(command -> command.hasPermission(2))
                        .executes(commandContext -> {
                            EternalCurrenciesAPI.getRegisteredCurrencies(commandContext.getSource().getLevel().registryAccess()).forEach((currency, data) ->
                                    commandContext.getSource().sendSystemMessage(Component.literal("identifier="+ currency +" icon="+ data.icon())));
                            return Command.SINGLE_SUCCESS;
                        }))
                //Create a new DirectCurrencyHolder on CurrencySavedData for ReferenceCurrencyHolders
                .then(Commands.literal("createreferrableholder")
                        .requires(command -> command.hasPermission(2))
                        .executes(context -> createReferrableHolder(
                                UUID.randomUUID(),
                                context
                        ))
                        .then(Commands.argument("address", UuidArgument.uuid())
                                .suggests((context, suggestions) ->
                                        SharedSuggestionProvider.suggest(new String[]{UUID.randomUUID().toString()}, suggestions))
                                .executes(context -> createReferrableHolder(
                                        UuidArgument.getUuid(context, "address"),
                                        context
                                ))))
                //Debug - List CurrencySavedData map UUIDs
                .then(Commands.literal("listreferrableholders")
                        .requires(command -> command.hasPermission(2))
                        .executes(EternalCurrenciesCommands::listReferrableHolders)
                )
                //Set linked Account for target player (or source/executor, if player)
                .then(Commands.literal("setholderaddress")
                        .requires(command -> command.hasPermission(2))
                        .then(Commands.argument("address", UuidArgument.uuid())
                                .suggests(SUGGEST_REFERRABLE_HOLDERS)
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> setHolderAddress(
                                                EntityArgument.getPlayer(context, "player"),
                                                context
                                        )))
                                .requires(CommandSourceStack::isPlayer)
                                .executes(context -> setHolderAddress(
                                        context.getSource().getPlayer(),
                                        context
                                ))
                        ))
                //Reset referenced Balance ID to original
                .then(Commands.literal("resetreference")
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(command -> command.hasPermission(2))
                                .executes(context -> resetReference(
                                        EntityArgument.getPlayer(context, "player"),
                                        context
                                )))
                        .requires(CommandSourceStack::isPlayer)
                        .executes(context -> resetReference(
                                context.getSource().getPlayer(),
                                context
                        ))
                )
        );

        //"ec" Alias
        dispatcher.register(Commands.literal("ec").redirect(ecCommand));
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, ResourceLocation> withCurrencyAndAmount(
            ArgumentBuilder<CommandSourceStack, RequiredArgumentBuilder<CommandSourceStack, T>> builder
    ) {
        return Commands.argument("currency", ResourceLocationArgument.id())
                .suggests(SUGGEST_CURRENCIES)
                .then(Commands.argument("amount", LongArgumentType.longArg())
                        .then(builder));
    }

    public static ArgumentBuilder<CommandSourceStack, RequiredArgumentBuilder<CommandSourceStack, ResourceLocation>> executesWithCurrencyAndPlayer(
            BiFunction<CommandContext<CommandSourceStack>, ServerPlayer, Integer> executor
    ) {
        return Commands.argument("currency", ResourceLocationArgument.id())
                .suggests(SUGGEST_CURRENCIES)
                .then(Commands.argument("amount", LongArgumentType.longArg())
                        .executes(context ->
                                executeIfCurrencyExists(context, executor, () -> context.getSource().getPlayer()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    return executeIfCurrencyExists(context, executor, () -> player);
                                })));
    }

    public static int executeIfCurrencyExists(
            CommandContext<CommandSourceStack> context,
            BiFunction<CommandContext<CommandSourceStack>, ServerPlayer, Integer> executor,
            Supplier<ServerPlayer> player
    ) {
        ResourceLocation currency = ResourceLocationArgument.getId(context, "currency");
        if(EternalCurrenciesAPI.getRegisteredCurrencies(context.getSource().getLevel().registryAccess()).containsKey(currency))
            return executor.apply(context, player.get());

        context.getSource().sendFailure(Component.translatable("commands.eternalcurrencies.error.unregistered_currency",
                currency));
        return 0;
    }


    private static int setBalance(ResourceLocation currency, long amount, ServerPlayer targetPlayer, CommandContext<CommandSourceStack> context) {
        EternalCurrenciesAPI.setBalanceFor(targetPlayer, currency, amount);
        context.getSource().sendSuccess(() -> Component.translatable("commands.eternalcurrencies.setbalance.result",
                EternalCurrenciesAPI.getCurrencyTranslationComponent(currency), amount, targetPlayer.getGameProfile().getName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int addCurrency(ResourceLocation currency, long amount, ServerPlayer targetPlayer, CommandContext<CommandSourceStack> context) {
        EternalCurrenciesAPI.addBalanceFor(targetPlayer, currency, amount);
        context.getSource().sendSuccess(() -> Component.translatable("commands.eternalcurrencies.addcurrency.result",
                        EternalCurrenciesAPI.getCurrencyTranslationComponent(currency), amount, targetPlayer.getGameProfile().getName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int removeCurrency(ResourceLocation currency, long amount, ServerPlayer targetPlayer, CommandContext<CommandSourceStack> context) {
        EternalCurrenciesAPI.takeAnywayFor(targetPlayer, currency, amount);
        context.getSource().sendSuccess(() -> Component.translatable("commands.eternalcurrencies.removecurrency.result",
                        EternalCurrenciesAPI.getCurrencyTranslationComponent(currency), amount, targetPlayer.getGameProfile().getName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int getBalance(ResourceLocation currency, ServerPlayer targetPlayer, CommandContext<CommandSourceStack> context) {
        long amount = EternalCurrenciesAPI.getBalanceFor(targetPlayer, currency);
        context.getSource().sendSuccess(() -> Component.translatable("commands.eternalcurrencies.checkbalance.result",
                EternalCurrenciesAPI.getCurrencyTranslationComponent(currency), amount, targetPlayer.getGameProfile().getName()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int pay(ResourceLocation currency, long amount, ServerPlayer playerTo, ServerPlayer senderPlayer) {
        CurrencyData currencyDef = EternalCurrenciesAPI.getCurrencyData(currency, senderPlayer.level().registryAccess());
        if(currencyDef == null
                ||!currencyDef.transferable()) {
            senderPlayer.sendSystemMessage(Component.translatable("commands.eternalcurrencies.pay.untransferable_currency"));
            return 0;
        }

        if(EternalCurrenciesAPI.takeBalanceFor(senderPlayer, currency, amount)) {
            EternalCurrenciesAPI.addBalanceFor(playerTo, currency, amount);
            senderPlayer.sendSystemMessage(Component.translatable("commands.eternalcurrencies.pay.success",
                    EternalCurrenciesAPI.getCurrencyTranslationComponent(currency), amount, playerTo.getGameProfile().getName()));
            if(senderPlayer != playerTo)
                playerTo.sendSystemMessage(Component.translatable("commands.eternalcurrencies.pay.receive",
                        EternalCurrenciesAPI.getCurrencyTranslationComponent(currency), amount, senderPlayer.getGameProfile().getName()));

            return Command.SINGLE_SUCCESS;
        }
        senderPlayer.sendSystemMessage(Component.translatable("commands.eternalcurrencies.pay.insufficient_balance",
                EternalCurrenciesAPI.getCurrencyTranslationComponent(currency), amount, playerTo.getGameProfile().getName(), EternalCurrenciesAPI.getBalanceFor(senderPlayer, currency)));
        return 0;
    }

    private static int createReferrableHolder(UUID holderAddress, CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        MinecraftServer server = source.getServer();
        CurrencySavedData currencySavedData = CurrencySavedData.getFromServer(server);
        if(!currencySavedData.doesAddressExist(holderAddress)) {
            source.sendSuccess(() -> Component.translatable("commands.eternalcurrencies.createreferrableholder.success", holderAddress.toString()), true);
            currencySavedData.getOrCreate(holderAddress);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendSuccess(() -> Component.translatable("commands.eternalcurrencies.createreferrableholder.address_already_exists", holderAddress.toString()), true);
            return 0;
        }
    }

    private static int listReferrableHolders(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        CurrencySavedData.getFromServer(source.getServer()).getAllAddresses().forEach(address ->
                source.sendSystemMessage(Component.literal(address.toString())));
        return Command.SINGLE_SUCCESS;
    }

    private static int setHolderAddress(ServerPlayer player, CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        UUID address = UuidArgument.getUuid(context, "address");
        CurrencySavedData currencySavedData = CurrencySavedData.getFromServer(source.getServer());
        LazyOptional<ICurrencies> currencies = player.getCapability(ICurrencies.CAPABILITY);
        if(currencySavedData.doesAddressExist(address)) {
            if (currencies.isPresent() && currencies.resolve().get() instanceof ReferenceCurrencyHolder referenceHolder) {
                if(!referenceHolder.getReference().equals(address)) {
                    referenceHolder.updateReference(address);
                    source.sendSystemMessage(Component.translatable("commands.eternalcurrencies.setholderaddress.success",
                            address.toString(), player.getGameProfile().getName()));
                    return Command.SINGLE_SUCCESS;
                }
                source.sendSystemMessage(Component.translatable("commands.eternalcurrencies.setholderaddress.already_set_to_this",
                        address, player.getGameProfile().getName()));
                return 0;
            }
            source.sendFailure(Component.translatable("commands.eternalcurrencies.setholderaddress.target_does_not_have_reference_capability"));
            return 0;
        }
        source.sendFailure(Component.translatable("commands.eternalcurrencies.setholderaddress.address_does_not_exist", address));
        return 0;
    }

    private static int resetReference(ServerPlayer player, CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        LazyOptional<ICurrencies> lazyCurrencies = player.getCapability(ICurrencies.CAPABILITY);

        if(lazyCurrencies.isPresent()
                && lazyCurrencies.resolve().get() instanceof ReferenceCurrencyHolder referenceHolder) {
            UUID playerUUID = player.getUUID();
            if(referenceHolder.getReference().equals(playerUUID)) {
                source.sendSystemMessage(Component.translatable("commands.eternalcurrencies.resetreference.already_default", player.getGameProfile().getName()));
                return 0;
            }
            referenceHolder.updateReference(playerUUID);
            source.sendSuccess(() -> Component.translatable("commands.eternalcurrencies.resetreference.success", player.getGameProfile().getName()), true);
            return Command.SINGLE_SUCCESS;
        }

        source.sendFailure(Component.translatable("commands.eternalcurrencies.resetreference.failure", player.getGameProfile().getName()));
        return 0;
    }
}
