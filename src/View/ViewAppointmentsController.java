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

public class ViewAppointmentsController implements Initializable {


    public TextArea monthView;
    private String appointmentId, location, type, start;

    public TextArea weekView;
    private String apptId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            setWeeklyAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void setWeeklyAppointments() throws SQLException {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime week = now.plusWeeks(1);
        LocalDateTime month = now.plusMonths(1);

        Date d;
        LocalDateTime startPoint;
        String s;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT appointmentId, location, type, start FROM appointment WHERE createdBy = ?");
        ps.setString(1, LoginController.getUserName());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {

            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


            LocalDateTime oldDateTime = LocalDateTime.parse(rs.getString(4).substring(0,13), formatter);
            ZoneId oldZone = ZoneId.of("America/Los_Angeles");

            ZoneId newZone = ZoneId.of(TimeZone.getDefault().getID());
            LocalDateTime newDateTime = oldDateTime.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime();


                appointmentId = rs.getString(1);
                location = rs.getString(2);
                type = rs.getString(3);
                //start = rs.getString(4);
                start = newDateTime.format(formatter2);

            System.out.println(start.substring(0,13));
                startPoint = LocalDateTime.parse(start.substring(0,13), formatter);

            //if(LocalDateTime.parse(start).isAfter(now) && LocalDateTime.parse(start).isBefore(week)) {
            if(startPoint.isAfter(now) && startPoint.isBefore(week)) {
                String toAppend = "Appt ID: " + appointmentId + "\n" + "Location: " +  location + "\n" + "Type: " + type + "\n" + "Start Date/Time: " + start;
                weekView.appendText(toAppend);
                weekView.appendText("\n\n");
            }
            //changed isAfter from week to now and converted else if to if
            // - purpose to have all appointments within 1 month display instead of only appointments outside the week range but within the month
            if(startPoint.isAfter(now) && startPoint.isBefore(month)){
                String toAppend = "Appt ID: " + appointmentId + "\n" + "Location: " +  location + "\n" + "Type: " + type + "\n" + "Start Date/Time: " + start;
                monthView.appendText(toAppend);
                monthView.appendText("\n\n");
            }
            else
                System.out.println("outside the date range");
        }

    }

    public void home(ActionEvent e) throws IOException {

        Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
    }


}
