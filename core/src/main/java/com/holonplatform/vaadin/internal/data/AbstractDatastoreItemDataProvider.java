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
package com.holonplatform.vaadin.internal.data;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import com.holonplatform.core.ParameterSet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.exceptions.DataAccessException;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryConfigurationProvider;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.vaadin.data.ItemDataProvider;
import com.holonplatform.vaadin.data.QueryConfigurationProviderSupport;
import com.vaadin.shared.Registration;

/**
 * Base {@link ItemDataProvider} using a {@link Datastore} to perform item set count and load operations.
 * <p>
 * Supports {@link QueryConfigurationProvider} registration through {@link QueryConfigurationProviderSupport}.
 * </p>
 * 
 * @param <T> Item type
 * 
 * @since 5.1.0
 */
public abstract class AbstractDatastoreItemDataProvider<T>
		implements ItemDataProvider<T>, QueryConfigurationProviderSupport {

	private static final long serialVersionUID = -4873927916647805467L;

	/**
	 * Datastore
	 */
	private final Datastore datastore;

	/**
	 * Data target
	 */
	private final DataTarget<?> target;

	/**
	 * Query configuration providers
	 */
	private List<QueryConfigurationProvider> queryConfigurationProviders = new LinkedList<>();

	/**
	 * Constructor.
	 * @param datastore Datastore to use (not null)
	 * @param target Data target (not null)
	 */
	public AbstractDatastoreItemDataProvider(Datastore datastore, DataTarget<?> target) {
		super();
		ObjectUtils.argumentNotNull(datastore, "Datastore must be not null");
		ObjectUtils.argumentNotNull(target, "DataTarget must be not null");
		this.datastore = datastore;
		this.target = target;
	}

	/**
	 * Get the {@link Datastore} to use to perform count and load operations.
	 * @return the datastore
	 */
	protected Datastore getDatastore() {
		return datastore;
	}

	/**
	 * Get the data target to use.
	 * @return the data target
	 */
	protected DataTarget<?> getTarget() {
		return target;
	}

	/**
	 * Get the registered {@link QueryConfigurationProvider}s.
	 * @return the available query configuration providers, an empty List if none
	 */
	protected List<QueryConfigurationProvider> getQueryConfigurationProviders() {
		return queryConfigurationProviders;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.vaadin.data.QueryConfigurationProviderSupport#addQueryConfigurationProvider(com.holonplatform.
	 * core.query.QueryConfigurationProvider)
	 */
	@Override
	public Registration addQueryConfigurationProvider(QueryConfigurationProvider queryConfigurationProvider) {
		ObjectUtils.argumentNotNull(queryConfigurationProvider, "QueryConfigurationProvider must be not null");
		queryConfigurationProviders.add(queryConfigurationProvider);
		return () -> queryConfigurationProviders.remove(queryConfigurationProvider);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetCounter#size(com.holonplatform.core.query.QueryConfigurationProvider)
	 */
	@Override
	public long size(QueryConfigurationProvider configuration) throws DataAccessException {
		return buildQuery(configuration, false).count();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.vaadin.data.ItemSetLoader#load(com.holonplatform.core.query.QueryConfigurationProvider,
	 * int, int)
	 */
	@Override
	public Stream<T> load(QueryConfigurationProvider configuration, int offset, int limit) throws DataAccessException {
		// build a configure a query
		final Query query = buildQuery(configuration, true);

		// limit and offset
		if (limit > 0) {
			query.limit(limit);
			query.offset(offset);
		}

		// execute
		return executeQuery(query);
	}

	/**
	 * Execute the configured query and obtain the items result stream.
	 * @param query Query to execute
	 * @return Items result stream
	 */
	protected abstract Stream<T> executeQuery(Query query);

	/**
	 * Build and configure a {@link Query}, using given <code>configuration</code> and any available
	 * {@link QueryConfigurationProvider}.
	 * @param configuration Query configuration (not null)
	 * @param withSorts Whether to apply sorts, if any, to query
	 * @return A new query instance
	 */
	protected Query buildQuery(QueryConfigurationProvider configuration, boolean withSorts) {

		// get a Query form Datastore
		final Query query = getDatastore().query();

		// target
		query.target(getTarget());

		// filters
		final List<QueryFilter> filters = new LinkedList<>();

		QueryFilter filter = configuration.getQueryFilter();
		if (filter != null) {
			filters.add(filter);
		}

		getQueryConfigurationProviders().forEach(p -> {
			QueryFilter qf = p.getQueryFilter();
			if (qf != null) {
				filters.add(qf);
			}
		});

		QueryFilter.allOf(filters).ifPresent(f -> query.filter(f));

		// sorts
		if (withSorts) {

			final List<QuerySort> sorts = new LinkedList<>();

			QuerySort sort = configuration.getQuerySort();
			if (sort != null) {
				sorts.add(sort);
			}

			getQueryConfigurationProviders().forEach(p -> {
				QuerySort qs = p.getQuerySort();
				if (qs != null) {
					sorts.add(qs);
				}
			});

			if (!sorts.isEmpty()) {
				if (sorts.size() == 1) {
					query.sort(sorts.get(0));
				} else {
					query.sort(QuerySort.of(sorts));
				}
			}
		}

		// parameters
		ParameterSet parameters = configuration.getQueryParameters();
		if (parameters != null) {
			parameters.forEachParameter((n, v) -> query.parameter(n, v));
		}

		getQueryConfigurationProviders().forEach(p -> {
			if (p.getQueryParameters() != null) {
				p.getQueryParameters().forEachParameter((n, v) -> query.parameter(n, v));
			}
		});

		return query;
	}

}
