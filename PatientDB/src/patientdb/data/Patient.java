package patientdb.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class Patient extends ItemOfTree {

    private static final long serialVersionUID = -6641232890797160029L;

    private String ariaID;

    private String lastName;

    private String firstName;

    private LocalDate birthday;

    private LocalDate deathDay;

    private Diagnosis diagnoses;

    private String study;

    private String studyName;

    private Boolean pretherapy;

    private String sex;

    private List<Series> series;

    public String getAriaID() {
        return ariaID;
    }

    public void setAriaID(String ariaID) {
        this.ariaID = ariaID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getDeathDay() {
        return deathDay;
    }

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    public void setDeathDay(LocalDate deathDay) {
        this.deathDay = deathDay;
    }

    public Diagnosis getDiagnose() {
        return diagnoses;
    }

    public void setDiagnoses(Diagnosis diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public Boolean getPretherapy() {
        return pretherapy;
    }

    public void setPretherapy(Boolean pretherapy) {
        this.pretherapy = pretherapy;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ariaID).append(" \t").append(this.lastName).append(", ").append(this.firstName);
        if (this.birthday != null) {
            sb.append("\t\t").append(this.birthday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Patient)) {
            return false;
        }
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }
        Patient pat = (Patient) o;
        return this.ariaID.contentEquals(pat.ariaID);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.ariaID);
        return hash;
    }
}
