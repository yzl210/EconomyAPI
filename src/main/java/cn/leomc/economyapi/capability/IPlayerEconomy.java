package cn.leomc.economyapi.capability;

import java.math.BigInteger;

public interface IPlayerEconomy {

    BigInteger getEconomy();

    void setEconomy(BigInteger economy);

    void addEconomy(BigInteger economy);

    //  PlayerEntity getPlayer();

}
