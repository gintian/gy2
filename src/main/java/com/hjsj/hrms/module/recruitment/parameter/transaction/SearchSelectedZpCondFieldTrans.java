package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ZpCondTemplateXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 25, 2007:11:27:24 AM</p> 
 *@author zx
 *@version 4.0
 */
public class SearchSelectedZpCondFieldTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException{
		ArrayList list = new ArrayList();
		ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.getFrameconn());
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String type=(String)hm.get("type");
		if(type ==null)
		     type = "0";
		
		if("0".equals(type))
		{
			list = bo.getAttributeValues(type);
	    	ContentDAO dao =new ContentDAO(this.getFrameconn());
	    	try{
	    	    for(int i=0;i<list.size();i++){
	    		  LazyDynaBean bean = (LazyDynaBean)list.get(i);
		    	  String id=(String)bean.get("name");
		          String codesetid = "";
    		      String itemtype="";
    			  String e_value = (String)bean.get("e_value");
    			  String s_value = (String)bean.get("s_value");
	    		  if("createtime".equalsIgnoreCase(id))
	    			 {
	    				  bean.set("itemdesc","简历入库时间");
	    		    	  bean.set("itemtype","D");
	    		    	  bean.set("itemlength","10");
	    		    	  bean.set("codesetid","0");
	    		    	  bean.set("decimalwidth","0");
	    		    	  bean.set("itemid","createtime");
	    			      bean.set("s_value",s_value);
	    			      bean.set("view_s_value_value",s_value);
	    			      bean.set("e_value",e_value);
	    			      bean.set("flag",bean.get("flag"));
	    			      continue;
	    			 }
			     
    			this.frowset = dao.search("select itemdesc,itemid,itemtype,codesetid,itemlength,decimalwidth from fielditem where itemid ='"+id+"'");
    			while(this.frowset.next()){
	    		    	bean.set("itemdesc",this.frowset.getString("itemdesc"));
	    		    	bean.set("itemtype",this.frowset.getString("itemtype"));
	    		    	bean.set("itemid",this.frowset.getString("itemid"));
	    		    	bean.set("codesetid",this.frowset.getString("codesetid"));
	    		    	bean.set("itemlength",this.frowset.getString("itemlength"));
	    		    	bean.set("decimalwidth",this.frowset.getString("decimalwidth"));
	    		    	codesetid=this.frowset.getString("codesetid");
	    		    	itemtype = this.frowset.getString("itemtype");
    			}
    			if("A".equals(itemtype)&&!"0".equals(codesetid)){
    				if(e_value !=null && !"".equals(e_value)){
					
    				    bean.set("view_e_value",AdminCode.getCodeName(codesetid,e_value));
				
    				}
				
    				bean.set("view_s_value_view",AdminCode.getCodeName(codesetid,s_value));
				
    			}
    			bean.set("flag",bean.get("flag"));
    		    }
			    
			 
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    		this.getFormHM().put("fieldSetList",list);
		}
		else
		{
			list=bo.getComplexTemplateList();
			this.getFormHM().put("complexTemplateList",list);
		}
	
		this.getFormHM().put("zp_cond_template_type",type);
		
	}

}
