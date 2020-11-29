package com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.definition;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class BudgetDefBo {
	private Connection conn = null;
	private UserView userView = null;
	public BudgetDefBo(Connection conn, UserView userView)
	{
		this.conn = conn;
		this.userView = userView;
	}
	//从constant表中得到预算表分类的数值
	public ArrayList getKindList(){
		ArrayList list = new ArrayList();
		String kindstr = "";
		try{
			RowSet rowSet = null;
			ContentDAO dao=new ContentDAO(this.conn);
			rowSet = dao.search("select str_value from Constant where Constant='GZ_BUDGET_PARAMS'");
			if(rowSet.next()){
				String str_value = Sql_switcher.readMemo(rowSet,"str_value");
				if (str_value != null && str_value.trim().length()>0){
					    Document doc = PubFunc.generateDom(str_value);
					    String xpath = "//params";
					    XPath xpath_ = XPath.newInstance(xpath);
					    Element ele = (Element) xpath_.selectSingleNode(doc);
					    Element child;
					    if (ele != null){
							child = ele.getChild("kindstr");
							if (child != null){
								kindstr = child.getTextTrim();
							}
					    }	
				}
    
			}
			if(rowSet!=null)
				rowSet.close();
			kindstr = kindstr.replaceAll("，", ",");
			String[] temp = kindstr.split(",");
			CommonData obj2=new CommonData("","");//预算表分类可以为空
			list.add(obj2);
			for(int i=0;i<temp.length;i++){
				CommonData obj=new CommonData(temp[i],temp[i]);
				list.add(obj);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	//得到代码类的list
	public ArrayList getCodesetList(){
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			CommonData obj2=new CommonData("","");//可以为空
			list.add(obj2);
			rowSet = dao.search("select codesetdesc,codesetid from codeset");
			while(rowSet.next()){
				String codesetid = rowSet.getString("codesetid");
				String codesetdesc = codesetid + " " + rowSet.getString("codesetdesc");
				CommonData obj=new CommonData(codesetid,codesetdesc);
				list.add(obj);
			}
			if(rowSet!=null)
				rowSet.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	

}
