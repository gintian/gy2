package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.Pageable;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.command.BusinessListView;
import com.hrms.struts.command.WFMapping;
import com.hrms.struts.command.WfunctionView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;

public class GetTableDataTrans extends IBusiness {


	public void execute() throws GeneralException {

		String subModuleId = (String)this.formHM.get("subModuleId");
		String pageStr = (String)this.formHM.get("page");
		if(pageStr==null)
			pageStr="1";
		int page = Integer.parseInt(pageStr);
			page = page<1?1:page;//xiegh 20170327 处理页数为负的情况
		int limit = Integer.parseInt((String)this.formHM.get("limit"));
		
		TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
		List  dataList = new ArrayList();
		String tableSql = new String();
		String sortSql = new String();
		String[] fields = tableCache.getDataFields();
		HashMap columnMap = tableCache.getColumnMap();
		int totalCount = 0;
		String sort = (String)this.formHM.get("sort");
		if(tableCache.getTableData()!=null){
			ArrayList allData = tableCache.getTableData();
			sortflag:if(sort!=null){
				sort = PubFunc.hireKeyWord_filter_reback(sort);
				JSONArray sortArray = JSONArray.fromObject(sort);
				JSONObject sortObj = sortArray.getJSONObject(0);
				String sortColumnId  = sortObj.get("property").toString();
				String sortDirection = sortObj.get("direction").toString();
				HashMap customParamHM = tableCache.getCustomParamHM()==null?new HashMap():tableCache.getCustomParamHM();
				String property = customParamHM.get("property")==null?"":(String)customParamHM.get("property");
				String direction = customParamHM.get("direction")==null?"":(String)customParamHM.get("direction");
				if(!columnMap.containsKey(sortColumnId))
					break sortflag;
				if(property.equalsIgnoreCase(sortColumnId)&&direction.equalsIgnoreCase(sortDirection))
					break sortflag;
				ColumnsInfo sortColumn = (ColumnsInfo)columnMap.get(sortColumnId);
				String disformat = sortColumn.getDecimalWidth()+"";
				if("D".equalsIgnoreCase(sortColumn.getColumnType())){
	      	        if(sortColumn.getColumnLength()==4)
	      	        	disformat = "yyyy";
	      	        else if(sortColumn.getColumnLength()==7)
	      	        	disformat = "yyyy-MM";
	      	        else if(sortColumn.getColumnLength()==10)
	      	        	disformat = "yyyy-MM-dd";
	      	        else if(sortColumn.getColumnLength()==16)
	      	        	disformat = "yyyy-MM-dd HH:mm";
	      	        else if(sortColumn.getColumnLength()==18)
	      	        	disformat = "yyyy-MM-dd HH:mm:ss";
	      	        else
	      	        	disformat = "yyyy-MM-dd";
				}
				allData = PubFunc.sortList(sortColumnId, sortDirection, sortColumn.getColumnType(), sortColumn.getCodesetId(), disformat, allData);
				customParamHM.put("property", sortColumnId);
				customParamHM.put("direction", sortDirection);
				tableCache.setCustomParamHM(customParamHM);
			}
			if(allData == null){
				totalCount = 0;
			}else{
		        totalCount = allData.size();
		        dataList = allData.subList(limit*(page-1), (limit*page>totalCount?totalCount:limit*page));
			}
		}else{
			HashMap customParamHM = tableCache.getCustomParamHM()==null?new HashMap():tableCache.getCustomParamHM();
			tableSql = tableCache.getTableSql();
			tableSql = "select * from ("+tableSql+") myGridData where 1=1 ";
			if(tableCache.getQuerySql()!=null)
				tableSql = tableSql+tableCache.getQuerySql();
			String filterParam = (String)this.formHM.get("filterParam");
			if(filterParam!=null){
				tableSql = createFilterSql(tableSql,filterParam,tableCache);
			}else {
				tableCache.setFilterSql("");
			}
			//保存 完整的查询sql，包含通过快速过滤和方案查询生成的查询条件
			tableCache.put("combineSql", tableSql);
			sortSql = tableCache.getSortSql();
			sortSql = sortSql==null?"":sortSql;
			sortflag:if(sort!=null){
				sort = PubFunc.hireKeyWord_filter_reback(sort);
				JSONArray sortArray = JSONArray.fromObject(sort);
				JSONObject sortObj = sortArray.getJSONObject(0);
				
				String sortColumnId  = sortObj.get("property").toString();
				String sortDirection = sortObj.get("direction").toString();
				if(!columnMap.containsKey(sortColumnId))
					break sortflag;
				
				ColumnsInfo sortColumn = (ColumnsInfo)columnMap.get(sortColumnId);
				
				//如果显示排序类型 按照所选排序类型排序
				if(sortColumn.isShowSortType()){
					sortSql = " order by ";
					String sortType = this.formHM.get("sortType").toString();
					//排序类型：py=拼音；bh=笔画
					if("py".equals(sortType))
						sortSql+=Sql_switcher.sortByPinYin(sortColumnId)+sortDirection;
					else
						sortSql+=Sql_switcher.sortByStroke(sortColumnId)+sortDirection;
					
					tableCache.setSortSql(sortSql);
					break sortflag;
				}
				
				//数值型排序
				if("N".equals(sortColumn.getColumnType())){
					sortSql = "order by "+Sql_switcher.isnull(sortColumnId, "0")+" "+sortDirection;
					tableCache.setSortSql(sortSql);
					break sortflag;
				}
				
				//其他类型排序
				sortSql = "order by "+sortColumnId+" "+sortDirection;
				customParamHM.put("property", sortColumnId);
				customParamHM.put("direction", sortDirection);
				tableCache.setCustomParamHM(customParamHM);
				tableCache.setSortSql(sortSql);
			}else {
				if(customParamHM.containsKey("property"))
					customParamHM.put("property", "");
				if(customParamHM.containsKey("direction"))
					customParamHM.put("direction", "");
				tableCache.setCustomParamHM(customParamHM);
			}
			
			
			String indexkey = tableCache.getIndexkey();
			Connection conn = null;
			RowSet rs = null;
			try{
				doBefore(tableCache);
				
				PaginationManager paginationm =null;
		        paginationm=new PaginationManager(tableSql,"","",sortSql,fields,"");
		        paginationm.setBAllMemo(true);
		        paginationm.setPagerows(limit);
		        paginationm.setKeylist(splitKeys(indexkey));
		        totalCount = paginationm.getMaxrows();
		        dataList=(ArrayList)paginationm.getPage(page);
		        if(dataList.isEmpty() && page!=1){
		        	dataList = (ArrayList)paginationm.getPage(page-1);
		        }
				
		        //更新form中的pageable分页信息  xuj add 2015-1-27
		        Pageable pageable = tableCache.getPageable();
		        if(pageable!=null){
			        pageable.setPageNumber(page);
			        pageable.setTotal(totalCount);
			        pageable.setPageSize(limit);
		        }
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(conn);
				PubFunc.closeDbObj(rs);
			}
			
			
		}
		
		try{
			
			this.formHM.put("totalCount", totalCount);
			//部门显示几级层级
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		    String uplevelStr = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
		    if(uplevelStr==null||uplevelStr.length()==0)
		    	uplevelStr="0";
		    int upLevel = Integer.parseInt(uplevelStr);
			//统计sql
			StringBuffer sumSql = new StringBuffer("select 1 as extra ");
			HashMap sumColumn = new HashMap();
			ArrayList dataObjList = new ArrayList();
			ColumnsInfo ci = null;
			Iterator ite = null;
			String keyValue;
			for(int i=0;i<dataList.size();i++){
				LazyDynaBean record = (LazyDynaBean)dataList.get(i);
				HashMap recordObj = new HashMap();
				HashMap cusData = new HashMap();
				ite = columnMap.values().iterator();
			    while(ite.hasNext()){
				    	ci = (ColumnsInfo)ite.next();
				    	if(ci.getLoadtype() == ColumnsInfo.LOADTYPE_NOTLOAD)
				    		continue;
				    	String itemid = ci.getColumnId();
				    	
				    	if(!record.getMap().containsKey(itemid)){
				    		recordObj.put(itemid, "");
				    		continue;
				    	}
				    	
				    	if(ci.isEncrypted()){
				    		String value = (String)record.get(ci.getColumnId());
				    		String value_e = PubFunc.encrypt(value);
				    		if(ci.isEncrypted_64base()) {
				    			value_e = PubFunc.encrypt(SafeCode.convertTo64Base(value));
				    		}
				    		recordObj.put(itemid+"_e", value_e);
				    		continue;
				    	}
				    	
				    	if(ci.getValueTranslator()!=null){
				    		    String[] translator = ci.getValueTranslator().split(":");
				    		    try{
				    		    	Class clas = Class.forName(translator[0]);
					    		    Object clasIns = clas.newInstance();
					    		    Method method = clas.getDeclaredMethod(translator[1],Connection.class,UserView.class,LazyDynaBean.class,String.class);
					    		    Object newValue = method.invoke(clasIns,this.frameconn,this.userView,record,itemid);
					    		    cusData.put(itemid, record.get(itemid));
					    		    recordObj.put(itemid, newValue);
				    		    }catch(Exception e){
				    		    	e.printStackTrace();
				    		    }
				    		    continue;
				    	}	   
				    	
				    	//统计方式
			    	    if("N".equals(ci.getColumnType()) && ci.getSummaryType()!=0 && !sumColumn.containsKey(ci.getColumnId())){
			    			switch (ci.getSummaryType()) {
			    			case 1:
			    				sumSql.append( ",SUM("+(Sql_switcher.searchDbServer()==Constant.MSSQL?"convert(float,"+ci.getColumnId()+")":ci.getColumnId())+") "+ci.getColumnId());
			    				break;
			    			case 2:
			    				sumSql.append( ",AVG("+ci.getColumnId()+") "+ci.getColumnId());
			    				break;
			    			case 4:
			    				sumSql.append( ",MAX("+ci.getColumnId()+") "+ci.getColumnId());
			    				break;
			    			case 3:
			    				sumSql.append( ",MIN("+ci.getColumnId()+") "+ci.getColumnId());
			    				break;
			    			}
			    			sumColumn.put(ci.getColumnId().toLowerCase(), ci.getDecimalWidth());
			    	    }
			    	    if(ci.getOperationData()!=null){
			    	    		recordObj.put(itemid,record.get(itemid));
			    	    }else if("A".equals(ci.getColumnType()) && ci.getCodesetId()!=null && ci.getCodesetId().trim().length()>0&& !"0".equals(ci.getCodesetId())){
				    		if(record.get(itemid)==null || record.get(itemid).toString().length()<1){
				    			recordObj.put(itemid,"");
				    			continue;
				    		}
				    		//如果是部门，按设置显示部门层级
				    		ifUM:if("UM".equals(ci.getCodesetId()) && upLevel>0){
				    			CodeItem code = AdminCode.getCode(ci.getCodesetId(),(String)record.get(itemid), upLevel);
				    			if(code==null)
				    				break ifUM;
				    			recordObj.put(itemid,(String)record.get(itemid)+"`"+code.getCodename());
				    			continue;
				    		}
						    String name = AdminCode.getCodeName(ci.getCodesetId(),(String)record.get(itemid));
						    //兼容处理，单位没找到，找部门
						    if("UN".equals(ci.getCodesetId()) && name.length()<1){
						    		name = AdminCode.getCodeName("UM",(String)record.get(itemid));
						    }
						    //兼容处理，部门没找到，找单位
						    if("UM".equals(ci.getCodesetId()) && name.length()<1)
						    	   name = AdminCode.getCodeName("UN",(String)record.get(itemid));
						    if(name.length()<1){
						    		if(ci.getCodeRealValue()) {
						    			recordObj.put(itemid,record.get(itemid)+"`"+record.get(itemid));
						    		}else {
						    			recordObj.put(itemid,"");
						    		}
							    	continue;
						    }
						    recordObj.put(itemid, record.get(itemid)+"`"+name);
				    }else if("N".equals(ci.getColumnType()) && record.get(itemid)!=null){
				    	    String value = record.get(itemid).toString();
						if(value.length()<1){
							value = ci.getDefaultValue();
							if(ci.getDefaultValue()!=null && ci.getDefaultValue().length()>0)
								value = ci.getDefaultValue();
							else{
								continue;
							}
						}
					    	if(ci.getDecimalWidth()>0){
					    		recordObj.put(itemid, new Double(value));
					    	}else{
					    	    //String value =record.get(itemid).toString();
					    	    if (value.contains(".")){//兼容数据库中为float类型的情况 取出值为0.00;
					    	        int k1= value.indexOf(".");
					    	        value=value.substring(0,k1);
					    	    }
					    		recordObj.put(itemid, new Integer(value));
					    	}
				    }else if("D".equals(ci.getColumnType()) && record.get(itemid)!=null && record.get(itemid).toString().length()>0){
				    	    String datevalue = record.get(itemid).toString().replace(".", "-");
					    	if(ci.getColumnLength()>0 && ci.getColumnLength()<17 && datevalue.length()>ci.getColumnLength())//{
					    		datevalue = datevalue.substring(0,ci.getColumnLength());
						recordObj.put(itemid, datevalue);
				    }else if("M".equals(ci.getColumnType())){
				    	   String value = record.get(itemid)==null?"":record.get(itemid).toString();
				    	   if(ci.getInputType()==0)
				    		   value = PubFunc.reverseHtml(value);
				    	    recordObj.put(itemid,value);
				    }else{
				    		recordObj.put(itemid,record.get(itemid));
				    }
			    }
			    recordObj.put("MVP_Data_Key", cusData);
			    dataObjList.add(recordObj);
			}
			this.formHM.put("dataobjs", dataObjList);
			
			if(sumColumn.size()>0 && tableSql.length()>0){
				HashMap summaryData = new HashMap();
				sumSql.append(" from (");
				sumSql.append(tableSql);
				sumSql.append(") gridSummary ");
				Connection conn = null;
				RowSet rs = null;
				String zero = "0000000000000000";
				try{
					conn = AdminDb.getConnection();
					ContentDAO dao = new ContentDAO(conn);
					rs = dao.search(sumSql.toString());
					rs.next();
					for(int i=2;i<=rs.getMetaData().getColumnCount();i++){
						String columnName = rs.getMetaData().getColumnName(i);
						Object value = rs.getObject(columnName);
						int dLen = (Integer)sumColumn.get(columnName.toLowerCase());
						
					    value = value==null?0:value;
					    String format = "0";
					    if(dLen>0){
					    	    format+="."+zero.substring(0, dLen);
					    }
					    DecimalFormat myformat=new java.text.DecimalFormat(format);
					    String va = myformat.format(value);
						summaryData.put(columnName.toLowerCase(), va);
					}
					
					this.formHM.put("summaryData", summaryData);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					PubFunc.closeDbObj(conn);
					PubFunc.closeDbObj(rs);
				}
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	/**
	 * 表格加载前走的交易类的方法
	 * @param tableCache
	 * @throws GeneralException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private void doBefore(TableDataConfigCache tableCache) throws GeneralException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String functionid = tableCache.getBeforeLoadFunctionId();
		//如果交易类号为空，跳出
		if(functionid==null || functionid.length()<1)
			return;
		//如果交易类号和当前执行交易类号相同，跳出。否则会死循环
		if("ZJ100000000".equalsIgnoreCase(functionid.trim()))
			return;
		HashMap newForm = (HashMap)this.formHM.clone();
		newForm.remove("summaryData");
		newForm.remove("dataobjs");
		newForm.remove("totalCount");
		WFMapping wfm = new WFMapping();
		WfunctionView wfView=wfm.getFunctionView(functionid);
		HashMap seqMap = wfView.getBusiHash();
		for (int i = 0; i < seqMap.size(); i++){
		     
	        BusinessListView busiview = (BusinessListView) seqMap.get(Integer.toString(i));
	        if (!"".equals(busiview.getMainClass()))
	        {
	          IBusiness trans = (IBusiness) Class.forName(busiview.getMainClass().trim()).newInstance();
	          trans.setFormHM(newForm);
	          trans.setUserView(this.getUserView());
	          trans.setFrameconn(this.getFrameconn());
	          trans.setFstmt(this.getFstmt());
	          trans.setFrowset(this.getFrowset());
	          trans.setFrecset(this.getFrecset());
	          trans.execute();
	        } 
	    }	
		newForm = null;
	}
	
	/**
	 * 组件 指标过滤sql语句
	 * @param tableSql 源数据sql
	 * @param filterParam  过滤参数
	 * @return
	 */
	private String createFilterSql(String tableSql,String filterParam,TableDataConfigCache tableCache){
		StringBuffer filterSql = new StringBuffer();
		filterSql.append(tableSql);
		JSONObject json = JSONObject.fromObject(SafeCode.keyWord_reback(filterParam));
		String itemid = json.getString("field");
		String itemtype = json.getString("itemtype");
		JSONArray factor = json.getJSONArray("factor");
		String expr = json.getString("expr");
		//如果为空或者没有数据，返回 原sql
		if(factor==null || factor.isEmpty()){
			tableCache.setFilterSql("");//add by xiegh date20180309 bug35327  bug中描述的不对  原因：当过滤条件取消时，缓存中的sql没有清除，导致导出Excel不对
			return tableSql;
		}
		
		StringBuffer filterWhere = new StringBuffer(" and (");
	    String symbol;
	    String value = "";
    	    filterIf:if("C".equals(itemtype)){// C代码型指标
    	    	    expr = "or";
	    	    	 for(int i=0;i<factor.size();i++){
	    		    	    String f = factor.getString(i);
	    		    	    value = f.substring(f.indexOf("`")+1);
	    		    	    filterWhere.append("UPPER("+itemid+") like '"+value.toUpperCase()+"%' or ");
	    	    	 }
    		}else if("D".equals(itemtype)){//时间类型
    			String plan = json.getString("plan");
    			
    			if("custom".equals(plan)){
    				for(int i=0;i<factor.size();i++){
    					
    					String f = factor.getString(i);
		    	        symbol = f.substring(0,f.indexOf("`"));
		    	        value = f.substring(f.indexOf("`")+1);
		    	        
		    	        String format = "YYYY-MM-DD HH24:mi:ss";
		    	        if(value.length()==4){
		    	        		format = "YYYY";
		    	        }else if(value.length()==7)
		    	        		format = "YYYY-MM";
		    	        else if(value.length()==10)
		    	        		format = "YYYY-MM-DD";
		    	        else if(value.length()==16){
		    	        		//当日期没有秒时，补位
		    	        		value+=":00";
		    	        }
		    	        
		    	        filterWhere.append(Sql_switcher.dateToChar(itemid, format)).append(symbol).append(" '").append(value).append("' ");
		    	        filterWhere.append(expr).append(" ");
    				}
    				
    				break filterIf;
    			}
    			
    			String f = factor.getString(0);
   	        symbol = f.substring(0,f.indexOf("`"));
   	        value = f.substring(f.indexOf("`")+1);
    			Calendar c = Calendar.getInstance();
       	     
   	         if("nextMonth".equals(symbol)){
   	        	 	filterWhere.append(Sql_switcher.month(itemid)+"="+(c.get(Calendar.MONTH)+2)+" "+expr+" ");
   	         }else if("thisMonth".equals(symbol)){
	        	 	filterWhere.append(Sql_switcher.month(itemid)+"="+(c.get(Calendar.MONTH)+1)+" "+expr+" ");
   	         }else if("lastMonth".equals(symbol)){
   	        	 	filterWhere.append(Sql_switcher.month(itemid)+"="+c.get(Calendar.MONTH)+" "+expr+" ");
   	         }else if("nextYear".equals(symbol)){
   	        	 	filterWhere.append(Sql_switcher.year(itemid)+"="+(c.get(Calendar.YEAR)+1)+" "+expr+" ");
   	         }else if("thisYear".equals(symbol)){
   	        	 	filterWhere.append(Sql_switcher.year(itemid)+"="+c.get(Calendar.YEAR)+" "+expr+" ");
   	         }else if("lastYear".equals(symbol)){
   	        	 	filterWhere.append(Sql_switcher.year(itemid)+"="+(c.get(Calendar.YEAR)-1)+" "+expr+" ");
   	         }else{
   	        	     int nextYear = -1;
   	        	     int lastYear = -1;
   	        	     String nextSeason = "";
	    	         String thisSeason = "";
	    	         String lastSeason = "";
	    	         if(c.get(Calendar.MONTH)<3){
	    	        	 	thisSeason = "1,2,3";
	    	        	 	lastYear = c.get(Calendar.YEAR)-1;
	    	         }else if(c.get(Calendar.MONTH)<6){
	    	        	    nextSeason = "7,8,9";
	    	        	 	thisSeason = "4,5,6";
	    	        	 	lastSeason = "1,2,3";
	    	         }else if(c.get(Calendar.MONTH)<9){
	    	        	 	nextSeason = "10,11,12";
	    	        	 	thisSeason = "7,8,9";
	    	        	 	lastSeason = "4,5,6";
	    	         }else{
	    	        	 	thisSeason = "10,11,12";
	    	        	 	nextYear = c.get(Calendar.YEAR)+1;
	    	        	 }
   	        	 	 if("nextSeason".equals(symbol)){
   	        	 		 if(nextYear>0)
   	        	 			 filterWhere.append(Sql_switcher.month(itemid)+" in (1,2,3) and "+Sql_switcher.year(itemid)+"="+nextYear+" "+expr+" ");
   	        	 		 else
   	        	 			 filterWhere.append(Sql_switcher.month(itemid)+" in ("+nextSeason+") "+expr+" ");
	    	         }else if("thisSeason".equals(symbol)){
	    	        	 	 filterWhere.append(Sql_switcher.month(itemid)+" in ("+thisSeason+") "+expr+" ");
	    	         }else if("lastSeason".equals(symbol)){
	    	        	 	 if(lastYear>0)
	    	        	 		 filterWhere.append(Sql_switcher.month(itemid)+" in (10,11,12) and "+Sql_switcher.year(itemid)+"="+lastYear+" "+expr+" ");
   	        	 		 else
   	        	 			 filterWhere.append(Sql_switcher.month(itemid)+" in ("+lastSeason+") "+expr+" ");
	    	         }
   	         }
    		}else if("N".equals(itemtype)){//int型
	    	    for(int i=0;i<factor.size();i++){
	    	    		String f = factor.getString(i);
	    	        symbol = f.substring(0,f.indexOf("`"));
	    	        value = f.substring(f.indexOf("`")+1);
	    		    filterWhere.append(Sql_switcher.isnull(itemid, "0")+symbol+value+" "+expr+" ");
	    	    	}
    		}else{//M(文本)型和A(字符)型
    			for(int i=0;i<factor.size();i++){
    				 String f = factor.getString(i);
	    	         symbol = f.substring(0,f.indexOf("`"));
	    	         try {
						value = URLDecoder.decode(f.substring(f.indexOf("`")+1), "UTF-8");
						value = PubFunc.hireKeyWord_filter(value);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					};
    				if("sta".equals(symbol)){//开头是
    					filterWhere.append(itemid+" like '"+value+"%' "+expr+" ");
    				}else if("stano".equals(symbol)){//开头不是
    					filterWhere.append(itemid+" not like '"+value+"%' "+expr+" ");
    				}else if("end".equals(symbol)){//结尾是
    					filterWhere.append(itemid+" like '%"+value+"' "+expr+" ");
    		    		}else if("endno".equals(symbol)){//结尾不是
    		    			filterWhere.append(itemid+" not like '%"+value+"' "+expr+" ");
    		    		}else if("cont".equals(symbol)){//包含
    		    			filterWhere.append(itemid+" like '%"+value+"%' "+expr+" ");
    		    		}else if("contno".equals(symbol)){//不包含
    		    			filterWhere.append(itemid+" not like '%"+value+"%' "+expr+" ");
    		    		}else{
    		    			if("=".equals(symbol) &&  value.indexOf("？")+value.indexOf("＊")>-2){
    		    				symbol = " like ";
    		    				value = value.replaceAll("？", "?");
    		    				value = value.replaceAll("＊", "%");
    		    			}
    		    			if(value.length()==0 && "=".equals(symbol) ){
    		    				filterWhere.append(" ("+Sql_switcher.sqlToChar(itemid)+symbol+" '' or "+Sql_switcher.sqlToChar(itemid)+" is null ) "+expr+" ");
    		    			}else
    		    				filterWhere.append(Sql_switcher.sqlToChar(itemid)+symbol+" '"+value+"' "+expr+" ");
    		    		}
    			}
    		}
	    	if("or".equals(expr))
	    		filterWhere.append(" 1=2 ");
	    	else
	    		filterWhere.append(" 1=1 ");
	    filterWhere.append(" )");
	    tableCache.setFilterSql(filterWhere.toString());
	    filterSql.append(filterWhere);
		return filterSql.toString();
	}
	
	private ArrayList splitKeys(String indexkey)
    {
    	if(indexkey==null|| "".equals(indexkey))
    		return null;
        ArrayList list=new ArrayList();
        String temp=indexkey.toLowerCase();
        StringTokenizer st = new StringTokenizer(temp, ",");
        while (st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }   
        return list;
    }
}
