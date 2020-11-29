package com.hjsj.hrms.module.gz.standard.standard.transaction;

import com.hjsj.hrms.module.gz.standard.standard.businessobject.IStandTableService;
import com.hjsj.hrms.module.gz.standard.standard.businessobject.impl.StandTableServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @Description 二级指标表达式保存交易类
 * @Author wangz
 * @param item 指标名称
 * @param item_id 指标序号
 * @param description 指标表述
 * @param type 指标类型
 * @param lowerValue 低端数据值
 * @param lowerOperate 低端操作符值
 * @param heightValue 高端数据值
 * @param heightOperate 高端操作符值
 * @param middleValue 中端数据值,只有日期型指标传递此值
 * @param isAccuratelyDay 是否精确到天,只有日期型使用此值
 * @Date 2019/12/3 11:58
 * @Version V1.0
 **/
public class SaveItemLexprTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String return_code = "success";
        try {
            String item=(String)this.getFormHM().get("item");
            String item_id=(String)this.getFormHM().get("item_id");
            String description=(String)this.getFormHM().get("description");
            String type=(String)this.getFormHM().get("type");   
            String lowerValue=(String)this.getFormHM().get("lowerValue");
            String lowerOperate=PubFunc.keyWord_reback((String)this.getFormHM().get("lowerOperate"));
            String heightValue=(String)this.getFormHM().get("heightValue");
            String heightOperate=PubFunc.keyWord_reback((String)this.getFormHM().get("heightOperate"));
            String middleValue="";
            if("D".equals(type))
                middleValue=(String)this.getFormHM().get("middleValue");
            String isAccuratelyDay=String.valueOf(this.getFormHM().get("isAccuratelyDay")); 
            
            HashMap itemInfor = new HashMap();
            itemInfor.put("item", item);
            itemInfor.put("item_id", item_id);
            itemInfor.put("description", description);
            itemInfor.put("type", type);
            itemInfor.put("lowerValue", lowerValue);
            itemInfor.put("lowerOperate", lowerOperate);
            itemInfor.put("heightValue", heightValue);
            itemInfor.put("heightOperate", heightOperate);
            itemInfor.put("middleValue", middleValue);
            itemInfor.put("isAccuratelyDay", isAccuratelyDay);
            
            IStandTableService saveItemLexprServer = new StandTableServiceImpl(this.frameconn,this.userView);
            return_code = saveItemLexprServer.saveItemLexpr(itemInfor);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return_code = "fail";
            this.getFormHM().put("return_msg",e);
        }
        this.getFormHM().put("return_code",return_code); 

    }
}
