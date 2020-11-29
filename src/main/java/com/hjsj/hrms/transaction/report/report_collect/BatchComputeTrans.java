package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportOperationFormulaAnalyse;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class BatchComputeTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String units=(String)this.getFormHM().get("units");
		 	String formula_str=(String)this.getFormHM().get("formulastr");
			String flag=(String)this.getFormHM().get("flag"); //1:表内计算  2:表间计算
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
					if(record[i]==null||record[i].trim().length()==0)
						continue;
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
			RecordVo vo=null;
			
			String[] strArr=units.split(",");
			ArrayList unitList=new ArrayList();
			for(int i=0;i<strArr.length;i++)
			{
				if(strArr[i]!=null&&strArr[i].length()>0)
					unitList.add(strArr[i]);
			}
			
			ArrayList alist=new ArrayList();
			int n=0;
			for(int i=0;i<formulaList.size();i++)
			{
				vo=(RecordVo)formulaList.get(i);
				alist=new ArrayList();
				alist.add(vo);
				int aflag = vo.getInt("colrow");	//公式类型
				int tabid=vo.getInt("tabid");
				if(aflag==1) // 表内列公式
				{
					reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),String.valueOf(tabid),alist,2,unitList);
					reportOperationFormulaAnalyse.setUserView(this.userView);
					String info=reportOperationFormulaAnalyse.reportFormulaAnalyse();
					if(!"null".equalsIgnoreCase(info))
					{
						n++;
						this.getFormHM().put("info",info);
						break;
					}
				}
				else
				{
				  
				   for(int j=0;j<unitList.size();j++)
				   {
					    String unitcode=(String)unitList.get(j);
					 	reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),String.valueOf(tabid),alist,2,unitcode);
						reportOperationFormulaAnalyse.setUserView(this.userView);
						String info=reportOperationFormulaAnalyse.reportFormulaAnalyse();
						 
						if(!"null".equalsIgnoreCase(info))
						{
							this.getFormHM().put("info",info);
							n++;
							break;
						}
				   }
				   if(n!=0)
					   break;
				
				}
			}
			if(n==0)
				this.getFormHM().put("info","null");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
