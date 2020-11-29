package com.hjsj.hrms.taglib.kq;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.util.ArrayList;
/**
 * 
 * @author xujian 2009-12-3
 *
 */
public class KqAnalysis2 extends BodyTagSupport {
	private String v1;
	private String v2;

	public int doEndTag() throws JspException {
		if (v1 == null || v1.length() <= 0 || v2 == null || v2.length() <= 0)
			return SKIP_BODY;
		try {
			ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);
			boolean flag = false;
			for (int j = 0; j < fielditemlist.size(); j++) {
				FieldItem fielditem = (FieldItem) fielditemlist.get(j);

				if (v2.equalsIgnoreCase(fielditem.getItemid())) {
					if (fielditem.getItemdesc().indexOf("次") != -1) {
						flag = true;
					}
				}
			}
			if (flag) {
				String on = v1;
				String ss = "";
				String out[] = on.split("\\.");
				for (int i = 0; i < out.length; i++) {
					if (i == 0) {
						ss = out[i];
						// System.out.println(ss);
						pageContext.getOut().println(ss);
					}
				}
			} else {
				pageContext.getOut().println(v1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	public String getV1() {
		return v1;
	}

	public void setV1(String v1) {
		this.v1 = v1;
	}

	public String getV2() {
		return v2;
	}

	public void setV2(String v2) {
		this.v2 = v2;
	}

}
