package com.example.eventplanner.config;

import com.example.eventplanner.model.Role;
import com.example.eventplanner.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            //after every reinitialization of server create roles automatically (if repository is empty)
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("ROLE_USER"));
                roleRepository.save(new Role("ROLE_PUP"));
                roleRepository.save(new Role("ROLE_OD"));
                roleRepository.save(new Role("ROLE_ADMIN"));
                System.out.println("Default roles have been initialized.");
            }
        };
    }
}