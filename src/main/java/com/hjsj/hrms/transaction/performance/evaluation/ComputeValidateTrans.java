package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * 
 *<p>Title:ComputeValidateTrans</p> 
 *<p>Description:校验考评计划是否可计算</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 14, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class ComputeValidateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
//			String plan_id=(String)this.getFormHM().get("plan_id");
//			PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),plan_id,"");
//			this.getFormHM().put("info",SafeCode.encode(pe.computeValidate(plan_id)));
//			this.getFormHM().put("planStatus",String.valueOf(pe.getPlanVo().getInt("status")));
			
			 String validateInfo="";//计算前的校验信息
			 String validateOper="";//计算前的校验操作 分为考核主体对考核对象的考评的校验和等级结果的检查校验
			
			 String khObjWhere = (String)this.getFormHM().get("khObjWhere2");
			 khObjWhere=PubFunc.keyWord_reback(khObjWhere);
			 HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			 String planid = (String) hm.get("planid");
				CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
				boolean _flag = _bo.isHavePriv(this.userView, planid);
				if(!_flag){
					return;
				}
			 hm.remove("planid");
			 this.getFormHM().put("planid",planid);
			 
			 validateOper = (String) hm.get("validateOper");// 1-考核主体对考核对象的考评的校验 2-等级结果的检查校验
			 hm.remove("validateOper");
			
			 if("1".equals(validateOper))
			 {
					PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"");
					validateInfo=pe.computeValidate(planid,khObjWhere);
					if(validateInfo.trim().length()==0)//如果考核主体对考核对象的考评检验通过，要继续进行等级结果的检查校验
						validateOper="2";
			 }
			 if("2".equals(validateOper))
			 {
				 LoadXml loadXml=new LoadXml(this.getFrameconn(), planid);
				 Hashtable params = loadXml.getDegreeWhole();
				 String	perdegreeId=(String)params.get("GradeClass");	
				 if("-1".equals(perdegreeId.trim()))
				 {
					 this.getFormHM().put("validateInfo","");
					 this.getFormHM().put("validateOper",validateOper);
					 return;
				 }
				 
				 ContentDAO dao = new ContentDAO(this.getFrameconn());
				 RecordVo vo = new RecordVo("per_degree");
					vo.setString("degree_id", perdegreeId);
					try
					{
					    vo = dao.findByPrimaryKey(vo);
					} catch (SQLException e)
					{
					    e.printStackTrace();
					}
					String name = vo.getString("degreename");
					String flag = vo.getString("flag");// 0-分值 1-比例 2-混合先算分值 3-混合先算比例
					StringBuffer checkResult = new StringBuffer();
					checkResult.append(name);
					checkResult.append(" 检查结果:\n");
				
					StringBuffer errorInfo = new StringBuffer();
					StringBuffer warningInfo = new StringBuffer();
					StringBuffer hintInfo = new StringBuffer();
					int errorCount = 0;
					int warningCount = 0;
					int hintCount = 0;
				
					PerDegreeBo bo = new PerDegreeBo(this.getFrameconn(), perdegreeId,planid);
					ArrayList list = bo.getDegrees();
					if (list.size() == 0)
					{
					    errorInfo.append("\n错误:指定等级分类没有等级！");
					    errorCount++;
					}else if(list.size() == 1)
					{
					    warningInfo.append("\n警告:等级分类中只有一个等级！");
					    warningCount++;
					}
					String theStr = bo.testItemName();
					if(theStr.length()>0)
					{
					    errorInfo.append("\n错误:第"+theStr+"个等级的名称为空！");
					    errorCount++;
					}
					if ("1".equals(flag))
					{
					    float sumPercent = bo.getSumPercent();
					    if (Math.abs(100 - sumPercent) > 0.00001)
					    {
						errorInfo.append("\n错误:等级的比例之和不等于100%！");
						errorCount++;
					    }
					} else if ("2".equals(flag))//2-混合先算分值
					{
					    float sumPercent = bo.getSumPercent(flag);
					    if (list.size() > 2 && Math.abs(100 - sumPercent) > 0.00001)
					    {
						errorInfo.append("\n错误:等级的比例之和不等于100%！");
						errorCount++;
					    }
					} else if ("3".equals(flag))// 3-混合先算比例
					{
					    float sumPercent = bo.getSumPercent(flag);
					    if (sumPercent > 100)
					    {
						errorInfo.append("\n错误:等级的比例之和大于100%！");
						errorCount++;
					    }
					}
				
					if (("3".equals(flag) || "2".equals(flag)) && list.size() <= 2)
					{
					    hintInfo.append("\n提示:混合模式的等级级数大于2才有意义！");
					    hintCount++;
					}
				
					if ("0".equals(flag) || "2".equals(flag))
					{
					    float strict = bo.getStrict();
					    if (strict >= 1)
					    {
						warningInfo.append("\n警告:限制的比例大于或等于100%!");
						warningCount++;
					    }
					}
				
					if ("3".equals(flag) && list.size() >= 2 && bo.isSmallThan())
					{
					    warningInfo.append("\n警告:限制的分值比下一等级的最低分还要小!");
					    warningCount++;
					}
				
					if (warningCount > 0)
					    checkResult.append(warningInfo.toString());
					if (hintCount > 0)
					    checkResult.append(hintInfo.toString());
					if (errorCount > 0)
					    checkResult.append(errorInfo.toString());
					if(warningCount+hintCount+errorCount>0)	    
					{
					checkResult.append("\n\n\n合计:" + Integer.toString(errorCount) + "个错误,");
					checkResult.append(Integer.toString(warningCount) + "个警告,");
					checkResult.append(Integer.toString(hintCount) + "个提示。");
					validateInfo=checkResult.toString();
					}
				 
					
			 }
			 this.getFormHM().put("validateInfo",validateInfo);
			 this.getFormHM().put("validateOper",validateOper);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
