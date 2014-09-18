package org.bh.tools.comps.evt;

import java.util.EventListener;

/**
 * FileChoiceListener, made for BH File Chooser NetBeans Project, is copyright Blue Husky Programming ©2014 CC 3.0 BY-SA<HR/>
 * 
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * @since 2014-09-17
 */
public interface FileChoiceListener extends EventListener
{
	public void fileChosen(FileChoiceEvent evt);
}
