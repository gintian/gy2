package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * <p>
 * Title:ApproveMediaTrans
 * </p>
 * <p>
 * Description:多媒体文件的批准和驳回
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2010-2-2 17:41:54
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 */

public class ApproveMediaTrans extends IBusiness {

	public void execute() throws GeneralException {
		String a0100 = (String) this.getFormHM().get("a0100");
		String dbname = (String) this.getFormHM().get("dbname");
		// 批准退回状态，2为退回，3为批准
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String state = (String) map.get("state");

		// 数据批准后是否可驳回,true为批准后可以驳回
		String checked_may_reject = "";
		try {
			checked_may_reject = SystemConfig.getProperty("checked_may_reject");
		} catch(Exception e) {
			checked_may_reject = "false";
		}
		// 获得选中的数据集合
		ArrayList mediainfolist = (ArrayList) this.getFormHM().get(
				"selectedlist");

		// 选中的数据的i9999值
		String i9999s = "";
		boolean oflag = true;
		for (int i = 0; i < mediainfolist.size(); i++) {
			if (i != 0) {
				i9999s += ",";
			}
			LazyDynaBean rec = (LazyDynaBean) mediainfolist.get(i);
			
			/**
			 * 批准：只批准已报批的多媒体文件
			 * 退回：当设置批准后的数据可以退回时，追加报批和批准的数据
			 * 		当设置批准后的数据可以退回时，只追加报批的数据
			 */
			if ("2".equals(state)) {//退回
				if ("true".equalsIgnoreCase(checked_may_reject)) {

					// 追加报批数据的i9999值
					if ("1".equals(rec.get("state").toString())) {
						i9999s += rec.get("i9999").toString();
					}else if ("3".equals(rec.get("state").toString())) {// 追加已批准数据的i9999值
						i9999s += rec.get("i9999").toString();
					}else{
						oflag = false;
					}

				} else {
					
					// 追加报批数据的i9999值
					if ("1".equals(rec.get("state").toString())) {
						i9999s += rec.get("i9999").toString();
					}else{
						oflag = false;
					}
				}
			} else {//批准
				
				// 追加报批数据的i9999值
				if ("1".equals(rec.get("state").toString())) {
					i9999s += rec.get("i9999").toString();
				}else{
					oflag = false;
				}
			}
		}
		boolean flag = true;
		// 执行操作
		if(oflag){
			flag = approve(a0100, dbname, i9999s, state);
		}else{
			String approve = "";
			if ("2".equals(state)) {
				approve = "退回";
			}
			if ("3".equals(state)) {
				approve = " 批准";
			}
			throw GeneralExceptionHandler.Handle(new GeneralException("多媒体文件"
					+ approve + "失败！！该信息状态下不允许此操作！"));
		}

		// 执行失败时抛出异常
		if (!flag) {
			String approve = "";
			if ("2".equals(state)) {
				approve = "退回";
			}
			if ("3".equals(state)) {
				approve = " 批准";
			}
			throw GeneralExceptionHandler.Handle(new GeneralException("多媒体文件"
					+ approve + "失败！！"));
		}

	}

	/**
	 * 对数据进行批准退回操作
	 * 
	 * @param a0100
	 * @param dbname
	 * @param i9999s
	 *            所选数据的i9999值
	 * @param state
	 *            批准或退回，2为退回，3为批准
	 * @return
	 */
	private boolean approve(String a0100, String dbname, String i9999s,
			String state) {
		//当没有记录可批准或退回时，返回true，
		if (i9999s == null || i9999s.trim().length() <= 0 ||i9999s.split(",").length <= 0) {
			
			return true;
		}
		// 执行是否成功
		boolean flag = true;

		// sql语句
		StringBuffer sql = new StringBuffer();
		sql.append("update ");
		sql.append(dbname);
		sql.append("A00 set state='");
		sql.append(state);
		sql.append("' where a0100='");
		sql.append(a0100);
		sql.append("' and i9999 in (");
		sql.append(i9999s);
		sql.append(")");

		// 执行批准操作，更改状态
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(sql.toString());
		} catch (SQLException e) {
			// 操作失败
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}
}
