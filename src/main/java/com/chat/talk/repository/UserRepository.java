package com.chat.talk.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chat.talk.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	  @Query(value = "from User where username= :username")
	  User findByUsername(@Param("username") String username);

	  @Query(value = "from User where nickname= :nickname")
	  User findByNickname(@Param("nickname") String nickname);

	  Page<User> findByNicknameContainingOrEmailContaining(String nickname,String email,Pageable pageable);
	  
}

