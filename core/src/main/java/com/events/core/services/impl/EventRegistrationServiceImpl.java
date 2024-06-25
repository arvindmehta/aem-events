package com.events.core.services.impl;

import com.events.core.pojo.EventRegistrationDTO;
import com.events.core.services.EventRegistrationService;
import com.events.core.services.RestAPIService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = EventRegistrationService.class, immediate = true,
        property = {
                org.osgi.framework.Constants.BUNDLE_NAME + "= Event Registration Service",
                org.osgi.framework.Constants.SERVICE_DESCRIPTION + "=Service for registering users for Events"
        })
public class EventRegistrationServiceImpl implements EventRegistrationService {

    public static final String EVENT_REGISTRATION_URL = "/api/register-event";

    @Reference
    RestAPIService restAPIService;

    @Override
    public String registerEvent(SlingHttpServletRequest request, EventRegistrationDTO eventRegistrationDTO) {
        return restAPIService.postResponse(EVENT_REGISTRATION_URL,null,eventRegistrationDTO,request);
    }
}
