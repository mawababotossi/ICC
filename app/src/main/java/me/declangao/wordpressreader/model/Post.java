package me.declangao.wordpressreader.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Post object along with its properties, as well as their corresponding getter and setter methods
 */
public class Post {
    private String title;
    private String content;
    private String thumbnailUrl;
    private String featuredImageUrl = "";
    private String viewCount;
    private String date;
    private String author;
    private String url;
    private int commentCount;
    private JSONObject customFields;
    private int id;
    private ArrayList<String> categories;
    private String categoriesString;

    public Post() {

    }

    /**
     * Override equals method to help Set decide if two posts are the same
     *
     * @param o Another object
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Post && this.getId() == (((Post) o).getId());
    }

    /**
     * Override hashCode method to help Set decide if two posts are the same
     */
    @Override
    public int hashCode() {
        //return this.getId();
        return Integer.valueOf(this.getId()).hashCode();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }

    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void setCategoriesString(String catsString) {
        this.categoriesString = catsString;
    }

    public String getCategoriesString() {
        return categoriesString;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public JSONObject getCustomFields() {
        return customFields;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCustomFields (JSONObject cf) {
        this.customFields = cf;
    }

    public boolean isEvent(){
        boolean b = false;
        try {
            if(this.getCustomFields().getString("type").toString().contains("event") ){
                b = true;}
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return b;
    }

    public boolean isAudio(){
        boolean b = false;
        try {
            if(this.getCustomFields().getString("type").toString().contains("audio") ) b = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return b;
    }
}
