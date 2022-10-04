package View;

import Model.*;
import Util.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SaveNewContactInfoScreenController {


    public TextField nameField;
    public TextField phoneField;
    public TextField addressField;
    public TextField addressLine2;
    public TextField countryField;
    public TextField postalCodeField;
    public TextField cityField;

    private String last4 = "createDate = current_timestamp, createdBy = ?, lastUpdate = current_timestamp, lastUpdateBy = ?";

    private static ArrayList<ContactInfo> contacts = new ArrayList<>();

    private static int custId; private static String customerIdOut;

    private static String user = LoginController.getUserName();

    public static ArrayList<ContactInfo> getContacts(){
        return contacts;
    }

    public static int getCustomerId(){
        return custId;
    }

    public static String getCustIdString(){
        return customerIdOut;
    }


    public void newInfoSave(ActionEvent e) throws SQLException, IOException {

        String name = nameField.getText();
        String phone = phoneField.getText();
        String address = addressField.getText();
        String address2 = addressLine2.getText();
        String country = countryField.getText();
        String postal = postalCodeField.getText();
        String city = cityField.getText();


            //inserting user data
        String countryQuery = "INSERT INTO country SET country = ?, " +  last4;
        PreparedStatement psCountry = DatabaseConnection.getConnection().prepareStatement(countryQuery);
        psCountry.setString(1, country);
        psCountry.setString(2, user);
        psCountry.setString(3, name);
        psCountry.executeUpdate();
            //selecting countryId for later use
        PreparedStatement psCountryId = DatabaseConnection.getConnection().prepareStatement("SELECT DISTINCT countryId FROM country WHERE country = ?");
        psCountryId.setString(1, country);
        ResultSet rsCountry = psCountryId.executeQuery();
        rsCountry.next();
        String countryId = rsCountry.getString(1);
        System.out.println("country ID :" + countryId);



        PreparedStatement psCity = DatabaseConnection.getConnection().prepareStatement("INSERT INTO city SET city = ?, countryId = ?, " +  last4);
        psCity.setString(1, city);
        psCity.setString(2, countryId);
        psCity.setString(3, user);
        psCity.setString(4, name);
        psCity.executeUpdate();
        PreparedStatement psCityId = DatabaseConnection.getConnection().prepareStatement("SELECT DISTINCT cityId FROM city WHERE city = ?");
        psCityId.setString(1, city);
        ResultSet rsCity = psCityId.executeQuery();
        rsCity.next();
        String cityId = rsCity.getString(1);



        String addressQuery = "INSERT INTO address SET address = ?, address2 = ?, cityId = ?, postalCode = ?, phone = ?, " +  last4;
        PreparedStatement psAddress = DatabaseConnection.getConnection().prepareStatement(addressQuery);
        psAddress.setString(1, address);
        psAddress.setString(2, address2);
        psAddress.setString(3, cityId);
        psAddress.setString(4, postal);
        psAddress.setString(5, phone);
        psAddress.setString(6, user);
        psAddress.setString(7, name);
        psAddress.executeUpdate();
        PreparedStatement psAddressId = DatabaseConnection.getConnection().prepareStatement("SELECT DISTINCT addressId FROM address WHERE phone = ?");
        psAddressId.setString(1, phone);
        ResultSet rsAddress = psAddressId.executeQuery();
        rsAddress.next();
        String addressId = rsAddress.getString(1);


        String customerQuery = "INSERT INTO customer SET customerName = ?, addressId = ?, active = ?, " +  last4;
        PreparedStatement psCustomer = DatabaseConnection.getConnection().prepareStatement(customerQuery);
        psCustomer.setString(1, name);
        psCustomer.setString(2, addressId);
        psCustomer.setString(3, "1");
        psCustomer.setString(4, user);
        psCustomer.setString(5, name);
        psCustomer.executeUpdate();
        PreparedStatement psCustomerId = DatabaseConnection.getConnection().prepareStatement("SELECT DISTINCT customerId FROM customer WHERE addressId = ?");
        psCustomerId.setString(1, addressId);
        ResultSet rsCustomerId = psCustomerId.executeQuery();
        rsCustomerId.next();
        String customerId = rsCustomerId.getString(1);
        customerIdOut = customerId;

        // using customerId

        ContactInfo ci = new ContactInfo();
        ci.setId(Integer.parseInt(customerId));
        ci.setName(name);
        ci.setAddress(address);
        ci.setAddress2(address2);
        ci.setCity(city);
        ci.setCountry(country);
        ci.setPhone(phone);
        ci.setPostal(postal);
        contacts.add(ci);
        custId = Integer.parseInt(customerId) -1;

        System.out.println(user + " save new ");

        Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();


    }

    public void home(ActionEvent e) throws IOException {
        Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene sc = new Scene(p);
        Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
        s.setScene(sc);
        s.show();

    }

}
