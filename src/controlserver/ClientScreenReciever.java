package controlserver;

import java.awt.Button;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

class ClientScreenReciever extends Thread {

    private ObjectInputStream cObjectInputStream = null;
    private JPanel cPanel = null;
    private boolean continueLoop = true;
    private JButton bt;
    private boolean isPaused = true;
    private String textBt = null;
    private PrintWriter writer = null;
    private Socket soc = null;

    public ClientScreenReciever(ObjectInputStream ois, JPanel p, JButton bt, Socket c) {
        cObjectInputStream = ois;
        cPanel = p;
        this.bt = bt;
        this.soc = c;
        textBt = bt.getText();

        try {
            writer = new PrintWriter(soc.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientScreenReciever.class.getName()).log(Level.SEVERE, null, ex);
        }

        start();
        bt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isPaused = !isPaused;
                if (!isPaused) {
                    writer.println(EnumCommands.START_CONTROL1.getAbbrev());
                    System.out.print("START----------: " + EnumCommands.START_CONTROL1.getAbbrev());
                    bt.setText(textBt);
                    unpause();
                    System.out.print("Unpaused");
                } else {
                    writer.flush();
                    writer.println(EnumCommands.STOP_CONTROL.getAbbrev());
                    bt.setText(textBt + " paused");
                    System.out.println("STOP_CONTROL--------------:      " + EnumCommands.STOP_CONTROL.getAbbrev());
                }
            }
        });
    }

    public void unpause() {
        synchronized (this) {
            this.notify();
        }
    }

    public void run() {

        try {
            //Read screen of client
            //int i = 0;
            while (continueLoop) {
                if (isPaused) {
                    synchronized (this) {
                        this.wait();
                    }
                    System.out.println(this + " paused");
                }
                //Recieve client screenshot and resize it to the current panel size
                ImageIcon imageIcon = (ImageIcon) cObjectInputStream.readObject();
                System.out.println("New image recieved");
                Image image = imageIcon.getImage();
                image = image.getScaledInstance(cPanel.getWidth(), cPanel.getHeight(), Image.SCALE_FAST);
                //Draw the recieved screenshot
                Graphics graphics = cPanel.getGraphics();
                graphics.drawImage(image, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);
                //System.out.println(this + " cycle " + i);
                //i++;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientScreenReciever.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
