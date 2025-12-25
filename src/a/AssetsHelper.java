package a;

import javax.swing.*;
import java.util.ArrayList;
import java.net.URL;
import java.awt.Image;
import java.util.List;

public class AssetsHelper {

    // ---------------- APP ICONS (MULTI SIZE) ----------------
    public static List<Image> loadAppIcons() {

        List<Image> icons = new ArrayList<>();

        try {
            icons.add(new ImageIcon(AssetsHelper.class.getResource("/img/icon322.png")).getImage());
            icons.add(new ImageIcon(AssetsHelper.class.getResource("/img/icon322.png")).getImage());
            icons.add(new ImageIcon(AssetsHelper.class.getResource("/img/icon64.png")).getImage());
            icons.add(new ImageIcon(AssetsHelper.class.getResource("/img/icon128.png")).getImage());
            icons.add(new ImageIcon(AssetsHelper.class.getResource("/img/icon256.png")).getImage());
        } catch (Exception e) {
            System.out.println("App icons not found");
        }

        return icons;
    }

    // ---------------- BACK CARD ----------------
    public static final String BACK_IMAGE = "/img/back.jpg";

    // ---------------- HEART ----------------
    public static final String HEART_IMAGE = "/img/hearts.png";

    // ---------------- FRONT CARD LIST ----------------
    public static ArrayList<String> imgs = new ArrayList<>();

    // ---------------- LOAD ALL CARD IMAGES ----------------
    public static void loadAllImages() {

        try {
            imgs.clear();

            imgs.add("/img/cute1.png");
            imgs.add("/img/cute2.jpg");
            imgs.add("/img/cute3.png");
            imgs.add("/img/cute4.png");
            imgs.add("/img/cute5.png");
            imgs.add("/img/cute6.png");
            imgs.add("/img/cute7.png");
            imgs.add("/img/cute8.png");
            imgs.add("/img/cute9.png");
            imgs.add("/img/cute10.png");
            imgs.add("/img/cute11.png");
            imgs.add("/img/cute12.png");
            imgs.add("/img/cute13.png");
            imgs.add("/img/cute14.png");
            imgs.add("/img/cute15.png");
            imgs.add("/img/cute16.png");
            imgs.add("/img/cute17.png");
            imgs.add("/img/cute18.png");
            imgs.add("/img/cute19.png");
            imgs.add("/img/cute20.png");

        } catch (Exception e) {
            System.out.println("Error loading image paths: " + e.getMessage());
        }
    }

    // ---------------- IMAGE LOADER ----------------
    public static ImageIcon loadIcon(String path) {

        try {
            URL resource = AssetsHelper.class.getResource(path);

            if (resource == null) {
                throw new IllegalArgumentException("Image not found: " + path);
            }

            return new ImageIcon(resource);

        } catch (Exception e) {
            System.out.println("Failed to load image: " + path);
            return new ImageIcon();
        }
    }
}


