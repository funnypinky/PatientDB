/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import patientdb.DatabaseConnection;
import patientdb.data.Patient;
import patientdb.data.PatientCollection;

/**
 *
 * @author shaesler
 */
public class Print {

    private JasperReport jasperReport;
    private JasperPrint jasperPrint;

    private JRResultSetDataSource dataSource;

    private final ArrayList<Patient> patients;

    private final DatabaseConnection connection;

    public Print(DatabaseConnection connection) {
        this.connection = connection;
        this.patients = (ArrayList<Patient>) this.connection.getPatientList();
    }

    public void prepareData() {
        try {
            JAXBContext jc = JAXBContext.newInstance(PatientCollection.class);
            Marshaller m = jc.createMarshaller();
            PatientCollection patientsColl = new PatientCollection();
            patientsColl.setPatientList(patients);
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            m.marshal(patientsColl, new File("C:\\temp\\test.xml"));
            Statement stmt = this.connection.getStmt();
            ResultSet rs = stmt.executeQuery("SELECT * FROM patienttable p, diagnosictable d, sessiontable s where p.ariaid = d.ariaid and p.ariaid=s.ariaid;");
            this.dataSource = new JRResultSetDataSource(rs);

        } catch (SQLException | JAXBException ex) {
            Logger.getLogger(Print.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void print() {
        try {
            prepareData();
            Map<String, Object> parameters = new HashMap<>();
            //parameters.put("DS1", this.dataSource);
            parameters.put("DS1", this.dataSource);
            File path = new File("printtemplates\\krankenkasse.jrxml");
            jasperReport = JasperCompileManager.compileReport(path.getAbsolutePath());
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            JasperViewer jv = new JasperViewer(jasperPrint, false);

            jv.setVisible(true);
            //JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\temp\\Example1.pdf");

        } catch (JRException ex) {
            Logger.getLogger(Print.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
