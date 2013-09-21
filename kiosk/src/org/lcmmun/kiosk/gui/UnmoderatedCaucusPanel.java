package org.lcmmun.kiosk.gui;

import java.awt.Font;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.gui.events.CaucusEvent;
import org.lcmmun.kiosk.gui.events.CaucusEvent.CaucusEventType;
import org.lcmmun.kiosk.gui.events.CaucusListener;

import tools.customizable.Time;

/**
 * A panel to display the remaining time in an unmoderated caucus.
 * 
 * @author William Chargin
 * 
 */
public class UnmoderatedCaucusPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The time bar.
	 */
	private TimeBar timeBar;

	/**
	 * The {@code EventListenerList} for this object.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * The speaker list panel to use.
	 */
	private SpeakersListPanel speakersListPanel;

	/**
	 * Creates the panel.
	 */
	public UnmoderatedCaucusPanel() {
		this(null);
	}

	/**
	 * Creates the panel.
	 * 
	 * @param model
	 *            the delegate model, or {@code null} to create a new one
	 */
	public UnmoderatedCaucusPanel(DelegateModel model) {
		super(new MigLayout(new LC().flowY()));

		timeBar = new TimeBar();
		add(timeBar, new CC().growX().pushX());
		timeBar.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				if (timeBar.isFinished()) {
					stopCaucus();
				}
			}
		});

		speakersListPanel = new SpeakersListPanel(model, null);
		speakersListPanel.setCanSpeak(false);

		add(new JSeparator(JSeparator.HORIZONTAL), new CC().growX().pushX());

		final JLabel lblConfigTitle = new JLabel(
				Messages.getString("UnmoderatedCaucusPanel.GSLConfigTitle")); //$NON-NLS-1$
		lblConfigTitle.setFont(lblConfigTitle.getFont().deriveFont(Font.BOLD));
		lblConfigTitle.setHorizontalAlignment(JLabel.CENTER);

		final JLabel lblConfigDescription = new JLabel(
				Messages.getString("UnmoderatedCaucusPanel.GSLConfigDescription")); //$NON-NLS-1$
		lblConfigDescription.setHorizontalAlignment(JLabel.CENTER);

		add(lblConfigTitle, new CC().growX().pushX());
		add(lblConfigDescription, new CC().growX().pushX());
		add(speakersListPanel, new CC().grow().push());
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
	 * Starts the unmoderated caucus with the given total time.
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

	/**
	 * Delegate method for {@link SpeakersListPanel#setCommittee(Committee)}.
	 * 
	 * @param committee
	 *            the new committee
	 */
	public void setCommittee(Committee committee) {
		speakersListPanel.setCommittee(committee);
	}

	/**
	 * Delegate method for
	 * {@link SpeakersListPanel#updateDelegates(java.util.Collection)}.
	 * 
	 * @param presentDelegates
	 *            the new list of delegates
	 */
	public void updateDelegates(Collection<Delegate> presentDelegates) {
		speakersListPanel.updateDelegates(presentDelegates);
	}

}
