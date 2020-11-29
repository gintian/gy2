package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddorEditCodeitemTrans extends IBusiness {
	private CodeItem code=new CodeItem();
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
//		AdminCode.addCodeItem(new CodeItem());
		if (reqhm != null) {
			RecordVo codeitemvo = new RecordVo("codeitem");
			if (reqhm.containsKey("codesetid")) {
				// 修改codeitem
//				code=AdminCode.getCode("AB","110101");
				String control = (String) (reqhm.get("codesetid") != null ? reqhm
						.get("codesetid")
						: "");
				String[] itemorsetid = control.split("/");
				String codesetid = itemorsetid[1];
				String codeitemid = itemorsetid[0];

				codeitemvo.setString("codeitemid", codeitemid);
				codeitemvo.setString("codesetid", codesetid);
				try {
					codeitemvo = dao.findByPrimaryKey(codeitemvo);
					reqhm.remove("codeitemid");
					reqhm.remove("codesetid");
					this.getFormHM().put("codeitemvo", codeitemvo);
					this.getFormHM().put("flag", "1");
					this.getFormHM().put("vflag", "1");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("codemaintence.codefail"),"",""));
				} 
			}
			if (reqhm.containsKey("query")) {
				// 增加codeitem
				String len = null;
				codeitemvo.clearValues();
				String control = (String) reqhm.get("control");
				reqhm.remove("control");
				String[] temp = getControl(control);
				if (temp.length == 1) {
					codeitemvo.setString("codesetid", temp[0]);
					len = this.getChildLen(temp[0]);
				} else {
					codeitemvo.setString("codesetid", temp[1]);
					codeitemvo.setString("parentid", temp[0]);
					len = this.getChildLen(temp[1], temp[0]);
				}

				this.getFormHM().put("len", len);

				reqhm.remove("query");
				this.getFormHM().put("vflag", "0");
				this.getFormHM().put("codeitemvo", codeitemvo);
			}
		} else {
			String excep="";
			DynaBean codeitembean = (LazyDynaBean) this.getFormHM().get(
					"codeitemvo");
			/*
			 * flag =0增加codeitem =1修改codeitem
			 */
			String flag = (String) this.getFormHM().get("flag");
			if (flag != null && "0".equals(flag)) {
				try {
					if(this.judgeCodesetdesc((String) codeitembean.get("codeitemdesc"))){
						excep="<"+(String) codeitembean.get("codeitemdesc")+">代码描述已存在，添加代码失败！";
						Exception ex=new Exception();
						throw GeneralExceptionHandler.Handle(ex);
					}
					dao.addValueObject(this.getRecordVo(codeitembean));
//					增加数字字典
					AdminCode.addCodeItem(this.getCode(codeitembean));
//					修改相关内容
					this.relativeUpate((String)codeitembean.get("codesetid"),(String)codeitembean.get("codeitemid"));
					updateCodeitem(dao, codeitembean);
				} catch (Exception e) {
					if(excep.length()<1){
						excep="<"+(String) codeitembean.get("codeitemid")+">"+ResourceFactory.getProperty("codemaintence.codeitem.exist");
					}
					e=new Exception(excep);
					throw GeneralExceptionHandler.Handle(e);

				}
			}
			if (flag != null && "1".equals(flag)) {
				try {
					if(this.judgeCodesetdesc((String) codeitembean.get("codeitemdesc"))){
						excep="<"+(String) codeitembean.get("codeitemdesc")+">代码描述已存在，修改代码失败！";
						Exception ex=new Exception();
						throw GeneralExceptionHandler.Handle(ex);
					}
					dao.updateValueObject(this.getRecordVo(codeitembean));
//					修改数字字典
					AdminCode.updateCodeItemDesc((String)codeitembean.get("codesetid"),(String)codeitembean.get("codeitemid"),(String)codeitembean.get("codeitemdesc"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(excep.length()<1){
						excep=ResourceFactory.getProperty("codemaintence.codeitem.update.fail");
					}
					throw GeneralExceptionHandler.Handle(new GeneralException("",excep,"",""));
//					throw GeneralExceptionHandler.Handle(e);
				}
			}
		}

	}
	public boolean judgeCodesetdesc(String codesetdesc){
		ArrayList codeitemlist=AdminCode.getCodeItemList();
		for(int i=0;i<codeitemlist.size();i++){
			CodeItem ci=(CodeItem) codeitemlist.get(i);
			if(codesetdesc.equals(ci.getCodename())){
				return true;
			}
		}
		
		return false;
	}
	public RecordVo getRecordVo(DynaBean codeitembean) {
		RecordVo codeitemvo = new RecordVo("codeitem");
		codeitemvo.setString("codesetid", (String) codeitembean
				.get("codesetid"));
		codeitemvo.setString("codeitemid", (String) (codeitembean
				.get("codeitemid") != null ? codeitembean.get("codeitemid")
				: ""));
		codeitemvo.setString("codeitemdesc", (String) (codeitembean
				.get("codeitemdesc") != null ? codeitembean.get("codeitemdesc")
				: ""));
		codeitemvo.setString("parentid",
				(String) (codeitembean.get("parentid") != null ? codeitembean
						.get("parentid") : ""));
		codeitemvo.setString("childid",
				(String) (codeitembean.get("childid") != null ? codeitembean
						.get("childid") : ""));
		return codeitemvo;
	}

	public RecordVo getCodesetVo(ContentDAO dao, String codesetid) throws GeneralException {
		RecordVo codesetvo = new RecordVo("codeset");
		codesetvo.setString("codesetid", codesetid);
		try {
			codesetvo = dao.findByPrimaryKey(codesetvo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("codemaintence.codeset.unpresent"),"",""));
		} 
		return codesetvo;
	}

	public String[] getControl(String control) {
		String[] temp = control.split("/");
		return temp;
	}

	public void updateCodeitem(ContentDAO dao, DynaBean codeitembean) throws GeneralException {
		RecordVo codeitemvo = new RecordVo("codeitem");
		codeitemvo.setString("codesetid", (String) codeitembean
				.get("codesetid"));
		codeitemvo.setString("codeitemid", (String) codeitembean
				.get("parentid"));
		try {
			codeitemvo = dao.findByPrimaryKey(codeitemvo);
			if (codeitemvo.getString("codeitemid").equals(
					codeitemvo.getString("childid"))) {
				codeitemvo.setString("childid", (String) codeitembean
						.get("codeitemid"));
				dao.updateValueObject(codeitemvo);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
	}

	public String getChildLen(String codesetid, String codeitemid) throws GeneralException {
		RecordVo codeitemvo = new RecordVo("codeitem");
		String len = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		codeitemvo.setString("codesetid", codesetid);
		codeitemvo.setString("codeitemid", codeitemid);
		try {
			codeitemvo = dao.findByPrimaryKey(codeitemvo);
			if (codeitemvo.getString("codeitemid").equalsIgnoreCase(
					codeitemvo.getString("childid"))) {
				len = null;

			} else {
				Integer l = new Integer(codeitemvo.getString("childid")
						.length()-codeitemid.length());
				len = l.toString();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		return len;
	}

	public String getChildLen(String codesetid) throws GeneralException {
		String len = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			RowSet rs = dao
					.search("select codeitemid from codeitem where codeitemid=parentid and codesetid='"
							+ codesetid + "'");
			if (rs.next()) {
				int is = rs.getString("codeitemid").length();
				len = new Integer(is).toString();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return len;
	}

	public CodeItem getCode(DynaBean codeitembean) {
		code.setCcodeitem((String) codeitembean.get("childid")!=null?(String) codeitembean.get("childid"):"");
		code.setCodeid((String) codeitembean.get("codesetid")!=null?(String)codeitembean.get("codesetid"):"");
		code.setCodeitem((String) codeitembean.get("codeitemid")!=null?(String)codeitembean.get("codeitemid"):"");
		code.setCodename((String) codeitembean.get("codeitemdesc")!=null?(String)codeitembean.get("codeitemdesc"):"");
		code.setPcodeitem((String) codeitembean.get("parentid")!=null?(String)codeitembean.get("parentid"):"");
		return code;
	}
    public void relativeUpate(String codesetid,String addcodeitemid) throws GeneralException{
    	ArrayList fieldlist =new ArrayList();
    	ContentDAO dao =new ContentDAO(this.getFrameconn());
    	String sqlquery="select pre,fieldsetid,itemlength ,useflag,itemid,itemdesc from dbname,fielditem where codesetid='"+codesetid+"' ";
    	fieldlist=(ArrayList) ExecuteSQL.executeMyQuery(sqlquery);
    	if(fieldlist.size()>0){
    	LazyDynaBean firstbean =(LazyDynaBean) fieldlist.get(0);
    	int itemlength=0;
    	itemlength=((String)(firstbean.get("itemlength"))).length();
//    	修改fieldItem表中的itemlength
    	Connection conn = null;
    	try {
			if(itemlength<addcodeitemid.length()){
				dao.update("update fielditem set itemlength="+addcodeitemid.length()+" where codesetid='"+codesetid+"'");
				conn =AdminDb.getConnection();
				for(int j=0;j<fieldlist.size();j++){
					LazyDynaBean rtable=(LazyDynaBean) fieldlist.get(j);
					if("1".equals((String)rtable.get("useflag"))){
						Table temptable=new Table((String) rtable.get("pre")+(String)rtable.get("fieldsetid"));
						DbWizard dbwizard=new DbWizard(conn);
						Field tempfield=new Field((String) rtable.get("itemid"),(String)rtable.get("itemdesc"));
						tempfield.setDatatype(DataType.STRING);
						tempfield.setVisible(false);
						tempfield.setNullable(false);
						tempfield.setKeyable(true);
						tempfield.setSortable(false);	
						tempfield.setLength(addcodeitemid.length());
						temptable.addField(tempfield);
						dbwizard.alterColumns(temptable);
						dictionaryUpdate((String) rtable.get("itemid"),itemlength);
					}
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(conn!=null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
    	}
//    	修改相关表的定义
 
    }
    public void dictionaryUpdate(String itemid,int itemlength){
    	FieldItem fielditem=DataDictionary.getFieldItem(itemid);
    	fielditem.setItemlength(itemlength);
    }

}
