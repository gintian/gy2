package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.structuresql.A0100Bean;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ApproveInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap reqhm = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_code = (String) reqhm.get("code");
		String codeDesc = AdminCode.getCodeName("UN", a_code);
        if(StringUtils.isEmpty(codeDesc))
            codeDesc = AdminCode.getCodeName("UM", a_code);
        
        if(StringUtils.isEmpty(codeDesc))
            codeDesc = AdminCode.getCodeName("@k", a_code);
        //如果解密后的值为空则a_code的值变回原来传过来的值
        if(StringUtils.isEmpty(codeDesc))
            a_code = PubFunc.decrypt(a_code);
		
		a_code = a_code != null && a_code.trim().length() > 0 ? a_code : "";
		reqhm.remove("code");

		String kind = (String) reqhm.get("kind");
		kind = kind != null && kind.trim().length() > 0 ? kind : "";
		reqhm.remove("kind");

		String fcheck = (String) reqhm.get("fcheck");
		fcheck = fcheck != null && fcheck.trim().length() > 0 ? fcheck
				: "fclose";
		reqhm.remove("fcheck");

		String chg_id = (String) reqhm.get("chg_id");
		//此处chg_id只是一条记录的id，故在此交易类中直接对chg_id进行加密解密        by chenxg add 2015-05-24
		if(chg_id != null && chg_id.length() > 0)
		    chg_id = PubFunc.decrypt(chg_id);
		    
		chg_id = chg_id != null && chg_id.trim().length() > 0 ? chg_id : "";
		reqhm.remove("chg_id");
        
		String setname = (String) this.getFormHM().get("setname");
		if(setname==null){//tianye add 
			String userbase = (String) this.getFormHM().get("userbase");
			if(userbase==null||userbase.length()<0){
				ArrayList dblist=userView.getPrivDbList();
	        	userbase=(String)dblist.get(0);
	        }
			setname = userbase;
		}
		
		String sp_flag = (String) this.getFormHM().get("sp_flag");
		sp_flag = sp_flag != null && sp_flag.trim().length() > 0 ? sp_flag
				: "02";
		String returnValue = (String) reqhm.get("returnVa");
		if (returnValue == null) {
			returnValue = "";
		} else {
			//zxj 20160528 前台通过url传入的转码数据（包括汉字等特殊字符）
			returnValue = SafeCode.decode(returnValue);
                }
		//过滤领导批示中的特殊字符 by add chenxg 2015-08-24
		returnValue = PubFunc.keyWord_filter(returnValue);
		String returnflag = (String) reqhm.get("returnflag");
		if (returnflag == null) {
		    returnflag = "";		
		}
		reqhm.remove("returnflag");
		
		
		MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,
				this.userView);

		String savEdit = (String) reqhm.get("savEdit");
		savEdit = savEdit != null && savEdit.trim().length() > 0 ? savEdit
				: "search";
		reqhm.remove("savEdit");
		
		if ("boflagall".equals(savEdit))// 驳回
		{
			String chg_idall = (String) reqhm.get("chg_idall");
			chg_idall = chg_idall != null && chg_idall.trim().length() > 0 ? chg_idall
					: "";
			if (chg_idall != null && chg_idall.trim().length() > 0) {
				String[] arr = chg_idall.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] != null && arr[i].trim().length() > 0) {
					    mysel.setCommentValue(returnValue);
						mysel.batchMyselfDataApply(PubFunc.decrypt(arr[i]), "07");
						String orgAndName = this.setOrgInfo(userView
								.getDbname(), userView.getA0100(),
								this.frameconn);
						//【6976】员工管理/信息审核，批示，如果业务用户做的批准，批示中姓名显示不出来，不对。jingq add 2015.02.03
						if("///".equals(orgAndName)){
							orgAndName = userView.getUserFullName();
						}
						mysel.approval(this.userView, orgAndName, "退回", PubFunc.decrypt(arr[i]),
								returnValue);
						/*
						 * A0100Bean bean = this.getA0100bean(arr[i]);
						 * this.mediaProve("2", bean);
						 */
					}
				}
			}
		} else if ("pflagall".equals(savEdit)) {// 批准
			String chg_idall = (String) reqhm.get("chg_idall");
			chg_idall = chg_idall != null && chg_idall.trim().length() > 0 ? chg_idall
					: "";
			if (chg_idall != null && chg_idall.trim().length() > 0) {
				String[] arr = chg_idall.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] != null && arr[i].trim().length() > 0) {
						mysel.batchMyselfDataApply(PubFunc.decrypt(arr[i]), "03");
						String orgAndName = this.setOrgInfo(userView
								.getDbname(), userView.getA0100(),
								this.frameconn);
						if("///".equals(orgAndName)){
							orgAndName = userView.getUserFullName();
						}
						mysel.approval(this.userView, orgAndName, "批准", PubFunc.decrypt(arr[i]),returnValue);
						/*
						 * A0100Bean bean = this.getA0100bean(arr[i]);
						 * this.mediaProve("3", bean);
						 */
					}
				}
			}
		} else if ("delall".equals(savEdit)) {
			String chg_idall = (String) reqhm.get("chg_idall");
			chg_idall = chg_idall != null && chg_idall.trim().length() > 0 ? chg_idall
					: "";
			if (chg_idall != null && chg_idall.trim().length() > 0) {
				String[] arr = chg_idall.split(",");
				for (int i = 0; i < arr.length; i++) {
					if (arr[i] != null && arr[i].trim().length() > 0) {
						// A0100Bean bean = this.getA0100bean(arr[i]);
						mysel.deleteMyselfData(PubFunc.decrypt(arr[i]));
						// this.deleteMedia(bean);
					}
				}
			}
		}
		String viewitem = (String) reqhm.get("viewitem");
		viewitem = viewitem != null && viewitem.trim().length() > 0 ? viewitem
				: "0";
		if ("fopen".equals(fcheck)) {

			ArrayList fieldlist = mysel.queryMyselfFieldSetListFormChgid(
					chg_id, "01,02,03,07");
			String check = (String) reqhm.get("check");
			check = check != null && check.trim().length() > 0 ? check
					: "close";
			reqhm.remove("check");

			String setid = (String) reqhm.get("setid");
			setid = setid != null && setid.trim().length() > 0 ? setid : "";
			reqhm.remove("setid");

			mysel.getOtherParamList(chg_id, setid, "01,02,03,07");
			ArrayList keylist = mysel.getKeyvalueList();

			String keyid = (String) reqhm.get("keyid");
			if(keyid != null && keyid.trim().length() > 0)
			    keyid = PubFunc.decrypt(keyid);
			
			keyid = keyid != null && keyid.trim().length() > 0 ? keyid : "";
			if (keyid.trim().length() < 1) {
				if (keylist.size() > 0)
					keyid = (String) keylist.get(0);
			}
			reqhm.remove("keyid");

			ArrayList typelist = mysel.getTypeList();
			String typeid = (String) reqhm.get("typeid");
			typeid = typeid != null && typeid.trim().length() > 0 ? typeid : "";
			if (typeid.trim().length() < 1) {
				if (typelist.size() > 0)
					typeid = (String) typelist.get(0);
			}
			reqhm.remove("typeid");

			ArrayList sequenceList = mysel.getSequenceList();
			String sequenceid = (String) reqhm.get("sequenceid");
			sequenceid = sequenceid != null && sequenceid.trim().length() > 0 ? sequenceid
					: "";
			if (sequenceid.trim().length() < 1) {
				if (sequenceList.size() > 0)
					sequenceid = (String) sequenceList.get(0);
			}
			reqhm.remove("sequenceid");

			if ("open".equals(check)) {
				ArrayList newFieldList = new ArrayList();
				// ArrayList cenlist=new ArrayList();
				ArrayList oldFieldList = new ArrayList();

				if ("save".equals(savEdit)) {
					newFieldList = (ArrayList) this.getFormHM().get(
							"newFieldList");
					oldFieldList = (ArrayList) this.getFormHM().get(
							"oldFieldList");
					// cenlist = (ArrayList)this.getFormHM().get("cenlist");
					// oldFieldList=cloneList(cenlist);
					// cenlist = cloneList(newFieldList);
					FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
					mysel.updateMyselfData(chg_id, fieldset, newFieldList,
							oldFieldList, typeid, "02", keyid, sequenceid);
					this.getFormHM().put("allflag", "02");
				} else if ("search".equalsIgnoreCase(savEdit)) {
					mysel.getOneMyselfData(chg_id, setid, keyid, typeid,
							sequenceid, viewitem);
					newFieldList = mysel.getNewValueList();
					oldFieldList = mysel.getOldValueList();
					// cenlist=cloneList(newFieldList);
					this.getFormHM().put("allflag", mysel.getRecord_spflag());
				} else if ("boflag".equals(savEdit)) {// 驳回
					FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
					mysel.updateApployMyselfDataApp(chg_id, fieldset, "07",
							keyid, typeid, sequenceid);
					fieldlist = mysel.queryMyselfFieldSetListFormChgid(chg_id,
							"01,02,03,07");
					newFieldList = (ArrayList) this.getFormHM().get(
							"newFieldList");
					// cenlist = (ArrayList)this.getFormHM().get("cenlist");
					oldFieldList = (ArrayList) this.getFormHM().get(
							"oldFieldList");
					this.getFormHM().put("allflag", "07");
				} else if ("pflag".equals(savEdit)) {// 批准
					FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
					mysel.updateApployMyselfDataApp(chg_id, fieldset, "03",
							keyid, typeid, sequenceid);
					fieldlist = mysel.queryMyselfFieldSetListFormChgid(chg_id,
							"01,02,03,07");
					newFieldList = (ArrayList) this.getFormHM().get(
							"newFieldList");
					// cenlist = (ArrayList)this.getFormHM().get("cenlist");
					oldFieldList = (ArrayList) this.getFormHM().get(
							"oldFieldList");
					this.getFormHM().put("allflag", "03");
				}
				this.getFormHM().put("sp_flag", mysel.getRecord_spflag());
				this.getFormHM().put("newFieldList", changeList(newFieldList));
				this.getFormHM().put("oldFieldList", changeList(oldFieldList));
				this.getFormHM().put("keyid", PubFunc.encrypt(keyid));
				this.getFormHM().put("typeid", typeid);
				this.getFormHM().put("typelist", typelist);
				this.getFormHM().put("keylist", keylist);
				// this.getFormHM().put("cenlist",cenlist);
				this.getFormHM().put("sequenceid", sequenceid);
				this.getFormHM().put("sequenceList", sequenceList);
			} else {
				this.getFormHM().put("allflag", sp_flag);
			}
			this.getFormHM().put("check", check);
			this.getFormHM().put("setid", setid);
			this.getFormHM().put("fieldlist", fieldlist);
		} else {

			String sqlStr[] = getStatesql(setname, a_code, kind, sp_flag);
			this.getFormHM().put("setid", "");
			this.getFormHM().put("sql", sqlStr[0]);
			this.getFormHM().put("where", sqlStr[2]);
			
			//task=1 首页代办进入  // 0 员工审核进入  2013-12-25 gdd
			String task = (String)reqhm.get("task");
			if(task!=null && task.length()>0)
				this.getFormHM().put("task", task);
			reqhm.remove("task");
			if("1".equals(this.formHM.get("task"))){
				String userUNid = userView.getUserOrgId();
				if(userUNid != null && userUNid.length()>0)
				  this.getFormHM().put("where",sqlStr[2]+" and b0110='"+userUNid+"'");
				
				//如果task有值则是首次进入，设置审批标示为 已报批
				if("1".equals(task))
					sp_flag = "02";
			}
			this.getFormHM().put("column", sqlStr[1]);
		}
		
		this.getFormHM().put("redundantInfo", mysel.getRedundantInfo());
		this.getFormHM().put("viewitem", viewitem);
		this.getFormHM().put("setname", setname);
		this.getFormHM().put("sp_flag", sp_flag);
		this.getFormHM().put("a_code", PubFunc.encrypt(a_code));
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("setnamelist", setnameList());
		this.getFormHM().put("spflaglist", spflagList());
		this.getFormHM().put("fcheck", fcheck);
		this.getFormHM().put("chg_id", PubFunc.encrypt(chg_id));
		this.getFormHM().put("returnflag", returnflag);

	}

	private String[] getStatesql(String dbname, String a_code, String kind,
			String sp_flag) throws GeneralException {
		String[] sql = new String[4];
		if (!this.getUserView().isSuper_admin()) {
			if (a_code.trim().length() < 1) {
				a_code = this.userView.getManagePrivCodeValue();
			}
			if (kind.trim().length() < 1) {
				kind = getKind(this.userView.getManagePrivCode());
			}
		}

		/*
		 * 使用oracle数据库时，create_time的数据类型为date类型，在paginationdb标签中会将
		 * date类型的数据自动转成YYYY-MM-DD类型，无法显示时分秒，所以在使用oracle数据库时将
		 * create_time转成字符窜类型，避免标签的自动转化，从而显示时分秒
		 * 
		 * Sql_switcher.searchDbServer()从服务器下的system.properties文件中获得dbserver的值，
		 * 1为sqlserver数据库，2为oracle数据库
		 * 
		 * wangzhongjun 2010-01-21
		 */
		if (Sql_switcher.searchDbServer() == 2) {
			sql[0] = "select chg_id,nbase,B0110,E0122,E01a1,A0101,A0100,A0000,content,sp_idea,sp_flag,to_char(create_time,'YYYY-MM-DD HH24:MI:SS') as create_time,description";
		} else {
			sql[0] = "select chg_id,nbase,B0110,E0122,E01a1,A0101,A0100,A0000,content,sp_idea,sp_flag,create_time,description";
		}

		sql[1] = "chg_id,nbase,B0110,E0122,E01a1,A0101,A0100,A0000,content,sp_idea,sp_flag,create_time,description";
		StringBuffer wheresql = new StringBuffer("from t_hr_mydata_chg where nbase='" + dbname + "'");
		
		//人员权限过滤
		String wherePrivSql ="select A0100 "+userView.getPrivSQLExpression(dbname, false);
		wheresql.append(" and A0100 in("+wherePrivSql+") ");
		
//		String privCode = userView.getManagePrivCode();
//		String privCodeValue = userView.getManagePrivCodeValue();
//		if(privCode.length()>=2){
//			if(privCode.equalsIgnoreCase("UN"))
//			    wheresql.append(" AND B0110 like '" + privCodeValue + "%'");
//			else if(privCode.equalsIgnoreCase("UM"))
//	            wheresql.append(" AND E0122 like '" + privCodeValue + "%'");
//			else if(privCode.equalsIgnoreCase("@K"))
//	            wheresql.append(" AND E01A1 like '" + privCodeValue + "%'");
//			if (a_code != null && a_code.trim().length() > 0) {
//				if (kind.equals("2"))
//					wheresql.append(" and B0110 like '" + a_code + "%'");
//				else if (kind.equals("1"))
//					wheresql.append(" and E0122 like '" + a_code + "%'");
//				else if (kind.equals("0"))
//					wheresql.append(" and E01a1 like '" + a_code + "%'");
//			}
//		}else{
//			    wheresql.append(" AND B0110 like '" + userView.getUnit_id() + "%'");
//	            wheresql.append(" AND E0122 like '" + userView.getUserDeptId() + "%'");
//		}
		//信息审核/机构树中选中 对右侧表无效    jingq add 2014.10.30
		if(a_code != null && !"".equals(a_code.trim()))
			wheresql.append(" and (B0110 like '"+a_code+"%' or E0122 like '"+a_code+"%' or E01a1 like '"+a_code+"%')");
		if (!"all".equalsIgnoreCase(sp_flag))
			wheresql.append(" and sp_flag='" + sp_flag + "'");
		String employeeName = (String) this.getFormHM().get("employeeName");
		if (employeeName != null && employeeName.length() > 0) {
			employeeName = PubFunc.getStr(employeeName);
			//wheresql.append(" and a0101='"); tiany 更改为支持模糊查询
			wheresql.append(" and a0101 like '");
			wheresql.append(employeeName);
			wheresql.append("%'");
			//wheresql.append("'");
		}
		sql[2] = wheresql.toString();
		sql[3] = "order by A0000";
		return sql;
	}

	private ArrayList setnameList() {
		ArrayList list = new ArrayList();
		/**应用库过滤前缀符号*/
        ArrayList dblist=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<dblist.size();i++)
        {
        	
        	if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)dblist.get(i));
            cond.append("'");
        }
        if(dblist.size()==0)
            cond.append("''");
        cond.append(")");
        cond.append(" order by dbid");
        /**应用库过滤前缀符号*/
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(cond.toString());
			while (this.frowset.next()) {
				CommonData obj = new CommonData(this.frowset.getString("pre"),
						this.frowset.getString("dbname"));
				list.add(obj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	private ArrayList spflagList() {
		ArrayList list = new ArrayList();
		String name[] = { "全部", "起草", "已报批", "已批", "退回" };
		String id[] = { "all", "01", "02", "03", "07" };
		for (int i = 0; i < id.length; i++) {
			CommonData obj = new CommonData(id[i], name[i]);
			list.add(obj);
		}

		return list;
	}

	private String getKind(String codeid) {
		String kind = "";
		if ("UN".equalsIgnoreCase(codeid))
			kind = "2";
		else if ("UM".equalsIgnoreCase(codeid))
			kind = "1";
		else if ("@K".equalsIgnoreCase(codeid))
			kind = "0";
		return kind;
	}

	public ArrayList changeList(ArrayList list) {
		ArrayList itemlist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			FieldItem item = (FieldItem) list.get(i);
			ArrayList priitemlist = this.userView.getPrivFieldList(item
					.getFieldsetid(), 0);
			FieldItem fielditem = null;
			for (int j = 0; j < priitemlist.size(); j++) {
				fielditem = (FieldItem) priitemlist.get(j);
				if (fielditem != null
						&& fielditem.getItemid().equalsIgnoreCase(
								item.getItemid()))
					break;
			}
			if (fielditem == null || fielditem.getPriv_status() == 0) {
				String pri = this.userView.analyseFieldPriv(item.getItemid());
				if ("0".equals(pri))
					item.setPriv_status(0);
				else if ("1".equals(pri))
					item.setPriv_status(1);
				else if ("2".equals(pri))
					item.setPriv_status(2);
			} else {
				item.setPriv_status(fielditem.getPriv_status());
			}
			if (item.isCode()) {
				item.setViewvalue(AdminCode.getCodeName(item.getCodesetid(),
						item.getValue()));
			}
			itemlist.add(item);
		}
		return itemlist;
	}

	public ArrayList cloneList(ArrayList list) {
		ArrayList itemlist = new ArrayList();
		FieldItem item = null;
		for (int i = 0; i < list.size(); i++) {
			FieldItem fielditem = (FieldItem) list.get(i);
			item = (FieldItem) fielditem.clone();
			itemlist.add(item);
		}
		return itemlist;
	}

	/**
	 * 获得“单位/部门/职位/姓名”形式字符窜
	 * 
	 * @param userbase
	 * @param A0100
	 * @param dao
	 */
	private String setOrgInfo(String userbase, String A0100,
			Connection connection) {
		ContentDAO dao = new ContentDAO(connection);
		StringBuffer strsql = new StringBuffer();
		StringBuffer name = new StringBuffer();
		String b0110 = "";
		String e0122 = "";
		String e01a1 = "";
		String a0101 = "";
		try {
			if (userbase != null && userbase.length() > 0 && A0100 != null
					&& A0100.length() > 0) {
				strsql.append("select b0110,e0122,e01a1,a0101 from ");
				strsql.append(userbase);
				strsql.append("A01 where a0100='");
				strsql.append(A0100);
				strsql.append("'");
				this.frowset = dao.search(strsql.toString());
				if (this.frowset.next()) {
					b0110 = this.getFrowset().getString("B0110");
					e0122 = this.getFrowset().getString("E0122");
					e01a1 = this.getFrowset().getString("E01A1");
					a0101 = this.getFrowset().getString("a0101");
				}
			}
		} catch (Exception e) {

		} finally {
			if (b0110 != null && b0110.trim().length() > 0)
				b0110 = AdminCode.getCode("UN", b0110) != null ? AdminCode
						.getCode("UN", b0110).getCodename() : " ";
			if (e0122 != null && e0122.trim().length() > 0)
				e0122 = AdminCode.getCode("UM", e0122) != null ? AdminCode
						.getCode("UM", e0122).getCodename() : " ";
			if (e01a1 != null && e01a1.trim().length() > 0)
				e01a1 = AdminCode.getCode("@K", e01a1) != null ? AdminCode
						.getCode("@K", e01a1).getCodename() : " ";
			if (a0101 != null && a0101.trim().length() > 0)
				a0101 = a0101 != null ? a0101 : " ";
		}

		if (b0110 == null) {
			name.append("");
		} else {
			name.append(b0110);
		}		
		name.append("/");
		if (e0122 == null) {
			name.append("");
		} else {
			name.append(e0122);
		}
		name.append("/");
		if (e01a1 == null) {
			name.append("");
		} else {
			name.append(e01a1);
		}
		name.append("/");
		if (a0101 == null) {
			name.append("");
		} else {
			name.append(a0101);
		}

		return name.toString();
	}

	private A0100Bean getA0100bean(String chg_id) {
		StringBuffer sql = new StringBuffer();
		sql = new StringBuffer();
		sql
				.append("select a0100,nbase,b0110,e0122,e01a1,a0000,a0101 from t_hr_mydata_chg");
		sql.append(" where chg_id='" + chg_id + "'");
		A0100Bean bean = new A0100Bean();
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql.toString());
			if (rs.next()) {
				bean.setA0000(rs.getString("a0000"));
				bean.setA0101(rs.getString("a0101"));
				bean.setB0110(rs.getString("b0110"));
				bean.setE0122(rs.getString("e0122"));
				bean.setE01a1(rs.getString("e01a1"));
				bean.setA0100(rs.getString("a0100"));
				bean.setNbase(rs.getString("nbase"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	/**
	 * 更新多媒体的审批状态
	 * 
	 * @param state
	 *            0活null为编辑，1为报批，2为驳回，3为批准
	 * @param bean
	 */
	private void mediaProve(String state, A0100Bean bean) {
		StringBuffer sql = new StringBuffer();
		sql.append("update ");
		sql.append(bean.getNbase());
		sql.append("A00 set state='");
		sql.append(state);
		sql.append("' where a0100='");
		sql.append(bean.getA0100());
		sql.append("' and state='1'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(sql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除报批的多媒体
	 * 
	 * @param state
	 *            0活null为编辑，1为报批，2为驳回，3为批准
	 * @param bean
	 */
	private void deleteMedia(A0100Bean bean) {
		StringBuffer sql = new StringBuffer();
		sql.append("delete ");
		sql.append(bean.getNbase());
		sql.append("A00 ");
		sql.append("where a0100='");
		sql.append(bean.getA0100());
		sql.append("' and state='1'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			dao.update(sql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
