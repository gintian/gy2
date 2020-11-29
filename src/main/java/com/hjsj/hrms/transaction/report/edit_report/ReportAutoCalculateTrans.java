package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TformulaBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportAutoFormulaAnalyse;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:ReportAutoCalculateTrans</p>
 * <p>Description:报表自动计算</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 12, 2006:9:05:34 AM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class ReportAutoCalculateTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String tabid=(String)this.getFormHM().get("tabid");
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		String gridName=(String)this.getFormHM().get("gridName");  //得到格名称
		String i=gridName.substring(1,gridName.indexOf("_"));
		String j=gridName.substring(gridName.indexOf("_")+1);
		//得到报表的表内计算公式
		TformulaBo tformulaBo=new TformulaBo(this.getFrameconn());
		ArrayList withinFormulaList=tformulaBo.getFormula(tabid,"a");		//内部计算公式集合
		
		TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid);
		
		int[][]   digitalResults=tnameBo.getDigitalResults();
		HashMap  rowMap=tnameBo.getRowMap();
		HashMap  colMap=tnameBo.getColMap();	
		ArrayList formulaList=tformulaBo.findFormulaList(i,j,withinFormulaList,rowMap,colMap);

		int rows=Integer.parseInt((String)this.getFormHM().get("rows"));
		int cols=Integer.parseInt((String)this.getFormHM().get("cols"));
		String info="";
		if(formulaList.size()>0)
		{
			//得到结果集
			double[][] result=new double[rows][cols];
			ArrayList resultList=(ArrayList)this.getFormHM().get("pageResult");
			for(int a=0;a<resultList.size();a++)
			{
				String[] rowResult=((String)resultList.get(a)).split("/");
				for(int b=0;b<rowResult.length;b++)
				{
					result[a][b]=Double.parseDouble(rowResult[b]);
				}
			}
			
/*			System.out.println("dengcan");
			for(int a = 0 ; a<result.length; a++){
				for(int b = 0; b<result[0].length;b++){
					System.out.print("  ["+a+"]["+b+"]= " +result[a][b]);
				}
				System.out.println();
			}
			System.out.println("**************************************************");
	*/
			ReportAutoFormulaAnalyse autoAnaalyse=new ReportAutoFormulaAnalyse(this.getFrameconn(),Integer.parseInt(i),Integer.parseInt(j),result,tnameBo,formulaList);
			info=autoAnaalyse.reportAutoFormulaAnalyse();
			
		}
		this.getFormHM().put("info",info);
		
		
	}

}
