package fi.drajala.hitbox;

/**
 * Provides a method for painting a line. Used to paint a {@link Hitbox}.
 * */
public interface LinePainter {
	/**
	 * A method which paints a given line.
	 * @param x1 start x-coordinate
	 * @param y1 start y-coordinate
	 * @param x2 stop x-coordinate
	 * @param y2 stop y-coordinate
	 * */
	public void paintLine(float x1, float y1, float x2, float y2);
}
