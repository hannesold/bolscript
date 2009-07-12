package config;

import static algorithm.pilots.WayPoint.CurvePointTypes.LEVEL;
import static algorithm.pilots.WayPoint.CurvePointTypes.LINEAR;
import gui.composers.kaida.pilots.PilotItem;
import algorithm.pilots.Pilot;
import algorithm.raters.RaterAverageSpeed;
import algorithm.raters.RaterDifferentFromBefore;
import algorithm.raters.RaterInnerRepetitiveness;
import algorithm.raters.RaterOffBeatness;
import algorithm.raters.RaterSimilarEnd;
import algorithm.raters.RaterSimilarStart;
import algorithm.raters.RaterSpeedStdDeviation;

public class Pilots {

	/**
	 * Speeds up quickly, ends after 4 variations.
	 */
	public static Pilot getPilot01() {
		Pilot pilot = new Pilot("Short spice","Speeds up to 2 quickly with low deviation.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f, 1.0f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f, 0.3f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	8f, 0.01f, LINEAR);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f, 10f,LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f, 0.5f,LINEAR);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	2f, 0.0f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class, 	1, 	1f, 1f, LEVEL);	
		
		pilot.addPoint(RaterAverageSpeed.class, 		1, 	2.0f, 1f, LINEAR);
		
		pilot.addPoint(RaterSpeedStdDeviation.class, 	2, 0.3f, 0.6f, LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class,			3, 2.0f, 2f, LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class,			4,	2.6f,	1f,LEVEL);
		
		pilot.addPoint(RaterSpeedStdDeviation.class,	4,	0.5f,	0.3f,LEVEL);
	
		
		return pilot;
	}

	/**
	 * theme repetition at db speed Speeds up quickly, ends after 5 variations.
	 */
	public static Pilot getPilot06() {
		Pilot pilot = new Pilot("Short valid kaida","Repeats theme at double speed. Speeds up quickly, ends after 5 variations.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f, 1.0f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f, 0.3f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	8f, 0.01f, LINEAR);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f, 10f,LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f, 0.5f,LINEAR);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	2f, 0.0f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class, 	1, 	1f, 1f, LEVEL);	
		
		pilot.addPoint(RaterAverageSpeed.class, 		1, 	2.0f, 1f, LINEAR);
		
		pilot.addPoint(RaterSimilarStart.class,			1,	16f, 0.3f, LINEAR);
		pilot.addPoint(RaterSimilarEnd.class,			1,	16f, 0.3f, LINEAR);
		
		pilot.addPoint(RaterSimilarStart.class,			2, 	6f, 0.5f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			2, 	8f, 0.01f, LINEAR);
		
		pilot.addPoint(RaterSpeedStdDeviation.class, 	3, 0.3f, 0.6f, LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class,			4, 2.0f, 2f, LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class,			5,	2.6f,	1f,LEVEL);
		
		pilot.addPoint(RaterSpeedStdDeviation.class,	5,	0.5f,	0.3f,LEVEL);
	
		
		return pilot;
	}

	/**
	 * theme repetition at db speed, Increases offbeatness and repetitiveness
	 * @return
	 */
	public static Pilot getPilot07() {
		Pilot pilot = new Pilot("Syncopator","Increases offbeatness and repetitiveness");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f,  1f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f,  0f, LEVEL);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	4f,  0.25f, LEVEL);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f,  2f, LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f,  0.25f, LEVEL);		
		
		
		pilot.addPoint(RaterSimilarStart.class,			1,	16f, 0.3f, LINEAR);
		pilot.addPoint(RaterSimilarEnd.class,			1,	16f, 0.3f, LINEAR);
		pilot.addPoint(RaterAverageSpeed.class, 		1, 	2f,  0.5f, LEVEL);	
		pilot.addPoint(RaterDifferentFromBefore.class,  1,	1f,  1f, LEVEL);
		
		pilot.addPoint(RaterSimilarStart.class,			2, 	6f, 0.5f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			2, 	8f, 0.01f, LINEAR);
		
	
		pilot.addPoint(RaterSpeedStdDeviation.class, 	2, 	0f,  2f, LINEAR);
		pilot.addPoint(RaterAverageSpeed.class, 		4, 	2.2f,  0.8f, LINEAR);
		
		int[] values = new int[]{0, 10, 12};
		int[] valuesRepetitiveness = new int[]{2,5,5};
		
		for (int i=0; i < values.length; i++) {
			pilot.addPoint(RaterOffBeatness.class, i+1, (double)values[i], 1f, LINEAR);
			pilot.addPoint(RaterInnerRepetitiveness.class, i+1, (double)valuesRepetitiveness[i], 0.1f , LINEAR);
		}
		
		pilot.addPoint(RaterSpeedStdDeviation.class,	5, 1f, 1f, LINEAR);
		pilot.addPoint(RaterOffBeatness.class, 6, 0f, 0.1f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class,	8, 0.5f, 0f, LINEAR);
		
		pilot.addPoint(RaterOffBeatness.class, 9, 0f, 1f, LEVEL);
		pilot.addPoint(RaterAverageSpeed.class,			9, 4f, 2f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class,	9, 0f, 2f, LINEAR);
		
		pilot.setDuration(10);
		
		return pilot;
	}

	/**
	 * theme repetition at db speed, Increases offbeatness and repetitiveness, theme at the end in 4x
	 * @return
	 */
	public static Pilot getPilot08() {
		Pilot pilot = new Pilot("Syncopator end theme","Increases offbeatness and repetitiveness, tries to repeat the theme in the end.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f,  1f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f,  0f, LEVEL);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	4f,  0.25f, LEVEL);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f,  2f, LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f,  0.25f, LEVEL);		
		
		
		pilot.addPoint(RaterSimilarStart.class,			1,	16f, 0.3f, LEVEL);
		pilot.addPoint(RaterSimilarEnd.class,			1,	16f, 0.3f, LEVEL);
		
		pilot.addPoint(RaterSimilarStart.class,			2, 	6f, 0.5f, LEVEL);
		pilot.addPoint(RaterSimilarEnd.class, 			2, 	8f, 0.01f, LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class, 		2, 	2f,  0.5f, LEVEL);	
		pilot.addPoint(RaterDifferentFromBefore.class,  2,	1f,  1f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	2, 	0f,  2f, LINEAR);
		pilot.addPoint(RaterAverageSpeed.class, 		4, 	2.2f,  0.8f, LINEAR);
		
		int[] values = new int[]{0, 10, 12};
		int[] valuesRepetitiveness = new int[]{2,5,5};
		
		for (int i=0; i < values.length; i++) {
			pilot.addPoint(RaterOffBeatness.class, i+1, (double)values[i], 1f, LINEAR);
			pilot.addPoint(RaterInnerRepetitiveness.class, i+1, (double)valuesRepetitiveness[i], 0.1f , LINEAR);
		}
		
		pilot.addPoint(RaterSpeedStdDeviation.class,	5, 1f, 1f, LINEAR);
		pilot.addPoint(RaterOffBeatness.class, 6, 0f, 0.1f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class,	8, 0.5f, 0f, LINEAR);
		
		pilot.addPoint(RaterSimilarStart.class,			8,	16f, 0.5f, LINEAR);
		pilot.addPoint(RaterSimilarEnd.class,			8,	16f, 0.5f, LINEAR);
		
		pilot.addPoint(RaterOffBeatness.class, 9, 0f, 1f, LEVEL);
		pilot.addPoint(RaterAverageSpeed.class,			9, 4f, 2f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class,	9, 0f, 2f, LINEAR);
		
		
		
		
		pilot.setDuration(10);
		
		return pilot;
	}

	public static Pilot getPilot9() {
		Pilot pilot = new Pilot("Isolated speed change","Increases speed goal to 4.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f, 1.0f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f, 0.01f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	8f, 0.01f, LINEAR);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f, 10f,LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f, 0.01f,LINEAR);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	2f, 0.0f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class, 	1, 	1f, 1f, LEVEL);	
		
		pilot.addPoint(RaterAverageSpeed.class, 		8, 	4.0f, 1f, LEVEL);
	
	
		pilot.setDuration(8);
		
		return pilot;
	}

	/**
	 * Establishes a nice deviation, then increases speed up to 8
	 */
	public static Pilot getPilot11() {
		Pilot pilot = new Pilot("Deviator p","Establishes a nice deviation, then increases speed up to 8, if allowed.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	2f, 1.0f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f, 0.3f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	16f, 1f, LINEAR);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	1f, 0f,LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	16f, 1f,LINEAR);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	2f, 0.0f, LINEAR);
		
		pilot.addPoint(RaterSimilarEnd.class, 			1, 	8f, 0.01f, LINEAR);
		pilot.addPoint(RaterSimilarStart.class,			1, 	6f, 0.5f,LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class, 	1, 	1f, 1f, LEVEL);	
		
		pilot.addPoint(RaterAverageSpeed.class, 		1, 	4.0f, 1f, LEVEL);
		
		pilot.addPoint(RaterSpeedStdDeviation.class, 	1, 0.5f, 0.8f, LINEAR);
		
		
				
		pilot.addPoint(RaterAverageSpeed.class,			2,	5f,	1f,LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class,	2,	1.8f, 0.8f,LINEAR);
		
		pilot.addPoint(RaterAverageSpeed.class,			3,	6f,	1f,LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class,	3,	2f, 0.8f,LINEAR);
		
		pilot.addPoint(RaterSpeedStdDeviation.class,	4,	1f,	0.8f,LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class,			5,	7f,	1.2f,LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class,	5,	0.5f,	0.8f,LEVEL);
		
		pilot.setDuration(5);
		
		return pilot;
	}

	public static Pilot getPilot12() {
		Pilot pilot = new Pilot("Isolated deviation change","Increases deviation to 2.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f, 1.0f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f, 0.01f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	8f, 0.01f, LINEAR);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f, 10f,LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f, 0.01f,LINEAR);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	2f, 0.0f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class, 	1, 	1f, 1f, LEVEL);	
		pilot.addPoint(RaterAverageSpeed.class, 		1, 	1f, 0.01f, LINEAR);
		
		pilot.addPoint(RaterSpeedStdDeviation.class, 		6, 	2.0f, 1f, LINEAR);
	
	
		pilot.setDuration(8);
		
		return pilot;
	}
	
	
	/**
	 * starts increasing avspeed at t=2, runs 60 variations (!)
	 * @return
	 */
	public static Pilot getPilot03() {
		Pilot pilot = new Pilot("Longrun","For longer tests, starts increasing average speed to 2 at t=2. Ends after 60 Variations.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f, 1.0f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f, 0.3f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	16f, 0.01f, LINEAR);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f, 10f,LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f, 0.5f,LINEAR);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	2f, 0.0f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class, 	1, 	1f, 1f, LEVEL);	
		pilot.addPoint(RaterAverageSpeed.class, 		1, 	2.0f, 1f, LINEAR);
		
		pilot.addPoint(RaterSpeedStdDeviation.class, 	10, 0.5f, 0.6f, LEVEL);
		pilot.addPoint(RaterInnerRepetitiveness.class,	10,	3f,	0.5f,LINEAR);
		
		pilot.addPoint(RaterInnerRepetitiveness.class,	20,	2f,	0f,LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class,			30,	2.5f,1f,LINEAR);
		pilot.addPoint(RaterAverageSpeed.class,			40,	2f,1f,LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	50, 0.0f, 0.6f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	60, 0.3f, 0.6f, LEVEL);
		
		return pilot;
	}

	/**
	 * Slows down first
	 * @return
	 */
	public static Pilot getPilot04() {
		Pilot pilot = new Pilot("Slowing down first","Slows down first, then speeds up to 2.");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f, 1.0f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f, 0.3f,LINEAR);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	16f, 0.01f, LINEAR);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f, 10f,LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f, 0.5f,LINEAR);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	2f, 0.0f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class, 	2, 	1f, 1f, LEVEL);	
		pilot.addPoint(RaterAverageSpeed.class, 		2, 	0.5f, 1f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	2, 	0.2f, 0.3f,LEVEL);
		
		pilot.addPoint(RaterAverageSpeed.class, 		4,  1f, 0.6f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	4, 0.2f, 0.6f, LEVEL);
		pilot.addPoint(RaterAverageSpeed.class, 		8,  2f, 0.6f, LINEAR);
		
		return pilot;
	}

	/**
	 * Increases offbeatness and repetitiveness
	 * @return
	 */
	public static Pilot getPilot02() {
		Pilot pilot = new Pilot("Syncopator","Increases offbeatness and repetitiveness");
		
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f,  1f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f,  0f, LEVEL);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	4f,  0.25f, LEVEL);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f,  2f, LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f,  0.25f, LEVEL);		
		
		pilot.addPoint(RaterAverageSpeed.class, 		1, 	2f,  0.5f, LEVEL);	
		pilot.addPoint(RaterDifferentFromBefore.class,  1,	1f,  1f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	1, 	0f,  2f, LINEAR);
		pilot.addPoint(RaterAverageSpeed.class, 		3, 	2.2f,  0.8f, LINEAR);
		
		int[] values = new int[]{0, 10, 12};
		int[] valuesRepetitiveness = new int[]{2,5,5};
		
		for (int i=0; i < values.length; i++) {
			pilot.addPoint(RaterOffBeatness.class, i, (double)values[i], 1f, LINEAR);
			pilot.addPoint(RaterInnerRepetitiveness.class, i, (double)valuesRepetitiveness[i], 0.1f , LINEAR);
		}
		
		pilot.addPoint(RaterSpeedStdDeviation.class,	4, 1f, 1f, LINEAR);
		pilot.addPoint(RaterOffBeatness.class, 5, 0f, 0.1f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class,	7, 0.5f, 0f, LINEAR);
		
		pilot.addPoint(RaterOffBeatness.class, 8, 0f, 1f, LEVEL);
		pilot.addPoint(RaterAverageSpeed.class,			8, 4f, 2f, LINEAR);
		pilot.addPoint(RaterSpeedStdDeviation.class,	8, 0f, 2f, LINEAR);
		
		pilot.setDuration(9);
		
		return pilot;
	}

	public static Pilot getPilot05_OffBeatness() {
		Pilot pilot = new Pilot("Offbeatness demonstration", "Increases Offbeatness from 0 to 128");
	
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f,  0f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f,  0.5f, LEVEL);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	16f, 0f, LEVEL);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f,  0f,  LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f,  0f,LEVEL);
		pilot.addPoint(RaterInnerRepetitiveness.class,	0,	0f,  0f, LINEAR);
		//pilot.addPoint(RaterOffBeatness.class,			0,	0f,	 1f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class,	1, 	1f,  0.5f,  LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class,	4, 	0f,  0.9f,  LEVEL);
		
		int[] values = new int[]{0,2,4,8,16,32,64,128};
		for (int i=0; i < values.length; i++) {
			pilot.addPoint(RaterOffBeatness.class, i, (double)values[i], 1f, LINEAR);
		}
		pilot.setDuration(8);
		return pilot;
	
	}

	public static Pilot getPilot10_Repetitiveness() {
		Pilot pilot = new Pilot("Repetitiveness demonstration", "Increases Repetitiveness from 0 to 6");
	
		pilot.addPoint(RaterAverageSpeed.class, 		0, 	1f,  0f, LEVEL);
		pilot.addPoint(RaterSpeedStdDeviation.class, 	0, 	0f,  1f, LEVEL);
		pilot.addPoint(RaterSimilarEnd.class, 			0, 	16f, 0f, LEVEL);
		pilot.addPoint(RaterDifferentFromBefore.class,	0, 	0f,  10f,  LEVEL);
		pilot.addPoint(RaterSimilarStart.class,			0, 	6f,  0f,LEVEL);
		pilot.addPoint(RaterOffBeatness.class,			0,	0f,	 0f, LINEAR);
		
		pilot.addPoint(RaterDifferentFromBefore.class,	1, 	1f,  0.1f,  LEVEL);
		pilot.addPoint(RaterAverageSpeed.class,	1, 	4f,  0.8f,  LEVEL);
		
		int[] values = new int[]{0,1,2,3,4,5,6,6};
		for (int i=0; i < values.length; i++) {
			pilot.addPoint(RaterInnerRepetitiveness.class, i, (double)values[i], 1f, LINEAR);
		}
		pilot.setDuration(8);
		return pilot;
	
	}

	public static PilotItem[] getPilotsAsMenuItems() {
		
		PilotItem[] pi = new PilotItem[]{
		new PilotItem("Pilot1", getPilot01()),
		new PilotItem("Pilot2", getPilot02()),
		new PilotItem("Pilot3", getPilot03()),
		new PilotItem("Pilot4", getPilot04()),
		new PilotItem("Pilot5", getPilot05_OffBeatness()),
		new PilotItem("Pilot6", getPilot06()),
		new PilotItem("Pilot7", getPilot07()),
		new PilotItem("Pilot8", getPilot08()),
		new PilotItem("Pilot9", getPilot9()),
		new PilotItem("Pilot10", getPilot10_Repetitiveness()),
		new PilotItem("Pilot11", getPilot11()),
		new PilotItem("Pilot12", getPilot12())
		
		
		};
		return pi;
	}

}
