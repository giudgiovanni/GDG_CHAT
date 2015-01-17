/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SRMulticast;

/**
 *
 * @author Administrator
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;

// Class to manage Client chat Box.
public class ChatClient {

    public static ChatAccess access;
    public static String server;
    public static String Utente;
    public static String chatRoom;
    public static OnlineUsers users;
    public static List<String> clients;
    public static List<String> controlListUsers;
    public static JList list;
    public static JTextArea textArea;
    public static JTextField inputTextField;
    public static JButton sendButton;
    public static File storico;
    public static FileOutputStream prova;
    public static PrintStream scrivi;
    public static PrintWriter out;
    public static int SIZE;
    public static int SYNCOUNTER;
    public static int ACKCOUNTER;
    public static Map<String, String> AckExpected = new HashMap();
    public DefaultListModel listModel;
    public static String match = null;
    public static final String hireString = "Highlight";
    public static int coloreLista;
    public static int indexesList;
    // public static ChatAccess recursive;
    public static JCheckBox[] checkBoxes;
	public static boolean[] enabledFlags;
        
         public static JComponent glassPane;
    
    

    /**
     * Chat client access
     */
    public ChatClient() {
    }

    static class ChatAccess extends Observable {

        private MulticastSocket socket;
        private Socket response;
        private OutputStream outputStream;
        public InetAddress multicastGroup;
        public int multicastPort;
        public DatagramPacket packet;
        private Thread receivingThread;
        // Vector<String> users = new Vector<String>();
        private String text;
        private String msg;

        @Override
        public void notifyObservers(Object arg) {
            super.setChanged();
            super.notifyObservers(arg);
        }

        /**
         * Create socket, and receiving thread
         */
        public ChatAccess(String server, int port) throws IOException {

            // recursive =this;
            
            //test

            multicastGroup = InetAddress.getByName(server);
            multicastPort = port;
            // outputStream = socket.getOutputStream();

            receivingThread = new Thread() {
                @Override
                public void run() {
                    try {
                        socket = new MulticastSocket(multicastPort);
                        socket.joinGroup(multicastGroup);

                        while (true) {

                            byte[] message = new byte[65535];
                            packet = new DatagramPacket(message,
                                    message.length, multicastGroup,
                                    multicastPort);
                            socket.receive(packet);

                            // controlListUsers.addAll(clients);

                            msg = (new String(packet.getData()).trim());
                            // System.out.println(msg);
                            if (msg.startsWith("/c/ ")) {
                                text = (msg.split("/c/")[1]);

                                String[] u = msg.split("/c/");
                                boolean answer = users
                                        .checkUserOnlineOnList(text);
                                // JOptionPane.showMessageDialog(null, answer);
                                if (!answer) {
                                    clients.add(text);
                                    // controlListUsers.add(text);
                                    users.update((ArrayList<String>) clients);
                                    send("/i/ " + Utente);

                                }else
                                {
                                    send("/i/ " + Utente);
                                }

                                

                            } else if (msg.startsWith("/d/ ")) {
                                String id = msg.split("/d/")[1];
                                clients.remove(id);
                                // controlListUsers.remove(id);
                                users.update((ArrayList<String>) clients);
                            } else if (msg.startsWith("/i/ ")) {
                                String id = msg.split("/i/")[1];
                                boolean answer = users
                                        .checkUserOnlineOnList(id);
                                // JOptionPane.showMessageDialog(null, answer);
                                if (!answer) {
                                    clients.add(id);
                                    controlListUsers.add(id);
                                    users.update((ArrayList<String>) clients);

                                }
                                // recursiveSend();

                                // /DA RIVEDERE DA QUI
                            } else if (msg.startsWith("SYN")) {
                                String appoggio = msg.split("SYN")[1];
                                String sender = appoggio.split(" ")[1];

                                appoggio = appoggio.trim();
                                // JOptionPane.showMessageDialog(null,sender);
                                String receiver = appoggio.split("RCV ")[1];
                                receiver = receiver.trim();
                                // JOptionPane.showMessageDialog(null,receiver);
                                String myHostName = InetAddress.getLocalHost()
                                        .getHostName();
                                // JOptionPane.showMessageDialog(null,myHostName);
                                // JOptionPane.showMessageDialog(null,receiver.equals(myHostName.trim()));

                                if (receiver.equals(myHostName.trim())
                                        && (!(sender.equals(myHostName.trim())))) {
                                    if (users.checkUserOnlineOnList(" "
                                            + sender.trim())) {
                                        // JOptionPane.showMessageDialog(null,"esiste");
                                        receiver = receiver.trim();
                                        sender = sender.trim();

                                        send("ACK" + " " + receiver + " "
                                                + "SND" + " " + sender);
                                    } else {
                                        // JOptionPane.showMessageDialog(null," NON esiste");
                                        clients.add(" " + sender.trim());
                                        users.update((ArrayList<String>) clients);
                                        receiver = receiver.trim();
                                        sender = sender.trim();

                                        send("ACK" + " " + receiver + " "
                                                + "SND" + " " + sender);
                                    }

                                    // JOptionPane.showMessageDialog(null,"ACK sent");

                                } else if ((!(sender.equals(myHostName.trim())))
                                        && (!(users.checkUserOnlineOnList(" "
                                        + sender.trim())))
                                        && receiver.equals("NULL")) {

                                    // JOptionPane.showMessageDialog(null,"send ricevuto");

                                    sender = sender.trim();
                                    clients.add(" " + sender.trim());
                                    users.update((ArrayList<String>) clients);
                                    send("ACK" + " " + Utente + " " + "SND"
                                            + " " + "NULL");

                                    // JOptionPane.showMessageDialog(null,"ACK sent");

                                }

                                // /DA RIVEDERE DA QUI
                            } else if (msg.startsWith("ACK")) {

                                String appoggio = msg.split("ACK")[1];
                                String receiver = appoggio.split(" ")[1];

                                appoggio = appoggio.trim();
                                receiver = receiver.trim();

                                String sender1 = appoggio.split("SND")[1];
                                sender1 = sender1.trim();

                                // se ricevo un ack dove il receiver ed il
                                // sender sono uguali
                                // NON FARE NULLA
                                if ((receiver.trim()).equals((InetAddress
                                        .getLocalHost().getHostName()).trim())) {
                                } else // invece se ricevo un ack dove il
                                // receiver è DIVERSO da HOSTNAME ed il
                                // sender è UGUALE ad HOSTNAME
                                if (!(receiver.trim()).equals((InetAddress
                                        .getLocalHost().getHostName()).trim())
                                        && (sender1.trim()).equals((InetAddress
                                        .getLocalHost().getHostName())
                                        .trim())) {
                                    // JOptionPane.showMessageDialog(null,
                                    // "Diverso");
                                    boolean key = AckExpected
                                            .containsKey(receiver.trim());
                                    // JOptionPane.showMessageDialog(null, key);
                                    AckExpected.remove(receiver.trim());
                                    // da qui
                                } else // invece se ricevo un ack dove il
                                // receiver è DIVERSO da HOSTNAME ed il
                                // sender è UGUALE ad HOSTNAME
                                if ((!(receiver.trim()).equals((InetAddress
                                        .getLocalHost().getHostName()).trim()) && (!(users
                                        .checkUserOnlineOnList(" "
                                        + receiver.trim()))))
                                        && sender1.equals("NULL")) {
                                    // JOptionPane.showMessageDialog(null,
                                    // "ack ricevuto");
                                    clients.add(" " + receiver.trim());
                                    users.update((ArrayList<String>) clients);

                                }
                                // recursiveSend();

                                // /DA RIVEDERE DA QUI
                            } else if (msg.startsWith("/R/ ")) {
                                String id = msg.split("/R/")[1];

                                
                                boolean answer = users.checkUserOnlineOnList(id);
                                
                                if(answer)
                                {
                                   
                                 //     JOptionPane.showMessageDialog(null,indexesList);
                                            enabledFlags[indexesList] = true;
                                     //       JOptionPane.showMessageDialog(null,indexesList);
                                    checkBoxes[indexesList].setSelected(true);
                                   // list.setSelectedIndex(indexesList);
                                 //   list.setFocusable(true);
                                 
                                    list.repaint();
                                    
                                 //   JOptionPane.showMessageDialog(null, indexesList);
                                    
                                    
                                    
                                    
                                }
                                
                                /*
                                boolean answer = AckExpected
                                        .containsKey(id.trim());

                                if (answer) {
                                    
                                      // JOptionPane.showMessageDialog(null, answer);
                                    // JOptionPane.showMessageDialog(null, indexesList);
                                   
                                    try {
                                        // JOptionPane.showMessageDialog(null, indexesList);
                                        Thread.sleep(100);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                     users.checkUserOnlineOnList(id);
                                    list.setSelectedIndex(indexesList);
                                    //JOptionPane.showMessageDialog(null, list.getSelectedIndex());
                                    AckExpected.remove(id.trim());
                                    
                                   ////PROVARE A RICHIAMARE LA FUNZIONE ACKNOLEDGE CHE FA LA STESSA COSA
                                    // E FA GIA' IL CONTROLLO 
                                    
                                    boolean check = AckExpected.isEmpty();
                                    
                                    
                                    
                                    if (check) {
                                        JOptionPane.showMessageDialog(null, check);
                                    } else
                                    {
                                        if(!check && (indexesList==(clients.size()-1))){
                                            JOptionPane.showMessageDialog(null, "fine lista" + AckExpected.get(id));
                                   
                                        }
                                    }
                                    
                                        



                                        //    JOptionPane.showMessageDialog(null, AckExpected.get(id));
                                    

                                    // 
                                    //list.setSelectionForeground(Color.lightGray);
                                    //match = id;
                                    //     list.setCellRenderer(new MyListCellRenderer());
                                    //list.repaint();
                                }*/
                            } else if (msg.startsWith("/S/ ")) {
                                String id = msg.split("/S/")[1];
                           //      JOptionPane.showMessageDialog(null,"S arrivato" + id);
                                
                                access.send("/R/" + " " + Utente);
                               
                            } // JOptionPane.showMessageDialog(null,
                            // controlListUsers.size());
                            // JOptionPane.showMessageDialog(null, answer);
                            // Thread.sleep(3000);
                            // users.removeUserfromList(controlListUsers);
                            // users.update((ArrayList<String>) clients);
                            // JOptionPane.showMessageDialog(null,
                            // controlListUsers.size());
                            else {

                                notifyObservers(msg);

                                if (msg.startsWith("< ")) {
                                    String id = (msg.split("< ")[1]);
                                    String id2 = id.split(" :")[0];
                                    // msg.split(text, multicastPort)
                                    // JOptionPane.showMessageDialog(null, id2);
                                 //   send("/R/" + " " + Utente);
                                    if (!(id2.equals(InetAddress.getLocalHost()
                                            .getHostName()))) {

                                        ImageFlasher.timer.start();

                                    }
                                }

                            }



                            //  
                            //                                        

                        }

                    } catch (IOException ex) {
                        // notifyObservers(ex);
                        JOptionPane
                                .showMessageDialog(
                                null,
                                "Non è stata indiviata nessuna corrispondenza\n"
                                + "\t\t                   Utente<->Workstation\n"
                                + "     Rivolgersi al proprio Amministratore\n"
                                + "                   Click su ok per uscire");
                        System.exit(0);
                    } // catch (InterruptedException ex) {
                    // Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE,
                    // null, ex);
                    // }
                }
            };
            receivingThread.start();

        }
        private static final String CRLF = "\r\n"; // newline

        /**
         * Send a line of text
         */
        public void send(String text) {
            try {
                byte[] msg = text.getBytes();
                packet = new DatagramPacket(msg, msg.length, multicastGroup,
                        multicastPort);
                // outputStream.write((text + CRLF).getBytes());
                // outputStream.flush();
                socket.setTimeToLive(16);
                socket.send(packet);


            } catch (IOException ex) {

                // AGGIUNGERE QUI IL CODICE PER IL TESTO ROSSO DELL'UTENTE
                notifyObservers(ex);
            }
        }

        /**
         * Close the socket
         */
        public void close() throws IOException {
            socket.leaveGroup(multicastGroup);
            socket.close();

        }

        public void recursiveSend() {
            // JOptionPane.showMessageDialog(null, "1");

            send("/u/ " + Utente);
            // JOptionPane.showMessageDialog(null, "2");
            // invia ogni 5 sec un controllo per dire che l'utente è ancora
            // connesso

            // System.out.println(new Date());

        }
    }

    public static class SyncroSend {

        public static void sendSYN() throws InterruptedException {
            // JOptionPane.showMessageDialog(null, "1");
            // JOptionPane.showMessageDialog(null, "send");

            //	if (clients.size() != 1) {
            checkBoxes = new JCheckBox[clients.size()];
		enabledFlags = new boolean[clients.size()];
            for (int i = 0; i < clients.size(); i++) {
                
                checkBoxes[i] = new JCheckBox(clients.get(i).trim());
                	if (((clients.get(i)).trim().equals(Utente))) {
                             checkBoxes[i].setSelected(true);
			
			enabledFlags[i] = true;
                            
                        }else
                        {

                
               
			
                checkBoxes[i].setSelected(false);
			
			enabledFlags[i] = false;
                        }
                
                AckExpected.put(clients.get(i).trim(), "chosen");



                // JOptionPane.showMessageDialog(null,
                // clients.get(i));
                //	SYNCOUNTER++;



            }
           
            
            access.send("/S/" + " " + Utente);
            Thread.sleep(100);
             list.setCellRenderer(new DisabledItemListCellRenderer());
            // acKnowledge();

            //	}



            // JOptionPane.showMessageDialog(null, "SendSyn");
        }

        // JOptionPane.showMessageDialog(null, "2");
        // invia ogni 5 sec un controllo per dire che l'utente è ancora
        // connesso
        // System.out.println(new Date());
        //	} else {
        // JOptionPane.showMessageDialog(null,
        // "utente cerca di riconnettersi");
        //	}
        public static void acKnowledge() throws InterruptedException {


            Thread.sleep(100);
            boolean isEmpty = AckExpected.isEmpty();
             JOptionPane.showMessageDialog(null, isEmpty);
            if (!isEmpty) {
                Set keys = AckExpected.keySet();
                Iterator i = keys.iterator();
                while (i.hasNext()) {
                    String keyHash = (String) i.next();
                    String value = (String) AckExpected.get(keyHash);
                    // JOptionPane.showMessageDialog(null, keyHash );

                   list.setForeground(Color.GRAY);


                    //	clients.remove(" " + keyHash.trim());
                    // JOptionPane.showMessageDialog(null, forse );

                    // controlListUsers.remove(id);
                    //users.update((ArrayList<String>) clients);
                    AckExpected.remove(keyHash);
                }

            }

        }
    }
    
    public static class DisabledItemListCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(JList list1, Object value,
				int index, final boolean isSelected, final boolean cellHasFocus) {
			Component comp = super.getListCellRendererComponent(list1, value,
					index, false, false);
			JComponent jc = (JComponent) comp;
			if (enabledFlags[index]) {
				//if (isSelected && cellHasFocus) {
                                if (isSelected ) {
                                    	comp.setForeground(Color.black);
					comp.setBackground(Color.red);
				} else {
					comp.setForeground(Color.CYAN);
				}
				if (!isSelected) {
					if ((value.toString()).trim().equals("yellow")) {
						comp.setForeground(Color.orange);
						comp.setBackground(Color.magenta);
					}
				}
				return comp;
			}
                        
                        MouseListener mouseListener = new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent mouseEvent) {
         // list.setEnabled(false);
          
          if(list.hasFocus())
          {
              JOptionPane.showMessageDialog(null, "focus");
          }
          list.setFocusable(false);
        CursorToolkitTwo.startWaitCursor(list.getRootPane());
      }
      @Override
      public void mouseExited(MouseEvent mouseEvent) {
       CursorToolkitTwo.stopWaitCursor(list.getRootPane());
       list.setEnabled(true);
       list.setFocusable(true); 
      }
      
      
       public void mousePressed(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
    };
   
			comp.setEnabled(false);
			return comp;
		}
	}

    public static class OnlineUsers {

        public boolean existed;
        public boolean controlled;

        public OnlineUsers() {
        }

        public void update(String[] users) {
            list.setListData(users);
            list.repaint();

        }

        public void update(ArrayList<String> usersL) {
            Iterator<String> itr = usersL.iterator();
            while (itr.hasNext()) {

                list.setListData(usersL.toArray());

                itr.next();

                list.repaint();

            }

        }

        public void removeUser(ArrayList<String> usersL) {
            Iterator<String> itr = usersL.iterator();
            while (itr.hasNext()) {

                list.setListData(usersL.toArray());
                itr.next();

            }

        }

        public boolean checkUserOnlineOnList(String id) {

            // JOptionPane.showMessageDialog(null, id);
            existed = false;
            for (int i = 0; i < clients.size(); i++) {

                if (id.equals(clients.get(i))) {

                    existed = true;
                    indexesList = i;
                    // JOptionPane.showMessageDialog(null, existed);
                    // break;
                }

            }
            return existed;

        }

        public static int indexesList() {

            return indexesList;
        }

        public boolean checkUserDisconnectedList(String id) {

            // JOptionPane.showMessageDialog(null, id);
            existed = false;
            for (int i = 0; i < controlListUsers.size(); i++) {

                if (id.equals(controlListUsers.get(i))) {

                    existed = true;
                    // JOptionPane.showMessageDialog(null, existed);
                    // break;
                }

            }
            return existed;

        }

        public void removeDifferences(List<String> userOnline,
                List<String> usersChecked, int sz) {
            // user =new ArrayList<String>(sz);
            // usersLSrc.addAll(usersLDest);
        }

        public void removeUserfromList(List<String> toCheck) {
            // JOptionPane.showMessageDialog(null, toCheck.size());
            for (int i = 0; i < toCheck.size(); i++) {

                String toRemove = toCheck.get(i);
                clients.remove(toRemove);

            }

        }

        public boolean checkDisconnectedUsers(List<String> list,
                List<String> check) {

            controlled = list.equals(check);

            return controlled;

        }
    }

    /**
     * Chat client UI
     */
    public static class ChatFrame extends JFrame implements Observer {

        
         
        
        
        public void Uniconed() {

            setExtendedState(1);
        }
        private ChatAccess chatAccess;

        public ChatFrame(ChatAccess chatAccess) {
           
            Image frmImg = Toolkit.getDefaultToolkit().getImage(
                    getClass().getClassLoader().getResource("diavoletto.png"));

            this.chatAccess = chatAccess;
            chatAccess.addObserver(this);
            buildGUI();
            setIconImage(frmImg);

        }

        public static class MyListRenderer extends DefaultListCellRenderer {

            
            private HashMap theChosen = new HashMap();

            public Component getListCellRendererComponent(JList list1, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list1, value, index, isSelected,
                        cellHasFocus);


                if (isSelected) {
                    theChosen.put(value, "chosen");
                    setForeground(Color.red);
                    SwingWorker worker = new SwingWorker() {
					@Override
					public Object doInBackground() {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) { /* Who cares */
						}
						return null;
					}

					@Override
					public void done() {
						 match = null;
						 list.repaint();
                                                 
					}
				};
				 worker.execute();
                    
                   
                }

                if (theChosen.containsKey(value)) {
                    
                    
                     
                } else {
                    setForeground(Color.black);
                }

                return (this);
            }
        }

        
        
        
        
        
        /**
         * Builds the user interface
         */
        private void buildGUI() {
 
 
            textArea = new JTextArea(20, 300);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            
            // list.setVisible(true);
           
            JScrollPane txA = new JScrollPane(textArea);
            txA.setPreferredSize(new Dimension(430, 50));
            add(txA, BorderLayout.WEST);
            list = new JList();
           
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
           // list.setSelectionInterval(0, list.getModel().getSize() -1);
           
            //                    list.setSelectionModel(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            list.setSize(20, 35);

            
        //    list.setCellRenderer(new MyListRenderer());
            //list.setForeground(Color.GREEN);
           
            final JScrollPane txL = new JScrollPane(list);
            
            txL.setPreferredSize(new Dimension(160, 20));
             
            //add(txL, BorderLayout.EAST);
            add(txL, BorderLayout.EAST);
         
            
           // glassPane.setVisible(false);
   //         add(glassPane,BorderLayout.EAST);
            
            MouseListener mouseListener = new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent mouseEvent) {
          list.setEnabled(false);
        CursorToolkitTwo.startWaitCursor(txL.getRootPane());
      }
      @Override
      public void mouseExited(MouseEvent mouseEvent) {
       CursorToolkitTwo.stopWaitCursor(txL.getRootPane());
       list.setEnabled(true);
       list.repaint();
        
      }
      
      
       public void mousePressed(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
    };
    list.addMouseListener(mouseListener);

            
            setSize(600, 400);

            // add(list,BorderLayout.EAST);
            Box box = Box.createHorizontalBox();
            box.setSize(20, 30);
            add(box, BorderLayout.SOUTH);
            inputTextField = new JTextField();
            inputTextField.setFocusable(true);

            sendButton = new JButton("Send");
            box.add(inputTextField);
            box.add(sendButton);
            box.setFocusable(true);

            users = new OnlineUsers();

            // Action for the inputTextField and the goButton
            ActionListener sendListener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String str = inputTextField.getText();
                    if (str != null && str.trim().length() > 0) {
                        chatAccess.send("< "
                                + Utente
                                + " : "
                                + new SimpleDateFormat("HH:mm:ss")
                                .format(Calendar.getInstance()
                                .getTime()) + "> " + str);
                       
                        
                        try {
                            Thread.sleep(200);
                             list.clearSelection();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        try {

                            SyncroSend.sendSYN();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }


                    //   list.setForeground(Color.red);
                    inputTextField.selectAll();
                    inputTextField.requestFocus();
                    inputTextField.setText("");
                    //   list.setCellRenderer(new MyListCellRenderer()); 

                }
            };
            inputTextField.addActionListener(sendListener);
            sendButton.addActionListener(sendListener);

            KeyListener comKey = new KeyListener(){
                public void keyPressed(KeyEvent e) {
		// System.out.println("keyPressed");
		if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown())
                          ImageFlasher.stat = 1;
                        ImageFlasher.sSt.setState(ImageFlasher.stat);
			System.out.println("destra");
	
                
                }

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			System.out.println("");
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			System.out.println("sinistra");
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			System.out.println("giù");
		else if (e.getKeyCode() == KeyEvent.VK_UP)
			System.out.println("su");

	}

	public void keyTyped(KeyEvent e) {
		System.out.println("keyTyped");
	}
            };
            
            inputTextField.addKeyListener(comKey);
            
            // ***************ATTENZIONE MODIFICA************
            // test da qui per DO_NOTHING_ON_CLOSE del frame
			/*
             * this.addWindowListener(new WindowAdapter() {
             * 
             * @Override public void windowClosing(WindowEvent e) { String
             * UonLine = "/d/ " + Utente; chatAccess.send(UonLine); try {
             * chatAccess.close(); } catch (IOException ex) {
             * Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE,
             * null, ex); } } });
             */

           
            
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {

                    // clients = new ArrayList<String>(100);



                    clients = new ArrayList<String>();
                    controlListUsers = new ArrayList<String>(clients);

                    String UonLine = "/c/ " + Utente.trim();
                    chatAccess.send(UonLine);
                    inputTextField.requestFocusInWindow();

                    // chatAccess.send("/u/ " + Utente);
                    String pathStorico = "log/storico.txt";

                    storico = new File("log/storico.txt");
                    try {

                        File logDir = new File("log");
                        if (!logDir.exists()) {
                            logDir.mkdirs();
                        }

                        if (storico.exists()) {
                            prova = new FileOutputStream("log/storico.txt",
                                    true);
                            scrivi = new PrintStream(prova);
                        } else if (storico.createNewFile()) {
                        } else {
                            System.out.println("Il file " + pathStorico
                                    + " non può essere creato");
                        }

                    } catch (IOException ev) {
                        ev.printStackTrace();
                    }
                    
                        /*
                         * try { Thread.sleep(10); chatAccess.send("/u/ " + Utente);
                         * } catch (InterruptedException ex) {
                         * Logger.getLogger(ChatClient
                         * .class.getName()).log(Level.SEVERE, null, ex); }
                         */

                        /*
                         * Timer timer2 = new Timer(5000, new ActionListener() {
                         * 
                         * @Override public void actionPerformed(ActionEvent evnt2)
                         * { // Code to be executed //
                         * JOptionPane.showMessageDialog(null, "ciao");
                         * 
                         * access.recursiveSend();
                         * 
                         * 
                         * 
                         * 
                         * } }); timer2.setRepeats(true); // Only execute once
                         * timer2.start(); // Go go go!
                         */
                        /*
                         * Timer timer = new Timer(1000, new ActionListener() {
                         * 
                         * @Override public void actionPerformed(ActionEvent evnt) {
                         * // Code to be executed //
                         * JOptionPane.showMessageDialog(null, "ciao");
                         * 
                         * boolean chk =
                         * users.checkDisconnectedUsers(controlListUsers, clients);
                         * // JOptionPane.showMessageDialog(null, chk); //
                         * JOptionPane.showMessageDialog(null,
                         * controlListUsers.size()+"+"+ clients.size());
                         * 
                         * 
                         * if (chk) { } else { same(clients, controlListUsers);
                         * users.update((ArrayList<String>) clients);
                         * 
                         * }
                         * 
                         * ChatClient.controlListUsers.clear();
                         * 
                         * 
                         * 
                         * } }); timer.setRepeats(true); // Only execute once
                         * timer.start(); // Go go go!
                         */
                    
                   
                    
                  
                }
            });

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // clients = new ArrayList<String>(100);
                    // String UonLine = "/d/ " + Utente;
                    // chatAccess.send(UonLine);
                    System.exit(1);
                }
            });

            /*
             * try { // Thread.sleep(5 * 1000);
             * 
             * while (true) {
             * 
             * //invia ogni 5 sec un controllo per dire che l'utente è ancora
             * connesso chatAccess.send("/u/ "+Utente); //
             * System.out.println(new Date()); Thread.sleep(5 * 1000); } } catch
             * (InterruptedException e) { e.printStackTrace(); }
             */
        }

        /**
         * Updates the UI depending on the Object argument
         */
        @Override
        public void update(Observable o, Object arg) {
            final Object finalArg = arg;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    // textArea.append("<"+Utente + "> ");
                    textArea.append(finalArg.toString());
                    textArea.append("\n");

                    scrivi.append(new SimpleDateFormat("dd/MM/yyyy")
                            .format(Calendar.getInstance().getTime())
                            + " "
                            + finalArg.toString() + "\n");
                }
            });
        }
    }

    // ************MAIN**********
    public static void main(String[] args) throws UnknownHostException,
            InterruptedException {

        // ***********SELEZIONE DELLA STANZA*******************
        // class TimerHandler implements ActionListener {
        // respond to Timer's event

        // public void actionPerformed(ActionEvent actionEvent) {
        // access.recursiveSend();
        // } // end method actionPerformed
        // } // end class TimerHandler
        //final SyncroSend SendSyncronize = new SyncroSend();
        RoomSelection selStanza = new RoomSelection();
        JFrame test = new JFrame();
        test.add(selStanza);
        test.setSize(200, 200);
        test.setUndecorated(true);
        test.setLocationRelativeTo(null);
        test.setAlwaysOnTop(true);
        test.setVisible(true);

        endFrame(test);

        boolean i = true;

        while (i) {
            if (RoomSelection.rooms.getSelectedIndex() != -1
                    && RoomSelection.InvioPressed == true) {
                i = false;
            }

        }
        // loopSend();
        // JOptionPane.showMessageDialog(null, "fuori loop");
        
        
        
        
        while (true){
            Thread.sleep(5000);
            checkBoxes = new JCheckBox[clients.size()];
		enabledFlags = new boolean[clients.size()];
            for (int l = 0; l < clients.size(); l++) {
                
                checkBoxes[l] = new JCheckBox(clients.get(l).trim());
            }
            
        }


        /*
         * while (true) { // JOptionPane.showMessageDialog(null, "loopSend");
         * 
         * // Thread.sleep(5000);
         * 
         * // access.sendSYN();
         * 
         * Thread.sleep(5000); // access.recursiveSend(); Thread.sleep(12000);
         * 
         * boolean chk = users.checkDisconnectedUsers(controlListUsers,
         * clients); // JOptionPane.showMessageDialog(null, chk); //
         * JOptionPane.showMessageDialog(null, controlListUsers.size()+"+"+
         * clients.size());
         * 
         * 
         * if (chk) { } else { same(clients, controlListUsers);
         * users.update((ArrayList<String>) clients);
         * 
         * }
         * 
         * ChatClient.controlListUsers.clear();
         * 
         * 
         * }
         */

    }// END MAIN

    public static void same(List<String> list1, List<String> list2) {

        for (int i = 0; i < list1.size(); i++) {
            if (!list2.contains(list1.get(i))) {
                list1.remove(i);
            }
        }

        // Collections.sort(list1);
        // Collections.sort(list2);

        /*
         * for (int i = 0; i < list2.size(); i++) { for (int u = 0;
         * u<list1.size(); u++){ if (!list1.get(i).equals(list2.get(u))) {
         * list1.remove(i+1); }
         * 
         * } }
         */
    }

    public static void endFrame(JFrame aa) {
        RoomSelection.Select = aa;
    }

    public static void loopSend() throws InterruptedException {
        while (true) {
            // JOptionPane.showMessageDialog(null, "loopSend");

            // Thread.sleep(5000);

            // access.sendSYN();

            Thread.sleep(5000);
            // access.recursiveSend();
            Thread.sleep(12000);

            boolean chk = users.checkDisconnectedUsers(controlListUsers,
                    clients);
            // JOptionPane.showMessageDialog(null, chk);
            // JOptionPane.showMessageDialog(null, controlListUsers.size()+"+"+
            // clients.size());

            if (chk) {
            } else {
                same(clients, controlListUsers);
                users.update((ArrayList<String>) clients);

            }

            ChatClient.controlListUsers.clear();

        }
    }

    public static void recall(String IndirizzoStanza, String StanzaSelezionata)
            throws UnknownHostException {

        // JOptionPane.showMessageDialog(null, StanzaSelezionata);

        CustomTitlebar fr = new CustomTitlebar();
        
        
        // JFrame window = new JFrame( "Nuovi messaggi" );
        ImageFlasher flash2JPanel = new ImageFlasher();
        fr.add(flash2JPanel);
        // window.setIconImage(flash2JPanel.logo);

        fr.setVisible(true);
        // window.add( flash2JPanel );

        
       
          
        // window.setUndecorated(true);
        // fr.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        // window.pack();
        // window.setSize( 102, 102 );
        // window.setVisible( true );

        String currentUser = System.getProperty("user.name");
        String hostName = InetAddress.getLocalHost().getHostName();
        XmlWriter creaXml = new XmlWriter();
        creaXml.scriviXml(hostName, hostName, IndirizzoStanza,
                StanzaSelezionata);
        xmlReader LeggiUtenti = new xmlReader();
        LeggiUtenti.leggi();

        server = LeggiUtenti.getMulticastG();
        Utente = LeggiUtenti.getMulticastU();
        chatRoom = LeggiUtenti.getChatRoomName();
        // JOptionPane.showMessageDialog(null, server);
        // JOptionPane.showMessageDialog(null, Utente);
        // JOptionPane.showMessageDialog(null, chatRoom);

        int port = 7777;
        access = null;
        try {
            access = new ChatAccess(server, port);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot connect to " + server
                    + ":" + port);
            ex.printStackTrace();
            System.exit(0);
        }
        JFrame frame = new ChatFrame(access);
        frame.setTitle("GDG Chat - connesso alla chatroom " + chatRoom
                + " con l'utente " + Utente);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setUndecorated(true);
        // frame.setAlwaysOnTop(true);
        // frame.pack();

        // frame.setSize(710, 400);
        frame.setResizable(false);
        // frame.setFocusable(true);
        // frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null);
        frame.setState(1);
        frame.setVisible(true);

        ImageFlasher.frameState(frame.getState());
        ImageFlasher.setObj(frame);

        
        
                
            
           
           
        // access.notifyObservers("Salve " + Utente);

    }

    public static class ImageFlasher extends JPanel  {

        public static ImageIcon image;
        private int width, height;
        public static boolean flash;
        public static Timer timer;
        public JButton readMsg;
        public static java.awt.Image logo;
        public static int stat;
        public static JFrame sSt;

        public static void setObj(JFrame ew) {

            sSt = ew;
            stat = ew.getState();
            // JOptionPane.showMessageDialog(null, stat);

        }

        // initialize variables, set background color, start timer
        public static void frameState(int st) {

            stat = st;

        }

        public static int setFrameState() {
            return stat;
        }

        
      




        public ImageFlasher() {

           
            setSize(100, 100);
            logo = Toolkit.getDefaultToolkit().getImage(
                    getClass().getClassLoader().getResource("diavoletto.png"));
            // this.setSize(380, 380);
            readMsg = new JButton();
            readMsg.setBorder(null);
            readMsg.setBackground(Color.BLACK);

            // readMsg.setVisible(false);
            readMsg.setEnabled(true);

            readMsg.setSize(150, 110);
            add(readMsg);
            readMsg.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    // timer.setDelay(300);
                    // ImageFlasher.image = new ImageIcon("green.jpg");
                    timer.stop();
                    flash = true;

                    repaint();

                    if (stat == 1) {
                        stat = 0;
                        sSt.setState(stat);
                    } else {
                        stat = 1;
                        sSt.setState(stat);
                    }
                    // frame.setState(ImageFlasher.setFrameState());

                }
            });

            flash = true;
            setBackground(Color.black);

            // image = new ImageIcon( "resources/green.jpg" );
            image = new ImageIcon(getClass().getClassLoader().getResource(
                    "green.jpg"));
            width = image.getIconWidth();
            height = image.getIconHeight();

            timer = new Timer(500, new ImageFlasher.TimerHandler());
            // timer.start();
        }

        public void paint(Graphics g) {
            super.paint(g);

            if (flash == true) {

                readMsg.setIcon(image);
                // image.paintIcon(this,g,0,0);
            } else {

                readMsg.setIcon(new ImageIcon(getClass().getClassLoader()
                        .getResource("red.jpg")));
                // g.fillRect(0,0,width,height);
            }
        }

        private class TimerHandler implements ActionListener {
            // respond to Timer's event

            public void actionPerformed(ActionEvent actionEvent) {
                flash = !flash;
                repaint(); // repaint animator
            } // end method actionPerformed
        } // end class TimerHandler
    }
}