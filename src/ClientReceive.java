import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;
import javax.swing.text.Document;
import java.text.SimpleDateFormat;

/*
 * ����ͻ�����Ϣ�շ���
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
         * �ͻ����շ��ķ���
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
         * �߳̿�ʼ
         */
        @Override
	public void run(){
		while(!socket.isClosed()){
			try{
				String type = (String)input.readObject();
				
				if(type.equalsIgnoreCase("ϵͳ��Ϣ")){
                                        SimpleDateFormat now= new SimpleDateFormat("hh:mm:ss");
                                        String nowtime = now.format(new java.util.Date());
					String sysmsg = "ϵͳ��Ϣ  " + nowtime + " ��" + (String)input.readObject();
                                        manageInfo(sysmsg);
				}
				else if(type.equalsIgnoreCase("����ر�")){
					output.close();
					input.close();
					socket.close();
                                        manageInfo("�������ѹرգ�\n");
					
					break;
				}
				else if(type.equalsIgnoreCase("������Ϣ")){
					String message = (String)input.readObject();
					manageInfo(message);
				}
				else if(type.equalsIgnoreCase("�û��б�")){
					String userlist = (String)input.readObject();
					String usernames[] = userlist.split("\n");
					combobox.removeAllItems();
					
					int i =0;
					combobox.addItem("������");
					while(i < usernames.length){
						combobox.addItem(usernames[i]);
						i ++;
					}
					combobox.setSelectedIndex(0);
					showStatus.setText("�����û� " + usernames.length + " ��");
				}
                                else if(type.equalsIgnoreCase("�����ļ�")){
                                    String user = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    String abPath = (String)input.readObject();
                                    String toSomebody = (String)input.readObject();

                                    System.out.println("�������ļ�");

                                    int j = JOptionPane.showConfirmDialog(
                                        textpane,"��������"+user+"���ļ�\n��"+flname+"����","�ļ�����",
                                        JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
                                    if (j == JOptionPane.YES_OPTION) {
                                        System.out.println("�����ļ�");
                                        output.writeObject("�����ļ�");
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
                                        output.writeObject("�ܾ��ļ�");
                                        output.flush();
                                         output.writeObject(user);
                                        output.flush();
                                        output.writeObject(toSomebody);
                                        output.flush();
                                        output.writeObject(flname);
                                        output.flush();
                                        manageInfo("�ܾ������� "+user+"���ļ���"+flname+"��\n");
                                     }
                                    
				}
                                else if(type.equalsIgnoreCase("׼������")) {
                                    String user = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    TransferClient ct = new TransferClient(textpane,serverIP);
                                    manageInfo("���� "+user+" ���ļ��ѱ����� E:\\"+flname+"\n");
                                }
                                else if(type.equalsIgnoreCase("�ܾ��ļ�")) {
                                    String  toSomebody = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    manageInfo(toSomebody+" �ܾ�������ļ�: "+flname+"\n");
                                }
                                else if(type.equalsIgnoreCase("�����ļ�")) {
                                    String toSomebody = (String)input.readObject();
                                    String flname = (String)input.readObject();
                                    manageInfo(toSomebody+" ����������ļ�: "+flname+"\n");
                                }
                                else if(type.equalsIgnoreCase("�鿴��¼")) {
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
         * ʵ����JTextPane����ʵ�ֲ����ַ����Լ�����ķ���
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
