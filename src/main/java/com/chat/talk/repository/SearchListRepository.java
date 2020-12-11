package com.chat.talk.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chat.talk.model.DBRoom;

@Repository
public interface SearchListRepository extends JpaRepository<DBRoom, Long> {

   List<DBRoom> findByTitleContains(String search);
}