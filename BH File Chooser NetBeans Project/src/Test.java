
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.bh.tools.comps.BHFileChooser;

/**
 * Test, made for BH File Chooser NetBeans Project, is copyright Blue Husky
 * Programming ©2014 CC 3.0 BY-SA<HR/>
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-09-15
 */
public class Test {

    static {
        String LAFClassName = UIManager.getSystemLookAndFeelClassName();
        if (!UIManager.getLookAndFeel().getClass().getName().equals(LAFClassName)) {
            LookAndFeel last = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(LAFClassName);
            } catch (UnsupportedLookAndFeelException ex) {
                try {
                    UIManager.setLookAndFeel(last);
                } catch (UnsupportedLookAndFeelException ex1) {
                    Logger.getLogger(Test.class.getName()).log(Level.SEVERE, "this should never happen", ex1);
                }
            } catch (Throwable t) {
                System.out.println("Very unexpected error when setting Look-And-Feel.");
                System.exit(1);
            }
        }
    }

	private static BHFileChooser bhfc;
    public static void main(String[] args) {
        JFrame frame = new JFrame("BH File Chooser test");
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
		JMenuBar jmb = new JMenuBar();
		jmb.add(new JCheckBox(new AbstractAction("Use old file choice method")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				bhfc.setUsesButton(jmb.isSelected());
			}
		}));
		frame.setJMenuBar(jmb);
        bhfc = new BHFileChooser(false, null, null);
		bhfc.addActionListener((ActionEvent e) ->
		{
			System.out.println("Files selected!");
			System.out.println(bhfc.getFiles());
			for (File file : bhfc.getFiles())
			{
				System.out.println("  \tOpening " + file + "...");
				try
				{
//					Desktop.getDesktop().open(file);
					FileInputStream fis = new FileInputStream(file);
					StringBuilder first64Bytes = new StringBuilder();
					for (int i = 0; i < 64; i++)
						first64Bytes.append((char)fis.read());
					JOptionPane.showOptionDialog(
						frame, // parent
						first64Bytes, // message
						"First 64 bytes of " + file.getName(), // title
						JOptionPane.NO_OPTION, // option type
						JOptionPane.PLAIN_MESSAGE, // message type
						null, // icon
						new Object[]{""}, // options
						null // default option
					);
				}
				catch (Throwable t)
				{
					Logger.getLogger(Test.class.getName()).log(Level.SEVERE, "Could not open file", t);
				}
			}
		});
        frame.add(bhfc, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(16, 16, 16, 16), 16, 16));
        frame.pack();
        frame.setVisible(true);
        bhfc.validate();
		Timer t = new Timer("Prettification");
		t.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				frame.pack();
			}
		}, 100);
    }
}
