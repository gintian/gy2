package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 保存年假计算公式
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 31, 2006:4:51:59 PM</p>
 * @author sx
 * @version 1.0
 *
 */
public class SaveExpreTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String c_expr = (String) this.getFormHM().get("c_expr");
            c_expr = PubFunc.keyWord_filter(c_expr);
            String hols_status = (String) this.getFormHM().get("hols_status");
            String hols_name = (String) this.getFormHM().get("hols_name");
            String exp_field = (String) this.getFormHM().get("exp_field");
            if (exp_field == null || exp_field.length() <= 0)
                exp_field = "q1703";
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            
            //有公式，需进行校验并保存
            if (c_expr != null && c_expr.length() > 0) {
                /* zxj 20141018 该验证不完善，暂时废除（不是所有的#。。#格式都不支持）
                java.util.regex.Pattern p = java.util.regex.Pattern.compile("#(.+?)#"); // 正则表达式，匹配 #........#
                java.util.regex.Matcher m = p.matcher(c_expr);
                String dateStr = null;
                while (m.find()) { // 在 str 中查找正则表达式匹配的部分
                    dateStr = m.group(1); // 获取日期，即两个#之间的部分
                    try {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                        if (!dateStr.equals(df.format(df.parse(dateStr))))//抛异常就不是正确格式
                        {
                            SimpleDateFormat sf = new SimpleDateFormat("yyyy.M.d");
                            if (!dateStr.equals(sf.format(sf.parse(dateStr)))) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.d");
                                if (!dateStr.equals(sdf.format(sdf.parse(dateStr)))) {
                                    this.getFormHM().put("sige", "1");
                                    this.getFormHM().put("sigh", ResourceFactory.getProperty("errors.query.expression"));
                                    this.getFormHM().put("errormsg", "#" + dateStr + "#^^^^此处必须是日期型，格式为#yyyy.mm.dd#，如#2002.5.16#");
                                    this.getFormHM().put("expr_flag", "0");
                                    this.getFormHM().put("expr_flag", "0");
                                    return;
                                }
                            }
                        }
                    } catch (Exception e) {
                        this.getFormHM().put("sige", "1");
                        this.getFormHM().put("sigh", ResourceFactory.getProperty("errors.query.expression"));
                        this.getFormHM().put("errormsg", "#" + dateStr + "#^^^^此处必须是日期型，格式为#yyyy.mm.dd#，如#2002.5.16#");
                        this.getFormHM().put("expr_flag", "0");
                        this.getFormHM().put("expr_flag", "0");
                        return;
                    }
                }
                */
                ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
                YksjParser yp = new YksjParser(getUserView(), alUsedFields, 
                        YksjParser.forSearch, new GetCheckExpreTrans().getFieldType(exp_field), YksjParser.forPerson, 
                        "Ht", "");
                yp.setCon(this.getFrameconn());
                if (!yp.Verify_where(c_expr.trim())) {//校验不通过
                    String strErrorMsg = yp.getStrError();
                    this.getFormHM().put("sige", "1");
                    this.getFormHM().put("sigh", ResourceFactory.getProperty("errors.query.expression"));
                    this.getFormHM().put("errormsg", strErrorMsg);
                } else {
                    ArrayList list = new ArrayList();
                    
                    /********交验通过********/
                    StringBuffer selectsql = new StringBuffer();
                    String b0110 = "";
                    if (this.userView.isSuper_admin()) {
                        b0110 = "UN";
                    } else {
                        ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                        String userOrgId = managePrivCode.getPrivOrgId();
                        b0110 = "UN" + userOrgId;
                    }
                    String name = "REST_" + exp_field.toUpperCase() + "_" + hols_status;
                    selectsql.append("select content  from kq_parameter where b0110=");
                    selectsql.append("'" + b0110 + "'");
                    selectsql.append(" and UPPER(name)='" + name.toUpperCase() + "'");
                    String sige = "4";
                    String sigh = ResourceFactory.getProperty("lable.performance.saveFail");

                    this.frowset = dao.search(selectsql.toString());
                    if (this.frowset.next()) {
                        StringBuffer update = new StringBuffer();
                        update.append("update kq_parameter set");
                        update.append(" content=?");
                        update.append(" where b0110='" + b0110 + "'");
                        update.append(" and UPPER(name)='" + name.toUpperCase() + "'");
                        
                        list.add(c_expr.trim());
                        dao.update(update.toString(), list);
                        sige = "5";
                        sigh = ResourceFactory.getProperty("kq.register.save.success");
                    } else {
                        StringBuffer insert = new StringBuffer();
                        insert.append("insert into kq_parameter");
                        insert.append(" (b0110,name,description,content,status)");
                        insert.append(" values (?,?,?,?,?)");
                        list.add(b0110);
                        list.add(name);
                        list.add(hols_name);
                        list.add(c_expr.trim());
                        list.add("1");
                        dao.insert(insert.toString(), list);
                        sige = "5";
                        sigh = ResourceFactory.getProperty("kq.register.save.success");
                    }
                        
                    this.getFormHM().put("sige", sige);
                    this.getFormHM().put("sigh", sigh);
                    this.getFormHM().put("expr_flag", "1");
                }
            } else { //无公式 需删除本单位假期公式
                ManagePrivCode managePrivCode = new ManagePrivCode(userView, this.getFrameconn());
                String b0110 = managePrivCode.getUNB0110();
                String name = "REST_" + exp_field.toUpperCase() + "_" + hols_status;
                StringBuffer selectsql = new StringBuffer();
                selectsql.append("select content  from kq_parameter where b0110=");
                selectsql.append("'" + b0110 + "'");
                selectsql.append(" and UPPER(name)='" + name.toUpperCase() + "'");
    
                this.frowset = dao.search(selectsql.toString());
                if (this.frowset.next()) {
                    StringBuffer delsql = new StringBuffer();
                    delsql.append("delete from kq_parameter where b0110=");
                    delsql.append("'" + b0110 + "' and UPPER(name)='" + name.toUpperCase() + "'");
                    ArrayList list = new ArrayList();
                    dao.delete(delsql.toString(), list);
                    this.getFormHM().put("sige", "5");
                    String sigh = ResourceFactory.getProperty("kq.register.save.success");
                    this.getFormHM().put("sigh", sigh);
                    this.getFormHM().put("expr_flag", "1");
                } else {
                    if (!"UN".equalsIgnoreCase(b0110)) {
                        String sigh = ResourceFactory.getProperty("kq.feast.only.delete.selfdept");
                        this.getFormHM().put("sigh", sigh);
                    } else {
                        this.getFormHM().put("sige", "5");
                        String sigh = ResourceFactory.getProperty("kq.register.save.success");
                        this.getFormHM().put("sigh", sigh);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

}
