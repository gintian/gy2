package com.hjsj.hrms.servlet.sys.options.customreport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class TreeServlet extends HttpServlet {

	// 当前递归的次数
	private int recursiveNum = 0;
	// 数据库连接
	private Connection conn = null;
	// 是否加载权限 0为不考虑权限，1为考虑权限（只针对组织机构树）
	private String priv = "0";
	// 登录用户
	//private UserView userView = null;
	// 层级，显示到第几层
	private String level = "9999";

	
	/**
	 * Constructor of the object.
	 */
	public TreeServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		recursiveNum = 0;
		response.setContentType("text/html");
		response.setCharacterEncoding("GBK");
		String strJson = "";
		try {
			conn = AdminDb.getConnection();
			
			// 获得父节点的id
			String parentId = request.getParameter("id");
			// 获得当前代码类的值
			String codeSetId = request.getParameter("codeset");
			//是否加载权限
			priv = request.getParameter("priv");
			// 层级
			level = request.getParameter("level");
			
			// 登录用户
			HttpSession session = request.getSession();
			String userViewStr = WebConstant.userView;
			UserView userView = (UserView) session.getAttribute(userViewStr);
			
			// 根节点设为0,搜索根节点的id
			if ("0".equalsIgnoreCase(parentId)) {
				if ("1".equals(priv) &&("UN".equalsIgnoreCase(codeSetId) 
						||"UM".equalsIgnoreCase(codeSetId)
						||"@K".equalsIgnoreCase(codeSetId))) {
					if (userView.isSuper_admin()) {
//						parentId = getRootNodeId("UN");
						strJson = this.getJsonById(parentId, codeSetId, userView);
					} else {
						String privCode = userView.getManagePrivCode();
//						parentId = userView.getManagePrivCodeValue();
						if (getKind(privCode) > getKind(codeSetId)) {
							codeSetId = privCode;
						}
						strJson = this.getJsonById(parentId, codeSetId, userView);
					}
				} else {
//					parentId = getRootNodeId("UN");
					strJson = this.getJsonById(parentId, codeSetId, userView);
				}
			} else {
				strJson = this.getJsonById(parentId, codeSetId, userView);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PrintWriter out = response.getWriter();
		out.print(strJson);
		out.flush();
		out.close();
	}

	private int getKind (String codeSetId) {
		if ("UN".equalsIgnoreCase(codeSetId)) {
			return 1;
		} else if ("UM".equalsIgnoreCase(codeSetId)) {
			return 2;
		} else if ("@K".equalsIgnoreCase(codeSetId)) {
			return 3;
		}
		return 4;
	}
	/**
	 * 通过递归算法生成树的JSON数据
	 * 
	 * <pre>
	 * [{id : '1',
	 *   text : '目录一',
	 *   leaf : false,
	 *   children : 
	 *   [{id : '6',
	 *     text : '标题一',
	 *     leaf : true,
	 *     singleClickExpand:true}],
	 *   singleClickExpand:true},
	 *   {id : '2',
	 *   text : '目录二',
	 *   leaf : false,
	 *   children : [],
	 *   singleClickExpand:true}]
	 * </pre>
	 * @param userView 
	 * 
	 * @param id
	 * @return
	 */
	private String getJsonById(String parentId, String codeSetId, UserView userView) {

		ArrayList dataList = this.getDataList(codeSetId, parentId, userView);
		if (dataList.size() == 0) {
			return "";
		}
		StringBuffer json = new StringBuffer("[");
		
		for (int i = 0; i < dataList.size(); i++) {
			
			HashMap map = (HashMap) dataList.get(i);
			json.append("{");
			json.append("id : '");
			json.append(map.get("codeitemid"));
			json.append("',text : '");
			json.append(map.get("codeitemdesc"));
			json.append("',");
			String pid = (String) map.get("parentid");
			String id = (String) map.get("codeitemid");
			String cid = (String) map.get("childid");
			String codset = (String) map.get("codesetid");
			
			if ("UN".equalsIgnoreCase(codset)) {
				json.append("icon : '/images/root.gif', ");
			} else if ("UM".equalsIgnoreCase(codset)) {
				json.append("icon : '/images/dept.gif', ");
			} else if ("@K".equalsIgnoreCase(codset)) {
				json.append("icon : '/images/pos_l.gif',");
			} 
			
			ArrayList childDataList = getDataList(codeSetId, id, userView);
			
			if (childDataList.size() == 0) {
				json.append("leaf : true ");
			} else {
				json.append("children:");
				json.append(getJsonById(id, codeSetId, userView));
			}
			json.append("}");
			if (i < dataList.size() - 1) {
				json.append(",");
			}
		}
		// json.append("{id : '01',text : '某集团公司',
		// children:[{id:'0101',text:'下属',leaf:true}]}");
		// json.append("{text:'某集团公司',id:'01',children:[{text:'下属',id:'0101','leaf':true}]}");
		json.append("]");
		return json.toString();
	}

	/**
	 * 根据id获得子节点
	 * 
	 * @param codeSetId
	 * @param parentId
	 * @param userView 
	 * @return
	 */
	public ArrayList getDataList(String codeSetId, String parentId, UserView userView) {
		recursiveNum++;
		String codeset = codeSetId;
		ArrayList dataList = new ArrayList();
		if ("UN".equalsIgnoreCase(codeSetId)) {
			codeSetId = "'UN'";
		} else if ("UM".equalsIgnoreCase(codeSetId)) {
			codeSetId = "'UN','UM'";
		} else if ("@K".equalsIgnoreCase(codeSetId)) {
			codeSetId = "'UN','UM','@K'";
		} else {
			codeSetId = "'" + codeSetId + "'";
		}
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid,codeitemid,");
		sql.append("codeitemdesc,parentid,childid from (");
		sql.append(" select codesetid,codeitemid,codeitemdesc,parentid,childid,grade from organization ");
		if (Sql_switcher.searchDbServer() == 1) {
			sql.append(" where GETDATE() between start_date and end_date ");
		} else if (Sql_switcher.searchDbServer() == 2){
			sql.append(" where sysdate between start_date and end_date ");
		}
		//sql.append(" union select codesetid,codeitemid,codeitemdesc,parentid,childid from vorganization");
		sql.append(" ) b");
		sql.append(" where codesetid in (");
		sql.append(codeSetId);
		sql.append(") ");
		if (!"0".equals(parentId)) {
			sql.append("and parentid ='");
			sql.append(parentId);
			sql.append("'");
		}
		sql.append(" and grade <=");
		sql.append(level);
		if ("1".equals(priv) &&("UN".equalsIgnoreCase(codeset) 
				||"UM".equalsIgnoreCase(codeset)
				||"@K".equalsIgnoreCase(codeset))) {
			if ("0".equals(parentId)) {
								
				String Manage = userView.getManagePrivCodeValue();
				
				if (recursiveNum == 1) {	
					if (userView.isSuper_admin()) {
						sql.append(" and codeitemid = parentid order by codeitemid");
					} else {
						sql.append(" and codeitemid ='");
						sql.append(Manage);
						sql.append("' order by codeitemid");
					}
				} else {
					sql.append(" and codeitemid != parentid order by codeitemid");
				}
			} else {
				if (recursiveNum == 1) {
					sql.delete(0, sql.length());
					sql.append("select codesetid,codeitemid,");
					sql.append("codeitemdesc,parentid,childid ");
					sql.append(" from ( select codesetid,codeitemid,");
					sql.append("codeitemdesc,parentid,childid from organization");
					if (Sql_switcher.searchDbServer() == 1) {
						sql.append(" where GETDATE() between start_date and end_date ");
					} else if (Sql_switcher.searchDbServer() == 2){
						sql.append(" where sysdate between start_date and end_date ");
					}
//					sql.append(" union select codesetid,codeitemid,codeitemdesc,");
//					sql.append("parentid,childid from vorganization ");
					sql.append(") b where codesetid in (");
					sql.append(codeSetId);
					sql.append(") and codeitemid ='");
					sql.append(parentId);
					sql.append("'");
					sql.append(" and codeitemid != parentid order by codeitemid");
				} else {
					sql.append(" and codeitemid != parentid order by codeitemid");
				}
			}
		} else {
			if ("UN".equalsIgnoreCase(codeset) 
					||"UM".equalsIgnoreCase(codeset)
					||"@K".equalsIgnoreCase(codeset)) {
				if (recursiveNum == 1) {
					sql.append(" and codeitemid = parentid order by codeitemid");
				} else {
					sql.append(" and codeitemid != parentid order by codeitemid");
				}
			} else {
				//sql.delete(0, sql.length());
//				sql.append("select codesetid,codeitemid,");
//				sql.append("codeitemdesc,parentid,childid from codeitem");
//				sql.append(" where codesetid in (");
//				sql.append(codeSetId);
//				sql.append(") ");
//				if (recursiveNum == 1) {
//					sql.append(" and codeitemid = parentid order by codeitemid");
//				} else {
//					sql.append(" and codeitemid != parentid order by codeitemid");
//				}
			}
		}
		try {
		    ContentDAO dao = new ContentDAO(conn);
			if (sql.length() > 0) {
				rs = dao.search(sql.toString());
				while (rs.next()) {
					HashMap map = new HashMap();
					map.put("codesetid", rs.getString("codesetid"));
					map.put("codeitemid", rs.getString("codeitemid"));
					map.put("codeitemdesc", rs.getString("codeitemdesc"));
					map.put("parentid", rs.getString("parentid"));
					map.put("childid", rs.getString("childid"));
					dataList.add(map);
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
		} finally { 
		    com.hjsj.hrms.utils.PubFunc.closeResource(rs);
		}
		return dataList;
	}

	/**
	 * 获得根节点的id，parentid最小
	 * @return 组织结构的根节点的id
	 */
	public String getRootNodeId(String codeSetId) {
		String rootId = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select min(parentid) rootid from (");
		sql.append("select codesetid,parentid from organization ");
		
		if (Sql_switcher.searchDbServer() == 1) {
			sql.append(" where GETDATE() between start_date and end_date ");
		} else if (Sql_switcher.searchDbServer() == 2){
			sql.append(" where sysdate between start_date and end_date ");
		}
		
		sql.append("union select codesetid,parentid from vorganization ");
		sql.append("union select codesetid,parentid from codeitem ");
		sql.append(") b where b.codesetid = '");
		sql.append(codeSetId);
		sql.append("'");
		
		ResultSet rs = null;
		try {
		    ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql.toString());
			if (rs.next()) {
				rootId = rs.getString("rootid");
				rootId = rootId != null ? rootId : "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    com.hjsj.hrms.utils.PubFunc.closeResource(rs);
		}
		return rootId;
	}
	

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
