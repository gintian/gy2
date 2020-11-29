package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 10, 2007:4:55:09 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class SaveLeaderParamTrans extends IBusiness {

  
    public void execute() throws GeneralException 
    {
    	String bz_fieldsetid=(String)this.getFormHM().get("bz_fieldsetid");
    	String bz_codeitemid=(String)this.getFormHM().get("bz_codeitemid");
    	String bz_codesetid=(String)this.getFormHM().get("bz_codesetid");
    	String hb_fieldsetid=(String)this.getFormHM().get("hb_fieldsetid");
    	String hb_codesetid=(String)this.getFormHM().get("hb_codesetid");
    	String hb_codeitemid=(String)this.getFormHM().get("hb_codeitemid");
    	String unit_fieldsetid=(String)this.getFormHM().get("unit_fieldsetid");
    	String unit_codeitemid=(String)this.getFormHM().get("unit_codeitemid");
    	String unit_codesetid=(String)this.getFormHM().get("unit_codesetid");
    	LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn()); 
    	leadarParamXML.setValue(LeadarParamXML.TEAM_LEADER,"setid",bz_fieldsetid);
    	leadarParamXML.setValue(LeadarParamXML.TEAM_LEADER,"fielditem",bz_codesetid);
    	leadarParamXML.setValue(LeadarParamXML.TEAM_LEADER,"value",bz_codeitemid);
    	
    	leadarParamXML.setValue(LeadarParamXML.CANDID_LEADER,"setid",hb_fieldsetid);
    	leadarParamXML.setValue(LeadarParamXML.CANDID_LEADER,"fielditem",hb_codesetid);
    	leadarParamXML.setValue(LeadarParamXML.CANDID_LEADER,"value",hb_codeitemid);
    	
    	leadarParamXML.setValue(LeadarParamXML.UNIT_ZJ,"setid",unit_fieldsetid);
    	leadarParamXML.setValue(LeadarParamXML.UNIT_ZJ,"fielditem",unit_codesetid);
    	leadarParamXML.setValue(LeadarParamXML.UNIT_ZJ,"value",unit_codeitemid);
    	leadarParamXML.saveParameter();    	
    }

}
