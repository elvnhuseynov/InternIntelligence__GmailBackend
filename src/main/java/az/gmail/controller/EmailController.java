package az.gmail.controller;

import az.gmail.dto.ComposeRequest;
import az.gmail.dto.*;
import az.gmail.service.EmailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping
    public Page<EmailListItem> getEmails(
            @RequestHeader("X-User-Id") @Positive Long userId,
            @RequestParam(defaultValue = "inbox") String folder,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String search) {
        return emailService.getEmails(userId, folder, pageable, search);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailDetailResponse> getEmail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(emailService.getEmailDetail(userId, id));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        emailService.markAsRead(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ComposeResponse> compose(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ComposeRequest req) {
        return ResponseEntity.ok(emailService.compose(userId, req));
    }

    @PatchMapping("/{id}/star")
    public ResponseEntity<Void> star(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        emailService.starEmail(userId, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/star")
    public ResponseEntity<Void> unstar(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        emailService.unstarEmail(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/menu-counts")
    public ResponseEntity<MenuCounts> menuCounts(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(emailService.getMenuCounts(userId));
    }
}
