package cn.leomc.economyapi.command;

import cn.leomc.economyapi.EconomyAPI;
import cn.leomc.economyapi.capability.IPlayerEconomy;
import cn.leomc.economyapi.command.argument.BigIntegerArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;

public class EconomyCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                Commands.literal("economy")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(Commands.literal("add")
                                .then(Commands.argument("amount", BigIntegerArgumentType.bigIntegerArg())
                                        .executes(context -> addEconomyToPlayer(BigIntegerArgumentType.getBigInteger(context, "amount"), context.getSource()))
                                        .then(Commands.argument("players", EntityArgument.players())
                                                .executes(context -> addEconomyToPlayer(BigIntegerArgumentType.getBigInteger(context, "amount"), EntityArgument.getPlayers(context, "players"), context.getSource())))))
                        .then(Commands.literal("query")
                                .executes(context -> queryEconomy(context.getSource()))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> queryEconomy(EntityArgument.getPlayer(context, "player"), context.getSource()))))

        );
    }


    public static int addEconomyToPlayer(BigInteger amount, CommandSource source) throws CommandSyntaxException {
        return addEconomyToPlayer(amount, Collections.singletonList(source.asPlayer()), source);
    }


    public static int addEconomyToPlayer(BigInteger amount, Collection<? extends PlayerEntity> targets, CommandSource source) {
        for (PlayerEntity player : targets) {
            IPlayerEconomy economy = EconomyAPI.getPlayerEconomyCapability(player);
            economy.addEconomy(amount);
        }
        source.sendFeedback(new TranslationTextComponent("economyapi.command.add", targets.size(), amount), true);
        return targets.size();
    }

    public static int queryEconomy(CommandSource source) throws CommandSyntaxException {
        return queryEconomy(source.asPlayer(), source);
    }

    public static int queryEconomy(PlayerEntity player, CommandSource source) {
        source.sendFeedback(new TranslationTextComponent("economyapi.command.query", player.getDisplayName(), EconomyAPI.getPlayerEconomyCapability(player).getEconomy()), true);
        return Command.SINGLE_SUCCESS;
    }


}
