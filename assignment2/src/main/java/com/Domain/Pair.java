package com.Domain;

public class Pair {
    private int job; // Job number
    private String filename; // Name of file to be printed

    public Pair(int job, String filename) { // Constructor for queue with tuples
        this.job = job;
        this.filename = filename;
    }
    
    public int getJob() { // Get job number
        return job;
    }

    public String getFilename() { // Get filename
        return filename;
    }

    public void setJob(int job) { // Set job number
        this.job = job;
    }

    public void setFilename(String filename) { // Set filename
        this.filename = filename;
    }

}
