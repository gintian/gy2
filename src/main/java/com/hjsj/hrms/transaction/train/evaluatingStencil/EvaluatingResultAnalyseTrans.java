package com.hjsj.hrms.transaction.train.evaluatingStencil;

import com.hjsj.hrms.businessobject.performance.singleGrade.SingGradeTemplateBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvaluatingResultAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		try
		{
			String r3101=(String)hm.get("r3101");
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(r3101);
			//判断前台传过来的培训班编号是否加密
			if(!isNum.matches())
			    r3101 = PubFunc.decrypt(SafeCode.decode(r3101));
			
			String where = "";
            if (!this.userView.isSuper_admin()) {
                TrainCourseBo bo = new TrainCourseBo(this.userView);
                String a_code = bo.getUnitIdByBusi();
                
                TransDataBo tbo = new TransDataBo();
                where = tbo.sqlWhere(null, a_code, null, null);
                where = where.substring(where.indexOf("where") + 5);
            }
            
            if(where == null || where.length() < 1)
                where = "1=1";
			
			String templateid=(String)hm.get("templateid");
			String titleName=(String)this.getFormHM().get("titleName");
			if(titleName==null)
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select * from r31 where r3101='"+r3101+"' and " + where);
				if(this.frowset.next())
					titleName=this.frowset.getString("r3130");
				
			}
			
			SingGradeTemplateBo singGradeTemplateBo=new SingGradeTemplateBo(this.getFrameconn(),20);
			singGradeTemplateBo.setR3101(r3101);
			String html=singGradeTemplateBo.getAnalyseHtml(templateid,titleName);
			
		//	EvaluatingToExcelBo bo=new EvaluatingToExcelBo(this.getFrameconn(),1);
		//	bo.setR3101(r3101);
		//	String url=bo.getEvaluatingExcel(templateid,titleName);
			
			this.getFormHM().put("analyseHtml",html);
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
