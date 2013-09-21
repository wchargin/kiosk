package org.lcmmun.kiosk.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.QuorumStatus;

/**
 * A panel that allows the user to update the quorum status for delegates in a
 * given committee. The changes are applied instantly using radio buttons. It is
 * recommended that this panel be placed in a scroll pane of some sort, such as
 * a {@link JScrollPane}.
 * 
 * @author William Chargin
 * 
 */
public class QuorumPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The map of delegates being edited.
	 */
	private Map<Delegate, QuorumStatus> delegateMap;

	/**
	 * Creates a quorum panel for the given committee.
	 * 
	 * @param committee
	 *            the committee to configure
	 */
	public QuorumPanel(Committee committee) {
		super(new MigLayout());

		delegateMap = new TreeMap<Delegate, QuorumStatus>(
				committee.getDelegateMap());
		for (final Delegate delegate : delegateMap.keySet()) {
			JLabel label = new JLabel(delegate.getName());
			add(label, new CC().growX().pushX().newline());
			label.setHorizontalAlignment(SwingConstants.RIGHT);

			ButtonGroup buttonGroup = new ButtonGroup();

			QuorumStatus[] statuses = QuorumStatus.values();
			for (int i = 0; i < statuses.length; i++) {
				final QuorumStatus status = statuses[i];

				JRadioButton rdbtn = new JRadioButton(status.toString());
				rdbtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						delegateMap.put(delegate, status);
					}
				});
				rdbtn.setSelected(status == delegateMap.get(delegate));
				buttonGroup.add(rdbtn);

				if (status == QuorumStatus.PRESENT_AND_VOTING
						&& !delegate.getStatus().canVoteSubstantive) {
					// The delegate can't vote. Why allow a present and voting
					// option to be selected?
					rdbtn.setEnabled(false);
					rdbtn.setSelected(false);
				}

				add(rdbtn, new CC().growX());
			}
		}
	}

	/**
	 * Gets the map that is being edited.
	 * 
	 * @return the map
	 */
	public Map<Delegate, QuorumStatus> getMap() {
		return delegateMap;
	}
}
