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
package com.holonplatform.vaadin.internal.components.builders;

import java.util.LinkedList;
import java.util.List;

import com.holonplatform.core.Path;
import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator;
import com.holonplatform.vaadin.components.ItemListing.ColumnAlignment;
import com.holonplatform.vaadin.components.ItemListing.ItemClickListener;
import com.holonplatform.vaadin.components.ItemListing.ItemDescriptionGenerator;
import com.holonplatform.vaadin.components.ItemListing.PropertyEditorFactory;
import com.holonplatform.vaadin.components.ItemListing.PropertyReorderListener;
import com.holonplatform.vaadin.components.ItemListing.PropertyResizeListener;
import com.holonplatform.vaadin.components.ItemListing.PropertyVisibilityListener;
import com.holonplatform.vaadin.components.ItemListing.RowStyleGenerator;
import com.holonplatform.vaadin.components.Selectable.SelectionListener;
import com.holonplatform.vaadin.components.Selectable.SelectionMode;
import com.holonplatform.vaadin.components.builders.ComponentPostProcessor;
import com.holonplatform.vaadin.components.builders.ItemListingBuilder;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.ItemDataSource.CommitHandler;
import com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator;
import com.holonplatform.vaadin.data.ItemIdentifierProvider;
import com.holonplatform.vaadin.data.container.ItemDataSourceContainer;
import com.holonplatform.vaadin.data.container.ItemDataSourceContainerBuilder.BaseItemDataSourceContainerBuilder;
import com.holonplatform.vaadin.internal.components.DefaultItemListing;
import com.vaadin.ui.Component;

/**
 * Base {@link ItemListingBuilder} implementation.
 * 
 * @param <T> Item data type
 * @param <P> Item property type
 * @param <C> Component type
 * @param <I> Internal instance
 * @param <B> Concrete builder type
 * @param <X> Concrete backing component type
 *
 * @since 5.0.0
 */
public abstract class AbstractItemListingBuilder<T, P, C extends ItemListing<T, P>, I extends DefaultItemListing<T, P>, B extends ItemListingBuilder<T, P, C, B, X>, X extends Component>
		extends AbstractComponentBuilder<C, I, B> implements ItemListingBuilder<T, P, C, B, X> {

	protected final List<ComponentPostProcessor<X>> postProcessors = new LinkedList<>();

	private PropertyReorderListener<P> reorderListener;

	/**
	 * Data source builder.
	 */
	protected final BaseItemDataSourceContainerBuilder<T, P> dataSourceBuilder = ItemDataSourceContainer.builder();

	/**
	 * Constructor
	 * @param instance Instance to build
	 */
	public AbstractItemListingBuilder(I instance) {
		super(instance);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemDataSourceComponentBuilder#dataSource(com.holonplatform.vaadin
	 * .data.ItemDataProvider, com.holonplatform.vaadin.data.ItemIdentifierProvider)
	 */
	@Override
	public B dataSource(ItemDataProvider<T> dataProvider, ItemIdentifierProvider<T, ?> itemIdentifierProvider) {
		ObjectUtils.argumentNotNull(dataProvider, "Item data provider must be not null");
		ObjectUtils.argumentNotNull(itemIdentifierProvider, "Item identifier provider must be not null");
		dataSourceBuilder.dataSource(dataProvider);
		dataSourceBuilder.itemIdentifier(itemIdentifierProvider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#sortable(java.lang.Object, boolean)
	 */
	@Override
	public B sortable(P property, boolean sortable) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		dataSourceBuilder.sortable(property, sortable);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#readOnly(java.lang.Object, boolean)
	 */
	@Override
	public B readOnly(P property, boolean readOnly) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		dataSourceBuilder.readOnly(property, readOnly);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#defaultValue(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public B defaultValue(P property, Object defaultValue) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		dataSourceBuilder.defaultValue(property, defaultValue);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#sortUsing(java.lang.Object,
	 * com.holonplatform.core.Path)
	 */
	@Override
	public B sortUsing(P property, final Path<?> sortPath) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		dataSourceBuilder.withPropertySortGenerator(property, (p, asc) -> QuerySort.of(sortPath, asc));
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#sortGenerator(java.lang.Object,
	 * com.holonplatform.vaadin.data.ItemDataSource.PropertySortGenerator)
	 */
	@Override
	public B sortGenerator(P property, PropertySortGenerator<P> generator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(generator, "Sort generator must be not null");
		dataSourceBuilder.withPropertySortGenerator(property, generator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#autoRefresh(boolean)
	 */
	@Override
	public B autoRefresh(boolean autoRefresh) {
		dataSourceBuilder.autoRefresh(autoRefresh);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#batchSize(int)
	 */
	@Override
	public B batchSize(int batchSize) {
		dataSourceBuilder.batchSize(batchSize);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#maxCacheSize(int)
	 */
	@Override
	public B maxCacheSize(int maxCacheSize) {
		dataSourceBuilder.maxCacheSize(maxCacheSize);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#withQueryConfigurationProvider(
	 * com.holonplatform.core.query.QueryConfigurationProvider)
	 */
	@Override
	public B withQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		ObjectUtils.argumentNotNull(queryConfigurationProvider, "QueryConfigurationProvider must be not null");
		dataSourceBuilder.withQueryConfigurationProvider(queryConfigurationProvider);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#fixedFilter(com.holonplatform.
	 * core.query.QueryFilter)
	 */
	@Override
	public B fixedFilter(QueryFilter filter) {
		dataSourceBuilder.fixedFilter(filter);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#fixedSort(com.holonplatform.
	 * core.query.QuerySort)
	 */
	@Override
	public B fixedSort(QuerySort sort) {
		dataSourceBuilder.fixedSort(sort);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#defaultSort(com.holonplatform.
	 * core.query.QuerySort)
	 */
	@Override
	public B defaultSort(QuerySort sort) {
		dataSourceBuilder.defaultSort(sort);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.BaseItemDataSourceComponentBuilder#queryParameter(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public B queryParameter(String name, Object value) {
		dataSourceBuilder.queryParameter(name, value);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#commitHandler(com.holonplatform.vaadin.data.
	 * ItemDataSource.CommitHandler)
	 */
	@Override
	public B commitHandler(CommitHandler<T> commitHandler) {
		ObjectUtils.argumentNotNull(commitHandler, "CommitHandler must be not null");
		dataSourceBuilder.commitHandler(commitHandler);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#withRowStyle(com.holonplatform.vaadin.components.
	 * ItemListing.RowStyleGenerator)
	 */
	@Override
	public B withRowStyle(RowStyleGenerator<T> rowStyleGenerator) {
		getInstance().addRowStyleGenerator(rowStyleGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#header(java.lang.Object,
	 * com.holonplatform.core.i18n.Localizable)
	 */
	@Override
	public B header(P property, Localizable header) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(header, "Header must be not null");
		getInstance().getPropertyColumn(property).setCaption(header);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#alignment(java.lang.Object,
	 * com.holonplatform.vaadin.components.ItemListing.ColumnAlignment)
	 */
	@Override
	public B alignment(P property, ColumnAlignment alignment) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(alignment, "Aligment must be not null");
		getInstance().getPropertyColumn(property).setAlignment(alignment);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#width(java.lang.Object, int)
	 */
	@Override
	public B width(P property, int widthInPixels) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setWidth(widthInPixels);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#editable(java.lang.Object, boolean)
	 */
	@Override
	public B editable(P property, boolean editable) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setEditable(editable);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#editor(java.lang.Object,
	 * com.holonplatform.vaadin.components.ItemListing.PropertyEditorFactory)
	 */
	@Override
	public B editor(P property, PropertyEditorFactory<P> propertyEditorFactory) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(propertyEditorFactory, "Factory must be not null");
		getInstance().getPropertyColumn(property).setEditorFactory(propertyEditorFactory);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#hidable(java.lang.Object, boolean)
	 */
	@Override
	public B hidable(P property, boolean hidable) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setHidable(hidable);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#hidden(java.lang.Object, boolean)
	 */
	@Override
	public B hidden(P property, boolean hidden) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		getInstance().getPropertyColumn(property).setHidden(hidden);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#hideHeaders()
	 */
	@Override
	public B hideHeaders() {
		getInstance().setHeadersVisible(false);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#style(java.lang.Object,
	 * com.holonplatform.vaadin.components.ItemListing.CellStyleGenerator)
	 */
	@Override
	public B style(P property, CellStyleGenerator<T, P> cellStyleGenerator) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		ObjectUtils.argumentNotNull(cellStyleGenerator, "Style generator must be not null");
		getInstance().getPropertyColumn(property).setStyle(cellStyleGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#selectionMode(com.holonplatform.vaadin.components
	 * .Selectable.SelectionMode)
	 */
	@Override
	public B selectionMode(SelectionMode selectionMode) {
		ObjectUtils.argumentNotNull(selectionMode, "SelectionMode must be not null");
		getInstance().setSelectionMode(selectionMode);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#withSelectionListener(com.holonplatform.vaadin.
	 * components.Selectable.SelectionListener)
	 */
	@Override
	public B withSelectionListener(SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		getInstance().addSelectionListener(selectionListener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#editable(boolean)
	 */
	@Override
	public B editable(boolean editable) {
		getInstance().setEditorEnabled(editable);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#withItemClickListener(com.holonplatform.vaadin.
	 * components.ItemListing.ItemClickListener)
	 */
	@Override
	public B withItemClickListener(ItemClickListener<T, P> listener) {
		getInstance().addItemClickListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#withPropertyReorderListener(com.holonplatform.
	 * vaadin.components.ItemListing.PropertyReorderListener)
	 */
	@Override
	public B withPropertyReorderListener(PropertyReorderListener<P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		this.reorderListener = listener;
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#withPropertyResizeListener(com.holonplatform.
	 * vaadin.components.ItemListing.PropertyResizeListener)
	 */
	@Override
	public B withPropertyResizeListener(PropertyResizeListener<P> listener) {
		getInstance().addPropertyResizeListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#withPropertyVisibilityListener(com.holonplatform.
	 * vaadin.components.ItemListing.PropertyVisibilityListener)
	 */
	@Override
	public B withPropertyVisibilityListener(PropertyVisibilityListener<P> listener) {
		getInstance().addPropertyVisibilityListener(listener);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ItemListingBuilder#itemDescriptionGenerator(com.holonplatform.vaadin
	 * .components.ItemListing.ItemDescriptionGenerator)
	 */
	@Override
	public B itemDescriptionGenerator(ItemDescriptionGenerator<T> rowDescriptionGenerator) {
		getInstance().setDescriptionGenerator(rowDescriptionGenerator);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#columnHidingAllowed(boolean)
	 */
	@Override
	public B columnHidingAllowed(boolean columnHidingAllowed) {
		getInstance().setColumnHidingAllowed(columnHidingAllowed);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#columnReorderingAllowed(boolean)
	 */
	@Override
	public B columnReorderingAllowed(boolean columnReorderingAllowed) {
		getInstance().setColumnReorderingAllowed(columnReorderingAllowed);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#footerVisible(boolean)
	 */
	@Override
	public B footerVisible(boolean footerVisible) {
		getInstance().setFooterVisible(footerVisible);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.builders.ComponentPostProcessorSupport#withPostProcessor(com.holonplatform.
	 * vaadin.components.builders.ComponentPostProcessor)
	 */
	@Override
	public B withPostProcessor(ComponentPostProcessor<X> postProcessor) {
		ObjectUtils.argumentNotNull(postProcessor, "ComponentPostProcessor must be not null");
		postProcessors.add(postProcessor);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ItemListingBuilder#build(java.lang.Iterable)
	 */
	@Override
	public C build(Iterable<P> visibleColumns) {
		return buildAndConfigure(visibleColumns);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.ComponentBuilder#build()
	 */
	@Override
	public C build() {
		return buildAndConfigure(null);
	}

	/**
	 * Build listing component and configure columns
	 * @param visibleColumns Columns to show. If <code>null</code> or empty, all data source properties will be used as
	 *        visibile columns.
	 * @return Listing component
	 */
	protected C buildAndConfigure(Iterable<P> visibleColumns) {
		// build
		I listing = getInstance();
		localize(listing);

		// data source
		ItemDataSourceContainer<T, P> dataSource = dataSourceBuilder.build();
		listing.setDataSource(dataSource);

		// visible columns
		listing.setPropertyColumns(
				(visibleColumns != null) ? visibleColumns : dataSource.getConfiguration().getProperties());

		// additional configuration
		configure(listing);

		// Reorder listener
		if (reorderListener != null) {
			listing.addPropertyReorderListener(reorderListener);
		}

		// build
		C built = build(listing);

		// post processors
		postProcessors.forEach(p -> p.process(built));

		// done
		return built;
	}

	/**
	 * Additional listing configuration
	 * @param instance Building instance
	 */
	protected abstract void configure(I instance);

}
