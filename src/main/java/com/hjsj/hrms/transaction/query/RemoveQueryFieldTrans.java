/*
 * Created on 2005-5-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.valueobject.common.LabelValueView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveQueryFieldTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List str_valueList=(List)this.getFormHM().get("str_valuelist");
		try
		{
			LabelValueView labelValueView;
			if(!str_valueList.isEmpty())                   //判断List对象是否为空
			{
				for(int i=0;i<str_valueList.size();i++)
				{
					labelValueView=(LabelValueView)str_valueList.get(i);
				    if(labelValueView.getValue().equals(this.getFormHM().get("str_value")))
					{
						str_valueList.remove(i);             //删除所有选择的指标
						if(i!=0)
						{
							labelValueView=(LabelValueView)str_valueList.get(i-1);
							this.getFormHM().put("str_value",labelValueView.getValue());
						}
						break;
					}
				}
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			this.getFormHM().put("str_valuelist",str_valueList);
		}

	}

}
