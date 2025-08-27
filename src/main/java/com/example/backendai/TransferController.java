package com.example.backendai;

import com.example.backendai.model.Transfer;
import com.example.backendai.model.User;
import com.example.backendai.repository.TransferRepository;
import com.example.backendai.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TransferController {

    private final UserRepository userRepository;
    private final TransferRepository transferRepository;

    public TransferController(UserRepository userRepository, TransferRepository transferRepository) {
        this.userRepository = userRepository;
        this.transferRepository = transferRepository;
    }

    @PostMapping("/transfer")
    @Transactional
    public ResponseEntity<?> transferPoints(Authentication authentication, @RequestBody TransferRequest req) {
        if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "unauthenticated"));
        }
        User from = (User) authentication.getPrincipal();
        if (req.getAmount() == null || req.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "invalid amount"));
        }

        // find recipient
        User to = null;
        if (req.getToUserId() != null) {
            to = userRepository.findById(req.getToUserId()).orElse(null);
        } else if (req.getToMemberCode() != null && !req.getToMemberCode().isBlank()) {
            to = userRepository.findByMemberCode(req.getToMemberCode()).orElse(null);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "recipient required"));
        }

        if (to == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "recipient not found"));
        }

        if (to.getId().equals(from.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "cannot transfer to self"));
        }

        int amount = req.getAmount();
        try {
            // deduct from sender
            userRepository.adjustPoints(from.getId(), -amount);
            // add to recipient
            userRepository.adjustPoints(to.getId(), amount);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }

        Transfer t = new Transfer();
        t.setFromUserId(from.getId());
        t.setToUserId(to.getId());
        t.setAmount(amount);
        t.setNote(req.getNote());
        Transfer saved = transferRepository.save(t);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/transfers")
    public ResponseEntity<?> recentTransfers(Authentication authentication, @RequestParam(value = "limit", defaultValue = "10") int limit) {
        if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "unauthenticated"));
        }
        User u = (User) authentication.getPrincipal();
        List<Transfer> list = transferRepository.findRecentByUserId(u.getId(), limit);
        return ResponseEntity.ok(list);
    }
}
