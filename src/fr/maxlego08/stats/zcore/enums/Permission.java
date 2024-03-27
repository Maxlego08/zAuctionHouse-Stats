package fr.maxlego08.stats.zcore.enums;

public enum Permission {

	ZAUCTIONHOUSE_STATS_USE,
	ZAUCTIONHOUSE_STATS_RELOAD,

	;

	private String permission;

	private Permission() {
		this.permission = this.name().toLowerCase().replace("_", ".");
	}

	public String getPermission() {
		return permission;
	}

}
