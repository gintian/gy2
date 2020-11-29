package com.hjsj.hrms.module.recruitment.exammanage.examhall.transaction;

import com.hjsj.hrms.module.recruitment.exammanage.examhall.businessobject.ExamHallBo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title: ExamHallTrans
 * Description:考场管理-增改 操作
 * Company: hjsj
 * create time: 2015-11-4 下午5:44:01
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class ExamHallTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            HashMap hm = this.getFormHM();
            /**
             * type 区分 affiliation-班次下拉数据获取 
             * add - 添加数据 
             * edit - 修改数据
             * update - 修改数据 
             * delete - 删除数据
             * beforedit - 修改原数据
             * query - 添加页面默认时间
             * examId - 判断同一批次是否存在同一考场号
             * examEditRight - 判断是否有删除、修改权限
             */
            ExamHallBo exbo = new ExamHallBo(this.frameconn, this.userView);
            String type = (String) hm.get("type");
            if (StringUtils.isEmpty(type))
                return;
            // 班次下拉数据获取
            if ("affiliation".equals(type)) {
                ArrayList affiliationList = new ArrayList();
                affiliationList = exbo.getAffiliationList();
                this.getFormHM().put("data", affiliationList);
            }

            // 数据添加
            if ("add".equals(type) || "edit".equals(type)) {
                String tip = "";
                String affiliation = (String) hm.get("affiliation");
                String examNumber = (String) hm.get("examNumber");
                String examName = (String) hm.get("examName");
                String situs = (String) hm.get("situs");
                String examDate = (String) hm.get("examDate");
                int sitNumber = Integer.parseInt((String) hm.get("sitNumber"));
                String startTime = (String) hm.get("startTime");
                String endTime = (String) hm.get("endTime");
                String b0110 = (String) hm.get("b0110");
                b0110 = b0110.substring(0, b0110.indexOf("`"));

                ArrayList addList = new ArrayList();
                addList.add(examNumber);
                addList.add(examName);
                addList.add(affiliation);
                addList.add(situs);
                addList.add(sitNumber);
                if(StringUtils.isNotEmpty(examDate)){
                    java.util.Date src_d = DateUtils.getDate(examDate, "yyyy-MM-dd");
                    java.sql.Date d = new java.sql.Date(src_d.getTime());
                    addList.add(d);
                }else
                    addList.add(null);
                addList.add(startTime + "-" + endTime);
                addList.add(b0110);

                if ("edit".equals(type)) {
                    String id = (String) hm.get("id");
                    addList.add(id);
                    tip = exbo.updateExamHall(addList);
                } else {
                    tip = exbo.addExamHall(addList);
                }
                this.getFormHM().put("tip", tip);
            }

            // 编辑前获取数据
            if ("beforedit".equals(type)) {
                String id = (String) hm.get("id");
                ArrayList list = exbo.editList(id);
                this.getFormHM().put("dataList", list);
            }

            // 进入添加页面时，添加默认时间数据
            if ("query".equals(type)) {
                ArrayList list = exbo.query();
                this.getFormHM().put("dataList", list);
            }

            // 判断此班次下是否存在此考场号,并且判断座位数与人数比较
            if ("examId".equals(type)) {
                String affiliation = (String) hm.get("affiliation");
                String examNumber = (String) hm.get("examNumber");
                String id = (String) hm.get("id");
                String sitNumber = (String) hm.get("sitNumber");
                ArrayList list = exbo.checkIdPeole(affiliation, examNumber, id, sitNumber);
                if(list.size()==0){
                	list.add("0");
                	list.add("0");
                }
                this.getFormHM().put("isExis", list.get(0));
                this.getFormHM().put("ple", list.get(1));
            }
            
            //判断是否有修改或删除权限
            if ("examEditRight".equals(type)) {
                ArrayList list = new ArrayList();
                String isEditOrDele = (String) hm.get("isEditOrDele");
                HashMap b = exbo.examEditRight((String) hm.get("id"), isEditOrDele);
                this.getFormHM().put("examEditRight", b.get("tip").toString());
                if ("dele".equals(isEditOrDele)) {
                    this.getFormHM().put("isExistStudent", b.get("isExistStudent").toString());
                    if ("0".equals(b.get("tip"))) {
                        this.getFormHM().put("tipNames", b.get("tipNames").toString());
                        this.getFormHM().put("canBeDeleIds", b.get("canBeDeleIds").toString());
                    } else {
                        this.getFormHM().put("tipNames","");
                        this.getFormHM().put("canBeDeleIds","");
                    }
                }
            }
            this.getFormHM().remove("type");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
