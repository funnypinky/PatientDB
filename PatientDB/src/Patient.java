
import java.util.ArrayList;
import java.util.Date;

public class Patient implements Serializable {

    private String ID;

    private Long uniqueID;

    private String lastName;

    private String firstName;

    private String surName;

    private Date birthday;

    private Diagnosis dianoses;

    private Boolean study;

    private String pretherapy;

    private ArrayList<String> comments;
}
