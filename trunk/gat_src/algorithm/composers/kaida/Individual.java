package algorithm.composers.kaida;

import java.util.HashMap;
import java.util.ArrayList;

import algorithm.raters.Rater;
import bols.Variation;

public class Individual extends basics.Basic{
	private Variation variation;
	private ArrayList<Feature> features;
	private HashMap<Class, Feature> classMap;
	private HashMap<Rater, Feature> raterMap;
	
	private boolean rated;
	
	public Individual(Variation variation) {
		this.variation = variation;
		features = new ArrayList<Feature>();
		rated = false;
		classMap = new HashMap<Class, Feature>();
		raterMap = new HashMap<Rater, Feature>();
	}
	
	public Individual(Variation variation, ArrayList<Feature> features) {
		this.variation = variation;
		this.features = features;
		classMap = new HashMap<Class, Feature>();
		raterMap = new HashMap<Rater, Feature>();
		
		if (features.isEmpty()) {
			rated = false;
		} else {
			rated = true;
			for(Feature f : features) {
				if (f.hasGenerator()) {
					classMap.put(f.getGenerator().getClass(), f);
					raterMap.put(f.getGenerator(), f);
				}
			}
		}
		
	}

	public void addFeature(Feature feature) {
		if (getFeature(feature.getGenerator()) == null) {
			features.add(feature);
			if (feature.hasGenerator()) {
				classMap.put(feature.getGenerator().getClass(), feature);
				raterMap.put(feature.getGenerator(), feature);
			}
			rated = true;
		} else {
			out("feature " + feature.toString() + " already exists ");
		}
	}
	
	public Feature getFeature(Rater r) {
		return raterMap.get(r);
	}
	
	public Feature getFeature(Class c) {
		return classMap.get(c);
	}
	
	public ArrayList<Feature> getFeatures() {
		return features;
	}
	
	public boolean isRated() {
		return rated;
	}

	public Variation getVariation() {
		return variation;
	}	
	
	public ArrayList<Feature> getFeaturesCopy() {
		ArrayList<Feature> f = new ArrayList<Feature>();
		for (Feature feature : features) {
			f.add(feature.getCopy());
		}
		return f;
	}
	
	public Individual getCopyKeepBolSequence() {
		return new Individual(variation.getCopyKeepBolSequence(), getFeaturesCopy());
	}

	/**
	 * @return A copy with the variation beeing copied while retaining the same basicBolSequence
	 */
	public Individual getCopyKeepBolSequenceStripFeatures() {
		return new Individual(variation.getCopyKeepBolSequence(), new ArrayList<Feature>());
	}
	
	public String featuresToString() {
		String s = "";
		for (Feature feature : features) {
			s += feature;			
		}
		return s;
	}
	
	public String toString() {
		String s = "";
		s += variation.toString() + featuresToString();
		return s;
	}
	
}
