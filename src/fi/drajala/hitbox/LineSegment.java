package fi.drajala.hitbox;

import java.io.Serializable;

/**
 * Represents a line segment consisting of a start point, an end point and a position.
 * The position is used to offset both the start and stop points, allowing the line 
 * segment to be moved around.
 * */
public class LineSegment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Point position;
	
	private final Point start;
	private final Point stop;
	
	private LineEquation equation;
	
	/**
	 * @param start the start point
	 * @param stop the end point
	 * @param position the position offset of the line segment
	 * */
	public LineSegment(Point position, Point start, Point stop) {
		this.position = position;
		this.start = start;
		this.stop = stop;
		this.equation = new LineEquation(this);
	}
	
	/**
	 * Returns the {@link LineEquation} describing the line segment. Used to calculate intersection points 
	 * with other line segments.
	 * */
	public LineEquation getEquation() {
		return equation;
	}

	/**
	 * @return start point of line segment
	 * */
	public Point getStart() {
		return start;
	}

	/**
	 * @return end point of line segment
	 * */
	public Point getStop() {
		return stop;
	}
	
	/**
	 * Lines are counted as vertical if the difference between their start and stop X-coordinates is less than 3.0f
	 * This is done to prevent problems caused by too slight angles from the Y-axis.
	 * @return <b>true</b> if the line segment is approximated as vertical <b>false</b> otherwise
	 * */
	public boolean isVertical() {
		return Math.abs(start.x - stop.x) <= 3.0f;
	}
	
	/**
	 * Checks if two line segments intersect.
	 * @return <b>true</b> if line segments intersect <b>false</b> otherwise.
	 * */
	public boolean intersects(LineSegment other) {
		// If both lines are vertical, they have to have the same x-coordinate and must overlap on the y-axis
		if(this.isVertical() && other.isVertical()) {
			return Math.min(this.start.x, this.stop.x) == Math.min(other.start.x, other.stop.x) && 
					(this.isPointOnLine(other.start.x, other.start.y) ||
					this.isPointOnLine(other.stop.x, other.stop.y));
		}

		// If one line is vertical, solve the non-vertical line equation with 
		// the vertical line's x-coordinate to get intersection point
		// and check if said point is one both lines
		if(this.isVertical() || other.isVertical()) {
			LineSegment vertical = this.isVertical() ? this : other;
			LineSegment nonVertical = this.isVertical() ? other : this;
			
			nonVertical.getEquation().formEquation();
			
			float x = vertical.getStart().x + vertical.getPosition().x;
			float y = nonVertical.getEquation().calculate(x);
			return vertical.isPointOnLine(x, y) && nonVertical.isPointOnLine(x, y);
		}
		
		// Solve intersection point and find out if the point is on both lines.
		Point intersectionPoint = equation.solveIntersectionPoint(other.getEquation());
		float x = intersectionPoint.x;
		float y = intersectionPoint.y;
		return this.isPointOnLine(x, y) && other.isPointOnLine(x, y);
	}
	
	/**
	 * Checks if a point is found on this line segment.
	 * @param x
	 * @param y
	 * @return <b>true</b> if the point is on the line segment <b>false</b> otherwise
	 * */
	public boolean isPointOnLine(float x, float y) {
		float xPos = position.x;
		float yPos = position.y;
		boolean betweenX = 
				(x >= start.x + xPos && x <= stop.x + xPos) ||
				(x <= start.x + xPos && x >= stop.x + xPos);
		
		boolean betweenY = 
				(y >= start.y + yPos && y <= stop.y + yPos) ||
				(y <= start.y + yPos && y >= stop.y + yPos);
		
		return betweenX && betweenY;
	}
	
	/**
	 * Draws the line segment using the {@link LinePainter} provided.
	 * @param painter used to draw the line segment
	 * */
	public void draw(LinePainter painter) {
		painter.paintLine(start.x + position.x, start.y + position.y, stop.x + position.x, stop.y + position.y);
	}

	/**
	 * Sets the position of the line segment to the given position.
	 * @param position
	 * */
	public void bindToPosition(Point position) {
		this.position = position;
		this.equation.setPosition(position);
	}

	/**
	 * @return position of the line segment
	 * */
	public Point getPosition() {
		return position;
	}
	
	/**
	 * @return String representing line segment
	 * */
	@Override
	public String toString() {
		return "Start: " + start.toString() + " Stop: " + stop.toString() + " Position: " + position.toString();
	}
}
