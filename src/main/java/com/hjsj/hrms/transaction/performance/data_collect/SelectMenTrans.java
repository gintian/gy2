package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.data_collect.DataCollectBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 
* 
* 类名称：SelectMenTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 11:59:45 AM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 11:59:45 AM   
* 修改备注：   手工引入人员
* @version    
*
 */
public class SelectMenTrans extends IBusiness {

	public void execute() throws GeneralException {

		try 
		{
			String fieldsetid=(String)this.getFormHM().get("fieldsetid");
			String flag = (String) this.getFormHM().get("flag");	
			String dbname = (String) this.getFormHM().get("dbname");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			DataCollectBo bo = new DataCollectBo(this.frameconn,"Params");
			DataCollectBo databo = new DataCollectBo(this.frameconn,this.userView);
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			String _flag  = bo.getXmlValue1("flag",fieldsetid);//flag 1:简单条件  2:复杂条件
			String _value = bo.getValue(fieldsetid);
			String set_id  = bo.getXmlValue1("set_id",fieldsetid);
			StringBuffer tempsql = new StringBuffer("");
			if("1".equals(_flag)){
				FactorList factor = new FactorList("1", _value,dbname, false, false, true, 1, this.userView.getUserId());				
				String strSql = factor.getSqlExpression();
				tempsql.append(" and "+dbname+"A01.a0100 in ( select "+dbname+"A01.a0100 "+strSql+")"); 
			}else if("2".equals(_flag)){
				String tempTableName ="";
				String w ="";
				int infoGroup = 0; // forPerson 人员
				int varType = 8; // logic	
				String whereIN="select "+dbname+"A01.a0100 from "+dbname+"A01";
				alUsedFields.addAll(databo.getMidVariableList(set_id));
				YksjParser yp = new YksjParser(this.userView ,alUsedFields,
						YksjParser.forSearch, varType, infoGroup, "Ht",dbname);
				YearMonthCount ymc=null;							
				yp.run_Where(_value, ymc,"","hrpwarn_result", dao, whereIN,this.frameconn,"A", null);
				tempTableName = yp.getTempTableName();
				w = yp.getSQL();
				tempsql.append("and exists (select null from "+tempTableName+" where "+tempTableName+".a0100="+dbname+"A01.a0100 and ( "+w+" ))");
			}
			if (("1").equals(flag)) 
			{
				String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));								
				String orgLink = "";
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				String[] strs = dbname.split(",");
				String whl = "";
				if (item != null) {
					whl = " OR " + item.getItemid() + "='" + name + "'";
				}			
				for (int i = 0; i < strs.length; i++) {
					if (strs[i].length() > 0) {
						StringBuffer priv = new StringBuffer("");
			    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			    		{
			    			
			    		}
			    		else
			    		{
							String priStrSql = InfoUtils.getWhereINSql(this.userView, strs[i]);
							priv.append("select "+strs[i]+"a01.A0100 ");
							if (priStrSql.length() > 0)
								priv.append(priStrSql);
							else
								priv.append(" from "+strs[i]+"a01");
			    		}
					
						String sql = "select b0110,e0122,e01a1 from "+ strs[i] + "A01 where (a0101='" + name+ "' " + whl+") ";
						if(priv.toString().length()>0)
							 sql+=" and "+strs[i]+"A01.a0100 in ("+priv.toString()+")";
						if(tempsql.toString().length()>0)
							 sql+=" "+tempsql+"";
						this.frowset = dao.search(sql);
						if (this.frowset.next()) {
							if (this.frowset.getString("e01a1") != null && this.frowset.getString("e01a1").length() > 0) {
								orgLink = getSuperOrgLink(this.frowset.getString("e01a1"), "@K");
							} else if (this.frowset.getString("e0122") != null&& this.frowset.getString("e0122").length() > 0) {
								orgLink = getSuperOrgLink(this.frowset.getString("e0122"), "UM");
							} else if (this.frowset.getString("b0110") != null&& this.frowset.getString("b0110").length() > 0) {
								orgLink = getSuperOrgLink(this.frowset.getString("b0110"), "UN");
							}

							// 添加应用库
							if (orgLink.length() > 0) {
								this.frowset = dao.search("select dbname from dbname where lower(pre)='"+ strs[i].toLowerCase() + "'");
								if (this.frowset.next())
									orgLink += "/"+ this.frowset.getString("dbname");
							}
							break;
						}
					}
				}
				this.getFormHM().put("orgLink", orgLink);
				
			} else if ("2".equals(flag)) 
			{
				String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				String[] strs = dbname.split(",");
				String whl = "";
				ArrayList userlist = new ArrayList();
				if (item != null) {
					whl = " OR " + item.getItemid() + " like '" + name + "%'";
				}
				boolean isOnly = false;
				if (item != null && !"0".equals(item.getUseflag()))
					isOnly = true;
				if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) // 干警考核系统
					isOnly = false;
				String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
				FieldItem  pyItem  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
				for (int i = 0; i < strs.length; i++) {
					if (strs[i].length() > 0) {
						
						StringBuffer priv = new StringBuffer("");
			    		if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
			    		{
			    			
			    		}
			    		else
			    		{
							String priStrSql = InfoUtils.getWhereINSql(this.userView, strs[i]);
							priv.append("select "+strs[i]+"a01.A0100 ");
							if (priStrSql.length() > 0)
								priv.append(priStrSql);
							else
								priv.append(" from "+strs[i]+"a01");
			    		}
						name = PubFunc.getStr(name);
						StringBuffer sql = new StringBuffer();
						sql.append("select b0110,e0122,A0100,A0101 "+ (isOnly ? ("," + item.getItemid()) : "")+ ",parentid from ");
						sql.append(strs[i]+ "A01 left join organization on "
										+ strs[i]
										+ "A01.e0122=organization.codeitemid where (A0101 like '"
										+ name + "%'  " + whl + " ");
						if (!(pinyin_field == null || "".equals(pinyin_field) || "#".equals(pinyin_field)||pyItem==null|| "0".equals(pyItem.getUseflag())))
							sql.append("or " + pinyin_field + " like '" + name+ "%'");
						sql.append(")");
						if(priv.toString().length()>0)
						{
							sql.append(" and "+strs[i]+"A01.a0100 in ("+priv.toString()+")");
						}
						if(tempsql.toString().length()>0)
							 sql.append(" "+tempsql+"");
						this.frowset = dao.search(sql.toString());
						int j=0;
						while (this.frowset.next()) {
							j++;
							if(j>=500)//如果查询的记录太多，默认就显示500条
								break;
							String e0122 = this.frowset.getString("e0122");
							String a0100 = this.frowset.getString("A0100");
							String a0101 = this.frowset.getString("a0101");
							if (isOnly) {
								if(this.frowset.getString(item.getItemid())!=null)
								{
							    	a0101 = a0101
									    	+ "("
									    	+ this.frowset.getString(item.getItemid()) + ")";
								}
							}
							String parentid = this.frowset
									.getString("parentid");
							String um = "";
							if (AdminCode.getCodeName("UM", parentid) != null
									&& AdminCode.getCodeName("UM", parentid)
											.trim().length() > 0)
								um = AdminCode.getCodeName("UM", parentid)
										+ "/";
							String value = a0100 + "/" + strs[i] + "/" + a0101
									+ "/p";
							String dataName = um
									+ AdminCode.getCodeName("UM", e0122) + "/"
									+ a0101;
							CommonData cd = new CommonData();
							cd.setDataName(dataName.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
							cd.setDataValue(value);
							userlist.add(cd);
						}
					}
				}
				this.getFormHM().put("namelist", userlist);
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	

	}


	public String getSuperOrgLink(String codeitemid, String codesetid) {
		StringBuffer org_str = new StringBuffer("");
		try {
			String itemid = codeitemid;

			org_str.append(AdminCode.getCodeName(codesetid, itemid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			while (true) {
				this.frowset = dao
						.search("select codeitemid,codeitemdesc from organization where codeitemid=(select parentid  from organization where codeitemid='"
								+ itemid + "')");
				if (this.frowset.next()) {
					String code_item_id = this.frowset.getString("codeitemid");
					if (code_item_id.equals(itemid))
						break;
					else {
						org_str.append("/"
								+ this.frowset.getString("codeitemdesc"));
						itemid = code_item_id;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return org_str.toString();
	} 
}
