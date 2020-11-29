package com.hjsj.hrms.utils.components.codeselector;

import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCodeDataExample extends CodeDataFactory {

	public ArrayList createCodeData(String codesetid, String code,
			UserView userView) {
		
		/*
		 * 此类可以自定义数据，可以根据具体业务生成代码数据
		 * 
		 */
		ArrayList myselect = new ArrayList();
	    //比如此处生成一个 下拉列表：
		 for(int i=0;i<10;i++){
			 HashMap codeobj = new HashMap();
			 codeobj.put("id", new Integer(i));
			 codeobj.put("text", "下拉列表："+i);
			 codeobj.put("leaf", Boolean.TRUE);
			 myselect.add(codeobj);
		 }
		 
		return myselect;
	}

	public ArrayList searchCodeByText(String codesetid, String text,
			UserView userView) {
		ArrayList myselect = new ArrayList();
		 for(int i=0;i<10;i++){
			 HashMap codeobj = new HashMap();
			 codeobj.put("id", new Integer(i));
			 codeobj.put("text", "下拉列表："+i);
			 codeobj.put("leaf", Boolean.TRUE);
			 myselect.add(codeobj);
		 }
		 
		return myselect;
	}

	
}
