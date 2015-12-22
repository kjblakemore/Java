package module5;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Karen Blakemore
 *
 */
// TODO: Change SimplePointMarker to CommonMarker as the very first thing you do 
// in module 5 (i.e. CityMarker extends CommonMarker).  It will cause an error.
// That's what's expected.
public class CityMarker extends CommonMarker {
	
	public static int TRI_SIZE = 5;  // The size of the triangle marker
	
	public CityMarker(Location location) {
		super(location);
	}
	
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}
	
	/**
	 * Draw an equilateral triangle to mark the city location.
	 */
	public void drawMarker(PGraphics pg, float x, float y) {
		// Calculate the x and y deltas for lower two points of triangle.
		float xdelta = (float) Math.cos(Math.toRadians(30.0)) * TRI_SIZE;
		float ydelta = (float) Math.sin(Math.toRadians(30.0)) * TRI_SIZE;
		
		// Save previous drawing style
		pg.pushStyle();
		
		// Draw the triangle and color light grey, with darker stroke.
		pg.fill(0xa0, 0xa0, 0xa0);
		pg.stroke(0x80, 0x80, 0x80);
		pg.triangle(x,  y-TRI_SIZE,  x-xdelta,  y+ydelta,  x+xdelta,  y+ydelta);
		
		// Restore previous drawing style
		pg.popStyle();
	}
	
	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		if(isSelected()) {
			pg.text(getCity() + " " + getCountry() + " " + getPopulation(), x, y);
		}
	}
	
	/* Local getters for some city properties.  
	 */
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
