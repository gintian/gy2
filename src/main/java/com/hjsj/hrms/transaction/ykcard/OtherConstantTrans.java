package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Calendar;
/**
 * 薪酬设置
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class OtherConstantTrans  extends IBusiness {
	   public void execute() throws GeneralException 
	   {
		   CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());	
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
		   String relating=cardConstantSet.getSearchRelating(dao);	
		   String b0110="";
		   if(!this.userView.isSuper_admin())		
			 b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,userView.getUserOrgId());
		   XmlParameter xml=new XmlParameter("UN",b0110,"00");
		   xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn(),"all");	
		   String year_restrict=xml.getYear_restrict();
		   this.getFormHM().put("year_restrict", year_restrict);
		   ArrayList yearlist=new ArrayList();
		   String year=(Calendar.getInstance().get(Calendar.YEAR))+"";
		   CommonData vo=new CommonData("#","不选择");
		   yearlist.add(vo);
		   for(int i=(Integer.parseInt(year)-10);i<=Integer.parseInt(year);i++)   
		   {
			   vo=new CommonData(i+"",i+"");
			   yearlist.add(vo);
		   }
		   this.getFormHM().put("yearlist", yearlist);
	   }
  
}
