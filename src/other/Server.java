package other;
import help.ClientInfo;
import help.MessageBean;

import java.awt.Choice;
import java.awt.Container;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	ServerSocket serverSocket;
	Socket clientSocket;
	DataInputStream dis;
	DataOutputStream dos;
	List clientList = new ArrayList();
	MessageBean msgBean = new MessageBean();
	
	//String head;
	//String clientIP;
	//String clientPort;
	//String userName;					
	public static void main(String[] args) {
		System.out.println("这是包含线程的服务器");	
		GuiServer guiServer = new GuiServer();
		Server a = new Server();
		guiServer.startButListenter(a);   //启动开始按钮监听事件
		guiServer.endButtonListenter(a);  //启动结束按钮监听事件
//		new Thread(cst).start();
	}
	
	public void startServer(){
			try {
				serverSocket=new ServerSocket(8000);
				
				new Thread(new StartServerThread()).start();			
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public void endServer() {
		System.exit(0);
	}
	

	class ClientProcessThread implements Runnable{
		Socket clientSocket;
		ClientProcessThread(Socket s)
		{
			clientSocket = s;
		}
		public void run()
		{			
			while(true)
			{
				try {
						InputStream ins = clientSocket.getInputStream();
						dis = new DataInputStream(ins);
						String str;
						str = dis.readUTF();
						ClientInfo clientInfo = new ClientInfo();
						int index1 = str.indexOf("/",0);
						if (index1!=-1) {
							System.out.println("来自客户端的消息：" + str);
							String head = str.substring(0,index1);
						
						if("NEW".equals(head)) {
							//MessageBean msgBean = new MesageBean();
							msgBean = msgBean.openString(str);
							String clientIP = msgBean.getIp();
							String clientPort = msgBean.getPort();
							String userName = msgBean.getUserName();
							//ClientInfo clientInfo = new ClientInfo();
							clientInfo.setClientSocket(clientSocket);
							clientInfo.setClientIP(clientIP);
							clientInfo.setClientPort(clientPort);
							clientInfo.setUserName(userName);
							
							//如果为新对象时，将这个对象放在集合中
							clientList.add(clientInfo);
							System.out.println("集合中的个数==" + clientList.size());
							System.out.println("这是" + userName +"--新客户在登录！");	
							sendNewToOld1(clientInfo,str);
							//因为新对象，还得将老对象发给它。
							sendOldToNew(clientInfo);							
						} else if ("ALL".equals(head)) {
							System.out.println("这是server端中ALL分支中的str == " + str);
							//将字符串打散变成一个Bean对象
							MessageBean msgBean = new MessageBean();
							msgBean = msgBean.openString(str);
							sendMsgToAll1(msgBean,str);
						} else if ("MSG".equals(head)) {
							System.out.println("这是server端中单独对单独聊天的str == " + str);
							MessageBean msgBean = new MessageBean();
							msgBean = msgBean.openString(str);
							sendMsgToOne(msgBean,str);
						} else if ("QUT".equals(head)) {
							//接收到QUT的消息，表示有客户端要下线了
							MessageBean msgBean = new MessageBean();
							msgBean = msgBean.openString(str);
							sendQuit(msgBean,str);
						}
					} else {
						System.out.println("你没有按照消息格式发送了这样的消息" + str);
					}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
			}
		
		}
		
	}
	
	class StartServerThread implements Runnable{
		//这是包含线程的服务器
		//ServerSocket serverSocket;
		int count= 0;
		
		public void run(){
			
			while(true){
				try {
						clientSocket = serverSocket.accept();	
						new Thread(new ClientProcessThread(clientSocket)).start();											
						count ++;
					//System.out.println("这是第" + count + "个连接用户");
					//new Thread(new ClientProcessThread(clientSocket)).start();	
				}catch(IOException e){
				e.printStackTrace();
				}
			}
		}
	}
		//这是下线
		private void sendQuit(MessageBean msgBean, String str) {
			String clientIP = msgBean.getIp();
			String clientPort = msgBean.getPort();
			Iterator iter = clientList.iterator();
			while(iter.hasNext()) {
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				String IPInside = clientInfoIn.getClientIP();
				String PortInside = clientInfoIn.getClientPort();
				boolean flag1 = clientIP.equals(IPInside);
				boolean flag2 = clientPort.equals(PortInside);
				if(!flag1 || !flag2) {
					try {
						DataOutputStream dos = new DataOutputStream(clientInfoIn.getClientSocket().getOutputStream());
						dos.writeUTF(str);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} //如果两项都相同，则集合中对象就是下线者，就将它从集合中移除
				else if(flag1 && flag2) {
					iter.remove();
				}
			}
			
		}
		//这是单聊
		private void sendMsgToOne(MessageBean msgBean, String str) {
			// 获取IP与Port
			String clientIP = msgBean.getIp();
			String clientPort = msgBean.getPort();
			
			Iterator iter = clientList.iterator();
			while (iter.hasNext()) {
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				//将集合中对象取出来，将对象中的IP与Port了取出来与接受者的IP与Port比较
				String IPInside = clientInfoIn.getClientIP();
				String PortInside = clientInfoIn.getClientPort();
				
				boolean flag1 = clientIP.equals(IPInside);
				boolean flag2 = clientPort.equals(PortInside);
				
				//当集合中某个对象的IP与Port与发送者两项都相同时，这个对象就是接受者对象，则向这个对象发送消息
				if(flag1 && flag2) {
					try {
						DataOutputStream dos = new DataOutputStream(clientInfoIn.getClientSocket().getOutputStream());
						dos.writeUTF(str);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		//群聊，参数msgBean是客户端点击发送端发来的信息转化为的Bean,此时发来的消息是发送者的资料
		private void sendMsgToAll1(MessageBean msgBean, String str) {
			// 获取发送者的IP与Port
			System.out.println("sendMsgToAll1.....");
			String senderIP = msgBean.getIp();
			String senderPort = msgBean.getPort();
			Iterator iter = clientList.iterator();
			while (iter.hasNext()) {
				//将集合中对象从集合中一个个取出来（包含新对象）
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				//如何保证不发给自己，将集合中的对象取出来，将对象中的IP和Port取出来比较
				String IPInside = clientInfoIn.getClientIP();
				String PortInside = clientInfoIn.getClientPort();
				boolean flag1 = senderIP.equals(IPInside);
				boolean flag2 = senderPort.equals(PortInside);
				if(!flag1 || !flag2) {
					try {
						DataOutputStream dos = new DataOutputStream(clientInfoIn.getClientSocket().getOutputStream());
						dos.writeUTF(str);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		public void sendNewToOld1(ClientInfo clientInfoNew, String str) {			
			Iterator iter = clientList.iterator();
			while(iter.hasNext()) {
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				if(clientInfoNew != clientInfoIn) {		
					Socket clientSocketIn = clientInfoIn.getClientSocket();
					try {
						OutputStream ous = clientSocketIn.getOutputStream();
						DataOutputStream dos = new DataOutputStream(ous);
						dos.writeUTF(str);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public void sendOldToNew(ClientInfo clientInfoNew) {
			Iterator iter = clientList.iterator();
			while(iter.hasNext()) {
				//将集合中对象从集合中一个个取出来（包含新对象）
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				if(clientInfoNew!=clientInfoIn) {
					try {
						//向新对象发送，向哪个对象的Socket获取输出流，就是向哪个对象发送消息
						OutputStream ops = clientInfoNew.getClientSocket().getOutputStream();
						DataOutputStream dos = new DataOutputStream(ops);
						String head = "OLD";
						String ip = clientInfoIn.getClientIP();
						String port = clientInfoIn.getClientPort();
						String userName = clientInfoIn.getUserName();
						MessageBean msgBean = new MessageBean();
						msgBean.setHead(head);
						msgBean.setIp(ip);
						msgBean.setPort(port);
						msgBean.setUserName(userName);
						String str = msgBean.buildString(msgBean);
						System.out.println("str=" + str );
						dos.writeUTF(str);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}

			
			