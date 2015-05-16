package org.pixelgaffer.turnierserver.minesweeper;

import lombok.Getter;
import lombok.Setter;

public class Cell {
	
	/**
	 * Die Größe des Feldes
	 */
	public static final int FIELD_SIZE = 8;
	/**
	 * Die Anzahl an Bomben pro Feld
	 */
	public static final int BOMB_COUNT = 12;
	
	public enum Type {
		BOMB, EMPTY, COVERED;
	}
	
	/**
	 * True, wenn sich eine Flagge auf der Zelle befindet
	 */
	@Getter @Setter
	private boolean flagged;
	/**
	 * True, wenn die Zelle uncovered wurde (wird von der GameLogic verwendet)
	 */
	@Getter
	private boolean uncovered;
	/**
	 * Die Anzahl an Bomben, welche sich um dieses Feld herum befinden. -1, wenn das Feld nicht aufgedeckt wurde.
	 */
	@Getter @Setter
	private int bombsArround = -1;
	/**
	 * Der Typ dieser Zelle
	 */
	@Getter
	private Type type;
	
	/**
	 * Erstellt eine neue Zelle. TYP DARF BEIM GENERIEREN NICHT COVERED SEIN!
	 * 
	 * @param type
	 */
	public Cell(Type type) {
		this.type = type;
	}
	
	/**
	 * Erstellt eine neue Cell aus einem String (NICHT VERWENDEN)
	 * 
	 * @param string
	 */
	public Cell(String string) {
		if(string.startsWith("0")) {
			type = Type.COVERED;
			flagged = Boolean.parseBoolean(string.split(" ")[1]);
		}
		if(string.equals("1")) {
			type = Type.BOMB;
			uncovered = true;
		}
		if(string.startsWith("2")) {
			type = Type.EMPTY;
			uncovered = true;
			bombsArround = Integer.parseInt(string.split(" ")[1]);
		}
	}
	
	/**
	 * Diese Methode wird nichts ändern. Verwende MinesweeperAi.uncover(x, y)
	 */
	public void uncover() {
		uncovered = true;
	}
	
	@Override
	public String toString() {
		if(!uncovered) {
			return "0 " + Boolean.toString(flagged);
		}
		if(type == Type.BOMB) {
			return "1";
		}
		return "2 " + bombsArround;
	}
	
	/**
	 * True, wenn die Koordinate sich im Feld befindet
	 * 
	 * @param x Die x-Koordinate
	 * @param y Die y-Koordinate
	 * @return Ob sich die Koordinate im Feld befindet
	 */
	public static boolean isInField(int x, int y) {
		return x >= 0 && x < FIELD_SIZE && y >= 0 && y < FIELD_SIZE;
	}
	
}
