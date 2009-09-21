package algorithm.visualiser;

import java.util.ArrayList;

import config.Pilots;
import config.Themes;

import algorithm.composers.kaida.GoalSet;
import algorithm.composers.kaida.Individual;
import algorithm.composers.kaida.KaidaComposer;
import algorithm.composers.kaida.ValueRange;
import algorithm.mutators.CrossOver;
import algorithm.mutators.CrossOverOnePoint;
import algorithm.mutators.Mutator;
import algorithm.mutators.MutatorDoublifyAll;
import algorithm.mutators.MutatorPermutate;
import algorithm.mutators.MutatorSpeedChange;
import algorithm.pilots.GoalCurve;
import algorithm.pilots.Pilot;
import algorithm.raters.FitnessRater;
import algorithm.raters.FitnessRaterEuklid;
import algorithm.raters.Rater;
import algorithm.raters.RaterAverageSpeed;
import algorithm.raters.RaterDifferentFromBefore;
import algorithm.raters.RaterInnerRepetitiveness;
import algorithm.raters.RaterOffBeatness;
import algorithm.raters.RaterSimilarEnd;
import algorithm.raters.RaterSimilarStart;
import algorithm.raters.RaterSpeedStdDeviation;
import algorithm.selectors.ComparerByFitness;
import algorithm.selectors.EliteAndWorstSelector;
import algorithm.selectors.EliteSelector;
import algorithm.selectors.LastPlayedSelector;
import algorithm.selectors.RouletteWheelQuadSelector;
import algorithm.selectors.Selector;
import algorithm.selectors.WorstSelector;
import algorithm.statistics.TestRunGenerationStatistics;
import algorithm.tools.Timer;
import bols.BolBase;
import bols.Variation;
import bols.tals.Teental;
import static algorithm.pilots.WayPoint.CurvePointTypes.*;

public class AlgorithmPerformanceTests extends Thread{
	
	private int nrOfContests;
	private int nrOfGenerations;
	private int pause;
	private boolean pleasePause;
	private ArrayList<TestRunGenerationStatistics> results;
	private double progress;
	

	public AlgorithmPerformanceTests() {
		nrOfContests = 10;
		nrOfGenerations = 100;
		pause = 0;
		pleasePause = false;
		results = new ArrayList<TestRunGenerationStatistics>();;
	}
	
	public void run() {
		boolean wasInterrupted = false;
		while (true) {
			try {
				runTests();
			} catch (InterruptedException e) {
				System.out.println("interrupted!");
				wasInterrupted = true;
			} catch (Exception e) {
				System.out.println("some exception: " + e);
				System.exit(1);
			}
			if (!wasInterrupted) {
				//was not interrupted, the algorithm seems to have terminated well
				setPleasePause(true);
			}
			while (isPleasePause()) {
				try {
				    System.out.println("Algorithm: starting to wait()");
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e1) {
					// wait for signal to continue (notify())
					System.out.println("Algorithm: waiting ended by interruption");
				}
			}
		}
		
	
	}
 

	private ArrayList<KaidaComposer> getCrossOverPermutationTest () throws Exception{
		BolBase bolBase = new BolBase();

		ArrayList<KaidaComposer> als = new ArrayList<KaidaComposer>();
		
		Variation initialVariation = Themes.getTheme01(bolBase);
		Individual initialIndividual = new Individual(initialVariation);
				
		//Initialise 4 Algorithms
		for (int i=0; i<4; i++) {
			KaidaComposer algorithm = new KaidaComposer(bolBase, new Teental(), initialVariation, false, null);
			als.add(algorithm);
			 
//			set Raters:
			ArrayList<Rater> raters = new ArrayList<Rater>();
			
			//set Raters:
			raters = new ArrayList<Rater>();
			raters.add(new RaterAverageSpeed(bolBase));
			raters.add(new RaterSpeedStdDeviation(bolBase));
//			raters.add(new RaterSimilarEnd(bolBase));
//			raters.add(new RaterDifferentFromBefore(bolBase));
//			raters.add(new RaterInnerRepetitiveness(bolBase));
//			raters.add(new RaterSimilarStart(bolBase));
//			raters.add(new RaterOffBeatness(bolBase));
			
			//set GoalSet
			GoalSet goalSet = new GoalSet();
			goalSet.addGoal(raters.get(0), 4f,     1f, new ValueRange(0.5, 4, 0.25));
			goalSet.addGoal(raters.get(1), 0f,   0.05f, new ValueRange(0, 2, 0.25));
//			goalSet.addGoal(raters.get(2), 16f,  0f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
//			goalSet.addGoal(raters.get(3), 0f,   0f, new ValueRange(0, 1, 1.0));
//			goalSet.addGoal(raters.get(4), 2f,  0.0f, new ValueRange(0,6,0.25));
//			goalSet.addGoal(raters.get(5), 6f,  0.5f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
//			goalSet.addGoal(raters.get(6), 8f,  0.0f, new ValueRange(0,64,1));


			Pilot pilot = new Pilot(goalSet);
			pilot.setActive(false);
//			pilot.addPoint(RaterAverageSpeed.class, 1, 4f, 1f, LINEAR);
//			pilot.addPoint(RaterSpeedStdDeviation.class, 4, 1f, 1f, LINEAR);
			//pilot = Pilot.getTestPilot4();
			algorithm.setPilot(pilot);
			
			int generationsPerCycle = 100;
			algorithm.setMaxNrOfGenerationsPerCycle(generationsPerCycle);
			
			//init FitnessRater
			FitnessRater fitnessRater = new FitnessRaterEuklid(goalSet);			

			//init CrossOver
			//CrossOver crossOver = new CrossOverOnePoint(0.5f);

			//init Selectors
			ArrayList<Selector> selectors = new ArrayList<Selector>();
			selectors.add(new RouletteWheelQuadSelector(90, fitnessRater));
		    selectors.add(new EliteAndWorstSelector(2,5,  new ComparerByFitness(fitnessRater)));
		    //LastPlayedSelector lps = new LastPlayedSelector(0,  new ComparerByFitness(fitnessRater));
			//selectors.add(lps);
			//algorithm.setLastPlayedSelector(lps);
			
			if (i == 0) {
			
				algorithm.setLabel("algorithm, no permutation, no cross-over");

				
				CrossOver crossOver = new CrossOverOnePoint(0.0f);
				algorithm.setCrossOver(crossOver);	
				
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
//				mutators.add(new MutatorDoublifyAll(0.01f,0.01f));
//				mutators.add(new MutatorPermutate(1.0f, 0.01f));
				mutators.add(new MutatorSpeedChange(1.0f,0.01f));
				algorithm.setMutators(mutators);
				
			} else if (i==1) {
				algorithm.setLabel("algorithm, no permutation, cross-over 0.5");
		    
			    
			    CrossOver crossOver = new CrossOverOnePoint(0.5f);
			    algorithm.setCrossOver(crossOver);	
			    
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
//				mutators.add(new MutatorDoublifyAll(0.01f,0.01f));
//				mutators.add(new MutatorPermutate(1.0f, 0.01f));
				mutators.add(new MutatorSpeedChange(1.0f, 0.01f));
				algorithm.setMutators(mutators);
				
				//goalSet.setGoalImportance(RaterSpeedStdDeviation.class,0.25f);
			} else if (i==2) {
				algorithm.setLabel("algorithm, permutation, no cross-over");
		    
			    
			    CrossOver crossOver = new CrossOverOnePoint(0f);
			    algorithm.setCrossOver(crossOver);	
			    
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
//				mutators.add(new MutatorDoublifyAll(0.01f,0.01f));
				mutators.add(new MutatorPermutate(1.0f, 0.01f));
				mutators.add(new MutatorSpeedChange(1.0f, 0.01f));
				algorithm.setMutators(mutators);
				
			} else if (i==3) {
				algorithm.setLabel("algorithm, permutation, cross-over 0.5");		    
			    
			    CrossOver crossOver = new CrossOverOnePoint(0.5f);
			    algorithm.setCrossOver(crossOver);	
			    
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
//				mutators.add(new MutatorDoublifyAll(0.01f,0.01f));
				mutators.add(new MutatorPermutate(1.0f, 0.01f));
				mutators.add(new MutatorSpeedChange(1.0f, 0.01f));
				algorithm.setMutators(mutators);
				
			}
			
			algorithm.setFitnessRater(fitnessRater);
			algorithm.setGoalSet(goalSet);
			algorithm.setRaters(raters);
			algorithm.setSelectors(selectors);
			
			
			
		}
		return als;
	}
	

	private ArrayList<KaidaComposer> getMutationProbabilityTests () throws Exception{
		BolBase bolBase = new BolBase();

		ArrayList<KaidaComposer> als = new ArrayList<KaidaComposer>();
		
		Variation initialVariation = Themes.getTheme01(bolBase);
		Individual initialIndividual = new Individual(initialVariation);
				
		//Initialise 4 Algorithms
		for (int i=0; i<4; i++) {
			KaidaComposer algorithm = new KaidaComposer(bolBase, new Teental(), initialVariation, false, null);
			als.add(algorithm);
			 
//			set Raters:
			ArrayList<Rater> raters = new ArrayList<Rater>();
			
			//set Raters:
			raters = new ArrayList<Rater>();
			raters.add(new RaterAverageSpeed(bolBase));
			raters.add(new RaterSpeedStdDeviation(bolBase));
			raters.add(new RaterSimilarEnd(bolBase));
			raters.add(new RaterDifferentFromBefore(bolBase));
			raters.add(new RaterInnerRepetitiveness(bolBase));
			raters.add(new RaterSimilarStart(bolBase));
			raters.add(new RaterOffBeatness(bolBase));
			
			//set GoalSet
			GoalSet goalSet = new GoalSet();
			goalSet.addGoal(raters.get(0), 4f,     1f, new ValueRange(0.5, 4, 0.25));
			goalSet.addGoal(raters.get(1), 0f,   0.05f, new ValueRange(0, 2, 0.25));
			goalSet.addGoal(raters.get(2), 16f,  0f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
			goalSet.addGoal(raters.get(3), 0f,   0f, new ValueRange(0, 1, 1.0));
			goalSet.addGoal(raters.get(4), 2f,  0.0f, new ValueRange(0,6,0.25));
			goalSet.addGoal(raters.get(5), 6f,  0.5f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
			goalSet.addGoal(raters.get(6), 8f,  0.0f, new ValueRange(0,64,1));


			Pilot pilot = Pilots.getPilot01();
			pilot.setActive(true);
//			pilot.addPoint(RaterAverageSpeed.class, 1, 4f, 1f, LINEAR);
//			pilot.addPoint(RaterSpeedStdDeviation.class, 4, 1f, 1f, LINEAR);
			//pilot = Pilot.getTestPilot4();
			algorithm.setPilot(pilot);
			
			int generationsPerCycle = 50;
			algorithm.setMaxNrOfGenerationsPerCycle(generationsPerCycle);
			
			//init FitnessRater
			FitnessRater fitnessRater = new FitnessRaterEuklid(goalSet);			

			//init CrossOver
			//CrossOver crossOver = new CrossOverOnePoint(0.5f);

			//init Selectors
			ArrayList<Selector> selectors = new ArrayList<Selector>();
			selectors.add(new RouletteWheelQuadSelector(90, fitnessRater));
		    selectors.add(new EliteAndWorstSelector(2,5,  new ComparerByFitness(fitnessRater)));
		    LastPlayedSelector lps = new LastPlayedSelector(0,  new ComparerByFitness(fitnessRater));
			selectors.add(lps);
			algorithm.setLastPlayedSelector(lps);
			
			if (i == 0) {
			
				algorithm.setLabel("algorithm, all probs to 0.01");

				
				CrossOver crossOver = new CrossOverOnePoint(0.5f);
				algorithm.setCrossOver(crossOver);	
				
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
				mutators.add(new MutatorDoublifyAll(0.01f));
				mutators.add(new MutatorPermutate(1.0f, 0.01f));
				mutators.add(new MutatorSpeedChange(1.0f,0.01f));
				algorithm.setMutators(mutators);
				
			} else if (i==1) {
				algorithm.setLabel("algorithm, all probs to 0.05");

			    CrossOver crossOver = new CrossOverOnePoint(0.5f);
			    algorithm.setCrossOver(crossOver);	
			    
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
				mutators.add(new MutatorDoublifyAll(0.05f));
				mutators.add(new MutatorPermutate(1.0f, 0.05f));
				mutators.add(new MutatorSpeedChange(1.0f, 0.05f));
				algorithm.setMutators(mutators);
				
				//goalSet.setGoalImportance(RaterSpeedStdDeviation.class,0.25f);
			} else if (i==2) {
				algorithm.setLabel("algorithm, all probs to 0.1");
		    
			    
			    CrossOver crossOver = new CrossOverOnePoint(0.5f);
			    algorithm.setCrossOver(crossOver);	
			    
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
				mutators.add(new MutatorDoublifyAll(0.1f));
				mutators.add(new MutatorPermutate(1.0f, 0.1f));
				mutators.add(new MutatorSpeedChange(1.0f, 0.1f));
				algorithm.setMutators(mutators);
				
			} else if (i==3) {
				algorithm.setLabel("algorithm, all probs to 0.5");		    
			    
			    CrossOver crossOver = new CrossOverOnePoint(0.5f);
			    algorithm.setCrossOver(crossOver);	
			    
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
				mutators.add(new MutatorDoublifyAll(0.5f));
				mutators.add(new MutatorPermutate(1.0f, 0.5f));
				mutators.add(new MutatorSpeedChange(1.0f, 0.5f));
				algorithm.setMutators(mutators);
				
			}
			
			algorithm.setFitnessRater(fitnessRater);
			algorithm.setGoalSet(goalSet);
			algorithm.setRaters(raters);
			algorithm.setSelectors(selectors);
			
			
			
		}
		return als;
	}
	
	private ArrayList<KaidaComposer> getEliteAndWorstComparisonTest () throws Exception{
		BolBase bolBase = new BolBase();
		KaidaComposer al;
		KaidaComposer al2;
		ArrayList<KaidaComposer> als = new ArrayList<KaidaComposer>();
		Variation initialVariation = Themes.getTheme02(bolBase);
		Individual initialIndividual = new Individual(initialVariation);
		
		al = new KaidaComposer(bolBase, new Teental(), initialVariation, false, null);
		al2 = new KaidaComposer(bolBase, new Teental(), initialVariation, false, null);
		
		als.add(al);
		als.add(al2);		
				
		Pilot pilot;
		
		//Initialise 2 Algorithms
		int i=0;
		for (KaidaComposer algorithm : als) {
//			set Raters:
			ArrayList<Rater> raters = new ArrayList<Rater>();
			//set Raters:
			raters = new ArrayList<Rater>();
			raters.add(new RaterAverageSpeed(bolBase));
			raters.add(new RaterSpeedStdDeviation(bolBase));
			raters.add(new RaterSimilarEnd(bolBase));
			raters.add(new RaterDifferentFromBefore(bolBase));
			raters.add(new RaterInnerRepetitiveness(bolBase));
			raters.add(new RaterSimilarStart(bolBase));
			raters.add(new RaterOffBeatness(bolBase));
			
			//set GoalSet
			GoalSet goalSet = new GoalSet();
			goalSet.addGoal(raters.get(0), 1f, 1.0f, new ValueRange(0.5, 4, 0.25));
			goalSet.addGoal(raters.get(1), 0f,   0f, new ValueRange(0, 2, 0.25));
			goalSet.addGoal(raters.get(2), 16f,  0f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
			//am anfang soll die ursprungsvariation erhalten bleiben
			goalSet.addGoal(raters.get(3), 0f,   10f, new ValueRange(0, 1, 1.0));
			goalSet.addGoal(raters.get(4), 2f,  0.0f, new ValueRange(0,6,0.25));
			goalSet.addGoal(raters.get(5), 6f,  0.5f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
			goalSet.addGoal(raters.get(6), 8f,  0.0f, new ValueRange(0,64,1));


			
//			pilot = new Pilot(goalSet);
//			pilot.addPoint(RaterDifferentFromBefore.class, 1, 1f, 1f, LEVEL);
//			pilot.addPoint(RaterAverageSpeed.class, 2, 4f, 1f, LINEAR);
//			pilot.addPoint(RaterAverageSpeed.class, 4, 1f, 1f, LINEAR);
			pilot = Pilots.getPilot02();
			algorithm.setPilot(pilot);
			
			int generationsPerCycle = 20;
			algorithm.setMaxNrOfGenerationsPerCycle(generationsPerCycle);
			
			//init FitnessRater
			FitnessRater fitnessRater = new FitnessRaterEuklid(goalSet);			

			//init CrossOver
			//CrossOver crossOver = new CrossOverOnePoint(0.5f);

			ArrayList<Selector> selectors = new ArrayList<Selector>();
			if (i==0) {
				algorithm.setLabel("algorithm with elite(2) and worst(0)");
				//init Selectors
				selectors.add(new RouletteWheelQuadSelector(90, fitnessRater));
			    selectors.add(new EliteSelector(2, new ComparerByFitness(fitnessRater)));	
			    selectors.add(new WorstSelector(5, new ComparerByFitness(fitnessRater)));
			    LastPlayedSelector lps = new LastPlayedSelector(90,  new ComparerByFitness(fitnessRater));
				selectors.add(lps);
				algorithm.setLastPlayedSelector(lps);
				
				CrossOver crossOver = new CrossOverOnePoint(0.5f);
				algorithm.setCrossOver(crossOver);	
				
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
				mutators.add(new MutatorDoublifyAll(0.01f));
				mutators.add(new MutatorPermutate(1.0f, 0.01f));
				mutators.add(new MutatorSpeedChange(1.0f,0.01f));
				algorithm.setMutators(mutators);
				
				algorithm.mutatePopulation();
				
				//goalSet.setGoalImportance(RaterSpeedStdDeviation.class,0.25f);
			} else {
				algorithm.setLabel("algorithm without elite and worst");
				selectors.add(new RouletteWheelQuadSelector(90, fitnessRater));
			    //selectors.add(new EliteSelector(10, new ComparerByFitness(fitnessRater)));	
			    //selectors.add(new WorstSelector(5, new ComparerByFitness(fitnessRater)));
			    LastPlayedSelector lps = new LastPlayedSelector(90,  new ComparerByFitness(fitnessRater));
				selectors.add(lps);
				algorithm.setLastPlayedSelector(lps);			    
			    
			    CrossOver crossOver = new CrossOverOnePoint(0.5f);
			    algorithm.setCrossOver(crossOver);	
			    
				//init Mutators
				ArrayList<Mutator> mutators = new ArrayList<Mutator>();
				mutators.add(new MutatorDoublifyAll(0.01f));
				mutators.add(new MutatorPermutate(1.0f, 0.01f));
				mutators.add(new MutatorSpeedChange(1.0f, 0.01f));
				algorithm.setMutators(mutators);
				
			    algorithm.mutatePopulation();
				//goalSet.setGoalImportance(RaterSpeedStdDeviation.class,0.25f);
			}
			
			algorithm.setFitnessRater(fitnessRater);
			algorithm.setGoalSet(goalSet);
			
			algorithm.setRaters(raters);
			algorithm.setSelectors(selectors);
			
			
			//algorithm.setDEBUG(false);
			i++;
			
		}
		return als;
	}
	
	public void runTests() throws Exception {
		
//		ArrayList<KaidaComposer> als = getEliteAndWorstComparisonTest();
		ArrayList<KaidaComposer> als = getCrossOverPermutationTest();
//		ArrayList<KaidaComposer> als = getMutationProbabilityTests();
		
		synchronized (results) {
			results.clear();
			for (KaidaComposer al : als) {
				results.add(new TestRunGenerationStatistics(nrOfContests,nrOfGenerations,al.getLabel(),al));	
			}			
		}
		
		setProgress(0f);
		
		try {
			for (int runs = 0; runs < nrOfContests; runs++) {
			//run the test n times
			
				System.out.println("\nContest Nr. " + runs + " started");
				Timer t = new Timer("Contest Nr. " + runs);
				t.start();
				
				for (KaidaComposer algorithm : als) {
					algorithm.restartOrTakeOver();
				}
				
				for (int g = 0; g < nrOfGenerations; g++) {
					//do one evolution step
					
					for (KaidaComposer algorithm : als) {
						if (algorithm.isCycleCompleted()) {
							algorithm.startNextCycle();
						}
						algorithm.doOneEvolutionCycle();
					}
					
				}
				
				t.stopAndPrint();
				t.start("Adding to results");
				synchronized (results) {
					for (int z=0; z < als.size(); z++) {
						results.get(z).addTestRun(als.get(z).getGenerations().getRange(0,nrOfGenerations));
					}
				}
				t.stopAndPrint();
				t.start("Calculating statistics");
				synchronized (results) {
					for (int z=0; z < als.size(); z++) {
						results.get(z).calculateStats();
					}
				}
				t.stopAndPrint();
				
				setProgress((double)runs/(double)(nrOfContests-1));
				
				if (pause!=0) {
					sleep(pause);
				}
				
				if (isPleasePause()) break;
			}
			synchronized (results) {
				for (int z=0; z < als.size(); z++) { //als.size()
					results.get(z).calculateStats();
				}
			}
			setProgress(1.0f);
			
		
			
		} catch (Exception e) {
			e.printStackTrace();
			//fail("al threw some Exception");
			System.exit(1);
		}
		
	}
	
	public static void main(String[] args) {
		AlgorithmPerformanceTests apt = new AlgorithmPerformanceTests();
		apt.setPause(50);
		apt.start();
	}
	
	private void setPause(int i) {
		// TODO Auto-generated method stub
		pause = i;
	}

	public synchronized double getProgress() {
		return progress;
	}
	
	private synchronized void setProgress(double prog) {
		progress = prog;
	}
	
	public synchronized boolean isPleasePause() {
		return pleasePause;
	}

	public synchronized void setPleasePause(boolean pleasePause) {
		this.pleasePause = pleasePause;
	}
	public synchronized ArrayList<TestRunGenerationStatistics> getResults() {
		return results;
	}
	

	
	public int getNrOfContests() {
		return nrOfContests;
	}


	public void setNrOfContests(int nrOfContests) {
		this.nrOfContests = nrOfContests;
	}


	public int getNrOfGenerations() {
		return nrOfGenerations;
	}


	public void setNrOfGenerations(int nrOfGenerations) {
		this.nrOfGenerations = nrOfGenerations+1;
	}
	
}
