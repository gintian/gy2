/**
 * 
 */
package com.hjsj.hrms.test;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:TestApplectTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2007-1-25:16:35:28</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class TestApplectTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list=(ArrayList)this.getFormHM().get("list");
		if(list==null||list.size()==0)
		{
			System.out.println("--->list size=0 or null");
			return;
		}
		int i=0;
		for(i=0;i<list.size();i++)
		{
			System.out.println(i+"===>"+list.get(i));
		}
		list.clear();
		list.add("how are you!");
		this.getFormHM().put("result",list);
	}

}
