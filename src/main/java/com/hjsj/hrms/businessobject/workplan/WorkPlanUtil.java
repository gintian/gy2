package com.hjsj.hrms.businessobject.workplan;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant.Cycle;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.summary.WorkPlanSummaryBo;
import com.hjsj.hrms.module.workplan.config.businessobject.WorkPlanConfigBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>Title:WorkPlanUtil.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-8-11 下午01:19:56</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */
@SuppressWarnings("all")
public class WorkPlanUtil {
    private Connection conn=null;
    private UserView userView;
    private ContentDAO dao; 
    private DbWizard dbw;
    
    private String superiorFld; //上级指标 用于多次使用上级指标时  
    private String myE01a1s; //我负责的岗位 用于多次使用时 缓存。
    
    /**
     * @Title: closeDBResource   
     * @Description: 关闭数据库资源（RowSet,Connection) 
     * @param @param dbResource 需关闭的资源
     * @return void    
     */
    public static void closeDBResource(Object dbResource) {
        if (dbResource == null) {
            return;
        }

        try {
            if (dbResource instanceof RowSet) {
                ((RowSet) dbResource).close();
            } else if (dbResource instanceof Connection) {
                ((Connection) dbResource).close();
            } else if (dbResource instanceof Statement) {
                ((Statement) dbResource).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    /**与计划无关
     * @param conn
     * @param userview
     */
    public WorkPlanUtil(Connection conn,UserView userview) {
        this.conn = conn;
        this.userView = userview;
        dbw = new DbWizard(this.conn);
        dao = new ContentDAO(this.conn);
    }
    
    /*
     * 得到电子邮箱指标
     */
    public String getEmailFld() {
        String emailFld = "";
        
        AutoSendEMailBo bo = new AutoSendEMailBo(null);
        emailFld = bo.getEmailField();
        if(emailFld==null || emailFld.length()<=0) {
            emailFld = "";
        }

        if (!fieldInA01(emailFld)) {
            emailFld = "";
        }
        
        return emailFld; 
    }
    
    /**
     * 取得唯一性指标
     * @Title: getUniquenessFld   
     * @Description:    
     * @return
     */
    public String getUniquenessFld() {
        //获取拼音简码的字段
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        String uniqueFld = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");;
        if (null == uniqueFld || "".equals(uniqueFld.trim())) {
            uniqueFld = "";
        }
        
        if (!fieldInA01(uniqueFld)) {
            uniqueFld = "";
        }
        
        return uniqueFld;
    }
    
    /**
     * 取得拼音指标
     * @Title: getPinYinFld   
     * @Description:    
     * @return
     */
    public String getPinYinFld() {
        //获取拼音简码的字段
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
        String pinyinFld = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
        if (null == pinyinFld || "".equals(pinyinFld.trim())) {
            pinyinFld = "";
        }
        
        if (!fieldInA01(pinyinFld)) {
            pinyinFld = "";
        }
        
        return pinyinFld;
    }
    
    /**
     * 是否是主集指标并已构库
     * @Title: fieldInA01   
     * @Description:    
     * @param field
     * @return
     */
    private boolean fieldInA01(String field) {
        boolean inA01 = false;
        if (null == field || "".equals(field.trim())) {
            return inA01;
        }
        
        FieldItem fieldItem = DataDictionary.getFieldItem(field, "a01");
        inA01 = null != fieldItem && "1".equals(fieldItem.getUseflag());
        
        return inA01;
    }
      
    /**   
     * @Title: isSelfUser   
     * @Description: 是自助用户 或者是业务用户但关联了自助用户   
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isSelfUser() {        
       boolean b=false;
       if(this.userView.getStatus()==0) 
       { 
            String a0100=this.userView.getA0100();
            if (a0100!=null && !"".equals(a0100)){
                b=true;
            }         
       }
       else if (this.userView.getStatus()== 4){            
            b=true;
       }

       return b;
        
    }
    
    
    /**   
     * @Title: isHasSubPos   
     * @Description: 是否有下级岗位 有团队计划  
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    private boolean isHasSubPos() {
        boolean b=false;
        try
        {   
            if(!isSelfUser()){
                return b;            
            }    
            //直接上级代码
            initSuperiorFld();
            //
            if (this.superiorFld!=null && this.superiorFld.length()>0){
                String e01a1=this.userView.getUserPosId();
                String str="select count(*) as cnt from k01 where "+this.superiorFld+" ='"+e01a1+"'";
                RowSet rset =dao.search(str);
                if (rset.getInt(1)>0){
                   b=true; 
                }
                closeDBResource(rset);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }         
        return b;
    }   
    
    /**   
     * @Title: isHasMyDept   
     * @Description: 有负责的机构   
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    private boolean isHasMyDept() {
        boolean b=false;
        try{   
            if(!isSelfUser()){
                return b;            
            }
            
            String  deptLeaderFld=WorkPlanConstant.DEPTlEADERFld;
            if (!dbw.isExistField("B01",deptLeaderFld ,false)){
                return b;
            }
            
            String e01a1=this.userView.getUserPosId();
            String str="select count(*) as cnt from B01 where "+deptLeaderFld+" ='"+e01a1+"'";
            RowSet rset =dao.search(str);
            if (rset.getInt(1)>0){
               b=true; 
            }
            closeDBResource(rset);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }           
        return b;
    }   
    
     
     
    /**   
     * @Title: isHasSubDept   
     * @Description:    查看该岗位下及下级岗位是否有负责部门  checkSelf=false 不判断该岗位 只判断下级
     * @param @param e01a1
     * @param @param checkSelf
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isHasSubDept(String e01a1,boolean checkSelf) {
        boolean b=false;
        try
        {   
            //直接上级代码
            String  deptLeaderFld=WorkPlanConstant.DEPTlEADERFld;
            initSuperiorFld();
            if (superiorFld==null || "".equals(superiorFld)){
                return b;
            };
            String str="";
            if (checkSelf){//判断自身
                 str="select count(*) as cnt from B01 where "+deptLeaderFld           
                +" ='"+e01a1+"'";            
                RowSet rset =dao.search(str);
                if (rset.next()){             
                    if(rset.getInt("cnt")>0) {
                        b=true;
                    }
                }
            }
            if (!b){  //判断下级           
                str="select e01a1 from K01 where "                    
                    + superiorFld+" ='"+e01a1+"'";  
                RowSet rset =dao.search(str);
                while (rset.next()){
                    String _e01a1=rset.getString("e01a1");
                    
                    if(!StringUtils.isBlank(_e01a1) && _e01a1.equals(e01a1)){
                    	continue;
                    }
                    b=isHasSubDept(_e01a1,true);
                    if (b) {
                        break;
                    }
                 }
                closeDBResource(rset);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }           
        return b;
    }   
    
    /**   
     * @Title: getMinP07Year   
     * @Description:获取p07表的最小年   
     * @param @return 
     * @return int 
     * @author:wangrd   
     * @throws   
    */
    public int getMinP07Year() { 
        RowSet rset=null;        
        int year=0;;
        String strsql="select min(p0727) from p07 " ;
        try{
            rset=dao.search(strsql);
            if (rset.next()){
                year= rset.getInt(1);
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        if (year==0){
            year=Calendar.getInstance().get(Calendar.YEAR); 
         }
        return year;
    } 
        
      
    /**   
     * @Title: getPeriodList   
     * @Description: 返回期间类型   
     * @param  
     * @return void 
     * @author:wangrd  
  
    */
    public String getPeriodList(String periodtype) {
        String periodList="";
        if (WorkPlanConstant.Cycle.YEAR.equals(periodtype)
               || WorkPlanConstant.Cycle.HALFYEAR.equals(periodtype)
               || WorkPlanConstant.Cycle.QUARTER.equals(periodtype)){                   
            Calendar cale = Calendar.getInstance();
            int curyear = cale.get(Calendar.YEAR);
            int minyear =curyear-1;   
            
            //已存在的最早的计划年度 todo
            int existsMinYear=getMinP07Year();
            if (minyear>existsMinYear) {
                minyear =existsMinYear;
            }
            for (int i=minyear;i<=(curyear+2);i++){
                String stritem="{"
                    +quotedDoubleValue("period_id")+":"+quotedDoubleValue(String.valueOf(i))
                    +","
                    +quotedDoubleValue("period_name")+":"+quotedDoubleValue(String.valueOf(i)+"年度")
                    +"}";
                if("".equals(periodList)){
                    periodList = stritem;  
                }
                else {
                    
                    periodList =periodList+","+ stritem;
                }
                
            }
        }
          
        periodList ="{"
            +quotedDoubleValue("periodlist")+":"
            +"[" +periodList+"]"
            +"}";
        return periodList;
        
 }
    
    /**   
     * @Title: getPeriodTypeJsonList   
     * @Description: 获取计划类型json   
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPeriodTypeJsonList() {
        String periodList="";    
        periodList =
            "{"
            +quotedDoubleValue("period_id")+":"+quotedDoubleValue(WorkPlanConstant.Cycle.WEEK)
            +","
            +quotedDoubleValue("period_name")+":"+quotedDoubleValue("周计划")
            +"}"
            +",{"
            +quotedDoubleValue("period_id")+":"+quotedDoubleValue(WorkPlanConstant.Cycle.MONTH)
            +","
            +quotedDoubleValue("period_name")+":"+quotedDoubleValue("月计划")
            +"}"
            +",{"
            +quotedDoubleValue("period_id")+":"+quotedDoubleValue(WorkPlanConstant.Cycle.QUARTER)
            +","
            +quotedDoubleValue("period_name")+":"+quotedDoubleValue("季度计划")
            +"}"
            +",{"
            +quotedDoubleValue("period_id")+":"+quotedDoubleValue(WorkPlanConstant.Cycle.HALFYEAR)
            +","
            +quotedDoubleValue("period_name")+":"+quotedDoubleValue("半年计划")
            +"}"
            +",{"
            +quotedDoubleValue("period_id")+":"+quotedDoubleValue(WorkPlanConstant.Cycle.YEAR)
            +","
            +quotedDoubleValue("period_name")+":"+quotedDoubleValue("年计划") 
            +"}";
        
        periodList ="{"
            +quotedDoubleValue("periodlist")+":"
            +"[" +periodList+"]"
            +"}";
        return periodList;
        
    }
    
    /**   
     * @Title: getPlanScopeList   
     * @Description: 返回可见范围   
     * @param  
     * @return void 
     * @author:wangrd  
  
    */
    public String getPlanScopeList() { 
        String scopeList="";
        try
        {
            scopeList=
                "{"
                    +quotedDoubleValue("scope_id")+":"+quotedDoubleValue(WorkPlanConstant.Scope.SUPERIOR)
                    +","
                    +quotedDoubleValue("scope_name")+":"+quotedDoubleValue("上级可见")
                +"}"               
                +","
                +"{"
                    +quotedDoubleValue("scope_id")+":"+quotedDoubleValue(
                            WorkPlanConstant.Scope.DEPARTMENT)
                    +","
                    +quotedDoubleValue("scope_name")+":"+quotedDoubleValue("本部门可见")
                +"}"
                
                +","
                +"{"
                +quotedDoubleValue("scope_id")+":"+quotedDoubleValue(
                        WorkPlanConstant.Scope.UNIT)
                        +","
                        +quotedDoubleValue("scope_name")+":"+quotedDoubleValue("本单位可见")
                        +"}"                       
                
                +","
                +"{"
                    +quotedDoubleValue("scope_id")+":"+quotedDoubleValue(
                            WorkPlanConstant.Scope.ALL)
                    +","
                    +quotedDoubleValue("scope_name")+":"+quotedDoubleValue("完全公开")
                +"}";
           
       
        }
        catch(Exception e){            
        
        }
        
        scopeList ="{"
            +quotedDoubleValue("planscopelist")+":"
            +"[" +scopeList+"]"
            +"}";
        return scopeList;
        
 }
    
    /**   
     * @Title: getAddMenuList   
     * @Description: 返回增加菜单列表   
     * @param  
     * @return void 
     * @author:wangrd  
  
    */
    public String getAddMenuList() { 
        String menuList="";
        try
        {
            menuList=
                "{"
                    +quotedDoubleValue("menu_id")+":"+quotedDoubleValue("1")
                    +","
                    +quotedDoubleValue("menu_name")+":"+quotedDoubleValue("增加任务")
                +"}"
                +","
                +"{"
                    +quotedDoubleValue("menu_id")+":"+quotedDoubleValue("2")
                    +","
                    +quotedDoubleValue("menu_name")+":"+quotedDoubleValue("增加子任务")
                +"}"
                +","
                +"{"
                +quotedDoubleValue("menu_id")+":"+quotedDoubleValue("3")
                +","
                +quotedDoubleValue("menu_name")+":"+quotedDoubleValue("复制上期未完成任务")
                +"}"
                +","
                +"{"
                    +quotedDoubleValue("menu_id")+":"+quotedDoubleValue("4")
                    +","
                    +quotedDoubleValue("menu_name")+":"+quotedDoubleValue("复制上期未完成任务")
                +"}";
           
       
        }
        catch(Exception e){            
        
        }
        
        menuList ="{"
            +quotedDoubleValue("menulist")+":"
            +"[" +menuList+"]"
            +"}";
        return menuList;
        
 }   
    
       
    /**   
     * @Title: quotedDoubleValue   
     * @Description:变量两边加双引号    
     * @param @param value
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public static String quotedDoubleValue(String value) {
        String str=value;    
        str="\""+str+"\"";      
      
        return str;
    }
    

    
    private void setInnerDeptList(RowSet rset,ArrayList deptList) {   
        try
        {   
            while (rset.next()){   
                LazyDynaBean bean = new LazyDynaBean();
                bean.set("b0110", rset.getString("b0110"));
                bean.set("deptdesc",  rset.getString("codeitemdesc"));   
                bean.set("ispart",  rset.getString("ispart"));   
                bean.set("b01ps",  rset.getString(WorkPlanConstant.DEPTlEADERFld));   
                deptList.add(bean);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }           
    }   
    
    /**   
     * @Title: isLogonUser   
     * @Description:是否是当前登陆用户  
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isLogonUser(String nbase,String a0100) {     
        boolean b=false;
        if (StringUtils.isEmpty(this.userView.getDbname())){
            return b;
        }
        try{
            if ((this.userView.getDbname().equalsIgnoreCase(nbase))&&(this.userView.getA0100().equals(a0100))){
              b=true;   
            }          
        }
        catch(Exception e){           
            e.printStackTrace();  
        }
        return b;
    }
    
    public void initSuperiorFld() {
        if (superiorFld==null || "".equals(superiorFld)){               
            RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.conn);
            if(ps_superior_vo!=null)
            {
                superiorFld=ps_superior_vo.getString("str_value");
            }
        }     
    }
    /*
     * 取直接上级字段(系统参数)
     */
    public String getSuperiorFld() {
    	if (superiorFld==null || "".equals(superiorFld)){               
    		RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.conn);
    		if(ps_superior_vo!=null)
    		{
    			superiorFld=ps_superior_vo.getString("str_value");
    		}
    	}  
    	return superiorFld;
    }

    /**   
     * @Title: getDeptLeaderE01a1   
     * @Description: 获取部门的负责岗位   
     * @param @param dept_id
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getDeptLeaderE01a1(String dept_id) {
        String e01a1="";
        try
        {   
            String strsql="select "+WorkPlanConstant.DEPTlEADERFld+" from b01 where "                
                + "  b0110='"+dept_id+"'";
            RowSet rset= dao.search(strsql); 
            if (rset.next()){ 
                e01a1=rset.getString(WorkPlanConstant.DEPTlEADERFld);                
            }
            closeDBResource(rset);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }   
        if (e01a1==null) {
            e01a1="";
        }
        return e01a1;
    } 
            
            
    /**   
     * @Title: getFirstDeptLeaders   
     * @Description: 获取当前部门的负责人 取第一个   
     * @param @param dept_id
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getFirstDeptLeaders(String dept_id) {
        String leader="";
        try
        {   
             String [] arrpre=getSelfUserDbs();    
             //主职                                                  
             for (int i=0;i<arrpre.length;i++){                
                 String pre = arrpre[i];                       
                 String a01tab=pre+"a01";
                 String strsql="select "+a01tab+".a0100"
                     +" from "+a01tab+",b01 where "
                     + a01tab+".e01a1= b01."+WorkPlanConstant.DEPTlEADERFld
                     + " and B01.b0110='"+dept_id+"'";
                 RowSet rset= dao.search(strsql); 
                 if (rset.next()){ 
                     leader=pre+rset.getString("a0100");                 
                     break;
                 }
                 closeDBResource(rset);
             }  
             if (leader.length()<1){
                 //兼职
                 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
                 String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag"); 
                 if("true".equals(flag)){
                     String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                     String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
                     String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                     if (!("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field))
                     {
                         
                         for (int i=0;i<arrpre.length;i++){                
                             String pre = arrpre[i];         
                             String a01tab=pre+setid;
                             String strsql="select "+a01tab+".a0100"
                             +" from "+a01tab+",b01 where "
                             + a01tab+"."+e01a1_field+"= b01."+WorkPlanConstant.DEPTlEADERFld
                             + " and B01.b0110='"+dept_id+"'"
                             +" and " +a01tab+"."+appoint_field+"='0'";;
                             RowSet rset= dao.search(strsql); 
                             if (rset.next()){ 
                                 leader=pre+rset.getString("a0100");                 
                                 break;
                             }
                             closeDBResource(rset);
                         }
                     }
                 }          
             }    


            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }           
        return leader;
    }   
    /**   
     * @Title: getFirstE01a1Leaders   
     * @Description: 获取岗位的负责人 取第一个   
     * @param @param e01a1
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
     */
    public String getFirstE01a1Leaders(String e01a1) {
        String leader="";
        RowSet rset=null;
        try
        {   
            String tablename=getPeopleSqlByE01a1(e01a1);
            if (!"".equals(tablename)){
                tablename="("+tablename+") T";
                String strsql="select T.* from "+tablename;               
                rset=dao.search(strsql);               
                if (rset.next()){
                    String _nbase=rset.getString("nbase");
                    String _a0100=rset.getString("a0100");    
                    leader=_nbase+_a0100;
                }                      
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
         } finally {
            closeDBResource(rset);
        }
        return leader;
    }  
    
    /**   
     * @Title: getAllE01a1Leaders   
     * @Description:获取岗位负责人 ，    
     * @param @param e01a1
     * @param @param nbase 只查找此库人员，如果不传则查找所有库。
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getFirstE01a1Leaders(String e01a1,String nbase) {
        String leader="";
        RowSet rset=null;
        try {
            String tablename=getPeopleSqlByE01a1(e01a1);
            if (!"".equals(tablename)){
                tablename="("+tablename+") T";
                String strsql="select T.* from "+tablename;               
                rset=dao.search(strsql);               
                while (rset.next()){
                    String _nbase=rset.getString("nbase");
                    if (nbase!=null && nbase.length()>0){
                        if (nbase.equalsIgnoreCase(_nbase)){
                            String _a0100=rset.getString("a0100");    
                            leader=_a0100;
                            break;
                        }
                    }
                    else {
                        String _a0100=rset.getString("a0100");    
                        leader=_nbase+_a0100;  
                        break;
                    }
                }                      
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
         } finally {
            closeDBResource(rset);
        }
        return leader;
    }  
    
    /**   
     * @Title: isMyTeamPeople   
     * @Description: 是否是我的团队成员 包含下下级    
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isMyTeamPeople(String nbase,String a0100) {
        boolean b= false;    
        if ("".equals(this.userView.getDbname())){
            return b;
        }
        //只检查主岗位是否是我的下级 不管兼职
        if (myE01a1s==null) {
            myE01a1s= getMyE01a1s(this.userView.getDbname(),this.userView.getA0100());
        }
        RecordVo vo= this.getPersonVo(nbase, a0100);
        try{
            if (vo!=null){
                String e01a1=vo.getString("e01a1");
                if (e01a1!=null && e01a1.length()>0){
                    if (isMySubE01a1(myE01a1s,e01a1)){
                        b=true; 
                    }
                }
            }
         
        }
        catch(Exception e){           
            e.printStackTrace();  
        }   
        return b;
    }
    
    public boolean isMyTeamPeople(String e01a1) {
        boolean b= false;    
        if ("".equals(this.userView.getDbname())){
            return b;
        }
        //只检查主岗位是否是我的下级 不管兼职
        if (myE01a1s==null) {
            myE01a1s= getMyE01a1s(this.userView.getDbname(),this.userView.getA0100());
        }
        if (e01a1!=null && e01a1.length()>0){
            if (isMySubE01a1(myE01a1s,e01a1)){
                b=true; 
            }
        }
        return b;
    }
    
    /**   
     * @Title: isMyTeamDept   
     * @Description: 是否是我的下属负责部门 包含我负责的   
     * @param @param objectid
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */   
	public boolean isMyTeamDept(String dept_id) {
		return isMyTeamDept(this.userView.getDbname(), this.userView.getA0100(), dept_id);
	}   
    
    public boolean isMyTeamDept(String nbase,String a0100 ,String dept_id) {
        boolean b= false; 
        if ("".equals(nbase)){
            return b;
        }
        try
        {   
             String e01a1 ="";           
             String strsql="select * from b01 where  b0110='"+dept_id+"'";
             RowSet rset= dao.search(strsql); 
             if (rset.next()){ 
                 e01a1=rset.getString(WorkPlanConstant.DEPTlEADERFld);                 
             }
             if (e01a1!=null && e01a1.length()>0){
                 if (myE01a1s==null) {
                     myE01a1s= getMyE01a1s(nbase,a0100);
                 }
                 if ((","+myE01a1s+",").indexOf(","+e01a1+",")>-1){
                     return true;
                 }
                 
                 if (isMySubE01a1(myE01a1s,e01a1)){
                    b=true; 
                  }
             }
             closeDBResource(rset);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }           
        return b;
    }  
    /**   
     * @Title: isMySubE01a1   
     * @Description:   判断e01a1是否是我的下级岗位 
     * @param @param myE01a1s 我的岗位列表
     * @param @param e01a1  下级岗位
     * @return void 
     * @author:wangrd   
     * @throws   
    */
    public boolean isMySubE01a1(String my_e01a1s,String e01a1) {
        boolean b=false;
        if(e01a1==null || e01a1.length()<1) {
            return b;
        }
        initSuperiorFld();
        if (superiorFld!=null && !"".equals(superiorFld)){
            String strsql="select "+superiorFld+" from k01 where e01a1='"+e01a1+"'";
            try{
                RowSet rset=dao.search(strsql);
                if (rset.next()){
                    String superE01a1= rset.getString(1);
                    if (superE01a1!=null && !"".equals(superE01a1)){
                        if (superE01a1.equals(e01a1)) {
                            return b;
                        }
                        if ((","+my_e01a1s+",").indexOf(","+superE01a1+",")>-1){
                            b=true;                            
                        }
                        else {
                            b=isMySubE01a1(my_e01a1s,superE01a1);
                        }          
                    }
                    
                }
                closeDBResource(rset);
            }
            catch(Exception e){           
                e.printStackTrace();  
            }
        }
        return b;
    }
    
    /**   
     * @Title: isMyDept   
     * @Description: 是否是我的部门   
     * @param @param dept_id
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isMyDept(String dept_id) {
        boolean b= false; 
        if ("".equals(this.userView.getDbname())){
            return b;
        }
        try
        {   
            List depts = getDeptList(
                    userView.getDbname(),userView.getA0100());
            for (int i = 0, len = depts.size(); i < len; i++) {
                LazyDynaBean bean = (LazyDynaBean) depts.get(i);
                if (dept_id.equals(bean.get("b0110"))) {
                    return true;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }           
        return b;
    }  
    
    /**   
     * @Title: getMyMainE01a1   
     * @Description: 获取用户的主岗位
     * @param @param a0100
     * @param @return 
     * @return String 
     * @author:wusy   
     * @throws   
    */
    public String getMyMainE01a1(String nbase, String a0100){
    	String myMainE01a1 = "";
    	RecordVo vo = new RecordVo(nbase + "A01");
    	vo.setString("a0100", a0100);
    	try {
			vo = dao.findByPrimaryKey(vo);
			myMainE01a1 = vo.getString("e01a1");
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	return myMainE01a1;
    }
    
    /**   
     * @Title: getMyE01a1s   
     * @Description: 获取用户的岗位列表，按逗号分隔   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getMyE01a1s(String nbase,String a0100) {
        String e01a1s= "";  
        if ("".equals(a0100)) {
            return e01a1s;
        }
        ArrayList e01a1list= getMyE01a1List(nbase,a0100,false);
        try{
            if (e01a1list.size()>0){                
                for (int i=0;i<e01a1list.size();i++){
                    LazyDynaBean e01abean= (LazyDynaBean)e01a1list.get(i);                    
                    String e01a1 =(String )e01abean.get("e01a1");
                    if ("".equals(e01a1)) {
                        continue;
                    }
                    e01a1s=e01a1s+","+e01a1;
                }    
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        }   
        return e01a1s;
    }
    
    /**   
     * @Title: getMyE01a1List   
     * @Description:获取用户的岗位列表，放在bean里面，有主职 兼职标志。    
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getMyE01a1List(String nbase,String a0100) {
        
        return getMyE01a1List(nbase,a0100,true);
    }
 
    /**   
     * @Title: getMyE01a1List   
     * @Description:获取用户的岗位列表，放在bean里面，有主职 兼职标志。    
     * @param @param nbase
     * @param @param a0100
     * @param @param bCheckE01a1 是否检查岗位属于当前人登陆人的下级岗位 当前人不用检查
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getMyE01a1List(String nbase,String a0100,boolean bCheckE01a1) {
        ArrayList e01a1list= new ArrayList();
        initSuperiorFld();
        //兼职            
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
        boolean bCurLogonUser=isLogonUser(nbase,a0100);
        String mye01a1s="";
        if (!bCurLogonUser && bCheckE01a1){ //取得登陆的用户的岗位
                mye01a1s= getMyE01a1s(this.userView.getDbname(),this.userView.getA0100());
        }
        if (superiorFld!=null && !"".equals(superiorFld)){
            String strsql="select e01a1,0 as ispart  from "+nbase+"a01 where a0100=?";
            List values = new ArrayList();
            values.add(a0100);
            if("true".equals(flag)){
                String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
                String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                String curPartTab=nbase+setid;
                if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                {
                    strsql=strsql+" union select "+e01a1_field+" as e01a1 ,1 as ispart from "+curPartTab+" where a0100=?"
                        +" and " +appoint_field+"='0'";
                    values.add(a0100);
                }
            } 
            try{
                RowSet mainSet=dao.search(strsql,values);
                while  (mainSet.next()){
                    LazyDynaBean bean = new LazyDynaBean();                    
                    String e01a1 =mainSet.getString("e01a1");
                    if(e01a1 == null || "".equals(e01a1)) {
                        continue;
                    }
                    String ispart =mainSet.getString("ispart");
                    if (!bCurLogonUser && bCheckE01a1){//不是当前用户 需判断此岗位是否属于当前用户的下级岗位                        
                        if (!isMySubE01a1(mye01a1s,e01a1)){
                           continue;  
                        }
                    }
                    bean.set("e01a1", e01a1);
                    bean.set("ispart", ispart);
                    e01a1list.add(bean);                    
                }
                closeDBResource(mainSet);
            }
            catch(Exception e){           
                e.printStackTrace();  
            }           
        }
        
        return e01a1list;
    }
    public ArrayList getDeptList(String nbase,String a0100) {
        ArrayList deptList= new ArrayList();
        try
        {   
        	deptList=getDeptList(nbase,a0100, true);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }           

        return deptList;        
    }  
    /**   
     * @Title: getDeptList   
     * @Description: 返回负责的部门列表   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return HashMap 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList getDeptList(String nbase,String a0100,boolean bcheck) {
        ArrayList deptList= new ArrayList();
        try
        {   
            String strsql="";
            String  deptLeaderFld=WorkPlanConstant.DEPTlEADERFld;   
  
            if ((nbase==null)||("".equals(nbase))) 
            {
                return deptList;
            }
            boolean bCurLogonUser=true;
            if (bcheck) {
            	bCurLogonUser=isLogonUser(nbase,a0100);
            }             
            
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
            List values = new ArrayList();
            if (bCurLogonUser){
                strsql="select B01.b0110,O.codeitemdesc,0 as ispart,"+deptLeaderFld+" from B01,organization O where B01.b0110=O.codeitemid "
                    +" and  "+deptLeaderFld+" in (select E01a1 from "+nbase+"A01"
                    +" where a0100=?)";
                values.add(a0100);
                if("true".equals(flag)){//兼职
                    String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                    String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
                    String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                    if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                    {
                        strsql=strsql+" union select B01.b0110,O.codeitemdesc,1 as ispart,"+deptLeaderFld+" from B01,organization O where B01.b0110=O.codeitemid"
                            +" and "+deptLeaderFld+" in (select "+e01a1_field+" from "+nbase+setid
                            +" where a0100=? and "+appoint_field+"='0' )";
                        values.add(a0100);
                    }
                }
                RowSet rset =dao.search(strsql,values);
                setInnerDeptList(rset,deptList); 
                closeDBResource(rset);
            } 
            else {//非当前登陆用户，需要判断此人的岗位是否是当前登陆用户的下级岗位                
                ArrayList e01a1list= getMyE01a1List(nbase, a0100);
                if (e01a1list.size()>0){                
                    for (int i=0;i<e01a1list.size();i++){
                        LazyDynaBean e01abean= (LazyDynaBean)e01a1list.get(i);                    
                        String e01a1 =(String )e01abean.get("e01a1");
                        String ispart =(String )e01abean.get("ispart");
                        if ("".equals(e01a1)) {
                            continue;
                        }
                        strsql="select B01.b0110,O.codeitemdesc,"+ispart+" as ispart,"+deptLeaderFld+" from B01,organization O where B01.b0110=O.codeitemid "
                            +" and  "+deptLeaderFld+" " +"= '"+e01a1+"'";
                            //SZK两次查询一样		
                            //"in (select E01a1 from "+nbase+"A01 where a0100='"+a0100+"')"; 
                        RowSet rset =dao.search(strsql);
                        setInnerDeptList(rset,deptList);
                        closeDBResource(rset);
                    }    
                } 
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }           

        return deptList;        
    }  
    
    public String getOrgDesc(String objectid) {
        String desc="";
        RowSet rset = null;
        try {
            String strsql = "select codeitemdesc from organization where codeitemid ='" + objectid + "'";
            rset = dao.search(strsql);
            if (rset.next()) {
                desc = rset.getString("codeitemdesc");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            closeDBResource(rset);
        }

        return desc;
            
    }    
    
    public String getDeptList_Json(String nbase,String a0100) { 
        String menuList="";
        try {
            ArrayList deptlist = this.getDeptList(nbase, a0100);
            for (int i = 0; i < deptlist.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) deptlist.get(i);
                String deptdesc = (String) bean.get("deptdesc");
                String b0110 = (String) bean.get("b0110");

                String deptItem = "{" + quotedDoubleValue("b0110") + ":" + quotedDoubleValue(encryption(b0110)) + ","
                        + quotedDoubleValue("deptdesc") + ":" + quotedDoubleValue(deptdesc) + "}";
                if ("".equals(menuList)) {
                    menuList = deptItem;
                } else {
                    menuList = menuList + "," + deptItem;
                }
            }
        } catch (Exception e) {
        }
        menuList ="{"
            +quotedDoubleValue("deptlist")+":"
            +"[" +menuList+"]"
            +"}";
        return menuList;
        
    }   
    
    public String getPlanStatusDesc(String status) { 
        String desc="";
        try {
            if ("0".equals(status)) {
                desc = "未提交";//未审批
            } else if ("1".equals(status)) {
                desc = "已发布";
            } else if ("2".equals(status)) {
                desc = "已批准";//已审批
            } else if ("3".equals(status)) {
                desc = "已退回";
            } else if ("4".equals(status)) {
                desc = "已启动";
            } else if ("5".equals(status)) {
                desc = "已结束";
            } else if ("10".equals(status)) {
                desc = "起草中";
            } else {
                desc = "未制订";
            }
        } catch (Exception e) {

        }     

        return desc;
    }  
    
    /**   
     * @Title: getKhPlanStatusDesc   
     * @Description:获取考核计划的状态描述 没找到     
     * @param @param status
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public static String getKhPlanStatusDesc(String status) { 
        String desc="";
        try {
            if ("0".equals(status)) {
                desc = ResourceFactory.getProperty("hire.jp.pos.draftout");//
            } else if ("1".equals(status)) {
                desc = ResourceFactory.getProperty("info.appleal.state1");
            } else if ("2".equals(status)) {
                desc = ResourceFactory.getProperty("label.hiremanage.status3");//已审批
            } else if ("3".equals(status)) {
                desc = ResourceFactory.getProperty("button.issue");
            } else if ("4".equals(status)) {
                desc = ResourceFactory.getProperty("gz.formula.implementation");
            } else if ("5".equals(status)) {
                desc = ResourceFactory.getProperty("lable.performance.status.pause");
            } else if ("6".equals(status)) {
                desc = ResourceFactory.getProperty("jx.khplan.Appraisal");
            } else if ("7".equals(status)) {
                desc = ResourceFactory.getProperty("label.hiremanage.status6");
            } else if ("8".equals(status)) {
                desc = ResourceFactory.getProperty("performance.plan.distribute");          
            } else {
                desc = "";
            }
        } catch (Exception e) {
            
        }     
        
        return desc;
    }  
    
    
    /**   
     * @Title: getKhPlanMethodDesc   
     * @Description: 考核方法   
     * @param @param menthod
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public static String getKhPlanMethodDesc(String menthod) { 
        String desc="";
        try {
            if ("2".equals(menthod)) {
                desc = ResourceFactory.getProperty("jx.khplan.khmethod2");//
            } else {
                desc = ResourceFactory.getProperty("jx.khplan.khmethod1");//
            }
        } catch (Exception e) {
            
        }     
        
        return desc;
    }  
    
    /**   
     * @Title: getKhPlanObjectTypeDesc   
     * @Description: 考核对象描述   
     * @param @param menthod
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public static String getKhPlanObjectTypeDesc(String type) { 
        String desc="";
        try {
            if ("1".equals(type)) {
                desc = ResourceFactory.getProperty("jx.khplan.team");
            } else if ("2".equals(type)) {
                desc = ResourceFactory.getProperty("task.selectobject.personnel");//已审批
            } else if ("3".equals(type)) {
                desc = ResourceFactory.getProperty("jx.khplan.unit");
            } else if ("4".equals(type)) {
                desc = ResourceFactory.getProperty("column.sys.dept");
            }    
        } catch (Exception e) {
            
        }     
        
        return desc;
    }  
    
    /**   
     * @Title: getKhPlanPeriodDesc   
     * @Description:获取考核计划期间    
     * @param @param cycle
     * @param @param year
     * @param @param month
     * @param @param quarter
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public static String getKhPlanPeriodDesc(String cycle,String year,String month,String quarter) { 
        String desc="";
        try {
            desc=year+" "+ResourceFactory.getProperty("datestyle.year");;
            if ("1".equals(cycle)) {//半年
                if ("01".equals(quarter)) {//上半年
                    desc = desc + ResourceFactory.getProperty("report.pigeonhole.uphalfyear");
                } else if ("02".equals(quarter)) {
                    desc = desc + ResourceFactory.getProperty("report.pigeonhole.downhalfyear");
                } 
            } else if ("2".equals(cycle)) {
                desc=desc+" ";
                if ("01".equals(quarter)) {
                    desc = desc + ResourceFactory.getProperty("report.pigionhole.oneQuarter");
                } else if ("02".equals(quarter)) {
                    desc =  desc +ResourceFactory.getProperty("report.pigionhole.twoQuarter");
                } 
                else if ("03".equals(quarter)) {
                    desc = desc + ResourceFactory.getProperty("report.pigionhole.threeQuarter");
                } else if ("04".equals(quarter)) {
                    desc =  desc +ResourceFactory.getProperty("report.pigionhole.fourQuarter");
                } 
            } else if ("3".equals(cycle)) {
                desc=desc+" ";
                if ("01".equals(month)) {
                    desc = desc + ResourceFactory.getProperty("date.month.january");
                } else if ("02".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.february");
                } 
                else if ("03".equals(month)) {
                    desc = desc + ResourceFactory.getProperty("date.month.march");
                } else if ("04".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.april");
                } 
                else   if ("05".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.may");
                } else if ("06".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.june");
                } 
                else if ("07".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.july");
                } else if ("08".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.auguest");
                } 
                else   if ("09".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.september");
                } else if ("10".equals(month)) {
                    desc =desc + ResourceFactory.getProperty("date.month.october");
                } 
                else if ("11".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.november");
                } else if ("12".equals(month)) {
                    desc = desc +ResourceFactory.getProperty("date.month.december");
                } 
                
            } else if ("4".equals(cycle)) {
            }    
        } catch (Exception e) {
            
        }     
        
        return desc;
    } 
    
    public String getPlanScopeDesc(String scope) { 
        String desc="";
        try {
            if (WorkPlanConstant.Scope.UNIT.equals(scope)) {
                desc = "本单位可见";
            } else if (WorkPlanConstant.Scope.DEPARTMENT.equals(scope)) {
                desc = "本部门可见";
            } else if (WorkPlanConstant.Scope.ALL.equals(scope)) {
                desc = "完全公开";
            } else if (WorkPlanConstant.Scope.CHILDREN.equals(scope)) {
                desc = "下级可见";
            } else {
                desc = "上级可见";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }    
        
        return desc;
        
    }  
    /**   
     * @Title: getSelfUserDbs   
     * @Description: 获取自助用户库  因为dbname 大小写的问题 同dbname表一致 
     * @param @return 
     * @return String[] 
     * @author:wangrd   
     * @throws   
    */
    public String[] getSelfUserDbs() { 
        String []  arrPre= new String[0];
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String strpres="";
        if(login_vo!=null) {
            strpres = login_vo.getString("str_value").toLowerCase();
        }
        
        arrPre = strpres.split(",");
        if (arrPre.length <= 0) {
            return arrPre;
        }
        
        RowSet rset = null;
        try {
            String strsql = "select * from dbname";
            rset = this.dao.search(strsql);
            while (rset.next()) {
                for (int i = 0; i < arrPre.length; i++) {
                    if (arrPre[i].equalsIgnoreCase(rset.getString("pre"))) {
                        arrPre[i] = rset.getString("pre");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rset);
        }
        
        return arrPre;
    }  
    
    /**   
     * @Title: getSelfUserDbs   
     * @Description: 获取自助用户库  业务用户使用
     * @param @return 
     * @return String[] 
     * @author:wangrd   
     * @throws   
    */
    public String[] getHrSelfUserDbs() { 
        String []  arrPre= new String[0];
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String strpres="";
        if(login_vo!=null) {
            strpres = login_vo.getString("str_value").toLowerCase();
        }
        
        arrPre = strpres.split(",");
        if (arrPre.length <= 0) {
            return arrPre;
        }
        
        RowSet rset = null;
        try {
            String dbPriv= this.userView.getDbpriv().toString();
            ArrayList list = new ArrayList();
            String strsql = "select * from dbname order by dbid";
            rset = this.dao.search(strsql);
            while (rset.next()) {
                for (int i = 0; i < arrPre.length; i++) {
                    if(!this.userView.isSuper_admin() && dbPriv.indexOf(rset.getString("pre")) == -1){
                       continue; 
                    }
                    if (arrPre[i].equalsIgnoreCase(rset.getString("pre"))) {
                        arrPre[i] = rset.getString("pre");
                        list.add(rset.getString("pre"));
                        break;
                    }
                }
            }
            int size = list.size();
            arrPre=(String [])list.toArray(new String[size]); 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDBResource(rset);
        }
        
        return arrPre;
    }  
    
    /**
     * 根据关键字从姓名拼音,姓名汉字和邮箱中查找相似的项
     * @param keyword 关键字,可以包含中文
     * @param excludeIds 不包含的人员id
     * @return 符合条件的LazyDyncBean集合,最多20个元素
     * @throws GeneralException
     */
    public List getCandidateByKeyword(final String keyword, final String excludeIds) throws GeneralException {
    	String exIDs = excludeIds == null ? "" : excludeIds;
    	
    	List candidates = new ArrayList(); // 符合条件的候选名单
    	
    	String pyField = this.getPinYinFld(); // 拼音字段
    	String emailField = this.getEmailFld(); // 邮箱字段

    	RowSet rs = null;
        try {
        	// 应用库
        	RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
        	String strpres = "";
        	if(login_vo != null) {
        		strpres = login_vo.getString("str_value");
        	}
        	String[] pres= strpres.split(",");
        	
			enough: for (int i = 0; i < pres.length; i++) {
				if (pres[i] == null) {
					continue;
				}
				
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT a0100,a0101,b0110,E0122,E01A1");
				if (pyField.length()>0) {
                    sql.append(",").append(pyField);
                }
				if (emailField.length()>0) {
                    sql.append(",").append(emailField);
                }
                sql.append(" FROM ");
				sql.append(pres[i]).append("A01");
				sql.append(" WHERE A0101 LIKE '").append(keyword).append("%'");
                if (pyField.length()>0) {
                    sql.append(" OR ").append(pyField).append(" LIKE '").append(keyword).append("%'");
                }
                if (emailField.length()>0) {
                    sql.append(" OR ").append(emailField).append(" LIKE '").append(keyword).append("%@%'");
                }
				sql.append(" ORDER BY a0000");
				
				rs = dao.search(sql.toString());
				WorkPlanBo wpbo = new WorkPlanBo(conn, userView);
				while (rs.next()) {
					if (exIDs.indexOf(pres[i] + rs.getString("a0100")) == -1) { // 从排除的id中未发现重复的id
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("id", WorkPlanUtil.encryption(pres[i] + rs.getString("a0100")));
						String a0101=rs.getString("a0101");
						if (a0101==null || a0101.length()<1) {
                            continue;
                        }
						bean.set("name",  getTruncateA0101(a0101));					
						String photo = wpbo.getPhotoPath(pres[i], rs.getString("a0100"));
						bean.set("photo", photo);
//						bean.set("abbr", a0101.substring(0, 1) + "");
						bean.set("unit", AdminCode.getCodeName("UM", rs.getString("e0122")) + "");
						String email="";
						if (emailField.length()>0){
						    email=rs.getString(emailField);
						    if (email==null) {
                                email="";
                            }
						}
						bean.set("email", email);
						
						candidates.add(bean);
						
						if (candidates.size() == WorkPlanConstant.TaskInfo.MAX_CANDIDATE_NUMBER) { // 已经拿到了足够的候选人
							break enough;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			closeDBResource(rs);
		}
        
        return candidates;
    }

    
    /**   
     * @Title: getUserNamePassword   
     * @Description:  获取自助用户名密码  
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return LazyDynaBean 
     * @author:wangrd   
     * @throws   
    */
    public LazyDynaBean getUserNamePassword(String nbase,String a0100)
    {
        if(nbase==null||nbase.length()<=0)
        {
            return null;
        }
        AttestationUtils utils=new AttestationUtils();
        LazyDynaBean fieldbean=utils.getUserNamePassField();
        String username_field=(String)fieldbean.get("name");
        String password_field=(String)fieldbean.get("pass");
        
        StringBuffer sql=new StringBuffer();
        sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
        sql.append(" where a0100='"+a0100+"'");
        List rs=ExecuteSQL.executeMyQuery(sql.toString());
        
        LazyDynaBean rec=null;
        if(rs!=null&&rs.size()>0)
        {
            rec=(LazyDynaBean)rs.get(0);            
        }
        return rec;
    } 
 
    
    /**
     * 加密
     * @param str
     * @return
     */
    public static String encryption(String ori_str)
    {
        if (null == ori_str || "".equals(ori_str)) {
            return "";
        }
        
    	String to_str=PubFunc.convertUrlSpecialCharacter(PubFunc.encryption(SafeCode.convertTo64Base(ori_str))); 
    	return to_str;
    }
    
    /**
     * 解密
     * @param ori_str
     * @return
     */
    public static String decryption(String ori_str)
    {
        if (null == ori_str||"".equals(ori_str)) {
            return "";
        }
        
    	String to_str=SafeCode.convert64BaseToString(PubFunc.decryption(PubFunc.convertCharacterUrlSpecial(ori_str))); 
    	return to_str;
    }
    
  
    /**   
     * @Title: getUsrA0101   
     * @Description: 取得人员姓名   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getUsrA0101(String nbase,String a0100) {
        String a0101="";
        if (nbase==null || a0100==null){
            return "";
        }
        RowSet rset=null;
        String strsql="select * from "+nbase+"A01 where a0100='"+a0100+"'";
        try{
            rset=dao.search(strsql);
            if (rset.next()){
                a0101= rset.getString("a0101");
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return a0101;
    } 
    /**
     * 根据登录名查找姓名
     * @param username
     * @return
     * 废弃不用,有问题 2016/01/19 
    public String getUsrA0101(String username) {
        String a0101="";
        if (username == null){
            return "";
        }
        RowSet rset=null;
        String strsql="select * from usra01 where username = ?";
        try{
            rset=dao.search(strsql, Arrays.asList(new Object[]{username}));
            if (rset.next()){
                a0101= rset.getString("a0101");
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return a0101;
    } 
    */
    
    /**
     * 根据登录名查找id
     * @param username
     * @return
     */
    public String getUserId(String username) {
        String a0100="";
        if (username == null){
            return "";
        }
        RowSet rset=null;
        String strsql="select * from usra01 where username = ?";
        try{
            rset=dao.search(strsql, Arrays.asList(new Object[]{username}));
            if (rset.next()){
                a0100= rset.getString("a0100");
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return a0100;
    }
    
    /**   
     * @Title: getPersonVo   
     * @Description:    
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return RecordVo 
     * @author:wangrd   
     * @throws   
    */
    public RecordVo getPersonVo(String nbase,String a0100) {
        if (nbase==null || a0100==null){
            return null;
        }
        RecordVo vo =new RecordVo(nbase+"A01");
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            vo.setString("a0100",a0100);
            vo = dao.findByPrimaryKey(vo);
           
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {

        }
        return vo;
    } 
    
    
    /**   
     * @Title: getSuperE01a1s   
     * @Description: 获取上级岗位   
     * @param @param e01a1
     * @param @param addSelf 是否加当前岗位
     * @param @return 返回上级岗位 以逗号分隔 岗位加单引号
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getSuperE01a1s(String e01a1,boolean addSelf) {
        String superE01a1s="";
        initSuperiorFld();
        if (superiorFld==null || superiorFld.length()<1) {
            return"";
        }
        
        String strsql="select "+superiorFld+" from k01 where e01a1='"+e01a1+"'";
        RowSet rset=null;
        try{
            rset=dao.search(strsql);
            if (rset.next()){
                String superE01a1= rset.getString(1);
                if (superE01a1!=null && !"".equals(superE01a1)){
                    if (!superE01a1.equals(e01a1)) {                  
                        String ids=getSuperE01a1s(superE01a1,true);  
                        if (ids.length()>0){
                            if(superE01a1s.length()>0) {
                                superE01a1s=superE01a1s+ ","+ids;
                            } else {
                                superE01a1s=ids;
                            }
                        }
                    }
                }
            }
            if (addSelf){
                e01a1="'"+e01a1+"'";
                if(superE01a1s.length()>0) {
                    superE01a1s=superE01a1s+","+e01a1;
                } else {
                    superE01a1s=e01a1;
                }
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return superE01a1s;
    }
    
    /**   
     * @Title: getDirectSuperE01a1   
     * @Description: 获取我的直接上级岗位  
     * @param @param e01a1
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getDirectSuperE01a1(String e01a1) {
        String superE01a1="";
        initSuperiorFld();
        if (superiorFld==null || superiorFld.length()<1) {
            return"";
        }
        
        String strsql="select "+superiorFld+" from k01 where e01a1='"+e01a1+"'";
        RowSet rset=null;
        try{
            rset=dao.search(strsql);
            if (rset.next()){
                superE01a1= rset.getString(1);
                if(superE01a1==null) {
                    superE01a1="";
                }
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return superE01a1;
    }
    /**   
     * @Title: getDirectSuperE01a1   
     * @Description: 获取我的直接上级岗位  ,如果上级岗位没人就找他的上上级 
     * @param @param e01a1
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getApprovedSuperE01a1(String e01a1) {
        String superE01a1="";
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
        String strpres = "";
        if(login_vo != null) {
            strpres = login_vo.getString("str_value");
        }
        String[] pres= strpres.split(",");
        initSuperiorFld();
        if (superiorFld==null || superiorFld.length()<1) {
            return"";
        }
        
        String strsql="select "+superiorFld+" from k01 where e01a1='"+e01a1+"'";
        RowSet rset=null;
        try{
            rset=dao.search(strsql);
            if (rset.next()){
                superE01a1= rset.getString(1);
                if(superE01a1==null) {
                    superE01a1="";
                }
                //判断当前的上级岗位是否有人，没有在找他的上上级
                boolean flag=true;
                for (int j = 0; j < pres.length; j++) {
                    if ("".equals(pres[j])) {
                        continue;
                    }
	                String sql="select * from  "+pres[j]+"A01 where e01a1='"+superE01a1+"'";
	                RowSet rset1=dao.search(sql);
	                if(rset1.next()){//上级岗位有人的情况
	                	flag=false;
	                	break;
	                }else{
	                	continue;
	                }
                }
                if(flag){
                	superE01a1=getApprovedSuperE01a1(superE01a1);
                }
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return superE01a1;
    }
    
    
   
    /**   
     * @Title: getMyAllSuperPerson   
     * @Description:  获取我的所有上级，返回人员编号 以逗号分隔  
     * @param @param objectid
     * @param @param p0723 区分 人员 团队
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getMyAllSuperPerson(String objectid,String p0723) {
        String superObjectid = "";
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
        String strpres = "";
        if(login_vo != null) {
            strpres = login_vo.getString("str_value");
        }
        String[] pres= strpres.split(",");
        
        
        //兼职            
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
        ArrayList e01a1list=null;
        if("1".equals(p0723)){
            String  nbase =objectid.substring(0, 3);
            String a0100 =objectid.substring(3); 
            e01a1list = this.getMyE01a1List(nbase, a0100);
        }   
        else if("2".equals(p0723)){
            //获取团队的负责岗位
            e01a1list= new ArrayList();
            String e01a1 =getDeptLeaderE01a1(objectid);
            if ("".equals(e01a1)) {
                return "";
            }
            LazyDynaBean bean = new LazyDynaBean();                    
            bean.set("e01a1", e01a1);
            e01a1list.add(bean);            
        }
        RowSet rset=null;
        try{
            for (int i = 0; i < e01a1list.size(); i++) {
                LazyDynaBean e01abean = (LazyDynaBean) e01a1list.get(i);
                String e01a1 = (String) e01abean.get("e01a1");
                if ("".equals(e01a1)) {
                    continue;
                }
                String e01a1s=this.getSuperE01a1s(e01a1, false);
                if ("".equals(e01a1s)) {
                    continue;
                }
                for (int j = 0; j < pres.length; j++) {
                    if ("".equals(pres[j])) {
                        continue;
                    }
                     
                    String strsql="select a0100 from "+pres[j]+"A01 where e01a1 in ("+e01a1s+")";
                    if("true".equals(flag)){
                        String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                        String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
                        String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                        String curPartTab=pres[j]+setid;
                        if (!("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field))
                        {
                            strsql=strsql+" union select a0100 from "+curPartTab
                            +" where " +e01a1_field+" in ("+e01a1s+")"+" and " +appoint_field+"='0'";
                        }
                    }       
                    
                    rset=dao.search(strsql);
                    while (rset.next()){
                        String _a0100= rset.getString(1);
                        if (superObjectid.length()<1) {
                            superObjectid=pres[j]+_a0100;
                        } else {
                            superObjectid=superObjectid+','+pres[j]+_a0100;
                        }
                    } 
                        
                }   
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return superObjectid;
    }
    
    
    /**   
     * @Title: getMyDirectSuperPerson   
     * @Description:  获取我的直接上级  
     * @param @param objectid
     * @param @param p0723 区分 人员 团队
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
   public String getMyDirectSuperPerson(String nbase,String a0100) {
        String superObjectid = "";
        RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
        String strpres = "";
        if(login_vo != null) {
            strpres = login_vo.getString("str_value");
        }
        String[] pres= strpres.split(",");
        RowSet rset=null;
        try{
            RecordVo personvo=getPersonVo(nbase, a0100);
            String e01a1= personvo.getString("e01a1");
            String superE01a1=getDirectSuperE01a1(e01a1);
            for (int j = 0; j < pres.length; j++) {
                if ("".equals(pres[j])) {
                    continue;
                }
                 
                String strsql="select a0100 from "+pres[j]+"A01 where e01a1 ='"+superE01a1+"'";
                rset=dao.search(strsql);
                if (rset.next()){
                    superObjectid= pres[j]+rset.getString(1);
                   break;
                } 
            }   
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return superObjectid;
    }
   

    public String getMyApprovedSuperPerson(String nbase,String a0100) {
        
            RecordVo personvo=getPersonVo(nbase, a0100);
            String e01a1= personvo.getString("e01a1");
            String superObjectid=getMyApprovedSuperPerson(e01a1);
        return superObjectid;
    }
    
    public String getMyApprovedSuperPerson(String e01a1) {
    	 String superObjectid = "";
         RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN");
         String strpres = "";
         if(login_vo != null) {
             strpres = login_vo.getString("str_value");
         }
         String[] pres= strpres.split(",");
         RowSet rset=null;
         try{
        	 boolean flag = true;
             String superE01a1=getDirectSuperE01a1(e01a1);
             if ("".equals(superE01a1)) {
                 return "";
             }
            	 for (int j = 0; j < pres.length; j++) {
                     if ("".equals(pres[j])) {
                         continue;
                     }
                      
                     String strsql="select a0100 from "+pres[j]+"A01 where e01a1 ='"+superE01a1+"'";
                     rset=dao.search(strsql);
                     if (rset.next()){
                    	 flag=false;
                         superObjectid= pres[j]+rset.getString(1);
                         break;
                     }else{
                    	 continue;
                     }
                 }   
            	if(flag){
            		superObjectid= getMyApprovedSuperPerson( superE01a1);
            	} 
             
         }
         catch(Exception e){           
             e.printStackTrace();  
         } finally {
             closeDBResource(rset);
         }
         return superObjectid;
    	
    }
    
      
    /**   
     * @Title: isHaveDirectSuper   
     * @Description: 是否有上级岗位    
     * @param @param objectid
     * @param @param p0723
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isHaveDirectSuper(String objectid,String p0723) {
        boolean b = false;
        try{
            String e01a1="";
            if("1".equals(p0723)){
                String  nbase =objectid.substring(0, 3);
                String a0100 =objectid.substring(3); 
                RecordVo personvo=getPersonVo(nbase, a0100);
                e01a1= personvo.getString("e01a1");
            }   
            else if("2".equals(p0723)){
                //获取团队的负责岗位
                e01a1 =getDeptLeaderE01a1(objectid);          
            }
            
            String superE01a1=getDirectSuperE01a1(e01a1);
            if (superE01a1.length()>0 &&!superE01a1.equals(e01a1)){
                b=true;
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
        }
        return b;
    }
    
    
    /**   
     * @Title: getPeopleSqlByE01a1   
     * @Description:  获取某岗位在编人员的sql语句  
     * @param @param e01a1 岗位编号
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPeopleSqlByE01a1(String e01a1) {
        String strSql="";
        String [] arrpre=getSelfUserDbs();    
        //兼职            
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
        /**兼职参数*/
        String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
          
        if( arrpre.length<1 ) {
            return "";
        }
        try{            
            for (int i=0;i<arrpre.length;i++){                
                String pre = arrpre[i];                
                String a01tab=pre+"A01";
                //主职
                String sql="select a0100,"
                    +"'"+pre+"' as nbase,0 as ispart "+",a0101 from "+a01tab
                    +" where E01A1 ='"+e01a1+"'"
                    +" ";
                //兼职
                if("true".equals(flag)){
                    String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
                    String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
                    String e01a1_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos"); 
                    String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                    String curPartTab=pre+setid;
                    if (!(("".equals(setid))||("".equals(e01a1_field))||"".equals(appoint_field)))
                    {
                       // if ("".equals(dept_field)||dept_field==null)dept_field="''";
                        sql=sql+" union "+"select "+a01tab+".a0100, "
                        +"'"+pre+"' as nbase,1 as ispart, "+a01tab+".a0101 from "+curPartTab+" left join "+a01tab
                        +" on "+curPartTab+".a0100="+a01tab+".a0100"
                        +" where "+e01a1_field+"='"+e01a1+"'"
                        +" and "+appoint_field+"='0'"
                        +" ";
                    }
              
                }  
                
                if (!"".equals(strSql)){
                    strSql=strSql+" union ";
                }
                strSql=strSql+  sql;
          
            }
            
        }
        catch(Exception e){           
            e.printStackTrace();  
        }  
        return strSql;
    }   
    
    /**   
     * @Title: isHZChar   
     * @Description:是否是汉字字符    
     * @param @param c
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public  boolean isHZChar(char c)
     {
         boolean isCorrect =false;
         if((c>='0'&&c<='9')||(c>='a'&&c<='z')||(c>='A'&&c<='Z'))
         {    
             isCorrect =false;  
         }else if(c=='-'||c=='/'){
             isCorrect =true; 
         }else{   
           if(Character.isLetter(c))
           {   //中文   
               isCorrect =true; 
           }else{   //符号或控制字符   
               isCorrect =false; 
           }   
         } 
         return isCorrect;
     } 
     
    /**   
     * @Title: getTruncateA0101   
     * @Description: 如果姓名大于指定长度 需截取姓名 获取截取后的姓名，   
     * @param @param a0101
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getTruncateA0101(String a0101) {        
        return  getTruncateA0101(a0101,WorkPlanConstant.HZA0101TRUNCATELEN*2);
    }
    
    
    /**   
     * @Title: getTruncateA0101   
     * @Description: 如果姓名大于指定长度 需截取姓名 获取截取后的姓名，   
     * @param @param a0101
     * @param @param len 需截取的长度
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getTruncateA0101(String a0101,int len) {         
        String returnStr=a0101;
        if (a0101==null){
          return "";  
        }
        
        if (returnStr.length()>0){
            int count=0;
            for(int i=0;i<returnStr.length();i++)
            {
                char c =returnStr.charAt(i);            
                if(isHZChar(c)){                            
                    count=count+2;  
                }   
                else {
                    count++; 
                }
               if(count>=len){
                   returnStr=returnStr.substring(0,i+1);
                   break;
               } 
            }  
        }
        return returnStr;
    }
   
    
    /** 格式化字符串，将其转换为浏览器能识别的字符或标签 */
    public static String formatText(String text) {
    	if (text == null || "".equals(text.trim())) {
    		return "";
    	}
    	
    	String rt = text;
//    	rt = rt.replaceAll("<", "&lt;");
//    	rt = rt.replaceAll(">", "&gt;");
    	rt = rt.replaceAll("&", "&#38;");
    	rt = rt.replaceAll("\r\n", "<br/>");
    	rt = rt.replaceAll("\n", "<br/>");
    	rt = rt.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    	rt = rt.replaceAll(" ", "&nbsp;");
    	
    	return rt;
    }
    
    /**   
     * @Title: getPlanCycleDesc   
     * @Description:  显示计划期间格式  
     * @param @param periodType
     * @param @param periodYear
     * @param @param periodMonth
     * @param @param periodWeek
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPlanPeriodDesc(String periodType, String periodYear, String periodMonth, String periodWeek) {
        String desc = periodYear + "年";
        if (WorkPlanConstant.Cycle.YEAR.equals(periodYear)) {
            return desc;
        }
        
        if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)) {
            if ("1".equals(periodMonth)) {
                desc = desc + "上";
            } else {
                desc = desc + "下";
            }
            desc = desc + "半年";              
        } else if (WorkPlanConstant.Cycle.QUARTER.equals(periodType)) {
            desc = desc + "第" + WorkPlanConstant.NUM_DESC[Integer.parseInt(periodMonth)-1] + "季度";
        } else if (WorkPlanConstant.Cycle.MONTH.equals(periodType)) {
            desc = desc + periodMonth + "月";
        } else if (WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
            desc = desc + periodMonth + "月"
                 + "第" + WorkPlanConstant.NUM_DESC[Integer.parseInt(periodWeek)-1] + "周";
            String[] dates=getBeginEndDates(periodType, periodYear, periodMonth,
                    Integer.parseInt(periodWeek));
            String firstday = dates[0];
            String endday = dates[1];
            Date firstDay =DateUtils.getDate(firstday, "yyyy-MM-dd");
            Date endDay =DateUtils.getDate(endday, "yyyy-MM-dd");
            desc=desc+"("+DateUtils.format(firstDay, "M.d")+"~"+DateUtils.format(endDay, "M.d")+")";
            
        }
            
        return desc;
    }
    
    /**   
     * @Title: getPlanPeriodTypeDesc   
     * @Description: 获取计划类型json   
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPlanPeriodTypeDesc(String periodtype) {
        String desc="年计划";
        if (WorkPlanConstant.Cycle.YEAR.equals(periodtype)){            
            return desc;
        }
        else if (WorkPlanConstant.Cycle.WEEK.equals(periodtype)){
            desc="周计划";
        }
        else if (WorkPlanConstant.Cycle.MONTH.equals(periodtype)){
            desc="月计划";
        }
        else if (WorkPlanConstant.Cycle.QUARTER.equals(periodtype)){
            desc="季度计划";
        }
        else if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodtype)){
            desc="半年计划";
        }
        return desc;
        
    }
    /**   
     * @Title: CovertToSummaryCycle   
     * @Description:将计划期间类型转为总结期间类型    
     * @param @param planCycle
     * @param @return 
     * @return int 
     * @author:wangrd   
     * @throws   
    */
    public int CovertToSummaryCycle(String planCycle) {
        String[] summaryCycles = {
                WorkPlanConstant.SummaryCycle.Day, 
                WorkPlanConstant.SummaryCycle.YEAR, 
                WorkPlanConstant.SummaryCycle.HALFYEAR,
                WorkPlanConstant.SummaryCycle.QUARTER, 
                WorkPlanConstant.SummaryCycle.MONTH, 
                WorkPlanConstant.SummaryCycle.WEEK };
        int summaryCycle = Integer.parseInt(summaryCycles[Integer.parseInt(planCycle)]);
        
        return summaryCycle;
    }
    
    /**   
     * @Title: CovertToPlanCycle   
     * @Description: 将总结期间类型转为计划期间类型   
     * @param @param summaryCycle
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String CovertToPlanCycle(int summaryCycle) {
        String[] planCycles = { Cycle.Day, Cycle.WEEK, Cycle.MONTH,Cycle.QUARTER, 
                Cycle.YEAR, Cycle.HALFYEAR };
        String planCycle = planCycles[summaryCycle];
        return planCycle;
       
    }
    
    
    /**   
     * @Title: isDateInThisWeekh   
     * @Description: 判断当前日期是否在给定的周内。   
     * @param @param curDate
     * @param @param year
     * @param @param month
     * @param @param week
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean isDateInThisWeekh(Date curDate,int year,int month,int week) {
        boolean b=false;
        WorkPlanSummaryBo wsBo = new WorkPlanSummaryBo(userView, this.conn);
        String[] summaryDates = wsBo.getSummaryDates(WorkPlanConstant.SummaryCycle.WEEK,
                String.valueOf(year), String.valueOf(month), week);
        String firstday = summaryDates[0];
        String endday = summaryDates[1];
        
        if ( (DateUtils.getDate(firstday, "yyyy-MM-dd").before(curDate)
                && DateUtils.getDate(endday, "yyyy-MM-dd").after(curDate))
                || firstday.equals(DateUtils.FormatDate(curDate,  "yyyy-MM-dd"))
                || endday.equals(DateUtils.FormatDate(curDate,  "yyyy-MM-dd"))                   
        ) {//
            b=true;
        } 
        
        return b;
    }
    /**   
     * @Title: getCurWeek   
     * @Description:  获取某一日期 是否属于哪一个月的第几周  
     * @param @param curDate
     * @param @return 
     * @return int[] 第一个元素表示年  第二个元素表示月   第三个元素表示第一周
     * @author:wangrd   
     * @throws   
     */
    public int[] getWhichWeekInMonth(Date curDate) {
        int year = DateUtils.getYear(curDate);  
        int month = DateUtils.getMonth(curDate);  
        int day = DateUtils.getDay(curDate);  
        WorkPlanSummaryBo wsBo = new WorkPlanSummaryBo(userView, this.conn);
        int weekNum=4;
        try {
            //判断当前日期是否属于上一个月的最后一周
            int curYear=year;
            int curMonth=month;
            
            if (day<7){
                if (month==1){
                    curYear=year-1;
                    curMonth=12;                
                }
                weekNum = wsBo.getWeekNum(curYear, curMonth); 
                if (isDateInThisWeekh(curDate,curYear,curMonth,weekNum)){
                    int[] arrWeek = {curYear, curMonth,weekNum};
                    return arrWeek;
                }
            }
            //判断是否属于当月的某一周
            curYear=year;
            curMonth=month;    
            weekNum = wsBo.getWeekNum(curYear, curMonth);
            for (int i = 1; i <= weekNum; i++) {                  
                if (isDateInThisWeekh(curDate,curYear,curMonth,i)){
                    int[] arrWeek = {curYear, curMonth,i};
                    return arrWeek;
                }
            }            
       
             //判断是否属于下一个月的第一周
            if (month==12){
                curYear=year+1;
                curMonth=1;                
            }     
            else {
                curMonth=curMonth+1;
            }
            weekNum = wsBo.getWeekNum(curYear, curMonth); 
            if (isDateInThisWeekh(curDate,curYear,curMonth,1)){
                int[] arrWeek = {curYear, curMonth,1};
                return arrWeek;
            }
  
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
        }
        int[] arrWeek = {year, month,1};
        return arrWeek;
    } 
    

    /**   
     * @Title: getBeginEndDates   
     * @Description: 获取某一期间的旗帜日期   
     * @param @param periodType
     * @param @param period_year
     * @param @param period_month
     * @param @param period_week
     * @param @return 
     * @return String[] 
     * @author:wangrd   
     * @throws   
    */
    public String[] getBeginEndDates(String periodType, String period_year, 
            String period_month, int period_week) {
        String startDate = "";
        String endDate = "";

        if (WorkPlanConstant.Cycle.YEAR.equals(periodType)) {
            startDate = period_year + "-01-01";
            endDate = period_year + "-12-31";
        } else if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)) {
            if (Integer.parseInt(period_month)==1) {
                startDate = period_year + "-01-01";
                endDate = period_year + "-06-30";
            } else {
                startDate = period_year + "-07-01";
                endDate = period_year + "-12-31";
            }
        } else if (WorkPlanConstant.Cycle.QUARTER.equals(periodType)) {
            switch (Integer.parseInt(period_month)) {
            case 1:
                startDate = period_year + "-01-01";
                endDate = period_year + "-03-31";
                break;
            case 2:
                startDate = period_year + "-04-01";
                endDate = period_year + "-06-30";
                break;
            case 3:
                startDate = period_year + "-07-01";
                endDate = period_year + "-09-30";
                break;
            case 4:
                startDate = period_year + "-10-01";
                endDate = period_year + "-12-31";
                break;
            }
        } else if (WorkPlanConstant.Cycle.MONTH.equals(periodType)) {
            startDate = period_year + "-";
            if (Integer.parseInt(period_month) < 10) {
                startDate = startDate + "0";
            }
            startDate = startDate + period_month + "-";
            endDate = startDate;
            
            startDate = startDate + "01";
            
            Calendar cal = Calendar.getInstance();  
            cal.set(Integer.parseInt(period_year), Integer.parseInt(period_month) - 1, 1);  
            int lastDayInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);  
            endDate = endDate + String.valueOf(lastDayInMonth);
        } else if (WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
            WorkPlanSummaryBo wpsBo= new WorkPlanSummaryBo(null, this.conn);
            startDate = wpsBo.getMondayOfDate(Integer.parseInt(period_year), 
                    Integer.parseInt(period_month), period_week);
            endDate = wpsBo.getSunDayOfDate(Integer.parseInt(period_year), 
                    Integer.parseInt(period_month), period_week);
        }

        String[] Dates = {startDate, endDate};
        return Dates;
    }
    
    /**   
     * @Title: getWeekNum   
     * @Description:获取某一个月的周数    
     * @param @param period_year
     * @param @param period_month
     * @param @return 
     * @return int 
     * @author:wangrd   
     * @throws   
    */
    public int getWeekNum(String period_year, String period_month) {
       return new WorkPlanSummaryBo(null, this.conn).getWeekNum(Integer.parseInt(period_year),
                Integer.parseInt(period_month));
        
    }
    
    /**   
     * @Title: getWeekNum   
     * @Description:获取某一个月的周数    
     * @param @param period_year
     * @param @param period_month
     * @param @return 
     * @return int 
     * @author:wangrd   
     * @throws   
     */
    public int getWeekNum(int period_year, int period_month) {
        return new WorkPlanSummaryBo(null, this.conn).getWeekNum(period_year,
                period_month);
        
    }
    
    /**   
     * @Title: getLocationPeriod   
     * @Description:获取需要定位的月、周、季度、半年，非当前年值为1，其他取当前期间    
     * @param @param periodType
     * @param @param year
     * @param @param month
     * @param @return 
     * @return int[] 年、月（季度 ）、周
     * @author:wangrd   
     * @throws   
    */
    public int[] getLocationPeriod(String periodType, int year, int month) {
        int curIndex = 1;        
        int[] weeks = {year,curIndex,curIndex};
        Date now = new Date();
        int curYear = DateUtils.getYear(now);      
 
        int weeknum = 1; 
        if (WorkPlanConstant.Cycle.MONTH.equals(periodType)) {
            weeknum = 12;
        } else if (WorkPlanConstant.Cycle.QUARTER.equals(periodType)) {
            weeknum = 4;
        } else if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)) {
            weeknum = 2;
        }
        
        try {
            if (WorkPlanConstant.Cycle.WEEK.equals(periodType)){
                int weeks1[]= getWhichWeekInMonth(now);  
                if ((year==weeks1[0]) && (month==weeks1[1])) {//今天属于当前所选年月  
                    weeks[0]=weeks1[0];
                    weeks[1]=weeks1[1];
                    weeks[2]=weeks1[2];
                    return weeks;
                }
                else {
                    weeks[0]=year;
                    weeks[1]=month;
                    weeks[2]=curIndex;
                    return weeks;
                }
            }
            else {
                if (curYear != year){//如果所选年度不是当年
                    return weeks;
                }
                for (int i = 1; i <= weeknum; i++) {                                     
                    String[] summaryDates = getBeginEndDates(periodType,
                            String.valueOf(year), String.valueOf(i), i);
                    String firstday = summaryDates[0];
                    String endday = summaryDates[1];
                    
                    if ( (DateUtils.getDate(firstday, "yyyy-MM-dd").before(now)
                            && DateUtils.getDate(endday, "yyyy-MM-dd").after(now))
                            || firstday.equals(DateUtils.FormatDate(now,  "yyyy-MM-dd"))
                            || endday.equals(DateUtils.FormatDate(now,  "yyyy-MM-dd"))                   
                    ) {
                        curIndex = i;    
                        weeks[1]=curIndex;
                        weeks[2]=curIndex;
                        break;
                    } 
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return weeks;
        
    } 
    
  
    /**   
     * @Title: isMyManagePeople   
     * @Description: 是否是我管辖范围内的人或部门   
     * @param @param objectid 编号
     * @param @param p0723 区分人员部门
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean  isMyManagePeople(String objectid,String p0723) {
        boolean b=true;
        String operOrg = this.userView.getUnitIdByBusi("5");
        String orgWhere = "1=1";
        
        if("1".equals(p0723)){
            if (operOrg != null && operOrg.length() > 3) {
                StringBuffer tempSql = new StringBuffer("");
                String[] temp = operOrg.split("`");
                for (int i = 0; i < temp.length; i++) {
                    if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  e0122 like '" + temp[i].substring(2) + "%'");
                    }
                }
                orgWhere = tempSql.substring(3);
            }
    
        }   
        else if("2".equals(p0723)){
            if (operOrg != null && operOrg.length() > 3) {
                StringBuffer tempSql = new StringBuffer("");
                String[] temp = operOrg.split("`");
                for (int i = 0; i < temp.length; i++) {                
                    tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
                }
                orgWhere = tempSql.substring(3);
            }
        }

        try {
            if("1".equals(p0723)){
                String  nbase =objectid.substring(0, 3);
                String a0100 =objectid.substring(3); 
                String sql = "select * from " + nbase + "A01" + " where a0100=? and " + orgWhere + " ";
                
                ArrayList paramList= new ArrayList();
                paramList.add(a0100);
                RowSet rset=dao.search(sql,paramList);
                if (rset.next()){
                    b=true;
                }
                closeDBResource(rset);
            }   
            else if("2".equals(p0723)){
                String sql = "select * from organization where codeitemid=? and " + orgWhere + " ";
                
                ArrayList paramList= new ArrayList();
                paramList.add(objectid);
                RowSet rset=dao.search(sql,paramList);
                if (rset.next()){
                    b=true;
                }
                closeDBResource(rset); 
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return b;
        
    }
    
    /**   
     * @Title: isMyManagePeople   
     * @Description: 是否是我管辖范围内的岗位   
     * @param @param objectid 编号
     * @param @param p0723 区分人员部门
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public boolean  isMyManageE01a1(String e01a1) {
        boolean b=true;
        String operOrg = this.userView.getUnitIdByBusi("5");
        String orgWhere = "1=1";
 
        if (operOrg != null && operOrg.length() > 3) {
            StringBuffer tempSql = new StringBuffer("");
            String[] temp = operOrg.split("`");
            for (int i = 0; i < temp.length; i++) {                
                tempSql.append(" or  codeitemid like '" + temp[i].substring(2) + "%'");
            }
            orgWhere = tempSql.substring(3);
        }
     
        try {            
                String sql = "select * from organization where codeitemid=? and " + orgWhere + " ";
                
                ArrayList paramList= new ArrayList();
                paramList.add(e01a1);
                RowSet rset=dao.search(sql,paramList);
                if (rset.next()){
                    b=true;
                }
                closeDBResource(rset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return b;
        
    }
    
    /**   
     * @Title: getPaginationSql   
     * @Description:  返回分页语句  
     * @param @param tablename
     * @param @param page_size
     * @param @param cur_page 等于-1 标识取末页
     * @param @param orderFld
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getPaginationSql(String tablename,int page_size,int cur_page,String orderFld,HashMap returnMap,ArrayList paramList) {
        String strSql="";
        int db_type=Sql_switcher.searchDbServer();//数据库类型           
        int pagecount=0;
        strSql="select count(*) cnt from ("+tablename+") pageTab";
        try{
            RowSet rset=dao.search(strSql,paramList);
            if (rset.next()){
                pagecount=rset.getInt(1);
            }
            closeDBResource(rset);
        }
        catch(Exception e){           
            e.printStackTrace();  
        }
        if (page_size<1) {
            page_size=10;
        }
        int maxPage=(pagecount/page_size);
        if ((pagecount% page_size)>0){
            maxPage++;
        }
        
  
        if (cur_page>1){//是否大于最大页
            if (maxPage>0 && (cur_page>maxPage)){
                cur_page=maxPage; 
            }
            else  if (maxPage==0){
                cur_page=1;
            }
        }
        else if (cur_page==-1) {
        	if (maxPage == 0) {
                cur_page = 1 ;
            } else {
                cur_page=maxPage;
            }
        }
        tablename="("+tablename+") pageTab ";
        if  (db_type==1){                
            if (cur_page==1){
                strSql="select top "+page_size+" * from "+tablename+" order by "+orderFld;                    
            }
            else {   
                int except_index=(cur_page-1)*page_size;
                
                strSql="select top "+page_size+" * from "+tablename
                +" where "+orderFld+" not in "
                +"(select top "+String.valueOf(except_index)+orderFld+" from"
                + tablename + " order by "+orderFld+" ) order by "+orderFld;
                if (paramList.size()>0){
                    for (int i=paramList.size()-1;i>=0;i--){
                        paramList.add(paramList.get(i));
                    }
                }
            }     
        }
        else {          
            int begin_index=(cur_page-1)*page_size+1;
            int end_index=cur_page*page_size;
            strSql="select pageTab2.* from (select pageTab1.*,rownum rn from (select pageTab.* from "+tablename
            +" order by "+orderFld+") pageTab1 where rownum<="+end_index+") pageTab1  where pageTab2.rn >="+begin_index;                
        } 
        returnMap.put("curPage", cur_page+"");
        returnMap.put("sumCount", pagecount+"");
        returnMap.put("sumPage", maxPage+"");
        
        return strSql;
    }
    
    public static String formatDouble(double dValue,int decimal){
        String strValue=String.valueOf(dValue);        
        try {
            String strFormat="##0";
            for (int i=0;i<decimal;i++){
                if (i==0) {
                    strFormat=strFormat+".";
                }
                strFormat=strFormat+"0";
            }
            DecimalFormat f = new DecimalFormat(strFormat);
            strValue=f.format(dValue);
            
        } catch (Exception e) {
            
        }     
        if (dValue==100){
            strValue="100";
        }
        return strValue;
    }
    
    /** 如果text为空返回默认值，否则返回text，同Oracle的nvl函数 */
    public static String nvl(Object value, String defaultValue) {
    	if (value == null || "".equals(value)) {
    		return defaultValue;
    	} else {
    		return String.valueOf(value);
    	}
    }
    
    /** 将集合中的元素拼接成字符串，默认用逗号分割 */
    public static String join(List list) {
    	if (list == null || list.size() == 0) {
    		return "";
    	}
    	
    	StringBuffer buf = new StringBuffer();
    	for (int i = 0, len = list.size(); i < len; i++) {
    		Object o = list.get(i);
    		if (o == null) {
    			continue;
    		} else {
    			buf.append(o.toString()).append(",");
    		}
    	}
    	buf.replace(buf.length() - 1, buf.length(), "");
    	
    	return buf.toString();
    }

    /** 英文单词的首字母大写 lium */
    public static String initcap(String word) {
    	String s = nvl(word, "");
    	if ("".equals(s)) {
    		return s;
    	} else {
    		return s.substring(0, 1).toUpperCase() + s.substring(1);
    	}
    }

	/**是否是最高领导（无上级）
	 * @param a0100
	 * @param nbase
	 * @return
	 * @author szk
	 * 2014-11-4上午10:47:19
	 * @throws Exception 
	 */
	public String isLeader(String a0100, String nbase) throws Exception {
		// 上级字段(系统参数,可配置)
		if ("".equals(a0100)) {
			return "false";
		}
		
		boolean haveSuperiorFld = false;
		RecordVo ps_superior_vo = ConstantParamter.getRealConstantVo("PS_SUPERIOR", this.conn);
		String ps_superior = "";
		//有参数
		if (ps_superior_vo != null) {
			ps_superior = ps_superior_vo.getString("str_value");
			//参数值有效
			if(ps_superior != null && !"#".equals(ps_superior)) {
			    FieldItem item = DataDictionary.getFieldItem(ps_superior, "K01");
			    //指标存在且已构库
			    if (item != null && !"0".equals(item.getUseflag())) {
                    haveSuperiorFld = true;
                }
			}			    
		}
		
		if(!haveSuperiorFld){
			throw new Exception("岗位参数设置中未设置直接上级指标！");
		}
		
		StringBuffer sql = new StringBuffer("select A0100 from " + nbase + "A01 where E01A1 in (select E01A1 from k01");
		sql.append(" where  " + Sql_switcher.isnull(ps_superior, "''") + "= '' )");
		ArrayList a0100list = dao.searchDynaList(sql.toString());
		if (a0100list.size() != 0) {
			for (int i = 0; i < a0100list.size(); i++) {
				DynaBean bean = (DynaBean) a0100list.get(i);
				if (a0100.equals((String) bean.get("a0100"))) {
					return "true";
				}
			}
		}
			return "false";
	}
    

	/** 根据key和value从LazyDynaBean的集合中查找符合条件的元素 */
	public static LazyDynaBean retrieve(Collection coll, String key, String value) {
		if (coll == null || coll.size() == 0) {
			return null;
		}
		
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Object _tmp = iter.next();
			if (_tmp instanceof LazyDynaBean) {
				LazyDynaBean bean = (LazyDynaBean) _tmp;
				String actualValue = (String) bean.get(key);
				
				if (PlanTaskBo.equal(actualValue, value)) {
					return bean;
				}
			}
		}
		
		return null;
	}


	/**工作总结取上级e01a1
	 * @param e0a01
	 * @return
	 * @author szk
	 * 2014-11-27下午05:19:45
	 */
	public String getSuperE01a1s(String e0a01) {

		String superE01a1="";
        initSuperiorFld();
        if (superiorFld==null || superiorFld.length()<1) {
            return"";
        }
        
        String strsql="select "+superiorFld+" from k01 where e01a1='"+e0a01+"'";
        RowSet rset=null;
        try{
            rset=dao.search(strsql);
            
            if (rset.next()){
                superE01a1= rset.getString(1);
            }
            if (superE01a1==null) {
                superE01a1="";
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            closeDBResource(rset);
        }
        return superE01a1;
    
	}
    
   /**   
 * @Title: getPriorPeriodParam   
 * @Description: 取的上一期间的参数   
 * @param @param periodType
 * @param @param period_year
 * @param @param period_month
 * @param @param period_week
 * @param @return 
 * @return HashMap
 * @author:wangrd   
 * @throws   
*/
public HashMap getPriorPeriodParam(String periodType, String period_year, 
            String period_month, String period_week) {
        HashMap map = new HashMap();
        int year=Integer.parseInt(period_year); 
        map.put("periodType", periodType);
        map.put("periodYear", period_year);
        map.put("periodMonth", period_month);
        map.put("periodWeek", period_week);
        if (WorkPlanConstant.Cycle.YEAR.equals(periodType)) {
            map.put("periodYear", String.valueOf(year-1));
        } else if (WorkPlanConstant.Cycle.HALFYEAR.equals(periodType)) {
            if (Integer.parseInt(period_month)==1) {
                map.put("periodYear", String.valueOf(year-1));  
                map.put("periodMonth", "2");  
            }
            else {
                map.put("periodMonth", "1");  
            }
        } else if (WorkPlanConstant.Cycle.QUARTER.equals(periodType)) {
            if (Integer.parseInt(period_month)==1) {
                map.put("periodYear", String.valueOf(year-1));  
                map.put("periodMonth", "4");  
            }
            else {
                map.put("periodMonth", String.valueOf(Integer.parseInt(period_month)-1));  
            }
        } else if (WorkPlanConstant.Cycle.MONTH.equals(periodType)) {
            if (Integer.parseInt(period_month)==1) {
                map.put("periodYear", String.valueOf(year-1));  
                map.put("periodMonth", "12");  
            }
            else {
                map.put("periodMonth", String.valueOf(Integer.parseInt(period_month)-1));  
            }
        } else if (WorkPlanConstant.Cycle.WEEK.equals(periodType)) {
            if (Integer.parseInt(period_week)==1) {
                if (Integer.parseInt(period_month)==1) {
                    map.put("periodYear", String.valueOf(year-1));  
                    map.put("periodMonth", "12");  
                    int weekNum=getWeekNum(String.valueOf(year-1), "12"); 
                    map.put("periodWeek", weekNum+"");  
                }
                else {
                    map.put("periodMonth",String.valueOf(Integer.parseInt(period_month)-1));  
                    int weekNum=getWeekNum(year, Integer.parseInt(period_month)-1); 
                    map.put("periodWeek", String.valueOf(weekNum));    
                }  
            }
            else {
                map.put("periodWeek", String.valueOf(Integer.parseInt(period_week)-1));   
            }
        }
        return map;
    }

    public static boolean isClient(String clientname) {
        return SystemConfig.getPropertyValue("clientName")!=null&&clientname.equals(SystemConfig.getPropertyValue("clientName").trim());
    }
    
    /**
     * 根据当前登录人的的部门id查出所有上级部门的id
     * @param e0122 当前登陆人的部门id
     * @return
     */
    public String getParentDeptId(String e0122){
		RowSet rset = null;
		String sql = "";
		String dept_ids = "";
		try {
			// sql="select parentid,codesetid from organization where codeitemid='"+e0122+"'and (select codesetid  from organization  where codeitemid=(select parentid from organization where codeitemid='"+e0122+"'))<>'un'";
			sql = "select parentid,codesetid from organization where codeitemid='"
					+ e0122 + "'";
			rset = dao.search(sql);
			while (rset.next()) {
				String parentid = rset.getString("parentid");
				String codesetid = rset.getString("codesetid");
				if ("UM".equals(codesetid)) {
					String ids = getParentDeptId(parentid);
					if (ids.length() > 0) {
						if (dept_ids.length() > 0) {
                            dept_ids = dept_ids + "," + ids;
                        } else {
                            dept_ids = ids;
                        }
					}
				}
			}
			if (dept_ids.length() > 0) {
                dept_ids = dept_ids + "," + e0122;
            } else {
                dept_ids = e0122;
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rset);
		}
		return dept_ids;
    	
    }
    /**
     * 根据当前登录人的的单位id查出所有上级单位的id
     * @param e0110 当前登陆人的单位id
     * @return
     */
    public String getParentUnitId(String b0110){
		RowSet rset = null;
		String sql = "";
		String unit_ids = "";
		try {
			sql = "select parentid,codeitemid  from organization where codeitemid='"
					+ b0110 + "'";
			rset = dao.search(sql);
			while (rset.next()) {
				String parentid = rset.getString("parentid");
				String codeitemid = rset.getString("codeitemid");
				if (!codeitemid.equals(parentid)) {
					String ids = getParentUnitId(parentid);
					if (ids.length() > 0) {
						if (unit_ids.length() > 0) {
                            unit_ids = unit_ids + "," + ids;
                        } else {
                            unit_ids = ids;
                        }
					}
				}
			}
			if (unit_ids.length() > 0) {
                unit_ids = unit_ids + "," + b0110;
            } else {
                unit_ids = b0110;
            }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rset);
		}
		return unit_ids;

	}
    
    /**
     * 查找出当前部门的 直接上级单位（b0110）
     * @param b0110
     * @return
     */
    public String getDirectParentUnit(String b0110){
    	RowSet rset = null;
    	String sql="";
    	try {
    		sql = "select codesetid,parentid  from organization where codeitemid='"+ b0110 + "'";
			rset=dao.search(sql);
			while(rset.next()){
				String idFlag = rset.getString("codesetid");
				String parentid = rset.getString("parentid");
				if(!"UN".equals(idFlag)){
					b0110=getDirectParentUnit(parentid);
				}
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(rset);
		}
		return b0110;
    } 
    


     /** 
    * @Title: sendWeixinMessageFromEmail 
    * @Description:从邮箱格式中获取微信信息 
    * @param @param emailList
    * @return void
    */ 
    public void sendWeixinMessageFromEmail(List emailList){   
    	String corpid = (String) ConstantParamter.getAttribute("wx","corpid");
    	if (corpid==null || "".equals(corpid)){
    		return;
    	}
    	
		try {
			for (int i = 0, len = emailList.size(); i < len; i++) {
				LazyDynaBean emailBean = (LazyDynaBean) emailList.get(i);
				String usrId = (String) emailBean.get("objectId");
				if (usrId != null && usrId.length() > 0) {
					String nbase = usrId.substring(0, 3);
					String a0100 = usrId.substring(3);
					LazyDynaBean abean = getUserNamePassword(nbase, a0100);
					if (abean != null && abean.get("username") != null) {
						String username = (String) abean.get("username");
						emailBean.set("usrname", username);
					}
				}
			}
			sendWeixinMessage(emailList);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 发送待办，工作总结发布
     * @param emailList
     * chent
     */
    public void sendPending_publishSummary(List emailList){   
		try {
			for (int i = 0, len = emailList.size(); i < len; i++) {
				LazyDynaBean emailBean = (LazyDynaBean) emailList.get(i);
				String usrId = (String) emailBean.get("objectId");
				if (usrId != null && usrId.length() > 0) {
					String nbase = usrId.substring(0, 3);
					String a0100 = usrId.substring(3);
					LazyDynaBean abean = getUserNamePassword(nbase, a0100);
					if (abean != null && abean.get("username") != null) {
						String username = (String) abean.get("username");
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("pending_url", (String)emailBean.get("href"));
						bean.set("pending_title", (String)emailBean.get("subject"));
						insertPending_summary(this.userView.getUserName(), username, bean, (String)emailBean.get("p0100"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 发送待办，工作总结退回
     * @param emailList
     * 
     */
    public void sendPending_rejectSummary(List emailList){   
		try {
			for (int i = 0, len = emailList.size(); i < len; i++) {
				LazyDynaBean emailBean = (LazyDynaBean) emailList.get(i);
				String usrId = (String) emailBean.get("objectId");
				if (usrId != null && usrId.length() > 0) {
					String nbase = usrId.substring(0, 3);
					String a0100 = usrId.substring(3);
					LazyDynaBean abean = getUserNamePassword(nbase, a0100);
					if (abean != null && abean.get("username") != null) {
						String username = (String) abean.get("username");
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("pending_url", (String)emailBean.get("href"));
						bean.set("pending_title", (String)emailBean.get("subject"));
						insertPending_rejectSummary(this.userView.getUserName(), username, bean, (String)emailBean.get("p0100"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /**
     * 插入待办，工作总结退回
     * @param sender
     * @param receiver
     * @param bean
     * @param p0100
     */
    public void insertPending_rejectSummary(String sender,String receiver,
    		LazyDynaBean bean,String p0100) {
    	try {
    		String ext_flag="WS_P01_BH_"+p0100;
    		String pending_id= isHavePendingtask(receiver, ext_flag);
    		if(pending_id.length()<1){
    			insertPending(sender,receiver,ext_flag,bean);
    		}else{
    			updatePending("0", pending_id);
    			
    		}
    		//linbz 是否有发布代办，若存在则更新为已办
    		String ext_flag_sp="WS_P01_SP_"+p0100;
    		String pending_id_sp= isHavePendingtask("", ext_flag_sp);
    		pending_id_sp=(pending_id_sp.length()<1)?isHavePendingtask("", "WS_"+p0100):pending_id_sp;
    		if(pending_id_sp.length()>1){
    			updatePending("1", pending_id_sp);
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * 插入待办，工作总结发布
     * @param sender
     * @param receiver
     * @param bean
     * @param p0100
     */
    public void insertPending_summary(String sender,String receiver,
    		LazyDynaBean bean,String p0100) {
    	try {
    		String ext_flag="WS_P01_SP_"+p0100;
    		String pending_id= isHavePendingtask(receiver, ext_flag);
    		//兼容原有的"WS_"+p0100
    		pending_id=(pending_id.length()<1)?isHavePendingtask(receiver, "WS_"+p0100):pending_id;
    		if(pending_id.length()<1){
    			insertPending(sender,receiver,ext_flag,bean);
    		}else{
    			updatePending("0", pending_id);
    		}
    		//linbz 是否有驳回代办，若存在则更新为已办
    		String ext_flag_bh="WS_P01_BH_"+p0100;
    		String pending_id_bh= isHavePendingtask("", ext_flag_bh);
    		if(pending_id_bh.length()>1){
    			updatePending("1", pending_id_bh);
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    /**
     * 更新待办
     * @param emailList
     */
    public void updatePending_approveSummary(String flag, List emailList){   
		try {
			for (int i = 0, len = emailList.size(); i < len; i++) {
				
				LazyDynaBean emailBean = (LazyDynaBean) emailList.get(i);
				
				String p0100 = (String)emailBean.get("p0100");
				String ext_flag="WS_P01_SP_"+p0100;
				
				String pending_id= isHavePendingtask("", ext_flag);
				//兼容原有的"WS_"+p0100
	    		pending_id=(pending_id.length()<1)?isHavePendingtask("", "WS_"+p0100):pending_id;
				if(pending_id.length()>0){
					updatePending(flag, pending_id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public void sendWeixinMessageFromEmail(LazyDynaBean emailBean){  
		try {
			ArrayList list = new ArrayList();
			list.add(emailBean);
			sendWeixinMessageFromEmail(list);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /** 
    * @Title: sendWeixinMessage 
    * @Description:发送多条微信信息 
    * @param @param messageList
    * @return void
    */ 
    public void sendWeixinMessage(List messageList){    
    	String corpid = (String) ConstantParamter.getAttribute("wx","corpid");
    	String dd_corpid=(String) ConstantParamter.getAttribute("DINGTALK","corpid");
    	if ((corpid==null || "".equals(corpid))&&(dd_corpid==null || "".equals(dd_corpid))){
    		return;
    	}
    	 
		try {
			for (int i = 0, len = messageList.size(); i < len; i++) {
				LazyDynaBean emailBean = (LazyDynaBean) messageList.get(i);
				String usrname = (String) emailBean.get("usrname");
				if (usrname==null || usrname.length()<1) {
                    continue;
                }
				String title = (String) emailBean.get("subject");
				String description = (String) emailBean.get("bodyText");
				String url = (String) emailBean.get("href");
				String imgUrl = (String) emailBean.get("imgUrl");
				if (imgUrl==null) {
                    imgUrl=this.userView.getServerurl()+"/UserFiles/Image/tongzhi.png";
                }
				if(corpid!=null&&corpid.trim().length()>0) {
                    WeiXinBo.sendMsgToPerson(usrname, title, description, imgUrl, url);
                }
				if(dd_corpid!=null&&dd_corpid.trim().length()>0) {
                    DTalkBo.sendMessage(usrname, title, description, imgUrl, url);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /** 
     * @Title: sendWeixinMessage 
     * @Description:发送单条微信信息 
     * @param @param emailBean
     * @return void
     */ 
    public void sendWeixinMessage(LazyDynaBean emailBean){  
		try {
			ArrayList list = new ArrayList();
			list.add(emailBean);
			sendWeixinMessage(list);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**   
     * @Title: getMainDept   
     * @Description: 返回主要负责的部门   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return HashMap 
     * @author:wusy   
     * @throws   
    */
    public ArrayList getMainDept(String nbase,String a0100) {
    	ArrayList deptList= new ArrayList();
    	try {
    		String strsql="";
    		String  deptLeaderFld=WorkPlanConstant.DEPTlEADERFld; 
	    	strsql="select B01.b0110,O.codeitemdesc,0 as ispart,"+deptLeaderFld+" from B01,organization O where B01.b0110=O.codeitemid "
	        +" and  "+deptLeaderFld+" in (select E01a1 from "+nbase+"A01"
	        +" where a0100='"+a0100+"')";      
	    	 RowSet rset = null;
			rset = dao.search(strsql);
			setInnerDeptList(rset,deptList); 
			closeDBResource(rset);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return deptList;
    }
    
    
    /**   
     * @Title: getFristMainDept  
     * @Description: 是否是部门负责人,如果是,返回负责部门id,不是返回   (对应B01表B01ps)
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return String
     * @author:wusy   
     * @throws   
    */
    public String getFristMainDept(String nbase, String a0100){
    	String b0110 = "";
    	ArrayList list = (ArrayList) getMainDept(nbase, a0100);
    	if(list.size()==0){
    		return b0110;
    	}
    	LazyDynaBean bean =(LazyDynaBean) list.get(0);
    	b0110 = (String) bean.get("b0110");
    	return b0110;
    }
    
    /**
     * @Title: isPersonExist 
     * @Description: 根据nbase,a0100判断人员是否存在
     * @param nbase 
     * @param a0100 
     * @return boolean
     * @throws GeneralException
     * @author wsy  
     * @date 2015-9-16 下午03:27:42
     */
	public boolean isPersonExist(String nbase, String a0100) throws GeneralException{
		if(StringUtils.isBlank(nbase) || StringUtils.isBlank(a0100)){
			return false;
		}
		RowSet rs = null;
		StringBuffer sbf = new StringBuffer();
		sbf.append("select * from " + nbase + "A01 where a0100 =?");
		try {
			rs = dao.search(sbf.toString(), Arrays.asList(new Object[]{a0100}));
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally{
			PubFunc.closeResource(rs);
		}
		return false;
	}
	

    /**
     * 发布协办任务待办，进入协办任务表中时调用
     * @param sender 发送人
     * @param receiver 接收人
     * @param p0800
     */
    public void sendPending_ToCooperationTask(String sender,String receiver, String p0800) {
    	try {
    		String ext_flag="WP_P10_"+p0800;
			LazyDynaBean bean = new  LazyDynaBean();
			bean.set("pending_url", "/module/workplan/cooperationtask/CooperationTaskApprove.html?1=1");//待办查看链接
			bean.set("pending_title", "部门协作任务申请");//待办标题
			insertPending(sender,receiver,ext_flag,bean);
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
    
    /**
     * 发布协办任务待办，进入我的协办任务表中时调用
     * @param sender 发送人
     * @param receiver 接收人
     * @param p0800
     */
    public void sendPending_ToMyCooperationTask(String sender,String receiver, String title,String p0800) {
    	try {
    		String ext_flag="WP_P10_"+p0800;
			LazyDynaBean bean = new  LazyDynaBean();
			bean.set("pending_url", "/module/workplan/cooperationtask/MyCooperationTask.html?1=1");//待办查看链接
			bean.set("pending_title", title);//待办标题
			insertPending(sender,receiver,ext_flag,bean);
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
    
    /**
     * 发布协办任务待办，进入工作计划页面时调用
     * @param sender 发送人
     * @param receiver 接收人
     * @param p0800
     */
    public void sendPending_ToPlanList(String sender,String receiver, String url, String title, String p0800) {
    	try {
    		String ext_flag="WP_P10_"+p0800;
    		String pending_id= isHavePendingtask(receiver, ext_flag);
    		if(pending_id.length()<1){
    			LazyDynaBean bean = new  LazyDynaBean();
    			bean.set("pending_url", url);//待办查看链接
    			bean.set("pending_title", title);//待办标题
    			insertPending(sender,receiver,ext_flag,bean);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
    
    /**
     * 更新协办任务待办状态
     * @param flag 1:更新待办状态为已办，已阅   2：只更新已阅   9 置为无效状态
     * @param p0800
     * chent
     */
    public void update_cooperationTask(String flag, String p0800) {
     	try {
     		String ext_flag="WP_P10_"+p0800;
     		String pending_id= isHavePendingtask("", ext_flag);
     		if(pending_id.length()>0){
     			updatePending(flag, pending_id);
     		}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}

     }
    /**
     * 删除协办任务待办
     * @param p0800
     * chent
     */
    public void delete_cooperationTask(String p0800) {
    	
     	try {
     		String ext_flag="WP_P10_"+p0800;
     		String pending_id= isHavePendingtask("", ext_flag);
     		if(pending_id.length()>0){
    			String sql = "delete t_hr_pendingtask where pending_id=?";
    			ArrayList<String> list = new ArrayList<String>();
    			list.add(pending_id);
    			dao.delete(sql, list);
     		}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
     }
    /** 
     * @Title: sendPending_publishPlan 
     * @Description: 发布计划时发待办  重新发布也使用此方法
     * @param @param sender
     * @param @param receiver 接收人
     * @param @param p0700 计划号
     * @param @param bean 待办标题及待办链接
     * @return void
     */ 
    public void sendPending_publishPlan(String sender,String receiver,
    		LazyDynaBean bean,String p0700) {
    	try {
    		String ext_flag="WP_P07_SP_"+p0700;
    		String pending_id= isHavePendingtask(receiver, ext_flag);
    		if(pending_id.length()<1){
    			insertPending(sender,receiver,ext_flag,bean);
    		}else {//已有代办记录的需要封信代办状态为未办 haosl update 2018-2-8
    			updatePending("0", pending_id);
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }
    
    /** 
    * @Title: sendPending_approvePlan 
    * @Description:批准计划时 更改待办状态 
    * @param @param p0700
    * @return void
    */ 
    public void updatePending_approvePlan(String p0700) {
    	
    	RowSet rs = null;
    	try {
    		String ext_flag="WP_P07_SP_"+p0700;
    		//receiver传空值，有一个领导批过了，则待办消失。
    		String pending_id= isHavePendingtask("", ext_flag);
    		if(pending_id.length()>0){
    			// 如果重新指派了审批人，则需要清除新老两个审批人的待办 chent 20171218 modify
    			StringBuffer sql = new StringBuffer("");
    			sql.append("select pending_id,ext_flag,pending_status,bread,receiver from t_hr_pendingtask where Pending_type='58'");
    			sql.append(" and ext_flag='").append(ext_flag).append("'");
    			rs = dao.search(sql.toString());
    			while (rs.next()){
    				String _pending_id= rs.getString("pending_id");		
    				updatePending("1", _pending_id);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}

    }
    /** 
     * @Title: sendPending_BackPlan 
     * @Description: 计划驳回时发待办
     * @param @param sender
     * @param @param receiver 接收人
     * @param @param p0700 计划号
     * @param @param bean 待办标题及待办链接
     * @return void
     */ 
     public void sendPending_BackPlan(String sender,String receiver,
     		LazyDynaBean bean,String p0700) {
     	try {
     		String ext_flag="WP_P07_BH_"+p0700;
     		String pending_id= isHavePendingtask(receiver, ext_flag);
     		if(pending_id.length()<1){
     			insertPending(sender,receiver,ext_flag,bean);
     		}else {//已有代办记录的需要封信代办状态为未办 haosl update 2018-2-8
     			updatePending("0", pending_id);
     		}

 		} catch (Exception e) {
 			e.printStackTrace();
 		}

     }
     /** 
      * @Title: sendPending_approvePlan 
      * @Description:发布计划，更改驳回待办状态 
      * @param @param p0700
      * @return void
      */ 
      public void updatePending_BackPlan(String p0700) {
      	try {
      		String ext_flag="WP_P07_BH_"+p0700;
      		String pending_id= isHavePendingtask("", ext_flag);
      		if(pending_id.length()>0){
      			updatePending("1",pending_id);
      		}
  		} catch (Exception e) {
  			e.printStackTrace();
  		}

      }
     /** 
    * @Title: sendPending_remindPublishPlan 
    * @Description: 提醒写工作计划
    * @param @param sender
    * @param @param receiver 接收人
    * @param @param bean 待办标题及待办链接
    * @param @param planParam 有计划信息 如计划类型 计划年 月等期间信息
    * @return void
    */ 
    public void sendPending_remindPublishPlan(String sender,String receiver,
     		LazyDynaBean bean,HashMap planParam) {
     	try {
            String periodtype =getParamValue(planParam,"periodtype");
            String periodyear =getParamValue(planParam,"periodyear");
            String periodmonth =getParamValue(planParam,"periodmonth");
            String periodweek =getParamValue(planParam,"periodweek");           
            String p0723 =getParamValue(planParam,"p0723");           
            String extPlan=periodtype;
            if (WorkPlanConstant.Cycle.YEAR.equals(periodtype)){
            	extPlan=extPlan+"_"+periodyear;
            }
            else  if (WorkPlanConstant.Cycle.MONTH.equals(periodtype)
            		|| WorkPlanConstant.Cycle.HALFYEAR.equals(periodtype)
                    || WorkPlanConstant.Cycle.QUARTER.equals(periodtype)){
            	extPlan=extPlan+"_"+periodyear+"_"+periodmonth;
            }
            else  if (WorkPlanConstant.Cycle.WEEK.equals(periodtype)){
            	extPlan=extPlan+"_"+periodyear+"_"+periodmonth+"_"+periodweek;
            }
            	
          
            if (extPlan.length()>0){
            	String ext_flag="WP_P07_TX_"+p0723+"_"+extPlan;
            	String pending_id= isHavePendingtask(receiver, ext_flag);
            	if(pending_id.length()<1){
            		insertPending(sender,receiver,ext_flag,bean);
            	}
            	
            }
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
     }
    
    public void updatePending_PublishPlan(String receiver,HashMap planParam) {
     	try {
     		  String periodtype =getParamValue(planParam,"periodtype");
              String periodyear =getParamValue(planParam,"periodyear");
              String periodmonth =getParamValue(planParam,"periodmonth");
              String periodweek =getParamValue(planParam,"periodweek");  
              String p0723 =getParamValue(planParam,"p0723");   
              String extPlan=periodtype;
              if (WorkPlanConstant.Cycle.YEAR.equals(periodtype)){
              	extPlan=extPlan+"_"+periodyear;
              }
              else  if (WorkPlanConstant.Cycle.MONTH.equals(periodtype)
              		|| WorkPlanConstant.Cycle.HALFYEAR.equals(periodtype)
                      || WorkPlanConstant.Cycle.QUARTER.equals(periodtype)){
              	extPlan=extPlan+"_"+periodyear+"_"+periodmonth;
              }
              else  if (WorkPlanConstant.Cycle.WEEK.equals(periodtype)){
              	extPlan=extPlan+"_"+periodyear+"_"+periodmonth+"_"+periodweek;
              }
              	
            String ext_flag="WP_P07_TX_"+p0723+"_"+extPlan;
    		String pending_id= isHavePendingtask(receiver, ext_flag);
    		if(pending_id.length()>0){
    			updatePending("1",pending_id);
    		}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
     } 

    /** 
    * @Title: insertPending 
    * @Description:新增一条待办信息
    * @param @param sender
    * @param @param receiver
    * @param @param ext_flag
    * @param @param bean
    * @param @return
    * @return String
    */ 
    public String insertPending(String sender,String receiver,String ext_flag,
    		LazyDynaBean bean) {
    	String pending_id="";
    	try {
    		String pending_url  =(String)bean.get("pending_url");
    		String pending_title  =(String)bean.get("pending_title");
    		if (pending_url==null || pending_url.length()<1
    			|| pending_title==null || pending_title.length()<1){
    			return pending_id;
    		}
    		IDGenerator idg = new IDGenerator(2, conn);
    		pending_id = idg.getId("pengdingTask.pengding_id");
			RecordVo vo = new RecordVo("t_hr_pendingtask");
			vo.setString("pending_id", pending_id);
			vo.setDate("create_time", new Date());
			vo.setDate("lasttime", new Date());
			vo.setString("sender", sender);
			vo.setString("pending_type", "58");//OKR模块
			vo.setString("pending_title",pending_title);
			vo.setString("pending_url", pending_url);
			vo.setString("pending_status", "0");
			vo.setString("pending_level", "1");
			vo.setInt("bread", 0);
			vo.setString("receiver", receiver);
			vo.setString("ext_flag", ext_flag);
			dao.addValueObject(vo);
    	
		} catch (Exception e) {
			pending_id="";
			e.printStackTrace();
		}
    	return pending_id;
    }
	
    /** 
    * @Title: updatePending 
    * @Description: 更新待办信息
    * @param @param flag  0:为待办；1:更新待办状态为已办，已阅   2：只更新已阅   9 置为无效状态
    * @param @param pending_id
    * @param @return
    * @return String
    */ 
    public String updatePending(String flag,String pending_id) {
    	try {
    		StringBuffer sql= new StringBuffer();
    		if ("2".equals(flag)){
    			sql.append("update t_hr_pendingtask set bread='1',Lasttime="
    					+Sql_switcher.sqlNow()+" where ");
    			sql.append(" Pending_status='0' and pending_id="+pending_id);
    		}
    		else if ("9".equals(flag)){
    			sql.append("update t_hr_pendingtask setpending_status='9',Lasttime="
    					+Sql_switcher.sqlNow()+" where ");
    			sql.append(" Pending_status='0' and pending_id="+pending_id);
    		}
    		else if ("1".equals(flag)){
    			sql.append("update t_hr_pendingtask set bread='1',pending_status='1',Lasttime="
    					+Sql_switcher.sqlNow()+" where ");
    			sql.append(" pending_id="+pending_id);
    		}else if ("0".equals(flag)){
    			sql.append("update t_hr_pendingtask set bread='1',pending_status='0',Lasttime="
    					+Sql_switcher.sqlNow()+" where ");
    			sql.append(" pending_id="+pending_id);
    		}
    		
    		if (sql.length()>0) {
                dao.update(sql.toString());
            }
    	
		} catch (Exception e) {
			pending_id="";
			e.printStackTrace();
		}
    	return pending_id;
    }
    
	/** 
	* @Title: isHavePendingtask 
	* @Description: 查看是否有待办
	* @param @param receiver
	* @param @param ext_flag
	* @param @return
	* @return String
	*/ 
	public  String isHavePendingtask(String receiver,String ext_flag){
		String pending_id="";
		RowSet rs = null;
		try{
			String withNoLock="";
			if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
            {
                withNoLock=" WITH(NOLOCK) ";
            }
			StringBuffer sql = new StringBuffer("");
			sql.append("select pending_id,ext_flag,pending_status,bread,receiver from t_hr_pendingtask ");
			sql.append(withNoLock);
			sql.append(" where Pending_type='58'");
			
			if (receiver!=null && receiver.length()>0){
				sql.append(" and receiver='").append(receiver).append("'");
			}	
			//暂取消待办状态条件
//			sql.append(" and pending_status='0' ");
			sql.append(" and ext_flag='").append(ext_flag).append("'");
			rs = dao.search(sql.toString());
			if (rs.next()){
				pending_id= rs.getString("pending_id");		
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return pending_id;
	}
	
    /** 
    * @Title: getUserName 
    * @Description: 根据人员编号获取人员登录用户名
    * @param @param nbase
    * @param @param a0100
    * @param @return
    * @return String
    */ 
    public String getUserNameByA0100(String nbase,String a0100)
    {
    	String username="";
    	if(nbase==null||nbase.length()<=0)
        {
            return null;
        }
        AttestationUtils utils=new AttestationUtils();
        LazyDynaBean fieldbean=utils.getUserNamePassField();
        String username_field=(String)fieldbean.get("name");
        String password_field=(String)fieldbean.get("pass");
        
        StringBuffer sql=new StringBuffer();
        sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
        sql.append(" where a0100='"+a0100+"'");
        List rs=ExecuteSQL.executeMyQuery(sql.toString());
        
        LazyDynaBean rec=null;
        if(rs!=null&&rs.size()>0)
        {
            rec=(LazyDynaBean)rs.get(0);            
        }
        if (rec != null && rec.get("username") != null) {
			username = (String) rec.get("username");
		}
        return username;
    } 
    
    public String getParamValue(HashMap paramMap,String paramName)  {
        String value="";
        if (paramMap.get(paramName)!=null){
           value=(String)paramMap.get(paramName);
        }
        return value;
  }
    
   
    /**
     * 得到用户名usrName,OKR模块,不管是自助用户还是关联自助用户的业务用户登录,保存的时候都保存自助用户的usrName
     * @param userView
     * @return
     */
    public static String getSelfUsrName(UserView userView) {
    	if (userView.getStatus()==0){//业务是0,自助是4
    		return userView.getS_userName();
    	}
    	else {
    		return userView.getUserName();
    	}
    
  }
    
    /**
     * 获取登陆人和计划所有者之间的上下级关系,本人登录返回0,直接上级返回1,上级(不是直接上级)返回2,其他关系返回-1
     * @param p0700
     * @return
     */
    public int getLoaderRole(int p0700){
    	int role = -1;
    	String loaderId = this.userView.getDbname()+this.userView.getA0100();
    	String loaderE01a1s = this.getMyE01a1s(this.userView.getDbname(), this.userView.getA0100());
    	String planOwnerId = "";
    	String planOwnerE01a1 = "";
    	String objectid = "";
	    if(StringUtils.isNotBlank(p0700+"") && p0700>0){
	    	RecordVo p07vo = new RecordVo("p07");
	    	p07vo.setInt("p0700", p0700);
	    	try {
				p07vo = dao.findByPrimaryKey(p07vo);
				int p0723 = p07vo.getInt("p0723");
				String e01a1 = "";
				if(p0723 == 1){
					planOwnerId = p07vo.getString("nbase")+p07vo.getString("a0100");
					objectid = planOwnerId;
					planOwnerE01a1 = getMyMainE01a1(p07vo.getString("nbase"), p07vo.getString("a0100"));
				}else if(p0723 == 2){
					planOwnerId = getFirstDeptLeaders(p07vo.getString("p0707"));
					objectid = p07vo.getString("p0707");
					planOwnerE01a1 = getDeptLeaderE01a1(p07vo.getString("p0707"));
				}else{
					throw new RuntimeException("p0723错误");
				}
				//没有上级不必显示上级评价
				if(!isHaveDirectSuper(objectid, p0723+"")){
					return -1;
				}
				//计划所有人的直接上级岗位
				String planOwnerSuperE01a1 = getDirectSuperE01a1(planOwnerE01a1);
				//判断是否是直接上级
				if(loaderId.equalsIgnoreCase(planOwnerId)){
					return 0;
				}
				if(StringUtils.isNotBlank(planOwnerSuperE01a1)){
					if(loaderE01a1s.indexOf(","+planOwnerSuperE01a1)>-1){
						return 1;//直接上级
					}else if(isMyTeamPeople(planOwnerId.substring(0, 3), planOwnerId.substring(3))){
						return 2;//因为上一步已经验证过是直接上级,所以这里的肯定不是直接下级
					}else{
						//没神马关系
					}
				}else{
					//计划所有者没有上级
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} 
    	}
    	return role;
    }
    
    /**
     * 判断某职位是否是我的上级
     * @param myE01a1
     * @param otherE01a1
     * @return
     */
    public boolean isMySuper(String myE01a1, String otherE01a1){
    	if(StringUtils.isBlank(otherE01a1)){
    		return false;
    	}
    	boolean info = false;
    	String mySuperE01a1 = getDirectSuperE01a1(myE01a1);
    	if(otherE01a1.indexOf(mySuperE01a1) > -1){
    		return true;
    	}else{
    		isMySuper(mySuperE01a1, otherE01a1);
    	}
    	return info;
    }
    /**
	 * 判断两个用户是否有相同上级
	 * @param usrA0100_1：用户1
	 * @param usrA0100_2：用户2
	 * @return
	 * chent
	 */
	public boolean isSameSuper(String nbsA0100_1, String nbsA0100_2){
		boolean isSameSuper = false;
		
		WorkPlanUtil util = new WorkPlanUtil(this.conn, this.userView);
		if(StringUtils.isNotEmpty(nbsA0100_1) && StringUtils.isNotEmpty(nbsA0100_2)){
			String superObjectid_1 = util.getMyDirectSuperPerson(nbsA0100_1.substring(0, 3), nbsA0100_1.substring(3));//用户1的上级
			String superObjectid_2 = util.getMyDirectSuperPerson(nbsA0100_2.substring(0, 3), nbsA0100_2.substring(3));//用户2的上级
			
			if(StringUtils.isNotEmpty(superObjectid_1) && superObjectid_1.equalsIgnoreCase(superObjectid_2)){//两用户上级相同
				isSameSuper = true;
			}
		}
		
		return isSameSuper;
	}
	/**
	 * 根据任务号判断是否为协办任务
	 * @param p0800：任务号
	 * @param isUpdate：如是协办任务，是否更新协办任务标识
	 * @return
	 * chent
	 */
	public boolean isCooperationTask(int p0800, boolean isUpdate){
		boolean isCooperationTask = false;
		
		try{
			PlanTaskBo bo = new PlanTaskBo(this.conn, this.userView);
			RecordVo task = bo.getTask(p0800);
			String P0845 = task.getString("p0845");//1:协作任务  0|空：非协作任务
			if("1".equals(P0845)){//1:协作任务
				isCooperationTask = true;
			} else {
				String startPerson = this.getTaskRolePersionList(p0800, 5).get(0);//任务发起人
				String privPerson = this.getTaskRolePersionList(p0800, 1).size() == 0 ? "" : this.getTaskRolePersionList(p0800, 1).get(0);//任务负责人
				
				
				//是否为协办任务：【发起人和负责人不为同一人】 且 【发起人不是负责人直接上级】，该任务为协办任务
				if(!startPerson.equalsIgnoreCase(privPerson) && !this.isUpperObject(startPerson, privPerson)){
					isCooperationTask = true;
					
					if(isUpdate){
						//如果是协办任务，则把任务协办标识更新
						task.setInt("p0845", 1);
						dao.updateValueObject(task);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return isCooperationTask;
	}
	/**
	 * 判断是否为团队内协办
	 * @param p0800：任务号
	 * @return
	 * chent
	 */
	public boolean isInTeamCooperationTask(int p0800){
		boolean isInTeamCooperationTask = false;
		
		try{
			PlanTaskBo bo = new PlanTaskBo(this.conn, this.userView);
			String startPerson = this.getTaskRolePersionList(p0800, 5).get(0);//任务发起人
			String privPerson = this.getTaskRolePersionList(p0800, 1).get(0);//任务负责人
			String startPersonDirect = this.getMyDirectSuperPerson(startPerson.substring(0, 3), startPerson.substring(3)); //任务发起人直接上级
			String privPersonDirect = this.getMyDirectSuperPerson(privPerson.substring(0, 3), privPerson.substring(3)); //任务负责人直接上级
			
			// 判断团队内部协作：【负责人是发起人直接上级】 或 【发起人和负责人直接上级是同一人】
			if(this.isUpperObject(privPerson, startPerson) || startPersonDirect.equalsIgnoreCase(privPersonDirect)){
				isInTeamCooperationTask = true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return isInTeamCooperationTask;
	}
	/**
	 * 判断用户1是否是用户2的直接上级
	 * @param nbsA0100_1
	 * @param nbsA0100_2
	 * @return
	 * chent
	 */
	public boolean isUpperObject(String nbsA0100_1, String nbsA0100_2){
		boolean isUpperObject = false;
		
		if(StringUtils.isNotEmpty(nbsA0100_1) && StringUtils.isNotEmpty(nbsA0100_2)){
			String nbsA0100_2Director = this.getMyDirectSuperPerson(nbsA0100_2.substring(0, 3), nbsA0100_2.substring(3)); //直接上级
			
			if(!StringUtils.isEmpty(nbsA0100_2Director) && nbsA0100_2Director.equalsIgnoreCase(nbsA0100_1)){
				isUpperObject = true;
			}
		}
		
    	return isUpperObject;
    }
	/**
	 * 获取任务各个角色的人
	 * @param p0800:任务号
	 * @param flag：成员标识：1、负责人 2、参与人 5、发起人
	 * @return
	 * chent
	 */
	public ArrayList<String> getTaskRolePersionList(int p0800, int flag){
		ArrayList<String> list = new ArrayList<String>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "";
			ArrayList sqlList = new ArrayList();
			if(flag == 5){// 发起人
				sql = "SELECT org_id,nbase,a0100 FROM per_task_map WHERE p0800=? AND flag=?";
				sqlList.add(p0800);
				sqlList.add(flag);
			} else{//1、负责人 2、参与人
				sql = "SELECT org_id,nbase,a0100 FROM P09 WHERE P0901=2 AND P0903=? AND P0905=?";
				sqlList.add(p0800);
				sqlList.add(flag);
			}
			rs = dao.search(sql, sqlList);
			while (rs.next()) {
				String nbase = "";
				String a0100 = "";
				if(this.isPersonTask(p0800)){// 个人计划直接取nbse，a0100
					nbase = rs.getString("nbase");
					a0100 = rs.getString("a0100");
				} else if(!this.isPersonTask(p0800)){// 团队计划
					if(flag == 1 || flag == 2 ){//负责人、参与人
						nbase = rs.getString("nbase");
						a0100 = rs.getString("a0100");
					} else if(flag == 5){//发起人
						String org_id = rs.getString("org_id");
						if(!StringUtils.isEmpty(org_id)){
							String leader = this.getFirstDeptLeaders(org_id);
							if(!StringUtils.isEmpty(leader)){
								nbase = leader.substring(0, 3);
								a0100 = leader.substring(3);
							}
						}
					}
				}
				list.add(nbase+a0100);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 通过nbase、a0100获取用户信息
	 * @param nbase
	 * @param a0100
	 * @return
	 * chent
	 */
	public HashMap<String, String> getInfoByNbsA0100(String nbase, String a0100) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String tableName = nbase + "A01";
			String sql = "select e0122,a0101,GUIDKEY from "+tableName+" where A0100=?";
			rs = dao.search(sql, Arrays.asList(new Object[] {a0100}));
			if (rs.next()) {
				String e0122 = rs.getString("e0122");
				String a0101 = rs.getString("a0101");
				String guidKey = rs.getString("GUIDKEY");
				map.put("e0122", e0122);
				map.put("a0101", a0101);
				map.put("guidkey", guidKey);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	
	/**
	 * 通过机构号获取唯一标识
	 * @param orgid 机构号
	 * @return
	 * chent
	 */
	public HashMap<String, String> getInfoByOrgId(String orgid) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try{
			String sql = "select codeitemdesc,GUIDKEY from organization where codeitemid=?";
			rs = dao.search(sql, Arrays.asList(new Object[] {orgid}));
			if (rs.next()) {
				String codeitemdesc = rs.getString("codeitemdesc");
				String guidKey = rs.getString("GUIDKEY");
				map.put("codeitemdesc", codeitemdesc);
				map.put("guidkey", guidKey);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rs);
		}
		
		return map;
	}
	/**
	 * 协办任务发送邮件和微信
	 * @param pending_title 标题
	 * @param bodyText 邮件正文
	 * @param hrefDesc 按鈕文字
	 * @param pending_url 邮件点开链接
	 * @param receiver 接收者
	 */
	public void sendEmaiAndWeiXin(String title, String bodyText, String hrefDesc, String url, String receiver){
		
		LazyDynaBean email = new LazyDynaBean();
		email.set("objectId", receiver);
		email.set("subject", title);
		email.set("bodyText", bodyText);
		email.set("href", url);
		email.set("hrefDesc", hrefDesc);
		new AsyncEmailBo(this.conn, this.userView).send(email);
		
		// 发送微信
		this.sendWeixinMessageFromEmail(email);
		
	}
	
	/**
	 * 根据邮件模板类型获取邮件正文等信息
	 * @param emailMode 1:协作申请审批通知模板 2:协作申请已批通知模板 3:任务申请退回通知模板
	 * @return HashMap bodyText：正文 title：标题 hrefDesc：按钮描述
	 * chent
	 */
	public HashMap<String, String> getEmailTemplateInfo(int emailMode){
		HashMap<String, String> map = new HashMap();
		
		StringBuffer bodyText = new StringBuffer();// 正文
		StringBuffer title = new StringBuffer();// 标题
		StringBuffer hrefDesc = new StringBuffer();// 按钮描述
		
		if(emailMode == 1){//协作申请审批通知模板
			title.append("协作申请审批提醒");
			
			hrefDesc.append("去查看任务");
			
			bodyText.append("{mark1}，您好！<br /><br />");
			bodyText.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			bodyText.append("{mark2}提交了 {mark3} 的任务协作申请。请审批。<br /><br />");
			bodyText.append(PubFunc.getStringDate("yyyy年MM月dd日")).append("<br />");
			
		} else if(emailMode == 2){//协作申请已批通知发起人模板
			title.append("协作申请审批提醒");
			
			hrefDesc.append("去查看任务");
			
			bodyText.append("{mark1}，您好！<br /><br />");
			bodyText.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			bodyText.append("{mark2}已经批准了你的 {mark3} 任务协作申请。<br /><br />");
			bodyText.append(PubFunc.getStringDate("yyyy年MM月dd日")).append("<br />");
			
		} else if(emailMode == 3){//任务申请退回通知模板
			title.append("协作申请退回提醒");
			
			hrefDesc.append("去查看任务");
			
			bodyText.append("{mark1}，您好！<br /><br />");
			bodyText.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			bodyText.append("{mark2}已经退回了你的 {mark3} 任务协作申请。<br /><br />");
			bodyText.append(PubFunc.getStringDate("yyyy年MM月dd日")).append("<br />");
		} else if(emailMode == 4){//协作任务已批通知协作人模板
			title.append("协作申请审批提醒");
			
			hrefDesc.append("去查看任务");
			
			bodyText.append("{mark1}，您好！<br /><br />");
			bodyText.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			bodyText.append("{mark2}已经批准了指派给你的 {mark3} 任务协作申请。<br /><br />");
			bodyText.append(PubFunc.getStringDate("yyyy年MM月dd日")).append("<br />");
		}
		
		map.put("bodyText", bodyText.toString());
		map.put("title", title.toString());
		map.put("hrefDesc", hrefDesc.toString());
		
		return map;
	}
	/**
	 * 是否启用协办任务
	 * @return
	 */
	public boolean isOpenCooperationTask(){
		boolean isOpen = false;
		
		try {
			//获取OKR配置参数 【协作任务处理模式】如果是2：协作任务申请，则继续找计划下的任务是否有协办任务，如果有，则走协办逻辑
			WorkPlanConfigBo workPlanConfigBo = new WorkPlanConfigBo(this.conn);
			String cooperative_task = (String)workPlanConfigBo.getXmlData().get("cooperative_task");//配置参数：协作任务处理模式
			if("2".equals(cooperative_task)){//【协作任务处理模式】2：协作任务申请
				isOpen = true;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return isOpen;
	}
	/**
	 * 通过任务号，判断任务是否为个人任务
	 * @param p0800
	 * @return
	 */
	public boolean isPersonTask(int p0800){
		boolean isPersonTask = true;
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String sql = "select p0723 from p07 where p0700 in (select p0700 from p08 where p0800=?)";
			ArrayList<Integer> list = new ArrayList<Integer>();
			list.add(p0800);
			rs = dao.search(sql, list);
			while(rs.next()){
				int p0723 = rs.getInt("p0723");
				if(p0723 == 2){//团队任务
					isPersonTask = false;
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return isPersonTask;
	}
	/**
	 * 更新计划唯一标识
	 * @param p0700
	 * chent
	 */
	public void updatePlanGuidKey(String p0700){
		
		ContentDAO dao = new ContentDAO(this.conn);
    	try {
    		RecordVo p07vo = new RecordVo("p07");
    		p07vo.setInt("p0700", Integer.parseInt(p0700));
			p07vo = dao.findByPrimaryKey(p07vo);
			
			String guidkey = "";
			String nbase = "";
			String a0100 = "";
			int p0723 = p07vo.getInt("p0723");
			if(p0723 == 1){//人员计划
				nbase = p07vo.getString("nbase");
				a0100 = p07vo.getString("a0100");
			} else if(p0723 == 2){//部门计划
				String org_id = p07vo.getString("p0707");
				if(!StringUtils.isEmpty(org_id)){
					String leader = this.getFirstDeptLeaders(org_id);
					if(!StringUtils.isEmpty(leader)){
						nbase = leader.substring(0, 3);
						a0100 = leader.substring(3);
					}
				}
			}
			guidkey = this.getInfoByNbsA0100(nbase, a0100).get("guidkey");
			p07vo.setString("guidkey",guidkey);//唯一标识
			dao.updateValueObject(p07vo);
			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
}
