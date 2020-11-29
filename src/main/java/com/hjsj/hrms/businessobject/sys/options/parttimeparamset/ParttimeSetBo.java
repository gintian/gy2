package com.hjsj.hrms.businessobject.sys.options.parttimeparamset;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class ParttimeSetBo {
	private Connection conn;
	
	public ParttimeSetBo(Connection conn){
		this.conn=conn;
	}
	/**
	 * 得到兼职子集列表
	 * @return
	 */
	public ArrayList getSetList(){
		ArrayList list = new ArrayList();
		list.add(new CommonData(" ","请选择..."));
		
		
		// 查询的应该是构库后的指标名称
//		String sql = "select fieldsetid,fieldsetdesc from fieldset where fieldsetid like 'A%' and useflag ='1'";
		String sql = "select fieldsetid,customdesc from fieldset where fieldsetid like 'A%' and useflag ='1'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("fieldsetid"),rs.getString("customdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到兼职单位指标列表
	 * @param fieldSetId
	 * @return
	 */
	public ArrayList getUnitList(String fieldSetId){
		ArrayList list = new ArrayList();
		list.add(new CommonData(" ","请选择..."));
		if(fieldSetId.trim().length()==0) {
            return list;
        }
		String sql ="select itemid,itemdesc from fielditem where codesetid = 'UM' and fieldsetid='"+fieldSetId+"'";
		sql=sql+" and useflag='1'";
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 得到兼职单位职务指标列表
	 * @param fieldSetId
	 * @return
	 */
	public ArrayList getPosList(String fieldSetId){
		ArrayList list = new ArrayList();
		list.add(new CommonData(" ","请选择..."));
		if(fieldSetId.trim().length()==0) {
            return list;
        }
		StringBuffer sql =new StringBuffer();
		sql.append("select itemid,itemdesc from fielditem where codesetid <> 'UM' and codesetid <> 'UN' and codesetid <> '33'");
		sql.append("  and fieldsetid='"+fieldSetId+"' and useflag='1' and itemtype='A'");
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql.toString());
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 得到任免标识指标列表
	 * @param fieldSetId
	 * @return
	 */
	public ArrayList getAppointList(String fieldSetId){
		ArrayList list = new ArrayList();
		list.add(new CommonData(" ","请选择..."));
		if(fieldSetId.trim().length()==0) {
            return list;
        }
		String sql="select itemid,itemdesc from fielditem where codesetid='33' and fieldsetid='"+fieldSetId+"'";
		sql=sql+" and useflag='1'";
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql);
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
		
	}
	public ArrayList getCodeitemList(String fieldSetId,String codesetid){
		ArrayList list = new ArrayList();
		list.add(new CommonData(" ","请选择..."));
		if(fieldSetId.trim().length()==0) {
            return list;
        }
		StringBuffer sql =new StringBuffer();
		sql.append("select itemid,itemdesc from fielditem where codesetid = '"+codesetid+"'");
		sql.append("  and fieldsetid='"+fieldSetId+"' and useflag='1' and itemtype='A'");
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql.toString());
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList getNitemList(String fieldSetId){
		ArrayList list = new ArrayList();
		list.add(new CommonData(" ","请选择..."));
		if(fieldSetId.trim().length()==0) {
            return list;
        }
		StringBuffer sql =new StringBuffer();
		sql.append("select itemid,itemdesc from fielditem where ");
		sql.append("fieldsetid='"+fieldSetId+"' and useflag='1' and itemtype='N' and decimalwidth=0");
		ContentDAO dao =new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			rs=dao.search(sql.toString());
			while(rs.next()){
				list.add(new CommonData(rs.getString("itemid"),rs.getString("itemdesc")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}



