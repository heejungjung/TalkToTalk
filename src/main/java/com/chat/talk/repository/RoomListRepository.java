package com.chat.talk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chat.talk.model.DBRoom;

@Repository
public interface RoomListRepository extends JpaRepository<DBRoom, Long> {

	  @Query(value = "from DBRoom where title= :title")
	  DBRoom findByTitle(@Param("title") String title);
	  
	  @Query(value = "from DBRoom where title= :title")
	  DBRoom deleteByTitle(@Param("title") String title);
}
