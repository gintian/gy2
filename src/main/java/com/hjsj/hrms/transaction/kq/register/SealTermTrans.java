package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.SealTerm;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SealTermTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            String kq_duration = (String) this.getFormHM().get("kq_duration");
            if (kq_duration == null || kq_duration.length() <= 0) {
                kq_duration = RegisterDate.getKqDuration(this.getFrameconn());
            }
            ArrayList datelist = RegisterDate.getKqDate(this.getFrameconn(), kq_duration);
            String start_date = datelist.get(0).toString();
            String end_date = datelist.get(datelist.size() - 1).toString();

            String pigeonhole_type = KqParam.getInstance().getArchiveType();
            if (pigeonhole_type != null && "1".equals(pigeonhole_type)) {
                this.getFormHM().put("notapp_list", "");
                this.getFormHM().put("notQ07_list", "");
                this.getFormHM().put("notQ09_list", "");
                this.getFormHM().put("notapptag", "seal");
                this.getFormHM().put("pigeonhole_type", "1");
            } else {
                this.getFormHM().put("pigeonhole_type", "0");
                
                SealTerm sealTerm = new SealTerm(this.getFrameconn(), this.userView);
                
                //如果Q03表中当前期间数据有未审批的，那么直接返回，后边就不用检查了
//                if (sealTerm.haveNoApprovedDataInQ03("", start_date, end_date, "", "")) {
//                    this.getFormHM().put("notapp_list", "have");
//                    this.getFormHM().put("notQ07_list", "");
//                    this.getFormHM().put("notQ09_list", "");
//                    this.getFormHM().put("notapptag", "seal");
//                    this.getFormHM().put("pigeonhole_type", "0");
//                    return;
//                }
                
                /***得到员工日明细没有审批通过的部门,里面是vo*****/
                ArrayList baseE0122list = getAllE0122(start_date, end_date);
                if (baseE0122list != null && baseE0122list.size() > 0) {
                    this.getFormHM().put("notapp_list", "have");
                    this.getFormHM().put("notapptag", "noseal");
                    return;
                }

                this.getFormHM().put("notapp_list", "");

                //是否有部门日明细没生成
                boolean notDailyCollect = sealTerm.haveNotDailyCollect(start_date, end_date);
                if (notDailyCollect) {
                    this.getFormHM().put("notQ07_list", "have");
                    this.getFormHM().put("notapptag", "noseal");
                } else {
                    this.getFormHM().put("notQ07_list", "");
                }
                
                //是否有部门月汇总没生成
                boolean notSumCollect = sealTerm.haveNotSumCollect(kq_duration, start_date, end_date);
                if(notSumCollect) {
                    this.getFormHM().put("notQ09_list", "have");
                    this.getFormHM().put("notapptag", "noseal");
                } else {
                    this.getFormHM().put("notQ09_list", "");
                }
                
                if (baseE0122list == null || baseE0122list.size() <= 0) {
                    if(!notDailyCollect) {
                        if (!notSumCollect) {
                            this.getFormHM().put("notapptag", "seal");
                        }
                    }
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList getAllE0122(String start_date, String end_date) throws GeneralException {
        SealTerm sealTerm = new SealTerm(this.getFrameconn(), this.userView);
        ArrayList list = new ArrayList();
        
        //zxj 2014.02.14 人员库不再分单位设置
        //ArrayList privDBList = userView.getPrivDbList();
        KqUtilsClass kqUtils = new KqUtilsClass(this.getFrameconn(), this.userView);
        ArrayList privDBList = kqUtils.getKqPreList();
        
        for (int i = 0; i < privDBList.size(); i++) {
            String userbase = privDBList.get(i).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, userbase);

            //ArrayList b0100list = sealTerm.getAllBaseOrgid(userbase, "b0110", whereIN, this.getFrameconn(), start_date, end_date);
            //for (int t = 0; t < b0100list.size(); t++) {
            //    String b0110_one = b0100list.get(t).toString();
//                String nbase = RegisterInitInfoData.getOneB0110Dase(this.getFormHM(), this.userView, userbase, b0110_one, 
//                        this.getFrameconn());
                /********按照该单位的人员库的操作*********/
                if (userbase != null && userbase.length() > 0) {
                    //有未审批的数据
                    if (sealTerm.haveNoApprovedDataInQ03(userbase, start_date, end_date, whereIN, "")) {
                        list.add("1");
                        break;
                    }
                    
                    ArrayList baseE0122list = null;
                    if (!userView.isSuper_admin())
                        baseE0122list = sealTerm.getPrivBaseE0122(userbase, "", start_date, end_date, whereIN);
                    else
                        baseE0122list = sealTerm.getPrivBaseE0122(userbase, "", start_date, end_date, "");

                    for (int r = 0; baseE0122list.size() > r; r++) {
                        list.add(baseE0122list.get(r));
                        
                        break;
                    }
                    
                    //只要发现一个未审批数据，就不用看别的单位了
                    if (0 < list.size())
                        break;
                }
            //}
            
            //只要发现一个未审批数据，就不用看别的单位了
//            if (0 < list.size())
//                break; 
        }
        return list;
    }
}
