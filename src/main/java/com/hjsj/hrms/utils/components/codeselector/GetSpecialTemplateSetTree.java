package com.hjsj.hrms.utils.components.codeselector;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title: GetProjectMemberRoleSelectTreeResource </p>
 * <p>Description: 人事异动中单位模板特殊字段：codesetid，只显示单位和部门</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2016-7-5 下午3:48:58</p>
 * @author lis
 * @version 1.0
 */
public class GetSpecialTemplateSetTree extends CodeDataFactory {

    public ArrayList createCodeData(String codesetid, String code, UserView userView) {

        ArrayList myselect = new ArrayList();
        HashMap codeobj = new HashMap();
        codeobj.put("id", "UN");
        codeobj.put("text", ResourceFactory.getProperty("label.codeitemid.un"));//单位
        codeobj.put("codesetid", "codesetid");
        codeobj.put("leaf", Boolean.TRUE);
        myselect.add(codeobj);
        codeobj = new HashMap();
        codeobj.put("id", "UM");
        codeobj.put("text", ResourceFactory.getProperty("label.codeitemid.um"));//部门
        codeobj.put("codesetid", "codesetid");
        codeobj.put("leaf", Boolean.TRUE);
        myselect.add(codeobj);
        return myselect;
    }
    
    /**
     * 查询
     */
    public ArrayList searchCodeByText(String codesetid, String text, UserView userView) {

    	ArrayList myselect = new ArrayList();
        HashMap codeobj = new HashMap();
        String un = ResourceFactory.getProperty("label.codeitemid.un");//单位
        String um = ResourceFactory.getProperty("label.codeitemid.um");//部门
        String textTemp = "";
        if(un.indexOf(text) >= 0)
        	textTemp = un;
        else if(um.indexOf(text) >= 0)
        	textTemp = um;
        if(!"".equals(textTemp)){
        	codeobj.put("id", codesetid);
        	codeobj.put("text", textTemp);
        	codeobj.put("leaf", Boolean.TRUE);
        	codeobj.put("codesetid", codesetid);
        	myselect.add(codeobj);
        }
        return myselect;
    }
}
