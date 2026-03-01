package com.creatival;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDeleteScheduler {
	private final UserService userService;

    @Scheduled(cron = "0 45 11 * * ?")
    @Transactional
    public void deleteInactiveUsers() {

        LocalDateTime threshold = LocalDateTime.now().minusDays(0);

        List<Users> usersToDelete =
               	userService.getAllByIsDeleted(threshold);

        if (usersToDelete.isEmpty()) {
            System.out.println("삭제 대상 유저 없음");
            return;
        }
        
        userService.userAllDelete(usersToDelete);
    }
}
