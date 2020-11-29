package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存其他设置
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class SaveOtherConstantTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		String year_restrict=(String)this.getFormHM().get("year_restrict");
		String year_restrict_1=year_restrict;
		// linbz 20160927 防止传过来的字符未转码增加校验条件"＃"
		if(year_restrict==null||year_restrict.length()<=0|| "#".equals(year_restrict)|| "＃".equals(year_restrict))
			year_restrict="";
		String relating=(String)this.getFormHM().get("relating");
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
		xml.setYear_restrict(year_restrict);
		xml.WriteOutParameterXml("SS_SETCARD",false,"",false,"","","","",this.getFrameconn());
		this.getFormHM().put("year_restrict", year_restrict_1);
		
	}
}
