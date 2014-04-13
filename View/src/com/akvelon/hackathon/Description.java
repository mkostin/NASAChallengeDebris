package com.akvelon.hackathon;


import java.io.Serializable;
import java.util.List;
import org.htmlcleaner.*;


public class Description implements Serializable {
    public String link;
    public float size;

    public Description(String line) throws IllegalArgumentException {
        line = line.replaceAll("\n", "");
        try {
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode root = cleaner.clean(line);
            this.link = root.getElementsByName("a", true)[1].getAttributes().get("href");
            List<? extends BaseToken> linkElements = root.getElementsByName("body", true)[0].getAllChildren();
            for (int i = 0; i < linkElements.size(); i++) {
                if (linkElements.get(i).toString().equals("a")) {
                    String size = linkElements.get(i - 2).toString().trim();
                    this.size = Float.parseFloat(size.substring(0, size.length() - 2));
                    break;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
