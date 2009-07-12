    package midi;//edu.uiuc.cs397rhc.util;
    
    //code is from edu.uiuc.cs397rhc.util;
  
    import java.io.File;
    import java.util.Collections;
    import java.util.Comparator;
    import java.util.LinkedList;
    import java.util.List;
    import javax.sound.midi.MidiEvent;
    import javax.sound.midi.MidiSystem;
    import javax.sound.midi.ShortMessage;
    import javax.sound.midi.Track;
    import static javax.sound.midi.ShortMessage.*;
    
    /**
     * Simple app that prints out all the events in a midi file, in order.  
     *
     * @author Pedro DeRose
     */
    public class PrintEventList
    {
       /**
        * Prints out all MIDI events in a given MIDI file.
        *
        * @param args the first element contains the name of the file to examine
        */
       public static void main(String[] args)
       {
          if(args.length<1) System.out.println("Usage: java PrintEventList <midiFile>");
          else {
             try {
                File input=new File(args[0]);
    
                List<MidiEvent> events=new LinkedList<MidiEvent>();
                for(Track track : MidiSystem.getSequence(input).getTracks())
                   for(int i=0;i<track.size();++i) events.add(track.get(i));
    
                Collections.sort(events,new Comparator<MidiEvent>() {
                   public int compare(MidiEvent e1,MidiEvent e2) { return((int)(e1.getTick()-e2.getTick())); }
                   public boolean equals(MidiEvent e1,MidiEvent e2) { return(e1.getTick()==e2.getTick()); }
                });
    
                for(MidiEvent event : events) printEvent(event);
             } catch(Exception e) { e.printStackTrace(); }
          }
       }
    
       /**
        * Prints a MIDI event in a nice format. For a {@code ShortMessage}, the
        * tick, command name, channel, and two data are printed. For other
        * events, just the tick and message are printed.
        *
        * @param event the event to print out
        */
       public static void printEvent(MidiEvent event)
       {
          if(event.getMessage() instanceof ShortMessage)
          {
             ShortMessage msg=(ShortMessage)event.getMessage();
             String out=event.getTick()+":\t";
             switch(msg.getCommand()) {
                case NOTE_ON:               out+="NOTE_ON "; break;
                case ACTIVE_SENSING:        out+="ACTIVE_SENSING "; break;
                case CHANNEL_PRESSURE:      out+="CHANNEL_PRESSURE "; break;
                case CONTINUE:              out+="CONTINUE "; break;
                case CONTROL_CHANGE:        out+="CONTROL_CHANGE "; break;
                case END_OF_EXCLUSIVE:      out+="END_OF_EXCLUSIVE "; break;
                case MIDI_TIME_CODE:        out+="MIDI_TIME_CODE "; break;
                case NOTE_OFF:              out+="NOTE_OFF "; break;
                case PITCH_BEND:            out+="PITCH_BEND "; break;
                case POLY_PRESSURE:         out+="POLY_PRESSURE "; break;
                case PROGRAM_CHANGE:        out+="PROGRAM_CHANGE "; break;
                case SONG_POSITION_POINTER: out+="SONG_POSITION_POINTER "; break;
                case SONG_SELECT:           out+="SONG_SELECT "; break;
                case START:                 out+="START "; break;
                case STOP:                  out+="STOP "; break;
                case SYSTEM_RESET:          out+="SYSTEM_RESET "; break;
                case TIMING_CLOCK:          out+="TIMING_CLOCK "; break;
                case TUNE_REQUEST:          out+="TUNE_REQUEST "; break;
             }
    
             assert out!=null;
    
             out+="\tchannel: "+msg.getChannel();
             out+="\tdata1: "+msg.getData1();
             out+="\tdata2: "+msg.getData2();
   
            System.out.println(out);
         }
         else System.out.println(event.getTick()+":\t"+event.getMessage());
      }
   }