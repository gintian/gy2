package com.hjsj.hrms.businessobject.sys.warn;

import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hjsj.hrms.transaction.sys.warn.ScanTrans;
import org.apache.log4j.Category;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * 由 Timer 安排为一次执行或重复执行的任务。
 *
 * @author Owner
 */
public class WarnScanTimerTask extends TimerTask implements IConstant {

    /**
     * 时间间隔，设置为10分钟
     */
    private int frequency = 10;
    private static Category log = Category.getInstance(WarnScanTimerTask.class.getName());

    private void log(String strMessage) {
        ContextTools.getContext().log(strMessage);
    }

    public WarnScanTimerTask(String frequency) {
        super();
        if (frequency == null || "".equals(frequency)) {
            this.frequency = 10;
        } else {
            this.frequency = Integer.parseInt(frequency);
        }
    }

    /**
     * 此计时器任务要执行的操作
     */
    @Override
    public synchronized void run() {
    	//任务调度开始
        ScanTrans.doScan(frequency);
    }
}
