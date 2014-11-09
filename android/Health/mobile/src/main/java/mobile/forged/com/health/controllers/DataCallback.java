package mobile.forged.com.health.controllers;

/**
 * Created by visitor15 on 10/19/14.
 */
public abstract class DataCallback<T> {

    public abstract void receiveResults(T results);
}
