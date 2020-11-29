package com.hjsj.hrms.transaction.org.gzdatamaint;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:AddSubclass.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 16, 2008</p> 
 *@author huaitao
 *@version 4.0
 */
public class AddSubclass extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList subclass_value=(ArrayList)this.getFormHM().get("subclass_value");
		String gzflag = (String)this.getFormHM().get("gzflag");
		String paravalue = "";
		String[] subclass = null;
		if(subclass_value!=null&&subclass_value.size()>0){
			subclass = new String[subclass_value.size()];
			for(int i=0;i<subclass_value.size();i++){
				subclass[i] = (String)subclass_value.get(i);
				paravalue+=(String)subclass_value.get(i)+",";
			}
		}else{
			subclass = new String[1];
			subclass[0] = "";
		}
		
		
		if("1".equals(gzflag)){
			ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
			constantbo.setValue("subset",paravalue);
			constantbo.saveStrValue();
		}else if("2".equals(gzflag)){
			GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
			xmlbo.saveInfo_paramNode("base_set",subclass,this.getFrameconn());
		}else if("3".equals(gzflag)){
			GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
			xmlbo.saveInfo_paramNode("ins_base_set",subclass,this.getFrameconn());
		}
	}

}
