import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import processing.core.*;

public class MySketch extends PApplet {

	PFont font;
	int hubId = 22;
	int total, current;
	double ratio = 0;
	ArrayList history = new ArrayList();
	boolean modifying = false;
	int frameI = 0;
	
    public void setup() {
          size(800, 800);
          font = loadFont("Junction-200.vlw");
          textFont(font, 50);
          textAlign(LEFT, TOP);
          text("starting", 50, 50);
          textAlign(RIGHT, CENTER);
          frameRate(60);
          
          new Thread(new Runnable() {

			public void run() {
				while (true) {
					JSONObject json = pullJSON("http://www.electric20.com/dataStore/request.php?u=toadhall&p=energy21&action=currentNetLoad");
					try {
						total = json.getInt("data");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	json = pullJSON("http://www.electric20.com/dataStore/request.php?u=toadhall&p=energy21&action=currentHubLoad&hubId=" + hubId);
					try {
						current = json.getInt("data");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ratio = (double)current / (double)total;
					modifying = true;
					history.add((int)((double)current / 9000 * 600));
					while (history.size() > 140) {
						history.remove(0);
					}
					modifying = false;
					delay(2000);
				}
			}
          }).start();
    }
    
    int x = 0;
    int y = 0;
    
    public void draw () {
    	background((int) (ratio * 255));
    	
    	frameI++;
    	if (frameI >= 60) {
    		frameI = 0;
    	}
    	
    	stroke(30 * frameI / 60, 32 * frameI / 60, 0);
    	fill(60 * frameI / 60, 64 * frameI / 60, 0);
    	ellipse(100, 100, (int)(5000 * frameI / 60 * ratio), (int)(5000 * frameI / 60 * ratio));
    	stroke(60 * frameI / 60, 64 * frameI / 60, 0);
    	fill(120 * frameI / 60, 127 * frameI / 60, 0);
    	ellipse(100, 100, (int)(1000 * frameI / 60 * ratio), (int)(1000 * frameI / 60 * ratio));
    	stroke(240, 255, 0);
    	fill(240 * frameI / 60, 255 * frameI / 60, 0);
    	ellipse(100, 100, (int)(300 * frameI / 60 * ratio), (int)(300 * frameI / 60 * ratio));
    	
    	if (!modifying) {
	    	stroke(240, 255, 0);
	    	Iterator i = history.iterator();
	    	int ox = 1;
	    	int oy = y;
	    	x = 0;
	    	try {
		    	while (i.hasNext()) {
		    		int y = (Integer) i.next();
				    x++;
				    line(750 - x * 5, 700 - y, 750 - ox * 5, 700 - oy);
				    ox = x;
				    oy = y;
		    	}
		    	textFont(font, 10);
		    	fill(240, 255, 0);
		    	rotate(radians(90));
		    	textAlign(RIGHT, BOTTOM);
		    	text("2 minutes ago", 700, -750);
		    	rotate(radians(-90));
		    	textAlign(RIGHT, BOTTOM);
		    	textFont(font, 50);
		    	String label = "hub" + hubId;
		        float width = textWidth(label);
		        float height = textAscent() + textDescent();
		        rect(700 - width, 175 - height, width + 30, height + 30);
		        fill((int) (ratio * 255));
		        text(label, 715, 200);
		        fill(255);
		        textFont(font, 200);
		        textAlign(RIGHT, CENTER);
				text(current + "W", 750, 400);
				fill(240, 255, 0);
				text(Math.round(ratio * 100) + "%", 750, 600);
	    	} catch (ConcurrentModificationException c) {
	    		
	    	}
    	}
    }
    
    JSONObject pullJSON(String targetURL) {
    	  String[] lines = loadStrings(targetURL);
    	  if (lines != null) {
    	    try {
    	      String jsonString = join(lines, "");
    	      return new JSONObject(jsonString);
    	    } catch (JSONException j) {
    	      j.printStackTrace();
    	    }
    	  }
    	  return null;
    	}
}