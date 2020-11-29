package com.hjsj.hrms.utils.components.codeselector;

import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 印章管理用户类型下拉显示
 * @author hej
 *
 */
public class GetSignatureSelectTree extends CodeDataFactory {
	
	public ArrayList createCodeData(String codesetid, String code, UserView userView) {

        ArrayList myselect = new ArrayList();
        HashMap codeobj = new HashMap();
        codeobj.put("id", 1);
        codeobj.put("text", "业务用户");
        codeobj.put("codesetid", codesetid);
        codeobj.put("leaf", Boolean.TRUE);
        //codeobj.put("checked", false);
        myselect.add(codeobj);
        codeobj = new HashMap();
        codeobj.put("id", 2);
        codeobj.put("text", "自助用户");
        codeobj.put("codesetid", codesetid);
        codeobj.put("leaf", Boolean.TRUE);
       // codeobj.put("checked", false);
        myselect.add(codeobj);
        return myselect;
    }
    
    /**
     * 查询
     */
    public ArrayList searchCodeByText(String codesetid, String text, UserView userView) {

    	ArrayList myselect = new ArrayList();
        HashMap codeobj = new HashMap();
        String business = "业务用户";
        String self = "自助用户";
        String id="";
        String textTemp = "";
        if(business.indexOf(text) >= 0) {
        	textTemp = business;
        	id="1";
        }else if(self.indexOf(text) >= 0) {
        	textTemp = self;
        	id="2";
        }
        if(!"".equals(textTemp)){
        	codeobj.put("id", id);
        	codeobj.put("text", textTemp);
        	codeobj.put("leaf", Boolean.TRUE);
        	codeobj.put("codesetid", codesetid);
        	myselect.add(codeobj);
        }
        return myselect;
    }
}
