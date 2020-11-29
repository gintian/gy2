package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hjsj.hrms.businessobject.sys.codemaintenc.FindFieldItem;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DelCodesetorItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		// HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList codesetlist=(ArrayList)this.getFormHM().get("list");
		if(codesetlist==null||codesetlist.size()==0)
            return;
		FindFieldItem ffitem = new FindFieldItem(new ContentDAO(this.getFrameconn()));
		String delflag  = "0";
		try {
			for(int i=0;i<codesetlist.size();i++){
				String codesetid = codesetlist.get(i).toString().trim();
				if (!ffitem.isrelitem(codesetid)) {
					delflag = "1";
				}
			}
			if("0".equalsIgnoreCase(delflag)){
				for(int i=0;i<codesetlist.size();i++){
					String codesetid = codesetlist.get(i).toString().trim();
					delCodeset(dao, codesetid);
					batchdelCodeitem(dao,codesetid);
					this.maintencDelRunCode(codesetid);
				}
			}
			this.getFormHM().put("delflag", delflag);
			//this.getFormHM().put("codesetlist",codesetlist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//String delflag = "";
		/*
		 * flag =0删除codeset =1删除codeitem delflag=0删除成功！ =1删除失败！
		 */
		/*if (hm.containsKey("delflag")) {
			delflag = (String) hm.get("delflag");
		}
		FindFieldItem ffitem = new FindFieldItem(new ContentDAO(this
				.getFrameconn()));
		String flag = (String) this.getFormHM().get("flag");
		if (flag.equals("0")) {
			String curruid = (String) this.getFormHM().get("curruid");
			String codesetid = curruid;
			try {
				if (ffitem.isrelitem(codesetid)) {
					delCodeset(dao, codesetid);
					batchdelCodeitem(dao,codesetid);
					this.maintencDelRunCode(codesetid);
					this.getFormHM().put("delflag", "0");
				} else {
					this.getFormHM().put("delflag", "1");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw GeneralExceptionHandler.Handle(e);
			}
		}
		if (flag.equals("1")) {
			String curruid = (String) this.getFormHM().get("curruid");
			String[] temp = curruid.split("/");
			String codeitemid = temp[0];
			String codesetid = temp[1];
			String parentid=temp[2];
			try {
				if (ffitem.isrelitem(codesetid)) {
					delCodeitem(dao, codesetid, codeitemid,parentid);
					this.maintencDelRunCode(codesetid,codeitemid);
					this.getFormHM().put("delflag", "0");
				} else {
					if (delflag.equals("0")) {
						delCodeitem(dao, codesetid, codeitemid,parentid);
						this.maintencDelRunCode(codesetid,codeitemid);
					} else {
						this.getFormHM().put("delflag", "2");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("codemaintence.delcode.fail"),"",""));
			}
		}*/
	}
	public void maintencDelRunCode(String codesetid){
		this.maintencDelRunCode(codesetid,null);
	}
	public void maintencDelRunCode(String codesetid,String codeitemid){
		List codeitemList=AdminCode.getCodeItemList(codesetid);
		if(codeitemid==null){
//			删除所有的相关代码
			for(int i=0;i<codeitemList.size();i++){
				AdminCode.removeCodeItem((CodeItem) codeitemList.get(i));
			}
			
		}else{
			for(int i=0;i<codeitemList.size();i++){
				CodeItem coit=(CodeItem) codeitemList.get(i);
				if(coit.getCodeitem().length()>=codeitemid.length()){
				String ttmp=coit.getCcodeitem().substring(0,codeitemid.length());
				if(ttmp.equals(codeitemid)){
					AdminCode.removeCodeItem(coit);
				}
				}
			}
//			
		}
		
	}
	
	/*public void delCodeitem(ContentDAO dao, String codesetid, String codeitemid,String parentid)
			throws GeneralException, SQLException {
		Connection conn=AdminDb.getConnection();
		
		String sqldel="delete from codeitem where codeitemid like '"+codeitemid+"%' and codesetid='"+codesetid+"'";
		String sqlquery="select * from codeitem where parentid='"+parentid+"' and codesetid='"+codesetid+"' and codeitemid<>parentid and codeitemid<>'"+codeitemid+"' order by codeitemid";
		ResultSet rs= stmt.executeQuery(sqlquery);
		if(rs.next()){
//			修改上层节点 childid=codeitemid
			String tempchild=rs.getString("codeitemid");
			String sqlupdate="update codeitem set childid='"+tempchild +"' where codesetid='"+
			codesetid+"' and codeitemid='"+parentid+"'";
			stmt.executeUpdate(sqlupdate);
		}else{
//			修改上层节电的childid=codeitemid
			String sqlupdate="update codeitem set childid=codeitemid where codesetid='"+
			codesetid+"' and codeitemid='"+parentid+"'";
			stmt.executeUpdate(sqlupdate);
		}
		stmt.execute(sqldel);
		stmt.close();
		conn.close();
	}*/
	public void batchdelCodeitem(ContentDAO dao,String codesetid) throws SQLException, GeneralException{
		List codelist=ExecuteSQL.executeMyQuery("select * from codeitem where codesetid='"+codesetid+"'");
		ArrayList mycodelist=new ArrayList();
		for(int i=0;i<codelist.size();i++){
			DynaBean codeitembean=(LazyDynaBean)codelist.get(i);
			RecordVo tempvo=new RecordVo("codeitem");
			tempvo.setString("codesetid",(String) codeitembean.get("codesetid"));
			tempvo.setString("codeitemid",(String) codeitembean.get("codeitemid"));
			mycodelist.add(tempvo);
		}
		dao.deleteValueObject(mycodelist);
	}
	public void delCodeset(ContentDAO dao, String codesetid)
			throws GeneralException, SQLException {
		RecordVo codesetvo = new RecordVo("codeset");
		codesetvo.setString("codesetid", codesetid);
		codesetvo = dao.findByPrimaryKey(codesetvo);
		dao.deleteValueObject(codesetvo);

	}
}
