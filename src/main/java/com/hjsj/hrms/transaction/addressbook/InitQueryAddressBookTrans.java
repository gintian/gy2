package com.hjsj.hrms.transaction.addressbook;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */
public class InitQueryAddressBookTrans extends IBusiness {

    public void execute() throws GeneralException {
        StringBuffer strsql = new StringBuffer();
        strsql.append("select str_value from constant where constant='SS_ADDRESSBOOK'");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        StringBuffer errorString = new StringBuffer(); 
        StringBuffer cloumns = new StringBuffer();
        try {
            String str_value = "";
            this.frowset = dao.search(strsql.toString());
            if (this.frowset.next())
                str_value = this.frowset.getString("str_value");

            if (str_value == null || str_value.length() <= 0)
                str_value = "b0110,e0122,e01a1,a0101";
            String str[] = str_value.split(",");
            for (int i = 0; i < str.length; i++) {
                String st = str[i];
                FieldItem item = new FieldItem();
                item = (FieldItem) DataDictionary.getFieldItem(st).clone();
                if ("0".equals(item.getUseflag())) {
                	errorString.append("“"+item.getItemdesc()+"”，");
				}
                list.add(item);
                cloumns.append(item.getItemid() + ",");
            }
            if (errorString.length() > 0) {
				errorString.setLength(errorString.length()-1);
				throw new GeneralException("");
			}
            cloumns.append("a0100");

            //zxj 20141013 改为取认证人员库（原全部人员库）
            ArrayList dbaselist = getNbaseList();
            CommonData data = (CommonData) dbaselist.get(0);
            String nbase = data.getDataValue();

            this.getFormHM().put("nbase", nbase);
            this.getFormHM().put("columns", cloumns.toString());
            this.getFormHM().put("dbaselist", dbaselist);
            this.getFormHM().put("fieldlist", list);
            this.getFormHM().put("code", "");

            Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
            String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
            if (uplevel == null || uplevel.length() == 0)
                uplevel = "0";
            this.getFormHM().put("uplevel", uplevel);
        }
        catch (GeneralException e) {
        	throw new GeneralException("【系统管理/应用设置/通讯录设置】中"+errorString+"未构库，请构库后再查看通讯录！");
        }catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("请检查【系统管理/应用设置/通讯录设置】的设置是否正确！");
        }
    }

    private ArrayList getNbaseList() throws GeneralException {
        ArrayList nbaseList = new ArrayList();

        DbNameBo dbNameBo = new DbNameBo(this.getFrameconn());
        ArrayList dblist = dbNameBo.getAllLoginDbNameList();
        if (dblist == null || dblist.size() == 0) {
            throw GeneralExceptionHandler.Handle(new GeneralException("没有定义认证人员库！"));
        }

        CommonData da = new CommonData();
        for (int i = 0; i < dblist.size(); i++) {
            RecordVo vo = (RecordVo) dblist.get(i);

            da = new CommonData();
            da.setDataName(vo.getString("dbname"));
            da.setDataValue(vo.getString("pre"));
            nbaseList.add(da);
        }

        return nbaseList;
    }
}
