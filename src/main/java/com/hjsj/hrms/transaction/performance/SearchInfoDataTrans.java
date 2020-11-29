/*
 * 创建日期 2005-7-4
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luangaojiong
 * 
 * 领导考核信息浏览
 */
public class SearchInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String userbase=(String)this.getFormHM().get("userbase");
		String code=(String)hm.get("code"); 
		String kind=(String)hm.get("kind");
        String strwhere="";
		StringBuffer wheresql=new StringBuffer();
		StringBuffer orderby=new StringBuffer();
		
		orderby.append(" order by "+userbase+"A01.b0110,e0122,");
		orderby.append(userbase);
		orderby.append("a01.a0000");
		if(userView.isSuper_admin()){                    //超级用户	
			//生成没有高级条件的from后的sql语句
			wheresql.append(" from ");
			wheresql.append(userbase);
			wheresql.append("A01 ");
			if("2".equals(kind) && code!=null && code.length()>0)
			{
				wheresql.append(" where b0110 like '");
			    wheresql.append(code);
			    wheresql.append("%'");
			}			   
			else if("1".equals(kind)  && code!=null && code.length()>0)
			{
				wheresql.append(" where e0122 like '"); 
				wheresql.append(code);
				wheresql.append("%'");
			}				
			else if("0".equals(kind) && code!=null && code.length()>0)
			{
				wheresql.append(" where e01a1 like '");
				wheresql.append(code);
				wheresql.append("%'");
			}else if("2".equals(kind))
			{
				wheresql.append(" where 1=1");
			}
			else
			{
				wheresql.append(" where 1=2");
			}
		    strwhere=wheresql.toString();
		}
		else{   
			  ArrayList fieldlist=new ArrayList();
		        try
		        {
		           String expr="1";
		           String factor="";
		           if("2".equals(kind))
						factor="B0110=";
				   else if("1".equals(kind))
				   	    factor="E0122="; 
				   else if("0".equals(kind))
				   	    factor="E01A1=";
				   if(code!=null && code.length()>0)
						factor+=code;
					factor+="%`";
		            cat.debug("expr="+expr);
		            cat.debug("factor="+factor);
		           
		            /**表过式分析*/
		            /**非超级用户且对人员库进行查询*/
		            strwhere=userView.getPrivSQLExpression(expr+"|"+factor,userbase,false,fieldlist);
		            cat.debug("---->Common query's priv="+strwhere);

		        }catch(Exception e){
		          e.printStackTrace();	
		        }
			}
		StringBuffer strsql=new StringBuffer();
		strsql.append("select ");
		
		
		StringBuffer columns=new StringBuffer("");
		ArrayList columnsList=new ArrayList();
		String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
		if(fieldstr!=null&&fieldstr.length()>0){
			fieldstr=fieldstr.toLowerCase().replaceAll(",a0101","");
			
			if(fieldstr.toLowerCase().indexOf("b0110")!=-1)
			{
				String newfieldstr=fieldstr.toLowerCase().replaceAll("b0110",userbase+"A01.b0110");
				strsql.append(userbase+"A01.a0100"+newfieldstr);
			}else
			{
				strsql.append(userbase+"A01.a0100"+fieldstr);
			}
			 columns.append("a0100"+fieldstr);
			 
			 String[] temps=fieldstr.split(",");
			 for(int i=0;i<temps.length;i++)
			 {
				if(temps[i].trim().length()>0)
				{
					FieldItem item=DataDictionary.getFieldItem(temps[i]);
					columnsList.add(getLazyDynaBean(item.getItemid(),item.getItemdesc(),item.getItemtype(),item.getCodesetid()));
				}
			 }
			 
			 
			 if(fieldstr.toLowerCase().indexOf("a0101")==-1)
			 {
				 strsql.append(",a0101");
				 columns.append(",a0101");
				 columnsList.add(getLazyDynaBean("a0101","姓名","A","0"));
			 }
			 
			 if(fieldstr.toLowerCase().indexOf("b0110")==-1)
			 {
				 strsql.append(",b0110");
				 columns.append(",b0110");
			 }
			
		}else{
			columns.append("A0100,B0110,E0122,E01A1,A0101");
			columnsList.add(getLazyDynaBean("b0110","单位名称","A","UN"));
			columnsList.add(getLazyDynaBean("e0122","部门","A","UM"));
			columnsList.add(getLazyDynaBean("e01a1","职位名称","A","@K"));
			columnsList.add(getLazyDynaBean("a0101","姓名","A","0"));
		
			strsql.append(userbase);
			strsql.append("A01.A0100,B0110,E0122,E01A1,A0101");
		}
		
		
		
		
		
		

		this.getFormHM().put("columns",columns.toString());
		this.getFormHM().put("columnsList",columnsList);
		
		
		
	    this.getFormHM().put("strsql",strsql.toString());
		this.getFormHM().put("cond_str",strwhere);
	    //this.getFormHM().put("cond_str","");
	    this.getFormHM().put("kind",kind);
		this.getFormHM().put("code",code);
		this.getFormHM().put("order_by",orderby.toString());
		
        /**查询条件*/
    
		
	}

	
	public LazyDynaBean getLazyDynaBean(String id,String name,String type,String codesetid)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("id",id);
		abean.set("name",name);
		abean.set("type",type);
		abean.set("codesetid",codesetid);
		return abean;
	}
}
