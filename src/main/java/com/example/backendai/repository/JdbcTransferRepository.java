package com.example.backendai.repository;

import com.example.backendai.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class JdbcTransferRepository implements TransferRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransferRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer save(Transfer t) {
        String sql = "INSERT INTO transfers(from_user_id, to_user_id, amount, note, created_at) VALUES(?,?,?,?,datetime('now'))";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, t.getFromUserId());
            ps.setLong(2, t.getToUserId());
            ps.setInt(3, t.getAmount());
            ps.setString(4, t.getNote());
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            t.setId(key.longValue());
        }
        // reload created_at
        jdbcTemplate.query("SELECT created_at FROM transfers WHERE id = ?", rs -> {
            if (rs.next()) {
                t.setCreatedAt(rs.getString("created_at"));
            }
        }, t.getId());
        return t;
    }

    @Override
    public List<Transfer> findRecentByUserId(Long userId, int limit) {
        String sql = "SELECT id, from_user_id, to_user_id, amount, note, created_at FROM transfers WHERE from_user_id = ? ORDER BY created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), userId, limit);
    }

    private Transfer mapRow(ResultSet rs) throws SQLException {
        Transfer t = new Transfer();
        t.setId(rs.getLong("id"));
        t.setFromUserId(rs.getLong("from_user_id"));
        t.setToUserId(rs.getLong("to_user_id"));
        t.setAmount(rs.getInt("amount"));
        t.setNote(rs.getString("note"));
        t.setCreatedAt(rs.getString("created_at"));
        return t;
    }
}
