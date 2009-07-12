package algorithm.composers.kaida;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;

import javax.sound.midi.Track;

import config.Pilots;

import managing.Command;
import algorithm.composers.Composer;
import algorithm.composers.ComposerStandard;
import algorithm.interpreters.KaidaInterpreter;
import algorithm.interpreters.VariationInterpreter;
import algorithm.mutators.CrossOver;
import algorithm.mutators.CrossOverOnePoint;
import algorithm.mutators.Mutator;
import algorithm.mutators.MutatorDoublifyAll;
import algorithm.mutators.MutatorPermutate;
import algorithm.mutators.MutatorSpeedChange;
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
import algorithm.selectors.LastPlayedSelector;
import algorithm.selectors.RouletteWheelQuadSelector;
import algorithm.selectors.Selector;
import algorithm.statistics.FitnessStats;
import algorithm.tools.RouletteWheel;
import algorithm.tools.Timer;
import bols.BolBase;
import bols.Variation;
import bols.tals.Tal;

public class KaidaComposer extends ComposerStandard implements EventListener, Composer {
	

	//bol stuff and individuals
	private BolBase bolBase;
	private Tal tal;
	private Individual initialIndividual;
	private Individual currentCandidate;
	private ArrayList<Individual> individualsPlayed;
	
	private Generations generations;
	
	private ArrayList<Rater> raters;
	private ArrayList<Mutator> mutators;
	private ArrayList<Selector> selectors;
	private Selector lastPlayedSelector;
	private CrossOver crossOver;

	private FitnessRater fitnessRater;
	private GoalSet goalSet;
	private Pilot pilot;
	
	private VariationInterpreter interpreter;

	private int initialPopulationSize;
	private int generationsEvolvedInCurrentCycle;
	private int maxNrOfGenerationsPerCycle;
	
	private boolean cycleCompleted;
	private int currentCycleNr;
	private long callCounter;
	
	private boolean keepClearingGenerations;
	
	private Timer timer1, timer2;
	
	public KaidaComposer (BolBase bolBase, Tal tal, Variation variation) throws Exception {
		this(bolBase, tal, variation, true, null);
	}	
	
	public KaidaComposer (BolBase bolBase, Tal tal, Variation variation, boolean initByStandards, ThreadGroup threadGroup) throws Exception {
		super(threadGroup, "Kaida Composer");
	
		//copy params
		this.bolBase = bolBase;		
		this.initialIndividual = new Individual(variation);
		this.tal = tal;
		this.mediator = null;
		
		interpreter = new KaidaInterpreter(bolBase);
		
		//init semi constants
		initialPopulationSize = 50;//maxIndividuals has to be dividable by 2!!
		pauseDuration = 1000;		
		maxNrOfGenerationsPerCycle = 15;	
		keepClearingGenerations = true;
		
		//init further 
		initGenerations();
			
		timer1 = new Timer("Algorithm Timer");
		timer1.mute();
		timer2 = new Timer("doing selection");
		timer2.mute();
		
		if (initByStandards){
			initEvolutionParams();
		} else {
			pilot = new Pilot();
		}
		initRunningVariables();	

	}
	
	private void initRunningVariables() {
		//running params
		individualsPlayed = new ArrayList<Individual>(1000);
		callCounter = 0;
		currentCycleNr = 0;
		generationsEvolvedInCurrentCycle = 0;
		cycleCompleted = false;
		currentCandidate = initialIndividual;
		pilot.setPosition(0);
	}

	/*
	 * Init evolution params by default values
	 */ 
	private void initEvolutionParams() {

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
		goalSet = new GoalSet();
		goalSet.addGoal(raters.get(0), 1f, 1.0f, new ValueRange(0.5, 8, 0.25));
		goalSet.addGoal(raters.get(1), 0f,   0f, new ValueRange(0, 2, 0.25));
		goalSet.addGoal(raters.get(2), 16f,  0f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
		//am anfang soll die ursprungsvariation erhalten bleiben
		goalSet.addGoal(raters.get(3), 0f,   10f, new ValueRange(0, 1, 1.0));
		goalSet.addGoal(raters.get(4), 2f,  0.0f, new ValueRange(0,6,0.25));
		goalSet.addGoal(raters.get(5), 6f,  0.5f, new ValueRange(0, (double) initialIndividual.getVariation().getAsSequence().getLength(), 1.0));
		goalSet.addGoal(raters.get(6), 8f,  0.0f, new ValueRange(0,128,1));
		
		//set Pilot
		pilot = Pilots.getPilot01();

		//init FitnessRater
		fitnessRater = new FitnessRaterEuklid(goalSet);
		
		//init Mutators
		mutators = new ArrayList<Mutator>();
		mutators.add(new MutatorDoublifyAll(0.01f));		
		mutators.add(new MutatorSpeedChange(1.0f, 0.01f));
		mutators.add(new MutatorPermutate(1.0f, 0.01f));
		
		//init CrossOver
		crossOver = new CrossOverOnePoint(0.5f);

		//init Selectors
		selectors = new ArrayList<Selector>();
		Selector rouletteWheelSelector = new RouletteWheelQuadSelector(90, fitnessRater);
		Selector eliteAndWorstSelector = new EliteAndWorstSelector(2, 5, new ComparerByFitness(fitnessRater));
		//Selector worstSelector = new WorstSelector(5, new ComparerByFitness(fitnessRater));
		lastPlayedSelector = new LastPlayedSelector(40, new ComparerByFitness(fitnessRater));
		//lastPlayedSelector.
		selectors.add(rouletteWheelSelector);
		selectors.add(eliteAndWorstSelector);
		//selectors.add(worstSelector);
		selectors.add(lastPlayedSelector);
	}


	
	private void initGenerations () {
		generations = new Generations();
		Generation initialGeneration = new Generation("Initial generation");
		generations.add(initialGeneration);
			
		for (int i=0; i < initialPopulationSize; i++) {
			initialGeneration.add(initialIndividual.getCopyKeepBolSequence());
		}
		mutatePopulation();
	}
	
	public void mutatePopulation() {
		Generation population = generations.lastElement();
		
		RouletteWheel wheel = new RouletteWheel();
		try {
			wheel.put(1.0f, new MutatorSpeedChange(1.0f,0.5f));
			wheel.put(0.5f, new MutatorSpeedChange(1.0f,1.0f));
			wheel.put(0.5f, new MutatorPermutate(1.0f,1.0f));
			wheel.put(0.1f, new MutatorDoublifyAll(1.0f));
			
			for (int i=0; i<population.size()-10;i++) {
				((Mutator)wheel.getRandom()).mutate(population.get(i));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}
	

	
	public synchronized void fillTrack(Track track, long startingAt) {

		//render variation to track
		interpreter.renderToMidiTrack(currentCandidate.getVariation(), track, startingAt);
				
		startNextCycle();

		//update display
		mediator.addCommand(new Command(Command.AddCurrentIndividualToPlaylist, currentCandidate, tal));
		
		if (!mediator.isProcessing()) mediator.interrupt();
		
		out("to be played next:\n" + currentCandidate.toString());
		//add currentCandidate to history of all played
		
		synchronized (this) {
			pleaseFillTrack = false;
		}
	}
	
	public void startNextCycle() {
		System.out.println("ending cycle nr." + currentCycleNr + ", generation nr: " + getGenerationNr());
		individualsPlayed.add(currentCandidate);
		resetGenerationCountInCurrentCycle();
		cycleCompleted = false;
		pleasePrepareFilling = false;
		currentCycleNr++;
		
		if (pilot.isActive()) {
			pilot.moveOn();
			pilot.updateGoalSet(goalSet);
		}
		
		if (mediator != null) {
			mediator.addCommand(new Command(Command.CycleCompleted,currentCycleNr));
			
			//pass on, if pilot is finished
			if (pilot.isActive() && pilot.isFinishedYet()) {
				mediator.addCommand(new Command(Command.PassOnToComposer, getNextComposer(), this, getLastPlayed().getVariation(), tal));
			}
			
			if (!mediator.isProcessing()) mediator.interrupt();
		}
		
		if (lastPlayedSelector!=null) lastPlayedSelector.setEnabled(true);
	}
	
	public void run() {
		out("Algorithm.run() called the " + callCounter + ". time");
		callCounter++;

		while(!isInterrupted()) {
			while (isPleasePause()) {
				try {
				    out("Algorithm: starting to wait()");
					synchronized (this) {
						System.out.println("wait!");
						this.wait();
					}
				} catch (InterruptedException e1) {
					// wait for signal to continue (notify())
					System.out.println("Algorithm: waiting ended by interruption" + e1.getCause());
					//e1.printStackTrace();
				}
			}	
			out("Algorithm: isPleasePause==false");
			try {
				if (getPleaseFillTrack()) {
					fillTrack(trackToFill,trackFillPosition);
				}
				doOneEvolutionCycle();
				if (pauseDuration!=0) {
					
					sleep(pauseDuration);
				}
			}
			catch (NullPointerException e) {
				System.out.println("caught NullPointerException");
				e.printStackTrace();
				System.exit(1);
				setPleasePause(true);
			}
			catch (InterruptedException e) {
				//let go on...
			}
			catch (Exception e) {
				out("evolution threw some kind of exception: " + e);
				e.printStackTrace();
				setPleasePause(true);
			}
		}
		System.out.println("Algorithm stopped running");
		
	}

	public void doOneEvolutionCycle() throws Exception {
		
		if ((pleaseRestart)||(pleaseTakeOver)) {
			restartOrTakeOver();
		}
		if ((!cycleCompleted)&&(pleasePrepareFilling)) {
			//one evolutionary cycle:
			//rate all individuals
			out("\nEvolutionary cycle started. Rating " + getCurrentGeneration());
			Generation population = getCurrentGeneration();
			Individual theBest = population.get(0);
			
			double fitnessSum = 0;

			
			timer1.start("rating individuals");
			if(individualsPlayed.size()==0) {
				individualsPlayed.add(initialIndividual);
				System.out.println("WARNING: no individuals played yet, adding initial");
			} else {
				//System.out.println(individualsPlayed.size() + " individuals played yet");
			}
			for (Individual individual : population) {
				
				for (Rater rater : raters) {
					if (rater.getType()==Rater.SIMPLE) {
						rater.rate(individual);
					} else {
						//out("COMPARATIVE raters not yet implemented in Algorithm!");
						//System.out.println("individualsPlayed.size = " + individualsPlayed.size())
						
						rater.rate(individual, individualsPlayed);
					}
				}
				//rate fitness of all individuals (by comparing to goalset via FitnessRater)
				Feature fitness = fitnessRater.rate(individual);
				
				//choose one as best if it has greatest fitness 
				//better done with sorting all afterwards?
				if (fitness.value > theBest.getFeature(fitnessRater).value) {
					theBest = individual;				
				}
				//build running sum of generation Fitness
				//System.out.println("adding fitness of " + fitness.getValue());
				fitnessSum += fitness.value;
			}	
			
			timer1.stopAndPrint();
			timer1.start("calculating stats");
			
			//population.setSummedFitness(fitnessSum);
			population.setTheFittest(theBest);
			population.setRated(true);
			population.calculateStats(fitnessRater);
			
			//System.out.println("The Fittest: " + population.getTheFittest().featuresToString());
			out("Summed Fitness: " + population.getStat(FitnessStats.SUMMEDFITNESS));
			timer1.stopAndPrint();
	

			timer2.start();
			//introduce next Generation
			Generation nextGeneration = new Generation("Generation " + generations.size());
			
			//------------------------------------
			for (Selector selector: selectors) {
				if (selector.isEnabled()) {
					try {
						
						
					ArrayList<Individual> children = selector.selectChildren(population);
					
					if ((selector.wantsFollowingCrossOver()) && (crossOver.isEnabled())) {
						timer1.start("doing cross over");
						////do crossOver (maybe)
						for (Iterator i = children.iterator(); i.hasNext();) {
							Individual child1 = (Individual) i.next();
							if (i.hasNext()) {
								Individual child2 = (Individual) i.next();
								if (Math.random() < crossOver.getProbability()) {
									crossOver.crossOver(child1, child2);
								}
							}	
						}
						timer1.stopAndPrint();
					}
					
					
					if (selector.wantsFollowingMutation()) {	
						timer1.start("doing mutation");
						for (Individual individual : children) {
							//randomize by mutation probabilities
							//apply mutations
							for (Mutator mutator : mutators) {
								if (mutator.isEnabled()) {
									if (Math.random() < mutator.getProbability()) mutator.mutate(individual);
								}
							}
						}	
						timer1.stopAndPrint();
					}
					nextGeneration.addAll(children);
					
					} catch (Exception e) {
						
					}
				}
			}

			//-------------------------------------
			timer2.stopAndPrint();

			timer1.start("adding generation");
			//activate new generation
			synchronized (generations) {
				generations.add(nextGeneration);
			}
			timer1.stopAndPrint();
			//activate winner as setCurrentCandidate
			timer1.start("setting candidate");
			setCurrentCandidate(theBest);
			timer1.stopAndPrint();
			
			
			generationsEvolvedInCurrentCycle++;
			
//			clean up a bit
			if (keepClearingGenerations) {
				population.clear();	
			}
			
			if (mediator!=null) {
				mediator.addCommand(new Command(Command.OneEvolutionStepDone,this));
				if (!mediator.isProcessing()) mediator.interrupt();
			}
		}
		
		if (generationsEvolvedInCurrentCycle >= maxNrOfGenerationsPerCycle) {
			cycleCompleted = true;
			setPleasePause(true);
		} else {
			cycleCompleted = false;
		}
		
		
	}
	
	
	//////////////////////
	// GETTERS AND SETTERS
	
	public void resetGenerationCountInCurrentCycle() {
		// TODO Auto-generated method stub
		generationsEvolvedInCurrentCycle=0;
	}

	public synchronized void setCurrentCandidate(Individual indy) {
		currentCandidate = indy;
	}
	
	public synchronized Individual getCurrentCandidate() {
		return currentCandidate;
	}
	
	public Generation getCurrentGeneration () throws NullPointerException {
		if (generations.size() > 0 ){
			return generations.lastElement();
		} else {
			throw new NullPointerException("no Generation available! Is everything initialised? ");
		}
	}

	public Generations getGenerations() {
		return generations;
	}
	
	public int getGenerationNr() {
		if (generations != null) {
			return generations.size();
		} else return 0;
	}

	public double getCrossOverProb() {
		return crossOver.getProbability();
	}

	public void setCrossOverProb(double crossOverProb) {
		crossOver.setProbability(crossOverProb);
	}

	public Generation getLastGeneration() {
		if (generations.size() > 1 ){
			return generations.get(generations.size()-2);
		} else {
			throw new NullPointerException("no Generation available! Is everything initialised? ");
		}
	}
	

	public CrossOver getCrossOver() {
		return crossOver;
	}

	public FitnessRater getFitnessRater() {
		return fitnessRater;
	}

	public GoalSet getGoalSet() {
		return goalSet;
	}

	public ArrayList<Mutator> getMutators() {
		return mutators;
	}

	public ArrayList<Rater> getRaters() {
		return raters;
	}

	public ArrayList<Selector> getSelectors() {
		return selectors;
	}
	

	public void setRaters (ArrayList<Rater> raters) {
		this.raters = raters;
	}
	public void setFitnessRater(FitnessRater fr){
		this.fitnessRater = fr;		
	}
	public void setCrossOver(CrossOver crossOver) {
		this.crossOver = crossOver;
	}	 
	public void setGoalSet(GoalSet gs) {
		this.goalSet = gs;
		fitnessRater.setGoalSet(gs);
	}
	public void setMutators(ArrayList<Mutator> ms) {
		this.mutators = ms;
	}
	public void setSelectors(ArrayList<Selector> ss) {
		this.selectors = ss;
	}

	public synchronized int getMaxNrOfGenerationsPerCycle() {
		return maxNrOfGenerationsPerCycle;
	}

	public synchronized void setMaxNrOfGenerationsPerCycle(
			int maxNrOfGenerationsPerCycle) {
		this.maxNrOfGenerationsPerCycle = maxNrOfGenerationsPerCycle;
	}

	protected synchronized int getGenerationsEvolvedInCurrentCycle() {
		return generationsEvolvedInCurrentCycle;
	}

	protected synchronized void setGenerationsEvolvedInCurrentCycle(
			int generationsEvolvedInCurrentCycle) {
		this.generationsEvolvedInCurrentCycle = generationsEvolvedInCurrentCycle;
	}

	public synchronized boolean isCycleCompleted() {
		return cycleCompleted;
	}

	public synchronized Pilot getPilot() {
		return pilot;
	}

	public synchronized void setPilot(Pilot pilot) {
		this.pilot = pilot;
	}

	protected synchronized Selector getLastPlayedSelector() {
		return lastPlayedSelector;
	}

	public synchronized void setLastPlayedSelector(Selector lastPlayedSelector) {
		this.lastPlayedSelector = lastPlayedSelector;
	}
	
	protected boolean isKeepClearingGenerations() {
		return keepClearingGenerations;
	}

	protected void setKeepClearingGenerations(
			boolean keepClearingGenerations) {
		this.keepClearingGenerations = keepClearingGenerations;
	}
	
	public int getPauseDuration() {
		return pauseDuration;
	}

	public void setPauseDuration(int pauseDuration) {
		this.pauseDuration = pauseDuration;
	}

	@Override
	public void restartOrTakeOver() {	
		Individual in;
		Tal t;
		
		if (restartParams != null) {
			try {
				in = new Individual((Variation) restartParams[1]);
				t = (Tal) restartParams[2];
				this.initialIndividual = in;
				this.tal = t;				
			} catch (Exception e) {
				//leave it!
				System.out.println("restarting did not really work!");
			}
			restartParams = null;
		}  
		if (takeOverParams != null) {
			//still unhandled - not much to handle, is there ? 
			takeOverParams = null;
		}
		
		initGenerations();
		initRunningVariables();	
		
		if (pilot!=null) {
			pilot.updateGoalSet(goalSet,0);
		}
		
		pleaseTakeOver = false;
		pleaseRestart = false;
	}
	
	public void restart(Variation var) {
		this.initialIndividual = new Individual(var);
		restartOrTakeOver();
	}
	
	//public void takeOver()
	
	public void passOnToNextComposer() {
		Command cmd;
		try {
			cmd = new Command(Command.PassOnToComposer, nextComposer, this, this.individualsPlayed.get(individualsPlayed.size()-1));
		} catch (IndexOutOfBoundsException e) {
			cmd = new Command(Command.PassOnToComposer, nextComposer, this, initialIndividual.getVariation());
		}
		mediator.addCommand(cmd);
	}

	public Variation getInitialVariation() {
		return initialIndividual.getVariation();
	}

	public Tal getTal() {
		return tal;
	}

	public Individual getLastPlayed() {
		// TODO Auto-generated method stub
		return individualsPlayed.get(individualsPlayed.size()-1);
	}

	public int getEvolutionProgress() {
		return  Math.round(100*generationsEvolvedInCurrentCycle / maxNrOfGenerationsPerCycle);
	}

}
