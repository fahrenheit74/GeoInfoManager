package com.jhlabs.map;

/*
	* The Ellipse Point2D class defines a point representing a location
	* in (x;y) coordinate space.
	* 
	* This class is only the abstract superclass for all objects that
	* store a 2D coordinate.
	* The actual storage representation of the coordinates is left to
	* the subclass.
	*
	* @version 1.18, 12/19/03
	* @author Jim Graham
*/



public abstract class Ellipse2D {

	public class Double extends Ellipse2D{
	public double x;
	public double y;
	public double width;
	public double height;
	
	public double getX() {
		 return  x;
	   }
	public double getY() {
		 return  y;
	   }
	public double getWidth() {
		 return  width;
	   }
	public double getHeight() {
		 return  height;
	   }
	
	public boolean isEmpty() {
		return (width <= 0.0 || height <= 0.0);
	}
	
	public void setFrame(double x, double y, double w, double h) {
		this.x =  x;
		this.y =  y;
		this.width =  w;
		this.height =  h;
	}
	
	public Double() {}
	
	public Double(double x, double y,double w,double h){
		setFrame(x,y,w,h);
	}
	public Rectangle2D getBounds2D(){
		return new Rectangle2D(x, y, width, height);
	}
	
	}

}
