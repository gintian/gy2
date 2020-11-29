package com.hjsj.hrms.utils.components.fielditemselector;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadFieldDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		
		String source = (String)this.formHM.get("source");
		if(source==null || source.length()<1)
			return;
		
		ArrayList childItem = new ArrayList();
		/*如果querykey有数据执行查询功能*/
		String querykey = (String)this.formHM.get("querykey");
		if(querykey!=null && querykey.length()>0){
			childItem = queryFieldByKey(querykey,source);
			
			this.formHM.put("children",childItem);
			return;
		}
		
		/*如果node为root说明是初始化进入*/
		String node = (String)this.formHM.get("node");
		if("root".equalsIgnoreCase(node)){
			String[] sourceList = source.split("`");
			//指标源为多个时，循环处理
			for(int i=0;i<sourceList.length;i++){
				String key = sourceList[i];
				ArrayList item = loadFields(key,sourceList.length);
				childItem.addAll(item);
			}
			
			this.formHM.put("children",childItem);
			return;
		}
		
		/*走到这里说明是加载指标*/
		childItem = loadStrutsFieldList(node);
		this.formHM.put("children",childItem);
		
	}
	private ArrayList queryFieldByKey(String key,String source){
		ArrayList resultList= new ArrayList();
		String[] sourceList = source.split("`");
		for(int i=0;i<sourceList.length;i++){
			resultList.addAll(singleSearch(key,sourceList[i]));
		}
		return resultList;
	}
	
	private ArrayList singleSearch(String key,String source){
		ArrayList result = new ArrayList();
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" select itemid,fieldsetid,itemtype,itemdesc,codesetid from ");
		//开头是Y:代表是业务字典表
		if(source.toUpperCase().startsWith("Y:")){
			source = source.substring(2);
			sql.append(" t_hr_busifield where fieldsetid=? and (itemid like ? or itemdesc like ?) ");
		}else if("A".equalsIgnoreCase(source) || "B".equalsIgnoreCase(source) || "K".equalsIgnoreCase(source) || "H".equalsIgnoreCase(source)){
			//系统信息集
			sql.append(" fielditem where fieldsetid like ? and (itemid like ? or itemdesc like ?) and useflag = '1' ");
			source = source+"%";
		}else{
			//具体子集
			sql.append(" fielditem where fieldsetid=? and (itemid like ? or itemdesc like ?) and useflag = '1' ");
		}
		
		ArrayList values = new ArrayList();
		values.add(source);
		values.add("%"+key+"%");
		values.add("%"+key+"%");
		
		Boolean multiple = (Boolean)this.formHM.get("multiple");
		String filterItems = (String)this.formHM.get("filterItems");
		filterItems = ","+filterItems+",".toLowerCase();
		String filterTypes = (String)this.formHM.get("filterTypes");//过滤指标类型  wangb 2019-02-20
		boolean codeFilter = (Boolean)this.formHM.get("codeFilter");
		filterTypes = ","+filterTypes+",".toUpperCase();
		ContentDAO dao = new ContentDAO(this.frameconn);
		try{
			this.frowset = dao.search(sql.toString(), values);
			
			while(this.frowset.next()){
				String itemid = this.frowset.getString("itemid");
				String itemtype = this.frowset.getString("itemtype");
				String codesetid = this.frowset.getString("codesetid");
				//无权限，跳过
				if("0".equals(userView.analyseFieldPriv(itemid)))
					continue;
				
				
				if(filterItems.indexOf(","+itemid.toLowerCase()+",")!=-1){
					continue;
				}
				if(filterTypes.indexOf(","+itemtype.toUpperCase()+",")!=-1){//过滤指标类型  wangb 2019-02-20
					if(StringUtils.equalsIgnoreCase(itemtype,"A")){
						if(!StringUtils.equalsIgnoreCase(codesetid,"0")){
							if(codeFilter){
								continue;
							}
						}else{
		                    continue;
		                }
					}else{
                        continue;
                    }
				}
				String fieldsetid = this.frowset.getString("fieldsetid");
				FieldSet fs = DataDictionary.getFieldSetVo(fieldsetid);
				
				HashMap field = new HashMap();
				field.put("id",itemid);
				field.put("text",this.frowset.getString("itemdesc")+"("+fs.getCustomdesc()+")");
				field.put("itemid",this.frowset.getString("itemid"));
				field.put("itemdesc",this.frowset.getString("itemdesc"));
				field.put("fieldsetid",this.frowset.getString("fieldsetid"));
				field.put("fieldsetdesc",fs.getCustomdesc());
				field.put("itemtype",this.frowset.getString("itemtype"));
				field.put("leaf", true);
				field.put("codesetid", this.frowset.getString("codesetid"));
				if(multiple)
					field.put("checked", false);
				result.add(field);
			}
		}catch(Exception e){
			e.printStackTrace();
			
		}
		
		return result;
	}
	
	
	private ArrayList loadFields(String key,int sourceSize){
		ArrayList childList = new ArrayList();
		HashMap itemMap = null;
		int domain = -1;
		//人员、单位、岗位信息群
		if("A".equalsIgnoreCase(key)){
			domain = Constant.EMPLOY_FIELD_SET;
			itemMap =  new HashMap();
			itemMap.put("id","A");
			itemMap.put("text",ResourceFactory.getProperty("kjg.title.userinformation"));
		}
		if("B".equalsIgnoreCase(key)){
			domain = Constant.UNIT_FIELD_SET;
			itemMap =  new HashMap();
			itemMap.put("id","B");
			itemMap.put("text",ResourceFactory.getProperty("kjg.title.unitinformation"));
		}
		if("K".equalsIgnoreCase(key)){
			domain = Constant.POS_FIELD_SET;
			itemMap =  new HashMap();
			itemMap.put("id","K");
			itemMap.put("text",ResourceFactory.getProperty("kjg.title.postinformation"));
		}
		if("H".equalsIgnoreCase(key)){
			domain = Constant.JOB_FIELD_SET;
			itemMap =  new HashMap();
			itemMap.put("id","H");
			itemMap.put("text",ResourceFactory.getProperty("kjg.title.jizhungangwei"));
		}
		
		if(itemMap!=null){
			itemMap.put("children",getFieldSetList(domain));
			itemMap.put("expanded",sourceSize==1);
			childList.add(itemMap);
			return childList;
		}
		
		//业务字典表
		if(key.toUpperCase().startsWith("Y:")){
			key = key.substring(2);
			FieldSet set = DataDictionary.getFieldSetVo(key);
			if(set!=null){
				itemMap =  new HashMap();
				itemMap.put("id",set.getFieldsetid());
				itemMap.put("text",set.getCustomdesc());
				itemMap.put("expanded",sourceSize==1);
				childList.add(itemMap);
			}
			
			return childList;
		}
		
		/*指定子集*/
		FieldSet set = DataDictionary.getFieldSetVo(key);
		setCheck:if(set!=null){
			//无权限，跳出
			if("0".equals(userView.analyseTablePriv(set.getFieldsetid()))){
				break setCheck;
			}
				
			itemMap =  new HashMap();
			itemMap.put("id",set.getFieldsetid());
			itemMap.put("text",set.getCustomdesc());
			itemMap.put("expanded",sourceSize==1);
			childList.add(itemMap);
		}
		
		return childList;
		
	}
	
	private ArrayList getFieldSetList(int domain){
		
		ArrayList setList;
		if(domain == Constant.JOB_FIELD_SET){
			setList = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.JOB_FIELD_SET);
		}else{
			setList = userView.getPrivFieldSetList(domain);
		}
		
		ArrayList children = new ArrayList();
		for(int i=0;i<setList.size();i++){
			FieldSet set = (FieldSet)setList.get(i);
			HashMap setMap =  new HashMap();
			setMap.put("id",set.getFieldsetid());
			setMap.put("text",set.getCustomdesc());
			children.add(setMap);
		}
		
		return children;
	}
	
	private ArrayList loadStrutsFieldList(String key){
		ArrayList fieldList = new ArrayList();
		
		Boolean multiple = (Boolean)this.formHM.get("multiple");
		String filterItems = (String)this.formHM.get("filterItems");
		filterItems = ","+filterItems+",".toLowerCase();
		String filterTypes = (String)this.formHM.get("filterTypes");//过滤指标类型  wangb 2019-02-20
		boolean codeFilter = (Boolean)this.formHM.get("codeFilter");
		filterTypes = ","+filterTypes+",".toUpperCase();
		
		if(key.startsWith("A") || key.startsWith("B") || key.startsWith("K")){
			fieldList = userView.getPrivFieldList(key);
		}else{
			fieldList = DataDictionary.getFieldList(key, Constant.USED_FIELD_SET);
		}
		FieldSet fs = DataDictionary.getFieldSetVo(key);
		
		ArrayList children = new ArrayList();
		for(int i=0;i<fieldList.size();i++){
			HashMap field = new HashMap();
			FieldItem fi = (FieldItem)fieldList.get(i); 
			
			if(filterItems.indexOf(","+fi.getItemid().toLowerCase()+",")!=-1){
				continue;
			}
			if(filterTypes.indexOf(","+fi.getItemtype().toUpperCase()+",")!=-1){//过滤指标类型  wangb 2019-02-20
				if(StringUtils.equalsIgnoreCase(fi.getItemtype(),"A")){
					if(!StringUtils.equalsIgnoreCase(fi.getCodesetid(),"0")){
						if(codeFilter){
							continue;
						}
					}else{
	                    continue;
	                }
				}else{
                    continue;
                }
			}
			field.put("id",fi.getItemid());
			field.put("text",fi.getItemdesc());
			field.put("itemid",fi.getItemid());
			field.put("itemdesc",fi.getItemdesc());
			field.put("fieldsetid",fi.getFieldsetid());
			field.put("fieldsetdesc",fs.getCustomdesc());
			field.put("itemtype",fi.getItemtype());
			field.put("leaf", true);
			field.put("codesetid", fi.getCodesetid());
			if(multiple)
				field.put("checked", false);
			children.add(field);
		}
		return children;
	}
}
