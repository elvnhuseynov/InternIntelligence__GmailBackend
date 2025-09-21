package az.gmail.service;

import az.gmail.dto.ComposeRequest;
import az.gmail.dto.*;
import az.gmail.entity.*;
import az.gmail.enums.EmailStatus;
import az.gmail.enums.RecipientType;
import az.gmail.exception.ResourceNotFoundException;
import az.gmail.mapper.EmailMapper;
import az.gmail.repository.*;
import az.gmail.enums.ActionType;
import az.gmail.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final EmailRecipientRepository recipientRepository;
    private final EmailLabelRepository labelRepository;
    private final EmailLabelMapRepository labelMapRepository;
    private final EmailStarRepository starRepository;
    private final EmailMapper emailMapper;

    private static final int SNIPPET_LEN = 120;

    @Override
    public Page<EmailListItem> getInbox(Long userId, Pageable pageable, String search) {
        Page<Email> page = emailRepository.findEmailsByUserIdAndLabelCode(userId, "INBOX", pageable);
        return page.map(e ->
                emailMapper.toEmailListItem(
                        e,
                        starRepository.existsByEmail_IdAndUser_Id(e.getId(), userId)
                ));
    }

    @Override
    public Page<EmailListItem> getStarred(Long userId, Pageable pageable) {
        Page<Email> page = starRepository.findStarredEmailsByUserId(userId, pageable);
        return page.map(e -> emailMapper.toEmailListItem(e, true));
    }

    @Override
    public Page<EmailListItem> getEmails(Long userId, String folder, Pageable pageable, String search) {
        if ("starred".equalsIgnoreCase(folder)) {
            return getStarred(userId, pageable);
        } else {
            return getInbox(userId, pageable, search);
        }
    }

    @Override
    public EmailDetailResponse getEmailDetail(Long userId, Long emailId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        if (!email.isRead()) {
            email.setRead(true);
            emailRepository.save(email);
        }
        boolean starred = starRepository.existsByEmail_IdAndUser_Id(emailId, userId);
        EmailDetailResponse resp = emailMapper.toEmailDetailResponse(email, starred);

        // To, CC, BCC əlavə edirik
        resp.setTo(email.getRecipients().stream()
                .filter(r -> r.getType() == RecipientType.TO)
                .map(r -> r.getRecipient().getEmail()).toList());
        resp.setCc(email.getRecipients().stream()
                .filter(r -> r.getType() == RecipientType.CC)
                .map(r -> r.getRecipient().getEmail()).toList());
        resp.setBcc(email.getRecipients().stream()
                .filter(r -> r.getType() == RecipientType.BCC)
                .map(r -> r.getRecipient().getEmail()).toList());

        return resp;
    }

    @Transactional
    @Override
    public ComposeResponse compose(Long userId, ComposeRequest req) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Email email = emailMapper.toEmail(req);
        email.setSender(sender);

        // ✅ ActionType-a uyğun status təyin edirik
        email.setStatus(req.getAction() == ActionType.SEND
                ? EmailStatus.SENT
                : EmailStatus.DRAFT);

        email = emailRepository.save(email);

        return emailMapper.toComposeResponse(email);
    }

    @Override
    public void markAsRead(Long userId, Long emailId) {
        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        if (!email.isRead()) {
            email.setRead(true);
            emailRepository.save(email);
        }
    }

    @Override
    public void starEmail(Long userId, Long emailId) {

    }

    @Override
    public void unstarEmail(Long userId, Long emailId) {

    }

    @Override
    public MenuCounts getMenuCounts(Long userId) {
        return new MenuCounts();
    }
}