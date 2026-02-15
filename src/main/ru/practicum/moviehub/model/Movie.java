package ru.practicum.moviehub.model;

public class Movie {
    private String name;
    private int year;
    private int id;

    public Movie(String name,int year) {
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(int year) {
        this.year = year;
    }
}