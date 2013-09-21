package org.lcmmun.kiosk;

import java.io.Serializable;

import org.lcmmun.kiosk.Speech.YieldType;

/**
 * A yield from a GSL speech.
 * 
 * @author William Chargin
 * 
 */
public class Yield implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The type of yield.
	 */
	public final YieldType type;

	/**
	 * The delegate yielded to. Only applies when {@link #type} is
	 * {@link YieldType#DELEGATE}.
	 */
	public final Delegate target;

	/**
	 * Creates the yield with the given type.
	 * 
	 * @param type
	 *            any type other than {@link YieldType#DELEGATE}; use the
	 *            {@link #Yield(Delegate)} constructor instead
	 * @throws UnsupportedOperationException
	 *             if the type is {@link YieldType#DELEGATE}
	 */
	public Yield(YieldType type) throws UnsupportedOperationException {
		if (type == YieldType.DELEGATE) {
			throw new UnsupportedOperationException(
					"Yields of type DELEGATE must be created with Yield(Delegate) constructor"); //$NON-NLS-1$
		} else {
			this.type = type;
			this.target = null;
		}
	}

	/**
	 * Creates the yield of type {@link YieldType#DELEGATE} to the given
	 * delegate.
	 * 
	 * @param target
	 *            the target delegate
	 */
	public Yield(Delegate target) {
		type = YieldType.DELEGATE;
		this.target = target;
	}

}
