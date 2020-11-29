/**
 * 
 */
package com.hjsj.hrms.businessobject.sys.sso;

import com.hrms.struts.constant.SystemConfig;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

import javax.servlet.ServletException;
import java.util.Timer;

/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-13:下午02:05:27</p> 
 *@author cmq
 *@version 4.0
 */
public class SyncLogonPlugin implements PlugIn {

	private transient static java.util.Timer timer =  new Timer();
	private SyncLogonInfoTimerTask synctask=null;
	@Override
    public void destroy() {
		if(synctask!=null) {
			synctask.cancel();
		}
	}
	/**
	 * 取得扫锚时间间隔
	 * @return
	 */
	private int searchScan_Seconds()
	{
		int mills=1000 * 60 * 10;
		try
		{
			String scan_time=SystemConfig.getProperty("scan_time");
			mills=Integer.parseInt(scan_time.trim())*1000;
		}
		catch(Exception ex)
		{
			;
		}
		return mills;
	}
	/**
	 * 分析是否要进行账号同步
	 * @return
	 */
	private boolean is_SsoSync()
	{
	  boolean bsync=false;
	  try
	  {
		String sso_sync="false";
		sso_sync=sso_sync!=null?sso_sync:"";
		if("true".equalsIgnoreCase(sso_sync)) {
			bsync=true;
		}
//		bsync=Boolean.parseBoolean(sso_sync);
		bsync=false;
	  }
	  catch(Exception ex)
	  {
		  ex.printStackTrace();
	  }
	  return bsync;
	}
	
	@Override
    public void init(ActionServlet arg0, ModuleConfig arg1)
			throws ServletException {
		if(is_SsoSync())
		{
			int mills=searchScan_Seconds();
			//参数1：所要安排的任务。参数2：执行任务前的延迟时间，单位是毫秒。 参数3：执行各后续任务之间的时间间隔，单位是毫秒。
			timer.schedule(synctask=new SyncLogonInfoTimerTask(),5000, mills);// 10分钟 = 1000 * 60 * 10 毫秒
		}

	}
	

}
