package fi.drajala.hitbox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A 2D polygon hitbox which is formed by an arbitrary amount of points.
 * This works by connecting the points to each other in the order in which 
 * they are given, with the last point connected to the first
 * */
public class Hitbox implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// A rough rectangular hitbox which envelops the polygonal hitbox.
	// The intersections of the polygonal hitpoints are only calculated if
	// the rough hitboxes intersect.
	private final RoughHitbox roughHitbox;
	
	// Position of the hitbox
	private Point position = new Point(300.0f, 300.0f);
	
	// The the points form line segments, which in turn form the hitbox
	private final List<LineSegment> lines = new ArrayList<LineSegment>();
	
	/**
	 * Construct the hitbox based on a list of {@link Point}s.
	 * */
	public Hitbox(List<Point> points) {
		formLines(points);
		roughHitbox = new RoughHitbox(lines, position);
	}
	
	/**
	 * Forms the {@link LineSegment}s based on the list of {@link Point}s
	 * */
	private void formLines(List<Point> points) {
		for(int i=0; i<points.size(); ++i) {
			if(i==points.size()-1) {
				lines.add(new LineSegment(position, points.get(i), points.get(0)));
			} else {
				lines.add(new LineSegment(position, points.get(i), points.get(i+1)));
			}
		}
	}
	
	/**
	 * @return a rectangular hitbox which envelops the polygon hitbox
	 * */
	public RoughHitbox getRoughHitbox() {
		return roughHitbox;
	}
	
	/**
	 * Checks if two hitboxes intersect
	 * @param other the other hitbox
	 * @return <b>true</b> if the hitboxes intersect, <b>false</b> otherwise
	 * */
	public boolean intersects(Hitbox other) {
		if(!this.roughHitbox.intersects(other.getRoughHitbox())) {
			return false;
		}
		for(LineSegment line : lines) {
			for(LineSegment otherLine : other.getLines()) {
				if(line.intersects(otherLine)) return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Sets the hitbox position to the given new position
	 * @param position the new position
	 * */
	public void setPosition(Point position) {
		roughHitbox.position = position;
		for(LineSegment line : lines) {
			line.setPosition(position);
		}
	}
	
	/**
	 * Draws the hitbox using the given {@LinePainter}.
	 * Used for visualizing in case of e.g. debugging
	 * */
	public void draw(LinePainter painter) {
		for(LineSegment line : lines) {
			line.draw(painter);
		}
	}
	
	/**
	 * A builder class which makes it easier to form the hitbox by 
	 * providing methods for adding {@link Point}s.
	 * */
	public static class Builder {
		private List<Point> points = new ArrayList<Point>();
		
		/**
		 * Adds a {@link Point} for the hitbox.
		 * @param point the new point to add
		 * @return <b>this</b> instance for further building
		 * */
		public Builder addPoint(Point point) {
			points.add(point);
			return this;
		}
		
		/**
		 * Adds a point for the hitbox
		 * @param x the x-coordinate of the point
		 * @param y the y-coordinate of the point
		 * @return <b>this</b> instance for further building
		 * */
		public Builder addPoint(float x, float y) {
			points.add(new Point(x, y));
			return this;
		}
		
		/**
		 * Builds the hitbox based on the given points
		 * @return the hitbox defined by the given points
		 * */
		public Hitbox build() {
			return new Hitbox(points);
		}
	}
	
	/**
	 * Sets the x-coordinate position of the hitbox
	 * @param x
	 * */
	public void setX(float x) {
		position.x = x;
	}
	
	/**
	 * Sets the y-coordinate position of the hitbox
	 * @param y
	 * */
	public void setY(float y) {
		position.y = y;
	}
	
	/**
	 * @return the list of {@link LineSegment}s that form the hitbox
	 * */
	public List<LineSegment> getLines() {
		return lines;
	}
	
	/**
	 * A rectangular hitbox which is formed around the polygon hitbox.
	 * Used to reduce the number of intersection calculations due to
	 * the simplicity of rectangular hitboxes.
	 * */
	private class RoughHitbox implements Serializable {
		private static final long serialVersionUID = 1L;

		// Width of the rectangle
		float width = 0.0f;
		
		// Height of the rectangle
		float height = 0.0f;
		
		// Position of the rectangle
		Point position;
		
		
		// Constructs the rough hitbox so that its height and width conforms to the height,
		// width and position of the polygon hitbox which it envelops
		RoughHitbox(List<LineSegment> segments, Point position) {
			float xMin = Float.MAX_VALUE;
			float xMax = Float.MIN_VALUE;
			float yMin = Float.MAX_VALUE;
			float yMax = Float.MIN_VALUE;
			
			float tempMaxY;
			float tempMaxX;
			float tempMinY;
			float tempMinX;
			
			for(LineSegment seg : segments) {
				
				float startX = seg.getStart().x;
				float stopX = seg.getStop().x;
				float startY = seg.getStart().y;
				float stopY = seg.getStop().y;
				
				// Keep track of the min and max x and y of the current line segment
				tempMaxX = Math.max(startX, stopX);
				tempMaxY = Math.max(startY, stopY);
				tempMinX = Math.min(startX, stopX);
				tempMinY = Math.min(startY, stopY);
				
				// Keep track of the maximum and minimum X and Y for 
				// finding the width and height of the rough hitbox
				if(tempMaxX >= xMax) xMax = tempMaxX;
				if(tempMinX <= xMin) xMin = tempMinX;
				if(tempMaxY >= yMax) yMax = tempMaxY;
				if(tempMinY <= yMin) yMin = tempMinY;
			}
			
			width = xMax - xMin;
			height = yMax - yMin;
			this.position = position;
		}
		
		/**
		 * Checks if this rough hitbox has collided with another
		 * @param other the other rough hitbox
		 * @return <b>true</b> if the hitboxes intersect <b>false</b> otherwise.
		 * */
		public boolean intersects(RoughHitbox other) {
			
			// Check if the the right wall is between the left and right wall of the other hitbox (and vice versa)
			boolean xOverlap = 
					(this.position.x + this.width >= other.position.x &&
					this.position.x + this.width < other.position.x + other.width) ||
					(other.position.x + other.width >= this.position.x &&
					other.position.x + other.width < this.position.x + this.width);
					
			// Check if the the bottom wall is between the top and bottom wall of the other hitbox (and vice versa)
			boolean yOverlap = 
					(this.position.y + this.height >= other.position.y &&
					this.position.y + this.height < other.position.y + other.height) ||
					(other.position.y + other.height >= this.position.y &&
					other.position.y + other.height < this.position.y + this.height);
			
			return xOverlap && yOverlap;
		}
	}
}
