package com.hjsj.hrms.businessobject.sys.cmpp;

import java.util.TimerTask;


/**
 * 接口调用
 * @author
 *
 */
public class MsgActivityTimer extends TimerTask{
	/**
	 * 短信接口长链接，定时进行链路检查
	 */
	protected void executeInternal() {
			System.out.println("×××××××××××××开始链路检查××××××××××××××");
			int count=0;
			boolean result=MsgContainer2.activityTestISMG();
			while(!result){
				count++;
				result=MsgContainer2.activityTestISMG();
				if(count>=(MsgConfig.getConnectCount()-1)){//如果再次链路检查次数超过两次则终止连接
					break;
				}
			}
			System.out.println("×××××××××××××链路检查结束××××××××××××××");
	}

	@Override
    public void run() {
		executeInternal();
		
	}
}
