package com.smartsure.auth.config;

import com.smartsure.auth.entity.Role;
import com.smartsure.auth.entity.RoleName;
import com.smartsure.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, RoleName.ROLE_CUSTOMER));
            roleRepository.save(new Role(null, RoleName.ROLE_ADMIN));
            log.info("Default roles initialized");
        }
    }
}
