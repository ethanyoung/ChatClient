import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


/**
 * 生成设置对话框的类
 */
public class Help extends JDialog {

	JPanel titlePanel = new JPanel();
	JPanel contentPanel = new JPanel();
	JPanel closePanel = new JPanel();

	JButton close = new JButton();
	JLabel title = new JLabel("聊天室客户端");
	JTextArea help = new JTextArea();

	Color bg = new Color(255,255,255);

	public Help(JFrame frame) {
		super(frame, true);
		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//设置运行位置，使对话框居中
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - 400) / 2 + 25,
						(int) (screenSize.height - 320) / 2);
		this.setResizable(false);
	}

	private void jbInit() throws Exception {
		this.setSize(new Dimension(300, 200));
		this.setTitle("关于");
		
		titlePanel.setBackground(bg);
		contentPanel.setBackground(bg);
		closePanel.setBackground(bg);
                
		help.setText("聊天程序的客户端,可以设置用户名，\n\n" +
                        "以及服务端的IP地址和端口号。\n\n" +
                        "可以发送表情，传输文件，查看聊天记录。");
		help.setEditable(false);

		titlePanel.add(new Label("              "));
		titlePanel.add(title);
		titlePanel.add(new Label("              "));

		contentPanel.add(help);

		closePanel.add(new Label("              "));
		closePanel.add(close);
		closePanel.add(new Label("              "));

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(titlePanel, BorderLayout.NORTH);
		contentPane.add(contentPanel, BorderLayout.CENTER);
		contentPane.add(closePanel, BorderLayout.SOUTH);

		close.setText("关闭");
		//事件处理
		close.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);
	}
}