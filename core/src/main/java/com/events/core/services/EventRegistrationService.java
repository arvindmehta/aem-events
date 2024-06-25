package com.events.core.services;

import com.events.core.pojo.EventRegistrationDTO;
import org.apache.sling.api.SlingHttpServletRequest;

public interface EventRegistrationService {

    String registerEvent(SlingHttpServletRequest request, EventRegistrationDTO eventRegistrationDTO);
}
