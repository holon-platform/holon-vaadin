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

import com.holonplatform.vaadin.components.builders.GridLayoutBuilder;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

/**
 * Default {@link GridLayoutBuilder} implementation.
 *
 * @since 5.0.0
 */
public class DefaultGridLayoutBuilder extends AbstractLayoutBuilder<GridLayout, GridLayout, GridLayoutBuilder>
		implements GridLayoutBuilder {

	public DefaultGridLayoutBuilder(int columns, int rows) {
		super(new GridLayout(columns, rows));
		getInstance().setMargin(false);
		getInstance().setSpacing(false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractLayoutBuilder#buildLayout(com.vaadin.ui.
	 * AbstractLayout)
	 */
	@Override
	protected GridLayout buildLayout(GridLayout instance) {
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.internal.components.builders.AbstractComponentBuilder#builder()
	 */
	@Override
	protected GridLayoutBuilder builder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#columns(int)
	 */
	@Override
	public GridLayoutBuilder columns(int columns) {
		getInstance().setColumns(columns);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#rows(int)
	 */
	@Override
	public GridLayoutBuilder rows(int rows) {
		getInstance().setRows(rows);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#add(com.vaadin.ui.Component, int, int)
	 */
	@Override
	public GridLayoutBuilder add(Component component, int column, int row) {
		getInstance().addComponent(component, column, row);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#add(com.vaadin.ui.Component, int, int, int,
	 * int)
	 */
	@Override
	public GridLayoutBuilder add(Component component, int column1, int row1, int column2, int row2) {
		getInstance().addComponent(component, column1, row1, column2, row2);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#newLine()
	 */
	@Override
	public GridLayoutBuilder newLine() {
		getInstance().newLine();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#skip()
	 */
	@Override
	public GridLayoutBuilder skip() {
		getInstance().space();
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#columnExpandRatio(int, float)
	 */
	@Override
	public GridLayoutBuilder columnExpandRatio(int columnIndex, float ratio) {
		getInstance().setColumnExpandRatio(columnIndex, ratio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#rowExpandRatio(int, float)
	 */
	@Override
	public GridLayoutBuilder rowExpandRatio(int rowIndex, float ratio) {
		getInstance().setRowExpandRatio(rowIndex, ratio);
		return builder();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.components.builders.GridLayoutBuilder#hideEmptyRowsAndColumns()
	 */
	@Override
	public GridLayoutBuilder hideEmptyRowsAndColumns() {
		getInstance().setHideEmptyRowsAndColumns(true);
		return builder();
	}

}
