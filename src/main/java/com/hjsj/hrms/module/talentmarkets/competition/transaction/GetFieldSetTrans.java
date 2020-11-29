package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @Description 岗位列表 栏目设置 新增指标交易类
 * @Author wangz
 * @Date 2019/8/6 14:13
 * @Version V1.0
 **/
public class GetFieldSetTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {
            String fieldset = (String) this.getFormHM().get("node");
            ArrayList list = new ArrayList();
            String setList = "K`Y:Z81";
            if("root".equals(fieldset)){
                list = TalentMarketsUtils.getFieldSetList(setList, this.getFrameconn(),this.userView);
            }else{
                list = TalentMarketsUtils.getFieldItemList(fieldset, this.getFrameconn(), "z81:z8101",this.userView);
            }
            this.formHM.put("children", list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
