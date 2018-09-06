package patientdb.data;

import java.io.Serializable;

public class Diagnosis implements Serializable {

    private static final long serialVersionUID = 8591018579378179239L;

    private Staging staging;

    private String ICD10;

    private Boolean primary;

    private Boolean rezidiv;

    private boolean preop;

    public Staging getStaging() {
        return staging;
    }

    public void setStaging(Staging staging) {
        this.staging = staging;
    }

    public String getICD10() {
        return ICD10;
    }

    public void setICD10(String ICD10) {
        this.ICD10 = ICD10;
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
