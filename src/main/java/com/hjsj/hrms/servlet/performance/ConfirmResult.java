package com.hjsj.hrms.servlet.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;

/**
 * 绩效结果确认
 * @author sunjian 2017-10-19
 *
 */
public class ConfirmResult  extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String planId = request.getParameter("plan_id");
		String objectId = request.getParameter("object_id");
		String nbase = request.getParameter("nbase");//由于现在人员全部为在职人员，暂时用不到，留着
		RowSet rs = null;
		Connection con=null;
		ArrayList list = new ArrayList();
		String flag = "";//1:已经确认，0：未确认
		try {
			con = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			String sql = "select * from per_result_"+planId+" where 1=2";//可能有的表没有confirmflag列
			rs=dao.search(sql);
			ResultSetMetaData data=rs.getMetaData();
			for(int j = 1; j <= data.getColumnCount(); j++)
			{
				String columnName=data.getColumnName(j);
				if("confirmflag".equalsIgnoreCase(columnName)) {
					String sqlSelect = "select confirmflag from per_result_" + planId + " where object_id = ?";//看是否已经确认过了
					list.add(objectId);
					rs = dao.search(sqlSelect,list);
					if(rs.next()) {
						String confirmflag = rs.getString("confirmflag");
						if("2".equalsIgnoreCase(confirmflag)) {
							flag = "1";
							break;
						}else {
							String sqlUpdate = "update per_result_" + planId + " set confirmflag = 2 where object_id = ?";
							dao.update(sqlUpdate,list);
							flag = "0";
							break;
						}
					}
				}
			}
			request.setAttribute("flag", flag);
			request.getRequestDispatcher("/performance/confirmResultServlet.jsp").forward(request, response);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(con);
			PubFunc.closeDbObj(rs);
		}
	}
}
