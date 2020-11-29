package com.hjsj.hrms.module.recruitment.position.businessobject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title: PositionDataMap </p>
 * <p>Description:主要是方便生成创建职位页面所需要的各个属性 </p>
 * <p>Company: hjsj</p>
 * <p>create time  2015-1-29 下午02:20:49</p>
 * @author xiongyy
 * @version 1.0
 */
public class PositionDataMap extends HashMap{
    /**
     * 对应前台json对象取值
     * @param id
     * @param desc 描叙名称
     * @param codeId 代码编号
     * @param level 显示级别=1下拉框 =2框 =3树结构
     * @param type  =A 字符 =N 数值 =D 日期=M 大文本   
     * @param codelist 
     * @param required =y必填 =n不是必填
     * @param itemLength 
     */
    public PositionDataMap(String id, String desc, String codeId, String level,
             String type, ArrayList codelist,String required, String itemLength) {
        put("id",id);
        put("desc",desc);
        put("codeId",codeId);
        put("level",level);
        put("type",type);
        put("codelist",codelist);
        put("required",required);
        put("itemLength",itemLength);
    }                                                                                    
                                                                                       
                                                                                         
    public PositionDataMap(){};
  
    
}
