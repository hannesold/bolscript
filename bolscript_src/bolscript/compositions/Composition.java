package bolscript.compositions;

import static bolscript.packets.types.PacketTypeFactory.COMMENT;
import static bolscript.packets.types.PacketTypeFactory.COMPOSER;
import static bolscript.packets.types.PacketTypeFactory.EDITOR;
import static bolscript.packets.types.PacketTypeFactory.GHARANA;
import static bolscript.packets.types.PacketTypeFactory.KEYS;
import static bolscript.packets.types.PacketTypeFactory.NAME;
import static bolscript.packets.types.PacketTypeFactory.SNIPPET;
import static bolscript.packets.types.PacketTypeFactory.SOURCE;
import static bolscript.packets.types.PacketTypeFactory.SPEED;
import static bolscript.packets.types.PacketTypeFactory.TAL;
import static bolscript.packets.types.PacketTypeFactory.TYPE;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import basics.Debug;
import basics.FileReadException;
import basics.Rational;
import bols.BolBase;
import bols.BolName;
import bols.tals.Tal;
import bolscript.Reader;
import bolscript.config.Config;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.packets.types.PacketType.ParseMode;
import bolscript.packets.types.PacketType.StorageType;
import bolscript.sequences.RepresentableSequence;

public class Composition implements DataStatePosessor{

	protected ArrayList<Rational> speedsR = null;
	protected Rational maxSpeed = null;

	protected ArrayList<String> searchStrings = null;

	protected String completeSearchString = null;
	protected StringBuilder completeSearchBuilder = null;
	boolean searchBuilderChanged = true;

	protected String rawData = new String("");
	protected String oldRawData = new String("");

	protected ArrayList<CompositionChangedListener> changeListeners;

	protected State dataState = State.NOT_CHECKED; //not connected, missing, connected, loaded

	protected String linkLocal = null;
	protected String linkServer = null;

	protected int id = 0; //id on the database

	protected Packets packets = null;


	protected boolean istal = false;

	/**
	 * This is where all uncomplicated meta-data is stored.
	 */
	protected MetaValues metaValues;

	/**
	 * This constructs a Composition from the rawData.
	 * RawData has to be given in bolscript format.
	 * @param rawData A String in bolscript format.
	 */
	public Composition(String rawData) {
		changeListeners = new ArrayList<CompositionChangedListener>();
		this.setRawData(rawData);
		metaValues = new MetaValues();
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

	public MetaValues getMetaValues() {
		return metaValues;
	}

	public String getName() {
		return metaValues.getString(NAME);
	}
	
	
	public ArrayList<String> getTals() {
		return metaValues.getList(TAL);
	}

	public ArrayList<String> getTypes() {
		return metaValues.getList(TYPE);
	}

	public ArrayList<String> getGharanas() {
		return metaValues.getList(GHARANA);
	}

	public ArrayList<String> getSources() {
		return metaValues.getList(SOURCE);
	}

	public ArrayList<String> getComposers() {
		return metaValues.getList(COMPOSER);
	}

	public ArrayList<String> getEditors() {
		return metaValues.getList(EDITOR);
	}

	public ArrayList<String> getKeys () {
		return metaValues.getList(KEYS);
	}

	public ArrayList<String> getComments() {
		return metaValues.getList(COMMENT);
	}

	public ArrayList<String> getSpeeds() {
		return metaValues.getList(SPEED);
	}

	public void addSpeed(Rational speed) {
		if(speedsR == null) speedsR = new ArrayList<Rational>();
		if (!speedsR.contains(speed)) {
			speedsR.add(speed);
			Collections.sort(speedsR);
		}	
		updateSpeedStringsFromRationals();
	}

	public void updateSpeedStringsFromRationals () {
		ArrayList<String> speeds = new ArrayList<String>();
		for (Rational s: speedsR) {
			speeds.add(s.toString());
		}	
		metaValues.setList(SPEED, speeds);
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

		for (int i=0; i < PacketTypeFactory.nrOfTypes; i++) {
			if (PacketTypeFactory.getType(i).isSearchable()) {
				addSearchString(metaValues.makeString(i));
			}
		}

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
				if (p.getType() == PacketTypeFactory.BOLS) {
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
		updateSpeedStringsFromRationals();	
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
		return metaValues.getString(SNIPPET);
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

		// reset everything
		metaValues.setDefault();
		speedsR = new ArrayList<Rational>();
		maxSpeed = new Rational(1);

		Packet firstBolPacket = null;

		for (int i=0; i < packets.size(); i++) {
			Packet p = packets.get(i);

			String key = packets.get(i).getKey();
			Object obj = packets.get(i).getObject();
			PacketType packetType = packets.get(i).getPType();
			int type = packetType.getId();

			if (type != PacketTypeFactory.FAILED) metaValues.addString(KEYS, p.getKey());

			if (obj!=null) {

				switch (type) {

				case PacketTypeFactory.TAL:
					metaValues.addString(TAL, ((Tal) obj).getName());
					break;

				case PacketTypeFactory.SPEED:
					addSpeed((Rational) obj);
					break;

				case PacketTypeFactory.BOLS:		
					RepresentableSequence compact = ((RepresentableSequence) obj).getCompact();
					maxSpeed = Rational.max(maxSpeed, compact.getMaxSpeed());
					if (metaValues.getString(SNIPPET).equals("")) {
						if (firstBolPacket == null && p.isVisible()) firstBolPacket = p;
						if ((key.equalsIgnoreCase("Snippet") 
								|| key.equalsIgnoreCase("Theme") 
								|| key.equalsIgnoreCase("Tukra") 
								|| key.equalsIgnoreCase("Theka"))) {

							metaValues.setString(SNIPPET,compact.generateSnippet());
							if (metaValues.getString(NAME).equals("")) metaValues.setString(NAME, compact.generateShortSnippet());
						}
					} 
					break;

				default:
					if (packetType.getStorageType() == StorageType.STRINGLIST) {
						if (packetType.getParseMode() == ParseMode.COMMASEPERATED) {
							Object [] entries = (Object[]) obj;
							for (int j=0; j < entries.length; j++) {
								metaValues.addString(packetType.getId(), (String) entries[j]);
							}		
						} else if (packetType.getParseMode() == ParseMode.STRING) {
							metaValues.addString(packetType.getId(), (String) obj);
						}
					} else if (packetType.getStorageType() == StorageType.STRING) {
						metaValues.setString(packetType.getId(), (String) obj);
					} 
				} //switch (type)
			} //obj != null
		} //for

		//check if it is a tal
		ArrayList<String> types =  metaValues.getList(TYPE);
		if (types.size() == 1) {
			//check if the entry has the value Tal, Tals or Tala or so
			PacketType associatedType = PacketTypeFactory.getType(types.get(0).toUpperCase());
			if (associatedType != null) if ( associatedType.getId() == TAL) {
				istal = true;
			} else {
				istal = false;
			}
		} else istal = false;

		ArrayList<String> tals = metaValues.getList(PacketTypeFactory.TAL);

		String name = metaValues.getString(NAME);

		//after processing all packets gather missing information
		if (istal && (metaValues.getList(TAL).size() == 0) && (!name.equals(""))) {
			//if this is of type tal and no tal is set, then add the tal itself.
			metaValues.addString(PacketTypeFactory.TAL, name);

		}

		if (metaValues.getString(SNIPPET).equals("") && firstBolPacket != null ){
			if (firstBolPacket.getObject() != null) {
				RepresentableSequence compact = ((RepresentableSequence) firstBolPacket.getObject()).getCompact();
				metaValues.setString(SNIPPET,compact.generateSnippet());

				if (name.equals("")) {
					metaValues.setString(NAME, compact.generateShortSnippet());
				}

			}
		} else if (name.equals("")) metaValues.setString(NAME, "Unnamed");

		removeSpeed(new Rational(1));

		rebuildFulltextSearch();
		fireCompositionChanged();
	}

	public String toString() {
		return metaValues.getList(TYPE) + " " + metaValues.getString(NAME);
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
