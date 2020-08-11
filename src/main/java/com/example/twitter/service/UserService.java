package com.example.demo.service;

import com.example.demo.domain.Role;
import com.example.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(s);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
    public boolean addUser(User user){
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if(userFromDb!=null){
            return false;
        }
        user.setActivationCode(UUID.randomUUID().toString());
        user.setRoles(Collections.singleton(Role.USER));
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
        sendMessage(user);
        return true;
    }

    private void sendMessage(User user) {
        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s!\n" +
                            "Welcome to Sweater! Please visit next link http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    public boolean activate(String code){
        User u = userRepo.findByActivationCode(code);
        if(u == null){
            return false;
        }
        u.setActivationCode(null);
        userRepo.save(u);
        return true;
    }
    public List<User> findAll(){
        return userRepo.findAll();
    }
    public void save(User user, String username, Map<String, String> form){
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());

        user.getRoles().clear();
        for(String key : form.keySet()){
            if(roles.contains(key)){
                user.getRoles().contains(Role.valueOf(key));
            }
        }
        userRepo.save(user);
    }
    public void updateUser(User user, String password, String email){
        String userEmail = user.getEmail();
        boolean isEmailChanged = (userEmail!=null&&!userEmail.equals(email))||
                (email!=null&&!email.equals(userEmail));
        if(isEmailChanged){
            user.setEmail(email);
            if(!StringUtils.isEmpty(email)){
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }
        if(!StringUtils.isEmpty(password)){
            user.setPassword(password);
        }
        userRepo.save(user);
        if(isEmailChanged){
            sendMessage(user);
        }
    }
}
