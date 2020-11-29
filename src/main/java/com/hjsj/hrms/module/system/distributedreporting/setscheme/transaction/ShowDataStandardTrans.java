package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.SetupSchemeBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @version: 1.0
 * @Description: 用于定义数据标准的回写
 * @author: zhiyh  
 * @date: 2019年3月12日 下午1:35:49
 */
public class ShowDataStandardTrans extends IBusiness  {
	@Override
	public void execute() throws GeneralException {
		try {
			SetupSchemeBo bo = new SetupSchemeBo(this.userView, this.frameconn);
			String Str_value=bo.getStr_Value();
			Document document = null;
			if (null!=Str_value&&!"".equals(Str_value)) {
				document = bo.getDocument(Str_value);
				 // 4.通过document对象获取xml文件的根节点
		        Element rootElement = document.getRootElement();
		        // 5.获取根节点下的子节点的List集合
		        List<Element> bodyList = rootElement.getChildren();
		        Element peopleelement=bodyList.get(3);
		        String peopleCheckbox=peopleelement.getAttributeValue("checkbox");
				if ("null".equals(peopleCheckbox)||null==peopleCheckbox||"".equals(peopleCheckbox)) {
					peopleCheckbox="false";
				}
				List<Element> list2=peopleelement.getChildren();
				Element peopledb = list2.get(0);
				String peopleDbpre=peopledb.getAttributeValue("pre");
				if ("null".equals(peopleDbpre)||null==peopleDbpre||"".equals(peopleDbpre)) {
					peopleDbpre="undefined";
				}
				String peopleDbname="undefined";
				if (!"undefined".equalsIgnoreCase(peopleDbpre)) {
					peopleDbname=bo.getDbname(peopleDbpre);
				}
				Element peopleConditionelement = list2.get(1);
				String peopleCondition=peopleConditionelement.getAttributeValue("condition");
				if ("null".equals(peopleCondition)||null==peopleCondition||"".equals(peopleCondition)) {
					peopleCondition="undefined";
				}
				Element fieldelement=bodyList.get(4);
				String fieldCheckbox=fieldelement.getAttributeValue("checkbox");
				if ("null".equals(fieldCheckbox)||null==fieldCheckbox||"".equals(fieldCheckbox)) {
					fieldCheckbox="false";
				}
				List<Element> list3=fieldelement.getChildren();
				Element fieldpreelement=list3.get(0);
				String fieldpre=fieldpreelement.getAttributeValue("pre");
				if ("".equals(fieldpre)||null==fieldpre||"".equals(fieldpre)) {
					fieldpre="undefined";
				}
				String fieldDbname="undefined";
				if (!"undefined".equalsIgnoreCase(fieldpre)) {
					fieldDbname=bo.getDbname(fieldpre);
				}
				Element fieldConditionOneelement = list3.get(1);
				String protectPeopleFieldOne = fieldConditionOneelement.getAttributeValue("condition");
				if ("null".equals(protectPeopleFieldOne)||null==protectPeopleFieldOne||"".equals(protectPeopleFieldOne)) {
					protectPeopleFieldOne="undefined";
				}
				Element fieldConditiontwoelement = list3.get(2);
				String protectPeopleFieldtwo = fieldConditiontwoelement.getAttributeValue("condition");
				if ("null".equals(protectPeopleFieldtwo)||null==protectPeopleFieldtwo||"".equals(protectPeopleFieldtwo)) {
					protectPeopleFieldtwo="undefined";
				}
				String protectPeopleFieldtwoValue="undefined";
				if (!"undefined".equals(protectPeopleFieldtwo)) {
					protectPeopleFieldtwoValue=bo.getFielddesc(protectPeopleFieldtwo);
				}
				Element photolement=bodyList.get(0);
				String photoCheckbox=photolement.getAttributeValue("reportPhoto");
				if ("null".equals(photoCheckbox)||null==photoCheckbox||"".equals(photoCheckbox)) {
					photoCheckbox="false";
				}
				String personItemid = null;
				if (bodyList.size()>=6) {
					Element fielddbnameelement=bodyList.get(5);
					List<Element> fielddbnamelist=fielddbnameelement.getChildren();
					Element personItemidElement = fielddbnamelist.get(0);
					personItemid = personItemidElement.getValue();
				}
				this.getFormHM().put("personItemid", personItemid);
				this.getFormHM().put("protectPeopleFieldOne", protectPeopleFieldOne);
				this.getFormHM().put("protectPeopleFieldtwo", protectPeopleFieldtwo);
				this.getFormHM().put("protectPeopleFieldtwoValue", protectPeopleFieldtwoValue);
				this.getFormHM().put("fieldDbname", fieldDbname);
				this.getFormHM().put("fieldpre", fieldpre);
				this.getFormHM().put("peopleCondition", peopleCondition);
				this.getFormHM().put("peopleDbpre", peopleDbpre);
				this.getFormHM().put("peopleDbname", peopleDbname);
				this.getFormHM().put("photoCheckbox", photoCheckbox);
				this.getFormHM().put("peopleCheckbox", peopleCheckbox);
				this.getFormHM().put("fieldCheckbox", fieldCheckbox);
			}
			//获得人员库
			Map dbmap = new HashMap();
			ArrayList list= new ArrayList();
			DbNameBo dd=new DbNameBo(this.frameconn);
			ArrayList dblist=dd.getAllDbNameVoList(this.userView);
			LazyDynaBean abean=null;
			for(int i=0;i<dblist.size();i++) {
				RecordVo vo=(RecordVo)dblist.get(i);
				String dbpre=vo.getString("pre");
				String dbname=vo.getString("dbname");
				abean=new LazyDynaBean();
				abean.set("pre",dbpre);
				abean.set("dbname",dbname);
				list.add(abean);
			}
			dbmap.put("list",list);
			this.getFormHM().put("dbmap", dbmap);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
