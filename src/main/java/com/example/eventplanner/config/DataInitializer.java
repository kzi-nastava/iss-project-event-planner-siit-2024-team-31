package com.example.eventplanner.config;

import com.example.eventplanner.model.EventTypeProductLink;
import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.product.Category;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDefaultRoles(RoleRepository roleRepository) {
        return args -> {
            //default Roles
            if (roleRepository.count() == 0) {
                roleRepository.saveAndFlush(new Role("ROLE_USER"));
                roleRepository.saveAndFlush(new Role("ROLE_PUP"));
                roleRepository.saveAndFlush(new Role("ROLE_OD"));
                roleRepository.saveAndFlush(new Role("ROLE_ADMIN"));
                System.out.println("Default roles have been initialized.");
            }
        };
    }

    @Bean
    CommandLineRunner initDefaultEventTypes(EventTypesRepository eventTypesRepository) {
        return args -> {
            //default EventTypes
            if (eventTypesRepository.count() == 0) {
                eventTypesRepository.saveAndFlush(new EventType(
                        "Conference",
                        "",
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Exhibition",
                        "",
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Festival",
                        "",
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Networking",
                        "",
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Workshop",
                        "",
                        null
                ));
            }
            System.out.print("Default events types have been initialized.");
        };
    }


    @Bean
    CommandLineRunner initDefaultProductCategoriesAndStatuses(ProductCategoryRepository productCategoryRepository, StatusRepository statusRepository) {
        return args -> {
            if (statusRepository.count() == 0) {
                statusRepository.saveAndFlush(new Status("ACTIVE", ""));
                statusRepository.saveAndFlush(new Status("INACTIVE", ""));
                statusRepository.saveAndFlush(new Status("PENDING", ""));
            }
            if (productCategoryRepository.count() == 0) {
                Status activeStatus = statusRepository.getStatusByName("ACTIVE");

                List<Category> defaultCategories = List.of(
                        new Category("Catering", "Food and beverage services for events", activeStatus, null),
                        new Category("Photography", "Professional event photography services", activeStatus, null),
                        new Category("Music", "Music and DJ services for all occasions", activeStatus, null),
                        new Category("Decorations", "Event decorations and theme designs", activeStatus, null),
                        new Category("Transportation", "Transportation services for guests or equipment", activeStatus, null),
                        new Category("Venue", "Event venues and spaces for hire", activeStatus, null),
                        new Category("Event Planning", "Professional event planning and coordination services", activeStatus, null),
                        new Category("Lighting", "Lighting equipment and setup for events", activeStatus, null),
                        new Category("AV Equipment", "Audio-visual equipment rental and support", activeStatus, null),
                        new Category("Security", "Security services to ensure event safety", activeStatus, null)
                );

                productCategoryRepository.saveAllAndFlush(defaultCategories);
            }
            System.out.print("Default categories and statuses have been initialized.");
        };

    }

    @Bean
    CommandLineRunner initDefaultAdmin(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {

                Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                User user = new User();
                user.setEmail("admin-roman");
                user.setPassword(passwordEncoder.encode("admin-roman-password"));
                user.setFirstName("Admin");
                user.setRole(adminRole);
                user.setActive(true);

                userRepository.saveAndFlush(user);
            }
        };
    }
}