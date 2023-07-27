package com.example.gameon;

public class firebasemodel {
    private String title;
    private String content;
    //private int difficulty;

    public firebasemodel() {

    }

    public firebasemodel(String title, String content) {
        this.title = title;
        this.content = content;
        //this.difficulty = difficulty;
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

//    public int getDifficulty() {
//        return difficulty;
//    }
//
//    public void setDifficulty(int difficulty) {
//        this.difficulty = difficulty;
//    }
}

