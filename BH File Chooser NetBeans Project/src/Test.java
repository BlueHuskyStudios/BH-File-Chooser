
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
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
        if (!UIManager.getLookAndFeel().getClass().getName()
                .equals(LAFClassName)) {
            LookAndFeel last = UIManager.getLookAndFeel();
            try {
                UIManager.setLookAndFeel(LAFClassName);
                Window[] windows = Window.getWindows();
                boolean isFrame;
                int state;
                for (int i = 0;
                        i < windows.length;
                        i++) {
                    java.awt.Rectangle r = new java.awt.Rectangle(windows[i].getBounds());//Changed to new rather than assignment {Feb 22, 2012 (1.1.1) for Marian}
                    isFrame = windows[i] instanceof java.awt.Frame;
                    state = isFrame ? ((java.awt.Frame) windows[i]).getState() : java.awt.Frame.NORMAL;
                    javax.swing.SwingUtilities.updateComponentTreeUI(windows[i]);
                    if (isFrame) {
                        ((java.awt.Frame) (windows[i])).setState(state);
                    }
                    if (!isFrame || state != java.awt.Frame.MAXIMIZED_BOTH) {
                        windows[i].setBounds(r);
                    }
                }
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("BH File Chooser test");
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        BHFileChooser bhfc = new BHFileChooser(true, null);
        frame.add(bhfc, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(16, 16, 16, 16), 16, 16));
        frame.pack();
        frame.setVisible(true);
        bhfc.validate();
        frame.pack();
    }
}
