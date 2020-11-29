package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;



public class SearchAmountDetailTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String b0110 = (String)map.get("b0110");
			String season = (String)map.get("season");
			String time=(String)map.get("year");
			String filtervalue=(String)map.get("filter");
			GrossManagBo bo = new GrossManagBo(this.getFrameconn(),this.getUserView());
			GzAmountXMLBo gbo = new GzAmountXMLBo(this.getFrameconn(),1);
			HashMap gmap =gbo.getValuesMap();
			String fc_flag=(String)gmap.get("fc_flag");
			if(fc_flag!=null&&fc_flag.length()!=0){
				bo.setFc_flag(fc_flag);
			}
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			String ctrl_peroid =  (String)this.getFormHM().get("ctrl_peroid");
			String z1=(String)map.get(fieldsetid.toLowerCase()+"z1");
			//String time=(String)this.getFormHM().get("yearnum");
			String spflagid=(String)this.getFormHM().get("spflagid");
			String code = (String)this.getFormHM().get("code");
			String codeitemid = (String)this.getFormHM().get("codeitemid");
			HashMap mp = new HashMap();
			if(fc_flag!=null&&fc_flag.length()!=0){
				mp=bo.getDetailInfo(fieldsetid, time, spflagid, b0110, ctrl_peroid, season, z1);
			}else{
				mp=bo.getDetailInfo(fieldsetid, time, spflagid, b0110,ctrl_peroid,season);
			}
			String unitname=bo.getUnitName(b0110, fieldsetid);
			this.getFormHM().put("columnList",(ArrayList)mp.get("1"));
			this.getFormHM().put("infoList",(ArrayList)mp.get("2"));
			this.getFormHM().put("yearnum",time);
			this.getFormHM().put("unitName",unitname);
			this.getFormHM().put("code",code);
			this.getFormHM().put("codeitemid",codeitemid);
			this.getFormHM().put("filtervalue",filtervalue);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
