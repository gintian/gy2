package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>
 * Title:AddBonusInformTrans.java
 * </p>
 * <p>
 * Description:新增奖金数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-09 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class AddBonusInformTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String a_code = (String) hm.get("a_code");
	hm.remove("a_code");
	this.getFormHM().put("a_code", a_code);
	String bonusSet =(String) this.getFormHM().get("bonusSet");	// 奖金子集	
	ArrayList fieldInfoList = new ArrayList();
	
	ArrayList fieldList = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
//	fieldInfoList.add(getFieldItemView(DataDictionary.getFieldItem("a0101")));
//	fieldInfoList.add(getFieldItemView(DataDictionary.getFieldItem("b0110")));
//	fieldInfoList.add(getFieldItemView(DataDictionary.getFieldItem("e0122")));
	
	for (int i = 0; i < fieldList.size(); i++)
	{	    
	    FieldItem fielditem = (FieldItem) fieldList.get(i);
	    String itemid = fielditem.getItemid();
	    if ("0".equals(this.userView.analyseFieldPriv(itemid, 0)) && "0".equals(this.userView.analyseFieldPriv(itemid, 1)))
		continue;//隐藏字段
	    if ("1".equals(this.userView.analyseFieldPriv(itemid, 0)) && "1".equals(this.userView.analyseFieldPriv(itemid, 1)))
		fielditem.setReadonly(true);
	    if (!"2".equals(this.userView.analyseTablePriv(bonusSet)))
		fielditem.setReadonly(true);

	    if ("处理状态".equals(fielditem.getItemdesc()) || "业务日期".equals(fielditem.getItemdesc()))
		fielditem.setReadonly(true);
	    fieldInfoList.add(getFieldItemView(fielditem));
	}
	  
	FieldItem  tempfield = new FieldItem();
	tempfield.setItemid("CreateUserName");
	tempfield.setItemdesc("录入员");
	tempfield.setFieldsetid(bonusSet);
	tempfield.setCodesetid("0");  
	tempfield.setItemtype("A");
	tempfield.setItemlength(50);
	tempfield.setReadonly(true);
	fieldInfoList.add(getFieldItemView(tempfield));
	this.getFormHM().put("fieldInfoList", fieldInfoList);
	
    }
    public FieldItemView getFieldItemView(FieldItem fieldItem)
    {
	FieldItemView fieldItemView = new FieldItemView();	
	fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
	fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
	fieldItemView.setCodesetid(fieldItem.getCodesetid());
	fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
	fieldItemView.setDisplayid(fieldItem.getDisplayid());
	fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
	fieldItemView.setExplain(fieldItem.getExplain());
	fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
	fieldItemView.setItemdesc(fieldItem.getItemdesc());
	fieldItemView.setItemid(fieldItem.getItemid());
	fieldItemView.setItemlength(fieldItem.getItemlength());
	fieldItemView.setItemtype(fieldItem.getItemtype());
	fieldItemView.setModuleflag(fieldItem.getModuleflag());
	fieldItemView.setState(fieldItem.getState());
	fieldItemView.setUseflag(fieldItem.getUseflag());
	fieldItemView.setPriv_status(fieldItem.getPriv_status());
	fieldItemView.setFillable(fieldItem.isFillable());
	fieldItemView.setReadonly(fieldItem.isReadonly());
	if("CreateUserName".equalsIgnoreCase(fieldItem.getItemid()))
	{
	    String creator = this.getUserView().getUserFullName();
	    fieldItemView.setViewvalue(creator);
	    fieldItemView.setValue(creator);
		
	} else if ("业务日期".equals(fieldItem.getItemdesc()))
	{
//	    String creatDate = PubFunc.getStringDate("yyyy.MM");
	    String creatDate = PubFunc.getStringDate("yyyy-MM-dd");
	    fieldItemView.setViewvalue(creatDate);
	    fieldItemView.setValue(creatDate);
	    
	}else if("处理状态".equals(fieldItem.getItemdesc()))
	{
	    fieldItemView.setViewvalue(AdminCode.getCode("51", "0") != null ? AdminCode.getCode("51", "0").getCodename() : "");
	    fieldItemView.setValue("0");
	}
	else
	{
	    fieldItemView.setViewvalue("");
	    fieldItemView.setValue("");
	}
	return fieldItemView;
    }
}
