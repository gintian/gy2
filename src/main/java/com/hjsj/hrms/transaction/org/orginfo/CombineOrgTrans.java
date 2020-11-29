/*
 * Created on 2005-12-20
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.info.OrgInfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.SqlDifference;
import com.hjsj.hrms.valueobject.common.OrganizationView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.tablemodel.ModelField;
import com.hrms.frame.dao.tablemodel.TableModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CombineOrgTrans extends IBusiness {

	private boolean version = false;
	private String end_date;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		if (this.userView.getVersion() >= 50) {
			version = true;
		}
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String selectcodeitems = (String) hm.get("selectcodeitemids");
		selectcodeitems = /*PubFunc.ToGbCode*/com.hrms.frame.codec.SafeCode.decode(selectcodeitems);
		String selects[] = selectcodeitems.split("`");
		ArrayList delorglist = new ArrayList();
		ArrayList peopleOrgList = new ArrayList();//人员变动前的机构 xuj 2010-4-28
		for (int i = 0; i < selects.length; i++) {
			String item = selects[i];
			String[] items = item.split(":");
			if (items.length == 3) {
				RecordVo vo = new RecordVo("organization");
				OrganizationView orgview=new OrganizationView();
				vo.setString("codeitemid", items[0]);
				orgview.setCodeitemid(items[0]);
				vo.setString("codeitemdesc", items[1]);
				vo.setString("codesetid", items[2]);
				orgview.setCodesetid(items[2]);
				delorglist.add(vo);
				peopleOrgList.add(orgview);
			}
		}
		this.getFormHM().put("peopleOrg", "combine");
		this.getFormHM().put("peopleOrgList", peopleOrgList);
		this.peopleOrgChange();
		//ArrayList delorglist=(ArrayList)this.getFormHM().get("selectedlist");
		String combinecodeitemid = (String) this.getFormHM().get(
				"combinecodeitemid");
		end_date = (String) this.getFormHM().get("end_date");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		String date = sdf.format(calendar.getTime());
		end_date = end_date != null && end_date.length() > 9 ? end_date : date;
		if (delorglist == null || delorglist.size() == 0)
			return;
		
		ArrayList srcList = new ArrayList();
		ArrayList codelist = new ArrayList();
		String firstNodeCode = "";
		// RecordVo vo1=(RecordVo)delorglist.get(0);
		// firstNodeCode =
		// vo1.getString("codesetid")+vo1.getString("codeitemid");
		for (int i = delorglist.size() - 1; i >= 0; i--) {
			RecordVo vo = (RecordVo) delorglist.get(i);
			codelist
					.add(vo.getString("codesetid") + vo.getString("codeitemid"));
		}
		/*
		 * for(int i=0;i<delorglist.size();i++){ RecordVo
		 * vo=(RecordVo)delorglist.get(i);
		 * combineorg[i]=vo.getString("codeitemid"); if(i==0)
		 * firstset=vo.getString("codesetid"); else srcList.add(vo);
		 * if(!firstset.equals(vo.getString("codesetid"))) throw
		 * GeneralExceptionHandler.Handle(new
		 * GeneralException("",ResourceFactory.getProperty("label.org.nocombineorg"),"","")); }
		 */
		// 判断新的合并后机构编码是否是新机构编码
		boolean flag = true;
		String codesetid = (String) this.getFormHM().get("codesetid");
		for (int i = 0; i < delorglist.size(); i++) {
			RecordVo vo = (RecordVo) delorglist.get(i);
			if (vo.getString("codeitemid").equals(combinecodeitemid)) {
				flag = false;
			}
		}
		String[] combineorg =null;
		if(flag){
			combineorg = new String[delorglist.size()+1];
			combineorg[0]=combinecodeitemid;
			firstNodeCode = codesetid+ combinecodeitemid;
			for (int i = 0; i < delorglist.size(); i++) {
				RecordVo vo = (RecordVo) delorglist.get(i);
				combineorg[i + 1] = vo.getString("codeitemid");
				srcList.add(vo);
			}
		}else{
			combineorg = new String[delorglist.size()];
			for (int i = 0; i < delorglist.size(); i++) {
				RecordVo vo = (RecordVo) delorglist.get(i);
				if (vo.getString("codeitemid").equals(combinecodeitemid)) {
					combineorg[0] = vo.getString("codeitemid");
					firstNodeCode = vo.getString("codesetid")
							+ vo.getString("codeitemid");
					delorglist.remove(i);
				}
			}
			if (combineorg[0] != null && combineorg[0].trim().length() > 0) {
	
			} else {
				combineorg[0] = ((RecordVo) delorglist.get(0))
						.getString("codeitemid");
				delorglist.remove(0);
			}
			for (int i = 0; i < delorglist.size(); i++) {
				RecordVo vo = (RecordVo) delorglist.get(i);
				combineorg[i + 1] = vo.getString("codeitemid");
				srcList.add(vo);
			}
		}
		String tarCodeitemdesc = (String) this.getFormHM().get(
				"tarcodeitemdesc");
		tarCodeitemdesc = PubFunc.splitString(tarCodeitemdesc, 50);
		/*
		 * { A0000 大排序
		 * 
		 * CodeItemId A0000 ------------------- 1 1 11 2 12 3 2 4 22 5 21 6 3 7
		 * 31 8 32 9 4 10 42 11 45 12
		 * 
		 * 
		 * 机构合并时，A0000 调整: 1. 计算源节点子节点数 srcChildCount 2. 得到 NewA0000
		 * 即：目的节点最后一个子节点的 A0000 + 1 3. 将 A0000 >= NewA0000 的节点的 A0000 增加
		 * srcChildCount 4. 更新源节点所有子节点的 A0000 从 NewA0000 开始编号，并保持原 A0000 顺序
		 * 
		 * 机构划转时，A0000 调整: 1. 计算源节点子节点数 srcCount (包括源节点) 2. 得到 NewA0000
		 * 即：目的节点最后一个子节点的 A0000 + 1 3. 将 A0000 >= NewA0000 的节点的 A0000 增加
		 * srcCount 4. 更新源节点及所有子节点的 A0000 从 NewA0000 开始编号，并保持原 A0000 顺序
		 * 
		 * 调整顺序, 将源机构插入到目的机构之前: 1. 计算源节点子节点数 srcCount (包括源节点) 2. 得到 DestA0000
		 * 即：目的节点的 A0000 3. 将 A0000 >= DestA0000 的节点的 A0000 增加 srcCount 4.
		 * 更新源节点及所有子节点的 A0000 从 DestA0000 开始编号，并保持原 A0000 顺序
		 * 
		 *  }
		 */
		// if(combineorg.length>0)
		// updateA0000_Combine(srcList,combineorg[0]);
		// 合并多个机构
		
		if (flag) {
			String grade = (String) this.getFormHM().get("grade");
			
			String code = (String) this.getFormHM().get("code");
			String corcode = (String) this.getFormHM().get("corcode");
			String end_date1 = "9999-12-31";// xuj 2009-10-30
									// 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.sql.Date start_date = null;
			java.sql.Date end_date = null;
			
			
			try {
				Date newStartDate = sdf.parse(this.end_date);
				Calendar ca = Calendar.getInstance();
				ca.setTime(newStartDate);
				ca.add(Calendar.DAY_OF_MONTH,1);
				
				start_date = new java.sql.Date(ca.getTimeInMillis());
				end_date = new java.sql.Date(sdf.parse(end_date1).getTime());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			StringBuffer sqlstr = new StringBuffer();

			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				String pid ="";
				if (code != null && code.trim().length() > 0)
					pid = code.toUpperCase();
				else
					pid = (combinecodeitemid).toUpperCase();
				sqlstr.delete(0, sqlstr.length());
//				sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date)values(?,?,?,?,?,?,?,?,?,?,?)");
				//添加levelA0000 统计排序字段值  wangb 20170807 
				sqlstr.append("insert into organization(codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,A0000,corcode,start_date,end_date,levelA0000)values(?,?,?,?,?,?,?,?,?,?,?,?)");
				ArrayList sqlvalue = new ArrayList();
				sqlvalue.add(codesetid);
				sqlvalue.add(combinecodeitemid.toUpperCase());
				sqlvalue.add(tarCodeitemdesc);
				cat.debug("-------code------------>" + code);
				
				sqlvalue.add(pid);
				sqlvalue.add(combinecodeitemid.toUpperCase());
				sqlvalue.add(null);
				sqlvalue.add(grade);
				sqlvalue.add(getMaxA0000(code));
				sqlvalue.add(corcode);
				sqlvalue.add(start_date);
				sqlvalue.add(end_date);
				sqlvalue.add(getMaxLevelA0000(code));//添加 levelA0000 同级排序值  wangb 20170807
				dao.insert(sqlstr.toString(), sqlvalue);

				try{
					String sql = null;
					SimpleDateFormat modtimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					calendar = Calendar.getInstance();
					date = modtimeSdf.format(calendar.getTime());
					boolean insertFlag = false;
					if("@K".equalsIgnoreCase(codesetid)){
							  sql = "select e01a1 from K01 where e01a1='"+combinecodeitemid.toUpperCase()+"'";
							  this.frecset = dao.search(sql);
							  if(this.frecset.next()){
								  RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
									if(pos_code_field_constant_vo!=null)
									{
									  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
									  FieldItem pos_code_fieldItem = DataDictionary.getFieldItem(pos_code_field);
									  if(pos_code_field!=null&&pos_code_field.length()>1 && pos_code_fieldItem != null && "1".equals(pos_code_fieldItem.getUseflag())){
										  sql="update K01 set e0122='"+pid+"',"+pos_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1 like '"+(combinecodeitemid).toUpperCase()+"%'";
									  }else{
										  sql="update K01 set e0122='"+pid+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1 like '"+(combinecodeitemid).toUpperCase()+"%'";
									  }
									}else{
										sql="update K01 set e0122='"+pid+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1 like '"+(combinecodeitemid).toUpperCase()+"%'";
									}
							  }else{
								  RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
								  insertFlag = true;
								  if(pos_code_field_constant_vo!=null)
									{
									  String  pos_code_field=pos_code_field_constant_vo.getString("str_value");
									  FieldItem pos_code_fieldItem = DataDictionary.getFieldItem(pos_code_field);
									  if(pos_code_field!=null&&pos_code_field.length()>1 && pos_code_fieldItem != null && "1".equals(pos_code_fieldItem.getUseflag())){
										  sql = "insert into K01(e0122,e01a1,"+pos_code_field+",createusername,modusername,createtime,modtime) values ('"+pid+"','"+(combinecodeitemid).toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
									  }else
										  sql = "insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values ('"+pid+"','"+(combinecodeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
									}else
										sql = "insert into K01(e0122,e01a1,createusername,modusername,createtime,modtime) values ('"+pid+"','"+(combinecodeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
									
							  }
							  dao.update(sql);
							  if(insertFlag) {
								  OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());	
								  orgInfoUtils.updateSequenceableValue(pid, "k01", "0");
							  }
						
					}else{
							  sql = "select b0110 from B01 where b0110='"+(combinecodeitemid).toUpperCase()+"'";
							  this.frecset = dao.search(sql);
							  if(this.frecset.next()){
								  RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
									if(unit_code_field_constant_vo!=null)
									{
									  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
									  FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
									  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){
										  sql="update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+","+unit_code_field+"='"+corcode+"' where b0110 like '"+(combinecodeitemid).toUpperCase()+"%'";
									  }else
										  sql="update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where b0110 like '"+(combinecodeitemid).toUpperCase()+"%'";
									}else
										sql="update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where b0110 like '"+(combinecodeitemid).toUpperCase()+"%'";
							  }else{
								  RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
								  insertFlag = true;
								  if(unit_code_field_constant_vo!=null)
									{
									  String  unit_code_field=unit_code_field_constant_vo.getString("str_value");
									  FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
									  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){
										  sql = "insert into B01(b0110,"+unit_code_field+",createusername,modusername,createtime,modtime) values ('"+(combinecodeitemid).toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
									  }else
										  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+(combinecodeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
									}else
										sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+(combinecodeitemid).toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
								 
							  }
							  dao.update(sql);
							  if(insertFlag) {
								  OrgInfoUtils orgInfoUtils=new OrgInfoUtils(this.getFrameconn());	
								  orgInfoUtils.updateSequenceableValue(combinecodeitemid, "b01", "0");
							  }
					}
				}catch(Exception e){
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				try {
					// this.getFrameconn().commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String issuperuser = "";
			String manageprive = "";
			if (userView.isSuper_admin()) {
				issuperuser = "1";
				manageprive = userView.getManagePrivCode()
						+ userView.getManagePrivCodeValue();
			} else if (userView.getStatus() == 4 || userView.getStatus() == 0) {
				issuperuser = "0";
				manageprive = userView.getManagePrivCode()
						+ userView.getManagePrivCodeValue();
			} else {
				issuperuser = "0";
				manageprive = userView.getManagePrivCode() + "no";
			}
			this.getFormHM().put("isnewcombineorg", "yes");
			this.getFormHM().put("issuperuser", issuperuser);
			this.getFormHM().put("manageprive", manageprive);
		}else{
			try{
				String sqlK01 = "";
				String sqlB01 = "";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				calendar = Calendar.getInstance();
				date = sdf.format(calendar.getTime());
				Timestamp timesTamp=DateUtils.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				ArrayList sqlvalue=new ArrayList();
				sqlK01="update K01 set createusername=?,modusername=?,modtime=? where e01a1 like ?";
				sqlB01="update B01 set createusername=?,modusername=?,modtime=? where b0110 like ?";
				sqlvalue.add(this.userView.getUserName());
				sqlvalue.add(this.userView.getUserName());
				if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
						sqlvalue.add(timesTamp);
				  }else {
					  sqlvalue.add(date);
				  }
				
				sqlvalue.add(combinecodeitemid.toUpperCase()+"%");
				if ("@K".equalsIgnoreCase(codesetid)) {
					dao.update(sqlK01,sqlvalue);
				}else {
					dao.update(sqlK01,sqlvalue);
					dao.update(sqlB01,sqlvalue);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			this.getFormHM().put("isnewcombineorg", "no");
		}
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			//removeCodeitem(combineorg, dao);
			Combineorg(combineorg, tarCodeitemdesc);
			checkorg(dao);
			addCodeitem(combineorg[0], dao);
			this.getFormHM().put("codelist", codelist);
			this.getFormHM().put("isrefresh", "combineorg");
			this.getFormHM().put("combinetext", tarCodeitemdesc);
			this.getFormHM().put("firstNodeCode", firstNodeCode);
			ArrayList msgb0110 = new ArrayList();
			//变动信息记录到被合并和合并后的机构
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			calendar = Calendar.getInstance();
			date = sdf.format(calendar.getTime());
			Timestamp timesTamp=DateUtils.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
			for(int i=0;i<combineorg.length;i++){
				msgb0110.add(combineorg[i]);
				String sqlK01 = "";
				String sqlB01 = "";
				ArrayList sqlvalue=new ArrayList();
				sqlK01="update K01 set createusername=?,modusername=?,modtime=? where e01a1 like ?";
				sqlB01="update B01 set createusername=?,modusername=?,modtime=? where b0110 like ?";
				sqlvalue.add(this.userView.getUserName());
				sqlvalue.add(this.userView.getUserName());
				if(Sql_switcher.searchDbServer() != Constant.MSSQL) {
						sqlvalue.add(timesTamp);
				  }else {
					  sqlvalue.add(date);
				  }
				
				sqlvalue.add(combineorg[i]+"%");
				if ("@K".equalsIgnoreCase(codesetid)) {
					dao.update(sqlK01,sqlvalue);
				}else {
					dao.update(sqlK01,sqlvalue);
					dao.update(sqlB01,sqlvalue);
				}
			}
			this.getFormHM().put("msgb0110",msgb0110);
			//合并机构levelA0000值更新   wangb 20170807
			combineSearchOrg(combinecodeitemid);
		} catch (Exception e) {
			this.getFormHM().put("isrefresh", "nocombineorg");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	private String getMaxA0000(String descode) throws GeneralException
	{
		String a0000="1";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
		   this.frowset=dao.search("select max(a0000) as a0000 from organization where codeitemid like '" + descode + "%'");
		   if(this.frowset.next())
		   {
			   a0000=String.valueOf(this.frowset.getInt("a0000") + 1);
			   dao.update("update organization set a0000=a0000 + 1 where a0000>" + this.frowset.getInt("a0000"));
		   }
		   else
			   dao.update("update organization set a0000=a0000 + 1 where a0000>0"); 
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return a0000;
	}
	private void addCodeitem(String combineorg, ContentDAO dao)
			throws Exception {
		try {
			this.frowset = dao
					.search("select * from organization where codeitemid like '"
							+ combineorg + "%'");
			while (this.frowset.next()) {
				CodeItem item = new CodeItem();
				item.setCodeid(this.frowset.getString("codesetid"));
				item.setCodename(this.frowset.getString("codeitemdesc"));
				item.setPcodeitem(this.frowset.getString("parentid"));
				item.setCcodeitem(this.frowset.getString("childid"));
				item.setCodeitem(this.frowset.getString("codeitemid"));
				item.setCodelevel(String.valueOf(this.frowset.getInt("grade")));
				AdminCode.addCodeItem(item);
				// AdminCode.updateCodeItemDesc(this.frowset.getString("codesetid"),this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}


	private void checkorg(ContentDAO dao) {
		StringBuffer sql = new StringBuffer();

		try {
			// 消除掉有子节点childid不正确的
			sql.delete(0, sql.length());
			sql.append("UPDATE ");
			sql
					.append("organization SET childid =(SELECT MIN(codeitemid) FROM ");
			sql.append("organization d");
			sql.append(" WHERE d.parentid = ");
			sql
					.append("organization.codeitemid AND d.parentid <> d.codeitemid and d.codesetid=organization.codesetid)");
			sql.append(" WHERE  EXISTS (SELECT * FROM ");
			sql.append("organization c");
			sql.append(" WHERE c.parentid = ");
			sql
					.append("organization.codeitemid AND c.parentid <> c.codeitemid and c.codesetid=organization.codesetid)");
			// System.out.println(sql.toString());
			dao.update(sql.toString());
			// 清除掉没有子节点childid不正确的
			/*
			 * sql.delete(0,sql.length()); sql.append("UPDATE ");
			 * sql.append("organization SET childid =codeitemid "); sql.append("
			 * WHERE not EXISTS (SELECT * FROM "); sql.append("organization c");
			 * sql.append(" WHERE c.parentid = ");
			 * sql.append("organization.childid AND organization.childid <>
			 * organization.codeitemid)");
			 */
			// System.out.println(sql.toString());
			// dao.update(sql.toString());
			
			StringBuffer updateParentcode=new StringBuffer();
     		updateParentcode.delete(0,updateParentcode.length());
     		updateParentcode.append("UPDATE ");
     		updateParentcode.append("organization SET childid =codeitemid  ");
     		updateParentcode.append(" WHERE not EXISTS (SELECT * FROM ");
     		updateParentcode.append("organization c");
     		updateParentcode.append(" WHERE c.parentid = ");
     		updateParentcode.append("organization.codeitemid and c.parentid<>c.codeitemid ) and organization.childid <> organization.codeitemid");
           // System.out.println(updateParentcode.toString());
		     dao.update(updateParentcode.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// 单个更新
	private void updateA0000_Combine(String codeitemid, String destOrgId)
			throws GeneralException {
		// for(int i=0;i<combineorg.size();i++)
		// {
		// RecordVo vo=(RecordVo)combineorg.get(i);
		// 不计算源节点节点数
		int srcChildCount = getOrgChildCount(codeitemid);
		// newA0000 = 目的节点最后一个子节点的 A0000 + 1
		int NewA0000 = getOrgChildA0000_Max(destOrgId) + 1;
		// 后面节点序号后移
		IncOrgA0000(NewA0000, srcChildCount);
		// 更新源节点所有子节点的 A0000 从 NewA0000 开始编号包括原节点
		updateOrgA0000(codeitemid, NewA0000, true);
		// }
	}

	private void updateOrgA0000(String orgId, int StartA0000,
			boolean IncludeRoot) throws GeneralException {
		String s;
		String strOn;
		String strWhere;
		String strSet;
		String strSelect;
		String tempTable; // 临时表
		StringBuffer sql = new StringBuffer();
		tempTable = "t#org_order_temp";
		sql.delete(0, sql.length());
		sql.append("drop table ");
		sql.append(tempTable);
		try {
			ExecuteSQL.createTable(sql.toString(), this.getFrameconn());
		} catch (Exception e) {
			// e.printStackTrace();
		}
		sql.delete(0, sql.length());
		// 创建排序临时表
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			sql.append("CREATE TABLE ");
			sql.append(tempTable);
			sql
					.append(" (orgId varchar(50), seqId Int IDENTITY(1,1), OrgA0000 Int)");
			break;
		}
		case Constant.DB2: {
			sql.append("CREATE TABLE ");
			sql.append(tempTable);
			sql
					.append(" (OrgId varchar(50),seqId INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),OrgA0000 INTEGER)");
			break;
		}
		case Constant.ORACEL: {
			sql.append("CREATE TABLE ");
			sql.append(tempTable);
			sql.append(" (orgId varchar2(50), seqId int, OrgA0000 int)");
			break;
		}
		}
		try {
			ExecuteSQL.createTable(sql.toString(), this.getFrameconn());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		strSelect = "select CodeItemId from Organization "
				+ " where CodeItemId Like '" + orgId + "%'";
		if (!IncludeRoot && (!"".equals(orgId))) // 不包括根节点
			strSelect = strSelect + " and CodeItemId <> '" + orgId + "'";
		strSelect = strSelect + " Order by A0000 ";

		sql.delete(0, sql.length());
		// 设置 SeqId
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			sql.append("Insert into ");
			sql.append(tempTable);
			sql.append("(orgId) ");
			sql.append(strSelect);
			break;
		}
		case Constant.DB2: {
			sql.append("Insert into ");
			sql.append(tempTable);
			sql.append("(orgId) ");
			sql.append(strSelect);
			break;
		}
		case Constant.ORACEL: {
			sql.append("Insert into ");
			sql.append(tempTable);
			sql.append(" (orgId, SeqId) ");
			sql.append(" select a.CodeItemId, RowNum from (");
			sql.append("   ");
			sql.append(strSelect);
			sql.append("   ) a"); // 别名
			break;
		}
		}
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		// 设置orgA0000 = seqId
		sql.delete(0, sql.length());
		sql.append("update ");
		sql.append(tempTable);
		sql.append(" set orgA0000 = SeqId");
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		sql.delete(0, sql.length());
		// 现在 orgA0000 从 1 开始, 更新 orgA0000 从 startA0000 开始
		if (StartA0000 > 1) {

			// MSSQL 中，不能直接更新标识列
			// s := 'update ' + tempTable + ' set SeqId = SeqId + ' +
			// IntToStr(startA0000 - 1);
			sql.append("update ");
			sql.append(tempTable);
			sql.append(" set orgA0000 = orgA0000 + ");
			sql.append(StartA0000 - 1);
			try {
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				dao.update(sql.toString());
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}

		// 更新 A0000
		strOn = "organization.CodeItemId = " + tempTable + ".orgId";
		strSet = "organization.A0000 = " + tempTable + ".orgA0000";
		strWhere = "organization.CodeItemId like '" + orgId + "%'";
		if (!IncludeRoot && (!"".equals(orgId))) { // 不包括根节点
			strWhere = strWhere + " and organization.CodeItemId <> '" + orgId
					+ "'";
		}
		sql.delete(0, sql.length());
		// 设置 SeqId
		/*
		 * 例： SQLSERVER: Update destTable Set destTable.F1 = srcTable.FA From
		 * DestTable Left Join srcTable On DestTable.FB = srcTable.FB WHERE
		 * srcWhere ACCESS: Update destTable Left Join srcTable On DestTable.FB =
		 * srcTable.FB Set destTable.F1 = srcTable.FA WHERE srcWhere WHERE
		 * destWhere
		 */
		/*
		 * 例: ORACLE, DB2: Update destTable Set (destTable.F1, destTable.F2) =
		 * (SELECT srcTable.F1, srcTable.F2 FROM srcTable WHERE strOn and
		 * srcWhere ) WHERE destWhere
		 */
		// getDBOper.RecordUpdate("organization", tempTable, strOn, strSet,
		// strWhere, strWhere);
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			sql.append("Update organization Set ");
			sql.append("organization.A0000 = " + tempTable + ".orgA0000");
			sql.append(" from organization left join ");
			sql.append(tempTable);
			sql.append(" on organization.CodeItemId = " + tempTable + ".orgId");
			sql.append(" where ");
			sql.append(strWhere);
			break;
		}
		case Constant.DB2: {
			sql.append("Update organization set ");
			sql.append("(organization.A0000)=(SELECT ");
			sql.append(tempTable);
			sql.append(".orgA0000 from ");
			sql.append(tempTable);
			sql.append(" where ");
			sql.append(strOn);
			sql.append(" and ");
			sql.append(strWhere);
			sql.append(")");
			sql.append(" where ");
			sql.append(strWhere);
			break;
		}
		case Constant.ORACEL: {
			sql.append("Update organization set ");
			sql.append("(organization.A0000)=(SELECT ");
			sql.append(tempTable);
			sql.append(".orgA0000 from ");
			sql.append(tempTable);
			sql.append(" where ");
			sql.append(strOn);
			sql.append(" and ");
			sql.append(strWhere);
			sql.append(")");
			sql.append(" where ");
			sql.append(strWhere);
			break;
		}
		}
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		// 删除临时表
		sql.delete(0, sql.length());
		sql.append("drop table ");
		sql.append(tempTable);
		try {
			ExecuteSQL.createTable(sql.toString(), this.getFrameconn());
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	// 后面节点序号后移
	private void IncOrgA0000(int StartA0000, int Increment)
			throws GeneralException {
		String strSet = "";
		if (Increment == 0)
			return;
		if (Increment > 0)
			strSet = "A0000 = A0000 + " + Increment;
		else
			strSet = "A0000 = A0000 - " + Math.abs(Increment);
		strSet = "update Organization set " + strSet + " where A0000 >= "
				+ StartA0000;
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(strSet);
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

	// newA0000 = 目的节点最后一个子节点的 A0000
	private int getOrgChildA0000_Max(String parentId) throws GeneralException {
		int n = 0;
		try {
			String s = "SELECT MAX(A0000) as a0000 FROM Organization  WHERE ParentId LIKE '"
					+ parentId + "%'";
			if (!"".equals(parentId))
				s = s + " AND CodeItemId <> '" + parentId + "'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(s);
			if (this.frowset.next()) {
				n = this.frowset.getInt("a0000");
			}
			/*
			 * 组织机构 2个机构合并为一个新机构时，
			 * 新机构没有子机构，n的值为0
			 * 重新查询新机构当前的a0000的值
			 * wangb 20170612
			 */
			if(n==0){
				String sql="SELECT MAX(A0000) as a0000 FROM Organization  WHERE codeItemId='"
					+ parentId + "'";
				this.frowset = dao.search(sql);
				if(this.frowset.next()){
					n = this.frowset.getInt("a0000");
				}
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		return n;
	}

	// 返回节点数
	private int getOrgChildCount(String parentId) throws GeneralException {
		int n = 0;
		try {
			String s = "SELECT count(*) as count FROM Organization WHERE codeitemid LIKE '"
					+ parentId + "%'";

			if (parentId != null && parentId.length() > 0) {
				// s = s + " AND CodeItemId <> '" + parentId + "'";
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(s);
			if (this.frowset.next()) {
				n = this.frowset.getInt("count");
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
		return n;
	}

	private void Combineorg(String[] combineorg, String tarCodeitemdesc)
			throws Exception {
		ArrayList IniOrgList = new ArrayList();
		String codesetid = "";
		String codeitemid = "";
		String parentid = "";
		String childid = "";
		String Level = "";
		boolean ishavechild = false;
		StringBuffer sqlstr = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());

		try {
			sqlstr.delete(0, sqlstr.length());
			sqlstr.append("select * from organization where codeitemid='");
			sqlstr.append(combineorg[0]);
			sqlstr.append("'");
			if (combineorg != null && combineorg.length > 0) {
				this.frowset = dao.search(sqlstr.toString());
				if (this.frowset.next()) {
					codesetid = this.frowset.getString("codesetid");
					codeitemid = this.frowset.getString("codeitemid");
					parentid = this.frowset.getString("parentid");
					childid = this.frowset.getString("childid");

					if (codeitemid.equals(childid))
						ishavechild = false;
					else
						ishavechild = true;
					Level = this.frowset.getString("grade");
				}
			}
			// 更新合并目标的codeitemdesc
			updateCombineOrgDesc(codesetid, combineorg, tarCodeitemdesc, dao);

			IniOrgList = getIniOrgList(dao, combineorg);
			ArrayList dblist = DataDictionary.getDbpreList();
			// System.out.println(IniOrgList.size());
			boolean version =false;
			if(this.getUserView().getVersion()>=50){
				version = true;
			}
			
			Date newStartDate = sdf.parse(this.end_date);
			Calendar ca = Calendar.getInstance();
			ca.setTime(newStartDate);
			ca.add(Calendar.DAY_OF_MONTH,1);
			String start_date = sdf.format(ca.getTime());
			
			for (int i = 0; i < IniOrgList.size(); i++) {
				OrganizationView orgview = (OrganizationView) IniOrgList.get(i);
				// if(i!=0)
				updateA0000_Combine(orgview.getCodeitemid(), combineorg[0]);
				// /System.out.println(ishavechild);
				String GetNextId = GetNextId(dao, orgview.getCodeitemid(),
						codeitemid, ishavechild, Level); // 获得下一个兄弟接点的codeitemid
				// /System.out.println("------GetNextId----->" + GetNextId);
				String temptable="t#"+this.userView.getUserName()+"_hr_org_c";
				if(version){
					// 删除临时表
					sqlstr.delete(0, sqlstr.length());
					sqlstr.append("drop table ");
					sqlstr.append(temptable);
					try {
						ExecuteSQL.createTable(sqlstr.toString(), this.getFrameconn());
					} catch (Exception e) {
						// e.printStackTrace();
					}
				//创建临时表存过度数据 
					sqlstr.delete(0, sqlstr.length());
					switch (Sql_switcher.searchDbServer()) {//复制数据
						case Constant.MSSQL: {
							sqlstr.append("select * into "+temptable);
							sqlstr.append(" from organization where ");
							//14-07-09 guodd 前面调用getIniOrgList(dao, combineorg)方法时已经把 子节点的 end_date修改了，所以这里再用date查询就什么都查不到了，没有历史了 
							//sqlstr.append("codeitemid like '"+orgview.getCodeitemid()+"%' and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
							sqlstr.append("codeitemid like '"+orgview.getCodeitemid()+"%' ");
							break;
						}
						case Constant.DB2: {
							
							break;
						}
						case Constant.ORACEL: {
							sqlstr.append("create table "+temptable);
							sqlstr.append(" as select * from organization where ");
							//14-07-09 guodd 前面调用getIniOrgList(dao, combineorg)方法时已经把 子节点的 end_date修改了，所以这里再用date查询就什么都查不到了，没有历史了
							//sqlstr.append("codeitemid like '"+orgview.getCodeitemid()+"%' and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
							sqlstr.append("codeitemid like '"+orgview.getCodeitemid()+"%' ");
							break;
						}
					}
					dao.update(sqlstr.toString());
				}
				
				sqlstr.delete(0, sqlstr.length());
				sqlstr.append("update organization set codeitemid='");
				sqlstr.append(GetNextId);
				sqlstr.append("',parentid='");
				sqlstr.append(codeitemid);
				sqlstr.append("',childid='");
				sqlstr.append(GetNextId);
				sqlstr.append("' "
						+ SqlDifference.getJoinSymbol()
						+ " "
						+ Sql_switcher.substr("childid", String.valueOf(orgview
								.getCodeitemid().length() + 1), Sql_switcher
								.length("childid")
								+ "-" + orgview.getCodeitemid().length()));
				// sqlstr.append(GetNextId.length() +1);
				// sqlstr.append(",len(childid)-");
				// sqlstr.append(GetNextId.length());
				sqlstr.append(",end_date="+Sql_switcher.dateValue("9999-12-31")+",start_date="+Sql_switcher.dateValue(start_date));
				sqlstr.append(" where codeitemid='");
				sqlstr.append(orgview.getCodeitemid());
				sqlstr.append("'");
				// System.out.println(sqlstr.toString());
				dao.update(sqlstr.toString());
				sqlstr.delete(0, sqlstr.length());
				sqlstr.append("update organization set codeitemid='");
				sqlstr.append(GetNextId);
				sqlstr.append("'  "
						+ SqlDifference.getJoinSymbol()
						+ " "
						+ Sql_switcher.substr("codeitemid", String
								.valueOf(orgview.getCodeitemid().length() + 1),
								Sql_switcher.length("codeitemid") + "-"
										+ orgview.getCodeitemid().length()));
				// sqlstr.append(GetNextId.length() +1);
				// sqlstr.append(",len(codeitemid)-");
				// sqlstr.append(GetNextId.length());
				sqlstr.append(",parentid='");
				sqlstr.append(GetNextId);
				sqlstr.append("' " + SqlDifference.getJoinSymbol() + " ");
				sqlstr.append(Sql_switcher.substr("parentid", String
						.valueOf(orgview.getCodeitemid().length() + 1),
						Sql_switcher.length("parentid") + "-"
								+ orgview.getCodeitemid().length()));
				sqlstr.append(",childid='");
				sqlstr.append(GetNextId);
				sqlstr.append("' "
						+ SqlDifference.getJoinSymbol()
						+ " "
						+ Sql_switcher.substr("childid", String.valueOf(orgview
								.getCodeitemid().length() + 1), Sql_switcher
								.length("childid")
								+ "-" + orgview.getCodeitemid().length()));
				// sqlstr.append(GetNextId.length() +1);
				// sqlstr.append(",len(childid)-");
				// sqlstr.append(GetNextId.length());
				sqlstr.append(",end_date="+Sql_switcher.dateValue("9999-12-31")+",start_date="+Sql_switcher.dateValue(start_date));
				sqlstr.append(" where codeitemid<>'");
				sqlstr.append(orgview.getCodeitemid());
				sqlstr.append("' and codeitemid like '");
				sqlstr.append(orgview.getCodeitemid());
				sqlstr.append("%'");
				// System.out.println(sqlstr.toString());
				dao.update(sqlstr.toString());
				
				
				
				if(version){
					
					RecordVo vo = new RecordVo("organization");
					boolean flag = vo.hasAttribute("guidkey");
					if(flag){
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update "+temptable+" set GUIDKEY=null ");
						dao.update(sqlstr.toString());
					}
					
					sqlstr.delete(0, sqlstr.length());//考回复制到临时表中的数据
					sqlstr.append("insert into organization select * from "+temptable);
					dao.update(sqlstr.toString());
					// 删除临时表
					sqlstr.delete(0, sqlstr.length());
					sqlstr.append("drop table ");
					sqlstr.append(temptable);
					try {
						ExecuteSQL.createTable(sqlstr.toString(), this.getFrameconn());
					} catch (Exception e) {
						// e.printStackTrace();
					}
				}
				if (codeitemid.equals(childid)) {
					sqlstr.delete(0, sqlstr.length());
					sqlstr.append("update  organization set childid='");
					sqlstr.append(GetNextId);
					sqlstr.append("' where codeitemid='");
					sqlstr.append(codeitemid);
					sqlstr.append("'");
					childid = GetNextId;
					ishavechild = true;
					dao.update(sqlstr.toString());
				}
				
				
				
				for (int j = 0; j < dblist.size(); j++) {
					if ("UN".equalsIgnoreCase(orgview.getCodesetid())) {
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("B0110='");
						sqlstr.append(GetNextId);
						sqlstr.append("' "
								+ SqlDifference.getJoinSymbol()
								+ " "
								+ Sql_switcher.substr("B0110", String
										.valueOf(orgview.getCodeitemid()
												.length() + 1), Sql_switcher
										.length("B0110")
										+ "-"
										+ orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where B0110 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E0122='");
						sqlstr.append(GetNextId);
						sqlstr.append("' "
								+ SqlDifference.getJoinSymbol()
								+ " "
								+ Sql_switcher.substr("E0122", String
										.valueOf(orgview.getCodeitemid()
												.length() + 1), Sql_switcher
										.length("E0122")
										+ "-"
										+ orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E0122 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E01A1='");
						sqlstr.append(GetNextId);
						sqlstr.append("' "
								+ SqlDifference.getJoinSymbol()
								+ " "
								+ Sql_switcher.substr("E01A1", String
										.valueOf(orgview.getCodeitemid()
												.length() + 1), Sql_switcher
										.length("E01A1")
										+ "-"
										+ orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());

					} else if ("UM".equalsIgnoreCase(orgview.getCodesetid())) {
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("B0110=");
						sqlstr
								.append(Sql_switcher
										.substr("'" + GetNextId + "'", "1",
												Sql_switcher.length("B0110")/*
																			 * Sql_switcher.length("'" +
																			 * GetNextId +
																			 * "'") +
																			 * "-(" +
																			 * Sql_switcher.length("E0122") +
																			 * "-" +
																			 * Sql_switcher.length("B0110") + ")
																			 */));
						// sqlstr.append(GetNextId);
						// sqlstr.append("', 1, LEN('");
						// sqlstr.append(GetNextId);
						// sqlstr.append("') - (LEN(E0122) - LEN(B0110))) ");
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E0122 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%' and B0110 IS NOT NULL");
						dao.update(sqlstr.toString());
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E0122='");
						sqlstr.append(GetNextId);
						sqlstr.append("' "
								+ SqlDifference.getJoinSymbol()
								+ " "
								+ Sql_switcher.substr("E0122", String
										.valueOf(orgview.getCodeitemid()
												.length() + 1), Sql_switcher
										.length("E0122")
										+ "-"
										+ orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E0122 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E01A1='");
						sqlstr.append(GetNextId);
						sqlstr.append("' "
								+ SqlDifference.getJoinSymbol()
								+ " "
								+ Sql_switcher.substr("E01A1", String
										.valueOf(orgview.getCodeitemid()
												.length() + 1), Sql_switcher
										.length("E01A1")
										+ "-"
										+ orgview.getCodeitemid().length()));
						// sqlstr.append(orgview.getCodeitemid().length() +1);
						// sqlstr.append(",len(E01A1)-");
						// sqlstr.append(orgview.getCodeitemid().length());
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());

					} else {
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("B0110=");
						/*sqlstr.append(Sql_switcher.substr(
								"'" + GetNextId + "'", "1", Sql_switcher
										.length("'" + GetNextId + "'")
										+ "-("
										+ Sql_switcher.length("E01A1")
										+ "-"
										+ Sql_switcher.length("B0110")
										+ ")"));*/
						sqlstr.append(Sql_switcher.substr(
								"'" + GetNextId + "'", "1", 
										Sql_switcher.length("B0110")));
						// sqlstr.append(GetNextId);
						// sqlstr.append("', 1, LEN('");
						// sqlstr.append(GetNextId);
						// sqlstr.append("') - (LEN(E01A1) - LEN(B0110))) ");
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like '");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%' and B0110 IS NOT NULL");
						dao.update(sqlstr.toString());
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E0122=");
						/*sqlstr.append(Sql_switcher.substr(
								"'" + GetNextId + "'", "1", Sql_switcher
										.length("'" + GetNextId + "'")
										+ "-("
										+ Sql_switcher.length("E01A1")
										+ "-"
										+ Sql_switcher.length("E0122")
										+ ")"));*/
						sqlstr.append(Sql_switcher.substr(
								"'" + GetNextId + "'", "1", Sql_switcher.length("E0122")));
						// sqlstr.append(GetNextId);
						// sqlstr.append("', 1, LEN('");
						// sqlstr.append(GetNextId);
						// sqlstr.append("') - (LEN(E01A1) - LEN(E0122))) ");
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like'");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%' and E0122 IS NOT NULL");
						dao.update(sqlstr.toString());
						sqlstr.delete(0, sqlstr.length());
						sqlstr.append("update ");
						sqlstr.append(dblist.get(j));
						sqlstr.append("A01 set ");
						sqlstr.append("E01A1='");
						sqlstr.append(GetNextId);
						sqlstr.append("' "
								+ SqlDifference.getJoinSymbol()
								+ " "
								+ Sql_switcher.substr("E01A1", String
										.valueOf(orgview.getCodeitemid()
												.length() + 1), Sql_switcher
										.length("E01A1")
										+ "-"
										+ orgview.getCodeitemid().length()));
						sqlstr.append(",modtime=");
						sqlstr.append(PubFunc.DoFormatSystemDate(true));
						sqlstr.append(",modusername='");
						sqlstr.append(userView.getUserName());
						sqlstr.append("' where E01A1 like'");
						sqlstr.append(orgview.getCodeitemid());
						sqlstr.append("%'");
						dao.update(sqlstr.toString());
					}
					dao.update(sqlstr.toString());
				}
				SysnK(orgview.getCodeitemid(), GetNextId, dao);
				// DeleteInfo(combineorg,dao);
			}
			if (!this.version) {
				DeleteInfo(combineorg, dao);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	private void DeleteInfo(String[] combineorg, ContentDAO dao) {
		StringBuffer delsql = new StringBuffer();
		List infoSetList = DataDictionary.getFieldSetList(
				Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
		ArrayList list = new ArrayList();
		for (int i = 1; i < combineorg.length; i++) {
			for (int k = 0; k < infoSetList.size(); k++) {
				FieldSet fieldset = (FieldSet) infoSetList.get(k);
				delsql.delete(0, delsql.length());
				delsql.append("delete ");
				delsql.append(fieldset.getFieldsetid());
				delsql.append(" where B0110 ='");
				delsql.append(combineorg[i]);
				delsql.append("'");
				list.add(delsql.toString());
			}
		}
		try {
			dao.batchUpdate(list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fromcode
	 * @param tocode
	 * @param dao
	 */
	private void SysnK(String fromcode, String tocode, ContentDAO dao)
			throws Exception {
		StringBuffer orgsql = new StringBuffer();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat modtimeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = modtimeSdf.format(calendar.getTime());
		Timestamp timesTamp=DateUtils.getTimestamp(date, "yyyy-MM-dd HH:mm:ss");
		List infoSetList = DataDictionary.getFieldSetList(
				Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
		ArrayList voList = new ArrayList();
		for (int k = 0; k < infoSetList.size(); k++) {
			FieldSet fieldset = (FieldSet) infoSetList.get(k);
			//String temptable = "temp_"+fieldset.getFieldsetid()+"_"+this.userView.getUserName();
			if(version){
				//创建临时表存过度数据 
				/*orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}
				orgsql.delete(0, orgsql.length());
					switch (Sql_switcher.searchDbServer()) {//复制数据
						case Constant.MSSQL: {
							orgsql.append("select * into "+temptable);
							orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("b0110 like '"+fromcode+"%'");
							break;
						}
						case Constant.DB2: {
							
							break;
						}
						case Constant.ORACEL: {
							orgsql.append("create table "+temptable);
							orgsql.append(" as select * from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("b0110 like '"+fromcode+"%'");
							break;
						}
					}
					dao.update(orgsql.toString());*/
				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				String sql = "select * from "+fieldset.getFieldsetid()+" where b0110 like '"+fromcode+"%'";
				voList = this.getRecordVoList(sql, dao, fieldset.getFieldsetid());
				/*orgsql.append("select * ");
				orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
				orgsql.append("b0110 like '"+fromcode+"%'");
				this.frecset = dao.search(orgsql.toString());*/
			}
			
			orgsql.delete(0, orgsql.length());
			orgsql.append("update  ");
			orgsql.append(fieldset.getFieldsetid());
			orgsql.append(" set B0110='");
			orgsql.append(tocode);
			orgsql.append("' "
					+ SqlDifference.getJoinSymbol()
					+ " "
					+ Sql_switcher.substr("b0110", String.valueOf(fromcode
							.length() + 1), Sql_switcher.length("b0110") + "-"
							+ fromcode.length()));
			orgsql.append(", modtime="+PubFunc.DoFormatSystemDate(true));
			orgsql.append(" where b0110 like '");
			orgsql.append(fromcode);
			orgsql.append("%'");
			dao.update(orgsql.toString());
			
			if(version){
				/*orgsql.delete(0, orgsql.length());//考回复制到临时表中的数据
				orgsql.append("insert into "+fieldset.getFieldsetid()+" select * from "+temptable);
				dao.update(orgsql.toString());
				// 删除临时表
				orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}*/
				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				for(int i=0;i<voList.size();i++){
					RecordVo vo = (RecordVo)voList.get(i);
					dao.addValueObject(vo);
				}
				voList.clear();
				
				
				/*ResultSetMetaData rsmd = this.frecset.getMetaData();
				orgsql.append("insert into "+fieldset.getFieldsetid()+" (");
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				for(int i=1;i<=rsmd.getColumnCount();i++){
					orgsql.append(rsmd.getColumnName(i)+",");
					sb.append("?,");
				}
				orgsql.setLength(orgsql.length()-1);
				sb.setLength(sb.length()-1);
				orgsql.append(") values "+sb.toString()+")");
				while(this.frecset.next()){
					ArrayList arr = new ArrayList();
					for(int i=1;i<=rsmd.getColumnCount();i++){
						arr.add(this.frecset.getObject(i));
					}
					dao.insert(orgsql.toString(), arr);
				}*/
			}
		}
		List infoSetListPos = DataDictionary.getFieldSetList(
				Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);
		for (int k = 0; k < infoSetListPos.size(); k++) {
			FieldSet fieldset = (FieldSet) infoSetListPos.get(k);
			//String temptable = "temp_"+fieldset.getFieldsetid()+"_"+this.userView.getUserName();
			if(version){
				/*orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}
				//创建临时表存过度数据 
				orgsql.delete(0, orgsql.length());
					switch (Sql_switcher.searchDbServer()) {//复制数据
						case Constant.MSSQL: {
							orgsql.append("select * into "+temptable);
							orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("e01a1 like '"+fromcode+"%'");
							break;
						}
						case Constant.DB2: {
							
							break;
						}
						case Constant.ORACEL: {
							orgsql.append("create table "+temptable);
							orgsql.append(" as select * from "+fieldset.getFieldsetid()+" where ");
							orgsql.append("e01a1 like '"+fromcode+"%'");
							break;
						}
					}
					dao.update(orgsql.toString());
					*/
				
				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				String sql = "select * from "+fieldset.getFieldsetid()+" where e01a1 like '"+fromcode+"%'";
				voList = this.getRecordVoList(sql, dao, fieldset.getFieldsetid());
				
				/*orgsql.append("select * ");
				orgsql.append(" from "+fieldset.getFieldsetid()+" where ");
				orgsql.append("e01a1 like '"+fromcode+"%'");
				this.frecset = dao.search(orgsql.toString());*/
			}
			
			orgsql.delete(0, orgsql.length());
			if("K01".equalsIgnoreCase(fieldset.getFieldsetid())){
				orgsql.append("update ");
				orgsql.append(fieldset.getFieldsetid());
				orgsql.append(" set e01a1='");
				orgsql.append(tocode);
				orgsql.append("' "
						+ SqlDifference.getJoinSymbol()
						+ " "
						+ Sql_switcher.substr("e01a1", String.valueOf(fromcode
								.length() + 1), Sql_switcher.length("e01a1") + "-"
								+ fromcode.length()));
				orgsql.append(",e0122='"+getTargetUMCodeitemid(tocode)+"'");
				orgsql.append(", modtime="+PubFunc.DoFormatSystemDate(true));
				orgsql.append(" where e01a1 like '");
				orgsql.append(fromcode);
				orgsql.append("%'");
			}else{
				orgsql.append("update ");
				orgsql.append(fieldset.getFieldsetid());
				orgsql.append(" set e01a1='");
				orgsql.append(tocode);
				orgsql.append("' "
						+ SqlDifference.getJoinSymbol()
						+ " "
						+ Sql_switcher.substr("e01a1", String.valueOf(fromcode
								.length() + 1), Sql_switcher.length("e01a1") + "-"
								+ fromcode.length()));
				orgsql.append(", modtime="+PubFunc.DoFormatSystemDate(true));
				orgsql.append(" where e01a1 like '");
				orgsql.append(fromcode);
				orgsql.append("%'");
			}
			dao.update(orgsql.toString());
			
			if(version){
				/*orgsql.delete(0, orgsql.length());//考回复制到临时表中的数据
				orgsql.append("insert into "+fieldset.getFieldsetid()+" select * from "+temptable);
				dao.update(orgsql.toString());
				// 删除临时表
				orgsql.delete(0, orgsql.length());
				orgsql.append("drop table ");
				orgsql.append(temptable);
				try {
					ExecuteSQL.createTable(orgsql.toString(), this.getFrameconn());
				} catch (Exception e) {
					// e.printStackTrace();
				}*/
				
				//xuj 2010-4-9 改进的方式 频繁的创建表再删除的方式倒换数据效率慢
				for(int i=0;i<voList.size();i++){
					RecordVo vo = (RecordVo)voList.get(i);
					dao.addValueObject(vo);
				}
				voList.clear();
				
				/*orgsql.delete(0, orgsql.length());
				ResultSetMetaData rsmd = this.frecset.getMetaData();
				orgsql.append("insert into "+fieldset.getFieldsetid()+" (");
				StringBuffer sb = new StringBuffer();
				sb.append("(");
				for(int i=1;i<=rsmd.getColumnCount();i++){
					orgsql.append(rsmd.getColumnName(i)+",");
					sb.append("?,");
				}
				orgsql.setLength(orgsql.length()-1);
				sb.setLength(sb.length()-1);
				orgsql.append(") values "+sb.toString()+")");
				while(this.frecset.next()){
					ArrayList arr = new ArrayList();
					for(int i=1;i<=rsmd.getColumnCount();i++){
						arr.add(this.frecset.getObject(i));
					}
					dao.insert(orgsql.toString(), arr);
				}*/
			}
		}
	}

	/**
	 * @param combineorg
	 * @param tarCodeitemdesc
	 * @param sqlstr
	 * @param dao
	 */
	private void updateCombineOrgDesc(String codesetid, String[] combineorg,
			String tarCodeitemdesc, ContentDAO dao) {
		try {
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("update organization set codeitemdesc='");
			sqlstr.append(tarCodeitemdesc);
			sqlstr.append("',end_date=" + Sql_switcher.dateValue("9999-12-31")//start_date="+ Sql_switcher.dateValue(sdf.format(new Date()))+ ",
					+ " where codeitemid='");
			sqlstr.append(combineorg[0]);
			sqlstr.append("'");
			dao.update(sqlstr.toString());
			sqlstr.setLength(0);
			sqlstr.append("update organization set ");
			sqlstr.append("end_date=" + Sql_switcher.dateValue("9999-12-31")//start_date="+ Sql_switcher.dateValue(sdf.format(new Date()))+ ",
					+ " where codeitemid like '");
			sqlstr.append(combineorg[0]);
			sqlstr.append("%' ");
			sqlstr.append("and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
			dao.update(sqlstr.toString());
			AdminCode.updateCodeItemDesc(codesetid, combineorg[0],
					tarCodeitemdesc);
		} catch (Exception e) {
			e.printStackTrace();
			// throw GeneralExceptionHandler.Handle(e);
		}
	}

	private ArrayList getIniOrgList(ContentDAO dao, String[] combineorg) {
		ArrayList IniOrgList = new ArrayList();
		StringBuffer sqlstr = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			for (int i = 1; i < combineorg.length; i++) {
				sqlstr.delete(0, sqlstr.length());
				sqlstr.append("select * from organization where parentid ='");
				sqlstr.append(combineorg[i]);
//				sqlstr.append("' and codeitemid<>parentid and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date order by codeitemid");
				sqlstr.append("' and codeitemid<>parentid and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date order by A0000");//合并时 按A0000 排序  wangb 20170830 31092
				this.frowset = dao.search(sqlstr.toString());
				while (this.frowset.next()) {
					OrganizationView orgview = new OrganizationView();
					orgview.setCodesetid(this.frowset.getString("codesetid"));
					orgview.setCodeitemid(this.frowset.getString("codeitemid"));
					cat.debug("------codeitemid------>"
							+ this.frowset.getString("codeitemid"));
					IniOrgList.add(orgview);

					/*
					 * CodeItem item=new CodeItem();
					 * item.setCodeid(this.frowset.getString("codesetid"));
					 * item.setCodename(this.frowset.getString("codeitemdesc"));
					 * item.setPcodeitem(this.frowset.getString("parentid"));
					 * item.setCcodeitem(this.frowset.getString("childid"));
					 * item.setCodeitem(this.frowset.getString("codeitemid"));
					 * item.setCodelevel(String.valueOf(this.frowset.getInt("grade")));
					 * AdminCode.removeCodeItem(item);
					 */

				}
				UpdatePersonAndBK(combineorg[0], combineorg[i], dao);

				/*
				 * this.frowset=dao.search("select * from organization where
				 * codeitemid='" + combineorg[i] + "'"); CodeItem item=new
				 * CodeItem();
				 * item.setCodeid(this.frowset.getString("codesetid"));
				 * item.setCodename(this.frowset.getString("codeitemdesc"));
				 * item.setPcodeitem(this.frowset.getString("parentid"));
				 * item.setCcodeitem(this.frowset.getString("childid"));
				 * item.setCodeitem(this.frowset.getString("codeitemid"));
				 * item.setCodelevel(String.valueOf(this.frowset.getInt("grade")));
				 * AdminCode.removeCodeItem(item);
				 */
				sqlstr.delete(0, sqlstr.length());
				if (this.version) {
					sqlstr.append("update organization set end_date="
							+ Sql_switcher.dateValue(this.end_date)
							+ " where codeitemid like '");
					sqlstr.append(combineorg[i]);
					sqlstr.append("%' and "+Sql_switcher.dateValue(sdf.format(new Date()))+" between start_date and end_date");
				} else {
					sqlstr
							.append("delete from organization where codeitemid='");
					sqlstr.append(combineorg[i]);
					sqlstr.append("'");
				}
				dao.delete(sqlstr.toString(), new ArrayList());
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return IniOrgList;
	}
	
	private void UpdatePersonAndBK(String targetcode, String combincode,
			ContentDAO dao) throws Exception {
		ArrayList dblist = DataDictionary.getDbpreList();
		StringBuffer sqlstr = new StringBuffer();
		try {
			sqlstr.delete(0, sqlstr.length());
			sqlstr.append("select * from organization where codeitemid ='");
			sqlstr.append(combincode);
			sqlstr.append("'");
			String codesetid = "";
			this.frowset = dao.search(sqlstr.toString());
			if (this.frowset.next()) {
				codesetid = this.frowset.getString("codesetid");
			}
			for (int j = 0; j < dblist.size(); j++) {
				if ("UN".equalsIgnoreCase(codesetid)) {
					sqlstr.delete(0, sqlstr.length());
					sqlstr.append("update ");
					sqlstr.append(dblist.get(j));
					sqlstr.append("A01 set ");
					sqlstr.append("B0110='");
					sqlstr.append(targetcode);
					// sqlstr.append("',");
					// sqlstr.append("E0122='");
					// sqlstr.append(e0122);
					// sqlstr.append("' " + SqlDifference.getJoinSymbol() + " "
					// +
					// Sql_switcher.substr("E0122",String.valueOf(combincode.length()
					// +1),Sql_switcher.length("E0122") + "-" +
					// combincode.length()));
					// sqlstr.append(",E01A1='");
					// sqlstr.append(e01a1);
					// sqlstr.append("' " + SqlDifference.getJoinSymbol() + " "
					// +
					// Sql_switcher.substr("E01A1",String.valueOf(combincode.length()
					// +1),Sql_switcher.length("E01A1") + "-" +
					// combincode.length())) ;

					sqlstr.append("',modtime=");
					sqlstr.append(PubFunc.DoFormatSystemDate(true));
					sqlstr.append(",modusername='");
					sqlstr.append(userView.getUserName());
					sqlstr.append("' where B0110='");
					sqlstr.append(combincode);
					sqlstr.append("'");
					dao.update(sqlstr.toString());
				} else if ("UM".equalsIgnoreCase(codesetid)) {
					sqlstr.delete(0, sqlstr.length());
					sqlstr.append("update ");
					sqlstr.append(dblist.get(j));
					sqlstr.append("A01 set ");
					sqlstr.append("E0122='");
					sqlstr.append(targetcode);
					// sqlstr.append("',E01A1='");
					// sqlstr.append(targetcode);
					// sqlstr.append("' " + SqlDifference.getJoinSymbol() + " "
					// +
					// Sql_switcher.substr("E01A1",String.valueOf(combincode.length()
					// +1),Sql_switcher.length("E01A1") + "-" +
					// combincode.length())) ;
					sqlstr.append("',modtime=");
					sqlstr.append(PubFunc.DoFormatSystemDate(true));
					sqlstr.append(",modusername='");
					sqlstr.append(userView.getUserName());
					sqlstr.append("' where E0122='");
					sqlstr.append(combincode);
					sqlstr.append("'");
					dao.update(sqlstr.toString());
				} else {
					sqlstr.delete(0, sqlstr.length());
					sqlstr.append("update ");
					sqlstr.append(dblist.get(j));
					sqlstr.append("A01 set ");
					sqlstr.append("E01A1='");
					sqlstr.append(targetcode);
					sqlstr.append("'");
					sqlstr.append(",modtime=");
					sqlstr.append(PubFunc.DoFormatSystemDate(true));
					sqlstr.append(",modusername='");
					sqlstr.append(userView.getUserName());
					sqlstr.append("' where E01A1='");
					sqlstr.append(combincode);
					sqlstr.append("'");
					dao.update(sqlstr.toString());
				}
			}
			// SysnKparentid(combincode,targetcode,dao);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	private String GetNextId(ContentDAO dao, String SrcCode, String DesCode,
			boolean ishavechild, String Level) {
		String strDesMaxChild = "";
		String result = "";
		if (ishavechild) {
			strDesMaxChild = getMaxChildid(dao, DesCode);
			// System.out.println("------strDesMaxChild1------>" +
			// strDesMaxChild);
		} else {
			strDesMaxChild = DesCode
					+ BackLevLenStr(dao, Integer.parseInt(Level));
			cat.debug("------strDesMaxChild2------>" + strDesMaxChild);
		}
		result = GetNextIdStr(SrcCode, DesCode, strDesMaxChild);
		cat.debug("------strDesMaxChild result------>" + result);
		return result;
	}

	private String getMaxChildid(ContentDAO dao, String codeitemid) {
		String maxchildid = "";
		StringBuffer sqlstr = new StringBuffer();
		try {
			// System.out.println("wlh"+ codeitemid);
			sqlstr.append("select * from organization where parentid='");
			sqlstr.append(codeitemid);
			sqlstr.append("' and codeitemid<>parentid order by codeitemid");
			this.frowset = dao.search(sqlstr.toString());
			while (this.frowset.next()) {
				if (this.frowset.getString("codeitemid").compareTo(maxchildid) > 0) {
					maxchildid = this.frowset.getString("codeitemid");
				}
			}
			cat.debug("---->combineorg maxchildid----->" + maxchildid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maxchildid;
	}

	private String BackLevLenStr(ContentDAO dao, int nLev) {
		int I;
		String Result = "";
		try {
			if (BackQryOnLev(dao, nLev + 1) == 0) {
				if (nLev == 1) {
					String strsql = "select codeitemid from organization where Grade="
							+ nLev;
					this.frowset = dao.search(strsql);
					if (this.frowset.next())
						for (I = 0; I < this.frowset.getString("codeitemid")
								.length(); I++) {
							Result = "0" + Result;
						}
				} else {
					String strsql = "select codeitemid from organization where Grade="
							+ (nLev - 1);
					this.frowset = dao.search(strsql);
					String StrParentId = "";
					if (this.frowset.next())
						StrParentId = this.frowset.getString("codeitemid");
					strsql = "select codeitemid from organization where Grade="
							+ nLev;
					this.frowset = dao.search(strsql);
					String StrNowId = "";
					if (this.frowset.next())
						StrNowId = this.frowset.getString("codeitemid");
					for (I = 0; I < StrNowId.length() - StrParentId.length(); I++)
						Result = "0" + Result;
				}
			} else {
				String strsql = "select codeitemid from organization where Grade="
						+ (nLev + 1);
				this.frowset = dao.search(strsql);
				String StrChildId = "";
				if (this.frowset.next())
					StrChildId = this.frowset.getString("codeitemid");
				strsql = "select codeitemid from organization where Grade="
						+ nLev;
				String StrNowId = "";
				this.frowset = dao.search(strsql);
				if (this.frowset.next())
					StrNowId = this.frowset.getString("codeitemid");
				for (I = 0; I < StrChildId.length() - StrNowId.length(); I++)
					Result = "0" + Result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Result;
	}

	private int BackQryOnLev(ContentDAO dao, int nLev) {
		try {
			String StrSql = "select count(*) as ncount from ORGANIZATION where Grade="
					+ nLev;
			this.frowset = dao.search(StrSql);
			if (this.frowset.next())
				return this.frowset.getInt("ncount");
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private String GetNextIdStr(String src, String des, String desMaxChild) {
		if ("".equals(desMaxChild)) // 如果是第一个子结点
		{
			return GetNext(src, des);
		} else {
			cat.debug("des max child --->" + desMaxChild + "  des----->" + des);
			return GetNext(desMaxChild, des);
		}
	}

	private String GetNext(String src, String des) {
		int nI, nTag;
		String ch;
		String result = "";
		nTag = 1; // 进位为1
		src = src.toUpperCase();
		for (nI = src.length(); nI > des.length(); nI--) {
			ch = src.substring(nI - 1, nI);
			if (nTag == 1)
				ch = GetNextChar(ch);
			result = ch + result;
			if ("0".equals(ch) && !"0".equals(src.subSequence(nI - 1, nI))) {
				nTag = 1;
			} else {
				nTag = 0;
			}

		}
		cat.debug("------ GetNext ----> " + result);
		return des + (result.length()==0?"01":result);
	}

	private String GetNextChar(String ch) // 获得下一个进位
	{
		String result = "";
		switch (ch.charAt(0)) {
		case '0': {
			result = "1";
			break;
		}
		case '1': {
			result = "2";
			break;
		}
		case '2': {
			result = "3";
			break;
		}
		case '3': {
			result = "4";
			break;
		}
		case '4': {
			result = "5";
			break;
		}
		case '5': {
			result = "6";
			break;
		}
		case '6': {
			result = "7";
			break;
		}
		case '7': {
			result = "8";
			break;
		}
		case '8': {
			result = "9";
			break;
		}
		case '9': {
			result = "A";
			break;
		}
		case 'A': {
			result = "B";
			break;
		}
		case 'B': {
			result = "C";
			break;
		}
		case 'C': {
			result = "D";
			break;
		}
		case 'D': {
			result = "E";
			break;
		}
		case 'E': {
			result = "F";
			break;
		}
		case 'F': {
			result = "G";
			break;
		}
		case 'G': {
			result = "H";
			break;
		}
		case 'H': {
			result = "I";
			break;
		}
		case 'I': {
			result = "J";
			break;
		}
		case 'J': {
			result = "K";
			break;
		}
		case 'K': {
			result = "L";
			break;
		}
		case 'L': {
			result = "M";
			break;
		}
		case 'M': {
			result = "N";
			break;
		}
		case 'N': {
			result = "O";
			break;
		}
		case 'O': {
			result = "P";
			break;
		}
		case 'P': {
			result = "Q";
			break;
		}
		case 'Q': {
			result = "R";
			break;
		}
		case 'R': {
			result = "S";
			break;
		}
		case 'S': {
			result = "T";
			break;
		}
		case 'T': {
			result = "U";
			break;
		}
		case 'U': {
			result = "V";
			break;
		}
		case 'V': {
			result = "W";
			break;
		}
		case 'W': {
			result = "X";
			break;
		}
		case 'X': {
			result = "Y";
			break;
		}
		case 'Y': {
			result = "Z";
			break;
		}
		case 'Z': {
			result = "0";
			break;
		}
		}
		return result;
	}
	private String getTargetUMCodeitemid(String code)
	{
		String pre="@K";
		String uncodeitemid="";
     	StringBuffer strsql=new StringBuffer();
     	ResultSet rs = null;
		try{
			
			
				ContentDAO db=new ContentDAO(this.getFrameconn());
				strsql.delete(0,strsql.length());
				strsql.append("select codesetid from organization where codeitemid='"+code+"'");
				rs = db.search(strsql.toString());
				while(rs.next()){
					pre=rs.getString("codesetid");
					uncodeitemid = code;
				}
				while("@K".equalsIgnoreCase(pre))
				{
					strsql.delete(0,strsql.length());
					strsql.append("select * from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");					
					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
					if(this.frowset.next())
					{
						pre=this.frowset.getString("codesetid");
						code=this.frowset.getString("parentid");
						if("@K".equalsIgnoreCase(pre))
							uncodeitemid=code;
					}			
				}				
			 
			}catch (SQLException sqle){
				sqle.printStackTrace();
			}		
		return uncodeitemid;
	}
	
	private ArrayList getRecordVoList(String sql,ContentDAO dao,String tablename) throws Exception {
		ArrayList voList=new ArrayList();
		try
	    {
	      DBMetaModel dbmeta = new DBMetaModel();
	      TableModel tableModel = dbmeta.searchTable(tablename.toLowerCase());
	      RowSet oSet = dao.search(sql);
	      ModelField[] fields = tableModel.getFields();
	      while(oSet.next()){
		      HashMap oMap = new HashMap();
		      for (int i = 0; i < fields.length; ++i)
		      {
		        if (fields[i].getFieldType() == 0)
		        {
		          if (oSet.getObject(fields[i].getTableField()) != null)
		          {
		            if (oSet.getObject(fields[i].getTableField()) instanceof Clob)
		            {
		              oMap.put(fields[i].getAttribute(), Sql_switcher.readMemo(oSet, fields[i].getTableField()));
		            }
		            else
		              oMap.put(fields[i].getAttribute(), oSet.getObject(fields[i].getTableField()));
		          }
		        }
		        else if ((fields[i].getFieldType() == 1) && 
		          (oSet.getObject(fields[i].getAttribute()) != null))
		        {
		          if (oSet.getObject(fields[i].getAttribute()) instanceof Clob)
		          {
		            oMap.put(fields[i].getAttribute(), Sql_switcher.readMemo(oSet, fields[i].getAttribute()));
		          }
		          else
		            oMap.put(fields[i].getAttribute(), oSet.getObject(fields[i].getAttribute()));
		        }
		      }
	
		      RecordVo ret = new RecordVo(tablename.toLowerCase());
		      ret.setValues(oMap);
		      voList.add(ret);
	      }
	    }
	    catch (SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw new SQLException("instantiate model class[" + tablename.toLowerCase() + "] failed");
	    }
	    catch (Exception e)
	    {
	      e.printStackTrace();
	      throw GeneralExceptionHandler.Handle(e);
	    }
		return voList;
	}
	/**
	 * 人员变动前的机构记录到选择的模板
	 * @throws GeneralException
	 */
	private void peopleOrgChange() throws GeneralException{
		try{
			String peopleOrg = (String) this.getFormHM().get("peopleOrg");
			ArrayList peopleOrgList = (ArrayList) this.getFormHM().get(
					"peopleOrgList");
			if (peopleOrg == null || "".equals(peopleOrg)
					|| peopleOrgList == null || peopleOrgList.size() == 0) {
				return;
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String tempid = "";
			if ("combine".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"combine");
				if (tempid == null || "".equals(tempid))
					return;
			} else if ("transfer".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"transfer");
				if (tempid == null || "".equals(tempid))
					return;
			} else if ("bolish".equalsIgnoreCase(peopleOrg)) {
				tempid = sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,
						"bolish");
				if (tempid == null || "".equals(tempid))
					return;
			}
			StringBuffer sql = new StringBuffer();
			ArrayList dblist = DataDictionary.getDbpreList();
			ContentDAO dao = new ContentDAO(this.frameconn);
			int nyear = 0;
			int nmonth = 0;
			nyear = DateUtils.getYear(new Date());
			nmonth = DateUtils.getMonth(new Date());
			RecordVo vo = new RecordVo("tmessage");
			vo.setString("username", "");
			vo.setInt("state", 0);
			vo.setInt("nyear", nyear);
			vo.setInt("nmonth", nmonth);
			vo.setInt("type", 0);
			vo.setInt("flag", 0);
			vo.setInt("sourcetempid", 0);
			vo.setInt("noticetempid", Integer.parseInt(tempid));
			StringBuffer changepre = new StringBuffer();
			StringBuffer change = new StringBuffer();
			for (int i = 0; i < peopleOrgList.size(); i++) {
				OrganizationView orgview = (OrganizationView) peopleOrgList
						.get(i);
				String codesetid = orgview.getCodesetid();
				String codeitemid = orgview.getCodeitemid();
				for (int n = 0; n < dblist.size(); n++) {
					String pre = (String) dblist.get(n);
					sql.setLength(0);
					sql.append("select a0100,a0101,b0110,e0122,e01a1 from "
							+ pre + "A01 where ");
					if ("UN".equalsIgnoreCase(codesetid)) {
						sql.append("b0110 like '" + codeitemid + "%'");
					} else if ("UM".equalsIgnoreCase(codesetid)) {
						sql.append("e0122 like '" + codeitemid + "%'");
					} else if ("@K".equalsIgnoreCase(codesetid)) {
						sql.append("e01a1 ='" + codeitemid + "'");
					}
					this.frowset = dao.search(sql.toString());
					vo.setString("db_type", pre);
					while (this.frowset.next()) {
						String a0100 = this.frowset.getString("a0100");
						String a0101 = this.frowset.getString("a0101");
						a0101 = a0101 != null ? a0101 : "";
						String b0110 = this.frowset.getString("b0110");
						String e0122 = this.frowset.getString("e0122");
						String e01a1 = this.frowset.getString("e01a1");
						vo.setString("a0100", a0100);
						vo.setString("a0101", a0101);
						changepre.setLength(0);
						change.setLength(0);
						if (b0110 != null && !"".equals(b0110)) {
							changepre.append("B0110=" + b0110 + ",");
							change.append("B0110,");
						}
						if (e0122 != null && !"".equals(e0122)) {
							changepre.append("E0122=" + e0122 + ",");
							change.append("E0122,");
						}
						if (e01a1 != null && !"".equals(e01a1)) {
							changepre.append("E01A1=" + e01a1 + ",");
							change.append("E01A1,");
						}
						if (a0101 != null && !"".equals(a0101)) {
							changepre.append("A0101=" + a0101 + ",");
							change.append("A0101,");
						}
						vo.setString("changepre", changepre.toString());
						vo.setString("change", change.toString());
						/** max id access mssql此字段是自增长类型 */
						if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
							int nid = DbNameBo.getPrimaryKey("tmessage", "id",
									this.frameconn);
							vo.setInt("id", nid);
						}
						dao.addValueObject(vo);
					}
				}
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 同级机构排序获取levelA0000最大值   wangb    20170708
	 * @param descode
	 * @return
	 * @throws GeneralException
	 */
	private String getMaxLevelA0000(String descode)throws GeneralException
	{
		String levelA0000 = "1";
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frowset=dao.search("SELECT MAX(LEVELA0000) as levelA0000 FROM organization where PARENTID='"+descode+"'");//多了from 30649 wangb 20170816
			if(this.frowset.next())
				levelA0000 = String.valueOf(this.frowset.getInt("levelA0000") + 1);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return levelA0000;
	}
	/**
	 * 合并机构后排序levelA0000值  wangb 20170807
	 * @param combinecodeitemid  合并机构 codeitemid
	 */
	private void combineSearchOrg(String combinecodeitemid)throws GeneralException{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql="SELECT CODEITEMID FROM ORGANIZATION WHERE PARENTID='"+ combinecodeitemid +"' ORDER BY A0000";
		try {
			this.frowset=dao.search(sql);
			String levelA0000 = "1";
			while(this.frowset.next()){
				dao.update("UPDATE ORGANIZATION SET levelA0000="+ levelA0000 +" where CODEITEMID='"+ this.frowset.getString("CODEITEMID") +"'");
				levelA0000 = String.valueOf(Integer.parseInt(levelA0000) + 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
}
