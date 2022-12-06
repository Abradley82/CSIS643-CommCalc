/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commcalc;

/**
 *
 * @author mhodge
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class ReportQuery {
    // credentials to connect to and query the database
    private static final String URL = "jdbc:mysql://localhost:3306/commissioncalculationdb";
    private static final String USERNAME = "NONE";
    private static final String PASSWORD = "NONE";
    private static Connection connection;
      
    // Creates a query that will be used to display the reports    
    public String getReportQuery(String reportPeriod){
        return String.format("SELECT salesPeriod, agentID, lastName, "
                + "firstName,  totalSales, commission FROM commcalcreports "
                + "WHERE salesPeriod LIKE '%s' ORDER BY lastName", reportPeriod);
    }
    
    // Clears table after a new report period is selected     
    public void clearTable(JTable ReportTable){
        DefaultTableModel tblModel = (DefaultTableModel)ReportTable.getModel();
        tblModel.setRowCount(0);
    }
    
    // Query to get the date for the reports
    public int getReport(JTable table, String reportPeriod){

        try{
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement st = connection.createStatement();
            
            clearTable(table);
            
            String sql = getReportQuery(reportPeriod);
            
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                String reportDate = String.valueOf(rs.getString("salesPeriod"));
                String id = String.valueOf(rs.getString("agentID"));
                String lname = String.valueOf(rs.getString("lastName"));
                String fname = String.valueOf(rs.getString("firstName"));
                String sales = String.valueOf(rs.getString("totalSales"));
                String comm = String.valueOf(rs.getString("commission"));
          
                String tbData[] = {reportDate, id, lname, fname, sales, comm};
                DefaultTableModel tblModel = (DefaultTableModel)table.getModel();
                
                tblModel.addRow(tbData);     
            }
            connection.close();
            return 1;     
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }        
        return 0;
    }   
  
    //Query to get the report dates for the reports view options
    public ArrayList<String> dateChecker(){
        
        ArrayList <String> dates = new ArrayList<String>();
        try{
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement st = connection.createStatement();
            
            String sql = "SELECT DISTINCT salesPeriod FROM commcalcreports";
            
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                String reportDate = String.valueOf(rs.getString("salesPeriod"));
                                
                dates.add(reportDate);       
            }
            connection.close();   
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return dates;
    }
}
