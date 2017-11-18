package src.java;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;

public class IdentifyMe {

	final private static String refPath = "reference_pic.jpeg";
	final private static String subPath = "new_pic.jpeg";

	public static void main(String[] args) {

		System.out.println("Found a camera - " + Webcam.getWebcams());
		// Take the code from Sarsox and get the camera up and running
		Webcam webcam = Webcam.getDefault();
		webcam.open();

		System.out.println("WebCam open now !");
		// Store the image, it will be used as your reference image for
		// authentication purposes
		BufferedImage image = webcam.getImage();
		try {
			ImageIO.write(image, "JPG", new File(refPath));
		} catch (Exception e) {
			System.out.println("An error occured - " + e.getMessage());
		}
		System.out.println("Reference Image Taken!");

		try {
			System.out.println("Image for authentication will be taken after 2 seconds ... ");
			for (int i = 1; i <= 2; i++) {
				Thread.sleep(2000);
				System.out.println(i + "...");
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		image = webcam.getImage();
		try {
			ImageIO.write(image, "JPG", new File(subPath));
		} catch (Exception e) {
			System.out.println("An error occured >> " + e.getMessage());
		}
		System.out.println("New Image Taken!");

		System.out.println("Starting the comparison >");
		BufferedImage reference = null, subject = null;
		try {
			reference = ImageIO.read(new File(refPath));
			subject = ImageIO.read(new File(subPath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean isMatchFound = comparePics(reference, subject);

		System.out.println("Are images having at least 20 % similarities ? " + isMatchFound);

	}

	private static boolean comparePics(BufferedImage reference, BufferedImage subject) {

		int refWidth = reference.getWidth();
		int refHeight = reference.getHeight();
		int subWidth = subject.getWidth();
		int subHeight = subject.getHeight();

		// matching the color pattern along with the pixels to find similarities

		if (refWidth != subWidth || refHeight != subHeight) {
			// considering equal dimensions of the photos taken for this demo
			System.out.println("Incorrect dimensions!");
			System.out.println("Reference : " + refWidth + " X " + refHeight);
			System.out.println("Subject   : " + subWidth + " X " + subHeight);
			return false;
		}

		// using some knowledge here
		// https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
		int variation = 0;
		long totalPixelsAvailable;
		for (int i = 0; i < refWidth; i++) {
			for (int j = 0; j < refHeight; j++) {
				int refRGB = reference.getRGB(i, j);
				int refRed = (refRGB & 0xff0000) >> 16;
				int refGreen = (refRGB & 0xff00) >> 8;
				int refBlue = refRGB & 0xff;
				int subRGB = subject.getRGB(i, j);
				int subRed = (subRGB & 0xff0000) >> 16;
				int subGreen = (subRGB & 0xff00) >> 8;
				int subBlue = subRGB & 0xff;
				variation += Math.abs(refRed - subRed) + Math.abs(refGreen - subGreen) + Math.abs(refBlue - subBlue);
			}
		}

		System.out.println("total variation - " + variation);
		totalPixelsAvailable = refHeight * subHeight *3 * 255;
		System.out.println("total pixels available - " + totalPixelsAvailable);
		double variationPercent = (variation / totalPixelsAvailable) * 100;
		System.out.println("variationPercent - " + variationPercent);
		if (variationPercent < 50) {
			return true;
		} else {
			return false;
		}
	}
}
