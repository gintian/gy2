package com.hjsj.hrms.transaction.gz.voucher;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 * 类名称:SaveItemSqlWhereTrans
 * 类描述:保存限制条件和计算公式交易类
 * 创建人: xucs
 * 创建时间:2013-8-23 上午11:06:47 
 * 修改时间:xucs
 * 修改时间:2013-8-23 上午11:06:47
 * 修改备注:
 * @version
 *
 */
public class SaveItemSqlWhereTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String clsflag =(String) this.getFormHM().get("clsflag");//clsflag 1:保存计算公式 2：保存限制条件
			String pn_id=(String) this.getFormHM().get("pn_id");//pn_id 财务凭证号
			String fl_id=(String) this.getFormHM().get("fl_id");// fl_id 凭证分录号
			String c_itemsql="";// c_itemsql 计算公式的值
			String c_where="";// 限制条件表达式的值
			String sql="";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList sqlList = new ArrayList();
			if("1".equals(clsflag)){
				c_itemsql=(String) this.getFormHM().get("c_itemsql");
				c_itemsql=SafeCode.decode(c_itemsql);
				c_itemsql=PubFunc.keyWord_reback(c_itemsql);
				sql="update gz_warrantlist set c_itemsql=? where pn_id=? and fl_id=?";
				sqlList.add(c_itemsql);
				sqlList.add(pn_id);
				sqlList.add(fl_id);
				//dao.update(sql);
				dao.update(sql, sqlList);
			}else if("2".equals(clsflag)){
				c_where=(String) this.getFormHM().get("c_itemsql");
				c_where=SafeCode.decode(c_where);
				c_where=PubFunc.keyWord_reback(c_where);
                sql="update gz_warrantlist set c_where=? where pn_id=? and fl_id=?";
                sqlList.add(c_where);
                sqlList.add(pn_id);
                sqlList.add(fl_id);
                dao.update(sql, sqlList);
			}else if("3".equals(clsflag)){
				c_itemsql=(String) this.getFormHM().get("c_itemsql");
				c_itemsql=SafeCode.decode(c_itemsql);
				c_itemsql=PubFunc.keyWord_reback(c_itemsql);
				sql="update gz_warrantlist set c_extitemsql=? where pn_id=? and fl_id=?";
				sqlList.add(c_itemsql);
				sqlList.add(pn_id);
				sqlList.add(fl_id);
				//dao.update(sql);
				dao.update(sql, sqlList);
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
	}
}
