package cn.leomc.economyapi.network.message;

import cn.leomc.economyapi.EconomyAPI;
import cn.leomc.economyapi.network.NetworkHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

@SuppressWarnings("InstantiationOfUtilityClass")
public class RequestEconomyMessage {

    public RequestEconomyMessage() {
    }

    public static void encode(RequestEconomyMessage message, PacketBuffer buffer) {
    }

    public static RequestEconomyMessage decode(PacketBuffer packetBuffer) {
        return new RequestEconomyMessage();
    }

    public static void handle(RequestEconomyMessage message, Supplier<NetworkEvent.Context> context) {
        ServerPlayerEntity player = context.get().getSender();
        if (player != null)
            context.get().enqueueWork(() ->
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new EconomyMessage(EconomyAPI.getPlayerEconomyCapability(player).getEconomy()))
            );
        context.get().setPacketHandled(true);
    }

}
