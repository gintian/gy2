package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject;

import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.vo.UploadContant;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelJsonInfo {
	
	private UserView userView;
	private Connection conn;
	public ExcelJsonInfo(UserView userView,Connection conn){
		this.userView=userView;
		this.conn=conn;
	}
	
	/**
	 * 功能：获取到权限信息。 是否读写。
	 * {
	 *   id：xx
	 *   text:名称
	 *   leaf:false
	 *   write:true
	 *   children:[
	 *     ..
	 *     ]
	 * }
  ]
}
	 * new ExcelJsonInfo(null, this.frameconn).getPermissionByJsonInfo(tabid);
	 * @param tabid
	 * @return
	 */
	public List<DynaBean> getPermissionByJsonInfo(String tabid){
		/**读取json字符串*/
		DownAttachUtils attachUtils=new DownAttachUtils(userView, conn, tabid);
		String jsonStr=attachUtils.getHtmlJsoninfo();
		if(StringUtils.isEmpty(jsonStr)){
			return new ArrayList();
		}
		/**根据json字符串，转换成json*/
		JSONObject jsonInfo = JSONObject.fromObject(jsonStr);
		/**
		 * {
	"module": "1",
	"pages": [{
		"layout": [{
			"content": [{
				"codesetid": "UN",
				"lay": 3,
				"placeholder": "请输入单位名称",
				"align": "left",
				"lable": "单位名称",
				"lable_align": "left",
				"fontcolor": "#000000",
				"type": "select",
				"element_id": "fld_0_5",
				"relation_id": "b0110_2",
				"maxlength": 30,
				"postil_flag": "false",
				"required": false,
				"readonly": false,
				"fontsize": 10.5
			}, {
				"codesetid": "UM",
				"lay": 4,
				"placeholder": "请输入部门",
				"align": "left",
				"lable": "部门",
				"lable_align": "left",
				"fontcolor": "#000000",
				"type": "select",
				"element_id": "fld_0_37",
				"relation_id": "e0122_2",
				"maxlength": 40,
				"postil_flag": "false",
				"required": false,
				"readonly": false,
				"fontsize": 10.5
			}, {
				"codesetid": "@K",
				"lay": 5,
				"placeholder": "请输入职位名称",
				"align": "left",
				"lable": "职位名称",
				"lable_align": "left",
				"fontcolor": "#000000",
				"type": "select",
				"element_id": "fld_0_40",
				"relation_id": "e01a1_2",
				"maxlength": 30,
				"postil_flag": "false",
				"required": false,
				"readonly": false,
				"fontsize": 10.5
			}],
			"columns_width": "33%,33%,33%",
			"horizontal_id": "h01",
			"columns_num": "3"
		}, {
			"content": [{
				"content": "分割线",
				"element_id": "d17f624b-864b-4268-abee-9e0585dd8f10",
				"contentPosition": "right",
				"direction": "horizontal",
				"type": "divider"
			}],
			"columns_width": "100%",
			"horizontal_id": "h09",
			"columns_num": "1"
		}],
		"page_id": "0",
		"page_desc": "第一页",
		"required": "",
		"fill_status": ""
	}],
	"avatar": {
		"element_id": "fld_0_18",
		"relation_id": "photo",
		"file_path": "",
		"lable": "照片",
		"required": false,
		"file_name": "",
		"readonly": true,
		"avatar_id": "p1"
	}
}
		 */
		List permList=new ArrayList();
		JSONArray pages=jsonInfo.getJSONArray("pages");
		if(pages.size()<=0){
			return permList;
		}
		//定义去重map，用于存放所有的elementid
		Map uniqMap=new HashMap();
		for(int i=0;i<pages.size();i++){
			JSONObject pageJson=pages.getJSONObject(i);
			/**"page_id": "0","page_desc": "第一页",*/
			String page_id=pageJson.getString("page_id");
			String page_desc=pageJson.getString("page_desc");
			JSONArray layoutArr=pageJson.getJSONArray("layout");
			DynaBean pagelz=new LazyDynaBean();
			pagelz.set("id", page_id);
			pagelz.set("text", page_desc);
//			pagelz.set("leaf", true);
//			if(layoutArr.size()<=0){
//				continue;
//			}
			pagelz.set("leaf", false);
			boolean pageBtn=false;
			List leafList=new ArrayList();
			for(int k=0;k<layoutArr.size();k++){
				JSONObject layout=layoutArr.getJSONObject(k);
				if(!layout.containsKey("content")){
					continue;
				}
				JSONArray contentArr=layout.getJSONArray("content");
				for(int c=0;c<contentArr.size();c++){
					DynaBean itemlz=new LazyDynaBean();
					JSONObject content=contentArr.getJSONObject(c);
					String type=content.getString("type");
					String element_id=content.containsKey("element_id")?content.getString("element_id"):"";
					if(StringUtils.isEmpty(element_id)||uniqMap.containsKey(element_id)){
						continue;
					}
					
					if(UploadContant.type_table.equals(type)){
						String hz=content.getString("label");
						String relation_id=content.getString("relation_id");
						String[] iteminfo=relation_id.split("_");
						if(StringUtils.isEmpty(hz)){
							hz=content.getString("label_hz");
						}
						itemlz.set("id", element_id);
						uniqMap.put(element_id, element_id);
						itemlz.set("text", hz);
						itemlz.set("leaf", true);
						String chgstate=iteminfo[iteminfo.length-1];
						//修改原因：附件其element_id是 attachment_1 ，其是变化后。使用readonly来校验取值
						if(content.containsKey("readonly")&&!(boolean)content.get("readonly")){
							chgstate="2";
						}
						itemlz.set("write", "2".equals(chgstate)?true:false);
						if(!pageBtn&&"2".equals(chgstate)){
							pageBtn=true;
						}
						//子集
						itemlz.set("isSet", true);
						//拼接所有的代码类
						StringBuffer codes=new StringBuffer();
						if(content.containsKey("columns")){
							JSONArray columnArr=content.getJSONArray("columns");
							for(int h=0;h<columnArr.size();h++){
								JSONObject column=columnArr.getJSONObject(h);
								if(!column.containsKey("codesetid")){
									continue;
								}
								String codesetid=column.getString("codesetid");
								if(StringUtils.isEmpty(codesetid)||"0".equals(codesetid)||codes.append(",").indexOf(","+codesetid+",")!=-1){
									continue;
								}
								codes.append(",");
								codes.append(codesetid);
							}
						}
						itemlz.set("codesetids", codes.length()>1?codes.substring(1):"");
						leafList.add(itemlz);
						continue;
					}
					
					if(content.containsKey("relation_id")){
						String hz=content.getString("label");
						String relation_id=content.getString("relation_id");
						if(StringUtils.isEmpty(relation_id)||StringUtils.isEmpty(element_id)){
							continue;
						}
						boolean isVar=false;//标识是否是临时变量
						if(content.containsKey("isVar")&&(boolean)content.get("isVar")){
							isVar=true;
						}
						String[] iteminfo=relation_id.split("_");
						if(!isVar&&iteminfo.length<2){
							continue;
						}
//							String itemid=iteminfo[0];
						itemlz.set("id", element_id);
						uniqMap.put(element_id,element_id);
						itemlz.set("text", hz);
						itemlz.set("leaf", true);
						String chgstate=isVar?"":iteminfo[1];
						//修改原因：附件其element_id是 attachment_1 ，其是变化后。使用readonly来校验取值
						if(content.containsKey("readonly")&&!(boolean)content.get("readonly")){
							chgstate="2";
						}
						//true 写  
						itemlz.set("write", "2".equals(chgstate)?true:false);
						if(!pageBtn&&"2".equals(chgstate)){
							pageBtn=true;
						}
						//指标
						itemlz.set("isSet", false);
						leafList.add(itemlz);
					}
				}
				
			}
			//如果第二页 没有指标，显示只读属性
			if(leafList.size()==0){
				pagelz.set("write", false); 
			}else{
				pagelz.set("write", pageBtn); 
			}
			pagelz.set("children", leafList);
			permList.add(pagelz);
		}
		
		return permList;
	}
	
}
