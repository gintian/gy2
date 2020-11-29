package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ExportTemplateTrans.java
 * </p>
 * <p>
 * Description:人事异动中下载模板
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-04-21 13:00:00
 * </p>
 *
 * @author xieguiquan
 * @version 1.0
 */
public class ExportTemplateTrans extends IBusiness {

    public void execute() throws GeneralException {

        String onlyflag = "0";//0表示原有逻辑，1表示唯一指标
        String fieldset_2 = "";//变化后子集
        HashMap fieldsetmap = new HashMap();//子集及数据的对应关系  及时子集中没有数据，其键值（sub_domain）也是有数据的
        String tabid = (String) this.getFormHM().get("tabid");//模板id
        String sqlStr = (String) this.getFormHM().get("sqlStr");//查询语句
        //获得templateSetList
        sqlStr = SafeCode.decode(sqlStr);
        sqlStr = PubFunc.keyWord_reback(sqlStr);

        String needcondition = (String) this.userView.getHm().get("template_sql_1");//(String) this.getFormHM().get("needcondition");//查询语句
        needcondition = SafeCode.decode(needcondition);
        StringBuffer headtarg = new StringBuffer(); //组合表头
        StringBuffer strsql = new StringBuffer();
        //组合sql
        String table_name = (String) this.getFormHM().get("table_name");
        String tasklist_str = (String) this.getFormHM().get("tasklist_str");
        tasklist_str = SafeCode.decode(tasklist_str);
        String codeid = (String) this.getFormHM().get("codeid");//相关代码类
        String orderStr = (String) this.getFormHM().get("orderStr");
        orderStr = SafeCode.decode(orderStr);
        String operationtype = (String) this.getFormHM().get("operationtype");
        /**安全平台改造，不能将sql传向前台**/
        String hmuster_sql = (String) this.userView.getHm().get("template_sql");//(String)this.getFormHM().get("hmuster_sql");
        hmuster_sql = PubFunc.keyWord_reback(hmuster_sql);
        String infor_type = (String) this.getFormHM().get("infor_type");//xcs infor_type 信息群的类型 1：人员2：单位3：岗位
        String hiddenItem = (String) this.getFormHM().get("hiddenItem");
        ArrayList fieldlist = new ArrayList();

        if (tasklist_str == null)
            tasklist_str = "";
        ArrayList tasklist = new ArrayList();
        ContentDAO dao = new ContentDAO(this.frameconn);
        if (tasklist_str.length() > 0) {
            String[] temp = tasklist_str.split(",");
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == null || temp[i].length() == 0)
                    continue;
                tasklist.add(temp[i]);

            }
        }
        String cname = "";


        TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(), Integer.parseInt(tabid), this.userView);
        TemplateListBo bo = new TemplateListBo(this.getFormHM().get("tabid").toString(), this.getFrameconn(), this.userView);
        bo.setIsMobile(0);//liuyz 下载过滤手机页
        ArrayList allCellList = bo.getAllCells();
        ArrayList templateSetList = (ArrayList) allCellList.get(0);
        ArrayList noPrintList = (ArrayList) allCellList.get(1);
        ArrayList headSetList = new ArrayList();
        FieldItem field = null;
        String onlyname = "";//唯一性指标
        String valid = "";
        if (bo.getBo() != null && bo.getBo().getInfor_type() == 1) {//如果是对人员的操作
            headtarg.append("A0100,");
            headtarg.append("BasePre,");
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
            onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
            if ("0".equals(valid)) {
                onlyname = "no";
            }
        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 2) {//对单位|部门的操作
            headtarg.append("B0110,");
            RecordVo unit_code_field_constant_vo = ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD", this.getFrameconn());
            if (unit_code_field_constant_vo != null) {
                onlyname = unit_code_field_constant_vo.getString("str_value");
            }
        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 3) {//对岗位的操作
            headtarg.append("e01a1,");
            RecordVo pos_code_field_constant_vo = ConstantParamter.getRealConstantVo("POS_CODE_FIELD", this.getFrameconn());
            if (pos_code_field_constant_vo != null) {
                onlyname = pos_code_field_constant_vo.getString("str_value");
            }
        }

        if (table_name.startsWith("templet")) {
            headtarg.append("ins_id,");
            headtarg.append("task_id,");
        }
        String repeatstr = "";

        HashMap fieldPrivByNode = new HashMap();
        if (tasklist.size() > 0) {
            fieldPrivByNode = tablebo.getFieldPriv((String) tasklist.get(0), this.getFrameconn());
        }
        //添加一个参数，如果titleflag为false，则列表方式显示的列标题与模板设置的标题一致；否则列表方式显示的列标题为"拟["模板设置的标题"]",liuzy 20151125
        boolean titleflag = false;

        //循环templateSetList，生成headtarg。去掉临时变量和子集
        for (int i = 0; i < templateSetList.size(); i++) {
            LazyDynaBean abean = (LazyDynaBean) templateSetList.get(i);
            if (!"0".equals(abean.get("isvar")))//去掉临时变量
                continue;
            if (!"0".equals(abean.get("subflag")))//去掉子集  只有子集时，fieldset_2和fieldsetmap才能被赋值
            {
                if ("2".equals(abean.get("chgstate"))) {
                    if (fieldset_2.indexOf("" + abean.get("setname")) == -1)
                        fieldset_2 += abean.get("setname") + ",";
                    fieldsetmap.put(abean.get("setname"), abean.get("sub_domain"));
                }


                continue;
            }
            if ((infor_type != null && !"1".equals(infor_type)) && (
                    "codesetid".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "codeitemdesc".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "corcode".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "parentid".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "start_date".equalsIgnoreCase(abean.get("field_name").toString().trim()))) {

            } else {

                String chgstate = (String) abean.get("chgstate");   // 1:变化前  2：变化后
                if (!this.userView.isSuper_admin() && "0".equals(tablebo.getUnrestrictedMenuPriv_Input())) {
                    //引入节点控制指标权限
                    String state = "";
                    String editable = (String) fieldPrivByNode.get(abean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); //	//0|1|2(无|读|写)
                    if (editable != null)
                        state = editable;
                    if ("0".equals(state)) {
                        continue;
                    } else {
                        if ("".equals(state) && "0".equalsIgnoreCase(this.getUserView().analyseFieldPriv(abean.get("field_name").toString().trim())))
                            continue;
                    }
                    //
                    //	if(this.getUserView().analyseFieldPriv(abean.get("field_name").toString().trim()).equalsIgnoreCase("0")&&bo.getBo().getUnrestrictedMenuPriv_Input().equals("0"))
                    //		 continue;	//无权限的去掉
                }


            }
            String sub_domain_id = "";
            if (abean.get("sub_domain_id") != null && "1".equals(abean.get("chgstate"))) {
                sub_domain_id = (String) abean.get("sub_domain_id");
                if (sub_domain_id != null && sub_domain_id.length() > 0) {
                    sub_domain_id = "_" + sub_domain_id;
                } else {
                    sub_domain_id = "";
                }
            }
            headtarg.append(abean.get("field_name") + sub_domain_id + "_" + abean.get("chgstate") + ",");
            headSetList.add(abean);
            FieldItem item = DataDictionary.getFieldItem(abean.get("field_name").toString());
            String desc = abean.get("hz").toString().replace("`", "");
            if ("2".equals(abean.get("chgstate"))) {
                if (desc != null && desc.trim().length() > 0 && "拟".equalsIgnoreCase(desc.substring(0, 1))) {
                } else {
                    if (titleflag) {
                        desc = "拟[" + desc + "]";
                    }
                }
            }
            //去掉隐藏指标,唯一标示去掉
            if (hiddenItem != null && hiddenItem.indexOf(abean.get("field_name") + ",") != -1) {
                if (onlyname != null && onlyname.trim().length() > 1 && onlyname.toLowerCase().equals(abean.get("field_name").toString().toLowerCase())) {

                } else {
                    continue;
                }
            }
            if (repeatstr.indexOf(abean.get("field_name") + "_" + abean.get("chgstate") + ",") == -1)
                repeatstr += abean.get("field_name") + sub_domain_id + "_" + abean.get("chgstate") + ",";
            else
                continue;  //	去掉重复指标
            //必填项标识 首先控制不是临时变量
            boolean needflag = false;
            if (abean.get("yneed") != null) {
                if ("1".equals(abean.get("yneed")))
                    needflag = true;
            }

            if (item != null) {
                Field item_0 = (Field) item.cloneField();
                FieldItem item_1 = (FieldItem) item.cloneItem();
                if (abean.get("field_type") != null && abean.get("field_type").toString().length() > 0)
                    item_1.setItemtype("" + abean.get("field_type"));
                item_1.setItemid(abean.get("field_name") + sub_domain_id + "_" + abean.get("chgstate"));
                item_1.setItemdesc(desc);
                item_1.setFillable(needflag);
                fieldlist.add(item_1);
            } else {
                FieldItem fielditem = new FieldItem(abean.get("field_name") + "_" + abean.get("chgstate"), abean.get("field_name") + "_" + abean.get("chgstate"));
                if ((infor_type != null && !"1".equals(infor_type)) && ("parentid".equalsIgnoreCase(abean.get("field_name").toString().trim()))) {
                    fielditem.setCodesetid("UM");
                } else {
                    fielditem.setCodesetid("0");
                }
                if (abean.get("field_type") == null || abean.get("field_type").toString().length() < 1) {
                    fielditem.setItemtype("A");
                } else
                    fielditem.setItemtype(abean.get("field_type").toString());
                fielditem.setFillable(needflag);
                fielditem.setItemdesc(desc);
                fieldlist.add(fielditem);
            }

        }//for循环结束
        for (int i = 0; i < noPrintList.size(); i++) {
            LazyDynaBean abean = (LazyDynaBean) noPrintList.get(i);
            if (onlyname.toLowerCase().equalsIgnoreCase(String.valueOf(abean.get("field_name")))) {
                String sub_domain_id = "";
                if (abean.get("sub_domain_id") != null && "1".equals(abean.get("chgstate"))) {
                    sub_domain_id = (String) abean.get("sub_domain_id");
                    if (sub_domain_id != null && sub_domain_id.length() > 0) {
                        sub_domain_id = "_" + sub_domain_id;
                    } else {
                        sub_domain_id = "";
                    }
                }
                FieldItem item = DataDictionary.getFieldItem(abean.get("field_name").toString());
                boolean needflag = false;
                if (abean.get("yneed") != null) {
                    if ("1".equals(abean.get("yneed")))
                        needflag = true;
                }
                String desc = abean.get("hz").toString().replace("`", "");
                if (item != null) {
                    Field item_0 = (Field) item.cloneField();
                    FieldItem item_1 = (FieldItem) item.cloneItem();
                    if (abean.get("field_type") != null && abean.get("field_type").toString().length() > 0)
                        item_1.setItemtype("" + abean.get("field_type"));
                    item_1.setItemid(abean.get("field_name") + sub_domain_id + "_" + abean.get("chgstate"));
                    item_1.setItemdesc(desc);
                    item_1.setFillable(needflag);
                    fieldlist.add(item_1);
                } else {
                    FieldItem fielditem = new FieldItem(abean.get("field_name") + "_" + abean.get("chgstate"), abean.get("field_name") + "_" + abean.get("chgstate"));
                    if ((infor_type != null && !"1".equals(infor_type)) && ("parentid".equalsIgnoreCase(abean.get("field_name").toString().trim()))) {
                        fielditem.setCodesetid("UM");
                    } else {
                        fielditem.setCodesetid("0");
                    }
                    if (abean.get("field_type") == null || abean.get("field_type").toString().length() < 1) {
                        fielditem.setItemtype("A");
                    } else
                        fielditem.setItemtype(abean.get("field_type").toString());
                    fielditem.setFillable(needflag);
                    fielditem.setItemdesc(desc);
                    fieldlist.add(fielditem);
                }
                headtarg.append(abean.get("field_name") + sub_domain_id + "_" + abean.get("chgstate") + ",");
            }
        }
        if (onlyname != null && onlyname.trim().length() > 1 && headtarg.toString().toLowerCase().indexOf(onlyname.toLowerCase() + "_1") != -1) {//不需要维护唯一性指标
            onlyflag = "1";
        } else {
            if (onlyname != null && onlyname.trim().length() > 1 && headtarg.toString().toLowerCase().indexOf(onlyname.toLowerCase() + "_2") != -1) {
                onlyflag = "2";
            }
        }
        strsql.append(" select  ");
        strsql.append(headtarg.toString().endsWith(",") ? "" + headtarg.toString().substring(0, headtarg.toString().length() - 1) : headtarg.toString());
        strsql.append(" from " + table_name);

        HashMap cell_param_map = tablebo.getModeCell4();//查找 变化后的的单元格各属性( 非子集 )
        String wherestr = "";
        if (hmuster_sql != null && hmuster_sql.length() > 0) {//高级花名册的人员信息sql
            if (hmuster_sql.indexOf("where") != -1) {
                wherestr = hmuster_sql.substring(hmuster_sql.indexOf("where"), hmuster_sql.length());
                strsql.append(" " + wherestr);
            }
        }
        if (wherestr.length() <= 0) {
            strsql.append(" where 1=1 ");
            if (tasklist != null && tasklist.size() > 0) {
                StringBuffer strins = new StringBuffer();
                for (int i = 0; i < tasklist.size(); i++)//按任务号查询需要审批的对象20080418
                {
                    if (i != 0)
                        strins.append(",");
                    strins.append((String) tasklist.get(i));
                }
                strsql.append(" and ( task_id in(");
                strsql.append(strins.toString());
                strsql.append(")");
                //角色属性是否为汇报关系 “直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，
                strsql.append(" or exists (select null from t_wf_task_objlink where " + table_name + ".seqnum=t_wf_task_objlink.seqnum and " + table_name + ".ins_id=t_wf_task_objlink.ins_id ");
                strsql.append("  and task_id in (" + strins.toString() + ") and state=0  and (" + Sql_switcher.isnull("special_node", "0") + "=0  or ( " + Sql_switcher.isnull("special_node", "0") + "=1 and (lower(username)='" + this.userView.getUserName().toLowerCase() + "' or lower(username)='" + this.userView.getDbname().toLowerCase() + this.userView.getA0100() + "' ) ) )) ) ");

            }


            if (needcondition.length() > 0)
                strsql.append(" " + needcondition);
            if (sqlStr.length() > 0)
                strsql.append(" and  " + sqlStr);
            if (orderStr.length() == 0)
                strsql.append(" order by a0000");
            else
                strsql.append(orderStr);
        }

        HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
        FileOutputStream fileOut = null;
		HashMap nameMap = new HashMap();
        try {
            this.frowset = dao.search("select name from template_table where tabid=" + tabid);
            if (this.frowset.next())
                cname = this.frowset.getString("name");
            String fields = "";
            if (fieldset_2.length() > 0) {
                String fieldset2[] = fieldset_2.split(",");
                for (int i = 0; i < fieldset2.length; i++) {
                    String fieldset = fieldset2[i];
                    if (fieldset != null && fieldset.trim().length() > 0)
                        fields += "'" + fieldset + "',";
                }
                if (fields.length() > 0) {
                    fields = fields.substring(0, fields.length() - 1);
                    this.frowset = dao.search("select * from fieldSet where fieldSetId in (" + fields + ") order by displayorder ");
                    fieldset_2 = "";
                    while (this.frowset.next()) {
                        nameMap.put(this.frowset.getString("fieldSetId"), this.frowset.getString("customdesc"));
                        fieldset_2 += this.frowset.getString("fieldSetId") + ",";//这行代码貌似多余的
                    }
                }
            }

            //模版名带特殊字符下载模版报错 需要先将cname去除特殊字符再使用。
            cname = cname.replace("\\", "").replace("/", "").replaceAll("\\)", "）").replaceAll("\\(", "（").replace(":", "").replace("*", "").replace("?", "").replace("\"", "").replace("<", "").replace(">", "");
            if ("0".equals(onlyflag)) {//没有唯一性指标
                bo.getExcel(wb, fieldlist, strsql, table_name, tablebo, cname);
            } else {//有唯一性指标
                bo.getExcel2(wb, fieldlist, strsql, table_name, tablebo, fieldset_2, fieldsetmap, cname, nameMap, onlyname, onlyflag, wherestr, orderStr);
            }

            String outName = cname;
            outName += PubFunc.getStrg() + ".xls";

            fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
            wb.write(fileOut);

            getFormHM().put("outName", PubFunc.encrypt(outName));

        } catch (Exception e) {
            e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
        }finally {
        	PubFunc.closeResource(wb);
        	PubFunc.closeResource(fileOut);
		}
    }
}
