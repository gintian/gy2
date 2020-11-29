package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.CallableStatement;
import java.util.Hashtable;

/**
 * <p>Title:SaveGradeFormulaTrans.java</p>
 * <p>Description:绩效评估 等级计算公式</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-03-01</p>
 * @author JinChunhai
 * @version 4.0
 */

public class SaveGradeFormulaTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String planid = (String) this.getFormHM().get("planid");
		String gradeFormula = (String) this.getFormHM().get("gradeFormula");
		String procedureName = (String) this.getFormHM().get("procedureName");
		procedureName = PubFunc.keyWord_reback(procedureName);
		String isReCalcu = (String) this.getFormHM().get("isReCalcu");
		
		LoadXml loadXml = new LoadXml(this.getFrameconn(), planid, "");
		loadXml.saveAttribute("PerPlan_Parameter", "GradeFormula", gradeFormula + ";" + procedureName);

		loadXml = new LoadXml(this.getFrameconn(), planid);
		Hashtable params = loadXml.getDegreeWhole();
		String gradeId = (String) params.get("GradeClass");
		int paramsCount = 0;
		boolean isCorrect = true;
		CallableStatement cstmt = null; // 存储过程

		// 取得存储工程中的参数个数
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "";
		if ("1".equals(gradeFormula) && procedureName.trim().length() > 0)
		{
			try
			{
				if (Sql_switcher.searchDbServer() == Constant.MSSQL)
				{
					sql = "select count(*) b from syscolumns where ID in (SELECT id FROM sysobjects as a WHERE OBJECTPROPERTY(id, N'IsProcedure') = 1 and ";
					sql += " id = object_id(N'[dbo].[" + procedureName + "]'))";
				} else if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				{
					sql = "select count(*) b from user_arguments where object_name = '" + procedureName.toUpperCase() + "'";
				}
				this.frowset = dao.search(sql);
				if (this.frowset.next())
					paramsCount = this.frowset.getInt(1);

				// String sqlCall = "{call " + procedureName + "(?,?,?,?)}";
				StringBuffer sqlCall = new StringBuffer("{call  " + procedureName + "(?,?");
				if (paramsCount == 2)
					sqlCall.append(")}");
				else if (paramsCount == 3)
					sqlCall.append(",?)}");
				else if (paramsCount == 4)
					sqlCall.append(",?,?)}");
				else
					throw GeneralExceptionHandler.Handle(new GeneralException("", "存储过程参数个数不对!", "", ""));

				cstmt = this.frameconn.prepareCall(sqlCall.toString());
				cstmt.setString(1, "per_result_" + planid);// 第一个参数为绩效结果表
				cstmt.setInt(2, Integer.parseInt(gradeId));// 第二个参数为该计划使用的的等级分类号
				if (paramsCount > 2)
					cstmt.setString(3, "1=1");// 第三个参数为当前显示的考核对象范围(可选)
				if (paramsCount > 3)
					cstmt.setInt(4, Integer.parseInt(planid));// 第四个参数为当前计划的计划ID号(可选)
				//不在此执行存储过程了
//				if(isReCalcu!=null && isReCalcu.equalsIgnoreCase("ok"))
//				{
//					cstmt.execute();
//				}
				
			} catch (Exception e)
			{
				isCorrect = false;
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(new GeneralException("", "调用存储过程出错！" + e.getMessage(), "", ""));
			} finally
			{
				PubFunc.closeResource(cstmt);
			}
		}
		if (isCorrect)
			this.getFormHM().put("isCorrect", "1");
		else
			this.getFormHM().put("isCorrect", "0");
		if(isReCalcu!=null && "ok".equalsIgnoreCase(isReCalcu))
		{
			PerEvaluationBo bo = new PerEvaluationBo(this.getFrameconn(), planid, "", this.userView);
			LoadXml loadxml = new LoadXml(this.frameconn, planid, "");
			Hashtable htxml = new Hashtable();			
			htxml = loadxml.getDegreeWhole();
			String gradeID = (String)htxml.get("GradeClass");
			bo.setGradeValue(gradeID,1);
		}
		
	}

}
