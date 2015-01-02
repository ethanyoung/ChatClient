import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;
import javax.swing.text.Document;
import java.text.SimpleDateFormat;

/*
 * 聊天客户端消息收发类
 */
public class ClientReceive extends Thread {
	private JComboBox combobox;
        private JTextPane textpane;
	
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream  input;
	JTextField showStatus;
	String serverIP;

        /*
         * 客户端收发的方法
         */
	public ClientReceive(Socket socket,ObjectOutputStream output,
		ObjectInputStream  input,JComboBox combobox,JTextField showStatus,
		JTextPane textpane,String serverIP){

		this.serverIP = serverIP;
		this.socket = socket;
		this.output = output;
		this.input = input;
		this.combobox = combobox;
		this.showStatus = showStatus;
                this.textpane = textpane;
	}

        /*
         * 线程开始
         */
        @Override
	public void run(){
		while(!socket.isClosed()){
			try{
				String type = (String)input.readObject();
				
				if(type.equalsIgnoreCase("系统信息")){
                                        SimpleDateFormat now= new SimpleDateFormat("hh:mm:ss");
                                        String nowtime = now.format(new java.util.Date());
					String sysmsg = "系统信息  " + nowtime + " ：" + (String)input.readObject();
                                        manageInfo(sysmsg);
				}
				else if(type.equalsIgnoreCase("服务关闭")){
					output.close();
					input.close();
					socket.close();
                                        manageInfo("服务器已关闭！\n");
					
					break;
				}
				else if(type.equalsIgnoreCase("聊天信息")){
					String message = (String)input.readObject();
					manageInfo(message);
				}
				else if(type.equalsIgnoreCase("用户列表")){
					String userlist = (String)input.readObject();
					String usernames[] = userlist.split("\n");
					combobox.removeAllItems();
					
					int i =0;
					combobox.addItem("所有人");
					while(i < usernames.length){
						combobox.addItem(usernames[i]);
						i ++;
					}
					combobox.setSelectedIndex(0);
					showStatus.setText("在线用户 " + usernames.length + " 人");
				}
                                else if(type.equalsIgnoreCase("发送文件")){
                                    String user = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    String abPath = (String)input.readObject();
                                    String toSomebody = (String)input.readObject();

                                    System.out.println("请求发送文件");

                                    int j = JOptionPane.showConfirmDialog(
                                        textpane,"接受来自"+user+"的文件\n“"+flname+"”吗？","文件传输",
                                        JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
                                    if (j == JOptionPane.YES_OPTION) {
                                        System.out.println("接收文件");
                                        output.writeObject("接收文件");
                                        output.flush();
                                        output.writeObject(user);
                                        output.flush();
                                        output.writeObject(flname);
                                        output.flush();
                                        output.writeObject(abPath);
                                        output.flush();
                                        output.writeObject(toSomebody);
                                        output.flush();
                                        
                                    }
                                     else {
                                        output.writeObject("拒绝文件");
                                        output.flush();
                                         output.writeObject(user);
                                        output.flush();
                                        output.writeObject(toSomebody);
                                        output.flush();
                                        output.writeObject(flname);
                                        output.flush();
                                        manageInfo("拒绝了来自 "+user+"的文件“"+flname+"”\n");
                                     }
                                    
				}
                                else if(type.equalsIgnoreCase("准备接收")) {
                                    String user = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    TransferClient ct = new TransferClient(textpane,serverIP);
                                    manageInfo("来自 "+user+" 的文件已保存在 E:\\"+flname+"\n");
                                }
                                else if(type.equalsIgnoreCase("拒绝文件")) {
                                    String  toSomebody = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    manageInfo(toSomebody+" 拒绝了你的文件: "+flname+"\n");
                                }
                                else if(type.equalsIgnoreCase("接收文件")) {
                                    String toSomebody = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    manageInfo(toSomebody+" 接收了你的文件: "+flname+"\n");
                                }
                                else if(type.equalsIgnoreCase("查看记录")) {
                                    String[][] cn = (String[][])input.readObject();
                                    MessageLog logFrame = new MessageLog(cn);
                                }
			}
			catch (Exception e ){
				System.out.println(e);
			}
		}
	}

        /*
         * 实现在JTextPane里面实现插入字符串以及表情的方法
         */
        public void manageInfo (String info) {
            int length = info.length();
            char[] every = new char[length];
            int count = 0;
            Document doc = textpane.getStyledDocument();
            SimpleAttributeSet attr = new SimpleAttributeSet();
            boolean hadsharp = false;
            for (int i=0;i<length;i++) {
                every[i] = info.charAt(i);
                if (info.charAt(i) == '#')
                    hadsharp = true;
            }
             
                for (int i=0;i<length;i++) {
                    if (hadsharp != true)
                        break;
                    if (every[i] == '#') {
                        String str = null;
                        str = info.substring(count,i);
                        try {
                            if (str != null){
                                doc.insertString(doc.getLength(), str, attr);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String icName;
                        icName = info.substring(i, i+3);
                        Icon ic = new ImageIcon(getClass().getResource("image\\"+ icName + ".gif"));
                        textpane.setCaretPosition(doc.getLength());
                        textpane.insertIcon(ic);
                        count = i+3;
                    }
                }
            
            if(count>=0 && count<length)
              {
                   String theLast=null;
                   theLast=info.substring(count, length);
                   try{
                        doc.insertString(doc.getLength(), theLast, attr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
              }
        }
}
