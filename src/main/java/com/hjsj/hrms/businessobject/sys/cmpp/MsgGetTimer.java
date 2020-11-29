package com.hjsj.hrms.businessobject.sys.cmpp;

import java.util.Timer;

public class MsgGetTimer {
	
	private static Timer timer;
	private MsgGetTimer() {
		
	}
	
	public static Timer getTimer() {
		if (timer == null) {
			timer = new Timer();
			timer.schedule(new MsgActivityTimer(), 10000,10000);
		}
		
		return timer;
	}
	
	
	
}
