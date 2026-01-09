<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gcd.exam.player.model.PlayerIndexView" %>
<%@ page import="com.gcd.exam.player.model.Indexer" %>
<%
    List<String> errors = (List<String>) request.getAttribute("errors");
    List<PlayerIndexView> playerIndexes = (List<PlayerIndexView>) request.getAttribute("playerIndexes");
    List<Indexer> indexers = (List<Indexer>) request.getAttribute("indexers");

    String formMode = (String) request.getAttribute("formMode");
    if (formMode == null) {
        formMode = "create";
    }
    String playerName = request.getAttribute("playerName") != null ? request.getAttribute("playerName").toString() : "";
    String playerAge = request.getAttribute("playerAge") != null ? request.getAttribute("playerAge").toString() : "";
    Object selectedIndexIdObj = request.getAttribute("selectedIndexId");
    Integer selectedIndexId = null;
    if (selectedIndexIdObj instanceof Integer) {
        selectedIndexId = (Integer) selectedIndexIdObj;
    } else if (selectedIndexIdObj != null) {
        try {
            selectedIndexId = Integer.parseInt(selectedIndexIdObj.toString());
        } catch (NumberFormatException ignored) {
        }
    }
    String value = request.getAttribute("value") != null ? request.getAttribute("value").toString() : "";
    String editId = request.getAttribute("editId") != null ? request.getAttribute("editId").toString() : null;
%>
<!DOCTYPE html>
<html>
<head>
    <title>Player Information</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #fdf5f1;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 900px;
            margin: 40px auto;
            background-color: #ffffff;
            padding: 20px 30px 40px 30px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        h1 {
            text-align: center;
            color: #d28a2f;
            margin-bottom: 30px;
        }
        .form-row {
            display: flex;
            gap: 20px;
            margin-bottom: 15px;
        }
        .form-group {
            flex: 1;
            display: flex;
            flex-direction: column;
        }
        label {
            font-size: 13px;
            margin-bottom: 4px;
            color: #555;
        }
        input[type="text"], input[type="number"], select {
            padding: 8px 10px;
            border-radius: 4px;
            border: 1px solid #ccc;
            font-size: 14px;
        }
        .submit-row {
            display: flex;
            justify-content: flex-end;
            margin-top: 10px;
        }
        button {
            background-color: #d8683c;
            color: white;
            border: none;
            padding: 8px 18px;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #c05630;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 30px;
            font-size: 14px;
        }
        th, td {
            padding: 8px 10px;
            border: 1px solid #e5a48a;
            text-align: left;
        }
        thead {
            background-color: #e88254;
            color: white;
        }
        tbody tr:nth-child(even) {
            background-color: #fff7f3;
        }
        .actions a {
            margin-right: 8px;
            text-decoration: none;
            color: #d8683c;
        }
        .errors {
            color: red;
            margin-bottom: 15px;
        }
        .errors ul { margin: 0; padding-left: 18px; }
    </style>
</head>
<body>
<div class="container">
    <h1>Player Information</h1>

    <% if (errors != null && !errors.isEmpty()) { %>
        <div class="errors">
            <ul>
                <% for (String e : errors) { %>
                    <li><%= e %></li>
                <% } %>
            </ul>
        </div>
    <% } %>

    <form method="post" action="<%= request.getContextPath() %>/players">
        <input type="hidden" name="action" value="<%= "edit".equals(formMode) ? "update" : "create" %>"/>
        <% if ("edit".equals(formMode) && editId != null) { %>
            <input type="hidden" name="id" value="<%= editId %>"/>
        <% } %>
        <div class="form-row">
            <div class="form-group">
                <label for="playerName">Player name</label>
                <input type="text" id="playerName" name="playerName" value="<%= playerName %>" />
            </div>
            <div class="form-group">
                <label for="playerAge">Player age</label>
                <input type="number" id="playerAge" name="playerAge" value="<%= playerAge %>" />
            </div>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label for="indexId">Index name</label>
                <select id="indexId" name="indexId">
                    <option value="">Select index</option>
                    <% if (indexers != null) {
                        for (Indexer idx : indexers) {
                            boolean selected = selectedIndexId != null && selectedIndexId == idx.getIndexId();
                    %>
                        <option value="<%= idx.getIndexId() %>" <%= selected ? "selected" : "" %>><%= idx.getName() %></option>
                    <%
                        }
                    } %>
                </select>
            </div>
            <div class="form-group">
                <label for="value">Value</label>
                <input type="number" step="0.01" id="value" name="value" value="<%= value %>" />
            </div>
        </div>
        <div class="submit-row">
            <button type="submit"><%= "edit".equals(formMode) ? "Update" : "Add" %></button>
        </div>
    </form>

    <table>
        <thead>
        <tr>
            <th>Id</th>
            <th>Player name</th>
            <th>Player age</th>
            <th>Index name</th>
            <th>Value</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <% if (playerIndexes != null) {
            for (PlayerIndexView v : playerIndexes) { %>
                <tr>
                    <td><%= v.getId() %></td>
                    <td><%= v.getPlayerName() %></td>
                    <td><%= v.getPlayerAge() %></td>
                    <td><%= v.getIndexName() %></td>
                    <td><%= v.getValue() %></td>
                    <td class="actions">
                        <a href="<%= request.getContextPath() %>/players?action=edit&id=<%= v.getId() %>">✏</a>
                        <a href="<%= request.getContextPath() %>/players?action=delete&id=<%= v.getId() %>" onclick="return confirm('Delete this record?');">🗑</a>
                    </td>
                </tr>
        <%  }
        } %>
        </tbody>
    </table>
</div>
</body>
</html>
