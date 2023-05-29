package ClientUserTest;
import javax.swing.JFrame;


public class ClientTest {
	public static void main(String[] args) {
		Client userClient;
		//Change the quotes to the server's ip4 address
		userClient = new Client("127.0.0.1");
		userClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		userClient.startRunning();
	}
}
