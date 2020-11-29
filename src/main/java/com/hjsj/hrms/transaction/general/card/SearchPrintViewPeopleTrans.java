package com.hjsj.hrms.transaction.general.card;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPrintViewPeopleTrans extends IBusiness {

    public void execute() throws GeneralException {
        ArrayList personlist = new ArrayList();
        try {
            String dbname = (String) this.getFormHM().get("dbname");
            String inforkind = (String) this.getFormHM().get("inforkind");

            String id = (String) this.getFormHM().get("id");
            id = id != null && id.length() > 0 ? id : "";

            String flag = (String) this.getFormHM().get("flag");//区分标志，=hire是在招聘外网调用打印，否则为空
            //zxj 20140915 招聘模块传入的是密文需解密，其它模块如同样的情况，请自行放开此条件
            //if ("hire".equalsIgnoreCase(flag)) {//招聘外网进行调用时不用解密,传递过来是就是明文
            if(null==flag){
                if(dbname!=null && dbname.length() > 3)//人员库暂时固定长度为3位，超过3位长度的皆默认为密文      add   chengxg   2015-07-25
                    dbname = PubFunc.decrypt(dbname);
                
                if(id!=null && id.length() > 0 && !id.matches("^\\d+$"))
                    id = PubFunc.decrypt(id);
            }

            StringBuffer sql = new StringBuffer();
            String[] ids = id.split("`");
            StringBuffer whereIN = new StringBuffer();
            for (int i = 0; i < ids.length; i++) {
                whereIN.append("'" + ids[i] + "',");
            }

            if (whereIN.length() > 0)
                whereIN.setLength(whereIN.length() - 1);
            else
                whereIN.append("''");

            if (flag != null && !"".equals(flag) && "hire".equalsIgnoreCase(flag)) {
                //在招聘中打印准考证时，自动生成准考证号
                ParameterXMLBo xmlBo = new ParameterXMLBo(this.getFrameconn());
                HashMap map = xmlBo.getAttributeValues();
                String isAttach = "0";
                if (map.get("attach") != null && ((String) map.get("attach")).length() > 0)
                    isAttach = (String) map.get("attach");
                
                EmployNetPortalBo bo = new EmployNetPortalBo(this.getFrameconn(), isAttach);
                ArrayList alist = bo.getZpFieldList();
                HashMap fieldMap = (HashMap) alist.get(1);
                ArrayList fieldList = (ArrayList) fieldMap.get("A01");//DataDictionary.getFieldList("A01",Constant.USED_FIELD_SET);	
                if (fieldList == null)
                    fieldList = (ArrayList) fieldMap.get("a01");
                
                IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                for (int i = 0; i < fieldList.size(); i++) {
                    String itemid = (String) fieldList.get(i);
                    FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
                    if (!item.isSequenceable())
                        continue;
                    
                    String prefix_field = item.getSeqprefix_field();
                    int prefix = item.getPrefix_field_len();
                    for (int j = 0; j < ids.length; j++) {
                        String person = (String) ids[j];
                        if (person == null || "".equals(person))
                            continue;
                        
                        try {
                            RecordVo pvo = new RecordVo(dbname + "A01");
                            pvo.setString("a0100", person);
                            pvo = dao.findByPrimaryKey(pvo);
                            if (pvo.getString(item.getItemid()) == null || "".equals(pvo.getString(item.getItemid()))) {
                                String prefix_value = "";
                                if (prefix_field != null && prefix_field.trim().length() > 0)
                                    prefix_value = pvo.getString(prefix_field.toLowerCase());
                                
                                if (prefix_value == null)
                                    prefix_value = "";
                                
                                if (prefix_value.length() > prefix && prefix_field != null && prefix_field.length() > 0)
                                    prefix_value = prefix_value.substring(0, prefix);
                                
                                String backfix = "";
                                if (prefix_value != null && prefix_value.length() > 0)
                                    backfix = "_" + prefix_value;
                                
                                RecordVo idFactory = new RecordVo("id_factory");
                                idFactory.setString("sequence_name", item.getFieldsetid().toUpperCase() + "."
                                        + item.getItemid().toUpperCase() + backfix);
                                String sequ_value = "";
                                /**如果该序号还没建立，取没有前缀的序号*/
                                if (dao.isExistRecordVo(idFactory)) {
                                    sequ_value = idg.getId(item.getFieldsetid().toUpperCase() + "."
                                            + item.getItemid().toUpperCase() + backfix);
                                } else {
                                    sequ_value = idg.getId(item.getFieldsetid().toUpperCase() + "."
                                            + item.getItemid().toUpperCase());
                                }
                                String value = prefix_value + sequ_value;
                                pvo.setString(item.getItemid(), value);
                                dao.updateValueObject(pvo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if ("1".equals(inforkind)) {
                sql.append("select ");
                sql.append(dbname);
                sql.append("A01.a0100,");
                sql.append(dbname);
                sql.append("A01.a0101 from ");
                sql.append(dbname + "A01 where ");
                sql.append(dbname);
                sql.append("A01.a0100 in (" + whereIN.toString() + ") ");
            } else if ("2".equals(inforkind)) {
                sql.append("select ");
                sql.append("organization.codeitemdesc,organization.codeitemid from ");
                sql.append("organization where organization.codeitemid in");
                sql.append("(" + whereIN.toString() + ")");

            } else if ("3".equals(inforkind)) {
                sql.append("select ");
                sql.append("organization.codeitemdesc,organization.codeitemid from ");
                sql.append("organization where organization.codeitemid in");
                sql.append("(" + whereIN.toString() + ")");
            } else if ("4".equals(inforkind)) {
                sql.append("select ");
                sql.append("organization.codeitemdesc,organization.codeitemid from ");
                sql.append("organization where organization.codeitemid in");
                sql.append("(" + whereIN.toString() + ")");
            } else if ("6".equals(inforkind)) // 基准岗位
            {
                String codeset = new CardConstantSet(userView, getFrameconn()).getStdPosCodeSetId();
                sql.append("select codeitemdesc,codeitemid from codeitem " + " where codesetid='" + codeset
                        + "' and codeitemid in(" + whereIN.toString() + ")");
            }

            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql.toString());
            String tag = "";
            int num = 0;
            if ("1".equals(inforkind)) {
                while (this.frowset.next()) {
                    tag = "<NBASE>" + dbname + "</NBASE><ID>" + this.frowset.getString("a0100") + "</ID><NAME>"
                            + this.frowset.getString("a0101") + "</NAME>";
                    CommonData dataobj = new CommonData(tag, this.frowset.getString("a0100"));
                    personlist.add(dataobj);
                    num++;
                    if (num > 500)
                        break;
                }
            } else if ("2".equals(inforkind)) {
                while (this.frowset.next()) {
                    tag = "<NBASE>" + dbname + "</NBASE><ID>" + this.frowset.getString("codeitemid") + "</ID><NAME>"
                            + this.frowset.getString("codeitemdesc") + "</NAME>";
                    CommonData dataobj = new CommonData(tag, this.frowset.getString("codeitemid"));
                    personlist.add(dataobj);
                    num++;
                    if (num > 500)
                        break;
                }
            } else if ("3".equals(inforkind)) {
                while (this.frowset.next()) {
                    tag = "<NBASE>" + dbname + "</NBASE><ID>" + this.frowset.getString("codeitemid") + "</ID><NAME>"
                            + this.frowset.getString("codeitemdesc") + "</NAME>";
                    CommonData dataobj = new CommonData(tag, this.frowset.getString("codeitemid"));
                    personlist.add(dataobj);
                    num++;
                    if (num > 500)
                        break;
                }
            } else if ("4".equals(inforkind)) {
                while (this.frowset.next()) {
                    tag = "<NBASE>" + dbname + "</NBASE><ID>" + this.frowset.getString("codeitemid") + "</ID><NAME>"
                            + this.frowset.getString("codeitemdesc") + "</NAME>";
                    CommonData dataobj = new CommonData(tag, this.frowset.getString("codeitemid"));
                    personlist.add(dataobj);
                    num++;
                    if (num > 500)
                        continue;
                }
            } else if ("6".equals(inforkind)) // 基准岗位
            {
                while (this.frowset.next()) {
                    tag = "<NBASE></NBASE><ID>" + this.frowset.getString("codeitemid") + "</ID><NAME>"
                            + this.frowset.getString("codeitemdesc") + "</NAME>";
                    CommonData dataobj = new CommonData(tag, this.frowset.getString("codeitemid"));
                    personlist.add(dataobj);
                    num++;
                    if (num > 500)
                        continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.getFormHM().put("personlist", personlist);
    }

}
