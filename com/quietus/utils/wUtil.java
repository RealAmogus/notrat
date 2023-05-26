package com.quietus.utils;

import org.imgscalr.Scalr;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

class wUtil {

    private String randomName() {

        List<String> randomList=new ArrayList<>();

        Random random = new Random();
        char randomizedCharacter;
        for (int i = 0; i < 15; i++) {
            randomizedCharacter = (char) (random.nextInt(26) + 'a');
            randomList.add(String.valueOf(randomizedCharacter));
        }

        ListIterator<String> randomIterator=randomList.listIterator();
        String randomFileName = "";

        while (randomIterator.hasNext()){

            randomFileName = randomFileName + randomIterator.next();
            randomIterator.remove();

        }

        return randomFileName;

    }

    String textWrite(String typedText)
            throws IOException {

        String randomFileName = randomName();

        File newFile = new File(randomFileName + ".log");
        RandomAccessFile stream = new RandomAccessFile(newFile, "rw");
        FileChannel channel = stream.getChannel();

        FileLock lock = null;
        try {
            lock = channel.tryLock();
        } catch (final OverlappingFileLockException e) {
            stream.close();
            channel.close();
        }
        byte[] b = typedText.getBytes(StandardCharsets.UTF_8);
        stream.write(b);

        if (lock != null) {

            lock.release();

        }

        stream.close();
        channel.close();

        return randomFileName + ".log";
    }

    String takeAndSaveSS(int imageWidth, int imageHeight, Scalr.Method quality, Boolean antiAliasing) throws IOException, AWTException{

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        Rectangle allScreenBounds = new Rectangle();
        for (GraphicsDevice screen : screens) {
            Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
            allScreenBounds.width += screenBounds.width;
            allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
        }

        BufferedImage capture = new Robot().createScreenCapture(allScreenBounds);

        if (antiAliasing) {

            Scalr.resize(capture, quality, Scalr.Mode.AUTOMATIC, imageWidth, imageHeight, Scalr.OP_ANTIALIAS);

        }
        else {

            Scalr.resize(capture, quality, Scalr.Mode.AUTOMATIC, imageWidth, imageHeight);

        }


        String fileName = jpgWrite(capture, 0.8f);

        capture.flush();

        return fileName;

    }

    String jpgWrite(BufferedImage bImage, float compressionRate) throws IOException {

        String randomFileName = randomName();

        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(compressionRate);

        DateTimeFormatter dtf  = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
        ZonedDateTime date = ZonedDateTime.now();
        String formattedDate = dtf.format(date);

        File imgFile = new File(randomFileName + "_" + formattedDate + ".jpg");
        FileImageOutputStream imgStream = new FileImageOutputStream(imgFile);

        writer.setOutput(imgStream);
        IIOImage idk = new IIOImage(bImage, null, null);
        writer.write(null, idk, iwp);

        writer.reset();
        writer.dispose();
        imgStream.flush();
        imgStream.close();
        bImage.flush();

        return randomFileName + "_" + formattedDate + ".jpg";

    }

}
