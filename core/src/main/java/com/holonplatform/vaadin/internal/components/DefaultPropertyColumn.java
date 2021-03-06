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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder.ColumnHeaderMode;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.vaadin.data.HasValue;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueProvider;
import com.vaadin.server.Resource;
import com.vaadin.ui.Component;
import com.vaadin.ui.renderers.Renderer;

/**
 * Default {@link PropertyColumn} implementation.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * 
 * @since 5.0.0
 */
public class DefaultPropertyColumn<T, P> implements PropertyColumn<T, P> {

	private static final long serialVersionUID = -4394599534028523259L;

	/**
	 * Virtual
	 */
	private final boolean virtual;

	/**
	 * Display position
	 */
	private DisplayPosition displayPosition;

	/**
	 * Display position relative property
	 */
	private P displayRelativeTo;

	/**
	 * Display position relative column id
	 */
	private String displayRelativeToColumnId;

	/**
	 * Caption (header)
	 */
	private Localizable caption;

	/**
	 * Header mode
	 */
	private ColumnHeaderMode columnHeaderMode;

	/**
	 * Alignment
	 */
	private ColumnAlignment alignment;

	/**
	 * Width in pixels
	 */
	private int width = -1;
	/**
	 * Minimum width in pixels
	 */
	private int minWidth = -1;
	/**
	 * Maximum width in pixels
	 */
	private int maxWidth = -1;

	/**
	 * Expand ratio
	 */
	private int expandRatio = -1;

	/**
	 * Min width form content
	 */
	private boolean minimumWidthFromContent = true;

	/**
	 * Editable
	 */
	private boolean editable = true;

	/**
	 * Required
	 */
	private boolean required = false;

	/**
	 * Required validation message
	 */
	private Localizable requiredMessage;

	/**
	 * Editor field
	 */
	private HasValue<?> editor;

	/**
	 * Validators
	 */
	private List<Validator<?>> validators = new LinkedList<>();

	/**
	 * Hidden
	 */
	private boolean hidden = false;
	/**
	 * Hidable
	 */
	private boolean hidable = true;

	/**
	 * Hide toggle menu caption
	 */
	private Localizable hidingToggleCaption;

	/**
	 * Resizable
	 */
	private boolean resizable = true;

	/**
	 * Icon
	 */
	private Resource icon;

	/**
	 * Style generator
	 */
	private CellStyleGenerator<T, P> style;

	/**
	 * Presentation provider
	 */
	private ValueProvider<?, ?> presentationProvider;

	/**
	 * Sort generator
	 */
	private PropertySortGenerator<P> propertySortGenerator;

	/**
	 * Renderer
	 */
	private Renderer<?> renderer;

	/**
	 * Constructor.
	 * @param property Property
	 * @param virtual Whether the property is virtual
	 */
	public DefaultPropertyColumn(P property, boolean virtual) {
		super();
		this.virtual = virtual;
		if (property != null) {
			if (Localizable.class.isAssignableFrom(property.getClass())) {
				this.caption = (Localizable) property;
			}
			if ((this.caption == null || this.caption.getMessage() == null)
					&& Path.class.isAssignableFrom(property.getClass())) {
				this.caption = Localizable.builder().message(((Path<?>) property).getName()).build();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#isVirtual()
	 */
	@Override
	public boolean isVirtual() {
		return virtual;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getDisplayPosition()
	 */
	@Override
	public DisplayPosition getDisplayPosition() {
		return (displayPosition == null) ? DisplayPosition.DEFAULT : displayPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.PropertyColumn#setDisplayPosition(com.holonplatform.vaadin.internal.
	 * components.PropertyColumn.DisplayPosition)
	 */
	@Override
	public void setDisplayPosition(DisplayPosition displayPosition) {
		this.displayPosition = displayPosition;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getDisplayRelativeTo()
	 */
	@Override
	public Optional<P> getDisplayRelativeTo() {
		return Optional.ofNullable(displayRelativeTo);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setDisplayRelativeTo(java.lang.Object)
	 */
	@Override
	public void setDisplayRelativeTo(P property) {
		this.displayRelativeTo = property;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getDisplayRelativeToColumnId()
	 */
	@Override
	public Optional<String> getDisplayRelativeToColumnId() {
		return Optional.ofNullable(displayRelativeToColumnId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setDisplayRelativeToColumnId(java.lang.String)
	 */
	@Override
	public void setDisplayRelativeToColumnId(String columnId) {
		this.displayRelativeToColumnId = columnId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.support.PropertyColumn#getCaption()
	 */
	@Override
	public Localizable getCaption() {
		return caption;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.support.PropertyColumn#setCaption(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public void setCaption(Localizable caption) {
		this.caption = caption;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getColumnHeaderMode()
	 */
	@Override
	public ColumnHeaderMode getColumnHeaderMode() {
		return columnHeaderMode != null ? columnHeaderMode : ColumnHeaderMode.TEXT;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setColumnHeaderMode(com.holonplatform.vaadin.
	 * components.builders.ItemListingBuilder.ColumnHeaderMode)
	 */
	@Override
	public void setColumnHeaderMode(ColumnHeaderMode columnHeaderMode) {
		this.columnHeaderMode = columnHeaderMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ColumnAlignment getAlignment() {
		return alignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAlignment(ColumnAlignment alignment) {
		this.alignment = alignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMinWidth() {
		return minWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getExpandRatio() {
		return expandRatio;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setExpandRatio(int expandRatio) {
		this.expandRatio = expandRatio;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMinimumWidthFromContent() {
		return minimumWidthFromContent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinimumWidthFromContent(boolean minimumWidthFromContent) {
		this.minimumWidthFromContent = minimumWidthFromContent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getEditor()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <E extends HasValue<?> & Component> Optional<E> getEditor() {
		final E hvc = (E) editor;
		return Optional.ofNullable(hvc);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setEditor(com.vaadin.data.HasValue)
	 */
	@Override
	public <E extends HasValue<?> & Component> void setEditor(E editor) {
		this.editor = editor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#addValidator(com.vaadin.data.Validator)
	 */
	@Override
	public void addValidator(Validator<?> validator) {
		ObjectUtils.argumentNotNull(validator, "Property validator must be not null");
		this.validators.add(validator);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getValidators()
	 */
	@Override
	public List<Validator<?>> getValidators() {
		return validators;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		this.required = required;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getRequiredMessage()
	 */
	@Override
	public Localizable getRequiredMessage() {
		return requiredMessage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setRequiredMessage(com.holonplatform.core.i18n.
	 * Localizable)
	 */
	@Override
	public void setRequiredMessage(Localizable message) {
		this.requiredMessage = message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHidable() {
		return hidable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHidable(boolean hidable) {
		this.hidable = hidable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.support.PropertyColumn#getHidingToggleCaption()
	 */
	@Override
	public Optional<Localizable> getHidingToggleCaption() {
		return Optional.ofNullable(hidingToggleCaption);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.support.PropertyColumn#setHidingToggleCaption(com.holonplatform.
	 * core.i18n.Localizable)
	 */
	@Override
	public void setHidingToggleCaption(Localizable hidingToggleCaption) {
		this.hidingToggleCaption = hidingToggleCaption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isResizable() {
		return resizable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Resource> getIcon() {
		return Optional.ofNullable(icon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setIcon(Resource icon) {
		this.icon = icon;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<CellStyleGenerator<T, P>> getStyle() {
		return Optional.ofNullable(style);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStyle(CellStyleGenerator<T, P> style) {
		this.style = style;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getPresentationProvider()
	 */
	@Override
	public Optional<ValueProvider<?, ?>> getPresentationProvider() {
		return Optional.ofNullable(presentationProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setPresentationProvider(com.vaadin.data.
	 * ValueProvider)
	 */
	@Override
	public void setPresentationProvider(ValueProvider<?, ?> presentationProvider) {
		this.presentationProvider = presentationProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getRenderer()
	 */
	@Override
	public Optional<Renderer<?>> getRenderer() {
		return Optional.ofNullable(renderer);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#setRenderer(com.vaadin.ui.renderers.Renderer)
	 */
	@Override
	public void setRenderer(Renderer<?> renderer) {
		this.renderer = renderer;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.PropertyColumn#getPropertySortGenerator()
	 */
	@Override
	public Optional<PropertySortGenerator<P>> getPropertySortGenerator() {
		return Optional.ofNullable(propertySortGenerator);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.internal.components.PropertyColumn#setPropertySortGenerator(com.holonplatform.vaadin.
	 * data.ItemDataSource.PropertySortGenerator)
	 */
	@Override
	public void setPropertySortGenerator(PropertySortGenerator<P> propertySortGenerator) {
		this.propertySortGenerator = propertySortGenerator;
	}

}
