package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.lcmmun.kiosk.Document;
import org.lcmmun.kiosk.Messages;
import org.lobobrowser.html.HtmlRendererContext;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.DocumentBuilderImpl;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.xml.sax.SAXException;

public class DocumentView extends JPanel {

	/**
	 * A document view for a specific type of document (e.g., a PDF, a Google
	 * Document, a papyrus scroll, etc.).
	 * 
	 * @author William Chargin
	 * 
	 */
	private interface DocumentSubView {

		/**
		 * Gets the view component for this view. If this class extends
		 * JComponent, it can return itself.
		 * 
		 * @return the view component
		 */
		public JComponent getComponent();

		/**
		 * Determines whether the view can display the given document in its
		 * current state.
		 * 
		 * @param d
		 *            the document to display
		 * @return {@code true} if it can be displayed, or {@code false} if it
		 *         cannot
		 */
		public boolean canDisplay(Document d);

		/**
		 * Sets the displayed document to the given document and updates the
		 * display. Users of this class are encouraged to use ensure that
		 * {@link #canDisplay(Document)} returns {@code true} on the same
		 * document, as invalid documents may produce unexpected results.
		 * 
		 * @param d
		 *            the new document
		 */
		public void setDocument(Document d);

		/**
		 * Gets the display name for this tab. This should probably return a
		 * constant (e.g., "PDF", "Google Docs", "Papyrus Scroll", etc.).
		 * 
		 * @return the display name
		 */
		public String getDisplayName();
	}

	/**
	 * A view for a Google document.
	 * 
	 * @author William Chargin
	 * 
	 */
	public static class GoogleDocsView extends JPanel implements
			DocumentSubView {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The Cobra HTML panel used to display the Google Document.
		 */
		private final HtmlPanel htmlPanel;

		/**
		 * The current document ID.
		 */
		private String docId;

		/**
		 * The default rendering context.
		 */
		private final HtmlRendererContext render;

		/**
		 * The document builder implementation.
		 */
		private final DocumentBuilderImpl builder;

		/**
		 * The "please wait" message document.
		 */
		private final org.w3c.dom.Document docPleaseWait;

		/**
		 * The "loading failed" message document.
		 */
		private final org.w3c.dom.Document docFailed;

		private static final String DOC_URL_PREFIX = "https://docs.google.com/document/d/"; //$NON-NLS-1$
		private static final String DOC_URL_SUFFIX = "/pub"; //$NON-NLS-1$

		/**
		 * Creates the Google docs view.
		 */
		public GoogleDocsView() {
			super(new BorderLayout());

			// Cobra has a ton of annoying INFOs.
			Logger.getLogger("org.lobobrowser").setLevel(Level.SEVERE); //$NON-NLS-1$

			htmlPanel = new HtmlPanel();
			add(htmlPanel, BorderLayout.CENTER);

			final SimpleUserAgentContext uac = new SimpleUserAgentContext();
			uac.setScriptingEnabled(false);
			uac.setAppName(Kiosk.class.getName());
			render = new SimpleHtmlRendererContext(htmlPanel, uac);
			builder = new DocumentBuilderImpl(render);

			StringBuilder sbPleaseWait = new StringBuilder();
			sbPleaseWait.append("<html><body><h1>"); //$NON-NLS-1$
			sbPleaseWait.append(Messages.getString("DocumentView.PleaseWait")); //$NON-NLS-1$
			sbPleaseWait.append("</h1><p>"); //$NON-NLS-1$
			sbPleaseWait.append(Messages
					.getString("DocumentView.DocumentLoading")); //$NON-NLS-1$
			sbPleaseWait.append("</p></body></html>"); //$NON-NLS-1$
			org.w3c.dom.Document tempPleaseWait = null;
			try {
				tempPleaseWait = builder.parse(new ByteArrayInputStream(
						sbPleaseWait.toString().getBytes()));
			} catch (Exception e) {
				// That's okay; it's just a "please wait" message.
				// Keep going.
				e.printStackTrace();
			} finally {
				docPleaseWait = tempPleaseWait;
			}

			StringBuilder sbFailed = new StringBuilder();
			sbFailed.append("<html><body><h1>"); //$NON-NLS-1$
			sbFailed.append(Messages.getString("DocumentView.DocumentFailed")); //$NON-NLS-1$
			sbFailed.append("</h1></body></html>"); //$NON-NLS-1$
			org.w3c.dom.Document tempFailed = null;
			try {
				tempFailed = builder.parse(new ByteArrayInputStream(
						sbPleaseWait.toString().getBytes()));
			} catch (Exception e) {
				// That's okay; it's just a "loading failed" message.
				// Keep going.
				e.printStackTrace();
			} finally {
				docFailed = tempFailed;
			}

			JButton btnRefresh = new JButton(
					Messages.getString("DocumentView.RefreshDocument")); //$NON-NLS-1$
			btnRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					refreshDocument();
				}
			});
			add(btnRefresh, BorderLayout.NORTH);
		}

		/**
		 * Refreshes the current document in the view.
		 */
		public void refreshDocument() {
			if (docId == null || docId.isEmpty()) {
				return;
			}

			// Display loading message.
			htmlPanel.setDocument(docPleaseWait, render);

			// Fetch document.
			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean success = false;
					try {
						URL u = new URL(DOC_URL_PREFIX + docId + DOC_URL_SUFFIX);
						BufferedReader isr = new BufferedReader(
								new InputStreamReader(u.openConnection(
										Proxy.NO_PROXY).getInputStream()));
						String line = null;
						StringBuilder sb = new StringBuilder();
						while ((line = isr.readLine()) != null) {
							// Google docs adds the sequence U+00C2 U+00A0 (A
							// with circumflex, then nonbreaking space) to the
							// document around formatting marks, so we replace
							// them with a standard space.
							sb.append(line.replace("\u00C2\u00A0", " ")); //$NON-NLS-1$ //$NON-NLS-2$
						}
						org.w3c.dom.Document doc = builder
								.parse(new ByteArrayInputStream(sb.toString()
										.getBytes()));
						htmlPanel.setDocument(doc, render);
						success = true;
					} catch (MalformedURLException e) {
						JOptionPane
								.showMessageDialog(
										GoogleDocsView.this,
										Messages.getString("DocumentView.MalformedURLText"), //$NON-NLS-1$
										Messages.getString("DocumentView.MalformedURLTitle"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} catch (IOException e) {
						JOptionPane
								.showMessageDialog(
										GoogleDocsView.this,
										Messages.getString("DocumentView.IOExceptionText"), //$NON-NLS-1$
										Messages.getString("DocumentView.IOExceptionTitle"), //$NON-NLS-1$
										JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} catch (SAXException e) {
						JOptionPane
								.showMessageDialog(
										GoogleDocsView.this,
										Messages.getString("DocumentView.SAXExceptionText"), //$NON-NLS-1$
										Messages.getString("DocumentView.SAXExceptionTitle"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						e.printStackTrace();
					} finally {
						if (!success) {
							htmlPanel.setDocument(docFailed, render);
						}
					}
				}
			}).start();
		}

		/**
		 * Sets the document ID and refreshes the document.
		 * 
		 * @param docId
		 *            the new ID
		 */
		public void setDocId(String docId) {
			this.docId = (docId == null ? null : docId.trim());
			refreshDocument();
		}

		@Override
		public JComponent getComponent() {
			return this;
		}

		@Override
		public boolean canDisplay(Document d) {
			return d != null && d.getGoogleDocsId() != null
					&& !d.getGoogleDocsId().trim().isEmpty();
		}

		@Override
		public void setDocument(Document d) {
			setDocId(d.getGoogleDocsId());
		}

		@Override
		public String getDisplayName() {
			return Messages.getString("DocumentView.GoogleDocs"); //$NON-NLS-1$
		}
	}

	/**
	 * A view for a PDF.
	 * 
	 * @author William Chargin
	 * 
	 */
	public static class PdfView extends JPanel implements DocumentSubView {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The swing controller for this view.
		 */
		private final SwingController sc;

		/**
		 * Creates the PDF view.
		 */
		public PdfView() {
			super(new BorderLayout());

			sc = new SwingController();
			SwingViewBuilder svb = new SwingViewBuilder(sc);
			add(svb.buildViewerPanel(), BorderLayout.CENTER);

		}

		/**
		 * Sets the displayed file to the given file.
		 * 
		 * @param file
		 *            the file to be displayed
		 */
		public void setFile(File file) {
			if (file == null) {
				sc.closeDocument();
			} else {
				// Disable logging except for errors
				Logger.getLogger(
						org.icepdf.core.pobjects.Catalog.class.toString())
						.setLevel(Level.SEVERE);
				sc.openDocument(file.getPath());
			}
		}

		@Override
		public JComponent getComponent() {
			return this;
		}

		@Override
		public boolean canDisplay(Document d) {
			return d != null && d.getFile() != null && d.getFile().exists()
					&& d.getFile().isFile()
					&& d.getFile().getName().toLowerCase().endsWith("pdf"); //$NON-NLS-1$
			// Probably valid.
		}

		@Override
		public void setDocument(Document d) {
			setFile(d.getFile());
		}

		@Override
		public String getDisplayName() {
			return Messages.getString("DocumentView.PDF"); //$NON-NLS-1$
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The key used for the {@code CardLayout} to show the tabbed pane with
	 * document views.
	 */
	private static final String KEY_SHOW = JTabbedPane.class.getSimpleName();

	/**
	 * The key used for the {@code CardLayout} to hide all document views.
	 */
	private static final String KEY_HIDE = new String();

	/**
	 * The map of subviews. Each subview reads a different type of document.
	 */
	private final Map<Class<? extends DocumentSubView>, DocumentSubView> subviews;

	/**
	 * The document currently being displayed.
	 */
	private Document document;

	/**
	 * The tabbed pane used for this component.
	 */
	private final JTabbedPane tab;

	/**
	 * The card layout used for this component.
	 */
	private final CardLayout cl = new CardLayout();

	/**
	 * 
	 */
	public DocumentView() {
		super();
		setLayout(cl);

		// Initialize the subview map.
		subviews = new LinkedHashMap<Class<? extends DocumentSubView>, DocumentSubView>();

		// Add subviews.
		subviews.put(PdfView.class, new PdfView());
		subviews.put(GoogleDocsView.class, new GoogleDocsView());

		tab = new JTabbedPane();
		for (DocumentSubView v : subviews.values()) {
			tab.addTab(v.getDisplayName(), v.getComponent());
		}

		add(tab, KEY_SHOW);
		add(new JPanel(), KEY_HIDE);

	}

	/**
	 * Updates the display of this document.
	 */
	public void updateDisplay() {
		boolean set = false;
		for (int i = 0; i < tab.getTabCount(); i++) {
			Component t = tab.getComponentAt(i);
			if (t instanceof DocumentSubView) {
				DocumentSubView view = (DocumentSubView) t;
				if (view.canDisplay(document)) {
					view.setDocument(document);
					tab.setEnabledAt(i, true);
					if (!set) {
						tab.setSelectedIndex(i);
						set = true;
					}
				} else {
					tab.setEnabledAt(i, false);
				}
			}
		}
		if (set) {
			// We set something.
			// Show it.
			cl.show(this, KEY_SHOW);
		} else {
			// We unset everything.
			// Hide it.
			cl.show(this, KEY_HIDE);
		}
	}

	/**
	 * Gets the document currently being displayed.
	 * 
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Sets the document to be displayed and updates the display.
	 * 
	 * @param document
	 *            the new document
	 */
	public void setDocument(Document document) {
		this.document = document;
		updateDisplay();
	}

}