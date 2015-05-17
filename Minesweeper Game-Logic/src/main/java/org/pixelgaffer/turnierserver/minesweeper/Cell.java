package org.pixelgaffer.turnierserver.minesweeper;

import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

import org.pixelgaffer.turnierserver.Parsers;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class Cell {
	
	public static class CellAdapter extends TypeAdapter<Cell> {
		@Override
		public void write(JsonWriter out, Cell value) throws IOException {
			out.beginArray();
			if(!value.uncovered) {
				out.value(0);
				out.value(value.flagged ? 1 : 0);
			}
			else if(value.type == Type.BOMB) {
				out.value(1);
			}
			else {
				out.value(2);
				out.value(value.bombsArround);
			}
			out.endArray();
		}

		@Override
		public Cell read(JsonReader in) throws IOException {
			in.beginArray();
			int type = in.nextInt();
			Cell cell = new Cell(type == 0 ? Type.COVERED : type == 1 ? Type.BOMB : Type.EMPTY);
			if(cell.type == Type.COVERED) {
				cell.uncovered = false;
				cell.bombsArround = -1;
				cell.flagged = in.nextInt() == 1;
				return cell;
			}
			if(cell.type == Type.BOMB) {
				cell.uncovered = true;
				cell.bombsArround = -1;
				cell.flagged = false;
				return cell;
			}
			if(cell.type == Type.EMPTY) {
				cell.uncovered = true;
				cell.bombsArround = in.nextInt();
				cell.flagged = false;
				return cell;
			}
			return cell;
		}
		
	}
	
	static {
		Parsers.addTypeAdapter(new TypeToken<Cell>() {}, new CellAdapter());
	}
	
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
	 * Diese Methode wird nichts ändern. Verwende MinesweeperAi.uncover(x, y)
	 */
	public void uncover() {
		uncovered = true;
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
