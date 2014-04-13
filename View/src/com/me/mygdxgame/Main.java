package com.me.mygdxgame;

import com.akvelon.hackathon.Placemark;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.me.mygdxgame.MyGDXSattelite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nikita.shupletsov
 * Date: 4/12/14
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */


public class Main {

    static List<Placemark> debris;

    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "my.gdx.game";
        cfg.width = 1920;
        cfg.height = 1280;
        new LwjglApplication(new MyGDXSattelite(), cfg);
    }

    public static List<Placemark> getDebris() {
        try {
            File baseDirectory = new File("D:\\4\\base");
            File[] bases = baseDirectory.listFiles();
            List<Placemark> placemarks = new ArrayList<Placemark>();
            for (int i = 0; i < 100; i++) {
                File base = bases[i];
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(base));
                placemarks.addAll((List) ois.readObject());
                ois.close();
//                break;
            }
            return placemarks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
