package org.lcmmun.kiosk.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.CommitteeState;
import org.lcmmun.kiosk.Crisis;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.WorkingPaper;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechListener;
import org.lcmmun.kiosk.gui.events.YieldActionEvent;
import org.lcmmun.kiosk.gui.events.YieldActionListener;
import org.lcmmun.kiosk.gui.events.YieldEvent;
import org.lcmmun.kiosk.gui.events.YieldListener;

import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TextProperty;
import tools.customizable.Time;

/**
 * The view showed to the members of the committee. This provides all the
 * information the delegates need to know.
 * <p>
 * This class replaces the various features of the
 * {@code org.lcmmun.kiosk.gui.widgets} package used in Kiosk II.
 * 
 * @since 3.0
 * 
 * @author William Chargin
 * 
 */
public class PublicDisplay extends JFrame implements YieldListener,
		YieldActionListener, SpeechListener {

	class MotionSpeechPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The panel for speakers in favor.
		 */
		private JPanel pnlInFavor;

		/**
		 * The panel for speakers against.
		 */
		private JPanel pnlAgainst;

		/**
		 * The speech panel.
		 */
		private RadialImageSpeechPanel speech = new RadialImageSpeechPanel();

		public MotionSpeechPanel() {
			super(new MigLayout());
			pnlInFavor = new JPanel(new MigLayout(new LC().flowY()));
			pnlAgainst = new JPanel(new MigLayout(new LC().flowY()));
			add(pnlInFavor, new CC().dockWest());
			add(speech, new CC().grow().push());
			add(pnlAgainst, new CC().dockEast());
		}

		private void speechAgainst(SpeechEvent se) {
			processSpeech(se, pnlAgainst);
		}

		@SuppressWarnings("incomplete-switch")
		private void processSpeech(SpeechEvent se, JPanel panel) {
			switch (se.type) {
			case STARTED:
				speech.startSpeech(se.speaker, MotionDebatePanel.TIME);
				panel.add(new JLabel(se.speaker.getMediumIcon()));
				revalidate();
				break;
			case PAUSED:
				speech.pauseSpeech();
				break;
			case FINISHED:
				stopSpeech();
				break;
			}
		}

		private void speechInFavor(SpeechEvent se) {
			processSpeech(se, pnlInFavor);
		}

		private void stopSpeech() {
			speech.stopSpeech();
		}

		public void clear() {
			stopSpeech();
			pnlInFavor.removeAll();
			pnlAgainst.removeAll();
			pnlInFavor.revalidate();
			pnlAgainst.revalidate();
		}

	}

	/**
	 * A glass pane to display a message.
	 * 
	 * @author William Chargin
	 * 
	 */
	class MessageGlassPane extends JComponent {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The message to be displayed in the pane.
		 */
		private String message;

		/**
		 * The current tick value.
		 */
		private int tick = -1;

		/**
		 * The timer used to increment the display.
		 */
		private final Timer increment = new Timer(40, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				tick++;
				if (tick > 50 /* 2 seconds */) {
					tick = -1;
					increment.stop();
				}
				getGlassPane().repaint();
			}
		});

		/**
		 * Creates the panel with the given message.
		 * 
		 * @param message
		 *            the message to use
		 */
		public MessageGlassPane(String message) {
			super();
			this.message = message;
		}

		/**
		 * Begins flashing the message.
		 */
		public void flashMessage() {
			tick = -1;
			increment.start();
		}

		/**
		 * Gets the message displayed by this pane.
		 * 
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (tick == -1 || message == null || message.isEmpty()) {
				// Nothing to do here; move along.
				return;
			}
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			float alpha = (tick > 25 ? (1 - ((float) tick - 25f) / 25f) : 1f);
			g2d.setColor(new Color(1f, 1f, 1f, alpha * 0.8f));
			g2d.fillRect(0, 0, getWidth(), getHeight());

			g2d.setColor((tick / 10) % 2 == 0 ? Color.RED : Color.BLACK);
			g2d.setStroke(new BasicStroke(4));
			g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);

			float width = (0.8f * getWidth());
			g2d.setColor(new Color((tick / 10 % 2 == 0) ? 0f : 1f, 0f, 0f,
					alpha));
			FontMetrics fm = g2d.getFontMetrics();
			float oldWidth = fm.stringWidth(message);
			float desiredWidth = width;
			float oldSize = g2d.getFont().getSize();

			// oldSize / oldWidth = desiredSize / desiredWidth
			// desiredSize = (desiredWidth) * (oldSize / oldWidth)
			g2d.setFont(g2d.getFont().deriveFont(
					(desiredWidth * (oldSize / oldWidth))));

			AffineTransform at = new AffineTransform();
			double scale = ((double) tick / 50d) * 0.25d + 0.75d;
			at.translate(getWidth() / 2d, getHeight() / 2d);
			at.scale(scale, scale);
			at.translate(-getWidth() / 2d, -getHeight() / 2d);
			g2d.setTransform(at);

			fm = g2d.getFontMetrics();
			g2d.drawString(message, (getWidth() - width) / 2, getHeight() / 2
					+ (fm.getAscent() / 3));
		}

		/**
		 * Sets the message displayed by this pane. {@code null} will cause no
		 * message to be displayed.
		 * 
		 * @param message
		 *            the new message
		 */
		public void setMessage(String message) {
			this.message = (message == null ? null : message.trim());
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The main card layout, whose components are the various view states.
	 */
	private final CardLayout clMain = new CardLayout();

	/**
	 * The content pane.
	 */
	private final JPanel pnlContent = new JPanel(clMain);

	/**
	 * The decorum glass pane.
	 */
	private final MessageGlassPane msgDecorum = new MessageGlassPane(null);

	/**
	 * The card layout for the {@linkplain #pnlDebate debate panel}.
	 */
	private final CardLayout clDebate = new CardLayout();

	/**
	 * The {@code ListDataListener} used to update the "next speaker" label.
	 */
	private final ListDataListener ldlNextSpeakerUpdate;

	/**
	 * The main debate panel, showing either the speakers' list or current
	 * caucus, as well as the current speaker (if applicable).
	 */
	private final JPanel pnlDebate = new JPanel(clDebate);

	/**
	 * The model used in the speakers' list.
	 */
	private DelegateModel speakersModel = new DelegateModel(false);
	/**
	 * The speakers' list.
	 */
	private final JList lstSpeakers = new JList(speakersModel);
	{
		lstSpeakers.setCellRenderer(new LargeDelegateRenderer());
	}

	/**
	 * The progress of a speech on the general speakers' list.
	 */
	private final RadialImageProgressBar ripbSpeakersListProgress = new RadialImageProgressBar();

	/**
	 * The GSL panel.
	 */
	private final PublicDisplayGSLPanel gslPanel = new PublicDisplayGSLPanel(
			null);

	/**
	 * The scrollpane for the speakers' list.
	 */
	private final JScrollPane scpnSpeakersList = new JScrollPane(lstSpeakers,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	/**
	 * The label showing the topic of the moderated caucus.
	 */
	private final JLabel lblModeratedCaucusTopic = createCenteredLabel(Character
			.toString(' '));
	{
		lblModeratedCaucusTopic.setFont(lblModeratedCaucusTopic.getFont()
				.deriveFont(lblModeratedCaucusTopic.getFont().getSize() * 1.5f)
				.deriveFont(Font.ITALIC));
	}

	/**
	 * The time bar for the moderated caucus total time.
	 */
	private final TimeBar tbModeratedCaucusTotal = new TimeBar();

	/**
	 * The progress bar for the moderated caucus speaker.
	 */
	private final RadialImageProgressBar ripbModeratedCaucusProgress = new RadialImageProgressBar();

	/**
	 * The number of seconds elapsed in the moderated caucus speech.
	 */
	private int secondsElapsed = 0;

	/**
	 * The total number of seconds in the moderated caucus speech.
	 */
	private int totalSeconds = 0;

	/**
	 * The timer to update the moderated caucus speaker progress.
	 */
	private final Timer tModeratedCaucusSpeaker = new Timer(1000,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					ripbModeratedCaucusProgress
							.setPercentage((double) (++secondsElapsed)
									/ (double) (totalSeconds));
					if (secondsElapsed == totalSeconds) {
						ripbModeratedCaucusProgress.setImage(null);
					}
				}
			});

	/**
	 * The time bar used for the unmoderated caucus.
	 */
	private final TimeBar tbUnmoderatedCaucus = new TimeBar();

	/**
	 * The time bar used for the formal caucus.
	 */
	private final TimeBar tbFormalCaucus = new TimeBar();

	/**
	 * The document viewer for a formal caucus.
	 */
	private final DocumentView pdfFormalCaucus = new DocumentView();

	/**
	 * The list model for the {@linkplain #lstMotions motions list}.
	 */
	private DefaultListModel motionsModel = new DefaultListModel();

	/**
	 * The list of motions on the floor.
	 */
	private final JList lstMotions = new JList();
	{
		lstMotions.setCellRenderer(new BigMotionRenderer());
	}

	/**
	 * The panel used to display motion speeches.
	 */
	private final MotionSpeechPanel pnlMotionSpeech = new MotionSpeechPanel();

	/**
	 * The scrollpane for the {@linkplain #lstMotions motions list}.
	 */
	private final JScrollPane scpnMotionList = new JScrollPane(lstMotions,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	{
		scpnMotionList.setEnabled(false);
	}

	/**
	 * The wait index for the speakers' list.
	 */
	private int waitIndex_Speakers = 0;

	/**
	 * The wait index for the motions list.
	 */
	private int waitIndex_Motions = 0;

	/**
	 * The timer to automatically scroll the speakers' list, motions list, and
	 * list of resolutions.
	 */
	private Timer autoScroll = new Timer(40, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			// The time in seconds to wait at the top and bottom.
			final int waitTime = 5;

			{// Speakers' list
				JScrollBar jsb = scpnSpeakersList.getVerticalScrollBar();
				final int value = jsb.getValue();
				final int max = jsb.getMaximum() - jsb.getVisibleAmount();
				final int delayTicks = waitTime * 1000 / autoScroll.getDelay();
				if (!((value == 0 || value >= max) && (++waitIndex_Speakers < delayTicks))) {
					jsb.setValue(value + 1);
					waitIndex_Speakers = 0;
					if (value >= max) {
						jsb.setValue(0);
					}
				}
			}
			{// Motions list
				JScrollBar jsb = scpnMotionList.getVerticalScrollBar();
				final int value = jsb.getValue();
				final int max = jsb.getMaximum() - jsb.getVisibleAmount();
				final int delayTicks = waitTime * 1000 / autoScroll.getDelay();
				if (!((value == 0 || value >= max) && (++waitIndex_Motions < delayTicks))) {
					jsb.setValue(value + 1);
					waitIndex_Motions = 0;
					if (value >= max) {
						jsb.setValue(0);
					}
				}
			}
		}
	});

	/**
	 * The PDF view for the working paper and resolution mode.
	 */
	private final DocumentView dvWorkingPaperResolution = new DocumentView();

	/**
	 * The crisis label.
	 */
	private final JLabel lblCrisis;

	/**
	 * The index of the crisis pulse.
	 */
	private int crisisPulseIndex;

	/**
	 * The pulser for the crisis label.
	 */
	private final Timer tmCrisisPulse = new Timer(50, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			crisisPulseIndex = (++crisisPulseIndex) % 40;
			lblCrisis.setForeground(new Color((float) (Math
					.cos(crisisPulseIndex / 80d * Math.PI * 2d) / 2 + 0.5), 0f,
					0f));
		}
	});

	/**
	 * The crisis document view.
	 */
	private final DocumentView dvCrisis = new DocumentView();

	/**
	 * THe guest speaker card layout.
	 */
	private final CardLayout clGuestSpeaker = new CardLayout();

	/**
	 * The panel containing the guest speaker information, or a message
	 * indicating that there is none.
	 */
	private final JPanel pnlGuestSpeaker = new JPanel(clGuestSpeaker);

	/**
	 * The property with the guest speaker's name.
	 */
	private final TextProperty tpGuestSpeakerName = new TextProperty(
			Messages.getString("PublicDisplay.GuestSpeaker"), new String()); //$NON-NLS-1$

	/**
	 * The property with the guest speaker's occupation.
	 */
	private final TextProperty tpGuestSpeakerOccupation = new TextProperty(
			Messages.getString("PublicDisplay.Occupation"), new String()); //$NON-NLS-1$

	/**
	 * The card layout for the Q&A session of a crisis.
	 */
	private final CardLayout clQaSession = new CardLayout();

	/**
	 * The panel for the time bar of the Q&A session, or a message indicating
	 * that no questinos will be entertained.
	 */
	private final JPanel pnlQaSession = new JPanel(clQaSession);

	/**
	 * The time bar for the crisis.
	 */
	private final TimeBar tbCrisis = new TimeBar();

	/**
	 * Creates the display for the given committee.
	 * 
	 * @param committee
	 *            the committee to display
	 */
	public PublicDisplay(Committee committee) {
		super(committee == null || committee.getName() == null ? new String()
				: committee.getName());

		setContentPane(pnlContent);

		setGlassPane(msgDecorum);
		msgDecorum.setVisible(true);

		final CC ccTitle = new CC().wrap().growX().pushX();

		JPanel pnlGeneralSpeakersList = new JPanel(new GridLayout(1, 2));

		JPanel pnlSpeakersList = new JPanel(new MigLayout());
		pnlGeneralSpeakersList.add(pnlSpeakersList);

		pnlSpeakersList
				.add(createTitleLabel(Messages.getString("PublicDisplay.GSL")), ccTitle); //$NON-NLS-1$

		JPanel pnlNextSpeaker = new JPanel(new MigLayout());
		JLabel lblNextText = new JLabel(Messages.getString("PublicDisplay.NextSpeakerLabel")); //$NON-NLS-1$
		lblNextText.setFont(lblNextText.getFont().deriveFont(18f));
		lblNextText.setHorizontalAlignment(JLabel.TRAILING);
		pnlNextSpeaker.add(lblNextText, new CC());
		final JLabel lblNextValue = new JLabel();
		lblNextValue.setFont(lblNextValue.getFont().deriveFont(Font.BOLD)
				.deriveFont(18f));
		pnlNextSpeaker.add(lblNextValue, new CC().growX().pushX());
		pnlSpeakersList.add(pnlNextSpeaker, new CC().growX().pushX().wrap());

		ldlNextSpeakerUpdate = new ListDataListener() {
			{
				// initial trigger
				contentsChanged(null);
			}

			@Override
			public void intervalRemoved(ListDataEvent e) {
				contentsChanged(e);
			}

			@Override
			public void intervalAdded(ListDataEvent e) {
				contentsChanged(e);
			}

			@Override
			public void contentsChanged(ListDataEvent e) {
				if (speakersModel.getSize() == 0) {
					lblNextValue.setText(Messages.getString("PublicDisplay.NextSpeakerNoneText")); //$NON-NLS-1$
					lblNextValue.setIcon(null);
				} else {
					Delegate d = speakersModel.getElementAt(0);
					lblNextValue.setText(d.getName());
					lblNextValue.setIcon(d.getSmallIcon());
				}
			}
		};
		speakersModel.addListDataListener(ldlNextSpeakerUpdate);
		pnlSpeakersList.add(scpnSpeakersList, new CC().grow().pushY().wrap());
		// pnlSpeakersList.add(ripbSpeakersListProgress, new
		// CC().grow().pushX());
		pnlSpeakersList.add(gslPanel, new CC().grow().pushX());

		JPanel pnlMotionsOnFloor = new JPanel(new MigLayout());
		pnlGeneralSpeakersList.add(pnlMotionsOnFloor);
		pnlMotionsOnFloor.add(createTitleLabel(Messages
				.getString("PublicDisplay.MotionsOnFloor")), ccTitle); //$NON-NLS-1$
		pnlMotionsOnFloor.add(scpnMotionList, new CC().grow().pushY().wrap());
		pnlMotionsOnFloor.add(pnlMotionSpeech,
				new CC().grow().push().hideMode(3));
		pnlMotionSpeech.setVisible(false);

		JPanel pnlModeratedCaucus = new JPanel(new MigLayout());
		pnlModeratedCaucus.add(createTitleLabel(Messages
				.getString("PublicDisplay.ModeratedCaucus")), ccTitle); //$NON-NLS-1$
		pnlModeratedCaucus.add(lblModeratedCaucusTopic, new CC().growX()
				.pushX().wrap());
		pnlModeratedCaucus.add(tbModeratedCaucusTotal, new CC().growX().pushX()
				.wrap());
		pnlModeratedCaucus.add(ripbModeratedCaucusProgress, new CC().grow()
				.push());

		JPanel pnlUnmoderatedCaucus = new JPanel(new MigLayout());
		pnlUnmoderatedCaucus.add(createTitleLabel(Messages
				.getString("PublicDisplay.UnmoderatedCaucus")), //$NON-NLS-1$
				ccTitle);
		pnlUnmoderatedCaucus.add(tbUnmoderatedCaucus, new CC().grow().push());

		JPanel pnlVotingBloc = new JPanel(new GridLayout(3, 1));
		JPanel pnlVotingBlocContents = new JPanel(new MigLayout());

		pnlVotingBlocContents
				.add(createTitleLabel(Messages
						.getString("PublicDisplay.VotingBloc")), ccTitle); //$NON-NLS-1$
		pnlVotingBlocContents.add(createCenteredLabel(Messages
				.getString("PublicDisplay.DoorsClosed")), //$NON-NLS-1$
				ccTitle);
		pnlVotingBlocContents.add(createCenteredLabel(Messages
				.getString("PublicDisplay.NotesSuspended")), //$NON-NLS-1$
				ccTitle);

		pnlVotingBloc.add(Box.createVerticalGlue());
		pnlVotingBloc.add(pnlVotingBlocContents);
		pnlVotingBloc.add(Box.createVerticalGlue());

		JPanel pnlFormalCaucus = new JPanel(new MigLayout());
		pnlFormalCaucus.add(createTitleLabel(Messages
				.getString("PublicDisplay.FormalCaucus")), ccTitle); //$NON-NLS-1$
		pnlFormalCaucus.add(tbFormalCaucus, new CC().grow().pushX().wrap());
		pnlFormalCaucus.add(pdfFormalCaucus, new CC().grow().push());

		pnlDebate.add(pnlGeneralSpeakersList,
				CommitteeState.SPEAKERS_LIST.toString());
		pnlDebate.add(pnlModeratedCaucus,
				CommitteeState.MODERATED_CAUCUS.toString());
		pnlDebate.add(pnlUnmoderatedCaucus,
				CommitteeState.UNMODERATED_CAUCUS.toString());
		pnlDebate.add(pnlFormalCaucus, CommitteeState.FORMAL_CAUCUS.toString());
		pnlDebate.add(createTitleLabel(Messages
				.getString("PublicDisplay.CommitteeAdjourned")), //$NON-NLS-1$
				CommitteeState.ADJOURNED.toString());
		pnlDebate.add(pnlVotingBloc, CommitteeState.VOTING_BLOC.toString());

		final JPanel pnlWorkingPaperView = new JPanel(new MigLayout(new LC()));
		pnlWorkingPaperView.add(createTitleLabel(Messages
				.getString("PublicDisplay.DocumentViewer")), ccTitle); //$NON-NLS-1$
		pnlWorkingPaperView.add(dvWorkingPaperResolution, new CC().grow()
				.push());

		final JPanel pnlCrisisView = new JPanel(new MigLayout(new LC()));

		lblCrisis = createTitleLabel(Messages
				.getString("PublicDisplay.CrisisViewer")); //$NON-NLS-1$
		pnlCrisisView.add(lblCrisis, ccTitle);

		pnlCrisisView.add(dvCrisis, new CC().grow().push().wrap());

		pnlCrisisView.add(pnlGuestSpeaker, new CC().grow().push());

		JPanel pnlGuestSpeakerExists = new JPanel(new BorderLayout());
		pnlGuestSpeaker.add(pnlGuestSpeakerExists, Boolean.TRUE.toString());

		PropertySet psGuestSpeaker = new PropertySet();
		psGuestSpeaker.add(tpGuestSpeakerName);
		psGuestSpeaker.add(tpGuestSpeakerOccupation);
		pnlGuestSpeakerExists.add(new PropertyPanel(psGuestSpeaker, false,
				false), BorderLayout.CENTER);

		pnlGuestSpeakerExists.add(pnlQaSession, BorderLayout.SOUTH);

		pnlQaSession.add(tbCrisis, Boolean.TRUE.toString());
		pnlQaSession.add(createCenteredLabel(Messages
				.getString("PublicDisplay.NoQuestions")), //$NON-NLS-1$
				Boolean.FALSE.toString());

		pnlGuestSpeaker.add(createCenteredLabel(Messages
				.getString("PublicDisplay.NoGuestSpeaker")), //$NON-NLS-1$
				Boolean.FALSE.toString());

		pnlContent.add(pnlDebate, ViewState.STANDARD_DEBATE.toString());
		pnlContent.add(pnlWorkingPaperView, ViewState.WPR_VIEW.toString());
		pnlContent.add(pnlCrisisView, ViewState.CRISIS_VIEW.toString());

		autoScroll.start();
	}

	/**
	 * Creates a centered label with the given text.
	 * 
	 * @param text
	 *            the text to put on the label
	 * @return a label with both X and horizontal alignments at center
	 */
	private JLabel createCenteredLabel(String text) {
		JLabel label = new JLabel(text);
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	/**
	 * Creates a label with title formatting and the given title.
	 * 
	 * @param title
	 *            the label text
	 * @return the label
	 */
	private JLabel createTitleLabel(String title) {
		JLabel label = createCenteredLabel(title);
		label.setFont(label.getFont()
				.deriveFont(label.getFont().getSize() * 2f)
				.deriveFont(Font.BOLD));

		return label;
	}

	/**
	 * Flashes the "decorum" message in the public display panel.
	 */
	public void flashDecorum() {
		flashMessage(Messages.getString("PublicDisplay.DecorumMessage")); //$NON-NLS-1$
	}

	/**
	 * Flashes the given message to the public display panel.
	 * 
	 * @param message
	 *            the message to flash
	 */
	public void flashMessage(String message) {
		msgDecorum.setMessage(message);
		msgDecorum.flashMessage();
	}

	/**
	 * Pauses the current GSL speech.
	 */
	public void pauseSpeech() {
		gslPanel.pauseSpeech();
	}

	/**
	 * Sets the committee.
	 * 
	 * @param committee
	 *            the new committee
	 */
	public void setCommittee(Committee committee) {
		gslPanel.setCommittee(committee);
	}

	/**
	 * Sets the current state.
	 * 
	 * @param state
	 *            the new state
	 */
	public void setCommitteeState(CommitteeState state) {
		clDebate.show(pnlDebate, state.toString());
	}

	/**
	 * Sets the document displayed in the working paper/resolution display
	 * section.
	 * 
	 * @param paper
	 *            the paper to display
	 */
	public void setDocument(WorkingPaper paper) {
		dvWorkingPaperResolution.setDocument(paper);
	}

	/**
	 * Sets the model of the list of motions.
	 * 
	 * @param model
	 *            the model
	 */
	public void setMotionModel(DefaultListModel model) {
		motionsModel = model;
		lstMotions.setModel(this.motionsModel);
	}

	/**
	 * Sets the model of the list of speakers.
	 * 
	 * @param model
	 *            the model
	 */
	public void setSpeakersModel(DelegateModel model) {
		if (speakersModel != null) {
			speakersModel.removeListDataListener(ldlNextSpeakerUpdate);
		}
		speakersModel = model;
		if (speakersModel != null) {
			speakersModel.addListDataListener(ldlNextSpeakerUpdate);
		}
		lstSpeakers.setModel(this.speakersModel);
	}

	/**
	 * Sets the {@link ViewState}.
	 * 
	 * @param state
	 *            the new view state
	 */
	public void setViewState(ViewState state) {
		clMain.show(pnlContent, state.toString());
	}

	@Override
	public void speechActionPerformed(SpeechEvent se) {
		gslPanel.speechActionPerformed(se);
	}

	/**
	 * Starts a moderated caucus speech by the given speaker for the given
	 * amount of time.
	 * 
	 * @param speaker
	 *            the speaker
	 * @param speakingTime
	 *            the time
	 */
	public void startCaucusSpeech(Image speaker, int speakingTime) {
		tModeratedCaucusSpeaker.stop();
		secondsElapsed = 0;
		totalSeconds = speakingTime;
		tModeratedCaucusSpeaker.start();
		ripbModeratedCaucusProgress.setPercentage(0);
		ripbModeratedCaucusProgress.setImage(speaker);
	}

	/**
	 * Starts the given crisis.
	 * 
	 * @param crisis
	 *            the crisis to start
	 */
	public void startCrisis(Crisis crisis) {
		tmCrisisPulse.start();
		dvCrisis.setDocument(crisis);
		if (crisis.isGuestSpeaker()) {
			tpGuestSpeakerName.setValue(crisis.getGuestSpeakerName());
			tpGuestSpeakerOccupation.setValue(crisis
					.getGuestSpeakerOccupation());
			if (crisis.getQaTime() != null) {
				tbCrisis.setString(Messages
						.getString("PublicDisplay.QANotStarted")); //$NON-NLS-1$
				tbCrisis.setStringPainted(true);
				clQaSession.show(pnlQaSession, Boolean.TRUE.toString());
			} else {
				clQaSession.show(pnlQaSession, Boolean.FALSE.toString());
			}
			clGuestSpeaker.show(pnlGuestSpeaker, Boolean.TRUE.toString());
		} else {
			clGuestSpeaker.show(pnlGuestSpeaker, Boolean.FALSE.toString());
		}
	}

	/**
	 * Starts a formal caucus.
	 * 
	 * @param paper
	 *            the working paper
	 * @param totalTime
	 *            the total time
	 */
	public void startFormalCaucus(WorkingPaper paper, Time totalTime) {
		tbFormalCaucus.start(totalTime);
		if (paper != null) {
			pdfFormalCaucus.setDocument(paper);
		}
	}

	/**
	 * Updates the moderated caucus panel to match the current caucus.
	 * 
	 * @param topic
	 *            the topic of the caucus
	 * @param totalTime
	 *            the total time allotted for the caucus
	 */
	public void startModeratedCaucus(String topic, Time totalTime) {
		lblModeratedCaucusTopic.setText(topic);
		tbModeratedCaucusTotal.start(totalTime);
	}

	/**
	 * Starts the Q&A session with the given time.
	 * 
	 * @param qaTime
	 *            the total time for the Q&A session
	 */
	public void startQaSession(Time qaTime) {
		tbCrisis.start(qaTime);
	}

	/**
	 * Starts a GSL speech for the given delegate.
	 * 
	 * @param delegate
	 *            the delegate speaking
	 * @param speakingTime
	 *            the speaking time
	 */
	public void startSpeech(Delegate delegate, Time speakingTime) {
		gslPanel.startSpeech(delegate, speakingTime);
	}

	/**
	 * Starts an unmoderated caucus for the given amount of time.
	 * 
	 * @param totalTime
	 *            the amount of time
	 */
	public void startUnmoderatedCaucus(Time totalTime) {
		tbUnmoderatedCaucus.start(totalTime);
	}

	/**
	 * Stops the current caucus moderated speech.
	 */
	public void stopCaucusSpeech() {
		tModeratedCaucusSpeaker.stop();
		ripbModeratedCaucusProgress.setImage(null);
	}

	/**
	 * Pauses or resumes the current moderated caucus speech.
	 */
	public void pauseCaucusSpeech() {
		if (tModeratedCaucusSpeaker.isRunning()) {
			tModeratedCaucusSpeaker.stop();
		} else {
			tModeratedCaucusSpeaker.start();
		}
	}

	/**
	 * Pauses or resumes the current moderated caucus.
	 */
	public void pauseModeratedCaucus() {
		if (tbModeratedCaucusTotal.isRunning()) {
			tbModeratedCaucusTotal.pause();
		} else {
			tbModeratedCaucusTotal.resume();
		}
	}

	/**
	 * Stops the current unmoderated caucus, if any.
	 */
	public void stopUnmoderatedCaucus() {
		tbUnmoderatedCaucus.stop();
	}

	/**
	 * Sets the speakers' list to the new list.
	 * 
	 * @param list
	 *            the new list
	 */
	public void updateSpeakersList(List<Delegate> list) {
		speakersModel.setContents(list);
	}

	/**
	 * Updates the speakers' list progress indicator.
	 * 
	 * @param image
	 *            the flag of the speaker
	 * @param percentage
	 *            the completion percentage
	 */
	public void updateSpeakersListProgress(Image image, double percentage) {
		ripbSpeakersListProgress.setImage(image);
		ripbSpeakersListProgress.setPercentage(percentage);
	}

	@Override
	public void yield(YieldEvent ye) {
		gslPanel.yield(ye);
	}

	@Override
	public void yieldActionPerformed(YieldActionEvent yae) {
		gslPanel.yieldActionPerformed(yae);
	}

	public void speechAgainstMotion(SpeechEvent se) {
		pnlMotionSpeech.setVisible(true);
		pnlMotionSpeech.speechAgainst(se);
	}

	public void speechInFavorOfMotion(SpeechEvent se) {
		pnlMotionSpeech.setVisible(true);
		pnlMotionSpeech.speechInFavor(se);
	}

	public void clearMotionSpeeches() {
		pnlMotionSpeech.setVisible(false);
		pnlMotionSpeech.clear();
	}

}