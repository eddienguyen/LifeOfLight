package maps;

import bases.events.EventManager;

public class Player {
    int x;
    int y;
    int Str;
    int Agi;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;

    }

    public boolean match(int x, int y){
        return this.x == x && this.y == y;
    }

    public boolean hitWall(Wall wall){
        if ( (this.x == wall.x) && (this.y == wall.y) ) return true;
        else return false;
    }

    public boolean hitEvent(Event event){
        return  ( (this.x == event.x) && (this.y == event.y) ) ;
    }

    public void setStr(int str) {
        Str += str;
    }


}
