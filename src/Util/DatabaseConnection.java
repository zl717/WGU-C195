package Util;

import java.sql.*;

public class DatabaseConnection {

    private static final String protocol = "jdbc:";
    private static final String vendor = "mysql:";
    private static final String ip = "3.227.166.251/U05FTZ";

    //jdbc url + driver
    private static final String URL = protocol + vendor + ip;
    private static String generatedURL = "jdbc:mysql://3.227.166.251:3306/U05FTZ";
    private static final String driver = "com.mysql.jdbc.Driver";

    private static final String user = "U05FTZ";
    private static String password = "53688486972";

    private static Connection conn;


    public static Connection startConnection() {

        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(generatedURL, user, password);
            System.out.println("connection established");
        }

        catch(ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
            System.out.println("connection failed");
        }
        return conn;
    }

    public static Connection getConnection(){
        return conn;
    }


    public static void closeConnection(){
        try {
            conn.close();
            System.out.println("closed");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("failed to close");
        }
    }


    public static void test(){
        System.out.println(generatedURL);
        System.out.println(URL);
        System.out.println();
    }

}
