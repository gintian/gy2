package com.hjsj.hrms.module.template.utils.javabean;

import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;

import java.util.HashMap;

/**
 * <p>Title:TemplateFrontProperty.java</p>
 * <p>Description>:解析及存储前台传递参数的javabean，主要是公用参数  </p>
 * 解析交易类的this.getFormHM()。
 * 各个功能点用到的私有属性自己从交易类的this.getFormHM()获取
 * 没有传递过来的参数 不能通过get方法获取，后台打印出错，供开发人员及时处理。
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-2-20 下午01:43:23</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class TemplateFrontProperty {
    
    /**平台标识  便于以后扩展使用
    1： 1：bs平台  2：移动平台  
   */  
    private String sysType="";  
      
	/**调用的模块标识、返回模块标识
	 * 0：返回待办任务界面
	 * 1：返回已办任务界面
	 * 2：返回我的申请界面
	 * 3：返回任务监控界面
	 * 4:返回业务申请界面
	 * 。5-10。暂时保留
	 * 11.首页待办
	 * 12、首页待办列表
	 * 13、关闭（来自第三方系统或邮件），提交后自动关闭
	 * 14、无关闭、返回按钮，提交后不跳转
	*/
    private String returnFlag="";    
    
    /** 区分报审、报备、加签 
     * 1：报审 2：加签  3 报备 
    */
    private String approveFlag="";      
    
    /** 模板号
     * 
    */
    private String tabId="";    
    
    /** 模板使用的模块
	 * 1、人事异动
	 * 2、薪资管理 
	 * 3、劳动合同
	 * 4、保险管理
	 * 5、出国管理
	 * 6、资格评审
	 * 7、机构管理
	 * 8、岗位管理 
	 * 9、业务申请（自助）
	 * 10、考勤管理
	 * 11、职称评审 
	*/	
    private String moduleId="";       
    
    /** 信息群类型
     * 1：人员 2： 单位 3： 岗位 后台根据模板类型判断
    */  
    private String inforType="";   
    
    /** 任务号（批量时为多个任务号 以逗号分隔）需加密 
     * 0：起草；加密的任务号（非0）：审批中，批量时以逗号分隔，分隔后再统一加密，后台读取时需先解密。
    */ 
    private String taskId="";    
    
    /** 显示方式  
     * list：列表，card 卡片。
    */ 
    private String viewType="";   
    /** 扩暂参数 （可选）
     * 以`分隔object_id="usr00000000001'";  
    * object_id //控制只显示的某人的记录 职称评审用 加密
    */ 
    private String otherParam="";   


    /**构造函数
     * @param hm 将交易类的this.getFormHM()
     */
    public TemplateFrontProperty(HashMap hm){
        parseFrontParam(hm);
    }
    
    /**   
     * @Title: parseFrontParam   
     * @Description:解析前台参数    
     * @param @param hm 
     * @return void 
     * @throws   
    */
    private void parseFrontParam(HashMap hm){
        if ((String) hm.get("sys_type")!=null ){
            this.setSysType(TemplateFuncBo.getValueFromMap(hm,"sys_type"));
        };        
        if ((String) hm.get("module_id")!=null ){
        	this.setModuleId(TemplateFuncBo.getValueFromMap(hm,"module_id"));
        };
        if ((String) hm.get("return_flag")!=null ){
        	this.setReturnFlag(TemplateFuncBo.getValueFromMap(hm,"return_flag"));
        };

        if ((String) hm.get("approve_flag")!=null ){
            this.setApproveFlag(TemplateFuncBo.getValueFromMap(hm,"approve_flag"));
        };
        if ((String) hm.get("tab_id")!=null ){
            this.setTabId(TemplateFuncBo.getValueFromMap(hm,"tab_id"));
        };         
        
        if ((String) hm.get("task_id")!=null ){//解密
            this.setTaskId(TemplateFuncBo.getDecValueFromMap(hm,"task_id"));
        };
        
        if ((String) hm.get("view_type")!=null ){
            this.setViewType(TemplateFuncBo.getValueFromMap(hm,"view_type"));
        };
        if ((String) hm.get("infor_type")!=null ){
        	this.setInforType(TemplateFuncBo.getValueFromMap(hm,"infor_type"));
        };
        if ((String) hm.get("other_param")!=null ){
            this.setOtherParam(TemplateFuncBo.getValueFromMap(hm,"other_param"));
        };
        
    }
    

    /**   
     * @Title: checkParam   
     * @Description:检查参数是否有值    
     * @param @param paramName
     * @param @param paramValue
     * @param @return 
     * @return String 
     * @throws   
    */
    private void checkParam(String paramName,String paramValue) {
        if ("".equals(paramValue)){
            printErrorParamInfo(paramName);;
        }
    }
    /**   
     * @Title: printErrorParamInfo   
     * @Description: 后台打印参数出错信息。   
     * @param @param param
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    private void printErrorParamInfo(String param) {    
            System.out.println("参数："+param+"存在没传递的情况，请开发人员检查");
      
    }
    
    public String getSysType() {
        checkParam("sysType",sysType);
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }


    public String getReturnFlag() {
        checkParam("returnFlag",returnFlag);
        return returnFlag;
    }

    public void setReturnFlag(String returnFlag) {
        this.returnFlag = returnFlag;
    }

    public String getApproveFlag() {
        checkParam("approveFlag",approveFlag);
        return approveFlag;
    }

    public void setApproveFlag(String bsFlag) {
        this.approveFlag = bsFlag;
    }

    public String getTabId() {
        checkParam("tabId",tabId);
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }


    public String getInforType() {
        checkParam("inforType",inforType);
        return inforType;
    }

    public void setInforType(String inforType) {
        this.inforType = inforType;
    }

    public String getTaskId() {
        checkParam("taskId",taskId);
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
    
    public String getModuleId() {
        checkParam("moduleId",moduleId);
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		
		this.moduleId = moduleId;
	}

	/**   
     * @Title: isCardView   
     * @Description: 是否是卡片模式   
     * @param @return 
     * @return boolean 
     * @throws   
    */
    public boolean isCardView() {
        return "card".equals(this.getViewType());
    }
    /**   
     * @Title: isListView   
     * @Description:是否是列表模式    
     * @param @return 
     * @return boolean 
     * @throws   
    */
    public boolean isListView() {
        return "list".equals(this.getViewType());
    }
    
    /**   
     * @Title: isListView   
     * @Description:是否是批量审批 
     * @param @return 
     * @return boolean 
     * @throws   
    */
    public boolean isBatchApprove() {
        return this.taskId.contains(",");
    }
    
    
    /**   
     * @Title: isSelfApply   
     * @Description:是否业务申请 
     * @param @return 
     * @return boolean 
     * @throws   
    */
    public boolean isSelfApply() {
        return  "9".equals(this.getModuleId())&& "0".equals(this.getTaskId());
    }

    public String getOtherParam() {
        return otherParam;
    }

    public void setOtherParam(String otherParam) {
        this.otherParam = otherParam;
        if (this.otherParam==null)
            this.otherParam="";
    }
    
    /**   
     * @Title: getOtherParam   
     * @Description:获取扩展参数中参数   即从otherParam中获取。
     * @param @param paramName 参数名
     * @param @return 
     * @return String 
     * @throws   
    */
    public String getOtherParam(String paramName) {        
        String value="";
        try {
        	if (this.otherParam.length()>0){
                String [] strList =this.otherParam.split("`");
                for (int i=0;i<strList.length;i++){
                    String [] strParamList =strList[i].split("=");
                    if (strParamList.length==2){
                        String param = strParamList[0];
                        String paramValue = strParamList[1];
                        if (paramName.equals(param) && paramValue.length()>0){
                            if("object_id".equals(paramName)){
                            	value= PubFunc.decryption(paramValue);
                            }else if("search_sql".equals(paramName)){
                            	value= PubFunc.keyWord_reback(SafeCode.decode(paramValue));
                            }else{
                            	value= paramValue;
                            }
                        }
                    }
                  }
                }
        }catch (Exception e){
            e.printStackTrace();
        }
        return value;
    }  
    
    /**   
     * @Title: getInitValueParam   
     * @Description:获取扩展初始参数中参数   即从otherParam中获取初始以特殊标识开头的参数（如：i_）
     * @param @param initvalueOther 其他参数串（i_AB101_2@KEY@01@INIT@i_AB109_2@KEY@2017.10.24 00:00@INIT@i_AB110_2@KEY@2017.10.24 23:59@INIT@）
     * @param @param paramflag  特殊标识开头的参数（如：iniv_）
     * @param @return 
     * @return map 
     * @throws   
    */
    public HashMap getInitValueParam(String initValueOther, String paramflag) {        
        HashMap map = new HashMap();
        try {
        	if (initValueOther.length()>0){ 
                String [] strList =initValueOther.split("@INIT@");
                for (int i=0;i<strList.length;i++){
                    String [] strParamList =strList[i].split("@KEY@");
                    String paramValue = "";
                    // 指标对应参数为空时，长度为1，
                    if (strParamList.length==2){
                    	paramValue = strParamList[1];
                    }
                    String param = strParamList[0];
                    // paramValue可能为空
//                  if (param.contains(paramflag) && paramValue.length()>0){
                    if (param.contains(paramflag)){
                    	// 替换掉标识i_ 
                    	param = param.replace(paramflag, "");
                    	map.put(param, paramValue);
                    }
                  }
                }
        }catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }  
    
}
