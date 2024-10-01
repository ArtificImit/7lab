package server.database;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.LoggerFactory;
import server.Server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserDatabaseManager implements UserStorageManager {
    private final Connection connection;
    @Setter
    @Getter
    protected String currentUser;
    private String tableName;
    private static UserDatabaseManager db;
    private static final ch.qos.logback.classic.Logger logger =
            (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Server.class);

    public UserDatabaseManager(String url, String user, String password, String tableName) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        this.tableName = tableName;
    }

    @Override
    @SneakyThrows
    public boolean register(String userName, String password) {
        if (this.checkUserExisted(userName))
            return false;
        PreparedStatement statement = null;
        try {
            String hashedPassword = hashPasswordMD2(password);  // Хешируем пароль
            synchronized (this.connection) {
                statement = connection.prepareStatement("INSERT INTO Users(user_name, password) VALUES (?, ?);");
                statement.setString(1, userName);
                statement.setString(2, hashedPassword);  // Сохраняем хеш пароля
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе с базой данных", e);
            throw e;
        }
    }
    @SneakyThrows
    public void clear() {
        synchronized (this.connection) {
            // SQL запрос для удаления всех записей из таблицы Users
            String clearUsersSql = "DELETE FROM " + tableName + ";";
            try (PreparedStatement statement = connection.prepareStatement(clearUsersSql)) {
                statement.executeUpdate();
            }
        }
    }


    @Override
    @SneakyThrows
    public boolean checkPassword(String userName, String password) {
        PreparedStatement statement = null;
        ResultSet resultSet;
        try {
            synchronized (this.connection) {
                statement = connection.prepareStatement("SELECT * FROM Users WHERE user_name = ?");
                statement.setString(1, userName);
                resultSet = statement.executeQuery();
            }
            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");
                return hashPasswordMD2(password).equals(storedHashedPassword);  // Сравниваем хеши
            }
            return false;
        } catch (SQLException e) {
            logger.error("Ошибка при работе с базой данных", e);
            throw e;
        }
    }

    public static UserDatabaseManager getAccess(String url, String user, String password, String tableName) throws SQLException {
        if (db == null)
            db = new UserDatabaseManager(url, user, password, tableName);
        return db;
    }

    @Override
    @SneakyThrows
    public boolean checkUserExisted(String userName) {
        PreparedStatement statement = null;
        try {
            synchronized (this.connection) {
                statement = connection.prepareStatement("SELECT * FROM Users WHERE user_name = ?");
                statement.setString(1, userName);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.next();
            }
        } catch (SQLException e) {
            logger.error("Ошибка при работе с базой данных", e);
            throw e;
        }
    }

    // Метод для хеширования пароля с использованием MD2
    private String hashPasswordMD2(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD2");
        byte[] digest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));  // Преобразуем каждый байт в шестнадцатеричное представление
        }
        return sb.toString();
    }
}
