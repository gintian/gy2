package com.hjsj.hrms.module.muster.showmuster.transaction;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;


/**
 * 展示花名册数据范围页面，包括数据回显。
 * @author caoz,jiaok
 *
 */
public class ShowMusterDataRangeTrans extends  IBusiness{
	@Override
	public void execute() throws GeneralException {
		try {
			String opt=(String) this.getFormHM().get("opt");
			if("echoData".equals(opt)) {//回显数据//数据范围所选花名册下面的子集和指标
				echoData();
				getSubsetIndicator();
			}else if("currentUserOwnPersonnel".equals(opt)){//数据范围查询当前登陆用户拥有的人员库权限
				getCurrentUserOwnPersonnel();
			}else if("changeByMYsetName".equals(opt)){//数据范围获取按月变化子集id
				getChangeByMYsetName();
			}else if("filter".equals(opt)) {//数据范围过滤条件回显
				filter();
			}else if("partHistoryIndicator".equals(opt)) {//数据范围部分历史记录指标回显
				getPartHistoryIndicator();
			}else if("setConditionIndicator".equals(opt)) {//数据范围可以设置条件的指标回显
				getSetConditionIndicator();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 数据范围所选花名册下面的子集和指标
	 * @throws GeneralException
	 */
	private void getSubsetIndicator() throws GeneralException {
		try {
		    String musterType = (String) this.getFormHM().get("musterType");
			int changeByMonthSetCount = 0;//记录年月子集个数
			int setCommonCount = 0; //记录子集个数
			int hadMainSet=0;
			int hadYearMonthSet=0;
			int hadChildSet=0;
			int itemNotM=0;//子集非备注型指标计数
			Set<String> setChildSet = new HashSet<String>();
			Set<String> yearMonthSet = new HashSet<String>();
			HashMap hm = this.getFormHM();
			ContentDAO dao = new ContentDAO(this.frameconn);
			Map<String,Integer> mainSetMap = new HashMap();
			Map<String,Integer> changeByMonthSubSetMap = new HashMap();
			Map<String,Integer> commonSubSetMap = new HashMap();
			ArrayList subsetIndicatorSqlValues = new ArrayList();
			List subsetIndicatorList = new ArrayList();
			String tabId = (String) hm.get("tabid");
			subsetIndicatorSqlValues.add(tabId);
			String subsetIndicatorSql = "select baseid,Field_name,ColHz from Lbase where tabid = ?";
			this.frowset=null;
			this.frowset = dao.search(subsetIndicatorSql,subsetIndicatorSqlValues);
			while(this.frowset.next()) {
				HashMap subsetIndicatorMap = new HashMap();
				String baseId = this.frowset.getString("baseid");
				String fieldName = this.frowset.getString("Field_name");
				/*
				 * 如果指标集不是主集且按月变化子集包含此指标集  说明此花名册包含一个主集和changeByMonthSetCount个按月变化子集
				 */
				String setName = fieldName.split("\\.")[0];
				FieldSet fieldSet = DataDictionary.getFieldSetVo(setName);
				ArrayList<FieldItem> fieldItemlist= userView.getPrivFieldList(setName,1);
				boolean flag = false;
				for (FieldItem fieldItem : fieldItemlist) {
					if (fieldItem.getItemid().equalsIgnoreCase(fieldName.split("\\.")[1])) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					if ("K01.E01A1".equalsIgnoreCase(fieldName)) {
						flag = true;
					}
				}
				if (flag) {
					if(setName.endsWith("01") && !mainSetMap.containsKey(setName)){//包含主集
						hadMainSet++;
						mainSetMap.put(setName, 1);
					}
					if(!setName.endsWith("01") && (StringUtils.equals(fieldSet.getChangeflag(),"1")||StringUtils.equals(fieldSet.getChangeflag(), "2"))) {//年月变化子集，当然也是子集
							yearMonthSet.add(setName);
							setChildSet.add(setName);
							changeByMonthSubSetMap.put(setName, -1);
					} 
					if(!setName.endsWith("01")) {//普通子集
						setChildSet.add(setName);
						commonSubSetMap.put(setName, 1);
					}
					
					String itemId=fieldName.split("\\.")[1];
	                FieldItem fieldItem=DataDictionary.getFieldItem(itemId);
	                String itemtype=fieldItem.getItemtype();
	                //基准岗位花名册，统计部分条件历史记录子集是否含有非备注型指标
	                //if("4".equals(musterType) && !setName.endsWith("01") && !"M".equals(itemtype)) {jk
	                if(!setName.endsWith("01") && !"M".equals(itemtype)) {
	                    itemNotM++;
	                }
									
					String colHz = this.frowset.getString("ColHz");
					if((!setName.endsWith("01"))){
	    				subsetIndicatorMap.put("baseId",baseId);
	    				subsetIndicatorMap.put("fieldName",fieldName);
	    				subsetIndicatorMap.put("colHz",colHz);
	    				subsetIndicatorList.add(subsetIndicatorMap);
					}
				}
			}
			hadYearMonthSet=yearMonthSet.size();
			hadChildSet=setChildSet.size();
			StringBuilder radioEnableMessage = new StringBuilder("");  
			if(hadMainSet!=1 || hadYearMonthSet!=1 || hadChildSet!=1) {
				radioEnableMessage.append("radio2disable");
			}
			if(hadMainSet!=1 || hadChildSet!=1) {
			    //if(!("4".equals(musterType) && itemNotM==0)) {//基准岗位花名册
			        radioEnableMessage.append("radio3disable");
			    //}
			}
	        hm.put("data",subsetIndicatorList);
            hm.put("radioEnableMessage",radioEnableMessage.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 * 数据范围查询当前登陆用户拥有的人员库权限
	 * @throws GeneralException
	 */
	private void getCurrentUserOwnPersonnel() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();		
			ContentDAO dao = new ContentDAO(this.frameconn);
			String currentUserOwnPersonnelSql = "select DBName,Flag,Pre from DBName order by dbid";
			Map<String, String> map = new LinkedHashMap();
			Map<String,String> resultMap;
			Map<Integer,Map<String,String>> returnMap = new LinkedHashMap();
			this.frowset = dao.search(currentUserOwnPersonnelSql);
			while(this.frowset.next()) {
				String dbName = this.frowset.getString("DBName");
				String pre = this.frowset.getString("Pre");
				map.put(pre,dbName);
			}
			int count = 0;
			for(int i = 0;i<this.userView.getPrivDbList().size();i++) {
                String pre = (String)this.userView.getPrivDbList().get(i);
                if(map.containsKey(pre)) {
                   resultMap= new LinkedHashMap();
                   resultMap.put(pre, map.get(pre));
                   returnMap.put(count, resultMap);
                   count++;
                }
			}
			hm.put("data",returnMap);
			hm.put("dataSize",count);
		}catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	
	/**
	 * 数据范围获取按月变化子集id
	 * @throws GeneralException
	 */
	private void getChangeByMYsetName() throws GeneralException {
		try {
			StringBuilder changeByMYsetName = new StringBuilder();
			HashMap hm = this.getFormHM();		
			ContentDAO dao = new ContentDAO(this.frameconn);
			String tabId = (String) hm.get("tabId");
			String getChangeByMYsetNameSql = "select Field_name from lbase where tabid=?";
			ArrayList values=new ArrayList();
			values.add(tabId);
			this.frowset = dao.search(getChangeByMYsetNameSql,values);
			while(this.frowset.next()) {
				String fieldSetId  = this.frowset.getString("Field_name").split("\\.")[0];
				if(fieldSetId.indexOf("01")==-1) {
                    FieldSet fieldSet=DataDictionary.getFieldSetVo(fieldSetId);
                    String changeflag=fieldSet.getChangeflag();
                    if("1".equals(changeflag)|| "2".equals(changeflag)) {//年或月变化子集
                        changeByMYsetName.append(fieldSetId);
                        break;
                    }
                    
                } 
			}
			hm.put("changeByMYsetId", changeByMYsetName.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	
	/**
	 * 获取数据范围过滤条件
	 * @throws GeneralException
	 */
	private void filter() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();
			List dataFilterList = new ArrayList();
			ContentDAO dao = new ContentDAO(this.frameconn);
			String dataFilterSql = "select Id,Name,Lexpr,Factor from lexpr";
			this.frowset = dao.search(dataFilterSql);
			HashMap dataFilterMap;
			
			//添加“无”选项
			dataFilterMap = new HashMap();
			dataFilterMap.put("id", "0");
			dataFilterMap.put("name", "无");
			dataFilterMap.put("lexpr", "");
			dataFilterMap.put("factor", "");
			dataFilterList.add(dataFilterMap);
			
			while(this.frowset.next()) {
				dataFilterMap = new HashMap();
				String id = this.frowset.getString("Id");
				String name = this.frowset.getString("Name");
				String lexpr = this.frowset.getString("Lexpr");
				String factor = this.frowset.getString("Factor");
				dataFilterMap.put("id", id);
				dataFilterMap.put("name", name);
				dataFilterMap.put("lexpr", lexpr);
				dataFilterMap.put("factor", factor);
				dataFilterList.add(dataFilterMap);
			}
			hm.put("data", dataFilterList);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void getPartHistoryIndicator() throws GeneralException {
	    try {
	        HashMap hm = this.getFormHM();     
	        ContentDAO dao = new ContentDAO(this.frameconn);
	        List list = new ArrayList();
	        String tabId = (String) hm.get("tabId");
	        String musterType = (String) hm.get("musterType");
	        StringBuffer sqlBuffer =new StringBuffer("");
	        sqlBuffer.append("select * from  Lbase where tabid = ");
	        sqlBuffer.append(Integer.parseInt(tabId));
	        this.frowset = dao.search(sqlBuffer.toString());
	        String fieldsetname = "";
	        while(this.frowset.next()) {
	            String fieldName = this.frowset.getString("Field_name");
	            String fieldsetid = fieldName.split("\\.")[0];
	            if (!fieldsetid.endsWith("01")) {
	                FieldSet fieldSet = DataDictionary.getFieldSetVo(fieldsetid);
	                fieldsetname = fieldSet.getFieldsetdesc();
	                //String itemid = fieldName.split("\\.")[1];
	                //FieldItem fielditem = DataDictionary.getFieldItem(itemid, fieldsetid);
	                List<FieldItem> fieldItemList = userView.getPrivFieldList(fieldsetid, 1);
	                for (FieldItem fieldItem : fieldItemList) {
	                    if ("M".equals(fieldItem.getItemtype())) {
                            continue;
                        }
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("fieldName", fieldItem.getFieldsetid()+"."+fieldItem.getItemid().toUpperCase());
                        map.put("colHz", fieldItem.getItemdesc());
                        map.put("itemType", fieldItem.getItemtype());
                        map.put("codesetId", fieldItem.getCodesetid());
                        list.add(map);
                    }
	                break;
                }
	           
	        }
	        if(!"4".equals(musterType)&&list.size()>0) {
                Map<String,String> childSetNameMap = new HashMap<String,String>();
                childSetNameMap.put("colHz", fieldsetname+"及条件...");
                childSetNameMap.put("fieldName","-1");
                list.add(childSetNameMap);
            }
            hm.put("data",list);
        } catch (Exception e) {
            e.printStackTrace();
        }
	   
	}
	/**
	 * 获取数据范围部分历史记录指标
	 * @throws GeneralException
	 *//*
	private void getPartHistoryIndicator() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();		
			ContentDAO dao = new ContentDAO(this.frameconn);
			List historyPartIndicatorList = new ArrayList();
			String tabId = (String) hm.get("tabId");
			String musterType = (String) hm.get("musterType");
			StringBuilder childSetName = new StringBuilder();
			Integer childSetNameFlag = 0;
			;
			StringBuffer historyPartIndicatorSql =new StringBuffer();
			historyPartIndicatorSql.append("select Lbase.tabid,Lbase.baseid,Lbase.Field_name,Lbase.ColHz,fieldSet.fieldSetId,fieldSet.fieldSetDesc, "
					+ "fielditem.itemtype,fielditem.codesetid,Lbase.field_type field_type,fielditem.itemtype itemtype from Lbase inner join fielditem on Lbase.tabid = ");
			historyPartIndicatorSql.append(Integer.parseInt(tabId));
			historyPartIndicatorSql.append(" and  "
					+ Sql_switcher.left("Field_name", 3)+" = fielditem.fieldsetid and  "
					+ Sql_switcher.right("Field_name", 5)+" = fielditem.itemid inner join fieldSet on "
					+ Sql_switcher.left("Field_name", 3)+"  = fieldSet.fieldSetId ");
			
			this.frowset = dao.search(historyPartIndicatorSql.toString());
			ArrayList<FieldSet> prvfieldsetList= userView.getPrivFieldSetList(1);
			while(this.frowset.next()) {//and Lbase.field_type!='M' and fielditem.itemtype!='M'
				Map<String,String> historyPartIndicatorMap = new HashMap<String,String>();
				String baseId = this.frowset.getString("baseid");
				String fieldName = this.frowset.getString("Field_name");
				String colHz = this.frowset.getString("ColHz");
				ArrayList<FieldItem> prv = userView.getPrivFieldList(fieldName.split("\\.")[0], 1);
				boolean flag = false;
				for (FieldItem fieldItem : prv) {
					if (fieldItem.getItemid().equalsIgnoreCase(fieldName.split("\\.")[1])) {
						flag = true;
						break;
					}
				}
				if (flag) {
					String itemType = this.frowset.getString("itemtype");
					String codesetId = this.frowset.getString("codesetid");
					String field_type=this.frowset.getString("field_type");
					String itemtype=this.frowset.getString("itemtype");
	                if(!fieldName.split("\\.")[0].endsWith("01")) {
	                	if(childSetNameFlag==0) {
	                		childSetName.append(this.frowset.getString("fieldSetDesc"));
	                		childSetNameFlag = 1;
	                	}
	                	//if(!"M".equals(field_type) && !"M".equals(itemtype)) {
	                	    historyPartIndicatorMap.put("baseId", baseId);
	                        historyPartIndicatorMap.put("fieldName", fieldName);
	                        historyPartIndicatorMap.put("colHz", colHz);
	                        historyPartIndicatorMap.put("itemType", itemType);
	                        historyPartIndicatorMap.put("codesetId", codesetId);
	                        historyPartIndicatorList.add(historyPartIndicatorMap);
	                	//}
	                }
				}
			}
			if(!"4".equals(musterType)&&historyPartIndicatorList.size()>0) {
			    Map<String,String> childSetNameMap = new HashMap<String,String>();
	            childSetNameMap.put("colHz", childSetName.toString()+"及条件...");
	            childSetNameMap.put("fieldName","-1");
	            historyPartIndicatorList.add(childSetNameMap);
			}
			hm.put("data",historyPartIndicatorList);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * 获取数据范围可以设置条件的指标
	 * @throws GeneralException
	 */
	private void getSetConditionIndicator() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();
			String tabId = (String) hm.get("tabId");
			ContentDAO dao = new ContentDAO(this.frameconn);
			String setConditionIndicatorSql = "select tabid,baseid,Field_name,ColHz  from Lbase where tabId = ? and field_type!='M'";
			ArrayList<String> setConditionIndicatorValues = new ArrayList<String>();
			ArrayList<HashMap<String, String>> setConditionIndicatorList = new ArrayList<HashMap<String, String>>();
			setConditionIndicatorValues.add(tabId);
			this.frowset = dao.search(setConditionIndicatorSql,setConditionIndicatorValues);
			//dataName: "发放次数"
		    //dataValue: "A00Z3"
			while(this.frowset.next()) {
				//HashMap setConditionIndicatorMap = new HashMap();
				//String dataName = this.frowset.getString("ColHz");
				String fieldName = this.frowset.getString("Field_name");
				String fieldsetid = fieldName.split("\\.")[0];
				if (!fieldsetid.endsWith("01")) {// bug 50936 只取子集的指标
					/*String dataValue = fieldName.split("\\.")[1];
					setConditionIndicatorMap.put("dataName",dataName);
					setConditionIndicatorMap.put("dataValue",dataValue);
					setConditionIndicatorList.add(setConditionIndicatorMap);*/
					List<FieldItem> fieldItemList = userView.getPrivFieldList(fieldsetid, 1);
					for (FieldItem fieldItem : fieldItemList) {
					    if ("M".equals(fieldItem.getItemtype())) {
                            continue;
                        }
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("dataName", fieldItem.getItemdesc());
                        map.put("dataValue", fieldItem.getItemid());
                        setConditionIndicatorList.add(map);
                    }
				    break;
				}
				
			}
			hm.put("setConditionIndicatorData",setConditionIndicatorList);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 回显数据
	 * @throws GeneralException
	 */
	private void echoData()  throws GeneralException{
		try {
			String tabId = (String)this.getFormHM().get("tabid");
			String sql="select Nbases,DataRange from lname where tabid=?";
			ArrayList values=new ArrayList();
			values.add(tabId);
			ContentDAO dao=new ContentDAO(this.frameconn);
			this.frowset=dao.search(sql,values);
			String dataRange="";
			String Nbases="";
			Map MultipleConditions=new HashMap(); //多个条件数据
			String oldcondition = "";//数据库原始的条件
			while(this.frowset.next()) {
				dataRange=this.frowset.getString("DataRange");
				Nbases=this.frowset.getString("Nbases");
			}
			
			Map data = new HashMap();
			if(dataRange!=null && !"".equals(dataRange)) {
				JSONObject obj = JSONObject.fromObject(dataRange);
				boolean parttimejobvalue = false;
                if (obj.containsKey("parttimejobvalue")) {
                    parttimejobvalue = obj.getBoolean("parttimejobvalue");
                }
				String filter = obj.getString("filter");
				String condition = obj.getString("condition");
				String range_type = obj.getString("range_type");
				Map mapCondition=new HashMap();
				HashMap<String, ArrayList<String>> map = getMusterFieldInfo(tabId, userView);
				ArrayList<String> fieldsetlist = map.get("fieldset");
				ArrayList<String> fielditemlist = map.get("fielditem");
				//不是两个信息的情况
				if (fieldsetlist.size()!=2&&!"0".equals(range_type)) {
					range_type = "0";
					condition = "";
				}
				//两个信息集都是子集的情况
				if (fieldsetlist.size()==2) {//20191227 解决选择两个指标时数组越界异常问题
					String fieldsetid1 = fieldsetlist.get(0);
					String fieldsetid2 = fieldsetlist.get(1);
					if (!fieldsetid1.endsWith("01")&&!"0".equals(range_type)) {
						range_type = "0";
						condition = "";
					}
				}
				if("1".equals(range_type)) {
					String fieldsetid2 = fieldsetlist.get(1);
					FieldSet fieldSet = DataDictionary.getFieldSetVo(fieldsetid2);
					if (!"1".equals(fieldSet.getChangeflag())&&!"2".equals(fieldSet.getChangeflag())) {
						range_type = "0";
						condition = "";
					}
				    oldcondition = condition;
                    if(StringUtils.isNotEmpty(condition)) {
                        String setName=condition.split(",")[0];
                        String date=condition.split(",")[1];
                        String[] dataArray = date.split("\\|");
                        String dateFrom=dataArray[0];
                        String dateTo=dataArray[1];
                        if(dateFrom.split("-")[1].startsWith("0")) {
                            dateFrom=dateFrom.split("-")[0]+"-"+dateFrom.split("-")[1].substring(1, 2)+"-"+dateFrom.split("-")[2];
                        }
                        if(dateFrom.split("-")[2].startsWith("0")) {
                            dateFrom=dateFrom.split("-")[0]+"-"+dateFrom.split("-")[1]+"-"+dateFrom.split("-")[2].substring(1, 2);
                        }
                        condition=setName+","+dateFrom+"|"+dateTo;
                        if (dataArray.length==3) {
                            condition+="|"+dataArray[2];
                        }
                    }
                }else if("2".equals(range_type)) {
                	String rightData=condition.split("\\|")[1];
                    String[] items=rightData.split("`");
                    boolean result =false;
                    for (int i = 0; i < items.length; i++) {
						String itemid = items[i].substring(0,5);
						FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
						if (fieldItem!=null&& "N".equals(fieldItem.getItemtype())) {
						    if (items[i].equals(itemid+"=null")) {
						        condition= condition.replace(itemid+"=null", itemid+"=");
                            }else if (items[i].equals(itemid+"<>null")) {
                                condition= condition.replace(itemid+"<>null", itemid+"<>");
                            }else if (items[i].equals(itemid+">=null")) {
                                condition= condition.replace(itemid+"<>null", itemid+"<>");
                            }else if (items[i].equals(itemid+">null")) {
                                condition= condition.replace(itemid+"<>null", itemid+"<>");
                            }else if ( items[i].equals(itemid+"<=null")) {
                                condition= condition.replace(itemid+"<>null", itemid+"<>");
                            }else if (items[i].equals(itemid+"<null")) {
                                condition= condition.replace(itemid+"<null", itemid+"<");
                            }
                        }
						 String fieldsetid = fieldItem.getFieldsetid();
                         ArrayList<FieldItem> fieldItemList= userView.getPrivFieldList(fieldsetid);
                         for (FieldItem fieldItem2 : fieldItemList) {
                             if (fieldItem2.getItemid().equalsIgnoreCase(itemid)) {
                                 result = true;
                                 break;
                             }
                         }
                         if (!result) {
                             range_type = "0";
                             condition = "";
                             break;
                         }
					}
                    if (result) {
                        oldcondition = SafeCode.encode(condition);
                        Map rightItem;
                        ArrayList rightDataList=new ArrayList();
                        for(String item : items) {
                            rightItem=new HashMap();
                            rightItem.put("dataValue", item.substring(0, 5));
                            //查询itemid对应的itemdesc
                            FieldItem fieldItem=DataDictionary.getFieldItem(item.substring(0, 5));
                            String dataName=fieldItem.getItemdesc();
                            rightItem.put("dataName", dataName);
                            rightDataList.add(rightItem);
                        }
                        //回显多个条件“下一步”后展示的数据
                        MultipleConditions.put("expr", SafeCode.encode(condition));
                        MultipleConditions.put("rightDataList", rightDataList);
                    }
                }else if("3".equals(range_type)) {
                	String itemid = condition.split(",")[0].split("\\.")[1];
                	if (fielditemlist.contains(itemid)) {
                		oldcondition = condition;
    					FieldItem fielditem = DataDictionary.getFieldItem(itemid);
    					String codeSetId = fielditem.getCodesetid();//代码类id
    					String itemType = fielditem.getItemtype();
    					String itemdesc=fielditem.getItemdesc();
    					String fromAndTO=condition.split(",")[1];
    					String from=fromAndTO.split("\\|")[0];
    					String to=fromAndTO.split("\\|")[1];
    					from=from+"`"+AdminCode.getCodeName(codeSetId, from);
    					to=to+"`"+AdminCode.getCodeName(codeSetId, to);
    					condition = condition+","+itemType+","+codeSetId+","+itemdesc+","+from+","+to;
					}else {
						range_type = "0";
						condition = "";
					}
                    
				}
				
				
				if("null".equals(filter)) {
					filter = "0";
				}
				data.put("oldcondition", oldcondition);
				data.put("parttimejobvalue", parttimejobvalue);
				data.put("nbases", Nbases);
				data.put("filter", filter);
				data.put("condition", condition);
				data.put("encryptCondition", SafeCode.encode(condition));
				data.put("range_type", range_type);
				data.put("MultipleConditions", MultipleConditions);
			}
			String[] nbases;
			if(Nbases!=null && !"".equals(Nbases)) {
				nbases=Nbases.split(",");
				data.put("nbases", nbases);
			}
			this.getFormHM().put("echoData",data);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param tabid
	 * @return
	 */
	private HashMap<String, ArrayList<String>> getMusterFieldInfo(String tabid,UserView userView) {
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		try {
			String sql = "select baseid,Field_name,ColHz from Lbase where tabid = '"+tabid+"'";
		    ContentDAO dao = new ContentDAO(frameconn);
		    this.frecset = dao.search(sql);
		    Set<String> set = new HashSet<String>();
		    ArrayList<String> itemlist = new ArrayList<String>();
		    while(this.frecset.next()) {
		    	String fieldname = this.frecset.getString("field_name");
		    	String fieldsetid = fieldname.split("\\.")[0];
		    	String itemid = fieldname.split("\\.")[1];
		    	ArrayList<FieldItem> list = userView.getPrivFieldList(fieldsetid,1);
		    	boolean flag = false ;
		    	if ("K01.E01A1".equalsIgnoreCase(fieldname)) {
					flag = true;
				}
		    	if (!flag) {
		    		for (FieldItem fieldItem : list) {
						String fieldid = fieldItem.getItemid();
						if (fieldid.equalsIgnoreCase(itemid)) {
							flag  = true;
							break;
						}
					}
				}
		        if (flag) {
		        	set.add(fieldsetid);
			    	itemlist.add(itemid);
				}
		    }
		    ArrayList<String> fieldsetlist = new ArrayList<String>();
		    fieldsetlist.addAll(set);
		    Collections.sort(fieldsetlist);
		    map.put("fieldset", fieldsetlist);
		    map.put("fielditem", itemlist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
