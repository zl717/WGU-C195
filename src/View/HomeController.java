package View;

import Util.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.sql.Date;
import java.time.*;
import java.time.format.*;
import java.util.*;
import Model.ApptTableView;

public class HomeController implements Initializable {

    public ApptTableView a = new ApptTableView();
    public Label userLocationLabel;
    public TextArea appointmentSoon;
    private ObservableList apptsList = FXCollections.observableArrayList();

    public TableView <ApptTableView> appointmentsTable;
    public TableColumn <ApptTableView, String> apptIdColumn;
    public TableColumn <ApptTableView, String> titleColumn;
    public TableColumn <ApptTableView, String> typeColumn;
    public TableColumn <ApptTableView, String> contactColumn;
    public TableColumn <ApptTableView, String> startColumn;
    public TableColumn <ApptTableView, String> endColumn;
    //public TableColumn <ApptTableView, String> timeZoneColumn;
    public TableColumn <ApptTableView, String> locationColumn;

    private String user = LoginController.getUserName();

    private int total;

    public static ApptTableView edit = new ApptTableView();

    public void OpenEditAppointment(ActionEvent e) throws IOException {

        edit = appointmentsTable.getSelectionModel().getSelectedItem();

        getEditSelection();

        Parent p = FXMLLoader.load(getClass().getResource("EditAppointment.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();

    }

    public static ApptTableView getEditSelection(){
        return edit;
    }


    public void deleteAppointment(ActionEvent e) throws SQLException {

        a = appointmentsTable.getSelectionModel().getSelectedItem();
        String delID = a.getID();
        apptsList.remove(a);

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("DELETE FROM appointment WHERE appointmentId = ?");
        ps.setString(1, delID);
        ps.executeUpdate();

        System.out.println("appointment ID: " + delID + " deleted successfully");

        appointmentsTable.setItems(apptsList);

    }

    public void openSaveNewContactInfo(ActionEvent e) throws IOException {

        Parent p = FXMLLoader.load(getClass().getResource("SaveNewContactInfoScreen.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
    }

    public void openEditContactInfo(ActionEvent e) throws IOException {

        Parent p = FXMLLoader.load(getClass().getResource("EditContactInformation.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
    }

    public void openSaveNewAppointment(ActionEvent e) throws IOException {

        Parent p = FXMLLoader.load(getClass().getResource("SaveNewAppointment.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
    }

    public void openViewAppointments(ActionEvent e) throws IOException {

        Parent p = FXMLLoader.load(getClass().getResource("ViewAppointments.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
    }

    public void getAllAppts() throws SQLException {

        //PreparedStatement ps = databaseConnection.getConnection().prepareStatement("SELECT * FROM appointment WHERE createdBy = ?");
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT appointmentId, title, type, contact, start, end, location FROM appointment WHERE createdBy = ?");

        ps.setString(1, LoginController.getUserName());
        System.out.println(LoginController.getUserName());
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


            LocalDateTime oldDateTime = LocalDateTime.parse(rs.getString(5).substring(0,13), formatter);
            ZoneId oldZone = ZoneId.of("America/Los_Angeles");

            ZoneId newZone = ZoneId.of(TimeZone.getDefault().getID());
            LocalDateTime newDateTime = oldDateTime.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime();

            LocalDateTime oldDateTimeEnd = LocalDateTime.parse(rs.getString(6).substring(0,13), formatter);
            ZoneId oldZoneEnd = ZoneId.of("America/Los_Angeles");

            ZoneId newZoneEnd = ZoneId.of(TimeZone.getDefault().getID());
            LocalDateTime newDateTimeEnd = oldDateTimeEnd.atZone(oldZoneEnd).withZoneSameInstant(newZoneEnd).toLocalDateTime();

            //System.out.println(rs.getString(1));
            ApptTableView a = new ApptTableView();
            a.setID(rs.getString(1));
            a.setTitle(rs.getString(2));
            a.setType(rs.getString(3));
            a.setContact(rs.getString(4));
            a.setStart(newDateTime.format(formatter2));
            a.setEnd(newDateTimeEnd.format(formatter2));
            a.setLocation(rs.getString(7));

            apptsList.add(a);
        }

        appointmentsTable.setItems(apptsList);

    }

    public void appointmentSoonCheck() throws SQLException {

        LocalDateTime nowLDT = LocalDateTime.now();
        LocalDateTime thenLDT = LocalDateTime.now().plusMinutes(15);

        //System.out.println("now " + nowLDT);
        //System.out.println("then " + thenLDT);


        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT start FROM appointment WHERE createdBy = ?");
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {

            Timestamp ttt = rs.getTimestamp(1);
            LocalDateTime startLDT = ttt.toLocalDateTime();

            //System.out.println("start " + startLDT);

            if (startLDT.compareTo(nowLDT) == 0 || startLDT.isAfter(nowLDT) && startLDT.isBefore(thenLDT))
                appointmentSoon.setText("appointment within 15 minutes");

        }

    }


    public void apptTypesByMonth() throws SQLException {

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT  MONTH(start) AS MONTH,\n" +
                "    type AS TYPE,\n" +
                "    count(type) AS COUNT\n" +
                "FROM appointment\n" +
                "GROUP BY MONTH(start),type;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            System.out.println("Month #: " + rs.getString(1) + " | Appt Type: " + rs.getString(2) + " | Count: " + rs.getString(3));
            total += Integer.parseInt(rs.getString(3));
        }
    }

    public void getSchedules() throws SQLException {

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT  contact AS CONTACT,\n" +
                "    type AS TYPE,\n" +
                "    start AS START,\n" +
                "    end AS END\n" +
                "FROM appointment\n" +
                "GROUP BY contact,type, start, end\n");
        ResultSet rs = ps.executeQuery();
        while(rs.next())
        System.out.println("Consultant Name: " + rs.getString(1) + " | Type: " + rs.getString(2) + " | Start: " + rs.getString(3) + "| End: " + rs.getString(4));
    }


    public void getTotalAppts(){

        System.out.println("REPORT 3: " + total + " appointments currently in the database");

    }

    public void generateReports(ActionEvent e) throws SQLException {
       apptTypesByMonth();
       getSchedules();
       getTotalAppts();

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            //generateReports();
            appointmentSoonCheck();
           // apptTypesByMonth();
            System.out.println("");
            //getSchedules();
            System.out.println("");
            //getTotalAppts();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        //System.out.println(TimeZone.getDefault().toZoneId());
        userLocationLabel.setText("User Time Zone: " + TimeZone.getDefault().toZoneId().toString());

        Locale currentLocale = Locale.getDefault();

        String locationInfo = currentLocale.getDisplayCountry() + "\n" + currentLocale.getDisplayLanguage();
        //userLocationLabel.setText(locationInfo);



        /*
        * These lambda expressions allowed me to populate the home screen table view using one line per column
        * This is more efficient than attempting to do it another way as it is write once, reuse, edit names
        */
        apptIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        startColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
        endColumn.setCellValueFactory(cellData -> cellData.getValue().endProperty());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());

        try {
            getAllAppts();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
