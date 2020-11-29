package com.hjsj.hrms.transaction.kq.register.historical;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.KqReportInit;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.history.ExecuteKqSumExcel;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterInitInfoData;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.HashMap;
/**
 * 
 * <p>Title:考勤报表月明细excel</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 16, 2009:2:16:00 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class CreateKqXmlExcelTrans extends IBusiness{

	public void execute() throws GeneralException {
		HSSFWorkbook workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
		try{
			HashMap hm=(HashMap)this.getFormHM();
			
			String code=(String)hm.get("code");
			String kind=(String)hm.get("kind");					
			String report_id=(String)hm.get("report_id");	
			String coursedate=(String)hm.get("coursedate");		
			String self_flag=(String)hm.get("self_flag");

			HSSFSheet sheet = workbook.createSheet("explain"); //生成一张表;
			String sort = (String) hm.get("sort");
			if (sort == null || sort.length() <= 0) {
				sort = "";
			}
			HSSFRow row = null;  //行
			HSSFCell cell=null;   //单元格
			if(coursedate==null||coursedate.length()<=0)
			{
				coursedate=RegisterDate.getKqDuration(this.getFrameconn());
			}
			if(!userView.isSuper_admin())
			{
				if(kind==null||kind.length()<=0)
				{
					LazyDynaBean bean=RegisterInitInfoData.getKqPrivCodeAndKind(userView);
					code=(String)bean.get("code");
					kind=(String)bean.get("kind");
				}			
			}else
			{
				if(code==null||code.length()<=0)
				{
				  ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
	 			  code=managePrivCode.getPrivOrgId();  			
				  kind="2";  
				}
			}
			if(kind==null||kind.length()<=0)
		   		kind="-2";
		   	if(!"-2".equals(kind)&&(code==null||code.length()<=0))
			{
			    code=userView.getUserOrgId();
				 		   
		    }
		   	if(!"-2".equals(kind)&&(code.length()<RegisterInitInfoData.getKqPrivCodeValue(userView).length()&&"UM".equals(RegisterInitInfoData.getKqPrivCode(userView))))
		    {
			   code=RegisterInitInfoData.getKqPrivCodeValue(userView);
			   kind="1";
		    }else if(!"-2".equals(kind)&&(code.length()<RegisterInitInfoData.getKqPrivCodeValue(userView).length()&&"@K".equals(RegisterInitInfoData.getKqPrivCode(userView))))
		    {
		    	code=RegisterInitInfoData.getKqPrivCodeValue(userView);
				kind="0";
		    }else if(kind==null||kind.length()<=0||code==null||code.length()<=0)
		    {
		    	if(this.userView.getUserDeptId()!=null&&this.userView.getUserDeptId().length()>0)
			    {
			    	code=this.userView.getUserDeptId();
			    	kind="1";
			    }else if(this.userView.getUserOrgId()!=null&&this.userView.getUserOrgId().length()>0)
			    {
			    	code=this.userView.getUserOrgId();
			    	kind="2";
			    }
		    }
		   	
			KqReportInit kqReportInit= new KqReportInit(this.getFrameconn());
			ReportParseVo parsevo =kqReportInit.getParseVo(report_id);
			String url="kq_"+ this.userView.getUserName() +".xls";
			if("q05".equals(parsevo.getValue().trim())){
				ExecuteKqSumExcel executeKqSumExcel = new ExecuteKqSumExcel(this.getFrameconn());
				executeKqSumExcel.setSorItem(sort);
				executeKqSumExcel.executeExcel(code,kind,coursedate,parsevo,this.userView,this.getFormHM(),workbook,sheet,row,cell);
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+url);
			workbook.write(fileOut);
			fileOut.close();	

			url = SafeCode.encode(PubFunc.encrypt(url));
			this.getFormHM().put("url",url);
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(workbook);
		}
	}

}
