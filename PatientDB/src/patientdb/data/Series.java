/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author shaesler
 */
public class Series extends ItemOfTree {

    private Boolean simCT;

    private Boolean simRT;

    private String complication;

    private LocalDate therapyDate;

    private LocalDate inDay;

    private LocalDate outDay;

    private String sapNumber;

    private String comments;

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

    public LocalDate getInDay() {
        return inDay;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setInDay(LocalDate inDay) {
        this.inDay = inDay;
    }

    public LocalDate getOutDay() {
        return outDay;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setOutDay(LocalDate outDay) {
        this.outDay = outDay;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Fall: " + sapNumber + "\t " + this.therapyDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}
