package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
/**
 * <p>Title:QueryInterfaceTrans</p>
 * <p>Description:查询接口交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-10:12:18:30</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class QueryInterfaceTrans extends IBusiness {

    /**
     * 
     */
    public QueryInterfaceTrans() {
    }
    
    /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone();
                 if(item_0!=null)
                 {
                	 item_0.setValue("");
                	 item_0.setViewvalue("");
                 }
                 list.add(item_0);
            }
           
        }
        return list;
    }
    
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        /**信息群种类=1(人员),=2(单位),=3(职位)*/
        String type=(String)hm.get("a_inforkind");
        if(type==null|| "".equals(type))
        	type="1";
        RecordVo vo=null;
        if("2".equals(type))
          vo= ConstantParamter.getRealConstantVo("SS_BQUERYTEMPLATE");
        else if("3".equals(type))
            vo= ConstantParamter.getRealConstantVo("SS_KQUERYTEMPLATE");        	
        else
            vo= ConstantParamter.getRealConstantVo("SS_QUERYTEMPLATE");        	
        ArrayList list=userView.getPrivDbList();
        StringBuffer cond=new StringBuffer();
        cond.append("select pre,dbname from dbname where pre in (");
        for(int i=0;i<list.size();i++)
        {
            if(i!=0)
                cond.append(",");
            cond.append("'");
            cond.append((String)list.get(i));
            cond.append("'");
        }
        if(list.size()==0)
            cond.append("''");
        cond.append(")");
        cond.append(" order by dbid");
        /**应用库前缀过滤条件*/
        this.getFormHM().put("dbcond",cond.toString());
        this.getFormHM().put("type",type);
        /**查询模板指标*/
        if(vo!=null)
        {
            String strfields=vo.getString("str_value");
            ArrayList fieldlist=splitField(strfields);
            this.getFormHM().put("fieldlist",fieldlist);            
        }
        else
        {
            this.getFormHM().put("fieldlist",new ArrayList());            	
        }
    	Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);
    	//兼职处理
    	String part_setid="";
		String part_unit="";
		String flag="";
		ArrayList<String> partList = new ArrayList<String>();
		//启用标识
		partList.add("flag");
		//兼职单位标识
		partList.add("unit");
		//兼职子集
		partList.add("setid");
    	HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,partList);
    	if(map!=null&& map.size()!=0){
			if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0)
				flag=(String)map.get("flag");
			if(flag!=null&& "true".equalsIgnoreCase(flag))
			{
				if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0)
					part_unit=(String)map.get("unit");
				if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0)
					part_setid=(String)map.get("setid");
			}		
		}
    	this.getFormHM().put("part_unit", part_unit.toLowerCase());
    	this.getFormHM().put("part_setid", part_setid);
    }

}
