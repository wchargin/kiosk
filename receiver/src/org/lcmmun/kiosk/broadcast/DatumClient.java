package org.lcmmun.kiosk.broadcast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.event.EventListenerList;

import org.lcmmun.kiosk.broadcast.gui.DatumHolder;
import org.lcmmun.kiosk.broadcast.gui.DatumView;

/**
 * A broadcast client, which attaches to a {@link DatumServer} and displays the
 * {@link BasicDatum} objects it receives.
 * 
 * @author William Chargin
 * 
 */
public class DatumClient {

	/**
	 * The list of GUI objects (datum holders).
	 */
	private final ArrayList<DatumHolder> holders = new ArrayList<DatumHolder>();

	/**
	 * The list of listeners on this object.
	 */
	private final EventListenerList listenerList = new EventListenerList();

	/**
	 * Directly before the socket closes, it will send this byte of data to
	 * indicate that it will close.
	 */
	protected static final byte CLOSE_COMMAND = 0;

	/**
	 * The socket currently connected.
	 */
	private Socket socket;

	/**
	 * The input stream to the socket.
	 */
	private InputStream inputStream;

	/**
	 * The object input stream to the socket.
	 */
	private ObjectInputStream ois;

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
	public DatumClient(String host) throws ConnectException,
			UnknownHostException {
		try {
			socket = new Socket(host, DatumServer.PORT);
			inputStream = socket.getInputStream();
			ois = new ObjectInputStream(inputStream);

			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							Object o = ois.readObject();
							if (!(o instanceof Datum)) {
								continue;
							}
							Datum d = (Datum) o;
							for (DatumHolder holder : holders) {
								holder.addDatumView(new DatumView(d));
							}
						} catch (ClassNotFoundException cnfe) {
							cnfe.printStackTrace();
						} catch (EOFException e) {
							// The server shut down.
							shutdown();
						} catch (SocketException e) {
							// The server shut down.
							shutdown();
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
	 * Shuts down the server.
	 */
	private void shutdown() {
		// Close stuff.
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
			} catch (Exception e) {
				// That's okay. Continue.
			}
		}
		try {
			socket.close();
		} catch (Exception e) {
			// Okay.
		}
		
		// Fire close events.
		for (ActionListener al : listenerList
				.getListeners(ActionListener.class)) {
			al.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, new String()));
			listenerList.remove(ActionListener.class, al);
		}
	}

	/**
	 * Adds the given action listener to the list of listeners.
	 * 
	 * @param al
	 *            the listener to add
	 */
	public void addActionListener(ActionListener al) {
		listenerList.add(ActionListener.class, al);
	}

	/**
	 * Removes the given action listener from the list of listeners.
	 * 
	 * @param al
	 *            the listener to remove
	 */
	public void removeActionListener(ActionListener al) {
		listenerList.remove(ActionListener.class, al);
	}

	/**
	 * Creates a {@link DatumHolder} panel to display the datum objects that the
	 * client receives. The object will be empty, but items will be added to it
	 * as they are received.
	 * 
	 * @return a new {@code DatumHolder}
	 */
	public synchronized DatumHolder createPanel() {
		DatumHolder holder = new DatumHolder();
		holders.add(holder);
		return holder;
	}

}
