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
import java.util.*;

public class LoginController implements Initializable {


    public PasswordField passwordField;
    public TextField usernameField;

    private static String user, password;
    public TextArea userLocationTextArea;

    public Button loginButton;
    public Button loginSaveButton;
    public Label titleLabel;
    public Label usernameLabel;
    public Label passwordLabel;
    public Button germanButton;

    private String last4 = "createDate = current_timestamp, createdBy = ?, lastUpdate = current_timestamp, lastUpdateBy = ?";
    private ResourceBundle rb;


    public void germanButtonUsed(ActionEvent e){

        Locale locale = Locale.getDefault();
        rb = ResourceBundle.getBundle("MiscFiles/Lang", locale);
        usernameLabel.setText(rb.getString("username"));
        passwordLabel.setText(rb.getString("password"));
        loginButton.setText(rb.getString("login"));
        loginSaveButton.setText(rb.getString("new"));
        userLocationTextArea.setText(rb.getString("location") + " - " + locale);
        titleLabel.setText(rb.getString("title"));
    }

    public void userLocationOnScreen(){

        userLocationTextArea.setText(Locale.getDefault().toString() + "\nEnglish Detected \n" +
                "German translation available for non U.S. users:\n" +
                "- change language to non English \nor press the Deutsche button");
    }

    public void saveNewUser(ActionEvent actionEvent) throws SQLException {

        user = usernameField.getText();
        password = passwordField.getText();

        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM `user` WHERE userName = ?");
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();

        PreparedStatement uid = DatabaseConnection.getConnection().prepareStatement("SELECT userId FROM user WHERE userName = ? AND password = ?");
        uid.setString(1, user);
        uid.setString(2, password);

        PreparedStatement insert = DatabaseConnection.getConnection().prepareStatement("INSERT INTO `user` SET userName = ?, password = ?, active = 1, " + last4);
        if(!rs.next()){
            insert.setString(1, user);
            insert.setString(2, password);
            insert.setString(3,  user);
            insert.setString(4,  user);
            insert.executeUpdate();

            System.out.println("new user created: " + user);
            //System.out.println ("neuer Benutzer erstellt:" + user);
        }

        else{
            System.out.println("operation failed - username already exists");
            //System.out.println ("Operation fehlgeschlagen - Benutzername existiert bereits");
        }
    }


    public void recordLoginActivity() throws IOException {

        GregorianCalendar gc = new GregorianCalendar();
        FileWriter fw = new FileWriter("src/Util/UserActivity.txt",true);
        PrintWriter out = new PrintWriter(fw);
        out.println("Username: '" + user + "' - Login: " + gc.getTime());
        out.close();

    }

     /*
     * Section F: entering an incorrect username and password
     */
    public void login(ActionEvent e) throws SQLException, IOException {

        user = usernameField.getText();
        password = passwordField.getText();
        Locale locale = Locale.getDefault();
        //Locale locale = new Locale("ar", "SA");

        rb = ResourceBundle.getBundle("MiscFiles/Lang", locale);


        getUserName();

        PreparedStatement st = DatabaseConnection.getConnection().prepareStatement("SELECT userName, password FROM `user` WHERE userName = ? AND password = ?" );
        st.setString(1, user);
        st.setString(2, password);
        ResultSet rs = st.executeQuery();
        //String userId = rs.getString(1);
        //System.out.println(userId);

        if(rs.next()){
            if (rs.getString(1).equals(user) && rs.getString(2).equals(password)){
            System.out.println("login successful");
            recordLoginActivity();
            //customerIdAtLogin();


            Parent p = FXMLLoader.load(getClass().getResource("Home.fxml"));
            Scene sc = new Scene(p);
            Stage s = (Stage) ((Node) e.getSource()).getScene().getWindow();
            s.setScene(sc);
            s.show();
            }

        }
        else {

            if(locale.getDisplayCountry().equals("English"))
                System.out.println("login failed");
            else if(!locale.getDisplayLanguage().equals("English")){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("\n" + "Anmeldung nicht erfolgreich");
                a.setTitle(rb.getString("alert"));
                a.show();
            }
        }

    }


    public static String getUserName(){
        return user;
    }

    public static String customerIdAtLogin() throws SQLException {

        PreparedStatement psAddressId = DatabaseConnection.getConnection().prepareStatement("SELECT DISTINCT addressId FROM address WHERE createdBy = ?");
            psAddressId.setString(1, user);
            ResultSet rsAddress = psAddressId.executeQuery();
            rsAddress.next();
            String addressId = rsAddress.getString(1);

            PreparedStatement psCustomerId = DatabaseConnection.getConnection().prepareStatement("SELECT DISTINCT customerId FROM customer WHERE addressId = ?");
            psCustomerId.setString(1, addressId);
            ResultSet rsCustomerId = psCustomerId.executeQuery();
            rsCustomerId.next();
            String customerId = rsCustomerId.getString(1);

            //customerIdOut = customerId;

            return customerId;
        }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userLocationOnScreen();
        Locale locale = Locale.getDefault();
        //Locale locale = new Locale("ar", "SA");
        rb = ResourceBundle.getBundle("MiscFiles/Lang", locale);


        System.out.println(locale.getDisplayLanguage());
        //System.out.println(locale.getDisplayCountry());
        //System.out.println(locale.getLanguage());

        if(locale.getDisplayLanguage() != "English") {
            usernameLabel.setText(rb.getString("username"));
            passwordLabel.setText(rb.getString("password"));
            loginButton.setText(rb.getString("login"));
            loginSaveButton.setText(rb.getString("new"));
            userLocationTextArea.setText(rb.getString("location") + " - " + locale);
            titleLabel.setText(rb.getString("title"));
            /*Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(rb.getString("alert"));
            a.setContentText("alertDesc");
            a.show();*/
        }


    }
}
