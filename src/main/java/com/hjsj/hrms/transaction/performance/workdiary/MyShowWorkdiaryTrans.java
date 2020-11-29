package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MyShowWorkdiaryTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String searchterm = (String) hm.get("searchterm");
		String searchflag = (String) reqhm.get("searchflag");
		if(!"diary".equals(searchflag)){
			searchterm = "";
			hm.put("searchterm", searchterm);
		}
		reqhm.remove("searchflag");
		try{
			if(this.userView.getA0100()==null||userView.getA0100().trim().length()<1)//非自助用户不能使用该功能
				throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
			hm.put("state","0");
			ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
			WorkdiarySQLStr wss=new WorkdiarySQLStr();
			wss.checkState(this.frameconn);
			UserView uv=this.getUserView();
			//过滤掉叶面不显示内容
			ArrayList filterlist=this.filteritem(fieldlist);
			String[] sql=new String[4];
			String startime="";
			String endtime="";	
			
			String where = "";
			String ydm = (String)hm.get("ymd");
			ydm=ydm!=null&&ydm.trim().length()>0?ydm:"0";

			if(!reqhm.containsKey("search")){
				WeekUtils weekutils = new WeekUtils();
				int monthnum = Calendar.getInstance().get(Calendar.MONTH)+1;
				int yearnum = Calendar.getInstance().get(Calendar.YEAR);
				String monthstr = monthnum+"";
				if(monthnum<10)
					monthstr = "0"+monthnum;
				startime=yearnum+"-"+monthstr+"-01";
				endtime=weekutils.lastMonthStr(yearnum,monthnum);	
				reqhm.remove("currweek");
				hm.put("ymd",ydm);
				sql=wss.getMyworkdiaryshow(uv.getA0100(),filterlist,startime,endtime,"0");
				where = sql[1];
			}else{
				//2016/1/27 wangjl 新增一个查询条件
				searchterm = "请输入批示、内容".equals(searchterm)?"":searchterm;
				searchterm = PubFunc.keyWord_filter(searchterm);
				startime=(String) reqhm.get("startime");
				endtime=(String) reqhm.get("endtime");
				if(reqhm.containsKey("currweek")){
					WeekUtils weekutils = new WeekUtils();
					startime=weekutils.numWeekStr(1);
					endtime=weekutils.numWeekStr(7);
					reqhm.remove("currweek");
					hm.put("ymd","1");
					sql=wss.getMyworkdiaryshow(uv.getA0100(),filterlist,startime,endtime,"0");
					where = sql[1];
					
				}
				if(reqhm.containsKey("currmonth")){
					Calendar   c   =   Calendar.getInstance();
					int year = c.get(Calendar.YEAR);
					int month = c.get(Calendar.MONTH)+1;
					WeekUtils weekutils = new WeekUtils();
					endtime = weekutils.lastMonthStr(year,month);
					sql=wss.getMyworkdiaryshow(uv.getA0100(),filterlist,startime,endtime,"0");
					where = sql[1];
					if(!"".equals(searchterm)){
						where += " and (p0103 like '%"+searchterm+"%' or p0113 like '%"+searchterm+"%')";
					}
					reqhm.remove("currmonth");
					hm.put("ymd","0");
				}
				if(reqhm.containsKey("currday")){
					sql=wss.getMyworkdiaryshow(uv.getA0100(),filterlist,startime,"0");
					where = sql[1];
					reqhm.remove("currday");
					hm.put("ymd","2");
				}
				if(reqhm.containsKey("timefield")){
					sql=wss.getMyworkdiaryshow(uv.getA0100(),filterlist,startime,endtime,"0");
					where = sql[1];
					if(!"".equals(searchterm)){
						where += " and (p0103 like '%"+searchterm+"%' or p0113 like '%"+searchterm+"%')";
					}
					reqhm.remove("timefield");
					hm.put("ymd","3");
				}
				reqhm.remove("search");
				reqhm.remove("startime");
				reqhm.remove("endtime");
			}
			
			
			
			hm.put("fieldlist",filterlist);
			hm.put("sql",sql[0]);
			hm.put("where",where);
			hm.put("column",sql[2]);
			hm.put("orderby",sql[3]);
			hm.put("startime",startime);
			hm.put("endtime",endtime);
			hm.put("columlist", this.searchlist());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 根据后台设置的显示＆隐藏进行控制
	 * @param fieldlist
	 * @return
	 */
	public ArrayList filteritem(ArrayList fieldlist ){
		ArrayList fieldlist1=new ArrayList();
		StringBuffer buf=new StringBuffer();
		buf.append("p0100");
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem) fieldlist.get(i);
			if(buf.indexOf(field.getItemid().toLowerCase())!=-1)
			{
				fieldlist1.add(field);
				continue;
			}
			if("nbase".equalsIgnoreCase(field.getItemid()))
				continue;
			if("A0101".equalsIgnoreCase(field.getItemid())|| "E0122".equalsIgnoreCase(field.getItemid())|| "E01A1".equalsIgnoreCase(field.getItemid()))
				continue;
			if("p0115".equalsIgnoreCase(field.getItemid())){
				fieldlist1.add(field);
			}else 
				if(field.isVisible())
					fieldlist1.add(field);
		}
		return fieldlist1;
/*		
		ArrayList item=new ArrayList();
		item.add("b0110");
//		item.add("p0100");
		item.add("e0122");
		item.add("e01a1");
		item.add("nbase");
		item.add("a0100");
		item.add("a0101");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem field=(FieldItem) fieldlist.get(i);
			for(int j=0;j<item.size();j++){
				String itemid=(String) item.get(j);
				if(field.getItemid().equals(itemid)){
					fieldlist.remove(i);
					i--;
					}				
			}
		}
//		ArrayList fieldlist1=new ArrayList();
		for(int i=0;i<fieldlist.size();i++){
			FieldItem tempitem=(FieldItem)fieldlist.get(i);
			String tempstr=tempitem.getItemid();
//			if(tempstr.equals("p0113")){
//				fieldlist.add(0,tempitem);
//				fieldlist.remove(i+1);
//			}
//			if(tempstr.equals("p0103")){
//				fieldlist.add(0,tempitem);
//				fieldlist.remove(i+1);
//			}
//			if(tempstr.equals("p0101")){
//				fieldlist.add(0,tempitem);
//				fieldlist.remove(i+1);
//			}
			if(tempstr.equals("p0115")){
				fieldlist.add(0,tempitem);
				fieldlist.remove(i+1);
			}
			
			
		}
		return fieldlist;
		*/
	}
	//生成按查询方式的指标
	private ArrayList searchlist(){
		ArrayList filterlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
		FieldItem item13 = DataDictionary.getFieldItem("p0113");
		FieldItem item03 = DataDictionary.getFieldItem("p0103");
		ArrayList columlist = new ArrayList();
		CommonData obj=new CommonData("none","");
		columlist.add(obj);
		for(int i=0;i<filterlist.size();i++){
			FieldItem field=(FieldItem) filterlist.get(i);
			if("p0113".equalsIgnoreCase(field.getItemid())|| "p0103".equalsIgnoreCase(field.getItemid())){
				obj=new CommonData(field.getItemid()+"`"+field.getItemtype()+"`"+field.getCodesetid(),field.getItemdesc());
				columlist.add(obj);
			}
		}
		return columlist;
	}

}
