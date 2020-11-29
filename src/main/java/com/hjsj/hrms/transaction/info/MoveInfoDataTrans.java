/*
 * Created on 2005-7-2
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MoveInfoDataTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
    public void execute() throws GeneralException {
        String flag = (String) this.getFormHM().get("moveFlag");
        if ("1".equalsIgnoreCase(flag)) { return; }

        String userbase = (String) this.getFormHM().get("userbase");
        String touserbase = (String) this.getFormHM().get("touserbase");
        StructureExecSqlString sql = new StructureExecSqlString();
        ArrayList moveinfodata = (ArrayList) this.getFormHM().get("movedinfolist");
        this.getFormHM().remove("movedinfolist");
        this.getFormHM().put("selectedinfolist", moveinfodata);
        String movingInfo = new String();
        addGuidKey();
        StringBuffer strsql = new StringBuffer();
        List fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
        ArrayList msglist = new ArrayList();
        try {
            if (moveinfodata == null || moveinfodata.isEmpty()) {
                return;
            }
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            String checkonly = "true";
            StringBuffer fieldstr = new StringBuffer();
            DbNameBo bo = new DbNameBo(this.getFrameconn(), this.userView);
            ArrayList listvalue = new ArrayList();
            // 判断超编
            ScanFormationBo scanFormationBo = new ScanFormationBo(this.frameconn);
            if (scanFormationBo.doScan()) {
                if ((!scanFormationBo.needDoScan(userbase + ",", "All"))
                        && scanFormationBo.needDoScan(touserbase + ",", "All")) {// 从非编制库移到编制库才检查
                    ArrayList beanList = new ArrayList();
                    ArrayList nbaseA0100List = new ArrayList();
                    for (int i = 0; i < moveinfodata.size(); i++) {
                        LazyDynaBean rec = (LazyDynaBean) moveinfodata.get(i);
                        String A0100 = rec.get("a0100").toString();
                        nbaseA0100List.add(userbase + "`" + A0100 + "`" + touserbase);
                        beanList = scanFormationBo.getMoveAddPersonData(nbaseA0100List);
                    }
                    
                    scanFormationBo.execDate2TmpTable(beanList);
                    String mess = scanFormationBo.isOverstaffs();
                    if (!"ok".equals(mess)) {
                        if ("warn".equals(scanFormationBo.getMode())) {
                            msglist.add(mess);
                        } else {
                            throw GeneralExceptionHandler.Handle(new GeneralException("", mess, "", ""));
                        }
                    }
                    
                }
            }
            
            for (int i = 0; i < moveinfodata.size(); i++) {
                LazyDynaBean rec = (LazyDynaBean) moveinfodata.get(i);
                String A0100 = rec.get("a0100").toString();
                String chk = bo.checkOnlyName(A0100, userbase, touserbase);
                if (!"true".equalsIgnoreCase(chk)) {
                    // 判读提示信息是否含有一下标点，如果没有就认为提示信息结尾出没有标点
                    if (!chk.endsWith("；") && !chk.endsWith("！") && !chk.endsWith("。") && !chk.endsWith("，")
                            && !chk.endsWith(";") && !chk.endsWith("!") && !chk.endsWith(".")
                            && !chk.endsWith(",")) {
                        if ("false".equalsIgnoreCase(chk)) {
                            chk = ResourceFactory.getProperty("workbench.info.error.idcardOrUniquenessvalid");
                        } else {
                            chk += ResourceFactory.getProperty("workbench.info.move.punctuation");
                        }
                    }
                    
                    if ("true".equalsIgnoreCase(checkonly)) {
                        checkonly = ResourceFactory.getProperty("workbench.info.not.move") + "<br/>"
                                + ResourceFactory.getProperty("workbench.info.move.error") + chk;
                    } else {
                        checkonly += "<br/>" + ResourceFactory.getProperty("workbench.info.move.error") + chk;
                    }
                    
                    continue;
                }
                
            }
            
            if (!"true".equalsIgnoreCase(checkonly)) {
                throw GeneralExceptionHandler.Handle(new GeneralException("", checkonly, "", "")); 
            }
            
            for (int i = 0; i < moveinfodata.size(); i++) {
                LazyDynaBean rec = (LazyDynaBean) moveinfodata.get(i);
                String A0100 = rec.get("a0100").toString();
                String toTable = touserbase + "A01";
                
                PosparameXML pos = new PosparameXML(this.getFrameconn());
                String dbs = pos.getValue(PosparameXML.AMOUNTS, "dbs");
                String toA0100 = getToA0100(this.getFrameconn(), A0100, toTable);
                DbWizard dbw = new DbWizard(this.getFrameconn());
                cat.debug("A0100--->" + A0100);
                if (!fieldsetlist.isEmpty())
                    for (int j = 0; j < fieldsetlist.size(); j++) {
                        FieldSet fieldset = (FieldSet) fieldsetlist.get(j);
                        if (!"a00".equalsIgnoreCase(fieldset.getFieldsetid())) {
                            List fields = DataDictionary.getFieldList(fieldset.getFieldsetid(),
                                    Constant.USED_FIELD_SET);
                            fieldstr.delete(0, fieldstr.length());
                            if (!fields.isEmpty()) {
                                for (int n = 0; n < fields.size(); n++) {
                                    FieldItem fielditem = (FieldItem) fields.get(n);
                                    fieldstr.append("," + fielditem.getItemid());
                                }
                            }
                            
                            // 为主集添加人员唯一标识 wangrd 2014-05-05 主集 子集都加 附件需要guidkey
                            if (dbw.isExistField(userbase + fieldset.getFieldsetid(), "GUIDKEY", false)) {
                                if (!dbw.isExistField(touserbase + fieldset.getFieldsetid(), "GUIDKEY", false)) {
                                    Table table = new Table(touserbase + fieldset.getFieldsetid());
                                    Field field = new Field("GUIDKEY", "人员唯一标识");
                                    field.setDatatype(DataType.STRING);
                                    field.setKeyable(false);
                                    field.setLength(38);
                                    table.addField(field);
                                    dbw.addColumns(table);
                                }
                                
                                fieldstr.append(",GUIDKEY");
                            }
                            
                            strsql = sql.transferInformation(userbase + fieldset.getFieldsetid(),
                                    touserbase + fieldset.getFieldsetid(), A0100, toA0100, fieldset.getFieldsetid(),
                                    fieldstr.toString(), this.getFrameconn());
                            movingInfo = fieldset.getFieldsetdesc() + "(" + touserbase + fieldset.getFieldsetid() + ")";
                            
                            dao.update(strsql.toString());
                            strsql.setLength(0);
                            if (!this.frameconn.getAutoCommit()) {
                             // oracle 主集和子集移库 数据必须写入 提交 wangb 20170923 31704
                                this.frameconn.commit();
                            }
                        }
                    }
                
                this.getFormHM().put("ismove", "true");
                /** 移动多媒体子集,chenmengqing added 200509 后有wlh维护修改 */
                fieldstr.delete(0, fieldstr.length());
                fieldstr.append(",title,ole,flag,state,id,ext");
                strsql = sql.transferInformation(userbase + "A00", touserbase + "A00", A0100, toA0100, "A00",
                        fieldstr.toString(), this.getFrameconn());
                movingInfo = "多媒体子集(" + touserbase + "A00)";
                dao.update(strsql.toString());
                if (dbs.indexOf(userbase) == -1 && dbs.indexOf(touserbase) != -1)
                    bo.removeMainSetA0100(toTable, "", "", toA0100, msglist);
                
                ArrayList<String> list = new ArrayList<String>();
                list.add(toA0100);
                list.add(touserbase);
                list.add(A0100);
                list.add(userbase);
                listvalue.add(list);
            }
            // 移库时修改薪资历史记录表中的记录
            if (listvalue != null && listvalue.size() > 0) {
                movingInfo = "";
                bo.updateSalaryPre(listvalue);
            }
            
            if (msglist.size() > 0) {
                StringBuffer msg = new StringBuffer();
                for (int i = 0; i < msglist.size(); i++) {
                    if (msglist.size() > 1) {
                        msg.append((i + 1) + ":" + msglist.get(i) + "\\n");
                    } else {
                        msg.append(msglist.get(i));
                    }
                }
                this.getFormHM().put("msg", msg.toString());
            } else
                this.getFormHM().put("msg", "");
            
            InfoUtils infoutils = new InfoUtils();
            String a0101s = infoutils.deletePersonInfo(this.frameconn, userbase, touserbase, moveinfodata, this.userView);
            this.getFormHM().put("@eventlog", a0101s);
        } catch (Exception e) {
            String s = e.getMessage();
            String sEx = "";
            if ("".equals(movingInfo)) {
                if (s == null || s.length() == 0) {
                    sEx = ((GeneralException) e).getErrorDescription();
                } else {
                    sEx = s;
                }

            } else {
                sEx = "复制数据到“" + movingInfo + "”时出现错误！" + s;
            }
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception(sEx));
        }
    }
	
	private void addGuidKey() {
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search("select pre from dbname");
			DbWizard dbw = new DbWizard(this.frameconn);
			while (this.frowset.next()) {
				String touserbase = this.frowset.getString("pre");
					if (!dbw.isExistField(touserbase + "A01", "GUIDKEY", false)) {
						Table table = new Table(touserbase + "A01");
						Field field = new Field("GUIDKEY","人员唯一标识");
						field.setDatatype(DataType.STRING);
						field.setKeyable(false);
						field.setLength(38);
						table.addField(field);
						dbw.addColumns(table);
					
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	/**
	 * @param stmt
	 * @param A0100
	 * @param toTable
	 * @return
	 * @throws SQLException
	 */
	//获得移库的目标id号
	private synchronized String getToA0100(Connection conn, String A0100, String toTable) throws SQLException {
		String toA0100="";
		String tempNumber;
		String tempsql =
			"select A0100 from "
				+ toTable
				+ " where A0100='"
				+ A0100
				+ "'";
		ContentDAO dao = new ContentDAO(conn);
		ResultSet rs = null;
		ResultSet idRs = null;
		try{
		    rs = dao.search(tempsql);
            if (rs.next()) {
            	String strsql = "select max(A0100) as a0100 from " + toTable + " order by A0100";
            	
            idRs=dao.search(strsql);
            
            int userPlace;
            if (idRs.next()) {
            	userPlace =Integer.parseInt(idRs.getString("a0100")) + 1;
            } else
            	userPlace = 1;
            
            tempNumber = Integer.toString(userPlace);
            for (int n = 0; n < 8 - (Integer.toString(userPlace)).length(); n++)
            	tempNumber = "0" + tempNumber;
            }	
            else {
            	tempNumber = A0100;
            }
            toA0100=tempNumber;
	    }catch(Exception e)
		{
      		e.printStackTrace();
      	}finally{
      	    com.hjsj.hrms.utils.PubFunc.closeResource(rs);
      	    com.hjsj.hrms.utils.PubFunc.closeResource(idRs);
      	}
		return toA0100;
	}
	private String getOrg(String tablename,String A0100,String cloumn){
		String codeitemdesc="";
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search("select "+cloumn+" as ss from "+tablename+" where A0100='"+A0100+"'");
			if(this.frowset.next()){
				codeitemdesc = this.frowset.getString("ss");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return codeitemdesc;
	}
	
}