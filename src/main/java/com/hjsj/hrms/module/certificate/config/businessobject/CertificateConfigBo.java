package com.hjsj.hrms.module.certificate.config.businessobject;

import com.hjsj.hrms.businessobject.sys.dbinit.FormationBase;
import com.hjsj.hrms.businessobject.sys.fieldsubset.IndexBo;
import com.hjsj.hrms.businessobject.sys.fieldsubset.SubSetBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 证书管理（证照）参数配置类
 * 该类负责参数配置数据的读取、保存（修改）
 */
public class CertificateConfigBo {
    private Connection conn;

    //证书人员库
    private ArrayList<String> certNbase;

    //证书类别代码类
    private String certCategoryCode;

    //证书信息集
    private String certSubset;

    //证书信息集证书类别指标
    private String certCategoryItemId;

    //证书信息集证书编号指标
    private String certNOItemId;

    //证书信息集证书到期日期指标
    private String certEndDateItemId;

    //证书信息集证书名称
    private String certName;

    //证书信息集证书状态
    private String certStatus;

    //证书信息集证书所属组织
    private String certOrganization;

    //证书借阅记录子集
    private String certBorrowSubset;
    //证书是否借出
    private String certBorrowState;

    //证书数据集合
    private HashMap certMap;
    private UserView userView;

    public CertificateConfigBo() {
        //默认构造函数，供证书类别代码维护页面以反射机制调用
    }

    public CertificateConfigBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        ReadParam();
    }

    /**
     * 保存参数，有则更新，无则新增
     */
    public String saveCertificateConfig(String certValue) {
        String info = "";
        RowSet rs = null;
        try {
            ArrayList<String> params = new ArrayList<String>();
            JSONObject jb = JSONObject.fromObject(certValue);
            Map<String, Object> map = (Map<String, Object>)jb;

            String certSubsetNew = (String) map.get("cert_subset");
            // 61584 当证书子集发生变化时，删除档案管理保存的所有栏目设置
            if(StringUtils.isNotBlank(this.certSubset) && !this.certSubset.equalsIgnoreCase(certSubsetNew)) {
                // 校验档案管理栏目设置标识 默认不校验
                String checkFlag = (null==map.get("check_table_scheme")) ? "0" : (String) map.get("check_table_scheme");
                String checkInfo = this.checkTableScheme(checkFlag);
                if("2".equals(checkInfo)) {
                    info = ResourceFactory.getProperty("certificate.info.config.msg");
                    return info;
                }
                this.deleteTableScheme("certificateManage_001");
            }
            String certBorrowSubsetNew = (String) map.get("cert_borrow_subset");
            //当证书借阅子集发生变化时，删除借阅台帐保存的所有栏目设置
            if(StringUtils.isNotBlank(this.certBorrowSubset) && !this.certBorrowSubset.equalsIgnoreCase(certBorrowSubsetNew)) {
                this.deleteTableScheme("borrowedList");
            }

            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select CONSTANT from constant where CONSTANT ='CERT_PARAM'";
            rs = dao.search(sql);
            StringBuffer saveSql = new StringBuffer("");
            params.add(certValue);
            if (rs.next()) {
                saveSql.append("UPDATE constant set STR_VALUE = ? ");
                saveSql.append(" WHERE CONSTANT ='CERT_PARAM' ");
                dao.update(saveSql.toString(), params);
            } else {
                saveSql.append("INSERT INTO constant (CONSTANT, STR_VALUE) ");
                saveSql.append("VALUES ('CERT_PARAM', ? )");
                dao.insert(saveSql.toString(), params);
            }

            changeItemState(certValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return info;
    }
    /**
     * 把证照管理参数配置页面中配置的证照子集的指标强制更改为必填项
     * @param certValue
     *              配置参数中保存的参数
     */
    private void changeItemState(String certValue) {
        try {
            JSONObject js = JSONObject.fromObject(certValue);
            String fieldSetId = js.getString("cert_subset");
            String certCategoryItemid = js.getString("cert_category_itemid");
            String certNoItemid = js.getString("cert_no_itemid");
            String certName = js.getString("cert_name");
            String certStatus = js.getString("cert_status");
            String certOrganization = js.getString("cert_organization");
            String certBorrowState = js.getString("cert_borrow_state");
            //把数据库中证照子集的设置的指标强制改为必填项
            StringBuffer updateSql = new StringBuffer();
            updateSql.append("update fielditem set reserveitem=1");
            updateSql.append("where fieldsetid=? and itemid in (?,?,?,?,?,?)");
            ArrayList<String> paramList = new ArrayList<String>();
            paramList.add(fieldSetId);
            paramList.add(certCategoryItemid);
            paramList.add(certNoItemid);
            paramList.add(certName);
            paramList.add(certStatus);
            paramList.add(certOrganization);
            paramList.add(certBorrowState);

            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(updateSql.toString(), paramList);
            //把业务字典中涉及到的指标强制改为必填
            FieldItem fi = DataDictionary.getFieldItem(certCategoryItemid, fieldSetId);
            fi.setFillable(true);
            fi = DataDictionary.getFieldItem(certNoItemid, fieldSetId);
            fi.setFillable(true);
            fi = DataDictionary.getFieldItem(certName, fieldSetId);
            fi.setFillable(true);
            fi = DataDictionary.getFieldItem(certStatus, fieldSetId);
            fi.setFillable(true);
            fi = DataDictionary.getFieldItem(certOrganization, fieldSetId);
            fi.setFillable(true);
            fi = DataDictionary.getFieldItem(certBorrowState, fieldSetId);
            fi.setFillable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 从数据库加载解析参数，初始化个参数值
     */
    public void ReadParam() {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;

        try {
            String sql = "select  STR_VALUE  from  constant  where  CONSTANT ='CERT_PARAM'";
            rs = dao.search(sql);

            if (rs.next()) {
                String certValue = rs.getString("STR_VALUE");
                certMap = (HashMap) JSON.parse(certValue);
                JSONArray jsonArray = JSONArray.fromObject(certMap.get("cert_nbase"));
                ArrayList<String> nbaseList = (ArrayList<String>) JSONArray.toCollection(jsonArray);

                this.setCertSubset((String) certMap.get("cert_subset"));
                this.setCertNOItemId((String) certMap.get("cert_no_itemid"));
                this.setCertBorrowSubset((String) certMap.get("cert_borrow_subset"));
                this.setCertCategoryItemId((String) certMap.get("cert_category_itemid"));
                this.setCertEndDateItemId((String) certMap.get("cert_enddate_itemid"));
                this.setCertName((String) certMap.get("cert_name"));
                this.setCertStatus((String) certMap.get("cert_status"));
                this.setCertOrganization((String) certMap.get("cert_organization"));
                this.setCertCategoryCode((String) certMap.get("cert_category_code"));
                this.setCertBorrowState((String) certMap.get("cert_borrow_state"));
                this.SetCertNbase(nbaseList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
    }

    public String getAddSubset(String certCategoryCode) {
        String dev_flag = SystemConfig.getPropertyValue("dev_flag");
        SubSetBo subset = new SubSetBo(this.conn);
        IndexBo indexBo = new IndexBo(this.conn);
        ArrayList<CommonData> subsetList = subset.getsubsetList("A");

        //选择的所有子集
        ArrayList<String> fieldlist = new ArrayList<String>();
        String codevalue = subset.getcodevalue(subsetList, dev_flag);
        try {
            String name = "证书借阅信息集";
            String newName = name;

            for (int i = 1; subset.checkname(newName); i++) {
                newName = name + String.valueOf(i);
            }

            String tag = "set_a";
            SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.conn);
            ArrayList<String> list = infoxml.getView_tag(tag); //获取子集分类名称集合
            int j = 1;

            for (int i = 0; i < list.size(); i++) {
                if (newName.equals(list.get(i))) {
                    newName = name + String.valueOf(j);
                    j++;
                    i = 0;
                }
            }

            String fieldsetid = codevalue.toUpperCase(); //子集A01,B01...
            int cdx = subset.initial(fieldsetid);
            int initid = subset.initorder(fieldsetid);
            subset.setmuster(newName, "0", fieldsetid, cdx, initid, "0", "");

            ArrayList<FieldItem> fielditemlist = getCategoryFieldItem(certCategoryCode, fieldlist, fieldsetid);
            String itemid;
            String fieldname;
            FieldItem fielditem;
            String itemlength;
            String codesetid;
            String itemtype;

            for (int i = 0; i < fielditemlist.size(); i++) {
                fielditem = (FieldItem) fielditemlist.get(i);
                fieldname = (String) fielditem.getItemdesc();
                itemid = (String) fielditem.getItemid();
                fieldsetid = (String) fielditem.getFieldsetid();
                itemlength = String.valueOf(fielditem.getItemlength());
                codesetid = (String) fielditem.getCodesetid();
                itemtype = (String) fielditem.getItemtype();

                if ("A".equalsIgnoreCase(itemtype)) {
                    if (StringUtils.isEmpty(codesetid) ||
                            "0".equals(codesetid)) {
                        indexBo.wordApp(itemid, fieldname, "", itemlength, "A",
                                fieldsetid, cdx, "0");
                    } else {
                        indexBo.codeApp(itemid, fieldname, "", cdx, fieldsetid,
                                codesetid, itemlength, "A", "0");
                    }
                } else if ("D".equalsIgnoreCase(itemtype)) {
                    indexBo.dateApp(itemid, fieldname, "", cdx, fieldsetid,
                            itemlength, "D", "0");
                } else if ("M".equalsIgnoreCase(itemtype)) {
                    indexBo.bzApp(itemid, fieldname, "", cdx, fieldsetid, "M",
                            "0", itemlength, 0);
                }

                DataDictionary.addFieldItem(fieldsetid, fielditem, 1);
            }

            String type = "0"; //判断指标是否构库
            String infor = "A"; //表示某一个指标 A,B,K
            ContentDAO dao = new ContentDAO(this.conn);
            FormationBase base = new FormationBase(dao, this.conn);

            base.formation(type, infor, fieldsetid, fieldlist, userView);

            DBMetaModel dbm = new DBMetaModel(this.conn);
            ArrayList<String> dblist = DataDictionary.getDbpreList();

            for (int i = 0; i < dblist.size(); i++) {
                String dbpre = dblist.get(i);
                dbm.reloadTableModel(dbpre + fieldsetid);
            }

            // 刷新系统参数中的子集信息
            SaveInfo_paramXml infoxmls = new SaveInfo_paramXml(this.conn);
            infoxmls.reOrederSet();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return codevalue;
    }

    public void checkCategoryFieldItem() throws GeneralException {
        ArrayList<String> fieldlist = new ArrayList<String>();
        ArrayList<FieldItem> fielditemlist = getCategoryFieldItem(certCategoryCode, fieldlist, certBorrowSubset);
        FieldItem temp = null;
        try {
            for (FieldItem fieldItem : fielditemlist) {
                temp = DataDictionary.getFieldItem(fieldItem.getItemid(), certBorrowSubset);
                if (temp == null || !"1".equals(temp.getUseflag())) {
                    throw new Exception("证书借阅子集“" + fieldItem.getItemdesc() + "”无效！");
                } else {
                    if (!fieldItem.getItemtype().equalsIgnoreCase(temp.getItemtype())) {
                        throw new Exception("证书借阅子集“" + fieldItem.getItemdesc() + "”指标类型不正确！");
                    } else if (!fieldItem.getCodesetid().equalsIgnoreCase(temp.getCodesetid())) {
                        throw new Exception("证书借阅子集“" + fieldItem.getItemdesc() + "”指标代码类不正确！");
                    }
                }
            }
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private ArrayList<FieldItem> getCategoryFieldItem(String certCategoryCode, ArrayList<String> fieldlist, String fieldsetid) {
        ArrayList<FieldItem> fielditemlist = new ArrayList<FieldItem>();
        String itemid = fieldsetid + "01";
        String fieldname = "借阅证书类别";
        FieldItem fielditem = new FieldItem();
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(30);
        fielditem.setItemtype("A");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setCodesetid(certCategoryCode);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        fieldname = "借阅证书编号";
        fieldname = isSameName(fieldname);
        itemid = fieldsetid + "03";
        fielditem.setItemlength(50);
        fielditem.setCodesetid("0");
        fielditem.setItemtype("A");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "05";
        fieldname = "借阅证书名称";
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(100);
        fielditem.setItemtype("A");
        fielditem.setCodesetid("0");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "07";
        fielditem.setItemlength(50);
        fielditem.setCodesetid("0");
        fielditem.setItemtype("A");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc("持证人姓名");
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "09";
        fieldname = "借用日期";
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(10);
        fielditem.setCodesetid("0");
        fielditem.setItemtype("D");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "11";
        fielditem.setCodesetid("0");
        fieldname = "预计归还日期";
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(10);
        fielditem.setItemtype("D");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "13";
        fielditem.setCodesetid("0");
        fieldname = "借阅理由";
        fieldname = isSameName(fieldname);
        fielditem.setItemtype("M");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "15";
        fieldname = "实际归还日期";
        fielditem.setCodesetid("0");
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(10);
        fielditem.setItemtype("D");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "17";
        fieldname = "归还人姓名";
        fielditem.setCodesetid("0");
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(50);
        fielditem.setItemtype("A");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "19";
        fieldname = "审批状态";
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(30);
        fielditem.setItemtype("A");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setCodesetid("23");
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "21";
        fieldname = "审批（退回）意见";
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(10);
        fielditem.setItemtype("M");
        fielditem.setCodesetid("0");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "23";
        fieldname = "归还标识";
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(30);
        fielditem.setItemtype("A");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setCodesetid("45");
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);

        fielditem = new FieldItem();
        itemid = fieldsetid + "25";
        fieldname = "归还说明";
        fieldname = isSameName(fieldname);
        fielditem.setItemlength(10);
        fielditem.setItemtype("M");
        fielditem.setFieldsetid(fieldsetid);
        fielditem.setItemid(itemid);
        fielditem.setItemdesc(fieldname);
        fielditem.setCodesetid("0");
        fielditem.setExplain("");
        fieldlist.add(itemid);
        fielditemlist.add(fielditem);
        return fielditemlist;
    }

    public ArrayList<HashMap<String, String>> getNbaseList() {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        HashMap<String, String> nbaseMap = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            String sql = "SELECT * FROM  DBNAME ORDER BY dbid";
            rs = dao.search(sql);

            while (rs.next()) {
                nbaseMap = new HashMap<String, String>();
                nbaseMap.put(rs.getString("DBNAME"), rs.getString("PRE"));
                list.add(nbaseMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return list;
    }

    private String isSameName(String fieldname) {
        IndexBo indexBo = new IndexBo(this.conn);
        String newFieldname;

        if (indexBo.checkname(fieldname)) {
            for (int i = 1; indexBo.checkname(fieldname); i++) {
                fieldname = fieldname + String.valueOf(i);
            }
        }

        return fieldname;
    }

    public ArrayList<CommonData> getCertCategoryCodeList() {
        RowSet rs = null;
        ArrayList<CommonData> list = new ArrayList<CommonData>();
        ContentDAO dao = new ContentDAO(this.conn);
        CommonData cd = new CommonData("", "请选择... ");
        list.add(cd);
        String sql = "select codesetid,codesetdesc from codeset ORDER BY codesetid ASC";

        try {
            rs = dao.search(sql);
            while (rs.next()) {
                String code = rs.getString("codesetid") + ":" +
                        rs.getString("codesetdesc");
                list.add(new CommonData(rs.getString("codesetid"), code));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return list;
    }

    /**
     * 获取子集下拉集合
     * @param flag	=0全部=1支持附件 =2符合借阅子集特征的
     * @return
     */
    public ArrayList<HashMap<String, String>> getFieldsetlist(String flag) {
        ArrayList<FieldSet> fieldsetlist = new ArrayList<FieldSet>();
        ArrayList<HashMap<String, String>> fieldSubsetlist = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("dataValue", "");
        map.put("dataName", "请选择...");
        fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,
                Constant.EMPLOY_FIELD_SET);
        fieldSubsetlist.add(0, map);

        for (int i = 0; i < fieldsetlist.size(); i++) {
            FieldSet fieldset = fieldsetlist.get(i);
            // 未构库指标不能出现在页面
            if (!"1".equalsIgnoreCase(fieldset.getUseflag()))
                continue;
            // 支持附件
            if ("1".equalsIgnoreCase(flag) && !"1".equals(fieldset.getMultimedia_file_flag()))
                continue;
            // 系统项多媒体子集过滤掉
            if ("A00".equalsIgnoreCase(fieldset.getFieldsetid()))
                continue;

            if ("2".equalsIgnoreCase(flag)) {
                // 借阅子集需要Axx19为审批状态指标
                FieldItem item = DataDictionary.getFieldItem(fieldset.getFieldsetid() + "19");
                if (item == null)
                    continue;

                if (!"1".equalsIgnoreCase(item.getUseflag()))
                    continue;

                if (!"23".equalsIgnoreCase(item.getCodesetid()))
                    continue;

                // 借阅子集需要Axx23为归还标识指标
                item = DataDictionary.getFieldItem(fieldset.getFieldsetid() + "23");
                if (item == null)
                    continue;

                if (!"1".equalsIgnoreCase(item.getUseflag()))
                    continue;

                if (!"45".equalsIgnoreCase(item.getCodesetid()))
                    continue;
            }

            String fieldsetdesc = (String) fieldset.getFieldsetid().toUpperCase() +":" + (String) fieldset.getCustomdesc();
            map = new HashMap<String, String>();
            map.put("dataValue", fieldset.getFieldsetid());
            map.put("dataName", fieldsetdesc);
            fieldSubsetlist.add(map);
        }

        return fieldSubsetlist;
    }
    /**
     * 校验参数
     * @param certMap
     * @return
     * @throws GeneralException
     */
    public String checkCertMap(HashMap certMap) throws GeneralException {

        String flag = "0";
        try {
            // 48667 若为null直接返回
            if(null == certMap || certMap.isEmpty())
                return "1";

            // 校验  证书借阅信息集   是否被删除或未构库
            String browSet = (String)certMap.get("cert_borrow_subset");
            FieldSet fieldset = DataDictionary.getFieldSetVo(browSet);
            if(null == fieldset || !"1".equalsIgnoreCase(fieldset.getUseflag()))
                certMap.put("cert_borrow_subset", "");
            // 校验  证书信息集   是否被删除或未构库
            String fieldsetid = (String) certMap.get("cert_subset");
            FieldSet certSubset = DataDictionary.getFieldSetVo(fieldsetid);
            if(null == certSubset || !"1".equalsIgnoreCase(certSubset.getUseflag())) {
                flag = "1";
                certMap.put("cert_subset", "");
                certMap.put("cert_status", "");
                certMap.put("cert_organization", "");
                certMap.put("cert_borrow_state", "");
                certMap.put("cert_no_itemid", "");
                certMap.put("cert_category_itemid", "");
                certMap.put("cert_enddate_itemid", "");
                certMap.put("cert_name", "");
            }else {

                String flaginfo = getItemidUserFlag((String) certMap.get("cert_status"), fieldsetid);
                if("0".equals(flaginfo)) {
                    certMap.put("cert_status", "");
                    flag = "1";
                }
                flaginfo = getItemidUserFlag((String) certMap.get("cert_organization"), fieldsetid);
                if("0".equals(flaginfo)) {
                    certMap.put("cert_organization", "");
                    flag = "1";
                }
                flaginfo = getItemidUserFlag((String) certMap.get("cert_borrow_state"), fieldsetid);
                if("0".equals(flaginfo)) {
                    certMap.put("cert_borrow_state", "");
                    flag = "1";
                }
                flaginfo = getItemidUserFlag((String) certMap.get("cert_no_itemid"), fieldsetid);
                if("0".equals(flaginfo)) {
                    certMap.put("cert_no_itemid", "");
                    flag = "1";
                }
                flaginfo = getItemidUserFlag((String) certMap.get("cert_category_itemid"), fieldsetid);
                if("0".equals(flaginfo)) {
                    certMap.put("cert_category_itemid", "");
                    flag = "1";
                }
                flaginfo = getItemidUserFlag((String) certMap.get("cert_enddate_itemid"), fieldsetid);
                if("0".equals(flaginfo)) {
                    certMap.put("cert_enddate_itemid", "");
                    flag = "1";
                }
                flaginfo = getItemidUserFlag((String) certMap.get("cert_name"), fieldsetid);
                if("0".equals(flaginfo)) {
                    certMap.put("cert_name", "");
                    flag = "1";
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return flag;
    }
    /**
     * 获取指标是否存在 构库
     * @param itemid
     * @param fieldSetid
     * @return
     */
    public String getItemidUserFlag(String itemid, String fieldSetid) {

        FieldItem fieldItem = DataDictionary.getFieldItem(itemid, fieldSetid);
        if(null == fieldItem)
            return "0";
        if(!"1".equals(fieldItem.getUseflag()))
            return "0";

        return "1";
    }
    /**
     * 保存前校验 证照子集-档案管理、借阅子集-借阅台账是否存在栏目设置
     * @param checkFlag		=1需要校验 ；=其他不需校验
     * @return info	 		=1没有栏目设置；=2存在
     */
    public String checkTableScheme(String checkFlag) {
        String info = "1";
        if("1".equals(checkFlag)) {
            RowSet rs = null;
            try {
                ContentDAO dao = new ContentDAO(this.conn);
                String sql = "select scheme_id from t_sys_table_scheme where submoduleid = 'certificateManage_001'";
                rs = dao.search(sql);
                if(rs.next()) {
                    info = "2";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PubFunc.closeResource(rs);
            }
        }
        return info;
    }
    /**
     * 删除栏目设置
     * @param submoduleId		=certificateManage_001 档案管理；=borrowedList 借阅台账
     * @return
     */
    public String deleteTableScheme(String submoduleId) {
        try {
            ArrayList<String> list = new ArrayList<String>();
            list.add(submoduleId);
            ContentDAO dao = new ContentDAO(this.conn);
            String deleteSql = "delete from t_sys_table_scheme_item "
                    + "where scheme_id in (select scheme_id from t_sys_table_scheme where submoduleid=?)";
            dao.delete(deleteSql, list);
            deleteSql = "delete from t_sys_table_scheme where submoduleid=?";
            dao.delete(deleteSql, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public String getCertSubset() {
        return certSubset;
    }

    public void setCertSubset(String certSubset) {
        this.certSubset = certSubset;
    }

    public String getCertCategoryItemId() {
        return certCategoryItemId;
    }

    public void setCertCategoryItemId(String certCategoryItemId) {
        this.certCategoryItemId = certCategoryItemId;
    }

    public String getCertNOItemId() {
        return certNOItemId;
    }

    public void setCertNOItemId(String certNOItemId) {
        this.certNOItemId = certNOItemId;
    }

    public String getCertEndDateItemId() {
        return certEndDateItemId;
    }

    public void setCertEndDateItemId(String certEndDateItemId) {
        this.certEndDateItemId = certEndDateItemId;
    }

    public String getCertBorrowSubset() {
        return certBorrowSubset;
    }

    public void setCertBorrowSubset(String certBorrowSubset) {
        this.certBorrowSubset = certBorrowSubset;
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(String certStatus) {
        this.certStatus = certStatus;
    }

    public HashMap getCertMap() {
        return certMap;
    }

    public void setCertMap(HashMap certMap) {
        this.certMap = certMap;
    }

    public String getCertOrganization() {
        return certOrganization;
    }

    public void setCertOrganization(String certOrganization) {
        this.certOrganization = certOrganization;
    }

    public ArrayList<String> getCertNbase() {
        return certNbase;
    }

    public void SetCertNbase(ArrayList<String> certNbase) {
        this.certNbase = certNbase;
    }

    public String getCertCategoryCode() {
        return certCategoryCode;
    }

    public void setCertCategoryCode(String certCategoryCode) {
        this.certCategoryCode = certCategoryCode;
    }

    public UserView getUserView() {
        return userView;
    }

    public void setUserView(UserView userView) {
        this.userView = userView;
    }

    public String getCertBorrowState() {
        return certBorrowState;
    }

    public void setCertBorrowState(String certBorrowState) {
        this.certBorrowState = certBorrowState;
    }
}
