package com.hjsj.hrms.utils.components.codeselector.interfaces;

import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;
import java.util.HashMap;

 public abstract class CodeDataFactory {

	
	
	protected HashMap paramsMap = new HashMap();
	/**
	 * 
	 *    
	 * @param codesetid 
	 * @param code
	 * @param userView
	 * @return
	 * 返回值格式：ArrayList(HashMap);
	 * HashMap key:
	 *    (1)id:代码codeitemid;   
	 *    (2)text：代码codeitemdesc;    
	 *    (3)leaf：是叶子节点传Boolean.TURE,不是传Boolean.FALSE
	 *    (4)qtip：onmouseover提示
	 *    ......具体格式请参考 Ext.data.NodeInterface对象的参数
	 */
	abstract public ArrayList createCodeData(String codesetid,String code,UserView userView);
	
	/**
	 * 搜索查询代码
	 * @param codesetid
	 * @param text
	 * @param userView
	 * @return ArrayList（HashMap）:
	 * 同 createCodeData()返回值
	 */
	abstract public ArrayList searchCodeByText(String codesetid,String text,UserView userView);
	
	public void setParamsMap(HashMap params){
		this.paramsMap = params;
	}
	public HashMap getParamsMap(){
		return this.paramsMap;
	}
}
