package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.team.KqClassArray;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 显示所有班次信息
 * <p>Title:BaseKqClassTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 22, 2007 1:32:33 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class BaseKqClassTrans  extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
		KqClassArray kqClassArray=new KqClassArray(this.getFrameconn(),userView);
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String flag=(String)hm.get("flag");
		if(flag==null||flag.length()<=0)
			flag="0";
		ArrayList list_vo=kqClassArray.selectAllKaClassVo(flag);
		this.getFormHM().put("class_list_vo",list_vo);
	}

	
}
