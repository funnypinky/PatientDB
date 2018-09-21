/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.data;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author shaesler
 */
@XmlJavaTypeAdapter(type = LocalDate.class,
        value = LocalDateAdapter.class)

@XmlRootElement(name = "patients")
public class PatientCollection {

    private List<Patient> patient;

    public List<Patient> getPatientList() {
        if (patient == null) {
            this.patient = new LinkedList<>();
        }
        return this.patient;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patient = patientList;
    }

}
