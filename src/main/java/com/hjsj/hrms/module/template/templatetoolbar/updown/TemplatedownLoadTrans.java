package com.hjsj.hrms.module.template.templatetoolbar.updown;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatelist.businessobject.TemplateListShowBo;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
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
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * @Title: TemplatedownLoadTrans.java
 * @Package com.hjsj.hrms.module.template.templatetoolbar.updown
 * @Description: 人事异动-下载模版
 * @author gaohy
 * @date 2016-1-14 下午05:20:35
 * @version V7x
 */
public class TemplatedownLoadTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {

        String onlyflag = "0";// 0表示原有逻辑，1表示唯一指标
        String fieldset_2 = "";// 变化后子集
        HashMap fieldsetmap = new HashMap();// 子集及数据的对应关系
        // 及时子集中没有数据，其键值（sub_domain）也是有数据的
        String tabid = (String) this.getFormHM().get("tabid");// 模板id
        //zhangh 2019-11-20 获取office版本类型
        String officeType = (String) this.getFormHM().get("officeType");// 模板id
        String needcondition = (String) this.userView.getHm().get("template_sql_1");// (String)
        // this.getFormHM().get("needcondition");//查询语句
        needcondition = SafeCode.decode(needcondition);
        StringBuffer headtarg = new StringBuffer(); // 组合表头
        StringBuffer strsql = new StringBuffer();
        // 组合sql
        String ins_id = (String) this.userView.getHm().get("ins_id");// 流程实例号，进入流程之前是0
        ins_id = ins_id != null && ins_id.length() > 0 ? ins_id : "0";
        String task_id = (String) this.getFormHM().get("task_id");
        Pattern pattern = Pattern.compile("[0-9]+");
        if(!pattern.matcher(task_id).matches()&&task_id.indexOf(",")==-1)
            task_id = PubFunc.decrypt(task_id);

        String table_name = this.userView.getUserName() + "templet_" + tabid;
        if (!"0".equals(task_id)) {
            table_name = "templet_" + tabid;
            ins_id= this.getIns_id(task_id);
        }
        task_id = task_id != null && task_id.length() > 0 ? task_id : "0";
        /** 安全平台改造，不能将sql传向前台* */
        TemplateDataBo dataBo = new TemplateDataBo(this.frameconn, this.userView, Integer.parseInt(tabid));
        String hmuster_sql = dataBo.getHmuster_sql();// (String)this.getFormHM().get("hmuster_sql");
        hmuster_sql = PubFunc.keyWord_reback(hmuster_sql);
        String infor_type = (String) this.getFormHM().get("infor_type");// xcs 1：人员2：单位3：岗位
        String view_type = (String) this.getFormHM().get("view_type");// list  card
        String hiddenItem = "";

        ArrayList fieldlist = new ArrayList();

        ArrayList tasklist = new ArrayList();
        ContentDAO dao = new ContentDAO(this.frameconn);
        if (task_id.length() > 0) {
            String[] temp = task_id.split(",");
            for (int i = 0; i < temp.length; i++) {
                String taskid = temp[i];
                if (temp[i] == null || temp[i].length() == 0)
                    continue;
                if(!pattern.matcher(taskid).matches())
                    taskid = PubFunc.decrypt(taskid);
                tasklist.add(taskid);
            }
        }
        String cname = "";

        TemplateTableBo tablebo = new TemplateTableBo(this.getFrameconn(), Integer.parseInt(tabid), this.userView);
        TemplateListBo bo = new TemplateListBo(this.getFormHM().get("tabid").toString(), this.getFrameconn(), this.userView);
        //zhangh 2019-11-20 设置office版本类型
        bo.setOfficeType(officeType);
        bo.setIsMobile(0);
        ArrayList allList = bo.getAllCells();//获取打印和不打印的指标集合
        ArrayList templateSetList=(ArrayList) allList.get(0);//获取打印的指标集合。
        ArrayList noPrintList=(ArrayList) allList.get(1);//获取不打印页的指标集合。
        ArrayList headSetList = new ArrayList();
        FieldItem field = null;
        String onlyname = "";// 唯一性指标
        String valid = "";
        if (bo.getBo() != null && bo.getBo().getInfor_type() == 1) {// 如果是对人员的操作
            headtarg.append("A0100,");
            headtarg.append("BasePre,");
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
            onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            valid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
            if ("0".equals(valid)) {
                onlyname = "no";
            }
        }

        else if (bo.getBo() != null && bo.getBo().getInfor_type() == 2) {// 对单位|部门的操作
            headtarg.append("B0110,");
            RecordVo unit_code_field_constant_vo = ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD", this.getFrameconn());
            if (unit_code_field_constant_vo != null) {
                onlyname = unit_code_field_constant_vo.getString("str_value");
            }
        } else if (bo.getBo() != null && bo.getBo().getInfor_type() == 3) {// 对岗位的操作
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
        // 添加一个参数，如果titleflag为false，则列表方式显示的列标题与模板设置的标题一致；否则列表方式显示的列标题为"拟["模板设置的标题"]",liuzy
        // 20151125
        boolean titleflag = false;

        // 循环templateSetList，生成headtarg。去掉临时变量和子集
        for (int i = 0; i < templateSetList.size(); i++) {
            LazyDynaBean abean = (LazyDynaBean) templateSetList.get(i);
            if (!"0".equals(abean.get("isvar")))// 去掉临时变量
                continue;
            if (!"0".equals(abean.get("subflag")))//只有子集时，fieldset_2和fieldsetmap才能被赋值
            {
                if ("2".equals(abean.get("chgstate"))) {
                    String sub_domain_id = (String)abean.get("sub_domain_id");
                    if (fieldset_2.indexOf("" + abean.get("setname")+"_"+sub_domain_id+",") == -1)
                        fieldset_2 += abean.get("setname") +"_"+ sub_domain_id + ",";
                    fieldsetmap.put(abean.get("setname") +"_"+ sub_domain_id, abean.get("sub_domain"));
                }
                continue;
            }
            if ((infor_type != null && !"1".equals(infor_type)) && ("codesetid".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "codeitemdesc".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "corcode".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "parentid".equalsIgnoreCase(abean.get("field_name").toString().trim()) || "start_date".equalsIgnoreCase(abean.get("field_name").toString().trim()))) {

            } else {

                String chgstate = (String) abean.get("chgstate"); // 1:变化前 2：变化后
                if (!this.userView.isSuper_admin() &&(( "0".equals(tablebo.getUnrestrictedMenuPriv_Input())&&"2".equals(chgstate))||"1".equals(chgstate))) {
                    // 引入节点控制指标权限
                    String state = "";
                    String editable = (String) fieldPrivByNode.get(abean.get("field_name").toString().trim().toLowerCase() + "_" + chgstate); // //0|1|2(无|读|写)
                    if (editable != null)
                        state = editable;
                    if ("0".equals(state)) {
                        continue;
                    } else {
                        if ("".equals(state) && "0".equalsIgnoreCase(this.getUserView().analyseFieldPriv(abean.get("field_name").toString().trim())))
                            continue;
                    }
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
            // 去掉隐藏指标,唯一标示去掉
            if (hiddenItem != null && hiddenItem.indexOf(abean.get("field_name") + ",") != -1) {
                if (onlyname != null && onlyname.trim().length() > 1 && onlyname.toLowerCase().equals(abean.get("field_name").toString().toLowerCase())) {

                } else {
                    continue;
                }
            }
            if (repeatstr.indexOf(abean.get("field_name") + "_" + abean.get("chgstate") + ",") == -1)
                repeatstr += abean.get("field_name") + sub_domain_id + "_" + abean.get("chgstate") + ",";
            else
                continue; // 去掉重复指标
            // 必填项标识 首先控制不是临时变量
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

        }// for循环结束
        //循环不打印页上指标，如果是唯一性指标，加入集合中。
        for(int i=0;i<noPrintList.size();i++){
            LazyDynaBean abean = (LazyDynaBean) noPrintList.get(i);
            if(onlyname.toLowerCase().equalsIgnoreCase(String.valueOf(abean.get("field_name")))){
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
        if (onlyname != null && onlyname.trim().length() > 1 && headtarg.toString().toLowerCase().indexOf(onlyname.toLowerCase() + "_1") != -1) {// 不需要维护唯一性指标
            onlyflag = "1";
        } else {
            if (onlyname != null && onlyname.trim().length() > 1 && headtarg.toString().toLowerCase().indexOf(onlyname.toLowerCase() + "_2") != -1) {
                onlyflag = "2";
            }
        }
        strsql.append(" select  ");
        strsql.append(headtarg.toString().endsWith(",") ? "" + headtarg.toString().substring(0, headtarg.toString().length() - 1) : headtarg.toString());
        strsql.append(" from " + table_name);

        HashMap cell_param_map = tablebo.getModeCell4();// 查找 变化后的的单元格各属性( 非子集 )
        String wherestr = "";
        if (hmuster_sql != null && hmuster_sql.length() > 0) {// 高级花名册的人员信息sql
            if (hmuster_sql.indexOf("where") != -1) {
                wherestr = hmuster_sql.substring(hmuster_sql.indexOf("where"), hmuster_sql.length());
                strsql.append(" " + wherestr);
            }
        }
        if(!"0".equals(task_id)){
            if(strsql.indexOf("where") == -1)
                strsql.append(" where ");
            if(hmuster_sql != null && hmuster_sql.length() > 0)
                strsql.append(" and ");
            //strsql.append(" ins_id in ("+ins_id+")");

            strsql.append(" exists (select null from t_wf_task_objlink where "+table_name+".seqnum=t_wf_task_objlink.seqnum  and "+table_name+".ins_id=t_wf_task_objlink.ins_id  ");
            strsql.append(" and ("+Sql_switcher.isnull("special_node","0")+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+
                    "' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ");

            if(task_id.contains(","))
            {
                strsql.append(" and   task_id in (");
                for(int i=0;i<tasklist.size();i++)
                {
                    if(i!=0)
                        strsql.append(",");
                    strsql.append(tasklist.get(i));
                }
                strsql.append(")");
            }
            else
            {
                if(!"0".equals(task_id))
                {
                    strsql.append(" and  task_id=");
                    strsql.append(task_id);
                }
            }
            strsql.append(" and (state is null or state<>3) ) ");
        }
        StringBuffer orderBy = new StringBuffer();
        if ((dataBo.getParamBo().getInfor_type() == 2 || dataBo.getParamBo().getInfor_type() == 3)
                && (dataBo.getParamBo().getOperationType() == 8 || dataBo
                .getParamBo().getOperationType() == 9)) {
            String key = "b0110";
            if (dataBo.getParamBo().getInfor_type() == 3)
                key = "e01a1";
            orderBy.append("  order by "
                    + Sql_switcher.isnull("to_id", "100000000")
                    + ",case when " + key
                    + "=to_id then 100000000 else a0000 end asc ");
        } else
            orderBy.append(" order by a0000");
        //liuyz bug32425 列表栏目设置更改排序规则，下载模版人员顺序跟随变化
        String subModuleId = "templet_"+tabid;
        TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get(subModuleId);//liuyz bug32425 修正模版下载中人员的顺序没有和栏目设置中设置顺序一致
        if(tableCache!=null)
        {
            String sortSql = tableCache.getSortSql();
            if(sortSql!=null&&sortSql.trim().length()>0)
            {
                orderBy.setLength(0);
                orderBy.append(sortSql);
            }
        }
        strsql.append(orderBy);
        HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿

        HashMap nameMap = new HashMap();
        try {
            this.frowset = dao.search("select name from template_table where tabid=" + tabid);
            if (this.frowset.next())
                cname = this.frowset.getString("name");
            String fields = "";
            if (fieldset_2.length() > 0) {
                String[] fieldset2 = fieldset_2.split(",");
                for (int i = 0; i < fieldset2.length; i++) {
                    String fieldset = fieldset2[i];
                    if (fieldset != null && fieldset.trim().length() > 0) {
                        String[] fieldsetarr =fieldset.split("_",2);
                        if(fields.indexOf(fieldsetarr[0])==-1)
                            fields += "'" + fieldsetarr[0] + "',";
                    }
                }
                if (fields.length() > 0) {
                    fields = fields.substring(0, fields.length() - 1);
                    this.frowset = dao.search("select * from fieldSet where fieldSetId in (" + fields + ") order by displayorder ");
                    //fieldset_2 = "";
                    while (this.frowset.next()) {
                        nameMap.put(this.frowset.getString("fieldSetId"), this.frowset.getString("customdesc"));
                        //fieldset_2 += this.frowset.getString("fieldSetId") + ",";// 这行代码貌似多余的
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //批量修改显示指标的顺序与列表模式下的栏目设置一致 lis 20160723 start
        //if("list".equalsIgnoreCase(view_type)){
        //liuyz bug32425 列表栏目设置更改指标顺序，下载模版指标顺序跟随变化
        TemplateListShowBo listBo = new TemplateListShowBo(this.frameconn, this.userView, Integer.valueOf(tabid));
        int schemeId = listBo.getSchemeId(SafeCode.encode(PubFunc.encrypt("templet_"+tabid)));
        // 从栏目设置中数据库中得到可以显示的
        if(schemeId > 0){
            //bug 32972 修改栏目设置指标名称之后，下载模板excel表名称没变
            String schemeDescSql="select itemid,displaydesc,is_display from t_sys_table_scheme_item where scheme_id=?";
            ArrayList schemeDescList=new ArrayList();
            schemeDescList.add(schemeId);
            Map schemeDescMap=new HashMap();
            ArrayList noshowschemeitemList = new ArrayList();
            RowSet schemeRowSet=null;
            try {
                schemeRowSet=dao.search(schemeDescSql,schemeDescList);
                while(schemeRowSet.next())
                {
                    String schemeItemId=schemeRowSet.getString("itemid");
                    String schemeItemDesc=schemeRowSet.getString("displaydesc");
                    String is_display=schemeRowSet.getString("is_display");
                    if(StringUtils.isNotBlank(schemeItemDesc))
                        schemeDescMap.put(schemeItemId.toUpperCase(),schemeItemDesc);
                    if("0".equals(is_display)) {//不显示的指标
                        noshowschemeitemList.add(schemeItemId.toUpperCase());
                    }
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally {
                PubFunc.closeDbObj(schemeRowSet);
            }
            ArrayList tempFieldItemList=new ArrayList();
            ArrayList<FieldItem> tempFieldList = new ArrayList<FieldItem>();
            HashMap<String, FieldItem> templateSetMap = this.getTempleteSetMap(fieldlist);
            ArrayList<String> itemIdList = listBo.getSchemeItems(schemeId, "1");
            for (int i = 0; i < itemIdList.size(); i++) {
                String itemId = itemIdList.get(i).toUpperCase();
                if(templateSetMap.containsKey(itemId)){
                    if(schemeDescMap.containsKey(itemId))
                    {
                        FieldItem item=templateSetMap.get(itemId);
                        item.setItemdesc(schemeDescMap.get(itemId).toString());
                    }
                    tempFieldList.add(templateSetMap.get(itemId));
                    tempFieldItemList.add(itemId);
                }
            }
            //用户更改模版栏目设置从新保存会丢失新增的字段需要把栏目设置中没有字段增加到最后。
            int indexNum=0;//特殊字段插入到前几列。
            for(int i=0;i<fieldlist.size();i++){
                FieldItem item=(FieldItem) fieldlist.get(i);
                if(!tempFieldItemList.contains(item.getItemid()))
                {
                    //bug 32905 特殊字段下载模版丢失列放到前几列。
                    String itemId=item.getItemid().substring(0,item.getItemid().indexOf("_"));
                    if("codesetid".equalsIgnoreCase(itemId) || "codeitemdesc".equalsIgnoreCase(itemId) || "corcode".equalsIgnoreCase(itemId) || "parentid".equalsIgnoreCase(itemId) || "start_date".equalsIgnoreCase(itemId))
                    {
                        tempFieldList.add(indexNum,item);
                        indexNum++;
                    }
                    else {
                        if(!noshowschemeitemList.contains(item.getItemid().toUpperCase())) {
                            tempFieldList.add(item);
                        }
                    }
                }
            }
            fieldlist = tempFieldList;
        }
        //}
        cname = cname.replace("\\", "").replace("/", "").replaceAll("\\)", "）").replaceAll("\\(", "（").replace(":", "").replace("*", "").replace("?", "").replace("\"", "").replace("<", "").replace(">", "");
        if ("0".equals(onlyflag)) {// 没有唯一性指标
            bo.getExcel(wb, fieldlist, strsql, table_name, tablebo, cname);
        } else {// 有唯一性指标
            bo.getExcel2(wb, fieldlist, strsql, table_name, tablebo, fieldset_2, fieldsetmap, cname, nameMap, onlyname, onlyflag, wherestr,orderBy.toString());
        }

        String outName = this.userView.getUserName() + "_" + cname + ".xls";

        try {
            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        // outName = outName.replace(".xls", "#");
        try {
            getFormHM().put("outName", PubFunc.encrypt(outName));
        } catch (Exception ee) {

        }

        // sheet = null;
        wb = null;
    }
    private String getIns_id(String task_id) {
        String ins_id = "";
        String taskid_ = "";
        RowSet rowSet=null;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String [] taskidarr = task_id.split(",");
        Pattern pattern = Pattern.compile("[0-9]+");
        for (int i = 0; i < taskidarr.length; i++) {
            String taskid = taskidarr[i];
            if (taskidarr[i] == null || taskidarr[i].length() == 0)
                continue;
            if(!pattern.matcher(taskid).matches())
                taskid = PubFunc.decrypt(taskid);
            if(i==0)
                taskid_ = taskid;
            else
                taskid_ += ","+taskid;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("select ins_id from t_wf_task where task_id in ("+taskid_+")");
        try {
            rowSet = dao.search(sb.toString());
            int i = 0;
            while(rowSet.next()){
                int insid = rowSet.getInt("ins_id");
                if(i==0)
                    ins_id = insid+"";
                else
                    ins_id += ","+insid+"";
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet);
        }
        return ins_id;
    }
    /**
     * @author lis
     * @Description: 获得map,key:itemid
     * @date Jul 25, 2016
     * @param fieldList
     * @return
     */
    public HashMap<String, FieldItem> getTempleteSetMap(ArrayList<FieldItem> fieldList){
        HashMap<String, FieldItem> map = new HashMap<String, FieldItem>();
        try {
            for(FieldItem item : fieldList){
                map.put(item.getItemid(), item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
    // public HSSFRichTextString cellStr(String context)
    // {
    //
    // HSSFRichTextString textstr = new HSSFRichTextString(context);
    // return textstr;
    // }
    //
    // public String decimalwidth(int len)
    // {
    //
    // StringBuffer decimal = new StringBuffer("0");
    // if (len > 0)
    // decimal.append(".");
    // for (int i = 0; i < len; i++)
    // {
    // decimal.append("0");
    // }
    // decimal.append("_ ");
    // return decimal.toString();
    // }
    // public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
    // {
    //
    // HSSFCellStyle style = workbook.createCellStyle();
    // style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
    // style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
    // style.setBorderRight(HSSFCellStyle.BORDER_THIN);
    // style.setBorderTop(HSSFCellStyle.BORDER_THIN);
    // style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
    // style.setBottomBorderColor((short) 8);
    // style.setLeftBorderColor((short) 8);
    // style.setRightBorderColor((short) 8);
    // style.setTopBorderColor((short) 8);
    // return style;
    // }
    // private String isPriv_ctrl(HashMap cell_param_map,String field){
    // String sub_domain="";
    // Document doc = null;
    // Element element=null;
    // StringBuffer sb = new StringBuffer();
    // LazyDynaBean bean = (LazyDynaBean)cell_param_map.get(field);
    // if(bean!=null&&bean.get("sub_domain")!=null)
    // sub_domain=(String)bean.get("sub_domain");
    // sub_domain = SafeCode.decode(sub_domain);
    // if(sub_domain!=null&&sub_domain.length()>0)
    // {
    // StringReader reader=new StringReader(sub_domain);
    // try {
    // doc=saxbuilder.build(reader);
    //
    // String xpath="/sub_para/para";
    // XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
    // List childlist=findPath.selectNodes(doc);
    // if(childlist!=null&&childlist.size()>0)
    // {
    // element=(Element)childlist.get(0);
    // String priv =(String)element.getAttributeValue("limit_manage_priv");
    // if("1".equals(priv)){
    // if(!this.userView.isSuper_admin()){
    // if(this.userView.getManagePrivCodeValue()!=null&&this.userView.getManagePrivCodeValue().length()>2)
    // sb.append(" and codeitemid like
    // '"+this.userView.getManagePrivCodeValue()+"%'");
    // }
    // }
    // }
    // } catch (JDOMException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    // return sb.toString();
    // }
}
