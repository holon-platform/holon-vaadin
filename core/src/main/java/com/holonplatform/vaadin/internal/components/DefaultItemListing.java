/*
 * Copyright 2000-2017 Holon TDCN.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.holonplatform.core.i18n.Localizable;
import com.holonplatform.core.i18n.LocalizationContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.Property;
import com.holonplatform.vaadin.components.ItemListing;
import com.holonplatform.vaadin.components.Selectable;
import com.holonplatform.vaadin.data.ItemDataSource;
import com.holonplatform.vaadin.data.ItemDataSource.ItemSort;
import com.holonplatform.vaadin.internal.data.ItemDataProviderAdapter;
import com.holonplatform.vaadin.internal.data.ItemDataSourceAdapter;
import com.vaadin.data.Binder.BindingBuilder;
import com.vaadin.data.HasValue;
import com.vaadin.data.SelectionModel.Multi;
import com.vaadin.data.SelectionModel.Single;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.Registration;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.UI;
import com.vaadin.ui.components.grid.Editor;
import com.vaadin.ui.components.grid.EditorCancelListener;
import com.vaadin.ui.components.grid.EditorOpenListener;
import com.vaadin.ui.components.grid.EditorSaveListener;
import com.vaadin.ui.components.grid.MultiSelectionModel;
import com.vaadin.ui.components.grid.MultiSelectionModel.SelectAllCheckBoxVisibility;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.renderers.TextRenderer;

/**
 * Default {@link ItemListing} implementation using a {@link Grid} as UI component.
 * 
 * @param <T> Item type
 * @param <P> Property type
 * 
 * @since 5.0.0
 */
public class DefaultItemListing<T, P> extends CustomComponent implements ItemListing<T, P> {

	private static final long serialVersionUID = -4573359150260491496L;

	/**
	 * Property column definitions
	 */
	private final Map<P, PropertyColumn<T, P>> propertyColumnDefinitions = new HashMap<>();

	/**
	 * Selection mode
	 */
	private com.holonplatform.vaadin.components.Selectable.SelectionMode selectionMode = com.holonplatform.vaadin.components.Selectable.SelectionMode.NONE;

	/**
	 * Row style generators
	 */
	private final List<RowStyleGenerator<T>> rowStyleGenerators = new LinkedList<>();

	/**
	 * Column hiding allowed
	 */
	private boolean columnHidingAllowed = true;

	/**
	 * Auto commit on row save
	 */
	private boolean commitOnSave = false;

	/**
	 * Auto commit on row remove
	 */
	private boolean commitOnRemove = false;

	/**
	 * Select all visibility
	 */
	private SelectAllCheckBoxVisibility selectAllCheckBoxVisibility = SelectAllCheckBoxVisibility.DEFAULT;

	/**
	 * Buffered mode
	 */
	private boolean buffered = false;

	/**
	 * Data source (buffered)
	 */
	private ItemDataSource<T, P> dataSource;

	/**
	 * The Grid
	 */
	private Grid<T> grid;

	protected DefaultItemListing() {
		super();
	}

	/**
	 * Constructor with internal Grid.
	 * @param grid The Grid instance (not null)
	 */
	public DefaultItemListing(Grid<T> grid) {
		super();
		initGrid(grid);
	}

	/**
	 * Init internal Grid.
	 * @param grid Grid
	 */
	protected void initGrid(Grid<T> grid) {
		this.grid = grid;

		grid.setWidth(100, Unit.PERCENTAGE);

		// reset selection model
		grid.setSelectionMode(com.vaadin.ui.Grid.SelectionMode.NONE);

		// row style generator
		grid.setStyleGenerator(i -> generateRowStyle(i));

		Editor<T> editor = grid.getEditor();
		if (editor != null) {
			editor.addSaveListener(e -> {
				if (isBuffered()) {
					requireDataSource().update(e.getBean());
					if (isCommitOnSave()) {
						requireDataSource().commit();
					}
				} else {
					requireDataSource().getConfiguration().getCommitHandler().ifPresent(ch -> {
						ch.commit(Collections.emptySet(), Collections.singleton(e.getBean()), Collections.emptySet());
					});
				}
			});
		}

		super.setWidth(100, Unit.PERCENTAGE);
		addStyleName("h-itemlisting", false);

		setCompositionRoot(grid);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setHeight(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setHeight(float height, Unit unit) {
		super.setHeight(height, unit);
		if (height > -1 && getCompositionRoot() != null) {
			getCompositionRoot().setHeight(100, Unit.PERCENTAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setWidth(float, com.vaadin.server.Sizeable.Unit)
	 */
	@Override
	public void setWidth(float width, Unit unit) {
		super.setWidth(width, unit);
		if (width > -1 && getCompositionRoot() != null) {
			getCompositionRoot().setWidth(100, Unit.PERCENTAGE);
		}
	}

	/**
	 * Get the internal {@link Grid}.
	 * @return the internal {@link Grid}
	 */
	public Grid<T> getGrid() {
		return grid;
	}

	/**
	 * Get the column id which corresponds to given property id.
	 * @param property The property id
	 * @return the column id which corresponds to given property id, or <code>null</code> if not found
	 */
	public String getColumnId(P property) {
		return (property != null) ? property.toString() : null;
	}

	/**
	 * Get the property id which corresponds to given column id.
	 * @param columnId Column id
	 * @return the property id which corresponds to given column id, or <code>null</code> if not found
	 */
	@SuppressWarnings("unchecked")
	protected P getColumnProperty(String columnId) {
		return (P) columnId;
	}

	/**
	 * Get the property/column type of given property id.
	 * @param property Property id
	 * @return property/column type which corresponds to given property id
	 */
	protected Class<?> getPropertyColumnType(P property) {
		if (property != null) {
			if (Property.class.isAssignableFrom(property.getClass())) {
				return ((Property<?>) property).getType();
			}
		}
		return Object.class;
	}

	/**
	 * Get or create the {@link PropertyColumn} definition bound to given property.
	 * @param property Property to get the definition for (not null)
	 * @return Property column definition
	 */
	public PropertyColumn<T, P> getPropertyColumn(P property) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		PropertyColumn<T, P> propertyColumn = propertyColumnDefinitions.get(property);
		if (propertyColumn == null) {
			propertyColumn = new DefaultPropertyColumn<>(property);
			propertyColumnDefinitions.put(property, propertyColumn);
		}
		return propertyColumn;
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#addStyleName(java.lang.String)
	 */
	@Override
	public void addStyleName(String style) {
		addStyleName(style, true);
	}

	/**
	 * Adds one or more style names to this component.
	 * @param styleName Style name to add
	 * @param reflectToContent <code>true</code> to add given <code>styleName</code> to content component too
	 */
	protected void addStyleName(String styleName, boolean reflectToContent) {
		super.addStyleName(styleName);
		if (reflectToContent) {
			getGrid().addStyleName(styleName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#removeStyleName(java.lang.String)
	 */
	@Override
	public void removeStyleName(String style) {
		super.removeStyleName(style);
		getGrid().removeStyleName(style);
	}

	/**
	 * Add a {@link RowStyleGenerator}
	 * @param rowStyleGenerator Generator to add (not null)
	 * @return the listener registration
	 */
	public Registration addRowStyleGenerator(RowStyleGenerator<T> rowStyleGenerator) {
		ObjectUtils.argumentNotNull(rowStyleGenerator, "RowStyleGenerator must be not null");
		rowStyleGenerators.add(rowStyleGenerator);
		return () -> rowStyleGenerators.remove(rowStyleGenerator);
	}

	/**
	 * Add an item-level validator.
	 * @param validator The validator to add (not null)
	 */
	public void addValidator(Validator<T> validator) {
		ObjectUtils.argumentNotNull(validator, "Validator must be not null");
		getGrid().getEditor().getBinder().withValidator(validator);
	}

	/**
	 * Get the most suitable {@link Locale} to use.
	 * @return the field, UI or {@link LocalizationContext} locale
	 */
	protected Locale findLocale() {
		Locale locale = getLocale();
		if (locale == null && UI.getCurrent() != null) {
			locale = UI.getCurrent().getLocale();
		}
		if (locale == null) {
			locale = LocalizationContext.getCurrent().filter(l -> l.isLocalized()).flatMap(l -> l.getLocale())
					.orElse(Locale.getDefault());
		}
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectionMode()
	 */
	@Override
	public com.holonplatform.vaadin.components.Selectable.SelectionMode getSelectionMode() {
		return selectionMode;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getSelectedItems()
	 */
	@Override
	public Set<T> getSelectedItems() {
		if (getSelectionMode() == SelectionMode.NONE) {
			return Collections.emptySet();
		}
		return getGrid().getSelectedItems();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#getFirstSelectedItem()
	 */
	@Override
	public Optional<T> getFirstSelectedItem() {
		if (getSelectionMode() == SelectionMode.NONE) {
			return Optional.empty();
		}
		return getGrid().getSelectionModel().getFirstSelectedItem();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#select(java.lang.Object)
	 */
	@Override
	public void select(T item) {
		if (getSelectionMode() == SelectionMode.NONE) {
			throw new IllegalStateException("The item listing is not selectable");
		}
		if (item != null) {
			getGrid().select(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselect(java.lang.Object)
	 */
	@Override
	public void deselect(T item) {
		if (getSelectionMode() == SelectionMode.NONE) {
			throw new IllegalStateException("The item listing is not selectable");
		}
		if (item != null) {
			getGrid().deselect(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#deselectAll()
	 */
	@Override
	public void deselectAll() {
		if (getSelectionMode() != SelectionMode.NONE) {
			getGrid().deselectAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setDeselectAllowed(boolean)
	 */
	@Override
	public void setDeselectAllowed(boolean deselectAllowed) {
		if (getSelectionMode() == SelectionMode.SINGLE) {
			((Single<?>) getGrid().getSelectionModel()).setDeselectAllowed(deselectAllowed);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#isDeselectAllowed()
	 */
	@Override
	public boolean isDeselectAllowed() {
		if (getSelectionMode() == SelectionMode.SINGLE) {
			((Single<?>) getGrid().getSelectionModel()).isDeselectAllowed();
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.Selectable#addSelectionListener(com.holonplatform.vaadin.components.
	 * Selectable.SelectionListener)
	 */
	@Override
	public Registration addSelectionListener(
			final com.holonplatform.vaadin.components.Selectable.SelectionListener<T> selectionListener) {
		ObjectUtils.argumentNotNull(selectionListener, "SelectionListener must be not null");
		return getGrid().addSelectionListener(e -> selectionListener.onSelectionChange(buildSelectionEvent(e)));
	}

	protected SelectionEvent<T> buildSelectionEvent(com.vaadin.event.selection.SelectionEvent<T> event) {
		if (SelectionMode.MULTI == getSelectionMode()) {
			return new DefaultSelectionEvent<>(event.getAllSelectedItems(), event.isUserOriginated());
		}
		return new DefaultSelectionEvent<>(event.getFirstSelectedItem().orElse(null), event.isUserOriginated());
	}

	/**
	 * Generate the row style names for given <code>item</code> using registered row style generators.
	 * @param item Item
	 * @return Row styles
	 */
	protected String generateRowStyle(T item) {
		StringBuilder sb = new StringBuilder();
		if (item != null && !rowStyleGenerators.isEmpty()) {
			for (RowStyleGenerator<T> rowStyleGenerator : rowStyleGenerators) {
				String style = rowStyleGenerator.getRowStyle(item);
				if (style != null && !style.trim().equals("")) {
					if (sb.length() > 0) {
						sb.append(" ");
					}
					sb.append(style);
				}
			}
		}
		return (sb.length() > 0) ? sb.toString() : null;
	}

	/**
	 * Generate cell style names for given <code>property</code> and <code>item</code> using column definition.
	 * @param property Column property
	 * @param item Item
	 * @return Cell style names
	 */
	protected String generatePropertyStyle(P property, T item) {
		PropertyColumn<T, P> column = getPropertyColumn(property);
		if (column != null && (column.getStyle() != null || column.getAlignment() != null)) {
			final StringBuilder sb = new StringBuilder();
			if (column.getAlignment() != null) {
				if (ColumnAlignment.CENTER.equals(column.getAlignment())) {
					sb.append("v-align-center");
				} else if (ColumnAlignment.RIGHT.equals(column.getAlignment())) {
					sb.append("v-align-right");
				}
			}
			if (column.getStyle() != null) {
				if (sb.length() > 0) {
					sb.append(" ");
				}
				column.getStyle().ifPresent(s -> {
					String cellStyle = s.getCellStyle(property, item);
					if (cellStyle != null) {
						sb.append(cellStyle);
					}
				});
			}
			return (sb.length() > 0) ? sb.toString() : null;
		}
		return null;
	}

	/**
	 * Setup column configuration for given <code>property</code> using its {@link PropertyColumn} definition.
	 * @param property Property to which the column is bound
	 * @param column Column to setup
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setupPropertyColumn(P property, Column<T, ?> column) {

		if (column == null) {
			throw new IllegalStateException(
					"No column available for property [" + property + "]: check listing property set");
		}

		PropertyColumn<T, P> propertyColumn = getPropertyColumn(property);

		// header
		if (propertyColumn.getCaption() != null) {
			String header = LocalizationContext.translate(propertyColumn.getCaption(), true);
			if (header != null) {
				column.setCaption(header);
			}
		}

		// sortable
		getDataSource().ifPresent(ds -> {
			column.setSortable(ds.getConfiguration().isPropertySortable(property));
		});

		// width
		if (propertyColumn.getWidth() > -1) {
			column.setWidth(propertyColumn.getWidth());
		}
		if (propertyColumn.getMinWidth() > -1) {
			column.setMinimumWidth(propertyColumn.getMinWidth());
		}
		if (propertyColumn.getMaxWidth() > -1) {
			column.setMaximumWidth(propertyColumn.getMinWidth());
		}

		column.setMinimumWidthFromContent(propertyColumn.isMinimumWidthFromContent());

		// expand
		if (propertyColumn.getExpandRatio() > -1) {
			column.setExpandRatio(propertyColumn.getExpandRatio());
		}

		// editing
		final boolean readOnly = requireDataSource().getConfiguration().isPropertyReadOnly(property);
		if (propertyColumn.isEditable()) {
			if (propertyColumn.getEditor().isPresent()) {
				setEditorBinding(property, column, propertyColumn.getEditor().get(), readOnly, propertyColumn);
			} else {
				getDefaultPropertyEditor(property).ifPresent(e -> {
					setEditorBinding(property, column, e, readOnly, propertyColumn);
				});
			}
		} else {
			if (column.getEditorBinding() != null) {
				column.setEditable(false);
			}
		}

		if (readOnly && column.getEditorBinding() != null) {
			column.setEditable(false);
		}

		// hiding
		if (columnHidingAllowed && propertyColumn.isHidable()) {
			column.setHidable(true);
			if (propertyColumn.isHidden()) {
				column.setHidden(true);
			}
			propertyColumn.getHidingToggleCaption().ifPresent(c -> {
				column.setHidingToggleCaption(LocalizationContext.translate(c));
			});
		} else {
			column.setHidable(false);
		}

		// style
		column.setStyleGenerator(i -> generatePropertyStyle(property, i));

		// rendering
		Renderer renderer = getPropertyRenderer(property).orElse(new TextRenderer());
		Optional<ValueProvider<?, ?>> presenter = getPropertyPresenter(property);

		if (presenter.isPresent()) {
			column.setRenderer((ValueProvider) presenter.get(), renderer);
		} else {
			column.setRenderer(renderer);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setEditorBinding(P property, Column column, HasValue editor, boolean readOnly,
			PropertyColumn<?, ?> pc) {
		BindingBuilder builder = getGrid().getEditor().getBinder().forField(editor);
		if (pc.isRequired()) {
			final Localizable requiredMessage = (pc.getRequiredMessage() != null) ? pc.getRequiredMessage()
					: RequiredInputValidator.DEFAULT_REQUIRED_ERROR;
			builder.asRequired(context -> {
				return LocalizationContext.translate(requiredMessage, true);
			});

		}
		// default validators
		getDefaultPropertyValidators(property).forEach(v -> builder.withValidator(new ValidatorWrapper<>(v)));
		// validators
		pc.getValidators().forEach(v -> builder.withValidator(v));
		// bind
		column.setEditorBinding(builder.bind(getColumnId(property)));
		// set editable if not read-only
		column.setEditable(!readOnly);
	}

	/**
	 * Get the default validators associated to given property id.
	 * @param property Property id
	 * @return property validators, empty if none
	 */
	protected Collection<com.holonplatform.core.Validator<?>> getDefaultPropertyValidators(P property) {
		return Collections.emptySet();
	}

	/**
	 * Get the default editor field to use to edit given property value.
	 * @param <E> Editor field type
	 * @param property Property id
	 * @return Optional default editor field
	 */
	protected <E extends HasValue<?> & Component> Optional<E> getDefaultPropertyEditor(P property) {
		return Optional.empty();
	}

	/**
	 * Get the default presenter to use for given property value.
	 * @param property Property id
	 * @return Optional default editor presenter
	 */
	protected Optional<ValueProvider<?, ?>> getDefaultPropertyPresenter(P property) {
		return Optional.empty();
	}

	/**
	 * Get the default property {@link Renderer} to use for given property value.
	 * @param property Property id
	 * @return Optional default editor renderer
	 */
	protected Optional<Renderer<?>> getDefaultPropertyRenderer(P property) {
		return Optional.empty();
	}

	/**
	 * Get the presenter to use with given property id.
	 * @param property Property id
	 * @return Optional property value presenter
	 */
	protected Optional<ValueProvider<?, ?>> getPropertyPresenter(P property) {
		Optional<ValueProvider<?, ?>> propertyPresenter = getPropertyColumn(property).getPresentationProvider();
		if (propertyPresenter.isPresent()) {
			return propertyPresenter;
		}
		return getDefaultPropertyPresenter(property);
	}

	/**
	 * Get the renderer to use with given property id.
	 * @param property Property id
	 * @return Optional property renderer
	 */
	protected Optional<Renderer<?>> getPropertyRenderer(P property) {
		Optional<Renderer<?>> propertyRenderer = getPropertyColumn(property).getRenderer();
		if (propertyRenderer.isPresent()) {
			return propertyRenderer;
		}
		return getDefaultPropertyRenderer(property);
	}

	/**
	 * Configure and set the listing columns according to given visible property set.
	 * @param columns Columns property set
	 */
	public void setPropertyColumns(Iterable<? extends P> columns) {
		setupVisibileColumns(columns);
	}

	/**
	 * Set given properties as listing visibile columns.
	 * @param visibleColumns Visible columns properties (not null)
	 */
	protected void setupVisibileColumns(Iterable<? extends P> visibleColumns) {
		ObjectUtils.argumentNotNull(visibleColumns, "Visible columns must be not null");
		List<String> ids = new LinkedList<>();

		visibleColumns.forEach(property -> {
			final String columnId = getColumnId(property);
			setupPropertyColumn(property, getGrid().getColumn(columnId));
			ids.add(getColumnId(property));
		});

		getGrid().setColumns(ids.toArray(new String[ids.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#getPropertyColumns()
	 */
	@Override
	public List<P> getPropertyColumns() {
		List<Column<T, ?>> columns = getGrid().getColumns();
		if (columns != null && !columns.isEmpty()) {
			List<P> properties = new ArrayList<>(columns.size());
			for (Column<T, ?> column : columns) {
				properties.add(getColumnProperty(column.getId()));
			}
			return properties;
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setPropertyColumnVisible(java.lang.Object, boolean)
	 */
	@Override
	public void setPropertyColumnVisible(P property, boolean visible) {
		ObjectUtils.argumentNotNull(property, "Property must be not null");
		String id = getColumnId(property);
		if (id != null) {
			Column<T, ?> column = getGrid().getColumn(id);
			if (column != null) {
				column.setHidden(!visible);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#isFooterVisible()
	 */
	@Override
	public boolean isFooterVisible() {
		return getGrid().isFooterVisible();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setFooterVisible(boolean)
	 */
	@Override
	public void setFooterVisible(boolean visible) {
		getGrid().setFooterVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#isItemDetailsVisible(java.lang.Object)
	 */
	@Override
	public boolean isItemDetailsVisible(T item) {
		return getGrid().isDetailsVisible(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setItemDetailsVisible(java.lang.Object, boolean)
	 */
	@Override
	public void setItemDetailsVisible(T item, boolean visible) throws UnsupportedOperationException {
		getGrid().setDetailsVisible(item, visible);
	}

	/**
	 * Set whether the listing column headers are visible.
	 * @param headersVisible whether the listing column headers are visible
	 */
	public void setHeadersVisible(boolean headersVisible) {
		getGrid().setHeaderVisible(headersVisible);
	}

	/**
	 * Add an {@link ItemClickListener} to be notified when user clicks on an item row.
	 * @param listener Listener to add (not null)
	 * @return the listener registration
	 */
	public Registration addItemClickListener(final ItemClickListener<T, P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		return getGrid().addItemClickListener(e -> listener.onItemClick(e.getItem(),
				getColumnProperty(e.getColumn().getId()), e.getMouseEventDetails()));
	}

	/**
	 * Adds a {@link PropertyReorderListener} that gets notified when property columns order changes.
	 * @param listener Listener to add (not null)
	 * @return the listener registration
	 */
	public Registration addPropertyReorderListener(final PropertyReorderListener<P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		return getGrid().addColumnReorderListener(
				e -> listener.onPropertyReordered(getPropertyColumns(), e.isUserOriginated()));
	}

	/**
	 * Adds a {@link PropertyResizeListener} that gets notified when a property column is resized.
	 * @param listener Listener to add (not null)
	 * @return the listener registration
	 */
	public Registration addPropertyResizeListener(final PropertyResizeListener<P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		return getGrid()
				.addColumnResizeListener(e -> listener.onPropertyResized(getColumnProperty(e.getColumn().getId()),
						(int) e.getColumn().getWidth(), e.isUserOriginated()));
	}

	/**
	 * Adds a {@link PropertyVisibilityListener} that gets notified when a property column is hidden or shown.
	 * @param listener Listener to add (not null)
	 * @return the listener registration
	 */
	public Registration addPropertyVisibilityListener(final PropertyVisibilityListener<P> listener) {
		ObjectUtils.argumentNotNull(listener, "Listener must be not null");
		return getGrid().addColumnVisibilityChangeListener(e -> listener.onPropertyVisibilityChanged(
				getColumnProperty(e.getColumn().getId()), e.isHidden(), e.isUserOriginated()));
	}

	/**
	 * Set the {@link ItemDescriptionGenerator} to use to generate item descriptions (tooltips).
	 * @param rowDescriptionGenerator Generator to set (not null)
	 */
	public void setDescriptionGenerator(final ItemDescriptionGenerator<T> rowDescriptionGenerator) {
		ObjectUtils.argumentNotNull(rowDescriptionGenerator, "Generator must be not null");
		getGrid().setDescriptionGenerator(row -> rowDescriptionGenerator.getItemDescription(row));
	}

	/**
	 * Set the item details component generator.
	 * @param itemDetailsGenerator the item details component generator to set (not null)
	 */
	public void setDetailsGenerator(ItemDetailsGenerator<T> itemDetailsGenerator) {
		ObjectUtils.argumentNotNull(itemDetailsGenerator, "Generator must be not null");
		getGrid().setDetailsGenerator(item -> itemDetailsGenerator.getItemDetails(item));
	}

	/**
	 * Sets whether column hiding by user is allowed or not.
	 * @param columnHidingAllowed <code>true</code> if column hiding is allowed
	 */
	public void setColumnHidingAllowed(boolean columnHidingAllowed) {
		this.columnHidingAllowed = columnHidingAllowed;
		propertyColumnDefinitions.values().forEach(c -> c.setHidable(false));
	}

	/**
	 * Sets whether column reordering is allowed or not.
	 * @param columnReorderingAllowed <code>true</code> if column reordering is allowed
	 */
	public void setColumnReorderingAllowed(boolean columnReorderingAllowed) {
		getGrid().setColumnReorderingAllowed(columnReorderingAllowed);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#setEditable(boolean)
	 */
	@Override
	public void setEditable(boolean editable) {
		setEditorEnabled(editable);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#isEditable()
	 */
	@Override
	public boolean isEditable() {
		return getGrid().getEditor().isEnabled();
	}

	/**
	 * Set whether the item editor is enabled.
	 * @param enabled whether the item editor is enabled
	 */
	public void setEditorEnabled(boolean enabled) {
		getGrid().getEditor().setEnabled(enabled);
	}

	/**
	 * Set whether the item editor is in buffered mode.
	 * @param buffered whether the item editor is in buffered mode
	 */
	public void setEditorBuffered(boolean buffered) {
		getGrid().getEditor().setBuffered(buffered);
	}

	/**
	 * Set the item editor <em>save</em> button caption
	 * @param caption the caption to set
	 */
	public void setEditorSaveCaption(String caption) {
		getGrid().getEditor().setSaveCaption(caption);
	}

	/**
	 * Set the item editor <em>cancel</em> button caption
	 * @param caption the caption to set
	 */
	public void setEditorCancelCaption(String caption) {
		getGrid().getEditor().setCancelCaption(caption);
	}

	/**
	 * Register an item editor save listener.
	 * @param listener The listener to add
	 * @return the listener registration
	 */
	public Registration addEditorSaveListener(EditorSaveListener<T> listener) {
		return getGrid().getEditor().addSaveListener(listener);
	}

	/**
	 * Register an item editor cancel listener.
	 * @param listener The listener to add
	 * @return the listener registration
	 */
	public Registration addEditorCancelListener(EditorCancelListener<T> listener) {
		return getGrid().getEditor().addCancelListener(listener);
	}

	/**
	 * Register an item editor open listener.
	 * @param listener The listener to add
	 * @return the listener registration
	 */
	public Registration addEditorOpenListener(EditorOpenListener<T> listener) {
		return getGrid().getEditor().addOpenListener(listener);
	}

	/**
	 * Set the row height of all the sections of the internal grid.
	 * @param rowHeight the row height to set
	 */
	public void setRowHeight(double rowHeight) {
		getGrid().setRowHeight(rowHeight);
	}

	/**
	 * Set the row height of the internal grid body section.
	 * @param rowHeight the row height to set
	 */
	public void setBodyRowHeight(double rowHeight) {
		getGrid().setBodyRowHeight(rowHeight);
	}

	/**
	 * Set the row height of the internal grid header section.
	 * @param rowHeight the row height to set
	 */
	public void setHeaderRowHeight(double rowHeight) {
		getGrid().setHeaderRowHeight(rowHeight);
	}

	/**
	 * Set the row height of the internal grid footer section.
	 * @param rowHeight the row height to set
	 */
	public void setFooterRowHeight(double rowHeight) {
		getGrid().setFooterRowHeight(rowHeight);
	}

	/**
	 * Set the height mode of the internal grid.
	 * @param heightMode the height mode to set
	 */
	public void setHeightMode(HeightMode heightMode) {
		getGrid().setHeightMode(heightMode);
	}

	/**
	 * Sets the number of rows that should be visible in internal Grid's body.
	 * @param rows the number of rows to set
	 */
	public void setHeightByRows(double rows) {
		getGrid().setHeightByRows(rows);
	}

	/**
	 * Set the <em>frozen</em> columns count.
	 * @param numberOfColumns the number of columns that should be frozen
	 */
	public void setFrozenColumnCount(int numberOfColumns) {
		getGrid().setFrozenColumnCount(numberOfColumns);
	}

	/**
	 * Add a {@link DataProviderListener} to the internal Grid data provider.
	 * @param listener The listener to add
	 * @return the listener registration
	 */
	public Registration addDataProviderListener(DataProviderListener<T> listener) {
		return getGrid().getDataProvider().addDataProviderListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#scrollToItem(java.lang.Object)
	 */
	@Override
	public void scrollToItem(T item) {
		if (!isBuffered()) {
			throw new NotBufferedException("The item listing is not in buffered mode");
		}
		int index = requireDataSource().indexOfItem(item);
		if (index > -1) {
			getGrid().scrollTo(index);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#scrollToIndex(int)
	 */
	@Override
	public void scrollToIndex(int index) {
		getGrid().scrollTo(index);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#scrollToTop()
	 */
	@Override
	public void scrollToTop() {
		getGrid().scrollToStart();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#scrollToEnd()
	 */
	@Override
	public void scrollToEnd() {
		getGrid().scrollToEnd();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ItemListing#sort(com.holonplatform.vaadin.data.ItemDataSource.ItemSort[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void sort(ItemSort<P>... sorts) {
		if (sorts != null && sorts.length > 0) {
			List<GridSortOrder<T>> orders = new LinkedList<>();
			for (ItemSort<P> sort : sorts) {
				String columnId = getColumnId(sort.getProperty());
				if (columnId != null) {
					Column<T, ?> column = getGrid().getColumn(columnId);
					if (column != null) {
						orders.add(new GridSortOrder<>(column,
								sort.isAscending() ? SortDirection.ASCENDING : SortDirection.DESCENDING));
					}
				}
			}
			if (!orders.isEmpty()) {
				getGrid().setSortOrder(orders);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.components.ItemListing#setSelectionMode(com.holonplatform.vaadin.components.Selectable.
	 * SelectionMode)
	 */
	@Override
	public void setSelectionMode(com.holonplatform.vaadin.components.Selectable.SelectionMode selectionMode) {
		ObjectUtils.argumentNotNull(selectionMode, "SelectionMode must be not null");
		this.selectionMode = selectionMode;
		switch (selectionMode) {
		case MULTI:
			getGrid().setSelectionMode(com.vaadin.ui.Grid.SelectionMode.MULTI);
			((MultiSelectionModel<T>) getGrid().getSelectionModel())
					.setSelectAllCheckBoxVisibility((selectAllCheckBoxVisibility != null) ? selectAllCheckBoxVisibility
							: SelectAllCheckBoxVisibility.DEFAULT);
			break;
		case NONE:
			getGrid().setSelectionMode(com.vaadin.ui.Grid.SelectionMode.NONE);
			break;
		case SINGLE:
			getGrid().setSelectionMode(com.vaadin.ui.Grid.SelectionMode.SINGLE);
			break;
		default:
			break;
		}
	}

	/**
	 * Set the {@link SelectAllCheckBoxVisibility} mode when listing is in multiple selection mode.
	 * @param selectAllCheckBoxVisibility the mode to set
	 */
	public void setSelectAllCheckBoxVisibility(SelectAllCheckBoxVisibility selectAllCheckBoxVisibility) {
		ObjectUtils.argumentNotNull(selectAllCheckBoxVisibility, "SelectAllCheckBoxVisibility must be not null");
		this.selectAllCheckBoxVisibility = selectAllCheckBoxVisibility;
		if (SelectionMode.MULTI == getSelectionMode()) {
			((MultiSelectionModel<T>) getGrid().getSelectionModel())
					.setSelectAllCheckBoxVisibility(selectAllCheckBoxVisibility);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#selectAll()
	 */
	@Override
	public void selectAll() {
		if (selectionMode == SelectionMode.MULTI) {
			((Multi<?>) getGrid().getSelectionModel()).selectAll();
		}
	}

	/**
	 * Check whether to call commit() on data source container when Grid editor save action is triggered
	 * @return <code>true</code> if should call commit() on data source container when Grid editor save action is
	 *         triggered
	 */
	public boolean isCommitOnSave() {
		return commitOnSave;
	}

	/**
	 * Sets whether to call commit() on data source container when Grid editor save action is triggered
	 * @param commitOnSave <code>true</code> to call commit() on data source container when Grid editor save action is
	 *        triggered
	 */
	public void setCommitOnSave(boolean commitOnSave) {
		this.commitOnSave = commitOnSave;
	}

	/**
	 * Check whether to call commit() on data source container when a row is removed using
	 * {@link ItemDataSource#remove(Object)}.
	 * @return <code>true</code> if should call commit() on data source container when a row is removed using
	 *         {@link ItemDataSource#remove(Object)}
	 */
	public boolean isCommitOnRemove() {
		return commitOnRemove;
	}

	/**
	 * Sets to whether call commit() on data source container when a row is removed using
	 * {@link ItemDataSource#remove(Object)}.
	 * @param commitOnRemove <code>true</code> to call commit() on data source container when a row is removed using
	 *        {@link ItemDataSource#remove(Object)}
	 */
	public void setCommitOnRemove(boolean commitOnRemove) {
		this.commitOnRemove = commitOnRemove;
	}

	/**
	 * Get the datasource.
	 * @return the datasource
	 */
	public Optional<ItemDataSource<T, P>> getDataSource() {
		return Optional.ofNullable(dataSource);
	}

	/**
	 * Get the datasource.
	 * @return the datasource
	 * @throws IllegalStateException If data source is not configured
	 */
	protected ItemDataSource<T, P> requireDataSource() {
		return getDataSource().orElseThrow(() -> new IllegalStateException("Missing ItemDataSource"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isBuffered() {
		return buffered;
	}

	/**
	 * Set the buffered mode.
	 * @param buffered <code>true</code> to activate the buffered mode
	 */
	public void setBuffered(boolean buffered) {
		boolean changed = this.buffered != buffered;
		this.buffered = buffered;
		if (changed) {
			setupDataProvider();
		}
	}

	/**
	 * Set the listing data source.
	 * @param dataSource The data source to set (not null)
	 */
	public void setDataSource(ItemDataSource<T, P> dataSource) {
		ObjectUtils.argumentNotNull(dataSource, "ItemDataSource must be not null");
		this.dataSource = dataSource;
		setupDataProvider();
	}

	/**
	 * Setup the Grid {@link DataProvider} according to buffered mode and current data source.
	 */
	protected void setupDataProvider() {
		if (this.dataSource != null) {
			if (isBuffered()) {
				getGrid().setDataProvider(new ItemDataSourceAdapter<>(this.dataSource));
			} else {
				getGrid().setDataProvider(new ItemDataProviderAdapter<>(this.dataSource.getConfiguration()));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemSet#refresh()
	 */
	@Override
	public void refresh() {
		if (isBuffered()) {
			requireDataSource().refresh();
		}
		getGrid().getDataProvider().refreshAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#clear()
	 */
	@Override
	public void clear() {
		if (getSelectionMode() != Selectable.SelectionMode.NONE) {
			deselectAll();
		}
		if (isBuffered()) {
			requireDataSource().clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#getItem(java.lang.Object)
	 */
	@Override
	public Optional<T> getItem(Object itemId) {
		if (!isBuffered()) {
			throw new NotBufferedException("The item listing is not in buffered mode");
		}
		return requireDataSource().get(itemId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#addItem(java.lang.Object)
	 */
	@Override
	public Object addItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");

		Object itemId = null;

		if (isBuffered()) {
			itemId = requireDataSource().add(item);
		} else {
			requireDataSource().getConfiguration().getCommitHandler().ifPresent(ch -> {
				ch.commit(Collections.singleton(item), Collections.emptySet(), Collections.emptySet());
			});
		}

		// refresh
		getGrid().getDataProvider().refreshAll();

		return itemId;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#removeItem(java.lang.Object)
	 */
	@Override
	public boolean removeItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");

		boolean removed = false;

		if (isBuffered()) {
			removed = requireDataSource().remove(item);

			// check commit on remove
			if (isCommitOnRemove()) {
				commit();
			}

			if (removed && getSelectionMode() != Selectable.SelectionMode.NONE) {
				getGrid().deselect(item);
			}

		} else {
			if (requireDataSource().getConfiguration().getCommitHandler().isPresent()) {
				requireDataSource().getConfiguration().getCommitHandler().get().commit(Collections.emptySet(),
						Collections.emptySet(), Collections.singleton(item));
				removed = true;
			}
		}

		// refresh
		getGrid().getDataProvider().refreshAll();

		return removed;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#refreshItem(java.lang.Object)
	 */
	@Override
	public void refreshItem(T item) {
		ObjectUtils.argumentNotNull(item, "Item must be not null");

		if (isBuffered()) {
			requireDataSource().refresh(item);
		}

		getGrid().getDataProvider().refreshItem(item);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#commit()
	 */
	@Override
	public void commit() {
		if (!isBuffered()) {
			throw new NotBufferedException("The item listing is not in buffered mode");
		}
		requireDataSource().commit();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.ItemListing#discard()
	 */
	@Override
	public void discard() {
		if (!isBuffered()) {
			throw new NotBufferedException("The item listing is not in buffered mode");
		}
		requireDataSource().discard();
	}

}
