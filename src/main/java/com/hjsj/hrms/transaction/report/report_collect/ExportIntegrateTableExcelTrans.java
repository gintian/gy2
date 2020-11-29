package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.ReportExcelBo;
import com.hjsj.hrms.businessobject.report.reportCollect.IntegrateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;

public class ExportIntegrateTableExcelTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			String tabid=(String)this.getFormHM().get("tabid");
			String operateObject="4";
			String unitcode=(String)this.getFormHM().get("unitcode");
			String   nums=(String)this.getFormHM().get("nums");   					 // aXX:列选择   bXX：行选择
			String temp_str=(String)this.getFormHM().get("temp_str");  //"UN:总部:'001':0:~UN:事业部:'002':0:~UN:中视影视事业部:'00201':0:";  //     //生成综合表的条件;
			if(temp_str!=null){
				temp_str=PubFunc.keyWord_reback(temp_str);
			}
			String[] right_fields=temp_str.split("~");
			String   cols=(String)this.getFormHM().get("cols");
			String   sortid="0";	  //表类别id
			String   tname="";        //表名称
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			if(temp_str!=null){
				temp_str=PubFunc.keyWord_reback(temp_str);
			}
			this.frowset=dao.search("select tsortid,name from tname where tabid="+tabid);
			if(this.frowset.next())
			{
				sortid=this.frowset.getString("tsortid");
				tname=this.frowset.getString("name");
			}
			UserView _userview=null;
			IntegrateTableBo integrateTableBo;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			{
				_userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
				_userview.canLogin();
				 integrateTableBo=new IntegrateTableBo(this.getFrameconn(),_userview.getUserId(),_userview.getUserName());
			}else{
				 integrateTableBo=new IntegrateTableBo(this.getFrameconn(),this.getUserView().getUserId(),this.getUserView().getUserName());
			}
			
			ArrayList resultList=integrateTableBo.getIntegrateTableData(right_fields,tabid,sortid,nums,unitcode,Integer.parseInt(cols));
			
			
			ReportExcelBo bbo=new ReportExcelBo(this.getUserView(),tabid,unitcode,operateObject,this.getFrameconn(),nums,resultList,right_fields);
			String outName=bbo.executReportExcel();
//			outName=outName.replaceAll(".xls","#");
			outName = PubFunc.encrypt(outName);  //add by wangchaoqun on 2014-9-22
			this.getFormHM().put("outName",outName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
