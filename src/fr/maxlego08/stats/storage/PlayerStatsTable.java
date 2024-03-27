package fr.maxlego08.stats.storage;

public class PlayerStatsTable {

    private final SqlConnection connection;

    public PlayerStatsTable(SqlConnection connection) {
        this.connection = connection;
    }
}
