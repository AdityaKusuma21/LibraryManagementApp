package com.example.librarymanagementapp;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {
    private String id;
    private String title;
    private String author;
    private String genre;
    private String summary;
    private String img;
    private List<Review> reviews;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }

    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
