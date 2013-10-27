package org.lcmmun.kiosk.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.CommitteeState;
import org.lcmmun.kiosk.Crisis;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.QuorumStatus;
import org.lcmmun.kiosk.Speech.YieldType;
import org.lcmmun.kiosk.Vote;
import org.lcmmun.kiosk.WorkingPaper;
import org.lcmmun.kiosk.Yield;
import org.lcmmun.kiosk.broadcast.CommitteeOutline;
import org.lcmmun.kiosk.broadcast.Datum;
import org.lcmmun.kiosk.broadcast.DatumFactory;
import org.lcmmun.kiosk.broadcast.DatumServer;
import org.lcmmun.kiosk.gui.events.CaucusEvent;
import org.lcmmun.kiosk.gui.events.CaucusListener;
import org.lcmmun.kiosk.gui.events.EditingFinishedEvent;
import org.lcmmun.kiosk.gui.events.EditingFinishedListener;
import org.lcmmun.kiosk.gui.events.MotionPassedEvent;
import org.lcmmun.kiosk.gui.events.MotionPassedListener;
import org.lcmmun.kiosk.gui.events.SpeechEvent;
import org.lcmmun.kiosk.gui.events.SpeechEvent.SpeechEventType;
import org.lcmmun.kiosk.gui.events.SpeechListener;
import org.lcmmun.kiosk.gui.events.YieldActionEvent;
import org.lcmmun.kiosk.gui.events.YieldActionEvent.CommentActionType;
import org.lcmmun.kiosk.gui.events.YieldActionListener;
import org.lcmmun.kiosk.gui.events.YieldEvent;
import org.lcmmun.kiosk.gui.events.YieldListener;
import org.lcmmun.kiosk.motions.AgendaMotion;
import org.lcmmun.kiosk.motions.CloseDebateMotion;
import org.lcmmun.kiosk.motions.DivisionOfTheQuestionMotion;
import org.lcmmun.kiosk.motions.FormalCaucusMotion;
import org.lcmmun.kiosk.motions.IntroduceWorkingPaperMotion;
import org.lcmmun.kiosk.motions.MajorityType;
import org.lcmmun.kiosk.motions.ModeratedCaucusMotion;
import org.lcmmun.kiosk.motions.Motion;
import org.lcmmun.kiosk.motions.Motion.Debatability;
import org.lcmmun.kiosk.motions.PrecedenceComparator;
import org.lcmmun.kiosk.motions.RollCallVoteMotion;
import org.lcmmun.kiosk.motions.SpeakingTimeMotion;
import org.lcmmun.kiosk.motions.TableDebateMotion;
import org.lcmmun.kiosk.motions.UnmoderatedCaucusMotion;
import org.lcmmun.kiosk.resources.CreditsMessages;
import org.lcmmun.kiosk.resources.ImageFetcher;
import org.lcmmun.kiosk.resources.ImageType;
import org.lcmmun.kiosk.speechanalysis.AnalysisServer;

import tools.customizable.CounterProperty;
import tools.customizable.EditAction;
import tools.customizable.LimitedTextProperty;
import tools.customizable.LongTextProperty;
import tools.customizable.MessageProperty;
import tools.customizable.MultipleChoiceProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TextProperty;
import tools.customizable.Time;
import tools.customizable.TimeProperty;
import tools.customizable.TrueFalseProperty;
import util.LAFOptimizer;

/**
 * The main class for the Kiosk application.
 * 
 * @author William Chargin
 * 
 */
@Version("3.1")
public class Kiosk extends JFrame {

	/**
	 * A generator for a motion of a given type. Used by menu items.
	 * 
	 * @author William Chargin
	 * 
	 * @param <T>
	 *            the motion type to generate
	 */
	private interface MotionGenerator<T extends Motion> {
		/**
		 * Generates a motion of the specified type.
		 * 
		 * @return a motion
		 */
		public T generateMotion();
	}

	/**
	 * A proposed motion.
	 * 
	 * @author William Chargin
	 * 
	 * @param <T>
	 *            the type of motion proposed
	 */
	public static class ProposedMotion<T extends Motion> implements
			Comparable<ProposedMotion<T>> {

		/**
		 * The comparator used for {@link #compareTo(ProposedMotion)}.
		 */
		private static final PrecedenceComparator COMPARATOR = new PrecedenceComparator();

		/**
		 * The motion that has been proposed.
		 */
		public final T motion;

		/**
		 * The action that should be taken, should this motion pass.
		 */
		public final MotionPassedListener<T> ifPassed;

		/**
		 * Creates the proposed motion, given the motion and the action.
		 * 
		 * @param motion
		 *            the motion
		 * @param ifPassed
		 *            the action
		 */
		public ProposedMotion(T motion, MotionPassedListener<T> ifPassed) {
			super();
			this.motion = motion;
			this.ifPassed = ifPassed;
		}

		@Override
		public int compareTo(ProposedMotion<T> other) {
			return COMPARATOR.compare(this.motion, other.motion);
		}

	}

	/**
	 * The text for OK buttons.
	 */
	private static final String OK_TEXT = Messages
			.getString("Kiosk.OKButtonText"); //$NON-NLS-1$

	/**
	 * The text for cancel buttons.
	 */
	private static final String CANCEL_TEXT = Messages
			.getString("Kiosk.CancelButtonText"); //$NON-NLS-1$

	/**
	 * Whether this application is running on Mac.
	 */
	private static final boolean ON_MAC = System.getProperty("os.name") //$NON-NLS-1$
			.toLowerCase().startsWith("mac os x"); //$NON-NLS-1$

	/**
	 * The port used to broadcast to OmniKiosks ({@code 5266} = {@code LCMO} =
	 * <strong>L</strong>eague of <strong>C</strong>reative
	 * <strong>M</strong>inds <strong>O</strong>mniKiosk).
	 */
	public static final int OMNIKIOSK_BROADCAST_PORT = 5266;

	/**
	 * The number of bytes in a discovery packet.
	 */
	public static final int OMNIKIOSK_BROADCAST_LENGTH = 1024 * 64;

	/**
	 * The file name extension for saved committee sessions.
	 */
	private static final String FILE_NAME_EXTENSION = "mun"; //$NON-NLS-1$

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The key used with the Preferences API to determine the last save/load
	 * location.
	 */
	private static final String LAST_ACCESS_KEY = "LAST_ACCESS"; //$NON-NLS-1$

	/**
	 * The key used with the Preferences API to determine the autosave interval.
	 */
	private static final String AUTOSAVE_INTERVAL_KEY = "AUTOSAVE_INTERVAL"; //$NON-NLS-1$

	/**
	 * The key used with the Preferences API to determine the survey preference.
	 * The possibilities are as follows:
	 * <p>
	 * <ul>
	 * <li><strong>{@code null}</strong> : the user has already taken the
	 * survey, or has asked not to be reminded again</li>
	 * <li><strong>a {@link Date} object</strong> : the user should be asked to
	 * take the survey on or after this date</li>
	 * </ul>
	 */
	private static final String SURVEY_KEY = "SURVEY_KEY"; //$NON-NLS-1$

	/**
	 * The key used with the Preferences API to determine if the user has taken
	 * the survey. The related value is a boolean.
	 */
	private static final String SURVEY_TAKEN_KEY = "SURVEY_TAKEN_KEY"; //$NON-NLS-1$

	/**
	 * The property set containing the about dialog information.
	 */
	private static final PropertySet aboutDialogPropertySet = new PropertySet();
	static {
		new Thread(new Runnable() {
			@SuppressWarnings("serial")
			@Override
			public void run() {
				final Version versionAnnotation = Kiosk.class
						.getAnnotation(Version.class);
				final String javaVersion = System.getProperty("java.version"); //$NON-NLS-1$
				boolean separator = false;
				if (versionAnnotation != null) {
					final MessageProperty mpVersion = new MessageProperty(
							Messages.getString("Kiosk.PropertyKioskVersion"), versionAnnotation.value()); //$NON-NLS-1$
					aboutDialogPropertySet.add(mpVersion);
					separator = true;
				}
				if (javaVersion != null && !javaVersion.isEmpty()) {
					final MessageProperty mpJavaVersion = new MessageProperty(
							Messages.getString("Kiosk.PropertyJavaVersion"), javaVersion.trim()); //$NON-NLS-1$
					aboutDialogPropertySet.add(mpJavaVersion);
					separator = true;
				}
				if (separator) {
					aboutDialogPropertySet.add(null);
				}
				// Eclipse (at time of writing, Juno) can't differentiate
				// between Messages and CreditsMessages and complains that
				// "Count" isn't in the messages.properties file.
				//
				// Workaround: don't specify a string literal in the call to
				// CreditsMessages.getString().
				final String countPropertyName = "Count";//$NON-NLS-1$
				int count = Integer.parseInt(CreditsMessages
						.getString(countPropertyName));
				Enumeration<String> keysenum = CreditsMessages.getKeys();
				ArrayList<String> keys = new ArrayList<String>();
				while (keysenum.hasMoreElements()) {
					keys.add(keysenum.nextElement());
				}
				for (int i = 0; i < count; i++) {
					// .name, .value, .url
					final int num = i + 1;
					final String NAME_PREFIX = ".Name"; //$NON-NLS-1$
					final String VALUE_PREFIX = ".Value"; //$NON-NLS-1$
					final String URL_PREFIX = ".Url"; //$NON-NLS-1$
					if (keys.contains(num + NAME_PREFIX)
							&& keys.contains(num + VALUE_PREFIX)) {
						MessageProperty mp = new MessageProperty(
								CreditsMessages.getString(num + NAME_PREFIX),
								CreditsMessages.getString(num + VALUE_PREFIX));
						aboutDialogPropertySet.add(mp);
						if (keys.contains(num + URL_PREFIX)
								&& Desktop.isDesktopSupported()) {
							mp.setEditability(EditAction.ACTION);
							mp.setEnabled(true);
							final Desktop dt = Desktop.getDesktop();
							final URI uri = URI.create(CreditsMessages
									.getString(num + URL_PREFIX));
							mp.setAction(new AbstractAction() {
								@Override
								public void actionPerformed(ActionEvent ae) {
									try {
										dt.browse(uri);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							});
						}
					} else {
						aboutDialogPropertySet.add(null);
					}
				}
			}
		}).start();
	}

	/**
	 * Configures the splash screen for use.
	 */
	private static void configureSplashScreen() {
		splash = SplashScreen.getSplashScreen();
		if (splash == null) {
			// Unsupported.
			return;
		}
		splashGraphics = splash.createGraphics();
		setSplashScreenText(new String());
	}

	/**
	 * Starts the Kiosk application.
	 * 
	 * @param args
	 *            the command-line arguments; the first argument, if present,
	 *            will be treated as a filepath to open
	 */
	public static void main(String[] args) {
		LAFOptimizer.optimizeSwing();

		System.setProperty("java.net.useSystemProxies", Boolean.toString(true)); //$NON-NLS-1$

		configureSplashScreen();
		Kiosk kiosk = new Kiosk();
		if (args.length > 0) {
			String path = args[0].trim();
			if (!path.isEmpty()) {
				File file = new File(path);
				if (file.exists() && !file.isDirectory() && file.canRead()) {
					kiosk.loadCommittee(file);
				}
			}
		}
		kiosk.pack();
		kiosk.setLocationRelativeTo(null);
		kiosk.setVisible(true);
		kiosk.toFront();
	}

	private synchronized static void setSplashScreenText(String message) {
		if (splashGraphics == null) {
			configureSplashScreen();
		}
		splashGraphics.setComposite(AlphaComposite.Clear);
		splashGraphics.setPaintMode();
		Image image = ImageFetcher.fetchImage(ImageType.SPLASH);
		if (image != null) {
			splashGraphics.drawImage(image, 0, 0, image.getWidth(null),
					image.getHeight(null), null);
			splashGraphics.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
			splashGraphics.setColor(Color.BLACK);
			splashGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			String text = Messages.getString("Kiosk.SplashLoadingText"); //$NON-NLS-1$
			FontMetrics fm = splashGraphics.getFontMetrics();
			int y = 215;
			int x = 160 - (fm.stringWidth(text) / 2);
			splashGraphics.drawString(text, x, y);

			splashGraphics.setFont(splashGraphics.getFont().deriveFont(12f));
			fm = splashGraphics.getFontMetrics();
			y = 4 + fm.getAscent();
			x = 6;
			splashGraphics.drawString(message, x, y);
		}
		splash.update();
	}

	/**
	 * The committee in progress.
	 */
	private Committee committee;

	/**
	 * The datum server in use.
	 */
	private DatumServer datumServer;

	/**
	 * The analysis server in use.
	 */
	private AnalysisServer analysisServer;

	/**
	 * The timer that broadcasts a message to omnikiosks indicating that this
	 * kiosk is present.
	 */
	private Timer omniDiscoveryTimer = new Timer(1000 * 5,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					broadcastToOmniKiosks();
				}
			});
	{
		omniDiscoveryTimer.setInitialDelay(0);
	}

	/**
	 * The speakers' list panel in the main window.
	 */
	private SpeakersListPanel speakersListPanel = new SpeakersListPanel(null,
			null);

	/**
	 * The panel showing the current speech on the General Speakers' List.
	 */
	private SpeechPanel speechPanel = new SpeechPanel(true, new Committee());

	/**
	 * The card layout for the various modes.
	 */
	private final CardLayout cardLayout = new CardLayout();

	/**
	 * The panel showing the card layout.
	 */
	private final JPanel deck = new JPanel(cardLayout);

	/**
	 * The panel showing the General Speakers' List.
	 */
	private final JPanel pnlSpeakersList;

	/**
	 * The panel for moderated caucuses.
	 */
	private final ModeratedCaucusPanel moderatedCaucusPanel = new ModeratedCaucusPanel();

	/**
	 * The panel for unmoderated caucuses.
	 */
	private final UnmoderatedCaucusPanel unmoderatedCaucusPanel;

	/**
	 * The list of motions.
	 */
	private final JList lstMotions;

	/**
	 * The model for the list of motions.
	 */
	private final DefaultListModel motionModel = new DefaultListModel();

	/**
	 * The public display.
	 */
	private final transient PublicDisplay publicDisplay = new PublicDisplay(
			null);
	{
		publicDisplay.setSpeakersModel(speakersListPanel.getModel());
		publicDisplay.setMotionModel(motionModel);
		publicDisplay
				.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
	}

	/**
	 * The list of motions on the floor.
	 */
	private final ArrayList<ProposedMotion<? extends Motion>> floor = new ArrayList<ProposedMotion<? extends Motion>>();

	/**
	 * The property representing the current topic. The property won't modify
	 * the current topic; only the other way around.
	 */
	private final TextProperty tpTopic;

	/**
	 * The property representing the current simple majority, qualified
	 * majority, and delegate count.
	 */
	private final TextProperty tpQuorum;

	/**
	 * The property representing the current speaking time on the General
	 * Speakers' List.
	 */
	private final TimeProperty tpSpeakingTime;

	/**
	 * Whether this should broadcast to omni kiosks.
	 */
	protected boolean broadcastToOmniKiosks;

	/**
	 * The property containing the committee comments.
	 */
	private LimitedTextProperty ltpComments;

	/**
	 * The button allowing for processing of the motions on the floor (same as
	 * the menu item).
	 */
	private final JButton btnProcess;

	/**
	 * Whether autosave is enabled.
	 */
	private boolean autosaveEnabled;

	/**
	 * The autosave interval.
	 */
	private Time autosaveInterval = new Time(0, 10, 0);

	/**
	 * The autosave timer.
	 */
	private final Timer tmAutosave = new Timer(0, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (beginSave(true)) {
				// File save completed.
				tmAutosaveOverlay.start();
			}
		}
	});

	/**
	 * The autosave overlay index.
	 */
	private int autosaveOverlayIndex = -1;

	/**
	 * The timer for the autosave overlay.
	 */
	private final Timer tmAutosaveOverlay = new Timer(25, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (++autosaveOverlayIndex > 80) {
				tmAutosaveOverlay.stop();
				autosaveOverlayIndex = -1;
			}
			repaint();
			getGlassPane().repaint();
		}
	});

	/**
	 * Whether the server has been started.
	 */
	private boolean serverStarted = false;

	/**
	 * The formal caucus panel.
	 */
	private final FormalCaucusPanel fcp = new FormalCaucusPanel();

	/**
	 * The image icon used instead of flags for delegates without a flag.
	 */
	private static final transient ImageIcon ICON_SUBSTITUTE;

	static {
		BufferedImage bi = new BufferedImage(256, 256,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int stroke = 4;
		g2d.setStroke(new BasicStroke(stroke));
		Ellipse2D circle = new Ellipse2D.Double(stroke / 2, stroke / 3,
				bi.getWidth() - stroke, bi.getHeight() - stroke);
		g2d.setPaint(new RadialGradientPaint(new Point(bi.getWidth() / 2, bi
				.getHeight() / 2), bi.getWidth() / 2, new float[] { 0f, 1f },
				new Color[] { Color.WHITE, Color.LIGHT_GRAY }));
		g2d.fill(circle);
		g2d.setColor(Color.BLACK);
		g2d.draw(circle);
		ICON_SUBSTITUTE = new ImageIcon(bi);
	}

	/**
	 * The {@link Runnable} to update enabled statuses.
	 */
	private final Runnable rUpdateEnabledStatuses;

	/**
	 * The label displaying the number of working papers and draft resolutions
	 * in the {@link ViewState#WPR_VIEW} state.
	 */
	private final JLabel lblWorkingPaperResolutionCount = new JLabel();

	/**
	 * The multi crisis editor.
	 */
	private final MultiCrisisEditor multiCrisisEditor = new MultiCrisisEditor(
			null) {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void deploy(Crisis crisis) {
			deployCrisis(crisis);
			multiCrisisEditor.updateCrisisController(crisis);
		}
	};

	/**
	 * The splash screen being used.
	 */
	private static SplashScreen splash;

	/**
	 * The graphics object for the splash screen.
	 */
	private static Graphics2D splashGraphics;

	@SuppressWarnings("serial")
	// Suppress warnings because of anonymous AbstractActions
	public Kiosk() {
		super(Messages.getString("Kiosk.KioskFrameTitle")); //$NON-NLS-1$
		setSplashScreenText(Messages.getString("Kiosk.SSMSettingUp")); //$NON-NLS-1$

		new Thread(new Runnable() {
			@Override
			public void run() {
				performSurveyCheck();
			}
		}).start();

		// Try to load, but don't complain if it fails.
		setSplashScreenText(Messages.getString("Kiosk.SSMLookingForCommittee")); //$NON-NLS-1$
		beginLoad(false);
		if (committee == null) {
			// Failed.
			setSplashScreenText(Messages
					.getString("Kiosk.SSMCreatingCommittee")); //$NON-NLS-1$
			committee = new Committee();
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent ce) {
					if (committee.equals(new Committee())) {
						// The command-line arguments haven't set it
						startCommitteeWizard();
						removeComponentListener(this);
					}
				}
			});
		} else {
			setSplashScreenText(Messages.getString("Kiosk.SSMCommitteeLoaded")); //$NON-NLS-1$
		}
		// Will be updated at end of constructor (among others).

		final Toolkit tk = Toolkit.getDefaultToolkit();

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				performExit();
			}
		});

		List<Image> images = new ArrayList<Image>();
		for (int dim : new int[] { 16, 24, 32, 48, 64, 128, 256 }) {
			try {
				final BufferedImage image = ImageIO.read(Kiosk.class
						.getResource("/org/lcmmun/kiosk/icon/" + dim + ".png"));//$NON-NLS-1$ //$NON-NLS-2$
				if (image != null) {
					images.add(image);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setIconImages(images);

		publicDisplay.setIconImage(getIconImage());

		{
			// Configure glass pane.
			final String autosaveText = Messages
					.getString("Kiosk.AutosavedText"); //$NON-NLS-1$
			JComponent glassPane = new JComponent() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					if (autosaveOverlayIndex == -1) {
						return;
					}
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 48f));
					FontMetrics fm = g2d.getFontMetrics();
					final int padding = 16;
					final int width = fm.stringWidth(autosaveText);
					final int height = fm.getAscent();
					RoundRectangle2D rect = new RoundRectangle2D.Double(
							(getWidth() - width) / 2 - padding,
							(getHeight() - height) / 3 - padding, width
									+ padding * 2, height + padding * 2,
							padding, padding);
					float alpha = (autosaveOverlayIndex < 40 ? 1f
							: 1f - (autosaveOverlayIndex - 40f) / 40f);
					g2d.setColor(new Color(0f, 0f, 0f, alpha / 2));
					g2d.fill(rect);
					g2d.setColor(new Color(1f, 1f, 1f, alpha));
					g2d.drawString(autosaveText, (getWidth() - width) / 2,
							(int) (getHeight() + height * 1.5) / 3);
				}
			};
			setGlassPane(glassPane);
			glassPane.setVisible(true);
		}

		setSplashScreenText(Messages.getString("Kiosk.SSMConfiguringMenus")); //$NON-NLS-1$
		{
			// Configure menus.
			JMenuBar menuBar = new JMenuBar();
			setJMenuBar(menuBar);

			ArrayList<Character> usedMnemonics = new ArrayList<Character>();

			JMenu mnFile = new JMenu(Messages.getString("Kiosk.MnFile")); //$NON-NLS-1$
			menuBar.add(mnFile);
			setMnemonic(mnFile, usedMnemonics);

			JMenuItem miNewCommittee = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiFileNew"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.NEW)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					moderatedCaucusPanel.stopCaucus();
					unmoderatedCaucusPanel.stopCaucus();
					speechPanel.stopSpeech();
					setCommittee(new Committee());
					clearLastAccess();
				}
			});
			mnFile.add(miNewCommittee);
			miNewCommittee.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
					tk.getMenuShortcutKeyMask()));

			JMenuItem miNewCommitteeWizard = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiNewCommitteeWizard"), ImageFetcher.fetchImageIcon(ImageType.WIZARD)) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							if (!committee.equals(new Committee())) {
								switch (JOptionPane.showConfirmDialog(
										Kiosk.this,
										Messages.getString("Kiosk.CreateNewCommitteePrompt"), //$NON-NLS-1$
										Messages.getString("Kiosk.CreateNewCommitteeTitle"), //$NON-NLS-1$
										JOptionPane.YES_NO_CANCEL_OPTION)) {
								case JOptionPane.YES_OPTION:
									committee = new Committee();
									break;
								case JOptionPane.NO_OPTION:
									break;
								case JOptionPane.CANCEL_OPTION:
								default:
									return;
								}
							}
							startCommitteeWizard();
						}
					});
			mnFile.add(miNewCommitteeWizard);

			JMenuItem miLoadSession = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiFileLoad"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.LOAD)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					loadFromUserFile(true);
				}
			});
			mnFile.add(miLoadSession);
			miLoadSession.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					tk.getMenuShortcutKeyMask()));
			setMnemonic(miLoadSession, 'O');

			JMenuItem miSaveSession = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiFileSave"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.SAVE)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					beginSave(true);
				}
			});
			mnFile.add(miSaveSession);
			miSaveSession.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					tk.getMenuShortcutKeyMask()));
			setMnemonic(miSaveSession, 'S');

			JMenuItem miSaveSessionAs = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiFileSaveAs"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.SAVE_AS)) {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveToUserFile(true);
				}
			});
			mnFile.add(miSaveSessionAs);
			miSaveSessionAs.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_S, tk.getMenuShortcutKeyMask()
							| KeyEvent.SHIFT_DOWN_MASK));
			setMnemonic(miSaveSessionAs, 'A');

			JMenuItem miAutosave = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiFileAutosave"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.AUTOSAVE)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					configureAutosave();
				}
			});
			mnFile.add(miAutosave);
			setMnemonic(miAutosave, 'U');

			Preferences node = Preferences.userNodeForPackage(Kiosk.class);
			int interval = node.getInt(AUTOSAVE_INTERVAL_KEY, -1);
			if (interval == -1) {
				autosaveEnabled = false;
			} else {
				autosaveEnabled = true;
				autosaveInterval = Time.fromSeconds(interval);
				tmAutosave.setDelay(1000 * interval);
				tmAutosave.setInitialDelay(tmAutosave.getDelay());
				tmAutosave.start();
			}

			mnFile.add(new JSeparator());

			JMenuItem miExit = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiFileExit"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.EXIT)) {

				@Override
				public void actionPerformed(ActionEvent ae) {
					performExit();
				}
			});
			mnFile.add(miExit);
			setMnemonic(miExit, 'X');

			JMenu mnView = new JMenu(Messages.getString("Kiosk.MnView")); //$NON-NLS-1$
			menuBar.add(mnView);
			setMnemonic(mnView, usedMnemonics);

			final JMenuItem miShowHidePublicPanel = new JMenuItem(
					new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent ae) {
							publicDisplay.setVisible(!publicDisplay.isVisible());
						}
					});
			mnView.add(miShowHidePublicPanel);
			miShowHidePublicPanel.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_F6, 0));

			final JMenuItem miFlashDecorum = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiViewFlashDecorum")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							publicDisplay.flashDecorum();
						}
					});
			mnView.add(miFlashDecorum);

			final JMenuItem miFlashMessage = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiViewFlashMessage")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							String result = JOptionPane.showInputDialog(
									Kiosk.this,
									Messages.getString("Kiosk.FlashMessagePrompt"), Messages.getString("Kiosk.FlashMessageTitle"), JOptionPane.OK_CANCEL_OPTION); //$NON-NLS-1$ //$NON-NLS-2$
							if (result != null) {
								publicDisplay.flashMessage(result);
							}
						}
					});
			mnView.add(miFlashMessage);

			mnView.add(new JSeparator());

			final ButtonGroup bgViewStates = new ButtonGroup();
			for (final ViewState state : ViewState.values()) {
				JRadioButtonMenuItem jrbmiState = new JRadioButtonMenuItem(
						new AbstractAction(state.name) {
							@Override
							public void actionPerformed(ActionEvent ae) {
								updateViewState(state);
							}
						});
				bgViewStates.add(jrbmiState);
				if (state.ordinal() == 0) {
					jrbmiState.setSelected(true);
				}
				mnView.add(jrbmiState);
			}

			mnView.addMenuListener(new MenuListener() {
				@Override
				public void menuCanceled(MenuEvent me) {
				}

				@Override
				public void menuDeselected(MenuEvent me) {
				}

				@Override
				public void menuSelected(MenuEvent me) {
					miShowHidePublicPanel.setText((publicDisplay.isVisible() ? Messages
							.getString("Kiosk.MiViewHidePrefix") //$NON-NLS-1$
							: Messages.getString("Kiosk.MiViewShowPrefix")) //$NON-NLS-1$
							+ Messages
									.getString("Kiosk.MiViewPublicDisplaySuffix")); //$NON-NLS-1$
				}
			});

			JMenu mnBroadcast = new JMenu(
					Messages.getString("Kiosk.MnBroadcast")); //$NON-NLS-1$
			menuBar.add(mnBroadcast);
			setMnemonic(mnBroadcast, usedMnemonics);

			final JMenuItem miStartStopServer = new JMenuItem(
					new AbstractAction(null,
							ImageFetcher.fetchImageIcon(ImageType.BROADCAST)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							if (!serverStarted) {
								datumServer = new DatumServer();
								analysisServer = new AnalysisServer();
								analysisServer.setCommitteeName(committee
										.getName());
								moderatedCaucusPanel.analysisServer = analysisServer;
								broadcastToOmniKiosks = true;
								broadcastToOmniKiosks();
								omniDiscoveryTimer.start();
							} else {
								datumServer.shutdown();
								analysisServer.shutdown();
								datumServer = null;
								analysisServer = null;
								moderatedCaucusPanel.analysisServer = analysisServer;
								omniDiscoveryTimer.stop();
								broadcastToOmniKiosks = false;
							}
							serverStarted = !serverStarted;
						}
					});
			mnBroadcast.add(miStartStopServer);

			JMenuItem miServerInfo = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiBroadcastServerInfo"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.BROADCAST_INFO)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					displayServerInfo();
				}
			});
			mnBroadcast.add(miServerInfo);

			final JMenuItem miManualMessage = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiBroadcastManualMessage"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.MANUAL_MESSAGE)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					String message = JOptionPane.showInputDialog(Kiosk.this,
							Messages.getString("Kiosk.ManualMessagePrompt")); //$NON-NLS-1$
					if (message != null
							&& !(message = message.trim()).isEmpty()) {
						pushDatum(new Datum(null, message));
					}
				}
			});
			mnBroadcast.add(miManualMessage);

			mnBroadcast.addMenuListener(new MenuListener() {
				@Override
				public void menuCanceled(MenuEvent e) {
				}

				@Override
				public void menuDeselected(MenuEvent e) {
				}

				@Override
				public void menuSelected(MenuEvent e) {
					miManualMessage.setEnabled(datumServer != null);
					miStartStopServer.setText(datumServer == null ? Messages
							.getString("Kiosk.MiBroadcastStart") //$NON-NLS-1$
							: Messages.getString("Kiosk.MiBroadcastStop")); //$NON-NLS-1$
				}
			});

			JMenu mnCommittee = new JMenu(
					Messages.getString("Kiosk.MnCommittee")); //$NON-NLS-1$
			menuBar.add(mnCommittee);
			setMnemonic(mnCommittee, usedMnemonics);

			final JMenuItem miManageDelegates = new JMenuItem(
					new AbstractAction(Messages
							.getString("Kiosk.MiCommitteeManage"), ImageFetcher //$NON-NLS-1$
							.fetchImageIcon(ImageType.MANAGE_DELEGATES)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							manageDelegates();
						}
					});
			mnCommittee.add(miManageDelegates);
			miManageDelegates.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_M, tk.getMenuShortcutKeyMask()));

			final JMenuItem miQuorum = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiCommitteeQuorum"), ImageFetcher.fetchImageIcon(ImageType.QUORUM)) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							questionQuorum();
						}
					});
			mnCommittee.add(miQuorum);
			miQuorum.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U,
					tk.getMenuShortcutKeyMask()));

			final JMenuItem miConfigureCommittee = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiCommitteeConfigure"), //$NON-NLS-1$
							ImageFetcher.fetchImageIcon(ImageType.CONFIGURE)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							configureCommittee();
						}
					});
			mnCommittee.add(miConfigureCommittee);
			miConfigureCommittee.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_C, tk.getMenuShortcutKeyMask()
							| KeyEvent.SHIFT_DOWN_MASK));

			final JMenuItem miCommitteeComments = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiCommitteeComments"), //$NON-NLS-1$
							ImageFetcher.fetchImageIcon(ImageType.COMMENTS)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							final JDialog dialog = new JDialog(
									Kiosk.this,
									Messages.getString("Kiosk.CommitteeCommentsDialogTitle")); //$NON-NLS-1$

							final LongTextProperty ltpComments = new LongTextProperty(
									Messages.getString("Kiosk.PropertyCommitteeComments"), committee.comments); //$NON-NLS-1$

							PropertyPanel pp = new PropertyPanel(Arrays
									.asList(ltpComments), true, false);

							JPanel panel = new JPanel(new BorderLayout());
							dialog.setContentPane(panel);

							panel.add(pp, BorderLayout.CENTER);

							JPanel pnlButtons = new JPanel(new FlowLayout(
									FlowLayout.TRAILING));
							panel.add(pnlButtons, BorderLayout.SOUTH);

							JButton btnCancel = new JButton(CANCEL_TEXT);
							btnCancel.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent ae) {
									dialog.dispose();
								}
							});
							pnlButtons.add(btnCancel);

							JButton btnOK = new JButton(OK_TEXT);
							btnOK.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									dialog.dispose();
									committee.comments = ltpComments.getValue();
								}
							});
							pnlButtons.add(btnOK);

							dialog.getRootPane().setDefaultButton(btnOK);
							dialog.pack();
							dialog.setMinimumSize(new Dimension(400, 1));
							dialog.setModal(true);
							dialog.setLocationRelativeTo(Kiosk.this);
							dialog.setVisible(true);
						}
					});
			mnCommittee.add(miCommitteeComments);

			final JMenuItem miVote = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiCommitteeVote"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.VOTE)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					beginVotingProcess();
				}
			});
			mnCommittee.add(miVote);
			miVote.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
					tk.getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK));

			mnCommittee.add(new JSeparator());

			JMenu mnCaucus = new JMenu(
					Messages.getString("Kiosk.MnnCommitteeCaucus")); //$NON-NLS-1$
			mnCommittee.add(mnCaucus);
			setMnemonic(mnCaucus, 'C');

			final JMenuItem miModeratedCaucus = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiiCaucusModerated")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<ModeratedCaucusMotion>() {
										@Override
										public ModeratedCaucusMotion generateMotion() {
											return new ModeratedCaucusMotion(
													getFirstPresentDelegate());
										}
									},
									new MotionPassedListener<ModeratedCaucusMotion>() {
										@Override
										public void motionPassed(
												final MotionPassedEvent<ModeratedCaucusMotion> mpe) {
											updateCommitteeState(CommitteeState.MODERATED_CAUCUS);
											publicDisplay.startModeratedCaucus(
													mpe.motion.getPurpose(),
													mpe.motion.getTotalTime());
											final ModeratedCaucusMotion mcm = mpe.motion;
											cardLayout
													.show(deck,
															CommitteeState.MODERATED_CAUCUS
																	.toString());
											moderatedCaucusPanel.start(
													mcm.getTotalTime(),
													mcm.getSpeakingTime(),
													mcm.getPurpose());
											rUpdateEnabledStatuses.run();
											final SpeechListener sl = new SpeechListener() {
												@Override
												public void speechActionPerformed(
														SpeechEvent se) {
													switch (se.type) {
													case STARTED:
														final int seconds = mpe.motion
																.getSpeakingTime()
																.getTotalSeconds();
														publicDisplay
																.startCaucusSpeech(
																		getSpeakerImageForDelegate(se.speaker),
																		seconds);
														break;
													case FINISHED:
														publicDisplay
																.stopCaucusSpeech();
														break;
													case PAUSED:
														publicDisplay
																.pauseCaucusSpeech();
														break;
													default:
														break;
													}
												}
											};
											moderatedCaucusPanel
													.addSpeechListener(sl);
											moderatedCaucusPanel
													.addCaucusListener(new CaucusListener() {
														@Override
														public void caucusActionPerformed(
																CaucusEvent ce) {
															switch (ce.type) {
															case ELAPSED:
																moderatedCaucusPanel
																		.removeSpeechListener(sl);
																moderatedCaucusPanel
																		.removeCaucusListener(this);
																updateCommitteeState(CommitteeState.SPEAKERS_LIST);
																break;
															case PAUSED:
																publicDisplay
																		.pauseModeratedCaucus();
																break;
															default:
																break;
															}
														}
													});
											pushDatum(DatumFactory
													.createModeratedCaucusDatum(
															mcm.getProposingDelegate(),
															mcm.getTotalTime(),
															mcm.getSpeakingTime(),
															mcm.getPurpose()));
											pushDatum(DatumFactory
													.createPromptDelegatesDatum());
										}
									});
						}
					});
			mnCaucus.add(miModeratedCaucus);
			miModeratedCaucus.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_1, tk.getMenuShortcutKeyMask()
							| KeyEvent.SHIFT_DOWN_MASK));

			final JMenuItem miUnmoderatedCaucus = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiiCaucusUnmod")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<UnmoderatedCaucusMotion>() {
										@Override
										public UnmoderatedCaucusMotion generateMotion() {
											return new UnmoderatedCaucusMotion(
													getFirstPresentDelegate());
										}
									},
									new MotionPassedListener<UnmoderatedCaucusMotion>() {
										@Override
										public void motionPassed(
												MotionPassedEvent<UnmoderatedCaucusMotion> mpe) {
											publicDisplay
													.startUnmoderatedCaucus(mpe.motion
															.getTotalTime());
											updateCommitteeState(CommitteeState.UNMODERATED_CAUCUS);
											cardLayout
													.show(deck,
															CommitteeState.UNMODERATED_CAUCUS
																	.toString());
											unmoderatedCaucusPanel
													.start(mpe.motion
															.getTotalTime());
											rUpdateEnabledStatuses.run();
											unmoderatedCaucusPanel
													.addCaucusListener(new CaucusListener() {
														@Override
														public void caucusActionPerformed(
																CaucusEvent ce) {
															switch (ce.type) {
															case ELAPSED:
																publicDisplay
																		.stopUnmoderatedCaucus();
																break;
															case PAUSED:
																break;
															default:
																break;
															}
														}
													});
											pushDatum(DatumFactory
													.createUnmoderatedCaucusDatum(
															mpe.motion
																	.getProposingDelegate(),
															mpe.motion
																	.getTotalTime()));
										}
									});
						}
					});
			mnCaucus.add(miUnmoderatedCaucus);
			miUnmoderatedCaucus.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_2, tk.getMenuShortcutKeyMask()
							| KeyEvent.SHIFT_DOWN_MASK));

			final JMenuItem miFormalCaucus = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiCaucusFormal")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {

							proposeMotion(
									new MotionGenerator<FormalCaucusMotion>() {

										@Override
										public FormalCaucusMotion generateMotion() {
											return new FormalCaucusMotion(
													getFirstPresentDelegate(),
													committee.workingPapers.get(committee
															.getCurrentTopic()));
										}
									},
									new MotionPassedListener<FormalCaucusMotion>() {

										@Override
										public void motionPassed(
												MotionPassedEvent<FormalCaucusMotion> mpe) {
											fcp.start(mpe.motion.getTotalTime());
											publicDisplay.startFormalCaucus(
													mpe.motion
															.getWorkingPaper(),
													mpe.motion.getTotalTime());
											cardLayout
													.show(deck,
															CommitteeState.FORMAL_CAUCUS
																	.toString());
											rUpdateEnabledStatuses.run();
											updateCommitteeState(CommitteeState.FORMAL_CAUCUS);
										}
									});
						}
					});
			mnCaucus.add(miFormalCaucus);
			miFormalCaucus.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
					tk.getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK));

			final JMenu mnOtherMotions = new JMenu(
					Messages.getString("Kiosk.MnnCommitteeOtherMotions")); //$NON-NLS-1$
			mnCommittee.add(mnOtherMotions);
			setMnemonic(mnOtherMotions, 'O');

			JMenuItem miIntroduceMotion = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiOtherIntroduceWP")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<IntroduceWorkingPaperMotion>() {
										@Override
										public IntroduceWorkingPaperMotion generateMotion() {
											return new IntroduceWorkingPaperMotion(
													getFirstPresentDelegate());
										}
									},
									new MotionPassedListener<IntroduceWorkingPaperMotion>() {
										@Override
										public void motionPassed(
												MotionPassedEvent<IntroduceWorkingPaperMotion> mpe) {
											introduceWorkingPaper();
										}
									});
						}
					});
			mnOtherMotions.add(miIntroduceMotion);

			JMenuItem miAgenda = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiOtherSetAgenda")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(new MotionGenerator<AgendaMotion>() {

								@Override
								public AgendaMotion generateMotion() {
									return new AgendaMotion(
											getFirstPresentDelegate(),
											committee);
								}

							}, new MotionPassedListener<AgendaMotion>() {
								@Override
								public void motionPassed(
										MotionPassedEvent<AgendaMotion> mpe) {
									committee.setCurrentTopic(mpe.motion
											.getTopic());
									updateComponents();
								}
							});
						}
					});
			mnOtherMotions.add(miAgenda);

			JMenuItem miDivideQuestion = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiOtherDivideQuestion")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<DivisionOfTheQuestionMotion>() {
										@Override
										public DivisionOfTheQuestionMotion generateMotion() {
											return new DivisionOfTheQuestionMotion(
													getFirstPresentDelegate());
										}
									}, null); // chair handles
						}
					});
			mnOtherMotions.add(miDivideQuestion);

			JMenuItem miSpeakingTime = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiOtherSetSpeakingTime")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<SpeakingTimeMotion>() {
										@Override
										public SpeakingTimeMotion generateMotion() {
											return new SpeakingTimeMotion(
													getFirstPresentDelegate(),
													committee.speakingTime,
													committee.numComments,
													committee.commentTime);
										}
									},
									new MotionPassedListener<SpeakingTimeMotion>() {
										@Override
										public void motionPassed(
												MotionPassedEvent<SpeakingTimeMotion> mpe) {
											committee.speakingTime = mpe.motion
													.getTime();
											committee.numComments = mpe.motion
													.getCommentCount();
											committee.commentTime = mpe.motion
													.getCommentTime();
										}
									});
						}
					});
			mnOtherMotions.add(miSpeakingTime);

			JMenuItem miTableDebate = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiOtherTableSuspend")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<TableDebateMotion>() {
										@Override
										public TableDebateMotion generateMotion() {
											return new TableDebateMotion(
													getFirstPresentDelegate());
										}
									}, null); // chairs deal with it
						}
					});
			mnOtherMotions.add(miTableDebate);

			JMenuItem miCloseDebate = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiOtherClose")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<CloseDebateMotion>() {
										@Override
										public CloseDebateMotion generateMotion() {
											return new CloseDebateMotion(
													getFirstPresentDelegate());
										}
									},
									new MotionPassedListener<CloseDebateMotion>() {
										@Override
										public void motionPassed(
												MotionPassedEvent<CloseDebateMotion> mpe) {
											committee.speakersList.clear();
											updateComponents();
											if (JOptionPane.showConfirmDialog(
													Kiosk.this,
													Messages.getString("Kiosk.VotePromptText"), //$NON-NLS-1$
													Messages.getString("Kiosk.VotePromptDialogTitle"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
													JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
												beginVotingProcess();
											}
										}
									});
						}
					});
			mnOtherMotions.add(miCloseDebate);

			JMenuItem miRollCallVote = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiOtherRollCallVote")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							proposeMotion(
									new MotionGenerator<RollCallVoteMotion>() {
										@Override
										public RollCallVoteMotion generateMotion() {
											return new RollCallVoteMotion(
													getFirstPresentDelegate());
										}
									},
									new MotionPassedListener<RollCallVoteMotion>() {
										@Override
										public void motionPassed(
												MotionPassedEvent<RollCallVoteMotion> mpe) {
											final MajorityType votingMajority = mpe.motion
													.getVotingMajority();
											performRollCallVote(votingMajority);
										}
									});
						}
					});
			mnOtherMotions.add(miRollCallVote);

			final JMenuItem miReturn = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiiCaucusReturn")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							if (JOptionPane.showConfirmDialog(
									Kiosk.this,
									Messages.getString("Kiosk.CaucusReturnPrompt"), //$NON-NLS-1$
									Messages.getString("Kiosk.CaucusReturnTitle"), //$NON-NLS-1$
									JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
								return;
							}
							if (moderatedCaucusPanel.isVisible()) {
								moderatedCaucusPanel.stopCaucus();
							} else if (unmoderatedCaucusPanel.isVisible()) {
								unmoderatedCaucusPanel.stopCaucus();
							} else if (fcp.isVisible()) {
								fcp.stopCaucus();
							}
							cardLayout.show(deck,
									CommitteeState.SPEAKERS_LIST.toString());
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									deck.requestFocusInWindow();
								}
							});
						}
					});
			mnCaucus.add(miReturn);
			miReturn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0,
					tk.getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK));

			final JMenu mnWP_Reso = new JMenu(
					Messages.getString("Kiosk.MnnCommitteeWorkingPapers")); //$NON-NLS-1$
			mnWP_Reso.setIcon(ImageFetcher
					.fetchImageIcon(ImageType.WORKING_PAPERS));
			mnCommittee.add(mnWP_Reso);
			setMnemonic(mnWP_Reso, 'W');

			final JMenuItem miIntroduceWorkingPaper = new JMenuItem(
					new AbstractAction(Messages
							.getString("Kiosk.MiiWPIntroduce"), ImageFetcher //$NON-NLS-1$
							.fetchImageIcon(ImageType.INTRODUCE_WORKING_PAPER)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							introduceWorkingPaper();
						}
					});
			mnWP_Reso.add(miIntroduceWorkingPaper);

			final JMenuItem miIntroduceDraftResolution = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiiWPPromote"), //$NON-NLS-1$
							ImageFetcher
									.fetchImageIcon(ImageType.INTRODUCE_DRAFT_RESOLUTION)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							introduceDraftResolution();
						}
					});
			mnWP_Reso.add(miIntroduceDraftResolution);

			final JMenuItem miViewEditWP_Reso = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiiWPView"), ImageFetcher //$NON-NLS-1$
									.fetchImageIcon(ImageType.EDIT_WORKING_PAPERS)) {
						@Override
						public void actionPerformed(ActionEvent ae) {
							viewEditWP_Resolutions();
						}
					});
			mnWP_Reso.add(miViewEditWP_Reso);

			final JMenuItem miProcessMotions = new JMenuItem(
					new AbstractAction(
							Messages.getString("Kiosk.MiCommitteeProcess")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							processMotionsOnFloor();
						}
					});
			mnCommittee.add(miProcessMotions);

			final JMenuItem miClearFloor = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiCommitteeClearFloor"), //$NON-NLS-1$
					ImageFetcher.fetchImageIcon(ImageType.CLEAR_FLOOR)) {
				@Override
				public void actionPerformed(ActionEvent ae) {
					clearFloor();
				}
			});
			mnCommittee.add(miClearFloor);

			rUpdateEnabledStatuses = new Runnable() {
				@Override
				public void run() {
					boolean canManage = floor.isEmpty();
					miManageDelegates.setEnabled(canManage);
					miQuorum.setEnabled(canManage);
					miVote.setEnabled(canManage);

					boolean canOperateOnFloor = !floor.isEmpty();
					miProcessMotions.setEnabled(canOperateOnFloor);
					miClearFloor.setEnabled(canOperateOnFloor);

					boolean notInCaucus = !inCaucus();
					mnOtherMotions.setEnabled(notInCaucus);
					miModeratedCaucus.setEnabled(notInCaucus);
					miUnmoderatedCaucus.setEnabled(notInCaucus);
					miFormalCaucus.setEnabled(notInCaucus);
					miReturn.setEnabled(!notInCaucus);
				}
			};
			mnCommittee.addMenuListener(new MenuListener() {

				@Override
				public void menuCanceled(MenuEvent me) {
				}

				@Override
				public void menuDeselected(MenuEvent me) {
				}

				@Override
				public void menuSelected(MenuEvent me) {
					rUpdateEnabledStatuses.run();
				}
			});

			final JMenu mnHelp = new JMenu(Messages.getString("Kiosk.MnHelp")); //$NON-NLS-1$
			menuBar.add(mnHelp);
			setMnemonic(mnHelp, usedMnemonics);

			final JMenuItem miAbout = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiHelpAbout")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							final JDialog dialog = new JDialog(Kiosk.this);
							dialog.setUndecorated(true);

							final Image image = ImageFetcher
									.fetchImage(ImageType.CREDITS_BG);
							dialog.setMinimumSize(new Dimension(image
									.getWidth(null), image.getHeight(null)));

							final JPanel panel = new JPanel(new MigLayout(
									new LC().flowY())) {

								/**
								 * The pulse frame length (fps = 20).
								 */
								private static final int PULSE_FRAMES = 20 * 4;

								/**
								 * The pulse index.
								 */
								private int index;

								/**
								 * The timer to increment the index.
								 */
								private final Timer timer = new Timer(50,
										new ActionListener() {
											@Override
											public void actionPerformed(
													ActionEvent e) {
												index = (++index)
														% PULSE_FRAMES;
												repaint();
											}
										});
								{
									timer.start();
								}

								@Override
								public void paintComponent(Graphics g) {
									Graphics2D g2d = (Graphics2D) g;
									final Container panel = dialog
											.getContentPane();
									g2d.setColor(Color.WHITE);
									int wP = panel.getWidth(), hP = panel
											.getHeight();
									g2d.fillRect(0, 0, wP, hP);
									int wI = image.getWidth(null), hI = image
											.getHeight(null);
									int dw = wP - wI, dh = hP - hI;
									double alphaPercentage = ((double) index)
											/ ((double) PULSE_FRAMES);
									double angle = alphaPercentage * Math.PI
											* 2;
									float alpha = (float) (1f / 8f * (Math
											.cos(angle))) + 5f / 32f;
									AlphaComposite ac = AlphaComposite
											.getInstance(
													AlphaComposite.SRC_OVER,
													alpha);
									Composite oldComposite = g2d.getComposite();
									g2d.setComposite(ac);
									g2d.drawImage(image, dw / 2, dh / 2, null);
									g2d.setComposite(oldComposite);
								}
							};
							dialog.setContentPane(panel);

							panel.add(Box.createGlue(), new CC().grow().push());

							JLabel lblKiosk = new JLabel(Messages
									.getString("Kiosk.AboutDialogTitle")); //$NON-NLS-1$
							lblKiosk.setHorizontalAlignment(SwingConstants.CENTER);
							panel.add(lblKiosk, new CC().growX());

							final JPanel pnlCredits = new PropertyPanel(
									aboutDialogPropertySet, false, true);
							pnlCredits.setOpaque(false);
							panel.add(pnlCredits, new CC().growX());

							panel.add(Box.createGlue(), new CC().grow().push());

							dialog.setModalityType(ModalityType.MODELESS);
							dialog.setModal(false);
							dialog.addFocusListener(new FocusAdapter() {
								@Override
								public void focusLost(FocusEvent e) {
									dialog.dispose();
								}
							});
							dialog.addKeyListener(new KeyAdapter() {
								@Override
								public void keyPressed(KeyEvent arg0) {
									dialog.dispose();
								}
							});
							panel.setBorder(BorderFactory.createLineBorder(
									Color.BLACK, 2));
							dialog.pack();
							dialog.setLocationRelativeTo(Kiosk.this);
							dialog.setVisible(true);
						}
					});
			mnHelp.add(miAbout);
			miAbout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
					tk.getMenuShortcutKeyMask()));

			final JMenuItem miSurvey = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiHelpTakeSurvey")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							takeSurvey();
						}
					});
			mnHelp.add(miSurvey);

			JMenuItem miUserGuide = new JMenuItem(new AbstractAction(
					Messages.getString("Kiosk.MiHelpUserGuide")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							viewUserGuide();
						}
					});
			mnHelp.add(miUserGuide);

			mnHelp.addMenuListener(new MenuListener() {
				@Override
				public void menuCanceled(MenuEvent me) {
				}

				@Override
				public void menuDeselected(MenuEvent me) {
				}

				@Override
				public void menuSelected(MenuEvent me) {
					// Don't show the survey button if we've already taken it.
					miSurvey.setVisible(!Preferences.userNodeForPackage(
							Kiosk.class).getBoolean(SURVEY_TAKEN_KEY, false));
				}
			});
		}

		setSplashScreenText(Messages
				.getString("Kiosk.SSMConfiguringDebateInterface")); //$NON-NLS-1$
		{
			// Set up main GUI
			setLayout(new BorderLayout());
			add(deck, BorderLayout.CENTER);

			// General Speakers' List
			pnlSpeakersList = new JPanel(new MigLayout());
			deck.add(pnlSpeakersList, CommitteeState.SPEAKERS_LIST.toString());

			pnlSpeakersList.add(speakersListPanel, new CC().spanY().grow()
					.push());
			pnlSpeakersList.add(speechPanel, new CC().grow().push().wrap());

			speakersListPanel.addSpeechListener(new SpeechListener() {
				@Override
				public void speechActionPerformed(final SpeechEvent se) {
					switch (se.type) {
					case STARTED:
						speechPanel.startSpeech(se.speaker,
								committee.speakingTime, analysisServer);
						publicDisplay.startSpeech(se.speaker,
								committee.speakingTime);
						publicDisplay.updateSpeakersListProgress(
								getSpeakerImageForDelegate(se.speaker), 0);
						break;
					case FINISHED:
						speechPanel.yield(new Yield(YieldType.CHAIR));
						publicDisplay.updateSpeakersListProgress(null, 0);
						break;
					default:
						break;
					}
				}
			});
			speechPanel.addYieldListener(publicDisplay);
			speechPanel.addYieldActionListener(publicDisplay);
			speechPanel.addSpeechListener(publicDisplay);

			speechPanel.addYieldListener(new YieldListener() {
				@Override
				public void yield(YieldEvent ye) {
					switch (ye.yield.type) {
					case COMMENTS:
						if (committee.numComments != 0) {
							pushDatum(DatumFactory.createYieldToCommentsDatum(
									ye.delegate, committee.numComments));
						}
						break;
					default:
						break;
					}
				}
			});
			speechPanel.addYieldActionListener(new YieldActionListener() {
				@Override
				public void yieldActionPerformed(YieldActionEvent yae) {
					if (yae.actionType instanceof CommentActionType) {
						switch ((CommentActionType) yae.actionType) {
						case COMMENT_ENDED:
							if (committee.numComments != 0) {
								if (yae.commentsRemaining > 0) {
									pushDatum(DatumFactory
											.createCommentsRemainingDatum(
													yae.delegate,
													yae.commentsRemaining));
								} else {
									pushDatum(DatumFactory
											.createCommentsExhaustedDatum(yae.delegate));
								}
							}
							break;
						default:
							break;
						}
					}
				}
			});

			SpeechListener slAlmostFinishedWarning = new SpeechListener() {
				@Override
				public void speechActionPerformed(SpeechEvent se) {
					if (se.type == SpeechEventType.ALMOST_FINISHED) {
						pushDatum(DatumFactory.createSpeechWarningDatum());
					}
				}
			};
			speechPanel.addSpeechListener(slAlmostFinishedWarning);
			speechPanel.addSpeechListener(new SpeechListener() {
				@Override
				public void speechActionPerformed(SpeechEvent se) {
					if (se.type == SpeechEventType.FINISHED) {
						speechPanel.yield(new Yield(YieldType.CHAIR));
						Delegate firstSpeaker = speakersListPanel
								.getFirstSpeaker();
						if (firstSpeaker != null) {
							pushDatum(DatumFactory
									.createNextSpeakerDatum(firstSpeaker));
						}
					}
				}
			});

			// Moderated caucus
			deck.add(moderatedCaucusPanel,
					CommitteeState.MODERATED_CAUCUS.toString());
			moderatedCaucusPanel.addCaucusListener(new CaucusListener() {
				@Override
				public void caucusActionPerformed(CaucusEvent ce) {
					switch (ce.type) {
					case ELAPSED:
						pushDatum(DatumFactory.createCaucusEndedDatum());
						cardLayout.show(deck,
								CommitteeState.SPEAKERS_LIST.toString());
						break;
					case PAUSED:
						break;
					default:
						break;
					}
				}
			});
			moderatedCaucusPanel.addSpeechListener(slAlmostFinishedWarning);
			moderatedCaucusPanel.addSpeechListener(new SpeechListener() {
				@Override
				public void speechActionPerformed(SpeechEvent se) {
					if (se.type == SpeechEventType.FINISHED) {
						pushDatum(DatumFactory.createPromptDelegatesDatum());
					}
				}
			});

			// Unmoderated caucus
			unmoderatedCaucusPanel = new UnmoderatedCaucusPanel(
					speakersListPanel.getModel());
			deck.add(unmoderatedCaucusPanel,
					CommitteeState.UNMODERATED_CAUCUS.toString());
			unmoderatedCaucusPanel.addCaucusListener(new CaucusListener() {
				@Override
				public void caucusActionPerformed(CaucusEvent ce) {
					switch (ce.type) {
					case ELAPSED:
						pushDatum(DatumFactory.createCaucusEndedDatum());
						cardLayout.show(deck,
								CommitteeState.SPEAKERS_LIST.toString());
						updateCommitteeState(CommitteeState.SPEAKERS_LIST);
						break;
					case PAUSED:
						break;
					default:
						break;
					}
				}
			});

			// Formal caucus
			deck.add(fcp, CommitteeState.FORMAL_CAUCUS.toString());
			fcp.addCaucusListener(new CaucusListener() {
				@Override
				public void caucusActionPerformed(CaucusEvent ce) {
					switch (ce.type) {
					case ELAPSED:
						pushDatum(DatumFactory.createCaucusEndedDatum());
						cardLayout.show(deck,
								CommitteeState.SPEAKERS_LIST.toString());
						updateCommitteeState(CommitteeState.SPEAKERS_LIST);
						break;
					case PAUSED:
						break;
					default:
						break;
					}
				}
			});

			// Working paper view
			{
				final JPanel pnlWorkingPaperResolutions = new JPanel(
						new BorderLayout());
				deck.add(pnlWorkingPaperResolutions,
						ViewState.WPR_VIEW.toString());

				pnlWorkingPaperResolutions.add(lblWorkingPaperResolutionCount,
						BorderLayout.NORTH);

				final JToggleButton tglbtnViewHide = new JToggleButton(
						Messages.getString("Kiosk.ViewWPDraftResolutionButton")); //$NON-NLS-1$
				pnlWorkingPaperResolutions.add(tglbtnViewHide,
						BorderLayout.CENTER);
				tglbtnViewHide.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (!tglbtnViewHide.isSelected()) {
							tglbtnViewHide.setText(Messages
									.getString("Kiosk.ViewWPDraftResolutionButton")); //$NON-NLS-1$
							publicDisplay
									.setViewState(ViewState.STANDARD_DEBATE);
						} else {
							final ArrayList<WorkingPaper> papersForTopic = committee.workingPapers
									.get(committee.getCurrentTopic());
							if (papersForTopic == null
									|| papersForTopic.isEmpty()) {
								JOptionPane.showMessageDialog(
										Kiosk.this,
										Messages.getString("Kiosk.NoWPToDisplayMessage"), //$NON-NLS-1$
										Messages.getString("Kiosk.NoWPToDisplayTitle"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
								tglbtnViewHide.setSelected(false);
								return;
							}
							final JDialog dialog = new JDialog(
									Kiosk.this,
									Messages.getString("Kiosk.SelectDocumentDialog")); //$NON-NLS-1$

							JPanel panel = new JPanel(new BorderLayout());
							dialog.setContentPane(panel);

							PropertySet ps = new PropertySet();

							final MultipleChoiceProperty<WorkingPaper> mcpPaper = new MultipleChoiceProperty<WorkingPaper>(
									Messages.getString("Kiosk.PropertyDocument"), papersForTopic, papersForTopic //$NON-NLS-1$
											.get(0));
							ps.add(mcpPaper);
							mcpPaper.setRenderer(new WorkingPaperRenderer());
							panel.add(new PropertyPanel(ps, true, false),
									BorderLayout.CENTER);

							JPanel pnlButtons = new JPanel(new FlowLayout(
									FlowLayout.TRAILING));
							panel.add(pnlButtons, BorderLayout.SOUTH);

							JButton btnCancel = new JButton(CANCEL_TEXT);
							pnlButtons.add(btnCancel);
							btnCancel.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent ae) {
									dialog.dispose();
									tglbtnViewHide.setSelected(false);
								}
							});

							JButton btnShow = new JButton(
									Messages.getString("Kiosk.SelectDocumentShowButton")); //$NON-NLS-1$
							pnlButtons.add(btnShow);
							btnShow.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent ae) {
									dialog.dispose();
									tglbtnViewHide.setText(Messages
											.getString("Kiosk.ViewWPDraftResolutionButtonHide")); //$NON-NLS-1$
									publicDisplay
											.setViewState(ViewState.WPR_VIEW);
									publicDisplay.setDocument(mcpPaper
											.getValue());
									if (!publicDisplay.isVisible()) {
										// Don't bother messing with focus if
										// it's already visible.
										publicDisplay.setVisible(true);
									}
								}
							});
							dialog.getRootPane().setDefaultButton(btnShow);

							dialog.pack();
							dialog.setLocationRelativeTo(Kiosk.this);
							dialog.setModal(true);
							dialog.setVisible(true);
						}
					}
				});
			}

			setSplashScreenText(Messages
					.getString("Kiosk.SSMConfiguringCrisisInterface")); //$NON-NLS-1$
			// Crisis view
			{
				deck.add(multiCrisisEditor, ViewState.CRISIS_VIEW.toString());
			}

			setSplashScreenText(Messages
					.getString("Kiosk.SSMConfiguringMotionsInterface")); //$NON-NLS-1$

			// Motions
			JPanel pnlMotionList = new JPanel(new BorderLayout());
			pnlSpeakersList.add(pnlMotionList, new CC().grow().push());

			lstMotions = new JList(motionModel);
			pnlMotionList.add(new JScrollPane(lstMotions), BorderLayout.CENTER);
			lstMotions.setCellRenderer(new MotionRenderer());
			lstMotions.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent ke) {
					if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE
							|| ke.getKeyCode() == KeyEvent.VK_DELETE) {
						if (!lstMotions.isSelectionEmpty()) {
							floor.remove(lstMotions.getSelectedIndex());
							updateListOfMotions();
						}
					}
				}
			});
			lstMotions.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					if (me.getClickCount() > 1) {
						int index = lstMotions.locationToIndex(me.getPoint());
						if (index != -1) {
							configureMotion(floor.get(index), false);
							updateListOfMotions();
						}
					}
				}
			});

			btnProcess = new JButton(new AbstractAction(
					Messages.getString("Kiosk.ProcessMotions")) { //$NON-NLS-1$
						@Override
						public void actionPerformed(ActionEvent ae) {
							processMotionsOnFloor();
						}
					});
			pnlMotionList.add(btnProcess, BorderLayout.SOUTH);
			btnProcess.setEnabled(false);

			setSplashScreenText(Messages
					.getString("Kiosk.SSMConfiguringQuickSettings")); //$NON-NLS-1$
			// Readout properties
			PropertySet ps1 = new PropertySet();

			tpTopic = new TextProperty(
					Messages.getString("Kiosk.PropertyBottomTopic"), committee.getCurrentTopic()); //$NON-NLS-1$
			tpTopic.setEditability(EditAction.ACTION);
			tpTopic.setAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					configureCommittee();
				}
			});
			ps1.add(tpTopic);

			tpQuorum = new TextProperty(
					Messages.getString("Kiosk.PropertyBottomQuorum"), new String()); //$NON-NLS-1$
			tpQuorum.setEditability(EditAction.ACTION);
			tpQuorum.setAction(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					if (committee.delegates.size() > 0) {
						questionQuorum();
					} else {
						manageDelegates();
					}
				}
			});
			ps1.add(tpQuorum);

			PropertySet ps2 = new PropertySet();
			tpSpeakingTime = new TimePresetProperty(
					Messages.getString("Kiosk.PropertyBottomSpeakingTime"), //$NON-NLS-1$
					committee.speakingTime);
			tpSpeakingTime.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent ce) {
					committee.speakingTime = tpSpeakingTime.getValue();
				}
			});
			ps2.add(tpSpeakingTime);

			ltpComments = new LimitedTextProperty(
					Messages.getString("Kiosk.PropertyBottomQuickComment"), //$NON-NLS-1$
					new String(), 16);
			ltpComments.setDescription(Messages
					.getString("Kiosk.PropertyToolTipBroadcast")); //$NON-NLS-1$
			ps2.add(ltpComments);
			ltpComments.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent ce) {
					broadcastToOmniKiosks();
				}
			});

			JPanel panel = new JPanel(new GridLayout(1, 2));
			panel.add(new PropertyPanel(ps1, false, true));
			panel.add(new PropertyPanel(ps2, false, true));
			add(panel, BorderLayout.SOUTH);

			setSplashScreenText(Messages
					.getString("Kiosk.SSMConfiguringPublicDisplay")); //$NON-NLS-1$
			Dimension screenSize = tk.getScreenSize();
			publicDisplay.setSize(new Dimension(screenSize.width / 2,
					screenSize.height / 2));
			publicDisplay.pack();
		}

		// Final update.
		setSplashScreenText(Messages.getString("Kiosk.SSMUpdatingComponents")); //$NON-NLS-1$
		updateComponents();

		updateCommitteeState(CommitteeState.SPEAKERS_LIST);
		setSplashScreenText(Messages.getString("Kiosk.SSMDone")); //$NON-NLS-1$
	}

	/**
	 * Displays the result of a motion in a dialog and in the broadcast server.
	 * 
	 * @param result
	 *            the result to display
	 */
	private void announceResult(Datum result) {
		pushDatum(result);
		JOptionPane
				.showMessageDialog(
						Kiosk.this,
						result.getDisplayText(),
						Messages.getString("Kiosk.VotingResultDialogTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
	}

	/**
	 * Begins the loading process. This method first checks to see if a previous
	 * path is set in the Preferences API. If it is, it continues with loading
	 * via {@link #loadCommittee(File)}. If not, and if
	 * {@code loadIfNoPreviousPath} is {@code true}, it prompts the user for a
	 * path, and then proceeds with loading.
	 * 
	 * @param loadIfNoPreviousPath
	 *            whether the method should prompt the user for a file location
	 *            if no previous path is found
	 */
	private void beginLoad(boolean loadIfNoPreviousPath) {
		Preferences node = Preferences.userNodeForPackage(Committee.class);
		String path = node.get(LAST_ACCESS_KEY, null);

		File file;

		// Determine the path.
		if (path == null || (path = path.trim()).isEmpty()
				|| (!(file = new File(path)).exists())) {

			// The path is either null, empty, or nonexistent.

			// Clear the last access: it's invalid.
			clearLastAccess();

			// If allowed, ask the user.
			if (loadIfNoPreviousPath) {
				loadFromUserFile(false);
			}
		} else {
			// Previous path exists.
			loadCommittee(file);
		}
	}

	/**
	 * Begins the process of saving the committee.
	 * 
	 * @param saveIfNoPreviousPath
	 *            whether to save if there is no previous path stored
	 * @return whether the save was successful
	 */
	private boolean beginSave(boolean saveIfNoPreviousPath) {
		Preferences node = Preferences.userNodeForPackage(Committee.class);
		String path = node.get(LAST_ACCESS_KEY, null);
		if (path == null || (path = path.trim()).isEmpty()) {
			clearLastAccess();
			// If allowed, proceed.
			if (saveIfNoPreviousPath) {
				return saveToUserFile(false);
			} else {
				return false;
			}
		} else {
			return saveCommittee(new File(path));
		}
	}

	/**
	 * Begins the voting process.
	 */
	private void beginVotingProcess() {
		List<Delegate> present = committee.getPresentDelegates();
		boolean ok = false;
		for (Delegate delegate : present) {
			if (delegate.getStatus().canVoteSubstantive) {
				ok = true;
				break;
			}
		}
		if (!ok) {
			// There are no present delegates with voting rights.
			JOptionPane
					.showMessageDialog(
							this,
							Messages.getString("Kiosk.NoDelegatesWithVotingRightsMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.NoDelegatesWithVotingRightsTitle"), //$NON-NLS-1$
							JOptionPane.ERROR_MESSAGE);
			return;
		}

		CommitteeState oldState = committee.state;
		updateCommitteeState(CommitteeState.VOTING_BLOC);
		do {
			final JDialog dialog = new JDialog(this);
			dialog.setTitle(Messages
					.getString("Kiosk.SubstantiveVoteDialogTitle")); //$NON-NLS-1$
			JPanel panel = new JPanel(new BorderLayout());
			dialog.setContentPane(panel);
			PropertySet psMajorityType = new PropertySet();
			final MultipleChoiceProperty<MajorityType> mcpMajorityType = new MultipleChoiceProperty<MajorityType>(
					Messages.getString("Kiosk.PropertyMajorityType"), Arrays.<MajorityType> asList(MajorityType //$NON-NLS-1$
									.values()), MajorityType.QUALIFIED);
			psMajorityType.add(mcpMajorityType);
			panel.add(new PropertyPanel(psMajorityType, true, false),
					BorderLayout.NORTH);
			JPanel pnlOptions = new JPanel(new MigLayout());
			panel.add(pnlOptions, BorderLayout.CENTER);

			JPanel pnlNoRollCall = new JPanel(new BorderLayout());
			pnlOptions.add(pnlNoRollCall, new CC().grow());

			PropertySet psRollCall = new PropertySet();

			int presentVotingMembers = 0;
			for (Delegate delegate : committee.getPresentDelegates()) {
				if (delegate.getStatus().canVoteSubstantive) {
					presentVotingMembers++;
				}
			}
			final int finalPresentVotingMembers = presentVotingMembers;

			final CounterProperty cpYes = new CounterProperty(
					Messages.getString("Kiosk.PropertyInFavor"), //$NON-NLS-1$
					0, 0, presentVotingMembers);
			final CounterProperty cpNo = new CounterProperty(
					Messages.getString("Kiosk.PropertyAgainst"), //$NON-NLS-1$
					0, 0, presentVotingMembers);
			final CounterProperty cpAbstain = new CounterProperty(
					Messages.getString("Kiosk.PropertyAbstentions"), 0, 0, presentVotingMembers); //$NON-NLS-1$
			final TrueFalseProperty tfpVeto = new TrueFalseProperty(
					Messages.getString("Kiosk.PropertyVetoed"), //$NON-NLS-1$
					Boolean.FALSE,
					Messages.getString("Kiosk.PropertyVetoedYes"), Messages.getString("Kiosk.PropertyVetoedNo")); //$NON-NLS-1$ //$NON-NLS-2$
			tfpVeto.setEnabled(false);

			psRollCall.add(cpYes);
			psRollCall.add(cpNo);
			psRollCall.add(cpAbstain);
			psRollCall.add(tfpVeto);

			final MessageProperty mpTotalVotes = new MessageProperty(
					Messages.getString("Kiosk.PropertyTotalVotes"), Integer.toString(0)); //$NON-NLS-1$
			psRollCall.add(mpTotalVotes);

			final MessageProperty mpPresentVotingMembers = new MessageProperty(
					Messages.getString("Kiosk.PropertyPresentMembersWithVotingRights"), //$NON-NLS-1$
					Integer.toString(presentVotingMembers));
			psRollCall.add(mpPresentVotingMembers);
			final JButton btnFinish = new JButton(
					Messages.getString("Kiosk.ButtonFinishVoting")); //$NON-NLS-1$
			btnFinish.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					dialog.dispose();
					Datum result = DatumFactory.createVoteResultsDatum(
							cpYes.getValue(), cpNo.getValue(),
							cpAbstain.getValue(), mcpMajorityType.getValue(),
							tfpVeto.getValue());
					announceResult(result);
				}
			});
			btnFinish.setEnabled(false);

			ChangeListener clUpdate = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent ce) {
					int totalVotes = 0;
					for (CounterProperty cp : new CounterProperty[] { cpYes,
							cpNo, cpAbstain }) {
						totalVotes += cp.getValue();
					}
					mpTotalVotes.setValue(Integer.toString(totalVotes));
					btnFinish.setEnabled(mpTotalVotes.getValue().equals(
							mpPresentVotingMembers.getValue()));
				}
			};
			for (final CounterProperty cp : new CounterProperty[] { cpYes,
					cpNo, cpAbstain }) {
				cp.addChangeListener(clUpdate);
				cp.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						int totalVotes = 0;
						for (CounterProperty cp : new CounterProperty[] {
								cpYes, cpNo, cpAbstain }) {
							totalVotes += cp.getValue();
						}
						if (totalVotes > finalPresentVotingMembers) {
							// Too high. Decrement this one.
							cp.setValue(cp.getValue() - 1);
						}
					}
				});
			}
			cpNo.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent ce) {
					if (cpNo.getValue() > 0) {
						tfpVeto.setEnabled(true);
					} else {
						tfpVeto.setEnabled(false);
						tfpVeto.setValue(false);
					}
				}
			});
			pnlNoRollCall.add(new PropertyPanel(psRollCall, true, false),
					BorderLayout.CENTER);
			pnlNoRollCall.add(btnFinish, BorderLayout.SOUTH);

			pnlOptions.add(new JSeparator(SwingConstants.VERTICAL), new CC()
					.growY().pad(2, 2, 2, 2));

			JButton btnRollCall = new JButton(
					Messages.getString("Kiosk.ButtonRollCallVote")); //$NON-NLS-1$
			btnRollCall.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					dialog.dispose();
					performRollCallVote(mcpMajorityType.getValue());
				}
			});
			pnlOptions.add(btnRollCall,
					new CC().growX().pushX().alignY("center")); //$NON-NLS-1$

			dialog.setModalityType(ModalityType.APPLICATION_MODAL);
			dialog.pack();
			dialog.setLocationRelativeTo(this);
			dialog.setVisible(true);
		} while (JOptionPane
				.showConfirmDialog(
						this,
						Messages.getString("Kiosk.PromptVoteAgainMessage"), Messages.getString("Kiosk.PromptVoteAgainTitle"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION);
		updateCommitteeState(oldState);
	}

	/**
	 * Alerts any listening OmniKiosks of the presence and status of this kiosk.
	 * 
	 * @throws UnknownHostException
	 *             if {@link InetAddress#getLocalHost()} fails
	 */
	private void broadcastToOmniKiosks() {
		if (!broadcastToOmniKiosks) {
			return;
		}
		final ArrayList<WorkingPaper> workingPapers = committee.workingPapers
				.get(committee.getCurrentTopic());
		CommitteeOutline outline = new CommitteeOutline(committee.getName(),
				committee.state, committee.getCurrentTopic(),
				ltpComments.getValue(), workingPapers == null ? 0
						: workingPapers.size());
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				OMNIKIOSK_BROADCAST_LENGTH);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(outline);
			byte[] byteArray = baos.toByteArray();
			if (byteArray.length > OMNIKIOSK_BROADCAST_LENGTH) {
				System.err.println("Serialized byte array is too long (" //$NON-NLS-1$
						+ byteArray.length + " bytes)"); //$NON-NLS-1$
				return;
			}
			final DatagramSocket socket = new DatagramSocket(
					OMNIKIOSK_BROADCAST_PORT);
			socket.setBroadcast(true);
			Enumeration<NetworkInterface> interfaces = NetworkInterface
					.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();

				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback
								// interface
				}

				for (InterfaceAddress interfaceAddress : networkInterface
						.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}

					// Send the broadcast package!
					try {
						DatagramPacket sendPacket = new DatagramPacket(
								byteArray, byteArray.length, broadcast,
								OMNIKIOSK_BROADCAST_PORT);
						socket.send(sendPacket);
					} catch (Exception e) {
					}

				}
			}
			socket.close();
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException ie) {
				// Fine. Nothing to do about it.
			}
		}
	}

	/**
	 * Prompts the user to clear all motions on the floor, then does so if the
	 * user agrees.
	 */
	private void clearFloor() {
		if (JOptionPane
				.showConfirmDialog(
						this,
						Messages.getString("Kiosk.ConfirmClearFloorMessage"), //$NON-NLS-1$
						Messages.getString("Kiosk.ConfirmClearFloorTitle"), JOptionPane.YES_NO_OPTION, //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
			floor.clear();
			motionModel.clear();
		}
	}

	/**
	 * Clears the value stored in the Preferences API as the last valid
	 * save/load location.
	 */
	private void clearLastAccess() {
		Preferences.userNodeForPackage(Committee.class).put(LAST_ACCESS_KEY,
				new String());
	}

	/**
	 * Allows the user to configure the autosave preferences.
	 */
	private void configureAutosave() {
		PropertySet ps = new PropertySet();
		final TrueFalseProperty tfpAutosaveEnabled = new TrueFalseProperty(
				Messages.getString("Kiosk.PropertyAutosave"), autosaveEnabled, Messages.getString("Kiosk.PropertyAutosaveEnabled"), Messages.getString("Kiosk.PropertyAutosaveDisabled")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		ps.add(tfpAutosaveEnabled);

		final TimeProperty tpInterval = new TimePresetProperty(
				Messages.getString("Kiosk.PropertyAutosaveInterval"), //$NON-NLS-1$
				autosaveInterval, TimePresetProperty.LONG_PRESETS);
		ps.add(tpInterval);
		tpInterval.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				if (tpInterval.getValue().getTotalSeconds() < 30) {
					// Don't autosave more than twice a minute. That's silly.
					tpInterval.setValue(new Time(0, 0, 30));
				} else if (tpInterval.getValue().getTotalSeconds() > 60 * 60 * 4) {
					// More than four hours is too long.
					tpInterval.setValue(new Time(4, 0, 0));
				}
			}
		});

		tfpAutosaveEnabled.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				tpInterval.setEnabled(tfpAutosaveEnabled.getValue());
			}
		});
		tpInterval.setEnabled(tfpAutosaveEnabled.getValue());

		final JDialog dialog = new JDialog(this,
				Messages.getString("Kiosk.AutosaveDialogTitle")); //$NON-NLS-1$

		JPanel panel = new JPanel(new BorderLayout());
		dialog.setContentPane(panel);

		panel.add(new PropertyPanel(ps, true, false), BorderLayout.CENTER);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panel.add(pnlButtons, BorderLayout.SOUTH);

		JButton btnCancel = new JButton(CANCEL_TEXT);
		pnlButtons.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
			}
		});

		JButton btnOK = new JButton(OK_TEXT);
		pnlButtons.add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
				autosaveInterval = tpInterval.getValue();
				tmAutosave.setDelay(autosaveInterval.getTotalSeconds() * 1000);
				tmAutosave.setInitialDelay(tmAutosave.getDelay());

				autosaveEnabled = tfpAutosaveEnabled.getValue();
				tmAutosave.stop();
				if (autosaveEnabled) {
					tmAutosave.start();
				}
				Preferences node = Preferences.userNodeForPackage(Kiosk.class);
				node.putInt(AUTOSAVE_INTERVAL_KEY,
						autosaveEnabled ? autosaveInterval.getTotalSeconds()
								: -1);
			}
		});
		dialog.getRootPane().setDefaultButton(btnOK);

		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
	}

	/**
	 * Opens the committee configuration dialog.
	 */
	private void configureCommittee() {
		speechPanel.stopSpeech();

		final JDialog dialog = new JDialog(this);
		dialog.setTitle(Messages
				.getString("Kiosk.ConfigureCommitteeDialogTitle")); //$NON-NLS-1$
		PropertySet ps = new PropertySet();

		final TextProperty tpCommitteeName = new TextProperty(
				Messages.getString("Kiosk.PropertyCommitteeName"), //$NON-NLS-1$
				committee.getName());
		ps.add(tpCommitteeName);

		final TimeProperty tpTime = new TimePresetProperty(
				Messages.getString("Kiosk.PropertyCommitteeSpeakingTime"), //$NON-NLS-1$
				committee.speakingTime);
		ps.add(tpTime);

		final CounterProperty cpCommentCount = new CounterProperty(
				Messages.getString("Kiosk.PropertyNumComments"), committee.numComments); //$NON-NLS-1$
		ps.add(cpCommentCount);

		final TimeProperty tpCommentLength = new TimePresetProperty(
				Messages.getString("Kiosk.PropertyCommentLength"), //$NON-NLS-1$
				committee.commentTime);
		ps.add(tpCommentLength);

		ps.add(null);

		StringBuilder sbTopics = new StringBuilder();
		Iterator<String> it_OldTopics = committee.getTopics().iterator();
		while (it_OldTopics.hasNext()) {
			final String oldTopic = it_OldTopics.next();
			sbTopics.append(oldTopic);
			if (it_OldTopics.hasNext()) {
				// Not last.
				sbTopics.append('\n');
			}
		}

		final ArrayList<String> newTopics = new ArrayList<String>();
		final LongTextProperty ltpTopics = new LongTextProperty(
				Messages.getString("Kiosk.PropertyListOfTopics"), //$NON-NLS-1$
				sbTopics.toString());
		ps.add(ltpTopics);

		final MultipleChoiceProperty<String> mcpCurrentTopic = new MultipleChoiceProperty<String>(
				Messages.getString("Kiosk.PropertyCurrentTopic"), new ArrayList<String>(), //$NON-NLS-1$
				committee.getCurrentTopic());
		ps.add(mcpCurrentTopic);
		ltpTopics.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				newTopics.clear();
				for (String topic : ltpTopics.getValue().split(
						Character.toString('\n'))) {
					if (!topic.isEmpty()) {
						newTopics.add(topic);
					}
					mcpCurrentTopic.setValues(newTopics);
				}
			}
		});
		ps.add(null);

		StringBuilder sbAffiliations = new StringBuilder();
		Iterator<String> it_OldAffiliations = committee.getAffiliations()
				.iterator();
		while (it_OldAffiliations.hasNext()) {
			final String oldAffiliation = it_OldAffiliations.next();
			sbAffiliations.append(oldAffiliation);
			if (it_OldAffiliations.hasNext()) {
				// Not last.
				sbAffiliations.append('\n');
			}
		}

		final LongTextProperty ltpAffiliations = new LongTextProperty(
				Messages.getString("Kiosk.PropertyListOfAffiliations"), sbAffiliations.toString()); //$NON-NLS-1$
		ltpAffiliations.setDescription(Messages
				.getString("Kiosk.PropertyToolTipAffiliations")); //$NON-NLS-1$
		ps.add(ltpAffiliations);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new PropertyPanel(ps, true, false));

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panel.add(pnlButtons, BorderLayout.SOUTH);

		JButton btnCancel = new JButton(CANCEL_TEXT);
		pnlButtons.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
			}
		});

		JButton btnOK = new JButton(OK_TEXT);
		pnlButtons.add(btnOK);
		dialog.getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
				committee.setName(tpCommitteeName.getValue());
				if (analysisServer != null) {
					analysisServer.setCommitteeName(committee.getName());
				}
				publicDisplay.setTitle(committee.getName());
				committee.speakingTime = tpTime.getValue();
				if (!newTopics.isEmpty()) {
					committee.setTopics(newTopics);
					committee.setCurrentTopic(mcpCurrentTopic.getValue());
				}
				committee.numComments = cpCommentCount.getValue();
				committee.commentTime = tpCommentLength.getValue();
				updateComponents();
				ArrayList<String> newAffiliations = new ArrayList<String>();
				for (String string : ltpAffiliations.getValue().split(
						Character.toString('\n'))) {
					newAffiliations.add(string);
				}
				committee.setAffiliations(newAffiliations);
				List<String> finalAffiliations = committee.getAffiliations();
				for (Delegate delegate : committee.getDelegateMap().keySet()) {
					if (!finalAffiliations.contains(delegate.getAffiliation())) {
						delegate.setAffiliation(finalAffiliations.isEmpty() ? null
								: finalAffiliations.get(0));
					}
				}
			}
		});

		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);

	}

	private boolean configureMotion(
			ProposedMotion<? extends Motion> proposedMotion, boolean okCancel) {
		final TrueFalseProperty tfpSaveChanges = new TrueFalseProperty(
				new String(), new String());
		tfpSaveChanges.setValue(false);
		final MotionConfigurationPanel mcp = new MotionConfigurationPanel(
				proposedMotion.motion, committee);
		final JDialog dialog = new JDialog(this);
		dialog.setTitle(Messages
				.getString("Kiosk.MotionConfigurationDialogTitle")); //$NON-NLS-1$
		JPanel panel = new JPanel(new BorderLayout());
		dialog.setContentPane(panel);

		panel.add(mcp, BorderLayout.CENTER);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panel.add(pnlButtons, BorderLayout.SOUTH);

		if (okCancel) {
			JButton btnCancel = new JButton(CANCEL_TEXT);
			pnlButtons.add(btnCancel);
			btnCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					dialog.dispose();
				}
			});
		}

		JButton btnOK = new JButton(OK_TEXT);
		pnlButtons.add(btnOK);
		dialog.getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				tfpSaveChanges.setValue(true);
				dialog.dispose();
			}
		});

		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		return tfpSaveChanges.getValue();
	}

	/**
	 * Deploys the given crisis.
	 * 
	 * @param crisis
	 *            the crisis to deploy
	 * @throws NullPointerException
	 *             if {@code crisis} is null
	 */
	private void deployCrisis(final Crisis crisis) throws NullPointerException {
		if (crisis == null) {
			throw new NullPointerException("crisis == null"); //$NON-NLS-1$
		}
		if (JOptionPane
				.showConfirmDialog(
						this,
						Messages.getString("Kiosk.ConfirmDeployCrisisPrefix") + crisis.getName() //$NON-NLS-1$
								+ Messages
										.getString("Kiosk.ConfirmDeployCrisisSuffix"), Messages.getString("Kiosk.ConfirmDeploymentTitle"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		if (!publicDisplay.isVisible()
				&& JOptionPane
						.showConfirmDialog(
								this,
								Messages.getString("Kiosk.PublicDisplayInvisiblePrompt"), //$NON-NLS-1$
								Messages.getString("Kiosk.PublicDisplayInvisibleTitle"), //$NON-NLS-1$
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			publicDisplay.setVisible(true);
		}

		JDialog dialog = new JDialog(this,
				Messages.getString("Kiosk.DeployCrisisButton")); //$NON-NLS-1$

		JPanel panel = new JPanel(new BorderLayout());
		dialog.setContentPane(panel);

		if (crisis.getQaTime() != null) {
			final JButton btnStartQaSession = new JButton(
					Messages.getString("Kiosk.StartQASessionButton")); //$NON-NLS-1$
			panel.add(btnStartQaSession, BorderLayout.NORTH);
			btnStartQaSession.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					publicDisplay.startQaSession(crisis.getQaTime());
					btnStartQaSession.setText(Messages
							.getString("Kiosk.StartQASessionButtonAlreadyStarted")); //$NON-NLS-1$
					btnStartQaSession.setEnabled(false);
				}
			});
		}

		// Valid PDF file (in name, at least).
		DocumentView pdfView = new DocumentView();
		pdfView.setDocument(crisis);
		panel.add(pdfView, BorderLayout.CENTER);

		publicDisplay.setViewState(ViewState.CRISIS_VIEW);
		publicDisplay.startCrisis(crisis);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setSize(new Dimension(screenSize.width * 3 / 4,
				screenSize.height * 3 / 4));
		dialog.setLocationRelativeTo(null);
		dialog.setModal(true);
		dialog.setVisible(true);

		crisis.setDeployed(true);
		publicDisplay.setViewState(ViewState.STANDARD_DEBATE);
	}

	/**
	 * Displays information related to the server.
	 */
	private void displayServerInfo() {
		PropertySet info = new PropertySet();

		try {
			InetAddress addr = InetAddress.getLocalHost();
			TextProperty tpIpAddress = new TextProperty(
					Messages.getString("Kiosk.PropertyLocalIP"), //$NON-NLS-1$
					addr.isLoopbackAddress() ? Messages
							.getString("Kiosk.NotConnectedToNetwork") //$NON-NLS-1$
							: addr.getHostAddress());
			info.add(tpIpAddress);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		TrueFalseProperty tfpServerRunning = new TrueFalseProperty(
				Messages.getString("Kiosk.PropertyServerStatus"), datumServer != null, Messages.getString("Kiosk.PropertyServerStatusOn"), Messages.getString("Kiosk.PropertyServerStatusOff")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		info.add(tfpServerRunning);

		if (tfpServerRunning.getValue() == true) {
			info.add(null);

			CounterProperty cpClientCount = new CounterProperty(
					Messages.getString("Kiosk.PropertyClientCount"), //$NON-NLS-1$
					datumServer.getClientCount());
			info.add(cpClientCount);

			StringBuilder sbHostnames = new StringBuilder();
			List<String> hostnames = datumServer.getClientHostnames();
			for (int i = 0; i < hostnames.size(); i++) {
				sbHostnames.append(hostnames.get(i).trim());
				if (i < (hostnames.size() - 1)) {
					// Not the last one.
					sbHostnames.append('\n');
				}
			}
			String hostnamesText = sbHostnames.toString().trim();
			LongTextProperty ltpHostnames = new LongTextProperty(
					Messages.getString("Kiosk.PropertyListClientHostnames"), hostnamesText.isEmpty() ? Messages.getString("Kiosk.NoClientsText") //$NON-NLS-1$ //$NON-NLS-2$
							: hostnamesText);
			info.add(ltpHostnames);
		}

		JDialog dialog = new JDialog(this,
				Messages.getString("Kiosk.ServerInfoDialogTitle")); //$NON-NLS-1$
		dialog.setContentPane(new PropertyPanel(info, false, false));
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	/**
	 * Gets the first present delegate.
	 * 
	 * @return the first present delegate
	 */
	private Delegate getFirstPresentDelegate() {
		return committee.getPresentDelegates().get(0);
	}

	/**
	 * Gets the image used for the delegate. This is the value of
	 * {@link Delegate#getIcon()}, or the {@link Kiosk#ICON_SUBSTITUTE} if the
	 * former is {@code null}.
	 * 
	 * @param delegate
	 *            the delegate for whom to fetch the image
	 * @return the image
	 */
	private Image getSpeakerImageForDelegate(Delegate delegate) {
		final ImageIcon icon = delegate.getIcon();
		final Image image = icon == null ? ICON_SUBSTITUTE.getImage() : icon
				.getImage();
		return image;
	}

	/**
	 * Determines whether a caucus (of any type) is in progress.
	 * 
	 * @return whether a caucus is in progress
	 */
	public boolean inCaucus() {
		if (committee.state == null) {
			return false;
		}
		switch (committee.state) {
		case MODERATED_CAUCUS:
		case UNMODERATED_CAUCUS:
		case FORMAL_CAUCUS:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Introduces a working paper as a draft resolution.
	 */
	private void introduceDraftResolution() {
		if (committee.getTopics().isEmpty()) {
			JOptionPane
					.showMessageDialog(
							Kiosk.this,
							Messages.getString("Kiosk.MustHaveTopicsSetToIntroduceMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.MustHaveTopicsSetToIntroduceTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		} else if (committee.getCurrentTopic() == null) {
			// Somehow there are topics, but none selected.
			committee
					.setCurrentTopic((String) committee.getTopics().toArray()[0]);
		}

		final ArrayList<WorkingPaper> existing = committee.workingPapers
				.get(committee.getCurrentTopic());
		if (existing == null || existing.isEmpty()) {
			JOptionPane
					.showMessageDialog(
							Kiosk.this,
							Messages.getString("Kiosk.NoWorkingPapersForTopicMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.NoWorkingPapersForTopicTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		}

		// We start with all, then remove already-draft resolutions.
		ArrayList<WorkingPaper> workingPapers = new ArrayList<WorkingPaper>(
				existing);
		Iterator<WorkingPaper> it = workingPapers.iterator();
		while (it.hasNext()) {
			WorkingPaper wp = it.next();
			if (wp.isDraftResolution()) {
				it.remove();
			}
		}
		if (workingPapers.isEmpty()) {
			JOptionPane
					.showMessageDialog(
							Kiosk.this,
							Messages.getString("Kiosk.NoNonIntroducedWorkingPaperMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.NoNonIntroducedWorkingPapersTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		}

		final JDialog dialog = new JDialog(this);
		dialog.setTitle(Messages.getString("Kiosk.SelectWorkingPaperToPromote")); //$NON-NLS-1$

		PropertySet ps = new PropertySet();

		final MultipleChoiceProperty<WorkingPaper> mcpWorkingPaper = new MultipleChoiceProperty<WorkingPaper>(
				Messages.getString("Kiosk.PropertyWorkingPaperToPromote"), workingPapers, workingPapers.get(workingPapers //$NON-NLS-1$
								.size() - 1));
		ps.add(mcpWorkingPaper);

		final JButton btnOpen = new JButton(
				Messages.getString("Kiosk.ButtonOpenFile")); //$NON-NLS-1$
		btnOpen.setEnabled(Desktop.isDesktopSupported());
		btnOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.open(mcpWorkingPaper.getValue().getFile());
				} catch (IOException ie) {
					JOptionPane.showMessageDialog(
							Kiosk.this,
							Messages.getString("Kiosk.FileCouldNotBeOpenedMessage"), Messages.getString("Kiosk.FileCouldNotBeOpenedTitle"), //$NON-NLS-1$ //$NON-NLS-2$
							JOptionPane.ERROR_MESSAGE);
					ie.printStackTrace();
				}
			}
		});
		mcpWorkingPaper.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				btnOpen.setEnabled(Desktop.isDesktopSupported()
						&& mcpWorkingPaper.getValue().getFile() != null);
			}
		});
		btnOpen.setEnabled(Desktop.isDesktopSupported()
				&& mcpWorkingPaper.getValue().getFile() != null);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));

		JButton btnCancel = new JButton(CANCEL_TEXT);
		pnlButtons.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
			}
		});

		JButton btnOK = new JButton(OK_TEXT);
		pnlButtons.add(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
				mcpWorkingPaper.getValue().introduceAsDraftResolution();
			}
		});

		JPanel panel = new JPanel(new MigLayout());
		dialog.setContentPane(panel);

		panel.add(new PropertyPanel(ps, true, false), new CC().push().grow()
				.spanX().wrap());
		panel.add(btnOpen, new CC().grow().spanX().wrap());
		panel.add(pnlButtons, new CC().grow());

		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
	}

	/**
	 * Introduces a working paper to the committee.
	 */
	private void introduceWorkingPaper() {
		if (committee.getTopics().isEmpty()) {
			JOptionPane
					.showMessageDialog(
							Kiosk.this,
							Messages.getString("Kiosk.NoTopicsSetMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.NoTopicsSetTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		} else if (committee.getCurrentTopic() == null) {
			// Somehow there are topics, but none selected.
			committee
					.setCurrentTopic((String) committee.getTopics().toArray()[0]);
		}
		final JDialog dialog = new JDialog(this);
		dialog.setTitle(Messages
				.getString("Kiosk.IntroduceWorkingPaperDialogTitle")); //$NON-NLS-1$

		final String topic = committee.getCurrentTopic();
		final int index = new ArrayList<String>(committee.getTopics())
				.indexOf(topic);
		final ArrayList<WorkingPaper> papers;
		if (committee.workingPapers.get(topic) == null) {
			committee.workingPapers.put(topic,
					papers = new ArrayList<WorkingPaper>());
		} else {
			papers = committee.workingPapers.get(topic);
		}

		final WorkingPaperEditor wpe = new WorkingPaperEditor(new WorkingPaper(
				index, papers.size()), committee);
		dialog.setContentPane(wpe);
		final WorkingPaper workingPaper = wpe.getWorkingPaper();
		wpe.addEditingFinishedListener(new EditingFinishedListener() {
			@Override
			public void editingFinished(EditingFinishedEvent efe) {
				dialog.dispose();
				papers.add(workingPaper);
				updateComponents();
				JPanel pnlUnilateralPrompt = new JPanel(new BorderLayout());
				pnlUnilateralPrompt.add(
						new JLabel(Messages
								.getString("Kiosk.UnilaterallyFormalPrompt")), BorderLayout.NORTH); //$NON-NLS-1$
				PropertySet psUnilateralTime = new PropertySet();
				psUnilateralTime.add(new TimePresetProperty(
						Messages.getString("Kiosk.UnilateralFormalCaucusTimePrompt"), new Time(0, //$NON-NLS-1$
								10, 0), TimePresetProperty.LONG_PRESETS));
				pnlUnilateralPrompt.add(new PropertyPanel(psUnilateralTime,
						true, false), BorderLayout.CENTER);
				if (JOptionPane.showConfirmDialog(
						Kiosk.this,
						pnlUnilateralPrompt, //$NON-NLS-1$
						Messages.getString("Kiosk.UnilaterallyFormalTitle"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { //$NON-NLS-1$
					FormalCaucusMotion fcm = new FormalCaucusMotion(
							workingPaper.submitters.isEmpty() ? getFirstPresentDelegate()
									: workingPaper.submitters.get(0),
							committee.workingPapers.get(committee
									.getCurrentTopic()), workingPaper);

					fcp.start(fcm.getTotalTime());
					publicDisplay.startFormalCaucus(fcm.getWorkingPaper(),
							fcm.getTotalTime());
					cardLayout.show(deck,
							CommitteeState.FORMAL_CAUCUS.toString());
					rUpdateEnabledStatuses.run();
					updateCommitteeState(CommitteeState.FORMAL_CAUCUS);
				}

			}
		});

		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
	}

	/**
	 * Loads the committee session stored in the given file into memory. If the
	 * load is successful, the file path is stored in the Preferences API.
	 * 
	 * @param file
	 *            the file containing the committee session
	 */
	private void loadCommittee(File file) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		boolean success = false;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			if (!(o instanceof Committee)) {
				return;
			}
			Committee committee = (Committee) o;

			// Committee state is transient.
			setCommittee(committee);
			updateCommitteeState(CommitteeState.SPEAKERS_LIST);

			// Provided that the loading process completed successfully, store
			// this file path for easy access next time.
			success = true;
			saveLocationToPreferences(file);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (Exception e) {
			// Something else happened.
			e.printStackTrace();
		} finally {
			for (Closeable closeable : new Closeable[] { ois, fis }) {
				try {
					closeable.close();
				} catch (IOException ie) {
					// Okay.
				}
			}
		}
		if (!success) {
			clearLastAccess();
		}
	}

	/**
	 * Attempts to load a committee session from a file the user selects.
	 * 
	 * @param defaultToPreference
	 *            whether the file chooser should default to the path of the
	 *            file stored in the preferences API as the last successful
	 *            access; this should only be {@code false} if it is known that
	 *            the last access is invalid
	 */
	private void loadFromUserFile(boolean defaultToPreference) {
		JFileChooser fileChooser;
		if (defaultToPreference) {
			Preferences node = Preferences.userNodeForPackage(Committee.class);
			String path = node.get(LAST_ACCESS_KEY, null);
			File f;
			if (path != null && !((path = path.trim()) == null)
					&& (f = new File(path)).exists()) {
				// The path is
				// * non-null
				// * non-empty
				// * trimmed
				// * in reference to an existing file
				// All systems go.
				fileChooser = new JFileChooser(f);
			} else {
				// Sorry.
				fileChooser = new JFileChooser();
			}
		} else {
			fileChooser = new JFileChooser();
		}

		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser
				.setFileFilter(new FileNameExtensionFilter(
						Messages.getString("Kiosk.CommitteeSessionsFileFilter"), FILE_NAME_EXTENSION)); //$NON-NLS-1$
		fileChooser.setMultiSelectionEnabled(false);

		int ret = fileChooser.showOpenDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// Cancelled or an error occurred.
			return;
		} else {
			loadCommittee(fileChooser.getSelectedFile());
		}

	}

	/**
	 * Opens a dialog to manage the delegates.
	 */
	private void manageDelegates() {
		final DelegatesConfigurationPanel dcp = new DelegatesConfigurationPanel(
				committee.delegates.keySet(), committee);

		final JDialog dialog = new JDialog(Kiosk.this);
		dialog.setTitle(Messages.getString("Kiosk.ManageDelegatesDialogTitle")); //$NON-NLS-1$
		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(dcp, BorderLayout.CENTER);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		contentPane.add(pnlButtons, BorderLayout.SOUTH);

		JButton btnCancel = new JButton(CANCEL_TEXT);
		pnlButtons.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
			}
		});

		JButton btnOK = new JButton(OK_TEXT);
		pnlButtons.add(btnOK);
		dialog.getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();

				List<Delegate> newDelegates = dcp.getDelegates();
				List<Delegate> currentDelegates = new ArrayList<Delegate>(
						committee.delegates.keySet());

				// Remove those that were removed.
				Iterator<Delegate> it = currentDelegates.iterator();
				while (it.hasNext()) {
					Delegate delegate = it.next();
					if (!newDelegates.contains(delegate)) {
						it.remove();
						committee.delegates.remove(delegate);
					}
				}

				// Add those that were added.
				for (Delegate delegate : newDelegates) {
					if (!currentDelegates.contains(delegate)) {
						committee.delegates.put(delegate, QuorumStatus.DEFAULT);
					}
				}

				// Finally, update the speakers' list, etc.
				// The delegates must be from the new list *and* present.
				ArrayList<Delegate> newPresentDelegates = new ArrayList<Delegate>(
						committee.getPresentDelegates());
				newPresentDelegates.retainAll(newDelegates);

				speakersListPanel.updateDelegates(newPresentDelegates);
				unmoderatedCaucusPanel.updateDelegates(newPresentDelegates);
				moderatedCaucusPanel.setDelegates(newPresentDelegates);

				updateComponents();
			}
		});

		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.pack();
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	/**
	 * Asks the user if the user wants to save before quitting. The user may
	 * elect to save and quit, quit without saving, or not quit at all.
	 */
	private void performExit() {
		int ret = JOptionPane
				.showConfirmDialog(
						this,
						Messages.getString("Kiosk.SaveChangesPrompt"), //$NON-NLS-1$
						Messages.getString("Kiosk.SaveChangesTitle"), JOptionPane.YES_NO_CANCEL_OPTION, //$NON-NLS-1$
						JOptionPane.WARNING_MESSAGE);
		switch (ret) {
		case JOptionPane.YES_OPTION:
			beginSave(true);
			System.exit(0);
		case JOptionPane.NO_OPTION:
			System.exit(0);
		case JOptionPane.CANCEL_OPTION:
		case JOptionPane.CLOSED_OPTION:
		default:
			return;
		}
	}

	/**
	 * Begins a roll call vote of the specified type.
	 * 
	 * @param votingMajority
	 *            the voting majority type
	 */
	private void performRollCallVote(final MajorityType votingMajority) {
		final RollCallVotePanel rcvp = new RollCallVotePanel(committee);
		final JDialog dialog = new JDialog(Kiosk.this);
		dialog.setTitle(Messages.getString("Kiosk.RollCallVoteDialog")); //$NON-NLS-1$
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(rcvp, BorderLayout.CENTER);
		JButton btnOK = new JButton(OK_TEXT);
		panel.add(btnOK, BorderLayout.SOUTH);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
			}
		});
		dialog.setContentPane(panel);
		dialog.getRootPane().setDefaultButton(btnOK);
		dialog.pack();
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setLocationRelativeTo(Kiosk.this);
		rcvp.beginVoting();
		dialog.setVisible(true);
		rcvp.beginSpeeches();

		// when done...

		int yes = 0, no = 0, abs = 0;
		for (Vote vote : rcvp.getVotes().values()) {
			if (vote == null) {
				continue;
			}
			switch (vote) {
			case YES:
			case YES_WITH_RIGHTS:
				yes++;
				break;
			case NO:
			case NO_WITH_RIGHTS:
				no++;
				break;
			case ABSTAIN:
				abs++;
				break;
			default:
				break;
			}
		}
		Datum result = DatumFactory.createVoteResultsDatum(yes, no, abs,
				votingMajority, rcvp.isVetoed());
		announceResult(result);
	}

	/**
	 * Checks the survey status.
	 */
	private void performSurveyCheck() {
		Preferences prefs = Preferences.userNodeForPackage(Kiosk.class);
		final byte[] firstRun = new byte[] { 0 };
		final byte[] prefsValue = prefs.getByteArray(SURVEY_KEY, firstRun);
		if (firstRun.equals(prefsValue)) {
			// It wasn't stored before.
			// Store a date a week from now.
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DAY_OF_MONTH, 7);
			prefs.putBoolean(SURVEY_TAKEN_KEY, false);

			// Serialize the new date.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(c.getTime());
				prefs.putByteArray(SURVEY_KEY, baos.toByteArray());
			} catch (IOException ie) {
				// Unlikely, but fine.
				ie.printStackTrace();
				return;
			}
		} else {
			// It was stored.
			if (prefsValue == null || prefsValue.length <= 1
					|| prefs.getBoolean(SURVEY_TAKEN_KEY, false)) {
				// The user asked not to be reminded again or has already taken
				// the survey.
				return;
			} else {
				// It's a serialized `Date' object.
				try {
					ObjectInputStream ois = new ObjectInputStream(
							new ByteArrayInputStream(prefsValue));
					Object o = ois.readObject();
					if (o instanceof Date) {
						// Good.
						Date d = (Date) o;

						// If we're on or after this date, display survey.
						if (!new Date().before(d)) {
							// Not before = on or after.
							final JPanel panel = new JPanel(new MigLayout());
							panel.add(
									new JLabel(
											Messages.getString("Kiosk.TakeSurveyPrompt")), //$NON-NLS-1$
									new CC().growX().pushX().wrap());
							final String YES = Messages.getString("Kiosk.Yes"), LATER = Messages.getString("Kiosk.NotNowRemindLater"), NO = Messages.getString("Kiosk.NoDontRemindMe"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							final List<String> values = Arrays.asList(YES,
									LATER, NO);
							final MultipleChoiceProperty<String> mcpChoice = new MultipleChoiceProperty<String>(
									Messages.getString("Kiosk.PropertySelectOption"), values, YES); //$NON-NLS-1$
							final PropertySet ps = new PropertySet();
							ps.add(mcpChoice);
							panel.add(new PropertyPanel(ps, true, false),
									new CC().growX().pushX());

							JOptionPane
									.showMessageDialog(
											this,
											panel,
											Messages.getString("Kiosk.SurveyDialogTitle"), JOptionPane.QUESTION_MESSAGE); //$NON-NLS-1$

							final String choice = mcpChoice.getValue();
							if (choice.equals(YES)) {
								// Survey.
								takeSurvey();
							} else if (choice.equals(LATER)) {
								Calendar c = Calendar.getInstance();
								c.add(Calendar.DAY_OF_MONTH, 7);

								// Serialize the new date.
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								ObjectOutputStream oos = new ObjectOutputStream(
										baos);
								oos.writeObject(c.getTime());
								prefs.putByteArray(SURVEY_KEY,
										baos.toByteArray());
								return;
							} else if (choice.equals(NO)) {
								// Never!
								prefs.putByteArray(SURVEY_KEY, new byte[] { 0 });
							}
						} else {
							// Not there yet.
						}
					}
				} catch (IOException ie) {
					// Shouldn't happen, but okay.
					ie.printStackTrace();
					return;
				} catch (ClassNotFoundException cnfe) {
					cnfe.printStackTrace();
				}
			}
		}
	}

	/**
	 * Processes a motion that has been proposed and is on the floor.
	 * 
	 * @param pm
	 *            the motion to process
	 * @return {@code true} if the user completed the dialog, or {@code false}
	 *         if the user closed the dialog
	 */
	private <T extends Motion> boolean processMotion(final ProposedMotion<T> pm) {
		final T motion = pm.motion;
		final Debatability debatability = motion.getDebatability();
		if (debatability != null && debatability != Debatability.NONE) {
			// We must debate!
			final JDialog dialog = new JDialog(Kiosk.this);
			dialog.setTitle(motion.getProposingDelegate().getName() + " - " //$NON-NLS-1$
					+ motion.getMotionName());
			MotionDebatePanel mdp = new MotionDebatePanel(motion, committee);
			JPanel panel = new JPanel(new BorderLayout());
			dialog.setContentPane(panel);
			panel.add(mdp, BorderLayout.CENTER);
			JButton btnClose = new JButton(
					Messages.getString("Kiosk.CloseButton")); //$NON-NLS-1$
			panel.add(btnClose, BorderLayout.SOUTH);
			btnClose.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					dialog.dispose();
				}
			});
			dialog.pack();
			dialog.setResizable(false);
			dialog.setLocationRelativeTo(Kiosk.this);
			dialog.setModalityType(ModalityType.APPLICATION_MODAL);
			dialog.setVisible(true);
		}

		final JDialog dialog = new JDialog(Kiosk.this);
		dialog.setTitle(Messages.getString("Kiosk.VoteOnMotionDialogTitle")); //$NON-NLS-1$

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new MotionViewingPanel(motion, Kiosk.this.committee));

		final JButton btnOK = new JButton(OK_TEXT);
		panel.add(btnOK, BorderLayout.SOUTH);
		dialog.getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
				btnOK.setEnabled(false);
				if (motion.getResult().passed) {
					floor.clear();
					if (pm.ifPassed != null) {
						pm.ifPassed.motionPassed(new MotionPassedEvent<T>(
								Kiosk.this, pm.motion));
					}
				}
			}
		});
		dialog.setContentPane(panel);
		dialog.pack();
		dialog.setLocationRelativeTo(Kiosk.this);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
		return !btnOK.isEnabled();
	}

	/**
	 * Processes all motions on the floor, in order of precedence from most
	 * disruptive to least so.
	 */
	private void processMotionsOnFloor() {
		sortFloor();
		speechPanel.stopSpeech();

		final Iterator<ProposedMotion<? extends Motion>> it = floor.iterator();
		int numFailed = 0;
		while (it.hasNext()) {
			ProposedMotion<? extends Motion> pm = it.next();
			if (!processMotion(pm)) {
				// The user closed the dialog box; stop here
				// Remove failed motions
				for (int i = 0; i < numFailed; i++) {
					floor.remove(0);
				}
				updateListOfMotions();
				updateComponents();
				return;
			}
			if (pm.motion.getResult().passed) {
				break;
			} else {
				numFailed++;
				updateListOfMotions();
			}
		}
		motionModel.clear();
		floor.clear();
		btnProcess.setEnabled(false);

		// Some motions (e.g. change speaking time) may require updated GUI.
		updateComponents();
	}

	/**
	 * Proposes the given motion, and executes a block of code if it passes.
	 * 
	 * @param motion
	 *            the motion to propose
	 * @param ifPassed
	 *            the code to execute if the motion passes
	 */
	private <T extends Motion> void proposeMotion(
			final MotionGenerator<T> generator,
			final MotionPassedListener<T> ifPassed) {
		if (committee.getPresentDelegates().isEmpty()) {
			JOptionPane
					.showMessageDialog(
							this,
							Messages.getString("Kiosk.NoDelegatesPresentForMotionMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.NoDelegatesPresentForMotionTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		}

		try {
			final T motion = generator.generateMotion();
			final ProposedMotion<T> proposed = new ProposedMotion<T>(motion,
					ifPassed);
			if (configureMotion(proposed, true)) {
				floor.add(new ProposedMotion<T>(motion, ifPassed));
				btnProcess.setEnabled(true);
				updateListOfMotions();
			}
		} catch (Exception e) {
			String message = e.getLocalizedMessage();
			if (message == null || (message = message.trim()).isEmpty()) {
				message = Messages.getString("Kiosk.ErrorCreatingMotionText"); //$NON-NLS-1$
			}
			JOptionPane.showMessageDialog(this, message,
					Messages.getString("Kiosk.ErrorCreatingMotionTitle"), //$NON-NLS-1$
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	/**
	 * Pushes the given {@code Datum} to the server, as long as the server is
	 * active.
	 * 
	 * @param datum
	 *            the datum to push
	 */
	public void pushDatum(Datum datum) {
		if (datumServer != null) {
			datumServer.pushDatum(datum);
		}
	}

	/**
	 * Questions the quorum.
	 */
	protected void questionQuorum() {
		final JDialog dialog = new JDialog(this);
		dialog.setTitle(Messages.getString("Kiosk.QuorumDialogTitle")); //$NON-NLS-1$
		JPanel panel = new JPanel(new BorderLayout());
		final QuorumPanel qp = new QuorumPanel(committee);
		final JScrollPane jScrollPane = new JScrollPane(qp);
		jScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(jScrollPane, BorderLayout.CENTER);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		panel.add(pnlButtons, BorderLayout.SOUTH);

		JButton btnCancel = new JButton(CANCEL_TEXT);
		pnlButtons.add(btnCancel);
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
			}
		});

		JButton btnOK = new JButton(OK_TEXT);
		pnlButtons.add(btnOK);
		dialog.getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				dialog.dispose();
				committee.delegates.putAll(qp.getMap());
				updateComponents();
			}
		});

		dialog.setContentPane(panel);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.pack();
		dialog.setSize(dialog.getSize().height <= 600 ? dialog.getSize()
				: new Dimension((int) dialog.getSize().getWidth(), 600));
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}

	/**
	 * Saves the committee to the given file.
	 * 
	 * @param file
	 *            the file to save to
	 * @return whether the save was successful
	 */
	private boolean saveCommittee(File file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		boolean failed = true;
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(committee);
			failed = false;
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			// No resource leaks here, please.
			for (Closeable closeable : new Closeable[] { fos, oos }) {
				try {
					closeable.close();
				} catch (IOException ie) {
					ie.printStackTrace();
				}
			}
		}

		// Provided that the saving process completed successfully, store this
		// file path for easy access next time.
		if (!failed) {
			saveLocationToPreferences(file);
		}
		return !failed;
	}

	/**
	 * Saves the given file reference to the Preference API as the last
	 * successful save/load of a committee session.
	 * 
	 * @param file
	 *            the file reference to store
	 */
	private void saveLocationToPreferences(File file) {
		Preferences node = Preferences.userNodeForPackage(Committee.class);
		node.put(LAST_ACCESS_KEY, file.getPath());
	}

	/**
	 * Saves the current committee to a file.
	 * 
	 * @param defaultToPreference
	 *            whether to default to the stored file path
	 * @return whether the save was successful
	 */
	private boolean saveToUserFile(boolean defaultToPreference) {
		JFileChooser fileChooser;
		if (defaultToPreference) {
			Preferences node = Preferences.userNodeForPackage(Committee.class);
			String path = node.get(LAST_ACCESS_KEY, null);
			File f = null;
			if (path != null && !((path = path.trim()).isEmpty())
					&& (f = new File(path)).exists()) {
				// The path is
				// * non-null
				// * non-empty
				// * trimmed
				// * in reference to an existing file
				// All systems go.
				fileChooser = new JFileChooser(f);
			} else {
				// Sorry.
				fileChooser = new JFileChooser();
			}
		} else {
			fileChooser = new JFileChooser();
		}

		fileChooser.setAcceptAllFileFilterUsed(false);
		final FileNameExtensionFilter fnef = new FileNameExtensionFilter(
				Messages.getString("Kiosk.CommitteeSessionsFileFilter"), FILE_NAME_EXTENSION); //$NON-NLS-1$
		fileChooser.setFileFilter(fnef);
		fileChooser.setMultiSelectionEnabled(false);

		int ret = fileChooser.showSaveDialog(this);
		if (ret != JFileChooser.APPROVE_OPTION) {
			// Cancelled or an error occurred.
			return false;
		} else {
			File file = fileChooser.getSelectedFile();
			// Verify that the file ends in ".mun"
			if (!fnef.accept(file)) {
				file = new File(file.getPath() + '.' + FILE_NAME_EXTENSION);
			}
			return saveCommittee(file);
		}
	}

	/**
	 * Sets the committee and updates all relevant graphical components.
	 * 
	 * @param committee
	 *            the committee in progress
	 */
	private void setCommittee(Committee committee) {
		this.committee = committee;
		if (analysisServer != null) {
			analysisServer.setCommitteeName(committee.getName());
		}
		multiCrisisEditor.setCommittee(this.committee);
		updateComponents();
	}

	/**
	 * {@linkplain #setMnemonic(AbstractButton, char)} Sets the mnemonic} on the
	 * given component, as long as the application is not running on Mac. The
	 * mnemonic will be the first character in the name not contained in the
	 * given list. It will be added to the list.
	 * 
	 * @param component
	 *            the component
	 * @param usedMnemonics
	 *            the list of used mnemonics
	 */
	private void setMnemonic(AbstractButton component,
			ArrayList<Character> usedMnemonics) {
		for (char c : component.getText().toCharArray()) {
			char upper = Character.toUpperCase(c);
			if (!usedMnemonics.contains(upper)) {
				usedMnemonics.add(upper);
				setMnemonic(component, upper);
				return;
			}
		}
	}

	/**
	 * Sets a mnemonic on the given component, as long as the application is not
	 * running on Mac.
	 * 
	 * @param component
	 *            the component
	 * @param mnemonic
	 *            the mnemonic
	 */
	private void setMnemonic(AbstractButton component, char upper) {
		if (!ON_MAC) {
			component.setMnemonic(upper);
		}
	}

	/**
	 * Sorts the motions on the floor, from most to least disruptive.
	 */
	private void sortFloor() {
		// We want the most disruptive *first*.
		Collections.sort(floor, Collections.reverseOrder());
	}

	/**
	 * Starts the new committee wizard.
	 */
	private void startCommitteeWizard() {
		final CommitteeWizard wizard = new CommitteeWizard(Kiosk.this,
				committee);
		wizard.pack();
		wizard.setLocationRelativeTo(Kiosk.this);
		wizard.setVisible(true);
		updateComponents();
	}

	/**
	 * Takes the survey.
	 */
	private void takeSurvey() {
		// Here's the URL of the survey.
		final String url = "https://docs.google.com/spreadsheet/viewform?formkey=dE1oOXFwUmt0WnRacmxIanYxNlBSaVE6MQ"; //$NON-NLS-1$
		if (Desktop.isDesktopSupported()) {
			// We can open it automatically.
			try {
				Desktop.getDesktop().browse(URI.create(url));
				JOptionPane
						.showMessageDialog(
								this,
								Messages.getString("Kiosk.SurveyOpeningMessage"), //$NON-NLS-1$
								Messages.getString("Kiosk.SurveyOpeningTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		} else {
			// We need the user to open it ... but we can copy to clipboard.
			final StringSelection stringSelection = new StringSelection(url);
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(stringSelection, stringSelection);
			JOptionPane
					.showMessageDialog(
							this,
							Messages.getString("Kiosk.SurveyURLCopiedMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.URLCopiedTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
		}
		// Done.
		final Preferences node = Preferences.userNodeForPackage(Kiosk.class);
		node.putByteArray(SURVEY_KEY, new byte[] { 0 });
		node.putBoolean(SURVEY_TAKEN_KEY, true);
	}

	/**
	 * Updates the committee state to the given state, and notifies required
	 * entities.
	 * 
	 * @param state
	 *            the new state
	 */
	private void updateCommitteeState(final CommitteeState state) {
		committee.state = state;
		publicDisplay.setCommitteeState(committee.state);
		if (rUpdateEnabledStatuses != null) {
			rUpdateEnabledStatuses.run();
		}
		broadcastToOmniKiosks();
	}

	/**
	 * Updates the various components to match the committee.
	 */
	private void updateComponents() {
		speakersListPanel.setCommittee(committee);
		speakersListPanel.updateDelegates(committee.getPresentDelegates());

		if (unmoderatedCaucusPanel != null) {
			unmoderatedCaucusPanel.setCommittee(committee);
			unmoderatedCaucusPanel.updateDelegates(committee
					.getPresentDelegates());
		}

		moderatedCaucusPanel.setDelegates(committee.getPresentDelegates());

		speechPanel.setCommittee(committee);

		final String currentTopic = committee.getCurrentTopic();
		if (tpTopic != null) {
			tpTopic.setValue(currentTopic == null ? Messages
					.getString("Kiosk.PropertyValueNoTopicSet") //$NON-NLS-1$
					: currentTopic);
		}

		int quorum = committee.getPresentDelegates().size();
		for (Delegate delegate : committee.getPresentDelegates()) {
			if (!delegate.getStatus().canVoteSubstantive) {
				quorum--;
			}
		}

		if (tpQuorum != null) {
			tpQuorum.setValue(quorum == 0 ? Messages
					.getString("Kiosk.PropertyValueNoDelegatesPresent") : (Integer //$NON-NLS-1$
							.toString((quorum / 2) + 1)
							+ Messages.getString("Kiosk.QuorumSeparator") //$NON-NLS-1$
							+ Integer.toString((int) (Math
									.ceil(((quorum) * 2d) / 3d)))
							+ Messages.getString("Kiosk.QuorumSeparator") + Integer.toString(quorum))); //$NON-NLS-1$
		}
		if (tpSpeakingTime != null) {
			tpSpeakingTime.setValue(committee.speakingTime);
		}

		final ArrayList<WorkingPaper> papersForTopic = committee.workingPapers
				.get(committee.getCurrentTopic());
		String workingPaperSingular = Messages
				.getString("Kiosk.WPDRCountWorkingPaperSingular"); //$NON-NLS-1$
		String workingPaperPlural = Messages
				.getString("Kiosk.WPDRCountWorkingPaperPlural"); //$NON-NLS-1$
		String draftResoSingular = Messages
				.getString("Kiosk.WPDRCountDraftResolutionSingular"); //$NON-NLS-1$
		String draftResoPlural = Messages
				.getString("Kiosk.WPDRCountDraftResolutionPlural"); //$NON-NLS-1$
		boolean plural = papersForTopic == null || papersForTopic.size() != 1;
		String workingPaper = plural ? workingPaperPlural
				: workingPaperSingular;
		String draftReso = plural ? draftResoPlural : draftResoSingular;
		lblWorkingPaperResolutionCount
				.setText(Integer.toString(papersForTopic == null ? 0
						: papersForTopic.size())
						+ ' '
						+ workingPaper
						+ Messages.getString("Kiosk.WPDRCountOr")//$NON-NLS-1$
						+ draftReso
						+ Messages.getString("Kiosk.WPDRCountCurrentTopic")); //$NON-NLS-1$
		lblWorkingPaperResolutionCount.setIcon(ImageFetcher
				.fetchImageIcon(papersForTopic == null
						|| papersForTopic.size() == 0 ? ImageType.WARNING
						: ImageType.OK));

		multiCrisisEditor.setCommittee(committee);

		publicDisplay.setCommittee(committee);
		publicDisplay.setTitle(committee.getName());
	}

	/**
	 * Updates the list of motions to match the motions on the floor. Also sorts
	 * the list of motions.
	 */
	protected void updateListOfMotions() {
		sortFloor();
		motionModel.clear();

		for (ProposedMotion<? extends Motion> motion : floor) {
			motionModel.addElement(motion);
		}
	}

	/**
	 * Sets the current state of the committee.
	 * 
	 * @param state
	 *            the state
	 */
	private void updateViewState(ViewState state) {
		if (state == ViewState.STANDARD_DEBATE) {
			// Update the public display.
			// All others have to be configured first.
			publicDisplay.setViewState(state);
		}
		cardLayout.show(deck,
				state == ViewState.STANDARD_DEBATE ? committee.state.toString()
						: state.toString());
		deck.requestFocusInWindow();
	}

	/**
	 * Shows a dialog allowing the user to view and edit working papers and
	 * resolutions.
	 */
	private void viewEditWP_Resolutions() {
		if (committee.getTopics().isEmpty()) {
			JOptionPane
					.showMessageDialog(
							Kiosk.this,
							Messages.getString("Kiosk.NoTopicSetMsesage"), //$NON-NLS-1$
							Messages.getString("Kiosk.NoTopicSetTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		} else if (committee.getCurrentTopic() == null) {
			// Somehow there are topics, but none selected.
			committee
					.setCurrentTopic((String) committee.getTopics().toArray()[0]);
		}

		final ArrayList<WorkingPaper> existing = committee.workingPapers
				.get(committee.getCurrentTopic());
		if (existing == null || existing.isEmpty()) {
			JOptionPane
					.showMessageDialog(
							Kiosk.this,
							Messages.getString("Kiosk.NoWorkingPapersOrDraftResolutionsForTopicMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.NoWorkingPapersOrDraftResolutionsForTopicTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		}

		JDialog dialog = new JDialog(this);
		dialog.setTitle(Messages.getString("Kiosk.ViewEditDialogTitle")); //$NON-NLS-1$
		dialog.setContentPane(new MultiWPREditor(committee.workingPapers
				.get(committee.getCurrentTopic()), committee));
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
	}

	/**
	 * Views the online user guide.
	 */
	private void viewUserGuide() {
		// Here's the URL of the survey.
		final String url = "https://docs.google.com/open?id=0B5NL1qvOouLnUVlMVDEzaGxhaUU"; //$NON-NLS-1$
		if (Desktop.isDesktopSupported()) {
			// We can open it automatically.
			try {
				Desktop.getDesktop().browse(URI.create(url));
				JOptionPane
						.showMessageDialog(
								this,
								Messages.getString("Kiosk.UserGuideOpeningMessage"), //$NON-NLS-1$
								Messages.getString("Kiosk.UserGuideOpeningTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		} else {
			// We need the user to open it ... but we can copy to clipboard.
			final StringSelection stringSelection = new StringSelection(url);
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(stringSelection, stringSelection);
			JOptionPane
					.showMessageDialog(
							this,
							Messages.getString("Kiosk.UserGuideURLCopiedMessage"), //$NON-NLS-1$
							Messages.getString("Kiosk.URLCopiedTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
		}
	}
}
