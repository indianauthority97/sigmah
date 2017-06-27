package org.sigmah.client.ui.presenter;

import java.io.IOException;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.search.SearchService;
import org.sigmah.client.search.SearchServiceAsync;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.ContactResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.OrgUnitResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.ProjectResultsClickHandler;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.contact.dashboardlist.ContactsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.LoadingMode;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.RefreshMode;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.DashboardView;
import org.sigmah.client.ui.view.SearchResultsView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.HasTreeGrid.TreeGridEventHandler;
import org.sigmah.client.ui.widget.WorkInProgressWidget;
import org.sigmah.client.ui.widget.orgunit.OrgUnitTreeGrid;
import org.sigmah.client.ui.widget.tab.Tab;
import org.sigmah.client.ui.widget.tab.TabId;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.profiler.Checkpoint;
import org.sigmah.client.util.profiler.Execution;
import org.sigmah.client.util.profiler.ExecutionAsyncDAO;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.sync.UpdateDates;
import org.sigmah.shared.command.SendProbeReport;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.conf.PropertyName;
import org.sigmah.shared.dto.profile.CheckPointDTO;
import org.sigmah.shared.dto.profile.ExecutionDTO;
import org.sigmah.shared.dto.search.SearchResultsDTO;

/**
 * Search Results page presenter.
 * 
 * 
 */

public class SearchResultsPresenter extends AbstractPagePresenter<SearchResultsPresenter.View> {

	/**
	 * View interface.
	 * 
	 */
	
	public static interface SearchResultsClickHandler{
    }
	
	public static interface ProjectResultsClickHandler extends SearchResultsClickHandler{
        public void onLabelClickEvent(Integer projectId);
    }
	
	public static interface ContactResultsClickHandler extends SearchResultsClickHandler{
        public void onLabelClickEvent(Integer contactId);
    }
	
	public static interface OrgUnitResultsClickHandler extends SearchResultsClickHandler{
        public void onLabelClickEvent(Integer orgUnitId);
    }
	
	@ImplementedBy(SearchResultsView.class)
	public interface View extends ViewInterface {
		
		void setSearchString(String searchText);

		void addSearchData(Object searchData);

		void addResultsPanel();

		ContentPanel getSearchResultsPanel();

		void setProjectClickHandler(ProjectResultsClickHandler handler);

		void setContactClickHandler(ContactResultsClickHandler handler);

		void setOrgUnitClickHandler(OrgUnitResultsClickHandler handler);
	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *            Presenter's view interface.
	 * @param injector
	 *            Injected client injector.
	 */
	@Inject
	public SearchResultsPresenter(View view, Injector injector) {
		super(view, injector);
		// injector.getPageManager().registerPage(this, isPopupView());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.SEARCH_RESULTS;
	}

	@Override
	public void onPageRequest(PageRequest request) {
		// TODO Auto-generated method stub
		// view.initialize() is default
		String title = request.getData(RequestParameter.TITLE).toString();
		// Window.alert("Executing onPageRequest, title is " + title );
		view.setSearchString(title);
		view.setProjectClickHandler(new ProjectResultsClickHandler() {
			@Override
			public void onLabelClickEvent(Integer projectId) {
//				Profiler.INSTANCE.startScenario(Scenario.OPEN_PROJECT);
				Window.alert("Opening project " + projectId );
//				eventBus.navigateRequest(Page.SEARCH_RESULTS.requestWith(RequestParameter.ID, projectId));
				PageRequest request = new PageRequest(Page.PROJECT_DASHBOARD);
				request.addParameter(RequestParameter.ID, projectId );
				eventBus.navigateRequest(request);
			}
		});
		view.setContactClickHandler(new ContactResultsClickHandler() {
			@Override
			public void onLabelClickEvent(Integer contactId) {
//				Profiler.INSTANCE.startScenario(Scenario.OPEN_PROJECT);
				Window.alert("Opening Contact " + contactId );
//				eventBus.navigateRequest(Page.SEARCH_RESULTS.requestWith(RequestParameter.ID, projectId));
				PageRequest request = new PageRequest(Page.CONTACT_DASHBOARD);
				request.addParameter(RequestParameter.ID, contactId );
				eventBus.navigateRequest(request);
			}
		});
		view.setOrgUnitClickHandler(new OrgUnitResultsClickHandler() {
			@Override
			public void onLabelClickEvent(Integer orgUnitId) {
//				Profiler.INSTANCE.startScenario(Scenario.OPEN_PROJECT);
				Window.alert("Opening OrgUnit " + orgUnitId );
//				eventBus.navigateRequest(Page.SEARCH_RESULTS.requestWith(RequestParameter.ID, projectId));
				PageRequest request = new PageRequest(Page.ORGUNIT_DASHBOARD);
				request.addParameter(RequestParameter.ID, orgUnitId );
				eventBus.navigateRequest(request);
			}
		});
		view.addSearchData(request.getData(RequestParameter.CONTENT));
		view.addResultsPanel();
	}

}
