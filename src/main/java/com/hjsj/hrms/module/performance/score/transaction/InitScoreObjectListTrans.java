package com.hjsj.hrms.module.performance.score.transaction;


import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.module.performance.score.businessobject.KhScoreBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 初始化评分人员列表
 *
 * @author ZhangHua
 * @date 15:35 2018/5/16
 */
public class InitScoreObjectListTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String model = (String) this.getFormHM().get("model");//模块ID 1:职称评审
        model = PubFunc.decrypt(SafeCode.decode(model));
        //考核计划标识 格式：模块ID_自定义内容例：职称评审格式设置为 模块ID_评审会议ID_环节ID
        String relation_Id = (String) this.getFormHM().get("relation_Id");
        relation_Id = PubFunc.decrypt(SafeCode.decode(relation_Id));

        ArrayList object_List = (ArrayList) this.getFormHM().get("object_List");//考核对象ID
        String mainbody_Id = this.getUserView().getUserName();//考核主体ID
        //  mainbody_Id = PubFunc.decrypt(SafeCode.decode(mainbody_Id));

        String template_Id = (String) this.getFormHM().get("template_Id");//模板id
        template_Id = PubFunc.decrypt(template_Id);

        KhScoreBo bo = new KhScoreBo(this.getFrameconn(), this.getUserView(), relation_Id, model, mainbody_Id);
        ArrayList<HashMap<String, String>> objectInfo = bo.getObjectListInfo(object_List, template_Id);
        ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String, String>>();
        int pointNum=bo.getPointNum(template_Id);
        HashMap<String,String> scoreMap=bo.getMemberScoreNum(template_Id);
        PhotoImgBo imgBo = new PhotoImgBo(this.getFrameconn());
        for (HashMap<String, String> map : objectInfo) {
            String imgPath = imgBo.getPhotoPathLowQuality("Usr", map.get("object_id"));
            map.put("scorePointNum",scoreMap.containsKey(map.get("objectKey"))?scoreMap.get(map.get("objectKey")):"0");
            map.put("imgPath", imgPath);
            map.put("objectKey", PubFunc.encrypt(map.get("objectKey")));
            map.put("object_id", PubFunc.encrypt(map.get("object_id")));
            String e0122 = map.get("e0122"), b0110 = map.get("b0110");
            if (StringUtils.isNotBlank(e0122)) {
                e0122 = AdminCode.getCodeName("UM", e0122);
            }
            map.put("depName", e0122);
            map.put("pointNum",String.valueOf(pointNum));
            if(Float.parseFloat(map.get("score"))==0f){
                list.add(map);
            }
        }
        objectInfo.removeAll(list);
        for (int i = 0; i < object_List.size(); i++) {
            for (HashMap<String, String> map : list) {
                if(String.valueOf(object_List.get(i)).equalsIgnoreCase(map.get("object_id"))){
                    objectInfo.add(map);
                }
            }
        }


        this.getFormHM().put("data", objectInfo);

    }
}
