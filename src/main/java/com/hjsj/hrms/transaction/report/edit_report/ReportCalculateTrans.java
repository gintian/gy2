package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportOperationFormulaAnalyse;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ReportCalculateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String formula_str=(String)this.getFormHM().get("formula_str");
			String tabid=(String)this.getFormHM().get("tabid");
			String unitcode=(String)this.getFormHM().get("unitcode");
			String operateObject=(String)this.getFormHM().get("operateObject");
			String username=SafeCode.decode((String)this.getFormHM().get("username1"));
			String obj1=(String) this.getFormHM().get("obj1");
			if(username==null|| "".equals(username)){
				username = userView.getUserName();
				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			}
			if(formula_str!=null){
				formula_str=PubFunc.keyWord_reback(formula_str);
			}
			

			
			ArrayList formulaList=new ArrayList();
			if(formula_str.indexOf("&&")==-1)
			{
				String[] recorder=formula_str.split("§§");
				RecordVo vo=new RecordVo("tformula");
				vo.setInt("expid",Integer.parseInt(recorder[0]));	
				vo.setInt("tabid",Integer.parseInt(recorder[5]));
				vo.setString("cname",recorder[1]);
				vo.setString("lexpr",recorder[2]);
				vo.setString("rexpr",recorder[3]);
				vo.setInt("colrow",Integer.parseInt(recorder[4]));
				
				formulaList.add(vo);
			}
			else
			{
				String[] record=formula_str.split("&&");
				for(int i=0;i<record.length;i++)
				{
					String record_str=record[i];
					String[] recorder=record_str.split("§§");
					RecordVo vo=new RecordVo("tformula");
					vo.setInt("expid",Integer.parseInt(recorder[0]));	
					vo.setInt("tabid",Integer.parseInt(recorder[5]));
					vo.setString("cname",recorder[1]);
					vo.setString("lexpr",recorder[2]);
					vo.setString("rexpr",recorder[3]);
					vo.setInt("colrow",Integer.parseInt(recorder[4]));
					formulaList.add(vo);
				}
			}	
			ReportOperationFormulaAnalyse reportOperationFormulaAnalyse=null;
			if("1".equals(operateObject))
				reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),tabid,formulaList,Integer.parseInt(operateObject),username);
			else
				reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),tabid,formulaList,Integer.parseInt(operateObject),unitcode);
			reportOperationFormulaAnalyse.setUserView(this.userView);
			String info=reportOperationFormulaAnalyse.reportFormulaAnalyse();
	
			this.getFormHM().put("info",info);
			this.getFormHM().put("obj1", obj1);
			this.getFormHM().put("username", username);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
