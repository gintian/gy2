package com.hjsj.hrms.module.selfservice.employeemanager.transction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.transaction.mobileapp.myteam.MyTeamBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Title EmployeeListTrans
 * @Description 员工档案列表数据交易类
 * @Company hjsj
 * @Author houby
 * @Date 2020/04/27
 */

public class EmployeeListTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        HashMap hm=this.getFormHM();
        String loadType = (String) hm.get("loadType");
        String unitid = (String) hm.get("unitid");
        String cond = (String) hm.get("cond");
        String limit = (String) hm.get("limit");
        String page = (String) hm.get("page");
        if(unitid == null || unitid.trim().length() == 0){//获取权限机构为空时，直接空数据返回  wangb  20190515 bug47910
            hm.put("empList", new ArrayList());
            return;
        }
        List empList=getEmpList(loadType, unitid, cond,page,limit);
        hm.put("empList", empList);
    }

    private List getEmpList(String loadType,String unitid,String cond,String page,String limit){
        List empList=new ArrayList();
        String sql="";
        //网络地址
        String url="/w_selfservice";
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            //根据加载类型判断
            if("1".equals(loadType)){
                String orgType="UN";
                CodeItem orgItem = AdminCode.getCode("UN", unitid);
                if(orgItem==null){
                    orgItem= AdminCode.getCode("UM", unitid);
                    orgType="UM";
                }

                MyTeamBo mtb=new MyTeamBo(this.frameconn,this.userView);
                cond=cond==null?"":cond;
                String codeSet="";
//					if(unitid.length()>0){
//						codeSet=orgType+"`"+unitid;
//					}
                if(!"ALL".equals(unitid)){
                    codeSet=orgType+"`"+unitid;
                }
                List list=mtb.searchInfoList(codeSet, cond, url, String.valueOf(page), String.valueOf(limit));
                for(Object o:list){
                    HashMap map=(HashMap)o;
                    map.put("nbase", PubFunc.encrypt((String) map.get("dbpre")));
                    map.remove("dbpre");
                    map.put("a0100", PubFunc.encrypt((String) map.get("a0100")));
                    String org=map.get("org").toString();
                    String photo=map.get("photo").toString();
                    if("/w_selfservice/images/photo.jpg".equals(photo)){
//                        map.remove("photo");
                        map.put("photo", "nophoto");
                    }
                    String[] array=org.split("/");
                    map.put("b0110_name", array[0]);
                    if(array.length>=2) {
                        map.put("e0122_name", array[1].trim());
                    } else {
                        map.put("e0122_name", "");
                    }
                    if(array.length>=3) {
                        map.put("e01a1_name", array[2].trim());
                    } else {
                        map.put("e01a1_name", "");
                    }
                    empList.add(map);
                }
                getTotalCount(loadType,unitid,cond);
            }else{
                HashMap hm=this.getFormHM();
                String a0100=hm.get("a0100")==null?this.userView.getA0100():hm.get("a0100").toString();
                String nbase=hm.get("nbase")==null?this.userView.getDbname():hm.get("nbase").toString();
                hm.put("a0100", a0100);
                hm.put("nbase", nbase);
                String objectId="";
                String nbaseCond="";
                String KCond="";
                String UMCond="";
                String UNCond="";
                HashMap map=new HashMap();
                //获取主审批关系 当前审批人的所有object_id
                sql = "select object_id from t_wf_mainbody  WHERE   relation_id =  (select relation_id from t_wf_relation where  default_line='1' and actor_type = '1') and sp_grade='9' and mainbody_id='"+nbase+a0100+"'";
                this.frowset = dao.search(sql.toString());
                String whereSql = "where 1<>1 ";
                while(this.frowset.next()){
                    objectId=this.frowset.getString("object_id");
                    if("".equals(objectId)) {
                        continue;
                    }
                    if("@K".equalsIgnoreCase(objectId.substring(0, 2))){
                        KCond+="'"+objectId.substring(2)+"',";
                    }else if("UM".equalsIgnoreCase(objectId.substring(0, 2))){
                        UMCond+="'"+objectId.substring(2)+"',";
                    }else if("UN".equalsIgnoreCase(objectId.substring(0, 2))){
                        UNCond+="'"+objectId.substring(2)+"',";
                    }else if(nbase.equalsIgnoreCase(objectId.substring(0, 3))){
                        nbaseCond+="'"+objectId.substring(3)+"',";
                    }
                }
                //通过objectid生成岗位，部门，机构，人员的过滤范围
                if("".equals(KCond)&&"".equals(UMCond)&&"".equals(UNCond)&&"".equals(nbaseCond)) {
                    return empList;
                }

                if(!"".equals(KCond)){
                    whereSql+=" or E01A1 in ("+KCond.substring(0,KCond.length()-1)+") ";
                }
                if(!"".equals(UMCond)){
                    whereSql+=" or E0122 in ("+KCond.substring(0,KCond.length()-1)+") ";
                }
                if(!"".equals(UNCond)){
                    whereSql+=" or B0110 in ("+KCond.substring(0,KCond.length()-1)+") ";
                }
                if(!"".equals(nbaseCond)){
                    whereSql+=" or A0100 in ("+nbaseCond.substring(0,nbaseCond.length()-1)+") ";
                }

                sql="SELECT  A0100,'"+nbase+"' dbpre,A0101 ,B0110,E0122,E01A1,a0000  FROM "+nbase+"A01  "+whereSql;
                this.frowset = dao.search(sql, Integer.parseInt(limit), Integer.parseInt(page));
                while(this.frowset.next()){
                    map = new HashMap();
                    String a0100_new = this.frowset.getString("a0100");
                    String dbpre = this.frowset.getString("dbpre");
                    String b0110 = this.frowset.getString("b0110");
                    String e0122 = this.frowset.getString("e0122");
                    String e01a1 = this.frowset.getString("e01a1");
                    Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
                    String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                    display_e0122 = display_e0122 == null || display_e0122.length() == 0 ? "0" : display_e0122;
                    map.put("dbpre", dbpre);
                    map.put("a0100", a0100_new);
                    map.put("b0110", this.frowset.getString("b0110"));
                    map.put("name", this.frowset.getString("a0101"));
                    b0110 = AdminCode.getCodeName("UN", b0110);
                    b0110 = b0110 == null ? "" : b0110;
                    CodeItem itemid = AdminCode.getCode("UM", e0122, Integer.parseInt(display_e0122));
                    if (itemid != null) {
                        e0122 = itemid.getCodename();
                    }
                    e0122 = e0122 == null ? "" : e0122;
                    e01a1 = AdminCode.getCodeName("@K", e01a1);
                    e01a1 = e01a1 == null ? "" : e01a1;
                    map.put("b0110_name", b0110);
                    map.put("e0122_name", e0122);
                    map.put("e01a1_name", e01a1);
                    StringBuffer photourl = new StringBuffer();
                    String filename = ServletUtilities.createPhotoFile(dbpre + "A00", this.frowset.getString("a0100"), "P", null);
                    if (!"".equals(filename)) {
                        photourl.append(url);
                        photourl.append("/servlet/vfsservlet?fromjavafolder=true&fileid=");
                        photourl.append(PubFunc.encrypt(filename));
                    } else {
                        photourl.append(url);
                        photourl.append("/images/nophoto.png");
                    }
                    map.put("photo", photourl.toString());
                    empList.add(map);
                }
                sql="SELECT  count(1) count FROM "+nbase+"A01  "+whereSql;
                this.frowset = dao.search(sql);
                if(this.frowset.next()){
                    this.formHM.put("totalCount",this.frowset.getInt("count"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empList;
    }

    private int getTotalCount(String loadType,String unitid,String cond){
        int count=0;
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            //根据加载类型判断
            String countSql = "SELECT  count(1) count  FROM UsrA01 WHERE '1'='1' ";
            String orgCond="";
            String hrCond="";
            if(!"".equals(unitid)&&!"ALL".equals(unitid)) {
                orgCond="And UsrA01.b0110 LIKE '"+unitid+"%' ";
            }
            if(!"".equals(cond)) {
                hrCond=getKeywordsWhereStr(cond);
            }
            countSql=countSql+orgCond+hrCond;
            this.frowset = dao.search(countSql);
            if(this.frowset.next()){
                this.formHM.put("totalCount",this.frowset.getInt("count"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return count;
    }


    private String getKeywordsWhereStr(String keywords) throws GeneralException {
        StringBuffer where = new StringBuffer();
        try {
            String[] keyword = keywords.split("\n");
            if(keyword.length<2){
                keyword = keywords.split(" ");
            }
            int flag = 0;
            where.append(" and (  ");// 姓名
            for (int i = 0; i < keyword.length; i++) {
                if ("".equals(keyword[i].trim())) {
                    flag++;
                    continue;
                }
                if (i == flag) {
                    where.append(" a0101 like '%" + keyword[i] + "%' ");
                } else {
                    where.append(" or a0101 like '%" + keyword[i] + "%' ");
                }
            }
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            FieldItem item = DataDictionary.getFieldItem(onlyname);
            if (item != null && !"a0101".equalsIgnoreCase(onlyname) && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim()))
                        continue;
                    where.append(" or " + onlyname + " like '%" + keyword[i] + "%' ");
                }
            }
            String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
            item = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
            if (!(pinyin_field == null || "".equals(pinyin_field)
                    || "#".equals(pinyin_field) || item == null || "0".equals(item.getUseflag()))
                    && !"a0101".equalsIgnoreCase(pinyin_field)
                    && !"0".equals(userView.analyseFieldPriv(item.getItemid()))) {
                for (int i = 0; i < keyword.length; i++) {
                    if ("".equals(keyword[i].trim()))
                        continue;
                    where.append(" or " + pinyin_field + " like '%" + keyword[i] + "%' ");
                }
            }
            if(keyword.length>1){
                where.append(" or a0101 like '%" + keywords + "%' ");
                where.append(" or " + onlyname + " like '%" + keywords + "%' ");
                where.append(" or " + pinyin_field + " like '%" + keywords + "%' ");
            }
            where.append(")");
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return where.toString();
    }
}
