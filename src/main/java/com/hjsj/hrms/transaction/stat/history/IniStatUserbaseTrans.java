package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class IniStatUserbaseTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbnameini="usr";
		ArrayList Dblist=userView.getPrivDbList();
	
		if(Dblist!=null && Dblist.size()>0){
			dbnameini=Dblist.get(0).toString();
		}else
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.stat.noprivdbname"),"",""));
		}
		this.getFormHM().put("userbase",dbnameini);
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
        String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	this.getFormHM().put("uplevel", uplevel);	

    	

    	String backdate = (String)this.getFormHM().get("backdate");
    	String backdates = backdate;
    	String sql = "select create_date from hr_hisdata_list order by create_date desc";
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	StringBuffer sb = new StringBuffer();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	try{
    		this.frowset = dao.search(sql);
    		while(this.frowset.next())
    			sb.append(","+sdf.format(this.frowset.getDate("create_date")));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	this.getFormHM().put("backdates", backdates);
    	this.getFormHM().put("allbackdates", sb.length()<1?"":sb.substring(1));
    	
    	ArrayList chart_types = new ArrayList();
    	//分组折线图 类型值为11
    	CommonData data = new CommonData("11","平面折线图");
    	chart_types.add(data);
  /*  	data = new CommonData("31","立体直方图");
    	chart_types.add(data);*/
    	data = new CommonData("29","平面直方图");
    	chart_types.add(data);
    /*	data = new CommonData("5","立体圆饼图");
    	chart_types.add(data);*/
    	data = new CommonData("20","平面圆饼图");
    	chart_types.add(data);
    	this.getFormHM().put("chart_types", chart_types);
    	
        //快照指标  wangrd 2014-01-03
        String snap_fields="";
        try{
            RowSet rs =dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
            if(rs.next()){
                ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
                snap_fields =xml.getTextValue("/Emp_HisPoint/Struct");
            }else{
                   //设置的快照指标
                rs = dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
                if(rs.next())
                    snap_fields=rs.getString("str_value");
            
            }   
            snap_fields = snap_fields+",B0110,E0122,E01A1,A0101,";
            snap_fields =","+snap_fields.toUpperCase()+",";
        }catch(Exception e){
            e.printStackTrace();
        }
        this.getFormHM().put("snap_fields", snap_fields);
	}

}
