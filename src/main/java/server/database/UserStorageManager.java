package server.database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserStorageManager {

    boolean register(String userName, String password);
    boolean checkPassword(String userName, String password);
    boolean checkUserExisted(String userName);
}
