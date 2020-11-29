package com.hjsj.hrms.interfaces.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

//import com.hjsj.hrms.interfaces.sys.IResourceConstant;

/**
 * <p>
 * Title:GetLawbaseDirectoryByXml
 * </p>
 * <p>
 * Description:通过xmlhttp协议生成前台规章制度目录结构树
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 1, 2005:11:30:12 AM
 * </p>
 * 
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class GetLawbaseDirectoryByXml {
	private String basetype;

	/**
	 * 参数串,当前目录
	 */
	private String params;

	private UserView userView;

	/**
	 * 目标窗口
	 */
	private String target;

	/**
	 * 用来保存组织机构Id
	 */
	private String orgId;

	/**
	 * 用来保存组织机构Id
	 */
	public void setOrgId(String myog) {
		orgId = myog;
	}
	/**
	 * 用来区别是否有点击连接功能
	 */
	private String flag;

	public GetLawbaseDirectoryByXml(String target, String params) {
		this.params = params;
		this.target = target;
	}

	/**
	 * 输出规章制度目录结构树
	 * 
	 * @return
	 */
	public String outPutDirectoryXml() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		StringBuffer strsql = new StringBuffer();
		Statement stmt = null;
		ResultSet rset = null;
		Connection conn = AdminDb.getConnection();
		Element root = new Element("TreeNode");
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "lawbase");
		Document myDocument = new Document(root);
		String actionname = "/selfservice/lawbase/law_maintenance.do";
		String theaction = null;
		String[] param = null;
		String param1="",param2="";
		DbSecurityImpl dbS = new DbSecurityImpl();
		params = PubFunc.keyWord_reback(params);
		if(params.indexOf("#")!=-1){
			param = params.split("#");
			param1 = param[0];
			param2 = param[1];
		}
		
		try {
			String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
			strsql.append("select base_id,name,status from law_base_struct where 1=1");
			if((this.userView.isSuper_admin()&&!this.userView.isBThreeUser())||!"false".equals(law_file_priv.trim())||!"base_id=up_base_id".equals(params)){
				if("".equals(param1)|| "".equals(param2)){
					strsql.append(" and "+ params);
				}
				else{
					strsql.append(" and " + param1);
					strsql.append(" and "+ param2);
				}
			}
			strsql.append(" and  basetype=" + basetype);
			if ("base_id=up_base_id".equals(params)) {
				if(!(this.userView.isSuper_admin()&&!this.userView.isBThreeUser()))
				{
					if(!"false".equals(law_file_priv.trim())){
						if (orgId == null || "".equals(orgId.trim())
								|| "null".equals(orgId.trim())) {
							strsql.append(" and (dir='-1' or dir is null or dir = '')");
						} else {						
							strsql.append(" and  (dir='-1' or dir is null or dir = '' or dir='" + orgId + "')");
						}
					}
				}
				
			}
			strsql.append(" order by displayorder");
			stmt = conn.createStatement();	
			dbS.open(conn, strsql.toString());
			rset = stmt.executeQuery(strsql.toString());
			while (rset.next()) {
				//System.out.println(userView.isHaveResource(IResourceConstant.LAWRULE, rset.getString("base_id")));
				if(!"false".equals(law_file_priv.trim())){
					if("1".equalsIgnoreCase(this.basetype))
					{
						if (!userView.isHaveResource(IResourceConstant.LAWRULE, rset.getString("base_id")))
							continue;
					}
					if("5".equalsIgnoreCase(this.basetype))
					{
						if (!userView.isHaveResource(IResourceConstant.DOCTYPE, rset.getString("base_id")))
							continue;
					}
					if("4".equalsIgnoreCase(this.basetype))
					{
						if (!userView.isHaveResource(IResourceConstant.KNOWTYPE, rset.getString("base_id")))
							continue;
					}
				}else{
					if("base_id=up_base_id".equals(params)){
						if("1".equalsIgnoreCase(this.basetype))
						{
							if (!userView.isHaveResource(IResourceConstant.LAWRULE, rset.getString("base_id")))
								continue;
						}
						if("5".equalsIgnoreCase(this.basetype))
						{
							if (!userView.isHaveResource(IResourceConstant.DOCTYPE, rset.getString("base_id")))
								continue;
						}
						if("4".equalsIgnoreCase(this.basetype))
						{
							if (!userView.isHaveResource(IResourceConstant.KNOWTYPE, rset.getString("base_id")))
								continue;
						}
						Vector v = selectAllParentList("law_base_struct","up_base_id", "base_id", rset.getString("base_id"), null,conn);
						boolean flag = false;
						for (int i = 0; i < v.size(); i++) {
							String up_base_id= (String)v.get(i);
							if(up_base_id.equals(rset.getString("base_id")))
								break;
							if("1".equalsIgnoreCase(this.basetype))
							{
								if (userView.isHaveResource(IResourceConstant.LAWRULE, up_base_id)){
									flag = true;
									break;
								}
							}
							if("5".equalsIgnoreCase(this.basetype))
							{
								if (userView.isHaveResource(IResourceConstant.DOCTYPE, up_base_id)){
									flag = true;
									break;
								}
							}
							if("4".equalsIgnoreCase(this.basetype))
							{
								if (userView.isHaveResource(IResourceConstant.KNOWTYPE, up_base_id)){
									flag = true;
									break;
								}
							}
						}
						if(flag)
							continue;
					}
				}
				boolean haschildnode = LawDirectory.hasChildNode(rset.getString("base_id"));
				Element child = new Element("TreeNode");
				child.setAttribute("id", SafeCode.encode(PubFunc.encrypt(rset.getString("base_id"))));
				child.setAttribute("text", rset.getString("name"));
				child.setAttribute("title", rset.getString("name"));
				theaction = actionname + "?b_query=link&encryptParam="
				+ PubFunc.encrypt("a_base_id=" + rset.getString("base_id")+"&status="+rset.getString("status"));
				if(flag!=null&& "1".equalsIgnoreCase(flag))
					child.setAttribute("href","");
				else
					child.setAttribute("href", theaction);
				child.setAttribute("target", this.target);
				if (haschildnode) {
					/*
					if(flag!=null&&flag.equalsIgnoreCase("1"))
						child.setAttribute("xml",
								"/selfservice/lawbase/get_lawbase_strut_tree.jsp?encryptParam="
						        + PubFunc.encrypt("params=base_id<>up_base_id and up_base_id%3D'"
								+ rset.getString("base_id")
								+ "'"
								+ "&basetype=" + basetype+"&flag=1"));
					else
						child.setAttribute("xml",
								"/selfservice/lawbase/get_lawbase_strut_tree.jsp?encryptParam="
						        + PubFunc.encrypt("params=base_id<>up_base_id and up_base_id%3D'"
								+ rset.getString("base_id")
								+ "'"
								+ "&basetype=" + basetype));
					 */
					/** /selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp为了保证安全要求params参数需要加密。
					 * 为什么不用统一的encryptParams全局参数加密？因为有前端js调用的地方，没办法全局加密
					 * */
					StringBuffer xml = new StringBuffer();
					xml.append("/selfservice/lawbase/get_lawbase_strut_tree.jsp?encryptParam=");
					StringBuffer params = new StringBuffer();
					params.append("params=");
					params.append(PubFunc.encrypt("base_id<>up_base_id and up_base_id = '"+rset.getString("base_id")+"'"));
					params.append("&basetype=").append(basetype);

					if(flag!=null&& "1".equalsIgnoreCase(flag)){
						params.append("&flag=1");
					}

					xml.append(PubFunc.encrypt(params.toString()));
					child.setAttribute("xml",xml.toString());
				}
				if("1".equalsIgnoreCase(rset.getString("status")))
				    child.setAttribute("icon", "/images/book1.gif");
				else
				    child.setAttribute("icon", "/images/book.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (SQLException ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}

			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(stmt);
			PubFunc.closeDbObj(conn);
		}
		return xmls.toString();
	}

	public String getBasetype() {
		return basetype;
	}

	public void setBasetype(String basetype) {
		this.basetype = basetype;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public Vector selectAllParentList(String tableName, String parentFieldName,
			String childFieldName, String nodeId, String baseTermValue,Connection conn) {
		Vector vct = new Vector();
		String parentId = "";
		StringBuffer sb = new StringBuffer("select " + childFieldName + ","
				+ parentFieldName);
		sb.append(" from " + tableName + " where " + childFieldName + "=?");
		PreparedStatement ps = null;
		boolean flg = false;// 标志baseTerm是否是默认操作
		if (baseTermValue == null || "".equals(baseTermValue.trim())) {
			flg = true;
		}
		ResultSet rs = null;
		
		try {
			ContentDAO dao=new ContentDAO(conn);
			List values=new ArrayList();
		    values.add(nodeId);
		    rs=dao.search(sb.toString(), values);
			if (rs.next())
				nodeId = rs.getString(parentFieldName);
			while (true) 
			{
				ps.setString(1, nodeId);
				rs = ps.executeQuery();
				if (!rs.next())
					break;
				vct.add(rs.getString(childFieldName));
				nodeId = rs.getString(parentFieldName);
				if (flg) {
					if (nodeId.trim().equals(
							rs.getString(childFieldName).trim())) {
						break;
					}
				} else {
					if (nodeId.trim().equals(baseTermValue.trim())) {
						break;
					}
				}
				rs.close(); //chenmengqing added at 20061010
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(rs);
			PubFunc.closeIoResource(ps);
		}
		return vct;
	}
}
