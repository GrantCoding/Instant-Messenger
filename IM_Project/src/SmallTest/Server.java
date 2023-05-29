package SmallTest;


import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow; //Display conversation
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server; // Port connections
	private Socket connection; //Computer connection
	
	boolean hasNumberSign = true;
	int valueFlipper = 1;
	int GroupCounter = 0;
	boolean readyToExit = false;
	
	//constructor
	public Server() {
		super("Server");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event){
				sendMessage(event.getActionCommand());
				userText.setText("");
		}});
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
		
	}
	
	//Runs the server
	public void startRunning() {
		try {
			server = new ServerSocket(6789, 100); //Server port with how many users can theoretically be on.
			while(true) {
				try {
					userConnecting();
					streamSetup();
					Conversation();
				}catch(EOFException eofException) {
					showMessage("\n Server ended the connection!");
				}finally {
					if (GroupCounter == 2) {
						closeConversation();
					}
					
				}
			}
		}catch(IOException ioException) 
		{ioException.printStackTrace();}
	}
	
	
	//User connection wait, and confirms once connected
	private void userConnecting() throws IOException {
		showMessage(" Waiting for other user to connect... \n");
		connection = server.accept();
		showMessage(" Connected to " + connection.getInetAddress().getHostName());
	}
	
	
	//Stream will receive and transmit data
	private void streamSetup() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush(); //Closes the channel
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Server ready to transmit data! \n");
	}
	
	//Able to have a conversation
	private void Conversation() throws IOException {
		String message = " You are now connected! ";
		sendMessage(message);
		
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
					showMessage("\n" + message);
		
					if (message.equals("CLIENT - #")) {
						valueFlipper = 0;
						hasNumberSign = true;
						userText.setEditable(true);
						ableToType(true);
					}
					if (message.equals("CLIENT - Exit")) {
						valueFlipper = 0;
						hasNumberSign = true;
						userText.setEditable(true);
						ableToType(true);
						readyToExit = true;
						GroupCounter++;
						showMessage("\nCLIENT has requested to exit the conversation. Type Exit if you are finised");
					}
				}catch(ClassNotFoundException classNotFoundException) {
					showMessage("\n Unknown Message ");
				}
				
			} while(!message.equals("CLIENT - Exit"));
		}

		

		
	}
	
	//ends the conversation and closes the existing streams
	private void closeConversation() throws IOException {
		showMessage("\n Closing connections... \n");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	//Sends a message to the client
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
			if (message.equals("#")) {
				hasNumberSign = false;
				userText.setEditable(false);
				ableToType(false);
				valueFlipper = 1;
			}
			if(message.equals("Exit")){
				GroupCounter ++;
				hasNumberSign = false;
				userText.setEditable(false);
				ableToType(false);
				valueFlipper = 1;
			}
		} catch(IOException ioException) {
			chatWindow.append("\n Error: Message can't be sent");
		}
	}
	
	//Updates chatWindow
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(text);
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
