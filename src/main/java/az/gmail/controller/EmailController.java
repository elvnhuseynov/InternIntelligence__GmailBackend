package az.gmail.controller;

import az.gmail.dto.ComposeRequest;
import az.gmail.dto.*;
import az.gmail.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/inbox")
    public Page<EmailListItem> inbox(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        Pageable p = PageRequest.of(page, size);
        return emailService.getInbox(userId, p, search);
    }

    @GetMapping("/starred")
    public Page<EmailListItem> starred(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable p = PageRequest.of(page, size);
        return emailService.getStarred(userId, p);
    }

    @GetMapping("/emails/{id}")
    public EmailDetailResponse getEmail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return emailService.getEmailDetail(userId, id);
    }

    @PostMapping("/emails/{id}/read")
    public ResponseEntity<Void> markRead(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        emailService.markAsRead(userId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/compose")
    public ComposeResponse compose(@RequestHeader("X-User-Id") Long userId,
                                   @Validated @RequestBody ComposeRequest req) {
        return emailService.compose(userId, req);
    }

    @PostMapping("/emails/{id}/star")
    public ResponseEntity<Void> star(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        emailService.starEmail(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/emails/{id}/star")
    public ResponseEntity<Void> unstar(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        emailService.unstarEmail(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/menu-counts")
    public MenuCounts menuCounts(@RequestHeader("X-User-Id") Long userId) {
        return emailService.getMenuCounts(userId);
    }
}
