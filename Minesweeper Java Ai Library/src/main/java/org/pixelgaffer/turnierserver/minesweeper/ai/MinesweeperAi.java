package org.pixelgaffer.turnierserver.minesweeper.ai;

import java.util.Map;

import org.pixelgaffer.turnierserver.ailibrary.Ai;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverChange;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverResponse;
import org.pixelgaffer.turnierserver.minesweeper.Cell;
import org.pixelgaffer.turnierserver.minesweeper.Grid;
import org.pixelgaffer.turnierserver.minesweeper.MinesweeperBuilderResponse;
import org.pixelgaffer.turnierserver.minesweeper.MinesweeperSolverResponse;

import com.google.gson.reflect.TypeToken;

public abstract class MinesweeperAi extends Ai<Grid, BuilderSolverChange<Map<String, Cell>>> {

	public MinesweeperAi(String[] args) {
		super(new TypeToken<BuilderSolverChange<Map<String, Cell>>>() {}, args);
	}

	private int xFlag = -1, yFlag = -1;
	private int xStep = -1, yStep = -1;
	
	private Grid grid = new Grid();
	
	@Override
	protected Object update(Grid state) {
		BuilderSolverResponse<MinesweeperBuilderResponse, MinesweeperSolverResponse> response = new BuilderSolverResponse<>();
		
		if(state.isBuilding()) {
			response.build.field = generateField();
			response.build.output = output.toString();
		}
		else {
			step(state.getField());
			response.solve.output = output.toString();
			response.solve.xFlag = xFlag;
			response.solve.yFlag = yFlag;
			response.solve.xStep = xStep;
			response.solve.yStep = yStep;
		}
		output = new StringBuilder("");
		
		return response;
	}
	@Override
	protected Grid getState(BuilderSolverChange<Map<String, Cell>> change) {
		grid.applyChanges(change);
		grid.setBuilding(true);
		return grid;
	}
	
	/**
	 * Muss das Feld generieren. Das Feld muss Cell.BOMB_COUNT bomben enthalten
	 * 
	 * @return Das generierte Feld
	 */
	protected abstract Cell[][] generateField();
	/**
	 * Macht einen Schritt. Es kann jeweils maximal ein Feld geflagged und aufgedeckt werden
	 * 
	 * @param state Der Spielzustand
	 */
	protected abstract void step(Cell[][] state);
	
	/**
	 * Markiert ein Feld
	 * 
	 * @param x Die x-Koordinate des Feldes
	 * @param y Die y-Koordinate des Feldes
	 */
	public void flag(int x, int y) {
		if(!Cell.isInField(x, y)) {
			return;
		}
		xFlag = x;
		yFlag = y;
	}
	
	/**
	 * Löscht die Flagge, die erstellt werden soll
	 */
	public void unflag() {
		xFlag = -1;
		yFlag = -1;
	}
	
	/**
	 * Enthült ein Feld
	 * 
	 * @param x Die x-Koordinate des Feldes
	 * @param y Die y-Koordinate des Feldes
	 */
	public void uncover(int x, int y) {
		if(!Cell.isInField(x, y)) {
			return;
		}
		xStep = x;
		yStep = y;
	}
	
	/**
	 * Deckt das enthülte Feld wieder zu
	 */
	public void resetUncover() {
		xStep = -1;
		yStep = -1;
	}
	
}
