package org.lcmmun.kiosk;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import javax.swing.ImageIcon;

/**
 * A delegate in an MUN setting. The delegate is a member of a {@link Committee}
 * and makes {@link Speech}es.
 * 
 * @author William Chargin
 * 
 */
public class Delegate implements Serializable, Comparable<Delegate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * The property change string for the name property.
	 */
	public static final String PROP_NAME = "PROP_NAME"; //$NON-NLS-1$

	/**
	 * The property change string for the icon property.
	 */
	public static final String PROP_ICON = "PROP_ICON"; //$NON-NLS-1$

	/**
	 * The property change string for the status property.
	 */
	public static final String PROP_STATUS = "PROP_STATUS"; //$NON-NLS-1$

	/**
	 * The {@code PropertyChangeSupport} for this object.
	 */
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * The affiliation of this delegate.
	 */
	private String affiliation;

	/**
	 * The name of this delegate. This is not the name of the individual person
	 * representing the delegate, but rather what the delegate represents (e.g.,
	 * the Delegate of Finland's {@code name} will not be "John"; instead, it
	 * would be "Finland"). The only exception is when delegates represent
	 * themselves (this happens rarely, and only in special committees, such as
	 * cabinets or project-based committees).
	 */
	private String name;

	/**
	 * The delegate icon. This is usually a flag.
	 */
	private DelegateIcon icon;

	/**
	 * This delegate's status in the committee (i.e., what powers does this
	 * delegate have?).
	 */
	private MemberStatus status;

	/**
	 * Creates the delegate with the required information, and the default
	 * {@link MemberStatus} of {@link MemberStatus#DEFAULT_STATUS
	 * DEFAULT_STATUS}.
	 * 
	 * @param name
	 *            the delegate's name
	 * @param icon
	 *            the delegate's icon
	 * 
	 * @see #name
	 * @see #icon
	 */
	public Delegate(String name, DelegateIcon icon) {
		this(name, icon, MemberStatus.DEFAULT_STATUS);
	}

	/**
	 * Creates the delegate with the required information.
	 * 
	 * @param name
	 *            the delegate's name
	 * @param icon
	 *            the delegate's icon
	 * @param MemberStatus
	 *            the delegate's status
	 * 
	 * @see #name
	 * @see #icon
	 * @see #status
	 */
	public Delegate(String name, DelegateIcon icon, MemberStatus status) {
		super();
		setName(name);
		this.icon = icon;
		this.status = status;
	}

	/**
	 * Adds the specified {@code PropertyChangeListener}.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Adds the specified {@code PropertyChangeListener} for the specified
	 * property.
	 * 
	 * @param property
	 *            the property to listen on
	 * @param pcl
	 *            the listener to add
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}

	@Override
	public int compareTo(Delegate o) {
		return name.compareToIgnoreCase(o.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Delegate)) {
			return false;
		}
		Delegate other = (Delegate) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equalsIgnoreCase(other.name)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the affiliation of this delegate.
	 * 
	 * @return the affiliation
	 */
	public String getAffiliation() {
		return affiliation;
	}

	/**
	 * Gets the {@link DelegateIcon}. From this object, the actual icon may be
	 * changed.
	 * 
	 * @return the delegate icon
	 */
	public DelegateIcon getDelegateIcon() {
		return icon;
	}

	/**
	 * Gets an {@link ImageIcon} with the large icon of this delegate.
	 * 
	 * @return an image icon
	 */
	public ImageIcon getIcon() {
		return new ImageIcon(getImage());
	}

	/**
	 * Gets the large image of this delegate.
	 * 
	 * @return the large image
	 */
	public Image getImage() {
		return icon.getLargeIcon();
	}

	/**
	 * Gets a small ({@value #MEDIUM_ICON_SIZE} by {@value #MEDIUM_ICON_SIZE})
	 * version of the icon.
	 * 
	 * @return the medium icon
	 */
	public ImageIcon getMediumIcon() {
		return new ImageIcon(getMediumImage());
	}

	/**
	 * Gets the medium image of this delegate.
	 * 
	 * @return the medium image
	 */
	public Image getMediumImage() {
		return icon.getMediumIcon();
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets a small ({@value #SMALL_ICON_SIZE} by {@value #SMALL_ICON_SIZE})
	 * version of the icon.
	 * 
	 * @return the small icon
	 */
	public ImageIcon getSmallIcon() {
		return new ImageIcon(getSmallImage());
	}

	/**
	 * Gets the small image of this delegate.
	 * 
	 * @return the small image
	 */
	public Image getSmallImage() {
		return icon.getSmallIcon();
	}

	public MemberStatus getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((name == null) ? 0 : name.toLowerCase().hashCode());
		return result;
	}

	/**
	 * Removes the specified {@code PropertyChangeListener}.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Removes the specified {@code PropertyChangeListener}, provided that it
	 * was added for this specific property.
	 * 
	 * @param propertyName
	 *            the property on which the listener was originally added
	 * @param listener
	 *            the listener to remove
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Sets the affiliation of this delegate.
	 * 
	 * @param affiliation
	 *            the affiliation
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		pcs.firePropertyChange(PROP_NAME, oldName, this.name);
	}

	public void setStatus(MemberStatus status) {
		MemberStatus oldStatus = this.status;
		this.status = status;
		pcs.firePropertyChange(PROP_STATUS, oldStatus, status);
	}

	@Override
	public String toString() {
		return name;
	}

}
