package cn.leomc.economyapi.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.math.BigInteger;

public class PlayerEconomy implements IPlayerEconomy, INBTSerializable<CompoundNBT> {

    private BigInteger economy;

    // private PlayerEntity player;

    public PlayerEconomy() {
        economy = new BigInteger("0");
    }

    public BigInteger getEconomy() {
        return economy;
    }

    public void setEconomy(BigInteger economy) {
        this.economy = economy;
    }


    public void addEconomy(BigInteger economy) {
        setEconomy(getEconomy().add(economy));
    }

    @Override
    public void removeEconomy(BigInteger economy) {
        setEconomy(getEconomy().subtract(economy));
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("economy", getEconomy().toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        setEconomy(new BigInteger(nbt.getString("economy")));
    }
}
