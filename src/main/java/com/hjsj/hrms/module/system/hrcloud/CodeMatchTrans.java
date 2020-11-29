package com.hjsj.hrms.module.system.hrcloud;

import com.hjsj.hrms.module.system.hrcloud.util.SyncConfigUtil;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CodeMatchTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		Map map = new HashMap();  
		try{
			String transType = (String)this.getFormHM().get("transType");
			if("loadMatch".equals(transType)){
				//获取代码匹配数据
				String appId = (String)this.getFormHM().get("appId");
				String tenantId = (String)this.getFormHM().get("tenantId");
				String appSecret = (String)this.getFormHM().get("appSecret");
				ArrayList codefieldsList = this.getFormHM().get("codefields") == null?null:(ArrayList)this.getFormHM().get("codefields");
				
				HashMap newMap = new HashMap();
				String codefields = "";
				for(Object obj:codefieldsList){
					MorphDynaBean codefieldsMap = (MorphDynaBean) obj;
					String codeset = (String) codefieldsMap.get("codesetid");
					String field = (String) codefieldsMap.get("fielditemid");
					FieldItem item=DataDictionary.getFieldItem(field);
					String hr_codeset = item.getCodesetid();
					//xus 19/11/27代码关联页去掉重复的代码页签
					if(!newMap.containsKey(codeset)){
						newMap.put(codeset, hr_codeset);
						codefields+=codeset+",";
					}
				}
				if(codefields.length()>0){
					codefields=codefields.substring(0, codefields.length()-1);
				}
				
				//逻辑
				HashMap configData = new HashMap();
				JSONObject matchTableData = getMatchTableData(codefields);
//				JSONArray codes = getCloudCodes();
				JSONArray codes = getCloudCodes(appId,tenantId,appSecret,codefields);
				
				if(codes == null){
					JSONObject returnStr = new JSONObject();
					returnStr.put("return_code", "fail");
					returnStr.put("return_msg", "未获取到云代码");
					returnStr.put("return_data", "");
					this.getFormHM().put("returnStr", returnStr);
					return ;
				}
				JSONArray codeitemsArray = getCodeItemsArray(codes,newMap);
				// 将系统参数中的值 放到数据中
				codes = insSysConstantCodes(codeitemsArray,matchTableData,newMap);
				//codes 数据类型：[{id:'XX',name:'XX',codeitems:[{id:'XX',name:'XX',cloud_codeid:'XX',cloud_codename:'XX',parentid:'XX'},{},{}...]}...]
//				configData.put("codes", codes);
//				this.getFormHM().put("configData", configData);
				
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_code", "success");
				returnStr.put("return_msg", "");
				returnStr.put("return_data", codes);
				this.getFormHM().put("returnStr", returnStr);
				
			}else if("autoMatch".equals(transType)){
				//自动关联功能
				//1、获取前台参数
				ArrayList configData = (ArrayList)this.getFormHM().get("json_str");
				//2、与hr代码库中进行对比
				JSONObject returnJson = getCloudMatchJson(configData);
				//3、返回到前台
//				this.getFormHM().put("returnJson", returnJson);
				JSONObject returnStr = new JSONObject();
				returnStr.put("return_code", "success");
				returnStr.put("return_msg", "");
				returnStr.put("return_data", returnJson);
				this.getFormHM().put("returnStr", returnStr);
				
			}else if("saveMatch".equals(transType)){
				//保存功能
				ArrayList configData = (ArrayList)this.getFormHM().get("json_str");
//				HashMap codeitemidsMap = getCodeItemIdS();
				String sql = "";
//				Statement stmt = null;
				ContentDAO dao = null;
				dao = new ContentDAO(this.frameconn);
				
				sql=" delete from t_sys_hrcloud_codematch ";
				dao.update(sql);
				
//				stmt = this.frameconn.createStatement();
				ArrayList list = new ArrayList();
				for(int i = 0;i<configData.size();i++){
					MorphDynaBean cjson = (MorphDynaBean) configData.get(i);
					String codesetid = (String) cjson.get("cloud_codesetid");
					String hr_codesetid = (String) cjson.get("hr_codesetid");
					ArrayList matchCode = (ArrayList) cjson.get("matchCode");
//					ArrayList<String> codeitemids = (ArrayList<String>) codeitemidsMap.get(codesetid);
					for(int j = 0; j<matchCode.size();j++){
						MorphDynaBean json = (MorphDynaBean) matchCode.get(j);
						String codeitemid = (String) json.get("cloud_codeid");
						String codeitemdesc = (String) json.get("cloud_codename");
						String parentid = (String) json.get("parentid");
						String hr_codeitemid = (String) json.get("hr_codeid");
						RecordVo vo = new RecordVo("t_sys_hrcloud_codematch");
						vo.setString("codesetid", codesetid);
						vo.setString("codeitemid", codeitemid);
						vo.setString("codeitemdesc", codeitemdesc);
						vo.setString("parentid", parentid);
						vo.setString("hr_codesetid", hr_codesetid);
						vo.setString("hr_codeitemid", hr_codeitemid);
						list.add(vo);
					}
				}
				dao.addValueObject(list);
			}
		}catch(Exception e){
			JSONObject returnStr = new JSONObject();
			returnStr.put("return_code", "fail");
			returnStr.put("return_msg", e.getMessage());
			returnStr.put("return_data", "");
			this.getFormHM().put("returnStr", returnStr);
			return ;
		}finally {
			
		}
	}
	/**
	 * 生成自动关联的数据
	 * {'XX'//cloud_codesetid:{'XX'//itemid:{cloud_codeid:'XX',cloud_codename:'XX',hr_codeid:'XX',hr_codename:'XX'}}}
	 * @param configData
	 * @return
	 */
	private JSONObject  getCloudMatchJson(ArrayList configData) {
		JSONObject returnJson = new JSONObject();
		JSONObject codeJson = new JSONObject();
		for(Object o:configData){
			codeJson = new JSONObject();
			MorphDynaBean cjson = (MorphDynaBean) o;
			String cloudcodesetid = (String) cjson.get("cloud_codesetid");
			String hr_codesetid = (String) cjson.get("hr_codesetid");
			ArrayList cloudcodeitems = (ArrayList) cjson.get("cloud_codeitems");
			HashMap codemap = getHrCodeMap(hr_codesetid);
			JSONObject json = new JSONObject();
			for(Object obj : cloudcodeitems){
				MorphDynaBean clouditem = (MorphDynaBean)obj;
				String cloud_codeid =  (String) clouditem.get("cloud_codeid");
				String cloud_codename =  (String) clouditem.get("cloud_codename");
				if(codemap.containsKey(cloud_codename)){
					String hr_codeid = (String) codemap.get(cloud_codename);
					json = new JSONObject();
					json.put("cloud_codeid", cloud_codeid);
					json.put("cloud_codename", cloud_codename);
					json.put("hr_codeid", hr_codeid);
					json.put("hr_codename", cloud_codename);
					codeJson.put(cloud_codeid, json);
				}
			}
			if(codeJson.size()>0){
				returnJson.put(cloudcodesetid, codeJson);
			}
		}
		
		return returnJson;
	}
	/**
	 * 获取hr系统中的代码map <name,id>
	 * @param hr_codesetid
	 * @return
	 */
	private HashMap getHrCodeMap(String hr_codesetid) {
		HashMap map = new HashMap();
		ArrayList hr_items = AdminCode.getCodeItemList(hr_codesetid);
		for(Object obj : hr_items){
			CodeItem item = (CodeItem)obj;
			map.put(item.getCodename(),item.getCodeitem());
		}
		return map;
	}
	/**
	 * 获取 id的map{codeitemid：codesetid}
	 * @return
	 */
	private HashMap getCodeItemIdS() {
		HashMap returnMap = new HashMap();
		String sql = "select codeitemid,codesetid from t_sys_hrcloud_codematch ";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
				String codeitemid = this.frowset.getString("codeitemid");
				String codesetid = this.frowset.getString("codesetid");
				
				ArrayList<String> codeitemids = new ArrayList<String>();
				if(returnMap.containsKey(codesetid)){
					codeitemids = (ArrayList<String>) returnMap.get(codesetid);
				}
				codeitemids.add(codeitemid);
				returnMap.put(codesetid, codeitemids);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnMap;
	}
	/**
	 * 将系统中的代码配置 加到数据中
	 * @param codeitemsArray
	 * @param matchTableData
	 * @param newMap 
	 * @return
	 */
	private JSONArray insSysConstantCodes(JSONArray codeitemsArray, JSONObject matchTableData, HashMap newMap) {
		if(matchTableData.size()==0){
			return codeitemsArray;
		}
		for(int i = 0;i<codeitemsArray.size();i++){
			JSONObject codeset = codeitemsArray.getJSONObject(i);
			String id = codeset.getString("id");
			if(!matchTableData.containsKey(id)){
				continue;
			}
			//codeset与hr系统中已存的值进行对应
			JSONObject syscodeset = matchTableData.getJSONObject(id);
			
			//如果从指标页获取的hr代码与代码表中的代码不对应 则不对应
			if(newMap.containsKey(id)&&!syscodeset.getString("codesetid").equals(newMap.get(id))){
				continue;
			}
			codeitemsArray.getJSONObject(i).put("codesetid", syscodeset.getString("codesetid"));
			codeitemsArray.getJSONObject(i).put("codesetdesc", syscodeset.getString("codesetdesc"));
			
			JSONArray codeitem = codeset.getJSONArray("codeitems");
			for(int j = 0;j < codeitem.size();j++){
				JSONObject codeitemObj = codeitem.getJSONObject(j);
				String codeitemid = codeitemObj.getString("cloud_codeid");
				if(!syscodeset.getJSONObject("codeitems").containsKey(codeitemid)){
					continue;
				}
				JSONObject syscodeitem = syscodeset.getJSONObject("codeitems").getJSONObject(codeitemid);
				codeitem.getJSONObject(j).put("hr_codeid", syscodeitem.getString("hr_codeitemid"));
				codeitem.getJSONObject(j).put("hr_codename", syscodeitem.getString("hr_codeitemdesc"));
			}
		}
		return codeitemsArray;
	}
	/**
	 * 获取表中已存在的记录
	 * {'AX':{id:'AX',codesetid:'XX',codesetdesc:'XX',codeitems:
	 * 			{'XY':{codesetid:'XX',codeitemid:'XY',codeitemdesc:'XX,parentid:'XX',hr_codesetid:'XX',hr_codeitemid:'XX',hr_codeitemdesc:'XX'...}
	 * 			 ,'XZ':{...}...
	 * 			}
	 * 		 }
	 * }
	 * @param codefields
	 * @return
	 */
	@SuppressWarnings("resource")
	private JSONObject getMatchTableData(String codefields) {
		JSONObject coseSetTexts = new JSONObject(); 
		
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select t.codesetid codesetid,t.codeitemid codeitemid,t.codeitemdesc codeitemdesc,t.parentid parentid,t.hr_codesetid hr_codesetid,t.hr_codeitemid hr_codeitemid,c.codeitemdesc hr_codeitemdesc,s.CodeSetDesc hr_codesetdesc from t_sys_hrcloud_codematch t left join codeitem c on t.hr_codeitemid = c.codeitemid and t.hr_codesetid = c.codesetid left join codeset s on t.hr_codesetid = s.CodeSetId where 1=1 ";
//			String sql = "select codesetid,codeitemid,codeitemdesc,parentid,hr_codesetid,hr_codeitemid from t_sys_hrcloud_codematch where 1=1 ";
//			String codesetSql = "select distinct t.codesetid id,c.CodeSetId codesetid,c.CodeSetDesc codesetdesc from t_sys_hrcloud_codematch t, CodeSet c where t.codesetid = c.CodeSetId where 1=1 ";
			ArrayList<String> codesList = new ArrayList<String>();
			if(!"".equals(codefields)){
				String[] codes = codefields.split(",");
				if(codes.length>0){
					sql += " and ( ";
//					codesetSql += " and ( ";
					for(int i=0;i<codes.length;i++){
						if(i>0){
							sql += " or ";
//							codesetSql += " or ";
						}
						sql += "  t.codesetid = ? ";
//						codesetSql += "  codesetid = ? ";
						codesList.add(codes[i]);
					}
					sql += " ) ";
				}
				this.frowset = dao.search(sql,codesList);
			}else{
				this.frowset = dao.search(sql);
			}
			while(this.frowset.next()){
				String id = this.frowset.getString("codesetid");
				if(!coseSetTexts.containsKey(id)){
					JSONObject codesetObject = new JSONObject();
					codesetObject.put("id", id);
					codesetObject.put("codesetid", this.frowset.getString("hr_codesetid"));
					codesetObject.put("codesetdesc", this.frowset.getString("hr_codesetdesc"));
					coseSetTexts.put(id, codesetObject);
				}
				JSONObject itemObject = new JSONObject();
				String codeitemid = this.frowset.getString("codeitemid");
				itemObject.put("codesetid", id);
				itemObject.put("codeitemid", codeitemid);
				itemObject.put("codeitemdesc", this.frowset.getString("codeitemdesc"));
				itemObject.put("parentid", this.frowset.getString("parentid"));
				itemObject.put("hr_codesetid", this.frowset.getString("hr_codesetid"));
				itemObject.put("hr_codeitemid", this.frowset.getString("hr_codeitemid"));
				itemObject.put("hr_codeitemdesc", this.frowset.getString("hr_codeitemdesc"));
				itemObject.put("hr_codesetdesc", this.frowset.getString("hr_codesetdesc"));
				if(!coseSetTexts.getJSONObject(id).containsKey("codeitems")){
					JSONObject codeitemObject = new JSONObject();
					codeitemObject.put(codeitemid, itemObject);
					coseSetTexts.getJSONObject(id).put("codeitems", codeitemObject);
				}else{
					coseSetTexts.getJSONObject(id).getJSONObject("codeitems").put(codeitemid,itemObject);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
		}
		return coseSetTexts;
	}
/**
 * 修改云中获取的数据格式
 * [{id:'XX',name:'XX',codeitems:[{id:'XX',name:'XX',cloud_codeid:'XX',cloud_codename:'XX',parentid:'XX'},{},{}...]}...]
 * @param codes
 * @param newMap 
 * @return
 */
	private JSONArray getCodeItemsArray(JSONArray codes, HashMap newMap){
		JSONArray returnarray = new JSONArray();
		for(int z = 0 ;z<codes.size();z++){
			JSONObject codeset = codes.getJSONObject(z);
			JSONObject newcodeset = new JSONObject();
			JSONArray array = new JSONArray();
			String id = (String) codeset.get("id");
			String name = (String) codeset.get("name");
			newcodeset.put("id", id);
			newcodeset.put("name", name);
			if(newMap.get(id)!=null){
				newcodeset.put("codesetid", newMap.get(id));
			}
			if(codeset.get("codeitems")==null){
				returnarray.add(newcodeset);
				continue ; 
			}else{
				JSONArray itemsArray = codeset.getJSONArray("codeitems");
				if(itemsArray.size()==0){
					returnarray.add(newcodeset);
					continue;
				}else{
					array = getChildArray(codeset,id,name);
					/*for(int i = 0 ; i<itemsArray.size();i++){
						JSONObject item = (JSONObject) itemsArray.get(i);
						JSONObject returnitemchild = new JSONObject();
						returnitemchild.put("id", id);
						returnitemchild.put("name", name);
						if(newMap.get(id)!=null){
							newcodeset.put("codesetid", newMap.get(id));
						}
						returnitemchild.put("cloud_codeid", item.get("id"));
						returnitemchild.put("cloud_codename", item.get("name"));
						returnitemchild.put("parentid", item.get("id"));
						array.add(returnitemchild);
						JSONArray childarray = getChildArray(item,id,name);
						array.addAll(childarray);
					}*/
				}
			}
			newcodeset.put("codeitems", array);
			returnarray.add(newcodeset);
		}
		return returnarray;
	}
	private JSONArray getChildArray(JSONObject item, String id, String name) {
		JSONArray array = new JSONArray();
		if(item.get("codeitems")==null){
			return null;
		}else{
			JSONArray itemsArray = (JSONArray)item.get("codeitems");
			JSONObject returnitemchild = new JSONObject();
			if(itemsArray.size()==0){
				return null;
			}else{
				for(int i = 0 ; i<itemsArray.size();i++){
					JSONObject itemchild = (JSONObject) itemsArray.get(i);
					returnitemchild.put("id", id);
					returnitemchild.put("name", name);
					returnitemchild.put("cloud_codeid", itemchild.get("id"));
					returnitemchild.put("cloud_codename", itemchild.get("name"));
					returnitemchild.put("parentid", item.get("id"));
					array.add(returnitemchild);
					JSONArray childarray = getChildArray(itemchild,id,name);
					array.addAll(childarray);
				}
			}
		}
		
		return array;
	}


	private JSONArray getCloudCodes() {
		
		
		JSONArray codes = new JSONArray();
		JSONObject codeMap = new JSONObject();
		codeMap.put("id", "AX");
		codeMap.put("name", "多层级");
		JSONArray codeitems = new JSONArray();
		JSONObject codeitemMap = new JSONObject();
		codeitemMap.put("id", "01");
		codeitemMap.put("name", "一级1");
		JSONObject codeitemMap2 = new JSONObject();
		JSONArray codeitems2 = new JSONArray();
		codeitemMap2.put("id", "03");
		codeitemMap2.put("name", "二级1");
		codeitems2.add(codeitemMap2);
		codeitemMap.put("codeitems", codeitems2);
		codeitems.add(codeitemMap);
		codeitemMap = new JSONObject();
		codeitemMap.put("id", "02");
		codeitemMap.put("name", "一级2");
		codeitems.add(codeitemMap);
		codeMap.put("codeitems", codeitems);
		codes.add(codeMap);
		return codes;
	}

	/**
	 * 获取云代码
	 * @param appId
	 * @param tenantId
	 * @param appSecret
	 * @return 
	 */
	private JSONArray getCloudCodes(String appId, String tenantId, String appSecret, String codefields) {
		JSONArray jSONArray = new JSONArray();
		
		JSONObject json = new JSONObject();
		json.put("appId", appId);
		JSONObject data = new JSONObject();
		data.put("tenantId", tenantId);
		data.put("codesetId", codefields);
		String dataEncode = SyncConfigUtil.AESEncrypt(data.toString(), appSecret);
		json.put("data", dataEncode);
		ArrayList datalist = new ArrayList();
		datalist.add(appId);
		datalist.add(dataEncode);
		String signEncode = SyncConfigUtil.digest(datalist);
		json.put("sign", signEncode);
		
		String url = SyncConfigUtil.IP_PORT+"/open/codes";
		String param = json.toString();
		String str = SyncConfigUtil.postHttp(url, param);
		
		if(str.indexOf("success")<0){
			return null;
		}
		JSONObject resJson = JSONObject.fromObject(str);
		String resData = resJson.getString("data");
		resData = SyncConfigUtil.AESDecrypt(resData,appSecret);
		JSONObject resDataJson = JSONObject.fromObject(resData);
		jSONArray = resDataJson.getJSONArray("codesets");
		return jSONArray;
	}

}
