/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.data;

import java.time.LocalDate;

/**
 *
 * @author shaesler
 */
public class Series {
    
    private Boolean simCT;

    private Boolean simRT;
    
    private String complication;
    
    private LocalDate therapyDate;
    
    private String sapNumber;

    public Boolean getSimCT() {
        return simCT;
    }

    public void setSimCT(Boolean simCT) {
        this.simCT = simCT;
    }

    public Boolean getSimRT() {
        return simRT;
    }

    public void setSimRT(Boolean simRT) {
        this.simRT = simRT;
    }

    public String getComplication() {
        return complication;
    }

    public void setComplication(String complication) {
        this.complication = complication;
    }

    public LocalDate getTherapyDate() {
        return therapyDate;
    }

    public void setTherapyDate(LocalDate therapyDate) {
        this.therapyDate = therapyDate;
    }

    public String getSapNumber() {
        return sapNumber;
    }

    public void setSapNumber(String sapNumber) {
        this.sapNumber = sapNumber;
    }
    
    
}
