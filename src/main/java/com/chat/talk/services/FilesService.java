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
	
	public void save(Files files,String username) {
		Files f = new Files();
		f.setUsername(username);
		f.setFilename(files.getFilename());
		f.setRawname(files.getRawname());
		f.setFileurl(files.getFileurl());
		
		filesRepository.save(f);
	}
	
	public String profile(String sender) {
		Files f = filesRepository.findByUsername(sender);
		String result = f.getFileurl()+"s_"+f.getFilename();
		
		return result;
	}
}
