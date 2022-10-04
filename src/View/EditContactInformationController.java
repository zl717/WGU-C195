package View;

import Model.*;
import Util.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import javax.xml.crypto.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class EditContactInformationController implements Initializable {

    public TextField nameField;
    public TextField phoneField;
    public TextField addressField;
    public TextField addressLine2;
    public TextField countryField;
    public TextField postalCodeField;
    public TextField cityField;

    public ComboBox customerPicker;

    private ArrayList<ContactInfo> contacts = SaveNewContactInfoScreenController.getContacts();

    LoginController lc = new LoginController();
    User u = new User();

    //private static String user = LoginController.getUserName();
    private static String createdBhai = LoginController.getUserName();
    private String user;// = LoginController.getUserName();
    // = customerPicker.getSelectionModel().getSelectedItem().toString();
    private String user1;

    public void editInfoSave(ActionEvent e) throws SQLException, IOException {

        //customerPicker.getItems().remove(user);
        String name = nameField.getText();
        //customerPicker.getItems().add(name);

        //changed createdBy to lastUpdateBy down the line
        //changing lastupdatedby from user to name
        PreparedStatement psCountry = DatabaseConnection.getConnection().prepareStatement("UPDATE country SET country = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? WHERE lastUpdateBy = ?");
        psCountry.setString(1, countryField.getText());
        psCountry.setString(2, name);
        psCountry.setString(3, user);
        psCountry.executeUpdate();


        PreparedStatement psAddress = DatabaseConnection.getConnection().prepareStatement("UPDATE address SET address = ?, address2 = ?, postalCode = ?, phone = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? WHERE lastUpdateBy = ?");
        psAddress.setString(1, addressField.getText());
        psAddress.setString(2, addressLine2.getText());
        psAddress.setString(3, postalCodeField.getText());
        psAddress.setString(4, phoneField.getText());
        psAddress.setString(6, user);
        psAddress.setString(5, name);

        psAddress.executeUpdate();


        PreparedStatement psCity = DatabaseConnection.getConnection().prepareStatement("UPDATE city SET city = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? WHERE lastUpdateBy = ?");
        psCity.setString(1, cityField.getText());
        psCity.setString(3, user);
        psCity.setString(2, name);

        psCity.executeUpdate();

        PreparedStatement psCustomer = DatabaseConnection.getConnection().prepareStatement("UPDATE customer SET customerName = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? WHERE lastUpdateBy = ?");
        psCustomer.setString(1, nameField.getText());
        //changed from user
        psCustomer.setString(3, user);
        psCustomer.setString(2, name);

        psCustomer.executeUpdate();
        customerPicker.getItems().remove(user);



        Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();

    }

    public void deleteRecords(String sql){

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user1);
            ps.executeUpdate();
            System.out.println("deleted");
        }

        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed delete");
        }

    }




    public void deleteCustomerInfo(ActionEvent e) throws SQLException, IOException {

        //Connection c = databaseConnection.getConnection();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("This operation will delete ALL data for: " + user + "\nincluding login information. \nThis action cannot be undone.");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType("Delete");
        ButtonType buttonTypeTwo = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne){

            //createdBy -> lastUpdateBy
            deleteRecords("DELETE FROM customer WHERE lastUpdateBy = ? AND customerId <> 0;");
            deleteRecords("DELETE FROM address WHERE lastUpdateBy = ? AND addressId <> 0;");
            deleteRecords("DELETE FROM city WHERE lastUpdateBy = ? AND cityId <> 0");
            deleteRecords("DELETE FROM country WHERE lastUpdateBy = ? AND countryId <> 0");
            deleteRecords("DELETE FROM appointment WHERE lastUpdateBy = ? AND appointmentId <> 0");
            //deleteRecords("DELETE FROM user WHERE createdBy = ?");
            customerPicker.getItems().remove(user1);
            System.out.println("USER : " + user + " --- USER1: " + user1);

            Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Scene sc = new Scene(p);
            Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
            s.setScene(sc);
            s.show();
            //customerPicker.getItems().remove(user);


        } else if (result.get() == buttonTypeTwo) {
            System.out.println("failed to delete");
        }

        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SET foreign_key_checks = 1");
            ps.execute();
        } catch (SQLException eX) {
            eX.printStackTrace();
        }

    }

    public void home(ActionEvent e) throws IOException {
        Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();
    }

    //filling the combo box, not setting user
    public void fillCustomerPicker() throws SQLException {

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT customerName FROM customer WHERE createdBy = ?");
        ps.setString(1, createdBhai);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            customerPicker.getItems().add(rs.getObject(1));
        }

    }

    public void getCustomerSelection(ActionEvent ex){
        user = customerPicker.getSelectionModel().getSelectedItem().toString();
        user1 = customerPicker.getValue().toString();
        System.out.println(user1);
        setCustomerForEdit();
        //return user;
    }

    public void setCustomerForEdit(){

        //user = customerPicker.getSelectionModel().getSelectedItem().toString();
        try {
            PreparedStatement psCustomer = DatabaseConnection.getConnection().prepareStatement("SELECT customerName FROM customer WHERE lastUpdateBy = ?");
            psCustomer.setString(1, user);
            ResultSet rsCustomer = psCustomer.executeQuery();
            if(rsCustomer.next()){
                nameField.setText(rsCustomer.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement psCountry = DatabaseConnection.getConnection().prepareStatement("SELECT country FROM country WHERE lastUpdateBy = ?");
            psCountry.setString(1, user);
            ResultSet rs = psCountry.executeQuery();
            if(rs.next())
                countryField.setText(rs.getString(1));
        }
        catch (SQLException ev) {
            ev.printStackTrace();
        }

        try {
            PreparedStatement psAddress = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM address WHERE lastUpdateBy = ?");
            psAddress.setString(1, user);
            ResultSet rsAddress = psAddress.executeQuery();
            if(rsAddress.next()){
                addressField.setText(rsAddress.getString(2));
                addressLine2.setText(rsAddress.getString(3));
                postalCodeField.setText(rsAddress.getString(5));
                phoneField.setText(rsAddress.getString(6));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement psCity = DatabaseConnection.getConnection().prepareStatement("SELECT city FROM city WHERE lastUpdateBy = ?");
            psCity.setString(1, user);
            ResultSet rsCity = psCity.executeQuery();
            if(rsCity.next()){
                cityField.setText(rsCity.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //System.out.println(user + " " + user1);

        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SET SQL_SAFE_UPDATES = 0");
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //System.out.println(lc.getUserName());
        //System.out.println(LoginController.getUserName() + " edit init");
        try {
            //customerPicker.getItems().clear();
            fillCustomerPicker();
            //user = customerPicker.getSelectionModel().getSelectedItem().toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //String n;

       /* try {
            PreparedStatement psCustomer = DatabaseConnection.getConnection().prepareStatement("SELECT customerName FROM customer WHERE createdBy = ?");
            psCustomer.setString(1, user);
            ResultSet rsCustomer = psCustomer.executeQuery();
            if(rsCustomer.next()){
                nameField.setText(rsCustomer.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement psCountry = DatabaseConnection.getConnection().prepareStatement("SELECT country FROM country WHERE createdBy = ?");
            psCountry.setString(1, user);
            ResultSet rs = psCountry.executeQuery();
            if(rs.next())
                countryField.setText(rs.getString(1));
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement psAddress = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM address WHERE createdBy = ?");
            psAddress.setString(1, user);
            ResultSet rsAddress = psAddress.executeQuery();
            if(rsAddress.next()){
                addressField.setText(rsAddress.getString(2));
                addressLine2.setText(rsAddress.getString(3));
                postalCodeField.setText(rsAddress.getString(5));
                phoneField.setText(rsAddress.getString(6));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            PreparedStatement psCity = DatabaseConnection.getConnection().prepareStatement("SELECT city FROM city WHERE createdBy = ?");
            psCity.setString(1, user);
            ResultSet rsCity = psCity.executeQuery();
            if(rsCity.next()){
                cityField.setText(rsCity.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
*/
    }
}
