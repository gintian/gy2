package com.hjsj.hrms.transaction.kq.month_kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FcJfMonthKqTrans extends IBusiness{
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	String isok = "操作失败!";
	ArrayList list = new ArrayList();
	public void execute() throws GeneralException {
		String type = this.getFormHM().get("type").toString(); //1. 封存 2.解封
		String year = this.getFormHM().get("year").toString();
		String month = this.getFormHM().get("month").toString();
		if("1".equals(type)){ 
			//封存之前还差一个判断 暂时未写 如果当月的数据有未批准的 则不进行封存操作
			if(this.isPiZhun(year, month)){
				isok = year + "年" + month +"月有尚未批准的人员数据，不能封存!";
			}else{
				if(this.FcMonthKqInfo(year, month)){
					isok = year + "年" + month + "月记录已经成功封存!";
				}else{
					isok = year + "年" + month + "月记录封存失败!";
				}
			}
		}else if("2".equals(type)){
			if(this.IsFc(year, month,"2")){
				isok = year + "年" + month + "月记录没有封存 ，不能解封!";
			}else{
				if(this.JfMonthKqInfo(year, month)){
					isok = year + "年" + month + "月记录已经成功解封!";
				}else{
					isok = year + "年" + month + "月记录解封失败!";
				}
			}
		}
		this.getFormHM().put("isok", isok);
	}
	
	//封存当年当月考勤记录
	public boolean FcMonthKqInfo(String year , String month){	
		StringBuffer sb = new StringBuffer();
		/*sb.append("insert into kq_duration");
		sb.append("values('");
		sb.append(year+"','");
		sb.append(month + "','");
		sb.append(sdf.format(new Date()) + "','");
		sb.append(sdf.format(new Date()) + "','");
		sb.append("1')");*/
		if(this.IsFc(year, month,"1")){			
			sb.append("insert into kq_duration ");
			sb.append("values(?,?,?,?,?,?,?)");//sysdate 
			list.add(year);
			list.add(month);
			list.add(new java.sql.Date(new Date().getTime()));//sysdate
			list.add(new java.sql.Date(new Date().getTime()));
			list.add("");
			list.add("");
			list.add("1");
			try {
				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.insert(sb.toString(), list);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}else{
			sb.append("update kq_duration set ");
			sb.append("finished = '1' where ");
			sb.append(" kq_year = '");
			sb.append(year + "' and ");
			sb.append(" kq_duration = '");
			sb.append(month + "'");
			try {
				ContentDAO dao = new ContentDAO(this.frameconn);
				if(0 != dao.update(sb.toString())){					
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	//解封当月考勤记录 可优化 如果找到当月记录则执行解封 如果没找到当月记录则提示(已优化)
	public boolean JfMonthKqInfo(String year , String month){
		StringBuffer sb = new StringBuffer();
		sb.append("update kq_duration set ");
		sb.append("finished = '0' where ");
		sb.append(" kq_year = '");
		sb.append(year + "' and ");
		sb.append(" kq_duration = '");
		sb.append(month + "'");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(0 != dao.update(sb.toString())){				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	//封存之前判断 如果当月的数据有未批准的 则不进行封存操作
	public boolean isPiZhun(String year , String month){
		StringBuffer sb = new StringBuffer();
		sb.append("select status ");
		sb.append("from q35 where ");
		//sb.append("year(q35z0) = '");
		sb.append(Sql_switcher.year("q35z0") + "='");
		sb.append(year + "' and ");
		//sb.append("month(q35z0) = '");
		sb.append(Sql_switcher.month("q35z0") + "='");
		sb.append(month + "'");
		sb.append(" and status != '03'");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//封存前进行判断 此年月数据在考勤区间表中有没有 如果有 则修改状态 没有则新增记录
	public boolean IsFc(String year , String month,String flag){
		//flag 1 封存 2 解封
		String sql = "";
		if("1".equals(flag)){			
			sql = "select * from kq_duration where kq_year = '"+year+"' and kq_duration = '"+month+"'";
		}else if("2".equals(flag)){
			sql = "select * from kq_duration where kq_year = '"+year+"' and kq_duration = '"+month+"' and finished = '1'";
		}
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
