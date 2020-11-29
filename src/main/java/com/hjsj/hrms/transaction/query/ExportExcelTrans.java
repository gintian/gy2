package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.query.QueryUtils;
import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 0202001021
 * <p>
 * Title:ExportExcelTrans.java
 * </p>
 * <p>
 * Description>:ExportExcelTrans.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Sep 29, 2010 2:56:28 PM
 * </p>
 * <p>
 * @version: 4.0
 * </p>
 * <p>s
 * @author: LiZhenWei
 */
public class ExportExcelTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String exportFlag = (String) this.getFormHM().get("exportFlag");
            if("1".equals(exportFlag)) {
                String fileName = (String) this.userView.getHm().get("exportEmployeFileName");
                String msg = (String) this.userView.getHm().get("msg");
                msg = StringUtils.isEmpty(msg) ? "ok" : msg;
                this.getFormHM().put("msg", msg);
                Object exportRows = (Object) this.userView.getHm().get("exportRows");
                exportRows = exportRows == null ? "0" : exportRows;
                this.getFormHM().put("exportRows", Integer.valueOf(exportRows.toString()));
                this.getFormHM().put("totalRows", this.userView.getHm().get("totalRows"));
                this.getFormHM().put("exportEmployeFileName", fileName);
                this.userView.getHm().remove("exportEmployeFileName");
                if(StringUtils.isNotEmpty(fileName)) {
                    this.userView.getHm().remove("msg");
                    this.userView.getHm().remove("totalRows");
                    this.userView.getHm().remove("exportRows");
                }
                
                return;
            }
            
            this.userView.getHm().remove("exportEmployeFileName");
            String sql = "";
            String db = (String) this.getFormHM().get("dbpre");
            // 导出excel的第一行的列指标
            String ids = (String) this.getFormHM().get("ids"); 
            // 1.人员 2.部门 3.岗位
            String infokind = (String) this.getFormHM().get("infokind");
            String isHistory = (String) this.getFormHM().get("strwhere");
            QueryUtils qu = new QueryUtils(this.getFrameconn());
            String querytype = (String) this.getFormHM().get("querytype");
            String service = (String) this.getFormHM().get("service");
            // 员工管理 快速查询、通用查询、常用查询、简单查询的查询结果页面 此参数值为true，默认为false
            String isQuery = (String) this.getFormHM().get("isQuery");
            String selectDate = (String) this.getFormHM().get("selectDate");
            String times = (String) this.getFormHM().get("times");
            String selectField = (String) this.getFormHM().get("selectField");
            String whereValue = (String) this.getFormHM().get("where");
            // 新加的代码 统计分析/常用统计里面也调用此类
            if (!"true".equalsIgnoreCase(isQuery) && null != this.userView.getHm().get("staff_sql")) {
                // 从前台页面得到相应的sql语句
                sql = this.userView.getHm().get("staff_sql").toString(); 
                // xiegh 20170705 bug:24047 也不知道为啥要把这个remove了 现在给自助服务统计分析导出加个标识
                // 如果后台接收到则不删除staff_sql 其他地方调用该交易类没有影响
                if (service == null || "".equals(service) || !"true".equals(service))
                    this.userView.getHm().remove("staff_sql");

                // liuy 2014-10-31
                sql = SafeCode.decode(sql);
                // liuy 2014-10-31
            }
            
            sql = QuestionesBo.toHtml(sql);
            ArrayList dbpreList = new ArrayList();
            String dbpre = "";
            // 复杂查询页面
            if ("5".equals(querytype)) {
                String pre = (String) this.getFormHM().get("pre");
                if ("ALL".equalsIgnoreCase(db))
                    dbpre = pre;
                else {
                    if ("ALL".equalsIgnoreCase(pre))
                        dbpre = db;
                    else
                        dbpre = pre;
                    
                }
            } else
                dbpre = db;
            
            StringBuffer whereStr = new StringBuffer();
            if ("1".equals(infokind)) {
                if (!"ALL".equalsIgnoreCase(dbpre)) {
                    String[] dbpres = dbpre.split("`");
                    for (int i = 0; i < dbpres.length; i++)
                        dbpreList.add(dbpres[i]);
                } else
                    dbpreList = this.userView.getPrivDbList();
                
                if("2".equals(isHistory)) {
                    String[] fields = ids.split("`");
                    String fielSetId = "";
                    for(String field : fields) {
                        FieldItem fi = DataDictionary.getFieldItem(field);
                        if(fi == null)
                            continue;
                        
                        if(!"A01".equals(fi.getFieldsetid())) {
                            fielSetId = fi.getFieldsetid();
                            break;
                        }
                    }
                    String[] date = selectDate.split("-");
                    
                    whereStr.append("(" + Sql_switcher.year(fielSetId + "."+ fielSetId + "z0") + "=" + date[0]);
                    whereStr.append(" and " + Sql_switcher.month(fielSetId + "."+ fielSetId + "z0") + "=" + date[1]);
                    whereStr.append(" and "+ fielSetId + "."+ fielSetId + "z1=" + times + ")");
                } else if("3".equals(isHistory)) {
                    if(selectField.length() > 3) {
                        String[] values = whereValue.split(":");
                        FieldItem fi = DataDictionary.getFieldItem(selectField);
                        String fielSetId = fi.getFieldsetid();
                        whereStr.append("(1=1");
                        if(!"#".equals(values[0])) {
                            whereStr.append(" and ");
                            if("D".equals(fi.getItemtype())) {
                                values[0] = checkDateStyle(values[0]);
                                whereStr.append(Sql_switcher.dateToChar(fielSetId + "."+ selectField, "yyyy-MM-dd") 
                                        + ">='" + values[0] + "'");
                                
                            } else if("N".equals(fi.getItemtype()))
                                whereStr.append(fielSetId + "." + selectField + ">=" + values[0]);
                            else
                                whereStr.append(fielSetId + "."+ selectField + ">='" + values[0] + "'");
                                
                        }
                        
                        if(!"#".equals(values[1])) {
                            whereStr.append(" and ");
                            if("D".equals(fi.getItemtype())) {
                                values[1] = checkDateStyle(values[1]);
                                whereStr.append(Sql_switcher.dateToChar(fielSetId + "."+ selectField, "yyyy-MM-dd") 
                                        + "<='" + values[1] + "'");
                            } else if("N".equals(fi.getItemtype()))
                                whereStr.append(fielSetId + "." + selectField + "<=" + values[1]);
                            else
                                whereStr.append(fielSetId + "."+ selectField + "<='" + values[1] + "'");
                            
                        }
                        
                        whereStr.append(")");
                    } else if(StringUtils.isNotEmpty(whereValue)){
                        String[] values = whereValue.split("::");
                        boolean like = false;
                        if("1".equals(values[2]))
                            like =true;
                        
                        FactorList factorlist = new FactorList(PubFunc.keyWord_reback(values[0]), PubFunc.reBackWord((values[1])),"usr",
                                true, like, true, 1, this.userView.getUserName());
                        
                        String where = factorlist.getSqlExpression();
                        if(StringUtils.isNotEmpty(where) && where.indexOf("WHERE") > -1) {
                            where = where.substring(where.indexOf("WHERE") + 5);
                            where = where.replace("usr" + selectField + ".", selectField + ".");
                        } else
                            where = "1=1";
                        
                        whereStr.append(where);
                    }
                }
            }
            
            String outName = "";
            if (!"".equals(sql))
                outName = qu.exportExcel1(infokind, ids, isHistory, userView, querytype, dbpreList, sql, whereStr.toString());
            else
                outName = qu.exportExcel(infokind, ids, isHistory, userView, querytype, dbpreList, whereStr.toString());
            
            outName = PubFunc.encrypt(outName);
            this.userView.getHm().put("exportEmployeFileName", outName);
            this.getFormHM().put("outName", outName);
            this.getFormHM().put("exportRows", this.userView.getHm().get("exportRows"));
            this.getFormHM().put("totalRows", this.userView.getHm().get("totalRows"));
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }
    /**
     * 校验日期格式
     * @param date 需要校验的日期
     * @return
     */
    private String checkDateStyle(String date) {
        try {
            String[] values = date.split("-");
            if(Integer.valueOf(values[1]) < 10)
                values[1] = "0" + values[1];
            
            if(Integer.valueOf(values[2]) < 10)
                values[2] = "0" + values[2];
            
            date = values[0] + "-" + values[1] + "-" + values[2];
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            date = format.format(format.parse(date));
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return date;
    }
}
