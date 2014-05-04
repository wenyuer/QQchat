package help;

import java.net.Socket;

public class ClientInfo {
	private Socket clientSocket;
	private String clientIP;
	private String clientPort;
	private String userName;

	public Socket getClientSocket() {
		return clientSocket;
	}
	
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public String getClientIP() {
		return clientIP;
	}
	
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	
	public String getClientPort() {
		return clientPort;
	}
	
	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName (String userName) {
		this.userName = userName;
	}
}
