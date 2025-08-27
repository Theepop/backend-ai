package com.example.backendai.repository;

import com.example.backendai.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFirstname(rs.getString("firstname"));
        u.setLastname(rs.getString("lastname"));
        u.setPhone(rs.getString("phone"));
        u.setBirthday(rs.getString("birthday"));
        u.setMemberCode(rs.getString("member_code"));
        u.setMembershipLevel(rs.getString("membership_level"));
        u.setRegisterDate(rs.getString("register_date"));
        int pts = rs.getInt("points");
        u.setPoints(pts);
        return u;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), email));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), id));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByMemberCode(String memberCode) {
        String sql = "SELECT * FROM users WHERE member_code = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapRow(rs), memberCode));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users(email, password, firstname, lastname, phone, birthday, member_code, membership_level, register_date, points) VALUES(?,?,?,?,?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator psc = (Connection con) -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getLastname());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getBirthday());
            ps.setString(7, user.getMemberCode());
            ps.setString(8, user.getMembershipLevel());
            ps.setString(9, user.getRegisterDate());
            if (user.getPoints() != null) {
                ps.setInt(10, user.getPoints());
            } else {
                ps.setInt(10, 0);
            }
            return ps;
        };
        jdbcTemplate.update(psc, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            user.setId(key.longValue());
        }
        return user;
    }

    @Override
    public void adjustPoints(Long userId, int delta) {
        // Use exception handling because queryForObject will throw when no row found
        Integer current;
        try {
            current = jdbcTemplate.queryForObject("SELECT points FROM users WHERE id = ?", Integer.class, userId);
        } catch (EmptyResultDataAccessException ex) {
            throw new IllegalArgumentException("user not found");
        }
        if (current == null) {
            current = 0;
        }
        int newVal = current + delta;
        if (newVal < 0) {
            throw new IllegalArgumentException("insufficient points");
        }
        jdbcTemplate.update("UPDATE users SET points = ? WHERE id = ?", newVal, userId);
    }
}
