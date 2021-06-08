package cn.leomc.economyapi.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.math.BigInteger;

public class BigIntegerArgumentType implements ArgumentType<BigInteger> {

    private BigIntegerArgumentType() {
    }

    public static BigIntegerArgumentType bigIntegerArg() {
        return new BigIntegerArgumentType();
    }


    public static BigInteger getBigInteger(final CommandContext<?> context, final String name) {
        return context.getArgument(name, BigInteger.class);
    }

    @Override
    public BigInteger parse(final StringReader reader) {
        return new BigInteger(reader.readUnquotedString());
    }


    @Override
    public String toString() {
        return "bigIntegerArg()";
    }

}
