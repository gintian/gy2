package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class PlanAnalyseTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		try{
		   
			String subModuleId = this.getFormHM().get("subModuleId").toString();
			String planId = this.getFormHM().get("planId").toString();
		    
		    this.formHM.clear();
		    TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, frameconn);
		    HashMap planConfig = tfb.getAnalysePlanConfig(planId);
		    int analyseType = Integer.parseInt(planConfig.get("analyseType").toString());
		    String itemType = (String)planConfig.get("itemType");
		    ArrayList hItems = (ArrayList)planConfig.get("h");
		    ArrayList vItems = (ArrayList)planConfig.get("v");
		    ArrayList chartList = tfb.createAnalyseChartData((ArrayList)hItems.clone(), (ArrayList)vItems.clone(),analyseType, itemType);
		    this.getFormHM().put("charts",chartList);
		    
		    
		    
		    
		    String sql =  tfb.createPlanAnalyseSql(hItems, vItems, analyseType, itemType);
		    
		    frowset = new ContentDAO(frameconn).search(sql);
		    boolean isDecimals = false;
		    int decimalLength = 0;
		    if(itemType!=null && DataDictionary.getFieldItem(itemType)!=null && (decimalLength=DataDictionary.getFieldItem(itemType).getDecimalwidth())>0){
		    	isDecimals = true;
		    }
		    ArrayList list = getAnalyseData(vItems,hItems,analyseType);
		    
		   
		    
		    StringBuilder sb = new StringBuilder("<table cellspacing=0 cellpadding=0 style='border-collapse:collapse; border:1px solid rgb(197,197,197);'>");
		    
		    if(hItems.isEmpty()){
		    	sb.append("<tr><td style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' colspan=2  >项目</td>");
		    	String title = "值";
		       if(analyseType!=1){
		    	   FieldItem fi = DataDictionary.getFieldItem(itemType);
		    	   title = fi.getItemdesc();
		       }
		    	   
		       sb.append("<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' >"+title+"</td><td style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);'>合计</td></tr>");
		    }else{
		    	sb.append("<tr><td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' colspan=2  rowspan=2>项目</td>");
			    String tr1 = "";
	        	String tr2 = "";
	            for(int i=0;i<hItems.size();i++){
	            	HashMap db = (HashMap)hItems.get(i);
	            	String itemdesc = db.get("itemName").toString();
	            	ArrayList code = (ArrayList)db.get("conds");
	            	ArrayList child = (ArrayList)db.get("child");
	            	if(child.isEmpty()){
	            		tr1+="<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center' colspan="+code.size()+">"+itemdesc+"</td>";
	            		for(int k=0;k<code.size();k++){
	            			String desc = ((HashMap)code.get(k)).get("condName").toString();
	            			tr2 += "<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center'>"+desc+"</td>";
	            		}
	            		
	            		continue;
	            	}
	            	
	            	for(int k=0;k<code.size();k++){
	            		String codedesc = ((HashMap)code.get(k)).get("condName").toString();
	            	    int colspanNum = 0;
		            	for(int b=0;b<child.size();b++){
		            		HashMap cdb = (HashMap)child.get(b);
		                	ArrayList ccode = (ArrayList)cdb.get("conds");
		                	colspanNum+=ccode.size();
		                	for(int d =0;d<ccode.size();d++){
		                		String desc = ((HashMap)ccode.get(d)).get("condName").toString();
		                		tr2+="<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center' >"+desc+"</td>";
		                	}
		                	
		            	}
		            	tr1 +="<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center' colspan="+colspanNum+">"+codedesc+"</td>";
	            	}
	            
	            }
			    sb.append(tr1);
			    sb.append("<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' rowspan=2 align='center'>合计</td>");
			    sb.append("</tr><tr>"+tr2+"</tr>");
		    }
		    
		    
		    ArrayList vTitleList = new ArrayList();
		    if(vItems.isEmpty()){
		    	String title = "值";
		    	if(analyseType!=1){
			    	   FieldItem fi = DataDictionary.getFieldItem(itemType);
			    	   title = fi.getItemdesc();
			       }
			       vTitleList.add("<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' colspan=2>"+title+"</td>");
			       vTitleList.add("<td nowrap colspan=2>合计</td>");
		    }else{
			    for(int i=0;i<vItems.size();i++){
			    	HashMap db = (HashMap)vItems.get(i);
	            	String itemdesc = db.get("itemName").toString();
	            	ArrayList code = (ArrayList)db.get("conds");
	            	ArrayList child = (ArrayList)db.get("child");
	            	if(child.isEmpty()){
	            		
	            		for(int k=0;k<code.size();k++){
	            			String desc = ((HashMap)code.get(k)).get("condName").toString();
	            			String str = "";
	            			if(k==0){
	            				str+="<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center' rowspan="+code.size()+">"+itemdesc+"</td>";
	            			}
	            			str += "<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center' >"+desc+"</td>";
	            			vTitleList.add(str);
	            		}
	            		continue;
	            	}
	            	
	            	for(int k=0;k<code.size();k++){
	            	    String codedesc = ((HashMap)code.get(k)).get("condName").toString();
		            	int rowspanNum = 0;
		            	ArrayList strs = new ArrayList();
		            	for(int b=0;b<child.size();b++){
		            		HashMap cdb = (HashMap)child.get(b);
		                	ArrayList ccode = (ArrayList)cdb.get("conds");
		                	rowspanNum+=ccode.size();
		                	for(int d =0;d<ccode.size();d++){
		                		String desc = ((HashMap)ccode.get(d)).get("condName").toString();
		                		String str="<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center' >"+desc+"</td>";
		                		strs.add(str);
		                	}
		            	}
		            	String spanStr ="<td nowrap style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center' rowspan="+(rowspanNum)+">"+codedesc+"</td>";
		            	vTitleList.add(spanStr+strs.get(0).toString());
		            	strs.remove(0);
		            	vTitleList.addAll(strs);
	            	}
			    }
		    }
		    double total = 0;
		    double[] htotal = null;
		    for(int i=0;i<list.size();i++){
		    	String[] rowData = list.get(i).toString().split(",");
		    	String str = vTitleList.get(i).toString();
		    	if(htotal==null)
		    		htotal = new double[rowData.length];
		    	double  hTotal = 0;
		    	sb.append("<tr>");
		    	sb.append(str);
		    	for(int k=0;k<rowData.length;k++){
		    		double value = Double.parseDouble(rowData[k]);
		    		sb.append("<td style='border:1px solid rgb(197,197,197);padding:5px 10px 5px 10px;' align='right' >"+String.format("%."+decimalLength+"f", value)+"</td>");
		    		hTotal+= value;
		    		total+= value;
		    		htotal[k] = htotal[k]+value;
		    	}
		    	sb.append("<td style='border:1px solid rgb(197,197,197);padding:5px 10px 5px 10px;'  align='right' >"+String.format("%."+decimalLength+"f", hTotal)+"</td>");
		    	sb.append("</tr>");
		    }
		    sb.append("<tr><td nowrap colspan=2 style='background-color:rgb(240,240,240);padding:5px 20px 5px 20px;border:1px solid rgb(197,197,197);' align='center'>合计</td>");
		    for(int i=0;i<htotal.length;i++){
		    	sb.append("<td style='border:1px solid rgb(197,197,197);padding:5px 10px 5px 10px;'  align='right'>"+String.format("%."+decimalLength+"f", htotal[i])+"</td>");
		    }
		    sb.append("<td style='border:1px solid rgb(197,197,197);padding:5px 10px 5px 10px;' align='right'>"+String.format("%."+decimalLength+"f", total)+"</td></tr>");
		    sb.append("</table>");
		    
		    
		    this.formHM.put("table", sb.toString());
		    
		    HashMap exportParam = new HashMap();
		    exportParam.put("hItems", hItems);
		    exportParam.put("vItems", vItems);
		    exportParam.put("values", list);
		    exportParam.put("itemType", itemType);
		    exportParam.put("analyseType", analyseType);
		    userView.getHm().put(subModuleId+"_export", exportParam);
		    
		}catch(Exception e){
			e.printStackTrace();
		}
	    
	}
	
	
	/**
	 * 获取对应计算值
	 * @param isDecimals 是否小数
	 * @param rowCode1   纵向 一级统计指标代码信息        （ 必须）
	 * @param rowCode2   纵向 二级统计指标代码信息       （ 如果没有二级，传null）
	 * @param colCode1   横向 一级统计指标代码信息         （必须）
	 * @param colCode2   横向 二级统计指标代码信息         （如果没有二级，传null）
	 * @return
	 */
	private String getAnalyseCount(HashMap rowCode1,HashMap rowCode2,HashMap colCode1,HashMap colCode2,int analyseType){
		double value = 0;
		int totalCount = 0;
		boolean init = false;
		try{
			if(frowset.first()){
				do{
					boolean flag =true;
					if(rowCode1!=null){
						String rcode1 = rowCode1.get("staticItemId").toString();
						flag = flag && frowset.getString("cond_"+rcode1)!=null;
					}
					if(!flag)
						continue;
					if(rowCode2!=null){
						String rcode2 = rowCode2.get("staticItemId").toString();
						flag = flag && frowset.getString("cond_"+rcode2)!=null;
					}
					if(!flag)
						continue;
					if(colCode1!=null){
						String ccode1 = colCode1.get("staticItemId").toString();
						flag = flag && frowset.getString("cond_"+ccode1)!=null;
					}
					if(!flag)
						continue;
					if(colCode2!=null){
						String ccode2 = colCode2.get("staticItemId").toString();
						flag = flag && frowset.getString("cond_"+ccode2)!=null;
					}
					if(!flag)
						continue;
					/*if(frowset.getObject("num")==null)
						continue;
					*/
					if(!init && (analyseType==2 || analyseType==3)){
						value = frowset.getDouble("num");
						init=true;
					}
					
					double itemValue = frowset.getDouble("num");
					switch(analyseType){
						case 2://最大
							value = itemValue>value?itemValue:value;
							break;
						case 3://最小
							value = itemValue<value?itemValue:value;
							break;
						case 4://平均
							value+=itemValue;
							totalCount++;
							break;
						default://1:count 5:sum 都是求和，走这里
							value+=itemValue;
					}
					
				}while(frowset.next());
			}
		}catch(Exception e){
			
		}
		if(analyseType==4 && totalCount != 0)
			value = value/totalCount;
		return value+"";
		
	}
     
	private  ArrayList  getAnalyseData(ArrayList rowItems,ArrayList colItems,int analyseType){
		ArrayList values = new ArrayList();
		
		//纵向没有的时候
		if(rowItems.isEmpty()){
			String valueStr = getColValue(colItems,null,null,analyseType);
			values.add(valueStr);
			return values;
		}
		
		for(int i=0;i<rowItems.size();i++){
		   HashMap rowItem = (HashMap)rowItems.get(i);
		   ArrayList code = (ArrayList)rowItem.get("conds");
		   ArrayList child = (ArrayList)rowItem.get("child");
		   if(child.isEmpty()){
			   for(int k=0;k<code.size();k++){
				  String valueStr = getColValue(colItems,(HashMap)code.get(k),null,analyseType);
				  values.add(valueStr);
			   }
			   
			   continue;
		   }
		   
		   for(int k=0;k<code.size();k++){
			   HashMap code1 = (HashMap)code.get(k);
			   for(int b=0;b<child.size();b++){
					HashMap cdb = (HashMap)child.get(b);
					ArrayList ccode = (ArrayList)cdb.get("conds");
					for(int c =0;c<ccode.size();c++){
						String valueStr = getColValue(colItems,code1,(HashMap)ccode.get(c),analyseType);
						  values.add(valueStr);
					}
				}
			   
		   }
		   
		}
		
		return values;
		
	}
	
	private String getColValue(ArrayList colItems,HashMap rowCode1,HashMap rowCode2,int analyseType){
		
		StringBuilder valueStr = new StringBuilder();
		//横向没有定义条件
		if(colItems.isEmpty()){
			String value = getAnalyseCount(rowCode1,rowCode2,null,null,analyseType);
			return value;
		}
		
		for(int i=0;i<colItems.size();i++){
			HashMap colItem = (HashMap)colItems.get(i);
			ArrayList code = (ArrayList)colItem.get("conds");
			ArrayList child = (ArrayList)colItem.get("child");
			
			if(child.isEmpty()){
				for(int k=0;k<code.size();k++){
					   String value = getAnalyseCount(rowCode1,rowCode2,(HashMap)code.get(k),null,analyseType);
					   valueStr.append(value+",");
				}
				
				continue;
			}
			
			for(int k=0;k<code.size();k++){
				HashMap code1 = (HashMap)code.get(k);
				
				for(int b=0;b<child.size();b++){
					HashMap cdb = (HashMap)child.get(b);
					ArrayList ccode = (ArrayList)cdb.get("conds");
					for(int c =0;c<ccode.size();c++){
						String value = getAnalyseCount(rowCode1,rowCode2,code1,(HashMap)ccode.get(c),analyseType);
						valueStr.append(value+",");
					}
				}
			}
			
		}
		
		return valueStr.toString();
	}
}
