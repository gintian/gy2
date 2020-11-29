package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.taglib.CommonData;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.util.ArrayList;

public class CodesetMultitermTag extends BodyTagSupport {

	private String itemid = "";
	private String itemvalue = "";
	private String codesetid = "";
	private String hiddenname = "";
	private String rownum = "";

	public int doEndTag() throws JspException {

		Connection conn = null;
		if (itemvalue == null || itemvalue.length() <= 0)
			itemvalue = "";
		StringBuffer html = new StringBuffer();
		try {
			conn = AdminDb.getConnection();
			InfoUtils infoUtils = new InfoUtils();
			ArrayList codelist = infoUtils.getCodeSetidChildList(codesetid, conn);
			html.append("<table width=\"100%\" border=\"0\" cellspacing=\"2\" cellpadding=\"2\">");
			int row = 0;
			if (rownum == null || rownum.length() <= 0)
				rownum = "5";

			int change_Row = Integer.parseInt(rownum);
			int iwidth = 100 / change_Row;
			boolean isChangeRow = false;
			for (int t = 0; t < codelist.size(); t++) {
				if (row == 0)
					html.append("<tr>");

				html.append("<td width=\"" + iwidth + "%\">");
				CommonData vo = (CommonData) codelist.get(t);

				String checkedStr = "";
				if (("`" + itemvalue + "`").indexOf("`" + vo.getDataValue() + "`") != -1)
					checkedStr = "checked";
				
				html.append("<input type=\"checkbox\" name=\"" + itemid + "\" value=\"" + vo.getDataValue()
						+ "\" onclick=\"fieldCheckBox('" + hiddenname + "','" + itemid
						+ "',document.getElementsByName('" + itemid + "')[" + t + "]);\" " + checkedStr + ">");
				html.append(vo.getDataName());
				html.append("</td>");
				row++;
				if (row == change_Row) {
					isChangeRow = true;
					html.append("</tr>");
					row = 0;
				}

			}

			if (!isChangeRow) {
				for (; row < change_Row; row++)
					html.append("<td width=\"" + iwidth + "%\"></td>");

				html.append("</tr>");
			} else if (row != change_Row)
				html.append("</tr>");

			html.append("</table>");
			pageContext.getOut().println(html.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return SKIP_BODY;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getItemvalue() {
		return itemvalue;
	}

	public void setItemvalue(String itemvalue) {
		this.itemvalue = itemvalue;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getHiddenname() {
		return hiddenname;
	}

	public void setHiddenname(String hiddenname) {
		this.hiddenname = hiddenname;
	}

	public String getRownum() {
		return rownum;
	}

	public void setRownum(String rownum) {
		this.rownum = rownum;
	}
}
