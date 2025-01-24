package com.example.eventplanner.config;

import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.product.Category;
import com.example.eventplanner.model.user.User;
import com.example.eventplanner.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    @Order(1)
    CommandLineRunner initDefaultRolesAndStatuses(RoleRepository roleRepository, StatusRepository statusRepository) {
        return args -> {
            //default Roles
            if (roleRepository.count() == 0) {
                roleRepository.saveAndFlush(new Role("ROLE_USER"));
                roleRepository.saveAndFlush(new Role("ROLE_PUP"));
                roleRepository.saveAndFlush(new Role("ROLE_OD"));
                roleRepository.saveAndFlush(new Role("ROLE_ADMIN"));
                System.out.println("Default roles have been initialized.");
            }
            if (statusRepository.count() == 0) {
                statusRepository.saveAndFlush(new Status("ACTIVE", ""));
                statusRepository.saveAndFlush(new Status("INACTIVE", ""));
                statusRepository.saveAndFlush(new Status("PENDING", ""));
                System.out.print("Default statuses have been initialized.");
            }
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner initDefaultProductCategories(ProductCategoryRepository productCategoryRepository, StatusRepository statusRepository) {
        return args -> {
            if (productCategoryRepository.count() == 0) {
                Status activeStatus = statusRepository.getStatusByName("ACTIVE");
                List<Category> defaultCategories = List.of(
                        new Category("Catering", "Food and beverage services for events", activeStatus),
                        new Category("Photography", "Professional event photography services", activeStatus),
                        new Category("Music", "Music and DJ services for all occasions", activeStatus),
                        new Category("Decorations", "Event decorations and theme designs", activeStatus),
                        new Category("Transportation", "Transportation services for guests or equipment", activeStatus),
                        new Category("Venue", "Event venues and spaces for hire", activeStatus),
                        new Category("Event Planning", "Professional event planning and coordination services", activeStatus),
                        new Category("Lighting", "Lighting equipment and setup for events", activeStatus),
                        new Category("AV Equipment", "Audio-visual equipment rental and support", activeStatus),
                        new Category("Security", "Security services to ensure event safety", activeStatus)
                );

                productCategoryRepository.saveAllAndFlush(defaultCategories);
                System.out.print("Default categories have been initialized.");
            }
        };
    }

    @Bean
    @Order(3)
    CommandLineRunner initDefaultEventTypes(EventTypesRepository eventTypesRepository, StatusRepository statusRepository) {
        return args -> {
            //default EventTypes
            if (eventTypesRepository.count() == 0) {
                Status activeStatus = statusRepository.getStatusByName("ACTIVE");
                Status inactiveStatus = statusRepository.getStatusByName("INACTIVE");

                eventTypesRepository.saveAndFlush(new EventType(
                        "All",
                        "",
                        activeStatus,
                        null

                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Conference",
                        "",
                        activeStatus,
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Exhibition",
                        "",
                        activeStatus,
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Festival",
                        "",
                        activeStatus,
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Networking",
                        "",
                        activeStatus,
                        null
                ));
                eventTypesRepository.saveAndFlush(new EventType(
                        "Workshop",
                        "",
                        inactiveStatus,
                        null
                ));
                System.out.print("Default events types have been initialized.");
            }
        };
    }




    @Bean
    @Order(4)
    CommandLineRunner initDefaultAdmin(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {

                Role adminRole = roleRepository.findByName("ROLE_ADMIN");
                User admin = new User();
                admin.setEmail("admin");
                admin.setPassword(passwordEncoder.encode("1"));
                admin.setFirstName("Admin");
                admin.setRole(adminRole);
                admin.setActive(true);

                Role userRole = roleRepository.findByName("ROLE_USER");
                User user = new User();
                user.setEmail("user");
                user.setPassword(passwordEncoder.encode("1"));
                user.setFirstName("User");
                user.setRole(userRole);
                user.setActive(true);

                Role pupRole = roleRepository.findByName("ROLE_PUP");
                User pup = new User();
                pup.setEmail("pup");
                pup.setPassword(passwordEncoder.encode("1"));
                pup.setFirstName("Pup");
                pup.setRole(pupRole);
                pup.setActive(true);

                Role odRole = roleRepository.findByName("ROLE_OD");
                User od = new User();
                od.setEmail("od");
                od.setPassword(passwordEncoder.encode("1"));
                od.setFirstName("Od");
                od.setRole(odRole);
                od.setActive(true);

                userRepository.saveAndFlush(admin);
                userRepository.saveAndFlush(pup);
                userRepository.saveAndFlush(od);
                userRepository.saveAndFlush(user);

                System.out.println("Default admin user has been initialized.");
            }

        };
    }
}