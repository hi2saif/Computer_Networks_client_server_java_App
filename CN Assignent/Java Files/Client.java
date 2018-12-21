/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reliabletransfer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import javax.swing.JFileChooser;

/**
 *
 * @author bad_engineer
 */
public class Client {
    StringBuffer strContent;
    int count1=0,packet=1;
    char temp;
    DatagramPacket DpReceive = null;
    DatagramPacket DpReceive7 = null;
    DatagramSocket ds;
    DatagramSocket ds2;
    DatagramSocket ds7;
    DatagramSocket ds21;
    DatagramPacket DpSendagain = null;
    InetAddress ip;
    DatagramPacket DpSend=null;
    byte[] receive2=null;
    byte[] output;
    int flag=0;
    String s1 ;
    byte[] buf = null;
    byte[] receive27 = null;
    byte[] buf2= null;
    int n=0,q2=0,k1,corrupt=-1,total=0;
    FileInputStream fin;
    String cor="-1";
    int flag1=0;
    int flag2=0,flag3=0,flag4=0,flag5=0;
    int c_packet=0;
    private BufferedWriter bfw;
   // String ipString="172.17.2.43";
    
    public void Corrupt(int a){        
        if(a>1){
            corrupt=a;       
        }
        else
            corrupt=1;//flag2=2;
    }
    
    
    public void send_again(){
        
        try {        
            try {
                ds21=new DatagramSocket();
            } catch (SocketException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            DpSendagain =
                    new DatagramPacket(output, output.length, ip, 1234);
            ds21.send(DpSendagain);
            
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void receive_Ack(){
        try {
            receive2 = new byte[65535];
            if(flag1==0){
                ds2 = new DatagramSocket(1239);
                flag1=1;
            }
            DpReceive = new DatagramPacket(receive2, receive2.length);
            try {
                bfw.write("Waiting for the Acknowledment");
                bfw.newLine();
                bfw.flush();
                ds2.receive(DpReceive);
                StringBuilder v=data(receive2);
                s1 = v.toString();              
                bfw.write("Received Ack No: " + s1);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void receive_NACK(){
          try {
            receive27 = new byte[65535];
            if(flag4==0){
                ds7 = new DatagramSocket(2239);
                flag4=1;
            }
            DpReceive7 = new DatagramPacket(receive27, receive27.length);
            try {
                bfw.write("Acknowledgement did not Matched");
                bfw.write("Waiting for the Negative Acknowledment");
                ds7.receive(DpReceive7);
                StringBuilder v=data(receive27);
                s1 = v.toString();              
                bfw.write("Received NACK.");
                
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void Send(String ipString){    
        try{
            bfw = new BufferedWriter(new FileWriter("PacketLogClient.txt",true));

        try {
           // bfw.write(fin2.getPath());
         //      JFileChooser File1 = new JFileChooser();
       //     if(File.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
      //          File file = File1.getSelectedFile();      
     //   } 
            ds = new DatagramSocket();           
            ip = null;
            try {
                ip = InetAddress.getByName(ipString);
            } catch (UnknownHostException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
           int ch;
          strContent = new StringBuffer("");
          fin = null;
            try{
                  fin = new FileInputStream(Menu.Filename);
                  //int file_size=fin.available();
                   while( (ch = fin.read()) != -1)
                   {
                      
                       if(n==30 ){
                           count1++; 
                           packet++;
                           
                           n=0;
                           buf=String.valueOf(strContent).getBytes();
                           q2++;
                           if(q2>9)
                                q2=1;
                           Checksum checksumEngine = new Adler32();
                           checksumEngine.update(buf, 0, buf.length);
                           long checksum = checksumEngine.getValue();
                           String checksum1=String.valueOf(checksum);
                           String prepend = String.valueOf(q2); 
                           byte[] pBytes = prepend.getBytes();  
                           byte[]checksum2=checksum1.getBytes();
                           output = new byte[pBytes.length + buf.length+checksum2.length];
                           for(int i=0;i<pBytes.length;i++){
                                  output[i] = pBytes[i];
                                    }
            int z=0;
        for(int j=pBytes.length;j<(buf.length+prepend.length());j++){
          output[j] = buf[z];
         z++;
}
        int z1=0;
        for(k1=pBytes.length+buf.length;k1<(buf.length+prepend.length()+checksum1.length());k1++){
        output[k1] = checksum2[z1];
         z1++;
}
                    if(count1==corrupt){
                                    temp=(char) output[5];
                                    output[5]='0';
                                    c_packet=1;
                                    
                      }
                    
                               strContent = new StringBuffer("");                                                               
                               DpSend =
                        new DatagramPacket(output, output.length, ip, 1234);
                                
                try {
                   // if(Integer.parseInt(s1==))
                    
                    
                     if(flag3==0){
                        ds.send(DpSend);
                        if(q2>9)
                                q2=1;
                        bfw.write("Sending the Packet No: "+ q2);
                        bfw.newLine();
                    bfw.flush();
                        flag=3;
                    }else if(c_packet==1 && flag5==0){
                        ds.send(DpSend);
                        bfw.write("Acknowledgement did not Matched");
                        bfw.newLine();
                        bfw.flush();
                        flag5=1;
                    }
                   // else
                   //     if(Integer.parseInt(s1)==packet){
                    //bfw.write(packet);
                  //  ds.send(DpSend);
                  //  bfw.write("Acknowledgement Matched");
                  //  flag3=3;
                    
                  //  }
                    if(count1==corrupt){
                            output[5]=(byte) temp;
                            send_again();
                            receive_NACK();
                            bfw.write("Sending The Packet Again");
                            bfw.write("");
                            bfw.newLine();
                            bfw.flush();
                    }else
                        if(count1%3==0){
                            receive_Ack();  
                            bfw.write(packet);
                            bfw.newLine();
                            bfw.flush();
                         
                            if(Integer.parseInt(s1)==packet){
                   
                         bfw.write("Acknowledgement Matched");
                         bfw.newLine();
                        bfw.flush();                   
                    }
                                                     
                        }                                      
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
                            
                        
                       
                       if(packet>9){
                           packet=1;
                       }
                       }
                           strContent.append((char)ch);
                           n++;                                            
                   }
                   
                 // bfw.write(total);
                   fin.close();
            }
            catch(Exception e){
                
            }
            
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        bfw.close();
        }
        catch(IOException ex)
        {
            
        }
    }
    
     public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret23 = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret23.append((char) a[i]);
            i++;
        }
        return ret23;
    }
}
