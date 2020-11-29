package com.hjsj.hrms.businessobject.ht.inform;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>ContracInforBo.java</p>
 * <p>Description:合同管理公共类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2009-3-13 下午03:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class ContracInforBo {
	private Connection conn;
	private ConstantXml consxml=null;
	private UserView userView;
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	public ContracInforBo(Connection conn){
		this.conn = conn;
	}
	public ContracInforBo(){
		
	}
	public ContracInforBo(Connection conn,ConstantXml consxml){
		this.conn = conn;
		this.consxml = consxml;
	}
	/**
	 * 获取合同表示代码
	 * @return ctlist
	 */
	public ArrayList ctflagList(){
		ArrayList ctlist = new ArrayList();
		String httype = this.consxml.getTextValue("/Params/httype");
		httype=httype!=null&&httype.trim().length()>0?httype:"";
		
//		// 检测用户对集子是否有权限
//		boolean flag = false;
//		if (this.userView != null) {
//			List list = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
//			for (int i = list.size(); i < list.size(); i++) {
//				FieldSet fieldset = (FieldSet) list.get(i);
//				if (fieldset.getFieldsetid().equalsIgnoreCase(httype)) {
//					flag = true;
//					break;
//				}
//			}
//			
//		}
//		if (!flag) {
//			httype = "";
//		}
		
		CommonData data =new CommonData("all","全部");
		ctlist.add(data);
		
		data =new CommonData("no","未签订");
		ctlist.add(data);
		
		if(httype.length()>0){
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from codeitem");
			sqlstr.append(" where codesetid='");
			sqlstr.append(httype);
			sqlstr.append("'");
			sqlstr.append(" union all ");
			sqlstr.append("select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from organization");
			sqlstr.append(" where codesetid='");
			sqlstr.append(httype);
			sqlstr.append("'");
			sqlstr.append(" union all ");
			sqlstr.append("select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from vorganization");
			sqlstr.append(" where codesetid='");
			sqlstr.append(httype);
			sqlstr.append("'");
			sqlstr.append(" order by CodeItemDesc");			
			try {
				RowSet rs = dao.search(sqlstr.toString());
				while(rs.next()){
					String codeitemid = rs.getString("codeitemid");
					codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
					String codeitemdesc = rs.getString("codeitemdesc");
					codeitemdesc=codeitemdesc!=null&&codeitemdesc.trim().length()>0?codeitemdesc:"";
					if(codeitemid.length()>0){
						data =new CommonData(codeitemid,codeitemdesc);
						ctlist.add(data);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			ArrayList list = AdminCode.getCodeItemList(httype);
//			for (int i = 0 ; i < list.size(); i++) {
//				CodeItem item = (CodeItem) list.get(i);
//				String codeitemid = item.getCodeitem();
//				String codeitemdesc = item.getCodename();
//				codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
//				codeitemdesc=codeitemdesc!=null&&codeitemdesc.trim().length()>0?codeitemdesc:"";
//				if(codeitemid.length()>0){
//					data =new CommonData(codeitemid,codeitemdesc);
//					ctlist.add(data);
//				}
//			}
		}
		return ctlist;
	}
	/**
	 * 获取合同表示代码
	 * @return ctlist
	 */
	public ArrayList ctflagList(String ctflag){
		ArrayList ctlist = new ArrayList();
		String httype = this.consxml.getTextValue("/Params/httype");
		httype=httype!=null&&httype.trim().length()>0?httype:"";
		CommonData data = null;
		if(httype.length()>0){
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='");
			sqlstr.append(httype);
			sqlstr.append("'");
			try {
				RowSet rs = dao.search(sqlstr.toString());
				while(rs.next()){
					String codeitemid = rs.getString("codeitemid");
					codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
//					if(codeitemid.equalsIgnoreCase(ctflag))
//						continue;
					String codeitemdesc = rs.getString("codeitemdesc");
					codeitemdesc=codeitemdesc!=null&&codeitemdesc.trim().length()>0?codeitemdesc:"";
					if(codeitemid.length()>0){
						data =new CommonData(codeitemid,codeitemdesc);
						ctlist.add(data);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ctlist;
	}
	/**
	 * 获取主合同的状态标识指标
	 * @param mfield　主合同子集id
	 * @return
	 */
	public String getHtmainFlagID(String htmain){
		String mfieldflagid = "";
		if(htmain!=null&&htmain.trim().length()>0){
			String httype = this.consxml.getTextValue("/Params/httype");
			httype=httype!=null?httype:"";
			ArrayList list=DataDictionary.getFieldList(htmain, Constant.USED_FIELD_SET);
			if(list == null || list.size() < 1)
			    return mfieldflagid;
			    
			for(int i=0;i<list.size();i++){
				FieldItem field = (FieldItem)list.get(i);
				if(field!=null&&field.getCodesetid()!=null){
					if(field.getCodesetid().equalsIgnoreCase(httype)){
						mfieldflagid = field.getItemid();
						break;
					}
				}
			}
		}
		return mfieldflagid;
	}
	/**取得i9999*/
	 public String getI9999(String table,String a0100)
	    {
		String i9999="";
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer strSql = new StringBuffer();
		strSql.append("select ");
		strSql.append(Sql_switcher.isnull("max(i9999)","0"));
		strSql.append(" from "+table);
		strSql.append(" where a0100='" );
		strSql.append(a0100);
		strSql.append("'");
		int count=1;
		try
		{
		    RowSet rs = dao.search(strSql.toString());
		    if(rs.next())
			count = rs.getInt(1)+1;
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
		i9999=new Integer(count).toString();	
		return i9999;	
	    }
}
