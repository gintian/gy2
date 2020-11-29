package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;


import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 第三步设置必填指标，根据字标集显示已选指标的指标
 * @author zyh 2018-09-28
 *
 */
public class ShowRequiredFielditemTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		SetupSchemeBo bo = new SetupSchemeBo(userView, frameconn);
		try {
			String fileds=(String)this.getFormHM().get("fileds");
			String[] filedArray =fileds.split(",");
			ArrayList<JSONObject> list=new ArrayList<JSONObject>();
			String Str_value=bo.getStr_Value();
			PareXmlUtils pareXmlUtils = new PareXmlUtils(Str_value);
			for(int i=0;i<filedArray.length;i++) {
				FieldItem item =DataDictionary.getFieldItem(filedArray[i]);
				String itemid=item.getItemid();
				boolean requiredfield = false;
				boolean onlyfield = false;
				if (StringUtils.isNotEmpty(Str_value)) {
		            List arrayList = pareXmlUtils.getNodes("scheme/fieldItem/item");
		            for(int j=0;j<arrayList.size();j++) {
				    	Element elment = (Element) arrayList.get(j);
				    	String id=elment.getAttributeValue("itemid");
				    	if (id.equalsIgnoreCase(itemid)) {
				    		String require=elment.getAttributeValue("mustfill");
				    		if ("true".equalsIgnoreCase(require)) {
								requiredfield=true;
							}
				    		String only=elment.getAttributeValue("uniq");
				    		if ("true".equalsIgnoreCase(only)) {
								onlyfield=true;
							}
						}
				    }
		            HashMap<String,Object> hashMap = new HashMap<String,Object>();
		            hashMap.put("fieldsetid", item.getFieldsetid());
		            hashMap.put("fieldsetdesc",bo.getFieldsetdesc(item.getFieldsetid()));
		            hashMap.put("dataName",item.getItemdesc());
		            hashMap.put("dataValue",item.getItemid());
		            hashMap.put("requiredfield",requiredfield);
		            hashMap.put("onlyfield",onlyfield);
		            hashMap.put("itemtype",item.getItemtype());
		            hashMap.put("codesetid",item.getCodesetid());
		            JSONObject jsonObject = JSONObject.fromObject(hashMap);
		            list.add(jsonObject);
				}else {
				    HashMap<String,Object> hashMap = new HashMap<String,Object>();
                    hashMap.put("fieldsetid", item.getFieldsetid());
                    hashMap.put("fieldsetdesc",bo.getFieldsetdesc(item.getFieldsetid()));
                    hashMap.put("dataName",item.getItemdesc());
                    hashMap.put("dataValue",item.getItemid());
                    hashMap.put("requiredfield",requiredfield);
                    hashMap.put("onlyfield",onlyfield);
                    hashMap.put("itemtype",item.getItemtype());
                    hashMap.put("codesetid",item.getCodesetid());
                    JSONObject jsonObject = JSONObject.fromObject(hashMap);
                    list.add(jsonObject);
				}
			}
			JSONArray jsonArray = JSONArray.fromObject(list);
		    this.getFormHM().put("list",jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
