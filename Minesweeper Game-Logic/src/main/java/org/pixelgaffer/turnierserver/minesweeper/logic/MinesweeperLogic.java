package org.pixelgaffer.turnierserver.minesweeper.logic;

import org.pixelgaffer.turnierserver.gamelogic.AllBuilderAllSolverLogic;
import org.pixelgaffer.turnierserver.gamelogic.interfaces.Ai;
import org.pixelgaffer.turnierserver.gamelogic.messages.BuilderSolverResponse;
import org.pixelgaffer.turnierserver.minesweeper.Cell;
import org.pixelgaffer.turnierserver.minesweeper.Grid;
import org.pixelgaffer.turnierserver.minesweeper.MinesweeperBuilderResponse;
import org.pixelgaffer.turnierserver.minesweeper.MinesweeperSolverResponse;

import com.google.gson.reflect.TypeToken;

public class MinesweeperLogic extends AllBuilderAllSolverLogic<MinesweeperObject, Grid, MinesweeperBuilderResponse, MinesweeperSolverResponse> {
	
	public MinesweeperLogic() {
		super(new TypeToken<BuilderSolverResponse<MinesweeperBuilderResponse, MinesweeperSolverResponse>>() {});
	}
	
	@Override
	public void failed(boolean building, Ai ai) {
		if(building) {
			getUserObject(ai).loose();
			return;
		}
		getUserObject(ai).score -= getUserObject(ai).building.getMoves();
 	}

	@Override
	public void succeeded(boolean building, Ai ai) {
		if(!building) {
			getUserObject(ai).score += Cell.FIELD_SIZE * Cell.FIELD_SIZE;
			getUserObject(ai).score -= getUserObject(ai).building.getMoves();
		}
	}

	@Override
	protected void gameFinished() {
		
	}

	@Override
	protected void setup() {
		
	}

	@Override
	protected MinesweeperObject createUserObject(Ai ai) {
		return new MinesweeperObject();
	}

	@Override
	public Grid createGameState(Ai ai) {
		Grid grid = new Grid();
		grid.setAi(ai);
		return null;
	}

}
