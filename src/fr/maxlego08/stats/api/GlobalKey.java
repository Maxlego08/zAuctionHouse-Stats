package fr.maxlego08.stats.api;

public enum GlobalKey {
    ITEM_SOLD_AMOUNT(Integer.class), // Le nombre d'items mis en vente
    ITEM_PURCHASE_AMOUNT(Integer.class), // Le nombre d'items acheté
    NUMBER_OF_ITEMS_NOT_SOLD(Integer.class), // Le nombre d'items pas vendu qui vont expirer
    NUMBER_OF_ITEMS_EXPIRED(Integer.class), // Le nombre d'items qui ont expiré
    NUMBER_OF_ITEMS_REMOVE_FROM_EXPIRE(Integer.class), // Le nombre d'items retiré du storage type EXPIRE
    NUMBER_OF_ITEMS_REMOVE_FROM_STORAGE(Integer.class), // Le nombre d'items retiré du storage type ITEMS
    NUMBER_OF_ITEMS_REMOVE_FROM_BOUGHT(Integer.class), // Le nombre d'items retiré du storage type BUY
    NUMBER_OF_ITEMS_DESTROY(Integer.class), // Le nombre d'items qui ont été détruit
    AUCTION_OPENED(Integer.class), //Le nombre de fois où l'hôtel des ventes a été ouvert

    ;

    private final Class<?> valueType;

    GlobalKey(Class<?> valueType) {
        this.valueType = valueType;
    }

    public Class<?> getValueType() {
        return this.valueType;
    }
}