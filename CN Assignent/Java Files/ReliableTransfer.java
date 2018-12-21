/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reliabletransfer;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

/**
 *
 * @author bad_engineer
 */
public class ReliableTransfer {

    /**
     * @param args the command line arguments
     */
    int m=0,rec=0,i1=0;
    DatagramSocket ds3;
    InetAddress ip;
    DatagramPacket DpSend;
    DatagramPacket DpSe;
    DatagramPacket NACKSend;
    String S1,S12;
    String S2;
    DatagramPacket DpReceive;
    DatagramSocket ds4;
    DatagramSocket ds5;
    DatagramSocket ds31;
    byte[] output=null;
    byte[] ack=null;
     byte[] nack=null;
     byte[] ack12=null;
    BufferedWriter bw = null;   
    int Corrupt=0;
    int ack1=0;
    
    private BufferedWriter bfw;
    
    public void Stop_the_Server(){
        if(ds4!=null)
            ds4.close();
         if(ds5!=null)
            ds5.close();
         if(ds31!=null)
            ds31.close();
         if(ds3!=null)
            ds3.close();
        System.out.println("Closing the Server");
    }
      
    public void Send_nack(String ServerIP3){
        try {
            try {
                ds5 = new DatagramSocket();
                try {
                    ip = InetAddress.getByName(ServerIP3);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
                }
                S2=String.valueOf(-1);
                nack=S2.getBytes();
                //int lm2=m+1;
                //bfw.write("Sending the Packet Again");
                NACKSend=  new DatagramPacket(nack, nack.length, ip, 2239);
            } catch (SocketException ex) {
                Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
            ds5.send(NACKSend);
        } catch (IOException ex) {
            Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void SendAck(String ServerIP2){
        if(Corrupt!=1){
            i1++;
            if(i1>9)
                i1=1;        
        }
          
        try {
            try {
                ds3 = new DatagramSocket();
                try {
                    ip = InetAddress.getByName(ServerIP2);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(ack1>9)
                        ack1=1;
                S1=String.valueOf(ack1+1);
                ack=S1.getBytes();
                int lm=ack1+1;
                if(lm>9)
                    lm=1;
                System.out.println("Sending Acknowlfngent "+ lm);
//                bfw.newLine();
//                bfw.flush();
                DpSend =  new DatagramPacket(ack, ack.length, ip, 1239);
            } catch (SocketException ex) {
                Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
            }
            ds3.send(DpSend);
        } catch (IOException ex) {
            Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    
    
    public void receive(String ServerIP){
        try{
            bfw = new BufferedWriter(new FileWriter("PacketLog.txt",true));
        try {           
            ds4 = new DatagramSocket(1234);
            byte[] receive = new byte[65535];
            
            DpReceive = null;
            while (true)
            {
                
          DpReceive = new DatagramPacket(receive, receive.length);
                
                try {
                    ds4.receive(DpReceive);
                } catch (IOException ex) {
                    Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
                }
              
                m++;
                if(m>9)
                    m=1;
                ack1++;
                if(ack1>9)
                    ack1=1;
                StringBuilder b = data(receive);
                String str1 = b.toString();
                String str2 = str1.substring(31);
                String str3 = str1.substring(1,31);
               // bfw.write(str1);
                    bfw.write("Packet Number "+ m +" is received"+ " " + data(receive));
                    bfw.newLine();
                    bfw.flush();
                    bfw.write(ack1);
                    bfw.newLine();
                    bfw.flush();
                    //bfw.write(str1);
                    
                    byte[] check = str3.getBytes();
                    Checksum checksumEngine = new Adler32();
                    checksumEngine.update(check, 0, check.length);
                    long checksum = checksumEngine.getValue();
                    String str4=String.valueOf(checksum);
                   //bfw.write(str4);
                  // bfw.write(str2);
                    if(str4.equals(str2)){
                    bfw.write("Checksum matched.Packets is not Corrupted");
                    bfw.newLine();
                    bfw.flush();
                   
                    if(ack1%3==0){
                        SendAck(ServerIP);
                    }
                                     
              try {
                  bw = new BufferedWriter(new FileWriter("output.txt",true));
              } catch (IOException ex) {
                  Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
              }
              try {
                  bw.write(str3);
              } catch (IOException ex) {
                  Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
              }
              try {
                  bw.close();
              } catch (IOException ex) {
                  Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
              }
                    }
                    else{
                        bfw.write("Checksum is not matched Packet is Corrupted ");
                        bfw.newLine();
                        bfw.flush();
                        bfw.write("Sending Negative Ackknowledgement");
                        bfw.newLine();
                        bfw.flush();
                        //Corrupt=1;
                        Send_nack(ServerIP);
                    }

//  if (m==4){
                  //      M();
                       // m=1;
                 //   }
                  
            }
        } catch (SocketException ex) {
            Logger.getLogger(ReliableTransfer.class.getName()).log(Level.SEVERE, null, ex);
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

