package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.gui.events.CaucusEvent;
import org.lcmmun.kiosk.gui.events.CaucusListener;
import org.lcmmun.kiosk.gui.events.CaucusEvent.CaucusEventType;

import tools.customizable.Time;

/**
 * A panel to display the remaining time in an formal caucus.
 * 
 * @author William Chargin
 * 
 */
public class FormalCaucusPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The time bar.
	 */
	private TimeBar timeBar;

	/**
	 * Creates the panel.
	 */
	public FormalCaucusPanel() {
		super(new BorderLayout());

		timeBar = new TimeBar();
		add(timeBar, BorderLayout.CENTER);
		timeBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				if (timeBar.isFinished()) {
					stopCaucus();
				}
			}
		});
	}

	/**
	 * Adds the given {@code CaucusListener} to the list of listeners.
	 * 
	 * @param cel
	 *            the listener to add
	 */
	public void addCaucusListener(CaucusListener cel) {
		listenerList.add(CaucusListener.class, cel);
	}

	/**
	 * Removes the given {@code CaucusListener} from the list of listeners.
	 * 
	 * @param cel
	 *            the listener to remove
	 */
	public void removeCaucusListener(CaucusListener cel) {
		listenerList.remove(CaucusListener.class, cel);
	}

	/**
	 * Starts the formal caucus with the given total time.
	 * 
	 * @param totalTime
	 *            the total time
	 */
	public void start(Time totalTime) {
		timeBar.start(totalTime);
	}

	/**
	 * Stops the current caucus.
	 */
	protected void stopCaucus() {
		timeBar.stop();
		for (CaucusListener cel : listenerList
				.getListeners(CaucusListener.class)) {
			cel.caucusActionPerformed(new CaucusEvent(this,
					CaucusEventType.ELAPSED));
		}
	}

}
