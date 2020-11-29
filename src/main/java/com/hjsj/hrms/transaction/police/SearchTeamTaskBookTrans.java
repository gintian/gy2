package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 
 *<p>Title:SearchTeamTaskBookTrans.java</p> 
 *<p>Description:查询队伍任务书</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:2010-2-9</p> 
 *@author wangzhongjun
 *@version 1.0
 */
public class SearchTeamTaskBookTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String manaagePriv = this.userView.getManagePrivCode();
		String manamgePrivCode = this.userView.getManagePrivCodeValue();
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String userbase = (String)this.getFormHM().get("userbase");
		if(userbase==null|| "".equalsIgnoreCase(userbase))
			userbase = "usr";
		String taskyear = (String)this.getFormHM().get("taskyear");
		if(taskyear==null|| "".equalsIgnoreCase(taskyear))
			taskyear = "";
		String username = (String)this.getFormHM().get("username");
		this.getFormHM().remove("username");
		if(username==null|| "".equalsIgnoreCase(username))
			username = "";
		String uid = (String)hm.get("a_code");
		String codesetid = uid.substring(0,2);
		String id = uid.substring(2);
		ArrayList list = new ArrayList();
		ArrayList dbnamelist = new ArrayList();
		ArrayList yearlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		if(id==null|| "".equalsIgnoreCase(id)){
			sql.append("select ");
			sql.append(userbase);
			sql.append("a01.a0100,a0101,b0110,e01a1,e0122,b.i9999,b.title,b.createtime,b.ole,b.ext from ");
			sql.append(userbase);
			sql.append("a01 left join ");
			sql.append("(select * from ");
			sql.append(userbase);
			sql.append("a00 where Upper(flag) = 'T'  ");
			
			sql.append(") b ");
			sql.append("on ");
			sql.append(userbase);
			sql.append("a01.a0100 = b.a0100 ");
			
		}else{
			sql.append("select ");
			sql.append(userbase);
			sql.append("a01.a0100,a0101,b0110,e01a1,e0122,b.i9999,b.title,b.createtime,b.ole,b.ext from ");
			sql.append(userbase);
			sql.append("a01 left join ");
			sql.append("(select * from ");
			sql.append(userbase);
			sql.append("a00 where Upper(flag) = 'T' ");//注意大小写
			sql.append(") b ");
			sql.append("on ");
			sql.append(userbase);
			sql.append("a01.a0100 = b.a0100 ");
			
		}
		
		// 获得周期参数,0为按年，1为按月，2为按季度
		String cycle = (String) this.getFormHM().get("cycle");
		if ("0".equals(cycle)) {

			// 默认当前时间的年份
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("yearlist", this.getYearList(id));

			// 该年份的范围
			String end = String.valueOf(Integer.parseInt(taskyear) + 1);
			sql.append(" and b.createtime >= ");
			sql.append(Sql_switcher.dateValue(taskyear));
			sql.append(" and b.createtime<" + Sql_switcher.dateValue(end));

		} else if ("1".equals(cycle)) {
			
			// 获得年份和月份
			taskyear = (String) this.getFormHM().get("taskyear");
			String taskmonth = (String) this.getFormHM().get("taskmonth");
			
			// 初始值为当前时间的年份和月份
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			if (taskmonth == null || "".equalsIgnoreCase(taskmonth)) {
				taskmonth = String.valueOf(new Date().getMonth() + 1);
			}
			
			// 获得时间范围
			String start = taskyear + "-" + taskmonth + "-" + "01";
			WeekUtils util = new WeekUtils();
			String end = util.lastMonthStr(Integer.parseInt(taskyear), Integer
					.parseInt(taskmonth));
			if (Integer.parseInt(taskmonth) == 12) {
				end = String.valueOf(Integer.parseInt(taskyear) + 1) + "-"
						+ "01-01";
			} else {
				end = taskyear + "-"
						+ String.valueOf(Integer.parseInt(taskmonth) + 1)
						+ "-01";
			}
			sql.append(" and b.createtime >=");
			sql.append(Sql_switcher.dateValue(start));
			sql.append(" and b.createtime<" + Sql_switcher.dateValue(end));
			
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("taskmonth", taskmonth);
			this.getFormHM().put("yearlist", this.getYearList(id));
			this.getFormHM().put("monthlist", this.getMonthList());

		} else if ("2".equals(cycle)) {
			// 获得年份和季度
			taskyear = (String) this.getFormHM().get("taskyear");
			String taskweek = (String) this.getFormHM().get("taskweek");
			
			// 默认为当前时间的年份和季度
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			if (taskweek == null || "".equalsIgnoreCase(taskweek)) {
				Calendar ca = Calendar.getInstance();
				ca.setTime(new Date());
				int month = ca.get(Calendar.MONTH) + 1;
				if (month % 3 != 0) {
					taskweek = String.valueOf(month / 3 + 1);
				} else {
					taskweek = String.valueOf(month / 3);
				}
			}
			
			// 时间范围
			String start = taskyear + "-"
					+ (Integer.parseInt(taskweek) * 3 - 2) + "-01";
			String end = "";
			if (Integer.parseInt(taskweek) == 4) {
				end = (Integer.parseInt(taskyear) + 1) + "-01-01";
			} else {
				end = taskyear + "-"
						+ ((Integer.parseInt(taskweek) + 1) * 3 - 2) + "-01";
			}
			sql.append(" and b.createtime >=");
			sql.append(Sql_switcher.dateValue(start));
			sql.append(" and b.createtime<" + Sql_switcher.dateValue(end));
			
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("taskweek", taskweek);
			this.getFormHM().put("yearlist", this.getYearList(id));
			this.getFormHM().put("weeklist",
					this.getWeekList());

		}
				
		if(id==null|| "".equalsIgnoreCase(id)){
			sql.append(" where 1=1 ");
		} else {
			if("@K".equalsIgnoreCase(codesetid)) {
				sql.append(" where e01a1 like '");
				sql.append(id);
				sql.append("%' ");
			}
			if("UN".equalsIgnoreCase(codesetid)) {
				sql.append(" where b0110 like '");
				sql.append(id);
				sql.append("%' ");
			}
			if("UM".equalsIgnoreCase(codesetid)) {
				sql.append(" where e0122 like '");
				sql.append(id);
				sql.append("%' ");
			}
		}
		
		if("@K".equalsIgnoreCase(manaagePriv)) {
			sql.append(" and e01a1 like '");
			sql.append(manamgePrivCode);
			sql.append("%' ");
		}
		if("UN".equalsIgnoreCase(manaagePriv)){
			sql.append(" and b0110 like '");
			sql.append(manamgePrivCode);
			sql.append("%' ");
		}
		if("UM".equalsIgnoreCase(manaagePriv)) {
			sql.append(" and e0122 like '");
			sql.append(manamgePrivCode);
			sql.append("%' ");
		}
		if(!"".equalsIgnoreCase(username)) {
			InfoUtils infoUtils=new InfoUtils();
			String whereA0101=infoUtils.whereA0101(this.userView,this.getFrameconn(), userbase, username,"1");
			sql.append(" and ");
			sql.append(whereA0101);
			/*sql.append(" and ");
			sql.append(userbase);
			sql.append("a01.a0101='");
			sql.append(username);
			sql.append("' ");*/
		}
		
		String ordersql= " order by "+userbase+"a01.a0000,b.createtime";
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String dbsql = "select * from dbname ";
			/**应用库过滤前缀符号*/
	        ArrayList dblist=userView.getPrivDbList();
	        StringBuffer cond=new StringBuffer();
	        cond.append("select pre,dbname from dbname where pre in (");
	        if(dblist.size()>0){
	        	userbase=dblist.get(0).toString();      
	        }
	        else
	        	userbase="usr";
	        for(int i=0;i<dblist.size();i++)
	        {
	        	
	        	if(i!=0)
	                cond.append(",");
	            cond.append("'");
	            cond.append((String)dblist.get(i));
	            cond.append("'");
	        }
	        if(dblist.size()==0)
	            cond.append("''");
	        cond.append(")");
	        cond.append(" order by dbid");
			this.frowset = dao.search(dbsql);
			while(this.frowset.next()){
				CommonData data = new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
				dbnamelist.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	//System.out.println(sql.toString());
    	this.getFormHM().put("column", "a0100,a0101,b0110,e01a1,e0122,i9999,title,createtime,ext");
    	this.getFormHM().put("sqlstr", sql.toString());
    	this.getFormHM().put("order_by", ordersql);
    	
    	this.getFormHM().put("uplevel",uplevel);
		this.getFormHM().put("rolelist",list);
		this.getFormHM().put("a_code",uid);
		this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("userbaselist",dbnamelist);
		this.getFormHM().put("username","");
		
	}

	/**
	 * 获得年列表
	 * 
	 * @param id
	 * @return
	 */
	private List getYearList(String id) {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select createtime from B00 where b0110 like '");
		sql.append(id);
		sql.append("%'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			int minyear = Integer.parseInt(PubFunc.FormatDate(new Date(),
					"yyyy"));
			if (this.frowset.next()) {
				int temp = Integer.parseInt(PubFunc.FormatDate(this.frowset
						.getDate("createtime"), "yyyy"));
				minyear = minyear > temp ? temp : minyear;
				while (this.frowset.next()) {
					temp = Integer.parseInt(PubFunc.FormatDate(this.frowset
							.getDate("createtime"), "yyyy"));
					minyear = minyear > temp ? temp : minyear;
				}
				int nowYear = Integer.parseInt(PubFunc.FormatDate(new Date(),
						"yyyy"));
				for (int i = nowYear; i >= minyear; i--) {
					CommonData data = new CommonData(String.valueOf(i), String
							.valueOf(i));
					list.add(data);
				}

			} else {
				CommonData data = new CommonData(PubFunc.FormatDate(new Date(),
						"yyyy"), PubFunc.FormatDate(new Date(), "yyyy"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得月列表
	 * 
	 * @param id
	 * @return
	 */
	private List getMonthList() {
		List list = new ArrayList();
		for (int i = 1; i < 13; i++) {
			CommonData data = new CommonData(String.valueOf(i), String
					.valueOf(i));
			list.add(data);
		}
		return list;
	}

	/**
	 * 获得季度列表
	 * 
	 * @return
	 */
	private List getWeekList() {
		List list = new ArrayList();
		String[] week = { "一", "二", "三", "四" };
		for (int i = 1; i <= 4; i++) {
			CommonData data = new CommonData(String.valueOf(i), "第"
					+ week[i - 1] + "季度");
			list.add(data);
		}

		return list;
	}
}
