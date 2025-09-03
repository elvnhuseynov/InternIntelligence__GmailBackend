package az.gmail.service;

import az.gmail.dto.ComposeResponse;
import az.gmail.dto.EmailDetailResponse;
import az.gmail.dto.EmailListItem;
import az.gmail.dto.MenuCounts;
import az.gmail.dto.ComposeRequest;
import az.gmail.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailService {
    Page<EmailListItem> getInbox(Long userId, Pageable pageable, String search);

    Page<EmailListItem> getStarred(Long userId, Pageable pageable);

    EmailDetailResponse getEmailDetail(Long userId, Long emailId);

    ComposeResponse compose(Long userId, ComposeRequest req);

    void markAsRead(Long userId, Long emailId);

    void starEmail(Long userId, Long emailId);

    void unstarEmail(Long userId, Long emailId);

    MenuCounts getMenuCounts(Long userId);
}
