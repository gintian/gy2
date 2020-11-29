package com.hjsj.hrms.businessobject.general.inform;

import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.jar.JarException;
/**
 * <p>Title:</p>
 * <p>Description:查找对应的指标id</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class CorField {
	public final static int SEX_ITEMID=0;//性别指标
	public final static int BIRTHDAY_ITEMID=1;//出生日期指标
	public final static int IDCARD_ITEMID=2;//身份证号指标
	public CorField(){
		super();
	}
	/**
	 * 根据类型标识获取对应指标
	 * @param type //类型标识
	 * @return
	 */
	public String getItemid(int type, Connection conn) {
        String itemid = "";
        try {
            switch (type) {
            case SEX_ITEMID:
                itemid = getParamItemId("sex", conn);
                if(StringUtils.isEmpty(itemid)) {
                    itemid = descToItemid("A01", "性别", "a0107");
                }
                
                break;
            case BIRTHDAY_ITEMID:
                itemid = getParamItemId("birthday", conn);
                if(StringUtils.isEmpty(itemid)) {
                    itemid = descToItemid("A01", "出生日期", "a0111");
                }
                
                break;
            case IDCARD_ITEMID:
                itemid =  getParamItemId("cardId", conn);
                if(StringUtils.isEmpty(itemid)) {
                    itemid = descToItemid("A01", "身份证号", "a0177");
                }
                
                break;
            }
            
            FieldItem fi = DataDictionary.getFieldItem(itemid, "A01");
            itemid = fi == null ? "" : itemid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemid;
	}
	/**
	 * 根据中文得到指标id
	 * @param setname //
	 * @param desc //中文名字
	 * @param def //默认值
	 * @return itemid
	 */
	public String descToItemid(String setname,String desc,String def){
		String itemid=def;
		ArrayList list=DataDictionary.getFieldList(setname, Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem field=(FieldItem)list.get(i);
			if(field!=null&&field.getItemdesc().equalsIgnoreCase(desc)) {
                itemid = field.getItemid().toLowerCase();
            }
		}
		
		return itemid;
	}
	
	private String getParamItemId(String type, Connection conn) throws Exception {
        String itemid = "";
        if(conn == null) {
            return itemid;
        }
        
        try {
            OtherParam param = new OtherParam(conn);
            Map setmap = param.serachAtrr("/param/formual[@name='bycardno']");
            if (setmap != null) {
                if ("cardId".equalsIgnoreCase(type)) {
                    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
                    itemid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); //身份证指标
                } else if ("birthday".equalsIgnoreCase(type)) {
                    itemid = setmap.get("birthday").toString().trim().toLowerCase();
                } else if ("age".equalsIgnoreCase(type)) {
                    itemid = setmap.get("age").toString().trim().toLowerCase();
                } else if ("sex".equalsIgnoreCase(type)) {
                    itemid = setmap.get("ax").toString().trim().toLowerCase();
                }
                
            }
            
            FieldItem fi = DataDictionary.getFieldItem(itemid, "A01");
            itemid = fi == null ? "" : itemid;
            
        } catch (JarException e) {
            e.printStackTrace();
        }

        return itemid;
    }
}
