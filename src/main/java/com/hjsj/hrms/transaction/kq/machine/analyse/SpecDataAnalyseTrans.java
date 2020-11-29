package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 个别分析
 * <p>Title:SpecDataAnalyseTrans.java</p>
 * <p>Description>:SpecDataAnalyseTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:May 4, 2010 7:19:10 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SpecDataAnalyseTrans extends IBusiness 
{
    public void execute() throws GeneralException 
	{
    	
		/**临时表名**/		
		String date=(String)this.getFormHM().get("specdata");
		if(date==null||date.length()<=0)
			return;
		String str[]= date.split("/");
		ArrayList one_list=new ArrayList();
		ArrayList nbasea0100=new ArrayList();
		StringBuffer infoBuffer = new StringBuffer();
		StringBuffer codebuff = new StringBuffer();//
        codebuff.append(" (");
    	for(int i=0;i<str.length;i++)
        {
    		String value=str[i];
    		String strvalue[]=value.split(",");
    		String a0100=strvalue[1];
    		a0100=a0100.substring(1,a0100.length()-1);
    		String nbase = strvalue[0];
    		nbase = nbase.substring(1, nbase.length() - 1);
    		if(infoBuffer.indexOf(nbase + a0100 + "`") != -1)
    			continue;
    		infoBuffer.append(nbase + a0100 + "`");
    		nbasea0100.add(nbase+"`"+ a0100);
    		codebuff.append("'" + a0100 + "',");
    		one_list.add(a0100);
    	}
    	codebuff.setLength(codebuff.length() - 1);
    	codebuff.append(")");
    	if(one_list==null||one_list.size()<=0)
    		return;
    	
    	String start_date=(String)this.getFormHM().get("start_date");
		String end_date=(String)this.getFormHM().get("end_date");
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		boolean isDateExcuse=kqUtilsClass.isExcuseCurKqDate(start_date,end_date);//处理时间包含在考勤期间范围内		
		if(!isDateExcuse){
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理时间范围应在当前考勤期间范围内！"));
		}
		ArrayList kq_dbase_list=new ArrayList();
		kq_dbase_list = kqUtilsClass.setKqPerList("", "");
		
		String nbase=(String)this.getFormHM().get("select_pre");
		if ("all".equals(nbase) || "".equals(nbase) || nbase == null) {
			for (int i = 0; i < kq_dbase_list.size(); i++) {
				String onenbase = (String) kq_dbase_list.get(i);
				if(!getB0110ForA0100(onenbase,one_list))
					throw GeneralExceptionHandler.Handle(new GeneralException("","个别处理人员必须是在一个单位下的人员！"));
			}
		}else {
			if(!getB0110ForA0100(nbase,one_list))
				throw GeneralExceptionHandler.Handle(new GeneralException("","个别处理人员必须是在一个单位下的人员！"));
		}
		/**数据处理模式 mark=1为集中处理，0为分用户**/
		String analyseType="";
		String mark=getDataprocessing();
		mark=mark!=null&&mark.length()>0?mark:"0";
		if("1".equalsIgnoreCase(mark))
		{
			analyseType="101";
		}else
		{
			analyseType="1";
		}
		/**结束**/		
				
		start_date=start_date.replaceAll("-","\\.");
		end_date=end_date.replaceAll("-","\\.");
		if(start_date==null||start_date.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理起始时间不能为空！"));
		if(end_date==null||end_date.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理结束时间不能为空！"));
		try
		{
			
			start_date=DateUtils.format(DateUtils.getDate(start_date,"yyyy.MM.dd"),"yyyy.MM.dd");
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理起始时间错误！"));
		}
		try
		{
			end_date=DateUtils.format(DateUtils.getDate(end_date,"yyyy.MM.dd"),"yyyy.MM.dd");
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","处理结束时间错误！"));
		}
		
		KqParameter para=new KqParameter(this.userView,"",this.getFrameconn());
	    HashMap hashmap =para.getKqParamterMap();
		String kq_type=(String)hashmap.get("kq_type");
		String kq_cardno=(String)hashmap.get("cardno");
		String kq_Gno=(String)hashmap.get("g_no");
		String dataUpdateType="0";
		String analysBase="all";		
		
    	DataProcedureAnalyse dataProcedureAnalyse=new DataProcedureAnalyse(this.getFrameconn(),this.userView,analyseType,kq_type,kq_cardno,kq_Gno,dataUpdateType,kq_dbase_list);
    	//szk为全部人员库的时候
    	if ("all".equals(nbase) || "".equals(nbase) || nbase == null) {
			dataProcedureAnalyse.specDataAnalys(nbasea0100,kq_dbase_list,start_date,end_date,analysBase,0);	//走数据处理class
		}
    	else {
    		kq_dbase_list.clear();
    		kq_dbase_list.add(nbase);
    		dataProcedureAnalyse.specDataAnalys(nbasea0100,kq_dbase_list,start_date,end_date,analysBase,0);	//走数据处理class
		}	
		
		String fAnalyseTempTab=dataProcedureAnalyse.getFAnalyseTempTab();	//数据处理表
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			String oneNbase = (String) kq_dbase_list.get(i);
	        String codewhere = "a0100 in " + codebuff.toString() + " and nbase='" + oneNbase + "'";
			dataProcedureAnalyse.setRest(fAnalyseTempTab, codewhere, start_date, end_date, i);
		}
		String fExceptCardTab=dataProcedureAnalyse.getFExceptCardTab();  //临时异常表的名称
    	String fTranOverTimeTab=dataProcedureAnalyse.getFTranOverTimeTab();  //临时延时加班表
    	String fBusiCompareTab=dataProcedureAnalyse.getFBusiCompareTab(); //申请比对表
    	this.getFormHM().put("start_date", start_date);
    	this.getFormHM().put("end_date", end_date);
    	this.getFormHM().put("analyseTempTab",fAnalyseTempTab);//分析结果表
    	this.getFormHM().put("exceptCardTab",fExceptCardTab);//异常刷卡
    	this.getFormHM().put("tranOverTimeTab",fTranOverTimeTab);//延时加班
    	this.getFormHM().put("busiCompareTab",fBusiCompareTab);//申请比对
		this.getFormHM().put("kq_type",kq_type);
		this.getFormHM().put("kq_cardno",kq_cardno);
	}
    public String getDataprocessing()
	{
		String data="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet= null;
		StringBuffer sql = new StringBuffer();
		sql.append("select content,status from kq_parameter where ");
		sql.append("name='DATA_PROCESSING' and b0110='UN'");
		try
		{
			rowSet=dao.search(sql.toString());
			if(rowSet.next())
			{
				data=rowSet.getString("content");
			}
			data=data!=null&&data.length()>0?data:"0";
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return data;
	}
    private boolean getB0110ForA0100(String nbase,ArrayList a0100list)
	 {
			boolean isCorrect=true;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs=null;
			try
			{
				StringBuffer  sql=new StringBuffer();
				sql.append("select count(DISTINCT b0110) aa from "+nbase+"A01 where a0100 in(");
			    for(int i=0;i<a0100list.size();i++)
			    {
			    	sql.append("'"+a0100list.get(i)+"',");
			    }
			    sql.setLength(sql.length()-1);
			    sql.append(")");
				rs=dao.search(sql.toString());
				if(rs.next())
				{
				 if(rs.getInt("aa")>1)
					 isCorrect=false;				
				}
			}catch(Exception e)
			{
			   e.printStackTrace();	
			}finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
			return isCorrect;
	}
}
