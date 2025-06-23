package com.example.eventplanner;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages(
        value = {"com.example.eventplanner.JUnit5", "com.example.eventplanner.MVC"}
)
class EventPlannerApplicationTests {

}
