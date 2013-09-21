package org.lcmmun.kiosk.speechanalysis;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * The analysis server, which sends out {@link AnalysisCue} objects.
 * 
 * @author William Chargin
 * 
 */
public class AnalysisServer {

	/**
	 * The port number to use for the server and clients.
	 */
	public static final int PORT = 5266;

	/**
	 * The command to close the stream.
	 */
	public static final int CLOSE_COMMAND = 0;

	/**
	 * The list of client sockets.
	 */
	private final ArrayList<Socket> clients = new ArrayList<Socket>();

	/**
	 * The list of client streams, to which the incoming {@code Datum} objects
	 * can be written.
	 */
	private final ArrayList<ObjectOutputStream> clientStreams = new ArrayList<ObjectOutputStream>();

	/**
	 * The list of items to close upon shutdown.
	 */
	private final ArrayList<Closeable> closeables = new ArrayList<Closeable>();

	/**
	 * The server socket.
	 */
	private ServerSocket server;

	/**
	 * The name of the committee.
	 */
	private String committeeName;

	/**
	 * Creates the analysis server, initializing it and setting it up to
	 * continuously listen for clients.
	 */
	public AnalysisServer() {
		super();
		try {
			// Create the socket on the required port.
			server = new ServerSocket(PORT);

			// Now, keep listening for new clients (but on a new thread).
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							// Block until the server accepts a client socket.
							final Socket client = server.accept();

							// Put the stream into the right format, and add it
							// to the list of streams.
							final OutputStream outputStream = client
									.getOutputStream();
							final ObjectOutputStream oos = new ObjectOutputStream(
									outputStream);
							clients.add(client);
							if (committeeName != null) {
								oos.writeObject(committeeName);
							}
							clientStreams.add(oos);

							new Thread(new Runnable() {
								@Override
								public void run() {
									try {
										InputStream is = client
												.getInputStream();
										while (is.read() != AnalysisServer.CLOSE_COMMAND) {
											continue;
										}
									} catch (Exception e) {
										// We're finished with this socket.
										// Proceed to remove.
									} finally {
										clients.remove(client);
										clientStreams.remove(oos);

										for (Closeable closeable : new Closeable[] {
												oos, outputStream }) {
											try {
												closeable.close();
											} catch (IOException ie) {
												// That's OK.
											} finally {
												closeables.remove(closeable);
											}
										}
										try {
											client.close();
										} catch (IOException ie) {
											// That's OK.
										} finally {
											closeables.remove(client);
										}
									}
								}
							}).start();

							closeables.add(oos);
							closeables.add(outputStream);
						} catch (SocketException se) {
							shutdown();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}).start();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Pushes the specified cue to all the listening clients.
	 * 
	 * @param cue
	 *            the cue to push
	 */
	public void pushCue(AnalysisCue cue) {
		// Write the object to each of the streams...
		for (ObjectOutputStream oos : clientStreams) {
			try {
				oos.writeObject(cue);
			} catch (IOException ie) {
				// ... or try to, anyway.
				ie.printStackTrace();
			}
		}
	}

	/**
	 * Gets the number of clients currently connected to the server.
	 * 
	 * @return the number of clients
	 */
	public int getClientCount() {
		return clientStreams.size();
	}

	/**
	 * Gets the list of client hostnames.
	 * 
	 * @return the list of hostnames
	 */
	public List<String> getClientHostnames() {
		ArrayList<String> names = new ArrayList<String>();
		for (Socket client : clients) {
			names.add(client.getInetAddress().getHostName());
		}
		return names;
	}

	/**
	 * Performs closing actions for the server.
	 */
	public void shutdown() {
		for (Closeable closeable : closeables) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Socket client : clients) {
			try {
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCommitteeName() {
		return committeeName;
	}

	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
		for (ObjectOutputStream oos : clientStreams) {
			try {
				oos.writeObject(committeeName);
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}

}
