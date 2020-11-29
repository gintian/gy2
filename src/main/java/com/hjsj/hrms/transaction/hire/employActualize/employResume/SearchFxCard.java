package com.hjsj.hrms.transaction.hire.employActualize.employResume;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class SearchFxCard extends IBusiness {

    public void execute() throws GeneralException {
        // String a0100=(String)this.getFormHM().get("a0100");
        RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
        String dbname = "";
        if (vo != null)
            dbname = vo.getString("str_value");
        else
            throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
        String str_sql = (String) this.getFormHM().get("str_sql");
        String str_whl = (String) this.getFormHM().get("str_whl");
        String order_str = (String) this.getFormHM().get("order_str");
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String isSelectedAll = (String) hm.get("isSelectedAll");
        String a0100 = (String) hm.get("a0100");
        a0100 = SafeCode.decode(a0100);// ~2700000009将这种转成十进制
        String sql = "";
        sql = str_sql + str_whl + order_str;
        a0100 = PubFunc.keyWord_reback(a0100);// 传来的出现中文的符号用这个转
        String[] a0100s = a0100.split(",");
        a0100 = "";
        for (int i = 0; i < a0100s.length;i++) {
            String personid = a0100s[i];
            personid = personid.substring(1, personid.length()-1);
            a0100 += "'" + PubFunc.decrypt(personid) + "',";
        }
        if ("0".equalsIgnoreCase(isSelectedAll))
            sql = str_sql + str_whl + "and " + dbname + "A01.a0100 in(" + a0100 + "'meibanfa')" + order_str;
        ArrayList fieldList = new ArrayList();
        fieldList = getFieldList2(sql, dbname);
        this.getFormHM().put("resumeFieldsList", fieldList);

    }

    private ArrayList getFieldList2(String sql, String dbname) throws GeneralException {
        ArrayList list = new ArrayList();

        ContentDAO dao = new ContentDAO(this.getFrameconn());
        HashMap s = new HashMap();
        HashMap b = doMethod();

        try {
            this.frowset = dao.search(sql);
            ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.getFrameconn());
            HashMap xmlMap = parameterXMLBo.getAttributeValues();
            String tag = "";
            String temp = "";
            String z0336 = "";
            HashMap c = new HashMap();
            while (this.frowset.next()) {

                String key = "";
                String value = "";
                String a0100 = this.frowset.getString("a0100");
                String a0101 = this.frowset.getString("a0101");
                if (a0101 == null)
                    a0101 = "";
                tag = "<NBASE>" + dbname + "</NBASE><ID>" + a0100 + "</ID><NAME>" + a0101 + "</NAME>";
                z0336 = (String) b.get(a0100);
                if (z0336 == null)// ||(temp+",").indexOf(","+z0336+",")!=-1
                    z0336 = "-1";
                // RecordVo vo = new RecordVo("rname");
                key = "CARDTABLE_" + z0336;

                if (xmlMap.get(key) != null)
                    value = (String) xmlMap.get(key);
                value = PubFunc.keyWord_reback(value);
                if (!"".equals(temp) && ("#".equals(value) || "".equals(value.trim()))) {
                    z0336 = temp.substring(temp.length() - 2);
                }
                if (s.get(z0336) != null) {
                    if (c.get(a0100) == null) {
                        LazyDynaBean bean = (LazyDynaBean) s.get(z0336);

                        String a0100s = (String) bean.get("a0100s");
                        String a0101s = (String) bean.get("a0101s");

                        String tags = (String) bean.get("tagStr");
                        String a0101ss = (String) bean.get("a0101s");
                        a0100s += "`" + a0100;
                        a0101ss += "," + a0101;
                        tags += "`<NBASE>" + dbname + "</NBASE><ID>" + a0100 + "</ID><NAME>" + a0101 + "</NAME>";
                        String[] dd = a0101s.split(",");
                        if (dd.length < 10)
                            a0101s += "," + a0101;
                        if (dd.length == 10)
                            a0101s += ",......";

                        bean.set("tagStr", tags);
                        bean.set("a0100s", a0100s);
                        bean.set("a0101s", a0101s);
                        bean.set("a0101ss", a0101ss);
                        s.put(z0336, bean);

                    }
                    c.put(a0100, a0101);
                    // String[] dd=a0100s.split("`");
                } else {
                    String tableName = "";
                    if ("-1".equals(z0336)) {
                        RecordVo vo = new RecordVo("rname");
                        key = "preview_table";
                        if (xmlMap.get(key) != null)
                            value = (String) xmlMap.get(key);
                        value = PubFunc.keyWord_reback(value);
                        temp += "," + z0336;
                        if ("#".equals(value) || "".equals(value.trim())) {
                            // tableName="ooooo";

                            value = "-1";
                        } else {
                            vo.setInt("tabid", Integer.parseInt(value));
                            vo = dao.findByPrimaryKey(vo);
                            tableName = vo.getString("name");
                        }

                    } else {
                        RecordVo vo = new RecordVo("rname");
                        key = "CARDTABLE_" + z0336;

                        if (xmlMap.get(key) != null)
                            value = (String) xmlMap.get(key);
                        value = PubFunc.keyWord_reback(value);
                        if ("#".equals(value) || "".equals(value.trim())) {
                            temp += "," + z0336;// ***
                            key = "preview_table";
                            if (xmlMap.get(key) != null)
                                value = (String) xmlMap.get(key);
                            if ("#".equals(value) || "".equals(value.trim())) {
                                // tableName="ooooo";
                                value = "-1";
                            } else {

                                vo.setInt("tabid", Integer.parseInt(value));
                                vo = dao.findByPrimaryKey(vo);
                                tableName = vo.getString("name");
                            }

                        } else {
                            vo.setInt("tabid", Integer.parseInt(value));
                            try {
                                vo = dao.findByPrimaryKey(vo);
                                tableName = vo.getString("name");
                            } catch (Exception e) {
                                value = "-1";
                            }

                        }
                    }
                    LazyDynaBean bean = new LazyDynaBean();

                    bean.set("tagStr", tag);
                    bean.set("a0100s", a0100);
                    bean.set("a0101s", a0101);
                    bean.set("a0101ss", a0101);
                    bean.set("rr", tableName);
                    bean.set("tableId", value);
                    s.put(z0336, bean);
                    c.put(a0100, a0101);
                }
            }
            Set keySet = s.keySet();
            for (Iterator t = keySet.iterator(); t.hasNext();) {
                String key = (String) t.next();
                LazyDynaBean bean = (LazyDynaBean) s.get(key);
                list.add(bean);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return list;
    }

    private HashMap doMethod() {
        HashMap hashmap = new HashMap();
        String sql = "select z0336,a0100 from Z03,zp_pos_tache where Z03.Z0301=zp_pos_tache.ZP_POS_ID";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                hashmap.put(this.frowset.getString("a0100"), this.frowset.getString("z0336"));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashmap;
    }

}
