package fr.maxlego08.stats.api;

import fr.maxlego08.stats.storage.SqlConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Table {

    private final SqlConnection connection;

    public Table(SqlConnection connection) {
        this.connection = connection;
    }

    protected abstract String getCreate();

    public void create() throws SQLException {
        try (PreparedStatement statement = connection.getConnection().prepareStatement(this.getCreate())) {
            statement.execute();
        }
    }
}
