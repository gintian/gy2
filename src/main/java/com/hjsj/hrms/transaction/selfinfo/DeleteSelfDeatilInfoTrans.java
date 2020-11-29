/*
 * Created on 2005-6-4
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DeleteSelfDeatilInfoTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selfinfolist = (ArrayList) this.getFormHM().get(
				"selectedlist");
		String setname = (String) this.getFormHM().get("setname");
		String userbasee = (String) this.getFormHM().get("userbase");
		if (this.getFormHM().containsKey("isAppEdite")) {
			String isAppEdite = (String) this.getFormHM().get("isAppEdite");
			if ("1".equals(isAppEdite)) {
				selfinfolist = (ArrayList) this.getFormHM().get(
						"pageselectedlist");
			}
		}
		/*
		 * String setname=(String)this.getFormHM().get("setname"); String
		 * userbase=(String)this.getFormHM().get("userbase"); String
		 * tablename=userbase + setname; HashMap
		 * hm=(HashMap)this.getFormHM().get("requestPamaHM"); String
		 * A0100=(String)hm.get("a0100"); //获得人员ID if("A0100".equals(A0100))
		 * A0100=userView.getUserId(); StringBuffer strsql=new StringBuffer();
		 * strsql.append("delete from "); strsql.append(tablename);
		 * strsql.append(" where A0100='"); strsql.append(A0100);
		 * strsql.append("'");
		 * if(!"A01".substring(1,3).equals(setname.substring(1,3))) {
		 * strsql.append(" and ("); for(int i=0;i<selfinfolist.size();i++) {
		 * RecordVo vo=(RecordVo)selfinfolist.get(i); strsql.append("I9999=");
		 * strsql.append(vo.getInt("i9999")); strsql.append(" or "); }
		 * strsql.append("1=2)"); } try { new
		 * ExecuteSQL().execUpdate(strsql.toString());
		 * 
		 * }catch(Exception e) { e.printStackTrace(); }
		 */
		if (selfinfolist == null || selfinfolist.size() == 0)
			return;
		RecordVo vo = (RecordVo)selfinfolist.get(0);
        if(!vo.getModelName().equalsIgnoreCase(userbasee+setname))
        	return;
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
		String inputchinfor = sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		inputchinfor = inputchinfor != null && inputchinfor.trim().length() > 0 ? inputchinfor
				: "1";
		String approveflag = sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		approveflag = approveflag != null && approveflag.trim().length() > 0 ? approveflag
				: "1";
		if ("1".equals(inputchinfor) && "1".equals(approveflag)
				) {/*去掉&& this.userView.getStatus() == 4 */
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String flag=(String)hm.get("flag");
			
			String A0100 = userView.getA0100();//(String) this.getFormHM().get("a0100");
			String userbase = userView.getDbname();//(String) this.getFormHM().get("userbase");
			if("notself".equalsIgnoreCase(flag)){
				A0100 = (String) this.getFormHM().get("a0100");
				userbase = (String) this.getFormHM().get("userbase");
				CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
				userbase = cps.checkDb(userbase);
				A0100 = cps.checkA0100("",userbase , A0100, "");
			}
			userbase = userbase != null && userbase.trim().length() > 0 ? userbase
					: "usr";

			FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
			ArrayList newlist = new ArrayList();
			
			ArrayList newFieldList = new ArrayList();
			ArrayList oldFieldList = new ArrayList();
			// for(int m=0;m< selfinfolist.size(); m++) {
			//	 			
			for (int i = 0; i < selfinfolist.size(); i++) {
				RecordVo itemvo = (RecordVo) selfinfolist.get(i);
				String state = itemvo.getString("state");
				String chg_id = itemvo.getString("id");
				String keyid = itemvo.getString("a0100");
				String typeid = itemvo.getString("state");
				String sequenceid = String.valueOf(itemvo.getInt("i9999"));
				if ("new".equals(state) || "insert".equals(state)
						|| "update".equals(state) || "delete".equals(state)) {
					MyselfDataApprove mysel = new MyselfDataApprove(
							this.frameconn, this.userView, userbase, A0100);
					
					mysel.delMyselfData(chg_id, fieldset, state, keyid,
							sequenceid);

					mysel.getOtherParamList(chg_id, setname, "01,02,03,07");
					List keylist = mysel.getKeyvalueList();
					if (keylist.size() > 0)
						keyid = (String) keylist.get(0);

					List typelist = mysel.getTypeList();
					if (typelist.size() > 0)
						typeid = (String) typelist.get(0);

					List sequenceList = mysel.getSequenceList();
					if (sequenceList.size() > 0)
						sequenceid = (String) sequenceList.get(0);

					mysel.getOneMyselfData(chg_id, setname, keyid, typeid,
							sequenceid, "0");
					newFieldList = mysel.getNewValueList();
					oldFieldList = mysel.getOldValueList();
					// itemlist=cloneList(newFieldList);
					this.getFormHM().put("sp_flag", mysel.getRecord_spflag());
					List fieldlist = mysel.queryMyselfFieldSetListFormChgid(
							chg_id, "01,02,03,07");
					if (fieldlist.size() < 1) {
						delRecode(chg_id);
					}
					String description = "";
					for (int j = 0; j < fieldlist.size(); j++) {
						FieldSet fieldSetvlaue = (FieldSet) fieldlist.get(j);
						description += fieldSetvlaue.getCustomdesc() + ",";
					}
					if (fieldlist.size() > 0)
						updateDescription(chg_id, description);
					this.getFormHM().put("sp_flag", mysel.getRecord_spflag());
				} else {
					ArrayList olist = new ArrayList();
					olist.add(selfinfolist.get(i));
					ArrayList listold = getOldList(userbase, setname, A0100,
							olist);
					MyselfDataApprove mysel = new MyselfDataApprove(
							this.frameconn, this.userView, userbase, A0100);
					for (int j = 0; j < listold.size(); j++) {
					ArrayList oldlist = (ArrayList) listold.get(j);
					mysel.getOtherParamList(userbase, A0100, fieldset
							.getFieldsetid(), "01,02,07", itemvo
							.getString("i9999"));
					ArrayList sequenceList = mysel.getSequenceList();
					if (sequenceList.size() < 1) {
						mysel.getOtherParamList(userbase, A0100, fieldset
								.getFieldsetid(), "03", itemvo
								.getString("i9999"));
						ArrayList sequenceList1 = mysel.getSequenceList();
						String sequence = "1";
						if (sequenceList1.size() > 0) {
							sequence = (Integer.parseInt((String) sequenceList1
									.get(sequenceList1.size() - 1)) + 1)
									+ "";
						}
						mysel.saveMyselfData(userbase, A0100, fieldset,
								newlist, oldlist, "delete", "01", itemvo
										.getString("i9999"), sequence);
					}
					}
				}
			}
			// }
			// ArrayList fieldlist =
			// DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET );
			// for(int i=0;i<selfinfolist.size();i++){
			// RecordVo itemvo = (RecordVo)selfinfolist.get(i);
			// ArrayList oldlist=new ArrayList();
			// for(int j=0;j<fieldlist.size();j++){
			// FieldItem fielditem = (FieldItem)fieldlist.get(j);
			// fielditem.setValue(itemvo.getString(fielditem.getItemid()));
			// oldlist.add(fielditem);
			// }
			// mysel.saveMyselfData(userbase,A0100,fieldset,newlist,oldlist,"delete","01",itemvo.getString("i9999"));
			// }

		} else {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				dao.deleteValueObject(selfinfolist);
			} catch (Exception sqle) {
				sqle.printStackTrace();
				throw GeneralExceptionHandler.Handle(sqle);
			}
		}

	}

	private ArrayList getOldList(String userbase, String setname, String A0100,
			ArrayList selfinfolist) {
		StringBuffer buf = new StringBuffer();
		buf.append("select * from " + userbase + setname);
		buf.append(" where a0100='" + A0100 + "' and i9999 in(");
		ArrayList list = new ArrayList();
		ArrayList fieldlist = DataDictionary.getFieldList(setname,
				Constant.USED_FIELD_SET);
		for (int i = 0; i < selfinfolist.size(); i++) {
			RecordVo itemvo = (RecordVo) selfinfolist.get(i);
			buf.append(itemvo.getString("i9999"));
			if (i < selfinfolist.size() - 1) {
				buf.append(",");
			}
		}
		buf.append(")");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(buf.toString());
			while (frowset.next()) {
				ArrayList oldlist = new ArrayList();
				for (int j = 0; j < fieldlist.size(); j++) {
					FieldItem fielditem =  (FieldItem) fieldlist.get(j);
					fielditem = (FieldItem)fielditem.clone();
					String typeitem = fielditem.getItemtype();
					String itemvalue = "";
					if(typeitem!=null&& "D".equalsIgnoreCase(typeitem))
					{
						Date itemate= frowset.getDate(fielditem.getItemid());
						if(itemate!=null)
							itemvalue=DateUtils.format(itemate,"yyyy.MM.dd");
					} else {
					itemvalue = this.frowset.getString(fielditem
							.getItemid());
					}
					itemvalue = itemvalue != null
							&& itemvalue.trim().length() > 0 ? itemvalue : "";
					fielditem.setValue(itemvalue);
					oldlist.add(fielditem);
				}
				list.add(oldlist);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	private void delRecode(String chg_id) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update("delete from t_hr_mydata_chg where chg_id=" + chg_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateDescription(String chg_id, String description) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		ArrayList deslist = new ArrayList();
		deslist.add(description);
		deslist.add(chg_id);
		list.add(deslist);
		try {
			dao.update(
					"update t_hr_mydata_chg set description=? where chg_id=?",
					deslist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
