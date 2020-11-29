package com.hjsj.hrms.module.muster.mustermanage.transaction;

import com.hjsj.hrms.module.muster.mustermanage.businessobject.impl.MusterManageServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * @Titile: SaveMusterTrans
 * @Description:新增花名册保存信息，编辑花名册查询信息交易类
 * @Company:hjsj
 * @Create time: 2019年4月4日下午5:16:27
 * @author: Luzy
 * @version 1.0
 *
 */
public class SaveMusterTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try {
            MusterManageServiceImpl musManSer = new MusterManageServiceImpl(this.frameconn, this.userView);
            int operate = (Integer) this.formHM.get("operate");//operate =1 数据的保存更新；=2编辑的回显操作
            if (operate == 1) {
                String flag = (String) this.formHM.get("flag");// =add 添加操作 ; =updata更新操作
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                String orderItem = (String) this.formHM.get("orderItem");//排序指标
                String musterItem = (String) this.formHM.get("musterItem");//花名册指标
                String usrDB = (String) this.formHM.get("usrDB");//人员库
                String musterName = (String) this.formHM.get("musterName");//花名册名称
                musterName = PubFunc.hireKeyWord_filter(musterName);
                int rangetype = (Integer) this.formHM.get("rangetype");//数据范围选项 0=当前记录；1=某条历史纪录；2=部分历史纪录
                String filter = (String) this.formHM.get("filter");//数据过滤范围的id号
                String moduleMusType = (String) this.formHM.get("modileMusterType");//花名册类型（模块）
                int tabid = this.getMusterTabid(dao);//花名册id
                int norder = tabid;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String time = df.format(new Date());// new Date()为获取当前系统时间  添加时间
                String musType = (String) this.formHM.get("musterType");//花名册分类
                String userName = userView.getUserName();//用户名
                String userFullName= userView.getUserFullName();
                if (StringUtils.isNotBlank(userFullName)&&!userFullName.equals(userName)) {
                    userName=userFullName;
                }
                String orgId = (String) this.formHM.get("musterUnit");//获取机构Id
                //if (!StringUtils.isNumeric(orgId)) {//无需解码,解码判定也不能这么判定
                //	orgId = PubFunc.decrypt(orgId);
				//};
                if (musType.length() < 2) { //花名册类型保存成两位
                    musType = "0" + musType;
                }
                String ModuleFlag = this.getModuleFlag(musType, userName);//ModuleFlag 字段的获取
                int showMode = 1;
                HashMap<String, Object> dataArea = this.getDataArea(filter, rangetype);
                JSONObject json = JSONObject.fromObject(dataArea);//转换为JSON格式的String保存
                if ("add".equals(flag)) {
                    RecordVo lnameVo = new RecordVo("lname");
                    lnameVo.setInt("tabid", tabid);
                    lnameVo.setString("hzname", StringUtils.deleteWhitespace(musterName));
                    lnameVo.setString("flag", moduleMusType);
                    lnameVo.setString("title", StringUtils.deleteWhitespace(musterName));
                    lnameVo.setString("moduleflag", ModuleFlag);
                    lnameVo.setString("sortfield", orderItem);
                    lnameVo.setString("showmode", showMode + "");
                    lnameVo.setInt("norder", norder);
                    lnameVo.setString("b0110", orgId);
                    lnameVo.setString("styleid", musType);
                    if("-1".equals(usrDB)) {
                        lnameVo.setString("nbases", "");
                    }else {
                        lnameVo.setString("nbases", usrDB);
                    }
                    lnameVo.setString("datarange", json.toString());
                    lnameVo.setString("create_name", userName);
                    lnameVo.setDate("create_date", time);
                    musManSer.saveMuster(lnameVo, musterItem);
                } else if ("updata".equals(flag)) {
                    String tabId = "";
                    int updataTabid = -1;
                    tabId = (String) this.formHM.get("tabid");
                    updataTabid = Integer.parseInt(tabId);
                    ArrayList updataValue = new ArrayList();
                    //hzname=?,flag=?,title=?,moduleflag=?,styleid=?,sortField=?,b0110=?,Nbases=?,DataRange=? WHERE tabid = ? 
                    updataValue.add(musterName);
                    updataValue.add(moduleMusType);
                    updataValue.add(musterName);
                    updataValue.add(ModuleFlag);
                    updataValue.add(musType);
                    updataValue.add(orderItem);
                    updataValue.add(orgId);
                    if("-1".equals(usrDB)) {
                        updataValue.add("");
                    }else {
                        updataValue.add(usrDB);
                    }
                    updataValue.add(json.toString());
                    updataValue.add(updataTabid);
                    musManSer.updataMuster(updataValue, tabId, musterItem);
                }
            } else if (operate == 2) {
                String id = (String) this.formHM.get("tabid");
                String musterType = (String) this.formHM.get("musterType");
                ArrayList item = musManSer.editMusterInit(id, "fieldData",musterType);
                ArrayList musterPro = musManSer.editMusterInit(id, "otherData",musterType);
                this.formHM.put("item", item);
                this.formHM.put("otherData", musterPro);
            }
        } catch (Exception e) {
            e.printStackTrace();
            GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 获取新增花名册的ID
     * @param dao 查询需要的dao
     * @return 返回花名册ID
     */
    private int getMusterTabid(ContentDAO dao) {
        int tabid = 0;
        try {
            ArrayList<Integer> tabidlist = new ArrayList<Integer>();
            String sql = "select tabid  from lname";
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                tabid = this.frowset.getInt("tabid");
                tabidlist.add(tabid);
            }
            for(int i=1;;i++) {
                if (!tabidlist.contains(i)) {
                    tabid = i;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tabid;
    }
    /**
     * 拼接ModuleFlag字段的数据
     * @param musType 花名册类型
     * @param userName  花名册名称
     * @return 返回ModuleFlag需要的数据
     */
    private String getModuleFlag(String musType, String userName) {
        String ModuleFlag = "";
        try {
            ModuleFlag = "0" + musType + userName + "`";
            int size = 20 - ModuleFlag.length();
            for (int i = 0; i < size; i++) {
                ModuleFlag = ModuleFlag + '0';
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ModuleFlag;

    }
    /**
     * 获取花名册的数据范围
     * @param filter    过滤条件Id
     * @param rangetype 数据范围类型 0=当前记录，1=某月历史纪录，2=部分历史纪录
     * @return 返回一个数据范围的HashMap集合
     */
    private HashMap<String, Object> getDataArea(String filter, int rangetype) {
        HashMap<String, Object> dataArea = new HashMap<String, Object>();
        boolean parttimejobvalue = (Boolean) this.formHM.get("parttimejobvalue");//是否显示兼职人员
        dataArea.put("parttimejobvalue", parttimejobvalue);
        if (!"-1".equals(filter)) {                     //数据范围的ID -1代表无
            dataArea.put("filter", filter + "");
        } else {
            dataArea.put("filter", null);
        }
        String rangeType = String.valueOf(rangetype);   //哪个类型的数据范围 0=当前记录，1=某月历史纪录，2=部分历史纪录的其他，3=部分历史纪录单个选择指标
        dataArea.put("range_type", rangeType);
        if (rangetype == 0) {
            dataArea.put("condition", null);
        } else if (rangetype == 1) {   //某月历史纪录放的数据
            String fieldByMonth = (String) this.formHM.get("fieldByMonth");
            int year = (Integer) this.formHM.get("year");
            int month = (Integer) this.formHM.get("month");
            String number = (String) this.formHM.get("numberCondition");
            String nextMonth = "0";
            String nextYear = "0";
            if (month < 12) {       //例如 2012-02-01|2012-03-01或2012.12.01-2013.01.01
                nextMonth = String.valueOf(month + 1);
                if (nextMonth.length() == 1) {
                    nextMonth = "0" + nextMonth;
                }
                nextYear = String.valueOf(year);
            } else if (month == 12) {
                nextMonth = "01";
                nextYear = String.valueOf(year + 1);
            }
            String selMonth = String.valueOf(month);
            if (selMonth.length() == 1) {
                selMonth = "0" + selMonth;
            }
            String areaTime = fieldByMonth + "," + year + "-" + selMonth + "-" + "01" + "|" + nextYear + "-" + nextMonth + "-" + "01";
            if (StringUtils.isNotEmpty(number)) {
                areaTime+="|"+number;
            }
            dataArea.put("condition", areaTime);
        } else if (rangetype == 3) {                                         //部分历史记录
            String selDataAreaField = (String) this.formHM.get("idflag");    //部分历史记录的单个指标的指标ID
            String areaBegin = (String) this.formHM.get("form");             //从
            String areaEnd = (String) this.formHM.get("to");                 //到
            if (areaEnd.indexOf("`") > -1) {
                areaEnd = areaEnd.split("`")[0];
            }
            if (areaBegin.indexOf("`") > -1) {
                areaBegin = areaBegin.split("`")[0];                        //{"filter":null,"condition":"A18.A18Z1,1|2","range_type":"3"}
            }
            String condition = selDataAreaField + "," + areaBegin + "|" + areaEnd;
            dataArea.put("condition", condition);
        } else if (rangetype == 2) {                                        //部分历史纪录多个指标
            String condition = (String) this.formHM.get("condition");       //{"filter":null,"condition":"1|B0110=01`","range_type":"2"}
            String moreField = SafeCode.decode(condition);
            dataArea.put("condition", moreField);
        }

        return dataArea;
    }
}
