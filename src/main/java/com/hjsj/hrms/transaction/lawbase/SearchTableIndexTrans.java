package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchTableIndexTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	String string ="";//增加文档指标过滤，去掉已删除的指标
    	String basetype = (String)this.getFormHM().get("basetype");
    	
        ArrayList fieldsetlist = DataDictionary.getFieldList("LAW_BASE_FILE",Constant.USED_FIELD_SET);
        for(int i=0;i<fieldsetlist.size();i++)
        {
            FieldItem fielditem=(FieldItem)fieldsetlist.get(i); 
            if ("0".equals(fielditem.getState())) //隐藏指标不显示
				continue;
            if ("1".equals(basetype))//规章
            {
                if("base_id".equalsIgnoreCase(fielditem.getItemid()) || "file_id".equalsIgnoreCase(fielditem.getItemid())
                        || "keywords".equalsIgnoreCase(fielditem.getItemid())|| "originalext".equalsIgnoreCase(fielditem.getItemid()))
                    continue;
                string = string + fielditem.getItemid() + "`" + fielditem.getItemdesc() + ",";
            }
            if ("4".equals(basetype))//知识
            {
                if("base_id".equalsIgnoreCase(fielditem.getItemid()) || "file_id".equalsIgnoreCase(fielditem.getItemid())
                        || "keywords".equalsIgnoreCase(fielditem.getItemid())|| "originalext".equalsIgnoreCase(fielditem.getItemid()))
                    continue;
                string = string + fielditem.getItemid() + "`" + fielditem.getItemdesc() + ",";
            }
            if ("5".equals(basetype))//文档
            {
                if("base_id".equalsIgnoreCase(fielditem.getItemid()) || "file_id".equalsIgnoreCase(fielditem.getItemid())
                        || "type".equalsIgnoreCase(fielditem.getItemid())
                        || "valid".equalsIgnoreCase(fielditem.getItemid())
                        || "Issue_org".equalsIgnoreCase(fielditem.getItemid())
                        || "Notes".equalsIgnoreCase(fielditem.getItemid())
                        || "implement_date".equalsIgnoreCase(fielditem.getItemid())
                        || "valid_date".equalsIgnoreCase(fielditem.getItemid())
                        || "digest".equalsIgnoreCase(fielditem.getItemid()))
                    continue;
                string = string + fielditem.getItemid() + "`" + fielditem.getItemdesc() + ",";
            }
        }
        string = string + "extfile" + "`" + "附件";
        ContentDAO dao = new ContentDAO(this.frameconn);
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
        if (hm.get("baseId") != null) {
            this.getFormHM().put("baseId", hm.get("baseId"));
            hm.remove("baseId");
        }
        String base_id=(String)this.getFormHM().get("baseId");
        base_id = PubFunc.decrypt(SafeCode.decode(base_id));
        
        String usable_fields = "";
        String usable_value = "";
        String table_fields = "";
        String table_value = "";
        
        StringBuffer sql = new StringBuffer();
        String field_str = "";
        String upstr  = "";
        String downstr = "";
        sql.append("select field_str from law_base_struct where base_id = '" + base_id + "'");
        ArchiveXml xml = new ArchiveXml();
        try
        {
            this.frecset = dao.search(sql.toString());
            while(this.frecset.next())
            {
                field_str = this.frecset.getString("field_str");
                field_str = field_str!=null?field_str:"";
                if ("".equals(field_str) || "null".equals(field_str))
                {
                    break;           
                }else{
                    upstr = xml.getElement("item", field_str);
                    downstr = xml.getElement("listing", field_str);
                    if (upstr == null || "".equals(upstr))
                    {
                        usable_fields = "";
                        usable_value = "";
                    }
                    if (downstr == null || "".equals(downstr))
                    {
                        table_fields = "";
                        table_value = "";
                    }
                    if (!"".equals(upstr))
                    {
                        String[] upstr_arr = upstr.split(",");
                        for (int i = 0; i < upstr_arr.length; i++)
                        {
                        	String[] arr = upstr_arr[i].split("`");
                    		if (string.indexOf(arr[0])!=-1) {
                        		usable_fields = usable_fields + arr[0] + ",";
                        		usable_value = usable_value + arr[1] + "、";
							}
                        }
                    }
                    if (!"".equals(downstr))
                    {
                        String[] down_arr = downstr.split(",");
                        for (int i = 0; i < down_arr.length; i++)
                        {
                        	String[] arr1 = down_arr[i].split("`");
                    		if (string.indexOf(arr1[0])!=-1) {
                        		table_fields = table_fields + arr1[0] + ",";
                        		table_value = table_value + arr1[1] + "、";
                        		
                        	}
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        this.getFormHM().put("basetype", basetype);
        this.getFormHM().put("usable_fields", usable_fields);
        this.getFormHM().put("usable_value", usable_value);
        this.getFormHM().put("table_fields", table_fields);
        this.getFormHM().put("table_value", table_value);
    }

}
