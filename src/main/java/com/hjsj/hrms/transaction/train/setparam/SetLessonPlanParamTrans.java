package com.hjsj.hrms.transaction.train.setparam;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.train.trainexam.exam.TrainExamPlanBo;
import com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class SetLessonPlanParamTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String opt = "";
        if(hm!=null){
            opt = (String)hm.get("opt");
            hm.remove("opt");
        }
        ConstantXml constantbo = new ConstantXml(this.getFrameconn(),"TR_PARAM");
        if("set".equals(opt)){
            TrainExamPlanBo planBo = new TrainExamPlanBo(this.frameconn);
            ArrayList itemlist = planBo.getMessageTmpList(null);        
            this.getFormHM().put("itemlist", itemlist);
            
            String mail = constantbo.getNodeAttributeValue("/param/lesson_hint", "mail");
            String sms = constantbo.getNodeAttributeValue("/param/lesson_hint", "sms");
            String weixin = constantbo.getNodeAttributeValue("/param/lesson_hint", "weixin");
            String template = constantbo.getNodeAttributeValue("/param/lesson_hint", "template");
            String enable_arch = constantbo.getNodeAttributeValue("/param/lesson_hint", "enable_arch");
            String disable_exam_learning = constantbo.getNodeAttributeValue("/param/lesson_hint", "disable_exam_learning");
            String dingTalk = constantbo.getNodeAttributeValue("/param/lesson_hint", "dingTalk");
            MyTestBo bo = new MyTestBo(this.frameconn);
            String flag = bo.isExistPro()+"";
            
            String speed = constantbo.getNodeAttributeValue("/param/lesson_hint", "speed");
            String viewname = constantbo.getNodeAttributeValue("/param/lesson_hint", "viewItems");
            viewname=viewname!=null&&viewname.trim().length()>3?viewname:"";
            
            ArrayList selectlist = new ArrayList();
            ArrayList fieldItemList = new ArrayList();
            ArrayList list=DataDictionary.getFieldList("r50",Constant.USED_FIELD_SET);
            for(int i=0;i<list.size();i++){
                boolean b=true;
                FieldItem item=(FieldItem)list.get(i);
                if("R5000".equalsIgnoreCase(item.getItemid()))
                    continue;
                else if("R5003".equalsIgnoreCase(item.getItemid()))
                    continue;
                else if("R5004".equalsIgnoreCase(item.getItemid()))
                    continue;
                else if("R5030".equalsIgnoreCase(item.getItemid()))
                    continue;
                else if("R5031".equalsIgnoreCase(item.getItemid()))
                    continue;
                
                String[] viewnames = viewname.split(",");
                if (viewnames.length > 0) {
                    for (int j = 0; j < viewnames.length; j++) {
                        if (item.getItemid().equalsIgnoreCase(viewnames[j])) 
                            b=false;
                    }
                }
                
                if(b){
                    CommonData dataobj = new CommonData(item.getItemid().toUpperCase(),item.getItemdesc());
                    fieldItemList.add(dataobj);
                }
            }
            
            boolean msg = true;
            String[] itemarr = viewname.split(",");
            for(int i=0;i<itemarr.length;i++){
                if("SCORE".equalsIgnoreCase(itemarr[i])) {
                    msg = false;
                    CommonData dataobj = new CommonData(itemarr[i],"成绩"); 
                    selectlist.add(dataobj);
                } else {
                    FieldItem item = DataDictionary.getFieldItem(itemarr[i]);
                    if(item!=null){
                        if("0".equals(item.getUseflag()))
                            continue;
                        
                        String itemid = item.getItemid().toUpperCase();
                        String itemdesc = item.getItemdesc().toUpperCase();
                        if("R5000".equalsIgnoreCase(item.getItemid()))
                            continue;
                        else if("R5003".equalsIgnoreCase(item.getItemid()))
                            continue;
                        else if("R5004".equalsIgnoreCase(item.getItemid()))
                            continue;
                        else if("R5030".equalsIgnoreCase(item.getItemid()))
                            continue;
                        else if("R5031".equalsIgnoreCase(item.getItemid()))
                            continue;
                        
                        CommonData dataobj = new CommonData(itemid,itemdesc); 
                        selectlist.add(dataobj);
                    }
                }
            }
            
            if(msg) {
                CommonData data = new CommonData("SCORE","成绩");
                fieldItemList.add(data);
            }
            
            this.getFormHM().put("enable_arch", enable_arch);
            this.getFormHM().put("disable_exam_learning", disable_exam_learning);
            this.getFormHM().put("mail", mail);
            this.getFormHM().put("sms", sms);
            this.getFormHM().put("weixin", weixin);
            this.getFormHM().put("template", template);
            this.getFormHM().put("existPro", flag);
            this.getFormHM().put("speed", speed);
            this.getFormHM().put("viewItemList", selectlist);
            this.getFormHM().put("fieldItemList", fieldItemList);
            this.getFormHM().put("dingTalk", dingTalk);
        }else{
            String mail = (String)this.getFormHM().get("mail");
            String sms = (String)this.getFormHM().get("sms");
            String weixin = (String) this.getFormHM().get("weixin");
            String template = (String)this.getFormHM().get("template");
            String enable_arch = (String)this.getFormHM().get("enable_arch");
            String disable_exam_learning = (String)this.getFormHM().get("disable_exam_learning");
            String speed = (String)this.getFormHM().get("speed");
            String selectItems = (String)this.getFormHM().get("viewItems");
            selectItems = StringUtils.isEmpty(selectItems) ? "" : selectItems;
            if(selectItems.endsWith(","))
                selectItems = selectItems.substring(0, selectItems.length() - 1);
            
            String dingTalk = (String)this.getFormHM().get("dingTalk");
            constantbo.setAttributeValue("/param/lesson_hint", "mail", mail);
            constantbo.setAttributeValue("/param/lesson_hint", "sms", sms);
            constantbo.setAttributeValue("/param/lesson_hint", "weixin", weixin);
            constantbo.setAttributeValue("/param/lesson_hint", "template", template);
            constantbo.setAttributeValue("/param/lesson_hint", "enable_arch", enable_arch);
            constantbo.setAttributeValue("/param/lesson_hint", "disable_exam_learning", disable_exam_learning);
            constantbo.setAttributeValue("/param/lesson_hint", "speed", speed);
            constantbo.setAttributeValue("/param/lesson_hint", "viewItems", selectItems);
            constantbo.setAttributeValue("/param/lesson_hint", "dingTalk", dingTalk);
            constantbo.saveStrValue();
            this.getFormHM().put("flag", "ok");
        }
        
        //设置休息时间
        if("set".equals(opt)){
            String restTime = constantbo.getNodeAttributeValue("/param/rest_hint", "interval");
            this.getFormHM().put("restTime", restTime);
        }else{
            String time = this.getFormHM().get("time").toString();
            constantbo.setAttributeValue("/param/rest_hint", "interval", time);
            constantbo.saveStrValue();
            this.getFormHM().put("flag", "ok");
        }
    }
}
