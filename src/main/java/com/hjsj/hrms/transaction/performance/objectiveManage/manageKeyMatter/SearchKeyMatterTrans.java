package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.businessobject.performance.objectiveManage.manageKeyMatter.KeyMatterBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchKeyMatterTrans.java</p>
 * <p>Description:查询关健事件交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 10:09:23</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchKeyMatterTrans extends IBusiness
{

    public void execute() throws GeneralException
    {  
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		String a_code=(String)hm.get("a_code");
		String refreshKey=(String)hm.get("refreshKey");
		hm.remove("a_code");
		hm.remove("refreshKey");
		
		String unionOrgCode = (String)this.getFormHM().get("unionOrgCode");
		if(refreshKey!=null && refreshKey.trim().length()>0 && "saveKey".equalsIgnoreCase(refreshKey))
		{
			a_code=unionOrgCode;
		}
		
		if(a_code==null)
		    a_code="";
		//1-部门 2－单位 3－人员 0－职位
		String kind="";
		String orgCode="";
		
		String codeid = "";	
		String operOrg = "";
		StringBuffer buf = new StringBuffer();
		
		String yORnDB = "false";
		ArrayList alist = this.userView.getPrivDbList(); // 判断有没有进行人员库授权		
		if(alist.size()<=0)		
			yORnDB = "false";			
		else
		{
			for (int i = 0; i < alist.size(); i++) 
			{
				String dbname = (String)alist.get(i);
				if("Usr".equalsIgnoreCase(dbname))
				{
					yORnDB = "true";
					break;
				}
			}			
		}
		this.getFormHM().put("dbname", yORnDB);	
//		if(yORnDB.equalsIgnoreCase("false"))
//			return;
		
		// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
//		if (!this.userView.isSuper_admin())
		{					
			operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{					
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)   //  主页面默认显示第一个节点下的关键事件
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");								
								
			}
			else if((!this.userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{								
				codeid = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();  // 取得登录用户的管理范围
				
				if (codeid.trim().length() > 0)// 说明授权了
				{					
				}else
					buf.append(" and 1=2 ");
				
			}else if(this.userView.isSuper_admin())
				codeid = "UN";
		} 
//		else
//		{
//		    codeid = "UN";
//		}
		if("".equals(a_code))
		    a_code = codeid;
		if(a_code.indexOf("UN")!=-1)
		{
		    kind="2";
		    orgCode=a_code.substring(2, a_code.length());
		}else if(a_code.indexOf("UM")!=-1)
		{
		    kind="1";
		    orgCode=a_code.substring(2, a_code.length());
		}else if(a_code.indexOf("Usr")!=-1)
		{
		    kind="3";
		    orgCode=a_code.substring(3, a_code.length());
		}else if(a_code.indexOf("@K")!=-1 || a_code.indexOf("@k")!=-1)
		{
		    kind="0";
		    orgCode=a_code.substring(2, a_code.length());
		}
		
		if(("".equals(a_code)) && ("".equals(codeid)))
		{
			kind="4";
			orgCode = buf.toString();
		}
		
		// 查询的参数
		String year = (String) this.getFormHM().get("year");
		String objectType = (String) this.getFormHM().get("objectType");
		String userbase = (String)this.getFormHM().get("userbase");
		String checkName = (String)this.getFormHM().get("checkName");				
		if(checkName.indexOf("'")!=-1)				
			checkName = checkName.replaceAll("'","‘"); 					
		
		KeyMatterBo bo = new KeyMatterBo(this.getFrameconn());
		try
		{	    
		    ArrayList setlist = bo.searchKeyMatter(year, objectType, orgCode, kind, userbase, this.userView, checkName);
		   
		    if(("2".equals(objectType)) && ("false".equalsIgnoreCase(yORnDB)))
		    	setlist=new ArrayList();
		    if("".equals(codeid) && operOrg.length()<= 2)
		    	setlist=new ArrayList();
		    this.getFormHM().put("setlist", setlist);
		    this.getFormHM().put("kind", kind);
		    this.getFormHM().put("code",orgCode);
		    this.getFormHM().put("unionOrgCode",a_code);
		    
		} catch (Exception ex)
		{
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		}
		
		this.getFormHM().put("yearList", bo.getYears());	
		this.getFormHM().put("objecTypeList", bo.getObjecType());
    }         
	
}