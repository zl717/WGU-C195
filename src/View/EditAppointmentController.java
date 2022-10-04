package View;

import Util.*;
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

import static java.time.LocalDate.parse;

public class EditAppointmentController implements Initializable {

    public TextField titleField;
    public TextField descriptionField;
    public ComboBox locationComboBox;
    public TextField contactNameField;
    public ComboBox typeComboBox;
    public DatePicker datePicker;
    public ComboBox startPicker;
    public ComboBox endPicker;
    public ComboBox contactSelect;
    public Label startLabel;
    public Label endLabel;

    private String apptId;

    public String title;
    public String description;
    public String location;
    public String contactName;
    public String type;
    private LocalDateTime dbStartTime;
    private LocalDateTime dbEndTime;
    private String start;
    private String end;
    private Date date;

    private LocalDateTime STARTTIME;
    private String startTime;
    private String endTime;

    private String startInitial, endInitial;

    private static String user = LoginController.getUserName();

    private int[] startTimesArrray = new int[]{10,11,12,13,14,15,16};
    private int[] endTimesArrray = new int[]{11,12,13,14,15,16,17};

    //private LocalDateTime[] starts, ends;
    //private ArrayList<LocalDateTime> starts = new ArrayList<LocalDateTime>();
    private static ArrayList<String> starts = new ArrayList<>();
    private static ArrayList<String> ends = new ArrayList<>();


    public static ArrayList<String> getStarts(){
        return starts;
    }

    public static ArrayList<String> getEnds(){
        return ends;
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
            contactSelect.getItems().add(rs.getObject(1));
        }

    }

    public void changePickersToZone(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        //startPicker.getItems().clear();

        for (int i = 0; i < startTimesArrray.length; i++) {
            LocalDateTime oldDateTime = LocalDateTime.parse(date + " " + startTimesArrray[i], formatter);
            ZoneId oldZone = ZoneId.of("America/Los_Angeles");
            ZoneId newZone = ZoneId.of(TimeZone.getDefault().getID());
            LocalDateTime newDateTime = oldDateTime.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime();
            starts.add(newDateTime.toString().substring(11));

            LocalDateTime oldDateTimeEnd = LocalDateTime.parse(date + " " + endTimesArrray[i], formatter);
            ZoneId oldZoneEnd = ZoneId.of("America/Los_Angeles");
            ZoneId newZoneEnd = ZoneId.of(TimeZone.getDefault().getID());
            LocalDateTime newDateTimeEnd = oldDateTimeEnd.atZone(oldZoneEnd).withZoneSameInstant(newZoneEnd).toLocalDateTime();
            ends.add(newDateTimeEnd.toString().substring(11));

        }

        startPicker.getItems().addAll(starts.toArray());
        endPicker.getItems().addAll(ends.toArray());

    }

    @Override
    public void initialize(URL loc, ResourceBundle resources) {


        apptId = HomeController.getEditSelection().getID();
        try {
            populate();
            populateContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println(homeController.getEditSelection().getID());

        locationComboBox.getItems().addAll("Phoenix, AZ", "New York, NY", "London, UK");
        locationComboBox.setValue(location);
        typeComboBox.getItems().addAll("Phone", "Company Office", "My Office");
        typeComboBox.setValue(type);

        titleField.setText(title);
        descriptionField.setText(description);
        //contactNameField.setText(contactName);
        contactSelect.setValue(contactName);
        String date = start.toString().substring(0,10);
        LocalDate d = parse(date);
        datePicker.setValue(d);

        startPicker.getItems().addAll("10 am", "11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm");
        if(Integer.parseInt(startTime) < 12){
            startPicker.setValue(startTime + " am");
        }
        else if(Integer.parseInt(startTime) == 12){
            startPicker.setValue(Integer.parseInt(startTime) + " pm");
        }
        else{
            startPicker.setValue((Integer.parseInt(startTime) - 12) + " pm");
        }

        endPicker.getItems().addAll("11 am", "12 pm", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm");
        if(Integer.parseInt(endTime) < 12){
            endPicker.setValue(endTime + " am");
        }

        else if(Integer.parseInt(endTime) == 12){
            endPicker.setValue(Integer.parseInt(endTime) + " pm");
        }
        else {
            endPicker.setValue((Integer.parseInt(endTime) - 12) + " pm");
        }

        startInitial = startPicker.getSelectionModel().getSelectedItem().toString();
        endInitial = endPicker.getSelectionModel().getSelectedItem().toString();

        TimeZone tz = TimeZone.getDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        //System.out.println(tz.getID());
        LocalDateTime startPickerTimeInit = LocalDateTime.parse(HomeController.getEditSelection().getStart().substring(0,13), formatter);
        ZonedDateTime setOne = startPickerTimeInit.atZone(TimeZone.getDefault().toZoneId());
        String s = setOne.toLocalDateTime().toString().substring(11);

        LocalDateTime endPickerTimeInit = LocalDateTime.parse(HomeController.getEditSelection().getEnd().substring(0,13), formatter);
        ZonedDateTime setOneEnd = endPickerTimeInit.atZone(TimeZone.getDefault().toZoneId());
        String s2 = setOneEnd.toLocalDateTime().toString().substring(11);

        if(!tz.getID().equals("America/Los_Angeles")){
            startPicker.getItems().clear();
            endPicker.getItems().clear();
            changePickersToZone();
            startPicker.setValue(s);
            endPicker.setValue(s2);
        }
        else{
            System.out.println("sddgsdgsdgsdgfsdg");
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


        //System.out.println(getStartTime() + " " + getEndTime() + "start/end on init");

    }
    
    public void populate() throws SQLException {

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM appointment WHERE appointmentId = ?");
        ps.setString(1, apptId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            title = rs.getString(4);
            description = rs.getString(5);
            location = rs.getString(6);
            contactName = rs.getString(7);
            type = rs.getString(8);
            date = rs.getDate(10);//
            start = rs.getString(10);
            //System.out.println(rs.getString(10).substring(10,13).trim() + " " + rs.getString(10));
            startTime = rs.getString(10).substring(10,13).trim();
            end = rs.getString(11);
            endTime = rs.getString(11).substring(10,13).trim();

            System.out.println(start + " " + end);
        }
        
    }


    public boolean checkOverlap() throws SQLException {

        boolean notOverlapping = false;

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


    public void update(ActionEvent e) throws SQLException, IOException {

        // scheduling an appointment outside business hours
        getStartTime();
        getEndTime();
        checkTime();

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE appointment SET title = ?, description = ?, location = ?, contact = ?, type = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP WHERE appointmentId = ?");
        ps.setString(1, titleField.getText());
        ps.setString(2, descriptionField.getText());
        ps.setString(3, locationComboBox.getValue().toString());
        //ps.setString(4, contactNameField.getText());
        ps.setString(4, contactSelect.getValue().toString());
        ps.setString(5, typeComboBox.getValue().toString());
        ps.setObject(6, getStartTime());
        ps.setObject(7, getEndTime());
        ps.setString(8, apptId);

        if(checkOverlap()){
            ps.executeUpdate();
            Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Scene sc = new Scene(p);
            Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
            s.setScene(sc);
            s.show();
            System.out.println(checkOverlap());
        }
        else{
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Time mismatch");
            a.setContentText("Times " + getStartTime() + " and " + getEndTime() + " not available, try again.");
            a.show();
        }

    }



    public LocalDateTime getStartTime(){
        LocalDate date = datePicker.getValue();
        //System.out.println(d);
        String start = (String) startPicker.getValue();
        String time = start.substring(0,2).trim();
        String stamp = date + " " + time;
        String newStamp = "";

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            dbStartTime = LocalDateTime.parse(stamp, formatter);

        }
        catch(Exception ex){
            //System.out.println("after 1pm");
            int timePlus12 = Integer.parseInt(time) + 12;

            newStamp = date + " " + Integer.toString(timePlus12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            dbStartTime = LocalDateTime.parse(newStamp, formatter);
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
        }
        catch(Exception ex){
            //System.out.println("after 1pm");
            int timePlus12 = Integer.parseInt(time) + 12;
            newStamp = date + " " + Integer.toString(timePlus12);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            dbEndTime = LocalDateTime.parse(newStamp, formatter);
            //System.out.println("END : " + dbEndTime);
        }

        return dbEndTime;

    }


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


}
