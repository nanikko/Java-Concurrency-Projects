import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Start extends JFrame{

	private static final long serialVersionUID = 1L;

	private JButton NewUser;
	private JButton ExistingUser;
	private JPanel panel;
	private JLabel label;

	final String HOST = "127.0.0.1";
	final int PORT = 1234;

	static Socket sock = null;
	static PrintWriter sockWriter = null;
	static BufferedReader sockReader = null;

	public static void main(String[] args) throws UnknownHostException, IOException  {
		Start setStart = new Start();
		setStart.init();
	}

	public void init() {
		// connecting to server on a particular port
		try {
			sock = new Socket(HOST, PORT);
			sockWriter = new PrintWriter(sock.getOutputStream(), true);
			sockReader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			// displays the menu containing new or existing user.
			menu();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// handles UI part
	public void menu() {
		panel = new JPanel();

		// setting label values
		label = new JLabel("Main Menu");
		NewUser = new JButton("New User");
		ExistingUser = new JButton("Existing User");

		Container cont = getContentPane();
		cont.setLayout(new FlowLayout());

		// adding labels and buttons to panel
		panel.setLayout(new GridLayout(6, 1, 20, 20));
		panel.add(label);
		panel.add(NewUser);
		panel.add(ExistingUser);

		cont.add(panel, BorderLayout.CENTER);

		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Main Menu");
		setBounds(100, 100, 300, 350);
		setResizable(false);
		setVisible(true);

		// action listener for NewUser button
		NewUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				// call to new user UI class
				NewUser nUser = new NewUser(sock, sockReader, sockWriter);
				nUser.newAccount();
			}
		});

		// action listener for ExistingUser button
		ExistingUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				// call to existing user UI class
				ExistingUser eUser = new ExistingUser(sock, sockReader, sockWriter);
				eUser.existingAccount();
			}
		});

		// handling the window close. Closing all streams and sockets
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
			sockWriter.println("6," + "close");
				try {
					sock.close();
					sockReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				sockWriter.close();
				System.exit(0);
		    }
		});
	}
}
