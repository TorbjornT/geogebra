package org.geogebra.web.html5.gui.accessibility;

import java.util.Collections;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.ScreenReaderBuilder;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.util.sliderPanel.SliderW;
import org.geogebra.web.html5.util.sliderPanel.SliderWI;

/**
 * Accessibility adapter for sliders
 */
public class AccessibleNumeric implements AccessibleWidget, HasSliders {

	private SliderW slider;
	private AccessibilityView view;
	private GeoNumeric numeric;

	/**
	 * @param geo
	 *            numeric
	 * @param widgetFactory
	 *            factory for sliders
	 * @param view
	 *            accessibility view
	 */
	public AccessibleNumeric(GeoNumeric geo, WidgetFactory widgetFactory,
			final AccessibilityView view) {
		this.numeric = geo;
		this.view = view;
		slider = widgetFactory.makeSlider(0, this);
		update();
	}

	@Override
	public List<SliderW> getWidgets() {
		return Collections.singletonList(slider);
	}

	@Override
	public void onValueChange(int index, double value) {
		view.select(numeric);

		double step = numeric.getAnimationStep();
		double intervalMin = numeric.getIntervalMin();
		double numericValue = value * step + intervalMin;
		numeric.setValue(numericValue);
		numeric.updateRepaint();
		slider.getElement().focus();
		updateValueText();
	}

	private void updateValueText() {
		slider.getElement().setAttribute("aria-valuetext", getAriaValueText());
	}

	private String getAriaValueText() {
		ScreenReaderBuilder builder = new ScreenReaderBuilder();

		builder.append(numeric.toValueString(StringTemplate.screenReader));
		builder.appendSpace();

		if (!StringUtil.empty(numeric.getCaptionSimple())) {
			String caption = numeric.getCaptionSimple();
			if (numeric.getCaptionSimple().indexOf("=") > 0) {
				caption = numeric.getCaptionSimple().substring(0,
						numeric.getCaptionSimple().indexOf("="));
			}
			builder.append(caption);
		} else {
			builder.append(numeric.getLabelSimple());
		}

		builder.endSentence();

		return builder.toString();
	}

	@Override
	public void update() {
		updateNumericRange(slider);
	}

	private void updateNumericRange(SliderWI range) {
		double intervalMin = numeric.getIntervalMin();
		double intervalMax = numeric.getIntervalMax();
		double step = numeric.getAnimationStep();
		double value = numeric.getValue();

		range.setMinimum(0);
		range.setMaximum(Math.round((intervalMax - intervalMin) / step));
		range.setStep(1);
		range.setValue((double) Math.round((value - intervalMin) / step));

		updateValueText();
	}

	@Override
	public void setFocus(boolean focus) {
		slider.setFocus(focus);
	}

}