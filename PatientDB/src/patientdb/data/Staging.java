package patientdb.data;

import ICD.ICDModel;
import java.io.Serializable;

public class Staging implements Serializable {

    private static final long serialVersionUID = 1888945088988678635L;

    private ICDModel mCode;

    private String grading;

    private String size;

    private String lokal;

    public String getGrading() {
        return grading;
    }

    public void setGrading(String grading) {
        this.grading = grading;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ICDModel getmCode() {
        return mCode;
    }

    public void setmCode(ICDModel mCode) {
        this.mCode = mCode;
    }

    public String getLokal() {
        return lokal;
    }

    public void setLokal(String lokal) {
        this.lokal = lokal;
    }

    
}
