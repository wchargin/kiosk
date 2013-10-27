package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.QuorumStatus;
import org.lcmmun.kiosk.gui.CommitteeWizard.ProgressLabel.Status;
import org.lcmmun.kiosk.resources.ImageFetcher;
import org.lcmmun.kiosk.resources.ImageType;

import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TextProperty;
import tools.customizable.TimeProperty;

/**
 * A committee setup wizard.
 * 
 * @author William Chargin
 * 
 */
public class CommitteeWizard extends JDialog {

	/**
	 * @author William
	 * 
	 */
	public static class ProgressLabel extends JLabel {

		public enum Status {
			NOT_STARTED(new ImageIcon(new BufferedImage(16, 16,
					BufferedImage.TYPE_INT_ARGB))), PENDING(ImageFetcher
					.fetchImageIcon(ImageType.STEP_PENDING)), COMPLETE(
					ImageFetcher.fetchImageIcon(ImageType.STEP_COMPLETED));

			private final ImageIcon icon;

			private Status(ImageIcon image) {
				this.icon = image;
			}
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The current status of this label.
		 */
		private Status status;

		public ProgressLabel(String text) {
			super(text);
			setStatus(Status.NOT_STARTED);
			setIconTextGap(0);
			setBorder(new EmptyBorder(6, 4, 6, 4));
			setFont(getFont().deriveFont(getFont().getSize() + 2f));
		}

		@Override
		public Dimension getPreferredSize() {
			Font oldFont = getFont();
			setFont(oldFont.deriveFont(Font.BOLD));
			Dimension val = super.getPreferredSize();
			setFont(oldFont);
			return val;
		}

		/**
		 * Gets the current label status.
		 * 
		 * // * @return the status
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * Sets the label status.
		 * 
		 * @param status
		 *            the status
		 */
		public void setStatus(Status status) {
			this.status = status;
			setIcon(status.icon);
			setFont(getFont().deriveFont(
					status == Status.PENDING ? Font.BOLD : ~Font.BOLD));
		}

	}

	private static class TitleBar extends JLabel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The padding on the bottom.
		 */
		private static final int BOTTOM_PADDING = 3;

		public TitleBar(String text) {
			super(text);
			setBorder(new CompoundBorder(new EmptyBorder(0, 0, BOTTOM_PADDING,
					0), new CompoundBorder(new MatteBorder(0, 0, 3, 0,
					Color.BLACK), new EmptyBorder(4, 4, 4, 4))));
			setFont(getFont().deriveFont(24f).deriveFont(Font.BOLD));
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension superSize = super.getPreferredSize();
			if (superSize.width < 450) {
				superSize.width = 450;
			}
			return superSize;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			GradientPaint gp = new GradientPaint(0, 0, Color.getHSBColor(
					2f / 3f, 0.4f, 1f), getWidth() < 350 ? getWidth() : 350, 0,
					Color.WHITE);
			g2d.setPaint(gp);
			g2d.fillRect(0, 0, getWidth(), getHeight() - BOTTOM_PADDING);
			super.paintComponent(g);
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The committee being created.
	 */
	public final Committee committee;

	/**
	 * The list of progress labels.
	 */
	private final ArrayList<ProgressLabel> labels = new ArrayList<ProgressLabel>();

	/**
	 * The card layout.
	 */
	private final CardLayout card = new CardLayout();

	/**
	 * The panel containing the various steps.
	 */
	private final JPanel pnlSteps = new JPanel(card);

	/**
	 * The topics text area.
	 */
	private final JTextArea txtrTopics;

	/**
	 * The delegates configuration panel.
	 */
	private final DelegatesConfigurationPanel dcp;

	public CommitteeWizard(Window parent, Committee committee) {
		super(parent);
		setModal(true);
		this.committee = committee;
		JPanel pnlContent = new JPanel(new BorderLayout());
		setContentPane(pnlContent);
		pnlContent
				.add(new TitleBar(Messages
						.getString("CommitteeWizard.WizardTitle")), //$NON-NLS-1$
						BorderLayout.NORTH);

		Box bProgress = Box.createVerticalBox();
		add(bProgress, BorderLayout.WEST);

		labels.add(new ProgressLabel(Messages
				.getString("CommitteeWizard.StepCommittee"))); //$NON-NLS-1$
		labels.add(new ProgressLabel(Messages
				.getString("CommitteeWizard.StepTopics"))); //$NON-NLS-1$
		labels.add(new ProgressLabel(Messages
				.getString("CommitteeWizard.StepDelegates"))); //$NON-NLS-1$
		boolean first = true;
		for (ProgressLabel label : labels) {
			if (first) {
				first = false;
				label.setStatus(Status.PENDING);
			}
			bProgress.add(label);
		}

		pnlContent.add(pnlSteps, BorderLayout.CENTER);
		pnlSteps.setBorder(new EmptyBorder(4, 4, 4, 4));
		PropertySet psCommittee = new PropertySet();

		final TextProperty tpName = new TextProperty(
				Messages.getString("Kiosk.PropertyCommitteeName"), //$NON-NLS-1$
				committee.getName());
		psCommittee.add(tpName);
		tpName.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				CommitteeWizard.this.committee.setName(tpName.getValue());
			}
		});

		final TimeProperty tpTime = new TimePresetProperty(
				Messages.getString("Kiosk.PropertyCommitteeSpeakingTime"), //$NON-NLS-1$
				committee.speakingTime);
		psCommittee.add(tpTime);
		tpTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				CommitteeWizard.this.committee.speakingTime = tpTime.getValue();
			}
		});
		PropertyPanel ppCommittee = new PropertyPanel(psCommittee, true, false);

		pnlSteps.add(ppCommittee, Integer.toString(1));

		JPanel pnlTopics = new JPanel(new BorderLayout());
		pnlTopics.add(
				new JLabel(Messages.getString("CommitteeWizard.TopicsPrompt")), //$NON-NLS-1$
				BorderLayout.NORTH);
		txtrTopics = new JTextArea();
		txtrTopics.setLineWrap(true);
		txtrTopics.setWrapStyleWord(true);
		Iterator<String> itTopic = committee.getTopics().iterator();
		while (itTopic.hasNext()) {
			txtrTopics.setText(txtrTopics.getText() + itTopic.next()
					+ (itTopic.hasNext() ? '\n' : new String()));
		}
		pnlTopics.add(new JScrollPane(txtrTopics,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		pnlSteps.add(pnlTopics, Integer.toString(2));

		dcp = new DelegatesConfigurationPanel(committee.getPresentDelegates(),
				committee);
		pnlSteps.add(dcp, Integer.toString(3));

		// In case there's too many labels...
		for (int i = pnlSteps.getComponentCount(); i < labels.size(); i++) {
			pnlSteps.add(new JPanel(), Integer.toString(i));
		}

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		pnlContent.add(pnlButtons, BorderLayout.SOUTH);

		JButton btnSkip = new JButton(
				Messages.getString("CommitteeWizard.SkipButton")); //$NON-NLS-1$
		pnlButtons.add(btnSkip);
		btnSkip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				applyChanges();
				dispose();
			}
		});
		btnSkip.setFocusable(false);

		final JButton btnNext = new JButton(
				Messages.getString("CommitteeWizard.NextButton")); //$NON-NLS-1$
		pnlButtons.add(btnNext);
		getRootPane().setDefaultButton(btnNext);
		btnNext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				card.next(pnlSteps);
				Iterator<ProgressLabel> it = labels.iterator();
				while (it.hasNext()) {
					ProgressLabel label = it.next();
					if (label.getStatus() == Status.PENDING) {
						label.setStatus(Status.COMPLETE);
						if (it.hasNext()) {
							ProgressLabel next = it.next();
							next.setStatus(Status.PENDING);
							if (!it.hasNext()) {
								btnNext.setText(Messages
										.getString("CommitteeWizard.FinishButton")); //$NON-NLS-1$
							}
						} else {
							// All done.
							applyChanges();
							dispose();
						}
						return;
					}
				}
			}
		});
	}

	private void applyChanges() {
		ArrayList<String> topics = new ArrayList<String>(
				Arrays.<String> asList(txtrTopics.getText().split(
						Character.toString('\n'))));
		Iterator<String> itTopic = topics.iterator();
		while (itTopic.hasNext()) {
			if (itTopic.next().trim().isEmpty()) {
				itTopic.remove();
			}
		}
		if (!topics.isEmpty()) {
			committee.setTopics(topics);
			if (committee.getCurrentTopic() == null) {
				committee.setCurrentTopic(topics.get(0));
			}
		}

		List<Delegate> newDelegates = dcp.getDelegates();
		List<Delegate> currentDelegates = new ArrayList<Delegate>(
				CommitteeWizard.this.committee.delegates.keySet());

		// Remove those that were removed.
		Iterator<Delegate> itCurrent = currentDelegates.iterator();
		while (itCurrent.hasNext()) {
			Delegate delegate = itCurrent.next();
			if (!newDelegates.contains(delegate)) {
				itCurrent.remove();
				CommitteeWizard.this.committee.delegates.remove(delegate);
			}
		}

		// Add those that were added.
		for (Delegate delegate : newDelegates) {
			if (!currentDelegates.contains(delegate)) {
				CommitteeWizard.this.committee.delegates.put(delegate,
						QuorumStatus.DEFAULT);
			}
		}
	}
}
