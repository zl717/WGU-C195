package View;

import Util.DatabaseConnection;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.*;
import java.time.format.*;
import java.util.*;


public class SaveNewAppointmentController implements Initializable {

    private static String user = LoginController.getUserName();

    public ComboBox locationComboBox;
    public ComboBox typeComboBox;
    public DatePicker datePicker;
    public ComboBox startPicker;
    public ComboBox endPicker;

    public TextField titleField;
    public TextField descriptionField;
    public TextField contactNameField;
    public ComboBox contactPicker;

    //YYYY-MM-DD hh:mm:ss mysql datetime format
    private LocalDateTime dbStartTime;
    private LocalDateTime dbEndTime;

    private String last4 = "createDate = current_timestamp, createdBy = ?, lastUpdate = current_timestamp, lastUpdateBy = ?";

    private int[] startTimesArrray = new int[]{10, 11, 12, 13, 14, 15, 16};
    private int[] endTimesArrray = new int[]{11, 12, 13, 14, 15, 16, 17};

    private ArrayList<String> starts = new ArrayList<>();
    private ArrayList<String> ends = new ArrayList<>();


    //uppercase H = 24 hour time lowercase h = 12 hour time
    public void saveNewAppointment(ActionEvent e) throws SQLException, IOException {


        //dbTime is timestamp for start time
        String title = titleField.getText();
        String description = descriptionField.getText();
        String contactName = contactNameField.getText();
        String link = "";
        String location = (String) locationComboBox.getValue();
        String type = (String) typeComboBox.getValue();


        // scheduling an appointment outside business hours
        getStartTime();
        getEndTime();
        checkTime();

        PreparedStatement appt = DatabaseConnection.getConnection().prepareStatement("INSERT INTO appointment SET customerId = ?, userId = ?, title = ?, description = ?, location = ?, contact = ?, " +
                "type = ?, url = ?, start = ?, end = ?, " + last4);
        appt.setString(1, LoginController.customerIdAtLogin());
        appt.setString(2, getUserId());
        appt.setString(3, title);
        appt.setString(4, description);
        appt.setString(5, location);
        //appt.setString(6, contactName);
        appt.setString(6, contactPicker.getValue().toString());
        appt.setString(7, type);
        appt.setString(8, link);
        appt.setObject(9, getStartTime());
        appt.setObject(10, getEndTime());
        appt.setString(11, LoginController.getUserName());
        appt.setString(12, LoginController.getUserName());


           /*Alert a = new Alert(Alert.AlertType.ERROR);
           a.setTitle("invalid data");
           a.setContentText("Some data entered was missing or invalid, try again");
           a.show();
           eziosdfnhg.printStackTrace();*/


    //

        if(checkOverlap()) {
        appt.executeUpdate();
        Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
        System.out.println(checkOverlap());
    }
        else {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Time mismatch");
        a.setContentText("Times " + getStartTime() + " and " + getEndTime() + " not available, try again.");
        a.show();
    }

}

    public String getUserId() throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT userId FROM `user` WHERE createdBy = ?");
        ps.setString(1, LoginController.getUserName());
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString(1);
    }

    public LocalDateTime getStartTime(){
        LocalDate date = datePicker.getValue();
        String start = (String) startPicker.getValue();
        String time = start.substring(0,2).trim();
        String stamp = date + " " + time;
        String newStamp = "";

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
//            LocalDateTime dateTime = LocalDateTime.parse(stamp, formatter);
            dbStartTime = LocalDateTime.parse(stamp, formatter);
           // System.out.println("START : " + dbStartTime);
            //System.out.println("12pm or before");
            //lastStamp = stamp;
        }
        catch(Exception ex){
           //System.out.println("after 1pm");
            int timePlus12 = Integer.parseInt(time) + 12;
            newStamp = date + " " + Integer.toString(timePlus12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            dbStartTime = LocalDateTime.parse(newStamp, formatter);
            //lastStamp = newStamp;
           //System.out.println("START : " + dbStartTime);
        }

        return dbStartTime;
    }

    public LocalDateTime getEndTime(){

        LocalDate date = datePicker.getValue();
        String end = (String) endPicker.getValue();
        String time = end.substring(0,2).trim();
        String stamp = date + " " + time;
        String newStamp = "";

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            dbEndTime = LocalDateTime.parse(stamp, formatter);
            //System.out.println("END : " + dbEndTime);
            //System.out.println("12pm or before");
            //lastStamp = stamp;
        }
        catch(Exception ex){
            //System.out.println("after 1pm");
            int timePlus12 = Integer.parseInt(time) + 12;
            newStamp = date + " " + Integer.toString(timePlus12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            dbEndTime = LocalDateTime.parse(newStamp, formatter);
            //lastStamp = newStamp;
            //System.out.println("END : " + dbEndTime);
        }

        return dbEndTime;

    }

    /*
    Section F: scheduling overlapping appointments
    */
    public boolean checkOverlap() throws SQLException {

        boolean notOverlapping = true;

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT start, end FROM appointment WHERE createdBy = ?");
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");


        //gui input
        LocalDateTime inputStart = getStartTime();
        LocalDateTime inputEnd = getEndTime();

        while(rs.next()){

            LocalDateTime dbStart = LocalDateTime.parse(rs.getString(1).substring(0,13), formatter);
            LocalDateTime dbEnd = LocalDateTime.parse(rs.getString(2).substring(0,13), formatter);


            if(dbStart.isAfter(inputStart) && dbEnd.isBefore(inputEnd)){
                System.out.println("not valid between");
                notOverlapping = false;
                break;
            }
            else if(dbEnd.isAfter(inputStart) && dbEnd.isBefore(inputEnd)){
                System.out.println("not valid left");
                notOverlapping = false;
                break;
            }
            else if(dbStart.isAfter(inputStart) && dbStart.isBefore(inputEnd)){
                System.out.println("not valid right");
                notOverlapping = false;
                break;
            }
            else {
                System.out.println("valid");
                notOverlapping = true;
            }

        }

        return notOverlapping;
    }

/*
Section F: entering nonexistent or invalid customer data
 */
    public boolean checkTime(){

        if (dbStartTime.isAfter(dbEndTime)){
        Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Invalid Time Combination");
            a.setContentText("Start time must be prior to end time");
            a.show();
            endPicker.setValue("");
            return false;
        }
        else
            return true;

    }


    public void cancelButton(ActionEvent e) throws IOException {
        Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
    }

    public void populateContacts() throws SQLException {

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT customerName FROM customer WHERE createdBy = ?");
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            contactPicker.getItems().add(rs.getObject(1));
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        TimeZone tz = TimeZone.getDefault();
        //System.out.println(LoginController.getUserName());

        /*
        Section F: entering nonexistent or invalid customer data + scheduling an appointment outside business hours
        Certain fields are limited to predetermined options in order to minimize user screw ups
        Appointments can't be scheduled outside business hours as they can't choose time outside business hours or past dates
        */
        try {
            populateContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        locationComboBox.getItems().addAll("Phoenix, AZ", "New York, NY", "London, UK");
        typeComboBox.getItems().addAll("Phone", "Company Office", "My Office");
        starts = EditAppointmentController.getStarts();
        ends = EditAppointmentController.getEnds();
        if(!tz.getID().equals("America/Los_Angeles")) {
            changePickersToZone();
            //startPicker.getItems().addAll(starts);
            //endPicker.getItems().addAll(ends);
        }
        else {
            startPicker.getItems().addAll("10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm");
            endPicker.getItems().addAll("11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm");
        }


        /*
        * Lambda expression disables dates prior to today's date
        * This adds efficiency as it negates the need for writing other exception controls for handling the validation of selected dates
        * It also makes it easy for the user to quickly determine today's date, streamlining the date selection process
        *
        * Doubles as exception control for Section F: entering nonexistent or invalid customer data
        */
        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

    }

    public void changePickersToZone(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        //startPicker.getItems().clear();

        for (int i = 0; i < startTimesArrray.length; i++) {
            LocalDateTime oldDateTime = LocalDateTime.parse("2000-01-01" + " " + startTimesArrray[i], formatter);
            ZoneId oldZone = ZoneId.of("America/Los_Angeles");
            ZoneId newZone = ZoneId.of(TimeZone.getDefault().getID());
            LocalDateTime newDateTime = oldDateTime.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime();
            starts.add(newDateTime.toString().substring(11));

            LocalDateTime oldDateTimeEnd = LocalDateTime.parse("2000-01-01" + " " + endTimesArrray[i], formatter);
            ZoneId oldZoneEnd = ZoneId.of("America/Los_Angeles");
            ZoneId newZoneEnd = ZoneId.of(TimeZone.getDefault().getID());
            LocalDateTime newDateTimeEnd = oldDateTimeEnd.atZone(oldZoneEnd).withZoneSameInstant(newZoneEnd).toLocalDateTime();
            ends.add(newDateTimeEnd.toString().substring(11));

        }

        startPicker.getItems().addAll(starts.toArray());
        endPicker.getItems().addAll(ends.toArray());

    }

}
