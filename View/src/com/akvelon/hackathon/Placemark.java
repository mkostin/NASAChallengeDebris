package com.akvelon.hackathon;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikita.shupletsov on 4/12/2014.
 */
public class Placemark implements Serializable {
    public Coordinate coordinate;
    public Description description;
    public List<Coordinate> orbit = new ArrayList<Coordinate>();
    private int position = 0;

    public Placemark(Description description, Coordinate coordinate) {
        this.coordinate = coordinate;
        this.description = description;
        orbit = prepareOrbit(description.link);
    }

    public static List<Coordinate> prepareOrbit(String link) {
        try {
            List<Coordinate> result = new ArrayList<Coordinate>();
            org.apache.http.client.HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(link);
            HttpResponse response = client.execute(get);
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            int received;
            while ((received = response.getEntity().getContent().read()) != -1) {
                responseBuffer.write(received);
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            ByteArrayInputStream bais = new ByteArrayInputStream(responseBuffer.toByteArray());
            NodeList list = db.parse(bais).getElementsByTagName("coordinates");
            String[] lines = list.item(0).getFirstChild().getNodeValue().split("\n");
            for (String coordinate : lines) {
                if (!coordinate.trim().isEmpty()) {
                    result.add(new Coordinate(coordinate));
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public Coordinate getNextCoordinate(float scale) {
        if (position == orbit.size()) {
            position = 0;
        }
        Coordinate coordinate = orbit.get(position);
        position++;
        return new Coordinate(coordinate.x / scale, coordinate.y / scale, coordinate.z /scale, coordinate.line);
    }

}
