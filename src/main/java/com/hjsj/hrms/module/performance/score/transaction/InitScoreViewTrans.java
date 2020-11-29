package com.hjsj.hrms.module.performance.score.transaction;

import com.hjsj.hrms.module.performance.score.businessobject.KhScoreBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.*;

/**
 * 职称评审评分主界面显示
 *
 * @author ZhangHua
 * @date 15:58 2018/4/10
 */

public class InitScoreViewTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        String model = (String) this.getFormHM().get("model");//模块ID 1:职称评审
        model = PubFunc.decrypt(SafeCode.decode(model));
        //考核计划标识 格式：模块ID_自定义内容例：职称评审格式设置为 模块ID_评审会议ID_环节ID
        String relation_Id = (String) this.getFormHM().get("relation_Id");
        relation_Id = PubFunc.decrypt(SafeCode.decode(relation_Id));

        String object_Id = (String) this.getFormHM().get("object_Id");//考核对象ID
        object_Id = PubFunc.decrypt(object_Id);
        String mainbody_Id = this.getUserView().getUserName();//考核主体ID

        String template_Id = (String) this.getFormHM().get("template_Id");//模板id
        template_Id = PubFunc.decrypt(template_Id);

        KhScoreBo bo = new KhScoreBo(this.frameconn, this.getUserView(), relation_Id, model, mainbody_Id);
        HashMap<String, String> mainbodyStatusMap = bo.getMainbodyStatus(object_Id, template_Id);//获取mainbody信息

        LinkedHashMap<String, LazyDynaBean> templateMap = bo.getTemplateMap(template_Id, mainbodyStatusMap);//获取模板数据


        this.getFormHM().put("data", this.buildScoreTree(templateMap));//拼接模板tree

    }

    /**
     * 拼接前台用的树结构
     * @param templateMap
     * templateMap 结构 略复杂。。说明一下
     *  'template_Item_Map':LinkedHashMap<String, LazyDynaBean>
     *      key: 模板项目id per_template_item.item_id
     *      value:  LazyDynaBean ['namePath':String 节点导航图，
     *                            'template_Item_Map': HashMap<String, HashMap<String, String>> 存放模板要素
     *                                   key要素id
     *                                   value:HashMap<String, String> 存放要素属性
     *                                      'pointname':要素名称
     *                                      'totalScore':标准分值
     *                                      'Score':分值
     *                                      'description':要素解释
     *
     * @author ZhangHua
     * @date 16:22 2018/4/19
     */
    public ArrayList<HashMap<String, Object>> buildScoreTree(LinkedHashMap<String, LazyDynaBean> templateMap) {
        ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
        try {

            HashMap<String, Object> rootMap, childBean;
            Iterator iter = templateMap.entrySet().iterator();

            while (iter.hasNext()) {//遍历所有的根节点
                Map.Entry entry = (Map.Entry) iter.next();
                LazyDynaBean val = (LazyDynaBean) entry.getValue();
                HashMap<String, HashMap<String, String>> childMap = (HashMap<String, HashMap<String, String>>) val.get("template_Item_Map");
                ArrayList<HashMap<String, Object>> childList = new ArrayList<HashMap<String, Object>>();
                if (childMap.size() > 0) {
                    Iterator iterchild = childMap.entrySet().iterator();
                    while (iterchild.hasNext()) {//遍历当前根节点所有子节点
                        Map.Entry entry1 = (Map.Entry) iterchild.next();
                        String key = (String) entry1.getKey();
                        childBean = new HashMap<String, Object>();
                        HashMap<String, String> tempMap = (HashMap<String, String>) entry1.getValue();//获取要素属性
                        childBean.put("id", key);//模板要素Point_id
                        childBean.put("text", tempMap.get("pointname"));//要素名称
                        childBean.put("totalScore", tempMap.get("totalScore"));//标准分值
                        childBean.put("Score", tempMap.containsKey("Score") ? tempMap.get("Score") : "");//分值
                        childBean.put("scoreMode", tempMap.containsKey("scoreMode") ? tempMap.get("scoreMode") : "0");//打分方式 0 需要打分 1不需要

                        childBean.put("description", tempMap.get("description").replaceAll("/r", "<br>"));//要素解释
                        childBean.put("leaf", true);
                        childList.add(childBean);
                    }
                }
                if (childList.size() == 0) {
                    continue;
                }
                rootMap = new HashMap<String, Object>();
                rootMap.put("text", val.get("namePath"));//插入导航图
                rootMap.put("children", childList);//插入子节点集
                dataList.add(rootMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
