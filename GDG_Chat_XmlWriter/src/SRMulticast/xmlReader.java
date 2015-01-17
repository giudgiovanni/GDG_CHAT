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
import static java.lang.System.out;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import javax.swing.JOptionPane;

import org.jdom2.*;
import org.jdom2.input.*;

public class xmlReader {

    public static String multicastG;
    public static String multicastU;
    public static String multicastIpU;
    public static String chatRoomName;
    public static boolean check;

    public void setMulticastG(String group) {
        multicastG = group;

    }

    public void setMulticastU(String user) {
        multicastU = user;

    }

    public void setMulticastIpU(String IP) {
        multicastIpU = IP;

    }

    public String getMulticastG() {

        return multicastG;
    }

    public String getMulticastU() {

        return multicastU;
    }

    public String getChatRoomName() {

        return chatRoomName;
    }

    public void leggi() {
        try {
            //Creo un SAXBuilder e con esco costruisco un document 
            SAXBuilder builder = new SAXBuilder();
          //  File xml = new File("MulticastGroups.xml");
            Document document = builder.build(new File("config/MulticastGroups.xml"));
        //    Document document = builder.build(xml);

            //Prendo la radice 
            Element root = document.getRootElement();
            //Estraggo i figli dalla radice 
            List children = root.getChildren();
            Iterator iterator = children.iterator();
            
            //Per ogni figlio 
            while (iterator.hasNext()) {
                //Mostro il valore dell'elemento figlio "DESCR" e degli 
                //attributi "importanza", "perc_completamento", e "completata" 
                //sullo standard output 
                Element item = (Element) iterator.next();
                Element description = item.getChild("group");
                //  System.out.println("\t*" + description.getText());
                //  System.out.println("\tmgroup: " + item.getAttributeValue("mgroup"));
                String G = item.getAttributeValue("mgroup");
                String GN = item.getAttributeValue("chatroom");

                List children2 = item.getChildren();
                Iterator iterator2 = children2.iterator();
                while (iterator2.hasNext()) {

                    Element item2 = (Element) iterator2.next();
                    Element description2 = item2.getChild("user");
                    //  System.out.println("\tusername: " + item2.getAttributeValue("username"));
                    //System.out.println("\t" +item2.getAttributeValue("ip_address"));
                    String InetAddr = item2.getAttributeValue("hostname");
                    String U = item2.getAttributeValue("username");

                    if (InetAddr.toString().equals(InetAddress.getLocalHost().getHostName())) {
                multicastG = G;
                multicastU = U;
                chatRoomName = GN;
                    }
                    
                   
                    Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                    for (NetworkInterface netint : Collections.list(nets)) {
              //          displayInterfaceInformation(netint, InetAddr, G, U, GN);
                     //   if (check) {
                      //      JOptionPane.showMessageDialog(null, InetAddr);
                      //  }
                    }
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Errore durante la lettura dal file");
            e.printStackTrace();
        }
            
    }

    public static void displayInterfaceInformation(NetworkInterface netint, String addressToCheck, String MG, String MU, String GN) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            out.printf("InetAddress: %s\n", inetAddress);
            if (inetAddress.toString().equals("/" + addressToCheck)) {
                multicastG = MG;
                multicastU = MU;
                chatRoomName = GN;
               
            }

        }
        out.printf("\n");
    }
}
