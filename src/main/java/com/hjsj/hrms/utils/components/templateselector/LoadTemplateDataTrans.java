package com.hjsj.hrms.utils.components.templateselector;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LoadTemplateDataTrans extends IBusiness {
	private boolean multiSelect=false;
	public void execute() throws GeneralException {
		int dataType = (Integer)this.formHM.get("dataType");
	    String nodeId = (String)this.formHM.get("node");
	    String staticId = (String) this.formHM.get("Static");
	    String filterStatic = (String)this.formHM.get("filterStatic");
	    //登记表类型
	    String rnameFlag = (String)this.formHM.get("rnameFlag");
	    rnameFlag = rnameFlag==null||rnameFlag.trim().length()==0?"A":rnameFlag;
	    if("true".equals(this.formHM.get("multiSelect").toString())) {
	    	multiSelect=true;
	    }
	    if(dataType==1){
	    	loadTemplateData(nodeId,staticId,filterStatic);
	    }
	    else{
	    	loadYKCardData(nodeId,rnameFlag);
	    }
		
	}
	   
	private void loadTemplateData(String nodeId,String staticId,String filterStatic){
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{
			HashMap item = null;
			ArrayList itemList = new ArrayList();
			//第一次从根节点进来
			if("root".equals(nodeId)){
				/*支持显示一个或多个模板分类  wangb 20180807 */
				String templateType = (String) this.formHM.get("templateType");
				StringBuffer sql = new StringBuffer();
				ArrayList list = new ArrayList();
				String staitic_="static";
	            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
	            	staitic_="static_o";
	            }
				sql.append("select OperationCode,OperationName from Operation  where 1=1 ");
				if(staticId !=null && staticId.trim().length()>0){
					sql.append(" and "+staitic_+" in(");
					String[] staticIds = staticId.split("`");
					for(int i = 0 ; i < staticIds.length ; i++){
						sql.append("?,");
						list.add(staticIds[i]);
					}
					sql.setLength(sql.length()-1);
					sql.append(") "); 
				}
				if(filterStatic !=null && filterStatic.trim().length()>0){
					sql.append(" and "+staitic_+" not in(");
					String[] filterStatics = filterStatic.split("`");
					for(int i = 0 ; i < filterStatics.length ; i++){
						sql.append("?,");
						list.add(filterStatics[i]);
					}
					sql.setLength(sql.length()-1);
					sql.append(") "); 
				}
				if((staticId == null || staticId.trim().length()==0)&&(filterStatic == null || filterStatic.trim().length()==0)){
					sql.append(" and "+staitic_+"=? ");
					list.add("1");
				}
				
				sql.append(" and " + Sql_switcher.datalength("OperationCode") + "=?");
				list.add(2);
				
				if(templateType != null && templateType.length()>0){
					String[] types = templateType.split(",");
					sql.append(" and OperationCode in (");
					
					for(int i =0 ; i < types.length ; i++){
						sql.append("?,");
						list.add(types[i]);
					}
					sql.deleteCharAt(sql.length()-1);
					sql.append(")");
				}
				
				//查询人事异动下的所有一级节点
				this.frowset=dao.search(sql.toString(),list);
				while(frowset.next()){
					item = new HashMap();
					item.put("id", frowset.getString("OperationCode"));
					item.put("text", frowset.getString("OperationName"));//只取人员分配业务模版
					itemList.add(item);
				}
			}else if(nodeId.length()==2){
				
				String childTemplateType = (String) this.formHM.get("childTemplateType");
				
				StringBuffer sql = new StringBuffer();
				sql.append("select OperationCode,OperationName from Operation where OperationCode like ? and "+Sql_switcher.datalength("OperationCode")+"=4 ");
				
				ArrayList values = new ArrayList();
				values.add(nodeId+"%");
				
				if(childTemplateType !=null && childTemplateType.length()>0){
					String[] childTypes = childTemplateType.split(",");
					String valuesstr ="";
					for(int i =0 ; i < childTypes.length ; i++){
						valuesstr+="?,";
						values.add(childTypes[i]);
					}
					valuesstr = valuesstr.substring(0, valuesstr.length()-1);
					sql.append("and Operationtype in ("+valuesstr+")");
				}else {
					sql.append("and Operationtype<>0 ");
				}
				
				this.frowset = dao.search(sql.toString(),values);
				while(frowset.next()){
					item = new HashMap();
					item.put("id", frowset.getString("OperationCode"));
					item.put("text", frowset.getString("OperationName"));//只取人员分配业务模版
					itemList.add(item);
				}
			}else{
				this.frowset = dao.search("select TabId,Name from template_table  where OperationCode=?",Arrays.asList(new String[]{nodeId}));
				String tabid="";
				while(this.frowset.next()){
					boolean flag = false;
					tabid=this.frowset.getString("TabId");
					//判断是否有此表
					if(this.userView.isHaveResource(IResourceConstant.RSBD,tabid)){//人事移动
						flag = true;
					}
					if(this.userView.isHaveResource(IResourceConstant.POS_BD,tabid)){
						flag = true;
					}
					if(this.userView.isHaveResource(IResourceConstant.ORG_BD,tabid)){
						flag = true;
					}
					if(!flag){
						continue;
					}
					item = new HashMap();
					item.put("id", this.frowset.getString("TabId"));//给节点id赋值
					item.put("text", this.frowset.getString("TabId")+"."+this.frowset.getString("Name"));//给节点名称赋值
					item.put("leaf", true);//是否是子节点
					if(multiSelect)
						item.put("checked",false);
					itemList.add(item);
				}
			}
			
			this.formHM.clear();
			this.formHM.put("child", itemList);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void loadYKCardData(String nodeId,String rnameFlag){
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{

			HashMap item = null;
			ArrayList itemList = new ArrayList();
			ArrayList sqlList = new ArrayList();
			sqlList.add(rnameFlag);
			//第一次从根节点进来
			if("root".equals(nodeId)){
				//查询登记表下的所有一级节点
				this.frowset=dao.search("select sortid,sortname from rsort where flag is null or flag=? order by id ",sqlList);
				ArrayList<String> list=new ArrayList<String>();
				list.add(rnameFlag);
				String s="";
				while(frowset.next()){
					item = new HashMap();
					item.put("id", frowset.getString("sortid"));
					item.put("text", frowset.getString("sortname"));
					itemList.add(item);
					list.add(frowset.getString("sortid"));
					s+="?,";
				}
				//删除分类 模板未清空,将失效分类的模板统一放置
				this.frowset=null;
				this.frowset=dao.search("select TabId,Name from Rname  where flagA=? and  moduleflag not in ("+s.substring(0, s.length()-1)+") ",list);
				String tabid="";
				while(frowset.next()){
					tabid=this.frowset.getString("TabId");
					//判断是否有此表
					if(this.userView.isHaveResource(IResourceConstant.CARD,tabid)){//登记表
						item = new HashMap();
						item.put("id", this.frowset.getString("TabId"));//给节点id赋值
						item.put("text", this.frowset.getString("TabId")+"."+this.frowset.getString("Name"));//给节点名称赋值
						item.put("leaf", true);//是否是子节点
						if(multiSelect)
							item.put("checked",false);
						itemList.add(item);
					}
				}
				
				
			}else{
				this.frowset = dao.search("select TabId,Name from Rname  where flagA=? and  moduleflag=?",Arrays.asList(new String[]{rnameFlag,nodeId}));
				String tabid="";
				while(this.frowset.next()){
					tabid=this.frowset.getString("TabId");
					//判断是否有此表
					if(this.userView.isHaveResource(IResourceConstant.CARD,tabid)){//登记表
						item = new HashMap();
						item.put("id", this.frowset.getString("TabId"));//给节点id赋值
						item.put("text", this.frowset.getString("TabId")+"."+this.frowset.getString("Name"));//给节点名称赋值
						item.put("leaf", true);//是否是子节点
						if(multiSelect)
							item.put("checked",false);
						itemList.add(item);
					}
				}
			}
			
			this.formHM.clear();
			this.formHM.put("child", itemList);
		
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
