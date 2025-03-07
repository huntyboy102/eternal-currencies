package com.mceternal.eternalcurrencies.command;

import com.mceternal.eternalcurrencies.EternalCurrencies;
import com.mceternal.eternalcurrencies.api.EternalCurrenciesAPI;
import com.mceternal.eternalcurrencies.data.CurrencyType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class EternalCurrenciesCommands {

    private static final DynamicCommandExceptionType ERROR_INVALID_CURRENCY = new DynamicCommandExceptionType( //TODO do we actually need this?
            (currencyType) -> Component.translatable(
                    "commands.eternalcurrencies.invalid_currency", currencyType
            ));

    private static final SuggestionProvider<CommandSourceStack> SUGGEST_CURRENCIES = (context, suggestions) ->
            SharedSuggestionProvider.suggest(EternalCurrenciesAPI.getRegisteredCurrencies().stream()
                .map(currency -> currency.type().toString()), suggestions);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        EternalCurrencies.LOGGER.info("Fired Command Registry!");
        //TODO: addbalance, removebalance, checkbalance, and pay commands. also consider if these should be individual commands, or subcommands.

        //Set Balance
        dispatcher.register(withCurrencyArgs(Commands.literal("setbalance"))
                        .executes(context -> setBalance(
                                ResourceLocationArgument.getId(context, "currency"),
                                LongArgumentType.getLong(context, "amount"),
                                context.getSource().getPlayer())));

        dispatcher.register(Commands.literal("listcurrencies")
                .executes(commandContext -> {
                    for (CurrencyType registeredCurrency : EternalCurrenciesAPI.getRegisteredCurrencies()) {
                        commandContext.getSource().sendSystemMessage(Component.literal("identifier="+ registeredCurrency.type().toString() +" icon="+registeredCurrency.icon()));
                    }
                    return Command.SINGLE_SUCCESS;
                }));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> withCurrencyArgs(LiteralArgumentBuilder<CommandSourceStack> command) {
        return command
                .requires(player -> player.hasPermission(2))
                .then(Commands.argument("currency", StringArgumentType.string())
                        .suggests(SUGGEST_CURRENCIES)
                        .then(Commands.argument("amount", LongArgumentType.longArg())));
    }


    private static int setBalance(ResourceLocation type, long amount, ServerPlayer targetPlayer) {
        EternalCurrenciesAPI.setBalanceFor(targetPlayer, type, amount);
        return Command.SINGLE_SUCCESS;
    }
}
