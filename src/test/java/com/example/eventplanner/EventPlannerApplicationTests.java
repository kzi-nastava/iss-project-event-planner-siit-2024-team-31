package com.example.eventplanner;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import com.example.eventplanner.repository.EventRepositoryTest;
import com.example.eventplanner.service.EventServiceTest;
import com.example.eventplanner.controller.EventControllerTest;

@Suite
@SuiteDisplayName("Event Management Test Suite")
@SelectClasses({
    EventRepositoryTest.class,
    EventServiceTest.class,
    EventControllerTest.class
})
class EventPlannerApplicationTests {

}
