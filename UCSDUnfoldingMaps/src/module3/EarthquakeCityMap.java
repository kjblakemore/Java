package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Karen Blakemore
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	public void setup() {
		size(950, 600, OPENGL);

		map = new UnfoldingMap(this, 50, 50, 850, 500, new Google.GoogleMapProvider());
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    List<Marker> markers = new ArrayList<Marker>();

	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // Print first earthquake record.
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    }
	    
	    for (PointFeature pt: earthquakes) 
	    	markers.add(createMarker(pt));
	    
	    map.addMarkers(markers);	    
	}
		
	// A helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// Color and radius of marker indicates magnitude, from yellow (0.0) to red (7.0).
	private SimplePointMarker createMarker(PointFeature feature)
	{
		Object magObj = feature.getProperty("magnitude");
		String magStr = magObj.toString();
    	float mag = Float.parseFloat(magStr);
    	
    	int colorLevel = (int) map(mag, 0, 7, 0, 255);
    	int shade = color(255, 255-colorLevel, 0);
    	int radius = (int) map(mag, 0, 7, 0, 20);
    	
    	SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
    	
    	marker.setColor(shade);
    	marker.setStrokeColor(shade);
    	marker.setRadius(radius);
    	
    	return marker;
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	private void addKey() 
	{
		// Create key box
		fill(255, 255, 255);
		rect(600, 475, 300, 75);
		
		// Create key title
		fill(50);
		textSize(14);
		textAlign(CENTER);
		text("Earthquake Magnitude on Richter Scale", 750, 495);
		
		// Create graduated color scale from yellow to red
		int c1 = color(255, 255, 0);
		int c2 = color(255, 0, 0);	
		for (int i = 610; i <= 890; i++) {
			float inter = map(i, 610, 890, 0, 1);
		    int c = lerpColor(c1, c2, inter);
		    stroke(c);
		    line(i, 500, i, 525);
		}
		
		// Add magnitude levels to color scale.
		textSize(12);
		textAlign(LEFT);
		text("0.0", 610, 540);
		textAlign(RIGHT);
		text("7.0", 890, 540);
	}
}
