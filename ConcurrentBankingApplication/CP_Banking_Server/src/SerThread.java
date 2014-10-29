import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SerThread implements Runnable {
	Socket sock;
	BufferedReader br = null;
	InputStreamReader isr = null;
	PrintWriter outWriter = null;

	ConcurrentHashMap<String, ReentrantLock> map;
	String userName;

	public SerThread(Socket sock, ConcurrentHashMap<String, ReentrantLock> map) {
		this.sock = sock;
		this.map = map;
	}

	/**
	 * Thread will run till the client window is closed. User may login and
	 * log-off. While loop breaks when it receives 6 from client.
	 */
	@Override
	public void run() {
		try {
			isr = new InputStreamReader(sock.getInputStream());
			br = new BufferedReader(isr);
			IniReader iniReader = null;

			String balance = "";
			String accNo = "";

			outWriter = new PrintWriter(sock.getOutputStream(), true);
			loop: while (true) {
				// server reads from client details such as what operation to be
				// performed, user name and account number in the form of a
				// string separated by ",". input is of type
				// "option,user name,amount,account number"
				String input = br.readLine();
				String[] info = input.split(",");

				switch (info[0]) {
				case "0": // existing user scenario
					userName = info[1];
					accNo = info[2];
					map.put(userName, new ReentrantLock());
					// iniReader object is used to perform operations on user
					// data text file.
					iniReader = new IniReader(userName, map,
							Integer.parseInt(accNo));
					balance = iniReader.loadAndGetProperty(userName, accNo,
							"Balance");
					// server returns acc number and balance to client
					outWriter.println(accNo + "," + balance);
					break;

				case "1": // case for get/view balance/refresh scenario
					balance = iniReader.loadAndGetProperty(userName, accNo,
							"Balance");
					outWriter.println(balance);
					break;

				case "2": // Withdrawal from self
					// calling withdraw method. info[1] has amount
					iniReader.withdraw(info[1]);
					balance = iniReader.loadAndGetProperty(userName, accNo,
							"Balance");
					outWriter.println(balance);
					break;

				case "3": // Transfer to another user
					// calling transfer method. info[1] has user
					// info[2] has amount to be transferred
					// info[3] has account number
					iniReader.transfer(info[1], info[3], info[2]);
					balance = iniReader.loadAndGetProperty(userName, accNo,
							"Balance");
					outWriter.println(balance);
					break;

				case "4": // Deposit to self
					// calling deposit method. info[1] has amount
					iniReader.deposit(info[1]);
					balance = iniReader.loadAndGetProperty(userName, accNo,
							"Balance");
					outWriter.println(balance);
					break;

				case "5": // to create a new account
					userName = info[1];
					// generating account number by using AtomicInteger
					int aNum = Server.accNoGenerator.incrementAndGet();
					accNo = Integer.toString(aNum);
					// creating a lock for the new user
					map.put(userName, new ReentrantLock());
					iniReader = new IniReader(userName, map, aNum);
					// creating file for new user
					iniReader.onCreate(userName, accNo);
					// sending bal info. New user has 0 balance initially.
					outWriter.println(aNum + ",0");
					break;

				case "6": // 6 for exit
					break loop;

				default:
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// closing all open streams and socket.
			try {
				br.close();
				isr.close();
				outWriter.close();
				if (!sock.isClosed()) {
					sock.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}