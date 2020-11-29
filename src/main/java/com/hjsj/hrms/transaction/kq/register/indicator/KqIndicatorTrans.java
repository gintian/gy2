package com.hjsj.hrms.transaction.kq.register.indicator;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 显示隐藏指标
 * <p>Title:KqIndicatorTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 27, 2007 2:02:24 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class KqIndicatorTrans extends IBusiness{
    public void execute()throws GeneralException
    {
    	String re_flag=(String)this.getFormHM().get("re_flag");
    	if(re_flag==null||re_flag.length()<=0)
    		re_flag="1";
    	this.getFormHM().put("re_flag",re_flag);
    	ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);        
    	this.getFormHM().put("fieldlist",fielditemlist);
    	ArrayList v_h_list=new ArrayList();
    	CommonData dataobj = new CommonData();
    	dataobj.setDataName("显示");
    	dataobj.setDataValue("1");
    	v_h_list.add(dataobj);
    	dataobj = new CommonData();
    	dataobj.setDataName("隐藏");
    	dataobj.setDataValue("0");
    	v_h_list.add(dataobj);
    	this.getFormHM().put("v_h_list",v_h_list);
    }
}
