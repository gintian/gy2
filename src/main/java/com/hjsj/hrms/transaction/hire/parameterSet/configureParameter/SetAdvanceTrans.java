/**   
 * @Title: SetAdvanceTrans.java 
 * @Package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter 
 * @Description: TODO
 * @author xucs
 * @date 2014-7-29 下午03:57:33 
 * @version V1.0   
*/
package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 * @ClassName: SetAdvanceTrans 
 * @Description: 面试测评初试复试采用不同的测评表
 * @author xucs 
 * @date 2014-7-29 下午03:57:33 
 *  
 */
public class SetAdvanceTrans extends IBusiness {
    
    public void execute() throws GeneralException {
        ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());//得到配置参数
        HashMap paraMap=parameterXMLBo.getAttributeValues();
        
        ArrayList testTemplatAdvance=(ArrayList) paraMap.get("testTemplatAdvance");//高级测评的相关参数
        
        ParameterSetBo parameterSetBo=new ParameterSetBo(this.getFrameconn());//配置参数的列表
        
        ArrayList testTemplateList=parameterSetBo.getPerTemplateList();     //考评表列表
        
        for(int i=0;i<testTemplatAdvance.size();i++){
            HashMap advanceMap=(HashMap) testTemplatAdvance.get(i);
            String hire_obj_code=(String) advanceMap.get("hire_obj_code");//得到招聘渠道 01：校园招聘 02：社招 03：内招
            if("01".equalsIgnoreCase(hire_obj_code)){
                advanceMap.put("hire_obj_desc", AdminCode.getCodeName("35", hire_obj_code));
            }else if("02".equalsIgnoreCase(hire_obj_code)){
            	 advanceMap.put("hire_obj_desc", AdminCode.getCodeName("35", hire_obj_code));
            }else{
            	 advanceMap.put("hire_obj_desc", AdminCode.getCodeName("35", hire_obj_code));
            }
            String interview=(String) advanceMap.get("interview");//得到面试方式 1：初试 2：复试
            advanceMap.put("interviewDesc", AdminCode.getCodeName("36", interview));
            String  score_item=(String) advanceMap.get("score_item");//得到测评结果对应面试安排信息表（Z05）数值型指标(初试成绩||复试成绩)
            FieldItem item=DataDictionary.getFieldItem(score_item);
            if(item!=null){
                advanceMap.put("score_item_desc", item.getItemdesc());
            }
            
            String  templateId=(String) advanceMap.get("templateId");//得到模版号
            for(int j=0;j<testTemplateList.size();j++){
                CommonData data=(CommonData) testTemplateList.get(j);
                String id=data.getDataValue();
                if(templateId.equalsIgnoreCase(id)){
                    advanceMap.put("templateDesc", data.getDataName());
                    break;
                }
            }
        }
        CommonData data=(CommonData) testTemplateList.get(0);
        data.setDataName("测评模版名称");//设置第一个显示的名字
        ArrayList channelList= new ArrayList();//招聘渠道list
        ArrayList setList=AdminCode.getCodeItemList("35");
        for(int i=0;i<setList.size();i++){
        	CodeItem item=(CodeItem) setList.get(i);
        	 CommonData chanelData=new CommonData(item.getCodeitem(),item.getCodename());
        	 channelList.add(chanelData);
        }
        ArrayList modeList=new ArrayList();//初试复试list
        CommonData modeDataCs=new CommonData("31", AdminCode.getCodeName("36", "31"));//设置初试
        CommonData modeDataFs=new CommonData("32", AdminCode.getCodeName("36", "32"));//设置复试
        modeList.add(modeDataCs);
        modeList.add(modeDataFs);
        ArrayList itemList=parameterSetBo.getItemList();//得到指标名称list
        
        this.getFormHM().put("testTemplatAdvance", testTemplatAdvance);
        this.getFormHM().put("channelList", channelList);
        this.getFormHM().put("modeList", modeList);
        this.getFormHM().put("testTemplateList", testTemplateList);
        this.getFormHM().put("itemList", itemList);
    }

}
