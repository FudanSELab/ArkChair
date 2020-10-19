package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Rollback(false)
class AuthServiceTest {

    private UserRepository userRepository;
    @Autowired
    public AuthServiceTest(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Test
    void register() {
        AuthService authService = new AuthService(userRepository);
        RegisterRequest registerRequestNotExist = new RegisterRequest();
        registerRequestNotExist.setEmail("fudan@126.com");
        registerRequestNotExist.setFullname("fudan");
        registerRequestNotExist.setUsername("fudan");
        registerRequestNotExist.setOrganization("fudan");
        registerRequestNotExist.setPassword("password");
        registerRequestNotExist.setRegion("SH");

        RegisterRequest registerRequestExist = new RegisterRequest();
        registerRequestExist.setEmail("fudan@126.com");
        registerRequestExist.setFullname("fudan");
        registerRequestExist.setUsername("admin");
        registerRequestExist.setOrganization("fudan");
        registerRequestExist.setPassword("password");
        registerRequestExist.setRegion("SH");

        assertNull(authService.register(registerRequestExist));
        assertTrue(authService.register(registerRequestNotExist).equals(userRepository.findByUsername("fudan")));
    }

    @Test
    void login() {
        AuthService authService = new AuthService(userRepository);
        assertEquals("notFound", authService.login("xuanzitao", "password"));
        assertEquals("badCredentials", authService.login("admin", "psw"));
        assertTrue(authService.login("admin", "password").equals(userRepository.findByUsername("admin")));
    }
}