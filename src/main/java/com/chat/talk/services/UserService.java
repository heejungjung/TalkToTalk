package com.chat.talk.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    
    //유저 정보 저장
    public User save(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        Role role = new Role();
        role.setId(2l);
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    //아이디 중복 체크
    public String idCheck(String userId) {
        User user = userRepository.findByUsername(userId);
        if (user == null) {
            return "YES";
        } else {
            return "NO";
        }
    }

    //닉네임 중복 체크
    public String nnCheck(String nickname) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            return "YES";
        } else {
            return "NO";
        }
    }

    //유저 성별 체크
	public String sex(String sender) {
		User user = userRepository.findByUsername(sender);
		return user.getSex();
	}
    
}