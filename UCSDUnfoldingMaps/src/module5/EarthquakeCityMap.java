package module5;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import module5.EarthquakeMarker;
import module5.LandQuakeMarker;
import parsing.ParseFeed;
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Karen Blakemore
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setup and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();	
	}
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		if(lastSelected == null)	// no quakes selected, check cities.
			selectMarkerIfHover(cityMarkers);
	}
	
	// Look for marker under the cursor, mark it as selected and set the 
	// lastSelected variable.
	private void selectMarkerIfHover(List<Marker> markers)
	{
		for(Marker marker: markers) {
			if(marker.isInside(map, (float)mouseX, (float)mouseY)) {
				marker.setSelected(true);
				lastSelected = (CommonMarker) marker;
				break;		
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if(lastClicked != null) {		// deselection click, make all markers visible
			lastClicked.setClicked(false);
			lastClicked = null;
			unhideMarkers();
		} else {						// selection click	
			selectMarkerIfClicked(quakeMarkers);
			if(lastClicked != null) 	// clicked on quake Marker
				displayThreatenedCities();
			else {					// check for click on city
				selectMarkerIfClicked(cityMarkers);
				if(lastClicked != null) 	// clicked on city
					displayThreateningQuakes();
			}
		}
	}
		
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// Look for marker under clicked cursor, mark it as clicked and set the 
	// lastClicked variable.
	private void selectMarkerIfClicked(List<Marker> markers)
	{
		for(Marker marker: markers) {
			if(marker.isInside(map, (float)mouseX, (float)mouseY)) {
				lastClicked = (CommonMarker) marker;
				lastClicked.setClicked(true);
				break;		
			}
		}
	}
	
	// Display only clicked quake (lastClicked) and threatened cities.
	// A city is threatened if it is felt at more than an intensity of II, which occurs
	// at distances less than or equal to 20*(1.8**(2*Magnitude - 5)).
	private void displayThreatenedCities() {
		
		// Hide all quakes, except the one clicked
		for(Marker marker: quakeMarkers)
			if(marker != lastClicked) marker.setHidden(true);
		
		// Hide all non-threatened cities
		float magnitude = ((EarthquakeMarker)lastClicked).getMagnitude();	
		double distance = 20 * Math.pow(1.8, 2*magnitude - 5);	
		
		Location loc = lastClicked.getLocation();	
		for(Marker marker: cityMarkers)
			if(marker.getDistanceTo(loc) > distance) marker.setHidden(true);				
	}
	
	// Display only clicked city (lastClicked) and threatening quakes.
	// A quake is threatening, if it can be felt at more than an intensity of II, which 
	// occurs at distances less than or equal to 20*(1.8**(2*Magnitude - 5)).
	private void displayThreateningQuakes() {
		
		// Hide all cities, except the one clicked
		for(Marker marker: cityMarkers)
			if(marker != lastClicked) marker.setHidden(true);
		
		// Hide all non-threatening quakes
		Location loc = lastClicked.getLocation();			
		for(Marker marker: quakeMarkers) {
			float magnitude = ((EarthquakeMarker)marker).getMagnitude();	
			double distance = 20 * Math.pow(1.8, 2*magnitude - 5);
			if(marker.getDistanceTo(loc) > distance) marker.setHidden(true);		
		}		
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		int xbase = 25;
		int ybase = 50;
		
		fill(255, 250, 240);
		rect(xbase, ybase, 150, 300);
			
		int title_x = xbase + 25;
		int title_y = ybase + 25;
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("EARTHQUAKE KEY", title_x, title_y);
			
			
		// Draw city marker as an equilateral triangle
		int tri_x = xbase + 35;
		int tri_y = ybase + 65;
		
		float xdelta = (float) Math.cos(Math.toRadians(30.0)) * 5;
		float ydelta = (float) Math.sin(Math.toRadians(30.0)) * 5;
		fill(color(160, 160, 160));
		stroke(color(128, 128, 128));
		triangle(tri_x, tri_y-5, tri_x-xdelta, tri_y+ydelta, tri_x+xdelta, tri_y+ydelta);
			
		// Draw land quake marker as circle
		int cir_x = xbase + 35;
		int cir_y = ybase + 80;
		fill(255, 255, 255);
		ellipse(cir_x, cir_y, 10, 10);
			
		// Draw ocean quake marker as square
		int square_x = xbase + 35;
		int square_y = ybase + 95;
		rect(square_x - 5, square_y - 5, 10, 10);
			
		// Draw labels
		int label_x = xbase + 30;
		int label_y = ybase + 50;
		fill(0, 0, 0);
		text("Markers", label_x, label_y);
		text("City", label_x + 20, label_y + 15);
		text("Land Quake", label_x + 20, label_y + 30);
		text("Ocean Quake", label_x + 20, label_y + 45);
			
		text("Magnitude", label_x, label_y + 60);
		text("Marker Size", label_x + 15, label_y + 80);
			
		text("Depth", label_x, label_y + 100);
			
		// Create graduated color scale from yellow to red for the Depth
		int depth_x = label_x;
		int depth_y = label_y + 115;
		int width = 110;
		
		int c1 = color(255, 255, 0);
		int c2 = color(255, 0, 0);
		for(int i = depth_y; i <= depth_y + width; i++) {
			float inter = map(i, depth_y, depth_y + width, 0, 1);
			int c = lerpColor(c1, c2, inter);
			stroke(c);
			line(depth_x, i, depth_x + 20, i);
		}
			
		// Add depth levels to color scale
		text("0 km", depth_x + 30, depth_y);
		text("700 kms", depth_x + 30, depth_y + width-5);
	}
	
	

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  
	private boolean isLand(PointFeature earthquake) {		
		for(Marker country: countryMarkers)
			if(isInCountry(earthquake, country)) return true;
		return false;
	}
	
	// prints countries with number of earthquakes
	private void printQuakes() {
		String name;		// current country name
		int count = 0;		// earthquake count		
		
		// For all countries with earthquakes, print country name & count of quakes
		for(Marker country: countryMarkers) {
			count = 0;
			name = country.getStringProperty("name");
			for(Marker quake: quakeMarkers)			  
				  if(((EarthquakeMarker)quake).isOnLand() && ((LandQuakeMarker)quake).getCountry() == name) 
					  count++;
			if(count != 0) System.out.println(name + ": " + count);
		}
		
		// Print number of off-shore earthquakes
		count = 0;
		for(Marker quake: quakeMarkers)
			if(!((EarthquakeMarker) quake).isOnLand()) count++;
		System.out.println("Off Shore Quakes: " + count);
	}
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
