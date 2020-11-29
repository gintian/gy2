/*
 * 创建日期 2005-7-14
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.interfaces.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

/**
 * 规章制度显示Tree
 */
public class GetLawTextDirectoryByXml {
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

	public void setOrgId(String myog) {
		orgId = myog;
	}

	public GetLawTextDirectoryByXml(String target, String params) {
		this.params = params;
		this.target = target;
	}

	/**
	 * 输出规章制度的目录
	 * 
	 * @param baction
	 *            是否带上点击的事件
	 * @return
	 * @throws GeneralException
	 */
	public String outPutDirectoryXml(boolean baction) throws GeneralException {
		if ("".equals(orgId.trim())) {
			orgId = "-1";
		}
		StringBuffer xmls = new StringBuffer("");
		StringBuffer strsql = new StringBuffer("");
		Statement stmt = null;
		ResultSet rset = null;
		params = PubFunc.keyWord_reback(params);
		Connection conn = AdminDb.getConnection();
		Element root = new Element("TreeNode");
		root.setAttribute("id", "00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "lawtext");
		Document myDocument = new Document(root);
		String actionname = "/selfservice/lawbase/lawtext/law_maintenance.do";
		String theaction = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			String law_file_priv=SystemConfig.getPropertyValue("law_file_priv");
			stmt = conn.createStatement();

			strsql.append("select base_id,name,status from law_base_struct where 1=1");
			if (baction) {
				strsql.append(" and status=1");
			}
			if((this.userView.isSuper_admin()&&!this.userView.isBThreeUser())||!"false".equals(law_file_priv.trim())||!"base_id=up_base_id".equals(params)){
				strsql.append(" and " + params);
			}
			strsql.append(" and  basetype=" + basetype);
			
			if(!(this.userView.isSuper_admin()&&!this.userView.isBThreeUser()))
			{
			  if ("base_id=up_base_id".equals(params)) {
				  
					if(!"false".equals(law_file_priv.trim())){
						if (orgId == null || "".equals(orgId.trim())
								|| "null".equals(orgId.trim())/*||orgId.trim().equals("-1")*/) {
							strsql.append(" and (dir='-1' or dir is null or dir = '')");
						} else {
							/*strsql.append(" and (dir='-1' or dir is null or dir = ''");
							LawDirectory lawDirectory=new LawDirectory();
							String orgsrt=lawDirectory.getOrgStrs(orgId,"UN",conn);
							strsql.append(" or dir in(" + orgsrt + "))");*/
							strsql.append(" and  (dir='-1' or dir is null or dir = '' or dir='" + orgId + "')");
						}
					}
			  }
			}

			strsql.append(" order by displayorder");

			//System.out.println(strsql.toString());
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
			String days=sysoth.getValue(Sys_Oth_Parameter.LAWRULE_FILE_DAYS);
			
			dbS.open(conn, strsql.toString());
			rset = stmt.executeQuery(strsql.toString());
			LawDirectory lawDirectory=new LawDirectory();
			String images_bir="<img src='/images/new0.gif' border=0>";
			while (rset.next()) {
//				System.out.println(rset
//						.getString("base_id"));
//				System.out.println(userView.isHaveResource(IResourceConstant.LAWRULE, rset
//						.getString("base_id")));
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
				String base_id=rset.getString("base_id");
				boolean haschildnode = LawDirectory.hasChildNode(base_id);
				String base_ids=lawDirectory.getBase_Ids(base_id,conn);
				boolean isCorrect =lawDirectory.isNewLawText(conn,base_ids,days);
				Element child = new Element("TreeNode");
				child.setAttribute("id", PubFunc.encrypt(rset.getString("base_id")));
				//child.setAttribute("text", rset.getString("name"));
				if(isCorrect)
					child.setAttribute("text", rset.getString("name")+images_bir);
				else
				   child.setAttribute("text", rset.getString("name"));
				
				child.setAttribute("title", rset.getString("name"));
				theaction = actionname + "?b_query=link&encryptParam=" + PubFunc.encrypt("a_base_id="
						+ rset.getString("base_id")+"&status="+rset.getString("status"));
				if (baction) {
					child.setAttribute("href", theaction);
					child.setAttribute("target", this.target);
				}
				if (haschildnode) {
					/*
					if (baction) {
						child.setAttribute("xml",
								"/selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp?encryptParam=" + PubFunc.encrypt("params=base_id<>up_base_id and up_base_id%3D'"
								+ rset.getString("base_id") + "'" + "&basetype=" + basetype));
					} else {
						child.setAttribute("xml",
								"/selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp?encryptParam=" + PubFunc.encrypt("params=base_id<>up_base_id and up_base_id%3D'"
								+ rset.getString("base_id") + "'" + "&basetype=" + basetype + "&action=0"));
					}
					*/
					/** /selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp为了保证安全要求params参数需要加密。
					 * 为什么不用统一的encryptParams全局参数加密？因为有前端js调用的地方，没办法全局加密
					 * */
					StringBuffer xml = new StringBuffer();
					xml.append("/selfservice/lawbase/lawtext/get_lawbase_strut_tree.jsp?encryptParam=");

					StringBuffer params = new StringBuffer();
					params.append("params=");
					params.append(PubFunc.encrypt("base_id<>up_base_id and up_base_id = '"+rset.getString("base_id")+"'"));
					params.append("&basetype=").append(basetype);
					if(!baction){
						params.append("&action=0");
					}

					xml.append(PubFunc.encrypt(params.toString()));
					child.setAttribute("xml",xml.toString());
				}
				child.setAttribute("icon", "/images/book1.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
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
			ArrayList values=new ArrayList();
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
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(ps);
		}
		return vct;
	}
}
