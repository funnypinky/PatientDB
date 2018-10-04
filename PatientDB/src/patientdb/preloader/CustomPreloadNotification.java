/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.preloader;

import javafx.application.Preloader;

/**
 *
 * @author shaesler
 */
public class CustomPreloadNotification implements Preloader.PreloaderNotification {
    
    private double progress;
    private String details;

    public CustomPreloadNotification(double progress, String details) {
        this.progress = progress;
        this.details = details;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    
    
    
}
