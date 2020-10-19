package fudan.se.lab2.controller;

import fudan.se.lab2.domain.User;
import fudan.se.lab2.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ProfileController {

    private ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService){
        this.profileService = profileService;
    }

    @GetMapping("/Profile")
    public ResponseEntity<List<Object>> profile(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Object> profile = profileService.findUserAndRelatedConference(user.getUsername());
        return ResponseEntity.ok(profile);
    }

}
