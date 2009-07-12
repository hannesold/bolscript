package algorithm.tools;

public class Timer {
	long startTime, stopTime,duration;
	String label;
	private boolean muted;
	
	public Timer() {
		startTime = System.currentTimeMillis();
		label = "Timer";
		muted = false;
	}
	
	public Timer(String s) {
		label = s;
		muted = false;
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	public void start(String s) {
		label = s;
		startTime = System.currentTimeMillis();
	}
	public void stop() {
		stopTime = System.currentTimeMillis();
		duration = stopTime - startTime;
	}
	public void stopAndPrint() {
		stopTime = System.currentTimeMillis();
		duration = stopTime - startTime;
		print();		
	}
	
	public long getDuration() {
		return duration;
	}
	
	public void print(String s) {
		if (!muted) {
			System.out.println("Timer: " + s + " took " + duration + "ms");
		}
	}
	
	public void print() {
		if (!muted) {
			System.out.println(label + " took " + duration + "ms");
		}
	}

	public void mute() {
		// TODO Auto-generated method stub
		muted = true;
	}
	public void unMute() {
		muted = false;
	}
}
