package com.hjsj.hrms.businessobject.sys.busimaintence;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;



public class Compositor {
	public  static ArrayList getBusiFieldList(String fieldsetid){
		ArrayList fieldlist=(ArrayList) ExecuteSQL.executeMyQuery("select * from t_hr_busiField where fieldsetid='"+fieldsetid+"' order by displayid");
		return fieldlist;
	}
	public static void compositfield(ContentDAO dao,RecordVo fieldvo,String flag) throws SQLException{
		/*
		 * flag=0 del =1 insert
		 */
		
		String fieldsetid=fieldvo.getString("fieldsetid");
		String itemid=fieldvo.getString("itemid");
		int dis=fieldvo.getInt("displayid");
		ArrayList fieldlist=getBusiFieldList(fieldsetid);
		for(int i=1;i<=fieldlist.size();i++){
			DynaBean dynabean=(DynaBean) fieldlist.get(i-1);
			String field=(String) dynabean.get("fieldsetid");
			String item=(String)dynabean.get("itemid");
			String displayid=(String)dynabean.get("displayid");
			int tempdis=(new Integer(displayid)).intValue();
			if(fieldsetid.equalsIgnoreCase(field)&&itemid.equalsIgnoreCase(item)||tempdis>=dis){				
				
				if("0".equals(flag)){
//					del
					dao.update("update t_hr_busiField set displayid="+(tempdis-1)+" where fieldsetid='"+field+"' and itemid='"+item+"'");
				}else{
//					insert
					if(fieldsetid.equalsIgnoreCase(field)&&itemid.equalsIgnoreCase(item)){
					}else{
						int disid = tempdis+1; //disid 新序号  tempdis 原序号
						dao.update("update t_hr_busiField set displayid="+disid+" where fieldsetid='"+field+"' and itemid='"+item+"'");
					}
					
				}
			}
		}		
	}
}
