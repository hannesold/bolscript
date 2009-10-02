package bols;

import basics.Rational;

/**
 * A simple structure for storing fundamental properties of
 * how to play a Bol, i.e. speed, velocity, empty. Speed can be accessed as a double and as a Rational (preferred).
 * The class also
 * provides methods for creating new PlayingStyles and different types of copying.
 * 
 * <p>Contains:<br>
 * - a relative speed - Rational<br>
 * - a relative velocity - double<br>
 * - a field for storing if it is empty (true) or not (false) - boolean<br>
 * </p>
 * @author hannes
 *
 */
public class PlayingStyle {
	
	/**
	 * is pause or not?
	 */
	public boolean empty; 
	
	/**
	 * playing speed (1=normal, 2=double ...)
	 * r stands for Rational
	 */
	public Rational rspeed;
	
	/**
	 * //volume (1=normal, 0.5=half as loud)
	 */
	public double velocity; 
	
	/**
	 * Velocity is set to 1, empty to false.
	 * @param speed
	 */
	public PlayingStyle(double speed) {
		this(new Rational(speed), 1, false);
	}
	/**
	 * Empty is set to false.
	 * @param speed
	 * @param velocity
	 */
	public PlayingStyle(double speed, double velocity) {
		this(new Rational(speed), velocity, false);
	}
	
	/**
	 * The standard constructor. 
	 * @param speed
	 * @param velocity
	 * @param empty
	 */
	public PlayingStyle(double speed, double velocity, boolean empty) {
		this(new Rational(speed), velocity, empty);
	}
	
	/**
	 * Uses an independent copy of the given speed. Empty is set to false.
	 * @param speed
	 * @param velocity
	 */
	public PlayingStyle(Rational speed, double velocity) {
		this(speed, velocity, false);	
	}
	
	/**
	 * Uses an independent copy of the given speed.
	 * @param speed
	 * @param velocity
	 * @param empty
	 */
	public PlayingStyle(Rational speed, double velocity, boolean empty) {
		this.rspeed = new Rational(speed);
		this.velocity = velocity;
		this.empty = empty;		
	}
	
	public String toString() {
		return "(speed: "+rspeed+", velocity: "+velocity+")";
	}
	
	public PlayingStyle getCopy() {
		return new PlayingStyle(rspeed.getCopy(),velocity, empty);
	}
	
	/**
	 * Builds a new Style by multiplying speed and velocity scalings,
	 * and setting empty if one of the styles is empty.
	 * @return
	 */
	public PlayingStyle getProduct(PlayingStyle style2) {
		PlayingStyle newStyle = new PlayingStyle(rspeed.times(style2.rspeed), 
				velocity * style2.velocity,
				(empty || style2.empty));
		return newStyle;
	}
	
	
	public boolean equals(PlayingStyle style) {
		return (empty==style.empty) && (rspeed.equals(style.rspeed)) && (velocity==style.velocity);
	}

	public void setSpeedValue(double speed) {
		this.rspeed = new Rational(speed);
	}
	
	public void setSpeed(Rational speed) {
		this.rspeed = speed;
	}

	public double getSpeedValue() {
		return rspeed.toDouble();
	}

	public Rational getSpeed() {
		return rspeed;
	}
	
	
}
