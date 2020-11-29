package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title:查找代码项</p>
 * <p>Description:根据codesetid查找其下的所有代码项</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-8-6:下午13:49:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class GetCodeItemsTrans extends IBusiness {

	private static final long serialVersionUID = -5345859977001390426L;

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(frameconn);
		RowSet rs = null;
		
		try {
			String codeSetId = (String) formHM.get("codeSetId");
			if (codeSetId == null || codeSetId.length() == 0 || "0".equals(codeSetId)) {
				throw new Exception("无效的代码集");
			}

			List items = new ArrayList();
			String sql = "SELECT * FROM codeitem WHERE parentid=codeitemid AND codesetid=?";
			rs = dao.search(sql, Arrays.asList(new String[] { codeSetId }));
			while (rs.next()) {
				List item = new ArrayList();
				item.add(rs.getString("codeitemid"));
				item.add(rs.getString("codeitemdesc"));
				items.add(item);
				
				subItems(items, codeSetId, rs.getString("codeitemid"), "&nbsp;&nbsp;&nbsp;&nbsp;");
			}
			
			formHM.put("code-" + codeSetId, items);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 查询parentid的子代码项
	 * @param items setId代码集下所有的代码项
	 * @param setId 代码集id
	 * @param parentId 上级代码项id
	 * @param indent 缩进规则
	 * @throws Exception
	 */
	public void subItems(List items, String setId, String parentId, String indent) throws Exception {
		ContentDAO dao = new ContentDAO(frameconn);
		RowSet rs = null;
		
		String sql = "SELECT * FROM codeitem WHERE parentid=? AND codesetid=? AND codeitemid<>parentid";
		try {
			rs = dao.search(sql, Arrays.asList(new String[] { parentId, setId }));
			while (rs.next()) {
				List item = new ArrayList();
				item.add(rs.getString("codeitemid"));
				item.add(indent + rs.getString("codeitemdesc"));
				items.add(item);
				
				subItems(items, setId, rs.getString("codeitemid"), indent + indent);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
