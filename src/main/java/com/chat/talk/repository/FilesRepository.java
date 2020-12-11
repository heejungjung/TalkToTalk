package com.chat.talk.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chat.talk.model.Files;

@Repository
@EnableJpaRepositories
public interface  FilesRepository extends JpaRepository<Files, Long> {

	@Query(value = "from Files where username= :username")
	Files findByUsername(@Param("username") String username);

	@Query(value = "from Files where nickname= :nickname")
	Files findByNickname(@Param("nickname") String nickname);

	@Transactional
	@Modifying
	@Query("UPDATE Files f set f.fileurl= :fileurl where f.username= :username")
	int updateFileurl(@Param("fileurl") String fileurl, @Param("username") String username);

	@Transactional
	@Modifying
	@Query("UPDATE Files f set f.filename= :filename where f.username= :username")
	int updateFilename(@Param("filename") String filename, @Param("username") String username);

	@Transactional
	@Modifying
	@Query("UPDATE Files f set f.rawname= :rawname where f.username= :username")
	int updaterawname(@Param("rawname") String rawname, @Param("username") String username);

	@Transactional
	@Modifying
	@Query("UPDATE Files f set f.message= :message where f.username= :username")
	int updateMessage(@Param("message") String message, @Param("username") String username);
}