package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CheckLawBaseFileByCharacter extends IBusiness {


	public void execute() throws GeneralException {
		String type=(String) this.getFormHM().get("type");
		if (StringUtils.isNotBlank(type)) {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String selsql = "select distinct(content_type) content_type from law_base_file where base_id in ( select base_id from law_base_struct where basetype = ? ) ";
			ArrayList contentlist = new ArrayList();
			ArrayList pList = new ArrayList();
			pList.add(this.getFormHM().get("basetype"));
			try {
				this.frowset=dao.search(selsql,pList);
				while(this.frowset.next()){
					CommonData data = new CommonData();
					String dataName=this.frowset.getString("content_type");
					if (dataName==null) {
						dataName="";
					}
					data.setDataName(dataName);
					data.setDataValue(dataName);
				   contentlist.add(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.getFormHM().put("contentlist",contentlist);
		}else {
		String userItem = "";//用户自增项
		String systemItem = "file_id,name,title,content_type,type,valid,note_num," +
				"issue_org,notes,issue_date,implement_date,valid_date,ext,base_id," +
				"content,viewcount,digest,fileorder,originalfile,originalext,b0110,keywords";//系统项
		
		String field_str_item = (String)this.getFormHM().get("field_str_item");//文档指标项
		HashMap itemHashMap = new HashMap();
		if (field_str_item != null && ! "".equals(field_str_item)) {
			String [] arry = field_str_item.split(",");
			for (int i = 0; i < arry.length; i++) {
				String item = arry[i];
				String [] arry2 = item.split("`");
				itemHashMap.put(arry2[0], arry2[1]);
			}
		}
		
		ArrayList itemList = new ArrayList();
		if (field_str_item != null && !"".equals(field_str_item)) {
			ArrayList fieldList = DataDictionary.getFieldList("LAW_BASE_FILE", Constant.USED_FIELD_SET);
			LazyDynaBean lBean;
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem fieldItem = (FieldItem)fieldList.get(i);
				String itemId = fieldItem.getItemid();
				if (systemItem.indexOf(itemId) == -1 && field_str_item.indexOf(itemId) != -1 &&
						"1".equals(fieldItem.getState())) {
					lBean = new LazyDynaBean();
					lBean.set("itemid", itemId);
					String itemdesc = (String)itemHashMap.get(fieldItem.getItemid());
					if(itemdesc != null && !"".equals(itemdesc))
						lBean.set("itemdesc",itemdesc);
					else
						lBean.set("itemdesc", fieldItem.getItemdesc());
					lBean.set("itemtype", fieldItem.getItemtype());
					lBean.set("codesetid", fieldItem.getCodesetid());
					itemList.add(lBean);
				}
			}
			
		}
	
		this.getFormHM().put("itemList", itemList);
	}
	}
}
