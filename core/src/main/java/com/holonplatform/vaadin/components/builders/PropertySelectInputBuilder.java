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
package com.holonplatform.vaadin.components.builders;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.vaadin.components.Input;
import com.holonplatform.vaadin.components.Selectable;
import com.holonplatform.vaadin.data.ItemConverter;
import com.holonplatform.vaadin.data.ItemDataProvider;

/**
 * Builder to create selection {@link Input}s with {@link Property} data source support.
 * 
 * @param <T> Value type
 * @param <C> Component type
 * @param <S> Selection type
 * @param <B> Concrete builder type
 * 
 * @since 5.0.0
 */
public interface PropertySelectInputBuilder<T, C extends Input<T>, S, B extends PropertySelectInputBuilder<T, C, S, B>>
		extends BaseSelectInputBuilder<T, C, S, PropertyBox, B> {

	/**
	 * Set the selection items data provider to obtain items.
	 * @param dataProvider Items data provider (not null)
	 * @return this
	 */
	B dataSource(ItemDataProvider<PropertyBox> dataProvider);

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param <P> Property type
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param properties Item property set (not null)
	 * @param queryConfigurationProviders Optional additional {@link QueryConfigurationProvider}s
	 * @return this
	 */
	@SuppressWarnings("rawtypes")
	default <P extends Property> B dataSource(Datastore datastore, DataTarget<?> dataTarget, Iterable<P> properties,
			QueryConfigurationProvider... queryConfigurationProviders) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return dataSource(ItemDataProvider.create(datastore, dataTarget,
				(properties instanceof PropertySet) ? (PropertySet<?>) properties : PropertySet.of(properties),
				queryConfigurationProviders));
	}

	/**
	 * Use given {@link Datastore} with given <code>dataTarget</code> as items data source.
	 * @param datastore Datastore to use (not null)
	 * @param dataTarget Data target to use to load items (not null)
	 * @param properties Item property set (not null)
	 * @return this
	 */
	default B dataSource(Datastore datastore, DataTarget<?> dataTarget, Property<?>... properties) {
		ObjectUtils.argumentNotNull(properties, "Properties must be not null");
		return dataSource(datastore, dataTarget, PropertySet.of(properties));
	}

	/**
	 * Set the {@link ItemConverter} to be used to convert the select property value to the model {@link PropertyBox}.
	 * <p>
	 * The item converter is required when the select value type is not a {@link PropertyBox} to allow value selection
	 * using {@link Input#setValue(Object)} or {@link Selectable#select(Object)}.
	 * </p>
	 * @param itemConverter The {@link ItemConverter} to set (not null)
	 * @return this
	 */
	B itemConverter(ItemConverter<S, PropertyBox> itemConverter);

}
