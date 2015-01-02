import java.awt.*;
import javax.swing.*;

/*
 * ���������¼�ĶԻ���
 * ���û��鿴���ڵ������¼
 */

public final class MessageLog extends JFrame {
    String[] colName = {"����ʱ��","��������","FROM","TO"};
    JTable messageLog;
    JScrollPane sp;
    JPanel panelMessageLog = new JPanel();
    Dimension faceSize = new Dimension(400, 600);
    String username;
    String[][] value;
    public MessageLog(String[][] cn){

        this.value = cn;
        this.setTitle("�����¼");
        this.setSize(500,500);

        messageLog = new JTable(cn,colName);
        messageLog.getColumn("����ʱ��").setPreferredWidth(100);
        messageLog.getColumn("��������").setPreferredWidth(140);
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
