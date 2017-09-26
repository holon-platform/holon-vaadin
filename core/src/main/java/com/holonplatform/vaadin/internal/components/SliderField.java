/*
 * Copyright 2016-2017 Axioma srl.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.vaadin.internal.components;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.internal.utils.TypeUtils;
import com.holonplatform.vaadin.internal.converters.NumberToNumberConverter;
import com.vaadin.data.ValueContext;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.Slider;

/**
 * Default Slider Field implementation.
 * 
 * @param <T> Number type
 * 
 * @since 5.0.0
 */
public class SliderField<T extends Number> extends AbstractCustomField<T, Double, Slider> {

	private static final long serialVersionUID = -5951702885128017698L;

	private NumberToNumberConverter<Double, T> converter;

	/**
	 * Constructor
	 * @param type Concrete field type
	 */
	public SliderField(Class<? extends T> type) {
		super(type);

		addStyleName("h-field");
		addStyleName("h-sliderfield");
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#buildInternalField(java.lang.Class)
	 */
	@Override
	protected Slider buildInternalField(Class<? extends T> type) {
		Slider slider = new Slider();
		if (TypeUtils.isIntegerNumber(getType())) {
			slider.setResolution(0);
		}
		return slider;
	}

	protected NumberToNumberConverter<Double, T> getConverter() {
		if (converter == null) {
			converter = new NumberToNumberConverter<>(Double.class, getType());
		}
		return converter;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#fromInternalValue(java.lang.Object)
	 */
	@Override
	protected T fromInternalValue(Double value) {
		return getConverter().convertToModel(value, new ValueContext(findLocale()))
				.getOrThrow(msg -> new IllegalArgumentException(msg));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.AbstractCustomField#toInternalValue(java.lang.Object)
	 */
	@Override
	protected Double toInternalValue(T value) {
		return getConverter().convertToPresentation(value, new ValueContext(findLocale()));
	}

	/**
	 * Gets the maximum slider value
	 * @return the largest value the slider can have
	 */
	public T getMax() {
		return fromInternalValue(getInternalField().getMax());
	}

	/**
	 * Set the maximum slider value. If the current value of the slider is larger than this, the value is set to the new
	 * maximum.
	 * @param max The new maximum slider value
	 */
	public void setMax(T max) {
		ObjectUtils.argumentNotNull(max, "Max value must be not null");
		getInternalField().setMax(toInternalValue(max));
	}

	/**
	 * Gets the minimum slider value
	 * @return the smallest value the slider can have
	 */
	public T getMin() {
		return fromInternalValue(getInternalField().getMin());
	}

	/**
	 * Set the minimum slider value. If the current value of the slider is smaller than this, the value is set to the
	 * new minimum.
	 * @param min The new minimum slider value
	 */
	public void setMin(T min) {
		ObjectUtils.argumentNotNull(min, "Min value must be not null");
		getInternalField().setMin(toInternalValue(min));
	}

	/**
	 * Get the current orientation of the slider (horizontal or vertical).
	 * @return {@link SliderOrientation#HORIZONTAL} or {@link SliderOrientation#VERTICAL}
	 */
	public SliderOrientation getOrientation() {
		return getInternalField().getOrientation();
	}

	/**
	 * Set the orientation of the slider.
	 * @param orientation The new orientation, either {@link SliderOrientation#HORIZONTAL} or
	 *        {@link SliderOrientation#VERTICAL}
	 */
	public void setOrientation(SliderOrientation orientation) {
		getInternalField().setOrientation(orientation);
	}

	/**
	 * Get the current resolution of the slider. The resolution is the number of digits after the decimal point.
	 * @return resolution
	 */
	public int getResolution() {
		return getInternalField().getResolution();
	}

	/**
	 * Set a new resolution for the slider. The resolution is the number of digits after the decimal point.
	 * @param resolution The resolution to set. Ignored for integer value types
	 */
	public void setResolution(int resolution) {
		if (!TypeUtils.isIntegerNumber(getType())) {
			getInternalField().setResolution(resolution);
		}
	}

}
