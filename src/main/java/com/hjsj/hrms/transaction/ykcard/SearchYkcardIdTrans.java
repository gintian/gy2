/*
 * Created on 2006-4-21
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * @author wlh
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchYkcardIdTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		//记录方式
		CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());	
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String relating=cardConstantSet.getSearchRelating(dao);	
		String b0110="";
		if(!this.userView.isSuper_admin())		
		 b0110=cardConstantSet.getRelatingValue(dao,this.userView.getA0100(),this.userView.getDbname(),relating,userView.getUserOrgId());
		XmlParameter xml=new XmlParameter("UN",b0110,"00");
		//检查一下代码是否同步
		xml.syncYkCardConfigCode("SS_SETCARD",this.frameconn);
		xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn(),"all");	
		String cardid=xml.getCard_id();
		String mobcardid = xml.getMobcardid();
		String type=xml.getType();
		String flag=xml.getFlag();		
		ArrayList codenamelist=xml.getCodenamelist();
	    ArrayList codesetlist=xml.getCodesetlist();
	    ArrayList cardnolist=xml.getCardnolist();
		if(flag==null||flag.length()<=0)
			flag="0"; 
		//flag="1";
		String codename="";
		if(codenamelist!=null&&codenamelist.size()>0)
		   codename=(String)codenamelist.get(0);
		/*codesetlist.add( new CommonData("1","AX"));
		codesetlist.add( new CommonData( "2","AX"));*/
		this.getFormHM().put("str_value",cardid);
		
		ArrayList  cardnomesslist=cardConstantSet.getCardMessList(cardnolist);
		ArrayList mobCardNoList = xml.getMobcardnoList();
		ArrayList mobCardNoMessList = cardConstantSet.getCardMessList(mobCardNoList);
		String cardidmess = cardConstantSet.getCardMesslist(cardid);
		String cardidmessapp = cardConstantSet.getCardMesslist(mobcardid);
		this.getFormHM().put("cardidmess",cardidmess);
		this.getFormHM().put("cardidmessapp",cardidmessapp);
		this.getFormHM().put("codename",codename.toUpperCase());
		this.getFormHM().put("codesetlist",codesetlist);
		this.getFormHM().put("codenamelist",codenamelist);
		this.getFormHM().put("cardnomesslist",cardnomesslist);
		this.getFormHM().put("mobcardnomesslist", mobCardNoMessList);
		this.getFormHM().put("cardnolist",cardnolist);
		this.getFormHM().put("fashion_flag",flag);
		this.getFormHM().put("type",type);
		ArrayList fieldlist = DataDictionary.getFieldList("A01",
				Constant.USED_FIELD_SET);
		ArrayList list=new ArrayList();
		CommonData da=new CommonData();
		da.setDataName("请选择.......");
        da.setDataValue("");
        list.add(da);
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(i);
			if("UN".equalsIgnoreCase(fielditem.getCodesetid()))
			{
				da=new CommonData();
				da.setDataName(fielditem.getItemdesc());
				da.setDataValue(fielditem.getItemid());
				list.add(da);
			}
		}
		this.getFormHM().put("relating", relating);
		this.getFormHM().put("relatinglist", list);
	}
}