package bolscript.compositions;

import java.util.HashMap;

public enum DataState implements DataStateTransition{
	NEW {
		public int id() { return 0;}
		public void close(DataStatePosessor d) {
			d.setDataState(DELETED);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(ERROR);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(NEW);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(EDITING);
		}

		public void connectfailed(DataStatePosessor d) {
			d.setDataState(MISSING);
		}
		
	},NOT_CHECKED {
		public int id() { return 1;}
		public void close(DataStatePosessor d) {
			d.setDataState(NOT_CHECKED);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(NOT_CHECKED);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(NOT_CHECKED);
		}
		public void connectfailed(DataStatePosessor d) {
			d.setDataState(MISSING);
			
		}
	},MISSING {
		public int id() { return 2;}
		public void close(DataStatePosessor d) {
			d.setDataState(MISSING);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(NOT_CHECKED);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(NOT_CHECKED);
		}	
		public void connectfailed(DataStatePosessor d) {
			d.setDataState(MISSING);
			
		}
	},
	EDITING {
		public int id() { return 3;}
		public void close(DataStatePosessor d) {
			d.setDataState(CONNECTED);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(EDITING);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(EDITING);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(EDITING);
		}
		public void connectfailed(DataStatePosessor d) {
			d.setDataState(NEW);
			
		}
		
	},
	CONNECTED {
		public int id() { return 4;}
		public void close(DataStatePosessor d) {
			d.setDataState(CONNECTED);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(EDITING);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}
		public void connectfailed(DataStatePosessor d) {
			d.setDataState(MISSING);
			
		}
		
	},ERROR {
		public int id() {
			return 5;
		}
		public void close(DataStatePosessor d) {
			d.setDataState(ERROR);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(EDITING);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}
		public void connectfailed(DataStatePosessor d) {
			d.setDataState(MISSING);
			
		}
		
	},REMOVED {
		public void close(DataStatePosessor d) {
			d.setDataState(REMOVED);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(EDITING);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(CONNECTED);
		}
		
		public void connectfailed(DataStatePosessor d) {
			d.setDataState(MISSING);
			
		}
	},
		DELETED {
	
		public int id() { return 5; }
		public void close(DataStatePosessor d) {
			d.setDataState(DELETED);	
		}

		public void connect(DataStatePosessor d) {
			d.setDataState(DELETED);
		}

		public void open(DataStatePosessor d) {
			d.setDataState(DELETED);
		}

		public void save(DataStatePosessor d) {
			d.setDataState(DELETED);
		}
		
		public void connectfailed(DataStatePosessor d) {
			d.setDataState(MISSING);
			
		}
		
	};
	
	public void remove(DataStatePosessor d) {
		d.setDataState(REMOVED);
	}
	public void delete(DataStatePosessor d) {
		d.setDataState(DELETED);
	}



}
