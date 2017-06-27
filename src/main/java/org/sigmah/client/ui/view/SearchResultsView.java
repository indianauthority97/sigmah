
package org.sigmah.client.ui.view;

import java.util.ArrayList;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.search.SearchService;
import org.sigmah.client.search.SearchServiceAsync;
import org.sigmah.client.ui.presenter.SearchResultsPresenter;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.ContactResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.OrgUnitResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.ProjectResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.SearchResultsClickHandler;
import org.sigmah.client.ui.presenter.contact.dashboardlist.ContactsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.orgunit.OrgUnitTreeGrid;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.search.SearchResultsDTO;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Dashboard view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */

public class SearchResultsView extends AbstractView implements SearchResultsPresenter.View {

	private String searchText;

	private ContentPanel searchResultsPanel;

	ListStore<SearchResultsDTO> searchResultsStore;

	private LayoutContainer centerContainer;

	private ArrayList<Map<String, String>> listMaps = new ArrayList<Map<String, String>>();

	// private Map<String, Integer> ProjectIDTo

	private static SearchResultsClickHandler handler;

	final SearchResultsClickHandler getSearchResultsClickHandler() {
		return handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectClickHandler(final ProjectResultsClickHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setContactClickHandler(final ContactResultsClickHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setOrgUnitClickHandler(final OrgUnitResultsClickHandler handler) {
		this.handler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		// default
		// Window.alert("Initializing Search View!");
	}

	public ContentPanel getSearchResultsPanel() {
		return searchResultsPanel;
	}

	public ListStore<SearchResultsDTO> getSearchResultsStore() {
		return searchResultsStore;
	}

	public LayoutContainer getCenterContainer() {
		return centerContainer;
	}

	public void setSearchString(String searchText) {
		this.searchText = searchText;
		// Window.alert("Searchtext set to " + searchText);
	}

	// -------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------

	private void createSearchResultsPanel() {

		// Window.alert("Searchtext is currently " + searchText );
		setContentPanel("Search results for \"" + searchText + "\"", false, null, null);

	}

	public void setContentPanel(String title, boolean collapsible, Layout layout, Scroll scroll, String... stylenames) {

		searchResultsPanel = new ContentPanel(layout != null ? layout : new FitLayout());
		searchResultsPanel.layout();

		searchResultsPanel.setHeadingHtml(title);
		searchResultsPanel.layout();
		searchResultsPanel.setHeaderVisible(true);
		searchResultsPanel.setCollapsible(collapsible);

		if (ClientUtils.isNotEmpty(stylenames)) {
			for (String stylename : stylenames) {
				if (ClientUtils.isBlank(stylename)) {
					continue;
				}
				searchResultsPanel.addStyleName(stylename);
			}
		}

		if (scroll != null) {
			searchResultsPanel.setScrollMode(scroll);
		}

	}

	private List<ColumnConfig> createSearchResultsGridColumns() {

		// Label column.
		ColumnConfig labelColumn = new ColumnConfig();
		labelColumn.setId("label");
		labelColumn.setHeaderHtml("Label");
		labelColumn.setWidth(100);

		// Add link
		labelColumn.setRenderer(new GridCellRenderer<SearchResultsDTO>() {

			@Override
			public Object render(final SearchResultsDTO model, String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<SearchResultsDTO> store, Grid<SearchResultsDTO> grid) {

				Map<String, String> retMap = toMap(model.getResult());
				
				model.setDTOtype(retMap.get("doc_type").toString());

				if (retMap.get("doc_type").toString().equals("PROJECT")) {
					model.setDTOid(retMap.get("databaseid").toString());
					// Window.alert("Set the id = " + model.getDTOid());
				} else if (retMap.get("doc_type").toString().equals("CONTACT")) {
					model.setDTOid(retMap.get("id_contact").toString());
				} else if (retMap.get("doc_type").toString().equals("ORG_UNIT")) {
					model.setDTOid(retMap.get("org_unit_id").toString());
				}

				if (retMap != null) {
					listMaps.add(retMap);
					// Window.alert(retMap.entrySet().toString());
				}

				// Window.alert(retMap.get("doc_type").toString());
				// Window.alert(retMap.get("doc_id").toString());

				com.google.gwt.user.client.ui.Label label = new com.google.gwt.user.client.ui.Label(model.getResult());
				// HTML h = new HTML();
				// h.setText(model.getResult());

				// Window.alert("Label is : " + model.getResult() +
				// "\n property: " + property + "\nconfig: " + config.toString()
				// + "\nrowindex: " + rowIndex +
				// "\ncolIndex:" + colIndex );
				label.addStyleName("hyperlink-label");
				// label.setHeight("");
				if (retMap.get("doc_type").toString().equals("PROJECT")) {
					// Window.alert("Adding project " + model.getDTOid());
					// Window.alert("Integer " +
					// Integer.parseInt(model.getDTOid()));
					// final Integer project_id =
					// Integer.parseInt(retMap.get("databaseid"));
					label.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							((ProjectResultsClickHandler) handler)
									.onLabelClickEvent(Integer.parseInt(model.getDTOid()));
						}
					});
				}

				if (retMap.get("doc_type").toString().equals("CONTACT")) {
					// Window.alert("Adding project " + model.getDTOid());
					// Window.alert("Integer " +
					// Integer.parseInt(model.getDTOid()));
					// final Integer project_id =
					// Integer.parseInt(retMap.get("databaseid"));
					label.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							// handler.onLabelClickEvent(project_id);
							((ContactResultsClickHandler) handler)
									.onLabelClickEvent(Integer.parseInt(model.getDTOid()));
						}
					});
				}

				if (retMap.get("doc_type").toString().equals("ORG_UNIT")) {
					// Window.alert("Adding project " + model.getDTOid());
					// Window.alert("Integer " +
					// Integer.parseInt(model.getDTOid()));
					// final Integer project_id =
					// Integer.parseInt(retMap.get("databaseid"));
					label.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							// handler.onLabelClickEvent(project_id);
							((OrgUnitResultsClickHandler) handler)
									.onLabelClickEvent(Integer.parseInt(model.getDTOid()));
						}
					});
				}

				label.setTitle(model.getResult());
				return label;
			}

		});

		return Arrays.asList(new ColumnConfig[] { labelColumn });

	}

	public void addResultsPanel() {

		if (centerContainer == null) {
			centerContainer = Layouts.vBox();
			centerContainer.layout();
		} else {
			centerContainer.removeAll();
			centerContainer.layout();
		}
		createSearchResultsPanel();
		searchResultsPanel.layout();

		Grid<SearchResultsDTO> searchResultsGrid = new Grid<SearchResultsDTO>(searchResultsStore,
				new ColumnModel(createSearchResultsGridColumns()));
		searchResultsGrid.getView().setForceFit(false);
		searchResultsGrid.getView().setAutoFill(true);
		searchResultsGrid.setAutoExpandColumn("label");
		searchResultsPanel.add(searchResultsGrid);
		searchResultsPanel.layout();

		centerContainer.add(searchResultsPanel, Layouts.vBoxData(Margin.TOP));
		centerContainer.layout();
		add(centerContainer);

	}

	public static Map<String, String> toMap(String jsonStr) {
		Map<String, String> map = new HashMap<String, String>();

		JSONValue parsed = JSONParser.parseStrict(jsonStr);
		JSONObject jsonObj = parsed.isObject();
		if (jsonObj != null) {
			for (String key : jsonObj.keySet()) {
				map.put(key.replaceAll("^\"|\"$", ""), jsonObj.get(key).toString().replaceAll("^\"|\"$", ""));
			}
		}

		return map;
	}

	public void addSearchData(Object searchData) {
		if (searchData != null) {
			// Window.alert("Received search results!: \n" +
			// searchData.toString());
			searchResultsStore = new ListStore<SearchResultsDTO>();
			for (Object object : (ArrayList) searchData) {
				searchResultsStore.add(object != null ? (SearchResultsDTO) object : null);

				// Window.alert("Received search result!: \n" +
				// object.getResult());
			}
		} else {
			Window.alert("Failed to receive search results!");
		}

	}

}