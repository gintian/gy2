package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.List;

public class PieceRatePrintTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String flag =(String)this.getFormHM().get("flag");
			String s0102 = "";
			String Fs0100="";
			if("jobtable".equals(flag)){
				String s0100=(String)this.getFormHM().get("s0100");	
				Fs0100=s0100;
				RowSet rs = dao.search("select s0102 from s01 where s0100 = "+s0100+"");
				if(rs.next()){
					s0102 = rs.getString("s0102");
				}
			}else if("signtable".equals(flag)){
				s0102 = (String) this.getFormHM().get("s0102");
				if("00".equals(s0102)){
					RowSet rs1 = dao.search("select s0102 from s01 where s0100 = (select min(s0100) from s01)");
					if(rs1.next()){
						s0102 = rs1.getString("s0102");
					}else{
						s0102 = "00";
					}
				}
			}
			RowSet rowSet = dao.search("select str_value from Constant where Constant='PIECE_PAY'");
			String jobtable = "00";//00代表为空
			String code = "";
			String signtable = "00";
			if (rowSet.next()) {
				String str_value = Sql_switcher.readMemo(rowSet, "str_value");
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
									if (s0102.equals(code)) {
										jobtable = child1.getAttributeValue("jobtable");
										signtable = child1.getAttributeValue("signtable");
									}
								}

							}
						}
					}
				}

			}
			this.getFormHM().put("jobtable", jobtable);
			this.getFormHM().put("signtable", signtable);
			this.getFormHM().put("s0100", Fs0100);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
