package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.List;

public class SetUpTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList setUplist = new ArrayList();
			ArrayList setlist = new ArrayList();
			RowSet rs = null;
			RowSet rowSet = null;
			String sql = "select codeitemid,codeitemdesc from codeitem where codesetid ='71' and invalid =1 order by codeitemid";
			this.frowset = dao.search(sql.toString());
			LazyDynaBean bean = null;

			while (this.frowset.next()) {
				String jobtable = "00";//00代表为空
				String code = "";
				String signtable = "00";
				bean = new LazyDynaBean();
				String codeitemid = this.frowset.getString("codeitemid");
				String codeitemdesc = this.frowset.getString("codeitemdesc");

				bean.set("codeitemid", codeitemid);
				bean.set("codeitemdesc", codeitemdesc);

				rowSet = dao
						.search("select str_value from Constant where Constant='PIECE_PAY'");
				if (rowSet.next()) {
					String str_value = Sql_switcher.readMemo(rowSet,
							"str_value");
					if (str_value != null && str_value.trim().length() > 0) {
						Document doc = PubFunc.generateDom(str_value);
						String xpath = "/param";
						XPath xpath_ = XPath.newInstance(xpath);
						Element ele = (Element) xpath_.selectSingleNode(doc);
						Element child;
						Element child1;
						if (ele != null) {
							child = ele.getChild("items");
							if (child != null) {
								List list = child.getChildren("item");

								if (list != null) {
									for (int i = 0; i < list.size(); i++) {
										child1 = (Element) list.get(i);
										code = child1.getAttributeValue("code");
										if (codeitemid.equals(code)) {
											jobtable = child1
													.getAttributeValue("jobtable");
											signtable = child1
													.getAttributeValue("signtable");
										}
									}

								}
							}
						}
					}

				}
				bean.set("jobtable", jobtable);
				bean.set("signtable", signtable);
				setUplist.add(bean);
				this.getFormHM().put("datalist", setUplist);
			}

			String sqll = "select id,name from t_custom_report where moduleid = 46 and report_type = 0";
			rs = dao.search(sqll.toString());
			CommonData datavo1 = new CommonData("00", "");
			setlist.add(datavo1);
			while (rs.next()) {
				CommonData datavo = new CommonData(rs.getString("id"), rs
						.getString("id")
						+ ":" + rs.getString("name"));
				setlist.add(datavo);
			}
			this.getFormHM().put("setlist", setlist);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
