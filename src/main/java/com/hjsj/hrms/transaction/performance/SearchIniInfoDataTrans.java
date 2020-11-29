/*
 * 创建日期 2005-7-4
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author luangaojiong
 * 领导查询员工考核右边信息展示
 */
public class SearchIniInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String userbase=(String)this.getFormHM().get("userbase");
		String strwhere="";	
		String kind="";
		StringBuffer orderby=new StringBuffer();
		orderby.append(" order by ");
		orderby.append("a0000");
		if(!userView.isSuper_admin())
		{
	           String expr="1";
	           String factor="";
			if("UN".equals(userView.getManagePrivCode()))
			{
				factor="B0110=";
			    kind="2";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					factor+=userView.getManagePrivCodeValue();
				factor+="%`";
			}
			else if("UM".equals(userView.getManagePrivCode()))
			{
				factor="E0122="; 
			    kind="1";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					factor+=userView.getManagePrivCodeValue();
				factor+="%`";
			}
			else if("@K".equals(userView.getManagePrivCode()))
			{
				factor="E01A1=";
				kind="0";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					factor+=userView.getManagePrivCodeValue();
				factor+="%`";
			}
			else
			{
				factor="B0110=";
			    kind="2";
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					factor+=userView.getManagePrivCodeValue();
				factor+="%`";
			}			
			 ArrayList fieldlist=new ArrayList();
		        try
		        {        
			     //   cat.debug("expr="+expr);
		         //   cat.debug("factor="+factor);
		         //   System.out.println("factor" + factor + " expr" + expr);
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		              strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
		              
		            cat.debug("---->Common query's priv="+strwhere);
		          //  System.out.println("factor" + factor + " expr" + expr);
		           // System.out.println(strwhere);
		        }catch(Exception e){
		          e.printStackTrace();	
		        }
	
		}else{
			StringBuffer wheresql=new StringBuffer();
			wheresql.append(" from ");
			wheresql.append(userbase);
			wheresql.append("A01 ");
			kind="2";
			strwhere=wheresql.toString();
		}
	//	System.out.println("strwhere" + strwhere);
		StringBuffer strsql=new StringBuffer();
		strsql.append("select ");
	    strsql.append(userbase);
		strsql.append("A01.A0100,B0110,E0122,E01A1,A0101,UserName ");
	    this.getFormHM().put("strsql",strsql.toString());
		this.getFormHM().put("cond_str",strwhere); 
		this.getFormHM().put("code",userView.getManagePrivCodeValue());
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("order_by",orderby.toString());


	}

}
