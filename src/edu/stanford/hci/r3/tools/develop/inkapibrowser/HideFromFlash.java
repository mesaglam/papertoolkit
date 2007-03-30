package edu.stanford.hci.r3.tools.develop.inkapibrowser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * This software is distributed under the BSD License. Copyright 2006, SantiagoSoft, Inc.
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 * 
 * An annotation specifying that this method should be exposed to the socket protocol.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HideFromFlash {
	public static String UNASSIGNED = "[unassigned]";

	// the name of the shortcut
	public String shortcutName() default UNASSIGNED;
}
