package com.unievt.notification.repository;

import com.unievt.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByDestinataireId(Long destinataireId);
    List<Notification> findByDestinataireIdAndLuFalse(Long destinataireId);
}
