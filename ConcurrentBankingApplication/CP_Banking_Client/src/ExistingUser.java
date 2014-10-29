import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ExistingUser extends JFrame {
	private static final long serialVersionUID = 1L;

	Socket sock ;
	PrintWriter sockWriter = null;
	BufferedReader sockReader = null;

	private JTextField USERNAME;
	private JTextField ACC;

	// constructor which takes socket info as parameters
	public ExistingUser(Socket sock, BufferedReader sockReader,  PrintWriter sockWriter) {
		this.sock = sock;
		this.sockReader = sockReader;
		this.sockWriter = sockWriter;
	}

	/**
	 *
	 */
	public void existingAccount(){

		// setting label values
		JLabel headerLabel = new JLabel("Enter Login Information");
		JLabel userNameLabel = new JLabel("USERNAME");
		JLabel accNoLabel = new JLabel("Acc No:");

		// setting buttons and textfields
		JButton LOGIN = new JButton("LOGIN");
		JButton CANCEL = new JButton("CANCEL");
		USERNAME = new JTextField(15);
		ACC = new JTextField(15);

		// setting head panel
		JPanel jpanel1 = new JPanel();
		jpanel1.setLayout(new GridLayout(1, 1, 1, 1));
		jpanel1.add(headerLabel);

		// adding labels to panel 2.
		JPanel jpanel2 = new JPanel();
		jpanel2.setLayout(new GridLayout(4, 1, 20, 20));
		jpanel2.add(userNameLabel);
		jpanel2.add(accNoLabel);

		// adding input fields to panel 3.
		JPanel jpanel3 = new JPanel();
		jpanel3.setLayout(new GridLayout(4, 1, 20, 20));
		jpanel3.add(USERNAME);
		jpanel3.add(ACC);

		// adding LOGIN & CANCEL buttons to panel 4
		JPanel jpanel4 = new JPanel();
		jpanel4.setLayout(new FlowLayout());
		jpanel4.add(LOGIN);
		jpanel4.add(CANCEL);

		JPanel jpanel5 = new JPanel();
		jpanel5.setLayout(new FlowLayout());
		jpanel5.add(jpanel2);
		jpanel5.add(jpanel3);

		// all the panels are put into containers
		Container headContainer = getContentPane();
		headContainer.setLayout(new FlowLayout());
		headContainer.add(jpanel1, BorderLayout.CENTER);

		Container loginContainer = getContentPane();
		loginContainer.setLayout(new FlowLayout());
		loginContainer.add(jpanel5, BorderLayout.CENTER);

		Container btnContainer = getContentPane();
		btnContainer.setLayout(new FlowLayout());
		btnContainer.add(jpanel4, BorderLayout.CENTER);

		setTitle("User Login");
		setBounds(100, 100, 300, 350);
		setVisible(true);
		setResizable(false);

		// action listener for LOGIN button
		LOGIN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 0 --> Existing user
				// sending 0 to server indicating operation for existing user
				sockWriter.println("0," + USERNAME.getText() + "," + ACC.getText());
				setVisible(false);

				// appending name,account number
				StringBuilder sb = new StringBuilder(USERNAME.getText());
				sb.append(",").append(readInfo());

				// call to dash board on clicking Login button
				DashBoard db = new DashBoard(sock, sockReader, sockWriter, sb.toString());
				db.viewDashBoard();
			}
		});

		// action listener for CANCEL button
		CANCEL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// on clicking cancel sending a dummy value.
				// Socket is closed only when 6 is sent.
				sockWriter.println("7,");
				setVisible(false);

				// calling initial menu on clicking cancel
				Start mainMenu = new Start();
				mainMenu.menu();
			}
		});

		// handling the window close. Closing all streams and sockets
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
			// passing 6 to server closes the socket
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

	// returns account No generated from server for the new account.
	public String readInfo(){
		String info = "";
		try {
			info = sockReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return info;
	}
}