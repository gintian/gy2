package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;

/**
 * <p>Title:GzProFilterTrans</p>
 * <p>Description:薪资发放项目过滤</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2007-9-14:下午01:20:22</p>
 *
 * @author FengXiBin
 * @version 4.0
 */
public class GzProFilterTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
            String id = "-1";
            String name = "";
            ArrayList operitem = new ArrayList();//存放不能删除，修改，重命名的过滤项
            String operitems = ",";//存放不能删除，修改，重命名的过滤项
            ArrayList itemfilterlist = new ArrayList();
            ArrayList filterFieldList=new ArrayList();
            //this.getFormHM().put("scopeflag", "2");//重命名不显示 共有，私有

            String model = map.containsKey("model") ? (String) map.get("model") : "";

            if (map.get("chkid") != null) {
                id = (String) map.get("chkid");
                RecordVo vo = new RecordVo("gzitem_filter");
                vo.setInt("id", Integer.parseInt(id));
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                vo = dao.findByPrimaryKey(vo);
                if (vo != null) {
                    name = vo.getString("chz");
                    //区分共有，私有
                    if (vo.getInt("scope") == 0) {
                        this.getFormHM().put("scopeflag", "0"); //共有
                    } else {
                        this.getFormHM().put("scopeflag", "1"); //私有
                    }
                }
                map.remove("chkid");
            } else {

                this.getFormHM().put("scopeflag", "1");//新增，默认选择私有单选按钮

            }
            String salaryid = (String) this.getFormHM().get("salaryid");
            if (StringUtils.isBlank(model)) {
                SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
                gzbo.syncGzTableStruct();
                ArrayList profilterlist = gzbo.getFieldlist();
                profilterlist = this.setfilterlist(profilterlist, gzbo);
                this.getFormHM().put("profilterlist", profilterlist);
                String filterid = gzbo.getFiltersIds(salaryid);
                itemfilterlist = getpProName(filterid, operitem);
                for (int i = 0; i < operitem.size(); i++) {
                    operitems += operitem.get(i) + ",";
                }
                filterFieldList = gzbo.getFilterField(id);
            }else if("history".equalsIgnoreCase(model)){//history 表示为薪资历史数据分析进入
                String[] salaryidItem=salaryid.split(",");
                ArrayList profilterlist=new ArrayList();
                ArrayList filedList=new ArrayList();
                LinkedHashMap<String,Field> fieldMap=new LinkedHashMap<String, Field>();
                LinkedHashMap<String,Field> profilterMap=new LinkedHashMap<String, Field>();
                for (String s : salaryidItem) {
                    if(StringUtils.isNotBlank(s)){
                        SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(s), this.userView);
                        gzbo.syncGzTableStruct();
                        ArrayList list=this.setfilterlist(gzbo.getFieldlist(), gzbo);
                        for (int i = 0; i < list.size(); i++) {
                            Field field=(Field)list.get(i);
                            if(!profilterMap.containsKey(field.getName())){
                                profilterMap.put(field.getName(),field);
                            }
                        }
                        list=gzbo.getFieldlist();
                        for (int i = 0; i < list.size(); i++) {
                            Field field=(Field)list.get(i);
                            if(!fieldMap.containsKey(field.getName())){
                                fieldMap.put(field.getName(),field);
                            }
                        }
                    }
                }
                Iterator iterator = profilterMap.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    profilterlist.add(entry.getValue());
                }
                this.getFormHM().put("profilterlist", profilterlist);

                iterator = fieldMap.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry entry = (Map.Entry) iterator.next();
                    filedList.add(entry.getValue());
                }
                SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn());
                bo.setFieldlist(filedList);
                filterFieldList = bo.getFilterField(id);
                map.put("model","");

                HistoryDataBo hbo=new HistoryDataBo(this.getFrameconn(),this.getUserView());
                itemfilterlist = getpProName(hbo.getFilterIdFromHistory(), operitem);
                for (int i = 0; i < operitem.size(); i++) {
                    operitems += operitem.get(i) + ",";
                }
            }
            this.getFormHM().put("operitems", operitems);

            this.getFormHM().put("filterFieldList", filterFieldList);
            this.getFormHM().put("del_filter_pro", itemfilterlist);
            this.getFormHM().put("chkid", id);
            this.getFormHM().put("chkName", name);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    public ArrayList getpProName(String ids, ArrayList operitem) {
        RowSet rs;
        ArrayList retlist = new ArrayList();
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer buf = new StringBuffer();
        buf.append("select id,chz,cfldname,scope,username from gzitem_filter where ");
        if (ids == null || "".equals(ids)) {
            buf.append(" 1=2 ");
        } else {
            buf.append("id in (");
            buf.append(ids);
            buf.append(")");
        }

        buf.append(" order by norder ");
        try {
            rs = dao.search(buf.toString());
            while (rs.next()) {
                if (this.userView.getUserName().equals(rs.getString("username"))) {
                    String id = rs.getString("id");
                    String chz = rs.getString("chz");

                    if (this.userView.isSuper_admin())
                        chz += "(" + this.userView.getUserName() + ")";

                    CommonData cd = new CommonData(id, chz);
                    retlist.add(cd);
                } else {
                    if (rs.getString("scope") == null || rs.getString("username") == null || "0".equals(rs.getString("scope"))) {
                        String id = rs.getString("id");
                        String chz = rs.getString("chz");
                        if (this.userView.isSuper_admin() && rs.getString("username") != null && rs.getString("username").trim().length() > 0)
                            chz += "(" + rs.getString("username") + ")";
                        CommonData cd = new CommonData(id, chz);
                        retlist.add(cd);
                        if (this.userView.isSuper_admin()) {
//						if(rs.getString("username")!=null&&rs.getInt("scope")!=0){
//						//	operitem.add(id);	
//						}
                        } else {
                            operitem.add(id);
                        }
                    } else {//超级用户组看到私有过滤项目
                        if (this.userView.isSuper_admin()) {
                            String id = rs.getString("id");
                            String chz = rs.getString("chz");
                            if (this.userView.isSuper_admin() && rs.getString("username") != null && rs.getString("username").trim().length() > 0)
                                chz += "(" + rs.getString("username") + ")";
                            CommonData cd = new CommonData(id, chz);
                            retlist.add(cd);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retlist;
    }

    public ArrayList setfilterlist(ArrayList profilterlist, SalaryTemplateBo gzbo) {
        ArrayList retlist = new ArrayList();
        String a01z0Flag = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0, "flag");  // 是否显示停发标识  1：有
        for (int i = 0; i < profilterlist.size(); i++) {
            Field fi = (Field) profilterlist.get(i);
            if (!"a00z0".equalsIgnoreCase(fi.getName()) && !"a00z1".equalsIgnoreCase(fi.getName()) && !"a00z2".equalsIgnoreCase(fi.getName()) && !"a00z3".equalsIgnoreCase(fi.getName())) {
                if (!(this.userView.isSuper_admin() || "1".equals(this.userView.getGroupId())) && !"sp_flag".equalsIgnoreCase(fi.getName()) && !"appprocess".equalsIgnoreCase(fi.getName())) {
                    FieldItem fielditem = DataDictionary.getFieldItem(fi.getName());
                    if (fielditem != null) {
                        if (this.userView.analyseFieldPriv(fi.getName()) == null)
                            continue;
                        if ("0".equals(this.userView.analyseFieldPriv(fi.getName())))
                            continue;
                        if ("".equals(this.userView.analyseFieldPriv(fi.getName())))
                            continue;
                    }
                }
            }
            if ("a01z0".equalsIgnoreCase(fi.getName()) && (a01z0Flag == null || "0".equals(a01z0Flag)))
                continue;

            if (!("A0000".equalsIgnoreCase(fi.getName())
                    || "A0100".equalsIgnoreCase(fi.getName())
                    || "NBASE".equalsIgnoreCase(fi.getName()) ||/*fi.getName().equalsIgnoreCase("appprocess")||*/"add_flag".equalsIgnoreCase(fi.getName())
                    || "a00z0".equalsIgnoreCase(fi.getName()) || "a00z1".equalsIgnoreCase(fi.getName()))) // 用户想看到审批意见  xieguiquan 20100901
            {

                if (SystemConfig.getPropertyValue("clientName") != null && "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))) {
                    if ("sp_flag".equalsIgnoreCase(fi.getName()) || "appprocess".equalsIgnoreCase(fi.getName()))
                        continue;
                }

                retlist.add(fi);
            }
        }
        return retlist;
    }
}
