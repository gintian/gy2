package com.hjsj.hrms.transaction.gz.gz_accounting.importMen;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:查询人员所属机构信息
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Jul 19, 2008
 * </p>
 * 
 * @author dengcan
 * @version 4.0
 */
public class QueryMenInfoTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try 
		{
			String flag = (String) this.getFormHM().get("flag");
			String showSelfNode = (String) this.getFormHM().get("showSelfNode");
			String selfUns = "";
	        if(showSelfNode!=null && showSelfNode.trim().length()>0 && "1".equals(showSelfNode))
	        {
	        	selfUns = getSelfUns();
	        }			
			if (("1").equals(flag)) 
			{
				String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
				String dbname = (String) this.getFormHM().get("dbname");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String orgLink = "";
				String isSalaryManager="N";
			   if(this.userView.hasTheFunction("327121201")||this.userView.hasTheFunction("324021201")||this.userView.hasTheFunction("325021201")||this.userView.hasTheFunction("327021201")||this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
					isSalaryManager="Y";
				else
					isSalaryManager="N";
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
						if ("N".equalsIgnoreCase(isSalaryManager)) {
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
				String dbname = (String) this.getFormHM().get("dbname");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String orgLink = "";
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				FieldItem item = DataDictionary.getFieldItem(onlyname);
				String[] strs = dbname.split(",");
				String whl = "";
				String isSalaryManager = "";
				if (this.userView.hasTheFunction("327121201")|| this.userView.hasTheFunction("324021201")|| this.userView.hasTheFunction("325021201")|| this.userView.hasTheFunction("327021201")|| this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId()))
					isSalaryManager = "Y";
				else
					isSalaryManager = "N";
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
						String db = "";
						RowSet rs = dao.search("select dbname from dbname where lower(pre)='"+ strs[i].toLowerCase() + "'");
						if (rs.next())
							db += rs.getString("dbname")+"/";
						StringBuffer priv = new StringBuffer("");
						if ("N".equalsIgnoreCase(isSalaryManager)) {
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
						this.frowset = dao.search(sql.toString());
						int j=0;
						while (this.frowset.next()) {
							j++;
							if(j>=500)//如果查询的记录太多，默认就显示500条
								break;
							String e0122 = this.frowset.getString("e0122");
							String a0100 = this.frowset.getString("A0100");
							String a0101 = this.frowset.getString("a0101");
							String b0110 = this.frowset.getString("b0110");
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
							String un = "";//加个单位的信息  zhaoxg add 2013-11-9
							if (AdminCode.getCodeName("UN", b0110) != null
									&& AdminCode.getCodeName("UN", b0110)
											.trim().length() > 0)
								un = AdminCode.getCodeName("UN", b0110)
										+ "/";
							
							String value = PubFunc.encrypt(a0100) + "/" + PubFunc.encrypt(strs[i]) + "/" + a0101
									+ "/p";
							String dataName = db+un+um
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
				
			} else if ("3".equals(flag)) 
			{
				Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this
						.getFrameconn());
				String onlyname = sysbo.getCHKValue(
						Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
				String name = PubFunc.getStr(SafeCode.decode((String) this
						.getFormHM().get("name")));
				if(name==null||"".equals(name))
					return;
				String dbname = (String) this.getFormHM().get("dbname");
				DbWizard db=new DbWizard(this.getFrameconn());
				String nbase[] = dbname.split(",");
				ArrayList userlist = new ArrayList();
				for (int i = 0; i < nbase.length; i++) {
					if (nbase[i] != null && nbase[i].length() > 0) {
						StringBuffer sql = new StringBuffer();
						if (onlyname != null && onlyname.length() > 0 && !"A0177".equalsIgnoreCase(onlyname)) {
							sql.append("SELECT A0100,A0101,B0110,A0177," + onlyname
									+ " FROM " + nbase[i]
									+ "A01 WHERE (A0101 LIKE '%" + name
									+ "%'");
							if(db.isExistField(nbase[i]+"A01", "C0103", false))
								sql.append(" OR C0103 LIKE '" + name + "%'");
							sql.append(" OR "+onlyname+" LIKE '%"+name+"%'");
						} else {
							sql.append("SELECT A0100,A0101,B0110,A0177 FROM "
											+ nbase[i]
											+ "A01 WHERE (A0101 LIKE '%" + name
											+ "%'");
							if(db.isExistField(nbase[i]+"A01", "C0103", false))
								sql.append(" OR C0103 LIKE '" + name + "%'");
							if(onlyname!=null&&onlyname.length()>0)
								sql.append(" OR "+onlyname+" LIKE '%"+name+"%'");
						}
						sql.append(")");
						TrainCourseBo tcb = new TrainCourseBo(this.userView);
						sql.append(tcb.getPrivSqlWhere(nbase[i]));
						ContentDAO dao = new ContentDAO(this.frameconn);
						this.frowset = dao.search(sql.toString());
						while (this.frowset.next()) {
							CommonData objvo = new CommonData();
							String a0100 = this.frowset.getString("a0100");
							String a0101 = this.frowset.getString("a0101");
							String a0177 = this.frowset.getString("a0177");
//							String b0110 = this.frowset.getString("b0110");
							String onlyname1 = "";
							String value = "";
							if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) // 干警考核系统
							{
								objvo.setDataName(a0101.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
								value = a0100 + "/" + nbase[i] + "/" + a0101 + "/p";
							}
							else
							{
								if (onlyname != null&& onlyname.trim().length() > 0) {
									onlyname1 = this.frowset.getString(onlyname);
									
									onlyname1=(onlyname1==null||onlyname1.length()<1)?"":("("+onlyname1+")");
									objvo.setDataName(a0101 + onlyname1);
									value = a0100 + "/" + nbase[i] + "/" + a0101 + onlyname1
									+ "/p";
								} else {
	//								String bName = AdminCode.getCodeName("UN",
	//										b0110);
	//								onlyname1 = a0177;
									objvo.setDataName(a0101.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
									value = a0100 + "/" + nbase[i] + "/" + a0101 + "/p";
								}
							}
//							objvo.setDataName(a0101
//									+ "(" + onlyname1 + ")");
//							String value = a0100 + "/" + nbase[i] + "/" + a0101 + "(" + a0177 + ")"
//							+ "/p";
							objvo.setDataValue(value);
							userlist.add(objvo);
						}
					}
				}
				this.getFormHM().put("namelist", userlist);
				
			} else if("4".equals(flag)) 
			{
				String tabId= (String) this.getFormHM().get("tabid"); ///人事异动调用
		        if(tabId==null)
		        	tabId="";
		        boolean kqFlag=false;
		        if (!"".equals(tabId)){
		        	TemplateTableParamBo parambo=new TemplateTableParamBo(this.frameconn);
		        	kqFlag= parambo.isKqTempalte(Integer.parseInt(tabId));//判断当前模板是否定义了考勤参数
		        }
				String priv = (String) this.getFormHM().get("priv");// =1走权限
				String dbpre = (String) this.getFormHM().get("dbpre");
				/**
				 * 登录用户库标识 =0 权限范围内的库 =1 权限范围内的登录用户库
				 */
				String dbtype = (String) this.getFormHM().get("dbtype");
				String viewunit = (String) this.getFormHM().get("viewunit");// =1按操作单位
				String nmodule = (String) this.getFormHM().get("nmodule");// =根据业务模块去
				String name = SafeCode.decode((String) this.getFormHM().get("name"));
				String isfilter=(String)this.getFormHM().get("isfilter");
				String dbvalue = (String)this.getFormHM().get("dbvalue");  //领导班子查询相应人员库新增参数
				String SYS_FILTER_FACTOR=SafeCode.decode((String) this.getFormHM().get("SYS_FILTER_FACTOR"));
				
				ContentDAO dao = new ContentDAO(this.getFrameconn());

				ArrayList dblist = null;
				if(dbvalue!=null && !"".equals(dbvalue)){   //wangcq 2014-12-15 领导班子获取相应人员库
					dblist = new ArrayList();
					String[] dbs = dbvalue.split(",");
					for(int i=0; i<dbs.length; i++){
						dblist.add(dbs[i]);
					}
				}else{
					DbNameBo dbbo = new DbNameBo(this.getFrameconn());
					if ("0".equals(dbtype)) {
						dblist = this.userView.getPrivDbList();
					} else {
						ArrayList alist = dbbo.getAllLoginDbNameList();
						dblist = new ArrayList();
						for (int i = 0; i < alist.size(); i++) {
							RecordVo vo = (RecordVo) alist.get(i);
							dblist.add((String) vo.getString("pre"));
						}
					}
				}
				StringBuffer s = new StringBuffer("");
				for (int i = 0; i < dblist.size(); i++) {
					if (i != 0)
						s.append(",");
					s.append((String) dblist.get(i));
				}
				dbpre = s.toString();
				ArrayList userlist = new ArrayList();
				StringBuffer str_value=new StringBuffer("");
				if (dbpre != null && dbpre.length() > 0) {
					Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this
							.getFrameconn());
					String onlyname = sysbo.getCHKValue(
							Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
					FieldItem item = DataDictionary.getFieldItem(onlyname);
					String whl = "";
					if (item != null&&!"0".equals(item.getUseflag())) {
						whl = " OR " + item.getItemid() + " like '" + name
								+ "%'";
					}
					boolean isOnly = false;
					if (item != null && !"0".equals(item.getUseflag()))
						isOnly = true;
					if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) // 干警考核系统
						isOnly = false;
					String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
					FieldItem  pyItem  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
					String[] strs = dbpre.split(",");
					for (int i = 0; i < strs.length; i++) {
						
				        if(showSelfNode!=null && showSelfNode.trim().length()>0 && "1".equals(showSelfNode) && selfUns.trim().length()>0)
				        {
				        	if(!this.userView.getDbname().equalsIgnoreCase(strs[i]))
				        		continue;
				        }
						if (strs[i].length() > 0) {
							name = PubFunc.getStr(name);
							StringBuffer sql = new StringBuffer();
							sql.append("select ");
							if(Sql_switcher.searchDbServer()==Constant.MSSQL)
								sql.append("top 500 ");
							sql.append(" b0110,e0122,A0100,A0101 "+ (isOnly ? ("," + item.getItemid()) : "")+ ",parentid,'"+strs[i]+"' as dbpre from ");
							sql.append(strs[i]
											+ "A01 left join organization on "
											+ strs[i]
											+ "A01.e0122=organization.codeitemid where (A0101 like '%"
											+ name + "%'  " + whl + " ");
							if (!(pinyin_field == null|| "".equals(pinyin_field) || "#".equals(pinyin_field)||pyItem==null|| "0".equals(pyItem.getUseflag())))
								sql.append("or " + pinyin_field + " like '"+ name + "%'");
							sql.append(")");
							
							if(showSelfNode!=null && showSelfNode.trim().length()>0 && "1".equals(showSelfNode) && selfUns.trim().length()>0)
					        {
								sql.append(" and ( b0110 in ("+selfUns+") or e0122 in ("+selfUns+") or e01a1 in ("+selfUns+"))");
					        }
							
							if("1".equals(isfilter)&&SYS_FILTER_FACTOR.trim().length()>0)
							{
								ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
								int infoGroup = 0; // forPerson 人员
								int varType = 8; // logic				 						
								YksjParser yp = new YksjParser( this.userView ,alUsedFields,
										YksjParser.forSearch, varType, infoGroup, "Ht", strs[i]);
								YearMonthCount ymc=null;		
								
								String tabid= (String) this.getFormHM().get("tabid"); ///人事异动调用
								if(tabid!=null&&!"-1".equalsIgnoreCase(tabid))
								{
									yp.setSupportVar(true,"select  *  from   midvariable where nflag=0 and templetid= "+tabid);  //支持临时变量
								}
								yp.run_Where(SYS_FILTER_FACTOR, ymc,"","", dao,"",this.getFrameconn(),"A", null);
								String tempTableName = yp.getTempTableName();
								sql.append(" and a0100 in  ( select a0100 from "+tempTableName+" where "+yp.getSQL()+" )");
							}
							
							StringBuffer privSQL = new StringBuffer();
							if ("1".equalsIgnoreCase(priv)) {
								if ("1".equals(viewunit)||(nmodule!=null&&nmodule.length()>0)) {
									String code = this.userView.getUnit_id();
									if(nmodule!=null&&nmodule.length()>0)
										code = this.userView.getUnitIdByBusi(nmodule);
									if (code == null
											|| "UN".equalsIgnoreCase(code)
											|| "".equals(code))
										privSQL.append(" and 1=2 ");
									else if (code.length() == 3)
										privSQL.append(" ");
									else {
										String[] arr = code.split("`");
										StringBuffer temp = new StringBuffer("");
										for (int j = 0; j < arr.length; j++) {
											if (arr[j] == null
													|| "".equals(arr[j]))
												continue;
											String codeset = arr[j].substring(
													0, 2);
											String value = arr[j].substring(2);
											temp.append(" or ");
											if ("UN".equalsIgnoreCase(codeset))
												temp.append(" b0110 ");
											else
												temp.append(" e0122 ");
											temp.append(" like '" + value
													+ "%'");
										}
										privSQL.append(" and ("
												+ temp.toString().substring(3)
												+ ")");
									}
								} else {
									String priStrSql = InfoUtils.getWhereINSql(
											this.userView, strs[i]);
									if(kqFlag){  
										priStrSql=RegisterInitInfoData.getWhereINSql(userView,strs[i]);
									}
									StringBuffer aa = new StringBuffer("");
									aa.append("select " + strs[i]
											+ "a01.A0100 ");
									if (priStrSql.length() > 0)
										aa.append(priStrSql);
									else
										aa.append(" from " + strs[i] + "a01");
									privSQL.append(" and a0100 in ("
											+ aa.toString() + ")");
								}
								sql.append(privSQL);
								if(Sql_switcher.searchDbServer()==Constant.DB2)
									sql.append(" and FETCH FIRST 500 ROWS ONLY");
								else if(Sql_switcher.searchDbServer()==Constant.ORACEL)
									sql.append(" and ROWNUM <= 500");
							}
							
							this.frowset = dao.search(sql.toString());
							String str = "";//xyy记录上一个人员的人员库
							while (this.frowset.next()) {
								String b0110 = this.frowset.getString("b0110");
								String e0122 = this.frowset.getString("e0122");
								String a0100 = this.frowset.getString("A0100");
								String a0101 = this.frowset.getString("a0101");
								if (isOnly) {
									String only = this.frowset.getString(item
											.getItemid());
									if (only == null || "".equals(only.trim()))
										only = "";
									else
										only = "(" + only + ")";
									a0101 = a0101 + only;
								}
								String un=AdminCode.getCodeName("UN",b0110);
								String parentid = this.frowset
										.getString("parentid");
								String um = "";
								if (AdminCode.getCodeName("UM", parentid) != null
										&& AdminCode
												.getCodeName("UM", parentid)
												.trim().length() > 0)
									um = AdminCode.getCodeName("UM", parentid)
											+ "/";
								String value = strs[i] + a0100;
								String _temp=un+"/"+ um+ (AdminCode.getCodeName("UM", e0122).length() > 0 ? AdminCode.getCodeName("UM", e0122): "");
								//if(_temp.trim().length()>1) //屏蔽 前台无岗位的人无法选
									str_value.append("`"+value+"~"+un+"/"+ um+ (AdminCode.getCodeName("UM", e0122).length() > 0 ? AdminCode.getCodeName("UM", e0122): "") + "/" + a0101); //查询人员的提示显示人员姓名   chenxg  add 2014-09-11  
								String dataName = um
										+ (AdminCode.getCodeName("UM", e0122)
												.length() > 0 ? AdminCode
												.getCodeName("UM", e0122)
												+ "/" : "") + a0101;
								CommonData cd = new CommonData();
								if(!str.equals(this.frowset.getString("dbpre"))){
								    userlist.add(new CommonData(this.frowset.getString("dbpre"),"---------【"+AdminCode.getCodeName("@@", this.frowset.getString("dbpre"))+"】---------"));
	                            }
								cd.setDataName(dataName.replaceAll("\\n", "").replaceAll("\\r","").replaceAll("\"", "\\\\\""));
								cd.setDataValue(value); 
								userlist.add(cd);
								str = this.frowset.getString("dbpre");
							}
						}
					}
				}
				if(str_value.length()>0)
					this.getFormHM().put("str_value",str_value.substring(1));
				else
					this.getFormHM().put("str_value","");
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

	/**
     * 取得当前用户所在单位的最上层节点
     * @param userview
     * @return
     */
    public String getSelfUns()
    {
    	String un = "";
    	try
    	{
    		if (this.userView.isSuper_admin())
    			return un;
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		ArrayList list = new ArrayList();
    		String a_un = this.userView.getUserOrgId();
    		String a_um = this.userView.getUserDeptId();
    		String a_k = this.userView.getUserPosId();
	          		         
    		if(a_un.trim().length()>0)
    			list.add(a_un);
    		String codeitemid = a_un;
    		if(codeitemid.trim().length()==0)
    			codeitemid = a_um;
    		if(codeitemid.trim().length()==0)
    			codeitemid = a_k;
    		
    		while(true)
    		{
    			String sql="select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"' and codeitemid<>parentid)";
    			this.frowset = dao.search(sql);
    			if(this.frowset.next())
    			{
    				String a_codeitemid = this.frowset.getString("codeitemid");
    				String codesetid = this.frowset.getString("codesetid");
    				if("UN".equalsIgnoreCase(codesetid))
    					list.add(a_codeitemid);
    				codeitemid = a_codeitemid;
    			}
    			else
    				break;
    		}
    		
    		for(int j=0;j<list.size();j++)
    		{
    			un+=",'"+((String)list.get(j))+"'";
    		}
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	if(un.length()>0)
    		return un.substring(1);
    	return un;
    }
    
}
