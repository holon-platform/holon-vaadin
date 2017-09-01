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
package com.holonplatform.vaadin.components.builders;

import java.util.Arrays;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.ItemSet.ItemCaptionGenerator;

/**
 * Builder to create selection {@link Input}s.
 * 
 * @param <T> Value type
 * @param <C> Input type
 * @param <S> Selection type
 * @param <ITEM> Selection items type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface SelectInputBuilder<T, C extends Input<T>, S, ITEM, B extends SelectInputBuilder<T, C, S, ITEM, B>>
		extends BaseSelectInputBuilder<T, C, S, ITEM, B> {

	/**
	 * Set the given items as selection item set.
	 * <p>
	 * When selection items are explicitly setted, any item data source is ignored.
	 * </p>
	 * @param items Items to set (not null)
	 * @return this
	 */
	B items(Iterable<ITEM> items);

	/**
	 * Set the given items as selection item set.
	 * <p>
	 * When selection items are explicitly setted, any item data source is ignored.
	 * </p>
	 * @param items Items to set (not null)
	 * @return this
	 */
	@SuppressWarnings("unchecked")
	default B items(ITEM... items) {
		ObjectUtils.argumentNotNull(items, "Items must be not null");
		return items(Arrays.asList(items));
	}

	/**
	 * Add a selection item to current selection item set.
	 * <p>
	 * When selection items are explicitly setted, any item data source is ignored.
	 * </p>
	 * @param item Item to add (not null)
	 * @return this
	 */
	B addItem(ITEM item);

	/**
	 * Add a selection item to current selection item set, providing an explicit item caption.
	 * <p>
	 * When selection items are explicitly setted, any item data source is ignored.
	 * </p>
	 * <p>
	 * Note that if an {@link ItemCaptionGenerator} is setted, the explicit caption is overridden by the caption
	 * provided by the generator.
	 * </p>
	 * @param item Item to add (not null)
	 * @param caption Localizable item caption
	 * @return this
	 */
	default B addItem(ITEM item, Localizable caption) {
		addItem(item);
		return itemCaption(item, caption);
	}

	/**
	 * Add a selection item to current selection item set, providing an explicit item caption.
	 * <p>
	 * When selection items are explicitly setted, any item data source is ignored.
	 * </p>
	 * <p>
	 * Note that if an {@link ItemCaptionGenerator} is setted, the explicit caption is overridden by the caption
	 * provided by the generator.
	 * </p>
	 * @param item Item to add (not null)
	 * @param caption Item caption
	 * @param messageCode Item caption translation code
	 * @return this
	 */
	default B addItem(ITEM item, String caption, String messageCode) {
		return addItem(item, Localizable.builder().message(caption).messageCode(messageCode).build());
	}

	/**
	 * Add a selection item to current selection item set, providing an explicit item caption.
	 * <p>
	 * When selection items are explicitly setted, any item data source is ignored.
	 * </p>
	 * <p>
	 * Note that if an {@link ItemCaptionGenerator} is setted, the explicit caption is overridden by the caption
	 * provided by the generator.
	 * </p>
	 * @param item Item to add (not null)
	 * @param caption Item caption
	 * @return this
	 */
	default B addItem(ITEM item, String caption) {
		return addItem(item, Localizable.builder().message(caption).build());
	}

}
