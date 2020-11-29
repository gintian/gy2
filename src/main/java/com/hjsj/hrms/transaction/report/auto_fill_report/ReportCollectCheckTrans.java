package com.hjsj.hrms.transaction.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:报表汇总校验</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 9, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ReportCollectCheckTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		StringBuffer reportinnercheckresult = new StringBuffer();
		
		//报表汇总校验页面头信息
		//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
		//reportinnercheckresult.append(ResourceFactory.getProperty("reportInnerCheckResult.title")+"(");
		reportinnercheckresult.append(ResourceFactory.getProperty("auto_fill_report.reportGatherValidate")+"(");
		reportinnercheckresult.append(this.getCurrentDate());
		reportinnercheckresult.append(")");
		reportinnercheckresult.append("<br>");
		reportinnercheckresult.append("<br>");
		
		//选中的列表集合
		ArrayList list = (ArrayList)this.getFormHM().get("selectedlist");
		
		//如果未选中抛出异常提示用户选择要校验的报表
		if(list == null || list.size()==0){
			Exception e = new Exception(ResourceFactory.getProperty("auto_fill_report.noValidateReport")+"！");
			throw GeneralExceptionHandler.Handle(e);
		}
		
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String checkunitcode = (String)hm.get("checkunitcode");
		
		String checkFlag = (String)hm.get("checkFlag");
		String print = (String)hm.get("print");
		this.getFormHM().put("checkflag",checkFlag);
		this.getFormHM().put("print",print);
		if("5".equals(print) && !"0".equals(checkFlag)){
			this.getFormHM().put("ischeck","show");
		}
		if("5".equals(print) && "0".equals(checkFlag)){
			this.getFormHM().put("ischeck","hidden");
		}
		
		//System.out.println("表内checkunitcode=" + checkunitcode);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer temp = new StringBuffer();
		ReportCollectBo bo=new ReportCollectBo(this.frameconn);
		
		
		if(checkunitcode == null || "".equals(checkunitcode)){////编辑报表中和自动取数中的总效验
			
			String unitcode="";
			this.frowset=dao.search("select unitcode from operuser where username='"+userView.getUserName()+"'");
			if(this.frowset.next())
				unitcode=this.frowset.getString("unitcode");
			
			HashMap reportStatusMap=new HashMap();
			this.frowset=dao.search("select * from treport_ctrl where unitcode='"+unitcode+"'");
			while(this.frowset.next())
				reportStatusMap.put(this.frowset.getString("tabid"),this.frowset.getString("status"));
			
			ArrayList new_list=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				RecordVo vo = (RecordVo)list.get(i);
				String tt = vo.getString("tabid"); //报表表号
				String tname = vo.getString("name");   //表名
				String status=(String)reportStatusMap.get(tt);
				if(status==null|| "-1".equals(status)|| "0".equals(status)|| "2".equals(status))
					new_list.add(vo);
				else
					temp.append("<br>"+tt+"."+tname+"&nbsp;"+ResourceFactory.getProperty("auto_fill_report.batchFillData.info6")+"！");
			}
			
			temp.append(bo.compareChildData2(unitcode, new_list));
			
		}else{//报表汇总中的总效验
			temp.append(bo.compareChildData2(checkunitcode, list));
		}
		if(temp == null || "".equals(temp.toString())){
			reportinnercheckresult.delete(0,reportinnercheckresult.length());
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			//reportinnercheckresult.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			reportinnercheckresult.append(ResourceFactory.getProperty("auto_fill_report.reportGatherValidateSuccess")+"！");
			this.getFormHM().put("downloadflag","hidden");
		}else{
			reportinnercheckresult.append(temp.toString());
			this.getFormHM().put("downloadflag","show");
		}
		this.getFormHM().put("reportInnerCheckResult" , reportinnercheckresult.toString());
		this.getFormHM().put("reportInnerCheckResult_t" , reportinnercheckresult.toString());
		
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public String getTabName(Connection conn ,String tabid)throws GeneralException{
		ContentDAO dao = new ContentDAO(conn);
		String tbname = "";
		String sql =" select name from tname where tabid = " + tabid;
		try {
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				tbname =this.frowset.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return tbname;
		
	}
	

	/**
	 * 获得系统当前时间
	 * @return
	 */
	public String getCurrentDate(){
		Date currentTime = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"); 
		return sdf.format(currentTime); 
	}
}
