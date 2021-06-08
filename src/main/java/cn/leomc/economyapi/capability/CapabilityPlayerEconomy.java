package cn.leomc.economyapi.capability;

import cn.leomc.economyapi.EconomyAPI;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigInteger;

public class CapabilityPlayerEconomy implements ICapabilitySerializable<INBT> {

    @CapabilityInject(IPlayerEconomy.class)
    public static Capability<IPlayerEconomy> PLAYER_ECONOMY = null;

    public static ResourceLocation ID = new ResourceLocation(EconomyAPI.MODID, "economy");

    private final LazyOptional<IPlayerEconomy> instance = LazyOptional.of(PLAYER_ECONOMY::getDefaultInstance);


    public static void register() {
        CapabilityManager.INSTANCE.register(IPlayerEconomy.class, new Capability.IStorage<IPlayerEconomy>() {
            @Override
            public INBT writeNBT(Capability<IPlayerEconomy> capability, IPlayerEconomy instance, Direction side) {
                return StringNBT.valueOf(instance.getEconomy().toString());
            }

            @Override
            public void readNBT(Capability<IPlayerEconomy> capability, IPlayerEconomy instance, Direction side, INBT nbt) {
                instance.setEconomy(new BigInteger(nbt.getString()));
            }

        }, PlayerEconomy::new);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PLAYER_ECONOMY.orEmpty(cap, instance);
    }

    @Override
    public INBT serializeNBT() {
        return PLAYER_ECONOMY.writeNBT(instance.orElseThrow(() -> new RuntimeException("Instance is null!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        PLAYER_ECONOMY.readNBT(instance.orElseThrow(() -> new RuntimeException("Instance is null!")), null, nbt);
    }
}
