package com.hjsj.hrms.utils.sys;

import com.hrms.struts.command.WFMapping;
import com.hrms.struts.command.WfunctionView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.BusinessProcess;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.TransInfoView;

import java.util.HashMap;

/**
 * 通过轮询获取异步交易类执行结果
 *
 * @author ZhangHua
 * @version V75
 * @date 13:39 2018/12/18
 */
public class PollingGetResultTrans extends IBusiness {

    /**
     * 前台通过 LRpc方法调用交易会进入此类
     * 首先执行异步交易调用实际传入的交易类号，并在userview中存入 以"GetResult_" + functionid做为key的一个map 做为传入交易类的执行完成情况，
     * 当交易开始时 map中的 status初始化为0 当交易执行完成 status设置为1  交易出错 status 设置为2
     *
     * 其次在前台LRpc方法会间隔执行总时长为1小时的轮询操作查询传入的交易类的执行完成情况，并返回执行结果参数。
     * @throws GeneralException
     */
    @Override
    public void execute() throws GeneralException {
        String type = (String) this.formHM.get("getResult_Type");
        String functionid = (String) this.formHM.get("getResult_Functionid");
        String key = "GetResult_" + functionid;
        HashMap dataMap = new HashMap();
        /**
         * init 首次进入
         */
        if ("init".equalsIgnoreCase(type)) {
            try {
                if (this.getUserView().getHm().containsKey(key)) {
                    dataMap = (HashMap) this.getUserView().getHm().get(key);
                    if ("0".equals(dataMap.get("status"))) {
                        return;
                    }
                }
                dataMap.put("status", "0");
                dataMap.put("errorMsg", "");
                dataMap.put("from", null);
                this.getUserView().getHm().put(key, dataMap);
                WFMapping wfm = new WFMapping();
                WfunctionView wfView = wfm.getFunctionView(functionid);
                TransInfoView transInfoView = new TransInfoView();
                transInfoView.setUserView(this.getUserView());
                transInfoView.setFormHM(this.getFormHM());
                transInfoView.setWfView(wfView);

                BusinessProcess bus = new BusinessProcess();
                bus.synJavaBeanExecute(transInfoView);

                dataMap.put("status", "1");
                dataMap.put("errorMsg", "");
                dataMap.put("from", this.getFormHM());
                this.getUserView().getHm().put(key, dataMap);
            } catch (Exception e) {
                e.printStackTrace();
                dataMap.put("status", "2");
                dataMap.put("errorMsg", ((GeneralException) e).getErrorDescription());
                dataMap.put("from", this.getFormHM());
                this.getUserView().getHm().put(key, dataMap);
            }
        }
        /**
         * search 前台轮询查询执行结果
         */
        else if ("search".equalsIgnoreCase(type)){
            try {
                if (this.getUserView().getHm().containsKey(key)) {
                    dataMap = (HashMap) this.getUserView().getHm().get(key);
                    this.getFormHM().put("getResult_status", dataMap.get("status"));
                    if ("1".equals(dataMap.get("status"))) {
                        if (dataMap.containsKey("from") && dataMap.get("from") != null) {
                            HashMap formMap = (HashMap) dataMap.get("from");
                            formMap.remove("getResult_Type");
                            formMap.remove("GetResult_Functionid");
                            this.getFormHM().putAll(formMap);
                            this.getUserView().getHm().remove(key);
                        }
                    } else if ("2".equals(dataMap.get("status"))) {
                        this.getUserView().getHm().remove(key);
                        throw GeneralExceptionHandler.Handle(new Exception((String) dataMap.get("errorMsg")));
                    }
                } else {
                    throw GeneralExceptionHandler.Handle(new Exception("出现未知错误，请重试！"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (this.getUserView().getHm().containsKey(key)) {
                    this.getUserView().getHm().remove(key);
                }
                throw GeneralExceptionHandler.Handle(e);
            }
        }
    }
}
