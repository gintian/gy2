package com.hjsj.hrms.transaction.general.muster.struct;

import com.hjsj.hrms.businessobject.general.muster.MusterBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveSortTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tabid = (String)this.getFormHM().get("tabid");
		tabid=tabid!=null?tabid:"";
		
		String sortitem = (String)this.getFormHM().get("sortitem");
		sortitem=sortitem!=null?sortitem:"";
		
		String infor = (String)this.getFormHM().get("infor");
		infor=infor!=null?infor:"1";
		
		String dbpre = (String)this.getFormHM().get("dbpre");
		dbpre=dbpre!=null?dbpre:"Usr";
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		StringBuffer updatesql = new StringBuffer();
		updatesql.append("update lname set SortField='");
		updatesql.append(sortItemStr(sortitem));
		updatesql.append("' where Tabid='");
		updatesql.append(tabid);
		updatesql.append("'");
		String checkflag = "1";
		try {
			musterResult(dao,tabid,infor,dbpre);
			dao.update(updatesql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			checkflag = "0";
			e.printStackTrace();
		}
		this.getFormHM().put("checkflag", checkflag);
	}
	private String sortItemStr(String sortitem){
		StringBuffer sortstr = new StringBuffer();
		if(sortitem==null||sortitem.trim().length()<1)
			return "";
		String arr[] = sortitem.split("`");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				String itemArr[] = arr[i].split(":");
				if(itemArr.length==3){
					FieldItem fielditem = DataDictionary.getFieldItem(itemArr[0]);
					if(fielditem!=null){
						sortstr.append(fielditem.getFieldsetid());
						sortstr.append(".");
						sortstr.append(fielditem.getItemid());
						if("1".equals(itemArr[2]))
							sortstr.append("0");
						else
							sortstr.append("1");
							
						sortstr.append(",");
					}
				}
			}
		}
		return sortstr.toString();
	}
	private void musterResult(ContentDAO dao,String tabid,String infor,String dbpre){
		String resultset = "";
		String itemid = "";
		String itemid_code = "";
		MusterBo musterbo=new MusterBo(this.getFrameconn(),this.userView);
		String tablename = musterbo.getTableName(infor, dbpre, tabid, this.userView.getUserName().replaceAll(" ", ""));		
		if(this.userView.getStatus()==4)
		{
			resultset="t_sys_result";
			itemid="obj_id";
			int resultFlag=0;
    		if("2".equals(infor))
    			resultFlag=1;
    		if("3".equals(infor))
    			resultFlag=2;
			StringBuffer sqlstr = new StringBuffer();
			if("2".equals(infor)){
				itemid_code="B0110_CODE";
			}else if("3".equals(infor)){
				itemid_code="E01A1_CODE";
			}else{
				itemid_code="A0100";
			}
			if ("1".equals(infor)) {
				sqlstr.append("insert into " + resultset);
				sqlstr.append("(username,nbase,obj_id,flag) ");
				sqlstr.append("select '"+userView.getUserName()+"' as username,'"+dbpre+"' as nbase,"+itemid_code+" as obj_id, 0 as flag from "+tablename);
			} else if ("2".equals(infor)) {
				sqlstr.append("insert into " + resultset + " (username,obj_id,flag,nbase) ");
				sqlstr.append("select '"+userView.getUserName()+"' as username,"+itemid_code+" as obj_id,1 as flag,'B' from "+tablename);
			} else if ("3".equals(infor)) {
				sqlstr.append("insert into " + resultset+ " (username,obj_id,flag,nbase)");
				sqlstr.append("select '"+userView.getUserName()+"' as username,"+itemid_code+" as obj_id,2 as flag,'K' from "+tablename);
			}
			try {
				String str = "delete from " + resultset+" where flag="+resultFlag+" and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
    			if("1".equals(infor))
    			{
    				str+=" and UPPER(nbase)='"+dbpre.toUpperCase()+"'";
    			}
				dao.update(str);
				/*RowSet rs = dao.search("select "+itemid_code+" from "+tablename);
				ArrayList list = new ArrayList();
				while(rs.next()){
					String values = rs.getString(itemid_code);
					values=values!=null&&values.trim().length()>0?values:"";
					if(values.length()>0){
						ArrayList valuelist = new ArrayList();
						valuelist.add(values);
						list.add(valuelist);
					}
				}
				dao.batchInsert(sqlstr.toString(),list);*/
				dao.update(sqlstr.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("insert into ");
			if("2".equals(infor)){
				resultset=this.userView.getUserName().replaceAll(" ", "")+"bresult";
				itemid="B0110";
				itemid_code="B0110_CODE";
			}else if("3".equals(infor)){
				resultset=this.userView.getUserName().replaceAll(" ", "")+"kresult";	
				itemid="E01A1";
				itemid_code="E01A1_CODE";
			}else{
				resultset=this.userView.getUserName().replaceAll(" ", "")+dbpre+"result";
				itemid="A0100";
				itemid_code="A0100";
			}
			sqlstr.append(resultset);
			sqlstr.append("(");
			sqlstr.append(itemid);
			sqlstr.append(") values(?)");
			try {
				dao.update("delete from "+resultset);
				RowSet rs = dao.search("select "+itemid_code+" from "+tablename);
				ArrayList list = new ArrayList();
				while(rs.next()){
					String values = rs.getString(itemid_code);
					values=values!=null&&values.trim().length()>0?values:"";
					if(values.length()>0){
						ArrayList valuelist = new ArrayList();
						valuelist.add(values);
						list.add(valuelist);
					}
				}
				dao.batchInsert(sqlstr.toString(),list);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
}
