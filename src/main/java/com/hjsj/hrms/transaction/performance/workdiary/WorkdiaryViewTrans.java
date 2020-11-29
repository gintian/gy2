package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySelStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class WorkdiaryViewTrans extends IBusiness {

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
			//if(field.getItemid().equalsIgnoreCase("A0101")||field.getItemid().equalsIgnoreCase("E0122")||field.getItemid().equalsIgnoreCase("E01A1"))
			//	continue;
			if(field.isVisible())
				fieldlist1.add(field);
		}
		return fieldlist1;	
	}
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		RecordVo p01Vo=new RecordVo("p01");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		WorkdiarySelStr wss=new WorkdiarySelStr();
		String p0100=(String) reqhm.get("p0100");
		p0100=p0100!=null&&p0100.trim().length()>0?p0100:"";
		p0100 = PubFunc.decrypt(p0100);
//		p0100 = PubFunc.decryption(p0100);
		reqhm.remove("p0100");
		p01Vo.setString("p0100",p0100);
		
		String curr_user="";
		UserView uv=this.getUserView();
		if(uv.getStatus()==0){
			uv=new UserView(uv.getS_userName(), uv.getS_pwd(), this.getFrameconn());
			try {uv.canLogin();} catch (Exception e) {e.printStackTrace();}
		}
		
		ArrayList fieldlist=DataDictionary.getFieldList("P01",Constant.USED_FIELD_SET);
		fieldlist=filteritem(fieldlist);
		try{
			p01Vo=dao.findByPrimaryKey(p01Vo);
			Date teststartime=p01Vo.getDate("p0104");
			Date testendtime=p01Vo.getDate("p0106");
			String startime = teststartime.toString().substring(0,10);
			String endtime = testendtime.toString().substring(0,10);
			curr_user=p01Vo.getString("curr_user");
//			if(p01Vo.getString("p0103")!=null){
//				p01Vo.setString("p0103",wss.getenter(p01Vo.getString("p0103")));
//			}
//			if(p01Vo.getString("p0109")!=null){
//				p01Vo.setString("p0109",wss.getenter(p01Vo.getString("p0109")));
//			}
//			if(p01Vo.getString("p0111")!=null){
//				p01Vo.setString("p0111",wss.getenter(p01Vo.getString("p0111")));
//			}
			if(p01Vo.getString("p0113")!=null){
				p01Vo.setString("p0113",wss.getenter(p01Vo.getString("p0113")));
			}
			this.getFormHM().put("p01Vo",p01Vo);
			this.getFormHM().put("disabled","0");
			this.getFormHM().put("fieldlist",fieldlist);
			if(reqhm.containsKey("query")){
				//2016/1/19 wangjl 全总 领导批示完成后，还希望增加一些批示内容
//				this.getFormHM().put("appflag","1");
				this.getFormHM().put("appflag","0");
				reqhm.remove("query");
				this.getFormHM().put("dis","true");
			}else{
				this.getFormHM().put("appflag","0");
				this.getFormHM().put("dis","true");
			}

			
			ArrayList list = wss.getSuperiorUser(uv.getA0100(), dao);
			if(list.size()>0){
				String flag="1";
				if(!uv.getUserName().equals(curr_user))
					flag="0";
				this.getFormHM().put("curr_user",flag);
			}
			//返回是否为抄送人员标记
			String flag1=wss.reChaoSongFlag(p0100, uv.getUserName(), uv.getA0100(), p01Vo.getString("nbase"), this.getFrameconn());
			String flag=wss.reChaoSongFlag2(p0100, uv.getUserName(), uv.getA0100(), p01Vo.getString("nbase"), this.getFrameconn());
			this.getFormHM().put("csflag", flag1);
			//取消待办中的任务
			if("1".equals(flag)){
				if("5".equals(reqhm.get("home")))
					dao.update("update per_diary_actor set display=1 where p0100="
							+p0100+"  and nbase='"+p01Vo.getString("nbase")+"' and a0100='"+uv.getA0100()+"'");
			}
			if(reqhm.get("pdCode")!=null && !"".equals(reqhm.get("pdCode")))
				this.getFormHM().put("pendingCode", reqhm.get("pdCode"));
			if (this.existFile(p0100)) {
				this.getFormHM().put("existFile","1");
			} else {
				this.getFormHM().put("existFile","0");
			}
			//程序中的starttime是本条日志的starttime，而form中的starttime是用户任意输入的starttime，所以也不需要put到form中了。郭峰注释   没搞懂为啥注释了，我给放开了，zhaoxg 2014-3-3  bug0042259
			this.getFormHM().put("startime",startime);
			this.getFormHM().put("endtime",endtime);
			String tablestr = perPlanTable(dao,startime,endtime,p01Vo.getString("a0100"));
			if(this.getFormHM().get("a0100")!=null&&this.getFormHM().get("a0100").toString().length()>0)
				tablestr="no";
			this.getFormHM().put("perPlanTable",tablestr);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 是否存在附件
	 * @param p0100
	 * @return
	 */
	private boolean existFile (String p0100) {
		boolean flag = false;
		// sql语句
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_diary_file where p0100 = '");
		sql.append(p0100);
		sql.append("'");
		
		// 查询
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(sql.toString());
			if (frowset.next()) {
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private String perPlanTable(ContentDAO dao,String starttime,String endtime,String a0100){
		StringBuffer tablestr=new StringBuffer();
		tablestr.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\"");
		tablestr.append(" cellpadding=\"0\">");
		WeekUtils wb = new WeekUtils();
		//Date date1 = wb.strTodate(endtime);
		Date date1=thedate(starttime,endtime);
		
		GregorianCalendar cal1 = new GregorianCalendar();
		cal1.setTime(date1);
		int month = cal1.get(GregorianCalendar.MONTH)+1;
		int quarter = wb.getQuarter(date1);
		
		String myear = "01";
		if(month>6){
			myear="02";
		}
		String monthstr = "";
		if(month>9)
			monthstr = month+"";
		else
			monthstr = "0"+month;
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select plan_id,name from ");
		strSearch.append("per_plan ");
		strSearch.append(" where ((theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and cycle='0') or (");
		strSearch.append(" theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and themonth='");
		strSearch.append(monthstr);	
		strSearch.append("' and cycle='3') or (");
		strSearch.append(" theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and thequarter='0");
		strSearch.append(quarter);	
		strSearch.append("' and cycle='2') or (");
		strSearch.append(" theyear='");
		strSearch.append(cal1.get(GregorianCalendar.YEAR));
		strSearch.append("' and thequarter='");
		strSearch.append(myear);	
		strSearch.append("' and cycle='1')");
		strSearch.append(" or (");
		strSearch.append(wss.getDataValue("start_date",">=",starttime));	
		strSearch.append(" and ");	
		strSearch.append(wss.getDataValue("start_date","<=",endtime));
		strSearch.append(" and cycle='7')");
		//张婕 结束时间所在月报看不到该目标卡，不对 应该都能看到
		strSearch.append(" or (");
		strSearch.append(wss.getDataValue("end_date",">=",starttime));	
		strSearch.append(" and ");	
		strSearch.append(wss.getDataValue("end_date","<=",endtime));
		strSearch.append(" and cycle='7')");
		
		strSearch.append(")  and plan_id in (select distinct plan_id from per_object where object_id='");
		strSearch.append(a0100);
		strSearch.append("' and sp_flag in ('03','06')) and method='2' and status <> 0");
		try {
			this.frowset = dao.search(strSearch.toString());
			int i=0;
			while(this.frowset.next()){
				String plan_id = this.frowset.getString("plan_id");
				String name = this.frowset.getString("name");
				tablestr.append("<tr>");
				tablestr.append("<td>&nbsp;");
				tablestr.append("<span style=\"cursor:hand;color:#0000FF\" onclick=\"perPlan('");
				tablestr.append(PubFunc.encryption(plan_id));
				tablestr.append("','"+PubFunc.encryption(a0100)+"');\">");
				tablestr.append(name);
				tablestr.append("</span></td>");
				tablestr.append("</tr>");
				i++;
			}
			if(i>0){
				tablestr.append("<tr><td>&nbsp;</td></tr>");
				tablestr.append("</table>");
			}else{
				tablestr.setLength(0);
				tablestr.append("no");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return tablestr.toString();
	}
	
	/**
	 * 判断日期段属于那个月的日志
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	private Date thedate(String starttime,String endtime){
		WeekUtils wb = new WeekUtils();
		Date date1 = wb.strTodate(starttime);
		Date date2 = wb.strTodate(endtime);
		if(DateUtils.getDay(date2)>1)
			return date2;
		else
			return date1;
	}
}
