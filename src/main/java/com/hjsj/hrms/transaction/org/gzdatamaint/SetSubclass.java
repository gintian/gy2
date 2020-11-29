package com.hjsj.hrms.transaction.org.gzdatamaint;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:SetSubclass.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 16, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class SetSubclass extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList();
		ArrayList fielditemlist = new ArrayList();
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String returnflag=(String)reqhm.get("returnflag"); 
		this.getFormHM().put("returnflag",returnflag);
		String infor =(String)reqhm.get("infor");
		infor=infor!=null&&infor.trim().length()>0?infor:"";
		reqhm.remove("infor");
		
		String tagname =(String)reqhm.get("tagname");
		tagname=tagname!=null&&tagname.trim().length()>0?tagname:"0";
		reqhm.remove("tagname");
		
		String gzflag =(String)reqhm.get("gzflag");
		gzflag=gzflag!=null&&gzflag.trim().length()>0?gzflag:"";
		reqhm.remove("gzflag");
		
		PosparameXML pos = new PosparameXML(this.frameconn);
		String ps_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
		if("1".equals(infor))
			fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
		else if("2".equals(infor))
			fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
		else if("3".equals(infor))
			fielditemlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
		ArrayList selectlist = new ArrayList();
		

		String viewname="";
		if("1".equals(gzflag)){
			ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
			viewname = constantbo.getValue("subset");
		}else if("2".equals(gzflag)){
			GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
			viewname = xmlbo.getValue("base_set");
		}else if("3".equals(gzflag)){
			GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
			viewname = xmlbo.getValue("ins_base_set");
		}
		if(viewname.length()>0){
			String[] viewnames = viewname.split(",");
			for(int i=0;i<viewnames.length;i++){
				CommonData dataobj;
				if("0".equals(this.userView.analyseTablePriv(viewnames[i].toUpperCase())))
		  	    	continue;
				FieldSet fieldset =DataDictionary.getFieldSetVo(viewnames[i].toUpperCase());
				if(fieldset!=null){
					dataobj = new CommonData(viewnames[i],fieldset.getCustomdesc());
					selectlist.add(dataobj);
				}
			}
		}
		if(fielditemlist!=null){
	    	for(int i=0;i<fielditemlist.size();i++)
	    	{
	    		boolean b = true;
	    		FieldSet fieldset=(FieldSet)fielditemlist.get(i);
		  	    if("0".equals(this.userView.analyseTablePriv(fieldset.getFieldsetid())))
		  	    	continue;
		  	    if("A00".equals(fieldset.getFieldsetid())|| "B01".equals(fieldset.getFieldsetid())
		  	    		|| "B00".equals(fieldset.getFieldsetid())
		  	    		|| "K01".equals(fieldset.getFieldsetid())|| "K00".equals(fieldset.getFieldsetid()))
		  	    	continue;
		  	    
		  	    //外部培训设置子集不需要显示主集
		  	    if("A01".equals(fieldset.getFieldsetid())&& "1".equals(gzflag))
		  	        continue;
		  	    
		  	    b = IsNeed(fieldset,viewname);
		  	    if(fieldset.getFieldsetid().equalsIgnoreCase(ps_set))
		  	    	b=false;
		  	    if(b){
			  	    CommonData dataobj = new CommonData(fieldset.getFieldsetid(), /*"(" + fieldset.getFieldsetid() + ")"+*/ fieldset.getCustomdesc()/*getFieldsetdesc()*/);
			        list.add(dataobj);
		  	    }
		    }
	    }
		this.getFormHM().put("subclasslist",list);
		this.getFormHM().put("selectsubclass",selectlist);
		this.getFormHM().put("tagname",tagname);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("gzflag",gzflag);
	}
	
	private boolean IsNeed(FieldSet fieldset,String tviewname){
		boolean b = true;
		String[] viewnames = tviewname.split(",");
	    	if(viewnames.length>0){
	    		for(int j=0;j<viewnames.length;j++){
	    			if(fieldset.getFieldsetid().equalsIgnoreCase(viewnames[j])){
	    				b = false;
	    			}
	    		}
	    	}
		return b;
	}
}
