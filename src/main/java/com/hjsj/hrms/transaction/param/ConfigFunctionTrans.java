package com.hjsj.hrms.transaction.param;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 输入参数得到系统是否需要此功能
 * @author Yxc
 *
 */
public class ConfigFunctionTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap rqhm=(HashMap)this.getFormHM().get("requestPamaHM");
		String pathflag = (String)rqhm.get("flag");
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();	
		ArrayList dblist=this.userView.getPrivDbList();
		String multimedia_file_flag="";
		List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		//自助平台--我的信息--信息维护--点击多媒体子集pathflag=self时，与pathflag=self处理方式相同   chenxg 2015-11-19
        if((dblist==null||dblist.size()==0)&&!("infoself".equalsIgnoreCase(pathflag) || "self".equalsIgnoreCase(pathflag)))
        {
            throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
        }
        // WJH　２０１３－４－２４
        String userbase = (String)this.getFormHM().get("userbase");
        //自助平台--我的信息--信息维护--点击多媒体子集pathflag=self时，数据错乱 chenxg 2015-11-19
        if ("infoself".equalsIgnoreCase(pathflag) || "self".equalsIgnoreCase(pathflag)){//自助 直接回传自己所在库 2014-03-12 wangrd  
		    this.getFormHM().put("userbase",this.userView.getDbname());	    
		}
		else {
		       if ((userbase==null) || ("".equals(userbase)))
		            this.getFormHM().put("userbase",(String)dblist.get(0));
		        else {
		            // 当前db 是不是有权的db
		            boolean have = false;
		            for (int i=0; i<dblist.size(); i++){
		                if (userbase.equalsIgnoreCase((String) dblist.get(i))) {
		                    have = true;
		                    break;
		                }
		            }       
		            if (!have && dblist.size() > 0) 
		                this.getFormHM().put("userbase",(String)dblist.get(0));
		        }		    
		}	
		
        VersionControl ver_ctrl=new VersionControl();
        boolean bpriv=ver_ctrl.searchFunctionId("26063")||ver_ctrl.searchFunctionId("0308");
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String approveflag=sysoth.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		String inputchinfor=sysoth.getValue(Sys_Oth_Parameter.INPUTCHINFOR);//如果1：不直接入库；0为直接入库
		/**默认状态为不需要审批*/
		if(approveflag==null|| "".equals(approveflag)){
			approveflag="0";			
		}
		if(inputchinfor==null||inputchinfor.length()<=0)
		{
			inputchinfor="0";
		}
		/**版本未加时，也为不需要审批*/
		if(!bpriv)
			approveflag="1";
		String num_per_page=sysoth.getValue(Sys_Oth_Parameter.NUM_PER_PAGE);
		if(num_per_page==null|| "".equals(num_per_page)){
			num_per_page="21";
		}
		String infosort=sysoth.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
		//infoxml.getView_value("A01", recname)
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
    	ArrayList list = new ArrayList();
		list.add("flag");
		list.add("unit");//兼职单位
		list.add("setid");//兼职子集
		list.add("appoint");//兼职标识
		list.add("pos");//兼职职务
		list.add("dept");//兼职部门
		list.add("order");//兼职排序
		list.add("format");//兼职显示格式
		String part_setid="";
		String part_unit="";
		String appoint=" ";
		String flag="";
		String part_pos="";
		String part_dept="";
		String part_order="";
		String part_format="";
		//兼职处理
    	HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
    	if(map!=null&& map.size()!=0){
			if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0)
				flag=(String)map.get("flag");
			if(flag!=null&& "true".equalsIgnoreCase(flag))
			{
				if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0)
					part_unit=(String)map.get("unit");
				if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0)
					part_setid=(String)map.get("setid");
				if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0)
					appoint=(String)map.get("appoint");
				if(map.get("pos")!=null && ((String)map.get("pos")).trim().length()>0)
					part_pos=(String)map.get("pos");
				if(map.get("dept")!=null && ((String)map.get("dept")).trim().length()>0)
					part_dept=(String)map.get("dept");
				if(map.get("order")!=null && ((String)map.get("order")).trim().length()>0)
					part_order=(String)map.get("order");
				if(map.get("format")!=null && ((String)map.get("format")).trim().length()>0)
					part_format=(String)map.get("format");
			}		
		}
    	this.getFormHM().put("part_map", map);
    	String roster=sysoth.getValue(Sys_Oth_Parameter.COMMON_ROSTER);
		roster=roster!=null&&roster.trim().length()>0&&!"#".equals(roster)?roster:"no";
		if(!this.userView.isSuper_admin()){
			String temp=this.userView.getResourceString(4);
			if(temp.indexOf(roster)<0){
				roster="no";
			}
		}
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if("A01".equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				multimedia_file_flag = fieldset.getMultimedia_file_flag();
				break;
			}
		}
		this.getFormHM().put("multimedia_file_flag", multimedia_file_flag);
		String browse_search_state=sysoth.getValue(Sys_Oth_Parameter.BROWSE_SEARCH_STATE);//人员信息浏览查询项状态 0，隐藏 1，显示
	    browse_search_state=browse_search_state!=null&&browse_search_state.length()>0?browse_search_state:"0";
	    
	    if("0".equals(browse_search_state))
	    	this.getFormHM().put("isShowCondition", "none");
	    else
	    	this.getFormHM().put("isShowCondition", "block");
	    
	    String isphotoview = (String) rqhm.get("fromPhoto");
	    rqhm.remove("fromPhoto");
	    if(StringUtils.isEmpty(isphotoview)) {
	    	isphotoview = sysoth.getValue(Sys_Oth_Parameter.BROWSE_PHOTO);//默认显示模式  0记录  1照片
	    }
	    
	    this.getFormHM().put("isphotoview",isphotoview==null? "":isphotoview); 
		this.getFormHM().put("roster",roster); 
    	this.getFormHM().put("part_unit", part_unit);
    	this.getFormHM().put("part_appoint", appoint);//任免指标
    	this.getFormHM().put("part_setid", part_setid);
    	this.getFormHM().put("part_pos", part_pos);
    	this.getFormHM().put("part_dept", part_dept);//
    	this.getFormHM().put("part_order", part_order);
    	this.getFormHM().put("part_format", part_format);
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	this.getFormHM().put("fenlei_priv", fenleiPriv(dao));
		hm.put("num_per_page",num_per_page);
		hm.put("approveflag",approveflag);	
		hm.put("inputchinfor", inputchinfor);
		this.getFormHM().put("browse_search_state", browse_search_state);
		this.getFormHM().put("infosort", infosort);
		this.getFormHM().put("uplevel", uplevel);
	}
    private String fenleiPriv(ContentDAO dao)
    {
    	String sql="select * from constant where upper(constant)='SYS_INFO_PRIV' and type='1'";
    	String value="";
		RowSet rs=null;
		try {
			rs=dao.search(sql);			
			if(rs.next())
			{
				value=rs.getString("str_value");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return value;
    		
    }
}
