/*
 * Copyright 2000-2016 Holon TDCN.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.holonplatform.core.i18n.Caption;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.AnnotationUtils;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.ItemSet;
import com.holonplatform.vaadin.components.Selectable;
import com.holonplatform.vaadin.components.builders.BaseSelectInputBuilder.RenderingMode;
import com.vaadin.data.Converter;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValueContext;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.Resource;
import com.vaadin.server.SerializableFunction;
import com.vaadin.shared.Registration;
import com.vaadin.ui.ComboBox.CaptionFilter;
import com.vaadin.ui.Component;

/**
 * Abstract select field implementation.
 * 
 * @param <T> Actual field type
 * @param <S> Internal select type
 * @param <ITEM> Selection items type
 */
public abstract class AbstractSelectField<T, S, ITEM, I, F extends HasValue<I> & Component>
		extends AbstractCustomField<T, I, F> implements Selectable<S>, ItemSet {

	private static final long serialVersionUID = -2069614658878818456L;

	/**
	 * Item converter
	 */
	private Converter<S, ITEM> itemConverter;

	/**
	 * Rendering mode
	 */
	private final RenderingMode renderingMode;

	/**
	 * Selection listeners
	 */
	private final List<SelectionListener<S>> selectionListeners = new LinkedList<>();

	/**
	 * Item caption generator
	 */
	private ItemCaptionGenerator<ITEM> itemCaptionGenerator;

	/**
	 * Item icon generator
	 */
	private ItemIconGenerator<ITEM> itemIconGenerator;

	/**
	 * Explicit item captions
	 */
	protected final Map<ITEM, Localizable> explicitItemCaptions = new HashMap<>(8);

	/**
	 * Explicit item icons
	 */
	protected final Map<ITEM, Resource> explicitItemIcons = new HashMap<>(8);

	/**
	 * Constructor
	 * @param type Select field type
	 * @param renderingMode UI rendering mode
	 */
	public AbstractSelectField(Class<? extends T> type, RenderingMode renderingMode) {
		super(type, false);
		this.renderingMode = renderingMode;

		addStyleName("h-select", false);

		init();
	}

	public abstract void setDataProvider(DataProvider<ITEM, ?> dataProvider);
	
	public abstract void setDataProvider(DataProvider<ITEM, ?> dataProvider, SerializableFunction<String, ?> filterConverter);

	public abstract Optional<DataProvider<ITEM, ?>> getDataProvider();

	public abstract void setItems(Collection<ITEM> items);
	
	public abstract void setItems(Collection<ITEM> items, CaptionFilter filter);

	/**
	 * Get the select rendering mode
	 * @return Rendering mode
	 */
	protected RenderingMode getRenderingMode() {
		return renderingMode;
	}

	public Optional<Converter<S, ITEM>> getItemConverter() {
		return Optional.ofNullable(itemConverter);
	}

	public void setItemConverter(Converter<S, ITEM> itemConverter) {
		this.itemConverter = itemConverter;
	}

	protected Converter<S, ITEM> requireItemConverter() {
		return getItemConverter()
				.orElseThrow(() -> new IllegalStateException("Item converter function not configured"));
	}

	protected S toSelection(ITEM item) {
		return requireItemConverter().convertToPresentation(item, new ValueContext(this, this, findLocale()));
	}

	protected ITEM toItem(S selection) {
		return requireItemConverter().convertToModel(selection, new ValueContext(this, this, findLocale())).getOrThrow(
				m -> new IllegalArgumentException("Failed to convert selection value [" + selection + "]: " + m));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemSet#refresh()
	 */
	@Override
	public void refresh() {
		getDataProvider().ifPresent(d -> d.refreshAll());
	}

	/**
	 * Get the item caption generator
	 * @return the ItemCaptionGenerator
	 */
	public Optional<ItemCaptionGenerator<ITEM>> getItemCaptionGenerator() {
		return Optional.ofNullable(itemCaptionGenerator);
	}

	/**
	 * Set the item caption generator
	 * @param itemCaptionGenerator the ItemCaptionGenerator to set
	 */
	public void setItemCaptionGenerator(ItemCaptionGenerator<ITEM> itemCaptionGenerator) {
		this.itemCaptionGenerator = itemCaptionGenerator;
	}

	/**
	 * Get the item icon generator
	 * @return the ItemIconGenerator
	 */
	public Optional<ItemIconGenerator<ITEM>> getItemIconGenerator() {
		return Optional.ofNullable(itemIconGenerator);
	}

	/**
	 * Set the item icon generator
	 * @param itemIconGenerator the ItemIconGenerator to set
	 */
	public void setItemIconGenerator(ItemIconGenerator<ITEM> itemIconGenerator) {
		this.itemIconGenerator = itemIconGenerator;
	}

	/**
	 * Set an explicit caption for given item.
	 * @param item Item to set the caption for
	 * @param caption Caption to set (not null)
	 */
	public void setItemCaption(ITEM item, Localizable caption) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		if (caption != null) {
			explicitItemCaptions.put(item, caption);
		} else {
			explicitItemCaptions.remove(item);
		}
	}

	/**
	 * Set an explicit icon for given item.
	 * @param item Item to set the caption for
	 * @param icon Icon to set (not null)
	 */
	public void setItemIcon(ITEM item, Resource icon) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");
		if (icon != null) {
			explicitItemIcons.put(item, icon);
		} else {
			explicitItemIcons.remove(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#addSelectionListener(com.holonplatform.vaadin.components.
	 * Selectable.SelectionListener)
	 */
	@Override
	public Registration addSelectionListener(SelectionListener<S> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		selectionListeners.add(selectionListener);
		return () -> selectionListeners.remove(selectionListener);
	}

	/**
	 * Triggers registered {@link SelectionListener}s.
	 */
	protected void fireSelectionListeners() {
		selectionListeners.forEach(l -> l.onSelectionChange(this));
	}

	/**
	 * Generate the select item icon for given <code>item</code>.
	 * @param item Item to generate the icon for
	 * @return Item icon (may be null)
	 */
	protected Resource generateItemIcon(ITEM item) {
		if (item != null) {
			return getItemIconGenerator().map(g -> g.getItemIcon(item)).orElse(getDefaultItemIcon(item));
		}
		return null;
	}

	/**
	 * Get the default select item icon for given <code>item</code>.
	 * @param item Item
	 * @return Default item icon
	 */
	protected Resource getDefaultItemIcon(ITEM item) {
		if (item != null) {
			return explicitItemIcons.get(item);
		}
		return null;
	}

	/**
	 * Generate the select item caption for given <code>item</code>.
	 * @param item Item to generate the caption for
	 * @return Item caption (not null)
	 */
	protected String generateItemCaption(ITEM item) {
		if (item != null) {
			return getItemCaptionGenerator().map(g -> g.getItemCaption(item)).orElse(getDefaultItemCaption(item));
		}
		return "";
	}

	/**
	 * Get the default select item caption for given <code>item</code>.
	 * @param item Item
	 * @return Default item caption
	 */
	protected String getDefaultItemCaption(ITEM item) {
		if (item != null) {
			// check explicit caption
			Localizable caption = explicitItemCaptions.get(item);
			if (caption != null) {
				return LocalizationContext.translate(caption, true);
			}
			// check Localizable
			Localizable lv = null;
			if (Localizable.class.isAssignableFrom(item.getClass())) {
				lv = (Localizable) item;
			} else {
				// check Caption annotation on enums
				if (item.getClass().isEnum()) {
					Enum<?> enm = (Enum<?>) item;
					try {
						final java.lang.reflect.Field fld = item.getClass().getField(enm.name());
						if (fld.isAnnotationPresent(Caption.class)) {
							lv = Localizable.builder().message(fld.getAnnotation(Caption.class).value()).messageCode(
									AnnotationUtils.getStringValue(fld.getAnnotation(Caption.class).messageCode()))
									.build();
						}
					} catch (@SuppressWarnings("unused") Exception e) {
						// ignore
					}
				}
			}
			if (lv != null) {
				return LocalizationContext.translate(lv, true);
			}
			// ID toString
			return item.toString();
		}
		return "";
	}

}
