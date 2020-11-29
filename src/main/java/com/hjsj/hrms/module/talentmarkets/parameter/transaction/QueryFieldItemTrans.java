package com.hjsj.hrms.module.talentmarkets.parameter.transaction;

import com.hjsj.hrms.module.talentmarkets.parameter.businessobject.TalentMarketsParameterService;
import com.hjsj.hrms.module.talentmarkets.parameter.businessobject.impl.TalentMarketsParameterServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

/**
 * 
 *
 * @Titile QueryFieldItemTrans
 * @Description 查询指标项 竞聘岗位详情，个人简介，岗位类别等
 * @Company hjsj
 * @Create time: 2019年8月8日下午6:34:22
 * @Author wangdi
 * @version 1.0
 *
 */
public class QueryFieldItemTrans extends IBusiness {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws GeneralException {
        try {
            String type = (String) this.formHM.get("type");//确认指标集
            TalentMarketsParameterService talentMarketsParameterService = new TalentMarketsParameterServiceImpl(this.frameconn);
             List<LazyDynaBean> valueList = talentMarketsParameterService.queryFieldItem(type);
            this.formHM.put("data", valueList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
