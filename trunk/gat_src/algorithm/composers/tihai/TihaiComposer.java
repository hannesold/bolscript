package algorithm.composers.tihai;

import java.util.ArrayList;

import javax.sound.midi.Track;

import managing.Command;
import algorithm.composers.Composer;
import algorithm.composers.ComposerStandard;
import algorithm.composers.kaida.Individual;
import algorithm.interpreters.TihaiInterpreter;
import algorithm.interpreters.VariationInterpreter;
import algorithm.tools.Calc;
import algorithm.tools.RouletteWheel;
import bols.BolBase;
import bols.BolSequence;
import bols.PlayingStyle;
import bols.SubSequence;
import bols.SubSequenceAdvanced;
import bols.SubSequenceAtomic;
import bols.Variation;
import bols.tals.Tal;

public class TihaiComposer extends ComposerStandard implements Composer {
	private BolBase bolBase;
	
	private Individual currentCandidate;
	private VariationInterpreter tihaiInterpreter;
	
	private Variation currentVariation;
	
	private Variation tihai;
	private SubSequence tihaiTrunc;
	private SubSequence tihaiTail;
	
	private Tal tal;
	
	private boolean focusOnBeginning;
	
	
	private static final int CONSTRAINTS_STRICT = 0;
	private static final int CONSTRAINTS_MODERATE = 1;
	private static final int CONSTRAINTS_LOOSE = 2;
	private static final int CONSTRAINTS_NONE = 3;
	private static final int[] CONSTRAINT_LEVELS = new int[] {CONSTRAINTS_STRICT, CONSTRAINTS_MODERATE, CONSTRAINTS_LOOSE, CONSTRAINTS_NONE};
	private static final String[] CONSTRAINT_NAMES = new String[] {"strict","moderate","loose", "none"};
	
	public TihaiComposer(BolBase bolBase, Tal tal, Variation variation) throws Exception {
		this(bolBase,tal,variation, null);
	}
	
	public TihaiComposer(BolBase bolBase, Tal tal, Variation variation, ThreadGroup threadGroup) throws Exception {
		super(threadGroup, "Tihai Composer");
		this.bolBase = bolBase;
		this.tal = tal;
		
		this.pauseDuration = 1000;
		this.tihaiInterpreter = new TihaiInterpreter(bolBase);
		this.currentVariation = variation;
		
		composeTihai(variation);
		this.currentCandidate = new Individual(this.tihai);
		
		this.focusOnBeginning = true;
		//this.tihai = this.composeTihai(variation);
		//this.tihaiTrunc
	}
	
	public Variation composeTihai(Variation variation) {
		TihaiStructure struct = null;
		ArrayList<TihaiStructure> candidates = getTihaiStructures(variation);
			
		try {
			struct = (TihaiStructure) getWheel(candidates).getRandom();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		tihaiTrunc = struct.subSequence;
		SubSequenceAdvanced tail = null;
				
		tail = new SubSequenceAdvanced(new BolSequence("Dha -", BolBase.getStandard()),new PlayingStyle(1f,1f), true);  
		tail.addSubSequence(0,1, new PlayingStyle(struct.speed,1.2f));
		for (int i=1; i < struct.tail; i++) {
			tail.addSubSequence(1,1,new PlayingStyle(struct.speed));
		}
		tihaiTail = (SubSequence) tail;
		
		tihai = new Variation(variation.getAsSequence());
		tihai.addSubSequence(tihaiTrunc);
		tihai.addSubSequence(tail);
		tihai.addSubSequence(tihaiTrunc);
		tihai.addSubSequence(tail);
		tihai.addSubSequence(tihaiTrunc);
		//tihai.addSubSequence(tail);
		
		return tihai;

	}
	
	private ArrayList<TihaiStructure> getTihaiStructures(Variation variation) {
		ArrayList<TihaiStructure> structs;
		int constraintIndex = 0;
		constraintIndex = CONSTRAINTS_STRICT;
		structs = getTihaiStructures(variation, CONSTRAINT_LEVELS[constraintIndex]);
		out ("Found " + structs.size() + " tihais at constraint level: " + CONSTRAINT_NAMES[constraintIndex]+"\n");
		while ((structs.size() == 0) && (constraintIndex < CONSTRAINT_LEVELS.length)) {
			int constraintLevel = CONSTRAINT_LEVELS[constraintIndex];
			structs = getTihaiStructures(variation, CONSTRAINT_LEVELS[constraintIndex]);
			out ("Found " + structs.size() + " tihais at constraint level: " + CONSTRAINT_NAMES[constraintIndex]+"\n");
			constraintIndex++;
		}
		
		return structs;
	}
	
	private ArrayList<TihaiStructure> getTihaiStructures(Variation variation, int constraintLevel) {
		
		out ("getTihaiStructures for " + variation);
		ArrayList<TihaiStructure> structs = new ArrayList<TihaiStructure>();
		
		
		ArrayList<SubSequence> subSeqsAll = variation.getSubSequencesRecursive(10);
		ArrayList<SubSequence> subSeqs = subSeqsAll;//variation.getSubSequences();
		
		//PlayingStyle style = variation.getSubSequence(0).getPlayingStyle();

		BolSequence seq = variation.getAsSequence();
		
		double speed = seq.getBol(0).getPlayingStyle().getSpeedValue();
		for (int i=1; i < seq.getLength(); i++) {
			speed = Math.max(seq.getBol(i).getSpeed(),speed);
		}
		PlayingStyle style = new PlayingStyle(speed, 1f);
		
		double oneStroke = 1.0 / style.getSpeedValue();
		double duration = variation.getDuration() * 2.0;
		int nrOfStrokesInCycle = (int) Math.round(duration * style.getSpeedValue()); 
		
		//pause should be <= than trunc
		int trunc = 1;
		int tail = 1;
		
		while ((trunc*3) <= (nrOfStrokesInCycle-2)) {
			
			int bolsLeftFromTrunc = nrOfStrokesInCycle - trunc*3;
			
			double tailduration = (double)bolsLeftFromTrunc / 2.0f;
		
			if (Math.round(tailduration) == tailduration) {
				//this will fit
				tail = (int) Math.round(tailduration);
				//out("examining " + new TihaiStructure(trunc,tail,style.speed));
				
				/**
				 * is this a valid tihai structure ?
				 */
				boolean valid = false;
				switch (constraintLevel) {
				case CONSTRAINTS_STRICT:
					valid = (tailduration <= trunc) && ((tail*oneStroke) < 4) && (tail > 1);
					break;
				case CONSTRAINTS_MODERATE:
					valid = (tailduration <= trunc) && ((tail*oneStroke) < 4);
					break;
				case CONSTRAINTS_LOOSE:
					valid = (tailduration <= trunc);
					break;
				case CONSTRAINTS_NONE:
					valid = true;
					break;
				}
				if (valid) {
					
						double truncDuration = Calc.round(oneStroke * (double)(trunc), 3);
						
						boolean matchFound = false;
						
						if (!focusOnBeginning) {
							/*
							 * subSequence parsing: find a group of subsequences, which
							 * provides the required trunc length. 
							 */
							for (int i=0; i < subSeqs.size(); i++) {
								
								if (subSeqs.get(i).getDuration() == truncDuration) {
									//the subSequence has the right duration 
									structs.add(new TihaiStructure(trunc,tail,style.getSpeedValue(), subSeqs.get(i)));
									out("added direct copy of a subseq: " + structs.get(structs.size()-1));
									matchFound = true;
									
								} else if (subSeqs.get(i).getDuration() < truncDuration) {
									//the subSequence is to short
									
									SubSequenceAdvanced subSeqNew = new SubSequenceAdvanced(new PlayingStyle(1f,1f),subSeqs.get(i));
	
									for (int j=i; j < subSeqs.size(); j++) {
										//subSeq i added with subSeq j has the right duration
										double dur = subSeqNew.getDuration() + subSeqs.get(j).getDuration();
										
										if (dur == truncDuration) {
											subSeqNew.addSubSequence(subSeqs.get(j));		
											structs.add(new TihaiStructure(trunc,tail,style.getSpeedValue(), subSeqNew));
											out("added a summed copy of subseqs: " + structs.get(structs.size()-1));
											matchFound = true;	
											break;
										} else if (dur < truncDuration){
											subSeqNew.addSubSequence(subSeqs.get(j));
	//										summed duration is still to small, continue adding
										} else {
											//the sum was to big
											matchFound = false;
											break;
										}
									}
								}
							}
						}
						if (!matchFound){
							//no matches found
							//synthesize a trunc nevertheless, even if it means cutting.
							
							double dur = 0.0;
							int i = 0;
							SubSequenceAdvanced subSeqNew = new SubSequenceAdvanced(variation.getBasicBolSequence(),new PlayingStyle(1f,1f), true);

							//requires subSeqs to be atomic!
							while (dur != truncDuration) {
								SubSequence subSeq = subSeqs.get(i);
								if ((dur + subSeq.getDuration())<= truncDuration) {
									subSeqNew.addSubSequence(subSeqs.get(i));
									dur = subSeqNew.getDuration();
									i++;
								} else {
									if (!subSeq.hasSubSequences()) {

										int len = 0;
										
										double microDur = 0.0;
										SubSequenceAdvanced subSeqPartial = new SubSequenceAdvanced(variation.getBasicBolSequence(),new PlayingStyle(1f,1f), true);
										
										int posInBasicSeq = subSeq.getStart();
										
										//go through bols
										for (int posInSubSeq = 0; posInSubSeq < subSeq.getLength();	posInSubSeq++) {
											
											microDur = 1.0 / subSeq.getAsSequence().getBol(posInSubSeq).getSpeed();
											//check if adding next bol makes it too long.
											//if not, ad it. if yes, change speed and add it as subSeq
											if ((dur + microDur) == truncDuration) {
												len++;
												out("completing at len: " + len);
												subSeqPartial.addSubSequence(posInBasicSeq,len,subSeq.getPlayingStyle());
												dur += microDur; //= subSeqNew.getDuration() + subSeqPartial.getDuration();
												break;
											} else if ((dur+microDur) < truncDuration) {
												len++;
												dur += microDur; 
												out("increasing len of partial subSeq, now " + len);
											} else if ((dur+microDur) > truncDuration) {
												subSeqPartial.addSubSequence(posInBasicSeq,len,subSeq.getPlayingStyle());
												out("filling up "+subSeqPartial);
												subSeqPartial.addSubSequence((posInBasicSeq+len),1, (double) (1.0/(truncDuration - dur)));
												out("now:" + subSeqPartial);
												out("should have had speed " + (1.0/(truncDuration - dur)));
												dur = subSeqNew.getDuration() + subSeqPartial.getDuration();
												break;
											}
										}
										
										//add partial subSeq to fill up.
										subSeqNew.addSubSequence(subSeqPartial);
										dur = subSeqNew.getDuration();
										out ("with duration: " + dur + ", goal was: " + truncDuration);
										structs.add(new TihaiStructure(trunc,tail,style.getSpeedValue(), subSeqNew));
										out("synthesized: " + structs.get(structs.size()-1));
									} else {
										//problem!!
										out("strong warning: tihai trunc could not be established properly");
									}
									
								}
								
							}
						}
						
					} else {
						//dont do it if truncDuration exceeds tailduration					
					}
				}
			
				trunc++;
			}	
			
			out("numbers of tihaiStructs established: " + structs.size());
			
			return structs;		
		}
		
		

	
	/**
	 * A simple way of setting probabilities, where moderate solutions get the highest prob.
	 * @param structs
	 * @return
	 */
	private RouletteWheel getWheel(ArrayList<TihaiStructure> structs) {
		RouletteWheel wheel = new RouletteWheel();
		
		//int halfway = structs.size() / 2;
		double prob = 1f;
		int i= 0;
		
		for (TihaiStructure struct : structs) {
			try {
				wheel.put(prob,struct);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}	
		}
		
		return wheel;
	}

	public void run() {
		while(!isInterrupted()) {
			if ((pleaseRestart)||(pleaseTakeOver)) {
				restartOrTakeOver();
			}
			while (isPleasePause()) {
				try {
				    out("TihaiComposer: starting to wait()");
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e1) {
					// wait for signal to continue (notify())
					out("Algorithm: waiting ended by interruption");
					//e1.printStackTrace();
				}
			}	
			try {
				
				if (getPleaseFillTrack()) {
					fillTrack(trackToFill,trackFillPosition);
				}
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
	}
	
	public synchronized void fillTrack(Track track, long startingAt) {

		//render variation to track
		tihaiInterpreter.renderToMidiTrack(currentCandidate.getVariation(), track, startingAt);	
					
		//update display
		mediator.addCommand(new Command(Command.AddCurrentIndividualToPlaylist, currentCandidate, tal));
		
		if (!mediator.isProcessing()) {
			mediator.interrupt();
		}
		
		out("to be played next:\n" + currentCandidate.toString());
		//add currentCandidate to history of all played
		
		synchronized (this) {
			pleaseFillTrack = false;
		}
		
		//tihai always runs only once!
		passOnToNextComposer();
		
	}

	/**
	 * Expects restartParams:
	 * [1] The variation to build tihai of
	 * [2] The tal to build tihai of
	 * @see algorithm.composers.Composer#restartOrTakeOver()
	 */
	public void restartOrTakeOver() {
		if (restartParams != null) {
			try {
				Variation var = (Variation) restartParams[1];
				Tal newTal = (Tal) restartParams[2];
				this.currentVariation = var;
				this.tal = newTal;
				composeTihai(currentVariation);
				this.currentCandidate = new Individual(this.tihai);
			} catch (Exception e) {
				//no nothing
			}
			restartParams = null;
		}
		if (takeOverParams != null) {
			try {
				Variation var = (Variation) takeOverParams[2];
				Tal newTal = (Tal) takeOverParams[3];
				this.currentVariation = var;
				this.tal = newTal;
				composeTihai(currentVariation);
				this.currentCandidate = new Individual(this.tihai);				
			} catch (Exception e) {
				//do nothing
			}
			takeOverParams = null;
		}
		pleaseTakeOver = false;
		pleaseRestart = false;
	}
	
	public void passOnToNextComposer() {
		Command cmd;
		cmd = new Command(Command.PassOnToComposer, nextComposer, this, this.tihaiTail);
		mediator.addCommand(cmd);
	}
	
	public int getPauseDuration() {
		return pauseDuration;
	}

	public void setPauseDuration(int pauseDuration) {
		this.pauseDuration = pauseDuration;
	}

	public Tal getTal() {
		// TODO Auto-generated method stub
		return tal;
	}

	public static Variation mergeTailWithTheka(SubSequence tail, Variation theka) {
//		ArrayList<Double> splitPoints = theka.getPositions();
		//double tailDur = tail.getDuration();
//		
//		Variation merged = new Variation(theka.getBasicBolSequence());
//		SubSequenceAdvanced tailSeq = new SubSequenceAdvanced(tail.getBasicBolSequence(), new PlayingStyle(1f,1f),true);
//		tailSeq.addSubSequence(tail);
//		
//		for(int i=0; i < theka.getSubSequences().size()-1; i++) {
//			double tailDur = tailSeq.getDuration();
//			
//			if (splitPoints.get(i)<tailDur) {
//				if (splitPoints.get(i+1) > tailDur) {
//					//recombine  a bit
//					while (merged.getDuration() != splitPoints.get(i+1)) {
//						//add bols...
//					}
//				} else if (splitPoints.get(i+1) == tailDur) {
//					merged.addSubSequence(tailSeq);
//					for (int j=i+1;j<theka.getSubSequences().size();j++) {
//						merged.addSubSequence(theka.getSubSequence(j));
//					}
//				} else if (splitPoints.get(i+1) < tailDur) {
//					i++;
//				}
//			}
//			
//		}
		Variation merged = new Variation(theka.getBasicBolSequence());
		
		
		if (theka.getAsSequence().getLength()==10) {
			// for jhap tal
			SubSequenceAdvanced newTail = new SubSequenceAdvanced("Dha - - - Na", BolBase.getStandard());  
			merged.addSubSequence(newTail);
			
			for (int i = 2; i < theka.getSubSequences().size(); i++) {
				merged.addSubSequence(theka.getSubSequence(i).getCopyFull());
			}
			
			
		} else {
			SubSequenceAdvanced newTail = new SubSequenceAdvanced(new BolSequence("Dha -", BolBase.getStandard()),new PlayingStyle(1f,1f), true);  
			
			newTail.addSubSequence(0,1);
			
			while (newTail.getLength() < theka.getSubSequence(0).getLength()-1) {
				newTail.addSubSequence(1,1);
			}
			newTail.addSubSequence(new SubSequenceAtomic(theka.getBasicBolSequence(),theka.getSubSequence(1).getStart()-1,1, new PlayingStyle(1f,1f)));
			
			merged.addSubSequence(newTail);	
			
			for (int i = 1; i < theka.getSubSequences().size(); i++) {
				merged.addSubSequence(theka.getSubSequence(i).getCopyFull());
			}
		}
		return merged;
	}

	public boolean isFocusOnBeginning() {
		return focusOnBeginning;
	}

	public void setFocusOnBeginning(boolean focusOnBeginning) {
		this.focusOnBeginning = focusOnBeginning;
	}

}
