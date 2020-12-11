package com.chat.talk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chat.talk.model.DBMsg;

@Repository
public interface DBMsgRepository extends JpaRepository<DBMsg, Long> {

}
