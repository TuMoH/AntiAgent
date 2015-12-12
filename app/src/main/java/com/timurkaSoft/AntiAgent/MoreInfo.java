package com.timurkaSoft.AntiAgent;


import java.util.ArrayList;
import java.util.List;

public class MoreInfo {

    private final String head;
    private final String author;
    private final String allInfo;
    private final String tel;
    private final String geo;
    private final List<String> img;

    public MoreInfo(String head, String author, String allInfo, String tel, String geo, List<String> img) {
        this.head = head;
        this.author = author;
        this.allInfo = allInfo;
        this.tel = tel;
        this.geo = geo;
        this.img = img;
    }

    public String getHead() {
        return head;
    }

    public String getAuthor() {
        return author;
    }

    public String getAllInfo() {
        return allInfo;
    }

    public String getTel() {
        return tel;
    }

    public String getGeo() {
        return geo;
    }

    public List<String> getImg() {
        return img;
    }

    public static class Builder {

        private String head = "Не удалось загрузить";
        private String author = "";
        private String allInfo = "";
        private String tel = "";
        private String geo = "";
        private List<String> img = new ArrayList<>();

        public Builder setHead(String head) {
            this.head = head;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setAllInfo(String allInfo) {
            this.allInfo = allInfo;
            return this;
        }

        public Builder setTel(String tel) {
            this.tel = tel;
            return this;
        }

        public Builder setGeo(String geo) {
            this.geo = geo;
            return this;
        }

        public Builder setImg(List<String> img) {
            this.img = img;
            return this;
        }

        public MoreInfo build() {
            return new MoreInfo(head, author, allInfo, tel, geo, img);
        }
    }
}
