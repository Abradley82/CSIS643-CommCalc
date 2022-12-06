/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commcalc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author mhodge
 */
public class LoginQuery {
    
    // credentials to connect to and query the database
    private static final String URL = "jdbc:mysql://localhost:3306/logindb";
    private static final String USERNAME = "NONE";
    private static final String PASSWORD = "NONE";
    private static Connection connection;
    
    public String loginQuery(String username, String password){
        return String.format("SELECT * FROM login "
                + "WHERE username = '%s' AND password = '%s';", username, password);
    }

    public  ArrayList<String> queryLoginDB(String uname, String pword){
        
        try{
            connection = (Connection) DriverManager.getConnection(URL, USERNAME,PASSWORD);
            Statement st = connection.createStatement();
            
            String sql = loginQuery(uname, pword);
            
            ResultSet rs = st.executeQuery(sql);

            ArrayList <String> credentials = new ArrayList <String>();
            while(rs.next()){
                String u = String.valueOf(rs.getString("username"));
                String p = String.valueOf(rs.getString("password"));
                credentials.add(u);
                credentials.add(p);
            }
            connection.close();
            return credentials;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
