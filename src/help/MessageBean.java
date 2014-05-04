package help;
public class MessageBean {
	private String head;
	private String ip;
	private String port;
	private String userName;
	private String sendMsg;

	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSendMsg() {
		return sendMsg;
	}
	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}
	//写一个方法组合字符串
	public String buildString(MessageBean msgBean){
		String str=null;
		str=msgBean.getHead()+"/"+msgBean.getIp()+"/"+msgBean.getPort()+"/"+msgBean.getUserName()+"/"+msgBean.getSendMsg();
		return str;
	}
	//写一个方法打开字符串，返回一个字符串的Bean对象
	public MessageBean openString(String str){
		MessageBean msgBean=new MessageBean();
		int index1=str.indexOf("/",0);
		int index2=str.indexOf("/",index1+1);
		int index3=str.indexOf("/",index2+1);
		int index4=str.indexOf("/",index3+1);
		
		String head=str.substring(0,index1);		
		String ip=str.substring(index1+1,index2);		
		String port=str.substring(index2+1,index3);		
		String userName=str.substring(index3+1,index4);
		String sendMsg=str.substring(index4+1);

		msgBean.setHead(head);
		msgBean.setIp(ip);
		msgBean.setPort(port);
		msgBean.setSendMsg(sendMsg);
		msgBean.setUserName(userName);		
		return msgBean;
		
		
	}
}
