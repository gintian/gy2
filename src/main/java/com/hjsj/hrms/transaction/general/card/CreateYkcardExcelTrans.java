package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.businessobject.ykcard.YkcardExcel;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class CreateYkcardExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		System.gc();
		//System.out.println("CreateYkcardExcelTrans-----------------------------------开始----------------------------");
		String nid=(String)this.getFormHM().get("nid");	
		String cyear=(String)this.getFormHM().get("cyear");	
		String querytype=(String)this.getFormHM().get("querytype");	
		String cmonth=(String)this.getFormHM().get("cmonth");	
		String userpriv=(String)this.getFormHM().get("userpriv");	
		String istype=(String)this.getFormHM().get("istype");              /*0代表薪酬1登记表*/	
		String season=(String)this.getFormHM().get("season");	
		String ctimes=(String)this.getFormHM().get("ctimes");	
		String cdatestart=(String)this.getFormHM().get("cdatestart");	
		String cdateend=(String)this.getFormHM().get("cdateend");			
		String cardid=(String)this.getFormHM().get("cardid");
		String infokind=(String)this.getFormHM().get("infokind");
		String userbase=(String)this.getFormHM().get("userbase");
		String tabid=(String)this.getFormHM().get("tabid");
		String b0110=(String)this.getFormHM().get("b0110");
		String pre=(String)this.getFormHM().get("pre");
		String fieldpurv=(String)this.getFormHM().get("fieldpurv");
		if("0".equals(istype))
		{
			XmlParameter xml=new XmlParameter("UN",b0110,"00");
			//System.out.println("CreateYkcardExcelTrans------XmlParameter------------------");
			xml.ReadOutParameterXml("SS_SETCARD",this.getFrameconn());	
			//System.out.println("CreateYkcardExcelTrans------XmlParameter------------------ReadOutParameterXml");
		    if(tabid==null||tabid.length()<=0)
		    {
		    	String flag=xml.getFlag();
				CardConstantSet cardConstantSet=new CardConstantSet(this.userView,this.getFrameconn());
				ArrayList cardidlist=cardConstantSet.setCardidSelect(this.getFrameconn(),this.userView,flag,pre,nid,b0110);
				if(cardidlist!=null&&cardidlist.size()>0)
				{
					CommonData dataobj=(CommonData)cardidlist.get(0);
					tabid=dataobj.getDataValue();
					cardid=tabid;
				}
		    }else
		    {
		    	cardid=tabid;
		    }
		    String type=xml.getType();              //0条件1时间
		    //System.out.println("CreateYkcardExcelTrans------type------------------" + type);
		    if("0".equals(type))
			 querytype="0";
		}else if("1".equals(istype)){
		   querytype="0";
		}
		//System.out.println("CreateYkcardExcelTrans------istype------------------" + istype);
		try{
			//System.out.println("ykcardExcel------开始------------------");
			YkcardExcel ykcardExcel=new YkcardExcel(this.getFrameconn(),this.userView, cardid);
			//System.out.println("url------开始------------------");
			String url=ykcardExcel.excelYkcard(Integer.parseInt(cardid),nid,userbase,this.userView,cyear,querytype,cmonth,userpriv,istype,season,ctimes,cdatestart,cdateend,infokind,fieldpurv);
		    //System.out.println(url);
			//System.out.println("url---" + url);
			url = SafeCode.encode(PubFunc.encrypt(url));
			this.getFormHM().put("url",url);
			//System.out.println("CreateYkcardExcelTrans-----------------------------------结束----------------------------");
		}catch(Exception e)
		{
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}finally{
			System.gc();
		}
	}
}



