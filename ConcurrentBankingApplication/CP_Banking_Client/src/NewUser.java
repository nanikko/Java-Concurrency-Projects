import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class NewUser extends JFrame {
	Socket sock ;
	PrintWriter sockWriter = null;
	BufferedReader sockReader = null;

	private static final long serialVersionUID = 1L;

	private JTextField NAME;
	private JTextField ADDRESS;
	private JTextField AGE;

	// constructor which takes socket info as parameters
	public NewUser(Socket sock, BufferedReader sockReader,  PrintWriter sockWriter) {
		this.sock = sock;
		this.sockReader = sockReader;
		this.sockWriter = sockWriter;
	}

	public void newAccount() {

		// setting label values
		JLabel headLabel = new JLabel("Enter Customer Information");
		JLabel nameLabel = new JLabel("NAME");
		JLabel addressLabel = new JLabel("ADDRESS");
		JLabel ageLabel = new JLabel("AGE");

		// setting buttons and textfields
		JButton ADD = new JButton("ADD");
		JButton CANCEL = new JButton("CANCEL");
		NAME = new JTextField(15);
		ADDRESS = new JTextField(15);
		AGE = new JTextField(3);

		// setting head panel
		JPanel jpanel1 = new JPanel();
		jpanel1.setLayout(new GridLayout(1, 1, 1, 1));
		jpanel1.add(headLabel);

		// adding field labels to panel 2.
		JPanel jpanel2 = new JPanel();
		jpanel2.setLayout(new GridLayout(4, 1, 20, 20));
		jpanel2.add(nameLabel);
		jpanel2.add(addressLabel);
		jpanel2.add(ageLabel);

		// adding input fields to panel 3.
		// address and age fields are not stored into the files.
		JPanel jpanel3 = new JPanel();
		jpanel3.setLayout(new GridLayout(4, 1, 20, 20));
		jpanel3.add(NAME);
		jpanel3.add(ADDRESS);
		jpanel3.add(AGE);

		// adding add & cancel buttons to panel 4
		JPanel jpanel4 = new JPanel();
		jpanel4.setLayout(new FlowLayout());
		jpanel4.add(ADD);
		jpanel4.add(CANCEL);

		JPanel jpanel5 = new JPanel();
		jpanel5.setLayout(new FlowLayout());
		jpanel5.add(jpanel2);
		jpanel5.add(jpanel3);

		// all the panels are put into containers
		Container container1 = getContentPane();
		container1.setLayout(new FlowLayout());
		container1.add(jpanel1, BorderLayout.CENTER);

		Container container2 = getContentPane();
		container2.setLayout(new FlowLayout());
		container2.add(jpanel5, BorderLayout.CENTER);

		Container container3 = getContentPane();
		container3.setLayout(new FlowLayout());
		container3.add(jpanel4);

		setTitle("New Account Creation");
		setBounds(100, 100, 300, 350);
		setVisible(true);
		setResizable(false);

		// action listener for ADD button
		ADD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 5 --> new account
				sockWriter.println("5," + NAME.getText());
				setVisible(false);

				// appending name,account number
				StringBuilder sb = new StringBuilder(NAME.getText());
				sb.append(",").append(readInfo());

				// call to dash board
				DashBoard db = new DashBoard(sock, sockReader, sockWriter, sb.toString());
				db.viewDashBoard();
			}
		});

		// action listener for CANCEL button
		CANCEL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// on cancel sending a dummy value. Socket is closed only when 6 is sent.
				sockWriter.println("7,");
				setVisible(false);

				// calling initial menu
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