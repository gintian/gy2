package com.hjsj.hrms.module.talentmarkets.competition.transaction;

import com.hjsj.hrms.module.talentmarkets.competition.businessobject.CompetitionService;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @Title GetPsnFieldSetTrans
 * @Description 栏目设置 获取人员信息集指标
 * @Company hjsj
 * @Author wangbs
 * @Date 2019/8/8
 * @Version 1.0.0
 */
public class GetPsnFieldSetTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        try {
            String fieldset = (String) this.getFormHM().get("node");
            ArrayList list;
            String setList = "A`Y:Z83";
            if("root".equals(fieldset)){
                list = TalentMarketsUtils.getFieldSetList(setList, this.getFrameconn(), this.userView);
            }else{
                String hiddenFields = CompetitionService.HIDDEN_FIELDS;
                if (!TalentMarketsUtils.getOpenInterview(this.frameconn)) {
                    hiddenFields = hiddenFields + ",Z83:Z8307";
                }
                list = TalentMarketsUtils.getFieldItemList(fieldset, this.getFrameconn(), hiddenFields, this.userView);
            }
            this.formHM.put("children", list);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
