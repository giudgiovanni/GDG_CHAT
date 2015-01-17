/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SRMulticast;

import static SRMulticast.RoomSelection.createImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Administrator
 */
public class RoomSelection extends JPanel
                          implements ActionListener {
    JLabel picture;
   public static  String roomName;
   public static  String roomAddress;
   
   public  static JComboBox rooms;
   
   public static JFrame Select;
   public static boolean InvioPressed = false;
   
   public static int RoomSelected;
 
    public RoomSelection() {
        super(new BorderLayout());
 
        String[] roomStrings = { "Stanza 1", "Stanza 2", "Stanza 3", "Stanza 4", "Stanza 5" };
 
        //Create the combo box, select the item at index 4.
        //Indices start at 0, so 4 specifies the pig.
        rooms = new JComboBox(roomStrings);
        rooms.setSelectedIndex(-1);
        rooms.addActionListener(this);
        
        
        
        
        
         JButton Invio = new JButton("Invio");
        Invio.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
    {
        
        if(rooms.getSelectedIndex()!=-1){
            try {
                RoomSelected = 1;
                InvioPressed = true;
                //JOptionPane.showMessageDialog(null, roomName);
               Select.dispose();
               ChatClient.recall(roomAddress,roomName);
               
             //  ChatClient.recursive.recursiveSend();
            } catch (UnknownHostException ex) {
                Logger.getLogger(RoomSelection.class.getName()).log(Level.SEVERE, null, ex);
            }
       
        }else
        {
            JOptionPane.showMessageDialog(null, "Nessuna stanza selezionata!");
        }
       
    }
        });
        
         
        //Lay out the demo.
        add(rooms, BorderLayout.PAGE_START);
      
        add(Invio,BorderLayout.PAGE_END);
        //  add(picture, BorderLayout.PAGE_END);
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
    }
 
    
    
    /**Listen to the butto Invio. */
    
   public int getRoomSelected()
   {
       return RoomSelected;
   }
    
    
    /** Listens to the combo box. */
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox)e.getSource();
        
        roomAddress = Switches(cb.getSelectedIndex());
        roomName = (String)cb.getSelectedItem();
        //updateLabel(roomName);
    }
 
    
    
    public String Switches(int index)
    {
        String NomeSt="";
        switch(index){
            case 0:
                NomeSt = "225.4.5.6";
                break;
            case 1:
                NomeSt = "225.4.5.7";
                break;
            case 2:
                NomeSt = "225.4.5.8";
                break;
            case 3:
                NomeSt = "225.4.5.9";
                break;
            case 4:
                NomeSt = "225.4.5.10";
                break;
            case 5:
                NomeSt = "225.4.5.11";
                break;
            case 6:
                NomeSt = "225.4.5.12";
                break;
            case 7:
                NomeSt = "225.4.5.13";
                break;
            case 8:
                NomeSt = "225.4.5.14";
                break;
            case 9:
                NomeSt = "225.4.5.15";
                break;
            case 10:
                NomeSt = "225.4.5.16";
                break;
            
        }
        return NomeSt;
    }
    
    protected void updateLabel(String name) {
        ImageIcon icon = createImageIcon("images/" + name + ".gif");
        picture.setIcon(icon);
        picture.setToolTipText("A drawing of a " + name.toLowerCase());
        if (icon != null) {
            picture.setText(null);
        } else {
            picture.setText("Image not found");
        }
    }
 
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = RoomSelection.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    public static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ComboBoxDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new RoomSelection();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
      
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    }
 /*
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    * 
    * */
}