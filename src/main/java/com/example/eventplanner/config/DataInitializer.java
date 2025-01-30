package com.example.eventplanner.config;

import com.example.eventplanner.model.Role;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.product.ProductCategory;
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
                roleRepository.saveAndFlush(new Role("USER"));
                roleRepository.saveAndFlush(new Role("PUP"));
                roleRepository.saveAndFlush(new Role("OD"));
                roleRepository.saveAndFlush(new Role("ADMIN"));
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
                List<ProductCategory> defaultCategories = List.of(
                        new ProductCategory("Catering", "Food and beverage services for events", activeStatus),
                        new ProductCategory("Photography", "Professional event photography services", activeStatus),
                        new ProductCategory("Music", "Music and DJ services for all occasions", activeStatus),
                        new ProductCategory("Decorations", "Event decorations and theme designs", activeStatus),
                        new ProductCategory("Transportation", "Transportation services for guests or equipment", activeStatus),
                        new ProductCategory("Venue", "Event venues and spaces for hire", activeStatus),
                        new ProductCategory("Event Planning", "Professional event planning and coordination services", activeStatus),
                        new ProductCategory("Lighting", "Lighting equipment and setup for events", activeStatus),
                        new ProductCategory("AV Equipment", "Audio-visual equipment rental and support", activeStatus),
                        new ProductCategory("Security", "Security services to ensure event safety", activeStatus)
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
            if (eventTypesRepository.count() == 0) {
                Status activeStatus = statusRepository.getStatusByName("ACTIVE");
                Status inactiveStatus = statusRepository.getStatusByName("INACTIVE");

                eventTypesRepository.saveAllAndFlush(List.of(
                        new EventType(
                                "All",
                                "Apply this event type to events that do not fit into any specific category or combine elements from multiple categories. A general-purpose option for diverse or hybrid events.",
                                activeStatus,
                                null
                        ),
                        new EventType(
                                "Conference",
                                "Formal gatherings focused on knowledge-sharing, expert discussions, and presentations within a specific industry or field. Ideal for professional networking and collaborative learning.",
                                activeStatus,
                                null
                        ),
                        new EventType(
                                "Exhibition",
                                "Events showcasing products, services, art, or innovations to a targeted audience. Designed for demonstrations, promotions, and direct engagement with attendees.",
                                activeStatus,
                                null
                        ),
                        new EventType(
                                "Festival",
                                "Large-scale celebrations featuring cultural, artistic, or entertainment activities. Includes live performances, food, games, and community-driven experiences.",
                                activeStatus,
                                null
                        ),
                        new EventType(
                                "Networking",
                                "Events centered around building professional or social connections. Facilitates interactions between participants through structured activities or informal mingling.",
                                activeStatus,
                                null
                        ),
                        new EventType(
                                "Workshop",
                                "Interactive sessions aimed at skill development, hands-on training, or collaborative problem-solving. Led by experts to encourage active participation and learning.",
                                inactiveStatus,
                                null
                        )
                ));

                System.out.println("Default event types have been initialized successfully.");
            }
        };
    }




    @Bean
    @Order(4)
    CommandLineRunner initDefaultAdmin(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {

                Role adminRole = roleRepository.findByName("ADMIN");
                User admin = new User();
                admin.setEmail("admin");
                admin.setPassword(passwordEncoder.encode("1"));
                admin.setFirstName("Admin");
                admin.setRole(adminRole);
                admin.setActive(true);

                Role userRole = roleRepository.findByName("USER");
                User user = new User();
                user.setEmail("user");
                user.setPassword(passwordEncoder.encode("1"));
                user.setFirstName("User");
                user.setRole(userRole);
                user.setActive(true);

                Role pupRole = roleRepository.findByName("PUP");
                User pup = new User();
                pup.setEmail("pup");
                pup.setPassword(passwordEncoder.encode("1"));
                pup.setFirstName("Pup");
                pup.setRole(pupRole);
                pup.setActive(true);

                Role odRole = roleRepository.findByName("OD");
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