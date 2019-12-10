package org.geogebra.web.richtext;

import com.google.gwt.user.client.ui.Widget;

/** The interface to the Carota editor */
public interface Editor {

	interface EditorChangeListener {

		/**
		 * Called 0.5s after the last change in the editor state
		 * @param content the JSON encoded content of the editor
		 */
		void onContentChanged(String content);

		/**
		 * Called instantly on editor state change
		 * @param width current height of the editor in pixels
		 * @param height current width of the editor in pixels
		 */
		void onSizeChanged(int width, int height);
	}

	/**
	 * Return the GWT widget that represents the editor.
	 *
	 * @return a GWT widget
	 */
	Widget getWidget();

	/**
	 * Focuses the editor.
	 */
	void focus();

	/**
	 * Sets the editor change listener
	 */
	void addListener(EditorChangeListener listener);

	/**
	 * Set the content of the editor
	 * @param content JSON encoded string in Carota format
	 */
	void setContent(String content);
}
