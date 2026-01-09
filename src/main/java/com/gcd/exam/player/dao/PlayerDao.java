package com.gcd.exam.player.dao;

import com.gcd.exam.player.db.DBConnectionUtil;
import com.gcd.exam.player.model.Player;

import java.sql.*;

public class PlayerDao {

    public Player findById(int id) throws SQLException {
        String sql = "SELECT player_id, name, full_name, age, index_id FROM player WHERE player_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Player findByNameAndAge(String name, int age) throws SQLException {
        String sql = "SELECT player_id, name, full_name, age, index_id FROM player WHERE name = ? AND age = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Player create(Player player) throws SQLException {
        String sql = "INSERT INTO player(name, full_name, age, index_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, player.getName());
            ps.setString(2, player.getFullName());
            ps.setInt(3, player.getAge());
            ps.setInt(4, player.getIndexId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    player.setPlayerId(rs.getInt(1));
                }
            }
        }
        return player;
    }

    public void update(Player player) throws SQLException {
        String sql = "UPDATE player SET name = ?, full_name = ?, age = ?, index_id = ? WHERE player_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, player.getName());
            ps.setString(2, player.getFullName());
            ps.setInt(3, player.getAge());
            ps.setInt(4, player.getIndexId());
            ps.setInt(5, player.getPlayerId());
            ps.executeUpdate();
        }
    }

    private Player mapRow(ResultSet rs) throws SQLException {
        Player p = new Player();
        p.setPlayerId(rs.getInt("player_id"));
        p.setName(rs.getString("name"));
        p.setFullName(rs.getString("full_name"));
        p.setAge(rs.getInt("age"));
        p.setIndexId(rs.getInt("index_id"));
        return p;
    }
}