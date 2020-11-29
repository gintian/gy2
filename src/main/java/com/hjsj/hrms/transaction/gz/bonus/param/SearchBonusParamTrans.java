package com.hjsj.hrms.transaction.gz.bonus.param;

import com.hjsj.hrms.businessobject.ht.ContractBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:SearchBonusParamTrans.java
 * </p>
 * <p>
 * Description:奖金参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-02 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SearchBonusParamTrans extends IBusiness
{
    public void execute() throws GeneralException
    {

	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String menuid = (String) hm.get("menuid");
	hm.remove("menuid");
	this.getFormHM().put("menuid", menuid);

	ContractBo bo = new ContractBo(this.frameconn, this.userView);
	ConstantXml xml = new ConstantXml(this.frameconn, "GZ_PARAM", "Params");
	if (menuid != null && "1".equals(menuid))
	{
	    // 人员库
	    String nbaseStr = xml.getTextValue("/Params/Bonus/base");
	    String[] nbaseArray = nbaseStr.split(",");
	    HashMap nbaseMap = new HashMap();
	    for (int i = 0; i < nbaseArray.length; i++)
	    {
		if (nbaseArray[i].trim().length() > 0)
		    nbaseMap.put(nbaseArray[i].toLowerCase(), nbaseArray[i]);
	    }

	    ArrayList nbaseList = bo.searchNbase(nbaseMap);
	    this.getFormHM().put("nbase", nbaseList);
	    // 工号
	    String jobnum = xml.getTextValue("/Params/Bonus/num");
	    this.getFormHM().put("jobnum", jobnum);
	    ArrayList fieldlist = this.userView.getPrivFieldList("A01", Constant.USED_FIELD_SET);
	    ArrayList jobnumList = new ArrayList();
	    CommonData noItem = new CommonData("", "");
	    jobnumList.add(noItem);
	    for (int i = 0; i < fieldlist.size(); i++)
	    {
		FieldItem item = (FieldItem) fieldlist.get(i);
		String itemtype = item.getItemtype();
		String codesetid = item.getCodesetid();
		String itemid = item.getItemid();
		String itemdesc = item.getItemdesc();
		if ("A".equalsIgnoreCase(itemtype) && ("0".equals(codesetid) || "".equals(codesetid)))
		{
		    CommonData temp = new CommonData(itemid.toUpperCase(), itemdesc);
		    jobnumList.add(temp);
		}
	    }
	    this.getFormHM().put("jobnumList", jobnumList);
	    // 奖金子集
	    String bonusSet = xml.getTextValue("/Params/Bonus/setid");
	    this.getFormHM().put("bonusSet", bonusSet);
	    // 人员信息集已构库的子集
	    // 奖金子集
	    ArrayList bonusSetList = new ArrayList();
	    bonusSetList.add(noItem);
	    ArrayList list = this.userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
	    for (int i = 0; i < list.size(); i++)
	    {
		FieldSet fieldset = (FieldSet) list.get(i);

		if ("0".equalsIgnoreCase(fieldset.getUseflag()))
		    continue;
		if ("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
		    continue;
		if ("A01".equalsIgnoreCase(fieldset.getFieldsetid()))
		    continue;
		ArrayList checklist = this.userView.getPrivFieldList(fieldset.getFieldsetid(), Constant.USED_FIELD_SET);
		if (checklist.size() < 1)
		    continue;

		String fieldsetid = fieldset.getFieldsetid();
		String fieldsetDesc = fieldset.getCustomdesc();
		ArrayList fieldList = DataDictionary.getFieldList(fieldsetid, Constant.USED_FIELD_SET);
		HashMap map = new HashMap();
		// map.put("51", "0" );
		// map.put("49", "0" );
		// map.put("50", "0" );
		// map.put("45", "0" );
		// map.put("金额", "0" );
		// map.put("业务日期", "0" );

		for (int j = 0; j < fieldList.size(); j++)
		{
		    FieldItem fieldItem = (FieldItem) fieldList.get(j);
		    String itemid = fieldItem.getItemid();
		    String itemName = fieldItem.getItemdesc();
		    String itemType = fieldItem.getItemtype();
		    String codesetId = fieldItem.getCodesetid();
		    if ("0".equals(codesetId) && ("N".equalsIgnoreCase(itemType) || "D".equalsIgnoreCase(itemType)))
			map.put(itemType, "");
		    else
			map.put(codesetId, "");
		}
		if (map.size() > 0 && map.get("51") != null && map.get("49") != null && map.get("50") != null && map.get("45") != null && map.get("N") != null && map.get("D") != null)
		{
		    CommonData temp = new CommonData(fieldset.getFieldsetid(), fieldset.getCustomdesc());
		    bonusSetList.add(temp);
		}
	    }
	    this.getFormHM().put("bonusSetList", bonusSetList);
	} else if (menuid != null)
	{
	    String sql = "select * from codeitem where codesetid='";
	    if ("2".equals(menuid))
		sql += "49'";
	    else if ("3".equals(menuid))
		sql += "50'";

	    ContentDAO dao = new ContentDAO(this.frameconn);
	    ArrayList datalist = new ArrayList();
//	  没有对应的代码项，则第一个长度为任意长度，只要小于30就可以 ;建第二个代码项时，必须和上次建的等长
	    String codeLen = "30";
	    try
	    {
		RowSet rs = dao.search(sql);
		while (rs.next())
		{
		    String codeitemid = rs.getString("codeitemid");
		    String codeitemdesc = rs.getString("codeitemdesc");
		    String codesetid = rs.getString("codesetid");

		    LazyDynaBean abean = new LazyDynaBean();
		    abean.set("codeitemid", codeitemid);
		    abean.set("codeitemdesc", codeitemdesc);
		    abean.set("codesetid", codesetid);
		    datalist.add(abean);
		    codeLen=Integer.toString(codeitemid.length());
		}
	    } catch (Exception e)
	    {
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	    }
	    this.getFormHM().put("codeDataList", datalist);
	    this.getFormHM().put("codeLen", codeLen);
	}
    }
}
