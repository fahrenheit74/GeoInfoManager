package com.jhlabs.map;

public class Rectangle2D {

	double x = 0.0;
	double y = 0.0;
	double width = 0.0;
	double height = 0.0;
	
	public Rectangle2D(){};
	public Rectangle2D(double x1,double y1, double w, double h){x=x1; y=y1; width = w; height = h;};
	
	
	public double getX(){return x;}
	public double getY(){return y;}
	public double getWidth(){return width;}
	public double getHeight(){return height;}
	
	public void setX(double x1){x = x1;}
	public void setY(double y1){x = y1;}
	public void setWidth(double w){width = w;}
	public void setHeight(double h){height =h;}
	
	public void setRect(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
	    this.height = h;   
	    }
	
	public double getMinX(){
		return x;
	}
	public double getMinY(){
		return y;
	}
	
	public double getMaxX(){
		return x+getWidth();
	}
	
	public double getMaxY(){
		return y+getWidth();
	}

	public void add(double newx, double newy) {
		double x1 = Math.min(getMinX(), newx);
		double x2 = Math.max(getMaxX(), newx);
		double y1 = Math.min(getMinY(), newy);
		double y2 = Math.max(getMaxY(), newy);
		setRect(x1, y1, x2 - x1, y2 - y1);
		}

}
	
