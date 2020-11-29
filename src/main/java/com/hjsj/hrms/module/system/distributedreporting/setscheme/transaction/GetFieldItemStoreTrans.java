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
 * 获取配置定义的数据规范中的上报指标
 * @author caoqy
 * @date 2019-3-20 15:11:44
 *
 */
public class GetFieldItemStoreTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList<HashMap> setlist = new ArrayList<HashMap>();
		RowSet rs = null;
		String schemexml = "";
		try {
			rs = dao.search("SELECT Str_Value FROM Constant WHERE Constant = 'BS_ASYN_PLAN_S'");
			if (rs.next()) {
				schemexml = rs.getString("Str_Value");
				Document doc = null;
				doc = PubFunc.generateDom(schemexml);
				String xpath = "/scheme/fieldItem/item";
				XPath path = XPath.newInstance(xpath);
				List fielditemslist = path.selectNodes(doc);
				HashMap tempFieldMap = null;
				for (int i = 0; i < fielditemslist.size(); i++) {
					tempFieldMap = new HashMap();
					Element tempitem = (Element) fielditemslist.get(i);
					String tempsetid = tempitem.getAttributeValue("setid");// 子集指标代码
					String tempitemid = tempitem.getAttributeValue("itemid");// 子集指标代码
					String tempdesc = tempitem.getAttributeValue("itemdesc");// 子集名称
					String tempitemtype = tempitem.getAttributeValue("itemtype");// 子集名称
					String tempcodesetid = tempitem.getAttributeValue("codesetid");// 子集名称
					if(!"A".equalsIgnoreCase(tempitemtype)||"0".equalsIgnoreCase(tempcodesetid)) {
						continue;
					}
					//把单位部门岗位指标筛选出去，不需要对应
					if("UM".equalsIgnoreCase(tempcodesetid)||"UN".equalsIgnoreCase(tempcodesetid)||"@K".equalsIgnoreCase(tempcodesetid)) {
						continue;
					}
					tempFieldMap.put("itemid", tempitemid.toUpperCase());
					tempFieldMap.put("itemdesc", tempdesc);
					tempFieldMap.put("setid", tempsetid.toUpperCase());
					tempFieldMap.put("itemiddesc", tempsetid+":"+tempdesc);
					setlist.add(tempFieldMap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}  finally {
			PubFunc.closeResource(rs);
			rs = null;
		}
		this.getFormHM().put("list", setlist);

	}

}
