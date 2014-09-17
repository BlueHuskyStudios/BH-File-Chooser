
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import org.bh.tools.comps.BHFileChooser;

/**
 * Test, made for BH File Chooser NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-09-15
 */
public class Test
{
	public static void main(String[] args)
	{
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
