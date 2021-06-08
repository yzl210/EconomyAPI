package cn.leomc.economyapi;

public class EconomyException extends RuntimeException {

    public EconomyException(String message) {
        super(message);
    }

    public EconomyException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public EconomyException(Throwable throwable) {
        super(throwable);
    }
}
