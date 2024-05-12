package fr.maxlego08.stats.api;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.RequestHelper;

import java.util.logging.Logger;

public abstract class TableUtils {

    protected final Logger logger;
    protected final DatabaseConnection connection;
    protected final RequestHelper requestHelper;

    public TableUtils(DatabaseConnection connection, Logger logger) {
        this.logger = logger;
        this.connection = connection;
        this.requestHelper = new RequestHelper(connection, logger);
    }
}
