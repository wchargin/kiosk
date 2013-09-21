package org.lcmmun.kiosk.gui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the version of a class.
 * 
 * @author William Chargin
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Version {

	/**
	 * The version of this class.
	 * 
	 * @return the version string
	 */
	public String value() default "1.0";

}
