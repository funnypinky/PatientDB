package patientdb.data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Patient implements Serializable {

    private static final long serialVersionUID = -6641232890797160029L;

    private String ariaID;

    private Long uniqueID;

    private String lastName;

    private String firstName;

    private String surName;

    private LocalDate birthday;

    private LocalDate deathDay;

    private Diagnosis dianoses;

    private Boolean study;

    private String pretherapy;

    private ArrayList<String> comments;
    
    private String sex;

    public String getAriaID() {
        return ariaID;
    }

    public void setAriaID(String ariaID) {
        this.ariaID = ariaID;
    }

    public Long getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(Long uniqueID) {
        this.uniqueID = uniqueID;
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

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getDeathDay() {
        return deathDay;
    }

    public void setDeathDay(LocalDate deathDay) {
        this.deathDay = deathDay;
    }

    public Diagnosis getDianoses() {
        return dianoses;
    }

    public void setDianoses(Diagnosis dianoses) {
        this.dianoses = dianoses;
    }

    public Boolean getStudy() {
        return study;
    }

    public void setStudy(Boolean study) {
        this.study = study;
    }

    public String getPretherapy() {
        return pretherapy;
    }

    public void setPretherapy(String pretherapy) {
        this.pretherapy = pretherapy;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public HashMap toMap(){
        HashMap map = new HashMap();
        map.put("AriaID", this.ariaID);
        map.put("BirthDay", this.birthday);
        map.put("DeathDay", this.deathDay);
        map.put("FirstName", this.firstName);
        map.put("LastName", this.lastName);
        map.put("Sex", this.sex);
        map.put("Study", this.study);
        map.put("Pretherapy", this.pretherapy);
        map.put("Study", this.study);
        return map;
    }
}
