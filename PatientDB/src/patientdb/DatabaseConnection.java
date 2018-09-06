/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patientdb;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import patientdb.data.Patient;

/**
 *
 * @author shaesler
 */
public class DatabaseConnection {

    private boolean firstRun = true;
    private String filePath = new String();
    private String user = "sa";
    private String password = "";
    private Connection con;
    private Statement stmt = null;
    private int result = 0;

    public DatabaseConnection() {
        boolean patientTableExist = false;
        boolean diagnosicTableExist = false;
        boolean stagingTableExist = false;
        try {
            //create Data-Directory
            File path = new File(new File("data\\data.db").getParent());
            if (!path.exists()) {
                path.mkdir();
            }
            this.filePath = "jdbc:hsqldb:file:" + path.getAbsolutePath() + "\\data.db";

            //Connect the Database
            con = DriverManager.getConnection(this.filePath, user, password);
            System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Databasepath: " + this.filePath);
            if (con != null) {
                System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Connection created successfully");
                DatabaseMetaData databaseMetaData = con.getMetaData();
                //Check if table exists, when no the create the table
                try (ResultSet resultSet = databaseMetaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (resultSet.next()) {
                        String test = resultSet.getString("TABLE_NAME");
                        if (!patientTableExist) {
                            patientTableExist = test.equalsIgnoreCase("patientTable");
                        }
                        if (!diagnosicTableExist) {
                            diagnosicTableExist = test.equalsIgnoreCase("diagnosicTable");
                        }
                        if (!stagingTableExist) {
                            stagingTableExist = test.equalsIgnoreCase("stagingTable");
                        }
                    }
                }
                stmt = con.createStatement();
                if (!patientTableExist) {
                    result = stmt.executeUpdate(
                            "CREATE TABLE patientTable(ARIAID VARCHAR(50) NOT NULL,"
                            + "LASTNAME VARCHAR(75) NOT NULL,"
                            + "FIRSTNAME VARCHAR(75) NOT NULL,"
                            + "BIRTHDAY DATE,"
                            + "DEATHDAY DATE,"
                            + "STUDY VARCHAR(25),"
                            + "PRETHERAPY VARCHAR(1024),"
                            + "SEX VARCHAR(25),"
                            + "CREATEDATE TIMESTAMP,"
                            + "MODIFYDATE TIMESTAMP,"
                            + "PRIMARY KEY (ARIAID));");
                } 
                if (!diagnosicTableExist) {
                    result = stmt.executeUpdate(
                            "CREATE TABLE diagnosicTable(ARIAID VARCHAR(50) NOT NULL,"
                            + "ICD10 VARCHAR(7) NOT NULL,"
                            + "primaryTumor BOOLEAN,"
                            + "rezidiv BOOLEAN,"
                            + "preop BOOLEAN,"
                            + "stagingKey VARCHAR(50) NOT NULL,"
                            + "PRIMARY KEY (ARIAID));");

                }
            } else {
                System.out.println("[e] " + LocalDate.now() + " " + LocalTime.now() + " Problem with creating connection");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public List<Patient> getPatientList() {
        ArrayList<Patient> patientList = new ArrayList();
        try {
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM PATIENTTABLE;");
            while (resultSet.next()) {
                Patient temp = new Patient();
                temp.setAriaID(resultSet.getString("ARIAID"));
                temp.setLastName(resultSet.getString("LASTNAME"));
                temp.setFirstName(resultSet.getString("FIRSTNAME"));
                temp.setSex(resultSet.getString("SEX"));
                temp.setBirthday(LocalDate.parse(resultSet.getString("BIRTHDAY")));
                temp.setDeathDay(resultSet.getString("DEATHDAY") != null ? LocalDate.parse(resultSet.getString("DEATHDAY")) : null);
                temp.setStudy(resultSet.getString("STUDY"));
                temp.setPretherapy(resultSet.getString("PRETHERAPY"));
                patientList.add(temp);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return patientList;
    }

    public boolean addPatient(Patient patient) {
        HashMap map = patient.toMap();
        try {
            if (patient.getAriaID() != null && patient.getFirstName() != null && patient.getLastName() != null) {
                StringBuilder sql = new StringBuilder("INSERT INTO patientTable (");
                sql.append("ARIAID, LASTNAME, FIRSTNAME");
                if (map.get("BirthDay") != null) {
                    sql.append(",BIRTHDAY");
                }
                if (map.get("DeathDay") != null) {
                    sql.append(",DEATHDAY");
                }
                if (map.get("Study") != null) {
                    sql.append(",STUDY");
                }
                if (map.get("Pretherapy") != null) {
                    sql.append(",PRETHERAPY");
                }
                if (map.get("Sex") != null) {
                    sql.append(",SEX");
                }
                sql.append(", CREATEDATE, MODIFYDATE) VALUES ('");
                sql.append(patient.getAriaID()).append("','");
                sql.append(patient.getLastName()).append("','");
                sql.append(patient.getFirstName()).append("',");
                if (map.get("BirthDay") != null) {
                    sql.append("'").append(map.get("BirthDay")).append("',");
                }
                if (map.get("DeathDay") != null) {
                    sql.append("'").append(map.get("DeathDay")).append("',");
                }
                if (map.get("Study") != null) {
                    sql.append("'").append(map.get("Study")).append("',");
                }
                if (map.get("Pretherapy") != null) {
                    sql.append("'").append(map.get("Pretherapy")).append("',");
                }
                if (map.get("Sex") != null) {
                    sql.append("'").append(map.get("Sex")).append("',");
                }
                sql.append("CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);");
                System.out.println(sql);
                return stmt.executeUpdate(sql.toString()) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler - Datenbankfehler");
            alert.setHeaderText("Datenbankfehler");
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
        }
        return false;
    }

    public boolean updatePatient(Patient patient, String ariaID) {
        HashMap map = patient.toMap();
        try {
            if (patient.getAriaID() != null && patient.getFirstName() != null && patient.getLastName() != null) {
                StringBuilder sql = new StringBuilder("UPDATE patientTable SET ");
                sql.append("ARIAID ='").append(patient.getAriaID()).append("',");
                sql.append("LASTNAME ='").append(patient.getLastName()).append("',");
                sql.append("FIRSTNAME ='").append(patient.getFirstName()).append("',");
                if (map.get("BirthDay") != null) {
                    sql.append("BIRTHDAY ='").append(map.get("BirthDay")).append("',");
                }
                if (map.get("DeathDay") != null) {
                    sql.append("DEATHDAY ='").append(map.get("DeathDay")).append("',");
                }
                if (map.get("Study") != null) {
                    sql.append("STUDY ='").append(map.get("Study")).append("',");
                }
                if (map.get("Pretherapy") != null) {
                    sql.append("PRETHREAPY ='").append(map.get("Pretherapy")).append("',");
                }
                if (map.get("Sex") != null) {
                    sql.append("SEX ='").append(map.get("Sex")).append("',");
                }
                sql.append("MODIFYDATE =CURRENT_TIMESTAMP WHERE ARIAID ='").append(ariaID).append("';");
                System.out.println(sql);
                return stmt.executeUpdate(sql.toString()) > 0;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fehler - Datenbankfehler");
            alert.setHeaderText("Datenbankfehler");
            alert.setContentText(ex.getLocalizedMessage());
            alert.showAndWait();
        }
        return false;
    }

    public void closeDB() {
        try {
            System.out.println("[i] " + LocalDate.now() + " " + LocalTime.now() + " Connection is closing");
            this.con.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean deletePatient(Patient patient) {
        try {
            return stmt.executeUpdate("DELETE FROM PATIENTTABLE WHERE ARIAID='" + patient.getAriaID() + "'") > 0;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
