package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.ReportResultBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.auto_fill_report.reportanalyse.ReportInstantCheck;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:报表即时表内校验</p>
 * <p>Description:报表即时校验-特定报表内校验，对传入的二维数组数据进行分析</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class PromptlyValidateTrans extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		
		String tabid=(String)this.getFormHM().get("tabid");
		String obj1 = (String) this.getFormHM().get("obj1");
		String username = SafeCode.decode((String) this.getFormHM().get("username"));
		if(username==null|| "".equals(username)){
			username = this.getUserView().getUserName();
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
		}

		int    rows=Integer.parseInt((String)this.getFormHM().get("rows"));
		int    cols=Integer.parseInt((String)this.getFormHM().get("cols"));
		int    operateObject=Integer.parseInt((String)this.getFormHM().get("operateObject"));
		String unitcode=(String)this.getFormHM().get("unitcode");

		ArrayList results=new ArrayList();
		ReportResultBo resultbo = new ReportResultBo(this.getFrameconn());
		if(operateObject==1){
			results =resultbo.getTBxxResultList(tabid,username);
		}else{
			results =resultbo.getTTxxResultList(tabid,unitcode);
		}
//		if(this.getFormHM().get("operate")!=null)
//		{
//			String a_result=(String)this.getFormHM().get("results");
//			String[] temp=a_result.split("#");
//			for(int i=0;i<temp.length;i++)
//				results.add(temp[i]);
//		}
//		else
//			results=(ArrayList)this.getFormHM().get("results");
		double[][] value=new double[rows][cols];
		for(int i=0;i<rows;i++)
		{
			if(results!=null&&results.size()>i){
			String[] temp=(String[])results.get(i);
			String[] temp_arr=temp;
			for(int j=0;j<cols;j++)
			{
				//add by wangchaoqun on 2014-9-28  当表被修改保存后，数据库中数据可能被改为null，此时需要兼容这种数据
				if(temp_arr[j] != null && !"null".equals(temp_arr[j])){
					value[i][j]=Double.parseDouble(temp_arr[j]);
				}
			}
			}else{
				for(int j=0;j<cols;j++)
				{
					value[i][j]=Double.parseDouble("0.0");
				}
			}
		}


		TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid);		
		String sqlFlag=this.getUserView().getUserName();
		if(operateObject==2)
			sqlFlag=unitcode;
		
		ReportInstantCheck reportInstantCheck=new  ReportInstantCheck(this.getFrameconn(),value ,tnameBo,operateObject,sqlFlag);

		
/*
	    String validateInfo="列校验:2+a+3=4..6*C100排除(12)行@null@语法错误：左表达式'2+a' 中无效字符！@null@null@null@null#列校验:2=6..8@第1行4≠6";
		   validateInfo+="\\n第2行20≠26\\n第3行21≠20025\\n第4行4≠6\\n第5行3≠4\\n第7行3≠20002@null@2,@6,7,8,@1,2,3,4,5,@null#行校验:14++7=(1..13)排除(8,12,333)列@null@语法错误：";
		   validateInfo+="左表达式'14+' 中此处语法错误！@null@null@null@null";
*/	
		  String  validateInfo=reportInstantCheck.reportInstantCheck();
		
		this.getFormHM().put("info",validateInfo);
	}
	
	
	

}
