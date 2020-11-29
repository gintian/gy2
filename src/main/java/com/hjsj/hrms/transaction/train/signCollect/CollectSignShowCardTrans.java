package com.hjsj.hrms.transaction.train.signCollect;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class CollectSignShowCardTrans extends IBusiness {

	public void execute() throws GeneralException {
		String desc="";
		String nbase=(String)this.getFormHM().get("nbase");
		String a0100=(String)this.getFormHM().get("a0100");
		if(nbase!=null&&nbase.length()>0&&a0100!=null&&a0100.length()>0){
			ConstantXml constantbo = new ConstantXml(this.getFrameconn(), "TR_PARAM");
			String card_no = constantbo.getTextValue("/param/attendance/card_no");// 获得设置的考号字段名称
			String sql="select "+card_no+" from "+nbase+"A01 where a0100='"+a0100+"'";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			try {
				this.frecset=dao.search(sql);
				if(this.frecset.next()){
					desc=this.frecset.getString(card_no);
					desc=desc==null||desc.length()<1?"":desc;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				this.getFormHM().put("desc", desc);
			}
		}else
			this.getFormHM().put("desc", "");
	}

}
