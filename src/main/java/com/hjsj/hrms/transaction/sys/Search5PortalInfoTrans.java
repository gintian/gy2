/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hjsj.hrms.businessobject.general.template.MatterTaskList;
import com.hjsj.hrms.businessobject.report.auto_fill_report.ReportBulletinList;
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
 * <p>Title:HJ-eHR5.0门户交易</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 8, 2008:3:05:09 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class Search5PortalInfoTrans extends IBusiness {

	/**
	 * 取得应用库（授权的应用库中第一个库前缀）
	 * @return
	 */
	private int rownuw=7;
	private String getDbPre()
	{
/*		ArrayList dblist=null;
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
		return pre;*/
		ArrayList dblist=null;
		String pre="";		
		try
		{
			dblist=this.userView.getPrivDbList();
			if(dblist!=null&&dblist.size()>0){
				pre=(String)dblist.get(0);
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return pre;
	}

	/**
	 * 公告栏
	 * @param twinkle
	 * @param scroll
	 * @return
	 * @throws GeneralException
	 */
	private String getBoardContent(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
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
		int view=this.rownuw;
    	if("1".equals(scroll))
    		view=100;
		try 
		{
			
			//content.append("<table width=\"99%\" border=\"0\"  cellspacing=\"0\" align=\"center\" cellpadding=\"0\" >\n");
			//content.append("		<tr ><td valign=\"top\">");
			//content.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" >\n");
			content.append("<ul >");
			int i=1;
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				String id=this.getFrowset().getString("id");
				if(!(this.userView.isHaveResource(IResourceConstant.ANNOUNCE,id)))
				{
							continue;
				}
				if(i>view)
					break;
				days=this.frowset.getInt("days");
				//content.append("		<tr><td class=\"RecordRow\" >");
				content.append("<li class=\"board_li\">");
				if("1".equals(twinkle))
				{
					if(days>announce_days)
						content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" >"+/*i+". "+*/subText(this.frowset.getString("topic"))+"("+PubFunc.nullToStr(this.frowset.getString("viewcount"))+"次)</a>");
					else
						content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" >"+/*i+". "+*/subText(this.frowset.getString("topic"))+"("+PubFunc.nullToStr(this.frowset.getString("viewcount"))+"次)<img src='/images/new0.gif' border='0'></a>");
				}
				else
				{
					if(days>announce_days)
						content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" >"+/*i+". "+*/subText(this.frowset.getString("topic"))+"("+PubFunc.nullToStr(this.frowset.getString("viewcount"))+"次)</a>");
					else
						content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href=\"/selfservice/welcome/welcome.do?b_view=link&a_id="+ this.frowset.getString("id")+"\" >"+/*i+". "+*/subText(this.frowset.getString("topic"))+"("+PubFunc.nullToStr(this.frowset.getString("viewcount"))+"次)<img src='/images/new0.gif' border='0'></a>");
				}
			    //content.append("		</td></tr>\n");
				content.append("</li>");
				++i;					
			}			
			content.append("</ul>");
			content=autoScroll(content.toString(),scroll);			
			if(i>1)
			{
				if(i>=this.rownuw)	
				{
					/*for(int r=0;r<7-i;r++)
					{
						content.append("<li class=\"board_li\">");
						content.append("&nbsp;");
						content.append("</li>");
					}*/
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/selfservice/welcome/boardTheMore.do?b_more=link\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				return content.toString();
			}
				
			else
				return "";
		}
		catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} 
	}
	/**
	 * 自动滚动
	 * @param content
	 * @param bscroll
	 * @return
	 */
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
			/*
			str.append("<table width=\"99%\" height=\"150\">");
			str.append("<tr>");
			str.append("<td>");
			str.append(content);
			str.append("</td>");
			str.append("</tr>");
			str.append("</table>");
			*/
			str.append(content);
    	}
		//System.out.println(str.toString());
		return str;
	}
	/**
	 * 预警提示
	 * @param twinkle
	 * @param scroll
	 * @return
	 * @throws GeneralException
	 */
	private String getWarnResult(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
		try
		{
			ScanTotal st = new ScanTotal(this.userView);
			ArrayList alTotal = st.execute();
			int iRows = alTotal.size();//alTotal.size()>5?5:alTotal.size();	
			int r=1;	
			int view=this.rownuw;
	    	if("1".equals(scroll))
	    		view=100;
			content.append("<ul >");
			for( int i=0; i<iRows; i++){
				CommonData cData = (CommonData)alTotal.get(i);
				if(r>view)
					break;
				content.append("<li class=\"board_li\">");
				if("1".equals(twinkle))
					content.append("		&nbsp;<img src=\"/images/forumme.gif\"> <a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+ cData.getDataValue()+"&ver=5\">"+/*(i+1)+". "+*/subText(cData.getDataName())+"</a>");
				else
					content.append("		&nbsp;<img src=\"/images/forumme1.gif\"> <a href=\"/system/warn/result_manager.do?b_query=link&warn_wid="+ cData.getDataValue()+"&ver=5\">"+/*(i+1)+". "+*/subText(cData.getDataName())+"</a>");
				content.append("</li>");				
				r++;
			}
//			if( alTotal.size()>4 ){
//				content.append("		<tr class=\"trDeep\"><td class=\"RecordRow\" align=\"right\"><a href=\"/system/warn/info_all.do?br_query=link\">>>更多(共"+alTotal.size()+"项)</a></td></tr>");
//			}
			//if(iRows>0)
			//	content.append("</table>");			
			content.append("</ul>");
			content=autoScroll(content.toString(),scroll);
			if(iRows>0)
			{
				if(r>=this.rownuw)	
				{
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/system/warn/info_all.do?br_query=link&ver=5\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				return content.toString();
			}
				
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
        if(type==null|| "".equals(type))
        	type="1";
        int view=this.rownuw;
    	if("1".equals(scroll))
    		view=100;
        try
		{

			//content.append("<table width=\"99%\" border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" class=\"ListTable\">\n");
			content.append("<ul>");
			MusterBo musterbo=new MusterBo(this.getFrameconn());
			ArrayList musterlist=musterbo.getMusterList(type);
        	int i=0;
        	int j=0;
        	int k=0;
        	int r=1;
        	for(i=0;i<musterlist.size();i++)
        	{
				RecordVo mustvo=(RecordVo)musterlist.get(i);	  
				String hzname=mustvo.getString("hzname");        		
        		if(!(this.userView.isHaveResource(IResourceConstant.MUSTER,mustvo.getString("tabid"))))
        			continue;           		
        		if(r>view)
					break;
        		j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				//content.append("		<tr class=\"trDeep\"\"><td class=\"RecordRow\" >");
				content.append("<li class=\"board_li\">");
				if("1".equals(twinkle))
				    content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/muster/muster_list.do?b_open=0&checkflag=2&a_inforkind=1&tabid="+ mustvo.getString("tabid")+"&ver=5\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				else
					content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/muster/muster_list.do?b_open=0&checkflag=2&a_inforkind=1&tabid="+ mustvo.getString("tabid")+"&ver=5\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				
				//content.append("		</td></tr>\n");
				content.append("</li>");
				++k; 
        		r++;
				
        	}
        	content.append("</ul>");
			content=autoScroll(content.toString(),scroll);
			if(k>=1)
			{
				if(r>=this.rownuw)	
				{
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/general/muster/muster_list.do?b_query=link&checkflag=2&a_inforkind=1&ver=5\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				
				return content.toString();
			}
				
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
	 * 常用条件
	 * @param type
	 * @param twinkle
	 * @param scroll
	 * @return
	 * @throws GeneralException
	 */
	private String getCommonCond(String type,String twinkle,String scroll) throws GeneralException
	{
		StringBuffer content=new StringBuffer();
        StringBuffer strsql=new StringBuffer();
        if(type==null|| "".equals(type))
        	type="1";
        strsql.append("select id,name,type from lexpr where type='");//
        strsql.append(type);
        strsql.append("' order by id");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        int view=this.rownuw;
    	if("1".equals(scroll))
    		view=100;
        try
		{
			//content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" class=\"ListTable\">\n");
        	content.append("<ul>");
			this.frowset=dao.search(strsql.toString());
        	int i=1;
        	while( this.frowset.next())
        	{
        		if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
        			continue;
        		if(i>view)
					break;
        		content.append("<li class=\"board_li\">");
				if("1".equals(twinkle))
				    content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/workbench/query/gquery_interface.do?b_query=link&home=4&type=1&ver=5&curr_id="+ this.frowset.getString("id")+"\")'>"+/*i+". "+*/subText(this.frowset.getString("name"))+"</a>");
				else
					content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/workbench/query/gquery_interface.do?b_query=link&home=4&type=1&ver=5&curr_id="+ this.frowset.getString("id")+"\")'>"+/*i+". "+*/subText(this.frowset.getString("name"))+"</a>");
			
				//content.append("		</td></tr>\n");
				content.append("</li>");
				++i;
        	}
//			if(i>4 ){
//				content.append("		<tr ><td class=\"rowbodyp\"  align=\"right\"><a href=\"/workbench/query/query_interface.do?home=1&b_gquery=link\" target=\"_self\">>>更多(共"+i+"项)</a></td></tr>");
//			}
			//content.append("</table>");	        	
			content.append("</ul>");
			content=autoScroll(content.toString(),scroll);
			if(i>1)
			{
				if(i>=this.rownuw)	
				{
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/workbench/query/query_interface.do?home=1&b_gquery=link&ver=5\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				return content.toString();
			}				
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
        try
		{
        	StringBuffer sql=new StringBuffer();
        
        	sql.append("select * from sname where infokind=1  order by snorder");
			//content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" class=\"ListTable\">\n");
        	content.append("<ul>");
			//List statlist=ExecuteSQL.executeMyQuery(sql.toString(),this.getFrameconn());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql.toString());
        	int i=1;
        	int j=0;
        	int view=this.rownuw;
        	if("1".equals(scroll))
        		view=100;
        	while( this.frowset.next())
        	{
        		if(!(this.userView.isHaveResource(IResourceConstant.STATICS,this.frowset.getString("id"))))
        			continue;            		
        		if(i>view)
					break;  
				String hzname=this.frowset.getString("name");//(String)statvo.get("name");
				j=hzname.indexOf(".");
				hzname=hzname.substring(j+1);
				//content.append("		<tr class=\"trDeep\"\"><td class=\"RecordRow\" >");
				content.append("<li class=\"board_li\">");
				if("1".equals(this.frowset.getString("type")))
					if("1".equals(twinkle))
					    content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_chart=link&querycond=&infokind=1&isshowstatcond=1&ver=5&home=1&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
					else
						 content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_chart=link&querycond=&infokind=1&isshowstatcond=1&ver=5&home=1&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
			    else
					if("1".equals(twinkle))
					   content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&infokind=1&isshowstatcond=1&ver=5&home=1&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");				
					else
						content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/static/commonstatic/statshow.do?b_doubledata=data&querycond=&infokind=1&isshowstatcond=1&ver=5&home=1&statid="+ this.frowset.getString("id")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");				
				content.append("</li>");				 
        		i++;
        	}        	
        	content.append("</ul>");
			content=autoScroll(content.toString(),scroll);
			if(i>1)
			{
				if(i>=this.rownuw)	
				{
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&isshowstatcond=1&home=1&ver=5\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				return content.toString();
			}				
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
	 * 取得常用登记表
	 * @return
	 * @throws GeneralException
	 */
	private String getCommonYkcard(String twinkle,String scroll)throws GeneralException
	{
		StringBuffer content=new StringBuffer();
        try
		{
        	
        	StringBuffer sql=new StringBuffer();
        	sql.append("select * from rname");
			//content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" class=\"ListTable\">\n");
        	content.append("<ul>");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(sql.toString());
        	int i=1;
        	int j=0;
        	int view=this.rownuw;
        	if("1".equals(scroll))
        		view=100;
        	while( this.frowset.next())
        	{
        		if(!(this.userView.isHaveResource(IResourceConstant.CARD,this.frowset.getString("tabid"))))
        			continue;        		
        		if(i>view)
					break;
				  String hzname=this.frowset.getString("name");//(String)cardvo.get("name");
				  j=hzname.indexOf(".");
				  hzname=hzname.substring(j+1);
				  //content.append("		<tr class=\"trDeep\"\"><td class=\"RecordRow\" >");
				  content.append("<li class=\"board_li\">");
				  if("1".equals(twinkle))
				    content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/general/card/searchshowcard.do?b_show=link&ver=5&inforkind=1&home=5&tabid="+ this.frowset.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				  else
				    content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/general/card/searchshowcard.do?b_show=link&ver=5&inforkind=1&home=5&tabid="+ this.frowset.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
				  //content.append("		</td></tr>\n");   
				  content.append("</li>");
        		i++;
        	}        	
        	content.append("</ul>");
			content=autoScroll(content.toString(),scroll);
			if(i>1)
			{
				if(i>=this.rownuw)	
				{
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/general/card/searchcard.do?b_query=link&home=5&inforkind=1&ver=5\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				return content.toString();
			}				
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
        try
		{
        	int view=this.rownuw;
        	if("1".equals(scroll))
        		view=100;
			//content.append("<table width=\"99%\"  border=\"0\" cellspacing=\"0\" align=\"center\" cellpadding=\"1\" class=\"ListTable\">\n");
        	content.append("<ul>");
			ReportBulletinList reportBulletinList=new ReportBulletinList(this.getFrameconn());
			ArrayList reportList=reportBulletinList.getReportList(this.getUserView());
			ArrayList customList=reportBulletinList.getCustomReportList(this.getUserView());
        	int i=0;
        	int j=0;
        	int r=1;
        	if(reportList!=null)
        	{
	        	for(i=0;i<reportList.size();i++)
	        	{        	
					RecordVo temp=(RecordVo)reportList.get(i);	 	        		
	        		if(!(this.userView.isHaveResource(IResourceConstant.REPORT,temp.getString("tabid"))))
	        			continue;  	        			        		
	        		if(r>view)
						break;
	        		String hzname=temp.getString("name");
					j=hzname.indexOf(".");
					hzname=hzname.substring(j+1);
					//content.append("		<tr class=\"trDeep\"\"><td class=\"RecordRow\"  >");
					content.append("<li class=\"board_li\">");
					if("1".equals(twinkle))
				    	content.append("			&nbsp;<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\"/report/edit_report/reportSettree.do?b_query2=query&operateObject=1&operates=1&status=1&ver=5&flag=1&code="+ temp.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
					else
						content.append("			&nbsp;<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\"/report/edit_report/reportSettree.do?b_query2=query&operateObject=1&operates=1&status=1&ver=5&flag=1&code="+ temp.getString("tabid")+"\")'>"+/*(i+1)+". "+*/subText(hzname)+"</a>");
					content.append("</li>");
	        		r++;	
	        	}    
        	}    
        	
				if(r>=view)	
				{
					
				}else{
					if(customList!=null&&customList.size()>0){
						for(int a=0;a<customList.size();a++)
			        	{        	
							LazyDynaBean temp=(LazyDynaBean)customList.get(a);	 	        		
			        		if(r>view)
								break;
			        		String hzname=""+temp.get("name");
							j=hzname.indexOf(".");
							hzname=hzname.substring(j+1);
							//content.append("		<tr class=\"trDeep\"\"><td class=\"RecordRow\"  >");
							content.append("<li class=\"board_li\">");
							if("1".equals(twinkle)){
						    	content.append("			&nbsp;<img src=\"/images/forumme.gif\"> ");
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
						    	content.append("			&nbsp;<img src=\"/images/forumme.gif\"> ");
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
							content.append("</li>");
			        		r++;	
			        	}    
					}
				}
			
        	content.append("</ul>");
			content=autoScroll(content.toString(),scroll);
			if(r>1)
			{
				if(r>=this.rownuw)	
				{
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&ver=5\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				return content.toString();
			}				
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
        try
		{
        	int i=0;
        	int j=0;
        	content.append("<ul>");
			MatterTaskList matterTaskList=new MatterTaskList(this.getFrameconn(),this.userView);
			matterTaskList.setReturnflag("8");
			ArrayList matterList=new ArrayList();
        	matterList=matterTaskList.getWaitTaskList(matterList);
        //	matterList=matterTaskList.getInstanceList(matterList);    
        	matterList=matterTaskList.getTmessageList(matterList);   
        	if(matterList!=null)
        	{
	        	for(i=0;i<matterList.size();i++)
	        	{        	
	        		CommonData cData=(CommonData)matterList.get(i);
	        		content.append("<li class=\"board_li\">");
					if("1".equals(twinkle))
					    content.append("			<img src=\"/images/forumme.gif\"> <a href='javascript:openlink(\""+ cData.getDataValue()+"\")'>"+cData.getDataName()+"</a>");
					else
					    content.append("			<img src=\"/images/forumme1.gif\"> <a href='javascript:openlink(\""+ cData.getDataValue()+"\")'>"+cData.getDataName()+"</a>");
					content.append("</li>"); 
	        	}	        	
        	}
        	content.append("</ul>");
			content=autoScroll(content.toString(),scroll);
			if(i>=1)
			{
				if(i+1<matterList.size())	
				{
					content.append("<p class=\"board_lri\">");
					content.append("<a href=\"/general/template/search_bs_tree.do?b_query=link&type=1&res_flag=7&module=20\" >更多</a>&nbsp;&nbsp;&nbsp;&nbsp;");
					content.append("</p>");
				}
				return content.toString();
			}
				
			else
				return "";
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}		
	}
	private String putvaluetojsp(String pre,ArrayList attributelist,int n,String dbpre) throws GeneralException
	{
		String twinkle="1";
		String scroll="1";	
		this.getFormHM().put("dbpre",dbpre);
		String value="";
		boolean isShow=false;
		for(int i=0;i<attributelist.size();i++)
		{
			LabelValueView item=(LabelValueView)attributelist.get(i);
		    if("show".equals(item.getLabel())&& "1".equals(item.getValue()))
		    {
		       this.getFormHM().put(pre + "serial",String.valueOf(n));
		       this.getFormHM().put(pre + "isvisible","true");
		       isShow=true;
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
		if("boardcontent".equals(pre)&&isShow)
			value=getBoardContent(twinkle,scroll);
		if("warn".equals(pre)&&isShow)
			value=getWarnResult(twinkle,scroll);
		if("muster".equals(pre)&&isShow)
		/**常用花名册列表，人员*/
		if(!(dbpre==null|| "".equals(dbpre)))
				value=getCommonMuster("1",twinkle,scroll);
		if("cond".equals(pre)&&isShow)
			if(!(dbpre==null|| "".equals(dbpre)))
				/**常用条件，人员*/
				value=getCommonCond("1",twinkle,scroll);
		if("stat".equals(pre)&&isShow)
			if(!(dbpre==null|| "".equals(dbpre)))
				value=getCommonStat("1",twinkle,scroll);
		if("ykcard".equals(pre)&&isShow)
			if(!(dbpre==null|| "".equals(dbpre)))
				value=getCommonYkcard(twinkle,scroll);
		if("report".equals(pre)&&isShow)
			if(!(dbpre==null|| "".equals(dbpre)))
				value=getReport(twinkle,scroll);
		if("matter".equals(pre)&&isShow)
			if(!(dbpre==null|| "".equals(dbpre)))
				value=getMatter(twinkle,scroll);
		return value;
	}
	
	/**
	 * 取得对应的面板内容
	 * @param dbpre
	 * @throws GeneralException
	 */
	private ArrayList setShowItem(String dbpre) throws GeneralException
	{
		ArrayList nodeslist=new PortalTailorXml().ReadOutParameterXml("SYS_PARAM",this.getFrameconn(),this.userView.getUserName());
		String value="";
		ArrayList portallist=new ArrayList();
		ArrayList morelist=new ArrayList();
		CommonData co=null;
		boolean isShow=false;
		for(int i=0;i<nodeslist.size();i++)
		{
			ArrayList attributelist=(ArrayList)nodeslist.get(i);			
			isShow=false;
			for(int j=0;j<attributelist.size();j++)
			{
				LabelValueView item=(LabelValueView)attributelist.get(j);
				if("show".equals(item.getLabel())&& "1".equals(item.getValue()))
				{
					isShow=true;
				}
			}
			for(int j=0;j<attributelist.size();j++)
			{
				LabelValueView item=(LabelValueView)attributelist.get(j);
				
				if("id".equals(item.getLabel()) && "1".equals(item.getValue()))
				{
					value=putvaluetojsp("boardcontent",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"board");
						portallist.add(obj);
						co=new CommonData("\"/selfservice/welcome/boardTheMore.do?b_more=link&ver=5\"","\"board\"");
						morelist.add(co);
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","board");
						portallist.add(obj);
					}
				}
				if("id".equals(item.getLabel()) && "2".equals(item.getValue()))
				{
					value=putvaluetojsp("warn",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"warn");
						portallist.add(obj);
						co=new CommonData("\"/system/warn/info_all.do?br_query=link&ver=5\"","\"warn\"");
						morelist.add(co);						
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","warn");
						portallist.add(obj);
					}					
				}	    			
				if("id".equals(item.getLabel()) && "3".equals(item.getValue()))
				{
					value=putvaluetojsp("muster",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"muster");
						portallist.add(obj);
						co=new CommonData("\"/general/muster/muster_list.do?b_query=link&checkflag=1&a_inforkind=1&ver=5\"","\"muster\"");
						morelist.add(co);
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","muster");
						portallist.add(obj);						
					}					
				}
				if("id".equals(item.getLabel()) && "4".equals(item.getValue()))
				{
					value=putvaluetojsp("cond",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"cond");
						portallist.add(obj);
						co=new CommonData("\"/workbench/query/query_interface.do?home=1&b_gquery=link&ver=5\"","\"cond\"");
						morelist.add(co);							
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","cond");
						portallist.add(obj);
					}
				}
				if("id".equals(item.getLabel()) && "5".equals(item.getValue()))
				{
					value=putvaluetojsp("stat",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"stat");
						portallist.add(obj);
						co=new CommonData("\"/general/static/commonstatic/statshow.do?b_ini=link&infokind=1&isshowstatcond=1&home=1&ver=5\"","\"stat\"");
						morelist.add(co);						
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","stat");
						portallist.add(obj);
					}						
				}
				if("id".equals(item.getLabel()) && "6".equals(item.getValue()))
				{
					value=putvaluetojsp("ykcard",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"ykcard");
						portallist.add(obj);
						co=new CommonData("\"/general/card/searchcard.do?b_query=link&home=5&inforkind=1&ver=5\"","\"ykcard\"");
						morelist.add(co);
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","ykcard");
						portallist.add(obj);
					}						
				}
				if("id".equals(item.getLabel()) && "7".equals(item.getValue()))
				{
					value=putvaluetojsp("report",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"report");
						portallist.add(obj);
						co=new CommonData("\"/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&ver=5\"","\"report\"");
						morelist.add(co);
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","report");
						portallist.add(obj);
					}				
				}	
				if("id".equals(item.getLabel()) && "8".equals(item.getValue()))
				{
					value=putvaluetojsp("matter",attributelist,i,dbpre);
					if(value.length()>0)
					{
						LabelValueView obj=new LabelValueView(value,"matter");
						portallist.add(obj);
						co=new CommonData("\"/general/template/search_bs_tree.do?b_query=link&type=1&res_flag=7&module=20\"","\"matter\"");
						morelist.add(co);
					}else if(isShow)
					{
						LabelValueView obj=new LabelValueView("","matter");
						portallist.add(obj);
					}					
				}
			}
		}
		this.getFormHM().put("morelist", morelist);
		return portallist;
	}
	
	public void execute() throws GeneralException {
		try
		{
			String dbpre=getDbPre();
			this.getFormHM().put("dbpre",dbpre);
			/*ArrayList portallist=setShowItem(dbpre);
			JSONArray jsonArray = JSONArray.fromObject( portallist );  
			
			//System.out.println("-->"+jsonArray.toString());
			this.getFormHM().put("board",jsonArray.toString());*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private String subText(String text)
	{
		if(text==null||text.length()<=0)
			return "";
		if(text.length()<20)
			return text;
		text=text.substring(0,20)+"...";
		return text;
	}

}
