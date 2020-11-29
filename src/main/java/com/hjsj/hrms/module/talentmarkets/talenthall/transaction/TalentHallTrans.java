package com.hjsj.hrms.module.talentmarkets.talenthall.transaction;


import com.hjsj.hrms.module.talentmarkets.talenthall.businessobject.TalentHallService;
import com.hjsj.hrms.module.talentmarkets.talenthall.businessobject.impl.TalentHallServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 人才展厅首页面交易类
 * @Author wangz
 * @Date 2019/10/9 16:21
 * @Version V1.0
 **/
public class TalentHallTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        //操作类型
        String operateType = (String) this.getFormHM().get("operateType");
        Map returnData = new HashMap();
        String return_code = "success";
        //空 表示无错误
        String return_msg_code = "";
        TalentHallService talentHallService = new TalentHallServiceImpl(this.userView,this.frameconn);
        try {
            if(StringUtils.equalsIgnoreCase(operateType,TalentHallService.OPERATE_TYPE_QUERY_DATA)){
                String pageStr = (String) this.getFormHM().get("page");
                int limit = Integer.parseInt((String) this.getFormHM().get("limit"));
                if (pageStr == null)
                    pageStr = "1";
                int page = Integer.parseInt(pageStr);
                page = page < 1 ? 1 : page;
                MorphDynaBean queryValues = (MorphDynaBean) this.getFormHM().get("queryValues");
                String orderyType = (String) this.getFormHM().get("orderValue");
                Map data = talentHallService.getData(page,limit,queryValues,orderyType);
                int totalCount = (Integer) data.get("totalCount");
                List dataList = (List)data.get("dataList");
                this.getFormHM().put("totalCount", totalCount);
                this.getFormHM().put("dataobjs", dataList);
            }else if(StringUtils.equalsIgnoreCase(operateType,TalentHallService.OPERATE_TYPE_CHANGE_RESUME_STATUS)){
                String guidkey = (String) this.getFormHM().get("guidkey");
                String opt = (String)this.getFormHM().get("opt");
                talentHallService.changeResumeStatus(guidkey,opt);
            }else if(StringUtils.equalsIgnoreCase(operateType,TalentHallService.OPERATE_TYPE_CHANGE_ATTENTION_STATUS)){
                String z8501 = (String) this.getFormHM().get("z8501");
                String attention = (String)this.getFormHM().get("attention");
                talentHallService.changeAttentionStatus(z8501,attention);
            }else if(StringUtils.equalsIgnoreCase(operateType,TalentHallService.OPERATE_TYPE_CHANGE_APPROVAL_STATUS)){
                String z8501 = (String) this.getFormHM().get("z8501");
                String approval = (String)this.getFormHM().get("approval");
                talentHallService.changeApprovalStatus(z8501,approval);
            }else if(StringUtils.equalsIgnoreCase(operateType,TalentHallService.OPERATE_TYPE_INIT_PARAM)){
                Map paramData = talentHallService.getInitParam();
                returnData.put("initParam",paramData);
            }else if(StringUtils.equalsIgnoreCase(operateType,TalentHallService.OPERATE_TYPE_GET_GRIDCONFIG)){
                String viewType = (String) this.getFormHM().get("viewType");
                String z8501 = (String) this.getFormHM().get("z8501");
                String gridConfig = talentHallService.getGridConfig(viewType,z8501);
                returnData.put("gridConfig",gridConfig);
            }else if(StringUtils.equalsIgnoreCase(operateType,TalentHallService.OPERATE_TYPE_VIEW_COUNT)){
                String z8501 = (String) this.getFormHM().get("z8501");
                talentHallService.changeViewCount(z8501);
            }

        } catch (GeneralException e) {
            return_code = "fail";
            return_msg_code = e.getErrorDescription();
            e.printStackTrace();
        }
        this.getFormHM().put("return_code", return_code);
        this.getFormHM().put("return_data", returnData);
        this.getFormHM().put("return_msg_code", return_msg_code);
    }
}
