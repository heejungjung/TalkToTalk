package com.chat.talk.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chat.talk.model.Files;
import com.chat.talk.model.Role;
import com.chat.talk.model.User;
import com.chat.talk.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User save(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        Role role = new Role();
        role.setId(1l);
        user.getRoles().add(role);
        return userRepository.save(user);
    }
    
    public String idCheck(String userId) {
        if (userRepository.findByUsername(userId) == null) {
            return "YES";
        } else {
            return "NO";
        }
    }
    
    public String nnCheck(String nickname) {
    	System.out.println("serviceeeeeeeeeeeeee:"+nickname);
        if (userRepository.findByNickname(nickname) == null) {
            return "YES";
        } else {
            return "NO";
        }
    }
	
	public String sex(String sender) {
		User user = userRepository.findByUsername(sender);
		return user.getSex();
	}
    
}