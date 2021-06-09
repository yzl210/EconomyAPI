package cn.leomc.economyapi.network;

import cn.leomc.economyapi.EconomyAPI;
import cn.leomc.economyapi.network.message.EconomyMessage;
import cn.leomc.economyapi.network.message.RequestEconomyMessage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel INSTANCE;

    private NetworkHandler() {
    }

    public static void register() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(EconomyAPI.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        int id = 0;
        INSTANCE.registerMessage(id++, EconomyMessage.class, EconomyMessage::encode, EconomyMessage::decode, EconomyMessage::handle);
        INSTANCE.registerMessage(id++, RequestEconomyMessage.class, RequestEconomyMessage::encode, RequestEconomyMessage::decode, RequestEconomyMessage::handle);
    }


    @SuppressWarnings("InstantiationOfUtilityClass")
    public static void syncEconomy() {
        INSTANCE.sendToServer(new RequestEconomyMessage());
    }


}
