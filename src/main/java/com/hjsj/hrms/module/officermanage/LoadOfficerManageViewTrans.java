package com.hjsj.hrms.module.officermanage;

import com.hjsj.hrms.module.officermanage.businessobject.CardViewService;
import com.hjsj.hrms.module.officermanage.businessobject.impl.CardViewServiceImpl;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadOfficerManageViewTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			CardViewService cardViewService=new CardViewServiceImpl(this.userView, this.frameconn);
			HashMap constantMap = cardViewService.getConstantXMl();
			ArrayList fieldList = (ArrayList) constantMap.get("list");
			ArrayList defaultList = new ArrayList();
			String b0110 = (String) constantMap.get("postOrg");// 任职单位
			String postStat = (String) constantMap.get("postStat");// 任职情况 只加载任职
			String expr = (String) constantMap.get("expr");// 过滤条件
			String setId=(String)constantMap.get("setid");//任免子集
			//添加不关联职务子集情况时
			boolean isHave_subSet=true;
			if(!StringUtils.isNotEmpty(setId)) {
				isHave_subSet=false;
			}
			String jobname=(String)constantMap.get("jobname");//职务名称
			ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
			String privOrg = this.userView.getManagePrivCodeValue();
			ColumnsInfo info = null;
			
			info=new ColumnsInfo();
			info.setColumnType("A");
			info.setCodesetId("0");
			info.setColumnId("A0100");
			info.setColumnDesc("人员id");
			info.setReadOnly(true);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnList.add(info);
			
			info=new ColumnsInfo();
			info.setColumnType("A");
			info.setCodesetId("0");
			info.setColumnId("Dbtype");
			info.setColumnDesc("人员库标识");
			info.setReadOnly(true);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnList.add(info);
			
			info=new ColumnsInfo();
			info.setColumnType("A");
			info.setCodesetId("0");
			info.setColumnId("guidkey");
			info.setColumnDesc("唯一标识");
			info.setReadOnly(true);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnList.add(info);
			if(StringUtils.isNotEmpty(jobname)&&isHave_subSet) {
				info=new ColumnsInfo();
				info.setColumnType("A");
				info.setCodesetId("0");
				info.setColumnId(jobname);
				info.setColumnDesc("职务名称");
				info.setTextAlign("left");
				info.setLocked(true);
				info.setRendererFunc("managerGobal.renderJobName");
				columnList.add(info);
			}
			
			info=null;
			StringBuffer field = new StringBuffer(" select ");
			field.append("UsrA01.A0000,");
			field.append("UsrA01.A0100,");
			field.append("'Usr' as Dbtype ,");
			field.append(" UsrA01.guidkey as guidkey,");
			//不关联职务子集
			if(isHave_subSet) {
				String codeSetId=DataDictionary.getFieldItem(jobname).getCodesetid();
				if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
					if(StringUtils.isNotEmpty(codeSetId)&&!"0".equals(codeSetId)) {
						if("UM".equalsIgnoreCase(codeSetId)||"UN".equalsIgnoreCase(codeSetId)||"@K".equalsIgnoreCase(codeSetId)) {
							field.append("(select listagg(organization.codeitemdesc,',') within group(order by i9999) "
									+ "from Usr"+setId+",organization where organization.codeitemid=Usr"+setId+"."+jobname+" and codesetid='"+codeSetId+"' and UsrA01.A0100=Usr"+setId+".A0100 and 'b'='b' and "+postStat+"=2 and 'b'='b' "
									+ "and "+Sql_switcher.sqlNull(b0110, " ")+" like '"+privOrg+"%' ) as "+jobname+",");
						}else {
							field.append("(select listagg(codeitem.codeitemdesc,',') within group(order by i9999) "
									+ "from Usr"+setId+",codeitem where codeitem.codeitemid=Usr"+setId+"."+jobname+" and codesetid='"+codeSetId+"' and UsrA01.A0100=Usr"+setId+".A0100 and 'b'='b' and "+postStat+"=2 and 'b'='b' "
									+ "and "+Sql_switcher.sqlNull(b0110, " ")+" like '"+privOrg+"%' ) as "+jobname+",");						
						}
					}else {
						field.append("(select listagg("+jobname+",',') within group(order by i9999) "
								+ "from Usr"+setId+" where UsrA01.A0100=Usr"+setId+".A0100 and 'b'='b' and "+postStat+"=2 and 'b'='b' "
								+ "and "+Sql_switcher.sqlNull(b0110, " ")+" like '"+privOrg+"%' ) as "+jobname+",");
					}
				}else {
					if(StringUtils.isNotEmpty(codeSetId)&&!"0".equals(codeSetId)) {
						if("UM".equalsIgnoreCase(codeSetId)||"UN".equalsIgnoreCase(codeSetId)||"@K".equalsIgnoreCase(codeSetId)) {
							field.append(" (select stuff((" + 
									"select ','+ organization.codeitemdesc " + 
									" from Usr"+setId+",organization where organization.codeitemid=Usr"+setId+"."+jobname+" and codesetid='"+codeSetId+"' and UsrA01.A0100=Usr"+setId+".A0100 and 'b'='b' and "+postStat+"=2 and 'b'='b' and "+Sql_switcher.sqlNull(b0110, "")+" like '"+privOrg+"%'"
									+ " order by i9999  for xml path('')),1,1,'') as name ) as "+jobname+", "); 
						}else {
							field.append(" (select stuff((" + 
									"select ','+ codeitem.codeitemdesc " + 
									" from Usr"+setId+",codeitem where codeitem.codeitemid=Usr"+setId+"."+jobname+" and codesetid='"+codeSetId+"' and UsrA01.A0100=Usr"+setId+".A0100 and 'b'='b' and "+postStat+"=2 and 'b'='b' and "+Sql_switcher.sqlNull(b0110, "")+" like '"+privOrg+"%'"
									+ " order by i9999  for xml path('')),1,1,'') as name ) as "+jobname+", "); 						
						}
					}else {
						field.append(" (select stuff((" + 
								"select ','+"+jobname + 
								" from Usr"+setId+" where UsrA01.A0100=Usr"+setId+".A0100 and 'b'='b' and "+postStat+"=2 and 'b'='b' and "+Sql_switcher.sqlNull(b0110, "")+" like '"+privOrg+"%'"
								+ " order by i9999  for xml path('')),1,1,'') as name ) as "+jobname+", "); 
					}
				}
			}
			for (int i = 0; i < fieldList.size(); i++) {
				HashMap map = (HashMap) fieldList.get(i);
				if (i < 6) {// 取前5个 作为默认查询条件
					if("nbase".equals(map.get("itemid"))){////通用查询使用nbase指标 改为Dbtype指标
						HashMap dbtypemap = new HashMap();
						dbtypemap.put("itemid", "Dbtype");
						dbtypemap.put("itemdesc", "人员库");
						dbtypemap.put("itemtype", "A");
						dbtypemap.put("codesetid", "0");
						defaultList.add(0, dbtypemap);
					}else{
						if(map.get("itemid").toString().equals(b0110))
							continue;
						defaultList.add(fieldList.get(i));
					}
				}
				String itemid = map.get("itemid").toString();
				info = new ColumnsInfo();
				if("a0101".equals(itemid) || "b0110".equals(itemid)|| "e0122".equals(itemid) || "e01a1".equals(itemid))	{
					info.setColumnId("custom"+itemid);
				}else
					info.setColumnId(itemid);
				info.setCtrltype("1");
				info.setColumnDesc(map.get("itemdesc").toString());
				info.setCodesetId(map.get("codesetid").toString());
				info.setColumnType(map.get("itemtype").toString());
				if("N".equalsIgnoreCase(map.get("itemtype").toString())) {
					info.setTextAlign("right");
				}else {
					info.setTextAlign("left");
				}
				if("nbase".equals(itemid)||"resume".equals(itemid)||"familyandrelation".equals(itemid)) {//  
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//人员库只加载不显示
				}
				
				if ("education".equals(itemid) || "degree".equals(itemid) || "school".equals(itemid)
						|| "educationmajor".equals(itemid)) {
					info.setRendererFunc("managerGobal.renderColFunc");
				}
				
				if("assessment".equals(itemid) || "rewardsandpenalties".equals(itemid)||"postreason".equals(itemid)) {//年度考核结果 奖惩信息
					info.setRendererFunc("managerGobal.reloadCol");
				}

				if (b0110.equalsIgnoreCase(itemid)) {
					info.setLocked(true);
				}
				if ("A0101".equals(itemid) || "B0110".equals(itemid)// 单位部门 岗位 itemid固定为大写 自定义添加指标时为小写 二者区分
						|| "E0122".equals(itemid) || "E01A1".equals(itemid)) {
					field.append("UsrA01." + itemid);
					info.setLocked(true);
				} else {
					if("a0101".equals(itemid) || "b0110".equals(itemid)|| "e0122".equals(itemid) || "e01a1".equals(itemid))	{
						field.append("om_officer_muster." + itemid+" as custom"+itemid);
					}else if(itemid.equals(b0110)&&isHave_subSet) {
						if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
							field.append(" (select "+b0110+" from ( select * from Usr"+setId+" where Usr"+setId+".A0100=UsrA01.A0100  and "+Sql_switcher.isnull(b0110, "' '")+" <>' ' order by "+b0110+" )Usr"+setId+" where rownum=1 and "
									+ " Usr"+setId+".A0100=UsrA01.A0100 and "+postStat+"=2  ");
						}else {
							field.append(" (select top 1 "+b0110+" from Usr"+setId+" where"
									+ " Usr"+setId+".A0100=UsrA01.A0100 and "+postStat+"=2  ");
						}
						
						if(StringUtils.isNotEmpty(privOrg))
							field.append("and "+b0110+" like '"+privOrg+"%' ");
						if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
							field.append(" and "+Sql_switcher.isnull(b0110, "' '")+" <>''");
							field.append(" order by "+b0110);
						}
						field.append(" ) as "+b0110);
					}else
						field.append("om_officer_muster." + itemid);
				}
				if ("A0101".equals(itemid)) {
			          info.setRendererFunc("managerGobal.renderA0101");
			    }
				if (i < fieldList.size() - 1)
					field.append(",");
				columnList.add(info);
			}
			field.append(" from UsrA01 left join om_officer_muster on UsrA01.Guidkey=om_officer_muster.guidkey  ");
			if (StringUtils.isNotEmpty(b0110)&&isHave_subSet) {
				
				field.append(" WHERE exists ( select * from Usr"+setId +" where Usr"+setId +".A0100=UsrA01.A0100 ");
				if(StringUtils.isNotEmpty(postStat)){
					field.append(" and 'a'='a' and ");
					field.append(postStat+"=2");
					field.append(" and 'a'='a' ");
				}
				if(StringUtils.isNotEmpty(privOrg)){
					field.append(" and ");
					field.append(b0110 +" like '"+privOrg+"%'");
				}
				field.append(")");
			}else {
				field.append(" WHERE 1=1 ");
			}

			YksjParser yp = new YksjParser(this.userView, DataDictionary.getAllFieldItemList(0, 0),
					YksjParser.forNormal, YksjParser.LOGIC, YksjParser.forPerson, "Ht", "");
			HashMap<String, FieldItem> map = null;
			if (StringUtils.isNotEmpty(expr)) {
				yp.run(expr);
				map = yp.getMapUsedFieldItems();
			}

			StringBuffer sql = new StringBuffer();
			String[] nbase = ((String) constantMap.get("nbase")).split(",");
			StringBuffer expSql = new StringBuffer("");
			for (int i = 0; i < nbase.length; i++) {
				expSql.setLength(0);
				if ("Usr".equalsIgnoreCase(nbase[i]))
					sql.append(field.toString());
				else {
					sql.append(field.toString().replace("UsrA01", nbase[i] + "A01").replace("'Usr'", "'"+nbase[i]+"'").replace("Usr",""+nbase[i]+""));
				}

				if (StringUtils.isNotEmpty(postStat)&&isHave_subSet) {// 任职状况不为空时 查在任数据
					FieldItem item = DataDictionary.getFieldItem(postStat);
					if (item == null)
						throw new Exception(ResourceFactory.getProperty("officer.postStatNotFind"));
					String fieldsetid = item.getFieldsetid();
					if (sql.indexOf("WHERE") > -1) {
						sql.append(" AND ");
					} else {
						sql.append(" WHERE ");
					}
					sql.append(nbase[i] + "A01.A0100 in (select A0100 from " + nbase[i] + fieldsetid + " where "
							+ postStat + "='2' ) ");
				}

				if (StringUtils.isNotEmpty(expr) && map != null) {
					String fromSql = "";
					String whereSql = "";
					String itemid = "";
					for (String key : map.keySet()) {
						FieldItem item = map.get(key);
						if (item == null)
							continue;
						if (!"A0100".equalsIgnoreCase(item.getItemid()))
							itemid += "," + item.getItemid();

						if (!"A01".equalsIgnoreCase(item.getFieldsetid())) {
							if (fromSql.indexOf(nbase[i] + item.getFieldsetid()) < 0)
								fromSql += "," + nbase[i] + item.getFieldsetid();
							if (whereSql.indexOf(nbase[i] + item.getFieldsetid()) < 0)
								whereSql += "and " + nbase[i] + "A01.A0100=" + nbase[i] + item.getFieldsetid()
										+ ".A0100 ";
						}

					}
					if (StringUtils.isNotEmpty(itemid)) {
						expSql.append(" select " + nbase[i] + "A01.A0100 " + itemid + " from  " + nbase[i] + "A01 ");
						expSql.append(fromSql);
						expSql.append(" WHERE ");
						if (StringUtils.isNotEmpty(whereSql))
							expSql.append(whereSql.substring(3) + " and ");
						expSql.append(yp.getSQL());
						sql.append(" and "+nbase[i] + "A01.A0100 in (select A0100 from ("+expSql.toString()+") expSql ) ");
					}
				}
				if (i < nbase.length - 1) {
					sql.append(" union all ");
				}
			}
			ArrayList delList=new ArrayList();
			for(int i=0;i<fieldList.size();i++){//通用查询 自定义指标 不需要fieldsetid 处理
				HashMap defamap=(HashMap)fieldList.get(i);
				if(defamap.containsKey("itemid")&&"nbase".equals(defamap.get("itemid"))){//通用查询使用nbase指标 改为Dbtype指标
					fieldList.remove(i);
					HashMap dbtypemap = new HashMap();
					dbtypemap.put("itemid", "Dbtype");
					dbtypemap.put("itemdesc", "人员库");
					dbtypemap.put("itemtype", "A");
					dbtypemap.put("codesetid", "0");
					fieldList.add(0,dbtypemap);
				}
				//去除 简历 家庭成员   
				if("resume".equalsIgnoreCase(defamap.get("itemid").toString())||
				   "familyandrelation".equalsIgnoreCase(defamap.get("itemid").toString())||
				   defamap.get("itemid").toString().equalsIgnoreCase(b0110)) {//去除任职单位筛选条件
					delList.add(defamap);
					
				}
				
				if(defamap.containsKey("customFlag")&&"1".equals(defamap.get("customFlag"))&&defamap.containsKey("fieldsetid")){
					defamap.remove("fieldsetid");
				}
			}
			if(delList.size()>0) {//筛选条件 去除 家庭成员与简历
				fieldList.removeAll(delList);
			}
			
			this.getFormHM().put("queryFields", fieldList);
			this.getFormHM().put("defaultFields", defaultList);
			this.getFormHM().put("flagType", true);
			ArrayList btnList = new ArrayList();
			StringBuffer sbf = new StringBuffer();
			sbf.append("<jsfn>");
			sbf.append("{xtype:'button',text:'功能导航',menu:["
					+ "{text:'导出Lrmx文件',"
						+ "menu:["
						+ "{text:'导出部分人员',handler:function(){managerGobal.outLRMX('xml','false');}},"
						+ "{text:'导出全部人员',handler:function(){managerGobal.outLRMX('xml','all');}}"
						+ "]},"
					+ "{text:'刷新',menu:["
					+ "{text:'刷新部分人员',handler:function(){managerGobal.loadData();}},"
					+ "{text:'刷新全部人员',handler:function(){managerGobal.loadData('all');}}"
					+ "]}"
					/*+",{text:'导入'},"
					+ "{text:'同步',menu:[{text:'同步部分人员'},{text:'同步全部人员'}]}"*/
					+ "]},");
			
			sbf.append("</jsfn>");
			btnList.add(sbf.toString());
			TableConfigBuilder builder = new TableConfigBuilder("OfficerManage_OfficerView", columnList, "OfficerView",
					userView, this.getFrameconn());

			
			builder.setDataSql(sql.toString());
			builder.setOrderBy(" order by A0000 ");
			//builder.setColumnFilter(true);
			builder.setTdMaxHeight(300);
			builder.setScheme(true);
			builder.setSchemePosition(TableConfigBuilder.SCHEME_POSITION_TITLE);
			builder.setSortable(true);
			builder.setTableTools(btnList);
			builder.setSelectable(true);
			builder.setTitle("干部信息管理");
			String config = builder.createExtTableConfig();
			this.getFormHM().put("config", config);
			this.getFormHM().put("b0110", b0110);
			this.getFormHM().put("postStat",postStat);
			this.getFormHM().put("dbNameList", cardViewService.getdbList((String) constantMap.get("nbase")));
		} catch (Exception e) {
			this.getFormHM().put("flagType", false);
			this.getFormHM().put("errorMsg", e.getMessage());
			e.printStackTrace();
		}
	}
}
