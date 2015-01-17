/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SRMulticast;

import java.awt.event.MouseAdapter;
import javax.swing.JComponent;
import javax.swing.RootPaneContainer;

/**
 *
 * @author Administrator
 */
public  class CursorToolkitTwo implements Cursors {
	public final static MouseAdapter mouseAdapter = new MouseAdapter() {
	};

	public  CursorToolkitTwo() {
	}

	/** Sets cursor for specified component to Wait cursor */
	public static void startWaitCursor(JComponent component) {
		RootPaneContainer root = ((RootPaneContainer) component
				.getTopLevelAncestor());
		root.getGlassPane().setCursor(WAIT_CURSOR);
		root.getGlassPane().addMouseListener(mouseAdapter);
		root.getGlassPane().setVisible(true);
	}

	/** Sets cursor for specified component to normal cursor */
	public static void stopWaitCursor(JComponent component) {
		RootPaneContainer root = ((RootPaneContainer) component
				.getTopLevelAncestor());
		root.getGlassPane().setCursor(DEFAULT_CURSOR);
		root.getGlassPane().removeMouseListener(mouseAdapter);
		root.getGlassPane().setVisible(false);
	}
}