package com.jhlabs.map;

/*
  	* The Point2Dclass defines a point representing a location
  	* in (x,y) coordinate space.
  	* 
  	* This class is only the abstract superclass for all objects that
  	* store a 2D coordinate.
 	* The actual storage representation of the coordinates is left to
 	* the subclass.
  	*
  	* @version 1.18, 12/19/03
 	* @author Jim Graham
 */



public abstract class Point2D implements Cloneable{
	//The Float class defines a point specified in float precision.
	public static class Float extends Point2D{
		public float x;
		public float y;
		
		public Float() {}
		
		public Float(float x, float y) {
			this.x = x;	this.y = y;
		}
		
		public double getX() {
			return (double) x;
		}
		public double getY() {
			return (double) x;
		}
		public void setLocation(double x, double y) {
			this.x = (float) x;
			this.y = (float) y;
		}
		
		public void setLocation(float x, float y) {
			this.x = (float) x;
			this.y = (float) y;
		}
		
		public String  toString() {
			return "Point2D.Float["+x+", "+y+"]";
			    }
		}
	
	public static class Double extends Point2D{
		public double x;
		public double y;
		
		public Double() {}
		
		public Double(double x, double y) {
			this.x = x;	this.y = y;
		}
		
		public double getX() {
			return  x;
		}
		public double getY() {
			return  x;
		}
		public void setLocation(double x, double y) {
			this.x =  x;
			this.y =  y;
		}
		
		public void setLocation(float x, float y) {
			this.x = (float) x;
			this.y = (float) y;
		}
		
		///public String JavaDoc toString() {
		//	return "Point2D.Float["+x+", "+y+"]";
		//	    }
		//}

	}
	
	protected Point2D() { }
	
	public abstract double getX();
	public abstract double getY();
	public abstract void setLocation(double x, double y);
	public void setLocation(Point2D p){
		setLocation(p.getX(), p.getY());
	}
	
	public static double distanceSq(double X1, double Y1,double X2, double Y2) {
		X1 -= X2;
		Y1 -= Y2;
		return (X1 * X1 + Y1 * Y1);
    }
	public static double distance(double X1, double Y1,double X2, double Y2) {
		X1 -= X2;	Y1 -= Y2;
		return Math.sqrt(X1 * X1 + Y1 * Y1);
	}
	 public double distanceSq(Point2D pt) {
		 double PX = pt.getX() - this.getX();
		 double PY = pt.getY() - this.getY();
		 return (PX * PX + PY * PY);
	 }
	 
	 public double distance(double PX, double PY) {
		 PX -= getX();
		 PY -= getY();
		 return Math.sqrt(PX * PX + PY * PY);   
	}

	 public double distance(Point2D pt) {
		 double PX = pt.getX() - this.getX();
		 double PY = pt.getY() - this.getY();
		 return Math.sqrt(PX * PX + PY * PY);
	 }
	 
}
