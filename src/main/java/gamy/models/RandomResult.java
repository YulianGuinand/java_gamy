package gamy.models;

public class RandomResult {
    private Game game;
    private boolean requiresPurchase;
    private Double priceToPay;

    public RandomResult(Game _game, boolean _requiresPurchase, Double _priceToPay) {
        this.game = _game;
        this.requiresPurchase = _requiresPurchase;
        this.priceToPay = _priceToPay;
    }

    public Game getGame() { return game; }
    public boolean isRequiresPurchase() { return requiresPurchase; }
    public Double getPriceToPay() { return priceToPay; }
}