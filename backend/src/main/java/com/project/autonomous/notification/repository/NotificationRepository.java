package com.project.autonomous.notification.repository;

import com.project.autonomous.notification.entity.Notification;
import com.project.autonomous.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiverOrderByCreateAtDesc(User receiver);

    @Transactional // 전체 알림 삭제
    @Modifying(clearAutomatically = true)
    void deleteAllByReceiver(User receiver);

    @Transactional // 전체 알림 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("update Notification n set n.checked = true where n.receiver = :receiver")
    void checkAllByReceiver(@Param("receiver") User receiver);
}