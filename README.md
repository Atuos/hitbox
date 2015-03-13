# Hitbox
A simple 2D polygon hitbox implementation written in Java e.g. for simple games.
Allows the creation of arbitrary polygonal hitboxes

# Use:

```Java
Hitbox.Builder builder = new Hitbox.Builder();
builder.addPoint(10.0f, 0.0f)
		.addPoint(110.0f, 10.0f)
		.addPoint(100.0f, 100.0f)
		.addPoint(130.0f, 130.0f)
		.addPoint(0.0f, 90.0f);

hitbox = builder.build();
// Alternatively call the constructor Hitbox(List<Point> points)

// Moves hitbox while maintaining the relative position of the points
hitbox.setPosition(player.getPosition());

if(hitbox.intersects(enemy.getHitbox())) {
  // Collision occurred!
}
```

For e.g. debugging you can also draw the hitbox by implementing the LinePainter interface and passing it to the draw() function:
```Java
final PApplet processing = ...

LinePainter painter = new LinePainter() {
	@Override
	public void paintLine(float x1, float y1, float x2, float y2) {
		processing.line(x1, y1, x2, y2);
	}
};
...

hitbox.draw(painter);
```

# How it works:
The hitbox is formed of points which are connected to each other. The first point connects to the second point, the second to the third, and so on, until the the last point is connected back to the first point. Between each point there is a line segment. If the hitboxes are close enough to each other (as determined by a regular, rectangular hitbox which contains the polygon hitbox), the line segments of each hitbox are compared to each other. If any two line segments intersect, a collision has occurred.

The intersection of line segments is determined by extending the line segments into a line and producing a linear equation for it of the form:

y = k * x + a

The equations of two lines are used to calculate the point at which they intersect, and if this point is on both the line segments, they intersect.