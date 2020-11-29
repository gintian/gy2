package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.businessobject.sys.EchartsBo;
import com.hjsj.hrms.taglib.general.ChartConstants;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author guodd
 * @Description:表格控件单指标分析
 * @date 2015-5-5
 */
public class SingleItemAnalyseTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			
			//指标id
			String itemid = this.getFormHM().get("itemid").toString();
			String itemdesc = this.getFormHM().get("itemdesc").toString();
			String codesetid = this.getFormHM().get("codesetid").toString();
			if(AdminCode.getCodeItemList(codesetid).size()<1)
				return;
			//其实就是submoduleid
			String subModuleId = this.getFormHM().get("subModuleId").toString();
			//选中的统计分析代码 ，默认为root，就是第一级
			String code = this.getFormHM().get("code").toString();
			//图表类型
			int chartType = Integer.parseInt(this.getFormHM().get("chartType").toString());
			//统计类型
			int analyseType = Integer.parseInt(this.getFormHM().get("analyseType").toString());
			//是否包含下级
			boolean  doChild = Boolean.parseBoolean(this.getFormHM().get("doChild").toString());
			//统计指标
			String itemType = (String)this.getFormHM().get("itemType");
			// 统计指标数据模式
			String itemTypeFormat = (String)this.getFormHM().get("itemTypeFormat");
			//从userview中获取数据
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
			//FieldItem fi = DataDictionary.getFieldItem(itemid);
			//String codesetid = fi.getCodesetid();
			
			
			//获取选中的代码
			ArrayList codeList = getCodeByValue(codesetid, code);
			//分析数据存储list
			ArrayList dataList = new ArrayList();
		    //只有 是sql 的情况才统计分析
			if(tableCache.getTableSql()!=null){
					/*52532 分析时取包含所有条件的sql guodd 2019-08-28*/
					String combineSql = (String)tableCache.get("combineSql");
					String sql = getDataSql(itemid, analyseType, codeList, itemType,doChild,/*tableCache.getTableSql()*/combineSql);
					HashMap dataMap = new HashMap();
					ContentDAO dao = new ContentDAO(frameconn);
					frowset = dao.search(sql);
				
					while(frowset.next()){
						CommonData dmd = new CommonData();
						String codeValue = frowset.getString(itemid);
						String codeDesc = AdminCode.getCodeName(codesetid, codeValue);//机构编码值和机构类型查询没有查询值情况 wangb 20180730 bug 38783
						if("@K".equalsIgnoreCase(codesetid) && codeDesc.length()==0){
							codeDesc = AdminCode.getCodeName("UN", codeValue);
							if(codeDesc.length()==0)
								codeDesc = AdminCode.getCodeName("UM", codeValue);
						}
						if("UM".equalsIgnoreCase(codesetid) && codeDesc.length()==0)
							codeDesc = AdminCode.getCodeName("UN", codeValue);
						codeDesc = codeDesc.length()>12?codeDesc.substring(0, 12)+"...":codeDesc;
						dmd.setDataName(codeDesc);
						dmd.setDataValue(frowset.getDouble("num")+"");
						dataMap.put(codeValue, dmd);
					}
					CodeItem ci = null;
					for(int k=0;k<codeList.size();k++){
						ci = (CodeItem)codeList.get(k);
						
						if(dataMap.containsKey(ci.getCodeitem())){
							dataList.add(dataMap.get(ci.getCodeitem()));
						}else{
							CommonData dmd = new CommonData();
							String codeDesc = ci.getCodename();
							codeDesc = codeDesc.length()>12?codeDesc.substring(0, 12)+"...":codeDesc;
							dmd.setDataName(codeDesc);
							dmd.setDataValue("0");
							dataList.add(dmd);
						}
					}
					
			}
		
			//生成统计图
			String barInfo = "true";
			//分类大于8个，不显示详细的信息了，界面占不下。鼠标指向图例可以看详细
			if(dataList.size()>8)
				barInfo = "false";
			EchartsBo scb = new EchartsBo(itemdesc,chartType, 400, 300,barInfo);
			//设置角度
			int xangle = Integer.parseInt(scb.computeXangle(dataList));
			scb.setXangle(xangle);
			
			//如果不是求和和个数，title不显示汇总信息
			if(analyseType>1 && analyseType<5)
				   scb.setIsneedsum("false");
			if(itemTypeFormat.indexOf(".")!=-1)//前台singleitemanalyse.js对象传入的itemTypeFormat 小数位数 
			   scb.setNumDecimals(itemTypeFormat.substring(itemTypeFormat.indexOf(".")+1).length());
			else if(null!=itemType&&!"".equals(itemType)&&1!=analyseType){//取指标的小数位
				   String[] array = itemType.split("_");
				   String sumItem = array[0];
				   FieldItem item = DataDictionary.getFieldItem(sumItem);
				   if(null!=item){
					   scb.setNumDecimals(item.getDecimalwidth());
					   if(4==analyseType)
						   scb.setNumDecimals(1);
				   }
			   }else{
				   scb.setNumDecimals(0);
			}
			String xml ="";
			//饼图
			if(chartType==ChartConstants.PIE || chartType==ChartConstants.PIE_3D)
			   xml = scb.outEchartPieXml(dataList,"","");
			// 柱状图
			else if(chartType==ChartConstants.VERTICAL_BAR || chartType==ChartConstants.VERTICAL_BAR_3D){
			   xml = scb.outEchartBarXml(dataList,null,null);
			}else{ 
				HashMap hm = new HashMap();
				String lineName = "个数";
				switch (analyseType) {
					case 2:
						lineName = "最大";
						break;
					case 3:
						lineName = "最小";
						break;
					case 4:
						lineName = "平均";
						break;	
					case 5:
						lineName = "求和";
						break;
				}
				hm.put(lineName, dataList);
			   xml = scb.outEchartLineXml(hm,"","");
			}
			xml = xml.replace("height:300", "height:350");
			xml = xml.replace("height:160", "height:350");
			this.getFormHM().clear();
			this.getFormHM().put("dataHtml", xml);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private ArrayList getCodeByValue(String codesetid,String code){
		ArrayList codeList = new ArrayList();
		try{
			String[] codes = code.split(",");
			if("UN".equals(codesetid) || "UM".equals(codesetid) || "@K".equals(codesetid)){
				for(int i=0;i<codes.length;i++){
					CodeItem ci = AdminCode.getCode("UN", codes[i]);
					if(ci==null)
						ci = AdminCode.getCode("UM", codes[i]);
					if(ci==null)
						ci = AdminCode.getCode("@K", codes[i]);
					if(ci==null)
						continue;
					codeList.add(ci);
				}
			}else{
				
				for(int i=0;i<codes.length;i++){
					CodeItem ci = AdminCode.getCode(codesetid, codes[i]);
					codeList.add(ci);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return codeList;
	}
	
	private String getDataSql(String itemid,int analyseType,ArrayList codeList,String itemType,boolean doChild,String sourceSql) throws GeneralException{
		StringBuilder dataSql = new StringBuilder();
		CodeItem ci = null;
		switch (analyseType) {
		case 1:
			dataSql.append("select "+itemid+",COUNT(1) num ");
			break;
		case 2:
			dataSql.append("select "+itemid+",MAX("+Sql_switcher.isnull(itemType,"0")+") num ");
			break;
		case 3:
			dataSql.append("select "+itemid+",MIN("+Sql_switcher.isnull(itemType,"0")+") num ");
			break;
		case 4:
			if(Sql_switcher.searchDbServer()== Constant.MSSQL)
				dataSql.append("select "+itemid+",AVG(cast("+Sql_switcher.isnull(itemType,"0")+" as decimal(10, 2))) num ");//xiegh add sqlserver 对int类型列求平均数不四舍五入 且不保留小数位 现将int类型转换成decimal类型
			else
				dataSql.append("select "+itemid+",AVG("+itemType+") num ");
			break;	
		case 5:
			dataSql.append("select "+itemid+",SUM("+Sql_switcher.isnull(itemType,"0")+") num ");
			break;
		}
		
		dataSql.append(" from ( ");
		if(doChild){
			/***
			 * dataSql.append(" select case ");
			for(int i=0;i<codeList.size();i++){
				ci = (CodeItem)codeList.get(i);
				dataSql.append(" when "+itemid+" like '"+ci.getCodeitem()+"%' then '"+ci.getCodeitem()+"' ");
			}
			dataSql.append(" end ");
			dataSql.append(itemid);
			if(analyseType!=1){
				dataSql.append(",");
				dataSql.append(itemType);
			}
			dataSql.append(" from (");
			dataSql.append(sourceSql);
			dataSql.append(") source ");
			dataSql.append(" ) anadata where "+itemid+" is not null ");
			 * */
			for(int i=0;i<codeList.size();i++){ //修改包含下级时 case 语句执行多条条件时只执行满足的第一个条件，其他忽略.改用union语句拼接 changxy 【13643 】
			dataSql.append(" select case ");
			ci = (CodeItem)codeList.get(i);
			dataSql.append(" when "+itemid+" like '"+ci.getCodeitem()+"%' then '"+ci.getCodeitem()+"' ");
			dataSql.append(" end ");
			dataSql.append(itemid);
			if(analyseType!=1){
				dataSql.append(",");
				dataSql.append(itemType);
			}
			dataSql.append(" from (");
			dataSql.append(sourceSql);
			dataSql.append(") source ");
			if(i!=codeList.size()-1)
				dataSql.append("union all"); //循环至最后一个不加 使用union all 将重复的添加 统计 changxy
			}
			dataSql.append(" ) anadata where "+itemid+" is not null ");
		}else{
			dataSql.append(sourceSql);
			dataSql.append(" ) anadata  where "+itemid+" in ( ");
			for(int i=0;i<codeList.size();i++){
				ci = (CodeItem)codeList.get(i);
				dataSql.append("'");
				dataSql.append(ci.getCodeitem());
				dataSql.append("',");
			}
			dataSql.append("'-1')");
		}
		dataSql.append(" group by ");
		dataSql.append(itemid);
		return dataSql.toString();
	}
	
}
