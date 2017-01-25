package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * @author Zbynek
 */
public class MarblePanel extends FlowPanel {
	private Marble marble;
	private boolean selected = false;
	/** warning triangle / help button */
	ToggleButton btnHelpToggle;
	/** av item */
	RadioTreeItem item;

	/**
	 * @param item
	 *            AV item
	 */
	public MarblePanel(RadioTreeItem item) {
		this.item = item;
		marble = new Marble(item);
		marble.setStyleName("marble");
		marble.setEnabled(shouldShowMarble());
		marble.setChecked(item.geo.isEuclidianVisible());

		addStyleName("marblePanel");
		add(marble);
		update();
	}

	/**
	 * @param selected
	 *            whether this should be highlighted
	 */
	public void setHighlighted(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Update marble visibility and highlighting
	 */
	public void update() {
		marble.setEnabled(shouldShowMarble());

		marble.setChecked(item.geo.isEuclidianVisible());

		setHighlighted(selected);
	}

	private boolean shouldShowMarble() {
		return item.geo.isEuclidianShowable()
				&& (!item.getApplication().isExam()
						|| item.getApplication().enableGraphing());
	}

	/**
	 * @param x
	 *            pointer x-coord
	 * @param y
	 *            pointer y-coord
	 * @return whether pointer is over this
	 */
	public boolean isHit(int x, int y) {
		return x > getAbsoluteLeft()
				&& x < getAbsoluteLeft() + getOffsetWidth()
				&& y < getAbsoluteTop() + getOffsetHeight();
	}

	/**
	 * @param warning
	 *            whether warning triangle should be visible
	 */
	public void updateIcons(boolean warning) {
		if (btnHelpToggle == null) {
			btnHelpToggle = new ToggleButton();
			btnHelpToggle.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					item.showCurrentError();

				}
			});
		}
		// if (!warning) {
		// clearErrorLabel();
		// }
		if (warning) {
			remove(marble);
			add(btnHelpToggle);
			addStyleName("error");
		} else {
			add(marble);
			marble.setEnabled(shouldShowMarble());
			remove(btnHelpToggle);
			removeStyleName("error");
		}
		btnHelpToggle.getUpFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));
		// new
		// Image(AppResources.INSTANCE.inputhelp_left_20x20().getSafeUri().asString()),
		btnHelpToggle.getDownFace().setImage(new NoDragImage(
				(warning ? GuiResourcesSimple.INSTANCE.icon_dialog_warning()
						: GuiResources.INSTANCE.icon_help()).getSafeUri()
								.asString(),
				24));

	}
}