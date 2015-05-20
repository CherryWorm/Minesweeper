package org.pixelgaffer.turnierserver.minesweeper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pixelgaffer.turnierserver.minesweeper.Cell.Type;
import org.pixelgaffer.turnierserver.minesweeper.ai.MinesweeperAi;

public class TestAi extends MinesweeperAi {
	
	private Random random = new Random();
	
	public static void main(String[] args) {
		new TestAi(args).start();
	}
	
	public TestAi(String[] args) {
		super(args);
	}

	@Override
	protected Cell[][] generateField() {
		
		Cell[][] field = new Cell[Cell.FIELD_SIZE][Cell.FIELD_SIZE];
		
		for(int i = 0; i < field.length; i++) {
			for(int j = 0; j < field.length; j++) {
				field[i][j] = new Cell(Type.EMPTY);
			}
		}
		
		List<int[]> bombs = new ArrayList<int[]>();
		while(bombs.size() != Cell.BOMB_COUNT) {
			int x = random.nextInt(Cell.FIELD_SIZE);
			int y = random.nextInt(Cell.FIELD_SIZE);
			
			for(int[] bomb : bombs) {
				if(bomb[0] == x && bomb[1] == y) {
					continue;
				}
			}
			
			field[x][y] = new Cell(Type.BOMB);
			bombs.add(new int[] {x, y});
		}
		
		return field;
	}
	
	@Override
	protected void step(Cell[][] state) {
		while(true) {
			int x = random.nextInt(Cell.FIELD_SIZE);
			int y = random.nextInt(Cell.FIELD_SIZE);
			
			if(state[x][y].getType() != Type.COVERED) {
				continue;
			}
			
			uncover(x, y);
			break;
		}
	}
	
}
