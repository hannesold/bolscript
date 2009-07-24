package bolscript.compositions;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import basics.Debug;
import basics.FileReadException;
import basics.Rational;
import basics.Tools;
import bols.BolBase;
import bols.BolName;
import bols.tals.Tal;
import bolscript.Reader;
import bolscript.config.Config;
import bolscript.packets.AccessRights;
import bolscript.packets.HistoryEvent;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.sequences.RepresentableSequence;

	public class Composition implements DataStatePosessor{
		
	protected String name = null;
	protected String description = null;
	protected String compositionType = null;
	protected ArrayList<String> tals = null;
	protected ArrayList<String> types = null;
	protected ArrayList<String> gharanas = null;
	protected ArrayList<String> speeds = null;
	protected ArrayList<String> editors = null;
	protected ArrayList<String> comments = null;
	protected ArrayList<Rational> speedsR = null;
	protected Rational maxSpeed = null;
	protected ArrayList<String> keys = null;
	
	protected ArrayList<String> searchStrings = null;
	protected String completeSearchString = null;
	protected StringBuilder completeSearchBuilder = null;
	boolean searchBuilderChanged = true;
	
	protected String snippet = null;
	protected String rawData = new String("");
	protected String oldRawData = new String("");
		
	protected ArrayList<CompositionChangedListener> changeListeners;
	
	protected State dataState = State.NOT_CHECKED; //not connected, missing, connected, loaded
	
	protected String linkLocal = null;
	protected String linkServer = null;
	
	protected int id = 0; //id on the database
	
	protected ArrayList<String> geneology = null;
	protected ArrayList<HistoryEvent> transcriptionHistory = null;
	protected ArrayList<AccessRights> rights = null;	
	protected Packets packets = null;

	protected boolean istal = false;
	
	
	/**
	 * This constructs a Composition from the rawData.
	 * RawData has to be given in bolscript format.
	 * @param rawData A String in bolscript format.
	 */
	public Composition(String rawData) {
		changeListeners = new ArrayList<CompositionChangedListener>();
		this.setRawData(rawData);
		extractInfoFromRawData();
	}
	
	/**
	 * Inits a composition from a bolscript file.
	 * Uses reader to get the rawData from the file.
	 * Sets the linklocal to the files absolute path
	 * and the datastate to CONNECTED.
	 * Makes a first backup of rawData.
	 * A FileReadException is thrown if any problems occurred in Reader,
	 * or the size of the file exceeded Config.maxBolscriptFileSize.
	 * 
	 * @param file
	 * @throws FileReadException
	 */
	public Composition(File file) throws FileReadException{
		this(Reader.getContents(file, Config.maxBolscriptFileSize));
		setLinkLocal(file.getAbsolutePath());
		setDataState(State.CONNECTED);
		backUpRawData();
	}
	
	


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getTals() {
		return tals;
	}
	public void addTal(String talAsString) {
		if (tals == null) tals = new ArrayList<String>();
		if (!tals.contains(talAsString)) {
			tals.add(talAsString);
			Debug.temporary(this, Tools.toString(tals));
			Collections.sort(tals);
			Debug.temporary(this, "sorted" + Tools.toString(tals));
			
			//s.comp
		}
	}
	
	
	public ArrayList<String> getTypes() {
		return types;
	}
	public void addType(String type) {
		if (types == null) types = new ArrayList<String>();
		if (!types.contains(type)) {
			types.add(type);
			Collections.sort(types);
			if (types.size() == 1) {

				Integer typeInt = Packet.keyPacketTypes.get(((String) type).toUpperCase());
				if (typeInt != null) if ( typeInt.intValue() == Packet.TAL) {
					istal = true;
				} else {
					istal = false;
				}

			} else istal = false;
		}
	}
	
	public ArrayList<String> getGharanas() {
		return gharanas;
	}
	
	public void addGharana(String gharana) {
		if (gharanas == null) gharanas = new ArrayList<String>();
		if (!gharanas.contains(gharana)) {
			gharanas.add(gharana);
			Collections.sort(gharanas);
		}
	}
	
	public ArrayList<String> getEditors() {
		return editors;
	}
	public void addEditor(String editor) {
		if (editors == null) editors = new ArrayList<String>();
		if (!editors.contains(editor)) {
			editors.add(editor);
			Collections.sort(editors);
		}
	}
	
	public void addComment(String comment) {
		if (comments == null) comments = new ArrayList<String>();
		if (!comments.contains(comment)) {
			comments.add(comment);
			Collections.sort(comments);
		}
	}
	
	public void addKey(String key) {
		if (keys== null) keys = new ArrayList<String>();
		if (!keys.contains(key)) {
			keys.add(key);
			Collections.sort(keys);
		}
	}
	public ArrayList<String> getKeys () {
		return keys;
	}
	
	public ArrayList<String> getComments() {
		return comments;
	}
	
	public ArrayList<String> getSpeeds() {
		
		return speeds;
	}
	
	public void addSpeed(Rational speed) {
		if(speedsR == null) speedsR = new ArrayList<Rational>();
		if (!speedsR.contains(speed)) {
			speedsR.add(speed);
			Collections.sort(speedsR);
			
			speeds = new ArrayList<String>();
			for (Rational s: speedsR) {
				speeds.add(s.toString());
			}
		}	
		
		
	}
	
	public void addSearchString(String s) {
		if (completeSearchBuilder == null) completeSearchBuilder = new StringBuilder();
		completeSearchBuilder.append(s);
		completeSearchBuilder.append("; ");
		searchBuilderChanged = true;
	}
	
	public String getFulltextSearchString() {
		if (searchBuilderChanged) {
			completeSearchString = completeSearchBuilder.toString();
		} return completeSearchString;
	}
	
	public void rebuildFulltextSearch() {
		completeSearchBuilder = new StringBuilder();
		searchBuilderChanged = true;
		addSearchString(getName());
		addSearchString(Tools.toString(getGharanas()));
		addSearchString(Tools.toString(getKeys()));
		addSearchString(Tools.toString(getComments()));
		addSearchString(Tools.toString(getSpeeds()));
		addSearchString(Tools.toString(getTals()));
		addSearchString(Tools.toString(getTypes()));
		addSearchString(Tools.toString(getEditors()));
		addSearchString(getDescription());
		
		if (linkLocal != null) {
			File f = new File(linkLocal);
			if (f.getName() != null) {
				addSearchString(f.getName());
				if (f.getParentFile() != null) {
					addSearchString(f.getParentFile().getName());
					if (f.getParentFile().getParentFile() != null) {
						addSearchString(f.getParentFile().getParentFile().getName());
					}
				}
			}
		};
		
		if (packets != null) {
		//add bols to searchstring
		for (Packet p: packets) {
			if (p.getType() == Packet.BOLS) {
				if (p.getObject() != null) {
					RepresentableSequence compact = ((RepresentableSequence) p.getObject()).getCompact();
					addSearchString(compact.toString(true, false, false, false, false, false, BolName.EXACT));
					addSearchString(compact.toString(true, false, false, false, false, false, BolName.SIMPLE));
				}
			}
			
		}
		}
		
	}
	
	public void removeSpeed(Rational speed) {
		if(speedsR == null) speedsR = new ArrayList<Rational>();
		speedsR.remove(speed);
		
		speeds = new ArrayList<String>();
		for (Rational s: speedsR) {
			speeds.add(s.toString());
		}		
		
	}
	
	public State getDataState() {
		return dataState;
	}
	public void setDataState(State s) {
		Debug.debug(this, "setting state from " + dataState + " to " + s);
		this.dataState = s;
	}
	public String getLinkLocal() {
		return linkLocal;
	}
	public void setLinkLocal(String linkLocal) {
		this.linkLocal = linkLocal;
	}
	public String getLinkServer() {
		return linkServer;
	}
	public void setLinkServer(String linkServer) {
		this.linkServer = linkServer;
	}
	public ArrayList<String> getGeneology() {
		return geneology;
	}
	public void setGeneology(ArrayList<String> geneology) {
		this.geneology = geneology;
	}
	public ArrayList<HistoryEvent> getTranscriptionHistory() {
		return transcriptionHistory;
	}
	public void setTranscriptionHistory(ArrayList<HistoryEvent> transcriptionHistory) {
		this.transcriptionHistory = transcriptionHistory;
	}
	public ArrayList<AccessRights> getRights() {
		return rights;
	}
	public void setRights(ArrayList<AccessRights> rights) {
		this.rights = rights;
	}
	public int getId() {
		return id;
	}
	public boolean isTal() {
		return istal;
	}

	public void setId(int id) {
		this.id = id;
	}
	public Packets getPackets() {
		return packets;
	}
	public void setPackets(Packets packets) {
		this.packets = packets;
	}
	
	public String getSnippet() {
		if (snippet == null) snippet = new String();
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public void extractInfoFromRawData() {
		if (rawData != null) {
			this.packets = Reader.compilePacketsFromString(rawData, BolBase.standard());
			extractInfoFromPackets(this.packets);
		} else {
			Debug.critical(this, "the rawdata is empty, will not be processed");
		}
	}
	
	/**
	 * Expects the packets to be preprocessed by reader.
	 * Calls rebuildFulltextSearch and fireCompositionChanged.
	 * @param packets
	 */
	protected void extractInfoFromPackets(Packets packets) {
		name = ""; //might be set somewhere else
		// reset everything
		tals = new ArrayList<String> (); 
		compositionType = "";
		description = "";
		snippet = "";
		gharanas = new ArrayList<String>();
		speedsR = new ArrayList<Rational>();
		speeds = new ArrayList<String>();
		maxSpeed = new Rational(1);
		editors = new ArrayList<String>();
		types = new ArrayList<String>();
		//addSpeed(new Rational(1));
		comments = new ArrayList<String>();
		keys = new ArrayList<String>();
		
		Packet firstBolPacket = null;
		
		istal = false;
		for (int i=0; i < packets.size(); i++) {
			Packet p = packets.get(i);
			String key = packets.get(i).getKey();
			Object obj = packets.get(i).getObject();
			int type = packets.get(i).getType();
			if (type != Packet.FAILED) addKey(key);
			
			if (obj!=null) switch (type) {
			case Packet.NAME:
				name = (String) obj;
				break;
			case Packet.TAL:
					addTal(((Tal) obj).getName());
				break;
			case Packet.TYPE:
				Object [] types = (Object[]) obj;
				for (int j=0; j < types.length; j++) {
					addType((String) types[j]);
				}
		
				break;
			case Packet.DESCRIPTION:
					setDescription((String) obj);
				break;
				
			case Packet.GHARANA:
				Object [] ghars = (Object[]) obj;
				for (int j=0; j < ghars.length; j++) {
					addGharana((String) ghars[j]);
				}
				break;
				
			case Packet.SPEED:
				addSpeed((Rational) obj);
				//addSpeed(obj.toString());
				break;
			
			case Packet.COMMENT:
				addComment((String) obj);
				break;
			case Packet.EDITOR:
				Object [] editors = (Object[]) obj;
				for (int j=0; j < editors.length; j++) {
					addEditor((String) editors[j]);
				}
				break;
			case Packet.BOLS:
				
				RepresentableSequence compact = ((RepresentableSequence) obj).getCompact();
				maxSpeed = Rational.max(maxSpeed, compact.getMaxSpeed());
				if (snippet.equals("")) {
					if (firstBolPacket == null && p.isVisible()) firstBolPacket = p;
					if ((key.equalsIgnoreCase("Snippet") 
							|| key.equalsIgnoreCase("Theme") 
							|| key.equalsIgnoreCase("Tukra") 
							|| key.equalsIgnoreCase("Theka"))) {
						
						snippet = compact.generateSnippet();
						if (name.equals("")) name = compact.generateShortSnippet();
					}
				} 
			} //switch (type)
		} //for
		
		//after processing all packets gather missing information
		if (istal && (tals.size() == 0) && (!name.equals(""))) {
			//if this is of type tal and no tal is set, then add the tal itself.
			addTal(name);
		}
		
		if (snippet.equals("") && firstBolPacket != null ){
			if (firstBolPacket.getObject() != null) {
				RepresentableSequence compact = ((RepresentableSequence) firstBolPacket.getObject()).getCompact();
				snippet = compact.generateSnippet();
				
				if (name.equals("")) {
					name = compact.generateShortSnippet();
				}
				
			}
		} else if (name.equals("")) name = "Unnamed";
		
		removeSpeed(new Rational(1));
		
		rebuildFulltextSearch();
		fireCompositionChanged();
	}

	public String toString() {
		return compositionType + " " + this.getName();
	}
	

	

	public boolean establishRawData() {
		if (dataState == State.NEW) return true;
		
		try {
			rawData = Reader.getContents(new File(this.linkLocal), Config.maxBolscriptFileSize);
			dataState.connect(this);
			return true;
		} 
		catch (FileReadException ex) {
			Debug.debug(this, "could not establish raw data! " + ex);
			dataState.connectfailed(this);
		}
		return false;
	}
	
	public String getRawData() {
		return rawData;
	}

	/**
	 * Sets the rawData to a copy of the passed String.
	 * @param rawData
	 */
	public void setRawData(String rawData) {
		this.rawData = new String(rawData);
	}
	
	public Rational getMaxSpeed() {
		return maxSpeed;
	}
	
	public void addChangeListener(CompositionChangedListener c) {
		if (!changeListeners.contains(c)) changeListeners.add(c);
	}
	
	public void removeChangeListener(CompositionChangedListener c) {
		changeListeners.remove(c);
	}
	public void removeAllListeners() {
		changeListeners.clear();
	}
	
	private void fireCompositionChanged() {
		for (CompositionChangedListener listener: changeListeners) {
			EventQueue.invokeLater(new CompositionChangeEvent(this, listener));
		}
	}



	public void backUpRawData() {
		if (rawData != null) this.oldRawData = new String(rawData);
	}
	
	public boolean hasChangedSinceBackup() {
		return (!oldRawData.equals(rawData));
	}
	
	public void revertFromBackup() {
		setRawData(new String(oldRawData));
		extractInfoFromRawData();
		
	}
	
	/*public Object getOldRawData() {
		return oldRawData ;
	}*/

}
