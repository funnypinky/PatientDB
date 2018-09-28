/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb.view;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import patientdb.DatabaseConnection;

/**
 *
 * @author shaesler
 */
public class Print {

    private JasperReport jasperReport;
    private JasperPrint jasperPrint;

    private final DatabaseConnection connection;

    public Print(DatabaseConnection connection) {
        this.connection = connection;
    }

    public void print(HashMap<String, Object> parameters) {
        try {
            File path = new File("printtemplates\\krankenkasse.jrxml");
            jasperReport = JasperCompileManager.compileReport(path.getAbsolutePath());
            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, this.connection.getCon());
            JasperViewer jv = new JasperViewer(jasperPrint, false);
            
            jv.setVisible(true);

        } catch (JRException ex) {
            Logger.getLogger(Print.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
