package org.sigmah.client.ui.presenter.zone;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.SearchView;
import org.sigmah.client.ui.widget.tab.Tab;
import org.sigmah.client.ui.widget.tab.TabBar;
import org.sigmah.client.ui.widget.tab.TabId;
import org.sigmah.client.ui.widget.tab.TabBar.TabBarListener;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.conf.PropertyName;

import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

/**
* Organization banner presenter displaying organization's name and logo.
* 
* @author 
*/

@Singleton
public class SearchPresenter extends AbstractZonePresenter<SearchPresenter.View> {
	
	@Inject
	public SearchPresenter(View view, Injector injector) {
		super(view, injector);
		// TODO Auto-generated constructor stub
	}

	/**
	 * View interface.
	 */
	@ImplementedBy(SearchView.class)
	public static interface View extends ViewInterface {

		HasHTML getNameLabel();
		Panel getSearchBarPanel();
		ListBox getSearchOptions();
		TextBox getSearchText();
		Button getSearchButton();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		// TODO Auto-generated method stub
		return Zone.SEARCH_BANNER;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		
		view.getSearchButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				final PageRequest request = new PageRequest(Page.SEARCH_RESULTS);
				//have to add a request param here so that it opens a tab with a new title
				//request.addParameter(RequestParameter.HEADER, view.getSearchText());
				eventBus.navigateRequest(request);
			}

		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {
		// TODO Auto-generated method stub
		
	}

}
