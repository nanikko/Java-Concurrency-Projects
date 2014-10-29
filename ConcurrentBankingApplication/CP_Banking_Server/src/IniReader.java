import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class IniReader {

	final Properties prop = new Properties();

	String name ;
	ConcurrentHashMap<String, ReentrantLock> map;
	int acNumber;

	public IniReader(String name, ConcurrentHashMap<String, ReentrantLock> map, int number) {
		this.name = name;
		this.map= map;
		this.acNumber = number;
	}

	/**
	 * Called when new user is created. Each new user is created a file having
	 * initial balance as 0.
	 * @param fileName
	 * @param accNumber
	 */
	public void onCreate(String fileName, String accNumber){
		File f = new File(fileName);
		try {
			f.createNewFile();
			prop.load(new FileInputStream(f));
			prop.setProperty("Name", fileName);
			prop.setProperty("Acc Number", accNumber);
			prop.setProperty("Balance", "0");
			prop.store(new FileOutputStream(f), "Init");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Loads the file, reads it and returns property value from it.
	 * @param fileName
	 * @param accNo
	 * @param property
	 * @return
	 */
	public String loadAndGetProperty(String fileName, String accNo, String property){
		String propVal="";
		ReentrantLock reLock = map.get(fileName);

		// acquiring lock
		reLock.lock();
		try {
			// reading passed property value
			prop.load(new FileInputStream(fileName));
			propVal = prop.getProperty(property);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// releasing lock
			if (reLock.isLocked())
				reLock.unlock();
		}
		return propVal;
	}

	/**
	 * updates it with new value and returns true if old value and current value
	 * in file are same else returns false
	 * @param old
	 * @param newVal
	 * @param fileName
	 * @param aNo
	 * @param property
	 * @return
	 */
	public boolean commit(String old, String newVal, String fileName, String aNo, String property) {
		ReentrantLock reLock = map.get(fileName);

		// acquiring lock
		reLock.lock();
		try {
			// checking if old equals current value in the file. If equals
			// write the new value to the file
			if (old.equals(loadAndGetProperty(fileName, aNo, "Balance"))) {
				prop.setProperty(property, newVal);
				prop.store(new FileOutputStream(fileName), this.name);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// releasing lock
			if (reLock.isLocked())
				reLock.unlock();
		}
		return false;
	}

	/**
	 * Deposits the entered amount to user's account (Self). This follows
	 * Optimistic Synchronisation pattern.
	 * @param amt
	 */
	public void deposit(String amt) {
		boolean success = false;
		String aNo = Integer.toString(acNumber);
		do {
			// getting current balance value. loadAndGetProperty is atomic as it
			// locks before getting value
			String propValue = loadAndGetProperty(this.name, aNo, "Balance");

			// calculating new value for balance
			int curBal = Integer.parseInt(propValue);
			int depAmt = Integer.parseInt(amt);
			int netBal = curBal + depAmt;

			// commits only when curBal and value stored in file is still same
			// commit is atomic here
			success = commit(Integer.toString(curBal),
					Integer.toString(netBal), this.name, aNo, "Balance");

		} while (!success);
	}

	/**
	 * Withdraws the entered amount from user's account (Self). This follows
	 * Optimistic Synchronisation pattern.
	 * @param amt
	 */
	public void withdraw(String amt){
		boolean success = false;
		String aNo = Integer.toString(acNumber);
		do {
			// getting current balance value. loadAndGetProperty is atomic as it
			// locks before getting value
			String propValue = loadAndGetProperty(this.name, aNo, "Balance");

			// calculating new value for balance
			int curBal = Integer.parseInt(propValue);
			int depAmt = Integer.parseInt(amt);
			int netBal = curBal - depAmt;

			// commits only when curBal and value stored in file is still same
			// commit is atomic here
			success = commit(Integer.toString(curBal), Integer.toString(netBal), this.name, aNo, "Balance");

		} while (!success);
	}

	/**
	 * @param userName --> user name of the other user (receiver)
	 * @param aNo --> account no of the other user (receiver)
	 * @param amt --> amount to be transferred
	 * method used to transfer funds from one user to another.
	 */
	public void transfer(String userName, String aNo, String amt) {
		// getting random wait time used for exponential back off
		int waitTime = getRandom();
		int waitCount = 0;

		// getting lock object for both users
		ReentrantLock ownLock = map.get(this.name);
		ReentrantLock otherLock = map.get(userName);

		while (true) {
			if (ownLock.tryLock()) {
				// if both locks are obtained, update the balance in both
				// files and unlock both locks. If failed to get other user's
				// lock,
				// then release the obtained lock and back off
				if (otherLock.tryLock()) {
					// when both locks are obtained reaches here.
					String selfPropValue = loadAndGetProperty(this.name,
							Integer.toString(this.acNumber), "Balance");
					String propValue = loadAndGetProperty(userName, aNo,
							"Balance");

					int amtVal = Integer.parseInt(amt);

					// calculation for self account
					int selfCurBal = Integer.parseInt(selfPropValue);
					int selfNetBal = selfCurBal - amtVal;

					// calculation for other user account
					int otherCurBal = Integer.parseInt(propValue);
					int otherNetBal = otherCurBal + amtVal;

					try {
						// saving transaction of self user
						prop.load(new FileInputStream(this.name));
						prop.setProperty("Balance",
								Integer.toString(selfNetBal));
						prop.store(new FileOutputStream(this.name), this.name);

						// saving transaction of other user
						prop.load(new FileInputStream(userName));
						prop.setProperty("Balance",
								Integer.toString(otherNetBal));
						prop.store(new FileOutputStream(userName), this.name);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// releasing locks for both users
					otherLock.unlock();
					ownLock.unlock();
					break;
				} else {
					// when self lock is obtained but failed to get lock on the
					// other user
					ownLock.unlock();
					Double expFactor = Math.pow(2, ++waitCount);
					try {
						Thread.sleep(waitTime * expFactor.intValue());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				// when both locks are not obtained
				Double expFactor = Math.pow(2, ++waitCount);
				try {
					Thread.sleep(waitTime * expFactor.intValue());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// returns a random number up to 200
	public int getRandom(){
		Random r = new Random();
		int timeLimit = r.nextInt(200) ;//sleep time
		return timeLimit ;
	}

	// function used to print all properties of a User.
	public void getAllDetails(String fileName) {
		ReentrantLock reLock = map.get(fileName);
		reLock.lock();
		try {
			prop.load(new FileInputStream(fileName));
			prop.list(System.out);
		} catch (Exception e) {
			System.out.println(e);
		}
		finally{
			reLock.unlock();
		}
	}
}
