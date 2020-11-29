package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class SearchDomainTrans extends IBusiness implements IConstant{

	public void execute() throws GeneralException {
		//CommonData firstEmptyData = new CommonData();
		//firstEmptyData.setDataName("--所有角色--");
		//firstEmptyData.setDataValue("RL");
		//tempList.add(firstEmptyData);
		
		DomainTool tool = new DomainTool();
		Map tempMap;
		
		ArrayList tempList = new ArrayList();

		String strIsRole = (String)getFormHM().get( Key_Request_Param_IsRole );
		if( "true".equals(strIsRole)){
			// 角色集合
			tempMap = tool.getRoleMap();
			Iterator it = tempMap.keySet().iterator();
			while(it.hasNext()){
				String strKey = (String)it.next();
				CommonData cd = new CommonData();
				cd.setDataValue( strKey );
				cd.setDataName( (String)tempMap.get(strKey));
				tempList.add( cd );
			}
			getFormHM().put(Key_Role_List, tempList);
			getFormHM().remove(Key_Org_Names); //删除组织集合
			
		}else if("false".equals(strIsRole)){
			tempMap = tool.getOrgMap();
			String strOrgCode = (String)getFormHM().get(Key_Request_Param_OrgCode);
			if( strOrgCode == null || strOrgCode.trim().length() < 1 ){
				return;
			}
			StringTokenizer st = new StringTokenizer(strOrgCode.substring(0,strOrgCode.length()), ",");
			String strOrgNames = "";
			
			while( st.hasMoreElements()){
				String strCode = st.nextElement().toString().trim();
				strOrgNames = strOrgNames+ tempMap.get( strCode ) + ",";
			}
			strOrgNames = strOrgNames.substring(0,strOrgNames.length()-1);
			getFormHM().remove(Key_Role_List);
			getFormHM().put(Key_Org_Names, strOrgNames);
		}
		
		
	}

}
