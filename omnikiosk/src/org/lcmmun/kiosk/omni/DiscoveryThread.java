package org.lcmmun.kiosk.omni;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.lcmmun.kiosk.advisorview.AdvisorOutline;
import org.lcmmun.kiosk.advisorview.AdvisorServer;
import org.lcmmun.kiosk.broadcast.CommitteeOutline;
import org.lcmmun.kiosk.speechanalysis.CommitteeDelegateSummary;
import org.lcmmun.kiosk.speechanalysis.DelegateSummary;

/**
 * A thread to discover kiosks.
 * 
 * @author William Chargin
 * 
 */
public class DiscoveryThread implements Runnable {

	/**
	 * The broadcast length.
	 */
	private static final int OMNIKIOSK_BROADCAST_LENGTH = 1024 * 64;

	/**
	 * The broadcast port.
	 */
	private static final int OMNIKIOSK_BROADCAST_PORT = 5266;

	/**
	 * The socket used for discovery.
	 */
	private DatagramSocket socket;

	/**
	 * The omnikiosk to which the discoverer reports.
	 */
	public static OmniKiosk omniKiosk;

	/**
	 * The singleton instance of this class.
	 */
	public static final DiscoveryThread INSTANCE = new DiscoveryThread();

	/**
	 * The map of IP addresses (Strings) to keys (Strings).
	 */
	private final Map<String, String> listeningAdvisors = new LinkedHashMap<String, String>();

	/**
	 * The server.
	 */
	private final AdvisorServer server = new AdvisorServer();

	/**
	 * This is a {@code Map<A, Map<B, ArrayList<C>>>}.
	 * <p>
	 * <ul>
	 * <li><strong>{@code A}</strong> : committee names</li>
	 * <li><strong>{@code B}</strong> : affiliations</li>
	 * <li><strong>{@code C}</strong> : delegate summaries</li>
	 * </ul>
	 */
	private final Map<String, Map<String, ArrayList<DelegateSummary>>> summaries = new LinkedHashMap<String, Map<String, ArrayList<DelegateSummary>>>();

	/**
	 * The timer that updates advisors.
	 */
	private final Timer advisorTimer = new Timer(1000 * 30,
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					for (String advisorIp : listeningAdvisors.keySet()) {
						String key = listeningAdvisors.get(advisorIp);

						// Create a map of committee names to lists of delegate
						// summaries.
						AdvisorOutline advisorData = new AdvisorOutline();
						for (String committeeName : summaries.keySet()) {
							Map<String, ArrayList<DelegateSummary>> m = summaries
									.get(committeeName);
							for (String affiliation : m.keySet()) {
								if (OmniKiosk.generateKeyFor(affiliation)
										.equals(key)) {
									advisorData.put(committeeName,
											m.get(affiliation));
								}
							}
						}
						System.out.println("Sending " + advisorData.size()
								+ " committees to " + advisorIp);
						server.pushOutline(advisorIp, advisorData);

					}
				}
			});
	{
		advisorTimer.setInitialDelay(0);
	}

	@Override
	public void run() {
		try {
			// Opening a socket on 0.0.0.0 (the wildcard address) will intercept
			// all UDP packets on the port, regardless of the to/from IP
			// addresses.

			socket = new DatagramSocket(OMNIKIOSK_BROADCAST_PORT,
					InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			while (true) {
				// Create a byte array. The packets will be placed in here.
				byte[] content = new byte[OMNIKIOSK_BROADCAST_LENGTH];

				// Receive a packet.
				DatagramPacket packet = new DatagramPacket(content,
						content.length);
				socket.receive(packet); // blocks until a packet is received

				// Since we're here, a packet was received.
				// Get its data.

				ByteArrayInputStream bais = new ByteArrayInputStream(
						packet.getData());
				ObjectInputStream ois = null;

				try {
					ois = new ObjectInputStream(bais);
					Object o = ois.readObject();
					System.out.println("Got a " + o.getClass().getName());
					if (o instanceof CommitteeOutline) {
						omniKiosk.refreshView(packet.getAddress()
								.getHostAddress().trim(), (CommitteeOutline) o);
					} else if (o instanceof String) {
						String s = ((String) o).trim();
						String prefix = "DELEGATEREQUEST_KEY=";
						if (s.startsWith(prefix)) {
							System.out.println("advisor request");
							// It's an advisor request.
							listeningAdvisors.put(packet.getAddress()
									.getHostAddress(), s.substring(prefix
									.length()));
							String replyText = "ACCEPTED";
							DatagramPacket reply = new DatagramPacket(
									replyText.getBytes(),
									replyText.getBytes().length,
									packet.getAddress(),
									OMNIKIOSK_BROADCAST_PORT);
							socket.send(reply);
							advisorTimer.restart(); // in case it hasn't already
						}
					} else if (o instanceof CommitteeDelegateSummary) {
						CommitteeDelegateSummary cds = (CommitteeDelegateSummary) o;
						System.out
								.println("Received a CommitteeDelegateSummary from "
										+ cds.committeeName);
						summaries.put(cds.committeeName, cds.summaries);
					}
				} catch (IOException ie) {
					ie.printStackTrace();
				} catch (ClassNotFoundException cnfe) {
					cnfe.printStackTrace();
				}

			}
		} catch (BindException be) {
			JOptionPane
			.showMessageDialog(
					null,
					"OmniKiosk is already running on this network or on this computer.",
					"Cannot Start", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		} catch (IOException ex) {
			Logger.getLogger(DiscoveryThread.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}
}
