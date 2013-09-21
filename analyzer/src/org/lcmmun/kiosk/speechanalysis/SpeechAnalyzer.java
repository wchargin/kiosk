package org.lcmmun.kiosk.speechanalysis;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.speechanalysis.SpeechAnalysis.Context;

import tools.customizable.AbstractProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TrueFalseProperty;
import util.LAFOptimizer;

/**
 * The GUI input for a speech index.
 * 
 * @author William Chargin
 * 
 */
public class SpeechAnalyzer extends JFrame {

	private static final int BROADCAST_LENGTH = 1024 * 64;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The port.
	 */
	private static final int OMNIKIOSK_BROADCAST_PORT = 5266;

	/**
	 * The name of panel holding the property panel and remove button for each
	 * analysis.
	 */
	private static final String HOLDER_NAME = "PROPERTY_PANEL_HOLDER"; //$NON-NLS-1$

	/**
	 * The list of property panels.
	 */
	private final ArrayList<PropertyPanel> propertyPanels = new ArrayList<PropertyPanel>();

	public static void main(String[] args) {
		LAFOptimizer.optimizeSwing();

		SpeechAnalyzer sa = new SpeechAnalyzer();
		if (args.length > 0) {
			String path = args[0].trim();
			if (!path.isEmpty()) {
				File file = new File(path);
				if (file.exists() && !file.isDirectory() && file.canRead()) {
					sa.loadIndex(file);
				}
			}
		}
		sa.pack();
		sa.setLocationRelativeTo(null);
		sa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sa.setVisible(true);
	}

	/**
	 * The panel containing the speech analyses.
	 */
	private final JPanel analysisHolder = new JPanel(new MigLayout(
			new LC().flowY()));

	/**
	 * The speech index.
	 */
	private final SpeechIndex index = new SpeechIndex();

	/**
	 * The delegate graph.
	 */
	private final DelegateGraph graph = new DelegateGraph(index);

	/**
	 * Whether to show the finished analyses.
	 */
	private boolean showFinished = false;

	/**
	 * The current analysis client.
	 */
	private AnalysisClient client;

	/**
	 * The name of this committee.
	 */
	private String committeeName;

	/**
	 * The button to connect or disconnect.
	 */
	private final JButton btnConnect;

	/**
	 * Creates the analyzer.
	 */
	public SpeechAnalyzer() {
		super(Messages.getString("SpeechAnalyzer.WindowTitle")); //$NON-NLS-1$
		List<Image> images = new ArrayList<Image>();
		for (int dim : new int[] { 16, 24, 32, 48, 64, 128, 256 }) {
			try {
				final BufferedImage image = ImageIO
						.read(SpeechAnalyzer.class
								.getResource("/org/lcmmun/kiosk/speechanalysis/icon/" + dim + ".png"));//$NON-NLS-1$ //$NON-NLS-2$
				if (image != null) {
					images.add(image);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setIconImages(images);

		JPanel pnlContent = new JPanel(new BorderLayout());
		setContentPane(pnlContent);

		final Toolkit tk = Toolkit.getDefaultToolkit();

		final JFrame frmGraphs = new JFrame(
				Messages.getString("SpeechAnalyzer.SpeechesWindowTitle")); //$NON-NLS-1$
		frmGraphs.setIconImage(getIconImage());
		frmGraphs.setContentPane(graph);
		frmGraphs.pack();

		btnConnect = new JButton(
				Messages.getString("SpeechAnalyzer.ConnectButtonText")); //$NON-NLS-1$
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				btnConnect.setEnabled(false);
				if (client == null) {
					String host = JOptionPane.showInputDialog(Messages
							.getString("SpeechAnalyzer.EnterHostnamePrompt")); //$NON-NLS-1$
					if (host == null || (host = host.trim()).isEmpty()) {
						// null/empty text. Goodbye.
						btnConnect.setEnabled(true);
						btnConnect.requestFocusInWindow();
						return;
					}
					AnalysisClient client = null;
					try {
						client = new AnalysisClient(host);
					} catch (ConnectException ce) {
						displayConnectionRefused(btnConnect, host);
						return;
					} catch (UnknownHostException ce) {
						displayConnectionRefused(btnConnect, host);
						return;
					}
					JOptionPane.showMessageDialog(
							null,
							Messages.getString("SpeechAnalyzer.ConnectionSuccessfulMessage"), //$NON-NLS-1$
							Messages.getString("SpeechAnalyzer.ConnectionSuccessfulTitle"), //$NON-NLS-1$
							JOptionPane.INFORMATION_MESSAGE);
					client.addSpeechAnalyzer(SpeechAnalyzer.this);
					SpeechAnalyzer.this.client = client;
					btnConnect.setEnabled(true);
					btnConnect.setText(Messages
							.getString("SpeechAnalyzer.DisconnectButtonText")); //$NON-NLS-1$
				} else {
					client.removeSpeechAnalyzer(SpeechAnalyzer.this);
					client = null;
					btnConnect.setText(Messages
							.getString("SpeechAnalyzer.ConnectButtonText")); //$NON-NLS-1$
					btnConnect.setEnabled(true);
				}
			}

			private void displayConnectionRefused(final JButton btnConnect,
					String host) {
				JOptionPane.showMessageDialog(
						null,
						Messages.getString("SpeechAnalyzer.ConnectionRefusedMessagePrefix") //$NON-NLS-1$
								+ host
								+ Messages
										.getString("SpeechAnalyzer.ConnectionRefusedMessageSuffix"), //$NON-NLS-1$
						Messages.getString("SpeechAnalyzer.ConnectionRefusedTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				btnConnect.setEnabled(true);
				btnConnect.requestFocusInWindow();
			}
		});
		pnlContent.add(btnConnect, BorderLayout.NORTH);

		JPanel analysisHolderWithDummy = new JPanel(new CardLayout());
		analysisHolderWithDummy.add(analysisHolder, "holder"); //$NON-NLS-1$
		final PropertyPanel dummyPanel = new PropertyPanel(
				new SpeechAnalysis(new String(), new String(),
						Context.SPEAKERS_LIST).getPropertySet(), true, false);
		dummyPanel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED));
		analysisHolderWithDummy.add(dummyPanel, "dummy"); //$NON-NLS-1$
		JScrollPane scpn = new JScrollPane(analysisHolderWithDummy,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		int threeHalvesScpnHeight = (int) Math.ceil(scpn.getPreferredSize()
				.getHeight() * 1.5);
		int twoThirdsScreenHeight = (int) (tk.getScreenSize().getHeight() * 2 / 3);
		scpn.setPreferredSize(new Dimension(
				(int) Math.ceil(scpn.getPreferredSize().getWidth() * 1.5),
				threeHalvesScpnHeight > twoThirdsScreenHeight ? twoThirdsScreenHeight
						: threeHalvesScpnHeight));
		pnlContent.add(scpn, BorderLayout.CENTER);

		PropertySet psShowFinished = new PropertySet();
		final TrueFalseProperty tfpShowFinished = new TrueFalseProperty(
				Messages.getString("SpeechAnalyzer.ShowFinishedPropertyName"), showFinished, Messages.getString("SpeechAnalyzer.ShowFinishedPropertyTrue"), //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("SpeechAnalyzer.ShowFinishedPropertyFalse")); //$NON-NLS-1$
		tfpShowFinished.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				showFinished = tfpShowFinished.getValue();
				updateVisibility();
			}
		});
		psShowFinished.add(tfpShowFinished);
		pnlContent.add(new PropertyPanel(psShowFinished, true, false),
				BorderLayout.SOUTH);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu(Messages.getString("SpeechAnalyzer.MnFile")); //$NON-NLS-1$
		menuBar.add(mnFile);
		mnFile.setMnemonic('F');

		JMenuItem miSave = new JMenuItem(
				Messages.getString("SpeechAnalyzer.MiFileSave")); //$NON-NLS-1$
		mnFile.add(miSave);
		miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				tk.getMenuShortcutKeyMask()));
		miSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				chooser.showSaveDialog(SpeechAnalyzer.this);
				File file = chooser.getSelectedFile();
				if (file == null) {
					return;
				}
				if (!file.getName().endsWith(".sai")) { //$NON-NLS-1$
					file = new File(file.getAbsolutePath() + ".sai"); //$NON-NLS-1$
				}
				if (file.exists()) {
					if (JOptionPane.showConfirmDialog(
							SpeechAnalyzer.this,
							Messages.getString("SpeechAnalyzer.FileExistsPrompt"), //$NON-NLS-1$
							Messages.getString("SpeechAnalyzer.FileExistsTitle"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						return;
					}
				}
				FileOutputStream fos = null;
				ObjectOutputStream oos = null;
				try {
					fos = new FileOutputStream(file);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(index);
				} catch (FileNotFoundException fnfe) {
					JOptionPane.showMessageDialog(SpeechAnalyzer.this, Messages
							.getString("SpeechAnalyzer.ErrorWritingMessage")); //$NON-NLS-1$
					fnfe.printStackTrace();
				} catch (IOException ie) {
					JOptionPane.showMessageDialog(SpeechAnalyzer.this, Messages
							.getString("SpeechAnalyzer.ErrorWritingMessage")); //$NON-NLS-1$
					ie.printStackTrace();
				}
			}
		});

		JMenuItem miLoad = new JMenuItem(
				Messages.getString("SpeechAnalyzer.MiFileLoad")); //$NON-NLS-1$
		mnFile.add(miLoad);
		miLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				tk.getMenuShortcutKeyMask()));
		miLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(new FileNameExtensionFilter(Messages
						.getString("SpeechAnalyzer.FileTypeName"), "sai")); //$NON-NLS-1$ //$NON-NLS-2$
				chooser.showOpenDialog(SpeechAnalyzer.this);
				File file = chooser.getSelectedFile();
				if (file == null || !file.exists() || file.isDirectory()) {
					return;
				}
				loadIndex(file);
			}
		});

		mnFile.add(new JSeparator());

		JMenu mnExport = new JMenu(
				Messages.getString("SpeechAnalyzer.MnnFileExportToCSV")); //$NON-NLS-1$
		mnFile.add(mnExport);

		JMenuItem miDelegateAverages = new JMenuItem(
				Messages.getString("SpeechAnalyzer.MiiExportAverages")); //$NON-NLS-1$
		mnExport.add(miDelegateAverages);
		miDelegateAverages.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
				tk.getMenuShortcutKeyMask()));
		miDelegateAverages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				writeToFile(index.generateAverageCsv());
			}
		});

		JMenuItem miIndividualSpeeches = new JMenuItem(
				Messages.getString("SpeechAnalyzer.MiiExportIndividual")); //$NON-NLS-1$
		mnExport.add(miIndividualSpeeches);
		miIndividualSpeeches.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_E, tk.getMenuShortcutKeyMask()
						| KeyEvent.SHIFT_DOWN_MASK));
		miIndividualSpeeches.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				writeToFile(index.generateSpeechCsv());
			}
		});

		JMenu mnView = new JMenu(Messages.getString("SpeechAnalyzer.MnView")); //$NON-NLS-1$
		menuBar.add(mnView);
		mnView.setMnemonic('V');

		JMenuItem miViewGraph = new JMenuItem(
				Messages.getString("SpeechAnalyzer.MiViewSpeechGraph")); //$NON-NLS-1$
		mnView.add(miViewGraph);
		miViewGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				frmGraphs.setVisible(!frmGraphs.isVisible());
			}
		});

		final Timer timer = new Timer(1000 * 30, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				broadcastToOmniKiosks();
			}
		});
		timer.setInitialDelay(10000);
		timer.start();

	}

	protected void addAnalysis(final SpeechAnalysis analysis) {
		final PropertySet propertySet = analysis.getPropertySet();
		for (final AbstractProperty<?> property : propertySet) {
			boolean found = property.getName().equals(
					SpeechAnalysis.COMPLETED_PROPERTY_NAME);
			if (found) {
				property.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent ce) {
						updateVisibility();
					}
				});
			}
		}
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setName(HOLDER_NAME);

		final PropertyPanel propertyPanel = new PropertyPanel(propertySet,
				true, false);
		propertyPanels.add(propertyPanel);
		panel.add(propertyPanel, BorderLayout.CENTER);

		JButton btnRemove = new JButton(
				Messages.getString("SpeechAnalyzer.RemoveButtonText")); //$NON-NLS-1$
		panel.add(btnRemove, BorderLayout.SOUTH);
		btnRemove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				analysisHolder.remove(panel);
				analysisHolder.validate();
				analysisHolder.repaint();
				propertyPanels.remove(propertyPanel);
				index.remove(analysis);
				graph.refreshPlot();
			}
		});

		panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		analysisHolder.add(panel, new CC().growX().pushX().hideMode(3));
		analysisHolder.validate();
		analysisHolder.repaint();
		updateVisibility();
	}

	/**
	 * Adds a speech analysis to the index of analyses.
	 * 
	 * @param delegateName
	 *            the delegate who is speaking
	 * @param affiliation
	 *            the delegate's affiliation
	 * @param context
	 *            the context in which the delegate is speaking
	 */
	public void addBlankAnalysis(String delegateName, String affiliation,
			Context context) {
		SpeechAnalysis analysis = new SpeechAnalysis(delegateName, affiliation,
				context);
		index.add(analysis);
		graph.refreshPlot();
		addAnalysis(analysis);
	}

	/**
	 * Alerts any listening OmniKiosks of the presence and status of this kiosk.
	 * 
	 * @throws UnknownHostException
	 *             if {@link InetAddress#getLocalHost()} fails
	 */
	private void broadcastToOmniKiosks() {
		final Map<String, ArrayList<DelegateSummary>> summaryMap = new LinkedHashMap<String, ArrayList<DelegateSummary>>();
		final ArrayList<String> names = new ArrayList<String>();
		for (SpeechAnalysis analysis : index) {
			if (!summaryMap.containsKey(analysis.affiliation)) {
				summaryMap.put(analysis.affiliation,
						new ArrayList<DelegateSummary>());
			}
			if (!names.contains(analysis.delegateName)) {
				names.add(analysis.delegateName);
				summaryMap
						.get(analysis.affiliation)
						.add(new DelegateSummary(
								analysis.delegateName,
								analysis.affiliation,
								index.getDelegateAverage(analysis.delegateName),
								index.getTotalSpeechesByDelegate(analysis.delegateName),
								index.getCommentsForDelegate(analysis.delegateName),
								index.getLastSpeechTime(analysis.delegateName)));
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(BROADCAST_LENGTH);
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(new CommitteeDelegateSummary(committeeName,
					summaryMap));
			byte[] byteArray = baos.toByteArray();
			if (byteArray.length > BROADCAST_LENGTH) {
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
	 * Updates the visibility for all components, pursuant to the value of the
	 * {@link #showFinished} variable and the "completed" property of each
	 * property panel.
	 */
	public void updateVisibility() {
		for (PropertyPanel panel : propertyPanels) {
			Component holder = SwingUtilities.getAncestorNamed(HOLDER_NAME,
					panel);
			if (showFinished) {
				holder.setVisible(true);
			} else {
				for (AbstractProperty<?> property : panel.getProperties()) {
					if (property.getName().equals(
							SpeechAnalysis.COMPLETED_PROPERTY_NAME)) {
						holder.setVisible(property.getValue().equals(
								Boolean.FALSE));
					}
				}
			}
		}
	}

	/**
	 * Prompts the user to select a file and then writes the given string to
	 * that file.
	 * 
	 * @param text
	 *            the text to write
	 */
	private void writeToFile(String text) {
		JFileChooser chooser = new JFileChooser();
		chooser.showSaveDialog(SpeechAnalyzer.this);
		File file = chooser.getSelectedFile();
		if (file == null) {
			return;
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(text);
		} catch (IOException ie) {
			ie.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// Okay.
				}
			}
		}
	}

	/**
	 * Sets the committee name to the given name.
	 * 
	 * @param committeeName
	 *            the new name
	 */
	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
	}

	/**
	 * Invoked when the server has been disconnected.
	 */
	public void disconnected() {
		JOptionPane
				.showMessageDialog(
						this,
						Messages.getString("SpeechAnalyzer.MainKioskClosedMessage"), //$NON-NLS-1$
						Messages.getString("SpeechAnalyzer.MainKioskClosedTitle"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
		client = null;
		if (btnConnect != null) {
			btnConnect.setText(Messages
					.getString("SpeechAnalyzer.ConnectButtonText")); //$NON-NLS-1$
		}
	}

	public void loadIndex(File file) {
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			Object o = ois.readObject();
			if (!(index instanceof SpeechIndex)) {
				JOptionPane
						.showMessageDialog(SpeechAnalyzer.this, Messages
								.getString("SpeechAnalyzer.InvalidFileMessage")); //$NON-NLS-1$
			}
			SpeechIndex newIndex = (SpeechIndex) o;
			index.clear();
			index.addAll(newIndex);
			analysisHolder.removeAll();
			for (SpeechAnalysis sa : newIndex) {
				addAnalysis(sa);
			}
		} catch (FileNotFoundException fnfe) {
			JOptionPane.showMessageDialog(SpeechAnalyzer.this, Messages
					.getString("SpeechAnalyzer.ErrorReadingFromFileMessage")); //$NON-NLS-1$
			fnfe.printStackTrace();
		} catch (IOException ie) {
			JOptionPane.showMessageDialog(SpeechAnalyzer.this, Messages
					.getString("SpeechAnalyzer.ErrorReadingFromFileMessage")); //$NON-NLS-1$
			ie.printStackTrace();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} finally {
			for (Closeable closeable : new Closeable[] { fis, ois }) {
				if (closeable != null) {
					try {
						closeable.close();
					} catch (IOException ie) {
						// Okay.
					}
				}
			}
		}
	}
}
