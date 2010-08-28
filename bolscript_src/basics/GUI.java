package basics;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.event.TextListener;
import java.awt.event.WindowListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;

/**
 * This class bundles some tools usefull for GUI related tasks.
 * @author hannes
 *
 */
public class GUI {
	
	public static void setNativeLookAndFeel()
	{
	  try {
		UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName() );
	  } catch( Exception e ) { e.printStackTrace(); }
	}
	
	public static void setCrossPlattformLookAndFeel()
	{
	  try {
	    UIManager.setLookAndFeel(
	    		UIManager.getCrossPlatformLookAndFeelClassName());
	  } catch( Exception e ) { e.printStackTrace(); }
	}
	
	public static void printDimensions(String name, JComponent comp) {
		System.out.println("--------------\n"+name + ", " + comp.getName());
		Dimension dim = comp.getSize();
		if (dim != null) {
			System.out.println("size: " + dim);
		}
		dim = comp.getPreferredSize();
		if (dim != null) {
			System.out.println("pref: " + dim);
		}
		dim = comp.getMinimumSize();
		if (dim != null) {
			System.out.println("min: " + dim);
		}
		 dim = comp.getMaximumSize();
		if (dim != null) {
			System.out.println("max: " + dim);
		} 
	}
	
	public static Point topRight(Component c) {
		return new Point(getRight(c), getTop(c));
	}

	
	public static int getTop(Component c) {
		return c.getBounds().y;		
	}
	
	public static int getRight(Component c) {
		return c.getBounds().x + c.getBounds().width;
	}
	
	public static int getBottom(Component c) {
		return c.getBounds().y + c.getBounds().height;
	}
	
	public static int getLeft(Component c) {
		return c.getBounds().x;
	}
	
	public static Dimension addPreferredHeights(JComponent a, JComponent b) {
		return new Dimension(a.getPreferredSize().width,
				a.getPreferredSize().height+b.getPreferredSize().height);
	}
	
	public static Dimension addPreferredHeights(JComponent ... c) {
		Dimension d = new Dimension(c[0].getPreferredSize().width,0);
		for (int i=0; i < c.length; i++) {
			d.width = Math.max(c[i].getPreferredSize().width, d.width);
			d.height += c[i].getPreferredSize().height;
		}
		return d;
	}
	
	public static Dimension addPreferredWidths(JComponent ... c) {
		Dimension d = new Dimension(0,c[0].getPreferredSize().height);
		for (int i=0; i < c.length; i++) {
			d.width += c[i].getPreferredSize().width;
			d.height = Math.max(c[i].getPreferredSize().height, d.height);
		}
		return d;
	}

	public static Dimension addPreferredHeights(Dimension a, JComponent b) {
		return new Dimension(a.width,
				a.height+b.getPreferredSize().height);
	}

	
	public static Dimension dimDiff(Dimension a, Dimension b) {
		return new Dimension(b.width-a.width,b.height-a.height);
	}
	public static Dimension addDim(Container a, Dimension b) {
		Dimension aa = a.getPreferredSize();
		aa.width += b.width;
		aa.height += b.height;
		return aa;
	}

	public static ActionListener proxyActionListener(Object implementor, String methodname) {
		//vorher: static ActionListener proxyActionListener(JComponent target, Object implementor, String methodname) { 
		return (ActionListener) (GenericListener.create(ActionListener.class,
				"actionPerformed", implementor, methodname));
	}

	public static MouseListener proxyClickListener(Object implementor, String methodname) {
		return (MouseListener) (GenericListener.create(MouseListener.class,
				"mouseClicked", implementor, methodname));
	}
	
	public static ChangeListener proxyChangeListener(Object implementor, String methodname) {
		return (ChangeListener) (GenericListener.create(ChangeListener.class,
				"stateChanged", implementor, methodname));
	}


	public static TextListener proxyTextListener(Object implementor, String methodname) {
		return (TextListener) (GenericListener.create(TextListener.class,
				"textValueChanged", implementor, methodname));

	}
	
	public static WindowListener proxyWindowCloseListener(Object implementor,
			String methodname) {
		return (WindowListener) (GenericListener.create(WindowListener.class,
				"windowClosing", implementor, methodname));

	}
	
	public static ComponentListener proxyComponentResizedListener(Object implementor,
			String methodname) {
		return (ComponentListener) (GenericListener.create(ComponentListener.class,
				"componentResized", implementor, methodname));

	}
	
	public static String dimensionToString(Dimension size) {
		return "("+size.width +", "+size.height+")";
	}

	public static void setAllSizes(Component component, Dimension size) {
		component.setMaximumSize(new Dimension(size));
		component.setPreferredSize(new Dimension(size));
		component.setSize(new Dimension(size));
	}
	
	public static Dimension getPrefferedSize(JLabel label, int max) {
		Dimension d = label.getPreferredSize();
		int i=2;
		while ((d.height>=max)&&(i<10)) {
			Debug.debug(GUI.class, "BAD LABEL, HEIGHT:" + d.height);
			d = label.getPreferredSize();	
			Debug.debug(GUI.class, i+"th try -> " + d.height);
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				
			}
			i++;	
		}
		return d;
		
	}

	public static void init() {
		/*MultiLineToolTipUI.setMaximumWidth(250);
		MultiLineToolTipUI.initialize();*/
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(20000);		
	}	
	






}
