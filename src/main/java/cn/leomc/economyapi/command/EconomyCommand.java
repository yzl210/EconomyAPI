package cn.leomc.economyapi.command;

import cn.leomc.economyapi.EconomyAPI;
import cn.leomc.economyapi.EconomyException;
import cn.leomc.economyapi.capability.IPlayerEconomy;
import cn.leomc.economyapi.command.argument.BigIntegerArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;

public class EconomyCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("economy")
                        .then(Commands.literal("add")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(Commands.argument("amount", BigIntegerArgumentType.bigIntegerArg())
                                        .executes(context -> addEconomyToPlayer(BigIntegerArgumentType.getBigInteger(context, "amount"), context.getSource()))
                                        .then(Commands.argument("players", EntityArgument.players())
                                                .executes(context -> addEconomyToPlayer(BigIntegerArgumentType.getBigInteger(context, "amount"), EntityArgument.getPlayers(context, "players"), context.getSource())))))
                        .then(Commands.literal("set")
                                .requires(source -> source.hasPermissionLevel(2))
                                .then(Commands.argument("amount", BigIntegerArgumentType.bigIntegerArg())
                                        .executes(context -> setPlayerEconomy(BigIntegerArgumentType.getBigInteger(context, "amount"), context.getSource()))
                                        .then(Commands.argument("players", EntityArgument.players())
                                                .executes(context -> setPlayerEconomy(BigIntegerArgumentType.getBigInteger(context, "amount"), EntityArgument.getPlayers(context, "players"), context.getSource())))))
                        .then(Commands.literal("query")
                                .executes(context -> queryEconomy(context.getSource()))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> queryEconomy(EntityArgument.getPlayer(context, "player"), context.getSource()))))
                        .then(Commands.literal("send")
                                .then(Commands.argument("amount", BigIntegerArgumentType.bigIntegerArg())
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(context -> sendPlayerEconomy(context.getSource(), BigIntegerArgumentType.getBigInteger(context, "amount"), EntityArgument.getPlayer(context, "player"), context.getInput(), false))
                                                .then(Commands.literal("confirm")
                                                        .executes(context -> sendPlayerEconomy(context.getSource(), BigIntegerArgumentType.getBigInteger(context, "amount"), EntityArgument.getPlayer(context, "player"), context.getInput(), true))))))

        );
    }


    public static int sendPlayerEconomy(CommandSource source, BigInteger amount, PlayerEntity receiver, String input, boolean confirm) throws CommandSyntaxException {

        try {

            if (receiver.equals(source.asPlayer())) {
                source.sendErrorMessage(new TranslationTextComponent("economyapi.command.send.self"));
                return 0;
            }

            IPlayerEconomy economy = EconomyAPI.getPlayerEconomyCapability(source.asPlayer());
            if (economy.getEconomy().compareTo(amount) < 0) {
                source.sendErrorMessage(new TranslationTextComponent("economyapi.command.send.notenough"));
                return 0;
            }


            if (!confirm) {
                String command = input + " confirm";
                IFormattableTextComponent textComponent = new TranslationTextComponent("economyapi.command.send.confirm", amount, receiver.getDisplayName(), command);
                textComponent.modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
                source.sendFeedback(textComponent, true);
                return Command.SINGLE_SUCCESS;
            }

            changePlayerEconomy(amount, Collections.singletonList(receiver), source, "add", false);
            economy.removeEconomy(amount);

            source.sendFeedback(new TranslationTextComponent("economyapi.command.send", amount, receiver.getDisplayName()), true);
            return Command.SINGLE_SUCCESS;
        } catch (EconomyException e) {
            source.sendErrorMessage(new TranslationTextComponent("economyapi.command.send.failed", amount, receiver.getDisplayName(), e.toString()));
            return 0;
        }
    }

    public static int setPlayerEconomy(BigInteger amount, CommandSource source) throws CommandSyntaxException {
        return setPlayerEconomy(amount, Collections.singletonList(source.asPlayer()), source);
    }


    public static int setPlayerEconomy(BigInteger amount, Collection<? extends PlayerEntity> targets, CommandSource source) {
        return changePlayerEconomy(amount, targets, source, "set", true);
    }

    public static int addEconomyToPlayer(BigInteger amount, CommandSource source) throws CommandSyntaxException {
        return addEconomyToPlayer(amount, Collections.singletonList(source.asPlayer()), source);
    }


    public static int addEconomyToPlayer(BigInteger amount, Collection<? extends PlayerEntity> targets, CommandSource source) {
        return changePlayerEconomy(amount, targets, source, "add", true);
    }

    public static int queryEconomy(CommandSource source) throws CommandSyntaxException {
        return queryEconomy(source.asPlayer(), source);
    }

    public static int queryEconomy(PlayerEntity player, CommandSource source) {
        source.sendFeedback(new TranslationTextComponent("economyapi.command.query", player.getDisplayName(), EconomyAPI.getPlayerEconomyCapability(player).getEconomy()), true);
        return Command.SINGLE_SUCCESS;
    }

    public static int changePlayerEconomy(BigInteger amount, Collection<? extends PlayerEntity> targets, CommandSource source, String action, boolean feedback) {
        int succeeded = 0;
        for (PlayerEntity player : targets) {
            try {
                IPlayerEconomy economy = EconomyAPI.getPlayerEconomyCapability(player);
                if ("set".equals(action))
                    economy.setEconomy(amount);
                else if ("add".equals(action))
                    economy.addEconomy(amount);
                else if ("remove".equals(action))
                    economy.removeEconomy(amount);
                succeeded++;
            } catch (EconomyException e) {
                e.printStackTrace();
                if (feedback)
                    source.sendErrorMessage(new TranslationTextComponent("economyapi.command." + action + ".failed", player.getDisplayName(), e.toString()));
                else
                    throw e;
            }
        }
        if (succeeded > 0 && feedback)
            source.sendFeedback(new TranslationTextComponent("economyapi.command." + action, succeeded, amount), true);
        return succeeded;
    }


}
