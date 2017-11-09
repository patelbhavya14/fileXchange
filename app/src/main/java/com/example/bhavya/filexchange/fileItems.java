package com.example.bhavya.filexchange;

/**
 * Created by bhaVYa on 13/09/17.
 */

class fileItems {
    private String title,tag,type,url,name;

    public fileItems(String name, String title,String tag,String type,String url) {

        super();

        this.name = name;
        this.title = title;
        this.tag = tag;
        this.type = type;
        this.url = url;
    }


    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
