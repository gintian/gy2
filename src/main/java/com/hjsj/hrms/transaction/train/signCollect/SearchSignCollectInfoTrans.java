package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 得到浏览培训考勤汇总的信息
 * <p>Title:SearchSignCollectInfoTrans.java</p>
 * <p>Description>:SearchSignCollectInfoTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 12, 2011 2:53:04 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SearchSignCollectInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		//String classplan=(String)this.getFormHM().get("classplan");
		String courseplan=(String)this.getFormHM().get("courseplan");
		if(courseplan != null && courseplan.length()>0)
			courseplan = PubFunc.decrypt(SafeCode.decode(courseplan));
		
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
		StringBuffer cloums=new StringBuffer();//得到属性列
		for(int i=0;i<fielditemlist.size();i++){
			FieldItem fielditem=(FieldItem)fielditemlist.get(i);
			cloums.append(fielditem.getItemid()+",");					
		}
		cloums.setLength(cloums.length()-1);
		
		TrainAtteBo tb=new TrainAtteBo();
		String search=(String)this.getFormHM().get("search");
		search=tb.getSearchWhere(search);//条件查询
		
		String sql="select "+cloums.toString();
		String where ="from r47 where r4101='"+courseplan+"'"+search;
		String order="order by b0110,e0122";
		this.userView.getHm().put("train_sql", sql + " " + where + " " + order);
		this.userView.getHm().put("train_columns", cloums.toString());
		this.getFormHM().put("sql_str", sql);
		this.getFormHM().put("where_str", where);
		this.getFormHM().put("order_str", order);
		this.getFormHM().put("columns", cloums.toString());
		this.getFormHM().put("loadclass", "false");
		this.getFormHM().put("courseplan", SafeCode.encode(PubFunc.encrypt(courseplan)));
		this.getFormHM().put("search", "");
		TransDataBo transbo = new TransDataBo(this.getFrameconn(),"3"); 
		this.getFormHM().put("timelist",transbo.timeFlagList());
	}

}
