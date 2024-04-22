package com.chat.talk.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.talk.model.Profiles;
import com.chat.talk.repository.ProfilesRepository;
import com.chat.talk.repository.UserRepository;

@Service
public class ProfilesService {
	
	@Autowired
	ProfilesRepository filesRepository;
	
	@Autowired
	UserRepository userRepository;
	
	public void save(Profiles files) {
		filesRepository.save(files);
	}
	
	public String profile(String sender) {
		Profiles f = filesRepository.findByUsername(sender);
		// String result = f.getFileurl()+"s_"+f.getFilename();
		String result = f.getFileurl();
		
		return result;
	}
}
