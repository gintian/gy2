package com.hjsj.hrms.module.recruitment.thirdpartyresume.transaction;

import com.hjsj.hrms.businessobject.hire.EmployResumeBo;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeBase;
import com.hjsj.hrms.module.recruitment.thirdpartyresume.base.ThirdPartyResumeSourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @ClassName: ItemCorrespondTrans
 * @Description: TODO指标对应
 * @author zhangcq
 * @date 2016-6-8 
 * 
 */
public class ItemCorrespondTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		
		//HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		HashMap hm = this.getFormHM();
		 String thirdpartName = (String) hm.get("name");
		String resumeset = "";
		String fieldSet = "";
		EmployResumeBo bo = new EmployResumeBo(this.getFrameconn());
		ArrayList itemIDList = new ArrayList();
		LazyDynaBean bean = new LazyDynaBean();
		ArrayList<LazyDynaBean> contentList = new ArrayList<LazyDynaBean>();
		ArrayList<CommonData> itemList = new ArrayList<CommonData>();
		ThirdPartyResumeBase base = ThirdPartyResumeSourceFactory.getThirdPartyResumeBo(this.frameconn, thirdpartName, this.userView);
		resumeset = (String) hm.get("resumeset");
		resumeset = SafeCode.decode(resumeset);
		fieldSet = (String) hm.get("fieldset");
		try {
		
			HashMap param = base.getResumeParam();
			HashMap<String, ArrayList<LazyDynaBean>> resumeXmlItem = (HashMap<String, ArrayList<LazyDynaBean>>) param.get("fielditem");
			ArrayList<LazyDynaBean> itemXmlList = resumeXmlItem.get(resumeset);
			itemIDList = DataDictionary.getFieldList(fieldSet, Constant.USED_FIELD_SET);
			///itemList.add(new CommonData("", "请选择..."));
			StringBuffer	itemNewList = new StringBuffer();	
			itemNewList.append("Ext.create('Ext.data.Store', { fields: ['id', 'name'],");
			itemNewList.append("data : [{'id':'#','name':'请选择'},");
			if (itemIDList != null) {
				for (int i = 0; i < itemIDList.size(); i++) {
					FieldItem set = (FieldItem) itemIDList.get(i);
					
					itemNewList.append("{'id':'"+set.getItemid().toUpperCase()+"', 'name':'"+set.getItemid().toUpperCase() + ":" + set.getItemdesc()+"'},");
					
					CommonData cd = new CommonData();
					cd.setDataName(set.getItemid().toUpperCase() + ":" + set.getItemdesc());
					cd.setDataValue(set.getItemid().toUpperCase());
					itemList.add(cd);
				}
			}
			 if(itemNewList.toString().endsWith(","))
				 itemNewList.setLength(itemNewList.length() - 1);
			itemNewList.append("]})");
			StringBuffer jsonInfo = new StringBuffer("[");
			for (int i = 0; i < itemXmlList.size(); i++) {
				bean = (LazyDynaBean) itemXmlList.get(i);
				if (bean.get("resumefld") != null) {
					String ehrset = "" ;
					String resumeset1 = (String) bean.get("resumefld");
					String ehrfld = (String) bean.get("ehrfld");
				
					LazyDynaBean contentBean = new LazyDynaBean();
					contentBean.set("resumefld", bean.get("resumefld"));//简历指标集
					contentBean.set("ehrfld", bean.get("ehrfld"));
					contentBean.set("resumefldid", bean.get("resumefldid"));//简历指标
					contentBean.set("itemList", itemList);
					contentBean.set("iselected", (String) bean.get("ehrfld"));//选中的人员库指标
					contentBean.set("resumeset",resumeset);//人员库指标集
					contentList.add(contentBean);
					if(StringUtils.isNotEmpty(ehrfld)) {
						FieldItem item=DataDictionary.getFieldItem(ehrfld);
						if(item != null) {
						    String itemName =item.getItemdesc();//指标名称
						    ehrfld = ehrfld + "`" + ehrfld + ":" + itemName;
						} else {
						    ehrfld = "";
						}
					}
					jsonInfo.append("{resumeItems:'"+resumeset1+"',userItems:'"+ehrfld+"'},");
				}
			}
			 if(jsonInfo.toString().endsWith(","))
		   			jsonInfo.setLength(jsonInfo.length() - 1);
			jsonInfo.append("]");
			//System.out.println(jsonInfo);
			StringBuffer rzColumn = new StringBuffer("[");
			rzColumn.append("{text:'简历指标',width:200,locked:false,align:'center',dataIndex:'resumeItems'},");
			rzColumn.append("{text:'人员库指标',width:200,align:'center',dataIndex:'userItems',renderer :thirdPartyRensumeItem.changeItemCombo, " +
					"editor:new EHR.extWidget.field.CodeSelectField({typeAhead: true," +
					"triggerAction: 'all',style :'',autoLoad : true,mode: 'local',id : 'itemComData',store: "+itemNewList+",displayField: 'name',valueField: 'id'})}");
			rzColumn.append("]");
			this.getFormHM().put("rzColumn", rzColumn.toString());
			this.getFormHM().put("itemlist", contentList);
			this.getFormHM().put("resumeset", resumeset);	
			this.getFormHM().put("ilist", itemList);	
			this.getFormHM().put("fieldset", fieldSet);
			this.getFormHM().put("rzValue", jsonInfo.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
