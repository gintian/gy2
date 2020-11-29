package com.hjsj.hrms.transaction.kq.month_kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 保存月度考勤信息
 * <p>Title:SaveMonthKqInfoTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author jinjiawei
 * @version 1.0
 * */
public class SaveMonthKqInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		String a0100 = this.getFormHM().get("a0100").toString();
		String field = this.getFormHM().get("field").toString();
		String codeid = this.getFormHM().get("codeid").toString();
		String year = this.getFormHM().get("year").toString();
		String month = this.getFormHM().get("month").toString();
		int isok = 0;
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		if(bo.IsFc(year, month)){			
			if(this.isDel(a0100,year,month)){
				this.updateMonthKqInfo(a0100, field, codeid,year,month);
				String info = this.getImgInfoById(codeid);
				this.getFormHM().put("info", info);   //考勤状态
				this.getFormHM().put("field", field); //哪一号 
			}else{
				isok = 1;
			}
		}else{
			isok = 3;
		}
		this.getFormHM().put("isok", isok+"");
	}
	
	//保存某一个人哪一号的考勤信息
	public void updateMonthKqInfo(String a0100,String field,String codeid,String year,String month){
		StringBuffer sb = new StringBuffer();
		ArrayList list = new ArrayList();
		sb.append("update q35 ");
		sb.append("set "+field);
		sb.append(" =?");
		sb.append(" where ");
		//sb.append("a0100 = ? and year(q35z0) = ? and month(q35z0) = ?");
		sb.append(" a0100 = ? and ");
		sb.append(Sql_switcher.year("q35z0") + "=? and ");
		sb.append(Sql_switcher.month("q35z0") + "= ?");
		list.add(codeid);
		list.add(a0100);
		list.add(year);
		list.add(month);
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//System.out.println(sb.toString() + codeid +":"+a0100 +":"+year+":"+month);
			dao.update(sb.toString(), list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//保存完之后取得考勤对应的图标 动态在前台显示 实现无刷新页面动态变更前台展示效果
	public String getImgInfoById(String codeitemid){
		//String sql = "select codeitemdesc,corcode from codeitem where codesetid='27' and codeitemid = '"+codeitemid+"'";
		String imgInfo = "";
		String sql = "select item_name , item_symbol from kq_item where item_id = '"+codeitemid+"'";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				//if(null != this.frowset.getString("corcode")){
				//	imgInfo = this.frowset.getString("corcode");
				//}else{
				//	imgInfo = this.frowset.getString("codeitemdesc");
				//}
					imgInfo = this.frowset.getString("item_symbol");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imgInfo;
	}
	
	//判断当前这个人的状态 是否能进行编辑
	//删除之前判断当前人的状态
	public boolean isDel(String a0100,String year ,String month){
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
