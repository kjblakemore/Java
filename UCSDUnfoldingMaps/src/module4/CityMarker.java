package module4;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Karen Blakemore
 *
 */
public class CityMarker extends SimplePointMarker {
	
	// The radius of the triangle.
	public static final int TRI_SIZE = 5;  
	
	public CityMarker(Location location) {
		super(location);
	}
	
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
	}
	
	
	/**
	 * Implementation of method to draw marker on the map.
	 * Draws a triangle centered at (x,y) with radius of TRI_SIZE.
	 */
	public void draw(PGraphics pg, float x, float y) {
		// Calculate the x and y deltas for lower two points of triangle.
		float xdelta = (float) Math.cos(Math.toRadians(30.0)) * TRI_SIZE;
		float ydelta = (float) Math.sin(Math.toRadians(30.0)) * TRI_SIZE;
		
		// Save previous drawing style
		pg.pushStyle();
		
		// Draw the triangle and color light grey, with darker stroke.
		pg.fill(0xFFA0A0A0);
		pg.stroke(0xFF808080);
		pg.triangle(x,  y-TRI_SIZE,  x-xdelta,  y+ydelta,  x+xdelta,  y+ydelta);
		
		// Restore previous drawing style
		pg.popStyle();
	}
	
	/* Local getters for some city properties.  You might not need these 
	 * in module 4. 	 */
	public String getCity()
	{
		return getStringProperty("name");
	}
	
	public String getCountry()
	{
		return getStringProperty("country");
	}
	
	public float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}
	
}
