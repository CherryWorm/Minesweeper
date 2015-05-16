package org.pixelgaffer.turnierserver.minesweeper;

import java.util.HashMap;
import java.util.Map;

import org.pixelgaffer.turnierserver.minesweeper.Cell.Type;

public class Grid {
	
	private Cell[][] field;
	private boolean won;
	
	public Grid(Map<String, String> map) {
		fromMap(map);
	}
	
	public Grid(Cell[][] field) {
		this.field = field;
	}
	
	public void fromMap(Map<String, String> map) {
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j < Cell.FIELD_SIZE; j++) {
				if(map.containsKey(i + ":" + j)) {
					field[i][j] = new Cell(map.get(i + ":" + j));
				}
			}
		}
	}
	
	public Map<String, String> toMap() {
		Map<String, String> map = new HashMap<String, String>();
		
		for(int i = 0; i < field.length; i++) {
			for(int j = 0; j < field.length; j++) {
				map.put(i + ":" + j, field[i][j].toString());
			}
		}
		
		return map;
	}
	
	public Map<String, String> uncover(int x, int y) {
		return uncover(x, y, new HashMap<>());
	}
	
	private Map<String, String> uncover(int x, int y, Map<String, String> map) {
		Cell cell = field[x][y];
		if(cell.getType() == Type.BOMB) {
			return null;
		}
		if(cell.isUncovered()) {
			return map;
		}
		cell.uncover();
		map.put(x + ":" + y, cell.toString());
		if(cell.getBombsArround() != 0) {
			uncover(x + 1, y, map);
			uncover(x + 1, y + 1, map);
			uncover(x + 1, y - 1, map);
			uncover(x, y + 1, map);
			uncover(x, y - 1, map);
			uncover(x - 1, y, map);
			uncover(x - 1, y + 1, map);
			uncover(x - 1, y - 1, map);
		}
		return map;
	}
	
	public void countSurroundingBombs() {
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j < Cell.FIELD_SIZE; j++) {
				countSurroundingBombs(i, j);
			}
		}
	}
	
	private void countSurroundingBombs(int x, int y) {
		Cell cell = field[x][y];
		int bombsArround = 0;
		bombsArround += isBomb(x + 1, y, field);
		bombsArround += isBomb(x + 1, y + 1, field);
		bombsArround += isBomb(x + 1, y - 1, field);
		bombsArround += isBomb(x, y + 1, field);
		bombsArround += isBomb(x, y - 1, field);
		bombsArround += isBomb(x - 1, y, field);
		bombsArround += isBomb(x - 1, y + 1, field);
		bombsArround += isBomb(x - 1, y - 1, field);
		cell.setBombsArround(bombsArround);
	}
	
	private int isBomb(int x, int y, Cell[][] field) {
		if(x < 0 || x >= Cell.FIELD_SIZE || y < 0 || y >= Cell.FIELD_SIZE) {
			return 0;
		}
		return field[x][y].getType() == Type.BOMB ? 1 : 0;
	}
	
	public Cell[][] getField() {
		return field;
	}
	
	public boolean hasEmpty() {
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j< Cell.FIELD_SIZE; j++) {
				if(field[i][j] == null || field[i][j].getType() == Type.COVERED) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getBombs() {
		int bombs = 0;
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j < Cell.FIELD_SIZE; j++) {
				if(field[i][j].getType() == Type.BOMB) {
					bombs++;
				}
			}
		}
		return bombs;
	}
	
	public Cell get(int x, int y) {
		return field[x][y];
	}
	
	public boolean won() {
		if(won) {
			return true;
		}
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j < Cell.FIELD_SIZE; j++) {
				Cell cell = get(i, j);
				if(!cell.isUncovered() && cell.getType() == Type.EMPTY) {
					return false;
				}
			}
		}
		won = true;
		return true;
	}
	
}
