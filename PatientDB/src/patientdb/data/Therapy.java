package patientdb.data;

import java.io.Serializable;
import java.util.Date;

public class Therapy implements Serializable {

    private static final long serialVersionUID = 5449104584684212805L;

    private Boolean simCT;

    private Boolean simRT;

    private Date firstHT;

    private int plannedHT;

    private int remainHT;

    private String complication;

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

    public Date getFirstHT() {
        return firstHT;
    }

    public void setFirstHT(Date firstHT) {
        this.firstHT = firstHT;
    }

    public int getPlannedHT() {
        return plannedHT;
    }

    public void setPlannedHT(int plannedHT) {
        this.plannedHT = plannedHT;
    }

    public int getRemainHT() {
        return remainHT;
    }

    public void setRemainHT(int remainHT) {
        this.remainHT = remainHT;
    }

    public String getComplication() {
        return complication;
    }

    public void setComplication(String complication) {
        this.complication = complication;
    }

}
