package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:CheckPerDegreeTrans.java</p>
 * <p>Description:检查考核等级分类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-01-04 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class CheckPerDegreeTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
    	try
    	{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			
			String returnflag=(String)hm.get("returnflag");
			this.getFormHM().put("returnflag",returnflag);
			
			
			String perdegreeId ="";
			if(hm!=null)
				perdegreeId=(String) hm.get("degreeID");
			
/*			
			boolean aflag=false;
			if(this.getFormHM().get("plan_id")!=null && this.getFormHM().get("plan_id").toString().trim().length()>0)
			{
				String plan_id=(String)this.getFormHM().get("plan_id");
				LoadXml loadXml=new LoadXml(this.getFrameconn(), plan_id);
				Hashtable params = loadXml.getDegreeWhole();
				perdegreeId=(String)params.get("GradeClass");					//等级分类ID
				this.getFormHM().remove("plan_id");
				aflag=true;
			}
*/
			
			if("-1".equals(perdegreeId.trim()))
			{
			    this.getFormHM().put("checkResult", "");
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
		
			PerDegreeBo bo = new PerDegreeBo(this.getFrameconn(), perdegreeId,"");
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
		
			if (("3".equals(flag) || "2".equals(flag)) && list.size() <= 2 && list.size()>0)
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
			}
			else
			  checkResult.append("没有发现等级分类需要修改的地方！");
			
//			if(aflag)
//				this.getFormHM().put("checkResult", checkResult.toString());
//			else
				this.getFormHM().put("checkResult", checkResult.toString());
    	
    	
    	
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
    	}

    }

}
