package com.hjsj.hrms.transaction.report.actuarial_report.validate_rule;

import com.hjsj.hrms.businessobject.report.actuarial_report.validate_rule.TargetsortBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SetSubclassTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
//		String tag = (String)this.getFormHM().get("tag");
//		String tagname = (String)this.getFormHM().get("tagname");
		HashMap hm =(HashMap)this.getFormHM().get("requestPamaHM");
		String targetsortid = (String)hm.get("targetsortid");
//		if(tag==null||tag.equalsIgnoreCase(""))
//			tag = "set_a";
		ArrayList list = new ArrayList();
		ArrayList fielditemlist = new ArrayList();
		
			 fielditemlist=DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
			//System.out.println("fielditemlist.size:"+fielditemlist.size());
			 if(fielditemlist!=null){
		    	for(int i=0;i<fielditemlist.size();i++)
		    	{
		    		if(fielditemlist.get(i)==null)
		    			continue;
		    		
		    		//FieldItem item = (FieldItem)z03list.get(i);
		    		FieldItem fielditem = (FieldItem)fielditemlist.get(i);
		    		//if(fielditem.getItemid().toLowerCase().equals("u0200"))
			  	    //	continue;
		    		
			  	  CommonData dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
			      list.add(dataobj);
			       }
		    }
		
		this.getFormHM().put("subclasslist",list);
		ArrayList selectlist = new ArrayList();
		TargetsortBo infoxml = new TargetsortBo(this.getFrameconn());
		String viewname = infoxml.getView_value(targetsortid);
	
			if(viewname.length()>0){
				String[] viewnames = viewname.split(",");
				 fielditemlist=DataDictionary.getFieldList("U02",Constant.USED_FIELD_SET);
					if(fielditemlist!=null){
					 	for(int j=0;j<viewnames.length;j++){
				    	for(int i=0;i<fielditemlist.size();i++)
				    	{
				    		
				    		if(fielditemlist.get(i)==null)
				    			continue;
				    		//FieldItem item = (FieldItem)z03list.get(i);
				    		FieldItem fielditem = (FieldItem)fielditemlist.get(i);
					  	  new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					 
							CommonData dataobj;
							if(fielditem.getItemdesc().equals(viewnames[j])){
								dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
								selectlist.add(dataobj);
								break;
							}
							}
					       }
				    }
				
			}
		
		
		this.getFormHM().put("selectsubclass",selectlist);
		this.getFormHM().put("targetsortid",targetsortid);
		 
	}
	
	

}
