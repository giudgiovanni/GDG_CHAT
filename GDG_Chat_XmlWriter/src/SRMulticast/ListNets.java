/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SRMulticast;

/**
 *
 * @author Administrator
 */
import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;
import javax.swing.JOptionPane;

public class ListNets {

    public static String IPtoCheck="";
    public static InetAddress nicIp = null;
    public static boolean answer =false;
    
    public ListNets(String ipaddress)
    {
        IPtoCheck = ipaddress;
    }
    
    
    public static void setIPtoCheck(String setIP)
    {
        IPtoCheck = setIP;
    }
    
    public static String getIPtoCheck()
    {
        return IPtoCheck;
    }
    
    
    private static void setnicIp(InetAddress add)
    {
        nicIp = add;
        
        
    }
    
    private static InetAddress getnicIp()
    {
        
        
        return nicIp;
        
    }
    
    public static boolean checkIp(){
         String ip = getnicIp().toString();
        JOptionPane.showMessageDialog(null, "checkip1"+ip);
        JOptionPane.showMessageDialog(null, "checkip2"+IPtoCheck);
        
        if(ip.equals(IPtoCheck)){ ////ERRORE
            
            JOptionPane.showMessageDialog(null, "ok");
        return true;
        }
        return false;
    }
    
    public static boolean getAnswer()
    {
        return answer;
    }
    
    public static void setAnswer(boolean answ)
            {
                answer = answ;
        
    }
    
    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint,getIPtoCheck());
    }

    
    
    public static void displayInterfaceInformation(NetworkInterface netint,String addrToCheck) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
           // JOptionPane.showMessageDialog(null, inetAddress);
            
            
          if(inetAddress.toString().equals("/"+addrToCheck))
              JOptionPane.showMessageDialog(null, "ok");
            
            
         //   setnicIp(inetAddress); //// errore
       //     JOptionPane.showMessageDialog(null, getnicIp());
            
          // setAnswer(checkIp());
            
                
        }
        out.printf("\n");
     }
}  
