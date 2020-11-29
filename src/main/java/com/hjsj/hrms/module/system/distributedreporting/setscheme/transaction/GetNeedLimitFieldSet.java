package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 指标对应功能第一步，获取需要设置过滤条件的子集
 * @author Caoqy
 * @date 2019-3-20 15:11:44
 *
 */
public class GetNeedLimitFieldSet extends IBusiness {
	ContentDAO dao = null;
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws GeneralException {
		dao = new ContentDAO(this.getFrameconn());
		ArrayList<HashMap> setlist = new ArrayList<HashMap>();
		RowSet rs = null;
		String schemexml = "";
		try {
			rs = dao.search("SELECT Str_Value FROM Constant WHERE Constant = 'BS_ASYN_PLAN_S'");
			if(rs.next()) {
				schemexml = rs.getString("Str_Value");
				Document doc = null;
				doc = PubFunc.generateDom(schemexml);
				String xpath = "/scheme/fieldSet/set";
				XPath path = XPath.newInstance(xpath);
				List fielditemslist = path.selectNodes(doc);
				HashMap tempFieldMap = null;
				for(int i = 0;i<fielditemslist.size();i++) {
					tempFieldMap = new HashMap();
					Element tempitem = (Element)fielditemslist.get(i);
					String tempsetid = tempitem.getAttributeValue("setid");//子集指标代码
					String tempdesc = tempitem.getAttributeValue("desc");//子集名称
					tempFieldMap.put("setid", tempsetid);
					tempFieldMap.put("desc", tempdesc);
					setlist.add(tempFieldMap);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(rs);
			rs=null;
		}
		this.getFormHM().put("list", setlist);
	}

}
