package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 业务分类
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 15, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SearchModuleTemplateTrans extends IBusiness {
	public void execute() throws GeneralException {
		String operationcode=(String)this.getFormHM().get("operationcode");
		String operationname=(String)this.getFormHM().get("operationname");
		String staticid=(String)this.getFormHM().get("staticid");
		this.getFormHM().put("operationname", operationname);
		this.getFormHM().put("staticid", staticid);
		if(operationname==null||operationname.length()<=0)
			return;
		/*if(operationcode==null||operationcode.length()<=0)
			return ;*/
		operationname=SafeCode.decode(operationname);
		try
		{
			/*Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			*//**如果未定义，则按企业性质*//*
			String unittype=sysbo.getValue(Sys_Oth_Parameter.UNITTYPE,"type");
			if(unittype==null||unittype.equals(""))
				unittype="3";
			ArrayList list=getTemplateList(unittype,operationcode);*/
			String res_flag = (String)this.getFormHM().get("res_flag");
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String template_ids = (String)hm.get("template_ids");
			ArrayList list=getBuisTemplatelList(operationname,staticid, res_flag, template_ids);
			this.getFormHM().put("templist",list);
		}catch(Exception e)
		{
	       e.printStackTrace();
		}
	}
	private ArrayList getBuisTemplatelList(String operationname,String staticid,String res_flag,String template_ids)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		SubsysOperation subsysOperation=new SubsysOperation(this.getFrameconn(),this.userView);
		
		String codes = "";
		if(StringUtils.isNotEmpty(res_flag)&&"38".equals(res_flag))
			codes = template_ids;
		else 
			codes=subsysOperation.getView_value(staticid, operationname);
		if("60".equals(staticid)) //考勤业务办理
		{
			try
			{
				TemplateTableParamBo tp=new TemplateTableParamBo(this.getFrameconn());  
				if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.overtimeApply"))) //加班
				{
					String tabids=tp.getAllDefineKqTabs(1);  
					if(tabids.length()>0)
						codes=tabids.substring(1);
				}
				if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.leavetimeApply"))) //请假
				{
					String tabids=tp.getAllDefineKqTabs(2);  
					if(tabids.length()>0)
						codes=tabids.substring(1);
				}
				if(operationname.equalsIgnoreCase(ResourceFactory.getProperty("general.template.officetimeApply"))) //公出
				{
					String tabids=tp.getAllDefineKqTabs(3);  
					if(tabids.length()>0)
						codes=tabids.substring(1);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(codes!=null&&codes.length()>0)
		{
			String opertationcodes[]=codes.split(",");
			StringBuffer buf=new StringBuffer();
			for(int i=0;i<opertationcodes.length;i++)
			{
				buf.append("'"+opertationcodes[i]+"',");
			}
			buf.setLength(buf.length()-1);
			StringBuffer sql=new StringBuffer();
			sql.append("select tabid,name,sp_flag,content from template_table where tabid in("+buf.toString()+") order by tabid");
			try
			{
				RowSet rset=dao.search(sql.toString());
				String sp_flag="0";
				while(rset.next())
				{
					/**此业务模板无权限时，不加(对人事变动)*/
					TemplateTableBo templateTableBo = new  TemplateTableBo( this.getFrameconn(), rset.getInt("tabid"), userView);
					String view = templateTableBo.getView();
					if(view==null)
						view ="";
					LazyDynaBean dynabean=new LazyDynaBean();
					dynabean.set("tabid",rset.getString("tabid"));				
					dynabean.set("name",rset.getString("name"));
					sp_flag=rset.getString("sp_flag")==null?"0":"1";
					dynabean.set("sp_flag",sp_flag);
					dynabean.set("content",Sql_switcher.readMemo(rset,"content"));
					dynabean.set("view",view);
					if("37".equals(staticid)|| "38".equals(staticid)|| "55".equals(staticid))//人事异动37,合同管理38,资格评审55
					{
						if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}
					if("34".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.GZBD,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}
					if("39".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.INS_BD,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}
					if("40".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}
					if("56".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.ORG_BD,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}
					if("57".equals(staticid))
					{
						if(!this.userView.isHaveResource(IResourceConstant.POS_BD,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}
					else if("51".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS,rset.getString("tabid")))
							  dynabean.set("ishave", "0");
							else
							  dynabean.set("ishave", "1");
					}
					else if("52".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG,rset.getString("tabid")))
							  dynabean.set("ishave", "0");
							else
							  dynabean.set("ishave", "1");
					}
					else if("53".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS_FG,rset.getString("tabid")))
							  dynabean.set("ishave", "0");
							else
							  dynabean.set("ishave", "1");
					}
					else if("54".equals(staticid)){
						if(!this.userView.isHaveResource(IResourceConstant.PSORGANS_GX,rset.getString("tabid")))
							  dynabean.set("ishave", "0");
							else
							  dynabean.set("ishave", "1");
					}else if("60".equals(staticid)){ //考勤业务办理
						if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid")))
							  dynabean.set("ishave", "0");
							else
							  dynabean.set("ishave", "1");
					}
					/*if(staticid.equals("38"))
					{
						if(!this.userView.isHaveResource(IResourceConstant.HIGHMUSTER,rset.getString("tabid")))
						  dynabean.set("ishave", "0");
						else
						  dynabean.set("ishave", "1");
					}*/
					
					
					list.add(dynabean);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		
		return list;
	}
	private ArrayList getTemplateList(String unittype,String opertationcode)throws GeneralException
	{
		ArrayList list=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select tabid,name,sp_flag,content from template_table where operationcode='");
			buf.append(opertationcode+"'");
			/*buf.append(" and flag=");
			buf.append(Integer.parseInt(unittype));*/
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(buf.toString());
			String sp_flag="0";
			while(rset.next())
			{
				/**此业务模板无权限时，不加(对人事变动)*/
				LazyDynaBean dynabean=new LazyDynaBean();
				dynabean.set("tabid",rset.getString("tabid"));				
				dynabean.set("name",rset.getString("name"));
				sp_flag=rset.getString("sp_flag")==null?"0":"1";
				dynabean.set("sp_flag",sp_flag);
				dynabean.set("content",Sql_switcher.readMemo(rset,"content"));
				if(!this.userView.isHaveResource(IResourceConstant.RSBD,rset.getString("tabid")))
				  dynabean.set("ishave", "0");
				else
				  dynabean.set("ishave", "1");
				list.add(dynabean);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}
}
