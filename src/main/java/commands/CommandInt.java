package commands;

import QA.Response;

import java.sql.SQLException;

public interface CommandInt {

    Response execute() throws SQLException;
    String getHelp();

}
