package jdbc;
/* MySqlLoadDriver2.java
 * Copyright (c) HerongYang.com. All Rights Reserved.
 */
import java.sql.*;
public class MySqlLoadDriver2 {
    public static void main(String [] args) {
        Connection con = null;
        try {

            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC driver loaded ok.");

        } catch (Exception e) {
            System.err.println("Exception: "+e.getMessage());
        }
    }
}