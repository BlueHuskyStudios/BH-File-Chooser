package org.bh.tools.comps;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.border.StrokeBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileFilter;

/**
 * BHFileChooser, made for BH File Chooser NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-09-15
 */
public class BHFileChooser extends JComponent implements AncestorListener, DropTargetListener, ComponentListener
{
	//<editor-fold defaultstate="collapsed" desc="declarations: psf">
	public static final Color DEFAULT_COLOR_WAITING      = SystemColor.textText;
	public static final Color DEFAULT_COLOR_EVALUATING   = SystemColor.textInactiveText;
	public static final Color DEFAULT_COLOR_ACCEPTABLE   = SystemColor.textHighlight;
	public static final Color DEFAULT_COLOR_UNACCEPTABLE = SystemColor.RED;
	public static final Color DEFAULT_COLOR_LOADING      = SystemColor.textInactiveText;
	public static final Color DEFAULT_COLOR_LOADED       = SystemColor.textText;
	
	private static final String MESSAGE_IO_EXCEPTION = "Aw, man! Something's seriously wrong!";
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="declarations: private">
	private boolean useButton = true, isAccepting = true;
	private Color
	waitingColor      = DEFAULT_COLOR_WAITING,
	evaluatingColor   = DEFAULT_COLOR_EVALUATING,
	acceptableColor   = DEFAULT_COLOR_ACCEPTABLE,
	unacceptableColor = DEFAULT_COLOR_UNACCEPTABLE,
	loadingColor      = DEFAULT_COLOR_LOADING,
	loadedColor       = DEFAULT_COLOR_LOADED;
	private FileFilter fileFilter;
	private List<File> files = new ArrayList<>();
	private State state = State.WAITING;
	private int acceptableAction = DnDConstants.ACTION_COPY_OR_MOVE, userAction;
	//</editor-fold>
	
	public BHFileChooser(boolean initUseButton, FileFilter initFileFilter)
	{
		useButton = initUseButton;
		fileFilter = initFileFilter;
		initGUI();
	}

	//<editor-fold defaultstate="collapsed" desc="GUI">
	//<editor-fold defaultstate="collapsed" desc="initialization">
	private ChooseFileButton chooseButton;
	private JLabel dragDropCTA;
	private JFileChooser oldFileChooser;
	private void initGUI()
	{
		setLayout(new GridBagLayout());
		{
			setUsesButton(useButton);
		}
		{
			dragDropCTA = new JLabel();
			add(
					dragDropCTA,
					new GridBagConstraints(
							0, 0, 1, 1, 1, 1,
							GridBagConstraints.CENTER,
							GridBagConstraints.NONE,
							new Insets(0, 0, 0, 0),
							0, 0
					)
			);
		}
		{
			setDropTarget(new DropTarget(this, acceptableAction, this, true));
		}
		addAncestorListener(this);
	}
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="Validation">
	@Override
	public void validate()
	{
		super.validate(); //To change body of generated methods, choose Tools | Templates.
		getDropTarget().setActive(isAccepting && state != State.EVALUATED_UNACCEPTABLE);
		validateBorder();
		validateCTA();
	}
	
	private void validateBorder()
	{
		Font font = getFont();
		setBorder(
			new DashedBorder(
				// size is the 50% font size if available, else 5% the window size, with a minimum of 1:
				(float)Math.max(
					1,
					font == null
						? Math.max(getHeight(), getWidth()) * 0.05
						: font.getSize2D() * 0.5),
				getStateColor()
			)
		);
	}
	
	private void validateCTA()
	{
		Font font = getFont();
		if (font != null)
			dragDropCTA.setFont(font.deriveFont(font.getSize2D() * 2));
		dragDropCTA.setForeground(getStateColor());
		dragDropCTA.setText("Drag & drop " + (fileFilter == null ? "files" : fileFilter.getDescription()) + " here");
	}
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="GUI Overrides">
	//<editor-fold defaultstate="collapsed" desc="AncestorListener">
	@Override
	public void ancestorAdded(AncestorEvent event)
	{
//		System.out.println("Added: " + event);
		validate();
	}
	
	@Override public void ancestorRemoved(AncestorEvent event){}
	@Override public void ancestorMoved(AncestorEvent event){}
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="DropTargetListener">
	@Override
	public void dragEnter(DropTargetDragEvent dtde)
	{
		setState(State.WAITING);
		setState(State.EVALUATING);
		setState(canAccept(dtde.getTransferable()) ? State.EVALUATED_ACCEPTABLE : State.EVALUATED_UNACCEPTABLE);
		validate();
	}
	
	@Override
	public void dragExit(DropTargetEvent dte)
	{
		System.out.println("exiting");
		setState(State.WAITING);
		validate();
	}
	
	@Override public void dragOver(DropTargetDragEvent dtde){}
	
	@Override
	public void dropActionChanged(DropTargetDragEvent dtde)
	{
		userAction = dtde.getDropAction();
		validate();
	}
	
	@Override
	public void drop(DropTargetDropEvent dtde)
	{
		setState(State.WAITING); // necessary to reset the state
		setState(State.EVALUATING);
		dtde.acceptDrop(acceptableAction);
		Transferable t = dtde.getTransferable();
		try
		{
			if (canAccept(t))
				try
				{
					setState(State.EVALUATED_ACCEPTABLE);
					List<File> data = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
					acceptFiles(data);
				}
				catch (UnsupportedFlavorException ex)
				{
					setState(State.EVALUATED_UNACCEPTABLE);
				}
				catch (IOException ex)
				{
					Logger.getGlobal().log(Level.SEVERE, MESSAGE_IO_EXCEPTION, ex);
				}
			else
				setState(State.EVALUATED_UNACCEPTABLE);
		}
		catch (Throwable th)
		{
			th.printStackTrace();
			setState(State.EVALUATED_UNACCEPTABLE); // evaluation could not be completed, so we can't accept it
			setState(State.WAITING);                // go back to waiting for another file
		}
		
		validate();
	}
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="ComponentListener">
	@Override
	public void componentResized(ComponentEvent e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public void componentMoved(ComponentEvent e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public void componentShown(ComponentEvent e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	@Override
	public void componentHidden(ComponentEvent e)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	//</editor-fold>
	//</editor-fold>
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="public getters and setters">
	public void setUsesButton(boolean usesButton)
	{
		if (usesButton)
		{
			if (chooseButton == null)
				chooseButton = new ChooseFileButton(oldFileChooser = new JFileChooser());
			add(chooseButton,
				new GridBagConstraints(
						0, 0, 1, 1, 1d, 1d,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(0, 0, 0, 0),
						1, 1
				)
			);
		}
		else if (chooseButton != null)
		{
			remove(chooseButton);
		}
	}
	
	/**
	 * Accepts all given files. This assumes they've already been evaluated to be acceptable, and therefore that the current
	 * state is {@link State#EVALUATED_ACCEPTABLE} or {@link State#LOADING}.
	 *
	 * @param fileList the list of files to accept
	 */
	public void acceptFiles(List<File> fileList)
	{
		setState(State.LOADING);
		try
		{
			files.clear();
			for (File file : fileList)
				files.addAll(fileList);
			setState(State.LOADED);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			setState(State.EVALUATED_UNACCEPTABLE);
			setState(State.WAITING);
		}
	}
	public void acceptFile(File f)
	{
		ArrayList<File> al = new ArrayList<>();
		al.add(f);
		acceptFiles(al);
	}
	
	public void setDropAction(int newAction)
	{
		getDropTarget().setDefaultActions(newAction);
		validate();
	}
	public int getDropAction()
	{
		return getDropTarget().getDefaultActions();
	}
	
	public void setAccepting(boolean newAccepting)
	{
		isAccepting = newAccepting;
		validate();
	}
	public boolean isAccepting()
	{
		return isAccepting;
	}
	//</editor-fold>
	
	//<editor-fold defaultstate="collapsed" desc="private getters and setters">
	private Color getStateColor()
	{
		switch (state)
		{
			case WAITING:
				return waitingColor;
			case EVALUATING:
				return evaluatingColor;
			case EVALUATED_UNACCEPTABLE:
				return unacceptableColor;
			case EVALUATED_ACCEPTABLE:
				return acceptableColor;
			case LOADING:
				return loadingColor;
			case LOADED:
				return loadedColor;
			default:
				throw new AssertionError();
		}
	}
	
	@SuppressWarnings({"ThrowableInstanceNotThrown", "ThrowableInstanceNeverThrown"})
	private void setState(State newState)
	{
		if (state.ganGoTo(newState))
			state = newState;
		else
		{
			Logger.getGlobal().log(Level.WARNING, "Tried to go from {0} to {1}", new Object[]{state, newState});
			new Throwable().printStackTrace();
		}
	}
	
	private boolean canAccept(Transferable t)
	{
		try
		{
			System.out.println(Arrays.deepToString(t.getTransferDataFlavors()));
			
			if (
				   0 != (acceptableAction | userAction)
				|| !t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
			)
				return false;
			
			List<File> data = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
			if (fileFilter == null)
				return true;
			return
				data.stream() // treat the list as a stream
					.noneMatch(   // return true if none match the following rule:
						(f) -> (                  // for each file
							!fileFilter.accept(f) // if the filter doesn't accept it
						)
					)
				;
		}
		catch (UnsupportedFlavorException ex){}
		catch (IOException ex)
		{
			Logger.getLogger(BHFileChooser.class.getName()).log(Level.SEVERE, MESSAGE_IO_EXCEPTION, ex);
		}
		return false;
	}
	//</editor-fold>


	//<editor-fold defaultstate="collapsed" desc="inner classes">
	public static class ChooseFileButton extends JButton
	{
		private static final String DEFAULT_TEXT = "Choose File...";
		
		JFileChooser jfc;
		Window w;
		
		public ChooseFileButton()
		{
			this(DEFAULT_TEXT, new JFileChooser());
		}

		public ChooseFileButton(String text)
		{
			this(text, new JFileChooser());
		}

		public ChooseFileButton(JFileChooser fileChooserToShow)
		{
			this(DEFAULT_TEXT, fileChooserToShow);
		}
		
		public ChooseFileButton(CharSequence initText, JFileChooser fileChooserToShow)
		{
			super(String.valueOf(initText));
			jfc = fileChooserToShow;
			
			addActionListener((ActionEvent e) ->
			{
				if (w == null)
				{
					Container c = jfc;
					do c = c.getParent();
					while (!(c instanceof Window) && c != null);
					
					if (c != null && c instanceof Window) // if it's already in a window, use that
						w = (Window)c;
					else
						((JWindow)(w = new JWindow())).setContentPane(jfc);
				}
				w.setVisible(true);
			});
		}
	}
	
	/**
	 * A class that simplifies the creation of a dashed border
	 */
	public static class DashedBorder extends StrokeBorder
	{
		private Color color;
		
		/**
		 * Creates a new dashed border with the given thickness and color
		 *
		 * @param initThickness the initial thickness of the dashed border. Note that this will also be used as a basis for the
		 *			spacing of the dashes. Also note that, due to the parent {@link StrokeBorder} class not exposing its
		 *			{@link StrokeBorder#stroke} object, this is permanent. To change this, <STRONG>you must create a new
		 *			{@link DashedBorder} object.</STRONG>
		 * @param initColor the initial color of the border.
		 */
		public DashedBorder(float initThickness, Color initColor)
		{
			super(new DashStroke(initThickness));
			color = initColor;
		}
		
		/**
		 * Changes the color of this dashed border. Note that this does <EM>not</EM> repaint the border, so the component
		 * hosting it must be repainted.
		 *
		 * @param newColor the new color to set
		 */
		public void setColor(Color newColor)
		{
			color = newColor;
		}
		
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			Color oldForeground = c.getForeground();
			c.setForeground(color); // we have to do this
			g.setColor(color); // because this doesn't work for some reason. I'll keep it for cross-version compatibility, though.
			super.paintBorder(c, g, x, y, width, height);
			c.setForeground(oldForeground);
		}
	}
	
	public static class DashStroke extends BasicStroke
	{
		public DashStroke(float initThickness)
		{
			super(
					initThickness,
				BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND,
				1f,
				new float[]{initThickness * 2, initThickness * 2},
				initThickness);
		}
	}
	
	private static enum State
	{
		WAITING,
		EVALUATING,
		EVALUATED_ACCEPTABLE,
		EVALUATED_UNACCEPTABLE,
		LOADING,
		LOADED;
		
		public boolean ganGoTo(State newState)
		{
			if (this == newState)
				return true;
			
			switch (this)
			{
				case WAITING:
					switch (newState)
					{
						case EVALUATING:
							return true;
						default:
							return false;
					}
				case EVALUATING:
					switch (newState)
					{
						case EVALUATED_ACCEPTABLE:
						case EVALUATED_UNACCEPTABLE:
							return true;
						default:
							return false;
					}
				case EVALUATED_ACCEPTABLE:
					switch (newState)
					{
						case WAITING:
						case LOADING:
							return true;
						default:
							return false;
					}
				case EVALUATED_UNACCEPTABLE:
					return newState == WAITING;
				case LOADING:
					switch (newState)
					{
						case EVALUATED_UNACCEPTABLE:
						case LOADED:
							return true;
						default:
							return false;
					}
				case LOADED:
					return newState == WAITING;
				default:
					throw new AssertionError(newState);
			}
		}
	}
	//</editor-fold>
}
