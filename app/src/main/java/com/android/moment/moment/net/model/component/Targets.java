package com.android.moment.moment.net.model.component;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Targets represents a single image in different resolutions. It provides access to the different versions through getImage-method which takes size
 */
public class Targets {
    private static final String TAG = "Targets";

    /**
     * represents possible Size of images
     */
    public static enum Size {
        SMALL("small"),
        SMALL2X("small2x"),
        MEDIUM("medium"),
        MEDIUM2X("medium2x"),
        LARGE("large"),
        LARGE2X("large2x"),
        XLARGE("xlarge"),
        XLARGE2X("xlarge2x");

        private String name;

        private Size(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * @param targets
     * @return
     * @throws org.json.JSONException
     */
    public static Targets createFromJsonObject(JSONObject targets) throws JSONException {
        Targets resultTargets = new Targets();
        for (Size size : Size.values()) {
            if (targets.has(size.toString())) {
                JSONObject target = targets.getJSONObject(size.toString());
                Image newImage = Image.createFromJsonObject(target, size);
                resultTargets.addImage(newImage);
            }
        }
        return resultTargets;
    }

    /**
     * returns the Image in a certain size
     *
     * @param size
     * @return
     */
    public Image getImage(Size size) {
        Image result = imageList.get(size);
        if (result == null) {
            //determine closest image size;
            int difference = 99;
            int ordinal = -1;
            for (Size current : Size.values()) {
                int newDiff = Math.abs(current.ordinal() - size.ordinal());
                if (newDiff < difference && imageList.containsKey(current)) {
                    difference = newDiff;
                    ordinal = current.ordinal();
                }
            }
            if (ordinal != -1) {
                //return closest
                result = imageList.get(Size.values()[ordinal]);
                Log.d(TAG, "1" + (result == null));
            } else {
                //return first
                result = imageList.get(imageList.keySet().iterator().next());
                Log.d(TAG, "2" + (result == null));
            }
        }
        Log.d(TAG, "getImage returns: " + result.getSize() + " desired " + size +
                "  desired was null?" + (imageList.get(size) == null));
        return result;

    }

    private Map<Size, Image> imageList = new HashMap<Size, Image>();

    public void addImage(Image image) {
        this.imageList.put(image.getSize(), image);
    }

    public void updateImages(Targets targets) {
        this.imageList = targets.imageList;
    }

    public void setImages(Map<Size, Image> imageList) {
        this.imageList = imageList;
    }

    public static class Image {
        private Size size;
        private String url;
        private int width;
        private int height;
        private String mimeType;

        public static Image createFromJsonObject(JSONObject image, Size size) throws JSONException {
            String url = image.getString("url");
            String mimeType = image.getString("mimeType");
            int height = image.getInt("height");
            int width = image.getInt("width");
            return new Image(url, height, width, mimeType, size);
        }

        public Image(String url, int height, int width, String mimeType, Size size) {
            this.url = url;
            this.height = height;
            this.width = width;
            this.mimeType = mimeType;
            this.size = size;
        }

        public Size getSize() {
            return size;
        }

        public void setSize(Size size) {
            this.size = size;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getUrl() {
            return this.url;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        @Override
        public String toString() {
            return (new StringBuilder("Image:").append(url).append(width).append(":").append(height)).toString();
        }
    }
}
