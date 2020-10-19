package fudan.se.lab2.service;

import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.UserRepository;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Rollback(false)//不回滚
class JwtUserDetailsServiceTest {

    private UserRepository userRepository;

    @Autowired
    JwtUserDetailsServiceTest(UserRepository userRepository){this.userRepository = userRepository;}

    @Test
    void loadUserByUsername(){
        JwtUserDetailsService jwtUserDetailsService = new JwtUserDetailsService(userRepository);
        User user = new User("zitao",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "xuanzitao",
                "USER");
        if(userRepository.findByUsername("zitao")==null) {
            userRepository.save(user);
        }

        assertEquals(userRepository.findByUsername("zitao"), jwtUserDetailsService.loadUserByUsername("zitao"));


        try{
            jwtUserDetailsService.loadUserByUsername("notExist");
            fail("Expected an UsernameNotFoundException to be thrown");
        }catch (Exception ex){
            assertTrue(ex instanceof UsernameNotFoundException);
            assertTrue(ex.getMessage().contains("User: '" + "notExist" + "' not found."));

        }

    }

    @Test
    void findUserById(){
        JwtUserDetailsService jwtUserDetailsService = new JwtUserDetailsService(userRepository);
        User user = new User("zitaoForFindUser",
                "password",
                "zitao@126.com",
                "SH",
                "SH",
                "zitaoForFindUser",
                "USER");
        if(userRepository.findByUsername("zitaoForFindUser")==null) {
            userRepository.save(user);
        }

        assertEquals(user.toString(), jwtUserDetailsService.findUserById(user.getId()).toString());
    }
}