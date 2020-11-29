package com.hjsj.hrms.module.gz.salaryaccounting.sendmsg.businessobject;

import com.hjsj.hrms.businessobject.sys.AsyncEmailIsSuccessIF;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
/**
 * 薪资发放，发送邮件更新发送状态。
 * @author zhanghua 2016-08-08
 *
 */
public class SendMsgIsSuccess implements AsyncEmailIsSuccessIF {
	
	/**
	 * 更新发送状态发放
	 * @param emailContent  AsyncEmailBo.java send方法中传入的bean
	 * @param isSuccess 若成功返回值为""，若失败，返回值为错误信息。
	 */
	@Override
    public void sendEmailIsSuccess(LazyDynaBean emailContent,
                                   String isSuccess) {
		// TODO Auto-generated method stub
		int flag= "".equals(isSuccess.trim())?1:2;
		Connection conn = null;
	    try
	    {
	    	conn=AdminDb.getConnection();
	    	ContentDAO dao=new ContentDAO(conn);
			updateTableCloumns(conn);
			StringBuffer buf = new StringBuffer();
			buf.append("update email_content set send_ok=");
			buf.append(flag);
			buf.append(" where id=");
			buf.append(emailContent.get("templateId").toString());
			buf.append(" and a0100='");
			buf.append(emailContent.get("a0100").toString() + "' and lower(pre)='");
			buf.append(emailContent.get("pre").toString().toLowerCase()+"' ");
			String send_time=(String)emailContent.get("send_time");
			String module_type=(String)emailContent.get("module_type");
			String module_id=(String)emailContent.get("module_id");
			String username=(String)emailContent.get("username");
			String a00z3=(String)emailContent.get("a00z3");
			buf.append(" and module_type="+module_type);
			buf.append(" and module_id="+module_id);
			buf.append(" and "+Sql_switcher.dateToChar("send_time", "YYYY-MM-DD")+"='"+send_time+"'");		
			buf.append(" and a00z3="+a00z3+" and username='"+username+"'");
			/*
			buf.append("' and I9999=");
			buf.append(emailContent.get("I9999").toString());*/
			
			
			dao.update(buf.toString());
	    }catch(Exception s){
	    	s.printStackTrace();
	    }finally{
	    	PubFunc.closeDbObj(conn);
	    }
	}
	/**
	 * 动态创建邮件发送标志列,并设置初始值
	 * @throws GeneralException 
	 * 
	 */
	private void updateTableCloumns(Connection conn) throws GeneralException {
		try {
			String tablename = "email_content";
			RecordVo vo = new RecordVo(tablename);
			Table table = new Table(tablename);
			int num = 0;
			if (!vo.hasAttribute("send_ok"))// 发送标识
			{
				Field obj = new Field("send_ok", "send_ok");
				obj.setDatatype(DataType.INT);
				obj.setKeyable(false);
				obj.setVisible(false);
				obj.setAlign("left");
				table.addField(obj);
				num++;
			}
			if (num > 0) {
				DbWizard dbWizard = new DbWizard(conn);
				dbWizard.addColumns(table);
				DBMetaModel dbmodel = new DBMetaModel(conn);
				dbmodel.reloadTableModel(tablename);
				/** 新建send-ok列.将值赋为0 */
				StringBuffer sql = new StringBuffer();
				sql.append("update email_content set send_ok=0");
				ContentDAO dao = new ContentDAO(conn);
				dao.update(sql.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
