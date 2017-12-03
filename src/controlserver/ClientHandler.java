package controlserver;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

class ClientHandler extends Thread {
    private JPanel cPanel = null;
    private JPanel screenPanel = null;
    private Socket cSocket = null;
    private JButton bt = null;
    public ClientHandler(Socket cSocket, JPanel cPanel, JPanel screen) {
        this.cSocket = cSocket;
        this.cPanel = cPanel;
        this.screenPanel = screen;
        start();
    }
    public void pauseThread(){
        stop();
    }
    public void resumeThread(){
        start();
    }
    public void drawGUI(){
        //ImageIcon image = new ImageIcon("C:/Users/iemn/OneDrive/ControlServer/src/image/user.pnp");
        JButton cButton = new JButton(cSocket.getInetAddress().toString());
        //cButton.setIcon(image);
        this.bt = cButton;
        cPanel.add(cButton);
        cPanel.revalidate();
        cPanel.repaint();
    }
    
    public void run(){

        //used to represent client screen size
        Rectangle clientScreenDim = null;
        ObjectInputStream ois = null;
        drawGUI();
        
        
        try {
            //Read client screen dimension
            ois = new ObjectInputStream(cSocket.getInputStream());
            clientScreenDim = (Rectangle) ois.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        //Bắt đầu chụp ảnh
        new ClientScreenReciever(ois, screenPanel, bt,cSocket);
        
        new ClientCommandsSender(cSocket, screenPanel, clientScreenDim);
    }

}
