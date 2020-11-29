package com.hjsj.hrms.utils.components.complexcondition.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.complexcondition.businessobject.ComplexConditionBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * 项目名称 ：ehr7.x
 * 类名称：GetFormulaOrHeapFlagTrans
 * 类描述：得到初始化数据
 * 创建人： lis
 * 创建时间：2015-11-6
 */
public class GetInitDataTrans extends IBusiness {

    public void execute() throws GeneralException {
        ArrayList<CommonData> list = new ArrayList<CommonData>();
        try {
            String flag = (String) this.getFormHM().get("flag");  //  1:  子标集,  2: 子标,  3: 代码 , 0:累计方式
            String imodule = (String) this.getFormHM().get("imodule");//3:薪资类别；
            String opt = (String) this.getFormHM().get("opt");//0:薪资类别-薪资项目导入项和累计项 1:薪资类别=薪资属性-复杂条件
            //下拉列表需要查询哪些子集数据，以逗号分隔，可以只写A 或者K 例如 A01,K01,K 兼容旧代码 为""时查全部
            String inforKindFlag = (String) this.getFormHM().get("inforKindFlag");
            ComplexConditionBo bo = new ComplexConditionBo(this.getFrameconn(), this.userView);

            String itemSet = (String) this.getFormHM().get("value");
            String itemid = (String) this.getFormHM().get("value");

            ArrayList<CommonData> fieldItemList = new ArrayList<CommonData>();
            ArrayList<CommonData> codeItemList = new ArrayList<CommonData>();

            String fieldsetid = "";
            if ("3".equals(imodule) && "0".equals(opt)) {
                String formual = "";
                String initflag = (String) this.getFormHM().get("initflag");//1:积累项，2：导入项
                if (StringUtils.isBlank(flag)) {//初始化数据
                    String fieldid = (String) this.getFormHM().get("fieldid");//薪资项目id
                    String salaryid = (String) this.getFormHM().get("salaryid");//薪资类别id
                    salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
                    ContentDAO dao = new ContentDAO(this.getFrameconn());
                    RecordVo vo = new RecordVo("salaryset");
                    vo.setInt("salaryid", Integer.parseInt(salaryid));
                    vo.setInt("fieldid", Integer.parseInt(fieldid));
                    vo = dao.findByPrimaryKey(vo);
                    formual = vo.getString("formula") != null ? vo.getString("formula") : vo.getString("itemdesc");
                    String heapFlag = StringUtils.isNotBlank(vo.getString("heapflag")) ? vo.getString("heapflag") : "0";

                    this.getFormHM().put("formula", SafeCode.encode(formual));
                    this.getFormHM().put("heapFlag", heapFlag);
                    this.getFormHM().put("itemtype", vo.getString("itemtype"));

                    if ("2".equals(initflag)) {//导入项
                        ArrayList<CommonData> fieldSetList = bo.getfieldSetList("");//子集列表
                        fieldsetid = fieldSetList.get(0).getDataValue();
                        this.getFormHM().put("fieldSetList", fieldSetList);
                    } else if ("1".equals(initflag)) {
                        fieldsetid = vo.getString("fieldsetid");
                    }

                    if (StringUtils.isNotBlank(fieldsetid)) {
                        fieldItemList = bo.getfieldItemList(fieldsetid);//指标列表
                    }

                    if (fieldItemList.size() > 0) {
                        codeItemList = bo.getcodeItemList(fieldItemList.get(0).getDataValue());//代码列表
                    }
                    this.getFormHM().put("fieldItemList", fieldItemList);
                    this.getFormHM().put("codeItemList", codeItemList);

                } else if ("1".equals(flag)) {
                    list = bo.getfieldItemList(itemSet);
                } else if ("2".equals(flag)) {
                    list = bo.getcodeItemList(itemid);
                }
                this.getFormHM().put("list", list);

                return;
            }


            if (StringUtils.isBlank(flag)) {
                ArrayList<CommonData> fieldSetList = bo.getfieldSetList(inforKindFlag);//子集列表
                fieldsetid = fieldSetList.get(0).getDataValue();
                if (StringUtils.isNotBlank(fieldsetid)) {
                    fieldItemList = bo.getfieldItemList(fieldsetid);//指标列表
                }
                this.getFormHM().put("fieldSetList", fieldSetList);
                this.getFormHM().put("fieldItemList", fieldItemList);
            }
            if ("1".equals(flag)) {
                list = bo.getfieldItemList(itemSet);
            } else if ("2".equals(flag)) {
                list = bo.getcodeItemList(itemid);
            }
            this.getFormHM().put("list", list);


        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
