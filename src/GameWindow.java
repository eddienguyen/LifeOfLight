
import bases.GameObject;
import bases.events.EventManager;
import bases.inputs.CommandListener;
import bases.inputs.InputManager;
import bases.uis.InputText;
import bases.uis.StatScreen;
import bases.uis.TextScreen;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import novels.Story;
import novels.Choice;
import settings.Settings;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static java.lang.System.nanoTime;

/**
 * Created by huynq on 7/28/17.
 * modified by eddienguyen on 10/2/17.
 */
public class GameWindow extends JFrame {
    private long lastTimeUpdate = -1;

    private GameCanvas canvas;

    //map info
    int playerX = 1;
    int playerY = 1;
    int trapX = 2;
    int trapY = 5;
    int width = 10;                                     //mapWidth
    int height = 10;                                    //mapHeght
    String[][] location = new String[width][height];

    boolean gameOn = false;
    //end map info

    int currentArc = 0;
    Story currentStory;

    void loadArc(int arcNo) {
        try {
            //step1: read whole text file:
            String url = "assets/events/event_arc_" + arcNo + ".json";
            byte[] bytes = Files.readAllBytes(Paths.get(url));                  //convert data to byte
            String content = new String(bytes, StandardCharsets.UTF_8);

            //step2: parse json using 'gson':
            Gson gson = new Gson();
            TypeToken<List<Story>> token = new TypeToken<List<Story>>() {
            };
            List<Story> stories = gson.fromJson(content, token.getType());

            storyHashMap.clear();
            for (Story story : stories) {
                if (storyHashMap.get(story.id) != null) System.out.println("Duplicate " + story.id);
                else storyHashMap.put(story.id, story);
            }

            currentStory = stories.get(0);
            EventManager.pushUIMessageNewLine(currentStory.text);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    HashMap<String, Story> storyHashMap = new HashMap<>();

    void changeStory(String newStoryID) {
        currentStory = storyHashMap.get(newStoryID);
        EventManager.pushUIMessageNewLine(currentStory.text);
        if (currentStory.isType("Timeout")) {
            EventManager.pushUIMessageNewLine("#0000ffPress Next");
        }
        if (currentStory.isType("nextArc")) {
            EventManager.pushUIMessageNewLine("#0000ffPress Next");
        }
        if (currentStory.isType("Map")) {
            EventManager.pushUIMessageNewLine("#0000ffPress Start");
        }
    }

    public void loadMap() {
        for (int y = 0; y <= height - 1; y++) {
            for (int x = 0; x <= width - 1; x++) {
                if (0 == x || 0 == y || height - 1 == y || width - 1 == x) {
                    location[x][y] = " #";
                } else if (x == playerX && y == playerY) {
                    location[x][y] = " @";
                } else if (playerX == trapX && playerY == trapY) {
                    EventManager.pushClearUI();
                    changeStory("E000008");
                    EventManager.pushUIMessageNewLine("You DIE!");
                    System.exit(1);
                } else if (playerY == 8 && playerY == 9) {
                    changeStory("E000007");
                } else {
                    location[x][y] = " .";
                }
            }//width
        }//height
        location[1][0] = " s";                          //start
        location[8][9] = " e";                          //end
        String line;
        //print Map:
        for (int y = 0; y <= height - 1; y++) {
            line = "";
            for (int x = 0; x <= width - 1; x++) {
                line += location[x][y];
            }
            EventManager.pushUIMessageNewLine(line);
        }//end print map
    }//loadMap


    public GameWindow() {
        setupFont();
        setupPanels();
        setupWindow();

        loadArc(currentArc);

        InputManager.instance.addCommandListener(new CommandListener() {
            @Override
            public void onCommandFinished(String command) {
                EventManager.pushUIMessageNewLine("");
                EventManager.pushUIMessageNewLine("#ffff00" + command);
                EventManager.pushUIMessageNewLine("");

                if (currentStory.isType("Input")) {
                    for (Choice choice : currentStory.choices) {
                        if (choice.match(command)) {
                            changeStory(choice.to);
                            break;
                        } else if (choice.value.equalsIgnoreCase("other")) {
                            changeStory(choice.to);
                            break;
                        } else if (choice.value.equalsIgnoreCase("notAns"))
                            EventManager.pushUIMessageNewLine("wrong");                                      //DOES NOT RUN.WHY??
                    }
                } else if (currentStory.isType("Timeout")) {
                    if (command.equalsIgnoreCase("next")) changeStory(currentStory.time.to);
                    else EventManager.pushUIMessageNewLine("#ff0099wrong command! press 'Next'");
                } else if (currentStory.isType("nextArc")) {
                    currentArc++;
                    loadArc(currentArc);
                } else if (currentStory.isType("Map")) {
                    loadMap();
                    //---------------movement for player in map---------------

                    if (command.equals("start")) {
                        EventManager.pushClearUI();
                        gameOn = true;
                        loadMap();
                        EventManager.pushUIMessage("next move ?");
                    } else if (command.equals("up")) {
                        if (gameOn == true) {
                            EventManager.pushClearUI();
                            EventManager.pushUIMessageNewLine("you moved up");
                            playerY--;
                            if (playerY == 0) {
                                EventManager.pushUIMessageNewLine("You hit the wall");
                                playerY++;
                            }
                            loadMap();
                        } else EventManager.pushUIMessageNewLine("unable to move right now");
                    } else if (command.equals("down")) {
                        if (gameOn == true) {
                            EventManager.pushClearUI();
                            EventManager.pushUIMessageNewLine("you moved down");
                            playerY++;
                            if (playerY == height - 1) {
                                EventManager.pushUIMessageNewLine("You hit the wall");
                                playerY--;
                            }
                            loadMap();
                        } else EventManager.pushUIMessage("unable to move right now");
                    } else if (command.equals("left")) {
                        if (gameOn == true) {
                            EventManager.pushClearUI();
                            EventManager.pushUIMessageNewLine("you moved left");
                            playerX--;
                            if (playerX == 0){
                                EventManager.pushUIMessageNewLine("You hit the wall");
                                playerX++;
                            }
                            loadMap();
                        } else EventManager.pushUIMessage("unable to move right now");
                    } else if (command.equals("right")) {
                        if (gameOn == true) {
                            EventManager.pushClearUI();
                            EventManager.pushUIMessageNewLine("you moved right");
                            playerX ++;
                            if (playerX == width - 1){
                                EventManager.pushUIMessage("You hit the wall");
                                playerX--;
                            }
                            loadMap();
                        } else EventManager.pushUIMessage("unable to move right now");
                    } else if (command.equals("help")) {
                        EventManager.pushUIMessageNewLine("type ';#0000FFstart;' to start the game");
                        EventManager.pushUIMessageNewLine("type ';#0000FFup, down, left, right;' to move your character");
                    } else {
                        EventManager.pushHelpMessage();
                    }

                    //---------------end movement for player in map---------------
                }

            }//onCommandFinished

            @Override
            public void commandChanged(String command) {

            }
        });

    }

    private void setupFont() {

    }

    private void setupPanels() {
        canvas = new GameCanvas();
        setContentPane(canvas);

        TextScreen textScreenPanel = new TextScreen();
        textScreenPanel.setColor(Color.BLACK);
        textScreenPanel.getSize().set(
                Settings.TEXT_SCREEN_SCREEN_WIDTH,
                Settings.TEXT_SCREEN_SCREEN_HEIGHT);
        pack();
        textScreenPanel.getOffsetText().set(getInsets().left + 20, getInsets().top + 20);
        GameObject.add(textScreenPanel);


        InputText commandPanel = new InputText();
        commandPanel.getPosition().set(
                0,
                Settings.SCREEN_HEIGHT
        );
        commandPanel.getOffsetText().set(20, 20);
        commandPanel.getSize().set(
                Settings.CMD_SCREEN_WIDTH,
                Settings.CMD_SCREEN_HEIGHT
        );
        commandPanel.getAnchor().set(0, 1);
        commandPanel.setColor(Color.BLACK);
        GameObject.add(commandPanel);


        StatScreen statsPanel = new StatScreen();
        statsPanel.getPosition().set(
                Settings.SCREEN_WIDTH,
                0
        );

        statsPanel.getAnchor().set(1, 0);
        statsPanel.setColor(Color.BLACK);
        statsPanel.getSize().set(
                Settings.STATS_SCREEN_WIDTH,
                Settings.STATS_SCREEN_HEIGHT
        );
        GameObject.add(statsPanel);

        InputManager.instance.addCommandListener(textScreenPanel);
    }


    private void setupWindow() {
        this.setSize(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        this.setVisible(true);
        this.setTitle(Settings.GAME_TITLE);
        this.addKeyListener(InputManager.instance);
        this.setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void gameLoop() {
        while (true) {
            if (-1 == lastTimeUpdate) lastTimeUpdate = nanoTime();

            long currentTime = nanoTime();

            if (currentTime - lastTimeUpdate > 17000000) {
                lastTimeUpdate = currentTime;
                GameObject.runAll();
                InputManager.instance.run();
                canvas.backRender();
            }
        }
    }
}
