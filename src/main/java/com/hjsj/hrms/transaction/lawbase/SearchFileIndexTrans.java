package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFileIndexTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
        ArrayList list = new ArrayList();
        ArrayList list1 = new ArrayList();
        ArrayList list2 = new ArrayList();
        String para = (String)this.getFormHM().get("paramters");
        String[] arr1 = para.split("\\&");
        HashMap map = new HashMap();
        for (int i = 0; i < arr1.length; i++)
        {
            String[] arr2 = arr1[i].split("=");
            if(arr2.length != 2)
                continue;
            arr2[1] = "".equals(arr2[1])?"":arr2[1];
            map.put(arr2[0], arr2[1]);
        }
        String flag = (String) map.get("flag");
        String basetype = (String)map.get("basetype");
        String table_fields = "";
        String table_value = "";
        String usable_fields = "";
        String usable_value = "";
//        if(value.size()>2){
            usable_fields = (String) map.get("usable_fields");
            usable_value = SafeCode.decode((String) map.get("usable_value"));
            usable_value = usable_value.replace("\n", "").replace("\t", "");
//        }
//        if(flag.equalsIgnoreCase("right") && value.size()>4){
            table_fields = (String) map.get("table_fields");
            table_value = SafeCode.decode((String) map.get("table_value"));
            table_value = table_value.replace("\n", "").replace("\t", "");
//        }
        
        if (table_fields != null)
        {
            String[] fields1 = table_fields.split(",");
            String[] name1 = table_value.split("、");
//            if(flag.equalsIgnoreCase("right")){
                for (int j = 0; j < fields1.length; j++)
                {
                    CommonData dataobj = new CommonData(fields1[j],name1[j]);
                    list1.add(dataobj);
                }
//            }
        }
        
        ArrayList fieldsetlist = DataDictionary.getFieldList("LAW_BASE_FILE",Constant.USED_FIELD_SET);
        HashMap typeMap = new HashMap();
        for(int i=0;i<fieldsetlist.size();i++)
        {
            FieldItem fielditem=(FieldItem)fieldsetlist.get(i); 
            if ("0".equals(fielditem.getState())) //隐藏指标不显示
				continue;
            typeMap.put(fielditem.getItemid(), fielditem.getItemtype());
            if ("1".equals(basetype))//规章
            {
                if("base_id".equalsIgnoreCase(fielditem.getItemid()) || "file_id".equalsIgnoreCase(fielditem.getItemid())
                        || "keywords".equalsIgnoreCase(fielditem.getItemid())|| "originalext".equalsIgnoreCase(fielditem.getItemid()))
                    continue;
                CommonData dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
                list2.add(dataobj);
            }
            if ("4".equals(basetype))//知识
            {
                if("base_id".equalsIgnoreCase(fielditem.getItemid()) || "file_id".equalsIgnoreCase(fielditem.getItemid())
                        || "keywords".equalsIgnoreCase(fielditem.getItemid())|| "originalext".equalsIgnoreCase(fielditem.getItemid()))
                    continue;
                CommonData dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
                list2.add(dataobj);
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
                CommonData dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
                list2.add(dataobj);
            }
            
            
        }
        CommonData dataobj1 = new CommonData("extfile","附件");
        list2.add(dataobj1);
        
        if (usable_fields != null)
        {
        	String[] fields = usable_fields.split(",");
        	String[] name = usable_value.split("、");
        	if (!(fields.length == 1 && "".equals(fields[0])))
        	{
        		for (int i = 0; i < fields.length; i++)
        		{
        			if("right".equals(flag) && "M".equals(typeMap.get(fields[i])))
        				continue;
        			CommonData dataobj = new CommonData(fields[i],name[i]);
        			list.add(dataobj);
        		}
        	}
        	
        } 
        this.getFormHM().put("queryfieldlist", list);//已选的可选指标
        this.getFormHM().put("queryfieldlist1", list1);//已选的列表指标
        this.getFormHM().put("queryfieldlist2", list2);//文档指标
        this.getFormHM().put("table_fields", table_fields);
        this.getFormHM().put("table_value", table_value);
        this.getFormHM().put("selectFlag", flag);
    }
    
}
