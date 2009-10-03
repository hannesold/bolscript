package gui.bols;


import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import algorithm.composers.kaida.Individual;
import algorithm.mutators.CrossOverOnePoint;
import algorithm.mutators.MutatorDoublifyAll;
import algorithm.mutators.MutatorPermutate;
import algorithm.mutators.MutatorSpeedChange;
import basics.Debug;
import basics.FileManager;
import basics.GUI;
import bols.BolBase;
import bols.BolName;
import bols.BolSequence;
import bols.Variation;
import bols.tals.Jhaptal;
import bols.tals.Tal;
import bols.tals.Teental;
import bolscript.config.Config;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.sequences.RepresentableSequence;
import config.Themes;

public class VariationDrawTester extends JFrame implements WindowListener{

	private static final long serialVersionUID = 4811914925372441514L;
	
	public VariationDrawTester(String title, Dimension size, boolean tightSizes) throws Exception {
		super(title + ", " + GUI.dimensionToString(size) + ", " + ((tightSizes)?"fixed maxspeed":"not fixed"));
		JPanel content = new JPanel();
		BoxLayout bx = new BoxLayout(content,BoxLayout.Y_AXIS);
		JScrollPane scrollPane = new JScrollPane(content);
		
		content.setLayout(bx);
		this.setContentPane(scrollPane);
		
		addWindowListener(this);
		BolBase bb = new BolBase();
		this.setSize(size);
		
		//VariationPanel vp = new VariationPanel(new Teental(bb).getTheka(), this.getSize());
		ArrayList<Variation> vars = new ArrayList<Variation>(20);
		ArrayList<Tal> tals = new ArrayList<Tal>(20);
		
		BolSequence seq1 = Themes.getTheme01(bb).getBasicBolSequence().getCopy();
		Variation var = new Variation(seq1);
		var.addSubSequence(0,4,4f);
		var.addSubSequence(0,4,2d);
		var.addSubSequence(0,8,3d);
		
	//Packets packets = Reader.compilePacketsFromFile(config.Config.pathToCompositions + "test.tabla.txt", bb);
		
		Tal tt = new Teental();
/*
		for (Packet p : packets) {
			content.add(Box.createRigidArea(new Dimension(10,20)));
			if ((p.getType()==Packet.BOLS) && (p.isVisible())) {
				if (tightSizes) {
					content.add(new VariationPanel((RepresentableSequence) p.getObject(), tt, this.getSize()));
				} else {
					content.add(new VariationPanel((RepresentableSequence) p.getObject(), tt, this.getSize(),0,"",0, bols.BolName.SIMPLE, Config.bolFontSizeStd[BolName.SIMPLE]));
				}
			}
		}*/
		
		vars.add(var);
		tals.add(new Teental());
		
		vars.add(Variation.fromTal(new Teental()));
		tals.add(new Teental());
		
		vars.add(Variation.fromTal(new Jhaptal()));
		tals.add(new Jhaptal());
		
		
		var = Variation.fromTal(tt);
		Debug.out("tt.getThasVar: " + var.toStringCompact());
		vars.add(var);
		tals.add(tt);
		
		//System.out.println(tt.getThekaAsVariation().toString());
		
		var = new Variation(var.getAsSequence());
		Debug.out(var);
		Debug.out(var.getAsSequence());
		
		var.addSubSequence(0,4,0.5f);
		var.addSubSequence(8,4,2f);
		var.addSubSequence(8,4,2f);
		var.addSubSequence(12,4,2f);
		var.addSubSequence(12,4,2f);
		vars.add(var);
		tals.add(tt);
		
		VariationItem[] variationItems = Themes.getKaidaThemesAsMenuItems(bb);
		for (VariationItem vi : variationItems) {
			vars.add(vi.variation);
			tals.add(vi.tal);
		}
		
		//documentation of mutators
		
		//doublify all
		var = new Variation("Dha Ti Te, Dha Ti Te, Dha Dha", bb);
		vars.add(var);
		tals.add(tt);
		
		var = new Variation("Dha Ti Te, Dha Ti Te, Dha Dha", bb);
		
		Individual in1 = new Individual(var);
		
		new MutatorDoublifyAll(1f).mutate(in1);
		vars.add(var);
		tals.add(tt);
		
		//speed change
		var = new Variation("Dha Ti Te, Dha Ti Te, Dha Dha", bb);
		in1 = new Individual(var);
		new MutatorSpeedChange(1f,0.3f).mutate(in1);
		vars.add(var);
		tals.add(tt);
		
		//permutation
		var = new Variation("Dha Ti Te, Dha Ti Te, Dha Dha", bb);
		in1 = new Individual(var);
		new MutatorPermutate(1f,0.3f).mutate(in1);
		vars.add(var);
		tals.add(tt);
		
		//crossover
		BolSequence seq = new BolSequence ("Dha Ti Te Dha Ti Te Dha Dha", bb);
		var = new Variation(seq);
		var.addSubSequence(0,3,2);
		var.addSubSequence(0,3,2);
		var.addSubSequence(3,3,2);
		var.addSubSequence(3,3,2);
		var.addSubSequence(6,2,1);
		in1 = new Individual(var);
		vars.add(var);
		tals.add(tt);

		var = new Variation(seq);
		var.addSubSequence(0,3,1);
		var.addSubSequence(3,3,1);
		var.addSubSequence(6,2,2);
		var.addSubSequence(6,2,2);
		vars.add(var);
		tals.add(tt);
		
		Individual in2 = new Individual(var);
		Individual in3 = in1.getCopyKeepBolSequence();
		new CrossOverOnePoint(1f).crossOver(in2, in3);
		vars.add(in2.getVariation());
		tals.add(tt);
		vars.add(in3.getVariation());
		tals.add(tt);
		
		seq = new BolSequence("Ti Ri Ke Te Dha -",bb);
		var = new Variation(seq);
		var.addSubSequence(0,4,4f);
		var.addSubSequence(4,2,4f);
		var.addSubSequence(0,4,4f);
		var.addSubSequence(4,2,4f);
		var.addSubSequence(0,4,4f);
		var.addSubSequence(4,2,4f);
		vars.add(var);
		tals.add(tt);
		
		seq = new BolSequence("Dhin Na Dhin Dhin Dha Ge Ti Ri Ke Te Ta Ke", bb);
		var = new Variation(seq);
		var.addSubSequence(0,2,1f);
		var.addSubSequence(4,8,4f);
		vars.add(var);
		tals.add(tt);
		
		
		
		//var.addSubSequence(new SubSequence(seq1,0,4,new PlayingStyle(1f,1f)));
		for (int i=0; i < vars.size(); i++) {
			content.add(Box.createRigidArea(new Dimension(10,20)));
			if (tightSizes) {
				content.add(new VariationPanel(vars.get(i), tals.get(i), this.getSize()));
			} else {
				content.add(new VariationPanel(vars.get(i), tals.get(i), this.getSize(),0,"",0));
			}
			
		}
		
		this.setVisible(true);

		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			VariationDrawTester vdt = new VariationDrawTester("Variation drawTest", new Dimension(800,800), false);
			vdt = new VariationDrawTester("Variation drawTest", new Dimension(250,800), false);
			//vdt = new VariationDrawTester("Variation drawTest", new Dimension(800,800), true);
		//	vdt = new VariationDrawTester("Variation drawTest", new Dimension(350,800), false);
		//	vdt = new VariationDrawTester("Variation drawTest", new Dimension(350,800), true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}



	public void windowClosing(WindowEvent e) {
		System.exit( 0 );	
	}
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

}
