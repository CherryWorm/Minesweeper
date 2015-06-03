package org.pixelgaffer.turnierserver.minesweeper;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.pixelgaffer.turnierserver.gamelogic.interfaces.BuilderSolverGameState;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverChange;
import org.pixelgaffer.turnierserver.minesweeper.Cell.Type;
import org.pixelgaffer.turnierserver.minesweeper.logic.MinesweeperRenderData;

public class Grid extends BuilderSolverGameState<Map<String, Cell>, MinesweeperBuilderResponse, MinesweeperSolverResponse> {
	
	private Cell[][] field;
	private boolean won;
	@Getter
	private int moves;
	
	@Getter @Setter
	private boolean building;
	
	public Grid(Cell[][] field) {
		this.field = field;
	}

	public Grid() {
		
	}

	@Override
	public void applyChanges(BuilderSolverChange<Map<String, Cell>> changes) {
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j < Cell.FIELD_SIZE; j++) {
				if(changes.change.containsKey(i + ":" + j)) {
					field[i][j] = changes.change.get(i + ":" + j);
				}
			}
		}
	}

	@Override
	public Response<Map<String, Cell>> build(MinesweeperBuilderResponse response) {
		field = response.field;
		Response<Map<String, Cell>> result = new Response<>();
		result.finished = true;
		result.valid = !hasEmpty() && getBombs() == Cell.BOMB_COUNT;
		if(result.valid) {
			countSurroundingBombs();
		}
		MinesweeperRenderData data = new MinesweeperRenderData();
		data.aiID = getAi().getId();
		data.calculationTime = getAi().getObject().millisLeft;
		data.field = response.field;
		data.output = response.output;
		result.renderData = data;
		return result;
	}

	@Override
	public Response<Map<String, Cell>> solve(MinesweeperSolverResponse response) {
		
		Response<Map<String, Cell>> result = new Response<>();
		result.changes = new HashMap<>();
		result.valid = true;
		
		MinesweeperRenderData data = new MinesweeperRenderData();
		data.aiID = getAi().getId();
		data.calculationTime = getAi().getObject().millisLeft;
		data.field = field;
		data.output = response.output;
		result.renderData = data;
		
		Cell cell = get(response.xFlag, response.yFlag);
		if(cell != null) {
			cell.setFlagged(!cell.isFlagged());
			result.changes.put(response.xFlag + ":"  + response.yFlag, cell);
		}
		
		cell = get(response.xStep, response.yStep);
		if(cell != null) {
			Map<String, Cell> uncover = uncover(response.xStep, response.yStep);
			
			moves++;
			
			if(uncover == null) {
				result.finished = true;
				result.valid = false;
				result.changes = null;
				return result;
			}
						
			result.changes.putAll(uncover);
			if(won()) {
				result.finished = true;
				result.changes = null;
			}
			
		}
		return result;
	}

	@Override
	public Map<String, Cell> getState() {
		Map<String, Cell> response = new HashMap<>();
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j < Cell.FIELD_SIZE; j++) {
				response.put(i + ":" + j, get(i, j));
			}
		}
		return response;
	}
	
	private Map<String, Cell> uncover(int x, int y) {
		return uncover(x, y, new HashMap<>());
	}
	
	private Map<String, Cell> uncover(int x, int y, Map<String, Cell> map) {
		Cell cell = field[x][y];
		if(cell.getType() == Type.BOMB) {
			return null;
		}
		if(cell.isUncovered()) {
			return map;
		}
		cell.uncover();
		map.put(x + ":" + y, cell);
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
	
	private void countSurroundingBombs() {
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
	
	private boolean hasEmpty() {
		for(int i = 0; i < Cell.FIELD_SIZE; i++) {
			for(int j = 0; j< Cell.FIELD_SIZE; j++) {
				if(field[i][j] == null || field[i][j].getType() == Type.COVERED) {
					return true;
				}
			}
		}
		return false;
	}
	
	private int getBombs() {
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
	
	private Cell get(int x, int y) {
		if(!Cell.isInField(x, y)) {
			return null;
		}
		return field[x][y];
	}
	
	private boolean won() {
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
