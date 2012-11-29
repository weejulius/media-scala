package com.thenetcircle.services.media.service.impl.transform.processing;

import com.thenetcircle.services.media.service.impl.transform.Transform;
import com.thenetcircle.services.media.service.impl.Image;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.awt.*;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * rotate the image by a given angle
 * 
 * note
 * I tried to send 15 rotation transforms in a request
 * it caused outofmemory error
 * be careful!
 * 
 * @author fan
 *
 */
public class RotateImageTransform implements Transform {

	private static final double DEGREE = Math.PI / 180;
	private static final double PI_2 = PI / 2d;
	
	
	private int radius = 0;
	// TODO not apply yet
	private boolean zoomIn = false;
	
	private Color bgColor = null;
	
	private double convertRadius(int radius) {
		return (float) (DEGREE * radius);
	}
	
	public RotateImageTransform(int radius) {
		this.radius = radius;
	}
	
	/* (non-Javadoc)
	 * @see com.thenetcircle.services.media.service.impl.transform.Transform#transform(com.thenetcircle.services.media.service.impl.Image)
	 */
	@Override
	public Image transform(Image inputImage) {
		final PImage inputPImage = (PImage) inputImage.get();
		final PApplet pApplet = Processing.get();

		int newWidth = inputPImage.width;
		int newHeight = inputPImage.height;
		double angle = convertRadius(radius);
		
		int originalWidth = inputPImage.width;
		int originalHeight = inputPImage.height;
		
		if (zoomIn) {
			double originalAngle = Math.atan2(originalHeight, originalWidth);
			
			double newAngle = angle;
			newAngle = newAngle > PI_2 ? PI - newAngle : newAngle;
			newAngle += originalAngle;
			
			inputPImage.resize((int)(inputPImage.height * (double)(cos(originalAngle) / sin(newAngle))), 
					(int)(inputPImage.height *  (double)(sin(originalAngle) / sin(newAngle))));
		} else {
			newWidth = (int) (inputPImage.width * abs(cos(angle)) + inputPImage.height * abs(sin(angle)));
			newHeight = (int) (inputPImage.height * abs(cos(angle)) + inputPImage.width * abs(sin(angle)));
		}
		
		final PGraphics drawer = pApplet.createGraphics(newWidth, newHeight, PApplet.JAVA2D);
		drawer.beginDraw();
		
		if (bgColor != null) {
			drawer.fill(bgColor.hashCode());
			drawer.rect(0, 0, originalWidth, originalHeight);
//			drawer.translate(originalWidth / 2, originalHeight / 2);
		}
		
		drawer.translate(newWidth / 2, newHeight / 2);
		drawer.rotate((float) angle);
		drawer.translate(-newWidth / 2, -newHeight / 2);
		drawer.image(inputPImage, ( newWidth - inputPImage.width) / 2, ( newHeight - inputPImage.height) / 2);
//		drawer.image(inputPImage, 0, 0);

		drawer.endDraw();
		inputImage.set(drawer);
		return inputImage;
	}

}
