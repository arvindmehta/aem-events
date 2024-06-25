package com.events.core.servlets;

import com.events.core.pojo.EventRegistrationDTO;
import com.events.core.services.EventRegistrationService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class EventRegistrationServletTest {
    public AemContext context = new AemContext();

    public static final String MOCKED_RESPONSE = "{ \"status\": \"Success\", \"errors\": [], \"data\": { \"message\": \"You have been successfully registered for the event \" } }";

    private EventRegistrationServlet eventRegistrationServlet;

    @Mock
    EventRegistrationService eventRegistrationService;


    @BeforeEach
    public void setUp() {
        context.registerService(EventRegistrationService.class, eventRegistrationService);
        eventRegistrationServlet = context.registerInjectActivateService(new EventRegistrationServlet());
    }

    @Test
    void testDoPost() throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("firstName", "John");
        paramMap.put("lastName", "Doe");
        paramMap.put("email", "john.doe@yopmail.com");
        context.request().setParameterMap(paramMap);
        context.requestPathInfo().setResourcePath("/content/events/event.register-event.json");
        eventRegistrationServlet.doPost(context.request(), context.response());
        assertEquals(200, context.response().getStatus());
    }
}
