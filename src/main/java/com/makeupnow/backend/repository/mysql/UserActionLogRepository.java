package com.makeupnow.backend.repository.mysql;

import com.makeupnow.backend.model.mysql.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {

    List<UserActionLog> findByAnonymizedTrue();

    List<UserActionLog> findByUserId(Long userId);

    List<UserActionLog> findByUserIdAndAnonymizedTrue(Long userId);
}
