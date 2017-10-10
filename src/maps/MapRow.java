package maps;

import java.util.ArrayList;
import java.util.Arrays;

public class MapRow {
    public String[] cells;

    public MapRow(String line) {
        cells = line.split("");
    }

    public int findPlayerX(){
        for (int i = 0; i < cells.length; i++){
            String cell = cells[i];
            if (cell.equalsIgnoreCase("@")){
                cells[i] = " ";
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "MapRow{" +
                "cells=" + Arrays.toString(cells) +
                '}';
    }
}
