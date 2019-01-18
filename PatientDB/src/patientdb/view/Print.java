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
import javafx.stage.Window;
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

    private WorkIndicatorDialog wd = null;

    private final DatabaseConnection connection;

    private Window owner;

    public Print(DatabaseConnection connection) {
        this.connection = connection;
    }

    public void print(HashMap<String, Object> parameters) {

        wd = new WorkIndicatorDialog(this.owner, "Bereite Bericht vor...");
        wd.addTaskEndNotification((Object result) -> {            
            JasperViewer jv = new JasperViewer(jasperPrint, false);
            jv.setVisible(true);
            wd = null; // don't keep the object, cleanup
        });
        wd.exec(null, inputParam -> {
            File path = new File("printtemplates\\krankenkasse.jrxml");
            try {
                jasperReport = JasperCompileManager.compileReport(path.getAbsolutePath());
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, this.connection.connect());
            } catch (JRException ex) {
                Logger.getLogger(Print.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 1;
        });
    }

    public Window getOwner() {
        return owner;
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }
    
}
