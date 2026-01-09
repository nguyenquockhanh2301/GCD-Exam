package com.gcd.exam.player.web;

import com.gcd.exam.player.dao.IndexerDao;
import com.gcd.exam.player.dao.PlayerDao;
import com.gcd.exam.player.dao.PlayerIndexDao;
import com.gcd.exam.player.model.Indexer;
import com.gcd.exam.player.model.Player;
import com.gcd.exam.player.model.PlayerIndex;
import com.gcd.exam.player.model.PlayerIndexView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PlayerServlet", urlPatterns = "/players")
public class PlayerServlet extends HttpServlet {

    private final IndexerDao indexerDao = new IndexerDao();
    private final PlayerDao playerDao = new PlayerDao();
    private final PlayerIndexDao playerIndexDao = new PlayerIndexDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "edit":
                    showEditForm(req, resp);
                    break;
                case "delete":
                    delete(req, resp);
                    break;
                case "list":
                default:
                    list(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "create";
        }

        try {
            switch (action) {
                case "update":
                    update(req, resp);
                    break;
                case "create":
                default:
                    create(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        List<Indexer> indexers = indexerDao.findAll();

        // Simple pagination: 10 records per page
        final int pageSize = 10;
        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {
            }
        }
        if (page < 1) {
            page = 1;
        }

        int totalRecords = playerIndexDao.countAll();
        int totalPages = (int) Math.ceil(totalRecords / (double) pageSize);
        if (totalPages < 1) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }
        int offset = (page - 1) * pageSize;

        List<PlayerIndexView> playerIndexes = playerIndexDao.findPageWithJoin(offset, pageSize);

        req.setAttribute("indexers", indexers);
        req.setAttribute("playerIndexes", playerIndexes);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        if (req.getAttribute("formMode") == null) {
            req.setAttribute("formMode", "create");
        }

        req.getRequestDispatcher("/player-info.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/players");
            return;
        }
        int id = Integer.parseInt(idParam);

        PlayerIndex pi = playerIndexDao.findById(id);
        if (pi == null) {
            resp.sendRedirect(req.getContextPath() + "/players");
            return;
        }
        Player player = playerDao.findById(pi.getPlayerId());

        req.setAttribute("formMode", "edit");
        req.setAttribute("editId", pi.getId());
        if (player != null) {
            req.setAttribute("playerName", player.getName());
            req.setAttribute("playerAge", player.getAge());
        }
        req.setAttribute("selectedIndexId", pi.getIndexId());
        req.setAttribute("value", pi.getValue());

        list(req, resp);
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null) {
            int id = Integer.parseInt(idParam);
            playerIndexDao.delete(id);
        }
        resp.sendRedirect(req.getContextPath() + "/players");
    }

    private void create(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        handleSave(req, resp, false);
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        handleSave(req, resp, true);
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp, boolean isUpdate) throws SQLException, ServletException, IOException {
        List<String> errors = new ArrayList<>();

        String playerName = trim(req.getParameter("playerName"));
        String playerAgeStr = trim(req.getParameter("playerAge"));
        String indexIdStr = trim(req.getParameter("indexId"));
        String valueStr = trim(req.getParameter("value"));

        Integer age = null;
        Integer indexId = null;
        Float value = null;

        if (playerName == null || playerName.isEmpty()) {
            errors.add("Player name is required.");
        }

        if (playerAgeStr == null || playerAgeStr.isEmpty()) {
            errors.add("Player age is required.");
        } else {
            try {
                age = Integer.parseInt(playerAgeStr);
                if (age < 10 || age > 60) {
                    errors.add("Player age must be between 10 and 60.");
                }
            } catch (NumberFormatException e) {
                errors.add("Player age must be a number.");
            }
        }

        if (indexIdStr == null || indexIdStr.isEmpty()) {
            errors.add("Index is required.");
        } else {
            try {
                indexId = Integer.parseInt(indexIdStr);
            } catch (NumberFormatException e) {
                errors.add("Invalid index selected.");
            }
        }

        if (valueStr == null || valueStr.isEmpty()) {
            errors.add("Value is required.");
        } else {
            try {
                value = Float.parseFloat(valueStr);
            } catch (NumberFormatException e) {
                errors.add("Value must be a number.");
            }
        }

        // Check against index value range
        if (indexId != null && value != null) {
            Indexer idx = indexerDao.findById(indexId);
            if (idx == null) {
                errors.add("Selected index does not exist.");
            } else {
                if (value < idx.getValueMin() || value > idx.getValueMax()) {
                    errors.add("Value for " + idx.getName() + " must be between " + idx.getValueMin() + " and " + idx.getValueMax() + ".");
                }
            }
        }

        // Preserve form values
        req.setAttribute("playerName", playerName);
        req.setAttribute("playerAge", playerAgeStr);
        req.setAttribute("selectedIndexId", indexId);
        req.setAttribute("value", valueStr);

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("formMode", isUpdate ? "edit" : "create");
            if (isUpdate) {
                req.setAttribute("editId", req.getParameter("id"));
            }
            list(req, resp);
            return;
        }

        if (age == null || indexId == null || value == null) {
            // Should not happen if validation above passed, but guard anyway
            errors.add("Invalid data.");
            req.setAttribute("errors", errors);
            list(req, resp);
            return;
        }

        if (isUpdate) {
            int id = Integer.parseInt(req.getParameter("id"));
            PlayerIndex pi = playerIndexDao.findById(id);
            if (pi == null) {
                resp.sendRedirect(req.getContextPath() + "/players");
                return;
            }
            Player player = playerDao.findById(pi.getPlayerId());
            if (player != null) {
                player.setName(playerName);
                player.setFullName(playerName);
                player.setAge(age);
                player.setIndexId(indexId);
                playerDao.update(player);
            }
            pi.setIndexId(indexId);
            pi.setValue(value);
            playerIndexDao.update(pi);
        } else {
            // create
            Player player = playerDao.findByNameAndAge(playerName, age);
            if (player == null) {
                player = new Player();
                player.setName(playerName);
                player.setFullName(playerName);
                player.setAge(age);
                player.setIndexId(indexId);
                playerDao.create(player);
            }

            PlayerIndex pi = new PlayerIndex();
            pi.setPlayerId(player.getPlayerId());
            pi.setIndexId(indexId);
            pi.setValue(value);
            playerIndexDao.create(pi);
        }

        resp.sendRedirect(req.getContextPath() + "/players");
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}
