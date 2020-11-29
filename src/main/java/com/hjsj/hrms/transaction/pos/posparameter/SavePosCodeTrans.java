package com.hjsj.hrms.transaction.pos.posparameter;

import com.hjsj.hrms.businessobject.duty.MoveSdutyBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class SavePosCodeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String ps_code = (String) this.getFormHM().get("ps_code");
		ps_code = PubFunc.hireKeyWord_filter_reback(ps_code);
		String ps_level_code = (String) this.getFormHM().get("ps_level_code");
		ps_level_code = PubFunc.hireKeyWord_filter_reback(ps_level_code);
		String ps_c_code = (String) this.getFormHM().get("ps_c_code");
		ps_c_code = PubFunc.hireKeyWord_filter_reback(ps_c_code);
		String ps_c_level_code = (String) this.getFormHM().get("ps_c_level_code");
		ps_c_level_code = PubFunc.hireKeyWord_filter_reback(ps_c_level_code);
		String unit_code_field = (String) this.getFormHM().get("unit_code_field");
		unit_code_field = PubFunc.hireKeyWord_filter_reback(unit_code_field);
		String pos_code_field = (String) this.getFormHM().get("pos_code_field");
		pos_code_field = PubFunc.hireKeyWord_filter_reback(pos_code_field);
		
		// 原来设置单位转换代码的字段
		String oldUnits = (String) this.getFormHM().get("oldUnits");
		oldUnits = PubFunc.hireKeyWord_filter_reback(oldUnits);
		// 原来设置岗位转换代码的字段
		String oldPosts = (String) this.getFormHM().get("oldPosts");
		oldPosts = PubFunc.hireKeyWord_filter_reback(oldPosts);
		try{
			excDao("PS_CODE",ps_code);
			excDao("PS_LEVEL_CODE",ps_level_code);
			excDao("PS_C_CODE",ps_c_code);
			excDao("PS_C_LEVEL_CODE",ps_c_level_code);
			excDao("UNIT_CODE_FIELD",unit_code_field);
			excDao("POS_CODE_FIELD",pos_code_field);
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}
		
		//将ps_c_code推到数据字典h0100 的codesetid中
		ArrayList itemlist = DataDictionary.getFieldList("H01", Constant.ALL_FIELD_SET);
		for(int i=0;itemlist!=null && i<itemlist.size();i++){
			FieldItem item = (FieldItem)itemlist.get(i);
			if("H0100".equals(item.getItemid().toUpperCase())){
				itemlist.remove(i);
				item.setCodesetid(ps_c_code);
				itemlist.add(i, item);
				break;
			}
		}
		
		/*
		 * 将单位和岗位的转换代码保存到b01和k01中对应的字段中
		 *2010-2-21 wangzhongjun
		 */
		this.handleTransCode(oldUnits, unit_code_field, oldPosts, pos_code_field);
		
	}
	private void excDao(String constant,String str_value) throws GeneralException{
		StringBuffer sql = new StringBuffer();
		sql.setLength(0);
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			if("PS_CODE".equals(constant)||"PS_C_CODE".equals(constant)){
				sql.append("select str_value from constant where constant='"+constant+"'");
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next()){
					String oldvalue=this.frowset.getString("str_value");
					if(!str_value.equalsIgnoreCase(oldvalue)){
						sql.setLength(0);
						String state="1";
						if("PS_CODE".equals(constant)){
							state="2";
						}else{
							MoveSdutyBo msb = new MoveSdutyBo(dao, frowset); //岗位体系代码 有变化时重新初始化岗位体系代码的a0000
							msb.initA0000(str_value);
							emptyJobData();//岗位体系改变时，清空基准岗位表里的数据 
						}
						sql.append("delete from tr_job_course where state="+state);
						dao.update(sql.toString());
					}
				}
			}
			sql.setLength(0);
			sql.append("delete from constant where constant='"+constant+"'");
			dao.delete(sql.toString(), new ArrayList());
			sql.delete(0, sql.length());
			sql.append("insert into constant(constant,type,str_value,describe)values('"+constant+"','0','"
							+ str_value + "','')");
			dao.insert(sql.toString(), new ArrayList());
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 处理单位和岗位的转换代码
	 * @param oldUnits 原来设置单位转换代码的字段
	 * @param newUnits 现在设置单位转换代码的字段
	 * @param oldPosts 原来设置岗位转换代码的字段
	 * @param newPosts 现在设置岗位转换代码的字段
	 * 
	 */
	private void handleTransCode(String oldUnits, String newUnits, String oldPosts, String newPosts) {
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		//单位转化代码的字段发生变化
		if (oldUnits != null && !oldUnits.equals(newUnits)) {
			//清空原来字段的内容
			if (!"#".equals(oldUnits)) {
				clearOption(oldUnits, "b01");	
			}
			//为新字段赋值
			if (!"#".equals(newUnits)) {
				assignOption(newUnits, "b01", "b0110");
			}
		}
		
		//岗位转化代码的字段发生变化
		if (oldPosts!= null && !oldPosts.equals(newPosts)) {
			//清空原来字段的内容
			if (!"#".equals(oldPosts)) {
				clearOption(oldPosts, "k01");
			}
			//为新字段赋值
			if (!"#".equals(newPosts)) {
				assignOption(newPosts, "k01", "e01a1");
			}
		}
	}
	
	/**
	 * 清空原来字段的内容
	 * @param old 字段名称
	 * @param table 表名
	 */
	private void clearOption(String old, String table) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer clearsql = new StringBuffer();
		clearsql.append("update ");
		clearsql.append(table);
		clearsql.append(" set ");
		clearsql.append(old);
		clearsql.append("=''");
		try {
			dao.update(clearsql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 为新字段赋值
	 * @param news 字段名称
	 * @param table 表名
	 */
	private void assignOption(String news, String table, String primaryKey) {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer assignSql = new StringBuffer();
		assignSql.append("update ");
		assignSql.append(table);
		assignSql.append(" set ");
		assignSql.append(news);
		assignSql.append("=(select corcode from ");
		assignSql.append("((select codeitemid,corcode from organization) ");
		assignSql.append(") a ");
		assignSql.append("where a.codeitemid=");
		assignSql.append(table);
		assignSql.append(".");
		assignSql.append(primaryKey);
		assignSql.append(") where "+table+"."+primaryKey+" in (select codeitemid from organization)");
		try {
			dao.update(assignSql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		assignSql.setLength(0);
		assignSql.append("update ");
		assignSql.append(table);
		assignSql.append(" set ");
		assignSql.append(news);
		assignSql.append("=(select corcode from ");
		assignSql.append("(");
		assignSql.append("(select codeitemid,corcode from vorganization)) a ");
		assignSql.append("where a.codeitemid=");
		assignSql.append(table);
		assignSql.append(".");
		assignSql.append(primaryKey);
		assignSql.append(") where "+table+"."+primaryKey+" in (select codeitemid from vorganization)");
		try {
			dao.update(assignSql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 清空H01表数据
	 */
	private void emptyJobData(){
		try {
			DbWizard wd = new DbWizard(getFrameconn());
			ArrayList list = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.JOB_FIELD_SET);
	
			for(int i=0;i<list.size();i++){
				FieldSet fs = (FieldSet)list.get(i);
				wd.emptyTable(fs.getFieldsetid());
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}

}
