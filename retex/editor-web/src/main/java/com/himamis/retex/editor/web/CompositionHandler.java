package com.himamis.retex.editor.web;

import com.google.gwt.event.shared.EventHandler;

public interface CompositionHandler extends EventHandler {

	/**
	 * Called when CompositionUpdateEvent is fired.
	 * 
	 * @param event
	 *            the {@link CompositionUpdateEvent} that was fired
	 */
	void onCompositionUpdate(CompositionUpdateEvent event);


}
