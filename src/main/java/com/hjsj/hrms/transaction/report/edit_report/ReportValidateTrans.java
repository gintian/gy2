package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ReportSpaceIntantCheck;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 * <p>Title:处理表间校验</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2006:10:03:53 AM</p>
 * @author dengc
 * @version 1.0
 *
 */
public class ReportValidateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		String username = SafeCode.decode((String) this.getFormHM().get("username"));
		if(username==null|| "".equals(username)){
			username = this.getUserView().getUserName();
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		}
		int    rows=Integer.parseInt((String)this.getFormHM().get("rows"));
		int    cols=Integer.parseInt((String)this.getFormHM().get("cols"));
		ArrayList results=(ArrayList)this.getFormHM().get("results");
		int    operateObject=Integer.parseInt((String)this.getFormHM().get("operateObject"));
		String unitcode=(String)this.getFormHM().get("unitcode");
		double[][] value=new double[rows][cols];
		for(int i=0;i<rows;i++)
		{
			String temp=(String)results.get(i);
			String[] temp_arr=temp.split("/");
			for(int j=0;j<cols;j++)
			{
				value[i][j]=Double.parseDouble(temp_arr[j]);
			}
		}
		//String sqlFlag=this.getUserView().getUserName();
		if(operateObject==2)
			username=unitcode;
		ReportSpaceIntantCheck rsic = new ReportSpaceIntantCheck(this.getFrameconn(),value,tabid,operateObject,username);
		rsic.setUserView(this.userView);
		 String validateInfo = rsic.reportSpaceIntantChek();
/*//		TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid,this.getUserView().getUserId(),this.getUserView().getUserName()," ");		
//		ReportInnerCheck reportInnerCheck=new  ReportInnerCheck(this.getFrameconn(),value ,tnameBo);	
		//	String validateInfo=reportInnerCheck.getReportInnerCheckErrors ();
	    String validateInfo="列校验:2+a+3=4..6*C100排除(12)行@null@语法错误：左表达式'2+a' 中无效字符！@null@null@null@null#列校验:2=5..7@第1行4≠6";
		   validateInfo+="\\n第2行20≠26\\n第3行21≠20025\\n第4行4≠6\\n第5行3≠4@null@2,@5,6,7,@1,2,3,4,5,@null#行校验:14++7=(1..13)排除(8,12,333)列@null@语法错误：";
		   validateInfo+="左表达式'14+' 中此处语法错误！@null@null@null@null";*/
	//	 System.out.println("vinfo=" + validateInfo);
		this.getFormHM().put("info",validateInfo);

	}

}
