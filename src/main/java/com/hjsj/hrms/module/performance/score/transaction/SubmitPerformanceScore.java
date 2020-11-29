package com.hjsj.hrms.module.performance.score.transaction;

import com.hjsj.hrms.module.performance.score.businessobject.KhScoreBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 提交评分明细
 *
 * @author ZhangHua
 * @date 15:31 2018/5/16
 */
public class SubmitPerformanceScore extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String model = (String) this.getFormHM().get("model");//模块ID 1:职称评审
        model = PubFunc.decrypt(SafeCode.decode(model));
        //考核计划标识 格式：模块ID_自定义内容例：职称评审格式设置为 模块ID_评审会议ID_环节ID
        String relation_Id = (String) this.getFormHM().get("relation_Id");
        relation_Id = PubFunc.decrypt(SafeCode.decode(relation_Id));
        String mainbody_Id = this.getUserView().getUserName();//考核主体ID
        ArrayList<String> submitPreList = (ArrayList<String>) this.getFormHM().get("submitPreList");//模板id

        ArrayList<String> object_List = (ArrayList<String>) this.getFormHM().get("object_List");//考核对象ID
        ArrayList<String> objectList = new ArrayList<String>();//考核对象ID
        for (String s : object_List) {
            objectList.add(PubFunc.decrypt(s));
        }


        String template_Id = (String) this.getFormHM().get("template_Id");//模板id
        template_Id = PubFunc.decrypt(template_Id);
        int modelId = 1;
        if (StringUtils.isNotBlank(model)) {
            modelId = Integer.parseInt(model);
        }

        KhScoreBo bo = new KhScoreBo(this.getFrameconn(), this.getUserView(), relation_Id, model, mainbody_Id);
        switch (modelId) {
            case 1: {//职称评审
                String W0301 = relation_Id.split("_")[1];
                String W0555 = relation_Id.split("_")[2];

                ArrayList<String> dataList = new ArrayList<String>();
                for (String str : submitPreList) {
                    dataList.add(PubFunc.decrypt(str));
                }

                bo.submitKhScore(W0301, W0555, template_Id, dataList,objectList);
            }
            ;
            break;
            default:
                return;
        }


    }
}
