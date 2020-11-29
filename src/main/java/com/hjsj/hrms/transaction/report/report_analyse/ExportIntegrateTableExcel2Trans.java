package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.ReportExcelBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;

public class ExportIntegrateTableExcel2Trans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			String tabid=(String)this.getFormHM().get("tabid");
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			String operateObject="4";
			String unitcode=(String)this.getFormHM().get("unitcode");
			String   nums=(String)this.getFormHM().get("nums");   					 // aXX:列选择   bXX：行选择
			String temp_str=(String)this.getFormHM().get("temp_str");  //":2010;1;6;1:2010;1;6;1:0~:2010;1;6;2:2010;1;6;2:0 //     //生成综合表的条件;
			temp_str=PubFunc.keyWord_reback(temp_str);
			String[] right_fields=temp_str.split("~");
			String   cols=(String)this.getFormHM().get("cols");
			String   sortid="0";	  //表类别id
			String   tname="";        //表名称
			String totalnum = (String)this.getFormHM().get("totalnum");
			this.frowset=dao.search("select tsortid,name from tname where tabid="+tabid);
			if(this.frowset.next())
			{
				sortid=this.frowset.getString("tsortid");
				tname=this.frowset.getString("name");
			}
			
			IntegrateTableBo integrateTableBo=new IntegrateTableBo(this.getFrameconn(),totalnum);
			UserView _userview=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			{
				_userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
				_userview.canLogin();
			}
			
			ArrayList resultList=null;
			String reportTypes = (String)this.getFormHM().get("reportTypes");
			String countid = (String)this.getFormHM().get("countid");
			String weekid = this.getFormHM().get("weekid2")==null?"":(String)this.getFormHM().get("weekid2");
			String yearid =(String) this.getFormHM().get("yearid");
			
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
				resultList=integrateTableBo.getIntegrateTableAnalyseData(right_fields,tabid,sortid,nums,unitcode,Integer.parseInt(cols),_userview.getUserId(),_userview.getUserName(),reportTypes,yearid,countid,weekid);
			else
				resultList=integrateTableBo.getIntegrateTableAnalyseData(right_fields,tabid,sortid,nums,unitcode,Integer.parseInt(cols),this.getUserView().getUserId(),this.getUserView().getUserName(),reportTypes,yearid,countid,weekid);
			
			
			ReportExcelBo bbo=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
				bbo=new ReportExcelBo(_userview,tabid,unitcode,operateObject,this.getFrameconn(),nums,resultList,right_fields);
			else
				bbo=new ReportExcelBo(this.getUserView(),tabid,unitcode,operateObject,this.getFrameconn(),nums,resultList,right_fields);
			String outName=bbo.executReportExcel();
			outName = PubFunc.encrypt(outName);  //add by wangchaoqun on 2014-9-22
			this.getFormHM().put("outName",SafeCode.decode(outName));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
