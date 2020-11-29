package com.hjsj.hrms.module.recruitment.util.transaction;

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
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
/**
 * 招聘管理，发送邮件更新发送状态。
 * @author wangjl 2017-12-15
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
		int flag= "".equals(isSuccess.trim())?1:2;
		Connection conn = null;
	    try
	    {
	    	Date date = new Date();
			Timestamp create_time = new Timestamp(date.getTime());
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
        	String pre="";  //应聘人员库
			if(vo!=null)
				pre=vo.getString("str_value");
			else
				throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
	    	conn=AdminDb.getConnection();
	    	ContentDAO dao=new ContentDAO(conn);
			updateTableCloumns(conn);
			StringBuffer buf = new StringBuffer();
			
			buf.append("INSERT INTO email_content ");
			buf.append(" (send_ok,id,pre,username,a0100,subject,address,content,send_time)");
			buf.append("values(?,?,?,?,?,?,?,?,?) ");
			ArrayList value = new ArrayList();
			value.add(flag);
			value.add(emailContent.get("templateId"));
			value.add(pre);
			value.add(emailContent.get("username"));
			value.add(emailContent.get("a0100"));
			value.add(emailContent.get("subject"));
			value.add(emailContent.get("toAddr"));
			value.add(emailContent.get("bodyText"));
			value.add(create_time);
			dao.update(buf.toString(),value);
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
