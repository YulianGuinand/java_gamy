package gamy.daos;

import gamy.models.Game;

public class GameDAO extends AbstractDAO<Game> {

    public GameDAO() {
        super(Game.class);
    }
}