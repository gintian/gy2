package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.general.impev.ImportantEvBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WorkdiaryShowTrans extends IBusiness {

	public void execute() throws GeneralException {
	        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
	        String A01="";
	        if(login_vo!=null) 
	          A01 = login_vo.getString("str_value").toLowerCase();
	        if(A01==null ||"".equals(A01)){
				throw GeneralExceptionHandler.Handle(new Exception("认证库没有选择"));
	        }
	       
			String pt_kpi_role_id = "";
			try{
				pt_kpi_role_id = SystemConfig.getProperty("pt_kpi_role_id");
			}catch(Exception e){
				//e.printStackTrace();
			}
			HashMap hm=this.getFormHM();
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
			String frommenu = (String)reqhm.get("frommenu");//为了控制timeflag
			reqhm.remove("frommenu");
			String fromoutermenu = (String)reqhm.get("fromoutermenu");//为了控制timeflag
			reqhm.remove("fromoutermenu");
			ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
			WorkdiarySQLStr wss=new WorkdiarySQLStr();
			UserView uv=this.getUserView();
			if((this.userView.getA0100()==null||userView.getA0100().trim().length()<1)&&userView.getStatus()==0)
				throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
			if(uv.getStatus()==0){
				uv=new UserView(uv.getS_userName(), uv.getS_pwd(), this.getFrameconn());
				try {uv.canLogin();} catch (Exception e) {e.printStackTrace();}
			}
			
			ArrayList filterlist=this.filteritem(fieldlist);//过滤掉页面不显示内容
			String fw="";
			String[] sql=new String[4];
			if(reqhm.containsKey("a_code")){
				fw=(String) reqhm.get("a_code");
			}
			String startime="";
			String endtime="";
			
			String logo = (String)reqhm.get("logo");///logo=null或空 从本模块进入        =1，从绩效进入 郭峰
			reqhm.remove("logo");
			String showDayWeekMonth = "";
			String plan_id = (String)reqhm.get("plan_id");
			if(plan_id!=null && plan_id.trim().length()>0)
			{
				LoadXml loadxml = new LoadXml(this.getFrameconn(), plan_id);
				Hashtable params = loadxml.getDegreeWhole();
				showDayWeekMonth = (String)params.get("ShowDayWeekMonth");			
			}
			String a0100=(String)hm.get("a0100");//绩效查看日志参数 这里把a0100作为标识
			a0100=a0100==null||a0100.length()<3?"":a0100;
			a0100 = PubFunc.convert64BaseToString(SafeCode.decode(a0100));
			String start_date=(String)hm.get("start_date");
			String end_date=(String)hm.get("end_date");
			
			Calendar calendar = Calendar.getInstance();
			String appstate = (String)hm.get("appstate");
			appstate=appstate!=null&&appstate.trim().length()>0?appstate:"all";
			ArrayList ymdlist=getYmdList(a0100,showDayWeekMonth,logo);
			String ymd = (String)hm.get("ymd");//0 日 1周 2月报
			if(logo!=null && "1".equals(logo)){//为了一开始让它显示周报（如果没有周报，则显示其它的）
				ymd = "";
			}
			if(ymdlist.size()==0){
				throw new GeneralException(ResourceFactory.getProperty("workdiary.message.func.erorr"));
			}
			if(ymd==null || ymd.trim().length()<=0)
			{
				if(ymdlist!=null&&ymdlist.size()>0)
				{
					CommonData obj=null;
					if(ymdlist.size()==1){
						obj = (CommonData)ymdlist.get(0);
					}else{
						obj = (CommonData)ymdlist.get(1);
					}
					ymd=obj.getDataValue();
					if(!"1".equals(ymd)){//如果不是周报
						if("2".equals(ymd) && "0".equals(((CommonData)ymdlist.get(0)).getDataValue()))
							ymd="0";
					}
				}
			}
			
			if(Integer.parseInt(ymd)>2){
				ymd = "1";
			}
			
			//从最外层菜单（不是生成树的菜单）进入时默认显示周报  郭峰
			if(fromoutermenu!=null && "1".equals(fromoutermenu)){
				if(ymdlist!=null&&ymdlist.size()>0)
				{
					CommonData obj=null;
					if(ymdlist.size()==1){
						obj = (CommonData)ymdlist.get(0);
					}else{
						obj = (CommonData)ymdlist.get(1);
					}
					ymd=obj.getDataValue();
					if(!"1".equals(ymd)){//如果不是周报
						if("2".equals(ymd) && "0".equals(((CommonData)ymdlist.get(0)).getDataValue()))
							ymd="0";
					}
				}
			}
			String yearnum = (String)hm.get("yearnum");
			yearnum=yearnum!=null&&yearnum.trim().length()>0?yearnum:calendar.get(Calendar.YEAR)+"";
			
			String monthnum = (String)hm.get("monthnum");
			monthnum=monthnum!=null&&monthnum.trim().length()>0?monthnum:calendar.get(Calendar.MONTH)+1+"";
			
			String weeknum = (String)hm.get("weeknum");
			weeknum=weeknum!=null&&weeknum.trim().length()>0?weeknum:"1";
			
			if(logo!=null && logo.trim().length()>0 && "1".equalsIgnoreCase(logo))
			{
				String temp_start_time = (String)reqhm.get("start_date");
				reqhm.remove("start_date");
				if(temp_start_time!=null && temp_start_time.length()>0){
					String[] array = temp_start_time.split("-");
					yearnum = array[0];
					monthnum = Integer.parseInt(array[1])+"";//为了把03变为3
				}else{
					yearnum = calendar.get(Calendar.YEAR)+"";
					monthnum = calendar.get(Calendar.MONTH)+1+"";
				}
				weeknum = "1";
			}		
			/*当选择前的月份的最后一周超过选择月的周数，weeknum变为1，防止为空记录(由第五周切换到下个月时，为空)*/
			ArrayList list = weekList(Integer.parseInt(yearnum),Integer.parseInt(monthnum));
			if (list != null && Integer.parseInt(weeknum) > list.size()) {
				weeknum = "1";
			}
			
			String timeflag = (String)hm.get("timeflag");//是否按时间范围 1是
			timeflag=timeflag!=null&&timeflag.trim().length()>0?timeflag:"0";
			if(frommenu!=null && "1".equals(frommenu)){//如果是从菜单进入的
				timeflag = "0";
			}
			
			String colum = (String)hm.get("colum");
			colum=colum!=null&&colum.trim().length()>0?colum:"";
			String[] cs = colum.split("`");
			if(cs.length>0)
				colum = cs[0];
			String name1 = (String)reqhm.get("name1");
			name1=name1!=null&&name1.trim().length()>0?name1:"";
			String namevalue = (String)reqhm.get("namevalue");
			namevalue=namevalue!=null&&namevalue.trim().length()>0?namevalue:"";
	/**6696：员工日志，按时间范围及姓名来查询，如姓名为于向阳，查询不出结果，不对。
	 * 不需要转码
			try { 			
				name1=PubFunc.ToGbCode(name1);
			} catch (IOException e) {
				e.printStackTrace();
			}			
			try {
				namevalue=PubFunc.ToGbCode(namevalue);
			} catch (IOException e) {
				e.printStackTrace();
			}
	*/
			WeekUtils weekutils = new WeekUtils();
			if("0".equals(ymd)){
				if("0".equals(timeflag)){//本月
					monthnum = (calendar.get(Calendar.MONTH)+1)+"";
					startime=yearnum+"-"+monthnum+"-01";
					endtime=weekutils.lastMonthStr(Integer.parseInt(yearnum),Integer.parseInt(monthnum));
				}else if("2".equals(timeflag)){//本周
					startime=weekutils.numWeekStr(1);
					endtime=weekutils.numWeekStr(7);
				}else if("3".equals(timeflag)){//本日
					Date date = new Date();
					startime=weekutils.dateTostr(date);
					endtime=weekutils.dateTostr(date);
				}else{//时间段 1
					startime=(String)hm.get("startime");
					startime=startime!=null&&startime.trim().length()>0?startime:"";
					endtime=(String)hm.get("endtime");
					endtime=endtime!=null&&endtime.trim().length()>0?endtime:"";
				}
				
				if(a0100!=null&&a0100.length()>3){
					if(startime==null||startime.length()<10)
						startime=start_date;
					if(endtime==null||endtime.length()<10)
						endtime=end_date;
				}
				if(logo!=null && logo.trim().length()>0 && "1".equalsIgnoreCase(logo))
				{
					startime = start_date;
					endtime = end_date;
				}
				
				if(this.userView.haveRoleId(pt_kpi_role_id)){//部门KPI管理员角色 普天个性需求
					sql=wss.getWorkdiaryshow1(uv,fw,filterlist,startime,endtime,"0",appstate,colum,name1,namevalue);
				}else
					sql=wss.getWorkdiaryshow(uv,fw,filterlist,startime,endtime,"0",appstate,colum,name1,namevalue,a0100,start_date,end_date);
			}else if("1".equals(ymd)){
				startime=weekutils.numWeekStr(Integer.parseInt(yearnum),Integer.parseInt(monthnum),
						Integer.parseInt(weeknum),1);
				endtime=weekutils.numWeekStr(Integer.parseInt(yearnum),Integer.parseInt(monthnum),
						Integer.parseInt(weeknum),7);
				if(this.userView.haveRoleId(pt_kpi_role_id)){
					sql=wss.getWorkdiaryshow1(uv,fw,filterlist,startime,endtime,"1",appstate,colum,name1,namevalue);
				}else
					sql=wss.getWorkdiaryshow(uv,fw,filterlist,startime,endtime,"1",appstate,colum,name1,namevalue,a0100,start_date,end_date);
			}else if("2".equals(ymd)){
				startime=yearnum+"-"+monthnum+"-01";
				endtime=weekutils.lastMonthStr(Integer.parseInt(yearnum),Integer.parseInt(monthnum));
				if(this.userView.haveRoleId(pt_kpi_role_id)){//如果有角色
					sql=wss.getWorkdiaryshow1(uv,fw,filterlist,startime,endtime,"2",appstate,colum,name1,namevalue);
				}else
					sql=wss.getWorkdiaryshow(uv,fw,filterlist,startime,endtime,"2",appstate,colum,name1,namevalue,a0100,start_date,end_date);
			}
			
			if(a0100==null||a0100.length()<3){
				hm.put("start_date",startime);
				hm.put("end_date",endtime);
			}
			
			ArrayList datalist = new ArrayList();//extenditerate标签中用到的list。由于备注型指标截取有问题，不再用paginationdb标签了。  郭峰
			StringBuffer sb = new StringBuffer("");
			sb.append(sql[0]);
			sb.append(" "+sql[1]);
			sb.append(" "+sql[3]);
			try{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
				this.frowset = dao.search(sb.toString());
				while(this.frowset.next()){
					LazyDynaBean bean = new LazyDynaBean();
					if(filterlist!=null && filterlist.size()>0){
						bean.set("state", this.frowset.getString("state")!=null?this.frowset.getString("state"):"0");//filterlist总没有state这个字段。而页面上要用到
						for(int i=0;i<filterlist.size();i++){
							FieldItem fielditem=(FieldItem)filterlist.get(i);
							if("M".equalsIgnoreCase(fielditem.getItemtype())){
								bean.set(fielditem.getItemid(), ImportantEvBo.delHTMLTag(this.frowset.getString(fielditem.getItemid())));
							}else if("D".equalsIgnoreCase(fielditem.getItemtype())){//针对oracle库
							 
								Date d= this.frowset.getDate(fielditem.getItemid());
								if(d!=null)
								{
									bean.set(fielditem.getItemid(),df.format(d));
								}
								else
									bean.set(fielditem.getItemid(),""); 
							}
							else{ 
								if("p0100".equalsIgnoreCase(fielditem.getItemid())){
									bean.set(fielditem.getItemid(), PubFunc.encrypt(this.frowset.getString(fielditem.getItemid())!=null?this.frowset.getString(fielditem.getItemid()):""));
								}else
									bean.set(fielditem.getItemid(), this.frowset.getString(fielditem.getItemid())!=null?this.frowset.getString(fielditem.getItemid()):"");
							}
						}
					}
					datalist.add(bean);
				}
			}catch(Exception e){
				throw GeneralExceptionHandler.Handle(e);
				
			}
			hm.put("datalist", datalist);
			hm.put("ymd",ymd);
	//		sql=wss.getWorkdiaryshow(fw,filterlist,startime,endtime);
			hm.put("fieldlist",filterlist);
			hm.put("sql",sql[0]);
			hm.put("where",sql[1]);
			hm.put("column",sql[2]);
			hm.put("orderby",sql[3]);	
			this.userView.getHm().put("performance_sql", sql[0]+" "+sql[1]+" "+sql[3]);
			hm.put("startime",startime);
			hm.put("endtime",endtime);
			hm.put("yearlist",yearList(calendar.get(Calendar.YEAR)));
			hm.put("monthlist",monthList(calendar.get(Calendar.MONTH)));
			hm.put("ymdlist",ymdlist);
			hm.put("statelist",stateList());
			hm.put("yearnum",yearnum);
			hm.put("monthnum",monthnum);
			hm.put("weeknum",weeknum);
			hm.put("name1","");
			hm.put("namevalue", "");
			hm.put("a_code",fw);
			hm.put("appstate",appstate);
			hm.put("weeklist",weekList(Integer.parseInt(yearnum),Integer.parseInt(monthnum)));
			hm.put("timeflag",timeflag);
			hm.put("columlist", this.columlist(filterlist));
			hm.put("predbnamelist", this.predbnamelist());
			
/*		}
		catch(Exception e)
		{
			 e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}*/
		
	}
	
	/**
	 * 过滤不需要显示的项目
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
				fieldlist1.add(field.clone());
				continue;
			}
			if("p0115".equalsIgnoreCase(field.getItemid()))
				fieldlist1.add(field.clone());
			else
				if(field.isVisible())
					fieldlist1.add(field.clone());
		}
		return fieldlist1;
		/*
		ArrayList item=new ArrayList();
//		item.add("b0110");
//		item.add("p0100");
//		item.add("e0122");
//		item.add("e01a1");
		item.add("nbase");
		item.add("a0100");
//		item.add("a0101");
		for(int i=0;i<fieldlist.size();i++)
		{
			FieldItem field=(FieldItem) fieldlist.get(i);
			for(int j=0;j<item.size();j++)
			{
				String itemid=(String) item.get(j);
				if(field.getItemid().equals(itemid))
				{
					fieldlist.remove(i);
					i--;
				}				
			}
		}
		for(int i=0;i<fieldlist.size();i++){
			FieldItem tempitem=(FieldItem)fieldlist.get(i);
			String tempstr=tempitem.getItemid();
//			if(tempstr.equals("p0113")){
//				fieldlist.add(5,tempitem);
//				fieldlist.remove(i+1);
//			}
//			if(tempstr.equals("p0103")){
//				fieldlist.add(5,tempitem);
//				fieldlist.remove(i+1);
//			}
//			if(tempstr.equals("p0101")){
//				fieldlist.add(5,tempitem);
//				fieldlist.remove(i+1);
//			}
			if(tempstr.equals("p0115")){
				fieldlist.add(0,tempitem);
				fieldlist.remove(i+1);
			}
//			if(tempstr.equals("a0101")){
//				fieldlist.add(4,tempitem);
//				fieldlist.remove(i+1);
//			}
			
			
		}
		return fieldlist;
		*/
		
	}
	public ArrayList yearList(int yearmum){
		ArrayList yearlist=new ArrayList();
		for(int i=yearmum;i>=yearmum-15;i--){
			CommonData obj=new CommonData(i+"",i+"");
			yearlist.add(obj);
		}
		return yearlist;
	}
	public ArrayList monthList(int monthnum){
		ArrayList monthlist=new ArrayList();
		for(int i=1;i<=12;i++){
			CommonData obj=new CommonData(i+"",i+"");
			monthlist.add(obj);
		}
		return monthlist;
		
	}
	/**
	 * showDayWeekMonth：绩效参数中的变量
	 * 通过360绩效进入日志时，logo有值
	 * */
	public ArrayList getYmdList(String a0100,String showDayWeekMonth,String logo)
	{
		ArrayList ymdList = new ArrayList();
		
		boolean showDay = false;
		boolean showWeek = false;
		boolean showMonth = false;
		if(logo!=null && logo.length()>0 && showDayWeekMonth !=null && !"".equals(showDayWeekMonth))
		{
			String[] empRecordType = showDayWeekMonth.split(",");
			for(int i=0;i<empRecordType.length;i++)
			{
				if("1".equals(empRecordType[i]))
					showDay = true;
				if("2".equals(empRecordType[i]))
					showWeek = true;
				if("3".equals(empRecordType[i]))
					showMonth = true;
			}
		}else if(logo==null || logo.length()<=0){
			showDay = true;
			showWeek = true;
			showMonth = true;
		}
		
		String week[] = {"日报","周报","月报"};
		for(int i=0;i<week.length;i++)
		{
			if(i==0 && ((!this.userView.hasTheFunction("0306a")&&!this.userView.hasTheFunction("01050") ) || !showDay))
				continue;
			else if(i==1 && ((!this.userView.hasTheFunction("0306b")&&!this.userView.hasTheFunction("01051") ) || !showWeek))
				continue;
			else if(i==2 && ((!this.userView.hasTheFunction("0306c")&&!this.userView.hasTheFunction("01052") ) || !showMonth))
				continue;
			CommonData obj=new CommonData(i+"",week[i]);
			ymdList.add(obj);
		}
		return ymdList;
	}
	public ArrayList stateList(){
		ArrayList stateList=new ArrayList();
		CommonData obj=new CommonData("all","全部");
		stateList.add(obj);
		obj=new CommonData("02","已报批");
		stateList.add(obj);
		obj=new CommonData("03","已批");
		stateList.add(obj);
		obj=new CommonData("07","驳回");
		stateList.add(obj);
		return stateList;
		
	}
	//生成按查询方式的指标
	private ArrayList columlist(ArrayList filterlist){
		ArrayList columlist = new ArrayList();
		CommonData obj=new CommonData("none","");
		columlist.add(obj);
		for(int i=0;i<filterlist.size();i++){
			FieldItem field=(FieldItem) filterlist.get(i);
			if("p0115".equalsIgnoreCase(field.getItemid())|| "p0100".equalsIgnoreCase(field.getItemid())){
				continue;
			}
			obj=new CommonData(field.getItemid()+"`"+field.getItemtype()+"`"+field.getCodesetid(),field.getItemdesc());
			columlist.add(obj);
		}
		return columlist;
	}
	
	//生成人员库列表
	private ArrayList predbnamelist(){
		ArrayList columlist = new ArrayList();
		String sql = "select dbname,pre from dbname";
		CommonData obj=new CommonData("","");
		columlist.add(obj);
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frecset = dao.search(sql);
			while(this.frecset.next()){
				obj = new CommonData(this.frecset.getString("pre"),this.frecset.getString("dbname"));
				columlist.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columlist;
	}
	public ArrayList weekList(int year,int month){
		ArrayList weekList=new ArrayList();
		WeekUtils WeekUtils= new WeekUtils();
		int num = WeekUtils.totalWeek(year,month);
		String week[] = {"第一周","第二周","第三周","第四周","第五周","第六周"};
		for(int i=0;i<num;i++){
			CommonData obj=new CommonData((i+1)+"",week[i]);
			weekList.add(obj);
		}
		return weekList;
	}
}
