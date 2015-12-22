package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for land earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Karen Blakemore
 *
 */
public class LandQuakeMarker extends EarthquakeMarker {
	
	public LandQuakeMarker(PointFeature quake) {
		
		// calling EarthquakeMarker constructor
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = true;
	}

	// Draws a circle centered on (x, y), with scaled Radius
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.ellipse(x, y, 1.6f*this.radius, 1.6f*this.radius);		
	}
	
	// Get the country the earthquake is in
	public String getCountry() {
		return (String) getProperty("country");
	}
		
}