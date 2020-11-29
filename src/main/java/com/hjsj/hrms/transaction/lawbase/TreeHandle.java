package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 该类的功能为操作树型结构包括查找所有父结点与子结点等功能
 * @author lzy
 *
 */
public class TreeHandle {

	public TreeHandle() {
		super();
	}

	private Connection con = null;

	public TreeHandle(Connection con) {
		super();
		if (con != null) {
			this.con = con;
		}
	}

	/**
	 * @author lzy
	 * 适用于父子链树型结构，通过输入父字段名和子字段名当前结点ID值返回当前结点所有父结点列表
	 * @param tableName
	 *            表名
	 * @param parentFieldName
	 *            父字段名称
	 * @param childFieldName
	 *            子字段名称
	 * @param nodeId
	 *            当前结点id值
	 * @param baseTerm
	 *            根节点条件。这个参数需要根据你设定的树型数据结构进行设定。 判断根节点条件例如：父字段名 = "" || 父字段名=
	 *            null该参为空时默认条件为子字段名 = 父字段。
	 * @return 返回父结点列表
	 */
	public Vector selectAllParentList(String tableName, String parentFieldName,
			String childFieldName, String nodeId, String baseTermValue) {
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
		ContentDAO dao = new ContentDAO(con);
		try {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			    PubFunc.closeResource(rs);
			    PubFunc.closeResource(ps);
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return vct;
	}

	/**
	 * 
	 *  参数含义参照selectAllParentList方法。此方法将selectAllParentList返回
	 *             的list分析成sql语句的where条件。注意当childFieldName字段类型值不需要加''
	 *             例如数字型。才调用此方法。
	 * @return 返回包含所有父结点的where子句
	 */
	public String selectAllParentNum(String tableName, String parentFieldName,
			String childFieldName, String nodeId, String baseTermValue,
			boolean parentOrChild) {
		StringBuffer whereText = new StringBuffer("");
		Vector v = null;
		if (parentOrChild) {
			v = selectAllParentList(tableName, parentFieldName, childFieldName,
					nodeId, baseTermValue);
		} else {
			v = selectAllChildList(tableName, parentFieldName, childFieldName,
					nodeId);
		}
		for (int i = 0; i < v.size(); i++) {
			whereText.append(childFieldName + "=" + v.get(i));
			if (i != (v.size() - 1)) {
				whereText.append(" or ");
			}
		}
		return whereText.toString();
	}

	/**
	 * 
	 * 参数含义参照selectAllParentList方法。此方法将selectAllParentList返回
	 *             的list分析成sql语句的where条件。注意当childFieldName字段类型值需要加''例如字符型。
	 *             才调用此方法。
	 * @return 返回包含所有父结点的where子句
	 */
	public String selectAllParentStr(String tableName, String parentFieldName,
			String childFieldName, String nodeId, String baseTermValue,
			boolean parentOrChild) {
		StringBuffer whereText = new StringBuffer("");
		Vector v = null;
		if (parentOrChild) {
			v = selectAllParentList(tableName, parentFieldName, childFieldName,
					nodeId, baseTermValue);
		} else {
			v = selectAllChildList(tableName, parentFieldName, childFieldName,
					nodeId);
		}
		for (int i = 0; i < v.size(); i++) {
			whereText.append(childFieldName + "='" + v.get(i) + "'");
			if (i != (v.size() - 1)) {
				whereText.append(" or ");
			}
		}
		return whereText.toString();
	}

	public Vector selectAllChildList(String tableName, String parentFieldName,
			String childFieldName, String nodeId) {
		Vector vct = new Vector();
		String parentId = "";
		StringBuffer sb = new StringBuffer("select " + childFieldName + ","
				+ parentFieldName);
		sb.append(" from " + tableName + " where " + parentFieldName + "=?");
		sb.append(" and base_id <> up_base_id");// 如果采用根节点为父子相等方式，则过滤掉根节点因为根节点也不可能是谁的子结点
		
		ResultSet rs = null;
		ContentDAO dao = new ContentDAO(con);
		try {
			List values=new ArrayList();
			values.add(nodeId);
			rs=dao.search(sb.toString(), values);
			ArrayList up_list=new ArrayList();
			while (rs.next())
			{
					vct.add(rs.getString(childFieldName));
					nodeId = rs.getString(childFieldName);
					up_list.add(nodeId);
		    }
			for(int i=0;i<up_list.size();i++)
			{
				nodeId=up_list.get(i).toString();
				ArrayList list=getChildIdlist(nodeId,tableName,parentFieldName,childFieldName);
				for(int r=0;r<list.size();r++)
				{
					vct.add(list.get(r));
					up_list.add(list.get(r));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			    PubFunc.closeResource(rs);
			    
			} catch (Exception err) {
				err.printStackTrace();
			}
		}
		return vct;
	}

	public static void main(String[] args) {
	}
    public ArrayList getChildIdlist(String nodeId,String tableName, String parentFieldName,String childFieldName)
    {
    	ArrayList list=new ArrayList();
    	StringBuffer sb = new StringBuffer("select " + childFieldName + ","
				+ parentFieldName);
		sb.append(" from " + tableName + " where " + parentFieldName + "='"+nodeId+"'");
		sb.append(" and base_id <> up_base_id");// 如果采用根节点为父子相等方式，则过滤掉根节点因为根节点也不可能是谁的子结点
		ContentDAO dao=new ContentDAO(this.con);
		RowSet rs=null;
		try
		{
			rs=dao.search(sb.toString());
			while(rs.next())
			{
				list.add(rs.getString(childFieldName));
			}
		}catch(Exception  e)
		{
			e.printStackTrace();
		}finally{
			if(rs!=null)
				try {
				    PubFunc.closeResource(rs);
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return list;
    }
}
