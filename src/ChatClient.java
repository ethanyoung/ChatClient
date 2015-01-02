import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

/*
 * 聊天客户端的主框架类
 */
public class ChatClient extends JFrame implements ActionListener,AdjustmentListener {


	String ip = "127.0.0.1";//连接到服务端的ip地址
	int port = 8888;//连接到服务端的端口号
	String userName = "YZY";//用户名
	int type = 0;//0表示未连接，1表示已连接

	Image icon;//程序图标
	JComboBox combobox;//选择发送消息的接受者
        JTextPane messageShowp;
	JScrollPane messageScrollPane;//信息显示的滚动条

	JLabel express,sendToLabel,messageLabel ;

	JTextField clientMessage;//客户端消息的发送-
        JDialog action;//表情面板
        JDialog chooser;//文件选择
	JButton clientMessageButton;//发送消息
	JButton messageLogBtn;//聊天记录
        JButton sendFile;//发送文件
        JFileChooser fileChooser;//发送文件选择
        JButton motion;//表情按钮
        JButton wx,gx,lh,yw,zm,kq;//六个表情
	JTextField showStatus;//显示用户连接状态
	
	Socket socket;
	ObjectOutputStream output;//网络套接字输出流
	ObjectInputStream input;//网络套接字输入流
	
	ClientReceive recvThread;

	//建立菜单栏
	JMenuBar jMenuBar = new JMenuBar(); 
	//建立菜单组
	JMenu operateMenu = new JMenu ("操作(O)"); 
	//建立菜单项
	JMenuItem loginItem = new JMenuItem ("用户登录(I)");
	JMenuItem logoffItem = new JMenuItem ("用户注销(L)");
	JMenuItem exitItem=new JMenuItem ("退出(X)");

	JMenu conMenu=new JMenu ("设置(C)");
	JMenuItem userItem=new JMenuItem ("用户设置(U)");
	JMenuItem connectItem=new JMenuItem ("连接设置(C)");
	
	
	JMenu helpMenu=new JMenu ("帮助(H)");
	JMenuItem helpItem=new JMenuItem ("关于(A)");

	//建立工具栏
	JToolBar toolBar = new JToolBar();
	//建立工具栏中的按钮组件
	JButton loginButton;//用户登录
	JButton logoffButton;//用户注销
	JButton userButton;//用户信息的设置
	JButton connectButton;//连接设置
	JButton exitButton;//退出按钮

	//框架的大小
	Dimension faceSize = new Dimension(400, 600);

	JPanel downPanel ;
	GridBagLayout girdBag;
	GridBagConstraints girdBagCon;
	
	public ChatClient(){
		init();//初始化程序

		//添加框架的关闭事件处理
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		//设置框架的大小
		this.setSize(faceSize);

		//设置运行时窗口的位置
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - faceSize.getWidth()) * 1/4,
						 (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);
		this.setTitle("客户端    当前用户："+userName); //设置标题

		//程序图标
		icon = getImage("icon_color_red32.png");
		this.setIconImage(icon); //设置程序图标
		show();

		
		//为操作菜单栏设置热键'V'
		operateMenu.setMnemonic('O');

		//为用户登录设置快捷键为ctrl+i
		loginItem.setMnemonic ('I'); 
		loginItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_I,InputEvent.CTRL_MASK));

		//为用户注销快捷键为ctrl+l
		logoffItem.setMnemonic ('L'); 
		logoffItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_L,InputEvent.CTRL_MASK));

		//为退出快捷键为ctrl+x
		exitItem.setMnemonic ('X'); 
		exitItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_X,InputEvent.CTRL_MASK));

		//为设置菜单栏设置热键'C'
		conMenu.setMnemonic('C');

		//为用户设置设置快捷键为ctrl+u
		userItem.setMnemonic ('U'); 
		userItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_U,InputEvent.CTRL_MASK));

		//为连接设置设置快捷键为ctrl+c
		connectItem.setMnemonic ('C'); 
		connectItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_C,InputEvent.CTRL_MASK));

		//为帮助菜单栏设置热键'H'
		helpMenu.setMnemonic('H');

		//为帮助设置快捷键为ctrl+p
		helpItem.setMnemonic ('A');
		helpItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_A,InputEvent.CTRL_MASK));
	}

	/**
	 * 程序初始化函数
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		//添加菜单栏
		operateMenu.add (loginItem);
		operateMenu.add (logoffItem);
		operateMenu.add (exitItem);
		jMenuBar.add (operateMenu); 
		conMenu.add (userItem);
		conMenu.add (connectItem);
		jMenuBar.add (conMenu);
		helpMenu.add (helpItem);
		jMenuBar.add (helpMenu); 
		setJMenuBar (jMenuBar);

		//初始化按钮
		loginButton = new JButton("登录");
		logoffButton = new JButton("注销");
		userButton  = new JButton("用户设置" );
		connectButton  = new JButton("连接设置" );
		messageLogBtn = new JButton("聊天记录");
                sendFile = new JButton("发送文件");
                motion = new JButton("表情");
		exitButton = new JButton("退出" );
		//当鼠标放上显示信息
		loginButton.setToolTipText("连接到指定的服务器");
		logoffButton.setToolTipText("与服务器断开连接");
		userButton.setToolTipText("设置用户信息");
		connectButton.setToolTipText("设置所要连接到的服务器信息");
		//将按钮添加到工具栏
		toolBar.add(userButton);
		toolBar.add(connectButton);
		toolBar.addSeparator();//添加分隔栏
		
		toolBar.add(loginButton);
		toolBar.add(logoffButton);
		toolBar.addSeparator();//添加分隔栏
		
		toolBar.add(messageLogBtn);
		toolBar.addSeparator();//添加分隔栏
		
		toolBar.add(exitButton);
		//工具栏放到窗体上部
		contentPane.add(toolBar,BorderLayout.NORTH);

                
                action = new JDialog();
                action.setTitle("选择表情");
                action.setSize(150, 120);
                action.setResizable(false);
                action.setLayout(new GridLayout(2,3));
                wx = new JButton();
                wx.setIcon(new ImageIcon(getImage("image\\#wx.gif")));
                gx = new JButton();
                gx.setIcon(new ImageIcon(getImage("image\\#gx.gif")));
                kq = new JButton();
                kq.setIcon(new ImageIcon(getImage("image\\#kq.gif")));
                lh = new JButton();
                lh.setIcon(new ImageIcon(getImage("image\\#lh.gif")));
                zm = new JButton();
                zm.setIcon(new ImageIcon(getImage("image\\#zm.gif")));
                yw = new JButton();
                yw.setIcon(new ImageIcon(getImage("image\\#yw.gif")));
//                kl = new JButton();
//                kl.setIcon(new ImageIcon(getImage("image\\#kl.gif")));
//                fn = new JButton();
//                fn.setIcon(new ImageIcon(getImage("image\\#fn.gif")));
//                zj = new JButton();
//                zj.setIcon(new ImageIcon(getImage("image\\#zj.gif")));
                action.add(gx);
                action.add(wx);
                action.add(kq);
                action.add(zm);
                action.add(yw);
                action.add(lh);
//                action.add(kl);
//                action.add(fn);
//                action.add(zj);

                


		//初始时
		loginButton.setEnabled(true);
		logoffButton.setEnabled(false);
                messageLogBtn.setEnabled(false);
                motion.setEnabled(false);
                sendFile.setEnabled(false);

		//为菜单栏添加事件监听
		loginItem.addActionListener(this);
		logoffItem.addActionListener(this);
		exitItem.addActionListener(this);
		userItem.addActionListener(this);
		connectItem.addActionListener(this);
		helpItem.addActionListener(this);
		
		//添加按钮的事件侦听
		loginButton.addActionListener(this);
		logoffButton.addActionListener(this);
		userButton.addActionListener(this);
		connectButton.addActionListener(this);
		messageLogBtn.addActionListener(this);
		exitButton.addActionListener(this);
                sendFile.addActionListener(this);
                motion.addActionListener(this);

                //表情选择的事件侦听
                wx.addActionListener(this);
                gx.addActionListener(this);
                kq.addActionListener(this);
                zm.addActionListener(this);
                yw.addActionListener(this);
                lh.addActionListener(this);
//                kl.addActionListener(this);
//                fn.addActionListener(this);
//                zj.addActionListener(this);

                
		
		combobox = new JComboBox();
		combobox.insertItemAt("所有人",0);
		combobox.setSelectedIndex(0);
                combobox.setEnabled(false);
		
                
                messageShowp = new JTextPane()
                {
                    {setOpaque(false);}
                    @Override
                    public void paintComponent(Graphics g){
                        g.drawImage(getImage("image\\bgp.jpg"),0,0,this);
                        super.paintComponent(g);
                    }
                }
                ;
                messageShowp.setEditable(false);
		//添加滚动条
		messageScrollPane = new JScrollPane(messageShowp,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setBackground(Color.BLUE);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
                messageScrollPane.getVerticalScrollBar().addAdjustmentListener(this);
                messageScrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
		messageScrollPane.revalidate();
		
		clientMessage = new JTextField(23);
		clientMessage.setEnabled(false);
		clientMessageButton = new JButton();
                clientMessageButton.setEnabled(false);
		clientMessageButton.setText("发送");
		//messageLogBtn = new JButton();
		//messageLogBtn.setText("聊天记录");

		//添加系统消息的事件侦听
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);
		//messageLogBtn.addActionListener(this);

		sendToLabel = new JLabel("发送至:");
		express = new JLabel("         表情:   ");
		messageLabel = new JLabel("发送消息:");
		downPanel = new JPanel();
                JPanel attpanel = new JPanel();
                attpanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		girdBag = new GridBagLayout();
		downPanel.setLayout(girdBag);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 0;
		girdBagCon.gridwidth = 6;
		girdBagCon.gridheight = 2;
		girdBagCon.ipadx = 5;
		girdBagCon.ipady = 5;
		JLabel none = new JLabel("    ");
		girdBag.setConstraints(none,girdBagCon);
		downPanel.add(none);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 2;
		girdBagCon.insets = new Insets(1,0,0,0);
		girdBag.setConstraints(sendToLabel,girdBagCon);
		downPanel.add(sendToLabel);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx =1;
		girdBagCon.gridy = 2;
		girdBagCon.anchor = GridBagConstraints.LINE_START;
		girdBag.setConstraints(combobox,girdBagCon);
		downPanel.add(combobox);
                attpanel.add(motion);
                attpanel.add(sendFile);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 3;
		girdBagCon.gridy = 2;
		girdBagCon.anchor = GridBagConstraints.LINE_END;
		girdBag.setConstraints(attpanel,girdBagCon);
		downPanel.add(attpanel);
		
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(messageLabel,girdBagCon);
		downPanel.add(messageLabel);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 1;
		girdBagCon.gridy = 3;
		girdBagCon.gridwidth = 3;
		girdBagCon.gridheight = 1;
		girdBag.setConstraints(clientMessage,girdBagCon);
		downPanel.add(clientMessage);

		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 4;
		girdBagCon.gridy = 3;
		girdBag.setConstraints(clientMessageButton,girdBagCon);
		downPanel.add(clientMessageButton);

		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		girdBagCon = new GridBagConstraints();
		girdBagCon.gridx = 0;
		girdBagCon.gridy = 5;
		girdBagCon.gridwidth = 5;
		girdBag.setConstraints(showStatus,girdBagCon);
		downPanel.add(showStatus);

		//contentPane.add(messageScrollPane,BorderLayout.CENTER);
		/*JLabel bgp = new JLabel();
		bgp.setIcon(new ImageIcon(getImage("image\\bgp.jpg")));
		contentPane.add(bgp,BorderLayout.CENTER);*/
        	contentPane.add(messageScrollPane,BorderLayout.CENTER);
		contentPane.add(downPanel,BorderLayout.SOUTH);
		
		//关闭程序时的操作
		this.addWindowListener(
			new WindowAdapter(){
                                @Override
				public void windowClosing(WindowEvent e){
					if(type == 1){
						DisConnect();
					}
					System.exit(0);
				}
			}
		);
	}

        public void adjustmentValueChanged(AdjustmentEvent evt) {
//            Graphics g = messageShowp.getGraphics();
//            g.clearRect(0, messageScrollPane.getVerticalScrollBar().getValue(),
//                    messageScrollPane.getWidth(),
//                    messageScrollPane.getHeight());
//            g.drawImage(getImage("image\\bgp.jpg"),0,
//                    messageScrollPane.getVerticalScrollBar().getValue(),this);
//            System.out.println(messageScrollPane.getVerticalScrollBar().getValue());
        }
	/**
	 * 事件处理
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if (obj == userItem || obj == userButton) { //用户信息设置
			//调出用户信息设置对话框
			UserConf userConf = new UserConf(this,userName);
			userConf.show();
			userName = userConf.userInputName;
                        this.setTitle("客户端    当前用户："+userName); //设置标题
		}
		else if (obj == connectItem || obj == connectButton) { //连接服务端设置
			//调出连接设置对话框
			ConnectConf conConf = new ConnectConf(this,ip,port);
			conConf.show();
			ip = conConf.userInputIp;
			port = conConf.userInputPort;
		}
		else if (obj == loginItem || obj == loginButton) { //登录
			Connect();
		}
		else if (obj == logoffItem || obj == logoffButton) { //注销
			DisConnect();
			showStatus.setText("");
		}
		else if (obj == clientMessage || obj == clientMessageButton) { //发送消息
			SendMessage();
			clientMessage.setText("");
		}
                else if (obj == sendFile) { //发送文件
                        if (combobox.getSelectedItem().equals("所有人")) {
                            JOptionPane.showMessageDialog(this, "请选择确定的发送对象！");
                        }
                        else {
                        SendFile();
                        }
                }
		else if (obj == exitButton || obj == exitItem) { //退出
			int j=JOptionPane.showConfirmDialog(
				this,"真的要退出吗?","退出",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				if(type == 1){
                                    DisConnect();
				}
				System.exit(0);
			}
		}
		else if (obj == helpItem) { //菜单栏中的帮助
			//调出帮助对话框
			Help helpDialog = new Help(this);
			helpDialog.show();
		}
		else if (obj == messageLogBtn) {//聊天记录
                        MessageLog();
		}
                 else if (obj == motion) {//选择表情
                    action.setLocationRelativeTo(motion);
                    action.setVisible(true);
                 }
                 else if (obj == wx || obj ==gx || obj ==lh || 
                            obj ==kq || obj ==yw || obj ==zm
//                            || obj ==kl || obj ==fn || obj ==zj
                            )
                 {//发送表情
                    InsertAction(obj);
                 }
	}

	
	public void Connect(){
		try{
			socket = new Socket(ip,port);
		}
		catch (Exception e){
			JOptionPane.showConfirmDialog(
				this,"不能连接到指定的服务器。\n请确认连接设置是否正确。","提示",
				JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}

		try{
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input  = new ObjectInputStream(socket.getInputStream() );
			output.writeObject(userName);
			output.flush();

                        //创建一个收发线程
			recvThread = new ClientReceive(socket,output,input,
					combobox,showStatus,messageShowp,ip);
			recvThread.start();
			
			loginButton.setEnabled(false);
			loginItem.setEnabled(false);
			userButton.setEnabled(false);
			userItem.setEnabled(false);
			connectButton.setEnabled(false);
			connectItem.setEnabled(false);
			logoffButton.setEnabled(true);
			logoffItem.setEnabled(true);
                        messageLogBtn.setEnabled(true);
                        motion.setEnabled(true);
                        sendFile.setEnabled(true);
                        clientMessageButton.setEnabled(true);
                        combobox.setEnabled(true);
                        //发送信息窗口可用
			clientMessage.setEnabled(true);

                        messageShowp.setText("连接服务器 "+ip+":"+port+" 成功...\n");
			type = 1;//标志位设为已连接
		}
		catch (Exception e){
			System.out.println(e);
			return;
		}
	}
	
	public void DisConnect(){
		loginButton.setEnabled(true);
		loginItem.setEnabled(true);
		userButton.setEnabled(true);
		userItem.setEnabled(true);
		connectButton.setEnabled(true);
		connectItem.setEnabled(true);
		logoffButton.setEnabled(false);
		logoffItem.setEnabled(false);
		clientMessage.setEnabled(false);
		
		if(socket.isClosed()){
			return ;
		}
		
		try{
			output.writeObject("用户下线");
			output.flush();
			input.close();
			output.close();
			socket.close();
			messageShowp.setText("已经与服务器断开连接...\n");
			type = 0;//标志位设为未连接
		}
		catch (Exception e){
			//
		}
	}
	
	public void SendMessage(){
                //获得接收者
		String toSomebody = combobox.getSelectedItem().toString();

                //获得表情
		//String action = actionlist.getSelectedItem().toString();
                //获得聊天内容
		String message = clientMessage.getText();
		
		if(socket.isClosed()){
			return ;
		}
		
		try{
			output.writeObject("聊天信息");
			output.flush();
			output.writeObject(toSomebody);
			output.flush();
//			output.writeObject(action);
//			output.flush();
			output.writeObject(message);
			output.flush();
			//messageShowp.getGraphics().clearRect(10,10, messageShowp.getWidth(), messageShowp.getHeight());
			//messageShowp.getGraphics().drawImage(getImage("image\\bgp.jpg"),0,90,this);
		}
		catch (Exception e){
			//
		}
	}

        public void SendFile () {
            fileChooser = new JFileChooser();
            int showOpenDialog = fileChooser.showOpenDialog(this);
            if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            String name = fileChooser.getSelectedFile().getName();
            String toSomebody = combobox.getSelectedItem().toString();
            if(socket.isClosed()){
                return;
            }
            if(combobox.getSelectedItem().equals("所有人")) {
                return;
            }
            try {
                output.writeObject("发送文件");
                output.flush();
                output.writeObject(toSomebody);
                output.flush();
                output.writeObject(path);
                output.flush();
                output.writeObject(name);
                output.flush();
            } catch (Exception e) {
                System.out.println("文件选取后通讯错误");
                e.printStackTrace();
            }
            }
        }
        
        /**
         * 获得表情的代码输入到输入框
         * @param index
         */

        public void InsertAction (Object obj) {
            this.action.setVisible(false);
            if (obj == wx) {
                clientMessage.setText(clientMessage.getText() + "#wx");
            }
            else if (obj == gx) {
                clientMessage.setText(clientMessage.getText() + "#gx");
            }
            else if (obj == lh) {
                clientMessage.setText(clientMessage.getText() + "#lh");
            }
            else if (obj == kq) {
                clientMessage.setText(clientMessage.getText() + "#kq");
            }
            else if (obj == zm) {
                clientMessage.setText(clientMessage.getText() + "#zm");
            }
            else if (obj == yw) {
                clientMessage.setText(clientMessage.getText() + "#yw");
            }
//            else if (obj == kl) {
//                clientMessage.setText(clientMessage.getText() + "#kl");
//            }
//            else if (obj == fn) {
//                clientMessage.setText(clientMessage.getText() + "#fn");
//            }
//            else if (obj == zj) {
//                clientMessage.setText(clientMessage.getText() + "#zj");
//            }
        }

        /**
         * 获取聊天记录
         */
        public void MessageLog () {
            try {
                output.writeObject("查看记录");
                output.flush();
                output.writeObject(userName);
                output.flush();
            } catch (Exception e) {
                System.out.println("获取聊天记录错误！");
            }

        }
	/**
	 * 通过给定的文件名获得图像的方法
	 */
	public Image getImage(String filename) {
		URLClassLoader urlLoader = (URLClassLoader)this.getClass().
			getClassLoader();
		URL url = null;
		Image image = null;
		url = urlLoader.findResource(filename);
		image = Toolkit.getDefaultToolkit().getImage(url);
		MediaTracker mediatracker = new MediaTracker(this);
		try {
			mediatracker.addImage(image, 0);
			mediatracker.waitForID(0);
		}
		catch (InterruptedException _ex) {
			image = null;
		}
		if (mediatracker.isErrorID(0)) {
			image = null;
		}

		return image;
	}

	public static void main(String[] args) {
//        try{
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//        } catch (Exception e) {
//        }
            ChatClient app = new ChatClient();
	}
}
