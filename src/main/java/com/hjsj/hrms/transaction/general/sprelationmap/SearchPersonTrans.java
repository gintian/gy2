package com.hjsj.hrms.transaction.general.sprelationmap;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 查询人员汇报关系图
 * @author tianye
 * @date 2013-5-13
 */
public class SearchPersonTrans  extends IBusiness{
	private HashMap descmap = new HashMap();
	private HashMap parentmap = new HashMap();
	public void execute() throws GeneralException {

		try
		{
			String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
			String type = "0";//0根据名字或拼音查询人员信息  1展开树查询人员部门层级信息
			String dbname = (String)this.getFormHM().get("dbname");
			dbname=dbname==null|| "".equals(dbname.trim())?"Usr":dbname;//现在审批关系默认配置的是Usr,其实就是Usr，下面用“，”分割dbname并循环考虑以后可能配置多个人员库
			String priv = (String)this.getFormHM().get("priv");//0代表权限不控制1控制
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String orgLink = "";
			
			ArrayList orgLinks = new ArrayList();// 姓名查询出的人与下面personList中的人员信息形式不同前者是用于前台展开查询人员树使用后者是查询人员后在多个人中选择用
			ArrayList personList = new ArrayList();
			String[] dbnames = dbname.split(",");
			String sql = "select codeitemid,parentid,codeitemdesc from organization ";
			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				descmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
				parentmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("parentid"));
			}
			String codeid=null;
			String codevalue=null;
			String unsStr = "";
			if(userView.isSuper_admin()){
			    unsStr= "UN";
			}else{
			    unsStr= userView.getUnitIdByBusi("4").trim();
			}
			String[] uns = unsStr.split("`");
			for(int j = 0 ;(j<uns.length&&!"".equals(unsStr));j++){
				//根据登录人控制的范围在单位、部门或者岗位上的不同处理情况不同
				if(uns[j].length()>2){
					codeid  = uns[j].substring(0,2);
					codevalue = uns[j].substring(2);
				}
				for (int i = 0; i < dbnames.length; i++)
				{
					if (dbnames[i].length() > 0)
					{
						// 添加应用库
						StringBuffer dbStr = new StringBuffer();
						this.frowset = dao.search("select dbname from dbname where lower(pre)='" + dbnames[i].toLowerCase() + "'");
						if (this.frowset.next())
							dbStr.append("/" + this.frowset.getString("dbname")) ;
							
						StringBuffer andsql = new StringBuffer();
						if(codeid!=null&& "UM".equalsIgnoreCase(codeid))
							andsql.append(" and e0122 like '");
						else if(codeid!=null&& "UN".equalsIgnoreCase(codeid))
							andsql.append("and  b0110 like '");
						if(codevalue!=null){
							andsql.append(codevalue +"%' ");
						}
						
						StringBuffer strSql = new StringBuffer();//根据前台传过来的name中是否含有'/'符号来区分是查询符合的人放在前台以供选择还是直接查询人员展开树并显示查询的第一个人的审批关系
						String[] info = name.split("/");//不含有‘/’证明是查询符合name人员供用户选择的（未来可能存在用户手动输入“/”的bug）它只会以最后一个/后的信息为查询条件
						Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
						String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
						
						if(info.length>=2){
							type ="1";
							String empName,num;//姓名和身份证号码
							String empNameAndNum = info[info.length-1];
							int index = empNameAndNum.indexOf("〔");
							if(index==-1){
								empName = empNameAndNum;
								strSql.append("select b0110,e0122,e01a1,a0100,a0101,a0177 from "+dbnames[i]+"A01  where ( A0101 like '"+empName +"%'");
								if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )){
									strSql.append( " or "+pinyin_field +" like '"+empName+"%' )" );
								}else{
									strSql.append(" )");
								}
							}else{
								empName = empNameAndNum.substring(0,index);
								num =  empNameAndNum.substring(index+1,empNameAndNum.length()-1);
								
								if("未填身份证号码".equals(num)){
									strSql.append( "select b0110,e0122,e01a1,a0100,a0101,a0177 from "+dbnames[i]+"A01  where ( A0101 = '"+empName +"' and a0177 is null");
								}else{
									strSql.append( "select b0110,e0122,e01a1,a0100,a0101,a0177 from "+dbnames[i]+"A01  where ( A0101 = '"+empName +"' and a0177='"+num+"'");
								}
								
								if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )){
									strSql.append( " or "+pinyin_field +" like '"+empName+"%' )") ;
								}else{
									strSql.append(" )");
								}
								
							}
							
						}else{
							strSql.append("select b0110,e0122,e01a1,a0100,a0101,a0177 from "+dbnames[i]+"A01 where (A0101 like '"+name+"%'");
							if(!(pinyin_field==null || "".equals(pinyin_field) || "#".equals(pinyin_field) )){
								strSql.append( " or "+pinyin_field +" like '"+name+"%' )" );
							}else{
								strSql.append(" )");
							}
								
							if(priv!=null&& "1".equals(priv)&&codevalue!=null&&!"".equalsIgnoreCase(codevalue)&&!"".equals(andsql.toString())){
								strSql.append(andsql);
							}
						}
						strSql.append(" order by b0110,e0122,e01a1 ");
						this.frowset = dao.search(strSql.toString());
						
						while (this.frowset.next())
						{
							String e01a1 = this.frowset.getString("e01a1");
							String e0122 = this.frowset.getString("e0122");
							String b0110 = this.frowset.getString("b0110");
							String a0100 = this.frowset.getString("a0100");
							String a0101 = this.frowset.getString("a0101");
							String a0177 = this.frowset.getString("a0177")==null?"未填身份证号码":this.frowset.getString("a0177");
							
							if (e01a1 != null && e01a1.length() > 0 && AdminCode.getCodeName("@K", e01a1)!=null && !"".equals(AdminCode.getCodeName("@K", e01a1)))
								orgLink = getSuperOrgLink(e01a1, "@K");
							else if (e0122 != null && e0122.length() > 0 && AdminCode.getCodeName("UM", e0122)!=null && !"".equals(AdminCode.getCodeName("UM", e0122)))
								orgLink = getSuperOrgLink(e0122, "UM");
							else if (b0110 != null && b0110.length() > 0 && AdminCode.getCodeName("UN", b0110)!=null && !"".equals(AdminCode.getCodeName("UN", b0110)))
								orgLink = getSuperOrgLink(b0110, "UN");
							if (orgLink.length() > 0)
								orgLink += dbStr+"/"+dbnames[i]+a0100;
							if("0".equals(type)){
								personList.add(AdminCode.getCodeName("UM", e0122)+"/"+a0101+"("+a0177+")");
							}else if("1".equals(type)){
									orgLinks.add(orgLink);
							}
						}
					}
				}
			}
			
			this.getFormHM().put("orgLinks", orgLinks);
			this.getFormHM().put("personList", personList);
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	public String getSuperOrgLink(String codeitemid, String codesetid) throws GeneralException
	{
		StringBuffer org_str = new StringBuffer("");
		try
		{
			String itemid = codeitemid;
			org_str.append(AdminCode.getCodeName(codesetid, itemid));
			while (true)
			{
				String parentid = (String) this.parentmap.get(itemid);
				if (parentid.equals(itemid))
					break;
				else
				{
					org_str.append("/" + this.descmap.get(parentid));
					itemid = parentid;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return org_str.toString();
	}

}
