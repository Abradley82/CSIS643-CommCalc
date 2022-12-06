/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package commcalc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 *
 * @author mhodge
 */
public class CommCalc {
    
    // credentials to connect to and query the database
    private static final String getURL = "jdbc:mysql://localhost:3306/agentportaldb";
    private static final String insURL = "jdbc:mysql://localhost:3306/commissioncalculationdb";
    private static final String payURL = "jdbc:mysql://localhost:3306/payrolldb";
    private static final String USERNAME = "NONE";
    private static final String PASSWORD = "NONE";
    private static final String commcatdb = "commcalcreports";
    private static final String paydb = "monthlypayroll";
    private static Connection connection;
    
    public static LocalDate date = java.time.LocalDate.now();
    
    public String getQuery(String date){
        return String.format("SELECT agent_sales.salesPeriod, agent_portal.agentID," +
                "agent_portal.firstName, agent_portal.lastName, agent_sales.totalSales, " +
                "agent_sales.totalSales * 0.03 AS commission FROM agent_portal " +
                "INNER JOIN agent_sales ON agent_portal.agentID = agent_sales.agentID " +
                "WHERE agent_sales.salesPeriod LIKE '%s'AND agent_portal.activeStatus = 'active';", date);
    }
    
    
    public String delQuery( String saledate){
        return String.format("DELETE FROM commcalcreports "
                + "WHERE salesPeriod = '%s';",saledate);
    }
    
    public String payDelQuery( String saledate){
        return String.format("DELETE FROM monthlypayroll "
                + "WHERE payPeriod = '%s';", saledate);
    }
    
    
    public String insPayroll(String saledate, String agentID, String commission){
        return String.format("INSERT INTO monthlypayroll (payPeriod, agentID, "
                + "basePay, commission) VALUES ('%s', '%s', '2000', '%s');",
                saledate, agentID, commission);
    }
    
    // Clears previous data if report has been generated and inserts new/updated data
    public String insQuery(String saledate, String agentID, String fname, String lname, String sales, String comm){
        return String.format("INSERT INTO commcalcreports (salesPeriod, agentID, "
                + "firstName, lastName, totalSales, commission) "
                + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                 saledate, agentID, fname, lname, sales, comm);
    }
    
    public void deletePayrollComm(String saledate){
        try{

            connection = DriverManager.getConnection(payURL, USERNAME, PASSWORD);
            Statement st = connection.createStatement();
            
            String sqlDelete = payDelQuery(saledate);
            
            st.executeUpdate(sqlDelete);
            
            connection.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }        
    }
    
    public void insertPayroll(String saledate, String agentID, String comm){
        try{
            
            connection = DriverManager.getConnection(payURL, USERNAME, PASSWORD);
            Statement st = connection.createStatement();
            
            String sqlInsert = insPayroll(saledate, agentID, comm);
            
            st.executeUpdate(sqlInsert);

            connection.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }        
    }
    
    
    public void deleteCommCalc(String saledate){
        try{

            connection = DriverManager.getConnection(insURL, USERNAME, PASSWORD);
            Statement st = connection.createStatement();
            
            String sqlDelete = delQuery(saledate);

            System.out.println(sqlDelete);
            
            st.executeUpdate(sqlDelete);
            
            connection.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }        
    }
    
    // Adds the data into the commission calculation commission database
    public void insertCommCalc(String saledate, String agentID, String fname, String lname, String sales, String comm){
        try{
            
            connection = DriverManager.getConnection(insURL, USERNAME, PASSWORD);
            Statement st = connection.createStatement();
            
            String sqlInsert = insQuery(saledate, agentID, fname, lname, sales, comm);
            
            st.executeUpdate(sqlInsert);

            connection.close();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }        
    }
    
    // Gets the data for the commission report
    public void commissionCalculation(){
        
        int year = date.getYear();
        int month = date.getMonthValue();
        String reportDate = String.format("%s-%s-01", year, month);
        
        deleteCommCalc(reportDate);
        deletePayrollComm(reportDate);
        
        try{
            
            connection = DriverManager.getConnection(getURL, USERNAME, PASSWORD);
            Statement st = connection.createStatement();

            String sql = getQuery(reportDate);
            
            ResultSet rs = st.executeQuery(sql);

            while(rs.next()){
                String salesdate = String.valueOf(rs.getString("salesPeriod"));
                String agentid = String.valueOf(rs.getString("agentID"));
                String firstName = String.valueOf(rs.getString("firstName"));
                String lastName = String.valueOf(rs.getString("lastName"));
                String sales = String.valueOf(rs.getString("totalSales"));
                String comm = String.valueOf(rs.getString("commission"));
                
                insertCommCalc(salesdate, agentid, firstName, lastName, sales, comm);
                insertPayroll(salesdate, agentid, comm);
            }
            connection.close();             
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }           
    }
}
