package com.example.bhavya.filexchange;

/**
 * Created by bhaVYa on 13/09/17.
 */

class fileItems {
    private String title,tag,type,url,name,key;

    public fileItems(String name, String title,String tag,String type,String url,String key) {

        super();

        this.name = name;
        this.title = title;
        this.tag = tag;
        this.type = type;
        this.url = url;
        this.key = key;
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

    public String getKey() {
        return key;
    }
}
