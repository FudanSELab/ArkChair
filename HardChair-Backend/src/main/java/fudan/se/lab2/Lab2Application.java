package fudan.se.lab2;

import fudan.se.lab2.domain.*;
import fudan.se.lab2.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Welcome to 2020 Software Engineering Lab2.
 * This is your first lab to write your own code and build a spring boot application.
 * Enjoy it :)
 *
 * @author LBW
 */
@SpringBootApplication
public class Lab2Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab2Application.class);
    }

    private static final String ADMIN  = "admin";

    /**
     * This is a function to create some basic entities when the application starts.
     * Now we are using a In-Memory database, so you need it.
     * You can change it as you like.
     */
    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, AuthorityRepository authorityRepository, ConferenceRepository conferenceRepository, User_ConferenceRepository userConferenceRepository, PasswordEncoder passwordEncoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // Create authorities if not exist.
                getOrCreateAuthority("ADMIN", authorityRepository);
                getOrCreateAuthority("CHAIR", authorityRepository);
                getOrCreateAuthority("PC_MEMBER", authorityRepository);
                getOrCreateAuthority("AUTHOR", authorityRepository);

                // Create an admin if not exists.
                if (userRepository.findByUsername(ADMIN) == null) {
                    User admin = new User(
                            ADMIN,
                            passwordEncoder.encode("password"),
                            "admin@qq.com",
                            "FDU",
                            "China",
                            ADMIN,
                            "ADMIN"
                    );
                    userRepository.save(admin);
                }
            }

            private void getOrCreateAuthority(String authorityText, AuthorityRepository authorityRepository) {
                Authority authority = authorityRepository.findByAuthority(authorityText);
                if (authority == null) {
                    authority = new Authority(authorityText);
                    authorityRepository.save(authority);
                }
            }
        };
    }
}

