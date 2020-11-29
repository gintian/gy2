package com.hjsj.hrms.utils;

import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.valueobject.UserView;

import java.util.ArrayList;

/**
 * 标准版与专业版功能判断
 * <p>Title: FuncVersion </p>
 * <p>Description: 标准版与专业版功能判断类</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2016-6-6 下午04:28:26</p>
 * @author zhaoxj
 * @version 1.0
 */
public class FuncVersion {
    /**
     * 专业版
     */
    public final static int VER_PRO = 1;
    
    /**
     * 标准版
     */
    public final static int VER_STD = 0;
    
    private EncryptLockClient lock;
    private UserView userView;
    private int versionType = -1;
    
    /**
     * 专业版功能权限号列表
     */
    private ArrayList<String> proVerFuncs;
    /**
     * 专业版独有的功能模块名称，用户标准版版本显示授权模块时需过滤的模块
     */
    private ArrayList<String> proVerModuleNames;
    
    private FuncVersion() {
        
    }
    
    public FuncVersion(UserView userView) {
        this.userView = userView;
        this.lock = null;
        if (this.userView != null)
            this.versionType = userView.getVersion_flag();
        init();
    }
    
    public FuncVersion(UserView userView, int versionType) {
        this.userView = userView;
        this.versionType = versionType;
        this.lock = null;
        init();
    }
    
    private void init() {
    	proVerModuleNames = new ArrayList<String>();
    	proVerModuleNames.add("薪资预算");
    	proVerModuleNames.add("计件工资");
    	
        proVerFuncs = new ArrayList<String>();
        
        /*组织机构 标准版不包括如下功能：*/
        //编制控制
        proVerFuncs.add("23064");
        //编制管理大菜单控制数据联动没了。数据联动标准版还是要的，注释此处 guodd 2018-09-30
        //proVerFuncs.add("2312");
        proVerFuncs.add("23062");
        proVerFuncs.add("0501021");
        proVerFuncs.add("050103");
        //历史时点 zxj 20170828 历史时点功能放开
//        proVerFuncs.add("230500");
//        proVerFuncs.add("230501");
//        proVerFuncs.add("230511");
//        proVerFuncs.add("23052");
        //机构图-历史时点查询
//        proVerFuncs.add("0501021");
        //组织机构调整（模板）
        proVerFuncs.add("23067");
        proVerFuncs.add("231102");
        
        /*人员信息 标准版不包括如下功能：*/
        //历史时点
//        proVerFuncs.add("26012");
        
        /*薪资管理 标准版不包括如下功能：*/
        //薪资预算
        proVerFuncs.add("32420");
        //薪资总额
        proVerFuncs.add("32405");
        proVerFuncs.add("32411");
        //薪资分析图
        proVerFuncs.add("324072");
        //发放进展表
        proVerFuncs.add("324073");
        
        /*保险管理标准版与专业版功能相同*/
        
        /*合同管理标准版与专业版功能相同*/
        
        /*报表管理标准版与专业版功能相同*/
        
        /*表格工具标准版与专业版功能相同*/
        
        /*人事异动标准版与专业版功能相同*/

        /*绩效管理--目标考核不区分专业还是标准 haosl 2020年1月8日：*/
        //proVerFuncs.add("0607");
        /*绩效管理 标准版不包括如下功能：*/
        /*proVerFuncs.add("0608");*/
        
        
        /*20180306 考勤管理不区分专业版、标准版*/
        //业务历史数据
        //proVerFuncs.add("27053");
        //proVerFuncs.add("0C347");

        //刷卡历史数据功能
        //proVerFuncs.add("27054");
        //proVerFuncs.add("0C40");
        
        //考勤卡发卡功能
        //proVerFuncs.add("27082");
               
        //假期计划制定
        //proVerFuncs.add("27044");
        //proVerFuncs.add("0C36");
        //proVerFuncs.add("0B11");
        
        /*招聘管理(新版) 不区分专业版、标准版*/
        
        /*培训管理 标准版不提供培训 ,   20180201 dengcan 培训模块在标准版不外挂在线学习和考试（销售控制），产品上不作限制 */
        //培训管理
      //  proVerFuncs.add("323");
        //培训自助
      //  proVerFuncs.add("09");
    }
    
    public boolean haveVersionFunc(String funcid) {
        //审批流程、流程设计器，60及以上提供
        if (this.userView != null && ",9A51,9A52,".contains(","+funcid+",")) {
            return this.userView.getVersion()>=60;
        }
        
        for (int i=0; i<proVerFuncs.size(); i++) {
            String proFuncId = (String)proVerFuncs.get(i).toUpperCase();
            //proFuncId为专业版功能（例外：汇报关系图的权限号异常，其在历史机构23052之下）
            if (!funcid.toUpperCase().startsWith(proFuncId)
                 || funcid.toUpperCase().startsWith("230521"))
                continue;
            
            if (this.lock != null)
                return this.lock.getVersion_flag() == VER_PRO;
            else
                return this.versionType == VER_PRO;
        }
        
        return true;
    }
    
    public static boolean haveVersionFunc(UserView uv,String funcid,int versionType) {
        FuncVersion fVersion = new FuncVersion(uv, versionType);
        return fVersion.haveVersionFunc(funcid);        
    }
    
    /**
     * 当前系统是否为专业版
     * @Title: isProVer   
     * @Description:当前系统是否为专业版    
     * @return true:专业版 false:不是专业版（标准版）
     */
    public boolean isProVer() {
        if (this.lock != null)
            return this.lock.getVersion_flag() == VER_PRO;
        else
            return this.versionType == VER_PRO; 
    }

    /**
     * 是否有加班转调休功能
     * @Title: haveKqLeaveTypeUsedOverTimeFunc   
     * @Description:    
     * @return
     */
    public boolean haveKqLeaveTypeUsedOverTimeFunc() {
        return true; //20180306 调休假功能放开，考勤所有功能都不再区分标准版与专业版 isProVer();      
    }
    
    /**
     * 根据锁版本生成模块明细信息 guodd 2019-05-18 bug【39990】
     * @param moduleNames
     * @return
     */
    public String getRealModuleNames(String moduleNames) {
    	if(isProVer())
    		return moduleNames;
    	
    	moduleNames = moduleNames+",";
    	for(int i=0;i<proVerModuleNames.size();i++) {
    		
    		String moduleName = (String)proVerModuleNames.get(i);
    		
    		
    		int index = moduleNames.indexOf(moduleName+"(");
    		if(index==-1)
    			continue;
    		
    		String[] partStr = moduleNames.split(moduleName+"\\(");
    		if(partStr[0].length()<1) {
    			moduleNames = partStr[1].substring(partStr[1].indexOf(",")+1);
    			continue;
    		}
    		
    		moduleNames = partStr[0] + partStr[1].substring(partStr[1].indexOf(",")+1);
    	}
    	
    	moduleNames = moduleNames.substring(0, moduleNames.length()-1);
    	
    	return moduleNames;
    }
    
}
