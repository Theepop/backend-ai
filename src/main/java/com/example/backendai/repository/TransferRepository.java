package com.example.backendai.repository;

import com.example.backendai.model.Transfer;
import java.util.List;

public interface TransferRepository {
    Transfer save(Transfer t);
    List<Transfer> findRecentByUserId(Long userId, int limit);
}
