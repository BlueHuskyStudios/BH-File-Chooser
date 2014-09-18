package org.bh.tools.comps.evt;

import java.io.File;
import java.util.EventObject;

/**
 * FileChoiceEvent, made for BH File Chooser NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-09-17
 */
public class FileChoiceEvent extends EventObject
{
	public final File[] CHOSEN_FILES;

	public FileChoiceEvent(File[] chosenFiles, Object source)
	{
		super(source);
		CHOSEN_FILES = chosenFiles;
	}
}
