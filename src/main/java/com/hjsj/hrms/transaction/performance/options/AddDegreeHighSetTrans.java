package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:AddDegreeHighSetTrans.java</p>
 * <p>Description:添加等级分类高级设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-17 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class AddDegreeHighSetTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		// 如果为编辑,num能取到值
		String num = (String) hm.get("num");
		String degreeID = (String) hm.get("degreeID");
		String plan_id = (String) hm.get("plan_id");
	    hm.remove("plan_id");
		PerDegreeBo bo = new PerDegreeBo(this.frameconn, degreeID, plan_id);
		String tableName = "degree_highset";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
	
		ArrayList degrees = bo.getDegrees();
		this.getFormHM().put("itemCount", new Integer(degrees.size()));
	
		try
		{
		    if (num == null || "".equals(num))
		    {
				this.getFormHM().put("num", "");
				this.getFormHM().put("mode", "1");
				this.getFormHM().put("oper", "1");
				this.getFormHM().put("value", "");
				this.getFormHM().put("grouped", "-1");
				this.getFormHM().put("UMGrade", "");
				for (int i = 0; i < degrees.size(); i++)
				{
				    LazyDynaBean bean = (LazyDynaBean) degrees.get(i);
				    bean.set("value", "0");
				}
				
		    } else
		    {
				this.frowset = dao.search("select * from " + tableName + " where num1=" + num);
				if (this.frowset.next())
				{
				    this.getFormHM().put("num", this.frowset.getString("num1"));
				    this.getFormHM().put("mode", this.frowset.getString("mode1"));
				    this.getFormHM().put("oper", this.frowset.getString("oper"));
				    this.getFormHM().put("value", this.frowset.getString("value")==null?"": this.frowset.getString("value"));
				    this.getFormHM().put("grouped", this.frowset.getString("grouped"));
					this.getFormHM().put("UMGrade", this.frowset.getString("UMGrade")==null?"": this.frowset.getString("UMGrade"));
				    for (int i = 0; i < degrees.size(); i++)
				    {
						LazyDynaBean bean = (LazyDynaBean) degrees.get(i);
						String id = (String) bean.get("id");
						String field = "degree" + id;
						// vo.setString(field, this.frowset.getString(field));
						bean.set("value", this.frowset.getString(field));
				    }
				}
		    }
		    
		    // 等级设置/高级设置 将原有按部门分组列改为:可以选择 部门、对象类别、以及评估表结构中引入的代码型指标 进行分组控制
		    
//		    String plan_id = (String) this.getFormHM().get("plan_id");		    	
			ArrayList groupList = bo.getGroupList(plan_id);
			this.getFormHM().put("groupList",groupList);
//			this.getFormHM().put("plan_id",plan_id);
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("degrees", degrees);
    }
}
