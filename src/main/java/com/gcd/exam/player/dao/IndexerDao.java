package com.gcd.exam.player.dao;

import com.gcd.exam.player.db.DBConnectionUtil;
import com.gcd.exam.player.model.Indexer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IndexerDao {

    public List<Indexer> findAll() throws SQLException {
        String sql = "SELECT index_id, name, valueMin, valueMax FROM indexer ORDER BY index_id";
        List<Indexer> list = new ArrayList<>();
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Indexer idx = new Indexer();
                idx.setIndexId(rs.getInt("index_id"));
                idx.setName(rs.getString("name"));
                idx.setValueMin(rs.getFloat("valueMin"));
                idx.setValueMax(rs.getFloat("valueMax"));
                list.add(idx);
            }
        }
        return list;
    }

    public Indexer findById(int id) throws SQLException {
        String sql = "SELECT index_id, name, valueMin, valueMax FROM indexer WHERE index_id = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Indexer idx = new Indexer();
                    idx.setIndexId(rs.getInt("index_id"));
                    idx.setName(rs.getString("name"));
                    idx.setValueMin(rs.getFloat("valueMin"));
                    idx.setValueMax(rs.getFloat("valueMax"));
                    return idx;
                }
            }
        }
        return null;
    }
}