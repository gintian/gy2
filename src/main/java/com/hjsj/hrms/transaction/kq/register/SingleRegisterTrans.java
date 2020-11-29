package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * 检索个人考勤信息
 * @param userbase 库前缀
 * @param code 部门
 * @A0100 员工编号 
 * */
public class SingleRegisterTrans extends IBusiness {
    public void execute() throws GeneralException {
        try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
            String userbase = (String) hm.get("userbase");
            userbase = PubFunc.decrypt(userbase);
            String start_date = (String) hm.get("start_date");
            String end_date = (String) hm.get("end_date");
            String code = (String) hm.get("code");
            String rflag = (String) hm.get("rflag");
            String marker = (String) hm.get("marker");
            if (rflag == null || rflag.length() <= 0)
                rflag = "";
            this.getFormHM().put("rflag", rflag);
            if (code == null || code.length() <= 0) {
                code = "";
            }
            String A0100 = (String) hm.get("A0100");
            A0100 = PubFunc.decrypt(A0100);
            CheckPrivSafeBo checkPriv = new CheckPrivSafeBo(this.frameconn, this.userView);
            code = checkPriv.checkOrg(code, "");
            if (code == null || "".equals(code)) {
                code = this.userView.getUserDeptId();//自助/考勤表，为空的话，显示空白
            }
            /**一下2个越权过滤的暂时去掉 wangmj 2013年5月3日16:08:47**/
            //userbase=checkPriv.checkDb(userbase);
            //A0100=checkPriv.checkA0100(code, userbase, A0100, "");

            ArrayList fieldlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
            getSingleMessage(userbase, A0100);
            ArrayList fielditemlist = new ArrayList();
            int lockedNum = 0;
            boolean isC = true;
            for (int i = 0; i < fieldlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fieldlist.get(i);
                if ("1".equals(fielditem.getState())) {
                    fielditem.setVisible(true);
                    if (isC && "A".equalsIgnoreCase(fielditem.getItemtype())) {
                        lockedNum++;
                        if ("a0101".equalsIgnoreCase(fielditem.getItemid())) {
                            isC = false;
                        }
                    } else {
                        isC = false;
                    }
                } else if ("q03z0".equals(fielditem.getItemid())) {
                    fielditem.setVisible(true);
                    lockedNum++;

                } else {
                    fielditem.setVisible(false);
                }
                
                if (!"state".equals(fielditem.getItemid())) {
                    fielditemlist.add(fielditem.clone());
                }
            }
            
            StringBuffer column = new StringBuffer();
            for (int i = 0; i < fielditemlist.size(); i++) {
                FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                column.append(fielditem.getItemid() + ",");
            }
            
            DbWizard dbw = new DbWizard(this.frameconn);
            boolean isQ03_arc = dbw.isExistTable("Q03_arc",false);
            boolean dataInQ03 = dataInQ03(start_date);
            
            String columnstr = column.toString();
            columnstr = columnstr.substring(0, columnstr.length() - 1);
            String sqlstr = "select " + columnstr;
            StringBuffer wheresql = new StringBuffer();
            StringBuffer condition = new StringBuffer();
            if(dataInQ03 || !isQ03_arc){
            	wheresql.append(" from Q03 where");
            }else{
            	wheresql.append(" from Q03_arc where");
            }
//            wheresql.append(" from Q03 where");
            condition.append("  a0100='" + A0100 + "'");
            condition.append(" and nbase='" + userbase + "'");
            condition.append(" and Q03Z0 >= '" + start_date + "'");
            condition.append(" and Q03Z0 <= '" + end_date + "'");
            wheresql.append(" " + condition.toString());
            String orderby = " order by a0100,q03z0";
            /** *****输出数据******* */
            int num = fielditemlist.size();
            String numstr = "" + num;

            //修改日明细登记数据
            String up_dailyregister = KqParam.getInstance().getUpdateDailyRegister();
            this.getFormHM().put("up_dailyregister", up_dailyregister);
            this.getFormHM().put("num", numstr);
            this.getFormHM().put("sqlstr", sqlstr);
            this.getFormHM().put("columns", columnstr);
            this.getFormHM().put("strwhere", wheresql.toString());
            this.getFormHM().put("orderby", orderby);
            this.getFormHM().put("singfielditemlist", fielditemlist);
            this.getFormHM().put("code", code);
            // 涉及SQL注入直接放进userView里
    		this.userView.getHm().put("kq_condition", "3`" + condition.toString());
            this.getFormHM().put("relatTableid", "3");
            this.getFormHM().put("returnURL", "/kq/register/browse_single.do?b_browse=link");
            this.getFormHM().put("marker", marker);
            this.getFormHM().put("lockedNum", lockedNum + "");
            this.getFormHM().put("a0100", A0100);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /***************************
     * 得到用户的基本信息
     * @param userbase 库前缀
     * @param code 部门
     * @A0100 员工编号 
     * 直接this.getFormHM().put();
     * 
     * */
    private void getSingleMessage(String userbase, String A0100) {
        StringBuffer sql = new StringBuffer();
        sql.append("select b0110,e0122,e01a1,a0101 ");
        sql.append(" from " + userbase + "A01 ");
        sql.append(" where a0100='" + A0100 + "'");
        String b0110 = "";
        String e0122 = "";
        String e01a1 = "";
        String a0101 = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql.toString());
            if (this.frowset.next()) {
                b0110 = (String) this.frowset.getString("b0110");
                e0122 = (String) this.frowset.getString("e0122");
                e01a1 = (String) this.frowset.getString("e01a1");
                a0101 = (String) this.frowset.getString("a0101");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList restList = IfRestDate.search_RestOfWeek(b0110, userView, this.getFrameconn());
        String rest_date = restList.get(0).toString();
        this.getFormHM().put("rest_date", rest_date);
        String b0110_value = b0110;
        this.getFormHM().put("b0110_value", b0110_value);
        this.getFormHM().put("b0110", b0110);
        this.getFormHM().put("e0122", e0122);
        this.getFormHM().put("e01a1", e01a1);
        this.getFormHM().put("a0101", a0101);
    }
    /**
     * 判断某日期数据是否在Q03（员工日明细表）中
     * 
     * @return
     * @throws GeneralException
     */
    private boolean dataInQ03(String registerdate) throws GeneralException {
    	boolean bool = true;
    	RowSet rs = null;
    	if (registerdate == null) {
    		return false;
    	}
    	registerdate = registerdate.substring(0, registerdate.length()-3);
    	StringBuffer sql = new StringBuffer();
    	sql.append("select count(q03z0) num from Q03 ");
    	sql.append(" where q03z0 like '"+registerdate+"%'");
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	int num = 0;
        try {
            rs = dao.search(sql.toString());
            while (rs.next()) {
            	num = rs.getInt("num");
            }
            if(num == 0){
            	bool = false; 
            }            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeDbObj(rs);
        }
        return bool;
    }
}
