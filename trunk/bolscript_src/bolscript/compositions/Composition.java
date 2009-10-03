package bolscript.compositions;



import static bolscript.packets.types.PacketTypeFactory.KEYS;
import static bolscript.packets.types.PacketTypeFactory.NAME;
import static bolscript.packets.types.PacketTypeFactory.SNIPPET;
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
import bols.BolName;
import bols.tals.Tal;
import bols.tals.TalBase;
import bolscript.Master;
import bolscript.FileManager;
import bolscript.config.Config;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeFactory;
import bolscript.packets.types.PacketType.ParseMode;
import bolscript.packets.types.PacketType.StorageType;
import bolscript.scanner.Parser;
import bolscript.sequences.RepresentableSequence;
import bolscript.sequences.SpeedUnit;

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

	protected boolean isTal = false;
	
	/**
	 * Used for retrieving tals
	 */
	protected TalBase talBase = null;
	
	/**
	 * If the composition has type TAL
	 * it will get a talInfo object, which implements Tal.
	 */
	protected TalInfo talInfo = null;

	/**
	 * This is where all uncomplicated meta-data is stored.
	 */
	protected MetaValues metaValues;

	/**
	 * This constructs a Composition from the rawData.
	 * RawData has to be given in bolscript format.
	 * @param rawData A String in bolscript format.
	 */
	public Composition(String rawData, TalBase talBase) {
		changeListeners = new ArrayList<CompositionChangedListener>();
		this.talBase = talBase;
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
	public Composition(File file, TalBase talBase) throws FileReadException{
		this(FileManager.getContents(file, Config.maxBolscriptFileSize, Config.compositionEncoding), talBase);
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
						addSearchString(compact.toString(RepresentableSequence.SHOW_ALL, BolName.EXACT));
						addSearchString(compact.toString(RepresentableSequence.SHOW_ALL, BolName.SIMPLE));
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
		return isTal;
	}
	
	/**
	 * Returns the talInfo object, which implements Tal,
	 * under the condition that it has been established (in extractInfoFromPackets)
	 */
	public Tal getTalInfo() {
		if (isTal) {
			return talInfo;
		} else return null;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	public Packets getPackets() {
		return packets;
	}

	public void extractInfoFromRawData() {
		if (rawData != null) {
			this.packets = Parser.compilePacketsFromString(rawData);
			extractInfoFromPackets(this.packets);
		} else {
			Debug.critical(this, "the rawdata is empty, will not be processed");
		}
	}

	/**
	 * This extracts the metaData from the packets.
	 * Expects the packets to be preprocessed by Parser.
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
					String talName = (String) obj;
					if (Master.master != null) {
						Tal tal = talBase.getTalFromName(talName);
						if (tal != null) {
							talName = tal.getName(); //unified apearence for registered tals!
						}
					}
					metaValues.addString(TAL, talName);
					break;

				case PacketTypeFactory.SPEED:
					addSpeed((Rational) obj);
					break;

				case PacketTypeFactory.BOLS:		
					RepresentableSequence flat = ((RepresentableSequence) obj).flatten(SpeedUnit.getDefaultSpeedUnit());
					
					maxSpeed = Rational.max(maxSpeed, flat.getMaxSpeed());
					if (metaValues.getString(SNIPPET).equals("")) {
						if (firstBolPacket == null && p.isVisible()) firstBolPacket = p;
						if ((key.equalsIgnoreCase("Snippet") 
								|| key.equalsIgnoreCase("Theme") 
								|| key.equalsIgnoreCase("Tukra") 
								|| key.equalsIgnoreCase("Theka"))) {

							metaValues.setString(SNIPPET,flat.generateSnippet());
							if (metaValues.getString(NAME).equals("")) {
								metaValues.setString(NAME, flat.generateShortSnippet());
							}
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
				
				if (talInfo == null) talInfo = new TalInfo(this);
				
				//attempt to extract additional tal relevant info from the packets and store it in talInfo
				boolean isValidTal = talInfo.extractTalInfoFromPackets(packets);
				//if it worked this composition may call itself tal
				isTal = isValidTal;
				
			} else {
				isTal = false;
			}
		} else isTal = false;
		
		
		String name = metaValues.getString(NAME);

		//after processing all packets gather missing information
		if (isTal && (metaValues.getList(TAL).size() == 0) && (!name.equals(""))) {
			//if this is of type tal and no tal is set, then add the tal itself.
			metaValues.addString(PacketTypeFactory.TAL, name);
		}
		
		if (metaValues.getString(SNIPPET).equals("") && firstBolPacket != null ){
			if (firstBolPacket.getObject() != null) {
				RepresentableSequence compact = ((RepresentableSequence) firstBolPacket.getObject()).flatten(SpeedUnit.getDefaultSpeedUnit()); //the speed unit does not influence the appearance of the snippet!
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
			rawData = FileManager.getContents(new File(this.linkLocal), Config.maxBolscriptFileSize, Config.compositionEncoding);
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

}
