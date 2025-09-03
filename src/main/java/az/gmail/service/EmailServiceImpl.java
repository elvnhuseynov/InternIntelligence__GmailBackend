package az.gmail.service;

import az.gmail.dto.ComposeRequest;
import az.gmail.dto.*;
import az.gmail.entity.*;
import az.gmail.enums.EmailStatus;
import az.gmail.enums.RecipientType;
import az.gmail.exception.ResourceNotFoundException;
import az.gmail.repository.*;
import az.gmail.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class EmailServiceImpl implements EmailService{
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;
    private final EmailRecipientRepository recipientRepository;
    private final EmailLabelRepository labelRepository;
    private final EmailLabelMapRepository labelMapRepository;
    private final EmailStarRepository starRepository;

    private static final int SNIPPET_LEN = 120;

    @Override
    public Page<EmailListItem> getInbox(Long userId, Pageable pageable, String search) {
        Page<Email> page = emailRepository.findEmailsByUserIdAndLabelCode(userId, "INBOX", pageable);
        return page.map(e -> mapToListItem(e, userId));
    }

    @Override
    public Page<EmailListItem> getStarred(Long userId, Pageable pageable) {
        Page<Email> page = starRepository.findStarredEmailsByUserId(userId, pageable);
        return page.map(e -> mapToListItem(e, userId));
    }

    @Override
    public EmailDetailResponse getEmailDetail(Long userId, Long emailId) {
        Email email = emailRepository.findById(emailId).orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        // mark read for this user context (optional: ensure mapping exists)
        // simple version:
        if(!email.isRead()) {
            email.setRead(true);
            emailRepository.save(email);
        }
        EmailDetailResponse resp = new EmailDetailResponse();
        resp.setId(email.getId());
        resp.setSubject(email.getSubject());
        resp.setBody(email.getBody());
        resp.setSenderName(email.getSender().getFullName());
        resp.setCreatedAt(email.getCreatedAt());
        resp.setRead(email.isRead());
        resp.setStarred(starRepository.existsByEmail_IdAndUser_Id(email.getId(), userId));

        List<String> to = email.getRecipients().stream()
                .filter(r -> r.getType() == RecipientType.TO)
                .map(r -> r.getRecipient().getEmail()).collect(Collectors.toList());
        List<String> cc = email.getRecipients().stream()
                .filter(r -> r.getType() == RecipientType.CC)
                .map(r -> r.getRecipient().getEmail()).collect(Collectors.toList());
        List<String> bcc = email.getRecipients().stream()
                .filter(r -> r.getType() == RecipientType.BCC)
                .map(r -> r.getRecipient().getEmail()).collect(Collectors.toList());

        resp.setTo(to);
        resp.setCc(cc);
        resp.setBcc(bcc);
        return resp;
    }

    @Transactional
    @Override
    public ComposeResponse compose(Long userId, ComposeRequest req) {
        User sender = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        Email email = new Email();
        email.setSender(sender);
        email.setSubject(Optional.ofNullable(req.getSubject()).orElse(""));
        email.setBody(req.getBody());
        email.setStatus("SEND".equalsIgnoreCase(req.getAction()) ? EmailStatus.SENT : EmailStatus.DRAFT);
        email = emailRepository.save(email);

        // recipients
        List<String> allRecipients = new ArrayList<>();
        if(req.getTo() != null) allRecipients.addAll(req.getTo());
        if(req.getCc() != null) allRecipients.addAll(req.getCc());
        if(req.getBcc() != null) allRecipients.addAll(req.getBcc());

        if(!allRecipients.isEmpty()) {
            List<User> users = userRepository.findByEmailIn(allRecipients);
            Map<String, User> emailToUser = users.stream().collect(Collectors.toMap(User::getEmail, u -> u));
            // create recipients
            if(req.getTo() != null) {
                for(String emailAddr : req.getTo()) {
                    User rec = emailToUser.get(emailAddr);
                    if(rec == null) throw new ResourceNotFoundException("Recipient not found: " + emailAddr);
                    EmailRecipient er = new EmailRecipient();
                    er.setEmail(email);
                    er.setRecipient(rec);
                    er.setType(RecipientType.TO);
                    recipientRepository.save(er);
                }
            }
            if(req.getCc() != null) {
                for(String emailAddr : req.getCc()) {
                    User rec = emailToUser.get(emailAddr);
                    if(rec == null) throw new ResourceNotFoundException("Recipient not found: " + emailAddr);
                    EmailRecipient er = new EmailRecipient();
                    er.setEmail(email);
                    er.setRecipient(rec);
                    er.setType(RecipientType.CC);
                    recipientRepository.save(er);
                }
            }
            if(req.getBcc() != null) {
                for(String emailAddr : req.getBcc()) {
                    User rec = emailToUser.get(emailAddr);
                    if(rec == null) throw new ResourceNotFoundException("Recipient not found: " + emailAddr);
                    EmailRecipient er = new EmailRecipient();
                    er.setEmail(email);
                    er.setRecipient(rec);
                    er.setType(RecipientType.BCC);
                    recipientRepository.save(er);
                }
            }
        }

        // labels
        if(email.getStatus() == EmailStatus.SENT) {
            EmailLabel sentLabel = labelRepository.findByCode("SENT").orElseThrow(() -> new ResourceNotFoundException("Label SENT missing"));
            EmailLabel inboxLabel = labelRepository.findByCode("INBOX").orElseThrow(() -> new ResourceNotFoundException("Label INBOX missing"));

            // sender -> SENT
            EmailLabelMap mapSender = new EmailLabelMap();
            mapSender.setEmail(email);
            mapSender.setUser(sender);
            mapSender.setLabel(sentLabel);
            labelMapRepository.save(mapSender);

            // recipients -> INBOX
            for(EmailRecipient er : email.getRecipients()) {
                EmailLabelMap mapR = new EmailLabelMap();
                mapR.setEmail(email);
                mapR.setUser(er.getRecipient());
                mapR.setLabel(inboxLabel);
                labelMapRepository.save(mapR);
            }
        } else {
            EmailLabel draftLabel = labelRepository.findByCode("DRAFT").orElseThrow(() -> new ResourceNotFoundException("Label DRAFT missing"));
            EmailLabelMap mapDraft = new EmailLabelMap();
            mapDraft.setEmail(email);
            mapDraft.setUser(sender);
            mapDraft.setLabel(draftLabel);
            labelMapRepository.save(mapDraft);
        }

        return new ComposeResponse(email.getId(), email.getStatus().name());
    }

    @Override
    public void markAsRead(Long userId, Long emailId) {
        Email e = emailRepository.findById(emailId).orElseThrow(() -> new ResourceNotFoundException("Email not found"));
        if(!e.isRead()) {
            e.setRead(true);
            emailRepository.save(e);
        }
    }

    @Override
    public void starEmail(Long userId, Long emailId) {
        if(!starRepository.existsByEmail_IdAndUser_Id(emailId, userId)) {
            Email e = emailRepository.findById(emailId).orElseThrow(() -> new ResourceNotFoundException("Email not found"));
            User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
            EmailStar s = new EmailStar();
            s.setEmail(e);
            s.setUser(u);
            starRepository.save(s);
        }
    }

    @Override
    public void unstarEmail(Long userId, Long emailId) {
        starRepository.deleteByEmail_IdAndUser_Id(emailId, userId);
    }

    @Override
    public MenuCounts getMenuCounts(Long userId) {
        long inbox = labelMapRepository.countByUserIdAndLabelCode(userId, "INBOX");
        long sent = labelMapRepository.countByUserIdAndLabelCode(userId, "SENT");
        long draft = labelMapRepository.countByUserIdAndLabelCode(userId, "DRAFT");
        long starred = starRepository.countByUserId(userId);
        return new MenuCounts(inbox, starred, sent, draft);
    }

    private EmailListItem mapToListItem(Email e, Long userId) {
        EmailListItem it = new EmailListItem();
        it.setId(e.getId());
        it.setSubject(e.getSubject());
        String snippet = e.getBody() == null ? "" : e.getBody().substring(0, Math.min(SNIPPET_LEN, e.getBody().length()));
        it.setSnippet(snippet);
        it.setSenderName(e.getSender().getFullName());
        it.setCreatedAt(e.getCreatedAt());
        it.setRead(e.isRead());
        it.setStarred(starRepository.existsByEmail_IdAndUser_Id(e.getId(), userId));
        return it;
    }
}
