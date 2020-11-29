package com.hjsj.hrms.transaction.sys.options.template;

import com.hjsj.hrms.actionform.sys.options.template.TemplateSetForm;
import com.hjsj.hrms.businessobject.hire.HireTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryTemplateTrans extends IBusiness{
	public QueryTemplateTrans(){
		super();
	}
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String id = (String)hm.get("templateType");
		String opt = (String)hm.get("opt");
		StringBuffer str_sql = new StringBuffer();
		ArrayList list = new ArrayList();
		ArrayList zploop_list = new ArrayList();
		//取得招聘环节列表的sql语句
		String sql_zploop = "select codeitemid,codeitemdesc from codeitem where codesetid ='36' and( codeitemid ='1' or codeitemid ='2' or codeitemid='3' or codeitemid='4') order by codeitemid ";
		//取得模板列表的sql语句
		str_sql.append("select t.*,c.codeitemdesc from t_sys_msgtemplate t,codeitem c where c.codesetid ='36' and c.codeitemid = zploop");
	
		ArrayList sqlParams = new ArrayList();
		String queryType = (String)hm.get("queryType");
		String zploop = "#";
		String template_type="#";
		if("2".equals(opt))
		{
	    	/*if(queryType!=null){//按模板类型查询 0:邮件,1短信
    			if( queryType.equals("type")){
	    		    str_sql.append(" and template_type = "+this.getFormHM().get("template_type"));
	    		}
                if(queryType.equals("zploop")){//按招聘环节查询
	    			if(((String)this.getFormHM().get("zpLoop")).equals("#")){
					
	    			}else{
	    			str_sql.append(" and zploop = '"+this.getFormHM().get("zpLoop")+"'");
	    			}
	    		}
	    	}*/
		    
		    //zxj 20140924 传入的参数转回过滤前状态，sql用预处理查询
			zploop=(String)this.getFormHM().get("zpLoop");
			zploop = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(zploop);
			
			template_type = (String)this.getFormHM().get("template_type");
			template_type = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(template_type);
			
			if(zploop!=null&&!"#".equals(zploop)) {
			    str_sql.append(" and zploop = ?");
			    sqlParams.add(zploop);
			}
			
			if(template_type!=null&&!"#".equals(template_type)) {
				 str_sql.append(" and template_type = ?");
				 sqlParams.add(template_type);
			}
		}	
		str_sql.append(" order by template_id");
		try{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    //System.out.println(str_sql);
		    this.frowset = dao.search(str_sql.toString(), sqlParams);
		    HireTemplateBo bo = new HireTemplateBo(this.getFrameconn());
	        String b0110=bo.getB0110(this.userView);
	        boolean flag=true;
	        String[] b_arr=null;
	        if("HJSJ".equalsIgnoreCase(b0110))
	        {
	        	flag=false;
	        }
	        else
	        {
	        	b_arr=b0110.split("`");
	        }
		    while(this.frowset.next()){
		    	if(!flag)
		    	{
	    	    	DynaBean dbean = new LazyDynaBean();
		        	dbean.set("template_id",this.frowset.getInt("template_id")+"");
		        	dbean.set("zploop",this.frowset.getString("zploop"));
		         	dbean.set("name",this.frowset.getString("name"));
		        	dbean.set("title",this.frowset.getString("title"));
		         	dbean.set("address",this.frowset.getString("adress"));
		        	if(this.frowset.getInt("template_type") == 0)
		        		dbean.set("template_type","邮件通知");
		        	else if(this.frowset.getInt("template_type") == 1)	
		        	    dbean.set("template_type","短信通知");
		        	dbean.set("codeitemdesc",this.frowset.getString("codeitemdesc"));
		        	list.add(dbean);
		    	}
		    	else
		    	{
		    		String bb=this.frowset.getString("b0110");
		    		boolean bool=false;
		    		if(bb==null|| "".equals(bb)|| "HJSJ".equalsIgnoreCase(bb))
		    		{
		    			continue;
		    		}
		    		else
		    		{
		    			String[] bb_arr=bb.split("`");
		    			for(int i=0;i<bb_arr.length;i++)
		    			{
		    				String temp_bb=bb_arr[i];
		    				for(int j=0;j<b_arr.length;j++)
		    				{
		    					if(temp_bb.indexOf(b_arr[j])!=-1)
		    					{
		    						bool=true;
		    						break;
		    					}
		    				}
		    				if(bool)
		    				{
		    					break;
		    				}
		    			}
		    			if(bool)
		    			{
		    				DynaBean dbean = new LazyDynaBean();
				        	dbean.set("template_id",this.frowset.getInt("template_id")+"");
				        	dbean.set("zploop",this.frowset.getString("zploop"));
				         	dbean.set("name",this.frowset.getString("name"));
				        	dbean.set("title",this.frowset.getString("title"));
				         	dbean.set("address",this.frowset.getString("adress"));
				        	if(this.frowset.getInt("template_type") == 0)
				        		dbean.set("template_type","邮件通知");
				        	else if(this.frowset.getInt("template_type") == 1)	
				        	    dbean.set("template_type","短信通知");
				        	dbean.set("codeitemdesc",this.frowset.getString("codeitemdesc"));
				        	list.add(dbean);
		    			}
		    		}
		    	}
		    	
		    }
		    this.frowset = dao.search(sql_zploop);
		    CommonData data = new CommonData("#","   ");
		    zploop_list.add(data);
		    while(this.frowset.next()){
		            CommonData vo=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
					zploop_list.add(vo);
		    }
		  
		}catch(SQLException sqle){
			sqle.printStackTrace();
			
		}finally{
			
		    this.getFormHM().put("zpLoop",zploop);
			this.getFormHM().put("alist",list);
			this.getFormHM().put("zpLoop_list",zploop_list);
			this.getFormHM().put("id",id);
			this.getFormHM().put("template_type",template_type);
			TemplateSetForm setForm = new TemplateSetForm();
			setForm.setId(id);
		}
		
		hm.remove("queryType");
		
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
