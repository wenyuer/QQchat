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
		System.out.println("���ǰ����̵߳ķ�����");	
		GuiServer guiServer = new GuiServer();
		Server a = new Server();
		guiServer.startButListenter(a);   //������ʼ��ť�����¼�
		guiServer.endButtonListenter(a);  //����������ť�����¼�
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
							System.out.println("���Կͻ��˵���Ϣ��" + str);
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
							
							//���Ϊ�¶���ʱ�������������ڼ�����
							clientList.add(clientInfo);
							System.out.println("�����еĸ���==" + clientList.size());
							System.out.println("����" + userName +"--�¿ͻ��ڵ�¼��");	
							sendNewToOld1(clientInfo,str);
							//��Ϊ�¶��󣬻��ý��϶��󷢸�����
							sendOldToNew(clientInfo);							
						} else if ("ALL".equals(head)) {
							System.out.println("����server����ALL��֧�е�str == " + str);
							//���ַ�����ɢ���һ��Bean����
							MessageBean msgBean = new MessageBean();
							msgBean = msgBean.openString(str);
							sendMsgToAll1(msgBean,str);
						} else if ("MSG".equals(head)) {
							System.out.println("����server���е����Ե��������str == " + str);
							MessageBean msgBean = new MessageBean();
							msgBean = msgBean.openString(str);
							sendMsgToOne(msgBean,str);
						} else if ("QUT".equals(head)) {
							//���յ�QUT����Ϣ����ʾ�пͻ���Ҫ������
							MessageBean msgBean = new MessageBean();
							msgBean = msgBean.openString(str);
							sendQuit(msgBean,str);
						}
					} else {
						System.out.println("��û�а�����Ϣ��ʽ��������������Ϣ" + str);
					}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
			}
		
		}
		
	}
	
	class StartServerThread implements Runnable{
		//���ǰ����̵߳ķ�����
		//ServerSocket serverSocket;
		int count= 0;
		
		public void run(){
			
			while(true){
				try {
						clientSocket = serverSocket.accept();	
						new Thread(new ClientProcessThread(clientSocket)).start();											
						count ++;
					//System.out.println("���ǵ�" + count + "�������û�");
					//new Thread(new ClientProcessThread(clientSocket)).start();	
				}catch(IOException e){
				e.printStackTrace();
				}
			}
		}
	}
		//��������
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
				} //��������ͬ���򼯺��ж�����������ߣ��ͽ����Ӽ������Ƴ�
				else if(flag1 && flag2) {
					iter.remove();
				}
			}
			
		}
		//���ǵ���
		private void sendMsgToOne(MessageBean msgBean, String str) {
			// ��ȡIP��Port
			String clientIP = msgBean.getIp();
			String clientPort = msgBean.getPort();
			
			Iterator iter = clientList.iterator();
			while (iter.hasNext()) {
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				//�������ж���ȡ�������������е�IP��Port��ȡ����������ߵ�IP��Port�Ƚ�
				String IPInside = clientInfoIn.getClientIP();
				String PortInside = clientInfoIn.getClientPort();
				
				boolean flag1 = clientIP.equals(IPInside);
				boolean flag2 = clientPort.equals(PortInside);
				
				//��������ĳ�������IP��Port�뷢���������ͬʱ�����������ǽ����߶������������������Ϣ
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
		//Ⱥ�ģ�����msgBean�ǿͻ��˵�����Ͷ˷�������Ϣת��Ϊ��Bean,��ʱ��������Ϣ�Ƿ����ߵ�����
		private void sendMsgToAll1(MessageBean msgBean, String str) {
			// ��ȡ�����ߵ�IP��Port
			System.out.println("sendMsgToAll1.....");
			String senderIP = msgBean.getIp();
			String senderPort = msgBean.getPort();
			Iterator iter = clientList.iterator();
			while (iter.hasNext()) {
				//�������ж���Ӽ�����һ����ȡ�����������¶���
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				//��α�֤�������Լ����������еĶ���ȡ�������������е�IP��Portȡ�����Ƚ�
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
				//�������ж���Ӽ�����һ����ȡ�����������¶���
				ClientInfo clientInfoIn = (ClientInfo) iter.next();
				if(clientInfoNew!=clientInfoIn) {
					try {
						//���¶����ͣ����ĸ������Socket��ȡ��������������ĸ���������Ϣ
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

			
			