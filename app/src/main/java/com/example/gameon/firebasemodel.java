package com.example.gameon;

public class firebasemodel {
    private String title;
    private String content;
    private String aof;
    private int difficulty;
    private int reps;
    private int sets;

    public firebasemodel() {

    }

    public firebasemodel(String title, String content, String aof, int difficulty, int reps, int sets) {
        this.title = title;
        this.content = content;
        this.aof = aof;
        this.difficulty = difficulty;
        this.reps = reps;
        this.sets = sets;
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

    public String getAof() {
        return aof;
    }

    public void setAof(String aof) {
        this.aof = aof;
    }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }
}

