import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
	static ConcurrentHashMap<String, ReentrantLock> map = new ConcurrentHashMap<String, ReentrantLock>();
	public static AtomicInteger accNoGenerator ;
	final static int PORT = 1234;
	final static int START_NUMBER = 1000;

	public static void main(String[] args){
		// Used to generate Account number starting from START_NUMBER
		accNoGenerator = new AtomicInteger(START_NUMBER);
		ServerSocket serSock = null;
		try {
			serSock = new ServerSocket(PORT);

			// Listening for a connection to be made to this socket and
			// accepting it. Each new connection is started in a new thread
			while(true){
				Socket sock = serSock.accept();
				Thread t = new Thread(new SerThread(sock, map));
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			// closing server socket
			if(serSock != null){
				try {
					serSock.close();
				} catch (IOException e) {}
			}
		}
	}
}
