package com.mceternal.eternalcurrencies.command;

import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiFunction;

public class EternalCurrenciesCommands {

    private static final DynamicCommandExceptionType ERROR_INVALID_CURRENCY = new DynamicCommandExceptionType( //TODO do we actually need this?
            (currencyType) -> Component.translatable(
                    "commands.eternalcurrencies.invalid_currency", currencyType
            ));

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_CURRENCIES = (context, suggestions) ->
            SharedSuggestionProvider.suggest(EternalCurrenciesAPI.getRegisteredCurrencies().keySet()
                    .stream().map(ResourceLocation::toString), suggestions);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        //EternalCurrencies.LOGGER.info("Fired Command Registry!");
        //TODO: addbalance, removebalance, checkbalance, and pay commands. also consider if these should be individual commands, or subcommands.

        //Set Balance
        dispatcher.register(Commands.literal("setbalance").then(
                withCurrencyArgs((context, player) -> setBalance(
                        ResourceLocationArgument.getId(context, "currency"),
                        LongArgumentType.getLong(context, "amount"),
                        player,
                        context
                ))
        ));

        dispatcher.register(Commands.literal("listcurrencies")
                .executes(commandContext -> {
                    EternalCurrenciesAPI.getRegisteredCurrencies().forEach((location, currencyType) ->
                            commandContext.getSource().sendSystemMessage(Component.literal("identifier="+ location +" icon="+currencyType.icon())));
                    return Command.SINGLE_SUCCESS;
                }));
    }

    public static ArgumentBuilder<CommandSourceStack, RequiredArgumentBuilder<CommandSourceStack, ResourceLocation>> withCurrencyArgs(
            BiFunction<CommandContext<CommandSourceStack>, ServerPlayer, Integer> executor) { //TODO separate into "withCurrencyArgs" and "withDefaultOrSpecifiedPlayer" for commands which *can* target a player.
        return Commands.argument("currency", ResourceLocationArgument.id())
                .suggests(SUGGEST_CURRENCIES)
                .then(Commands.argument("amount", LongArgumentType.longArg())
                        .executes(context -> executor.apply(context, context.getSource().getPlayer()))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> executor.apply(context, EntityArgument.getPlayer(context, "player")))));
    }

    private static int setBalance(ResourceLocation type, long amount, ServerPlayer targetPlayer, CommandContext<CommandSourceStack> context) {
        EternalCurrenciesAPI.setBalanceFor(targetPlayer, type, amount);
        context.getSource().sendSystemMessage(
                Component.literal("set Balance of "+ type.toString() +" for "+ targetPlayer.getGameProfile().getName() +" to "+ amount));
        return Command.SINGLE_SUCCESS;
    }
}
