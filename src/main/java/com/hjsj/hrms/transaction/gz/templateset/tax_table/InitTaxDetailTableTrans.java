package com.hjsj.hrms.transaction.gz.templateset.tax_table;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableSetBo;
import com.hjsj.hrms.businessobject.gz.templateset.tax_table.TaxTableXMLBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class InitTaxDetailTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
	        HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
	        String taxid=(String)map.get("taxid");
	        map.remove("taxid");
	        taxid=taxid!=null&&taxid.trim().length()>0?taxid:"";
	        
	        String salaryid=(String)map.get("salaryid");
	        map.remove("salaryid");
	        salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
	        
	        String itemid=(String)map.get("itemid");
	        map.remove("itemid");
	        itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
	        
	        TaxTableSetBo bo = new TaxTableSetBo(this.getFrameconn());
	      
	        String k_base=bo.getK_base(taxid);
	        ArrayList taxTypeList= new ArrayList();
	        
	        taxTypeList=bo.getTaxTypeList();
	        ArrayList detailList = bo.getTaxDetailTableList(taxid);
	        ArrayList incomeList = bo.getIncomeList(salaryid);
	        ArrayList flagList =bo.getFlagList();
	        
	        String income="";
	        String mode="0";
	        if(salaryid.trim().length()>0&&itemid.trim().length()>0){
	        	SalaryCtrlParamBo salarybo = new SalaryCtrlParamBo(this.frameconn,Integer.parseInt(salaryid));
	        	income=salarybo.getValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",itemid);
	        	mode=salarybo.getValue(SalaryCtrlParamBo.YS_FIELDITEM,"id",itemid,"mode");
	        	mode=mode!=null&&mode.trim().length()>0?mode:"0";
	        }
	        
	        TaxTableXMLBo xmlBo = new TaxTableXMLBo(this.getFrameconn());
	        String param = xmlBo.getParamValue(taxid);
	        this.getFormHM().put("taxid",taxid);
	        this.getFormHM().put("k_base",bo.getXS(k_base,2));
	        this.getFormHM().put("taxTypeList",taxTypeList);
	        this.getFormHM().put("detailList",detailList);
	        this.getFormHM().put("flagList",flagList);
	        
	        this.getFormHM().put("salaryid",salaryid);
	        this.getFormHM().put("itemid",itemid);
	        this.getFormHM().put("income",income);
	        this.getFormHM().put("incomeList",incomeList); 
	        this.getFormHM().put("param",param);
	        this.getFormHM().put("mode",mode);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
