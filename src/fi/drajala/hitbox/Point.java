package fi.drajala.hitbox;

import java.io.Serializable;

/**
 * Represents a 2D point
 * */
public class Point implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public float x = 0.0f;
	public float y = 0.0f;
	
	/**
	 * Constructs a point with x=0.0f, y=0.0f
	 * */
	public Point() {
		this(0.0f, 0.0f);
	}
	
	/**
	 * Constructs a point represented by two floats
	 * @param x
	 * @param y
	 * */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return String of the form (x, y)
	 * */
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}
}
