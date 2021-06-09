package cn.leomc.economyapi.network.message;

import cn.leomc.economyapi.EconomyAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.math.BigInteger;
import java.util.function.Supplier;

public class EconomyMessage {

    public BigInteger economy;

    public EconomyMessage(BigInteger economy) {
        this.economy = economy;
    }

    public static void encode(EconomyMessage message, PacketBuffer buffer) {
        buffer.writeString(message.economy.toString());
    }

    public static EconomyMessage decode(PacketBuffer packetBuffer) {
        return new EconomyMessage(new BigInteger(packetBuffer.readString()));
    }

    public static void handle(EconomyMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> EconomyAPI.getPlayerEconomyCapabilityClient(Minecraft.getInstance().player).setEconomy(message.economy));
        context.get().setPacketHandled(true);
    }

}
