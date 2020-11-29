package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.GetValiateEndDate;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * 领导代销假
 * @author wangyao
 * @version 1.0
 *
 */
public class FuglePostureTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");     
		String table = ((String)hm.get("table")).toLowerCase();
		String id=(String)hm.get("id");
		StringBuffer sql=new StringBuffer();
		sql.append("select * from " + table);
		sql.append(" where " + table + "19='"+id+"'");
		sql.append(" and " + table + "17=1");
		RecordVo vo=new RecordVo(table); 
		Calendar now = Calendar.getInstance();
        Date dd=now.getTime();//系统时间
        GetValiateEndDate va = new GetValiateEndDate(this.userView,
				this.frameconn);
        try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			Map map = KqUtilsClass.getCurrKqInfo();
			Date kq_start = OperateDate.strToDate((String)map.get("kq_start"), "yyyy.MM.dd");
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				String q1501=this.frowset.getString(table + "01");
				vo.setString(table + "01",q1501);
				vo=dao.findByPrimaryKey(vo);
				Date z1=vo.getDate(table + "z1");
				Date z3=vo.getDate(table + "z3");
				if(kq_start.after(z1)){
					z1= kq_start;
					Map timeMap = va.getTimeByDate(vo.getString("nbase"), vo.getString("a0100"), z1);
					if (!(timeMap == null) && !"".equals(timeMap)
							&& !timeMap.isEmpty()) {
						z1 = OperateDate.strToDate(OperateDate
								.dateToStr(z1, "yyyy-MM-dd")
								+ " " + (String) timeMap.get("startTime"),
								"yyyy-MM-dd HH:mm");
					}
				}
				vo.setString(table + "z1",DateUtils.format(z1,"yyyy-MM-dd HH:mm"));
				vo.setString(table + "z3",DateUtils.format(z3,"yyyy-MM-dd HH:mm"));
				vo.setString(table + "05",DateUtils.format(dd,"yyyy-MM-dd HH:mm"));
				vo.setString(table + "17","1");
				/*vo.setString("q1501",this.frowset.getString("q1501"));
				vo.setString("nbase",this.frowset.getString("nbase"));
	            vo.setString("a0100",this.frowset.getString("a0100"));
	            vo.setString("b0110",this.frowset.getString("b0110"));
	            vo.setString("e0122",this.frowset.getString("e0122"));
	            vo.setString("a0101",this.frowset.getString("a0101"));
	            vo.setString("e01a1",this.frowset.getString("e01a1"));
	            vo.setString("q1503",this.frowset.getString("q1503"));
	            vo.setString("q15z1",this.frowset.getString("q15z1"));
	            vo.setString("q15z3",this.frowset.getString("q15z3"));
	            vo.setString("q1505",DateUtils.format(dd,"yyyy-MM-dd HH:mm:ss"));
	            vo.setInt("q1517",1);
	            vo.setString("q1519",id);*/
				
	        }else
			{
	        	vo.setString(table + "01",id);
				vo=dao.findByPrimaryKey(vo);
				Date z1=vo.getDate(table + "z1");
				Date z3=vo.getDate(table + "z3");
				if(kq_start.after(z1)){
					z1= kq_start;
					Map timeMap = va.getTimeByDate(vo.getString("nbase"), vo.getString("a0100"), z1);
					if (!(timeMap == null) && !"".equals(timeMap)
							&& !timeMap.isEmpty()) {
						z1 = OperateDate.strToDate(OperateDate
								.dateToStr(z1, "yyyy-MM-dd")
								+ " " + (String) timeMap.get("startTime"),
								"yyyy-MM-dd HH:mm");
					}
				}
				vo.setString(table + "z1",DateUtils.format(z1,"yyyy-MM-dd HH:mm"));
				vo.setString(table + "05",DateUtils.format(dd,"yyyy-MM-dd HH:mm"));
				vo.setString(table + "z3",DateUtils.format(z3,"yyyy-MM-dd HH:mm"));
				vo.setString(table + "z0","03");
				vo.setString(table + "z5","01");
				vo.setString(table + "19",id);
				vo.setString(table + "17","1");
//				String d =vo.getString("q1507");
				vo.setString(table + "07", "");  //销假是有不能出现请假事由内容；所以初始值为空
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("scope_start_time",vo.getString(table + "z1"));
		this.getFormHM().put("cancelvo",vo);
		SearchAllApp searchAllApp=new SearchAllApp();
		this.getFormHM().put("selist",searchAllApp.getTableList(table,this.getFrameconn()));
	}

}
