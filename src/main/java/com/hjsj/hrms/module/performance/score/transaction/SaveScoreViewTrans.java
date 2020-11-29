package com.hjsj.hrms.module.performance.score.transaction;

import com.hjsj.hrms.module.performance.score.businessobject.KhScoreBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;

/**
 * 保存打分明细
 *
 * @author ZhangHua
 * @date 15:31 2018/5/16
 */
public class SaveScoreViewTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String model = (String) this.getFormHM().get("model");//模块ID 1:职称评审
        model = PubFunc.decrypt(SafeCode.decode(model));
        //考核计划标识 格式：模块ID_自定义内容例：职称评审格式设置为 模块ID_评审会议ID_环节ID
        String relation_Id = (String) this.getFormHM().get("relation_Id");
        relation_Id = PubFunc.decrypt(SafeCode.decode(relation_Id));

        String object_Key = (String) this.getFormHM().get("objectKey");//考核对象ID
        object_Key = PubFunc.decrypt(object_Key);
        String mainbody_Id = this.getUserView().getUserName();//考核主体ID
        ArrayList<MorphDynaBean> dataList = (ArrayList<MorphDynaBean>) this.getFormHM().get("dataList");//模板id

        String template_Id = (String) this.getFormHM().get("template_Id");//模板id
        template_Id = PubFunc.decrypt(template_Id);

        KhScoreBo bo = new KhScoreBo(this.getFrameconn(), this.getUserView(), relation_Id, model, mainbody_Id);
        bo.saveKh_Score(bo.getMainbodyStatus(object_Key, template_Id), object_Key, dataList, template_Id);


    }
}
