package dev.mrmarshall.easypermissions.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EasyPermsDB {

    private void query(String sql) throws SQLException {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/testdb");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("");
        Connection connection = hikariDataSource.getConnection();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();

        statement.close();
        connection.close();
        hikariDataSource.close();
    }

    private String query(String sql, String filter) throws SQLException {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/testdb");
        hikariDataSource.setUsername("root");
        hikariDataSource.setPassword("");
        Connection connection = hikariDataSource.getConnection();

        String result = null;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        while(resultSet.next()) {
            result = resultSet.getString(filter);
        }

        resultSet.close();
        statement.close();
        connection.close();
        hikariDataSource.close();

        return result;
    }

    public void createTables() {
        String epdbGroups = "CREATE TABLE IF NOT EXISTS epdbGroups" +
                            "(name VARCHAR(255) NOT NULL," +
                            "permissions VARCHAR(255)," +
                            "inheritance VARCHAR(255)," +
                            "PRIMARY KEY (name))";

        String epdbUsers  = "CREATE TABLE IF NOT EXISTS epdbUsers" +
                            "(uuid VARCHAR(255) NOT NULL," +
                            "pgroup VARCHAR(255)," +
                            "permissions VARCHAR(255))";
        try {
            query(epdbGroups);
            query(epdbUsers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////> PLAYER
    public void registerPlayer(Player player) {
        String sql = "INSERT INTO epdbUsers" +
                     "(uuid," +
                     "pgroup," +
                     "permissions) VALUES ('" + player.getUniqueId().toString() + "', 'default', ' ')";
        try {
            query(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerGroup(Player player, String group) {
        String sql = "UPDATE epdbUsers " +
                     "SET pgroup = '" + group + "' " +
                     "WHERE uuid = '" + player.getUniqueId().toString() + "'";
        try {
            query(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerPermission(Player player, String permission) {
        String currentPermissions = getCurrentPlayerPermissions(player);
        String finalPermissions = currentPermissions + "," + permission;

        String sql = "UPDATE epdbUsers " +
                     "SET permissions = '" + finalPermissions + "' " +
                     "WHERE uuid = '" + player.getUniqueId().toString() + "'";
        try {
            query(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePlayerPermission(Player player, String permission) {
        String currentPermissions = getCurrentPlayerPermissions(player);
        String finalPermissions = currentPermissions.replaceAll("," + permission, "");

        String sql = "UPDATE epdbUsers " +
                "SET permissions = '" + finalPermissions + "' " +
                "WHERE uuid = '" + player.getUniqueId().toString() + "'";
        try {
            query(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentPlayerPermissions(Player player) {
        String result = null;
        String sql = "SELECT * FROM epdbUsers " +
                     "WHERE uuid = '" + player.getUniqueId().toString() + "'";
        try {
            result = query(sql, "permissions");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getCurrentPlayerGroup(Player player) {
        String result = null;
        String sql = "SELECT * from epdbUsers " +
                     "WHERE uuid = '" + player.getUniqueId().toString() + "'";
        try {
            result = query(sql, "pgroup");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    //////////////////////////> GROUP
    public void registerGroup(String group, String inheritance) {
        String sql = "INSERT INTO epdbGroups " +
                "(name, " +
                "permissions, " +
                "inheritance) VALUES " +
                "('" + group + "', " +
                "' ', " +
                "'" + inheritance + "')";
        try {
            query(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addGroupPermission(String group, String permission) {
        String currentPermissions = getCurrentGroupPermissions(group);
        String finalPermissions = currentPermissions + "," + permission.toLowerCase();

        String sql = "UPDATE epdbGroups " +
                "SET permissions = '" + finalPermissions + "' " +
                "WHERE name = '" + group + "'";
        try {
            query(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeGroupPermission(String group, String permission) {
        String currentPermissions = getCurrentGroupPermissions(group);
        String finalPermissions = currentPermissions.replaceAll("," + permission, "");

        String sql = "UPDATE epdbGroups " +
                "SET permissions = '" + finalPermissions + "' " +
                "WHERE name = '" + group + "'";
        try {
            query(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentGroupPermissions(String group) {
        String result = null;
        String sql = "SELECT * FROM epdbGroups " +
                "WHERE name = '" + group + "'";
        try {
            result = query(sql, "permissions");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getGroupInheritance(String group) {
        String result = null;
        String sql = "SELECT * FROM epdbGroups " +
                "WHERE name = '" + group + "'";
        try {
            result = query(sql, "inheritance");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}