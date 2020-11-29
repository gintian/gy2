/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportBulletinList;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.options.PortalTailorXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.sys.warn.ScanTotal;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:SearchPortalInfoTrans</p>
 * <p>Description:查询公告栏，常用花名册，常用查询条件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-18:16:36:53</p>
 * @author chenmengqing
 * @version 1.0 * 
 */
public class SearchPortalInfoTrans extends IBusiness {
    private int view_base=6;
    private String dbpre;
    
	private StringBuffer autoScroll(String content,String bscroll)
	{
		StringBuffer str=new StringBuffer();
		if("1".equals(bscroll))
		{
			str.append("<marquee scrolldelay=\"350\"  height=\"150\" direction=\"up\" onmouseover='this.stop()' onmouseout='this.start()'>");
			str.append(content);
			str.append("</marquee>");
		}
		else
    	{
			str.append("<table width=\"99%\" height=\"150\">");
			str.append("<tr>");
			str.append("<td>");
			str.append(content);
			str.append("</td>");
			str.append("</tr>");
			str.append("</table>");
    	}
		//System.out.println(str.toString());
		return str;
	}
	private String getBoardContent1(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		int days=0;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		int announce_days = Integer.parseInt((sysbo.getValue(Sys_Oth_Parameter.ANNOUNCE_DAYS)!=null&&sysbo.getValue(Sys_Oth_Parameter.ANNOUNCE_DAYS).trim().length()>0?sysbo.getValue(Sys_Oth_Parameter.ANNOUNCE_DAYS).trim():"3"));
		String a_tempstr="("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),"approvetime")+")<period";
		String diff="("+Sql_switcher.diffDays(Sql_switcher.sqlNow(),"approvetime")+")";
		String sql = "select id,topic,viewcount,priority,"+diff+" days ";
		sql=sql+" from announce where approve=1 and " + a_tempstr ;
		StringBuffer strsql = new StringBuffer();
		strsql.append(sql);
		strsql.append(" order by priority,createtime desc");		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int view=this.view_base;
    	if("1".equals(scroll))
    		view=100;
		try 
		{			
			str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
			str.append("		<tr ><td >");
			content.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			int i=0;
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				String id=this.getFrowset().getString("id");
				if(!(this.userView.isHaveResource(IResourceConstant.ANNOUNCE,id)))
				{
							continue;
				}				
				if(i<view)
				{
					days=this.frowset.getInt("days");
					content.append("		<tr><td class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					{
						if(days>announce_days)
							content.append("			<img src=\"/images/forumme.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" target=\"_blank\">"+/*i+". "+*/subText(this.frowset.getString("topic"))+"("+(this.frowset.getString("viewcount")==null||this.frowset.getString("viewcount").length()<=0?"0":this.frowset.getString("viewcount"))+"次)</a>");
						else
						{
							String text=this.frowset.getString("topic");
							if(text==null||text.length()<=0)
								text= "";
							if(text.length()>16)
							    text=text.substring(0,15)+"...";

							content.append("			<img src=\"/images/forumme.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" target=\"_blank\">"+/*i+". "+*/text+"("+(this.frowset.getString("viewcount")==null||this.frowset.getString("viewcount").length()<=0?"0":this.frowset.getString("viewcount"))+"次)<img src='/images/new0.gif' border='0'></a>");
						}							
					}
					else
					{
						if(days>announce_days)
							content.append("			<img src=\"/images/forumme1.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" target=\"_blank\">"+/*i+". "+*/subText(this.frowset.getString("topic"))+"("+(this.frowset.getString("viewcount")==null||this.frowset.getString("viewcount").length()<=0?"0":this.frowset.getString("viewcount"))+"次)</a>");
						else
						{
							String text=this.frowset.getString("topic");
							if(text==null||text.length()<=0)
								text= "";
							if(text.length()>16)
							  text=text.substring(0,15)+"...";

							content.append("			<img src=\"/images/forumme1.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" target=\"_blank\">"+/*i+". "+*/text+"("+(this.frowset.getString("viewcount")==null||this.frowset.getString("viewcount").length()<=0?"0":this.frowset.getString("viewcount"))+"次)<img src='/images/new0.gif' border='0'></a>");
						}							
					}
				    content.append("		</td></tr>\n");
				}
				i++;
			}
			content.append("</table>");		
			content=autoScroll(content.toString(),scroll);
			str.append(content);
			if(i>this.view_base ){
				str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/selfservice/welcome/boardTheMore.do?b_more=link\" target=\"_blank\">>>更多(共"+(i)+"项)</a></td></tr>");
			}else
			{
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}
			str.append("</table>");
			if(i>=1)
				return str.toString();
			else
				return "";
		}
		catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} 
	}

	private String getWarnResult(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		str.append("		<tr ><td>");
		try
		{
			ScanTotal st = new ScanTotal(this.userView);
			ArrayList alTotal = st.execute();
			int iRows = alTotal.size()>this.view_base?this.view_base:alTotal.size();		
			if("1".equals(scroll))
				iRows=alTotal.size();
			if(iRows>0)
				content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >");
			for( int i=0; i<iRows; i++){
				CommonData cData = (CommonData)alTotal.get(i);
				content.append("		<tr class=\""+(i%2==0?"":"")+"\"><td class=\"RecordRowPo\">");
				if("1".equals(twinkle))
				    content.append("		<img src=\"/images/forumme.gif\"> <a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+ cData.getDataValue()+"&dbpre=&dbPre=\">"+/*(i+1)+". "+*/subText(cData.getDataName())+"</a>");
				else
					content.append("		<img src=\"/images/forumme1.gif\"> <a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+ cData.getDataValue()+"&dbpre=&dbPre=\">"+/*(i+1)+". "+*/subText(cData.getDataName())+"</a>");
				content.append("		</td></tr>");
			}
			
			if(iRows>0)
				content.append("</table>");			
			content=autoScroll(content.toString(),scroll);
			str.append(content);
			str.append("</td></tr>");
			if( alTotal.size()>this.view_base-2 ){
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><a href=\"/system/warn/info_all.do?br_query=link\">>>更多(共"+alTotal.size()+"项)</a></td></tr>");
			}else
			{
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}
			str.append("</table>");
			if(iRows>0)
				return str.toString();
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private String getCommonCond(String type,String twinkle,String scroll) throws GeneralException
	{
		StringBuffer content=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        if(type==null|| "".equals(type))
        	type="1";
        strsql.append("select id,name,type from lexpr where type='");//
        strsql.append(type);
        strsql.append("' order by norder");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		str.append("		<tr ><td >");
        try
		{
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			this.frowset=dao.search(strsql.toString());
        	int i=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	while( this.frowset.next())
        	{
        		if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
        			continue;
				if(i<view)
				{
					content.append("		<tr class=\"\"><td class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					    content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/workbench/query/gquery_interface.do?b_query=link&home=4&type=1&curr_id="+ this.frowset.getString("id")+"\")'>"+/*i+". "+*/subText(PubFunc.nullToStr(this.frowset.getString("name")))+"</a>");
					else
						content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/workbench/query/gquery_interface.do?b_query=link&home=4&type=1&curr_id="+ this.frowset.getString("id")+"\")'>"+/*i+". "+*/subText(PubFunc.nullToStr(this.frowset.getString("name")))+"</a>");
				
					content.append("		</td></tr>\n");
				}
				++i;
        	}			
			content.append("</table>");	
			content=autoScroll(content.toString(),scroll);
			str.append(content);
			str.append("</td></tr>");
			if(i>this.view_base){
				if(this.userView.getBosflag()!=null&& "hl4".equalsIgnoreCase(this.userView.getBosflag()))
					str.append("	<tr ><td class=\"RecordRowPo\"  align=\"right\"><a href=\"/workbench/query/query_interface.do?home=4&b_gquery=link\" target=\"_self\">>>更多(共"+i+"项)</a></td></tr>");
				else
					str.append("		<tr ><td class=\"RecordRowPo\"  align=\"right\"><a href=\"/workbench/query/query_interface.do?home=1&b_gquery=link\" target=\"_self\">>>更多(共"+i+"项)</a></td></tr>");
			}else
			{
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}
			str.append("</table>");
			if(i>=1)
				return str.toString();
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}	
	private String getDbPre()
	{
		ArrayList dblist=null;
		String pre="";		
		try
		{
			DbNameBo dbvo=new DbNameBo(this.getFrameconn());
			dblist=this.userView.getPrivDbList();
			dblist=dbvo.getDbNameVoList(dblist);

			if(dblist.size()>0)
			{
				RecordVo dbname=(RecordVo)dblist.get(0);
				pre=dbname.getString("pre");
			} 	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return pre;
	}
	/**
	 * 取得常用登记表
	 * @return
	 * @throws GeneralException
	 */
	private String getCommonYkcard(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		str.append("		<tr ><td >");
        try
		{
        	
        	StringBuffer sql=new StringBuffer();
        	sql.append("select * from rname  where flagA='A'");
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			//List cardlist=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql.toString());
        	int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	while( this.frowset.next())
        	{
        		if(!(this.userView.isHaveResource(IResourceConstant.CARD,this.frowset.getString("tabid"))))
        			continue;        		
        		if(i<view)
        		{
				//LazyDynaBean cardvo=(LazyDynaBean)cardlist.get(i);	  
				String hzname=this.frowset.getString("name");//(String)cardvo.get("name");
				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				content.append("		<tr class=\"\"\"><td class=\"RecordRowPo\">");
				if("1".equals(twinkle))
				    content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/card/searchshowcard.do?b_show=link&inforkind=1&home=1&tabid="+ this.frowset.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				else
				    content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/card/searchshowcard.do?b_show=link&inforkind=1&home=1&tabid="+ this.frowset.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				content.append("		</td></tr>\n");   
				}        	
        		i++;
        	}
			content.append("</table>");	
			content=autoScroll(content.toString(),scroll);
			str.append(content);
			str.append("</td></tr>");
			if(i>this.view_base ){
				str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/card/searchcard.do?b_query=link&home=1&inforkind=1\" target=\"_self\">>>更多(共"+i+"项)</a></td></tr>");
			}else
			{
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}
			str.append("</table>");
			if(i>=1)
				return str.toString();
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	/**
	 * 取得常用统计
	 * @return
	 * @throws GeneralException
	 */
	private String getCommonStat(String type,String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		str.append("		<tr ><td >");
        try
		{
        	StringBuffer sql=new StringBuffer();
        
        	sql.append("select * from sname where infokind=1 order by snorder");
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			//List statlist=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql.toString());
        	int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	while( this.frowset.next())
        	{
        		if(!(this.userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
        			continue;            		
        		if(i<view)
        		{	
        		//LazyDynaBean statvo=(LazyDynaBean)statlist.get(i);	  
				String hzname=this.frowset.getString("name");//(String)statvo.get("name");
				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				content.append("		<tr class=\"\"\"><td class=\"RecordRowPo\">");
				if("1".equals(this.frowset.getString("type")))
					if("1".equals(twinkle))
					    content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_chart=link&querycond=&infokind=1&isshowstatcond=1&home=1&userbase="+this.dbpre+"&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
					else
						 content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_chart=link&querycond=&infokind=1&isshowstatcond=1&home=1&userbase="+this.dbpre+"&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
			    else
					if("1".equals(twinkle))
					   content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&infokind=1&isshowstatcond=1&home=1&userbase="+this.dbpre+"&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");				
					else
						content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&infokind=1&isshowstatcond=1&home=1&userbase="+this.dbpre+"&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");				
				//content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_chart=link&querycond=&infokind=1&isshowstatcond=1&statid="+ statvo.get("id")+"\")'>"+/*(i+1)+". "+*/hzname+"</a>");
				content.append("		</td></tr>\n");  
				}  
        		i++;
        	}
        	content.append("</table>");	
        	content=autoScroll(content.toString(),scroll);
        	str.append(content);
			str.append("</td></tr>");
			if(i>this.view_base ){
				str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/static/commonstatic/statshow.do?b_inizm=link&infokind=1&home=1\" target=\"_self\">>>更多(共"+i+"项)</a></td></tr>");//&isshowstatcond=1
			}else
			{
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}
			str.append("</table>");	
			if(i>=1)
				return str.toString();
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	/**
	 * 取得常用花名册列表
	 * @param type
	 * @return
	 * @throws GeneralException
	 */
	private String getCommonMuster(String type,String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
		StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		str.append("		<tr ><td >");
        if(type==null|| "".equals(type))
        	type="1";
        try
		{

			content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			MusterBo musterbo=new MusterBo(this.getFrameconn());
			ArrayList musterlist=musterbo.getPrivMusterList(type,this.userView);
        	int i=0;
        	int j=0;
        	int k=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	for(i=0;i<musterlist.size();i++)
        	{
				RecordVo mustvo=(RecordVo)musterlist.get(i);	  
				String hzname=mustvo.getString("hzname");        		
        		/*if(!(this.userView.isHaveResource(IResourceConstant.MUSTER,mustvo.getString("tabid"))))
        			continue;           		*/
        		if(k>=view)
        			break;

				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				content.append("		<tr class=\"\"\"><td class=\"RecordRowPo\">");
				if("1".equals(twinkle))
				    content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/muster/muster_list.do?b_open=0&checkflag=1&a_inforkind=1&tabid="+ mustvo.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				else
					content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/muster/muster_list.do?b_open=0&checkflag=1&a_inforkind=1&tabid="+ mustvo.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				
				content.append("		</td></tr>\n");
				++k;
        	}
			content.append("</table>");	
			content=autoScroll(content.toString(),scroll);
			str.append(content);
			str.append("</td></tr>");
			if(musterlist.size()>this.view_base ){
				str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/muster/muster_list.do?b_query=link&checkflag=1&a_inforkind=1\" target=\"_self\">>>更多(共"+musterlist.size()+"项)</a></td></tr>");
			}else
			{
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}
			str.append("</table>");	
			if(k>=1)
				return str.toString();
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	
	
	
	
	
	/**
	 * 取得报表列表
	 * @param type
	 * @return
	 * @throws GeneralException
	 */
	private String getReport(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer(); 
		StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		str.append("		<tr ><td >");
        try
		{
        	
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			ReportBulletinList reportBulletinList=new ReportBulletinList(this.getFrameconn());
			ArrayList reportList=reportBulletinList.getReportList(this.userView);
			ArrayList customList=reportBulletinList.getCustomReportList(this.userView);
			int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
        	if(reportList!=null)
        	{
	        	for(i=0;i<reportList.size();i++)
	        	{        	
					RecordVo temp=(RecordVo)reportList.get(i);	 	        		
	        		if(!(this.userView.isHaveResource(IResourceConstant.REPORT,temp.getString("tabid"))))
	        			continue;  	        			        		
	        		if(i>=view)
	        			break;
 
					String hzname=temp.getString("name");
					j=hzname.indexOf(".");
					hzname=hzname.substring(j+1);
					int status=temp.getInt("paper");
					String	ctrollflag="0";
					if(status==-1||status==0||status==2){
						
					}else{
						ctrollflag="1";
					}
					content.append("		<tr class=\"\"\"><td class=\"RecordRowPo\" >");
					if("1".equals(twinkle))
				    	content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/report/edit_report/reportSettree.do?b_query2=query&operateObject=1&operates=1&print=5&status="+status+"&ctrollflag="+ctrollflag+"&home=1&flag=1&code="+ temp.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
					else
						content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/report/edit_report/reportSettree.do?b_query2=query&operateObject=1&operates=1&print=5&status="+status+"&ctrollflag="+ctrollflag+"&home=1&flag=1&code="+ temp.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");

					content.append("		</td></tr>\n");        		
	        	}
        	}
					if(i>=view)	
					{
						
					}else{
						if(customList!=null&&customList.size()>0){
							for(int a=0;a<customList.size();a++)
				        	{        	
								LazyDynaBean temp=(LazyDynaBean)customList.get(a);	 	        		
				        		if(i>view)
									break;
				        		String hzname=""+temp.get("name");
								j=hzname.indexOf(".");
								hzname=hzname.substring(j+1);
								//content.append("		<tr class=\"trDeep\"\"><td class=\"RecordRow\"  >");
								content.append("		<tr class=\"\"\"><td class=\"RecordRowPo\" >");
								if("1".equals(twinkle)){
							    	content.append("			<img src=\"/images/forumme.gif\">");
							    	if("0".equals(temp.get("report_type"))){
							    		if(".xls".equals(temp.get("ext"))||".xlsx".equals(temp.get("ext"))||".xlt".equals(temp.get("ext"))||".xltx".equals(temp.get("ext"))||".htm".equals(temp.get("ext"))||".html".equals(temp.get("ext")))
							    		{
							    			content.append("<a href='javascript:openwins(\""+temp.get("id")+"\")'>"+subText(hzname)+"</a>");	
							    		}
							    		
							    	}else if("1".equals(temp.get("report_type"))){
							    		content.append("<a href='javascript:openwin(\"/system/options/customreport.do?b_query2=query`operateObject=1`operates=1`code="+temp.get("link_tabid")+"`status=1\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
							    		
							    	}else if("2".equals(temp.get("report_type"))){
							    		content.append("<a href='javascript:openwin(\"/general/card/searchcard.do?b_query2=link`home=2`inforkind="+temp.get("flaga")+"`result=0`tableid="+temp.get("link_tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
							    	}else if("3".equals(temp.get("report_type"))){
							    		if(temp.get("module")!=null&&!"".equals(temp.get("module"))){
							    			content.append("<a href='javascript:openwin(\"/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag="+temp.get("module")+"`a_inforkind="+temp.get("a_inforkind")+"`result=0`isGetData=1`operateMethod=direct`costID="+temp.get("link_tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
							    		}
							    	}
								}
								else{
							    	content.append("			<img src=\"/images/forumme1.gif\">");
							    	if("0".equals(temp.get("report_type"))){
							    		if(".xls".equals(temp.get("ext"))||".xlsx".equals(temp.get("ext"))||".xlt".equals(temp.get("ext"))||".xltx".equals(temp.get("ext"))||".htm".equals(temp.get("ext"))||".html".equals(temp.get("ext")))
							    		{
							    			content.append("<a href='javascript:openwins(\""+temp.get("id")+"\")'>"+subText(hzname)+"</a>");	
							    		}
							    		
							    	}else if("1".equals(temp.get("report_type"))){
							    		content.append("<a href='javascript:openwin(\"/system/options/customreport.do?b_query2=query`operateObject=1`operates=1`code="+temp.get("link_tabid")+"`status=1\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
							    		
							    	}else if("2".equals(temp.get("report_type"))){
							    		content.append("<a href='javascript:openwin(\"/general/card/searchcard.do?b_query2=link`home=2`inforkind="+temp.get("flaga")+"`result=0`tableid="+temp.get("link_tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
							    	}else if("3".equals(temp.get("report_type"))){
							    		if(temp.get("module")!=null&&!"".equals(temp.get("module"))){
							    			content.append("<a href='javascript:openwin(\"/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag="+temp.get("module")+"`a_inforkind="+temp.get("a_inforkind")+"`result=0`isGetData=1`operateMethod=direct`costID="+temp.get("link_tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
							    		}
							    	}
								}
								content.append("		</td></tr>\n");
				        		i++;	
				        	}    
						}
					}
				
	        	content.append("</table>");	
				content=autoScroll(content.toString(),scroll);
				str.append(content);
				str.append("</td></tr>");
				
				int size=0;
				if(reportList!=null)
					size=reportList.size();
				if(customList!=null)
					size=size+customList.size();
				if(size>this.view_base ){
					if(this.userView.getBosflag()!=null&& "hl4".equalsIgnoreCase(this.userView.getBosflag()))
						str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&home=4&print=5\" target=\"_self\">>>更多(共"+size+"项)</a></td></tr>");
					else
						str.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&home=1&print=5\" target=\"_self\">>>更多(共"+size+"项)</a></td></tr>");
				}else
				{
					str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
				}
				str.append("</table>");	
        	
			
			
			if(i>=1)
				return str.toString();
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	/**
	 * 代办事宜列表
	 * @param type
	 * @return
	 * @throws GeneralException
	 */
	private String getMatter(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();  
		StringBuffer str=new StringBuffer();
		str.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
		str.append("		<tr ><td >");
        try
		{
        	int i=0;
        	int j=0;
        	int view=this.view_base;
        	if("1".equals(scroll))
        		view=100;
			content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			MatterTaskList matterTaskList=new MatterTaskList(this.getFrameconn(),this.userView);
			ArrayList matterList=new ArrayList();
        	matterList=matterTaskList.getWaitTaskList(matterList);
      //  	matterList=matterTaskList.getInstanceList(matterList);   //已处理的任务 不显示
        	matterList=matterTaskList.getTmessageList(matterList);   
        	int num=0;
        	if(matterList!=null)
        	{
	        	for(i=0;i<matterList.size();i++,j++)
	        	{        	
	        		if(j>=view)
	        			break;
	        		CommonData cData=(CommonData)matterList.get(i);
	        		content.append("		<tr class=\"\"\"><td class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					    content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\""+ cData.getDataValue()+"\")'>"+cData.getDataName()+"</a>"); // "+subText(cData.getDataName())+"</a>");
					else
					    content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\""+ cData.getDataValue()+"\")'>"+cData.getDataName()+"</a>"); // "+subText(cData.getDataName())+"</a>");
					content.append("		</td></tr>\n");   
	        	}
	        	num=matterList.size();
        	}
        	SalaryPkgBo salaryPkgBo=new SalaryPkgBo(this.getFrameconn(),this.userView); 
			ArrayList salarylist=salaryPkgBo.getEndorseRecords(); //审批薪资
			LazyDynaBean abean=new LazyDynaBean();
        	if(salarylist!=null)
        	{
	        	for(i=0;i<salarylist.size();i++,j++)
	        	{        	
	        		if(j>=this.view_base)
	        			break;
	        		abean=(LazyDynaBean)salarylist.get(i);
	        		content.append("		<tr class=\"\"\"><td class=\"RecordRowPo\">");
					if("1".equals(twinkle))
					    content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\""+ abean.get("url")+"\")'>"+subText((String)abean.get("name"))+"</a>");
					else
					    content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\""+ abean.get("url")+"\")'>"+subText((String)abean.get("name"))+"</a>");
					content.append("		</td></tr>\n");   
	        	}
	        	num+=salarylist.size();
        	}        	
			content.append("</table>");	
			content=autoScroll(content.toString(),scroll);
			str.append(content);
			str.append("</td></tr>");
			if(j>=this.view_base )
        	{
        		//content.append("		<tr ><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/template/search_bs_tree.do?b_query=link&type=1&res_flag=7&module=20\" target=\"_self\">>>更多(共"+matterList.size()+"项)</a></td></tr>");
				str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><a href=\"/general/template/matterList.do?b_query=link\" target=\"_self\">>>更多(共"+num+"项)</a></td></tr>");
        	}else
			{
        		str.append("		<tr class=\"\"><td class=\"RecordRowPo\" align=\"right\"><br></td></tr>");
			}			
			str.append("</table>");	
			if(j>=1)
				return str.toString();
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	public void execute() throws GeneralException {
		dbpre=getDbPre();		
		setShowItem(dbpre);				
	}
	private void setShowItem(String dbpre) throws GeneralException
	{
		ArrayList nodeslist=new PortalTailorXml().ReadOutParameterXml("SYS_PARAM",this.getFrameconn(),this.userView.getUserName());
		for(int i=0;i<nodeslist.size();i++)
		{
			ArrayList attributelist=(ArrayList)nodeslist.get(i);
			for(int j=0;j<attributelist.size();j++)
			{
				LabelValueView item=(LabelValueView)attributelist.get(j);
				if("id".equals(item.getLabel()) && "1".equals(item.getValue()))
				{
				   putvaluetojsp("boardcontent",attributelist,i,dbpre);
				}
				if("id".equals(item.getLabel()) && "2".equals(item.getValue()))
				{
				   putvaluetojsp("warn",attributelist,i,dbpre);
				}	    			
				if("id".equals(item.getLabel()) && "3".equals(item.getValue()))
				{
				   putvaluetojsp("muster",attributelist,i,dbpre);
				}
				if("id".equals(item.getLabel()) && "4".equals(item.getValue()))
				{
				   putvaluetojsp("cond",attributelist,i,dbpre);
				}
				if("id".equals(item.getLabel()) && "5".equals(item.getValue()))
				{
				   putvaluetojsp("stat",attributelist,i,dbpre);
				}
				if("id".equals(item.getLabel()) && "6".equals(item.getValue()))
				{
				   putvaluetojsp("ykcard",attributelist,i,dbpre);
				}
				if("id".equals(item.getLabel()) && "7".equals(item.getValue()))
				{
				   putvaluetojsp("report",attributelist,i,dbpre);
				}	
				if("id".equals(item.getLabel()) && "8".equals(item.getValue()))
				{
				   putvaluetojsp("matter",attributelist,i,dbpre);
				}	
				/*if("id".equals(item.getLabel()) && "9".equals(item.getValue()))
				{
				   putvaluetojsp("salary",attributelist,i,dbpre);
				}*/
			}
		}
	}
	private void putvaluetojsp(String pre,ArrayList attributelist,int n,String dbpre) throws GeneralException
	{
		String twinkle="1";
		String scroll="1";	
		this.getFormHM().put("dbpre",dbpre);
		for(int i=0;i<attributelist.size();i++)
		{
			LabelValueView item=(LabelValueView)attributelist.get(i);
		    if("show".equals(item.getLabel())&& "1".equals(item.getValue()))
		    {
		       this.getFormHM().put(pre + "serial",String.valueOf(n));
		       this.getFormHM().put(pre + "isvisible","true");
		    }
		    else
		    {
		    	 this.getFormHM().put(pre + "serial",String.valueOf(n));
			     this.getFormHM().put(pre + "isvisible","false");
		    }
		    if("scroll".equals(item.getLabel()))
		    	scroll=item.getValue();
		    if("twinkle".equals(item.getLabel()))
		    	twinkle=item.getValue();		 
		}
		/**公告栏*/
		if("boardcontent".equals(pre))
			this.getFormHM().put("board",getBoardContent1(twinkle,scroll));
		if("warn".equals(pre))
			this.getFormHM().put("warn",getWarnResult(twinkle,scroll));
		if("muster".equals(pre))
			/**常用花名册列表，人员*/
			if(!(dbpre==null|| "".equals(dbpre)))
			    this.getFormHM().put("muster",getCommonMuster("1",twinkle,scroll));
		if("cond".equals(pre))
			if(!(dbpre==null|| "".equals(dbpre)))
				/**常用条件，人员*/
				this.getFormHM().put("cond",getCommonCond("1",twinkle,scroll));
		if("stat".equals(pre))
			if(!(dbpre==null|| "".equals(dbpre)))
				this.getFormHM().put("stat",getCommonStat("1",twinkle,scroll));
		if("ykcard".equals(pre))
			if(!(dbpre==null|| "".equals(dbpre)))
				this.getFormHM().put("ykcard",getCommonYkcard(twinkle,scroll));
		if("report".equals(pre))
			if(!(dbpre==null|| "".equals(dbpre)))
				this.getFormHM().put("report",getReport(twinkle,scroll));
		if("matter".equals(pre))
			if(!(dbpre==null|| "".equals(dbpre)))   //我的任务
				this.getFormHM().put("matter",getMatter(twinkle,scroll));
		
		
	}	
	private String subText(String text)
	{
		if(text==null||text.length()<=0)
			return "";
		if(text.length()<18)
			return text;
		text=text.substring(0,18)+"...";
		return text;
	}
}
