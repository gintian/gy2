package com.hjsj.hrms.businessobject.sys.warn;

import com.hjsj.hrms.businessobject.sys.sso.ScheduleJobBo;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.apache.log4j.Category;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

import javax.servlet.ServletException;
import java.sql.Connection;
import java.util.Timer;

/**
 * 由于咱们把hrms当作一个root应用，这个在tomcat把环境里就会造成加载两次（ActionServlet）
 * 
 * @author lenovo
 * 
 */
public class WarnScanPlugin implements PlugIn, IConstant {
	private String startOnLoad = null;

	// private int iFrequency = 1000 * 60 * 10; // 10分钟 = 1000 * 60 * 10 毫秒
	private String frequency = null;
	private transient WarnScanTimerTask warntask=null;
	private transient static java.util.Timer timer =  new Timer();
	/**后台任务调度*/
	private ScheduleJobBo jobbo=null;
	
	@Override
    public void destroy() {
		if(warntask!=null) {
			warntask.cancel();
		}
		if(jobbo!=null) {
			jobbo.close();
		}
	}

	@Override
    public void init(ActionServlet servlet, ModuleConfig arg1)
			throws ServletException {
		Connection conn=null;
		try 
		{
				conn=AdminDb.getConnection();  //?主要解决第二次加载数据库连接池出错的问题,直接退出不加载即可
				ContextTools.setContext(servlet.getServletContext());
				// startOnLoad参数在struts启动中注入(预警控制开关)
				if (getStartOnLoad() == null || !"true".equals(getStartOnLoad())) {
					return;
				}
				//参数1：所要安排的任务。参数2：执行任务前的延迟时间，单位是毫秒。 参数3：执行各后续任务之间的时间间隔，单位是毫秒。
				if (frequency == null || frequency.trim().length() < 1) {
					frequency = "10";
				}
				timer.schedule(warntask=new WarnScanTimerTask(frequency),5000, /*Integer.parseInt(getFrequency()) */Integer.parseInt(frequency) * 1000 * 60);// 10分钟 = 1000 * 60 * 10 毫秒
				Category.getInstance(this.getClass()).debug("Start WarnScan Timer after 5000 milliseconds at："+ ContextTools.getMilliSecond()+ " for the Frequency of every " + getFrequency()+ " minute");
				//控制预警#集群环境预警提示冲突，只能保证一台机器做预警,false|true,如果未定义或为true，则预警
				//warn_scan=true
				String warn_scan=SystemConfig.getPropertyValue("warn_scan");
				/*当设置不执行后台作业，但有某个需要例外的后台作业id，多个逗号隔开  guodd 2018-01-18 */
				String warn_scan_forcejob = SystemConfig.getPropertyValue("warn_scan_forcejob");
				/*设置不执行整体后台作业，但是有某个又必须要执行时 guodd 2018-01-18*/
				if(warn_scan!=null&& "false".equalsIgnoreCase(warn_scan) && warn_scan_forcejob.length()<1) {
					return;
				}
				/**任务调度，暂时放到这*/				
				ScheduleJobBo jobbo=new ScheduleJobBo(conn); 
				jobbo.run();

		} catch (Exception ex) {
			;//ex.printStackTrace();
		}
		finally
		{
			try{
				if(conn!=null) {
					conn.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public String getStartOnLoad() {
		return startOnLoad;
	}

	public void setStartOnLoad(String startOnLoad) {
		this.startOnLoad = startOnLoad;
	}

	public String getFrequency() {
		if (frequency == null || frequency.trim().length() < 1) {
			frequency = "10";
		}
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency.trim();
	}
}
