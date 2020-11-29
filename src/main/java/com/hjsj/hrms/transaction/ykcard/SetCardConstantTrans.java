/*
 * Created on 2005-4-28
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SetCardConstantTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try
        {
		   String fashion_flag=(String)this.getFormHM().get("fashion_flag");
		   String relating=(String)this.getFormHM().get("relating");
		   String type=(String)this.getFormHM().get("type");
		   if(type==null)
		   	  type="";		
		   String b0110=userView.getUserOrgId();
		   if(relating!=null&&relating.length()>0)
		   {
			   CardConstantSet cardConstantSet=new  CardConstantSet();
			   ContentDAO dao=new ContentDAO(this.getFrameconn());
			   cardConstantSet.getInsertRelating(dao,relating);
			   if(!this.userView.isSuper_admin())
			      b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,userView.getUserOrgId());
			   else
				   b0110="";
		   }
		   XmlParameter xml=new XmlParameter("UN",b0110,"00");
		   xml.setRelating(relating);
		   xml.WriteOutParameterXml("SS_SETCARD",false,"",false,"",type,fashion_flag,"",this.getFrameconn());		 		   
        }catch(Exception e){
           e.printStackTrace();
        }       
	}
}
