package SmallTest;
import javax.swing.JFrame;

public class ServerTest {
	public static void main(String[] args) {
		Server testClient = new Server();
		testClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testClient.startRunning();
	}
}
