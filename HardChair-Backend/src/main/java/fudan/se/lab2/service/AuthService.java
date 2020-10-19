package fudan.se.lab2.service;

import fudan.se.lab2.domain.User;
import fudan.se.lab2.repository.UserRepository;
import fudan.se.lab2.controller.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author LBW
 */
@Service
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if(user!=null){
            return null;
        }else {
            String encodedPassword = passwordEncoder.encode(request.getPassword().trim());
            User newUser = new User(
                    request.getUsername(),
                    encodedPassword,
                    request.getEmail(),
                    request.getOrganization(),
                    request.getRegion(),
                    request.getFullname(),
                    "USER"
            );
            userRepository.save(newUser);
            return newUser;
        }
    }

    /**
     * @param username username from the front end
     * @param password password from the front end
     * @return return token info to the front end(call generateToken func)
     * return value sent to : AuthController
     */
    public Object login(String username, String password) {
        User user= userRepository.findByUsername(username);
        // Step1 : user can be found or not
        if(user==null){
            return "notFound";
        } else {
            // Step2: username and password match or not
            String psw = user.getPassword();
            if(passwordEncoder.matches(password,psw)){
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return user;
            }else{
                return "badCredentials";
            }
        }
    }
}