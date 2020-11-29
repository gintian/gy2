package com.hjsj.hrms.transaction.general.print;
/**
 * 报表页面编辑
 */

import com.hjsj.hrms.businessobject.gz.ReportPageOptionsBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.print.PageOptionsXml;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class PageOptionsXmlTrans extends IBusiness{
	public void execute()throws GeneralException
	{
		HashMap hm=(HashMap)this.getFormHM();
		String id=(String)hm.get("id");		
		String state=(String)hm.get("state");	
		String flag=(String)hm.get("flag");
		if(flag==null||flag.length()<=0)
			flag="";
		if(id==null||id.length()<=0||state==null||state.length()<=0)
		{
			this.getFormHM().put("xmltype","false");
			//String xmlstr=getXML();
		}else
		{
			StringBuffer SQL=new StringBuffer();
			if("0".equals(state))//考勤报表
			{
				String xmlstr="";
				if(flag!=null&& "init".equalsIgnoreCase(flag))
					 xmlstr="";
				else
					xmlstr=getXML();
				SQL.append("update kq_report set ");
				SQL.append("content=? ");
				SQL.append("where report_id=? ");
				
				ArrayList list =new ArrayList();
				list.add(xmlstr);
				list.add(id);
				updateRecord(SQL.toString(),list);
			}else if("1".equals(state))//报表名称
			{   
				//综合报表
				
				String xmlstr="";
				if(flag!=null&& "init".equalsIgnoreCase(flag)) {
					ReportParseVo parsevo=new ReportParseVo();
					PageOptionsXml xml=new PageOptionsXml();
					xmlstr=xml.createPageOptionsXML(parsevo);
				}
				else
					xmlstr=getXML();
				SQL.append("update tname set ");
				SQL.append("xmlstyle=? ");
				SQL.append("where tabid=? ");
				ArrayList list =new ArrayList();
				list.add(xmlstr);
				list.add(id);
				updateRecord(SQL.toString(),list);
			}else if("2".equals(state))
			{
				//常用花名册
				String title=(String)hm.get("title_fw");
				String lhead=(String)hm.get("head_flw");
				String mhead=(String)hm.get("head_fmw");
				String rhead=(String)hm.get("head_frw");
				String lfoot=(String)hm.get("tile_flw");
				String mfoot=(String)hm.get("tile_fmw");
				String rfoot=(String)hm.get("tile_frw");
				String xmlstr="";
				String xml_style="";
				ArrayList list =new ArrayList();
				SQL.append("update lname set ");
				SQL.append("lhead=?,mhead=?,rhead=?,lfoot=?,mfoot=?,rfoot=?,xml_style=?  ");
				SQL.append("where tabid=? ");				
				if(flag!=null&& "init".equalsIgnoreCase(flag))
				{
					list.add("");
					list.add("");
					list.add("");
					list.add("");
					list.add("");
					list.add("");				
					list.add("");
					list.add(id);
				}	 
				else
				{
					xml_style=getXML();					
					list.add(lhead);
					list.add(mhead);
					list.add(rhead);
					list.add(lfoot);
					list.add(mfoot);
					list.add(rfoot);				
					list.add(xml_style);
					list.add(id);
				}	
				
				
				updateRecord(SQL.toString(),list);
			}else if("3".equals(state))
			{
				/****系统零星参数***/
				if(id==null||id.length()<=0)
				{
					this.getFormHM().put("xmltype","false");
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("report.sys.nopkName"),"",""));
				}else
				{
					Sys_Oth_Parameter sys_Oth_Parameter=new Sys_Oth_Parameter();
					ReportParseVo parsevo=setParseVo();
					parsevo.setName(id);
					String xmlstr="";
					if(flag!=null&& "init".equalsIgnoreCase(flag))
						 xmlstr="";
					else
						xmlstr=sys_Oth_Parameter.WriteOutParameterXml(parsevo,id,this.userView,this.getFrameconn());
					/*System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
					System.out.println(xmlstr);*/
					String xmltype=sys_Oth_Parameter.insert_XMLData(this.getFrameconn(),xmlstr);
					this.getFormHM().put("xmltype",xmltype);
				}
				
			}else if("4".equals(state))
			{
				/**工资报表和分析表*/
				String rsid= (String)this.getFormHM().get("rsid");
				String rsdtlid = (String)this.getFormHM().get("rsdtlid");
				int type = 0;
				if("init".equalsIgnoreCase(flag))//初始化
					type = 1;
				ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.getFrameconn(),this.userView,rsid,rsdtlid);
				ReportParseVo rpv = this.setParseVo();
				String xml = rpob.createXML(rpv, type);
				rpob.saveXML(xml);
				this.getFormHM().put("xmltype","ok");
			}
					
		}
		
	}
	public void updateRecord(String SQL,ArrayList list)
	{
		if(SQL.toString()!=null&&SQL.toString().length()>0)
		{
			//String xmlstr=getXML();
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
		    try
		    {
		    	dao.update(SQL.toString(),list);					
				this.getFormHM().put("xmltype","ok");
		    }catch(Exception e)
		    {
		    	this.getFormHM().put("xmltype","false");
		    	e.printStackTrace();
		    }
		}else
		{
			this.getFormHM().put("xmltype","false");
		}	
	}
	/**
	 * 得到ReportParseVo
	 * @return
	 */
	public String  getXML()
	{
		 ReportParseVo parsevo=setParseVo();
		 PageOptionsXml xml=new PageOptionsXml();
		 String xmlstr=xml.createPageOptionsXML(parsevo);
		 return xmlstr;		 
	}
	/**
	 * 组合ReportParseVo
	 * @return
	 */
	public ReportParseVo setParseVo()
	{
		 ReportParseVo parsevo=new ReportParseVo();		
		 HashMap hm=(HashMap)this.getFormHM();			 
         parsevo.setName((String)hm.get("name"));
         String value=(String)hm.get("value");
         if(value==null||value.length()<=0)
         {
        	 value="";
         }
	     parsevo.setValue(value);
	     String pagetype=(String)hm.get("pagetype");
	     if(pagetype==null||pagetype.length()<=0)
	    	 pagetype="A4";
	     parsevo.setPagetype(pagetype);
	     parsevo.setWidth((String)hm.get("width"));
	     parsevo.setHeight((String)hm.get("height"));
	     ArrayList list=(ArrayList)hm.get("orientation");	     
	     if(list.size()>=1)
	     {
	    	 parsevo.setOrientation(list.get(0).toString());
	     }else
	     {
	    	 parsevo.setOrientation("0");
	     }
	     ArrayList unitlist=(ArrayList)hm.get("unit");
	     
	     if(unitlist!=null)
	     {
	    	 if(unitlist.size()>=1)
		     {
		    	 parsevo.setUnit(unitlist.get(0).toString());
		     }else
		     {
		    	 parsevo.setUnit("px");
		     }	
	     }
	          
	     parsevo.setLeft((String)hm.get("left"));
	     parsevo.setRight((String)hm.get("right"));
	     parsevo.setTop((String)hm.get("top"));
	     parsevo.setBottom((String)hm.get("bottom"));	        
	     /**节点,报表标题**/	     
	     parsevo.setTitle_fb((String)hm.get("title_fb"));
	     parsevo.setTitle_fi((String)hm.get("title_fi"));
	     parsevo.setTitle_fu((String)hm.get("title_fu"));
	     parsevo.setTitle_fn((String)hm.get("title_fn"));
	     parsevo.setTitle_fz((String)hm.get("title_fz"));
	     parsevo.setTitle_h((String)hm.get("title_h"));
	     parsevo.setTitle_fw((String)hm.get("title_fw"));	    
	     hm.put("title_fw",enter((String)hm.get("title_fw")));	   //处理换行符  
	     parsevo.setTitle_fs((String)hm.get("title_fs"));//删除线
	     parsevo.setTitle_fc((String)hm.get("title_fc"));//颜色
	     /**节点,报表表头**/	     
	     parsevo.setHead_c((String)hm.get("head_c"));
	     parsevo.setHead_d((String)hm.get("head_d"));
	     parsevo.setHead_e((String)hm.get("head_e"));
	     parsevo.setHead_u((String)hm.get("head_u"));
	     parsevo.setHead_p((String)hm.get("head_p"));
	     parsevo.setHead_t((String)hm.get("head_t"));
	     parsevo.setHead_fb((String)hm.get("head_fb"));
	     parsevo.setHead_fi((String)hm.get("head_fi"));
	     parsevo.setHead_fu((String)hm.get("head_fu"));
	     parsevo.setHead_fn((String)hm.get("head_fn"));
	     parsevo.setHead_fz((String)hm.get("head_fz"));
	     parsevo.setHead_h((String)hm.get("head_h"));
	     parsevo.setHead_fw((String)hm.get("head_fw"));	    
	     hm.put("head_fw",enter((String)hm.get("head_fw")));	   //处理换行符  
	     parsevo.setHead_fs((String)hm.get("head_fs"));//删除
	     parsevo.setHead_fc((String)hm.get("head_fc"));//颜色
	     parsevo.setHead_flw((String)hm.get("head_flw"));//左上
	     hm.put("head_flw",enter((String)hm.get("head_flw")));	   //处理换行符  
	     parsevo.setHead_fmw((String)hm.get("head_fmw"));//左中
	     hm.put("head_fmw",enter((String)hm.get("head_fmw")));	   //处理换行符  
	     parsevo.setHead_frw((String)hm.get("head_frw"));//左下
	     hm.put("head_frw",enter((String)hm.get("head_frw")));	   //处理换行符  
	     /**节点,报表表尾**/	    
	     parsevo.setTile_c((String)hm.get("tile_c"));
	     parsevo.setTile_d((String)hm.get("tile_d"));
	     parsevo.setTile_e((String)hm.get("tile_e"));
	     parsevo.setTile_u((String)hm.get("tile_u"));
	     parsevo.setTile_p((String)hm.get("tile_p"));
	     parsevo.setTile_t((String)hm.get("tile_t"));
	     parsevo.setTile_fb((String)hm.get("tile_fb"));
	     parsevo.setTile_fi((String)hm.get("tile_fi"));
	     parsevo.setTile_fu((String)hm.get("tile_fu"));
	     parsevo.setTile_fn((String)hm.get("tile_fn"));
	     parsevo.setTile_fz((String)hm.get("tile_fz"));
	     parsevo.setTile_h((String)hm.get("tile_h"));
	     parsevo.setTile_fw((String)hm.get("tile_fw"));
	     hm.put("tile_fw",enter((String)hm.get("tile_fw")));	   //处理换行符  
	     parsevo.setTile_flw((String)hm.get("tile_flw"));
	     hm.put("tile_flw",enter((String)hm.get("tile_flw")));	   //处理换行符  
	     parsevo.setTile_frw((String)hm.get("tile_frw"));
	     hm.put("tile_frw",enter((String)hm.get("tile_frw")));	   //处理换行符  
	     parsevo.setTile_fmw((String)hm.get("tile_fmw"));
	     hm.put("tile_fmw",enter((String)hm.get("tile_fmw")));	   //处理换行符  
	     String tile_fc = (String)hm.get("tile_fc");
	     if(tile_fc!=null&&tile_fc.length()>0)	     
	     parsevo.setTile_fc((String)hm.get("tile_fc"));
	     parsevo.setTile_fs((String)hm.get("tile_fs"));
	     /**节点,报表表体**/	     
	     parsevo.setBody_fb((String)hm.get("body_fb"));
	     parsevo.setBody_fi((String)hm.get("body_fi"));
	     parsevo.setBody_fu((String)hm.get("body_fu"));
	     parsevo.setBody_fn((String)hm.get("body_fn"));
	     parsevo.setBody_fz((String)hm.get("body_fz"));
	     ArrayList pr_list=(ArrayList)hm.get("unit");
	     if(pr_list!=null)
	     {
	    	 if(pr_list.size()>=1)
		     {
		    	 parsevo.setBody_pr(pr_list.get(0).toString());
		     }else
		     {
		    	 parsevo.setBody_pr("#pr[0]");
		     } 
	     }	    	     
	     parsevo.setBody_rn((String)hm.get("body_rn"));	
	     parsevo.setBody_fc((String)hm.get("body_fc"));	
	     parsevo.setThead_fb((String)hm.get("thead_fb"));
	     parsevo.setThead_fc((String)hm.get("thead_fc"));
	     parsevo.setThead_fi((String)hm.get("thead_fi"));
	     parsevo.setThead_fn((String)hm.get("thead_fn"));
	     parsevo.setThead_fu((String)hm.get("thead_fu"));
	     parsevo.setThead_fz((String)hm.get("thead_fz"));
	     return parsevo;
	}
    public String enter(String fw)
    {
       /*for(int i=0;i<fw.length();i++)
       {
    	   if((int)fw.charAt(i) == 13)
    	   {
   				System.out.println("Enter=="+i);
   			}
   		
       }	*/
    	if(fw!=null&&fw.length()>0)
    	{
    		String bb=fw.replaceAll("\r\n","");
    	       return bb;
    	}else{
    		return "";
    	}
        
    }
}
