package maps;

import bases.events.EventManager;

import java.util.ArrayList;
import java.util.Random;

/*
    Created by eddienguyen on 10/10/17 inspired by huynq.
 */
public class Map {
    ArrayList<MapRow> rows;
    int width;
    int height;

    Player player;
    Wall wall;
    Event event;

    ArrayList walls = new ArrayList();
    ArrayList<Event> events = new ArrayList();
    boolean hasKey = false;

    int playerX = 2;
    int playerY = 2;

    public Map(String mapContent) {
        mapContent = mapContent.replace("\r", "");
        String[] lines = mapContent.split("\n");

        rows = new ArrayList<>();
        for (String line : lines) {
            MapRow row = new MapRow(line);
            rows.add(row);

        }
        height = lines.length;
        width = lines[0].length();


        // find player's location
        for (int y = 0; y < rows.size(); y++) {
            MapRow row = rows.get(y);
            playerX = row.findPlayerX();
            if (playerX != -1) {
                playerY = y;
                break;
            }
        }//end find player's location

        player = new Player(playerX, playerY);
    }

    public void pushUI() {
        for (int y = 0; y < height; y++) {
            MapRow row = rows.get(y);

            for (int x = 0; x < width; x++) {

                if (player.match(x, y)) {
                    EventManager.pushUIMessage("@ ");
                } else {
                    String cell = row.cells[x];
                    if (!cell.equalsIgnoreCase("@")) {
                        if (cell.equalsIgnoreCase("/")) {
                            wall = new Wall(x, y);
                            walls.add(wall);
                        }else if (cell.equalsIgnoreCase("E")) {
                            event = new Event(x, y);
                            events.add(event);
                        }

                        EventManager.pushUIMessage(String.format("%s ", cell));
                    }
                }
            }

            EventManager.pushUIMessageNewLine("");
        }
    }//pushUI


    public void playerUp() {

        player.y--;

        for (int i = 0; i < walls.size(); i++) {
            if (player.hitWall((Wall) walls.get(i))) {
                EventManager.pushUIMessageNewLine("You've hit the wall");
                player.y++;
            }
        }
        eventCollision();

    }

    public void playerDown() {
        player.y++;

        for (int i = 0; i < walls.size(); i++) {
            if (player.hitWall((Wall) walls.get(i))) {
                EventManager.pushUIMessageNewLine("You've hit the wall");
                player.y--;
            }
        }
        eventCollision();
    }

    public void playerRight() {
        player.x++;
        for (int i = 0; i < walls.size(); i++) {
            if (player.hitWall((Wall) walls.get(i))) {
                EventManager.pushUIMessageNewLine("You've hit the wall");
                player.x--;
            }
        }
        eventCollision();
    }

    public void playerLeft() {
        player.x--;
        for (int i = 0; i < walls.size(); i++) {
            if (player.hitWall((Wall) walls.get(i))) {
                EventManager.pushUIMessageNewLine("You've hit the wall");
                player.x++;
            }
        }
        eventCollision();


    }


    //when there's an item collision:
    public void eventCollision() {
        for (Event event : events) {
            if (player.hitEvent(event)) {
                events.remove(event);                           //doesn't work! Event is still there!
                Random rnd = new Random();
                int randomNum = rnd.nextInt(4);
                if (randomNum <= 2) {
                    EventManager.pushUIMessageNewLine(String.format("you've just gained %s strengh ", randomNum * 10));
                    player.setStr(randomNum * 10);
                    EventManager.pushUIMessageNewLine("Strengh:" + player.Str);
                    break;
                } else {
                    EventManager.pushUIMessageNewLine(String.format("You've just lost %s strengh", randomNum * 5));
                    player.setStr(randomNum * 5);
                    System.out.println(randomNum);
                    break;
                }
            }
        }
    }


    @Override
    public String toString() {
        return "Map{" +
                "rows=" + rows +
                '}';
    }
}
