
package com.hjsj.hrms.transaction.kq.options.struts;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * WARNING!!! 人事异动考勤模板对应关系已移至模板设计界面中，此类不再使用！！！
 * <p>Title: KqBusiTemplateMappingTrans </p>
 * <p>Description: 人事异动考勤模板对应关系类</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2013-8-27 下午04:30:37</p>
 * @author zhaoxj
 * @version 1.0
 */
public class KqBusiTemplateMappingTrans extends IBusiness {

    public void execute() throws GeneralException {
        try{
            HashMap hm = (HashMap)this.formHM.get("requestPamaHM");
            if(hm == null) 
                return;
            
            String busiType = (String)hm.get("type");
            String templateId = (String)hm.get("id");
            String strMappings = (String)hm.get("mapping");
            
            if(busiType == null || "".equals(busiType))
                return;
            
            if(templateId == null || "".equals(templateId) || "-1".equals(templateId))
                return;
              
            strMappings = SafeCode.decode(strMappings);
            String[] mappingArray = strMappings.split(","); 
            
            ArrayList kqItems = DataDictionary.getFieldList(busiType, Constant.USED_FIELD_SET);
            ArrayList templateItems = getTemplateItems(templateId);
            ArrayList mappings = this.getMappingList(busiType, kqItems, templateItems, mappingArray);
            this.getFormHM().put("mappings", mappings);
        }
        catch (Exception e) {
            e.printStackTrace();
            //throw GeneralExceptionHandler.Handle(new GeneralException(""));
        }
    }
    
    private ArrayList getTemplateItems(String templateId){
        ArrayList templateItems = new ArrayList();
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT Field_name,Hz,Field_type,chgstate FROM template_set");     
        sql.append(" WHERE tabid=" + templateId);
        sql.append(" AND field_name is not null");
        sql.append(" ORDER BY pageid,Gridno");
        try{
            this.frowset = dao.search(sql.toString());
            while (this.frowset.next()) {
                LazyDynaBean bean = new LazyDynaBean();
                
                String fieldName = this.frowset.getString("Field_name").toLowerCase();
                String hz = this.frowset.getString("Hz");
                hz = hz.replaceAll("`", "");
                if(2 == this.frowset.getInt("chgstate")){
                    fieldName = fieldName + "_2";
                    hz = "拟" + hz + "";
                }
                else {
                    fieldName = fieldName + "_1";
                    hz = "现" + hz + "";
                }
                
                bean.set("itemId", fieldName);
                bean.set("itemDesc", hz);
                bean.set("itemType", this.frowset.getString("Field_type"));
                templateItems.add(bean);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return templateItems;
    }
    
    private ArrayList getMappingList(String busiTab, ArrayList kqItems, ArrayList templateItems, String[] mappingArray){
        ArrayList mappingList = new ArrayList();
        
        for (int i = 0; i < kqItems.size(); i++) {
            LazyDynaBean mapping = new LazyDynaBean();
            FieldItem item = (FieldItem)kqItems.get(i);
            
            String itemId = item.getItemid();
            if("nbase".equalsIgnoreCase(itemId)
                    || "a0100".equalsIgnoreCase(itemId)
                    || "a0101".equalsIgnoreCase(itemId)
                    || "b0110".equalsIgnoreCase(itemId)
                    || "e0122".equalsIgnoreCase(itemId)
                    || "e01a1".equalsIgnoreCase(itemId)
                    || "i9999".equalsIgnoreCase(itemId)
                    || itemId.equalsIgnoreCase(busiTab + "09")
                    || itemId.equalsIgnoreCase(busiTab + "13")
                    || itemId.equalsIgnoreCase(busiTab + "z0")
                    || itemId.equalsIgnoreCase(busiTab + "z5")
                    )
                continue;
            
            mapping.set("kqItemId", item.getItemid());
            mapping.set("kqItemDesc", item.getItemdesc());
            
            String ydItemId = "";
            for (int j = 0; j < mappingArray.length; j++) {
                String[] amap = mappingArray[j].split(":");
                if(amap[0].equals(item.getItemid())){
                    ydItemId = amap[1];
                    break;
                }
            }
            mapping.set("ydItemId", ydItemId);
            
            ArrayList ydItems = null;
            if (!"Q1104".equalsIgnoreCase(itemId))
                ydItems = this.getYdItems(templateItems, item.getItemtype());
            else {
                //由于模板中无法直接从班次表中选择，所以转为代码处理（代码值与班次编号相同）
                ydItems = this.getYdItems(templateItems, "A");
            }
            mapping.set("ydItems", ydItems);
            
            mappingList.add(mapping);
        }
        
        return mappingList;
    }
    
    private ArrayList getYdItems(ArrayList templateItems, String itemType) {
        ArrayList items = new ArrayList();
        
        CommonData ydItem = new CommonData("#","请选择...");
        items.add(ydItem);
        
        for (int i = 0; i < templateItems.size(); i++) {
            LazyDynaBean aBean = (LazyDynaBean)templateItems.get(i);
            String ydItemType = (String)aBean.get("itemType");
            if (itemType.equalsIgnoreCase(ydItemType) 
                    || ("A".equalsIgnoreCase(itemType) && "M".equalsIgnoreCase(ydItemType))) {
                ydItem = new CommonData();
                ydItem.setDataName(aBean.get("itemDesc").toString());
                ydItem.setDataValue(aBean.get("itemId").toString());
                
                items.add(ydItem);
            }
        }
        
        return items;
    }

}
