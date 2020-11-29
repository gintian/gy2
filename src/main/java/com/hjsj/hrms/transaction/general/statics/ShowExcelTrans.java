package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.general.statics.ShowExcel;
import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;
/**
 * 
 *<p>Title:ShowExcelTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 1, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class ShowExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String userbase=(String)this.getFormHM().get("userbase");
			if(userbase==null|| "".equals(userbase))
				 userbase="Usr";
			String userbases=(String)this.getFormHM().get("userbases");
			String nam=(String)this.getFormHM().get("mess");
			String sone=(String)this.getFormHM().get("selOne");
	        String stwo=(String)this.getFormHM().get("selTwo");
	        if(sone==null||stwo==null|| "#".equalsIgnoreCase(sone)|| "#".equalsIgnoreCase(stwo))
	        {
	        	throw new GeneralException(ResourceFactory.getProperty("error.static.notselect"));
	        }
	        String infoFlag = (String)this.getFormHM().get("infor_Flag");
	        String result = (String)this.getFormHM().get("result");
	        boolean ret=true;
		     if(result==null|| "".equals(result)|| "0".equals(result))
		     {
		    	ret=true; 
		     }else{
		    	 ret =false;
		     }
		     String htotal=(String)this.getFormHM().get("htotal");
		     String vtotal=(String)this.getFormHM().get("vtotal");
			StatDataEncapsulation simplestat=new StatDataEncapsulation();
			int[][] getValues =null;
			if(userbases==null||userbases.length()==0){
				getValues =simplestat.getDoubleLexprData(sone,stwo,nam,userbase,"",userView.getManagePrivCode()+userView.getManagePrivCodeValue(),userView,infoFlag,ret,vtotal,htotal);
			}else
				getValues =simplestat.getDoubleLexprData(sone,stwo,nam,userbase.toUpperCase(),"",userView.getManagePrivCode()+userView.getManagePrivCodeValue(),userView,infoFlag,ret,userbases,vtotal,htotal);
			if(getValues==null) 
	        	throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("planar.two.error")));
		    
		    List dlist=simplestat.getVerticalArray();
			List hlist=simplestat.getHorizonArray();
			String snameplay=simplestat.getSNameDisplay();
			int tolvalue=simplestat.getTotalValue();
			ShowExcel show= new ShowExcel(this.getFrameconn());
			String filename = this.userView.getUserName()+"_"+nam+".xls";
			String excelfile=show.creatExcel(getValues,dlist,hlist,snameplay,tolvalue,filename);
			this.getFormHM().put("excelfile",PubFunc.encrypt(excelfile));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
