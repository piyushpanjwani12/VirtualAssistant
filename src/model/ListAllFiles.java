/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dbutil.DBConnection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;


/**
 *
 * @author DELL
 */
public class ListAllFiles {
    
     /**
     * List all the files under a directory
     * @param directoryName to be listed
     */
    public FileUpdate f=new FileUpdate();
    Connection conn;
    public int listFiles(String directoryName) {
    try{
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
//            f.start();
            if (file.isFile()){
                String s=file.getName();
                if(s.endsWith(".mp3") || s.endsWith(".mp4") || s.endsWith(".wmv")){
                    try{
                        conn=DBConnection.getConnection();
                        PreparedStatement ps=conn.prepareStatement("insert into speech values (?,?)");
                        ps.setString(2, "play_"+s.toLowerCase().substring(0, s.length()-4).replaceAll(" ", "_")); //song name
                        int divide=92;
                        ps.setString(1, directoryName+(char)divide+s); //song path
                        int ans=ps.executeUpdate();
                        if(ans==1)
                        {
                            JOptionPane.showMessageDialog(null,"Inserted Successfully to database","Success",JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch(SQLException ex){
                            ex.printStackTrace();
                    }
                    f.updateSong(" play "+s.toLowerCase().substring(0, s.length()-4));
                    System.out.println(s);
                }
                
            }
        }
        return 1;
//        f.close();
    }
    catch(NullPointerException npe){
        return 0;
    }
    }
    
    
}
