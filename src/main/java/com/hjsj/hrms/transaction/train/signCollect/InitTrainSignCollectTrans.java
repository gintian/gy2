package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class InitTrainSignCollectTrans  extends IBusiness {

	public void execute() throws GeneralException {
		String sort="1";//分类 1：人员：2：课程；3：班组
		ArrayList sortlist=new ArrayList();
		CommonData da=new CommonData();
		da.setDataName(ResourceFactory.getProperty("train.sign_collect.emp"));
        da.setDataValue("1");
        sortlist.add(da);
        da=new CommonData();
		da.setDataName(ResourceFactory.getProperty("train.sign_collect.class"));
        da.setDataValue("2");
        sortlist.add(da);
        da=new CommonData();
		da.setDataName(ResourceFactory.getProperty("train.sign_collect.team"));
        da.setDataValue("3");
        sortlist.add(da);        
        this.getFormHM().put("sort",sort);
        this.getFormHM().put("sortlist", sortlist);
        //显示部门层数
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
	    if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
        this.getFormHM().put("uplevel",uplevel); 
        DbWizard dbw = new DbWizard(this.getFrameconn());
        if(!dbw.isExistTable("R47",false))
        	return;
        ArrayList fielditemlist=DataDictionary.getFieldList("R47",Constant.USED_FIELD_SET);	
        ArrayList list=new ArrayList();
        for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);			
			if("1".equals(fielditem.getState()))
			{						
				fielditem.setVisible(true);					
			}else
			{
				fielditem.setVisible(false);
			}
			list.add(fielditem.clone());			
		}
        this.getFormHM().put("fielditemlist", list);
        this.getFormHM().put("a_code", "");
        this.getFormHM().put("view_record", "true");
//        StringBuffer cloums=new StringBuffer();//得到属性列
//		for(int i=0;i<fielditemlist.size();i++){
//			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
//			cloums.append(fielditem.getItemid()+",");					
//		}
//		cloums.setLength(cloums.length()-1);
//		String sql="select "+cloums.toString();
//		String where ="from r47 where 1=2";
//		String order="order by b0110,e0122";
//		this.getFormHM().put("sql_str", sql);
//		this.getFormHM().put("where_str", where);
//		this.getFormHM().put("order_str", order);
//		this.getFormHM().put("columns", cloums.toString());
//		this.getFormHM().put("loadclass", "false");
//		this.getFormHM().put("timeflag","00");
//		TransDataBo transbo = new TransDataBo(); 
//		this.getFormHM().put("timelist",transbo.timeFlagList());
        
	}

}
