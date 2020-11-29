package com.hjsj.hrms.transaction.kq.month_kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteMonthKqTrans extends IBusiness{

	public void execute() throws GeneralException {
		String a0100 = this.getFormHM().get("keyid").toString();
		String year = this.getFormHM().get("year").toString();
		String month = this.getFormHM().get("month").toString();
		
		a0100 = a0100.substring(0,a0100.length()-1);
		String isok = "删除失败!";
		
		String [] peopleId = a0100.split(",");
		
		MonthKqBo kqBo = new MonthKqBo(this.frameconn);
		if(kqBo.IsFc(year, month)){			
			for(int i = 0 ; i < peopleId.length ; i ++){
				if(this.isDel(peopleId[i],year,month)){
					if(this.deleteMonthKqInfoById(peopleId[i],year,month)){
						isok = "删除成功!";
					}
				}else{
					isok = "只能对起草、驳回的数据进行删除操作!";
				}
			}
		}else{
			isok = "该月数据已经封存，无法进行删除操作!";
		}
		this.getFormHM().put("isok", isok);
		
	}
	//删除记录
	public boolean deleteMonthKqInfoById(String a0100 ,String year , String month){
		boolean flag = false;
		ArrayList list = new ArrayList();
		//String sql = "delete  from q35 where a0100 = ? and year(q35z0) = ? and month(q35z0) = ?";
		StringBuffer sb = new StringBuffer();
		sb.append(" delete from q35 where ");
		sb.append("a0100 = ? and ");
		sb.append(Sql_switcher.year("q35z0") + "= ? and ");
		sb.append(Sql_switcher.month("q35z0") + "=?");
		list.add(a0100);
		list.add(year);
		list.add(month);
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(1 == dao.delete(sb.toString(), list)){
				flag = true;
			}else{
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	//删除之前判断当前人的状态
	public boolean isDel(String a0100,String year,String month){
		String sql = "select * from q35 where a0100 = '"+a0100+"' and "+Sql_switcher.year("q35z0")+" = '"+year+"' and "+Sql_switcher.month("q35z0")+" = '"+month+"'";
		String status = "";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				status = this.frowset.getString("status");
			}
			if("01".equalsIgnoreCase(status) || "07".equalsIgnoreCase(status)){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
