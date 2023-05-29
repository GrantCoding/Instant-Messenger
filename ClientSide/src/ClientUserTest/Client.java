package ClientUserTest;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	boolean hasNumberSign = true;
	int valueFlipper = 0;
	int GroupCounter = 0;
	boolean readyToExit = false;
	
	//constructor
	public Client(String host) {
		super("Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendClientMessage(event.getActionCommand());
						userText.setText("");
					}
				}
			);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
		
	}
	
	//Connects to server
	public void startRunning() {
		try {
			connectToServer();
			createStreams();
		
			clientConversation();
		} catch(EOFException eofException) {
			showClientMessage("\n Client terminated connection");
		} catch(IOException ioException) {
			ioException.printStackTrace();
		} finally {
			if(GroupCounter == 2) {
				closeClient();
			}
			
		}
	}
	
	//connect to server
	private void connectToServer() throws IOException {
		showClientMessage("Attempting connection.. \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789); //Change the port number if you want a different port
		showClientMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//Creates the streams for the client
	private void createStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showClientMessage("\n Connection Streams Successful \n");
	}
	
	//Chats with server
	private void clientConversation() throws IOException {
		if (valueFlipper == 0) {
			ableToType(true);
		}
		if (valueFlipper == 1) {
			ableToType(false);
		}
		while (GroupCounter < 2) {
			do {
				try {
					message = (String) input.readObject();
					showClientMessage("\n" + message);
					if (message.equals("SERVER - #")) {
						hasNumberSign = true;
						userText.setEditable(true);
						ableToType(true);
						valueFlipper = 1;
					}
					if (message.equals("SERVER - Exit")) {
						valueFlipper = 1;
						hasNumberSign = true;
						userText.setEditable(true);
						ableToType(true);
						readyToExit = true;
						GroupCounter++;
						showClientMessage("\nSERVER has requested to exit the conversation. Type Exit if you are finised");
					}
				} catch(ClassNotFoundException classNotfoundException) {
					showClientMessage("\n Error: Unknown Object Type");
				}
			}while(!message.equals("SERVER - Exit"));
		}

		
	}
	
	//close the existing connections
	private void closeClient() {
		showClientMessage("\n Closing all connections...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//send messages to server
	private void sendClientMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showClientMessage("\nCLIENT - " + message);
			if (message.equals("#")) {
				hasNumberSign = false;
				userText.setEditable(false);
				ableToType(false);
				valueFlipper = 0;
			}
			if(message.equals("Exit")){
				GroupCounter ++;
				hasNumberSign = false;
				userText.setEditable(false);
				ableToType(false);
				valueFlipper = 0;
			}
		} catch(IOException ioException) {
			chatWindow.append("\n Can't send message");
		}
	}
	
	//Displays the user message
	private void showClientMessage(final String clientText) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(clientText);
					}
				}
			);
	}
	
	//Lets user use the textbox
	private void ableToType(final boolean ToF) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userText.setEditable(ToF);
					}
				}
			);
	}
}
