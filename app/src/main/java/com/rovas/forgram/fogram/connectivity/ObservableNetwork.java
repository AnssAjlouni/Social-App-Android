package com.rovas.forgram.fogram.connectivity;

import java.util.Observable;

/**
 * Created by Mohamed El Sayed
 */

//bugfix Issue #61
public class ObservableNetwork extends Observable {
    private static ObservableNetwork instance = new ObservableNetwork();

    public static ObservableNetwork getInstance() {
        return instance;
    }

    private ObservableNetwork() {
    }

    public void updateValue(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}