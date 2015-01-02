import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

/*
 * ����ͻ��˵��������
 */
public class ChatClient extends JFrame implements ActionListener,AdjustmentListener {


	String ip = "127.0.0.1";//���ӵ�����˵�ip��ַ
	int port = 8888;//���ӵ�����˵Ķ˿ں�
	String userName = "YZY";//�û���
	int type = 0;//0��ʾδ���ӣ�1��ʾ������

	Image icon;//����ͼ��
	JComboBox combobox;//ѡ������Ϣ�Ľ�����
        JTextPane messageShowp;
	JScrollPane messageScrollPane;//��Ϣ��ʾ�Ĺ�����

	JLabel express,sendToLabel,messageLabel ;

	JTextField clientMessage;//�ͻ�����Ϣ�ķ���-
        JDialog action;//�������
        JDialog chooser;//�ļ�ѡ��
	JButton clientMessageButton;//������Ϣ
	JButton messageLogBtn;//�����¼
        JButton sendFile;//�����ļ�
        JFileChooser fileChooser;//�����ļ�ѡ��
        JButton motion;//���鰴ť
        JButton wx,gx,lh,yw,zm,kq;//��������
	JTextField showStatus;//��ʾ�û�����״̬
	
	Socket socket;
	ObjectOutputStream output;//�����׽��������
	ObjectInputStream input;//�����׽���������
	
	ClientReceive recvThread;

	//�����˵���
	JMenuBar jMenuBar = new JMenuBar(); 
	//�����˵���
	JMenu operateMenu = new JMenu ("����(O)"); 
	//�����˵���
	JMenuItem loginItem = new JMenuItem ("�û���¼(I)");
	JMenuItem logoffItem = new JMenuItem ("�û�ע��(L)");
	JMenuItem exitItem=new JMenuItem ("�˳�(X)");

	JMenu conMenu=new JMenu ("����(C)");
	JMenuItem userItem=new JMenuItem ("�û�����(U)");
	JMenuItem connectItem=new JMenuItem ("��������(C)");
	
	
	JMenu helpMenu=new JMenu ("����(H)");
	JMenuItem helpItem=new JMenuItem ("����(A)");

	//����������
	JToolBar toolBar = new JToolBar();
	//�����������еİ�ť���
	JButton loginButton;//�û���¼
	JButton logoffButton;//�û�ע��
	JButton userButton;//�û���Ϣ������
	JButton connectButton;//��������
	JButton exitButton;//�˳���ť

	//��ܵĴ�С
	Dimension faceSize = new Dimension(400, 600);

	JPanel downPanel ;
	GridBagLayout girdBag;
	GridBagConstraints girdBagCon;
	
	public ChatClient(){
		init();//��ʼ������

		//��ӿ�ܵĹر��¼�����
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		//���ÿ�ܵĴ�С
		this.setSize(faceSize);

		//��������ʱ���ڵ�λ��
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - faceSize.getWidth()) * 1/4,
						 (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);
		this.setTitle("�ͻ���    ��ǰ�û���"+userName); //���ñ���

		//����ͼ��
		icon = getImage("icon_color_red32.png");
		this.setIconImage(icon); //���ó���ͼ��
		show();

		
		//Ϊ�����˵��������ȼ�'V'
		operateMenu.setMnemonic('O');

		//Ϊ�û���¼���ÿ�ݼ�Ϊctrl+i
		loginItem.setMnemonic ('I'); 
		loginItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_I,InputEvent.CTRL_MASK));

		//Ϊ�û�ע����ݼ�Ϊctrl+l
		logoffItem.setMnemonic ('L'); 
		logoffItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_L,InputEvent.CTRL_MASK));

		//Ϊ�˳���ݼ�Ϊctrl+x
		exitItem.setMnemonic ('X'); 
		exitItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_X,InputEvent.CTRL_MASK));

		//Ϊ���ò˵��������ȼ�'C'
		conMenu.setMnemonic('C');

		//Ϊ�û��������ÿ�ݼ�Ϊctrl+u
		userItem.setMnemonic ('U'); 
		userItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_U,InputEvent.CTRL_MASK));

		//Ϊ�����������ÿ�ݼ�Ϊctrl+c
		connectItem.setMnemonic ('C'); 
		connectItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_C,InputEvent.CTRL_MASK));

		//Ϊ�����˵��������ȼ�'H'
		helpMenu.setMnemonic('H');

		//Ϊ�������ÿ�ݼ�Ϊctrl+p
		helpItem.setMnemonic ('A');
		helpItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_A,InputEvent.CTRL_MASK));
	}

	/**
	 * �����ʼ������
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		//��Ӳ˵���
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

		//��ʼ����ť
		loginButton = new JButton("��¼");
		logoffButton = new JButton("ע��");
		userButton  = new JButton("�û�����" );
		connectButton  = new JButton("��������" );
		messageLogBtn = new JButton("�����¼");
                sendFile = new JButton("�����ļ�");
                motion = new JButton("����");
		exitButton = new JButton("�˳�" );
		//����������ʾ��Ϣ
		loginButton.setToolTipText("���ӵ�ָ���ķ�����");
		logoffButton.setToolTipText("��������Ͽ�����");
		userButton.setToolTipText("�����û���Ϣ");
		connectButton.setToolTipText("������Ҫ���ӵ��ķ�������Ϣ");
		//����ť��ӵ�������
		toolBar.add(userButton);
		toolBar.add(connectButton);
		toolBar.addSeparator();//��ӷָ���
		
		toolBar.add(loginButton);
		toolBar.add(logoffButton);
		toolBar.addSeparator();//��ӷָ���
		
		toolBar.add(messageLogBtn);
		toolBar.addSeparator();//��ӷָ���
		
		toolBar.add(exitButton);
		//�������ŵ������ϲ�
		contentPane.add(toolBar,BorderLayout.NORTH);

                
                action = new JDialog();
                action.setTitle("ѡ�����");
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

                


		//��ʼʱ
		loginButton.setEnabled(true);
		logoffButton.setEnabled(false);
                messageLogBtn.setEnabled(false);
                motion.setEnabled(false);
                sendFile.setEnabled(false);

		//Ϊ�˵�������¼�����
		loginItem.addActionListener(this);
		logoffItem.addActionListener(this);
		exitItem.addActionListener(this);
		userItem.addActionListener(this);
		connectItem.addActionListener(this);
		helpItem.addActionListener(this);
		
		//��Ӱ�ť���¼�����
		loginButton.addActionListener(this);
		logoffButton.addActionListener(this);
		userButton.addActionListener(this);
		connectButton.addActionListener(this);
		messageLogBtn.addActionListener(this);
		exitButton.addActionListener(this);
                sendFile.addActionListener(this);
                motion.addActionListener(this);

                //����ѡ����¼�����
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
		combobox.insertItemAt("������",0);
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
		//��ӹ�����
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
		clientMessageButton.setText("����");
		//messageLogBtn = new JButton();
		//messageLogBtn.setText("�����¼");

		//���ϵͳ��Ϣ���¼�����
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);
		//messageLogBtn.addActionListener(this);

		sendToLabel = new JLabel("������:");
		express = new JLabel("         ����:   ");
		messageLabel = new JLabel("������Ϣ:");
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
		
		//�رճ���ʱ�Ĳ���
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
	 * �¼�����
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if (obj == userItem || obj == userButton) { //�û���Ϣ����
			//�����û���Ϣ���öԻ���
			UserConf userConf = new UserConf(this,userName);
			userConf.show();
			userName = userConf.userInputName;
                        this.setTitle("�ͻ���    ��ǰ�û���"+userName); //���ñ���
		}
		else if (obj == connectItem || obj == connectButton) { //���ӷ��������
			//�����������öԻ���
			ConnectConf conConf = new ConnectConf(this,ip,port);
			conConf.show();
			ip = conConf.userInputIp;
			port = conConf.userInputPort;
		}
		else if (obj == loginItem || obj == loginButton) { //��¼
			Connect();
		}
		else if (obj == logoffItem || obj == logoffButton) { //ע��
			DisConnect();
			showStatus.setText("");
		}
		else if (obj == clientMessage || obj == clientMessageButton) { //������Ϣ
			SendMessage();
			clientMessage.setText("");
		}
                else if (obj == sendFile) { //�����ļ�
                        if (combobox.getSelectedItem().equals("������")) {
                            JOptionPane.showMessageDialog(this, "��ѡ��ȷ���ķ��Ͷ���");
                        }
                        else {
                        SendFile();
                        }
                }
		else if (obj == exitButton || obj == exitItem) { //�˳�
			int j=JOptionPane.showConfirmDialog(
				this,"���Ҫ�˳���?","�˳�",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				if(type == 1){
                                    DisConnect();
				}
				System.exit(0);
			}
		}
		else if (obj == helpItem) { //�˵����еİ���
			//���������Ի���
			Help helpDialog = new Help(this);
			helpDialog.show();
		}
		else if (obj == messageLogBtn) {//�����¼
                        MessageLog();
		}
                 else if (obj == motion) {//ѡ�����
                    action.setLocationRelativeTo(motion);
                    action.setVisible(true);
                 }
                 else if (obj == wx || obj ==gx || obj ==lh || 
                            obj ==kq || obj ==yw || obj ==zm
//                            || obj ==kl || obj ==fn || obj ==zj
                            )
                 {//���ͱ���
                    InsertAction(obj);
                 }
	}

	
	public void Connect(){
		try{
			socket = new Socket(ip,port);
		}
		catch (Exception e){
			JOptionPane.showConfirmDialog(
				this,"�������ӵ�ָ���ķ�������\n��ȷ�����������Ƿ���ȷ��","��ʾ",
				JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}

		try{
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input  = new ObjectInputStream(socket.getInputStream() );
			output.writeObject(userName);
			output.flush();

                        //����һ���շ��߳�
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
                        //������Ϣ���ڿ���
			clientMessage.setEnabled(true);

                        messageShowp.setText("���ӷ����� "+ip+":"+port+" �ɹ�...\n");
			type = 1;//��־λ��Ϊ������
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
			output.writeObject("�û�����");
			output.flush();
			input.close();
			output.close();
			socket.close();
			messageShowp.setText("�Ѿ���������Ͽ�����...\n");
			type = 0;//��־λ��Ϊδ����
		}
		catch (Exception e){
			//
		}
	}
	
	public void SendMessage(){
                //��ý�����
		String toSomebody = combobox.getSelectedItem().toString();

                //��ñ���
		//String action = actionlist.getSelectedItem().toString();
                //�����������
		String message = clientMessage.getText();
		
		if(socket.isClosed()){
			return ;
		}
		
		try{
			output.writeObject("������Ϣ");
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
            if(combobox.getSelectedItem().equals("������")) {
                return;
            }
            try {
                output.writeObject("�����ļ�");
                output.flush();
                output.writeObject(toSomebody);
                output.flush();
                output.writeObject(path);
                output.flush();
                output.writeObject(name);
                output.flush();
            } catch (Exception e) {
                System.out.println("�ļ�ѡȡ��ͨѶ����");
                e.printStackTrace();
            }
            }
        }
        
        /**
         * ��ñ���Ĵ������뵽�����
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
         * ��ȡ�����¼
         */
        public void MessageLog () {
            try {
                output.writeObject("�鿴��¼");
                output.flush();
                output.writeObject(userName);
                output.flush();
            } catch (Exception e) {
                System.out.println("��ȡ�����¼����");
            }

        }
	/**
	 * ͨ���������ļ������ͼ��ķ���
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
