/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class DBConnection {
    private static Connection conn;
    static{
        try
        {
            Class.forName("oracle.jdbc.OracleDriver");
            conn=DriverManager.getConnection("jdbc:oracle:thin:@//DELL-A:1521/XE","project","java");
            JOptionPane.showMessageDialog(null,"Connected Successfully to the DB","Success",JOptionPane.INFORMATION_MESSAGE);
            
        } 
        catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,"Class not Loaded Successfully"+ex,"Failed",JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"SQL Error"+ex,"Failed",JOptionPane.ERROR_MESSAGE);
        }
    }
    public static Connection getConnection()
    {
        return conn;
    }
    public static void closeConnection()
    {
        if(conn!=null)
        {
        	try
		{
              	    conn.close();   
                    JOptionPane.showMessageDialog(null,"Connection CLosed Successfully","Success",JOptionPane.INFORMATION_MESSAGE);
		}
		catch(SQLException e3)
		{
               	    JOptionPane.showMessageDialog(null,"Sql Error:"+e3,"Error",JOptionPane.ERROR_MESSAGE);
		}
	}
    }
}
