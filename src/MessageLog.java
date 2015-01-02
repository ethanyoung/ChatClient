import java.awt.*;
import javax.swing.*;

/*
 * 生成聊天记录的对话框
 * 让用户查看近期的聊天记录
 */

public final class MessageLog extends JFrame {
    String[] colName = {"日期时间","聊天内容","FROM","TO"};
    JTable messageLog;
    JScrollPane sp;
    JPanel panelMessageLog = new JPanel();
    Dimension faceSize = new Dimension(400, 600);
    String username;
    String[][] value;
    public MessageLog(String[][] cn){

        this.value = cn;
        this.setTitle("聊天记录");
        this.setSize(500,500);

        messageLog = new JTable(cn,colName);
        messageLog.getColumn("日期时间").setPreferredWidth(100);
        messageLog.getColumn("聊天内容").setPreferredWidth(140);
        messageLog.getColumn("FROM").setPreferredWidth(10);
        messageLog.getColumn("TO").setPreferredWidth(10);
        messageLog.setEnabled(false);
        sp = new JScrollPane(messageLog);

        panelMessageLog.add(sp);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation( (int) (screenSize.width - faceSize.getWidth()) * 3/4,
                             (int) (screenSize.height - faceSize.getHeight()) / 2);
        this.getContentPane().add(panelMessageLog,BorderLayout.CENTER);
        this.setVisible(true);
    }
}
