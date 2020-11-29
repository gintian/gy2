package com.hjsj.hrms.transaction.general.print;

import com.hjsj.hrms.businessobject.gz.ReportPageOptionsBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParamterVo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PageOptionsEditTrans extends IBusiness{
	public void execute()throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String state=(String) hm.get("state");
		String id=(String)hm.get("id");	
		/**以下两个参数是工资报表中用到*/
		String rsid="";
		String rsdtlid ="";
		if(id==null||id.length()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.nopkid"),"",""));
		}else
		{
			id=SafeCode.decode(id);
		}
		if(state==null||state.length()<=0)
		{
			state="1";
			
		}
		if("1".equals(state))
		{
			setTnameParseVo(id);
		}else if("2".equals(state))
		{
			/*******常用花名册*********/
			setLnameParseVo(id);
		}else if("3".equals(state))
		{
			if(id==null||id.length()<=0)
			{
				return;
			}else
			{
				setSysParseVo(id);
			}
			
		}else if("4".equals(state))
		{
			rsid=(String)hm.get("rsid");
			rsdtlid = (String)hm.get("rsdtlid");
			this.setSalaryReportParseVo(rsid, rsdtlid);
		}
		else
		{
			this.getFormHM().put("sytle_title","height:70px;width:70%;");
		    this.getFormHM().put("sytle_head","height:40px;width:70%;");
		    this.getFormHM().put("sytle_tile","height:40px;width:70%;");
		}
		this.getFormHM().put("state",state);
		this.getFormHM().put("id",id);
		this.getFormHM().put("rsid",rsid);
		this.getFormHM().put("rsdtlid",rsdtlid);
	
	}
	public void setTnameParseVo(String id)
	{
		StringBuffer sql= new StringBuffer();
		   sql.append("select xmlstyle ");
		   sql.append(" from tname where tabid='"+id+"'");
		   String sytle_title="height:70px;width:70%;";
		   String sytle_head="height:40px;width:70%;";
		   String sytle_tile="height:40px;width:70%;";
		   ContentDAO dao = new ContentDAO(this.getFrameconn());
		   ReportParseVo parsevo=new ReportParseVo();
			try
		    {
		    	this.frowset=dao.search(sql.toString());
		    	if(this.frowset.next())
		    	{	    		
		    		String content=this.getFrowset().getString("xmlstyle");		    		
		    		if(content!=null&&content.length()>0)
		    		{
		    			ReportParseXml parseXml = new ReportParseXml();
		    			String kq_xpath="/report";
		    			parsevo=parseXml.ReadOutParseXml(content,kq_xpath);	
		    			
		    		}
		    		sytle_title=sytle_title+getStyle("title",parsevo);
		    		sytle_head=sytle_head+getStyle("head",parsevo);
		    		sytle_tile=sytle_tile+getStyle("tile",parsevo);
		    	}
		    }catch(Exception e)
		    {
		    	e.printStackTrace();
		    }	
		    this.getFormHM().put("parsevo",parsevo);
		    this.getFormHM().put("sytle_title",sytle_title);
		    this.getFormHM().put("sytle_head",sytle_head);
		    this.getFormHM().put("sytle_tile",sytle_tile);
	}
   public void setLnameParseVo(String id)
   { 
	   StringBuffer sql= new StringBuffer();
	   sql.append("select title,lhead,mhead,rhead,lfoot,mfoot,rfoot,xml_style ");
	   sql.append(" from lname where tabid='"+id+"'");
	   String sytle_title="height:70px;width:70%;";
	   String sytle_head="height:40px;width:70%;";
	   String sytle_tile="height:40px;width:70%;";
	   ContentDAO dao = new ContentDAO(this.getFrameconn());
	   ReportParseVo parsevo=new ReportParseVo();
		try
	    {
	    	this.frowset=dao.search(sql.toString());
	    	if(this.frowset.next())
	    	{	    		
	    		String content=this.getFrowset().getString("xml_style");
	    		parsevo.setTitle_fw(this.getFrowset().getString("title"));
	    		parsevo.setHead_flw(this.getFrowset().getString("lhead"));
	    		parsevo.setHead_fmw(this.getFrowset().getString("mhead"));
	    		parsevo.setHead_frw(this.getFrowset().getString("rhead"));
	    		parsevo.setTile_flw(this.getFrowset().getString("lfoot"));
	    		parsevo.setTile_fmw(this.getFrowset().getString("mfoot"));
	    		parsevo.setTile_frw(this.getFrowset().getString("rfoot"));	    		
	    		if(content!=null&&content.length()>0)
	    		{
	    			ReportParseXml parseXml = new ReportParseXml();
	    			String kq_xpath="/report";
	    			parsevo=parseXml.ReadOutParseXml(content,kq_xpath);	
	    			
	    		}
	    		sytle_title=sytle_title+getStyle("title",parsevo);
	    		sytle_head=sytle_head+getStyle("head",parsevo);
	    		sytle_tile=sytle_tile+getStyle("tile",parsevo);
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }	
	    this.getFormHM().put("parsevo",parsevo);
	    this.getFormHM().put("sytle_title",sytle_title);	   
	    this.getFormHM().put("sytle_head",sytle_head);
	    this.getFormHM().put("sytle_tile",sytle_tile);
   }
   public void setSalaryReportParseVo(String rsid,String rsdtlid)
   {
	   try
	   {
		   ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.getFrameconn(),this.userView,rsid,rsdtlid);
		   String sytle_title="height:70px;width:70%;";
		   String sytle_head="height:40px;width:70%;";
		   String sytle_tile="height:40px;width:70%;";
		   ReportParseVo parsevo=new ReportParseVo();
		   if(rpob.isHavePageOptions(1))
		   {
			   rpob.init();
			   parsevo = rpob.analyse(1);
			   sytle_title=sytle_title+getStyle("title",parsevo);
	    	   sytle_head=sytle_head+getStyle("head",parsevo);
	    	   sytle_tile=sytle_tile+getStyle("tile",parsevo);
		   }
		   else
			   /**工资报表中，默认字体大小为8而不是5*/
		   {
			   parsevo.setBody_fz("10");
			   parsevo.setHead_fz("10");
			   parsevo.setTile_fz("10");
			   parsevo.setTitle_fz("16");
			   parsevo.setTitle_fb("#fb[1]");
			   parsevo.setTitle_fw("&[单位名称]&[YYYY年YY月]&[报表名称]");
			   parsevo.setThead_fz("10");
		   }
		   
		   this.getFormHM().put("parsevo",parsevo);
		   this.getFormHM().put("sytle_title",sytle_title);	   
		   this.getFormHM().put("sytle_head",sytle_head);
		   this.getFormHM().put("sytle_tile",sytle_tile);
	   }
	   catch(Exception e)
	   {
		   e.printStackTrace();
	   }
   }
   public String getStyle(String tag,ReportParseVo parsevo)
   {
	   StringBuffer style=new StringBuffer();	   
	   if("title".equals(tag))
	   {
		   String sytle_t=getFontStyle(parsevo.getTitle_fn(),parsevo.getTitle_fi(),parsevo.getTitle_fu(),parsevo.getTitle_fs(),parsevo.getTitle_fb(),parsevo.getTitle_fz(),parsevo.getTitle_fc());
		   style.append(sytle_t);
	   }else if("head".equals(tag))
	   {
		  String sytle_h=getFontStyle(parsevo.getHead_fn(),parsevo.getHead_fi(),parsevo.getHead_fu(),parsevo.getHead_fs(),parsevo.getHead_fb(),parsevo.getHead_fz(),parsevo.getHead_fc()); 
		   style.append(sytle_h);
	   }else if("tile".equals(tag))
	   {
		   String sytle_t=getFontStyle(parsevo.getTile_fn(),parsevo.getTile_fi(),parsevo.getTile_fu(),parsevo.getTile_fs(),parsevo.getTile_fb(),parsevo.getTile_fz(),parsevo.getTile_fc()); 
		   style.append(sytle_t);
	   }
	   return style.toString();
   }
   
   public String getFontStyle(String fn,String fi,String fu,String fs,String fb,String fz,String fc)
   {
   	StringBuffer style= new StringBuffer();
   	if(fn!=null&&fn.length()>0)
   	{
   	    style.append("font-family: '"+fn+"';");
   	}else
   	{
   		style.append("font-family: '宋体';");
   	}
   	if(fi!=null&&fi.length()>0)
   	{
   		if("#fi[1]".equals(fi))//斜体
   	   	{
   	   		style.append("font-style: italic;");
   	   	}
   	}
   	if(fu==null)
   		fu="";
   	if(fs==null)
   		fs="";
   	if((fu.length()>0&& "#fu[1]".equals(fu))||(fs.length()>0&& "#fs[1]".equals(fs)))
   	{
   		style.append("text-decoration:");
   		if("#fu[1]".equals(fu))//下划线
   	   	{
   	   		style.append(" underline");
   	   	}
   		if("#fs[1]".equals(fs))//删除线
   	   	{
   	   		style.append(" line-through");
   	   	}
   		style.append(";");
   	}
   	if(fb!=null&&fb.length()>0)
   	{
   		if("#fb[1]".equals(fb))
   	   	{
   	   		style.append("font-weight: bolder;");
   	   	}
   	}
   	
   	if(fz!=null&&fz.length()>0)
   	{
   		style.append("font-size: "+fz+"pt;");
   	}else
   	{
   		style.append("font-size: 12pt;");
   	}
   	if(fc!=null&&fc.length()>0)
   	{
   		style.append("color: "+fc+";");
   	}else{
   		style.append("color: #000000;");
   	}
   	return style.toString();
   }
   
   /*********系统其他报表*************/
   public void setSysParseVo(String name)
	{
	   Sys_Oth_Parameter sys_Oth_Parameter=new Sys_Oth_Parameter();
	   String xmlConstant=sys_Oth_Parameter.search_SYS_PARAMETER(this.getFrameconn());
	   /*System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
	   System.out.println(xmlConstant);*/
	   ReportParamterVo paramtervo=sys_Oth_Parameter.ReadOneParameterXml(xmlConstant,name);
	   String sytle_title="height:70px;width:70%;";
	   String sytle_head="height:40px;width:70%;";
	   String sytle_tile="height:40px;width:70%;";
	   ReportParseXml parseXml = new ReportParseXml();
	   ReportParseVo parsevo=parseXml.parameterToParse(paramtervo);	  
	   sytle_title=sytle_title+getStyle("title",parsevo);
	   sytle_head=sytle_head+getStyle("head",parsevo);
	   sytle_tile=sytle_tile+getStyle("tile",parsevo);
	   this.getFormHM().put("parsevo",parsevo);
	   this.getFormHM().put("sytle_title",sytle_title);
	   this.getFormHM().put("sytle_head",sytle_head);
	   this.getFormHM().put("sytle_tile",sytle_tile);
	}
}
