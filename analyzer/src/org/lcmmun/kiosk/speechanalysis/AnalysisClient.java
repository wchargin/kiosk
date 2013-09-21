package org.lcmmun.kiosk.speechanalysis;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * An analysis client, which attaches to an {@link AnalysisServer} and forwards
 * cues to the {@link SpeechAnalyzer}.
 * 
 * @author William Chargin
 * 
 */
public class AnalysisClient {

	/**
	 * The list of GUI objects (speech analyzers).
	 */
	private final ArrayList<SpeechAnalyzer> analyzers = new ArrayList<SpeechAnalyzer>();

	/**
	 * The committee name.
	 */
	public String committeeName;

	/**
	 * Creates the client, attaching it to the server at a given host. This
	 * constructor will spawn a new thread to continuously listen for incoming
	 * {@link BasicDatum} objects.
	 * 
	 * @param host
	 *            the host representing the server
	 * @throws ConnectException
	 *             if it is thrown by {@link Socket#Socket(String, int)}
	 * @throws UnknownHostException
	 *             if it is thrown by {@link Socket#Socket(String, int)}
	 */
	public AnalysisClient(String host) throws ConnectException,
			UnknownHostException {
		try {
			final Socket socket = new Socket(host, AnalysisServer.PORT);
			final InputStream inputStream = socket.getInputStream();
			final ObjectInputStream ois = new ObjectInputStream(inputStream);
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						socket.getOutputStream().write(0);
					} catch (IOException e) {
						// It's already closed. Everything's okay then.
						// Continue with closing.
					}
					for (Closeable closeable : new Closeable[] { ois,
							inputStream }) {
						try {
							closeable.close();
						} catch (IOException ie) {
							// That's okay. Continue.
						}
					}
					try {
						socket.close();
					} catch (IOException ie) {
						// That's okay. Continue.
					}
				}
			}));

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Object o = ois.readObject();
							if (o instanceof AnalysisCue) {
								AnalysisCue c = (AnalysisCue) o;
								for (SpeechAnalyzer holder : analyzers) {
									holder.addBlankAnalysis(c.name,
											c.affiliation, c.context);
								}
							} else if (o instanceof String) {
								// It's the name of the committee.
								setCommitteeName((String) o);
							}
						} catch (ClassNotFoundException cnfe) {
							cnfe.printStackTrace();
						} catch (EOFException e) {
							// The server shut down.
							Iterator<SpeechAnalyzer> it = analyzers.iterator();
							while (it.hasNext()) {
								it.next().disconnected();
								it.remove();
							}
						} catch (SocketException e) {
							// The server shut down.
							System.exit(0);
						} catch (IOException ie) {
							ie.printStackTrace();
						}
					}
				}
			}).start();
		} catch (ConnectException ce) {
			throw ce;
		} catch (UnknownHostException uhe) {
			throw uhe;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the given speech analyzer to the list of analyzers.
	 * 
	 * @param analyzer
	 *            the analyzer to add
	 */
	public synchronized void addSpeechAnalyzer(SpeechAnalyzer analyzer) {
		analyzers.add(analyzer);
		analyzer.setCommitteeName(committeeName);
	}

	/**
	 * Removes the given speech analyzer from the list of analyzers.
	 * 
	 * @param analyzer
	 *            the analyzer to remove
	 */
	public void removeSpeechAnalyzer(SpeechAnalyzer speechAnalyzer) {
		while (analyzers.contains(speechAnalyzer)) {
			analyzers.remove(speechAnalyzer);
		}
	}

	/**
	 * Sets the committee name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setCommitteeName(String name) {
		committeeName = name;
		for (SpeechAnalyzer analyzer : analyzers) {
			analyzer.setCommitteeName(name);
		}
	}

}
