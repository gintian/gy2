package com.hjsj.hrms.module.recruitment.parameter.transaction;

import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCultureCodeItemTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			String codeSetid=(String)this.getFormHM().get("codesetid");
			String codeValue=(String)this.getFormHM().get("codeValue");
			String flag=(String)this.getFormHM().get("flag");
			String codeDesc = "";
			if(!"select".equals(flag)&&StringUtils.isNotEmpty(codeValue)&&!"#".equals(codeValue)) {
				CodeItem code = AdminCode.getCode(codeSetid, codeValue);
				if(code!=null)
					codeDesc = code.getCodename();
			}
			PositionBo bo = new PositionBo(this.frameconn, new ContentDAO(this.frameconn), this.userView);
			String level = bo.getCodeSetLayer(codeSetid);
			RecruitUtilsBo utilsBo = new RecruitUtilsBo(this.frameconn);
			utilsBo.getCodeItemMap(codeSetid, "1");
			ArrayList codeItemList = utilsBo.getCodeItemMap(codeSetid, "1");
            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map = new HashMap<String, String>();
            for(int i = 0; i<codeItemList.size();i++) {
            	CodeItem item= (CodeItem) codeItemList.get(i);
            	map = new HashMap<String, String>();
             	map.put("dataValue", item.getCodeitem());
             	map.put("dataName", item.getCodename());
             	map.put("layer", item.getCodelevel());
 				list.add(map);
            }
            this.getFormHM().put("codeSetid",codeSetid);
            this.getFormHM().put("codeDesc",codeDesc);
            this.getFormHM().put("level",level);
			this.getFormHM().put("codeList",list);
			/*ArrayList  list = new ArrayList();
			if(codesetid.equals("#"))
			{
				list.add(new CommonData("#","请选择..."));
			}
			else
			{
				ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());
				list=parameterSetBo.getCodeItem(codesetid);
			}
			//String res = JSONArray.fromObject(list).toString();
			StringBuffer listStr = new StringBuffer("");
			for (int i = 0; i < list.size(); i++) {
				CommonData cd = (CommonData) list.get(i);
				String dataName = cd.getDataName();
				String dataValue = cd.getDataValue();
				String tem = "{dataName:\""+dataName+"\",dataValue:\""+dataValue+"\"}";
				listStr.append(tem+";");
			}
			if(listStr.length()>0)
				listStr.setLength(listStr.length() - 1);
			String res = listStr.toString();
			this.getFormHM().put("itemList",list);
			this.getFormHM().put("listStr",res);*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
