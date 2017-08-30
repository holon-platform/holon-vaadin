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

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.OverlapsException;

/**
 * Builder to create {@link GridLayout} instances.
 *
 * @since 5.0.0
 */
public interface GridLayoutBuilder extends LayoutBuilder<GridLayout, GridLayoutBuilder> {

	/**
	 * Set the number of columns of the grid
	 * @param columns Number of columns
	 * @return this
	 */
	GridLayoutBuilder columns(int columns);

	/**
	 * Set the number of rows of the grid
	 * @param rows Number of rows
	 * @return this
	 */
	GridLayoutBuilder rows(int rows);

	/**
	 * Adds the component to the grid in cells column1,row1 (NortWest corner of the area.) End coordinates (SouthEast
	 * corner of the area) are the same as column1,row1. The coordinates are zero-based. Component width and height is
	 * 1.
	 * @param component the component to be added, not <code>null</code>
	 * @param column the column index, starting from 0.
	 * @param row the row index, starting from 0.
	 * @return this
	 */
	GridLayoutBuilder add(Component component, int column, int row);

	/**
	 * Adds a component to the grid in the specified area. The area is defined by specifying the upper left corner
	 * (column1, row1) and the lower right corner (column2, row2) of the area. The coordinates are zero-based.
	 * <p>
	 * If the area overlaps with any of the existing components already present in the grid, the operation will fail and
	 * an {@link OverlapsException} is thrown.
	 * </p>
	 * @param component the component to be added, not <code>null</code>.
	 * @param column1 the column of the upper left corner of the area <code>component</code> is supposed to occupy. The
	 *        leftmost column has index 0.
	 * @param row1 the row of the upper left corner of the area <code>c</code> is supposed to occupy. The topmost row
	 *        has index 0.
	 * @param column2 the column of the lower right corner of the area <code>component</code> is supposed to occupy.
	 * @param row2 the row of the lower right corner of the area <code>component</code> is supposed to occupy.
	 * @return this
	 */
	GridLayoutBuilder add(Component component, int column1, int row1, int column2, int row2);

	/**
	 * Forces the next component to be added at the beginning of the next line.
	 * <p>
	 * Sets the cursor column to 0 and increments the cursor row by one.
	 * </p>
	 * @return this
	 */
	GridLayoutBuilder newLine();

	/**
	 * Moves the cursor forward by one. If the cursor goes out of the right grid border, it is moved to the first column
	 * of the next row.
	 * @return this
	 */
	GridLayoutBuilder skip();

	/**
	 * Sets the expand ratio of given column.
	 * <p>
	 * The expand ratio defines how excess space is distributed among columns. Excess space means space that is left
	 * over from components that are not sized relatively. By default, the excess space is distributed evenly.
	 * </p>
	 * <p>
	 * Note, that width of this GridLayout needs to be defined (fixed or relative, as opposed to undefined height) for
	 * this method to have any effect.
	 * <p>
	 * Note that checking for relative width for the child components is done on the server so you cannot set a child
	 * component to have undefined width on the server and set it to <code>100%</code> in CSS. You must set it to
	 * <code>100%</code> on the server.
	 * @param columnIndex Index of the column to expand
	 * @param ratio Expand ratio
	 * @return this
	 */
	GridLayoutBuilder columnExpandRatio(int columnIndex, float ratio);

	/**
	 * Sets the expand ratio of given row.
	 * <p>
	 * Expand ratio defines how excess space is distributed among rows. Excess space means the space left over from
	 * components that are not sized relatively. By default, the excess space is distributed evenly.
	 * </p>
	 * <p>
	 * Note, that height of this GridLayout needs to be defined (fixed or relative, as opposed to undefined height) for
	 * this method to have any effect.
	 * <p>
	 * Note that checking for relative height for the child components is done on the server so you cannot set a child
	 * component to have undefined height on the server and set it to <code>100%</code> in CSS. You must set it to
	 * <code>100%</code> on the server.
	 * @param rowIndex Index of the row to expand
	 * @param ratio Expand ratio
	 * @return this
	 */
	GridLayoutBuilder rowExpandRatio(int rowIndex, float ratio);

	/**
	 * Empty rows and columns will be considered as non-existent when rendering the grid, i.e. the spacing between
	 * multiple empty columns (or rows) will be collapsed.
	 * @return this
	 */
	GridLayoutBuilder hideEmptyRowsAndColumns();

}
