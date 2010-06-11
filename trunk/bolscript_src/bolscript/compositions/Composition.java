package bolscript.compositions;



import static bolscript.packets.types.PacketTypeDefinitions.KEYS;
import static bolscript.packets.types.PacketTypeDefinitions.NAME;
import static bolscript.packets.types.PacketTypeDefinitions.SNIPPET;
import static bolscript.packets.types.PacketTypeDefinitions.SPEED;
import static bolscript.packets.types.PacketTypeDefinitions.TAL;
import static bolscript.packets.types.PacketTypeDefinitions.TYPE;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import basics.Debug;
import basics.FileManager;
import basics.FileReadException;
import basics.Rational;
import bols.BolName;
import bols.tals.Tal;
import bols.tals.TalBase;
import bolscript.Master;
import bolscript.config.Config;
import bolscript.config.UserConfig;
import bolscript.packets.HistoryEntries;
import bolscript.packets.Packet;
import bolscript.packets.Packets;
import bolscript.packets.types.HistoryEntry;
import bolscript.packets.types.HistoryOperationType;
import bolscript.packets.types.PacketType;
import bolscript.packets.types.PacketTypeDefinitions;
import bolscript.packets.types.PacketType.ParseMode;
import bolscript.packets.types.PacketType.StorageType;
import bolscript.scanner.Parser;
import bolscript.sequences.RepresentableSequence;
import bolscript.sequences.SpeedUnit;

public class Composition implements DataStatePosessor{

	protected ArrayList<Rational> speedsR = null;
	protected Rational maxSpeedInVisibleSequences = null;

	protected ArrayList<String> searchStrings = null;

	protected String completeSearchString = null;
	protected StringBuilder completeSearchBuilder = null;
	boolean searchBuilderChanged = true;

	protected ArrayList<CompositionChangedListener> changeListeners;

	protected DataState dataState = DataState.NOT_CHECKED; //not connected, missing, connected, loaded

	protected String linkLocal = null;
	protected String linkServer = null;
	
	protected HistoryEntries history = null;

	protected int id = 0; //id on the database

	/**
	 * The editable (editable in the composition text editor) part of the raw bolscript data
	 */
	protected String editableRawData = new String("");

	/**
	 * A backup of the editable part of the raw bolscript data
	 */
	protected String oldEditableRawData = new String("");
	
	/**
	 * The packets that are subject to (text-) editing
	 */
	protected Packets editorPackets = null;
	
	/**
	 * Tha packets that are not subject to the text editor
	 */
	protected Packets nonEditorPackets = null;
	
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
		initFromCompleteRawData(rawData);
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
		
		String completeRawData = FileManager.getContents(file, Config.maxBolscriptFileSize, Config.compositionEncoding);
		setLinkLocal(file.getAbsolutePath());
		changeListeners = new ArrayList<CompositionChangedListener>();
		this.talBase = talBase;
		
		initFromCompleteRawData(completeRawData);				
		setDataState(DataState.CONNECTED);
		backUpEditableRawData();
	}
	
	private void initFromCompleteRawData(String rawData) {		
		metaValues = new MetaValues();		
		this.editorPackets = Parser.compilePacketsFromString(rawData);		
		processNonEditablePacketsAndBuildEditablePortionOfRawData(rawData);		
		extractInfoFromPackets(editorPackets);
		
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

	/**
	 * Returns a String containing all searchable content.
	 */
	public String getFulltextSearchString() {
		if (searchBuilderChanged) {
			completeSearchString = completeSearchBuilder.toString();
		} return completeSearchString;
	}

	/**
	 * Rebuilds the searchable string, which is used for fulltext searching of this composition.
	 * - Adds all metaValues, whos type is tagges searchable, by appending the result of their makeString method.
	 * - Adds the local link (filename)
	 * - Adds all bol sequences from bol packets in simple and exact script
	 */
	public void rebuildFulltextSearch() {
		completeSearchBuilder = new StringBuilder();
		searchBuilderChanged = true;

		// add metaValues
		for (int i=0; i < PacketTypeDefinitions.nrOfTypes; i++) {
			if (PacketTypeDefinitions.getType(i).isSearchable()) {
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

		if (editorPackets != null) {
			//add bols to searchstring
			for (Packet p: editorPackets) {
				if (p.getType() == PacketTypeDefinitions.BOLS) {
					
					if (p.getObject() != null) {
						RepresentableSequence r = (RepresentableSequence) p.getObject();
						r = r.flatten(SpeedUnit.getDefaultSpeedUnit());						
						addSearchString(r.toString(RepresentableSequence.FOR_SEARCH_STRING, BolName.EXACT));
						addSearchString(r.toString(RepresentableSequence.FOR_SEARCH_STRING, BolName.SIMPLE));
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

	public DataState getDataState() {
		return dataState;
	}
	public void setDataState(DataState s) {
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
	
	
	public Packets getEditorPackets() {
		return editorPackets;
	}

	
	public void extractInfoFromEditableRawData() {
		if (editableRawData != null) {
			if (this.editorPackets == null) {
				this.editorPackets = Parser.compilePacketsFromString(editableRawData);
			} else {
				this.editorPackets = Parser.updatePacketsFromString(editorPackets, editableRawData);
			}
			extractInfoFromPackets(this.editorPackets);
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
		maxSpeedInVisibleSequences = new Rational(1);
		//HistoryEntries newlyParsedHistory = null;
		
		Packet firstBolPacket = null;

		for (int i=0; i < packets.size(); i++) {
			Packet p = packets.get(i);

			String key = packets.get(i).getKey();
			Object obj = packets.get(i).getObject();
			PacketType packetType = packets.get(i).getPType();
			int type = packetType.getId();

			if (type != PacketTypeDefinitions.FAILED) metaValues.addString(KEYS, p.getKey());

			if (obj!=null) {

				switch (type) {

				case PacketTypeDefinitions.TAL:
					String talName = (String) obj;
					if (Master.master != null) {
						Tal tal = talBase.getTalFromName(talName);
						if (tal != null) {
							talName = tal.getName(); //unified apearence for registered tals!
						}
					}
					metaValues.addString(TAL, talName);
					break;

				case PacketTypeDefinitions.SPEED:
					addSpeed((Rational) obj);
					break;

				case PacketTypeDefinitions.BOLS:	
					RepresentableSequence seq = ((RepresentableSequence) obj);
					RepresentableSequence flat = seq.flatten();
					
					if (p.isVisible()) maxSpeedInVisibleSequences = Rational.max(maxSpeedInVisibleSequences, flat.getMaxSpeed());
					
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
				/*case PacketTypeFactory.HISTORY:
					if (newlyParsedHistory == null) {
						newlyParsedHistory = (HistoryEntries) p.getObject();
					} else {
						//newlyParsedHistory = newlyParsedHistory.merge((HistoryEntries) p.getObject());
						//there is only supposed to be one history packet!	
					}
					break;*/
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
			PacketType associatedType = PacketTypeDefinitions.getType(types.get(0).toUpperCase());
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
			metaValues.addString(PacketTypeDefinitions.TAL, name);
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

		populateHistory();
		if (history != null) {
			metaValues.setString(PacketTypeDefinitions.CREATED, history.getCreationDateAsString());
			metaValues.setString(PacketTypeDefinitions.LAST_MODIFIED, history.getModifiedDateAsString());
		}

		rebuildFulltextSearch();
		fireCompositionChanged();
	}

	protected void processNonEditablePacketsAndBuildEditablePortionOfRawData(String completeRawData) {
		String editorRawData = new String (completeRawData);
		
		int textReferenceShift = 0;
		nonEditorPackets = new Packets();
		history = null;
		
		for (int i = 0; i < editorPackets.size(); i++) {
			Packet p = editorPackets.get(i);
			
			if (p.hasTextReferences()) {
				p.getTextReference().move(-textReferenceShift);
				p.getTextRefKey().move(-textReferenceShift);
				p.getTextRefValue().move(-textReferenceShift);
			}
			if (!p.getPType().displayForEditingInTextEditor()) {
				Debug.temporary(this, "found noneditable packet: " + p );
				
				//add to non-editable packets
				nonEditorPackets.add(p);
				
				if (p.hasTextReferences()) {
					
					int cutBegin = p.getTextReference().start();
					int cutEnd 	 = p.getTextReference().end();
					
					if (i < editorPackets.size()-1) {
						
						if (editorPackets.get(i+1).hasTextReferences()) {
							cutEnd = editorPackets.get(i+1).getTextReference().start();
						}
						
						// move textReferences of upcoming packets
						textReferenceShift += cutEnd - cutBegin;
						//remove from rawData.
						editorRawData = editorRawData.substring(0, Math.max(0,p.getTextReference().start())) + editorRawData.substring(cutEnd);
						
					} else {
						//remove from rawData
						editorRawData = editorRawData.substring(0, Math.max(0,p.getTextReference().start()));
					}
					
					Debug.temporary(this, "removed from rawData:\n" + p.getValue());
					
				}
				switch (p.getType()) {
					case PacketTypeDefinitions.HISTORY:
					history = (HistoryEntries) p.getObject();
					break;

					default:
					//no others yet to be treated
				}
				
				
			} 
		}
		
		editorPackets.removeAll(nonEditorPackets);
		this.setEditableRawData(editorRawData);
	}
	
	/**
	 * Returns the first entered 'Editor', or null if there are none.
	 */
	public String getEnteredUser() {
		ArrayList<String> users = metaValues.getList(PacketTypeDefinitions.EDITOR);
		if (users !=null) {
			if (users.size()>0) {
				return users.get(0);
			}
		}
		return null;
	}
	
	/**
	 * Expects processNonEditablePacketsAndBuildEditablePortionOfRawData
	 * and extractInfoFromPackets to have been run.
	 */
	protected void populateHistory() {
		Debug.temporary(this,"populateHistory");
		if (history == null) {
			Debug.temporary(this,"history == null");
			history = new HistoryEntries();
			
			PacketType historyType = PacketTypeDefinitions.getType(PacketTypeDefinitions.HISTORY);
			Packet historyPacket = new Packet(historyType.getKeys()[0], "", historyType.getId(), false);
			historyPacket.setObject(history);
			
			nonEditorPackets.add(historyPacket);
			Debug.temporary(this,"adding history packet, size now " + nonEditorPackets.size());
		}
		
		if (history.size() == 0) {

			if (linkLocal != null) {
				//if the composition comes from a file and has a valid lastModified date, this will be noted as the creation date.
				
				File file = new File(linkLocal);
				if (file.lastModified() != 0L) {
					String user = getEnteredUser();
					if (user == null) {
						user = UserConfig.getUserId();
					}
					history.add(new HistoryEntry(new Date(file.lastModified()), HistoryOperationType.CREATED, user));
				}
			} else {
				String user = getEnteredUser();
				if (user == null) {
					user = UserConfig.getUserId();
				}
				history.add(new HistoryEntry(Calendar.getInstance().getTime(), HistoryOperationType.CREATED, user));				
			}
		}		
	}
	
	public HistoryEntries getHistory() {
		return history;
	}

	public String toString() {
		return metaValues.getList(TYPE) + " " + metaValues.getString(NAME);
	}

	public boolean establishRawData() {
		if (dataState == DataState.NEW) return true;

		try {
			String rawData = FileManager.getContents(new File(this.linkLocal), Config.maxBolscriptFileSize, Config.compositionEncoding);
			initFromCompleteRawData(rawData);
			dataState.connect(this);
			return true;
		} 
		catch (FileReadException ex) {
			Debug.debug(this, "could not establish raw data! " + ex);
			dataState.connectfailed(this);
		}
		return false;
	}

	/**
	 * Returns the editable portion of the data as a string.
	 * @return
	 */
	public String getEditableRawData() {
		return editableRawData;
	}
	
	/**
	 * Returns the complete Data for storage in a file.
	 * This is composed of: <ul>
	 * <li>the noneditable packets</li>
	 * <li>the rawData, which represents the editor content</li>
	 * </ul>
	 */
	public String getCompleteDataForStoring() {
		Debug.temporary(this, "getCompleteDataForStoring, nonEdPack.size: " + nonEditorPackets.size());
		StringBuilder builder = new StringBuilder();
		for (Packet p: nonEditorPackets) {
			builder.append(p.formatForBolscript());	
			Debug.temporary(this, "appending: " + p);
			Debug.temporary(this, "formatted:\n" + p.formatForBolscript());
		}
		builder.append(editableRawData);
		return builder.toString();
	}

	/**
	 * Sets the editable portion of the raw data to a copy of the passed String.
	 * @param rawData
	 */
	public void setEditableRawData(String rawData) {
		this.editableRawData = new String(rawData);
	}

	public Rational getMaxSpeed() {
		return maxSpeedInVisibleSequences;
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

	public void backUpEditableRawData() {
		if (editableRawData != null) this.oldEditableRawData = new String(editableRawData);
	}

	public boolean hasChangedSinceBackup() {
		return (!oldEditableRawData.equals(editableRawData));
	}

	public void revertFromBackup() {
		setEditableRawData(new String(oldEditableRawData));
		extractInfoFromEditableRawData();

	}

}
