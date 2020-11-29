package com.hjsj.hrms.transaction.performance.showkhresult;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.showkhresult.CreateSqlStr;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ShowScoreResultTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		/*
		 * orm=0显示查看某个考核对象信息
		 * =1查看某个考核主体打分信息
		 * =2查看所有考核对象信息
		 */
		HashMap formhm=this.getFormHM();
		HashMap reqhm=(HashMap) formhm.get("requestPamaHM");
		String opt=(String)reqhm.get("opertor");
		String who="all";
		String modelType="ALL";
		ContentDAO dao =new ContentDAO(this.getFrameconn());
		String tplan_id="-1";
		if("1".equals(opt)){
			try
			{
				modelType=(String)reqhm.get("modelType");
	    		tplan_id=CreateSqlStr.getInitinfo(dao,modelType);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		else
		{
			modelType=(String)formhm.get("modelType");
			tplan_id=(String) formhm.get("plan_id");
		}
		if(tplan_id==null)
		{
			tplan_id="-1,-1";
			throw GeneralExceptionHandler.Handle(new Exception("没有符合条件的考核计划！"));
		}
		String[] templan=tplan_id.split(",");
		String plan_id=templan[0];
		String templateID=templan[1];
		BatchGradeBo batchGradeBo =new BatchGradeBo(this.getFrameconn(), plan_id);
		String ssss=batchGradeBo.getTableHeaderHtml(templateID,plan_id,"1",modelType);
		boolean isDeductMark = batchGradeBo.isHaveReasonsCloumn(plan_id);
		String isShowDeductMark="0";
		if(isDeductMark)
			isShowDeductMark="1";
		String flag=(String) formhm.get("flag");
		if(reqhm.containsKey("object_id")){
			who=(String)reqhm.get("object_id")+","+"object_id";
			String[] re=CreateSqlStr.getObjectInfo(dao,plan_id,(String)reqhm.get("object_id"));
			ssss=batchGradeBo.getTableHeaderHtml(templateID,plan_id,"2",modelType);
			isShowDeductMark="0";
			reqhm.remove("object_id");
			formhm.put("ocname",re[0]);
			formhm.put("odname",re[1]);
			formhm.put("objectname",re[2]);
		}
		if(reqhm.containsKey("mainbody_id")){
			who=(String) reqhm.get("mainbody_id")+","+"mainbody_id";
			ssss=batchGradeBo.getTableHeaderHtml(templateID,plan_id,"3",modelType);
			String[] re=CreateSqlStr.getMainbodyInfo(dao,plan_id,(String) reqhm.get("mainbody_id"));
			isShowDeductMark="0";
			reqhm.remove("mainbody_id");
			formhm.put("mcname",re[0]);
			formhm.put("mdname",re[1]);
			formhm.put("mainbodyname",re[2]);
		}
		if(flag==null|| "".equals(flag)){
			flag="score";
		}
		if("score".equals(flag)){
			formhm.put("sod","s");
		}else{
			formhm.put("sod","d");
		}
		ArrayList pointinfo=new ArrayList();
		ArrayList pointname=new ArrayList();
		ArrayList apointList=new ArrayList();
		ArrayList layItemList=new ArrayList();
		HashMap itemPoint=new HashMap();
		StringBuffer sbpoint=new StringBuffer();
		//分析绩效考核模版
		ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
		parameterSetBo.anaylseTemplateTable(apointList,layItemList,itemPoint,templateID,"");
		ArrayList guildlist=apointList;
		try {
			pointinfo=CreateSqlStr.getPointname(dao,apointList);
		} catch (Exception e1) {
			e1.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kh.plan.nofindpointid"),"",""));

		}
		for(int i=0;i<pointinfo.size();i++){
			String p=(String) pointinfo.get(i);
			String[] pp=p.split(",");
			pointname.add(pp[1]);
		}
		sbpoint.append("emp_001");
		ArrayList MOlist =CreateSqlStr.getMOinfo(dao,plan_id);
		formhm.put("molist",MOlist);
		formhm.put("pointname",pointname);
		formhm.put("sql",CreateSqlStr.getSql(guildlist,flag));
		formhm.put("where",CreateSqlStr.getWhere(guildlist,flag,plan_id,who));
		//System.out.println(CreateSqlStr.getSql(guildlist,flag)+" "+CreateSqlStr.getWhere(guildlist,flag,plan_id,who));
    	formhm.put("column",CreateSqlStr.getcolumns(guildlist,flag));
		formhm.put("orderby",CreateSqlStr.getOrderby());
		formhm.put("sbpoint",sbpoint.toString());
		formhm.put("fieldlist",CreateSqlStr.getFieldList(apointList));
		formhm.put("guildlist",guildlist);
		formhm.put("header",ssss);
		formhm.put("isShowDeductMark", isShowDeductMark);
        formhm.put("modelType",modelType);
        formhm.put("plan_id",tplan_id);
        formhm.put("flag", flag);
		try {
			formhm.put("objectList",CreateSqlStr.getObjectInfo(dao,plan_id));
			formhm.put("mainbodyList",CreateSqlStr.getMainbodyInfo(dao,plan_id));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kh.nofind.objectormainbodyinfo"),"",""));

		}
		
//		System.out.println((String)formhm.get("sql")+formhm.get("where")+formhm.get("orderby"));
		try {
			String selstr=CreateSqlStr.getSelstr(dao,plan_id,modelType);
			formhm.put("selstr",selstr);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kh.plan.nofindpointid"),"",""));

		}
	}
}
