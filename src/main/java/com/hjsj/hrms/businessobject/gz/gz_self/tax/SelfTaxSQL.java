package com.hjsj.hrms.businessobject.gz.gz_self.tax;

import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.sys.options.ParseSYS_OTH_PARAM;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Element;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.*;

public class SelfTaxSQL {
	// 查询个人所得税，按计税倒序显示
	public String[] getSqlStr(ContentDAO dao, UserView uv, String tablename)
			throws GeneralException {
		String sqlstr[] = new String[4];
		if (this.isGz_tax_mxExist(dao)) {
			String column = this.getColumn(this.getFieldList(tablename, dao));
			String select = "select " + column;
			String where = "from " + tablename + " where a0100='"
					+ uv.getA0100() + "' and Upper(nbase)='"
					+ uv.getDbname().toUpperCase() + "'";
			String order = "order by tax_date desc,declare_tax desc";
			sqlstr[0] = select;
			sqlstr[1] = column;
			sqlstr[2] = where;
			sqlstr[3] = order;
		}
		return sqlstr;
	}

	// 按年查询和按时间段查询
	public String[] getSqlStr(ContentDAO dao, UserView uv, String tablename,
			String startime, String endtime) throws GeneralException {
		WorkdiarySQLStr wss = new WorkdiarySQLStr();
		String tempstart = wss.getDataValue("declare_tax", ">=", startime);
		String tempend = wss.getDataValue("declare_tax", "<=", endtime);
		String timefield = " and " + tempstart + " and " + tempend;
		String sqlstr[] = new String[4];
		if (this.isGz_tax_mxExist(dao)) {
			String column = this.getColumn(this.getFieldList(tablename, dao));
			String select = "select " + column;
			String where = "";
			if (startime != null && startime.length() > 0 && endtime != null
					&& endtime.length() > 0) {
				where = "from " + tablename + " where a0100='" + uv.getA0100()
						+ "' and Upper(nbase)='" + uv.getDbname().toUpperCase()
						+ "' " + timefield;
			}
			if (startime == null || startime.length() < 1) {
				where = "from " + tablename + " where a0100='" + uv.getA0100()
						+ "' and Upper(nbase)='" + uv.getDbname().toUpperCase()
						+ "' and " + tempend;
			}
			if (endtime == null || endtime.length() < 1) {
				where = "from " + tablename + " where a0100='" + uv.getA0100()
						+ "' and Upper(nbase)='" + uv.getDbname().toUpperCase()
						+ "' and " + tempstart;
			}
			if (startime.length() < 1 && endtime.length() < 1) {
				where = " from " + tablename + " where a0100='" + uv.getA0100()
						+ "' and Upper(nbase)='" + uv.getDbname().toUpperCase()
						+ "' ";
			}
			String order = "order by tax_date,declare_tax asc";
			sqlstr[0] = select;
			sqlstr[1] = column;
			sqlstr[2] = where;
			sqlstr[3] = order;
		}
		return sqlstr;
	}

	// 按归属时间查询
	public String[] getSqlStr(ContentDAO dao, UserView uv, String tablename,
			String gssj) throws GeneralException {
		WorkdiarySQLStr wss = new WorkdiarySQLStr();
		String tempgssj = wss.getDataValue("declare_tax", "=", gssj);
		String timefield = " and " + tempgssj;
		String sqlstr[] = new String[4];
		if (this.isGz_tax_mxExist(dao)) {
			String column = this.getColumn(this.getFieldList(tablename, dao));
			String select = "select " + column;
			String where = "from " + tablename + " where a0100='"
					+ uv.getA0100() + "'" + timefield;
			String order = "order by tax_date,declare_tax asc";
			sqlstr[0] = select;
			sqlstr[1] = column;
			sqlstr[2] = where;
			sqlstr[3] = order;
		}
		return sqlstr;
	}

	// 判断gz_tax_mx是否存在
	public boolean isGz_tax_mxExist(ContentDAO dao) throws GeneralException {
		boolean flag = true;
		RowSet rowSet=null;
		try {
			rowSet=dao.search("select count(*) from gz_tax_mx where 1=2");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			flag = false;
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					ResourceFactory.getProperty("gz.self.tax.error.message"),
					"", ""));

		}
		finally
		{
			try
			{
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		return flag;
	}

	// 获得显示字段
	public String getColumn(ArrayList fieldlist) {
		StringBuffer column = new StringBuffer();
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem fit = (FieldItem) fieldlist.get(i);
			if (i == 0) {
				column.append(fit.getItemid());
			} else {
				column.append("," + fit.getItemid());
			}
		}
		return column.toString();
	}

	// 过滤字段
	public ArrayList getFieldList(String tablename, ContentDAO dao) {
		ArrayList retlist = new ArrayList();
		String sqlxml = "select * from constant where constant='GZ_TAX_MX'";
		String xml = "";
		RowSet rs=null;
		try {
			rs = dao.search(sqlxml);
			if (rs.next()) {
				xml = rs.getString("Str_Value");
			}
			if(xml==null||xml.trim().length()<=0)
			{
				xml="<?xml version='1.0' encoding='GB2312' ?><param></param>";		
			}
			ParseSYS_OTH_PARAM sop = new ParseSYS_OTH_PARAM(xml);
			Element root = sop.getRootUri();
			Element fields = root.getChild("fields");
			if(fields!=null)
			{
	    		List fieldslist = fields.getChildren("field");
		    	for (Iterator it = fieldslist.iterator(); it.hasNext();) {
		    		Element field = (Element) it.next();
			    	String visible = field.getAttributeValue("visible");
			    	if ("true".equalsIgnoreCase(visible)) {
				    	String width = field.getAttributeValue("width");
				    	String fieldesc = field.getAttributeValue("id").toLowerCase();
				    	FieldItem tempitem = DataDictionary.getFieldItem(fieldesc);
				    	if (tempitem == null || "tax_date".equalsIgnoreCase(fieldesc)|| "declare_tax".equalsIgnoreCase(fieldesc)|| "taxmode".equalsIgnoreCase(fieldesc)|| "description".equalsIgnoreCase(fieldesc)
				    			|| "deptid".equalsIgnoreCase(fieldesc)) {
			    			tempitem = new FieldItem();
			    			tempitem.setItemid(fieldesc);
				    		tempitem.setItemdesc((String) field.getAttributeValue("title"));
					    	if (!"tax_date".equalsIgnoreCase(fieldesc)
					     			&& !"declare_tax".equalsIgnoreCase(fieldesc)) {
					    		tempitem.setItemtype("N");
				    			if ("taxmode".equalsIgnoreCase(fieldesc)|| "description".equalsIgnoreCase(fieldesc)) {
					    			tempitem.setItemtype("A");
					    		}
				    			if("deptid".equalsIgnoreCase(fieldesc)){
				    				tempitem.setItemtype("A");
				    				tempitem.setCodesetid("UM");
				    			}
				    		} else {
			    				tempitem.setItemtype("D");
			    			}

						// tempitem.setDisplaywidth(new
						// Integer(width).intValue());
	    				}
	    				if ("a00Z2".equalsIgnoreCase(fieldesc))
			    			tempitem.setItemtype("D");
		     			tempitem.setAlign(width);
		    			if ("tax_max_id".equalsIgnoreCase(fieldesc)
				    			|| "a0100".equalsIgnoreCase(fieldesc)
				    			|| "a0000".equalsIgnoreCase(fieldesc)
					    		|| "b0110".equalsIgnoreCase(fieldesc)
					    		|| "e0122".equalsIgnoreCase(fieldesc)
				    			|| "e01a1".equalsIgnoreCase(fieldesc)
				    			||
							// fieldesc.equalsIgnoreCase("a0101")||
				    			"nbase".equalsIgnoreCase(fieldesc)
				     			|| "a00z0".equalsIgnoreCase(fieldesc)
					    		|| "a00z1".equalsIgnoreCase(fieldesc)
					    		|| "taxitem".equalsIgnoreCase(fieldesc)
					    		|| "salaryid".equalsIgnoreCase(fieldesc)
				    			|| "sskcs".equalsIgnoreCase(fieldesc)
				    			|| "sl".equalsIgnoreCase(fieldesc)
					     		|| "basedata".equalsIgnoreCase(fieldesc)
					    		|| "c2305".equalsIgnoreCase(fieldesc)
					    		|| "flag".equalsIgnoreCase(fieldesc)) {

		    			} else {
		     				retlist.add(tempitem);
		    			}
	     			}
    			}
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally
		{
		  try
		  {
			if(rs!=null)
				rs.close();
		  }
		  catch(Exception e2)
		  {
			  e2.printStackTrace();
		  }
		}
		return retlist;
	}

	public ArrayList getFieldList1(String tablename, ContentDAO dao) {
		RecordVo gztaxVo = new RecordVo("gz_tax_mx");
		HashMap hm = new HashMap();
		hm.put("tax_max_id", "主健");
		// hm.put("a00z0","年月标示");
		// hm.put("a00z1","发放次数");
		hm.put("taxmode", "计税方式");
		hm.put("tax_date", ResourceFactory.getProperty("gz.self.tax.taxdate"));
		hm.put("declare_tax", "报税时间");
		hm.put("basedata", ResourceFactory.getProperty("gz.self.tax.basedata"));
		hm.put("sl", ResourceFactory.getProperty("gz.self.tax.sl"));
		hm.put("sds", ResourceFactory.getProperty("gz.self.tax.sds"));
		hm.put("sskcs", ResourceFactory.getProperty("gz.self.tax.sskcs"));
		hm.put("ynse", "应纳税所得额");
		/** 纳税项目 */
		hm.put("description", "项目");
		String sqlxml = "select * from constant where constant='GZ_TAX_MX'";
		String xml = null;
		try {
			RowSet rs = dao.search(sqlxml);
			if (rs.next()) {
				xml = rs.getString("Str_Value");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ArrayList fieldlist = new ArrayList();
		ArrayList fieldesclist = new ArrayList();
		fieldesclist.add("tax_date");
		fieldesclist.add("ynse");
		fieldesclist.add("sds");
		fieldesclist.add("taxmode");
		fieldesclist.add("description");
		// fieldesclist.add("declare_tax");

		if (xml != null && xml.length() > 0) {
			try {
				ParseSYS_OTH_PARAM sop = new ParseSYS_OTH_PARAM(xml);
				Map myMap = sop.serachatomElemetValue("/param/items");
				if (myMap != null) {
					String resume = (String) myMap.get("items");
					String[] temp = resume.split(",");
					for (int i = 0; i < temp.length; i++) {
						if (temp[i].length() > 0)
							fieldesclist.add(temp[i]);

					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		for (int i = 0; i < fieldesclist.size(); i++) {
			String fieldesc = (String) fieldesclist.get(i);
			if ("tax_max_id".equalsIgnoreCase(fieldesc)
					|| "a0100".equalsIgnoreCase(fieldesc)
					|| "a0000".equalsIgnoreCase(fieldesc)
					|| "b0110".equalsIgnoreCase(fieldesc)
					|| "e0122".equalsIgnoreCase(fieldesc)
					|| "e01a1".equalsIgnoreCase(fieldesc)
					|| "a0101".equalsIgnoreCase(fieldesc)
					|| "nbase".equalsIgnoreCase(fieldesc)
					|| "a00z0".equalsIgnoreCase(fieldesc)
					|| "a00z1".equalsIgnoreCase(fieldesc)
					|| "taxitem".equalsIgnoreCase(fieldesc)
					|| "salaryid".equalsIgnoreCase(fieldesc)
					|| "sskcs".equalsIgnoreCase(fieldesc)
					|| "sl".equalsIgnoreCase(fieldesc)
					|| "basedata".equalsIgnoreCase(fieldesc)
					|| "c2305".equalsIgnoreCase(fieldesc)) {

			} else {

				FieldItem tempitem = DataDictionary.getFieldItem(fieldesc);
				if (tempitem == null) {
					tempitem = new FieldItem();
					tempitem.setItemid(fieldesc);
					tempitem.setItemdesc((String) hm.get(fieldesc));
					if (!"tax_date".equals(fieldesc)
							&& !"declare_tax".equals(fieldesc)) {
						tempitem.setItemtype("N");
						if ("taxmode".equalsIgnoreCase(fieldesc)) {
							tempitem.setItemtype("A");
						}
					} else {

						tempitem.setItemtype("D");

					}

					// System.out.println(tempitem.getItemid());
				}

				fieldlist.add(tempitem);

			}
		}

		return fieldlist;
	}

	public String getSumSql(String gssj, UserView uv) {
		WorkdiarySQLStr wss = new WorkdiarySQLStr();
		String tempgssj = wss.getDataValue("declare_tax", "=", gssj);
		String timefield = " and " + tempgssj;
		String sql = "select sum(ynse) as sumynse,sum(sds) as  sumsds from gz_tax_mx where a0100='"
				+ uv.getA0100()
				+ "' and Upper(nbase)='"
				+ uv.getDbname().toUpperCase()
				+ "' "
				+ timefield
				+ " and flag=1";
		return sql;
	}

	public String getSumSql(UserView uv) {

		String sql = "select sum(ynse) as sumynse,sum(sds) as  sumsds from gz_tax_mx where a0100='"
				+ uv.getA0100()
				+ "' and Upper(nbase)='"
				+ uv.getDbname().toUpperCase() + "' and flag=1";
		return sql;
	}

	public String getSumSql(UserView uv, String startime, String endtime) {
		WorkdiarySQLStr wss = new WorkdiarySQLStr();
		String tempstart = wss.getDataValue("declare_tax", ">=", startime);
		String tempend = wss.getDataValue("declare_tax", "<=", endtime);
		String timefield = " and " + tempstart + " and " + tempend;

		if (startime == null || startime.length() < 1) {
			timefield = " and " + tempend;
		}
		if (endtime == null || endtime.length() < 1) {
			timefield = " and " + tempstart;
		}
		if (startime.length() < 1 && endtime.length() < 1) {
			timefield = "";
		}

		String sql = "select sum(ynse) as sumynse,sum(sds) as  sumsds from gz_tax_mx where a0100='"
				+ uv.getA0100()
				+ "' and Upper(nbase)='"
				+ uv.getDbname().toUpperCase()
				+ "' "
				+ timefield
				+ " and flag=1";

		return sql;
	}
}
