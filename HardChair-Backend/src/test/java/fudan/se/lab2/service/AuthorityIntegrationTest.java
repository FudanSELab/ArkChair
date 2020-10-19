package fudan.se.lab2.service;

import fudan.se.lab2.controller.request.RegisterRequest;
import fudan.se.lab2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class AuthorityIntegrationTest {
    private UserRepository userRepository;

    @Autowired
    public AuthorityIntegrationTest(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Test
    void registerAndLogin(){
        AuthService authService = new AuthService(userRepository);
        RegisterRequest registerRequestNotExist = new RegisterRequest();
        registerRequestNotExist.setEmail("fudan@126.com");
        registerRequestNotExist.setFullname("userForIntegratedTest");
        registerRequestNotExist.setUsername("userForIntegratedTest");
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

        //test register
        assertNull(authService.register(registerRequestExist));
        assertTrue(authService.register(registerRequestNotExist).equals(userRepository.findByUsername("userForIntegratedTest")));

        //test login
        assertEquals("notFound", authService.login("xuanzitao", "password"));
        assertEquals("badCredentials", authService.login("userForIntegratedTest", "psw"));
        assertTrue(authService.login("userForIntegratedTest", "password").equals(userRepository.findByUsername("userForIntegratedTest")));

    }
}