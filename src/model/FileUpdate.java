/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class FileUpdate {
    public FileInputStream fin;
    public BufferedInputStream bin;
    public FileOutputStream fout;
    public BufferedOutputStream bout;
    
    
 public void update(String data){    
    try{    
        fin=new FileInputStream("C:\\Users\\DELL\\Documents\\SimpleSppechCalculator\\build\\classes\\grammars\\grammar.gram");    
        bin=new BufferedInputStream(fin);   
        char info[]=data.toCharArray();
        ArrayList<Character> arr=new ArrayList<>();
        int i;    
        char x=')';
        while((i=bin.read())!=-1){
            if((char)i==x)
            {
                arr.add(' ');
                arr.add('|');
                for(char ch:info){
                    arr.add(ch);
                }
                x='!';
            }
            arr.add((char)i);
        System.out.print((char)i);    
    }    
    String s="";
    System.out.println();
    for(char ch:arr){
        s=s+ch;
         System.out.print(ch);
    }
    byte b[]=s.getBytes();
    bin.close();    
    fin.close();    
     fout=new FileOutputStream("C:\\Users\\DELL\\Documents\\SimpleSppechCalculator\\build\\classes\\grammars\\grammar.gram");    
     bout=new BufferedOutputStream(fout);   
     bout.write(b);    
     bout.flush();    
     bout.close();    
        fout.close();  
        JOptionPane.showMessageDialog(null,"Inserted successfully to file","Success",JOptionPane.INFORMATION_MESSAGE);
     
  }catch(Exception e){System.out.println(e);}    
 }    
 
 //update songs
 
 public void updateSong(String data){    
  try{    
    fin=new FileInputStream("C:\\Users\\DELL\\Documents\\SimpleSppechCalculator\\build\\classes\\grammars\\grammar.gram");    
        bin=new BufferedInputStream(fin);   
    char info[]=data.toCharArray();
    ArrayList<Character> arr=new ArrayList<>();
    int i;    
    char x=')';
    int count=0;
    while((i=bin.read())!=-1){
        if((char)i==x)
        {
            if(count==0){
                count++;
               
            }
            else if(count==1){
                arr.add(' ');
                arr.add('|');
                for(char ch:info){
                    arr.add(ch);
                }
                x='!';
            }
        }
        arr.add((char)i);
     System.out.print((char)i);    
    }    
    String s="";
    System.out.println();
    for(char ch:arr){
        s=s+ch;
         System.out.print(ch);
    }
    byte b[]=s.getBytes();
    bin.close();    
    fin.close();    
     fout=new FileOutputStream("C:\\Users\\DELL\\Documents\\SimpleSppechCalculator\\build\\classes\\grammars\\grammar.gram");    
     bout=new BufferedOutputStream(fout);   
     bout.write(b);    
     bout.flush();    
     bout.close();    
        fout.close();  
        JOptionPane.showMessageDialog(null,"Inserted successfully to file","Success",JOptionPane.INFORMATION_MESSAGE);
  }catch(Exception e){System.out.println(e);}    
 }
}  

