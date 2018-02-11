package me.declangao.wordpressreader.model;

/**
 * Category object
 */
public class Category {
    private String name;
    private int id;
    private String imageUrl;
    private String slug;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageUrl(String catURL) {
        this.imageUrl = catURL;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSlug() {
        return slug;
    }

}
