package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.businessobject.propose.ProShowExcel;
import com.hjsj.hrms.businessobject.propose.ProposeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OutExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList column = new ArrayList();// 中文字段名
		ArrayList infolist = new ArrayList();// 各列数值
		ArrayList columnlist = new ArrayList();// 列名
		String sql="";
		String start_date=this.getFormHM().get("start_date").toString();
		String end_date=this.getFormHM().get("end_date").toString();
		sql=new ProposeBo().getSearchSQL(this.userView, start_date, end_date);
		column.add("提交人");
		column.add("提交日期");
		column.add("意见内容");  
		column.add("答复人");
		column.add("答复日期");
		column.add("答复内容");
		ContentDAO dao = new ContentDAO(this.frameconn);
		 
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				if("1".equals(this.frowset.getString("annymous")))
					 bean.set("createuser", "匿名");
				else
				     bean.set("createuser", PubFunc.nullToStr(this.frowset
						.getString("createuser"))); 
				bean.set("createtime", formatdate(this.frowset.getDate("createtime")));
				bean.set("scontent", PubFunc.getStr(PubFunc
						.nullToStr(this.frowset.getString("scontent"))));
				bean.set("replyuser", PubFunc.getStr(PubFunc
						.nullToStr(this.frowset.getString("replyuser"))));
				bean.set("replytime", formatdate(this.frowset.getDate("replytime")));
				bean.set("rcontent", PubFunc.nullToStr(this.frowset
						.getString("rcontent")));
				infolist.add(bean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		columnlist.add("createuser");
		columnlist.add("createtime");
		columnlist.add("scontent");
		columnlist.add("replyuser");
		columnlist.add("replytime");
		columnlist.add("rcontent");
		ProShowExcel show = new ProShowExcel();
		String filename = show.creatExcel(this.userView, column, infolist, columnlist);
		//xus 20/4/20 vfs改造
//		this.getFormHM().put("filename", SafeCode.encode(PubFunc.encrypt(filename)));
		this.getFormHM().put("filename", PubFunc.encrypt(filename));
	}
	
	public String formatdate(Date date){
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(date == null)
			return  "";
		return df.format(date);
	}
}
