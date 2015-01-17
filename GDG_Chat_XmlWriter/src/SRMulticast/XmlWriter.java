/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SRMulticast;

/**
 *
 * @author Administrator
 */
import org.jdom2.*;
import org.jdom2.output.*;
import java.io.*;
import  java.io.File;
public class XmlWriter {

      

    public void scriviXml(String uName,String hName,String roomAddr,String room) {
        try {
            //Elemento radice 
            Element root = new Element("multicast") {
            };
            //Documento 
            Document document = new Document(root);

            //Creazione di tre elementi figli denominato ITEM 
            //a ciascuno dei quali vengono settati tre attributi 
            //e viene aggiunto un elemento figlio contenente 
            //la descrizione della cosa da fare 
            Element item1 = new Element("group");
            item1.setAttribute("mgroup", roomAddr);
            item1.setAttribute("chatroom", room);
            Element utente = new Element("user");
            utente.setAttribute("username", hName);
            utente.setAttribute("hostname", hName);

            item1.addContent(utente);
            root.addContent(item1);



            //Creazione dell'oggetto XMLOutputter 
            XMLOutputter outputter = new XMLOutputter();
            //Imposto il formato dell'outputter come "bel formato" 
            outputter.setFormat(Format.getPrettyFormat());
            //Produco l'output sul file xml.foo 
           File configDir = new File ("config");
           if(!configDir.exists())
                 configDir.mkdirs();
               
            outputter.output(document, new FileOutputStream("config/MulticastGroups.xml"));
            System.out.println("File creato:");
            //Stampo l'output anche sullo standard output 
            outputter.output(document, System.out);
        } catch (IOException e) {
            System.err.println("Errore durante il parsing del documento");
            e.printStackTrace();
        }

    }
}