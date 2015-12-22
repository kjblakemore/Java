package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Karen Blakemore
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	
	// Draws a square centered on (x, y), with scaled Radius
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		float scaledRadius = 1.6f * this.radius;
		pg.rect(x - scaledRadius, y - scaledRadius, scaledRadius, scaledRadius);		
	}
	
}
