package fudan.se.lab2.controller;

import fudan.se.lab2.domain.TokenProcessor;
import fudan.se.lab2.domain.User;
import fudan.se.lab2.exception.UsernameHasBeenRegisteredException;
import fudan.se.lab2.security.jwt.JwtTokenUtil;
import fudan.se.lab2.service.AuthService;
import fudan.se.lab2.controller.request.LoginRequest;
import fudan.se.lab2.controller.request.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBW
 */
@RestController
public class AuthController {

    private AuthService authService;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public AuthController(AuthService authService, JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.authService = authService;
    }


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);

        if(user==null){
            throw new UsernameHasBeenRegisteredException(request.getUsername());
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenProcessor> login(@RequestBody LoginRequest request) {
        Object msg = authService.login(request.getUsername(), request.getPassword());
        if(msg.equals("notFound")) {
            throw new UsernameNotFoundException(request.getUsername());
        }else if (msg.equals("badCredentials")){
            throw new BadCredentialsException(request.getPassword());
        } else{
            User user = (User)msg;
            String token = jwtTokenUtil.generateToken(user);
            TokenProcessor t = new TokenProcessor();
            t.setToken(token);
            t.setUserType(user.getAuthority());
            return ResponseEntity.ok(t);
        }
    }

    /**
     * This is a function to test your connectivity. (健康测试时，可能会用到它）.
     */
    @GetMapping("/welcome")
    public ResponseEntity<Map<String, String>> welcome() {
        Map<String, String> response = new HashMap<>();
        String message = "Welcome to 2020 Software Engineering Lab2. ";
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

}



