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

public class DashBoard extends JFrame{
	private static final long serialVersionUID = 1L;

	Socket sock ;
	PrintWriter sockWriter = null;
	BufferedReader sockReader = null;
	String info;

	JLabel userNameValLabel ;
	JLabel balanceValLabel ;
	JLabel accNoValLabel ;
	JTextField amtInput;
	JTextField toUser;
	JTextField toAcc;

	// constructor which takes socket info as parameters
	public DashBoard(Socket sock, BufferedReader sockReader,  PrintWriter sockWriter, String info) {
		this.sock = sock;
		this.sockReader = sockReader;
		this.sockWriter = sockWriter;
		this.info = info;
	}

	public String readBalInfo(){
		String bal = "";
		try {
			bal = sockReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bal;
	}

	public void viewDashBoard(){

		// setting label values
		JLabel userNameLabel = new JLabel("User:");
		JLabel accNoLabel = new JLabel("A/C No.:");
		JLabel balanceLabel = new JLabel("Balance:");
		JLabel amtLabel = new JLabel("Enter Amt:");
		JLabel toUserLabel = new JLabel("Transfer to:");
		JLabel toAccLabel = new JLabel("Transfer to Acc:");

		String[] usrInfo = info.split(",");

		// setting text fields
		userNameValLabel = new JLabel(usrInfo[0]);// setting username
		balanceValLabel = new JLabel(usrInfo[2]);// setting balance
		accNoValLabel = new JLabel(usrInfo[1]);// setting acc no
		amtInput = new JTextField(15);
		toUser = new JTextField(15);
		toAcc = new JTextField(15);

		// setting buttons for withdrawn, deposit etc
		JButton Withdraw_btn = new JButton("Withdraw");
		JButton Deposit_btn = new JButton("Deposit");
		JButton Transfer_btn = new JButton("Transfer");
		JButton Refresh_btn = new JButton("Refresh");
		JButton Logoff_btn = new JButton("Logoff");

		// adding labels to panel 2.
		JPanel jpanel2 = new JPanel();
		jpanel2.setLayout(new GridLayout(6, 1, 20, 20));
		jpanel2.add(userNameLabel);
		jpanel2.add(accNoLabel);
		jpanel2.add(balanceLabel);
		jpanel2.add(amtLabel);
		jpanel2.add(toUserLabel);
		jpanel2.add(toAccLabel);

		// adding input fields to panel 3.
		JPanel jpanel3 = new JPanel();
		jpanel3.setLayout(new GridLayout(6, 1, 20, 20));
		jpanel3.add(userNameValLabel);
		jpanel3.add(accNoValLabel);
		jpanel3.add(balanceValLabel);
		jpanel3.add(amtInput);
		jpanel3.add(toUser);
		jpanel3.add(toAcc);

		// adding WITHDRAWAL, DEPOSIT & TRANSFER buttons to panel 4
		JPanel jpanel4 = new JPanel();
		jpanel4.setLayout(new FlowLayout());
		jpanel4.add(Withdraw_btn);
		jpanel4.add(Deposit_btn);
		jpanel4.add(Transfer_btn);

		// adding REFRESH & LOGOFF buttons to panel 4
		JPanel jpanel6 = new JPanel();
		jpanel6.setLayout(new FlowLayout());
		jpanel6.add(Refresh_btn);
		jpanel6.add(Logoff_btn);

		JPanel jpanel5 = new JPanel();
		jpanel5.setLayout(new FlowLayout());
		jpanel5.add(jpanel2);
		jpanel5.add(jpanel3);

		// all the panels are put into containers
		Container loginContainer = getContentPane();
		loginContainer.setLayout(new FlowLayout());
		loginContainer.add(jpanel5, BorderLayout.CENTER);

		Container btnContainer = getContentPane();
		btnContainer.setLayout(new FlowLayout());
		btnContainer.add(jpanel4, BorderLayout.CENTER);
		btnContainer.add(jpanel6, BorderLayout.CENTER);

		setTitle("Account Information");
		setBounds(100, 100, 300, 350);
		setVisible(true);
		setResizable(false);

		// action listener for WITHDRAW button
		Withdraw_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 2 --> WITHDRAW operation
				// sending 2 to server indicates withdraw operation.
				sockWriter.println("2," + amtInput.getText());
				balanceValLabel.setText(readBalInfo()) ;
				clearAll();
			}
		});

		// action listener for DEPOSIT button
		Deposit_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 4 --> DEPOSIT operation
				// sending 4 to server indicates deposit operation.
				sockWriter.println("4," + amtInput.getText());
				balanceValLabel.setText(readBalInfo()) ;
				clearAll();
			}
		});

		// action listener for TRANSFER button
		Transfer_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 3 --> TRANSFER operation
				// sending 3 to server indicates transfer operation.
				// Here, current user is transferring to "toUser" an amount of
				// "amtInput" having account no as "toAcc"
				sockWriter.println("3," + toUser.getText() + "," + amtInput.getText() + "," + toAcc.getText());
				balanceValLabel.setText(readBalInfo()) ;
				clearAll();
			}
		});

		// action listener for REFRESH button
		Refresh_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1 --> GET_BALANCE operation
				// sending 1 to server indicates get balance operation.
				sockWriter.println("1");
				balanceValLabel.setText(readBalInfo()) ;
				clearAll();
			}
		});

		// action listener for LOGOFF button
		Logoff_btn.addActionListener(new ActionListener() {
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
			// sending 6 to server closes the socket
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

	/**
	 * clears all input fields
	 */
	public void clearAll(){
		amtInput.setText("");
		toUser.setText("");
		toAcc.setText("");
	}
}