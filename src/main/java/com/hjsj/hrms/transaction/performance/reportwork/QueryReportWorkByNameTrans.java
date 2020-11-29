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

import java.util.ArrayList;

public class QueryReportWorkByNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			StringBuffer sql_str= new StringBuffer("select a.id,a.b0110,a.e0122,b.e01a1,a.a0101,a.object_id,b.a0000");
			String columns="id,b0110,e0122,e01a1,a0101,object_id";
			StringBuffer where_str=new StringBuffer();
		    String name="";
		    String plan_id="";
		    if(this.getFormHM().get("name")!=null && ((String)this.getFormHM().get("name")).trim().length()>0)
			    name=(String)this.getFormHM().get("name");
		    if(this.getFormHM().get("plain_id")!=null && ((String)this.getFormHM().get("plain_id")).trim().length()>0)
			    plan_id=(String)this.getFormHM().get("plain_id");
		   // ArrayList personList=getListByName(name,plan_id);
		    else
		    	return;
		    where_str.append("from per_result_");
		    where_str.append(plan_id);
		    where_str.append(" a,usra01 b ");
		    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			   String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			   if(display_e0122==null|| "00".equals(display_e0122))
				   display_e0122="0";
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
		    if(name!=null&&name.trim().length()>0)
		    {
		         where_str.append(" and a.a0101 like '");
		         where_str.append(name+"%'");
		    }
		    
		    ArrayList plainList = getPlainList();
		    this.getFormHM().put("order_sql","order by b.a0000 asc");		
		    this.getFormHM().put("plainList",plainList);
		    //this.getFormHM().put("personList",personList);
		    this.getFormHM().put("sql_str",sql_str.toString());
			this.getFormHM().put("where_str",where_str.toString());
			this.getFormHM().put("columns",columns);
		    this.getFormHM().put("plain_id",plan_id);
		    this.getFormHM().put("name",name);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public ArrayList getPlainList(){
		ArrayList list = new ArrayList();
		LoadXml loadXml=new LoadXml();
		try{
			String sql = "select plan_id,name,parameter_content from per_plan where status=4 order by plan_id";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
		        String performanceType=loadXml.getPerformanceType(xmlContent);
		        if("1".equals(performanceType))
				       list.add(new CommonData(this.frowset.getString("plan_id"),this.frowset.getString("name")));
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
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
