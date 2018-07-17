package patientdb.data;

import java.io.Serializable;

public class Staging implements Serializable {

    private static final long serialVersionUID = 1888945088988678635L;

    private String TNM;

    private String stadium;

    private String grading;

    private String resektion;

    private String size;

    private String histology;

    public String getTNM() {
        return TNM;
    }

    public void setTNM(String TNM) {
        this.TNM = TNM;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public String getGrading() {
        return grading;
    }

    public void setGrading(String grading) {
        this.grading = grading;
    }

    public String getResektion() {
        return resektion;
    }

    public void setResektion(String resektion) {
        this.resektion = resektion;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHistology() {
        return histology;
    }

    public void setHistology(String histology) {
        this.histology = histology;
    }

}
