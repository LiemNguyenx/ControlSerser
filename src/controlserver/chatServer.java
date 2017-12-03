/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlserver;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author LiemNguyen
 */
public class chatServer extends Thread {

    private static HashMap<Integer, InetAddress> clientSet = new HashMap<Integer, InetAddress>();
    public JPanel chatPanel = null;
    public JTextArea chatArea = null;
    public JButton sendButton = null;
    public JTextField textChat = null;

    public chatServer(JTextArea chatArea, JButton sendButton, JTextField text) {
        this.chatArea = chatArea;
        this.sendButton = sendButton;
        this.textChat = text;
        start();
    }

    public void sendFromServer(HashSet<Integer> clientSet) {

    }

    public void run() {
        //int serverport = 7777;

        try {
            DatagramSocket udpServerSocket = new DatagramSocket(7777);
            System.out.println("Chat Server started...\n");
            sendButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    byte[] sendData = new byte[1024];
                    String sendMessage = "Server: "+textChat.getText();
                    sendData = sendMessage.getBytes();
                    chatArea.append("ME: "+textChat.getText()+"\n");
                    textChat.setText("");
                    for (Map.Entry<Integer, InetAddress> entry : clientSet.entrySet()) {
                        // Create a DatagramPacket to send, using the buffer, the clients IP address, and the clients port
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, entry.getValue(), entry.getKey());
                        System.out.println("Sending");
                        try {
                            // Send the message
                            udpServerSocket.send(sendPacket);
                        } catch (IOException ex) {
                            Logger.getLogger(chatServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });
            while (true) {
                // Create byte buffers to hold the messages to send and receive
                byte[] receiveData = new byte[1024];

                // Create an empty DatagramPacket packet
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                // Block until there is a packet to receive, then receive it  (into our empty packet)
                udpServerSocket.receive(receivePacket);

                // Extract the message from the packet and make it into a string, then trim off any end characters
                String clientMessage = (new String(receivePacket.getData())).trim();

                // Print some status messages
                System.out.println("Client Connected - Socket Address: " + receivePacket.getSocketAddress());
                System.out.println("Client message: \"" + clientMessage + "\"");
                // Display on chatArea
                chatArea.append(receivePacket.getSocketAddress() + " : " + clientMessage + "\n");
                // Get the IP address and the the port number which the received connection came from
                InetAddress clientIP = receivePacket.getAddress();

                // Print out status message
                System.out.println("Client IP Address & Hostname: " + clientIP + ", " + clientIP.getHostName() + "\n");

                // Get the port number which the recieved connection came from
                int clientport = receivePacket.getPort();
                System.out.println("Adding " + clientport);
                clientSet.put(clientport, clientIP);

                // Response message			
                String returnMessage = clientMessage;
                //String messageFromServer = textChat.getText();
                System.out.println(returnMessage);
                // Create an empty buffer/array of bytes to send back 
                byte[] sendData = new byte[1024];

                // Assign the message to the send buffer
                sendData = returnMessage.getBytes();

//                for (Integer port : clientSet) {
//                    System.out.println(port != clientport);
//                    if (port != clientport) {
//                        // Create a DatagramPacket to send, using the buffer, the clients IP address, and the clients port
//                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, port);
//                        System.out.println("Sending");
//                        // Send the echoed message          
//                        udpServerSocket.send(sendPacket);
//                    }
//                }
                for (Map.Entry<Integer, InetAddress> entry : clientSet.entrySet()) {
                    System.out.println(entry.getKey() != clientport);
                    if (entry.getKey() != clientport) {
                        // Create a DatagramPacket to send, using the buffer, the clients IP address, and the clients port
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP, entry.getKey());
                        System.out.println("Sending");
                        // Send the echoed message          
                        udpServerSocket.send(sendPacket);
                    }
                }
                System.out.println("So luong client: " + clientSet.size());
                for (Map.Entry<Integer, InetAddress> entry : clientSet.entrySet()) {
                    System.out.println(entry.getKey());
                    System.out.println(entry.getValue());
                }

            }

        } catch (SocketException ex) {
            Logger.getLogger(chatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(chatServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
