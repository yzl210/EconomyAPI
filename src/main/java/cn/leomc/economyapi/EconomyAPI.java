package cn.leomc.economyapi;

import cn.leomc.economyapi.capability.CapabilityPlayerEconomy;
import cn.leomc.economyapi.capability.IPlayerEconomy;
import cn.leomc.economyapi.command.EconomyCommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EconomyAPI.MODID)
public class EconomyAPI {

    public static final String MODID = "economyapi";

    public static final Logger LOGGER = LogManager.getLogger();

    public EconomyAPI() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFMLCommonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static IPlayerEconomy getPlayerEconomyCapability(PlayerEntity player) {
        return player.getCapability(CapabilityPlayerEconomy.PLAYER_ECONOMY).orElseThrow(() -> new EconomyException("Economy capability of player: " + player.getDisplayName().getString() + "  is null!"));
    }


    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        CapabilityPlayerEconomy.register();
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof PlayerEntity))
            return;

        event.addCapability(CapabilityPlayerEconomy.ID, new CapabilityPlayerEconomy());
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        EconomyCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;
        IPlayerEconomy economy = getPlayerEconomyCapability(event.getPlayer());
        economy.setEconomy(getPlayerEconomyCapability(event.getOriginal()).getEconomy());
    }

}
