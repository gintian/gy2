package com.hjsj.hrms.transaction.performance.reportwork;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchReportWorkTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			StringBuffer sql_str=new StringBuffer("select a.id,a.b0110,a.e0122,b.e01a1,a.a0101,a.object_id,b.a0000");
			StringBuffer where_str=new StringBuffer();
			String columns="id,b0110,e0122,e01a1,a0101,object_id";
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			//String dbname= this.userView.getDbname();
			String opt="1";
			String plain="";
			/*您用AdminCode.getCode(代码类，代码值，上几级)
			取得上几级代码项*/
			/**原来的权限范围是先找管理范围，如果没有管理范围再找其所在的部门，如果没有部门在找所在单位
			 * 山东高压需求，直接根据所在部门或单位，和在系统管理--参数管理--参数设置--其他参数中的部门显示包含上几级名称参数来作为权限
			 * 2008.10.29又改回来了
			 * */
		   Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		   String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		   if(display_e0122==null|| "00".equals(display_e0122))
			   display_e0122="0";
			if(map != null){
				opt=(String)map.get("opt");
				plain=(String)map.get("plain");
			}
			ArrayList plainList=getPlainList();
			if("1".equals(opt)){

			  if(getFirstPlanid() != null && getFirstPlanid().trim().length()>0)
				   plain=getFirstPlanid();
		      
			  where_str.append("from ");
			  where_str.append("per_result_"+plain);
			  where_str.append(" a,usra01 b");
			} else if("2".equals(opt)&&plain!=null&& plain.trim().length()>0){
				  where_str.append("from ");
				  where_str.append("per_result_"+plain);
				  where_str.append(" a,usra01 b");
			}
			if(!this.userView.isAdmin()&&!"1".equals(this.userView.getGroupId()))
			{
			//加权限
			String a0100=  this.userView.getA0100();
			String dbname= this.userView.getDbname();  
			String privCode=this.userView.getManagePrivCode();//代码
			String PrivCodeValue=this.userView.getManagePrivCodeValue();//值
			String e0122=getE0122(dbname,a0100);
			String b0110=getB0110(dbname,a0100);
            if(privCode!=null&&!"".equals(privCode))
            {
            	if(PrivCodeValue==null)
            		PrivCodeValue="";
            	if(e0122!=null&&!"".equals(e0122))
            	{
            		/**管理范围比所在部门大*/
            		String  value=PrivCodeValue;
            		if(e0122.length()>PrivCodeValue.length())
            		{
            			value=PrivCodeValue;
            			 if("UN".equalsIgnoreCase(privCode))
                 		{
            				 String privvalue="";
            				 if(value.length()>0)
            				 {	
            					 CodeItem item=AdminCode.getCode("UN",value,Integer.parseInt(display_e0122));
            				      privvalue=item.getCodeitem();
            				 }
                 			where_str.append(" where a.object_id=b.a0100 and ");
             	    		where_str.append("a.b0110 like '");
             	    		where_str.append(privvalue+"%'");
             	    	}
             	    	else if("UM".equalsIgnoreCase(privCode))
             	    	{
             	    		CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
       				        String privvalue=item.getCodeitem();
             		    	where_str.append(" where a.object_id=b.a0100 and ");
             		    	where_str.append("a.e0122 like '");
             		    	where_str.append(privvalue+"%'");
             	    	}
            		}
            		else
            		{
            			value=e0122;
            			CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
       				    String privvalue=item.getCodeitem();
       				    where_str.append(" where a.object_id=b.a0100 and ");
    				    where_str.append("a.e0122 like '");
    			    	where_str.append(privvalue+"%'");  
            		}
            	}else if(b0110!=null&&!"".equals(b0110))
   			    {
            		/**管理范围比所在部门大*/
            		String  value=PrivCodeValue;
            		if(b0110.length()>PrivCodeValue.length())
            		{
            			value=PrivCodeValue;
            			 if("UN".equalsIgnoreCase(privCode))
                 		{
            				 CodeItem item=AdminCode.getCode("UN",value,Integer.parseInt(display_e0122));
            				 String privvalue=item.getCodeitem();
                 			where_str.append(" where a.object_id=b.a0100 and ");
             	    		where_str.append("a.b0110 like '");
             	    		where_str.append(privvalue+"%'");
             	    	}
             	    	else if("UM".equalsIgnoreCase(privCode))
             	    	{
             	    		CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
       				        String privvalue=item.getCodeitem();
             		    	where_str.append(" where a.object_id=b.a0100 and ");
             		    	where_str.append("a.e0122 like '");
             		    	where_str.append(privvalue+"%'");
             	    	}
            		}
            		else
            		{
            			value=b0110;
            			CodeItem item=AdminCode.getCode("UN",value,Integer.parseInt(display_e0122));
       				    String privvalue=item.getCodeitem();
       				    where_str.append(" where a.object_id=b.a0100 and ");
    				    where_str.append("a.b0110 like '");
    			    	where_str.append(privvalue+"%'");  
            		}
   		    	 }
            	else
            	{
            		 if("UN".equalsIgnoreCase(privCode))
              		{
         				 CodeItem item=AdminCode.getCode("UN",PrivCodeValue,Integer.parseInt(display_e0122));
         				String privvalue=item.getCodeitem();
              			where_str.append(" where a.object_id=b.a0100 and ");
          	    		where_str.append("a.b0110 like '");
          	    		where_str.append(privvalue+"%'");
          	    	}
          	    	else if("UM".equalsIgnoreCase(privCode))
          	    	{
          	    		CodeItem item=AdminCode.getCode("UM",PrivCodeValue,Integer.parseInt(display_e0122));
    				    String privvalue=item.getCodeitem();
          		    	where_str.append(" where a.object_id=b.a0100 and ");
          		    	where_str.append("a.e0122 like '");
          		    	where_str.append(privvalue+"%'");
          	    	}
            	}
            }
            else
            {  
            if(e0122!=null&&!"".equals(e0122))
			 {
				 CodeItem item=AdminCode.getCode("UM",e0122,Integer.parseInt(display_e0122));
				 String privvalue=item.getCodeitem();
				 where_str.append(" where a.object_id=b.a0100 and ");
				 where_str.append("a.e0122 like '");
				 where_str.append(privvalue+"%'");  
			 }
			 else if(b0110!=null&&!"".equals(b0110))
			 {
				 CodeItem item=AdminCode.getCode("UN",b0110,Integer.parseInt(display_e0122));
				 String privvalue=item.getCodeitem();
				 where_str.append(" where a.object_id=b.a0100 and ");
				 where_str.append("a.b0110 like '");
				 where_str.append(privvalue+"%'");  
			 }
			 else{
	            	
	            	where_str.append(" where a.object_id=b.a0100 and 1=2");
	            	
			 }
            }
			}
			/**超级用户*/
			else
			{
				where_str.append(" where a.object_id=b.a0100 ");
			}
           this.getFormHM().put("order_sql","order by b.a0000 asc");		
			this.getFormHM().put("plainList",plainList);
			if(plainList.size() ==0||plainList==null)
				this.getFormHM().put("isnull","0");
			else
				this.getFormHM().put("isnull","1");
			this.getFormHM().put("sql_str",sql_str.toString());
		    this.getFormHM().put("where_str",where_str.toString());
			this.getFormHM().put("columns",columns);
			this.getFormHM().put("plain_id",plain);
			this.getFormHM().put("name","");
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	/**
	 * 得到所有考核计划
	 * @return
	 */
	public ArrayList getPlainList(){
		ArrayList list = new ArrayList();
		LoadXml loadXml=new LoadXml();
		try{
			String sql = "select plan_id,name,parameter_content from per_plan where status=4 order by plan_id desc";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
	           String performanceType=loadXml.getPerformanceType(xmlContent);
	           if("1".equals(performanceType))
	           {
				      list.add(new CommonData(this.frowset.getString("plan_id"),this.frowset.getString("name")));
	           }
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到考核计划下的人员
	 * @param plan_id
	 * @return
	 */
	public ArrayList getPersonList(String plan_id){
		ArrayList list = new ArrayList();
		if(plan_id == null || plan_id.trim().length()<=0)
			return list;
		try{
			String sql = "select id,b0110,e0122,e01a1,a0101,object_id from per_result_"+plan_id;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				LazyDynaBean bean=new LazyDynaBean();
				bean.set("b0110",AdminCode.getCodeName("UN",this.frowset.getString("b0110")));
				bean.set("e0122",AdminCode.getCodeName("UM",this.frowset.getString("e0122")));
				bean.set("e01a1",AdminCode.getCodeName("@K",this.frowset.getString("e01a1")));
				bean.set("a0101",this.frowset.getString("a0101"));
				bean.set("id",this.frowset.getString("id"));
				bean.set("object_id",this.frowset.getString("object_id"));
				System.out.println(this.frowset.getString("object_id"));
				list.add(bean);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	public String getFirstPlanid(){
		String id="";
		try{
			String sql="select plan_id,parameter_content from per_plan where status = 4 order by plan_id desc";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			LoadXml loadXml=new LoadXml();
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
		        String performanceType=loadXml.getPerformanceType(xmlContent);
		        if("1".equals(performanceType)){
				   id=this.frowset.getString("plan_id");
				   break;
		        }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return id;
	}
	public String getE0122(String dbname,String a0100)
	{
		String str="";
		String sql = "select e0122 from "+dbname+"a01 where a0100='"+a0100+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				str=this.frowset.getString("e0122");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
		
	}
	public String getB0110(String nbase,String a0100)
	{
		String str=null;
		try
		{
			String sql = "select b0110 from "+nbase+"a01 where a0100='"+a0100+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				str=this.frowset.getString("b0110");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

}
