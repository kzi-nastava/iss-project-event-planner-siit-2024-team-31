package com.example.eventplanner.config;

import com.example.eventplanner.model.EventTypeProductLink;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.repository.EventRepository;
import com.example.eventplanner.repository.EventTypesRepository;
import com.example.eventplanner.repository.RoleRepository;
import com.example.eventplanner.repository.StatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            //default Roles
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("ROLE_USER"));
                roleRepository.save(new Role("ROLE_PUP"));
                roleRepository.save(new Role("ROLE_OD"));
                roleRepository.save(new Role("ROLE_ADMIN"));
                System.out.println("Default roles have been initialized.");
            }
        };
    }

    @Bean
    CommandLineRunner initEventTypes(EventTypesRepository eventTypesRepository) {
        return args -> {
            //default EventTypes
            if (eventTypesRepository.count() == 0) {
                eventTypesRepository.save(new EventType(
                        "Conference",
                        "",
                        null
                ));
                eventTypesRepository.save(new EventType(
                        "Exhibition",
                        "",
                        null
                ));
                eventTypesRepository.save(new EventType(
                        "Festival",
                        "",
                        null
                ));
                eventTypesRepository.save(new EventType(
                        "Networking",
                        "",
                        null
                ));
                eventTypesRepository.save(new EventType(
                        "Workshop",
                        "",
                        null
                ));
            }
        };
    }

    @Bean
    CommandLineRunner initStatuses(StatusRepository statusRepository) {
        return args -> {
            //default statuses
            if (statusRepository.count() == 0) {
                statusRepository.save(new Status("ACTIVE", ""));
                statusRepository.save(new Status("INACTIVE", ""));
                statusRepository.save(new Status("PENDING", ""));
            }
        };
    }
}