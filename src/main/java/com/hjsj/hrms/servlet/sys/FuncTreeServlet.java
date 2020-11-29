/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.businessobject.sys.FunctionTree;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.businessobject.sys.bos.menu.MenuMainBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.sys.CreateCodeXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:功能树列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:4:17:07 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class FuncTreeServlet extends HttpServlet {
    private EncryptLockClient lock;
    private FunctionTree functionTree;
    //private String bflag="hl";
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

   	
	 
    /**
     * 当前对象是否有
     * @param func_str ，用户已授权的功能串列如 ,2020,30,
     * @param func_id
     * @return
     */
    private boolean haveTheFunc(String func_str,String func_id)
    {
    	if(func_str.indexOf(","+func_id+",")==-1)
    		return false;
    	else
    		return true;
    }
    
    /**
     * 标准版、专业版功能区分
     * @param funcid
     * @param ver_s =1专业版 =0标准版
     * @return
     */
    private boolean haveVersionFunc(UserView userView,String funcid,int ver_s)
    {
    	return PubFunc.haveVersionFunc(userView, funcid, ver_s);
    }
	/**
	 * 从功能授权配置的文件取得所有的功能编码
	 * @param userView
	 * @param conn
	 * @param func_str
	 * @param curr_id 当前需要展开的节点id
	 * @param nodeName 节点名称
	 * @param moduleMenuId 当前节点所属模块的id
	 * @return
	 * @throws GeneralException
	 */
    private String searchFunctionXmlHtml(UserView userView,Connection conn,String func_str,String curr_id,String nodeName,String moduleMenuId)throws GeneralException
    {
    	InputStream in = null;
        StringBuffer buf=new StringBuffer();
        try
        {
        	in=this.getClass().getResourceAsStream("/com/hjsj/hrms/constant/function.xml");
	        Document doc = PubFunc.generateDom(in);
	        Element root = doc.getRootElement();
	        List list=null;
	        /*menu.xml document对象*/
	        Document menuDoc = null;
	        //需要加载的节点是否是模块根菜单节点，如组织机构节点、员工管理节点等  guodd 2019-07-08
	        boolean isModuleNode = false;
	        
	        if("root".equalsIgnoreCase(curr_id))
	        {
	        	list = root.getChildren("function");
	        }
	        else
	        {
	        	//当被展开是 业务平台节点 和 自助平台 节点时，下面的的节点就是模块节点  guodd 2019-07-08
	        	if("2".equals(curr_id) || "0".equals(curr_id)) {
	        		isModuleNode = true;
	        	}
	        		
	        	//因为权限号有重复的情况，通过name进行二次匹配 guodd 2018-12-12
	        	String nameCond = nodeName!=null && nodeName.length()>0?" and @name=\""+nodeName+"\"":"";
	        	String xpath = "//function[@id=\"" + curr_id + "\" "+nameCond+"]";
	        	XPath xpath_ = XPath.newInstance(xpath);
	        	List eleList=xpath_.selectNodes(doc);
	        	Element ele=null;
	        	//根据锁版本判断需要展示的权限 展示出具有符合对应版本的节点，如果没有符合条件的则取出第一个没有设置ctrl_ver属性的节点
	        	//zhanghua 2017-6-2
	        	for(int i=0;i<eleList.size();i++){
	        		Element eleTemp = (Element) eleList.get(i);
	        		String Ver=eleTemp.getAttributeValue("ctrl_ver");
	        		if(ele==null&&StringUtils.isBlank(Ver))
	        			ele=eleTemp;
	        		if(StringUtils.isNotBlank(Ver)&&Ver.indexOf(String.valueOf(userView.getVersion()))>=0){
	        			ele=eleTemp;
	        			break;
	        		}
	        	}
//	        	if(ele==null){
//	        		ele = (Element)  XPath.newInstance("//function[@id=\"" + curr_id + "\"]").selectSingleNode(doc);
//	        	}
	        	list = ele.getChildren("function");
	        }
	        /**版本之间的差异控制，市场考滤*/
	        VersionControl ver_ctrl=new VersionControl();
	        ver_ctrl.setVer(this.lock.getVersion());
	        StringBuffer tmp=new StringBuffer();
	        /**如果仅购买5.0版本的自助平台,系统管理模块处理 cmq changed at 20110620*/
	        boolean b50self=false;
	        if(("hl".equalsIgnoreCase(userView.getBosflag())|| "hcm".equalsIgnoreCase(userView.getBosflag()))&&(!this.lock.isHaveBM(11)))
	        {
	        	b50self=true;
	        }
	        //end.
	        for (int i = 0; i < list.size(); i++)
	        {
	          Element node = (Element) list.get(i);
	          String func_id=node.getAttributeValue("id");
	         /**
	           * 270是通版考勤休假
	           * 271是薪资上报版考勤管理
	           * 272是高校、医院版考勤管理
	           * 0B是自助里的考勤自助	
	           * 0C是自助里的部门考勤	
	           * 	以上权限节点显示和menu中配置是否显示同步 guodd 2019-07-17
	           */
	          if(
	        	 "270".equals(func_id) || 
	             "271".equals(func_id) || 
	             "272".equals(func_id) || 
	             "0B".equalsIgnoreCase(func_id)  || 
	             "0C".equalsIgnoreCase(func_id)
	            ) {
	        	  MenuMainBo bo = new MenuMainBo();
	        	  if(menuDoc==null)
	        		  menuDoc = bo.getDocument();
	        	  XPath menuXPath = XPath.newInstance("//menu[@func_id=\""+func_id+"\"]");
	        	  Element menuEle = (Element) menuXPath.selectSingleNode(menuDoc);
	        	  String show = menuEle.getAttributeValue("menuhide");
	        	  if("false".equalsIgnoreCase(show))
	        		  continue;
	          }
	          
	          if("350".equals(func_id)){
	        	  if(!this.lock.isHaveBM(31))
	        		  continue;
	        	  /*if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
						continue;*/
	          }
	          
	          String VersionPriv=node.getAttributeValue("ctrl_ver");
	          //展示出没有设置版本属性或者符合当前锁版本的子节点 zhanghua 2017-6-2
	          if(StringUtils.isNotBlank(VersionPriv)&&(VersionPriv.indexOf(String.valueOf(userView.getVersion()))==-1)){
	        	  continue;
	          }
	          
	          //zxj changed at 20130510: 在线学习外挂控制
              if((    "32306".equals(func_id)    //课程体系
                   || "32364".equals(func_id)    //学习情况分析
                   || "3237305".equals(func_id)  //流媒体服务器设置
                   || "3237307".equals(func_id)  //学习进度提醒设置
                   || "090903".equals(func_id)   //自助：培训课程
                   || "32374".equals(func_id)    //培训新闻
                   || "3239".equals(func_id)     //积分管理
                   || "32391".equals(func_id)    //积分管理 兑换奖品
                   || "32392".equals(func_id)    //积分管理 兑换记录
                   || "090905".equals(func_id)   //自助：我的课程
                   || "0913".equals(func_id)     //自助：上传课程   
                   || "0911".equals(func_id)     //自助：我的积分
                   || "0912".equals(func_id)     //自助：培训场所申请
                   || "0910".equals(func_id)     //自助：积分兑换
                   || "091001".equals(func_id)   //自助：积分兑换 兑换奖品
                   || "091002".equals(func_id)   //自助：积分兑换 兑换记录
                   || "090901".equals(func_id)   //自助：知识中心
                  ) 
                  && !this.lock.isHaveBM(39))
              {
                 continue; 
              }
              
              //zxj changed at 20130510: 在线考试外挂
              if((   "3238".equals(func_id)   //培训考试
                  || "090907".equals(func_id) //自助：我的考试
                  )
                  && !this.lock.isHaveBM(40))
              {
                  continue;
              }
              
              //zxj changed at 20140210 60及以上版本 规章制度外挂（编号同文档管理 12）
              if (  ("1107".equals(func_id)   //自助: 规章制度
                 || "070701".equals(func_id)  //业务: 规章制度维护
                 || "081006".equals(func_id)  //资源分配：规章制度
                 || "3003406".equals(func_id) //资源分配：规章制度
                 )
                  && !this.lock.isHaveBM(12)
                  && userView.getVersion()>=60) 
              {
                  continue;
              }
              
              //wangrd 20140217 薪资预算
              if (("32420".equals(func_id)              
                  && !this.lock.isHaveBM(42))) 
              {
                  continue;
              }
              //wangrd 20140217 计件薪资
              if (("32421".equals(func_id) || "3242112".equals(func_id))
                       && !this.lock.isHaveBM(43))         
               {
                   continue;
               }
              
              
              // zxj 20180921 补充几个模块的锁权限控制
              // 招聘
              if (("311".equals(func_id) && !this.lock.isHaveBM(7))) 
              {
                  continue;
              }
              
              // 职称评审
              if (("380".equals(func_id) && !this.lock.isHaveBM(45))) 
              {
                  continue;
              }
              
              // 问卷调查
              if (("40".equals(func_id) && !this.lock.isHaveBM(46))) 
              {
                  continue;
              }
              
              // 关键目标（OKR）
              if (("0KR".equals(func_id) && !this.lock.isHaveBM(44))) 
              {
                  continue;
              }
              
              //分布同步
              if ("420".equals(func_id) && !this.lock.isHaveBM(53) && !this.lock.isHaveBM(54)) 
              {
                  continue;
              }
              //分布同步 菜单按 服务端和客户端分别控制。 服务端菜单：
              if (("4201".equals(func_id) || "4202".equals(func_id) || "4204".equals(func_id)) && !this.lock.isHaveBM(53)) 
              {
                  continue;
              }
              //客户端菜单
              if ("4203".equals(func_id)&& !this.lock.isHaveBM(54)) 
              {
                  continue;
              }
              

	          /*if("25012".equals(func_id)){
		          if(!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
						continue;
	          }*/
//	         if(func_id.equalsIgnoreCase("330"))
//	        	 System.out.println("func_id="+func_id);

	          /**HJ-eHR5.0平台界面,不显示参数设置、系统管理以及小工具条*/
	          if(("hl".equalsIgnoreCase(userView.getBosflag())|| "hcm".equalsIgnoreCase(userView.getBosflag()))&&("07".equalsIgnoreCase(func_id)|| "08".equalsIgnoreCase(func_id)|| "00".equalsIgnoreCase(func_id)))
	          {
	        	  continue;	        	  
	          }
	          /**工具箱功能、库结构只在5.0之后有*/
	          if(",9A0,3007,".indexOf(","+func_id+",")!=-1&&!("hl".equalsIgnoreCase(userView.getBosflag())|| "hcm".equalsIgnoreCase(userView.getBosflag()))){
	        	  continue;	   
	          }
	          /**流程定义非图形化功能在5.0界面就没有了，只有工具箱中图形化定义方式*/
	          if("hl".equalsIgnoreCase(userView.getBosflag())|| "hcm".equalsIgnoreCase(userView.getBosflag())){
	        	  if(",3214,3300102,331012,3202,3240103,3250103,3704,3714,3724,3734,".indexOf(","+func_id+",")!=-1){
	        		  continue;
	        	  }
	          }
	          if(!this.functionTree.isMayOut(func_id,userView.getUserName()))
	          {
	        	  /**单独判断业务平台0和系统管理300节点、小工具条301*/
	        	  if(!(("2".equalsIgnoreCase(func_id)|| "300".equalsIgnoreCase(func_id)|| "301".equalsIgnoreCase(func_id))&&b50self))
	        		  continue;
	          }
	          /**版本控制*/
	          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
	        	  continue;
	          /**机构、岗位调整模板设置功能控制按version*/
	          if(!ver_ctrl.searchFunctionId("23067")&&("9907304".equals(node.getAttributeValue("id"))||"3003424".equals(node.getAttributeValue("id"))))
	        	  continue;
	          if(!ver_ctrl.searchFunctionId("231102")&&("9907305".equals(node.getAttributeValue("id"))||"3003425".equals(node.getAttributeValue("id"))))
	        	  continue;
	          //专业版、普通版控制
	          if(!haveVersionFunc(userView,node.getAttributeValue("id"), lock.getVersion_flag()))
	        	  continue;
	          /**
	           * 支持分布式授权机制,如果登录用户关联了三员分离角色,则另行处理
	           */
	          if(userView.isBThreeUser())
	          {
	        	  if(!userView.hasThreeUserTheFunction(node.getAttributeValue("id")))
	        		  continue;	        	  
	          }
	          else
	          {
	        	  if(!userView.hasTheFunction(node.getAttributeValue("id")))
	        		  continue;
	          }
	          //控制记录录入生成随机账号
	          if(!"true".equalsIgnoreCase(SystemConfig.getPropertyValue("create_random_info"))){
	        	  if("26060A".equalsIgnoreCase(node.getAttributeValue("id")))
	        		  continue;
	          }
	          // 6.0平台以上就不需要友情链接啦，也没有地方显示 xuj 2014-02-27
	          if("hl".equalsIgnoreCase(userView.getBosflag())|| "hcm".equalsIgnoreCase(userView.getBosflag())){
	        	  if("13014".equalsIgnoreCase(node.getAttributeValue("id")))
	        		  continue;
	          }
	          
	          /*小工具条控制，由于hcm工具条变动和先前版本区别比较大，功能授权处需根据不同平台做不同显示控制 xuj start*/
	          if("hcm".equals(userView.getBosflag())){
	        	  if("3017".equals(func_id)){//信任站点
	        		  node.setAttribute("name", ResourceFactory.getProperty("toolbar.nav.setie"));
	        	  }
	        	  if("3013".equals(func_id)){//注销
	        		  continue;
	        	  }
	          }else{
	        	  if("3018".equals(func_id)){//头像设置
	        		  continue;
	        	  }
	          }
	          /*小工具条控制，由于hcm工具条变动和先前版本区别比较大，功能授权处需根据不同平台做不同显示控制 xuj end*/
	          tmp.append("{id:'");
	          tmp.append(node.getAttributeValue("id"));
	          tmp.append("',text:'");
	          tmp.append(node.getAttributeValue("name"));
	          tmp.append("'");
	          
	          //如果被展开节点上存在所属模块id，直接添加到本节点上 guodd 2019-07-08
	          if(moduleMenuId!=null) {
	        	  tmp.append(",moduleMenuId:'").append(moduleMenuId).append("'");
	          }else if(isModuleNode) {//如果被展开节点不存在，且当前节点时模块节点时，添加模块id属性 guodd 2019-07-08
	        	  tmp.append(",moduleMenuId:'").append(node.getAttributeValue("id")).append("'");
	          }
	          if(haveTheFunc(func_str,node.getAttributeValue("id")))
	          {
		          tmp.append(",checked:true");	        	  
	          }
	          else
	          {
	        	  tmp.append(",checked:false");
	          }	          
	          /*递归全部加载,功能太多速度很慢,改成异步加载
	          String childjson=searchSubFunctionXmlHtml(node,userView,conn,func_str); 
	          if(childjson.length()>0)
	          {
	        	  tmp.append(",children:");
	        	  tmp.append(childjson);
	          }
	          else
	          {
		          tmp.append(",leaf:true");
	          }
	          */
	          tmp.append(",leaf:false");
	          
	          tmp.append("}");
	          tmp.append(",");

	        } //for i loop end.
	        if(tmp.length()>0)
	        {
	        	tmp.setLength(tmp.length()-1);
	        	buf.append("[");
	        	buf.append(tmp.toString());
	        	buf.append("]");
	        }

        }
        catch(Exception ee)
        {
            throw GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            PubFunc.closeResource(in);
        }
        return buf.toString();

    }
	
    /**
     * 返回需要过滤的idMap
     * @param inputchinfor
     * @param approveflag
     * @return
     */
    private HashMap filtrateId(String inputchinfor,String approveflag)
    {
    	HashMap hashMap=new HashMap();
    	if("1".equals(inputchinfor)&& "1".equals(approveflag))
    	{
    		hashMap.put("01030115", "01030115");//整体报批
    		hashMap.put("03084", "03084");//整体批准
    		hashMap.put("03083", "03083");//整体驳回
    		hashMap.put("260633", "260633");//批准
    		hashMap.put("260634", "260634");//整体驳回
    	}else
    	{
    		hashMap.put("01030106", "01030106");//我的变动信息明细
    		hashMap.put("03085", "03085");//删除    		
    		hashMap.put("260635", "260635");//删除 
    	}
    	return hashMap;
    }
    
    /**
     * 过滤功能号 
     * @param id
     * @param map
     * @return
     */
    private boolean isFiltrate(String id,HashMap map)
    {
    	boolean isCorrect=false;
    	if(map!=null)
    	{
    		String filtrateid=(String)map.get(id);
    		if(filtrateid!=null&&filtrateid.length()>0)
    		{
    			isCorrect=true;
    		}	
    	}
    	return isCorrect;    
    }    
    /**
     * 子菜单功能
     * @param element
     * @param userView
     * @param conn
     * @param func_str
     * @return
     */
    private String searchSubFunctionXmlHtml(Element element,UserView userView,Connection conn,String func_str)
    {
        StringBuffer strPre=new StringBuffer();
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);//判断
        String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";//是不是直接入库
		String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
		approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";//是不是审批
		HashMap filtrateMap=filtrateId(inputchinfor,approveflag);

        List list = element.getChildren("function"); 
        VersionControl ver_ctrl=new VersionControl();
		StringBuffer buf=new StringBuffer();
		StringBuffer tmp=new StringBuffer();
        for(int i=0;i<list.size();i++)
        {
              Element node = (Element) list.get(i);
	          String func_id=node.getAttributeValue("id");
	          if(!this.functionTree.isMayOut(func_id,userView.getUserName()))
	        	  continue; 
	          /**版本控制*/
	          if(!ver_ctrl.searchFunctionId(node.getAttributeValue("id")))
	        	  continue;	          
	          /**
	           * 支持分布式授权机制
	           */
	          /**过滤**/
	          if(isFiltrate(func_id,filtrateMap))
	        	  continue;	
	          if(!userView.hasTheFunction(node.getAttributeValue("id")))
	              continue;	  
	          tmp.append("{id:'");
	          tmp.append(node.getAttributeValue("id"));
	          tmp.append("',text:'");
	          tmp.append(node.getAttributeValue("name"));
	          tmp.append("'");
	          if(haveTheFunc(func_str,node.getAttributeValue("id")))
	          {
		          tmp.append(",checked:true");	        	  
	          }
	          else
	          {
	        	  tmp.append(",checked:false");
	          }	          
	          String childjson=searchSubFunctionXmlHtml(node,userView,conn,func_str);     
	          if(childjson.length()>0)
	          {
		          //tmp.append(",leaf:false");
	        	  tmp.append(",children:");
	        	  tmp.append(childjson);
	          }
	          else
	          {
		          tmp.append(",leaf:true");
	          }
	          tmp.append("}");
	          tmp.append(",");	          
        }
        if(tmp.length()>0)
        {
        	tmp.setLength(tmp.length()-1);
        	buf.append("[");
        	buf.append(tmp.toString());
        	buf.append("]");
        }
        return buf.toString();
    }
    
    private String searchDbNameHtml(UserView userView,Connection conn,String db_str)throws GeneralException
    {
        StringBuffer strsql=new StringBuffer();;
        strsql.append("select dbid,dbname,pre from dbname order by dbid");
        ContentDAO dao=new ContentDAO(conn);
		StringBuffer buf=new StringBuffer();
		StringBuffer tmp=new StringBuffer();        
        RowSet rset=null;
	    try
	    {
	      rset = dao.search(strsql.toString());
	      while(rset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheDbName(rset.getString("pre")))
	              continue;
	          tmp.append("{id:'");
	          tmp.append(rset.getString("pre"));
	          tmp.append("',text:'");
	          tmp.append(rset.getString("dbname"));
	          tmp.append("'");
	          if(haveTheFunc(db_str,rset.getString("pre")))
	          {
		          tmp.append(",checked:true");	        	  
	          }
	          else
	          {
	        	  tmp.append(",checked:false");
	          }	          
	          tmp.append(",leaf:true");
	          tmp.append("}");
	          tmp.append(",");	          
	      }
          if(tmp.length()>0)
          {
        	tmp.setLength(tmp.length()-1);
        	buf.append("[");
        	buf.append(tmp.toString());
        	buf.append("]");
          }
          return buf.toString();    
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }    
    
    private String searchMediaHtml(UserView userView,Connection conn,String media_str)throws GeneralException
    {
        StringBuffer strsql=new StringBuffer();;
        strsql.append("select id,flag,sortname from mediasort order by id");
        ContentDAO dao=new ContentDAO(conn);
        RowSet rset=null;
	    try
	    {
	      rset = dao.search(strsql.toString());
		  StringBuffer buf=new StringBuffer();
		  StringBuffer tmp=new StringBuffer();
		  if(userView.hasTheMediaSet("K")){//xuj 2010-4-20 ，k代号已成为多媒体岗位说明书固定分类
	          tmp.append("{id:'K'");
	          tmp.append(",text:'");
	          tmp.append(ResourceFactory.getProperty("lable.pos.e01a1.manual"));
	          tmp.append("'");
	          if(haveTheFunc(media_str,"K"))
	          {
		          tmp.append(",checked:true");	        	  
	          }
	          else
	          {
	        	  tmp.append(",checked:false");
	          }	          
	          tmp.append(",leaf:true");
	          tmp.append("}");
	          tmp.append(",");
		  }
	      while(rset.next())
	      {
	          /**
	           * 支持分布式授权机制
	           */
	          if(!userView.hasTheMediaSet(rset.getString("flag")))
	              continue;
	          tmp.append("{id:'");
	          tmp.append(rset.getString("flag"));
	          tmp.append("',text:'");
	          tmp.append(rset.getString("sortname"));
	          tmp.append("'");
	          if(haveTheFunc(media_str,rset.getString("flag")))
	          {
		          tmp.append(",checked:true");	        	  
	          }
	          else
	          {
	        	  tmp.append(",checked:false");
	          }	          
	          tmp.append(",leaf:true");
	          tmp.append("}");
	          tmp.append(",");	
	      }
          if(tmp.length()>0)
          {
        	tmp.setLength(tmp.length()-1);
        	buf.append("[");
        	buf.append(tmp.toString());
        	buf.append("]");
          }
          return buf.toString();   	      
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
    }
    
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String type=(String)req.getParameter("type");
		String role_id=(String)req.getParameter("role_id");
		
		String flag=(String)req.getParameter("flag");
		String havechecked = (String)req.getParameter("havechecked");
		/**加载第一层*/
		lock=(EncryptLockClient)this.getServletContext().getAttribute("lock");
		this.functionTree=new FunctionTree(lock);
		Connection conn = null;	
		try
		{
			conn = (Connection) AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			StringBuffer buf=new StringBuffer();
	        UserView userView = (UserView) req.getSession().getAttribute(WebConstant.userView);	
	        //buf.append("[{id:'1',text:'aaa',leaf:true,checked:true},{id:'2',text:'bbbb',leaf:true,checked:false}]");
	        /**当前选中的角色或用户拥有的功能号列表*/
	        String tmp="";
		    if("funcpriv".equalsIgnoreCase(type))
		    {
		    	//getbflag(req);
		    	SysPrivBo sysPrivBo=new SysPrivBo(role_id,flag,conn,"functionpriv");
		    	String func_str=sysPrivBo.getFunc_str();
		    	String func_id=(String)req.getParameter("node");
		    	String nodeName = (String)req.getParameter("nodeName");
				if(nodeName!=null) {
					nodeName=PubFunc.keyWord_reback(nodeName);
					nodeName = URLDecoder.decode(nodeName,"utf-8");
				}
				//获取当前展开节点所属模块的id号 guodd 2019-07-08
				String moduleMenuId = (String)req.getParameter("moduleMenuId");
		    	tmp=searchFunctionXmlHtml(userView,conn,func_str,func_id,nodeName,moduleMenuId);
		    	//System.out.println("--->"+tmp);
		    }
		    if("dbpriv".equalsIgnoreCase(type))
		    {
		    	SysPrivBo sysPrivBo=new SysPrivBo(role_id,flag,conn,"dbpriv");
		    	String db_str=sysPrivBo.getDb_str();
		    	tmp=searchDbNameHtml(userView,conn,db_str);

		    }	
	        if("mediapriv".equals(type))
	        {
	           SysPrivBo sysPrivBo=new SysPrivBo(role_id,flag,conn,"mediapriv");
		       String media_str=sysPrivBo.getMedia_str();
		       tmp=searchMediaHtml(userView,conn,media_str);
            
	        }		    
		    if("managepriv".equalsIgnoreCase(type))
		    {
		    	SysPrivBo sysPrivBo=new SysPrivBo(role_id,flag,conn,"managepriv");	        	
		        String managed_str=sysPrivBo.getManage_str();		 
		    	String id=(String)req.getParameter("node");
/*		       Enumeration paraName = req.getParameterNames();	
		       while (paraName.hasMoreElements())
		       {
		         String name = (String) paraName.nextElement();
		         System.out.println("action parameter name=" + name);
		       }*/
		       String codeid="";
		       String codevalue="";
		       String privflag=null;
		    	
	    	   codeid="@K";//userView.getManagePrivCode();//"@K" for h23;// 
	    	   codevalue=userView.getManagePrivCodeValue();
	    	   if("@K".equals(codeid)&& "".equals(codevalue))
	    	   {                              
	    		   //添加 "UN".equalsIgnoreCase(userView.getManagePrivCode()) 条件判断 否则权限为空的时候限制失败  guodd 2014-10-29
	    	       if(!userView.isBThreeUser() && "UN".equalsIgnoreCase(userView.getManagePrivCode()))
	    	    	   codevalue="ALL";
	    	       else{
		    		   if("UN".equalsIgnoreCase(userView.getManagePrivCode())){
		    			   codevalue="ALL";
		    		   }
		    	   }
	    	   }

	    	   
	    	   if(userView.isSuper_admin()&&(!userView.isBThreeUser())) 
	    	   {
	    	     codevalue="ALL";  
	    	     codeid="@K";
	    	   }	

		       if("root".equalsIgnoreCase(id))
		       {
		    	   privflag="1";
		       }
		       else
		       {
//			    	codeid=id.substring(0, 2);
			    	codevalue=id.substring(2);  
		       }

		    	CreateCodeXml codexml=new CreateCodeXml(codeid,codevalue,privflag);
		    	tmp=codexml.outJSonCodeTree(managed_str,havechecked);
		    	//取得修改过后的组织机构名称  jingq add 2014.4.30
		    	Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(conn);
		    	String org_tree_desc = sysoth.getValue(11);
		       /**集团范围*/
 	            if("ALL".equalsIgnoreCase(codevalue))
		        {
		    	   buf.append("[{id:'ALL'");
		    	   buf.append(",text:'");
		    	   //判断组织机构名称是否修改过，如果修改了使用新名称  jingq add
		    	   if("".equals(org_tree_desc)){
		    		   buf.append(ResourceFactory.getProperty("tree.orgroot.orgdesc"));
		    	   } else {
		    		   buf.append(org_tree_desc);
		    	   }
		    	   buf.append("'");
		    	   if("UN".equalsIgnoreCase(managed_str))
		    		   buf.append(",checked:true");
		    	   else
		    		   buf.append(",checked:false");
		    	   buf.append(",leaf:false");
		    	   buf.append(",expanded: true");
		    	   buf.append(",icon:'/images/unit.gif'");
		    	   buf.append(",children:");
		    	   buf.append(tmp);		
		    	   buf.append("}]");
		        }
 	            else
 	            	buf.append(tmp);
 	            tmp=buf.toString();
		    }
		    
		    if("partymanagepriv".equalsIgnoreCase(type)|| "membermanagepriv".equalsIgnoreCase(type))
		    {
		    	SysPrivBo sysPrivBo=new SysPrivBo(role_id,flag,conn,"warnpriv");	        	
		        String warn_str=sysPrivBo.getWarn_str();
		        int res_type = IResourceConstant.PARTY;
		        if("membermanagepriv".equalsIgnoreCase(type))
		        	res_type = IResourceConstant.MEMBER;
		        ResourceParser parser=new ResourceParser(warn_str,res_type);
				String managed_str = parser.getContent();
		    	String id=(String)req.getParameter("node");
/*		       Enumeration paraName = req.getParameterNames();	
		       while (paraName.hasMoreElements())
		       {
		         String name = (String) paraName.nextElement();
		         System.out.println("action parameter name=" + name);
		       }*/
		       String codeid="";
		       String codevalue="";
		       String privflag=null;
		    	
	    	   codeid="64";
	    	   if("membermanagepriv".equalsIgnoreCase(type))
	    		   codeid="65";
	    	   codevalue = userView.getResourceString(res_type);
	    	   if(codevalue.length()<3){
	    		   if(userView.isSuper_admin()&&!userView.isBThreeUser())
	    			   codevalue="ALL";
	    		   else{
	    			   if("64".equals(codevalue)|| "65".equals(codevalue))
	    				   codevalue="ALL";
	    			   else
	    				   codevalue=""; 
	    		   }
	    	   }else{
	    		   codevalue=this.analyseManagePriv(codevalue);
	    		   if(codevalue.length()<1)
	    			   codevalue="ALL";
	    	   		
	    	   }
		       if("root".equalsIgnoreCase(id))
		       {
		    	   privflag="1";
		       }
		       else
		       {
//			    	codeid=id.substring(0, 2);
			    	codevalue=id.substring(2);  
		       }

		    	CreateCodeXml codexml=new CreateCodeXml(codeid,codevalue,privflag);
		    	tmp=codexml.outJSonCodeTree(managed_str,"0");
 	            if("ALL".equalsIgnoreCase(codevalue))
		        {
		    	   buf.append("[{id:'ALL'");
		    	   buf.append(",text:'");
		    	   if("partymanagepriv".equalsIgnoreCase(type))
		    		   buf.append(ResourceFactory.getProperty("dtgh.party.Y.codesetdesc"));
		    	   else if("membermanagepriv".equalsIgnoreCase(type))
		    		   buf.append(ResourceFactory.getProperty("dtgh.party.V.codesetdesc"));
		    	   buf.append("'");
		    	   if("partymanagepriv".equalsIgnoreCase(type)){
			    	   if(this.haveAll(managed_str,"64"))
			    		   buf.append(",checked:true");
			    	   else
			    		   buf.append(",checked:false");
		    	   }
		    	   if("membermanagepriv".equalsIgnoreCase(type)){
			    	   if(this.haveAll(managed_str,"65"))
			    		   buf.append(",checked:true");
			    	   else
			    		   buf.append(",checked:false");
		    	   }
		    	   buf.append(",leaf:false");
		    	   buf.append(",icon:'/images/unit.gif'");
		    	   buf.append(",children:");
		    	   buf.append(tmp);		
		    	   buf.append("}]");
		        }
 	            else
 	            	buf.append(tmp);
 	            tmp=buf.toString();
		    }
	        resp.setHeader("cache-control", "no-cache");
	        resp.setHeader("Pragma", "no-cache");
	        resp.setContentType("text/html;charset=UTF-8");

			resp.getWriter().write(tmp);
			resp.getWriter().close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				//if(rset!=null)
				//	rset.close();
				if(conn!=null&&(!conn.isClosed()))
					conn.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}		
	}
	
	private String analyseManagePriv(String managed_str){
		if(managed_str.length()<3)
			return "";
		StringBuffer sb = new StringBuffer();
		String[] strS = managed_str.split(",");
 		 String ids="";
 		 for(int i=0;i<strS.length;i++){
 			 String id = strS[i];
 			 if(id!=null&&id.length()>1){
 				 boolean check = true;
 				 for(int j=0;j<strS.length;j++){
 					 String id_s = strS[j];
 					 if(id_s!=null&&id_s.length()>1){
 						 if(id.length()>id_s.length()){
 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
								 check = false;
								 ids=id_s;
								 break;
							 }
 						 }else{
 							 if(id.equalsIgnoreCase(id_s)){
 								 continue;
 							 }
 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
 								 check = false;
 								ids=id_s;
 								 break;
 							 }
 						 }
 					 }
 				 }
 				 if(check){
 					if(sb.indexOf(id)==-1)
 						sb.append("','"+id.substring(2));
 				 }else{
 					 if(id.length()<ids.length()){
 						if(sb.indexOf(id)==-1)
 							sb.append("','"+id.substring(2));
 					 }
 				 }
 			 }
 		 }
 		if(sb.length()<4)
			return "";
		else
			return sb.substring(3);
	}

	private boolean haveAll(String manager_str,String value){
		boolean flag = false;
		String[] strs = manager_str.split(",");
		for(int i=strs.length-1;i>=0;i--){
			if(value.equals(strs[i])){
				flag=true;
				break;
			}
		}
		return flag;
	}
	
	/*private void getbflag(HttpServletRequest req){
		Cookie[] cookies=req.getCookies();
		Cookie cookie=null;
		bflag="hl";
		for (int i = 0; i < cookies.length ; i++)
	      {
	        if (cookies[i].getName().equals("bosflag"))
	        {
	          cookie = cookies[i];
	          break;
	        }
	      }
		if(cookie!=null)
			bflag=cookie.getValue();
	}*/
}
