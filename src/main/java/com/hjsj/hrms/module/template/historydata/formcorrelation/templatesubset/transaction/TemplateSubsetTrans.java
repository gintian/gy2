package com.hjsj.hrms.module.template.historydata.formcorrelation.templatesubset.transaction;

import com.hjsj.hrms.module.template.historydata.formcorrelation.templatesubset.businessobject.TemplateSubsetBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * 子集显示
* @Title: TemplateSubsetTrans
* @Description:
* @author: hej
* @date 2019年11月20日 下午6:35:40
* @version
 */
public class TemplateSubsetTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			String type=(String)this.getFormHM().get("type");
			if("1".equals(type)){//显示子集
				String tabid=(String)this.getFormHM().get("tabid");
				String record_id = (String)this.getFormHM().get("record_id");
				String archive_year = (String)this.getFormHM().get("archive_year");
				String archive_id = (String)this.getFormHM().get("archive_id");
				String columnName=(String)this.getFormHM().get("columnName");
				String Sub_dataXml=(String)this.getFormHM().get("data_xml");
				String nodePriv=(String)this.getFormHM().get("nodePriv");
				String rwPriv=(String)this.getFormHM().get("rwPriv");
				TemplateSubsetBo subBo=new TemplateSubsetBo(this.getFrameconn(),this.userView,tabid,columnName,archive_id);
				String showSubsetsOrder= SystemConfig.getPropertyValue("showSubsetsOrder");
				Boolean isNeedSubsetNo=false;
				if(StringUtils.isNotBlank(showSubsetsOrder)){
					String[] tabids=showSubsetsOrder.split(",");
					for(int i=0;i<tabids.length;i++){
						if(tabid.equals(tabids[i])){
							isNeedSubsetNo=true;
							break;
						}
					}
				}
				String Xml_param =subBo.getSubFieldsPropertys(nodePriv,rwPriv);
				if (StringUtils.isBlank(Sub_dataXml)){
					Sub_dataXml=subBo.getSub_dataXml(record_id,archive_year);
					Sub_dataXml=Sub_dataXml.replace("~", "～");
					Sub_dataXml=Sub_dataXml.replace("^", "＾");
				}
				else {
					if(Sub_dataXml.startsWith("<?xml")){
						Sub_dataXml=Sub_dataXml.replace("~", "～");
						Sub_dataXml=Sub_dataXml.replace("^", "＾");
					}
					Sub_dataXml= SafeCode.decode(Sub_dataXml);
				}
				HashMap subDataMap=subBo.getSubDataMap(Sub_dataXml);
				JSONObject subDatajson = JSONObject.fromObject(subDataMap); 
				FieldSet fieldSet=DataDictionary.getFieldSetVo(subBo.getSubDomain().getSetName().toUpperCase());
				String remarks=fieldSet.getExplain();
				if(StringUtils.isBlank(remarks)){
					remarks="";
				}
				this.getFormHM().put("remarks", SafeCode.encode(remarks));
				this.getFormHM().put("Xml_param", SafeCode.encode(Xml_param));
				this.getFormHM().put("subDatajson", subDatajson.toString()); 
				this.getFormHM().put("succeed", true);
				this.getFormHM().put("isNeedSubsetNo", isNeedSubsetNo);
				this.getFormHM().put("allow_del_his", subBo.getSubDomain().getAllow_del_his());
				this.getFormHM().put("record_key_id_pre", this.userView.getUserName());
				this.getFormHM().put("data_xml", "");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}