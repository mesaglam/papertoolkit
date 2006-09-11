package edu.stanford.hci.r3.actions.remote.transport;

import java.io.IOException;

import edu.stanford.hci.r3.actions.R3Action;

/**
 * <p>
 * Given an R3Action, the messengers will package it up and send it across the wire, so that the
 * receiver can perform the designated action.
 * </p>
 * <p>
 * <span class="BSDLicense"> This software is distributed under the <a
 * href="http://hci.stanford.edu/research/copyright.txt">BSD License</a>. </span>
 * </p>
 * 
 * @author <a href="http://graphics.stanford.edu/~ronyeh">Ron B Yeh</a> (ronyeh(AT)cs.stanford.edu)
 */
public interface ActionMessenger {

	/**
	 * 
	 */
	public void destroy();

	/**
	 * @param action
	 */
	public void sendAction(R3Action action) throws IOException;
}
