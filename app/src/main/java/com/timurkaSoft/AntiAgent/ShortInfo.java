package com.timurkaSoft.AntiAgent;


import org.parceler.Parcel;

import java.util.HashMap;

@Parcel
public class ShortInfo {

    private String text;
    private String price;
    private String photo;
    private String href;

    public ShortInfo() {
    }

    public ShortInfo(String text, String price, String photo, String href) {
        this.text = text;
        this.price = price;
        this.photo = photo;
        this.href = href;
    }

    public String getText() {
        return text;
    }

    public String getPrice() {
        return price;
    }

    public String getPhoto() {
        return photo;
    }

    public String getHref() {
        return href;
    }

    public HashMap<String, String> getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("text", text);
        map.put("price", price + " руб.");
        map.put("photo", photo + " фото");
        return map;
    }

    public static class Builder {

        private String text = "?";
        private String price = "0";
        private String photo = "0";
        private String href = "";

        public Builder setHref(String href) {
            this.href = href;
            return this;
        }

        public Builder setPhoto(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder setPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public ShortInfo build() {
            return new ShortInfo(text, price, photo, href);
        }
    }
}
