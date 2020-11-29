package com.hjsj.hrms.module.gz.salaryreport.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.*;
/**
 *
 * <p>Title:SalaryReportTrans.java</p>
 * <p>Description>:薪资报表展现交易</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 4, 2017 5:42:56 PM</p>
 * <p>@version: 7.0</p>
 * <p>@author:zhaoxg</p>
 */
public class SalaryReportTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try{
			String gz_module = (String) this.getFormHM().get("gz_module");////薪资和保险区分标识  1：保险  否则是薪资
			if(!StringUtils.isBlank(gz_module))
				gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
			String model = (String) this.getFormHM().get("model");//model=0工资发放进入，=1工资审批进入，=3，4是工资历史数据进入  3表示未归档 4表示归档。
			String rsid = (String) this.getFormHM().get("rsid");//表类号
			String rsdtlid = (String) this.getFormHM().get("rsdtlid");//具体表号

			String salaryid = (String) this.getFormHM().get("salaryid");//薪资类别
			if(!StringUtils.isBlank(salaryid))
				salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));

			String bosdate = (String) this.getFormHM().get("bosdate");//业务如期
			if(!StringUtils.isBlank(bosdate)) {
				bosdate = PubFunc.decrypt(SafeCode.decode(bosdate));
				if(bosdate.length() == 7)
					bosdate = bosdate + "-01";
				else if(bosdate.length() != 10)
					throw GeneralExceptionHandler.Handle(new Exception("业务日期异常，请检查业务日期是否正确"));
			}

			String count = (String) this.getFormHM().get("count");//发放次数
			if(!StringUtils.isBlank(count))
				count = PubFunc.decrypt(SafeCode.decode(count));

			String groupvalues = (String) this.getFormHM().get("groupvalues");//分组值
			if(!StringUtils.isBlank(groupvalues))
				groupvalues = SafeCode.decode(groupvalues);
		    if(salaryid==null||salaryid.length()<1){
		    	MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");//查询组件返回条件集合
		    	if(bean!=null){
					gz_module = (String) bean.get("gz_module");////薪资和保险区分标识  1：保险  否则是薪资
					gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
					model = (String) bean.get("model");//model=0工资发放进入，=1工资审批进入，=3是工资历史数据进入。
					rsid = (String) bean.get("rsid");//表类号
					rsdtlid = (String) bean.get("rsdtlid");//具体表号
					salaryid = (String) bean.get("salaryid");//薪资类别
					salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
					bosdate = (String) bean.get("bosdate");//业务日期
					bosdate = PubFunc.decrypt(SafeCode.decode(bosdate));
					count = (String) bean.get("count");//发放次数
					count = PubFunc.decrypt(SafeCode.decode(count));
					groupvalues = (String) bean.get("groupvalues");//分组值
		    	}
		    }
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			SalaryReportBo salarybo=new SalaryReportBo(this.getFrameconn(),salaryid,this.userView,rsid, rsdtlid,false);
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.getFrameconn(), Integer.parseInt(salaryid));
			String priv = ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag"); // 人员范围权限过滤标志 1：有
			String manager = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET,"user");
			String tableName = "";

			if ("0".equals(model)) {
				if (manager.length() == 0 || manager.equalsIgnoreCase(this.userView.getUserName())) {
					tableName = this.userView.getUserName() + "_salary_" + salaryid;
				} else {
					tableName = manager + "_salary_" + salaryid;
				}
			} else if ("1".equals(model) || "3".equalsIgnoreCase(model)) {
				tableName = "salaryhistory";
			} else if ("4".equals(model)) {
				tableName = "salaryarchive";
			}


			StringBuffer privsql = new StringBuffer();//权限sql 各表均用
			if("0".equals(model)){
				if(gzbo.getManager()!=null&&gzbo.getManager().length()>0&&!this.userView.getUserName().equalsIgnoreCase(gzbo.getManager()))//共享非管理员
					privsql.append(gzbo.getWhlByUnits(tableName, true));
				privsql.append(gzbo.getfilter(tableName));
				privsql.append(" and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3='"+count+"'");
			}else if("1".equals(model)){
				privsql.append("  and ((((AppUser is null  "+gzbo.getWhlByUnits(tableName, true)+" ) or AppUser Like '%;"+this.userView.getUserName()+";%' )) or curr_user='"+this.userView.getUserName()+"')");
				privsql.append(" and salaryid='"+salaryid+"' and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3='"+count+"'");
				String UserFlag = (String) this.getFormHM().get("UserFlag");
				if(UserFlag!=null&& UserFlag.length()>0&&!"all".equalsIgnoreCase(UserFlag)){
					privsql.append(" and UserFlag='"+ UserFlag +"'");
				}
			}else if(StringUtils.equalsIgnoreCase("3",model)||StringUtils.equalsIgnoreCase("4",model)){// 薪资分析-薪资历史数据 3 未归档 4已归档
				privsql.append(" and salaryid = ").append(salaryid);
				privsql.append(gzbo.getUnitsPrivSql());
				privsql.append(gzbo.getHistoryFilter(tableName));
				privsql.append(" and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3='"+count+"'");
			}

			if("1".equals(rsid)||"2".equals(rsid)||"3".equals(rsid)||"12".equals(rsid)||"13".equals(rsid)){
				//同步reportitem
				salarybo.synReportSet(salaryid,rsdtlid);

				String groupField=salarybo.getReportdetailvo().getString("fgroup").toLowerCase();//分组指标
				Boolean bgroup=false;//是否启用分组汇总标记
				if(!("3".equals(rsid)||"13".equals(rsid)))
					bgroup= "0".equals(salarybo.getReportdetailvo().getString("bgroup"))?false:true;//是否启用分组
				else
					bgroup=true;//汇总表默认启用分组

				groupField=bgroup==true?groupField:"";

				ArrayList headlist = salarybo.getTableHeadDescList();


				//------------------------------拼装表格列---------------------------
				ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();
				LazyDynaBean groupBean = salarybo.getGroupBean();
				String f_groupItem = groupBean.get("f_groupItem").toString();
				if("3".equals(rsid)|| "13".equals(rsid) || "2".equals(rsid) || "12".equals(rsid))
				{
					String f_groupDesc=(String)groupBean.get("f_groupDesc");
					String s_groupDesc=(String)groupBean.get("s_groupDesc");
					String columnName=f_groupDesc;
					if(s_groupDesc.length()>0)
						columnName+="&"+s_groupDesc;
					if(bgroup&&StringUtils.isNotBlank(f_groupItem.toLowerCase()))
						columnsInfo.add(salarybo.getColumnsInfo(f_groupItem.toLowerCase(), "0", columnName, "A", 0,"", "10", false));
					if(!"2".equals(rsid) && !"12".equals(rsid))
						columnsInfo.add(salarybo.getColumnsInfo("num", "0", "人数", "N", 0,"", "10", false));

					ColumnsInfo column = salarybo.getColumnsInfo("iscollect", "0", "是否是合计行", "A", 0,"", "10", false);//用来判断是否改改变行颜色
					column.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
					columnsInfo.add(column);
				}

				//获取列头
				for(int i=0;i<headlist.size();i++){
					LazyDynaBean bean = (LazyDynaBean) headlist.get(i);
					ColumnsInfo tempinfo=salarybo.getColumnsInfo(bean.get("itemid").toString().toLowerCase(), bean.get("codesetid").toString(), bean.get("itemdesc").toString(),
							bean.get("itemtype").toString(), Integer.parseInt(bean.get("decimalwidth").toString()),bean.get("fieldsetid").toString(),bean.get("itemlength").toString(),bgroup);

					//如果是签名表 分组项需要锁列，将汇总指标列改为显示文字 方便显示总计 合计。
					if(("2".equals(rsid)||"12".equals(rsid))&& !StringUtils.isBlank(groupField) &&bean.get("itemid").toString().equalsIgnoreCase(groupField)){
						//tempinfo.setLocked(true);
						tempinfo.setCodesetId("");
						columnsInfo.set(0, tempinfo);
					}else{
						if("UM".equalsIgnoreCase((tempinfo.getCodesetId()))||"UN".equalsIgnoreCase((tempinfo.getCodesetId()))){//xiegh 20170503 bug26901 add使之走业务范围
							tempinfo.setCtrltype("3");//Ctrltype:权限范围
							tempinfo.setNmodule("1");//setNmodule：设置模块号
						}
						columnsInfo.add(tempinfo);
					}
				}
				if("2".equals(rsid)||"12".equals(rsid)){
					ColumnsInfo tempinfo=salarybo.getColumnsInfo("signature", "0", "签名", "A", 0,"", "10", false);
					tempinfo.setRendererFunc("openSalaryReportScope.renderTo");
					tempinfo.setFilterable(false);
					tempinfo.setSortable(false);
					columnsInfo.add(tempinfo);
				}

				//------------------------------表格列拼装结束---------------------------
				//------------------------------------构建栏目设置------------------------------
				FieldItem field=null;
				LinkedHashMap columnMap=new LinkedHashMap();
				for(ColumnsInfo col:columnsInfo){
					String colid=col.getColumnId();
					String colName=col.getColumnDesc();
					if("a00z0".equalsIgnoreCase(colid)|| "a00z2".equalsIgnoreCase(colid)){
						field=new FieldItem();
						field.setItemtype("D");
						field.setItemdesc("a00z2".equalsIgnoreCase(colid)?"发放日期":"归属日期");
						field.setItemid(colid);
						field.setDisplaywidth(10);
						field.setItemlength(10);
						field.setFormat("yyyy-mm-dd");
						field.setDecimalwidth(0);
					}else if("a00z1".equalsIgnoreCase(colid)|| "a00z3".equalsIgnoreCase(colid)){
						field=new FieldItem();
						field.setItemdesc("a00z3".equalsIgnoreCase(colid)?"发放次数":"归属次数");
						field.setItemtype("N");
						field.setDisplaywidth(5);
						field.setItemid(colid);
						field.setFormat("yyyy-mm-dd");
						field.setDecimalwidth(0);
						field.setAlign("right");
					}else if("signature".equalsIgnoreCase(colid)&&("2".equals(rsid)|| "12".equals(rsid))){//签名列
						field=new FieldItem();
						field.setItemdesc("签名列");
						field.setItemtype("A");
						field.setDisplaywidth(10);
						field.setItemid(colid);
						field.setFormat("yyyy-mm-dd");
						field.setDecimalwidth(0);
					}else if("num".equalsIgnoreCase(colid)&&("3".equals(rsid)|| "13".equals(rsid))){//人数
						field=new FieldItem();
						field.setItemdesc("人数");
						field.setItemtype("N");
						field.setDisplaywidth(10);
						field.setItemid(colid);
						field.setFormat("yyyy-mm-dd");
						field.setDecimalwidth(0);
					}else if("iscollect".equalsIgnoreCase(colid)&&("3".equals(rsid)|| "13".equals(rsid))){
						field=new FieldItem();
						field.setItemdesc("是否是合计行");
						field.setItemtype("N");
						field.setDisplaywidth(5);
						field.setItemid(colid);
						field.setFormat("yyyy-mm-dd");
						field.setDecimalwidth(0);
					}else if("A01Z0".equalsIgnoreCase(colid)){
						field=new FieldItem();
						field.setItemdesc("停发标识");
						field.setItemtype("A");
						field.setDisplaywidth(10);
						field.setItemid(colid);
						field.setFormat("yyyy-mm-dd");
						field.setCodesetid("ZZ");
					}else if(f_groupItem.equalsIgnoreCase(colid)&&bgroup) {//显示出分组时候的全称
						field=new FieldItem();
						field.setItemdesc(colName);
						field.setItemtype("A");
						field.setDisplaywidth(10);
						field.setItemid(colid);
					}else
						field = DataDictionary.getFieldItem(colid);
					columnMap.put(colid, field);
				}

				ArrayList<ColumnsInfo> columnsItem=salarybo.getColumnsInfo(columnMap, rsid,bgroup);//获取栏目设置列

				if(columnsItem.size()!=columnsInfo.size())//若不存在栏目设置，则使用全部项目
				{
					Boolean ishave=false;
					for(ColumnsInfo col:columnsInfo){
						ishave=false;
						for(ColumnsInfo temp:columnsItem){
							if(col.getColumnId().equalsIgnoreCase(temp.getColumnId()))
							{
								if(f_groupItem.equalsIgnoreCase(col.getColumnId()))
									temp.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
								ishave=true;
								break;
							}
						}
						if(!ishave){
							if(("3".equals(rsid)|| "13".equals(rsid)|| "2".equals(rsid)|| "12".equals(rsid)) && bgroup) {
								if("n".equalsIgnoreCase(col.getColumnType())&&col.getDecimalWidth()>0)
									col.setRendererFunc("openSalaryReportScope.numRenderTo");
								else
									col.setRendererFunc("openSalaryReportScope.renderTo");
							}
							columnsItem.add(col);
						}
					}
				}
					//columnsItem=columnsInfo;


				//------------------------------------栏目设置构建结束------------------------------


				//------------------------------------获取页面数据-----------------------------
				String condSql = this.getCondSql("salaryreport_"+rsdtlid);
				if(condSql.length()>0){
					condSql = condSql.replaceAll("data", tableName);
					privsql.append(" and ( "+condSql+")");
				}

				String sql="";
				ArrayList<LazyDynaBean> list=null;
				if("1".equals(rsid)||(   ("2".equals(rsid)|| "12".equals(rsid))&&!bgroup  )){//若为 工资单或者没有设置汇总指标的签名表 则向表格控件写入sql，若存在汇总行为 则拼接list后发送到页面。
					ArrayList tabHeadList=salarybo.getTableHeadDescList();
					sql=salarybo.getReportSql("", tabHeadList, null, tableName, privsql.toString());

				}else
					list = salarybo.getRecordList(tableName, privsql.toString(), groupvalues,model);
				if(("2".equals(rsid)|| "12".equals(rsid))&&bgroup) {
					//在给行付颜色的时候，用columnsInfo.setRendererFunc("openSalaryReportScope.renderTo");之后数据出现问题，这里
					//对要传的数据进行拼接将代码转为汉字
					Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
					String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
					if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
						display_e0122="0";

					list = salarybo.getDataToList(list,display_e0122,columnMap,columnsInfo);
				}


				if(condSql.length()>0){//页面模糊查询
					TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("salaryreport_"+rsdtlid);
					tableCache.setTableSql(sql);
					this.userView.getHm().put("salaryreport_"+rsdtlid, tableCache);
					return;
				}
				//------------------------------------数据获取结束-------------------------------


				TableDataConfigCache configCache = new TableDataConfigCache();
				configCache.setTableColumns(columnsInfo);
				configCache.setColumnMap(columnMap);
				Integer pagesize = new Integer(20);
				configCache.setPageSize(pagesize);
				userView.getHm().put("salaryreport_"+rsdtlid, configCache);



				ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
				ButtonInfo pageSet = new ButtonInfo("页面设置","openSalaryReportScope.pageSet");
				pageSet.setIcon("/images/img_o.gif");
				buttonList.add(pageSet);
				ButtonInfo excel = new ButtonInfo("输出","openSalaryReportScope.ExportExcel");
				excel.setIcon("/images/portExcel.png");
				buttonList.add(excel);
				ButtonInfo pdf = new ButtonInfo("打印","openSalaryReportScope.showPdfWin");
				pdf.setIcon("/images/portpdf.png");
				buttonList.add(pdf);
				if("3".equals(rsid)|| "13".equals(rsid))
				{
					buttonList.add(new ButtonInfo("设置范围","openSalaryReportScope.defineGroup"));
				}else if(!("2".equals(rsid)||"12".equals(rsid))){
					ButtonInfo button = new ButtonInfo("请输入姓名",ButtonInfo.TYPE_QUERYBOX,"GZ00000507");
					button.setType(ButtonInfo.TYPE_QUERYBOX);
					buttonList.add(button);
				}




				TableConfigBuilder builder = new TableConfigBuilder("salaryreport_"+rsdtlid, columnsItem, "salaryreport", userView, this.getFrameconn());
				if("1".equals(rsid) ||(   ("2".equals(rsid)|| "12".equals(rsid))&&!bgroup  )){//若为 工资单或者没有设置汇总指标的签名表 则向表格控件写入sql，若存在汇总行为 则拼接list后发送到页面。
					builder.setDataSql(sql);
					builder.setOrderBy(" order by  dbid,a0000, A00Z0, A00Z1 ");
					builder.setColumnFilter(true);
					builder.setSortable(true);
				}
				else{
					builder.setDataList(list);
					builder.setColumnFilter(false);
					builder.setSortable(false);
				}
				builder.setSchemePosition("salaryreport_schemeSetting");//栏目设置渲染位置
				builder.setSchemeSaveCallback("openSalaryReportScope.closeSettingWindow");//栏目设置关闭回调
		    	builder.setAutoRender(false);
		    	//不加导致栏目设置分分页失败
		    	builder.setScheme(true);
				builder.setEditable(false);
				builder.setSetScheme(true);
				builder.setSelectable(false);
				//是超出字符的栏可以显示title
				builder.setLockable(true);
				//区别薪资，保险，工资发放，审批的权限
				if(userView.isSuper_admin()||
						(!"1".equals(gz_module)&&(("0".equals(model)&&userView.hasTheFunction("32402050304"))||("1".equals(model)&&userView.hasTheFunction("32403100104"))))||
						("1".equals(gz_module)&&(("0".equals(model)&&userView.hasTheFunction("32502050304"))||("1".equals(model)&&userView.hasTheFunction("32503100204")))))
					builder.setShowPublicPlan(true);

				builder.setTableTools(buttonList);
				String config = builder.createExtTableConfig();
				this.getFormHM().put("groupField", groupField);

				this.getFormHM().put("subModuleId", "salaryreport_"+rsdtlid);

				this.getFormHM().put("tableConfig", config);
//				this.getFormHM().put("datalist", list);
			}else if("4".equals(rsid)){
				String baseid = (String) this.getFormHM().get("baseid");//统计项目
				String itemid = (String) this.getFormHM().get("itemid");//统计指标


				if(StringUtils.isBlank(baseid)){
					ArrayList<LazyDynaBean> baseList = salarybo.getReportItem("A");

					if(baseList==null||baseList.size()==0)
						 throw GeneralExceptionHandler.Handle(new Exception("此薪资账套不存在可统计的代码类型分组指标！"));

					LazyDynaBean bean = (LazyDynaBean) baseList.get(0);
					baseid = (String) bean.get("value");
					this.getFormHM().put("basedata", baseList);
				}
				if(StringUtils.isBlank(itemid)){
					ArrayList<LazyDynaBean> itemList = salarybo.getReportItem("N");

					if(itemList==null||itemList.size()==0)
						 throw GeneralExceptionHandler.Handle(new Exception("此薪资账套不存在可统计的数值类型指标！"));
					LazyDynaBean bean = (LazyDynaBean) itemList.get(0);
					itemid = (String) bean.get("value");
					this.getFormHM().put("itemdata", itemList);
				}
				HashMap<String, Double> hm=new HashMap<String, Double>();
				ArrayList<LazyDynaBean> dataList = salarybo.getGzAnalyseList(rsid, baseid, itemid, tableName, privsql.toString(),hm);

				ArrayList<LazyDynaBean> graphDataList = this.getGraphDataList(hm,baseid);

				TableConfigBuilder builder = new TableConfigBuilder("salarycollect", salarybo.getGzAnalyseHeadDesc(baseid,itemid), "salaryAnalyse", userView, this.getFrameconn());
				builder.setDataList(dataList);
		    	builder.setAutoRender(false);
		    	builder.setSortable(false);
		    	builder.setSetScheme(false);
				builder.setEditable(false);
				builder.setSetScheme(false);
				builder.setSelectable(true);
				builder.setPageSize(20);
				builder.setSelectable(false);
				String config=  builder.createExtTableConfig();

				this.getFormHM().put("graphDataList", graphDataList);
				this.getFormHM().put("datalist", dataList);
				this.getFormHM().put("tableConfig", config);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 获取查询条件sql片段
	 * @param id
	 * @return
	 */
	public String getCondSql(String id){
		String condSql = "";
		try{
			// 查询类型，1为输入查询，2为方案查询
			String type = (String) this.getFormHM().get("type");
			ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");//页面查询框返回的内容
			if("1".equals(type)) {
				// 输入的内容
				StringBuffer str = new StringBuffer();
				for(int i=0;i<valuesList.size();i++){
					String queryValue = SafeCode.decode((String) valuesList.get(i));
					if(i==0){
						str.append("a0101 like '%"+queryValue+"%'");
					}else{
						str.append(" or a0101 like '%"+queryValue+"%'");
					}
				}
				if(valuesList.size()>0)
					condSql += str.toString();
			} else if ("2".equals(type)) {
				String exp = (String) this.getFormHM().get("exp");
				String cond = (String) this.getFormHM().get("cond");
				TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(id);
				HashMap queryFields = tableCache.getQueryFields();
				// 解析表达式并获得sql语句
				FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(),queryFields);
				condSql += parser.getSingleTableSqlExpression("data");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return condSql;
	}




	/**
	 *
	 * @Title: getGraphDataList
	 * @Description:数据大小排序，方便前台按大小组织统计图
	 * @param  hm
	 * @param strbaseid
	 * @return ArrayList
	 * @author:zhaoxg
	 * @throws
	 */
	private ArrayList<LazyDynaBean> getGraphDataList(HashMap<String, Double> hm,String strbaseid){

		ArrayList<LazyDynaBean> list=new ArrayList<LazyDynaBean>();
		try{
			ArrayList<Map.Entry<String, Double>> datalist=new ArrayList<Map.Entry<String, Double>>(hm.entrySet());
			Collections.sort(datalist, new Comparator<Map.Entry<String, Double>>() {
			    @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
			    	int i=0;
			    	if(o2.getValue() - o1.getValue()>0)
			    		i=1;
			    	else if(o2.getValue() - o1.getValue()==0)
			    		i=0;
			    	else
			    		i=-1;
			        return (i);
			    }
			});

			FieldItem fielditem=DataDictionary.getFieldItem(strbaseid);
			String codesetid=fielditem.getCodesetid();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);

			LazyDynaBean bean=null;
			Double sum=0.0;
			for(int i=0;i<datalist.size();i++){

				Map.Entry<String,Double> m=datalist.get(i);
					String content="";
					String value=m.getKey();
					bean=new LazyDynaBean();

					if(!StringUtils.isBlank(codesetid)){
					    if("un".equalsIgnoreCase(codesetid)){
					        content = AdminCode.getCodeName("UN", value);
					        if(StringUtils.isBlank(content))
					            content = AdminCode.getCodeName("UM", value);
					    }
					    else if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值
						{
							if("e0122".equalsIgnoreCase(fielditem.getItemid()))
							{
								if(Integer.parseInt(display_e0122)==0)
									content=AdminCode.getCodeName("UM",value);
								else
								{
									CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
					    	    	if(item!=null)
					    	    	{
					    	    		content=item.getCodename();
					        		}
					    	    	else
					    	    	{
					    	    		content=AdminCode.getCodeName("UM",value);
					    	    	}
								}
							}else
								content = (AdminCode.getCodeName("UM",value)==null || (AdminCode.getCodeName("UM",value)!=null && AdminCode.getCodeName("UM",value).trim().length()==0))?AdminCode.getCodeName("UN",value): AdminCode.getCodeName("UM",value);

					}else
						content=AdminCode.getCodeName(codesetid, value);

					    bean.set("baseid",content+"="+value );
				}else
					bean.set("baseid", value+"="+value);

					bean.set("percentnum", m.getValue().toString());
					list.add(bean);

			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}
