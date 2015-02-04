package com.marioga.grox.game.ai;

import com.marioga.grox.game.objects.Block;

public class GroxPlay {
	final private int row;
	final private int column;
	final private Block block;
	final private int blockRotation;
	
	public GroxPlay(int row, int column, Block block, int blockRotation) {
		this.row=row;
		this.column=column;
		this.block=block;
		this.blockRotation=blockRotation;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public Block getBlock() {
		return block;
	}

	public int getBlockRotation() {
		return blockRotation;
	}
}
