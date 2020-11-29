package com.hjsj.hrms.service.syncdata;

public class SyncDataService {

    /**
     * 增量与全量后台作业调用
     *
     * @param xmlMessage
     * @return
     */
    public String sendSyncJobMsg(String xmlMessage) {
        String returnStr = "";
        try {
            SyncDataBridge syncdatabridge = new SyncDataBridge(xmlMessage);
            returnStr = syncdatabridge.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * 数据视图调用
     *
     * @param xmlMessage
     * @return
     */
    public String sendSyncMsg(String xmlMessage) {
        StringBuffer returnStr = new StringBuffer();
        try {
            SyncDataThread qyThread = new SyncDataThread(xmlMessage);
            qyThread.start();

            returnStr.append("<?xml version='1.0' encoding='GB2312'?>");
            returnStr.append("<msg>");
            returnStr.append("数据同步已开启后台进行中，可关闭此提示框，刷新列表可显示记录同步状态，也可在log4j日志文件查看具体同步过程");
            returnStr.append("</msg>");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnStr.toString();
    }
}
