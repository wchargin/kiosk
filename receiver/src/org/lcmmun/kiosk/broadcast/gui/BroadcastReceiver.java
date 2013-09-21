package org.lcmmun.kiosk.broadcast.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.lcmmun.kiosk.broadcast.DatumClient;
import org.lcmmun.kiosk.broadcast.Messages;

/**
 * A GUI implementation for the {@link DatumClient}. Chairs wishing to receive
 * notifications from "the mothership" should run this application.
 * 
 * @author William Chargin
 * 
 */
public class BroadcastReceiver extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The client currently in use.
	 */
	private DatumClient client;

	/**
	 * The connected/disconnected status label.
	 */
	private final JLabel lblStatus = new JLabel();
	{
		lblStatus.setFont(lblStatus.getFont()
				.deriveFont(lblStatus.getFont().getSize() * 2f)
				.deriveFont(Font.BOLD));
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		updateLabelStatus(-1);
	}

	private JButton btnConnect = new JButton(
			Messages.getString("BroadcastReceiver.Connect")); //$NON-NLS-1$

	private JButton btnDisconnect = new JButton(
			Messages.getString("BroadcastReceiver.Disconnect")); //$NON-NLS-1$

	/**
	 * The current datum holder.
	 */
	private DatumHolder holder;

	private final ActionListener disconnect = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			disconnect();
		}
	};

	/**
	 * Prompts the user for a hostname and connects to it.
	 */
	public synchronized void connect() {
		btnConnect.setEnabled(false);
		String host = JOptionPane
				.showInputDialog(
						this,
						Messages.getString("BroadcastReceiver.EnterHostnamePrompt"), //$NON-NLS-1$
						Messages.getString("BroadcastReceiver.EnterHostnameTitle"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
		if (host == null || (host = host.trim()).isEmpty()) {
			btnConnect.setEnabled(true);
			return;
		}
		final String finalHost = host;
		updateLabelStatus(0);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DatumClient old = client;
					client = new DatumClient(finalHost);
					if (old != null) {
						old.removeActionListener(disconnect);
					}
					client.addActionListener(disconnect);
				} catch (Exception ce) {
					JOptionPane.showMessageDialog(
							null,
							Messages.getString("BroadcastReceiver.RefusedPrefix") //$NON-NLS-1$
									+ finalHost
									+ Messages
											.getString("BroadcastReceiver.RefusedSuffix"), //$NON-NLS-1$
							Messages.getString("BroadcastReceiver.RefusedTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
					btnConnect.setEnabled(true);
					updateLabelStatus(-1);
					return;
				}
				holder = client.createPanel();
				getContentPane().add(holder, BorderLayout.CENTER);
				BroadcastReceiver.this.validate();
				updateLabelStatus(1);
				JOptionPane.showMessageDialog(
						null,
						Messages.getString("BroadcastReceiver.SuccessfulMessage"), //$NON-NLS-1$
						Messages.getString("BroadcastReceiver.SuccessfulTitle"), //$NON-NLS-1$
						JOptionPane.INFORMATION_MESSAGE);
				btnDisconnect.setEnabled(true);
			}
		});
	}

	/**
	 * Updates the status label.
	 * 
	 * @param status
	 *            the status: negative values indicate not connected, zero
	 *            indicates connecting, positive values indicate connected
	 */
	private void updateLabelStatus(int status) {
		if (status < 0) {
			lblStatus.setText(Messages
					.getString("BroadcastReceiver.Disconnected")); //$NON-NLS-1$
			lblStatus.setForeground(Color.RED.darker());
		} else if (status == 0) {
			lblStatus.setText(Messages
					.getString("BroadcastReceiver.Connecting")); //$NON-NLS-1$
			lblStatus.setForeground(Color.BLUE.darker());
		} else if (status > 0) {
			lblStatus
					.setText(Messages.getString("BroadcastReceiver.Connected")); //$NON-NLS-1$
			lblStatus.setForeground(Color.GREEN.darker());
		}
	}

	public BroadcastReceiver() {
		super(Messages.getString("BroadcastReceiver.Title")); //$NON-NLS-1$
		List<Image> images = new ArrayList<Image>();
		for (int dim : new int[] { 16, 24, 32, 48, 64, 128, 256 }) {
			try {
				final BufferedImage image = ImageIO
						.read(BroadcastReceiver.class
								.getResource("/org/lcmmun/kiosk/broadcast/icon/" + dim + ".png"));//$NON-NLS-1$ //$NON-NLS-2$
				if (image != null) {
					images.add(image);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setIconImages(images);

		// Create and set the content pane.
		final JPanel pnlContent = new JPanel(new BorderLayout());
		setContentPane(pnlContent);

		final JPanel pnlNorth = new JPanel(new BorderLayout());
		pnlContent.add(pnlNorth, BorderLayout.NORTH);

		pnlNorth.add(btnConnect, BorderLayout.WEST);
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				connect();
			}
		});

		pnlNorth.add(btnDisconnect, BorderLayout.EAST);
		btnDisconnect.setFocusable(false);
		btnDisconnect.setEnabled(false);
		btnDisconnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disconnect();
			}
		});

		pnlNorth.add(lblStatus, BorderLayout.CENTER);

	}

	/**
	 * Disconnects from a server and performs cleanup.
	 */
	public synchronized void disconnect() {
		client = null;
		updateLabelStatus(-1);
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		if (holder != null) {
			getContentPane().remove(holder);
		}
		validate();
		repaint();
	}

	public static void main(String[] args)
			throws UnsupportedLookAndFeelException {
		// Preferred order: nimbus, system, metal.
		try {
			boolean foundNimbus = false;
			for (LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
				if (lafi.getClassName().endsWith("NimbusLookAndFeel")) { //$NON-NLS-1$
					UIManager.setLookAndFeel(lafi.getClassName());
					break;
				}
			}
			if (!foundNimbus) {
				String systemLaf = UIManager.getSystemLookAndFeelClassName();
				if (systemLaf.toLowerCase().contains("metal")) { //$NON-NLS-1$
					// Seriously? Well, minimize eyesores.
					UIManager.put("swing.boldMetal", Boolean.FALSE); //$NON-NLS-1$
				}
				UIManager.setLookAndFeel(systemLaf);
			}
		} catch (Exception e) {
			// Last resort.
			UIManager.put("swing.boldMetal", Boolean.FALSE); //$NON-NLS-1$
		}

		// Create frame.
		final JFrame frame = new BroadcastReceiver();

		// Set size and position.
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		frame.setSize(new Dimension(screenSize.width / 2,
				screenSize.height * 2 / 3));
		frame.setLocationRelativeTo(null);

		// Set parameters.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Finally, show the frame.
		frame.setVisible(true);
	}
}
