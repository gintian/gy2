package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;

/** 
 *<p>Title:SaveReportCycleTrans.java</p> 
 *<p>Description:精算报表、填报周期 保存</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 28, 2010</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class SaveReportCycleTrans extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		RecordVo vo = (RecordVo) this.getFormHM().get("reportcyclevo");
		if (vo == null)
			return;
		
		//判断是否是默认
		String date = "";
		if(this.getFormHM().get("adddate")!=null && !"".equals(this.getFormHM().get("adddate")))
		{
			date =(String)this.getFormHM().get("adddate");
			this.getFormHM().remove("adddate");
		}else
		{
			if(vo.getString("bos_date")!=null && !"".equals(vo.getString("bos_date")))
				date = vo.getString("bos_date");			
		}
		String year = "";
		if(vo.getString("theyear")!=null&&!"".equals(vo.getString("theyear"))){
			year = vo.getString("theyear");
		}else{
			Calendar c =  Calendar.getInstance();
			year =String.valueOf(c.get(Calendar.YEAR));
		}
		
		int id = DbNameBo.getPrimaryKey("tt_cycle","id",this.getFrameconn());
		vo.setInt("id", id);
		if("".equals(date)){
			vo.setDate("bos_date", new Date());
		}else{
			vo.setDate("bos_date", date);
		}
		vo.setString("theyear", year);
		vo.setString("status", "01");
		//AdminCode.getCodeName("23","01");
		
		StringBuffer strsql = new StringBuffer();		
		strsql.append("select id,name,bos_date,theyear,kmethod,status from tt_cycle where theyear='"+year+"' ");				
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)		
			strsql.append(" and bos_date=to_date('"+String.valueOf(vo.getDate("bos_date")).substring(0,10)+"','yyyy-mm-dd') ");
		else
			strsql.append(" and bos_date='"+String.valueOf(vo.getDate("bos_date")).substring(0,10)+"' ");		
		strsql.append(" and kmethod='"+vo.getString("kmethod")+"' ");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		boolean flag =false;
		try 
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				flag = true;
			}

		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
		
		if(!flag){
			dao.addValueObject(vo);
		}
	      	
	}

}
