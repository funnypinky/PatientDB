package patientdb.data;

import ICD.ICDModel;
import java.io.Serializable;

public class Diagnosis implements Serializable {

    private static final long serialVersionUID = 8591018579378179239L;

    private Staging staging;

    private ICDModel ICD10;

    private boolean primary;

    private boolean rezidiv;

    private boolean preop;

    public Staging getStaging() {
        return staging;
    }

    public void setStaging(Staging staging) {
        this.staging = staging;
    }

    public ICDModel getICD10() {
        return ICD10;
    }

    public void setICD10(ICDModel ICD10) {
        this.ICD10 = ICD10;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isRezidiv() {
        return rezidiv;
    }

    public void setRezidiv(boolean rezidiv) {
        this.rezidiv = rezidiv;
    }

    public boolean isPreop() {
        return preop;
    }

    public void setPreop(boolean preop) {
        this.preop = preop;
    }

}
