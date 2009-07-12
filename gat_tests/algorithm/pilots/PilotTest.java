package algorithm.pilots;

import java.util.ArrayList;

import junit.framework.TestCase;
import algorithm.composers.kaida.GoalSet;
import algorithm.raters.Rater;
import algorithm.raters.RaterAverageSpeed;
import algorithm.raters.RaterDifferentFromBefore;
import algorithm.raters.RaterSimilarEnd;
import algorithm.raters.RaterSpeedStdDeviation;
import bols.BolBase;
import static algorithm.pilots.WayPoint.CurvePointTypes.*;
public class PilotTest extends TestCase {
	BolBase bb;
	
	protected void setUp() throws Exception {
		super.setUp();
		bb = new BolBase();
	}

	/*
	 * Test method for 'algorithm.pilots.Pilot.updateGoalSet(GoalSet, long)'
	 */
	public void testUpdateGoalSet() {
		Pilot p1 = new Pilot();
		
		ArrayList<Rater> raters = new ArrayList<Rater>();
		raters.add(new RaterAverageSpeed(bb));
		raters.add(new RaterSpeedStdDeviation(bb));
		raters.add(new RaterSimilarEnd(bb));
		raters.add(new RaterDifferentFromBefore(bb));

		//set GoalSet
		GoalSet goalSet = new GoalSet();
		goalSet.addGoal(raters.get(0), 1f, 1.0f);
		//goalSet.addGoal("Goal Speed Deviation", raters.get(1), 0f, 0.30f);
		//goalSet.addGoal("Goal Similar Bols At End", raters.get(2), 6f, 0.01f);
		//am anfang soll die ursprungsvariation erhalten bleiben
		goalSet.addGoal(raters.get(3), 1f, 1f);
		
		//goalSet.addGoal(new Feature("Goal Repetitiveness", raters.get(4), 4f),0.01f);
		
		Pilot pilot = new Pilot(goalSet);
		pilot.addPoint(RaterDifferentFromBefore.class, 2, 0f, 1f, LEVEL);
		pilot.addPoint(RaterAverageSpeed.class, 2, 2f, 1f, LEVEL);
		pilot.addPoint(RaterAverageSpeed.class, 4, 4f, 1f, LINEAR);
		
		System.out.println(pilot.toString());
		
	}

}
