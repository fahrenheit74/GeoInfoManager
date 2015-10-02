package com.eleksheep.gim;

import java.text.DecimalFormat;
import java.util.ArrayList;

//import com.eleksheep.geocoord2.R.color;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.view.View;

public class SatView extends View {
     
     ArrayList<CurrentSat> cSats = new ArrayList<CurrentSat>();
     private Paint pStandard;
     private Paint pCircle;
     private Paint pSatText;
     private Paint pSatCircle;
     private Paint pLabText;
     
     private int m_satfix = 0;
     private int m_sattotal = 0;
     private float m_saterror = 9999;
     private String m_provider = "";
     
     public void setSatFix(int satFix){
    	 m_satfix = satFix;
     }
     public void setSatTotal(int satTotal){
    	 m_sattotal = satTotal;
     }
     public void setSatError(float saterror){
    	 m_saterror = saterror;
     }
     public void setProvider(String satProvider){
    	 m_provider = satProvider;
     }
     
     /* Working with a Enum is 10000%
      * safer than working with int's
      * to 'remember' the direction. */
     protected enum HorizontalDirection {LEFT, RIGHT}
     protected enum VerticalDirection {UP, DOWN}
     protected HorizontalDirection myXDirection = HorizontalDirection.RIGHT;
     protected VerticalDirection myYDirection = VerticalDirection.UP;

     public SatView(Context context) {
          super(context);
          pStandard = new Paint();
          pCircle = new Paint();
          pSatText = new Paint();
          pLabText = new Paint();
          pSatCircle = new Paint();
          
          this.setBackgroundColor(Color.argb(255, 221, 221, 204));  
     }

     @Override
     protected void onDraw(Canvas canvas) {
    	 
    	//Paints----Define paints below.-----------------------------------------------
    	 int standardColor = Color.BLACK;
    	
    	 pStandard.setAntiAlias(true);
         pStandard.setTextSize(16);
         pStandard.setStyle(Style.FILL);
         pStandard.setTextAlign(Align.CENTER);
         pStandard.setTypeface(Typeface.SANS_SERIF);
         pStandard.setTypeface(Typeface.DEFAULT_BOLD);
         pStandard.setStrokeCap(Cap.SQUARE);
         pStandard.setColor(standardColor);
         pStandard.setStrokeWidth(2);
         
         pCircle.setStyle(Style.STROKE);
         pCircle.setStrokeWidth(2);
         pCircle.setColor(standardColor);
         
         pSatText.setStyle(Style.FILL);
         pSatText.setColor(Color.BLACK);
         pSatCircle.setStyle(Style.FILL);
         pSatText.setTextAlign(Align.CENTER);
         
         pLabText.setColor(standardColor);
         pLabText.setTypeface(Typeface.SANS_SERIF);
         pLabText.setTypeface(Typeface.DEFAULT_BOLD);
         pLabText.setTextAlign(Align.CENTER);
         pLabText.setTextSize(16);
         
         Point cp = new Point();
         
         cp.x = 160; cp.y=160;
         int nconstellsize= 120;
         
         drawConstellation(canvas,nconstellsize, cp);
         drawSatellites(canvas, nconstellsize, cp);
         drawSatInfo(canvas, nconstellsize, cp);
     }
     
     void drawSatellites(Canvas canvas, int nconstellsize, Point cp){
    	 Point pt1 = new Point();
    	 double[] vec = new double[2];
         Point snrPt = new Point();
         Rect snrRect = new Rect();
         snrPt.x = (int)((canvas.getWidth()/2)-((cSats.size()*25)/2));
         snrPt.y = 360;
         
    	 for(int j=0; j<cSats.size(); j++){
        	 double ang=(Math.PI*cSats.get(j).GetElv())/180.0;
        	 double dist=nconstellsize*Math.cos(ang);
        	 ang=(Math.PI*(450-cSats.get(j).GetAzi()))/180.0;
        	 vec[0]=dist*Math.cos(ang);
        	 vec[1]=dist*Math.sin(ang);
        	 pt1.x= cp.x + (int)vec[0];
        	 pt1.y= cp.y - (int)vec[1];
        	 
        	 if(cSats.get(j).GetUsed()){
        		// pSatText.setColor(usedColor);
        		 pSatCircle.setColor(Color.argb(156, 52, 248, 52));
        	 }
        	 else{
        		// pSatText.setColor(unusedColor);
        		 pSatCircle.setColor(Color.argb(156, 248, 52, 52));
        	 }
        	 
        	 canvas.drawCircle(pt1.x, pt1.y, 12, pSatCircle);
        	 canvas.drawCircle(pt1.x, pt1.y, 12, pCircle);
        	 canvas.drawText(Integer.toString(cSats.get(j).GetPrn()), pt1.x, pt1.y+4, pSatText);
        	 
        	 snrRect.bottom = snrPt.y;
             snrRect.left = snrPt.x;
             snrRect.right = snrPt.x+20;
             snrRect.top = snrPt.y-(int)cSats.get(j).GetSNR();
             canvas.drawRect(snrRect,pSatCircle);
             canvas.drawRect(snrRect, pCircle);
             
             canvas.drawText(Integer.toString(cSats.get(j).GetPrn()), snrRect.centerX(), snrRect.centerY()+4, pSatText);
             
             snrPt.x =  snrPt.x+25;
         }
     }
     
     void drawConstellation(Canvas canvas, int nconstellsize, Point cp){
    	 canvas.drawCircle(cp.x, cp.y, nconstellsize, pCircle);
    	 DashPathEffect dashPath = new DashPathEffect(new float[]{6,2}, 1);
         double ang=(Math.PI*45)/180.0;
   	 	 double dist=nconstellsize*Math.cos(ang);
    	 canvas.drawCircle(cp.x, cp.y, (int)dist, pCircle);
    	 
    	 canvas.drawLine(cp.x, cp.y, cp.x, cp.y+nconstellsize, pCircle);
    	 canvas.drawLine(cp.x, cp.y, cp.x+nconstellsize, cp.y, pCircle);
    	 canvas.drawLine(cp.x, cp.y, cp.x, cp.y-nconstellsize, pCircle);
    	 canvas.drawLine(cp.x, cp.y, cp.x-nconstellsize, cp.y, pCircle);
    	 
    	 Rect snrbnd = new Rect();
    	 snrbnd.bottom = 360;
    	 snrbnd.top = snrbnd.bottom-60;
    	 snrbnd.left = 10;
    	 snrbnd.right = canvas.getWidth()-10;
    	 
    	 Paint psnrbnd = new Paint();
    	 psnrbnd.setStyle(Style.STROKE);
    	 psnrbnd.setStrokeWidth(3);
    	 psnrbnd.setColor(Color.BLACK);
    	 canvas.drawRect(snrbnd, psnrbnd);
    	
    	 psnrbnd.setStrokeWidth(1);
    	 psnrbnd.setColor(Color.GRAY);
    	 psnrbnd.setPathEffect(dashPath);
    	 
    	 for(int k=0;k<5; k++)
    		 canvas.drawLine(snrbnd.left, snrbnd.bottom-(10*(k+1)), snrbnd.right, snrbnd.bottom-(10*(k+1)), psnrbnd);
     }
     
    void drawSatInfo(Canvas canvas, int nconstellsize, Point cp){
    	 
    	Point startpt = new Point(cp.x,390);
    	String szProvider = "";
    	if(m_sattotal > 0){
	    	DecimalFormat twoPlaces = new DecimalFormat("0.00");
	    	
	    	if(m_provider.length() > 0)
	    		szProvider =  szProvider + "Provider: " + m_provider;
	    	 
	    	szProvider =  szProvider +  "  Fix/Sats: " + Integer.toString(m_satfix) + "/" + Integer.toString(m_sattotal);
	    	
	    	if(m_saterror < 9999)
	    		szProvider =  szProvider +  "  ACC: " + twoPlaces.format(m_saterror)+"m"; 
	    	
	    	canvas.drawText(szProvider, startpt.x, startpt.y, pLabText);
    	}
    	
     }
}