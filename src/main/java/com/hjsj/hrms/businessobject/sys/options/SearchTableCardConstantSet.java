package com.hjsj.hrms.businessobject.sys.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchTableCardConstantSet 
{
   private UserView userView;
   private Connection conn;
   public SearchTableCardConstantSet(){}
   public SearchTableCardConstantSet(UserView userView,Connection conn)
   {
	   this.userView=userView;
	   this.conn=conn;
   }
   public SearchTableCardConstantSet(Connection conn)
   {
	  
	   this.conn=conn;
   }
   /**
	 * 判断常量表中是否有SYS_OTH_PARAM字段
	 * @return
	 */
	public boolean check(){
		boolean b = false;
		String sql="select * from constant where constant='SYS_OTH_PARAM'";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * 效验指标是否是数值型
	 * @param itemId
	 * @return
	 */
	public String getItemDesc(String itemId){
		String itemDesc = "";
		ContentDAO dao = new ContentDAO(this.conn);
		String sql="select itemdesc from  fielditem where useflag=1 and itemid='"+itemId.trim()+"'";
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				itemDesc = rs.getString("itemdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return itemDesc;
	}
	/**
	 * 指标是否行合计
	 * @param itemId
	 * @param sumItemIds
	 * @return
	 */
	public boolean checkFieldItem(String itemId,String [] sumItemIds){
		boolean bflag = false;
		if(sumItemIds == null || sumItemIds.length == 0){
			return bflag;
		}
		for(int i = 0; i< sumItemIds.length; i++){
			String itemid = sumItemIds[i];
			if(itemid.equalsIgnoreCase(itemId)){
				bflag = true;
				break;
			}
		}
		return bflag;
	}
	/**
	 * 日期过滤子集
	 * @param itemID
	 * @return
	 */
	public ArrayList getDateSelectSetList(String itemID)
	{
		ArrayList dateitemlist = new ArrayList();//全部指标
		dateitemlist.add(new CommonData("",""));
		ContentDAO dao = new ContentDAO(this.conn);
		String sql="select * from  fielditem where useflag=1 and fieldsetid='"+itemID+"' ";
		try {
			RowSet rs = dao.search(sql);
			while(rs.next()){
				String itemtype=rs.getString("itemtype");
				if(itemtype==null||itemtype.length()<=0) {
                    itemtype="";
                }
				if("D".equals(itemtype.toUpperCase()))
				{
					String itemid = rs.getString("itemid");
					String itemdesc = rs.getString("itemdesc");
					CommonData dataobj = new CommonData(itemid, itemdesc);
					dateitemlist.add(dataobj);
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dateitemlist;
	}
	/**
	 * 判断常量表中是否有SYS_OTH_PARAM字段
	 * @return
	 */
	public boolean insert(){
		boolean b = false;
		String sql="insert into constant(constant,type,describe ) values('SYS_OTH_PARAM','A','系统参数') ";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			dao.insert(sql,new ArrayList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return b;
	}
	/**
	 * 获得指标名称
	 * @param itemid
	 * @return
	 */
	public String getChangeFlag(String itemid){
		if(itemid==null||itemid.length()<=0) {
            return "";
        }
		String changeflag = "";
		String sql = "select changeflag  from fieldset where fieldsetid = '"+itemid.trim()+"'";
		//System.out.println(sql);
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			 RowSet rs = dao.search(sql);
			if (rs.next()) {
				changeflag = rs.getString("changeflag");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return changeflag;
	}
	/**
	 * 获得指标名称
	 * @param itemid
	 * @return
	 */
	public String getFieldsetdesc(String fieldsetid){
		if(fieldsetid==null||fieldsetid.length()<=0) {
            return "";
        }
		String sql = "select customdesc from  fieldset where fieldsetid='"+fieldsetid+"'";
		//System.out.println(sql);
		ContentDAO dao = new ContentDAO(this.conn);
		String fieldsetdesc="";
		try {
			 RowSet rs = dao.search(sql);
			if (rs.next()) {
				fieldsetdesc = rs.getString("customdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fieldsetdesc;
	}
}
