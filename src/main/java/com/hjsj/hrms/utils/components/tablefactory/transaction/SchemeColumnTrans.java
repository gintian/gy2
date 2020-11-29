package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class SchemeColumnTrans extends IBusiness {


	public void execute() throws GeneralException {
		String subModuleId = (String)this.formHM.get("subModuleId");
		String actionName = (String)this.formHM.get("actionName");
	    Connection conn = null;
	    try{
	    	conn = AdminDb.getConnection();
	    	TableFactoryBO tfb = new TableFactoryBO(subModuleId,userView,conn);
	    	TableDataConfigCache cache = (TableDataConfigCache)this.userView.getHm().get(subModuleId);
	    	
	    	//更新列参数
	    	if("update".equals(actionName)){
	    		
	    		int scheme_id = tfb.getPersonalPlanSchemeID();
	    		//更新操作只针对私有方案，如果没有则创建
	    		if(scheme_id == -1){
	    			TableDataConfigCache tableCache = null;
		    	    	if(userView.getHm().get(subModuleId) != null)
		    	    		tableCache = (TableDataConfigCache)userView.getHm().get(subModuleId);
		    	    LazyDynaBean config  = new LazyDynaBean();
	    			
	    			//走到这里说明没有保存任何方案，获取初始默认方案
		    	    ArrayList columnConfigs = cache.getDefaultSchemeColumnList();
	        		//cache 中的defaultSchemeColumnList参数是后加的，以前取的是tableColumns。为防止程序报错，此处兼容一下 guodd 2018-04-14
	        		if(columnConfigs == null || columnConfigs.size()==0){
	        			columnConfigs = tfb.createSchemeConfigsByColumnList(cache.getTableColumns());
	        		}
	    			config.set("columnsConfigs", columnConfigs);
	    			JSONObject configObj = JSONObject.fromObject(config);
	    			LazyDynaBean plan = (LazyDynaBean)JSONObject.toBean(configObj, LazyDynaBean.class);
	    			plan.set("pageRows",tableCache.getPageSize());
	    			plan.set("schemeId",-1);
	    			scheme_id = tfb.saveTableConfig(plan,0);
	    			
	    			cache.setColumnDisplayConfig((ArrayList)plan.get("columnsConfigs"));
	    		}
	    		
	    		String updataType = (String)this.formHM.get("updateType");
	    		String itemid     = (String)this.formHM.get("itemid");
	    		if("lockUpdate".equals(updataType)){
	    			String lockstate = (String)this.formHM.get("lockstate");//lock & unlock
	    			tfb.updateColumnLocked(scheme_id, itemid, lockstate);
	    		}
	    		else if("widthUpdate".equals(updataType)){
	    			int  width = (Integer)this.formHM.get("width");
	    			tfb.updateColumnWidth(scheme_id, itemid, width);
	    		}
	    		else if("positionUptate".equals(updataType)){
	    			String nextid = (String)this.formHM.get("nextid");
	    			String is_lock = (String)this.formHM.get("is_lock");
	    			tfb.updateColumnMove(scheme_id, itemid, nextid,is_lock);
	    		}
	    		
	    		return;
	    	}
	    	
	    	//保存表格参数设置
	    	if("save".equals(actionName)){
	    		DynaBean perPlan = (DynaBean)this.formHM.get("personalPlan");
	    		DynaBean pubPlan = (DynaBean)this.formHM.get("publicPlan");
	    		
	    	    if(perPlan!=null){
			    tfb.saveTableConfig(perPlan, 0);
			    cache.setColumnDisplayConfig((ArrayList)perPlan.get("columnsConfigs"));
	    	    }else if(pubPlan!=null){
	    	    		tfb.saveTableConfig(pubPlan, 0);
				cache.setColumnDisplayConfig((ArrayList)pubPlan.get("columnsConfigs"));
	    	    }   
			    
			
			if(pubPlan!=null){
				HashMap config = tfb.searchTableConfig(1);
			    int schemeId = -1;
				if(config!=null)
					schemeId = (Integer)config.get("schemeId");
				pubPlan.set("schemeId", schemeId);
				tfb.saveTableConfig(pubPlan,1);
			}
			    
			    return;
		}
	    	
	    	//获取 表格 设置参数
	    	if("queryScheme".equals(actionName)){
	    		
	    		int isShare = (Integer)this.formHM.get("isShare");
	    		boolean doCompare = false;
	        	HashMap config = null;
	        	ArrayList columnsConfigs = null;
	        	boolean isPub = false;
	        	//如果请求私有方案
	        	if(isShare==0){
	        		config = tfb.searchTableConfig(0);
	        		doCompare = true;
	        	}
	        	//如果 config 为空，请求公共方案
	        	if(config==null){
	        		config = tfb.searchTableConfig(1);
	        		isPub = true;
	        	}
	        	
	        	//方案数据不为空，加载具体列数据
	        	if(config!=null){
	        		columnsConfigs = getColumnsConfigs((Integer)config.get("schemeId"),conn,cache,tfb,doCompare);
	        		//如果请求的是私有方案，因私有方案不存在 而加载的公有方案数据，需要将公有方案的id置为-1
	        		if(isShare==0 && isPub)
	        			config.put("schemeId", -1);
	        	}else{
	        		
	        		config = new HashMap();
	        		config.put("schemeId", -1);
	        		config.put("pageRows", cache.getPageSize());
	        		//走到这里说明没有保存任何方案，获取初始默认方案
	        		columnsConfigs = cache.getDefaultSchemeColumnList();
	        		//cache 中的defaultSchemeColumnList参数是后加的，以前取的是tableColumns。为防止程序报错，此处兼容一下 guodd 2018-04-14
	        		if(columnsConfigs == null || columnsConfigs.size()==0){
	        			columnsConfigs = tfb.createSchemeConfigsByColumnList(cache.getTableColumns());
	        		}
	        	}
	    		
	    		//权限field
	    		String schemePriv = cache.getSchemePrivFields();
	    		if(schemePriv!=null && schemePriv.length()>1){
	    			schemePriv = schemePriv.toLowerCase();
	    			schemePriv = ","+schemePriv+",";
	    			ArrayList newColumns = new ArrayList();
	    			for (int i = 0; i < columnsConfigs.size(); i++) {
		    			ColumnConfig  columsConfig =(ColumnConfig)columnsConfigs.get(i);
		    			String columnId=columsConfig.getItemid();//获取列id
		    			if(schemePriv!=null && schemePriv.indexOf(","+columnId+",")==-1)
		    				continue;
		    			newColumns.add(columsConfig);
				}
	    			columnsConfigs = newColumns;
	    		}
	    		
	    		
	    		
	    		JSONArray configObj = JSONArray.fromObject(columnsConfigs);
	    		config.put("columnsConfigs", configObj);
	    		
    			
	    		this.formHM.putAll(config);
	    		this.formHM.put("schemeData", config);
	    		
	    		JSONObject obj = JSONObject.fromObject(config);
    			LazyDynaBean plan = (LazyDynaBean)JSONObject.toBean(obj, LazyDynaBean.class);
    			cache.setColumnDisplayConfig((ArrayList)plan.get("columnsConfigs"));
	    	}
	    	
    		
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	PubFunc.closeResource(conn);
	    }

	}
	
	// 获取 列 设置
	private ArrayList getColumnsConfigs(int schemeId,Connection conn,TableDataConfigCache tableCache,TableFactoryBO tfb,boolean doCompare){
		
		ArrayList columnsConfigs = null;
		if(tableCache==null)
			return new ArrayList();
		
		HashMap columnMap = tableCache.getColumnMap();
		columnsConfigs = tfb.getSchemeColumnConfig(schemeId,columnMap,doCompare);
		
		return columnsConfigs;
	}

}
