package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.machine.EmpNetSignin;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitEmpNetSigninTrans extends IBusiness{
    public void execute()throws GeneralException{
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String kind=(String)hm.get("kind");
		String code = (String)hm.get("code");  
		String b0110="";
		String code_kind="";
		if(kind==null||kind.length()<=0)
		{
			kind=RegisterInitInfoData.getKindValue(kind,this.userView);
			code="";
		}
		if(code.length()<=0){
			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
			b0110=managePrivCode.getUNB0110();  
			
		}else{
			if("2".equals(kind))
    		{
				b0110="UN"+code;
    		}else
    		{
    			code_kind=RegisterInitInfoData.getDbB0100(code,kind,this.getFormHM(),this.userView,this.getFrameconn());
    			b0110="UN"+code_kind;
    		}
		}     
		
		EmpNetSignin empNetSignin = new EmpNetSignin(this.userView,this.getFrameconn());
		String cur_date = (String) this.getFormHM().get("registerdate");		
		if(cur_date==null||cur_date.length()<=0)
		{
			
			cur_date=empNetSignin.getWork_date();			
		}
		
		/*ArrayList datelist =RegisterDate.registerdate(b0110,this.getFrameconn(),this.userView); 
		if(datelist==null||datelist.size()<=0)
		{
			
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nosave"),"",""));	
		}else
		{
			CommonData vo = (CommonData) datelist.get(0);		
			cur_date = vo.getDataValue();// 开始日期		    
		}*/	
		String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field");
		if(sdao_count_field!=null&&sdao_count_field.length()>0)
		{
			this.getFormHM().put("issdao", "true");
		}else
		{
			sdao_count_field="";
			this.getFormHM().put("issdao", "false");
		}
		this.getFormHM().put("sdao_count_field", sdao_count_field);
		//this.getFormHM().put("datelist", datelist);
		this.getFormHM().put("registerdate",cur_date);
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
		ArrayList kq_dbase_list=kqUtilsClass.setKqPerList(code,kind);		
		this.getFormHM().put("kq_dbase_list", kq_dbase_list);
		this.getFormHM().put("kq_list",getKqNbaseList(kq_dbase_list));
		String kq_duration=RegisterDate.getKqDuration(this.frameconn);
		this.getFormHM().put("kq_duration",kq_duration);
		this.getFormHM().put("kind",kind); 
		this.getFormHM().put("code",code);
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
    	if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
		this.getFormHM().put("uplevel", uplevel);
		/*****按排班检索*****/
		ArrayList classlist=empNetSignin.getClassList();
//		for(Iterator it = classlist.iterator();it.hasNext();){
//			CommonData class_id = (CommonData) it.next();
//			String classValue = class_id.getDataValue();
//			if (!userView.isHaveResource(IResourceConstant.KQ_BASE_CLASS, classValue)){
//				it.remove();
//			}
//		}
		CommonData vo = new CommonData("All","全部");
		classlist.add(0,vo);
		this.getFormHM().put("classlist",classlist);
		this.getFormHM().put("curclass", "All");
		/******按刷卡标识*****/
		ArrayList signinlist=empNetSignin.getSigninList(sdao_count_field);
		vo = new CommonData("All","全部");
		signinlist.add(0,vo);
		this.getFormHM().put("signinlist",signinlist);
		this.getFormHM().put("cursignin", "All");
    }
    public ArrayList getKqNbaseList(ArrayList list)
	 {
	     ArrayList kq_list=new ArrayList();
        if(list==null||list.size()<=0)
        	return kq_list;
        StringBuffer buf=new StringBuffer();
        buf.append("(");
        for(int i=0;i<list.size();i++)
        {
        	buf.append(" Upper(pre)='"+list.get(i).toString().toUpperCase()+"'");
        	if(i!=list.size()-1)
        		buf.append(" or ");
        }
        buf.append(")");
        StringBuffer sql=new StringBuffer();
        sql.append("select dbname,pre from dbname where 1=1 and ");
        if(buf!=null&&buf.toString().length()>0)
            sql.append(buf.toString());
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        try
        {
       	 rs=dao.search(sql.toString());      
       	 CommonData da=new CommonData();
       	 da.setDataName("全部人员库");
  		 da.setDataValue("all");
  		 kq_list.add(da);
       	 while(rs.next())
       	 {
       		 da=new CommonData();
       		 da.setDataName(rs.getString("dbname"));
       		 da.setDataValue(rs.getString("pre"));
       		 kq_list.add(da);
       	 }
       	 if(kq_list.size() == 2)
       		kq_list.remove(0);
        }catch(Exception e)
        {
       	 e.printStackTrace();
        }finally{
        	if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        }   
        return kq_list;
	 }
}
