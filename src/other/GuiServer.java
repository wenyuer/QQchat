package other;

import java.awt.*;
import java.awt.event.*;

public class GuiServer extends Frame {
//写三个组件
	private	TextArea text = new TextArea("", 10,40);
	private	Button startButton = new Button("开始");
	private Button closeButton = new Button("结束");

//写了三个容器
	private	Panel p1 = new Panel();
	private Panel p2 = new Panel();
	private Panel p3 = new Panel();

	public GuiServer()  {	
		super("陈雯服务器");
		
		p1.add(startButton);
		p1.add(closeButton);		
		p2.add(text);		
		this.add(p2,BorderLayout.NORTH);
		this.add(p1,BorderLayout.CENTER);
		this.pack();
		this.setVisible(true);

	}
	
	public void startButListenter(final Server a) {
		startButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent e) {
				text.setText("服务器开始工作了。。。。。。。。");
				a.startServer();
			}
		});
	}
	
	public void endButtonListenter(final Server a) {
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				a.endServer();
			}
		});
	}
}
