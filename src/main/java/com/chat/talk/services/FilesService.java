package com.chat.talk.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chat.talk.model.Files;
import com.chat.talk.repository.FilesRepository;
import com.chat.talk.repository.UserRepository;

@Service
public class FilesService {
	
	@Autowired
	FilesRepository filesRepository;
	
	@Autowired
	UserRepository userRepository;
	
	public void save(Files files) {
		filesRepository.save(files);
	}
	
	public String profile(String sender) {
		Files f = filesRepository.findByUsername(sender);
		String result = f.getFileurl()+"s_"+f.getFilename();
		
		return result;
	}
}
