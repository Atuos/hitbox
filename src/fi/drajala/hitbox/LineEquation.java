package fi.drajala.hitbox;

import java.io.Serializable;

/**
 * An equation describing a {@link LineSegment}.
 * Used for finding out if two line segments intersect<p>
 * This works by extending the line segment into a 2D line which can be
 * described by the equation of the form: <p>
 * <i>y = k * x + a</i> <p>
 * where <i>k</i> is the derivative of the line and <i>a</i> is a constant
 * describing the distance in from the point (0, 0) in the direction of the y-axis.
 * */
public class LineEquation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// Position of the line segment
	private Point position;
	
	// Start point of the line segment
	private Point start;
	
	// Stop point of the line segment
	private Point stop;
	
	private Point result = new Point(0.0f, 0.0f);
	
	// Derivative of the line segment
	private float k;
	
	// Constant offset from (0, 0) in the direction of the y-axis
	private float a;
	
	/**
	 * @param {@link LineSegment} to form the equation for.
	 * */
	public LineEquation(LineSegment segment) {
		this.position = segment.getPosition();
		this.start = segment.getStart();
		this.stop = segment.getStop();
	}

	/**
	 * Forms the equation for the line in the form: <p>
	 * <i>y = k * x + a</i> <p>
	 * */
	public void formEquation() {
		if(stop.x==start.x) {
			k = 0.0f;
		} else {
			k = (stop.y - start.y) / (stop.x - start.x);
		}
		a = start.y + position.y - k * (start.x + position.x);
	}
	
	/**
	 * @return k, the derivative of the line
	 * */
	public float getK() {
		return k;
	}
	
	/**
	 * @return a, the constant offset in the y-axis direction from the point (0, 0)
	 * */
	public float getA() {
		return a;
	}
	
	/**
	 * Uses the equation with the help of another line equation, 
	 * solving for y the equation of the following form: <p>
	 * <i>y = k * x + a</i>
	 * @param other another line equation used for the simultaneous equation
	 * @return a point which satisfies both equations
	 * */
	public Point solveIntersectionPoint(LineEquation other) {
		// Update the equations to their current values
		this.formEquation();
		other.formEquation();
		
		// Solved by transferring all constants a to the "other" side of the equation
		// and all factors k to "this" side of the equation
		// E.g. 2x + 3 = 3x - 1 becomes x = 4
		float resultK = this.k - other.getK();
		float resultA = other.getA() - this.a;
		if(resultK!=0.0f) {
			resultA /= resultK;
		} else {
			resultA = 0.0f;//start.x;
		}

		// Now the equation is in the form 1*x = A, where A is the remaining constant value
		float x = resultA;
		
		// Form the result point (x, y), where y = k * x + a
		result.x = x;
		result.y = calculate(x);

		return result;
	}
	
	/**
	 * Solves the equation of the form <p>
	 * <i>y = k * x + a</i>
	 * @param x
	 * @return the result of the equation
	 * */
	public float calculate(float x) {
		return k * x + a;
	}
	
	/**
	 * Sets the position of the line equation
	 * @param position the new position
	 * */
	public void setPosition(Point position) {
		this.position = position;
	}
	
	/**
	 * @return a String in the form y = kx + a, with the equation's values for k and a
	 * */
	@Override
	public String toString() {
		return "y = " + k + "x + " + a;
	}
}
