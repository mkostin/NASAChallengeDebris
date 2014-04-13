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

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "my.gdx.game";
        cfg.useGL20 = false;
        cfg.width = 1920;
        cfg.height = 1280;
        cfg.vSyncEnabled = true;

        new LwjglApplication(new MyGDXSattelite(), cfg);
    }

    public static List<Placemark> getDebris() {
        try {
//            File baseDirectory = new File("D:\\4\\base");
//            File[] bases = baseDirectory.listFiles();
            List<Placemark> placemarks = new ArrayList<Placemark>();
//                File base = bases[0];
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("E:/base-10000.dat"));
            placemarks.addAll((List) ois.readObject());
            ois.close();
//                break;

            return placemarks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
