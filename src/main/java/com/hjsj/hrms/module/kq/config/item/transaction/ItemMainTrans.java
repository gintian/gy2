package com.hjsj.hrms.module.kq.config.item.transaction;


import com.hjsj.hrms.module.kq.config.item.businessobject.KqItemService;
import com.hjsj.hrms.module.kq.config.item.businessobject.impl.KqItemServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 考勤项目交易
 *
 * @author ZhangHua
 * @date 10:04 2018/10/18
 */
public class ItemMainTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        JSONObject returnJson = new JSONObject();
        try {
            MorphDynaBean jsonStrObject = (MorphDynaBean) this.getFormHM().get("jsonStr");

            /**
             * list: 初始化
             * save:保存数据
             * deleteItem:删除项目
             * move:上下移动
             * searchImportItems:查询来源子集中符合条件的指标
             * searchImportField：显示来源子集数据
             * saveImportParam：保存导入指标参数设置
             */
            String actionType = (String) jsonStrObject.get("type");
            KqItemService kqItemService = new KqItemServiceImpl(this.getUserView(), this.getFrameconn());
            //初始化方法
            if ("list".equalsIgnoreCase(actionType)) {
                kqItemService.synchronizeCodeItemToKqItem();
                
                JSONArray q35List = new JSONArray();
                ArrayList<LazyDynaBean> list = kqItemService.listQ35Item();
                for (LazyDynaBean bean : list) {
                    JSONObject jo = JSONObject.fromObject(bean);
                    q35List.add(jo);
                }
                
                JSONArray jsonList = new JSONArray();
                ArrayList<LazyDynaBean> dataList = kqItemService.listKqItem("", null, "");
                for (LazyDynaBean bean : dataList) {
                	String id = PubFunc.encrypt(String.valueOf(bean.get("item_id")));
                	bean.set("item_id", id);
                	String fielditemid = String.valueOf(bean.get("fielditemid"));
                	if(fielditemid.length() > 0) {
                		for (LazyDynaBean q35bean : list) {
                			// 61514 传统计指标 类型
                			if(fielditemid.equalsIgnoreCase(String.valueOf(q35bean.get("id")))) {
                				bean.set("fielditem_type", String.valueOf(q35bean.get("type")));
                				break;
                			}
                        }
                	}
                	// 61649 计算公式内容转码问题
            		bean.set("c_expr", SafeCode.encode(String.valueOf(bean.get("c_expr"))));
                	
                	JSONObject jo = JSONObject.fromObject(bean);
                	jsonList.add(jo);
                }

                JSONObject return_data = new JSONObject();
                return_data.put("list", jsonList);
                return_data.put("q35list", q35List);
                returnJson.put("return_data", return_data);
                returnJson.put("return_code", "success");

            }else if("save".equalsIgnoreCase(actionType)){

                String kq_itemid = PubFunc.decrypt((String)jsonStrObject.get("kq_itemid"));
                String column_id = (String)jsonStrObject.get("column_id");
                Object value = jsonStrObject.get("value");
                if("c_expr".equalsIgnoreCase(column_id)){
                    String strValue = SafeCode.decode(String.valueOf(value));
                    strValue = strValue.replace("!","\r");
                    strValue = strValue.replace("`","\n");
                    // 61649 计算公式内容转码问题
                    value = PubFunc.keyWord_reback(strValue);
                }

                if(kqItemService.saveData(kq_itemid,column_id,value)){
                    returnJson.put("return_code", "success");
                }else{
                	 returnJson.put("return_code", "fail");
                     returnJson.put("return_msg", "更新失败");
                     //【55907】develop：考虑到妇婴的业务比较特殊，会存在多个项目对应同一个统计指标的情况，所以需要在标准产品中吧这个限制功能放开，允许多个项目指定同一个统计指标
					/*
					 * if (value.equals("-1")) { returnJson.put("return_code", "success"); }else {
					 * returnJson.put("return_code", "fail"); if
					 * ("fielditemid".equalsIgnoreCase(column_id)) { returnJson.put("return_msg",
					 * ResourceFactory.getProperty("kq.item.error")); } }
					 */
                }

            }else if("deleteItem".equalsIgnoreCase(actionType)){
                String kq_itemid=(String)jsonStrObject.get("kq_itemid");

                if(kqItemService.deleteItem(kq_itemid)){
                    returnJson.put("return_code", "success");
                }else{
                    returnJson.put("return_code", "fail");
                    returnJson.put("return_msg", "删除失败");
                }
            }else if("move".equalsIgnoreCase(actionType)){
                String ori_id = (String) jsonStrObject.get("ori_id");
                ori_id = PubFunc.decrypt(ori_id);
                String to_id = (String) jsonStrObject.get("to_id");
                to_id = PubFunc.decrypt(to_id);
                String to_seq = (String) jsonStrObject.get("to_seq");
                String ori_seq = (String) jsonStrObject.get("ori_seq");
                String dropPosition = (String) jsonStrObject.get("dropPosition");
                if(StringUtils.isBlank(ori_seq))
                    ori_seq = "1";
                if(StringUtils.isBlank(to_seq))
                    to_seq = "1";

                ArrayList<LazyDynaBean> list= kqItemService.dropKqItem(ori_id,to_id,to_seq,ori_seq,dropPosition);

                JSONObject return_data = new JSONObject();
                JSONArray jsonList = new JSONArray();
                for (LazyDynaBean bean : list) {
                    JSONObject jo = JSONObject.fromObject(bean);
                    jsonList.add(jo);
                }
                return_data.put("list", jsonList);
                returnJson.put("return_data", return_data);
                returnJson.put("return_code", "success");
            } else if("searchImportField".equalsIgnoreCase(actionType)) {
            	String kq_itemid=(String)jsonStrObject.get("kq_itemid");
            	HashMap<String, Object> map = kqItemService.searchFieldImportParam(kq_itemid);
            	map.put("kq_itemid", kq_itemid);
            	returnJson = JSONObject.fromObject(map);
            } else if("searchImportItems".equalsIgnoreCase(actionType)) {
            	String fieldSetId=(String)jsonStrObject.get("fieldSetId");
            	String fieldItemId=(String)jsonStrObject.get("fieldItemId");
            	HashMap<String, Object> map = kqItemService.searchItem(fieldSetId, fieldItemId);
            	returnJson = JSONObject.fromObject(map);
            } else if("saveImportParam".equalsIgnoreCase(actionType)) {
            	HashMap<String, String> map = kqItemService.saveImportParam(jsonStrObject);
            	returnJson = JSONObject.fromObject(map);
            }
            
            this.getFormHM().put("returnStr",returnJson);
        } catch (Exception e) {
            returnJson.put("return_code", "fail");
            returnJson.put("return_msg", e.getMessage());
            this.getFormHM().put("returnStr",returnJson);
        }
    }
}
