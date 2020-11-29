/*
 * Created on 2005-5-28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.propose;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.Reader;
import java.sql.Clob;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 */
public class SearchHtmlFileTrans extends IBusiness {
	/**
	 * 将CLOB转成String ,静态方法
	 * 
	 * @param clob
	 *            字段
	 * @return 内容字串，如果出现错误，返回null
	 */
	public final static String clobToString(Clob clob) {
		if (clob == null) {
			return null;
		}
		
		StringBuffer sb = new StringBuffer(65535);// 64K
		Reader clobStream = null;
		try {
			clobStream = clob.getCharacterStream();
			char[] b = new char[60000];// 每次获取60K
			int i = 0;
			while ((i = clobStream.read(b)) != -1) {
				sb.append(b, 0, i);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			sb = null;
		} finally {
			try {
				if (clobStream != null)
					clobStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (sb == null)
			return null;
		else
			return sb.toString();
	}

	/*
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String id = (String) hm.get("a_id");
		//id = com.hjsj.hrms.utils.PubFunc.decrypt(id);
		String flag = (String) this.getFormHM().get("flag");
		/**
		 * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑 用户的使用习惯。
		 */
		if ("1".equals(flag))
			return;
		cat.debug("------>resource_lsit_id=====" + id);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo vo = new RecordVo("resource_list");
		String sql = "select id,name,description,createdate,status,ext from resource_list where contentid="	+ id;
		try {
			vo.setString("contentid", id);
			this.frowset = dao.search(sql);
			if (this.frowset.next()) {
				vo.setString("id", this.frowset.getString("id"));
				vo.setString("name", this.frowset.getString("name"));
				vo.setString("description", /*Sql_switcher.readMemo(frowset,
						"description")*/this.frowset.getString("description"));
//				Clob clob = null;
//				switch (Sql_switcher.searchDbServer()) {
//				case Constant.DB2:
//					clob = frowset.getClob("description");
//					vo.setString("description", clobToString(clob));
//					break;
//				case Constant.MSSQL:
//					vo.setString("description", this.frowset
//							.getString("description"));
//					break;
//				case Constant.ORACEL:
//
//					try {
//
//					} catch (Exception e) {
//						clob = frowset.getClob("description");
//						vo.setString("description", clob.toString());
//					}
//					break;
//				}

				vo
						.setString("createdate", this.frowset
								.getString("createdate"));
				vo.setString("status", this.frowset.getString("status"));
				vo.setString("ext", this.frowset.getString("ext"));

			}
		} catch (OutOfMemoryError error) {
			error.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
			this.getFormHM().put("proposeTb", vo);
		}

	}
}
