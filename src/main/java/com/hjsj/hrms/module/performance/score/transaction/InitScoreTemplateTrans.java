package com.hjsj.hrms.module.performance.score.transaction;

import com.hjsj.hrms.module.performance.score.businessobject.KhScoreBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

//import com.hjsj.hrms.module.performance.score.businessobject.KhScoreComputeBo;

/**
 * 初始化打分模板信息
 *
 * @author ZhangHua
 * @date 15:32 2018/5/16
 */
public class InitScoreTemplateTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String model = (String) this.getFormHM().get("model");//模块ID 1:职称评审
        model = PubFunc.decrypt(SafeCode.decode(model));
        //考核计划标识 格式：模块ID_自定义内容例：职称评审格式设置为 模块ID_评审会议ID_环节ID
        String relation_Id = (String) this.getFormHM().get("relation_Id");
        relation_Id = PubFunc.decrypt(SafeCode.decode(relation_Id));

        ArrayList<String> object_List = (ArrayList<String>) this.getFormHM().get("object_List");//考核对象ID
        String mainbody_Id = this.getUserView().getUserName();//考核主体ID
        //  mainbody_Id = PubFunc.decrypt(SafeCode.decode(mainbody_Id));

        KhScoreBo bo = new KhScoreBo(this.getFrameconn(), this.getUserView(), relation_Id, model, mainbody_Id);
        ArrayList<HashMap<String, String>> template_Info = bo.getTemplateInfo(object_List);
        ArrayList<HashMap<String, String>> emplate_Info_Encry = new ArrayList<HashMap<String, String>>();//加密的


        // KhScoreComputeBo computeBo=new KhScoreComputeBo(relation_Id,model,this.userView,this.getFrameconn(),mainbody_Id);
        // ArrayList<String> tempLateList= new ArrayList<String>();
        for (HashMap<String, String> map : template_Info) {
            //    tempLateList.add(map.get("template_Id"));
            map.put("template_Id", PubFunc.encrypt(map.get("template_Id")));
            emplate_Info_Encry.add(map);
        }
        //computeBo.computeFormula(tempLateList,object_List);

        this.getFormHM().put("template_Info", emplate_Info_Encry);

    }
}
