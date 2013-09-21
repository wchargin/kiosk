package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lcmmun.kiosk.Crisis;
import org.lcmmun.kiosk.Messages;

import tools.customizable.FileProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TextProperty;
import tools.customizable.Time;
import tools.customizable.TimeProperty;
import tools.customizable.TrueFalseProperty;

/**
 * A panel allowing editing of a {@link Crisis}.
 * 
 * @author William Chargin
 * 
 */
public class CrisisEditor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The crisis being edited.
	 */
	private final Crisis crisis;

	/**
	 * The runnable to apply changes. We use a runnable so that it can use
	 * enclosing class local variables instead of creating a whole bunch of
	 * fields.
	 */
	private final Runnable rApply;

	/**
	 * Creates the editor for the given crisis.
	 * 
	 * @param c
	 *            the crisis to edit
	 */
	public CrisisEditor(Crisis c) {
		super(new BorderLayout());
		if (c == null) {
			c = new Crisis();
		}
		crisis = c;

		PropertySet ps = new PropertySet();

		final TextProperty tpName = new TextProperty(
				Messages.getString("CrisisEditor.PropertyName"), crisis.getName()); //$NON-NLS-1$
		ps.add(tpName);

		final TrueFalseProperty tfpDeployed = new TrueFalseProperty(
				Messages.getString("CrisisEditor.PropertyAlreadyDeployed"), crisis.isDeployed(), Messages.getString("CrisisEditor.PropertyValueAlreadyDeployedYes"), //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("CrisisEditor.PropertyValueAlreadyDeployedNo")); //$NON-NLS-1$
		tfpDeployed.setEnabled(false);
		ps.add(tfpDeployed);

		ps.add(null);

		final FileProperty fpBriefing = new FileProperty(
				Messages.getString("CrisisEditor.PropertyBriefing"), //$NON-NLS-1$
				crisis.getFile());
		ps.add(fpBriefing);
		fpBriefing.setFilter(new FileNameExtensionFilter(Messages
				.getString("CrisisEditor.PDFFileFilter"), "PDF")); //$NON-NLS-1$ //$NON-NLS-2$

		final TextProperty tpGoogleDocs = new TextProperty(
				Messages.getString("CrisisEditor.GoogleDocsID"), //$NON-NLS-1$
				crisis.getGoogleDocsId());
		ps.add(tpGoogleDocs);

		ps.add(null);

		final TrueFalseProperty tfpGuestSpeaker = new TrueFalseProperty(
				Messages.getString("CrisisEditor.PropertyGuestSpeaker"), crisis.isGuestSpeaker(), Messages.getString("CrisisEditor.PropertyValueGuestSpeakerYes"), Messages.getString("CrisisEditor.PropertyValueGuestSpeakerNo")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ps.add(tfpGuestSpeaker);

		final TextProperty tpGuestSpeakerName = new TextProperty(
				Messages.getString("CrisisEditor.PropertyGuestSpeakerName"), crisis.getGuestSpeakerName()); //$NON-NLS-1$
		ps.add(tpGuestSpeakerName);

		final TextProperty tpGuestSpeakerOccupation = new TextProperty(
				Messages.getString("CrisisEditor.PropertyGuestSpeakerOccupation"), crisis.getGuestSpeakerOccupation()); //$NON-NLS-1$
		ps.add(tpGuestSpeakerOccupation);

		ps.add(null);

		final Time qaTime = crisis.getQaTime();
		final TrueFalseProperty tfpQaSession = new TrueFalseProperty(
				Messages.getString("CrisisEditor.PropertyQASession"), qaTime != null, Messages.getString("CrisisEditor.PropertyValueQASessionYes"), Messages.getString("CrisisEditor.PropertyValueQASessionNo")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ps.add(tfpQaSession);

		final TimeProperty tpQaTime = new TimeProperty(
				Messages.getString("CrisisEditor.PropertyQALength"), //$NON-NLS-1$
				qaTime == null ? new Time(0, 5, 0) : qaTime);
		ps.add(tpQaTime);

		tfpGuestSpeaker.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				boolean guestSpeaker = tfpGuestSpeaker.getValue();
				tpGuestSpeakerName.setEnabled(guestSpeaker);
				tpGuestSpeakerOccupation.setEnabled(guestSpeaker);
				tfpQaSession.setEnabled(guestSpeaker);
				boolean qaSession = guestSpeaker && tfpQaSession.getValue();
				tpQaTime.setEnabled(qaSession);
			}
		});

		tfpQaSession.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				tpQaTime.setEnabled(tfpGuestSpeaker.getValue()
						&& tfpQaSession.getValue());
			}
		});

		boolean guestSpeaker = tfpGuestSpeaker.getValue();
		tpGuestSpeakerName.setEnabled(guestSpeaker);
		tpGuestSpeakerOccupation.setEnabled(guestSpeaker);
		tfpQaSession.setEnabled(guestSpeaker);
		boolean qaSession = guestSpeaker && tfpQaSession.getValue();
		tpQaTime.setEnabled(qaSession);

		add(new PropertyPanel(ps, true, false), BorderLayout.CENTER);

		rApply = new Runnable() {
			@Override
			public void run() {
				crisis.setName(tpName.getValue());
				crisis.setDeployed(tfpDeployed.getValue());
				crisis.setFile(fpBriefing.getValue());
				crisis.setGoogleDocsId(tpGoogleDocs.getValue());
				crisis.setGuestSpeaker(tfpGuestSpeaker.getValue());
				crisis.setGuestSpeakerName(tpGuestSpeakerName.getValue());
				crisis.setGuestSpeakerOccupation(tpGuestSpeakerOccupation
						.getValue());
				crisis.setQaTime(tfpQaSession.getValue() ? tpQaTime.getValue()
						: null);
			}
		};
	}

	/**
	 * Applies the changes in the editor.
	 */
	public void applyChanges() {
		rApply.run();
	}

}
