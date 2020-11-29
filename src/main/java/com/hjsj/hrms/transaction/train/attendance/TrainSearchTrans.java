package com.hjsj.hrms.transaction.train.attendance;

import com.hjsj.hrms.businessobject.train.attendance.TrainAtteBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author LiWeichao
 * @version 1.0
 * 
 */
public class TrainSearchTrans extends IBusiness {

	public void execute() throws GeneralException {
		
	    	HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
	    	String t_type=(String)reqhm.get("t_type");
	    	String sort=(String)reqhm.get("sort");
	    	sort=sort==null||sort.length()<1?"1":sort;
	    	reqhm.remove("t_type");
	    	reqhm.remove("sort");
	    	this.getFormHM().put("sort", sort);
	    	
	    	ArrayList fieldlist = new ArrayList();
	    	//t_type=tr_cradtime培训考勤签到
	    	if("tr_cradtime".equalsIgnoreCase(t_type))
	    		fieldlist=cardTimeItemlist();
	    	//t_type=r47培训考勤汇总
	    	else if("r47".equalsIgnoreCase(t_type))
	    		fieldlist=r47Itemlist(sort);
	    	
	    	this.getFormHM().put("fieldlist",fieldlist);
	    	
	    	String r4101=(String)reqhm.get("r4101");
	    	ArrayList timelist=new ArrayList();
	    	if(r4101!=null&&r4101.length()>0){
	    		TrainAtteBo tb=new TrainAtteBo();
	    		String str[]=tb.getR41Info(this.getFrameconn(), r4101);
	    		if(str[0]!=null&&str[0].length()>0&&str[1]!=null&&str[1].length()>0)
	    			timelist=tb.displayEveryDate(str[0], str[1]);
	    	}
	    	this.getFormHM().put("tjtimelist", timelist);
	}
	private ArrayList cardTimeItemlist(){
		ArrayList itemlist = new ArrayList();
		CommonData tempobj = new CommonData("A0101:A:0:tr_cardtime",ResourceFactory.getProperty("label.title.name"));
		itemlist.add(tempobj);
		tempobj = new CommonData("B0110:A:UN:tr_cardtime",ResourceFactory.getProperty("b0110.label"));
		itemlist.add(tempobj);
		tempobj = new CommonData("E0122:A:UM:tr_cardtime",ResourceFactory.getProperty("e0122.label"));
		itemlist.add(tempobj);
		tempobj = new CommonData("E01A1:A:@K:tr_cardtime",ResourceFactory.getProperty("e01a1.label"));
		itemlist.add(tempobj);
		tempobj = new CommonData("card_time:D:0:tr_cardtime","刷卡日期");//ResourceFactory.getProperty("train.b_plan.reg.time"));
		itemlist.add(tempobj);
		tempobj = new CommonData("card_type:S:0:tr_cardtime",ResourceFactory.getProperty("reporttypelist.sort"));
		itemlist.add(tempobj);
		tempobj = new CommonData("leave_early:N:0:tr_cardtime","早退(分)");//ResourceFactory.getProperty("kq.class.leave_early"));
		itemlist.add(tempobj);
		tempobj = new CommonData("late_for:N:0:tr_cardtime","迟到(分)");//ResourceFactory.getProperty("kq.class.be_late_for"));
		itemlist.add(tempobj);
		
		return itemlist;
	}
	private ArrayList r47Itemlist(String sort){
		ArrayList itemlist  = DataDictionary.getFieldList("r47",
				Constant.EMPLOY_FIELD_SET);
		ArrayList newItem=new ArrayList();
		CommonData dataobj = new CommonData();
		if("2".equals(sort)){
			/*dataobj = new CommonData("r4101:A:1_06:r47","培训课程");
			newItem.add(dataobj);*///复杂的很，很复杂 跟r13和r41,都有关系
		}
		if("3".equals(sort)){
			//dataobj = new CommonData(":A:1_06:","培训班名称");
			//newItem.add(dataobj);
		}
		for (int i = 0; i < itemlist.size(); i++) {
			FieldItem fielditem=(FieldItem) itemlist.get(i);
			if("nbase".equalsIgnoreCase(fielditem.getItemid()))
				continue;
			if("a0100".equalsIgnoreCase(fielditem.getItemid()))
				continue;
			if("r4101".equalsIgnoreCase(fielditem.getItemid()))
				continue;
			if(!"1".equals(sort)){
				if("b0110".equalsIgnoreCase(fielditem.getItemid()))
					continue;
				if("e0122".equalsIgnoreCase(fielditem.getItemid()))
					continue;
				if("e01a1".equalsIgnoreCase(fielditem.getItemid()))
					continue;
				if("a0101".equalsIgnoreCase(fielditem.getItemid()))
					continue;
			}
			if(!fielditem.isVisible())
				continue;
			String desc=fielditem.getItemdesc();
			if("b0110".equalsIgnoreCase(fielditem.getItemid()))
				desc=ResourceFactory.getProperty("b0110.label");
			if("e0122".equalsIgnoreCase(fielditem.getItemid()))
				desc=ResourceFactory.getProperty("e0122.label");
			if("e01a1".equalsIgnoreCase(fielditem.getItemid()))
				desc=ResourceFactory.getProperty("e01a1.label");
			dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemtype()
				+":"+fielditem.getCodesetid()+":"+fielditem.getFieldsetid(),
				desc);
			newItem.add(dataobj);
		}
		
		return newItem;
	}

}
