package bolscript.compositions;

import java.util.ArrayList;

import bols.tals.Tal;
import bolscript.packets.AccessRights;
import bolscript.packets.HistoryEvent;

public interface CompositionGeneral {
	
	/**
	 * @return A name if it is set.
	 */
	public String getName();
	public void setName(String name);
	
	/**
	 * 
	 * @return A short description.
	 */
	public String getDescription();
	public void setDescription(String description);
	
	/**
	 * @return The Compositiontype e.g. Kaida, Tukra, Tal or others.
	 */
	public String getCompositionType();
	public void setCompositionType(String compositionType);
	
	/**
	 * @return A list of the involved tals.
	 */
	public ArrayList<String> getTals();
	public void addTal(String tal);
	
	/**
	 * The Gharana as a String
	 * @return
	 */
	public ArrayList<String> getGharanas();
	public void addGharana(String gharana);
	
	/**
	 * The state is one of the states that are given in CompositionInfo
	 * NOT_CHECKED, CONNECTED, etc.
	 * @return the datastate
	 */
	public int getDataState();
	public void setDataState(int dataState);
	
	/**
	 * @return the local filename
	 */
	public String getLinkLocal() ;
	public void setLinkLocal(String linkLocal);
	
	public String getLinkServer();
	public void setLinkServer(String linkServer);
	
	/**
	 * A list of names of people involved in the composition process and in passing the composition.
	 * @return The Geneology
	 */
	public ArrayList<String> getGeneology();
	public void setGeneology(ArrayList<String> geneology);
	
	/**
	 * 
	 * @return The list of history events
	 */
	public ArrayList<HistoryEvent> getTranscriptionHistory();
	public void setTranscriptionHistory(ArrayList<HistoryEvent> transcriptionHistory);
	
	/**
	 * @return The list of access rights
	 */
	public ArrayList<AccessRights> getRights();
	public void setRights(ArrayList<AccessRights> rights);
	
	/**
	 * @return The Id in the Database
	 */
	public int getId();
	public void setId(int id);
}

