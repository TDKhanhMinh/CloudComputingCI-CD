package com.example.Final;
import com.example.Final.entity.securityservice.Roles;
import com.example.Final.entity.securityservice.User;
import com.example.Final.repository.RolesRepository;
import com.example.Final.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitialization {

    @Bean
    public CommandLineRunner initData(RolesRepository rolesRepository, UserService userService) {
        return args -> {
            // Create ROLE_ADMIN if not exists
            Roles adminRole = rolesRepository.findRolesByName("ROLE_ADMIN");
            if (adminRole == null) {
                adminRole = new Roles();
                adminRole.setName("ROLE_ADMIN");
                rolesRepository.save(adminRole);
                System.out.println("Role ROLE_ADMIN created.");
            } else {
                System.out.println("Role ROLE_ADMIN already exists.");
            }

            // Create ROLE_REALTOR if not exists
            Roles realtorRole = rolesRepository.findRolesByName("ROLE_REALTOR");
            if (realtorRole == null) {
                realtorRole = new Roles();
                realtorRole.setName("ROLE_REALTOR");
                rolesRepository.save(realtorRole);
                System.out.println("Role ROLE_REALTOR created.");
            } else {
                System.out.println("Role ROLE_REALTOR already exists.");
            }
            // Create admin user if not exists
            if (userService.findUserByEmail("admin@admin.com") == null) {
                User realtor = new User();
                realtor.setEmail("admin@admin.com");
                realtor.setPassword("admin123"); // Plain text, should be encoded in the service
                realtor.setFullName("Admin");
                realtor.setPhone("123456789");
                userService.createAdmin(realtor);
                System.out.println("Admin user created: admin@admin.com");
            } else {
                System.out.println("Admin user already exists.");
            }
        };
    }
}
