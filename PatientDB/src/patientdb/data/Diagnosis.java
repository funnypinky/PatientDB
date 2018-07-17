package patientdb.data;

import java.io.Serializable;

public class Diagnosis implements Serializable {

    private static final long serialVersionUID = 8591018579378179239L;

    private Staging staging;

    private Therapy therapy;

    private String ICD10;

    private String tumorentity;

    private Boolean primary;

    private Boolean rezidiv;

    private boolean preop;

    public Staging getStaging() {
        return staging;
    }

    public void setStaging(Staging staging) {
        this.staging = staging;
    }

    public Therapy getTherapy() {
        return therapy;
    }

    public void setTherapy(Therapy therapy) {
        this.therapy = therapy;
    }

    public String getICD10() {
        return ICD10;
    }

    public void setICD10(String ICD10) {
        this.ICD10 = ICD10;
    }

    public String getTumorentity() {
        return tumorentity;
    }

    public void setTumorentity(String tumorentity) {
        this.tumorentity = tumorentity;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Boolean getRezidiv() {
        return rezidiv;
    }

    public void setRezidiv(Boolean rezidiv) {
        this.rezidiv = rezidiv;
    }

    public boolean isPreop() {
        return preop;
    }

    public void setPreop(boolean preop) {
        this.preop = preop;
    }

}
