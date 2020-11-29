/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform.emp;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:求插入或追加人员主集记录的初始化值
 * </p>
 * <p>
 * Description:A0000,A0100,B0110,E0122
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2007-8-16:下午03:01:03
 * </p>
 * 
 * @author cmq
 * @version 4.0
 */
public class SearchNewRecrodValueTrans extends IBusiness {

	/**
	 * 根据部门编码，查找对应的上级单位编码值,通过递归找到上级单位 节点。
	 * 
	 * @param codevalue
	 * @return
	 */
	private String getParentCodeValue(String codevalue) {

		String value = "";
		StringBuffer buf = new StringBuffer();
		buf
				.append("select codeitemid,codesetid,parentid from organization where codeitemid=?");
		ArrayList paralist = new ArrayList();
		paralist.add(codevalue);
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			RowSet rset = dao.search(buf.toString(), paralist);
			if (rset.next()) {
				String codeid = rset.getString("codesetid");
				String parentid = rset.getString("parentid");
				if (!"UN".equalsIgnoreCase(codeid))
					value = getParentCodeValue(parentid);
				else
					value = rset.getString("codeitemid");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return value;
	}

	public void execute() throws GeneralException {

		/** 子集名 */
		String setname = (String) this.getFormHM().get("setname");
		/**
		 * 操作方式 type=insert 插入记录 =append 追加记录
		 */
		String fieldsetid = setname;
		ArrayList dblist = this.userView.getPrivDbList();
		for (int i = 0; i < dblist.size(); i++) {
			String pre = (String) dblist.get(i);
			if (pre != null && pre.trim().length() > 0)
				fieldsetid = fieldsetid.replace(pre, "");
		}
		String fieldPri = this.userView.analyseTablePriv(fieldsetid);
		if (!"2".equals(fieldPri))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory
					.getProperty("workdiary.message.add.record.competence")
					+ "！"));

		String type = (String) this.getFormHM().get("type");
		String a_code = (String) this.getFormHM().get("a_code");
		a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
		// String preno=(String)this.getFormHM().get("preno");
		String a0100 = (String) this.getFormHM().get("A0100");
		try {
			if ("insert".equalsIgnoreCase(type)) {
				if (setname.indexOf("A01") != -1) {
					initMainSetValue(setname, a_code);
					/** 插入的记录要纠正a0000排序字段的值，否则一刷新变成最后一条了 */
					updateA000(setname, a0100);
				} else {
					String i9999 = (String) this.getFormHM().get("I9999");
					initSubSetValue(setname, i9999, fieldsetid);
				}
			} else {
				/** 对主集暂时这样处理啦。。。 */
				if (setname.indexOf("A01") != -1) {
					initMainSetValue(setname, a_code);
				} else {
					// String i9999=(String)this.getFormHM().get("I9999");
					initSubSetValue(setname, fieldsetid);
				}
			}
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

	/**
	 * 初始化子集参数
	 * 
	 * @param setname
	 * @param curri9999
	 *            //当前插入记录的值
	 * @throws GeneralException
	 */
	private void initSubSetValue(String setname, String curri9999,
			String fieldsetid) throws GeneralException {

		String a0100 = (String) this.getFormHM().get("A0100");
		String i9999 = DbNameBo.insertSubSetA0100(setname, a0100, curri9999,
				getFrameconn());
		String item = "";
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		ArrayList fieldlist = DataDictionary.getFieldList(fieldsetid,
				Constant.USED_FIELD_SET);
		StringBuffer updatestr = new StringBuffer();
		updatestr.append("update " + setname + " set ");
		ArrayList listvalue = new ArrayList();
		if(fieldlist!=null){
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem fielditem = (FieldItem) fieldlist.get(i);
				if (fielditem.isSequenceable()) {
					String seq_no = idg.getId(fieldsetid + "."
							+ fielditem.getItemid());
					item += fielditem.getItemid() + ",";
					updatestr.append(fielditem.getItemid() + "=?,");
					if (seq_no.length() > fielditem.getItemlength() - 1) {
						seq_no = seq_no.substring(0, fielditem.getItemlength() - 1);
					}
					this.getFormHM().put(fielditem.getItemid(), seq_no);
					listvalue.add(seq_no);
				}
			}
		}

		updatestr.append("CreateUserName=?");
		listvalue.add(this.userView.getUserName());
		updatestr.append(" where A0100=? and I9999=?");
		listvalue.add(a0100);
		listvalue.add(i9999);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.update(updatestr.toString(), listvalue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("fielditem", item);
		this.getFormHM().put("I9999", i9999);
		this.getFormHM().put("A0100", a0100);
	}

	private void initSubSetValue(String setname, String fieldsetid)
			throws GeneralException {

		String a0100 = (String) this.getFormHM().get("A0100");
		String i9999 = DbNameBo.insertSubSetA0100(setname, a0100,
				getFrameconn());
		String item = "";
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		ArrayList fieldlist = DataDictionary.getFieldList(fieldsetid,
				Constant.USED_FIELD_SET);
		StringBuffer updatestr = new StringBuffer();
		updatestr.append("update " + setname + " set ");
		ArrayList listvalue = new ArrayList();
		if(fieldlist!=null){
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem fielditem = (FieldItem) fieldlist.get(i);
				if (fielditem.isSequenceable()) {
					String seq_no = idg.getId(fieldsetid + "."
							+ fielditem.getItemid());
					item += fielditem.getItemid() + ",";
					updatestr.append(fielditem.getItemid() + "=?,");
					if (seq_no.length() > fielditem.getItemlength() - 1) {
						seq_no = seq_no.substring(0, fielditem.getItemlength() - 1);
					}
					this.getFormHM().put(fielditem.getItemid(), seq_no);
					listvalue.add(seq_no);
				}
			}
		}

		updatestr.append("CreateUserName=?");
		listvalue.add(this.userView.getUserName());
		updatestr.append(" where A0100=? and I9999=?");
		listvalue.add(a0100);
		listvalue.add(i9999);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.update(updatestr.toString(), listvalue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getFormHM().put("fielditem", item);
		this.getFormHM().put("I9999", i9999);
		this.getFormHM().put("A0100", a0100);
	}

	/**
	 * 初始化主集参数
	 * 
	 * @param setname
	 * @throws GeneralException
	 */
	private void initMainSetValue(String setname, String a_code)
			throws GeneralException {

		String b0110 = "";
		String e0122 = "";
		String e01a1 = "";
		String codesetid = "";
		String codevalue = "";

		if (a_code != null && a_code.trim().length() > 1)
			codesetid = a_code.substring(0, 2);
		if (a_code != null && a_code.trim().length() > 2)
			codevalue = a_code.substring(2);

		if ("UN".equalsIgnoreCase(codesetid)
				&& (!"".equalsIgnoreCase(codevalue))) {
			b0110 = codevalue;
		} else if ("UM".equalsIgnoreCase(codesetid)
				&& (!"".equalsIgnoreCase(codevalue))) {
			e0122 = codevalue;
			CodeItem codeitem = AdminCode.getCode("UM", codevalue);
			String parentid = codeitem.getPcodeitem();
			b0110 = getParentCodeValue(parentid);
		} else if ("@K".equalsIgnoreCase(codesetid)
				&& (!"".equalsIgnoreCase(codevalue))) {
			e01a1 = codevalue;
			CodeItem codeitem = AdminCode.getCode("@K", codevalue);
			e0122 = codeitem.getPcodeitem();
			b0110 = getParentCodeValue(e0122);
		}
		DbNameBo db = new DbNameBo(this.getFrameconn());
		if (e01a1 != null && e01a1.trim().length() > 0) {
			boolean inflag = db.overWorkOut(setname, e01a1, 1);
			String unitdesc = AdminCode.getCodeName("@K", e01a1);
			if (inflag) {
				throw GeneralExceptionHandler
						.Handle(new GeneralException(
								"",
								unitdesc
										+ ResourceFactory
												.getProperty("workdiary.message.person.excess")
										+ "！", "", ""));
			}
		}
		HashMap map = DbNameBo.appendMainSetA0100(setname, b0110, e0122, e01a1,
				getFrameconn());
		String A0100 = (String) map.get("A0100");
		db.appendMainSetA0100(setname, b0110, e0122, A0100);

		/** 追加主集记录 */
		String item = "";
		IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		ArrayList fieldlist = DataDictionary.getFieldList("A01",
				Constant.USED_FIELD_SET);
		StringBuffer updatestr = new StringBuffer();
		updatestr.append("update " + setname + " set ");
		ArrayList listvalue = new ArrayList();
		for (int i = 0; i < fieldlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fieldlist.get(i);
			if (fielditem.isSequenceable()) {
				String seq_no = idg.getId("A01." + fielditem.getItemid().toUpperCase());
				item += fielditem.getItemid() + ",";
				updatestr.append(fielditem.getItemid() + "=?,");
				if (seq_no.length() > fielditem.getItemlength() - 1) {
					seq_no = seq_no.substring(0, fielditem.getItemlength() - 1);
				}
				this.getFormHM().put(fielditem.getItemid(), seq_no);
				listvalue.add(seq_no);
			}
		}
		updatestr.append("CreateUserName=?");
		listvalue.add(this.userView.getUserName());
		updatestr.append(" where A0100=?");
		listvalue.add(A0100);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			dao.update(updatestr.toString(), listvalue);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getFormHM().put("fielditem", item);
		this.getFormHM().put("A0100", A0100);
		this.getFormHM().put("A0000", map.get("A0000"));
		/** 设置前台单位或部门的默认值 */
		this.getFormHM().put("B0110", b0110);
		this.getFormHM().put("E0122", e0122);
		this.getFormHM().put("E01A1", e01a1);

	}

	public void updateA000(String setname, String a0100) {

		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select a0000 from " + setname + " where  a0100='");
		sqlstr.append(a0100);
		sqlstr.append("'");
		String a0000 = "1";
		ContentDAO dao = new ContentDAO(this.frameconn);

		try {
			RowSet rset = dao.search(sqlstr.toString());
			if (rset.next())
				a0000 = rset.getString("a0000");

			sqlstr = new StringBuffer();
			sqlstr.append("update " + setname
					+ " set a0000=a0000+1 where a0000>=");
			sqlstr.append(a0000);

			dao.update(sqlstr.toString());

			sqlstr = new StringBuffer();
			sqlstr.append("update " + setname + " set a0000=");
			sqlstr.append(a0000);
			sqlstr.append(" where a0100=(select max(a0100) from " + setname
					+ ")");

			dao.update(sqlstr.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
