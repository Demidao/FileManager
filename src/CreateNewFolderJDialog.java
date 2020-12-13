import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/*
класс диалогового окна для создания нового диалога
 */

public class CreateNewFolderJDialog extends JDialog {
    private JTextField nameOfNewFolder = new JTextField(10);
    private JButton okButton = new JButton("Create");
    private JButton cancelButton = new JButton("Cancel");
    private String newFolderName;
    private JLabel nameFolderWait = new JLabel("New directory name: ");
    private boolean ready = false; //флаг готовности. Если 1, то папка создана, если 0 = еще не создана.

    public CreateNewFolderJDialog(JFrame jframe) {

        super(jframe, "Create new directory", true);
        setLayout(new GridLayout(2, 2, 5, 5));
        setSize(400, 200);

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newFolderName = nameOfNewFolder.getText();
                setVisible(false);
                ready = true;
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                ready = false;
            }
        });

        getContentPane().add(nameFolderWait);
        getContentPane().add(nameOfNewFolder);
        getContentPane().add(okButton);
        getContentPane().add(cancelButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getNewName() {
        return newFolderName;
    }

    public boolean getReady() {
        return ready;
    }

    public void Waiting() {
        while (!ready) {
        }
    }

}

