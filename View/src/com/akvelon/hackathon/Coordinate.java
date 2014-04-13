package com.akvelon.hackathon;

import java.io.Serializable;

/**
 * Created by nikita.shupletsov on 4/12/2014.
 */
public class Coordinate implements Serializable {
    public static double EarthRadius = 6400;
    public double x;
    public double y;
    public double z;
    public String line;

    public Coordinate(double x, double y, double z, String line) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.line = line;
    }

    public Coordinate(String line) throws IllegalArgumentException{
        try {
            String trimLine = line.trim();
            this.line = trimLine;
            String[] coordinates = trimLine.split(",");
            double lat = Double.parseDouble(coordinates[1]);
            double lon = Double.parseDouble(coordinates[0]);
            int signX = 1;
            int signY = 1;
            if (lat < 0) {
                signX = -1;
            }
            if (lon < 0) {
                signY = -1;
            }
            lat = Math.abs(lat);
            lon = Math.abs(lon);
            double radius = Double.parseDouble(coordinates[2]) / 1000f + EarthRadius;
            this.x = signX * Math.sin(Math.toRadians(lat > 90 ? 180 - lat : lat)) * radius;
            this.y = signY * Math.sin(Math.toRadians(lon > 90 ? 180 - lon : lon)) * radius;
            this.z = signX * Math.cos(Math.toRadians(lat > 90 ? 180 - lat : lat)) * radius;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}