/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.client.workbench.widgets.popups.activities.multiple;

import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.client.annotations.OnReveal;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.DefaultPlaceResolver;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.BeforeClosePlaceEvent;

/**
 * Popup presenter for when there are multiple Activities for a Place
 */
@ApplicationScoped
@WorkbenchPopup(identifier = "workbench.activities.multiple")
public class MultipleActivitiesFoundPresenter {

    private String requestedPlaceIdentifier;

    public interface View
            extends
            UberView<MultipleActivitiesFoundPresenter> {

        void setRequestedPlaceIdentifier( final String requestedPlaceIdentifier );

        void setActivities( Set<Activity> activities );

    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ActivityManager activityManager;

    @Inject
    private Event<BeforeClosePlaceEvent> closePlaceEvent;

    @Inject
    private DefaultPlaceResolver defaultPlaceResolver;

    private PlaceRequest place;

    @PostConstruct
    public void init() {
        view.init( this );
    }

    @OnStart
    public void onStart( final PlaceRequest place ) {
        this.place = place;
    }

    @OnReveal
    public void onReveal() {
        requestedPlaceIdentifier = place.getParameter( "requestedPlaceIdentifier", null );
        view.setRequestedPlaceIdentifier( requestedPlaceIdentifier );
        view.setActivities( activityManager.getActivities( new DefaultPlaceRequest( requestedPlaceIdentifier ) ) );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Multiple activities detected";
    }

    @WorkbenchPartView
    public UberView<MultipleActivitiesFoundPresenter> getView() {
        return view;
    }

    public void close() {
        closePlaceEvent.fire( new BeforeClosePlaceEvent( this.place ) );
    }

    public void activitySelected( final Activity activity ) {
        defaultPlaceResolver.saveDefaultEditor( place.getIdentifier(), activity.getSignatureId() );
        placeManager.goTo( new DefaultPlaceRequest( requestedPlaceIdentifier ) );
        close();
    }

}
