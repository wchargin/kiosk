package org.lcmmun.kiosk.omni;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import org.lcmmun.kiosk.broadcast.CommitteeOutline;
import org.lcmmun.kiosk.omni.resources.ImageFetcher;
import org.lcmmun.kiosk.resources.ImageType;

import tools.customizable.MessageProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TextProperty;
import util.LAFOptimizer;

/**
 * The manager for the various committees.
 * 
 * @author William Chargin
 * 
 */
public class OmniKiosk extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The map of IP addresses to views.
	 */
	private final Map<String, CommitteeOutlineView> views = new LinkedHashMap<String, CommitteeOutlineView>();

	/**
	 * The index of the eye roll.
	 */
	private int eyeRollIndex = 0;

	/**
	 * The eye rolling timer.
	 */
	private final Timer eyeRoller = new Timer(25, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			if (++eyeRollIndex >= 48) {
				eyeRollIndex = -1;
				eyeRoller.stop();
			}
			repaint();
		}
	});

	public OmniKiosk() {
		super("OmniKiosk");
		final Image icon = ImageFetcher.fetchImage(ImageType.OMNI_ICON);
		setIconImage(icon);

		pnlOutlineViews = new JPanel(new MigLayout(new LC().flowY())) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (eyeRollIndex != -1) {
					Graphics2D g2d = (Graphics2D) g;
					AffineTransform oldTransform = g2d.getTransform();
					Composite oldComposite = g2d.getComposite();
					g2d.setTransform(AffineTransform.getRotateInstance(
							((double) eyeRollIndex / 24d) * 2 * Math.PI,
							getWidth() / 2, getHeight() / 2));
					g2d.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER,
							(float) (1 - ((double) eyeRollIndex / 48d))));
					int w = icon.getWidth(null);
					int h = icon.getHeight(null);
					g2d.drawImage(icon, (getWidth() - w) / 2,
							(getHeight() - h) / 2, w, h, null);
					g2d.setTransform(oldTransform);
					g2d.setComposite(oldComposite);
				}
			}

		};

		setContentPane(new JScrollPane(pnlOutlineViews,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));

		final Toolkit tk = Toolkit.getDefaultToolkit();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnGenerate = new JMenu("Generate");
		menuBar.add(mnGenerate);

		JMenuItem miKey = new JMenuItem("Key");
		mnGenerate.add(miKey);
		miKey.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
				tk.getMenuShortcutKeyMask()));
		miKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				JDialog dialog = new JDialog(OmniKiosk.this);
				PropertySet ps = new PropertySet();

				final TextProperty tpAffiliation = new TextProperty(
						"Affiliation", new String());
				ps.add(tpAffiliation);

				@SuppressWarnings("serial")
				final MessageProperty mpKey = new MessageProperty("Key",
						generateKeyFor(tpAffiliation.getValue())) {
					@Override
					protected JLabel createEditor() {
						JLabel editor = super.createEditor();
						Font current = editor.getFont();
						editor.setFont(new Font(Font.MONOSPACED, current
								.getStyle(), current.getSize() * 3 / 2));
						return editor;
					}
				};
				ps.add(mpKey);
				tpAffiliation.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent ae) {
						mpKey.setValue(generateKeyFor(tpAffiliation.getValue()));
					}
				});
				mpKey.setEnabled(false);

				dialog.setContentPane(new PropertyPanel(ps, true, false));
				dialog.pack();
				dialog.setLocationRelativeTo(OmniKiosk.this);
				dialog.setTitle("Key Generator");
				dialog.setVisible(true);
			}
		});
	}

	/**
	 * Generates a key for the given affiliation.
	 * 
	 * @param affiliation
	 *            the affiliation for which to generate a key
	 * @return the key
	 * @throws NoSuchAlgorithmException
	 *             if MD5 is not recognized as an algorithm by
	 *             {@link MessageDigest#getInstance(String)}
	 */
	protected static String generateKeyFor(String affiliation) {
		try {
			affiliation = affiliation.toLowerCase();
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(affiliation.getBytes());
			final byte[] affiliationDigest = md.digest();
			final int start = (affiliation.isEmpty() ? 0 : affiliation
					.charAt(0) % 16);

			// Put it to an alphanum string without O or I because they can be
			// misinterpreted as zero and one.
			return new BigInteger(1, affiliationDigest).toString(26 + 10 - 2)
					.toUpperCase().replace('O', 'Y').replace('I', 'Z')
					.substring(start, start + 7);
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			return null;
		}
	}

	/**
	 * Refreshes the view for the given IP address with the new outline.
	 * 
	 * @param ipAddress
	 *            the IP address of the host kiosk
	 * @param outline
	 *            the outline
	 */
	public void refreshView(final String ipAddress, CommitteeOutline outline) {
		if (views.containsKey(ipAddress)) {
			views.get(ipAddress).updateForOutline(outline);
		} else {
			final CommitteeOutlineView view = new CommitteeOutlineView();
			pnlOutlineViews.add(view, new CC().growX().pushX());
			views.put(ipAddress, view);
			view.addRemoveClickedListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					pnlOutlineViews.remove(view);
					views.remove(ipAddress);
					pnlOutlineViews.revalidate();
					validate();
					repaint();
				}
			});
			view.updateForOutline(outline);
			pnlOutlineViews.revalidate();
			validate();
			repaint();
		}
	}

	/**
	 * The content pane.
	 */
	private JPanel pnlOutlineViews;

	public static void main(String[] args) {
		LAFOptimizer.optimizeSwing();

		OmniKiosk omniKiosk = new OmniKiosk();
		DiscoveryThread.omniKiosk = omniKiosk;
		omniKiosk.pack();
		omniKiosk.setMinimumSize(new Dimension(300, 500));
		omniKiosk.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		omniKiosk.setLocationRelativeTo(null);
		omniKiosk.setVisible(true);
		omniKiosk.eyeRoller.start();
		new Thread(DiscoveryThread.INSTANCE).start();
	}

}
