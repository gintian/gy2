package com.hjsj.hrms.interfaces.train;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>
 * Title:TrainProTree.java
 * </p>
 * <p>
 * Description:培训项目树
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2008-07-28 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class TrainProTree {
	private String code;
	private UserView userview;

	public TrainProTree(String code) {
		this.code = code;
	}

	public TrainProTree(String code, UserView userview) {
		this.userview = userview;
		this.code = code;
	}

	public String outPutXmlStr() {
		StringBuffer xml = new StringBuffer();
		try {
			// 创建xml文件的根元素
			Element root = new Element("TreeNode");
			// 设置根元素属性
			root.setAttribute("id", "");
			root.setAttribute("text", "root");
			root.setAttribute("title", "trainType");
			// 创建xml文档自身
			Document myDocument = new Document(root);

			ArrayList list = getChildList();
			for (Iterator t = list.iterator(); t.hasNext();) {
				LazyDynaBean bean = (LazyDynaBean) t.next();
				Element child = new Element("TreeNode");
				String id = (String) bean.get("id");
				String name = (String) bean.get("name");

				child.setAttribute("id", SafeCode.encode(PubFunc.encrypt(id)));
				child.setAttribute("text", name);
				child.setAttribute("title", name);
				String a_xml = "/train/resource/trainProTree.jsp?encryptParam="
						+ PubFunc.encrypt("code=" + SafeCode.encode(PubFunc.encrypt(id)));
				child.setAttribute("xml", a_xml);
				child.setAttribute("href", "trainProList.do?b_query=link&encryptParam="
						+ PubFunc.encrypt("code=" + SafeCode.encode(PubFunc.encrypt(id))));
				child.setAttribute("target", "mil_body");
				String imgStr = "/images/close.png";
				child.setAttribute("icon", imgStr);
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			// 格式化输出类
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);

			// 将生成的XML文件作为字符串形式
			xml.append(outputter.outputString(myDocument));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml.toString();
	}

	private ArrayList getChildList() {

		ArrayList list = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		try {
			this.code = PubFunc.decrypt(SafeCode.decode(this.code));
			String sql = "";
			if (this.code == null || "".equals(this.code))
				sql = "select * from r13 where (r1308 is null or r1308='')";
			else
				sql = "select * from r13 where r1308='" + this.code + "'";

			if (userview != null && !userview.isSuper_admin()) {
				String a_code = "";
				TrainCourseBo bo = new TrainCourseBo(this.userview);
				a_code = bo.getUnitIdByBusi();
				String unitarr[] = a_code.split("`");
				String str = "";
				if (a_code.indexOf("UN`") == -1) {// UN`全部
					for (int i = 0; i < unitarr.length; i++) {
						if (unitarr[i] != null && unitarr[i].trim().length() > 2
								&& "UN".equalsIgnoreCase(unitarr[i].substring(0, 2))) {
							str += "B0110 like '" + unitarr[i].substring(2) + "%' or ";
						}
					}
					if (str.length() > 0)
						sql += " and (b0110='HJSJ' or " + str.substring(0, str.lastIndexOf("or") - 1) + ")";
					else {
						sql += " and B0110 = 'HJSJ'";
					}
				}
			}

			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			sql += " order by r1301";
			rs = dao.search(sql);
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("id", rs.getString("r1301"));
				bean.set("name", rs.getString("r1302"));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(conn);
		}
		return list;
	}

}
