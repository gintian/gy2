package com.hjsj.hrms.businessobject.kq.set;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.NumberUtils;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;

import java.sql.Connection;
import java.util.Map;

/**
 * <p>Title:TurnOvertime.java</p>
 * <p>Description:休息日转加班 </p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 20, 2012 2:50:58 PM</p>
 * @author 郑文龙
 * @version 1.0
 */
public class TurnOvertime {

    private String     enable  = "";  //启用标识 0 不启用 1 启用

    private String     charge  = "";  //0 需要进出匹配 1 有刷卡即加班

    private String     tlong   = "";  //刷卡时长  2 默认时长 1 参考班次时长 0 实际刷卡时长

    private String     time    = "";  //刷卡时长

    private String     classid = "";  //被选班次

    private String     appdoc  = "";  //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认

    private ContentDAO dao     = null;

    public TurnOvertime() {
        init();//初始化获得参数
    }

    /**
     * 初始化 获取设置参数值
     * @return
     */
    private boolean init() {
        try {
            KqParam kqParam = KqParam.getInstance();

            this.enable = kqParam.getRestToOvertime();

            //0 需要进出匹配 1 有刷卡即加班
            this.charge = kqParam.getRestToOvertimeCard();

            //刷卡时长  2 默认时长 1 参考班次时长 0 实际刷卡时长
            String restToOvertimeCard = kqParam.getRestToOvertimeTimelen();
            if (restToOvertimeCard.indexOf(":") == -1) {//0 实际刷卡时长
                this.tlong = restToOvertimeCard;
            } else {//2 默认时长 1 参考班次时长
                String _long[] = restToOvertimeCard.split(":");
                if (_long.length == 2) {//处理错误
                    this.tlong = _long[0];
                    if ("1".equals(this.tlong)) {//1 参考班次时长
                        this.classid = _long[1];//被选班次
                    } else if ("2".equals(this.tlong)) {//2 默认时长
                        this.time = _long[1];//刷卡时长
                    }
                }
            }

            //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认
            this.appdoc = kqParam.getRestToOvertimeApply();
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveSet(Map parameterSet) {
        String turn_enable = (String) parameterSet.get("turn_enable"); //启用标识 0 不启用 1 启用
        String turn_charge = (String) parameterSet.get("turn_charge"); //0 需要进出匹配 1 有刷卡即加班
        String turn_tlong = (String) parameterSet.get("turn_tlong"); //刷卡时长  2 默认时长 1 参考班次时长 0 实际刷卡时长
        String turn_time = (String) parameterSet.get("turn_time"); //刷卡时长
        String turn_classid = (String) parameterSet.get("turn_classid"); //被选班次
        String turn_appdoc = (String) parameterSet.get("turn_appdoc"); //加班申请单 0 不需要生产申请单 1 生产申请单，需要确认 2 生产申请单，不需要确认
        
        Connection conn = null;
        try {
            //如果 时长选择的是 默认时长 turn_time 这个值为数字类型
            if (!NumberUtils.isNumber(turn_time) && "2".equals(turn_tlong)) {
                return false;
            }
            /*if ("2".equals(turn_tlong)) 
            {
            	float turn_time_long = Float.parseFloat(turn_time);
            	if(turn_time_long > 24.0)
            		return false;
            }*/
            //如果 时长选择的是 参考班次时长 turn_classid 必须选择班次
            if ("#".equals(turn_classid) && "1".equals(turn_tlong)) {
                return false;
            }
            conn = AdminDb.getConnection();
            //是否启动
            turn_enable = turn_enable == null || "".equals(turn_enable) ? "0" : turn_enable;
            if (!this.enable.equals(turn_enable)) {
                saveEnable(conn, turn_enable);
            }
            //进出匹配
            if (!this.charge.equals(turn_charge)) {
                saveCharge(conn, turn_charge);
                if ("1".equals(turn_charge)) {//
                    turn_appdoc = "0";
                }
            }

            //实际刷卡时长
            if (!this.tlong.equals(turn_tlong) && "0".equals(turn_tlong)) {//判断是否发生改变
                saveTlong(conn, turn_tlong, "");
            }
            //默认时长
            if ((!this.tlong.equals(turn_tlong) || !this.time.equals(turn_time)) && "2".equals(turn_tlong)) {//判断是否发生改变
                saveTlong(conn, turn_tlong, turn_time);
            }
            //参考班次时长
            if ((!this.tlong.equals(turn_tlong) || !this.classid.equals(turn_classid)) && "1".equals(turn_tlong)) {//判断是否发生改变
                saveTlong(conn, turn_tlong, turn_classid);
            }
            //申请单
            if (!this.appdoc.equals(turn_appdoc)) {//判断是否发生改变
                saveAppdoc(conn, turn_appdoc);
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(conn);
        }
        return true;
    }

    /**
     * 保存启用标识
     * @param enable
     * @return
     */
    private boolean saveEnable(Connection conn, String enable) {
        KqParam.getInstance().setRestToOvertime(conn, enable);
        return true;
    }

    /**
     * 保存进出匹配标识
     * @param charge
     * @return
     */
    private boolean saveCharge(Connection conn, String charge) {
        KqParam.getInstance().setRestToOvertimeCard(conn, charge);
        return true;
    }

    /**
     * 保存刷卡时长设置
     * @param tlong 时长标识
     * @param timeOrClass 时间或班次
     * @return
     */
    private boolean saveTlong(Connection conn, String tlong, String timeOrClass) {
        String content = "";
        if ("0".equals(tlong)) {
            content = tlong;
        } else {
            content = tlong + ":" + timeOrClass;
        }
        
        KqParam.getInstance().setRestToOvertimeTimelen(conn, content);
        return true;
    }

    /**
     * 保存加班申请单 标识
     * @param appdoc
     * @return
     */
    private boolean saveAppdoc(Connection conn, String appdoc) {
        KqParam.getInstance().setRestToOvertimeApply(conn, appdoc);
        return true;
    }

    /**
     * 获得启用标识值
     */
    public String getEnable() {
        String value = this.enable == null || "".equals(this.enable) ? "0" : this.enable;
        return value;
    }

    /**
     * 是否启用
     */
    public boolean isEnable() {
        if ("1".equals(this.enable)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得进出匹配标识值
     */
    public String getCharge() {
        String value = this.charge == null || "".equals(this.charge) ? "0" : this.charge;
        return value;
    }

    /**
     * 获得时长标识值
     */
    public String getTlong() {
        String value = "".equals(this.tlong) ? "2" : this.tlong;
        return value;
    }

    /**
     * 获得默认时间值
     */
    public String getTime() {
        String value = "".equals(this.time) ? "8" : this.time;
        return value;
    }

    /**
     * 获得班次值
     */
    public String getClassid() {
        String value = "".equals(this.classid) ? "#" : this.classid;
        return value;
    }

    /**
     * 获得启用标识值
     */
    public String getAppdoc() {
        String value = "".equals(this.appdoc) ? "0" : this.appdoc;
        return value;
    }

    /*
     * 数据处理是否是精简处理模式
     * 此方法同时供data_analyse.jsp页面使用
     */
    public boolean isQuickAnalyseMode() {
        return "1".equals(KqParam.getInstance().getQuickAnalyseMode());
    }

    /**
     * @Title: showTurnOvertimePage   
     * @Description: 数据处理界面是否需要显示转加班页签  
     * @return boolean ture:显示，false:不显示   
     * @throws
     */
    public boolean showTurnOvertimePage() {
        //非精简模式，启用刷卡转加班，并且需要进出匹配，并且不是默认时长，并且需要生成申请单并需确认
        return !isQuickAnalyseMode() && isEnable() && "0".equals(getCharge()) && !"2".equals(getTlong())
                && "1".equals(getAppdoc());
    }
}
