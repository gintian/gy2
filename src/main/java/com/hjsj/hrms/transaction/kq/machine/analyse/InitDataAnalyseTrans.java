package com.hjsj.hrms.transaction.kq.machine.analyse;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.DataProcedureAnalyse;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 初始化数据处理
 *<p>Title:InitDataAnalyseTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 25, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class InitDataAnalyseTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
        	//判断数据处理是不是从刷卡数据点进来
        	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        	if (hm != null) {
				String src = (String) hm.get("src");
				if ("card".equals(src)) {
					hm.remove("src");
					this.getFormHM().put("start_date", null);
					this.getFormHM().put("end_date", null);
				}
			}
            String a_code = (String) this.getFormHM().get("a_code");
            KqCardData kqCardData = new KqCardData(this.userView, this.getFrameconn());
            if (a_code == null || a_code.length() <= 0) {
                //    		a_code=kqCardData.getACode();   //更改为 查询出当前组织权限 
                a_code = kqCardData.getACodefull();
            }
            String codeid = "";
            if (a_code != null && a_code.length() > 2) {
                codeid = a_code.substring(2);
            }
            ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");
            if (a_code.indexOf("UN") != -1) {
                kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), codeid);
            } else if (a_code.indexOf("UM") != -1 || a_code.indexOf("@K") != -1) {
                String b0110 = codeid;
                String codesetid = codeid;
                do {
                    String codeset[] = RegisterInitInfoData.getB0100(b0110, this.getFrameconn());
                    if (codeset != null && codeset.length >= 0) {
                        codesetid = codeset[0];
                        b0110 = codeset[1];
                    }
                } while (!"UN".equals(codesetid));
                kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(), b0110);
            } else if (a_code.indexOf("EP") != -1) {
                String nbase = (String) this.getFormHM().get("nbase");
                ArrayList list = new ArrayList();
                list.add(nbase);
                kq_dbase_list = list;
            } else {
                kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(), this.userView, this.getFrameconn());
            }
            if (kq_dbase_list == null || kq_dbase_list.size() <= 0)
                throw new GeneralException(ResourceFactory.getProperty("kq.nbase.no"));

            this.getFormHM().put("kq_dbase_list", kq_dbase_list);
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            ArrayList kqList = new ArrayList();
            kqList = kqUtilsClass.getKqNbaseListNullAll(kq_dbase_list);
            this.getFormHM().put("kq_list", kqList);

            KqParameter kq_paramter = new KqParameter(this.userView, this.userView.getUserOrgId(), this.getFrameconn());
            HashMap hashmap = kq_paramter.getKqParamterMap();
            String kq_type = (String) hashmap.get("kq_type");
            String kq_cardno = (String) hashmap.get("cardno");
            String analyseType = "";
            String mark = KqParam.getInstance().getData_processing();
            mark = mark != null && mark.length() > 0 ? mark : "0";
            if ("1".equalsIgnoreCase(mark)) {
                analyseType = "101";
            } else {
                analyseType = "1";
            }
            String kq_Gno = (String) hashmap.get("g_no");
            String dataUpdateType = "0";
            DataProcedureAnalyse dataProcedureAnalyse = new DataProcedureAnalyse(this.getFrameconn(), this.userView, analyseType,
                    kq_type, kq_cardno, kq_Gno, dataUpdateType, kq_dbase_list);
            dataProcedureAnalyse.initTempTable();
            String fAnalyseTempTab = dataProcedureAnalyse.getFAnalyseTempTab();
            String fExceptCardTab = dataProcedureAnalyse.getFExceptCardTab();
            String fTranOverTimeTab = dataProcedureAnalyse.getFTranOverTimeTab();
            String fBusiCompareTab = dataProcedureAnalyse.getFBusiCompareTab();
            String cardToOverTimeTab = dataProcedureAnalyse.getCardToOverTime();
            this.getFormHM().put("analyseTempTab", fAnalyseTempTab);//分析结果表
            this.getFormHM().put("exceptCardTab", fExceptCardTab);//异常刷卡
            this.getFormHM().put("tranOverTimeTab", fTranOverTimeTab);//延时加班
            this.getFormHM().put("busiCompareTab", fBusiCompareTab);//申请比对
            this.getFormHM().put("cardToOverTimeTab", cardToOverTimeTab);//休息日转加班
            this.getFormHM().put("kq_type", kq_type);
            this.getFormHM().put("kq_cardno", kq_cardno);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
}
