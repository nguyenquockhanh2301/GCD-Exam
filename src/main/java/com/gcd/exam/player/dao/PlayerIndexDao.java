package com.gcd.exam.player.dao;

import com.gcd.exam.player.db.DBConnectionUtil;
import com.gcd.exam.player.model.PlayerIndex;
import com.gcd.exam.player.model.PlayerIndexView;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerIndexDao {

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM player_index";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<PlayerIndexView> findPageWithJoin(int offset, int limit) throws SQLException {
        String sql = "SELECT pi.id, pi.player_id, pi.index_id, pi.value, " +
                "p.name AS player_name, p.age AS player_age, i.name AS index_name " +
                "FROM player_index pi " +
                "JOIN player p ON pi.player_id = p.player_id " +
                "JOIN indexer i ON pi.index_id = i.index_id " +
                "ORDER BY pi.id LIMIT ? OFFSET ?";
        List<PlayerIndexView> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PlayerIndexView v = new PlayerIndexView();
                    v.setId(rs.getInt("id"));
                    v.setPlayerId(rs.getInt("player_id"));
                    v.setIndexId(rs.getInt("index_id"));
                    v.setPlayerName(rs.getString("player_name"));
                    v.setPlayerAge(rs.getInt("player_age"));
                    v.setIndexName(rs.getString("index_name"));
                    v.setValue(rs.getFloat("value"));
                    list.add(v);
                }
            }
        }
        return list;
    }

    public PlayerIndex findById(int id) throws SQLException {
        String sql = "SELECT id, player_id, index_id, value FROM player_index WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PlayerIndex pi = new PlayerIndex();
                    pi.setId(rs.getInt("id"));
                    pi.setPlayerId(rs.getInt("player_id"));
                    pi.setIndexId(rs.getInt("index_id"));
                    pi.setValue(rs.getFloat("value"));
                    return pi;
                }
            }
        }
        return null;
    }

    public void create(PlayerIndex pi) throws SQLException {
        String sql = "INSERT INTO player_index(player_id, index_id, value) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pi.getPlayerId());
            ps.setInt(2, pi.getIndexId());
            ps.setFloat(3, pi.getValue());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pi.setId(rs.getInt(1));
                }
            }
        }
    }

    public void update(PlayerIndex pi) throws SQLException {
        String sql = "UPDATE player_index SET player_id = ?, index_id = ?, value = ? WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pi.getPlayerId());
            ps.setInt(2, pi.getIndexId());
            ps.setFloat(3, pi.getValue());
            ps.setInt(4, pi.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM player_index WHERE id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}