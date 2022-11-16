package com.Domain;

import java.util.ArrayList;
import java.util.List;

public class Printer {
    private String printerName; // Name of printer
    private String status = "Idle"; // Status of printer
    private int jobNumber = 0; // Job number
    private List<Pair> queue = new ArrayList<>(); // Queue for storing jobs
    

    public Printer(String printerName) { // Constructor for printer
        this.printerName = printerName;
    }
    
    public String getPrinterName() { // Get printer name
        return printerName;
    }

    public int getJobNumber(int jobNumber) { // Get job number
        for(Pair pair : queue) {
            if(pair.getJob() == jobNumber) {
                return pair.getJob();
            }
        }
        return -1; // Job not found
    }


    public String getStatus() { // Get printer status
        return status;
    }


    public void setStatus(String status) { // Set printer status
        this.status = status;
    }


    public void setPrinterName(String printerName) { // Set printer name
        this.printerName = printerName;
    }


    public String addToqueue(String filename) { // Add job to queue
        queue.add(new Pair(jobNumber, filename)); // Add to queue
        jobNumber++; // Increment job number
        return "File " + filename + " added to queue " + this.printerName+ " as job " + (jobNumber - 1) + "\n";
    }


    public String queue(){ // Get the jobs added to the queue and turn all of them into a string for easy printing
        String queueString = "";
        for (Pair pair : queue) { // Iterate through queue
            queueString += pair.getJob() + " " + pair.getFilename() + "; ";
        }
        return queueString;
    }   


    public String topQueue(int job) { // Move job to top of queue
        for (Pair pair : queue) {
            if (pair.getJob() == job) { // If job is found
                queue.remove(pair); // Remove the job
                queue.add(0, pair); // Add to to the front of the list, representing the Job queue
                return "Job " + job + " moved to top of queue";
            }
        }
        return "Job " + job + " not found";
    }
}
