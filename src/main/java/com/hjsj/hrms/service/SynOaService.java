package com.hjsj.hrms.service;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Actor;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Instance;
import com.hjsj.hrms.service.ladp.Env;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 
 *  
 * <p>Title:SynOaService.java</p>
 * <p>Description>:人事异动同步外部系统数据 （GDZY）</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 11, 2012 2:42:11 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author:dengc
 */

public class SynOaService {
	// xml文档
	private Document doc = null; 
	private HashMap tabOptMap=new HashMap();  //1启动报批时发送  2提交入库时发送
	private String selfapply="0";  //不是业务申请
	private String synTabids="";
	private String data_item="";  //判断是公司人员 还是市场营销人员异动指标
	private HashMap dataItemValue=new HashMap(); //公文模板标识
	private HashMap orgFlagMap=new HashMap();    //机构标识
	private ArrayList linkFieldList=new ArrayList();
	// 调用log4j的日志，用于输出
	private static Category log = Category.getInstance(Env.class.getName());
	
	public SynOaService()
	{
		
	}
	
	/**
	 * 引入dataList中的 人员|机构 数据，同时将dataList数据更新到模板对应记录中，再自动执行提交操作
	 * @param tabid: 模板id
	 * @param userview:报批用户身份
	 * @param inforFlag: =0 人员  =1 岗位  = 2 单位|部门
	 * @param dataList： LazyDynaBean   { [key: nbase+a0100|e01a1|e0122 , e0122_1:"xxxxx" ,e0122_2:"xxxx",a2403_2:"2015-02-04 17:54" ......  ]   }       , key: 必填信息      日期格式支持: 2015-02-04 17:54:00 | 2015-02-04
	 * @param selfapply: =1 自助用户业务申请   =0 业务用户
	 * @return 1：报批成功   0：失败   或其它错误信息
	 * @throws GeneralException
	 */
	public String autoSubmitTemplate(int tabid,UserView userview,int inforFlag, ArrayList  dataList, String selfapply,Connection conn) throws GeneralException
	{
		String result="1";   //成功
		RowSet frowset=null;
		try
		{
				TemplateTableBo tablebo=new TemplateTableBo(conn,tabid,userview);
				/**员工自助申请*/
				if("1".equalsIgnoreCase(selfapply))
					tablebo.setBEmploy(true);
				ContentDAO dao=new ContentDAO(conn);
				if(dataList.size()==1&& "1".equals(selfapply))
				{
					tablebo.createTempTemplateTable();
				}
				impObjToTemplate(tabid,userview,inforFlag,dataList, selfapply, conn,tablebo);  //引入dataList中的 人员|机构 数据
				updateTemplateInfo(tabid,userview,inforFlag,dataList, selfapply, conn);  //dataList记录中的数据同步到template临时表
				
				if(tablebo.isBsp_flag()&&tablebo.getSp_mode()==0) //仅支持自动审批模板
				{
					/**员工自助申请*/
					if("1".equalsIgnoreCase(selfapply))
						tablebo.setBEmploy(true);
					//单据正在处理，不允许重复申请
					String info=tablebo.validateExistData();
					if(info.length()>0)
						throw new GeneralException(info); 
					/**必填项校验*/
					String srcTab=userview.getUserName()+"templet_"+tabid;
					ArrayList fieldlist=tablebo.getAllFieldItem();
					if("1".equalsIgnoreCase(selfapply))
						srcTab="g_templet_"+tabid;
					ArrayList personlist = new ArrayList();
					personlist = tablebo.getPersonlist(tablebo.getInfor_type(),srcTab);
					boolean isCal=false;
					if(!(tablebo.isBsp_flag()&&tablebo.getSp_mode()==0))
						isCal=true;
					if(tablebo.getSplit_data_model()!=null&&tablebo.getSplit_data_model().trim().length()>0&&("superior".equalsIgnoreCase(tablebo.getSplit_data_model())|| "groupfield".equalsIgnoreCase(tablebo.getSplit_data_model())))
						isCal=true;
					if(isCal)
					{ 
							if(tablebo.getAutoCaculate().length()==0)
							{
								if(SystemConfig.getPropertyValue("templateAutoCompute")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("templateAutoCompute")))//&&!selfapply.equalsIgnoreCase("1"))
								{
									tablebo.batchCompute("0");
								}
							}else if("1".equals(tablebo.getAutoCaculate()))
							{
								tablebo.batchCompute("0");
							}
							
				//			tablebo.checkMustFillItem(srcTab,fieldlist,/*ins_id*/0);
							tablebo.checkLogicExpress(srcTab, 0, fieldlist); 
					}
					
					WF_Actor wf_actor=getWf_actor(userview);
					
					 ArrayList whlList=new ArrayList(); 
					 whlList.add(""); 
					 //拆单
					 if(!"1".equalsIgnoreCase(selfapply))
					 {  
							 whlList=tablebo.getSplitInstanceWhl();
							 if(whlList.size()==1&&((String)whlList.get(0)).trim().length()==0)//不拆单
							 {
								 
							 }
							 else
								 wf_actor.setSpecialRoleUserList(new ArrayList());
						 
					 }
					
					 for(int i=0;i<whlList.size();i++)
					 {
						
						RecordVo ins_vo=new RecordVo("t_wf_instance");	
						WF_Instance ins=new WF_Instance(tablebo,conn); 
						String whl=(String)whlList.get(i); 
						if("1".equalsIgnoreCase(selfapply))
							ins.setObjs_sql(ins.getObjsSql(0,0,1,tabid+"",userview,""));
						else
							ins.setObjs_sql(ins.getObjsSql(0,0,2,tabid+"",userview,whl));
						if(ins.createInstance(ins_vo,wf_actor,whl))//将数据插入到t_wf_instance、t_wf_task_objlink和t_wf_task中
						{
						     
						    
						    boolean isOriData=false; // 表单数据没到临时表
							String sql="select count(*) as nrec from ";
							if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
								sql+=" g_templet_"+tabid+" where a0100='"+userview.getA0100()+"' and lower(basepre)='"+userview.getDbname().toLowerCase()+"'";
							else
							{
								sql+=userview.getUserName()+"templet_"+tabid+" where submitflag=1"+whl;
							
							}
							frowset=dao.search(sql);
							if(frowset.next())
							{
								if(frowset.getInt(1)>0)
								{
								    isOriData=true;
									
								}
							} 
							if(isOriData)
							{
							   
							    String approve_opinion = tablebo.getApproveOpinion(ins_vo,"0", wf_actor,"");
		                        tablebo.setApprove_opinion(approve_opinion);
		                        if("1".equalsIgnoreCase(selfapply))
		                            tablebo.saveSubmitTemplateData(ins_vo.getInt("ins_id"));
		                        else//将数据插入到template_tabid中
		                            tablebo.saveSubmitTemplateData(userview.getUserName(),ins_vo.getInt("ins_id"),whl);
							}
							else
							{
							   
							} 
						}
						   
						int ins_id = ins_vo.getInt("ins_id");
						frowset=dao.search("select * from id_factory where sequence_name='t_wf_file.file_id'");
						if(!frowset.next())
						{//这个语句是向id_factory添加主键自动生成的功能
							StringBuffer insertSQL=new StringBuffer();
							insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue,auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
							insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
							ArrayList list=new ArrayList();
							dao.insert(insertSQL.toString(),list);
						}
						frowset = dao.search(" select * from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+userview.getUserName()+"'");
						String sqlstrs = "";
						while(frowset.next()){
							String file_id = frowset.getString("file_id");
							 IDGenerator idg = new IDGenerator(2, conn);
				    			String file_id2 = idg.getId("t_wf_file.file_id");
							sqlstrs=" insert into t_wf_file(file_id,content,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,create_user,create_time) select "+file_id2+",content,filetype,objectid,basepre,attachmenttype,"+ins_id+",tabid,ext,name,create_user,create_time from t_wf_file where file_id="+file_id+"  ";
							dao.update(sqlstrs);
						} 
						
						
						boolean isSend=true;
						if(SystemConfig.getPropertyValue("clientName")!=null&& "gdzy".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())&& "1".equalsIgnoreCase(selfapply))
							isSend=false;
						 //将单据信息发送至外部系统
				//		 if(!selfapply.equalsIgnoreCase("1"))
						 if(isSend)
						 {
							 SynOaService sos=new SynOaService();
							 String tab_ids=sos.getTabids();
							 if(tab_ids.indexOf(","+tabid+",")!=-1)
							 {
								if("1".equals((String)sos.getTabOptMap().get(tabid+"")))
								{
									String _info=sos.synOaService(String.valueOf(ins.getTask_vo().getInt("task_id")),tabid+"",userview);  //创建成功返回1，否则返回详细错误信息
									if(!"1".equals(_info))
										throw GeneralExceptionHandler.Handle(new Exception(_info));	
								}
							 }
						 }
						
					 } //循环whlList 结束
					frowset=dao.search("select count(*) from "+srcTab+" where submitflag=0");
					boolean deleteFlag=true;
					if(frowset.next()){
					    if(frowset.getInt(1)!=0){
					        deleteFlag=false;
					    }
					} 
					 if(deleteFlag){
						 dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+userview.getUserName()+"' and (attachmenttype is null or attachmenttype=0)");
					 }
					//再清空个人附件
					 StringBuffer sb = new StringBuffer("");
					 if("1".equals(selfapply)){//如果是员工自助申请，那么直接删除
							dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+userview.getUserName()+"' and (attachmenttype=1) and objectid='"+userview.getA0100()+"' and lower(basepre)='"+userview.getDbname().toLowerCase()+"'");
					 }else{
						sb.setLength(0);
						if(tablebo.getInfor_type()==1){
							sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+userview.getUserName()+"' and (attachmenttype=1) and basepre=? and objectid=?");
						}else{
							sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+userview.getUserName()+"' and (attachmenttype=1) and objectid=?");
						}
						dao.batchUpdate(sb.toString(),personlist);
					}
					  
			
				}
				else
					result="程序仅支持自动流转模板";
		}
		catch(Exception e)
        {
			result=e.getMessage();
            throw GeneralExceptionHandler.Handle(e);
        }
		finally
		{
			PubFunc.closeDbObj(frowset);
		}
    	
		return result;
	}
	
	
	private WF_Actor 	getWf_actor(UserView userview)
	{
		String actorid="";
		String actor_type=""; 
		if(userview.getStatus()==0)
		{
			actorid=userview.getUserName();
			actor_type="4";
		}
		else
		{
			actorid=userview.getDbname()+userview.getA0100();  //this.userView.getUserName();
			actor_type="1";
		}	
		RowSet frowset=null;
		WF_Actor 	wf_actor=new WF_Actor(actorid,actor_type);
		wf_actor.setContent("");//当前提交人的审批意见
		wf_actor.setEmergency("3");
		wf_actor.setSp_yj("01"); 
		wf_actor.setActorname(userview.getUserFullName());	
		wf_actor.setBexchange(false);
		return wf_actor;
	}
	
	/**
	 * 引入dataList中的 人员|机构 数据
	* @param tabid: 模板id
	 * @param userview:报批用户身份
	 * @param inforFlag: =0 人员  =1 岗位  = 2 单位|部门
	 * @param dataList： LazyDynaBean   { [key: nbase+a0100|e01a1|e0122 , e0122_1:"xxxxx" ,e0122_2:"xxxx",a2403_2:"2015-02-04 17:54" ......  ]   }       , key: 必填信息    
	 * @param selfapply: =1 自助用户业务申请   =0 业务用户
	 * @return
	 */
	private  boolean  impObjToTemplate(int tabid,UserView userview,int inforFlag, ArrayList  dataList, String selfapply,Connection conn,TemplateTableBo tablebo)
	{
			boolean  flag=true;
			try
			{ 
				
				 ArrayList a0100List = new ArrayList();//存放选中人员的a0100
				LazyDynaBean bean=null;
				String unit_value=null;
				HashMap hm=new HashMap();
				ArrayList a0100list=new ArrayList(); 
				
				for(int i=0;i<dataList.size();i++)
				{
					bean=(LazyDynaBean)dataList.get(i);
					String obj_id=(String)bean.get("key");
					
					if(obj_id==null|| "".equals(obj_id))
						continue; 
					
					if(inforFlag==0)//如果是人员
					{
						String pre=obj_id.substring(0,2).toLowerCase();
						/**对人员信息群时，过滤单位、部门及职位*/
						if("UN".equalsIgnoreCase(pre)|| "UM".equalsIgnoreCase(pre)|| "@K".equalsIgnoreCase(pre))
							continue;
						pre=obj_id.substring(0,3).toLowerCase();
						/**按人员库进行分类*/
						if(!hm.containsKey(pre))//hm包含所有的人员库
						{
							a0100list=new ArrayList();
						}
						else
						{
							a0100list=(ArrayList)hm.get(pre);
						} 
						a0100list.add(obj_id.substring(3));
						hm.put(pre,a0100list); 
					}
					else///如果不是人员
					{
						if(a0100list==null)
							a0100list=new ArrayList();
						if(i==0)
						{
							unit_value=obj_id;
						}
						a0100list.add(obj_id);
					}
				} //for objlist loop end.
				
				if(inforFlag==2)
					hm.put("B",a0100list);
				if(inforFlag==1)
					hm.put("K",a0100list);
		 
				///开始一个人员库一个人员库地导入
				Iterator iterator=hm.entrySet().iterator();
				ArrayList tempList=null;
				while(iterator.hasNext())
				{
					Entry entry=(Entry)iterator.next();
					String pre=entry.getKey().toString();
					a0100list =(ArrayList)entry.getValue();
					if(a0100list.size()==0)
						continue;
					
					if(a0100list.size()<=500)
						tablebo.impDataFromArchive(a0100list,pre);
					else
					{
						
						int size=a0100list.size();
						int n=size/500+1;
						for(int i=0;i<n;i++)
						{
							tempList=new ArrayList();
							for(int j=i*500;j<(i+1)*500;j++)
							{
								if(j<a0100list.size())
									tempList.add((String)a0100list.get(j));
								else
									break;
							}
							if(tempList.size()>0)
								tablebo.impDataFromArchive(tempList,pre);
							
						}
						
					}
				}
		
			}
			catch (Exception e) {
				e.printStackTrace();  
				flag=false;
			}
			return flag;
	}
	
	
	/**
	 * dataList记录中的数据同步到template临时表
	 * @param tabid: 模板id
	 * @param userview:报批用户身份
	 * @param inforFlag: =0 人员  =1 岗位  = 2 单位|部门
	 * @param dataList： LazyDynaBean   { [key: nbase+a0100|e01a1|e0122 , e0122_1:"xxxxx" ,e0122_2:"xxxx",a2403_2:"2015-02-04 17:54:00" ......  ]   }       , key: 必填信息     日期格式支持: 2015-02-04 17:54:00 | 2015-02-04
	 * @param selfapply: =1 自助用户业务申请   =0 业务用户
	 * @return
	 */
	private boolean  updateTemplateInfo(int tabid,UserView userview,int inforFlag, ArrayList  dataList, String selfapply,Connection conn)
	{
		boolean  flag=true;
		String tabname=userview.getUserName().toLowerCase()+"templet_"+tabid;
		if(dataList.size()==1&& "1".equals(selfapply))
		{ 
			tabname="g_templet_"+tabid;
		}
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			LazyDynaBean bean=null;
			FieldItem item=null;
			for(int i=0;i<dataList.size();i++)
			{
				bean=(LazyDynaBean)dataList.get(i);
				String objectid=(String)bean.get("key");
			   
				RecordVo vo=new RecordVo(tabname);
	    		if(inforFlag==0)
	    		{	
	    			vo.setString("a0100",objectid.substring(3));
	    			vo.setString("basepre", objectid.substring(0,3));
	    		}
	    		else if(inforFlag==1)
	    			vo.setString("e01a1",objectid.substring(3));
	    		else if(inforFlag==2)
	    			vo.setString("b0110",objectid.substring(3));
	    		try
	    		{
	    			vo=dao.findByPrimaryKey(vo); 
	    		}
	    		catch(Exception ee)
	    		{
	    			continue;
	    		}
				Set keyset=bean.getMap().keySet(); 
			    String key="";
			    String value="";
			    for(Iterator t=keyset.iterator();t.hasNext();)
			    {
			    	key=((String)t.next());
			    	value=(String)bean.get(key);
			    	key=key.toLowerCase();
			    	if(!"key".equalsIgnoreCase(key))
			    	{
			    		if(key.startsWith("t_"))
			    			vo.setString(key, value);
			    		else 
			    		{
			    			String[] temps=key.split("_");
			    			item=DataDictionary.getFieldItem(temps[0]);
			    			if(item==null)
			    				continue;
			    			if("N".equalsIgnoreCase(item.getItemtype()))
			    			{
			    				if(item.getDecimalwidth()==0)
			    					vo.setInt(key,new Integer(value));
			    				else
			    					vo.setDouble(key,new Double(value));
			    			}
			    			else if("D".equalsIgnoreCase(item.getItemtype()))
			    			{ 
			    					vo.setDate(key, value); 
			    			}
			    			else
			    			{
			    					vo.setString(key, value);
			    			}
			    		} 
			    	}
			    	
			    } 
			    dao.updateValueObject(vo);
			} 
		}
		catch (Exception e) {
			e.printStackTrace();  
			flag=false;
		}
		return flag;
	}
	
	
	public static void main(String[] args)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("aa","dddd");
		abean.set("bb_2","dds");
		 Set keyset=abean.getMap().keySet();
		 for(Iterator t=keyset.iterator();t.hasNext();)
			 System.out.println((String)t.next());
	}
	
	
	/**
	 * 获得service配置文件
	 * 
	 * <?xml version="1.0" encoding = "GB2312" ?>
	 * <sync> 
	 *	<!--同步人员的外部系统webservice，username为webservice的用户名，没有可以为空；password为webservice的密码，没有可以为空，url为webservice的url地址，function为方法名，namespace为命名空间,style为方式，值为rpc、document、default、message、wrapped-->
	 *	<hrwebservice username="" password="" url="http://172.16.11.54:7070/RenYuanYiDong.asmx" function="ChuangJianTongZhiDan" namespace="http://www.excellence.com.cn/OA_HR/WebService" paramname="data" style="wrapped"></hrwebservice>
     * 	<!-- 机构标识  -->
	 *  	<server_id zy='0101,0102' gz='xxxx,xxxx' zj='' mz='' sg='' /> 
	 *	<!-- 同步OA模板编号 -->
	 *		<tabs>
	 *		<tab id='45'   >
	 *			<!-- 公文模板标识  itemid:通过模板中指标的值确定公文模板标识  -->
	 *			<date_id itemid='A3009_2' >
	 *				<code_id value='01' >363</code_id>
	 *			  <code_id value='02' >243</code_id>
	 *			</date_id> 
	 *			<fields_ref>
	 *			<!--模板指标对应，hrfield指标名称，xmlnodename表示需要发送的指标名称，为空时表示与hrfield同名（注：如指标是变化前的：xxxx_1 指标是变化后的 yyyy_2 ），des描述-->
	 *				<field_ref hrfield="a0101_1" xmlnodename="user_name" des="姓名"></field_ref>
	 *				<field_ref hrfield="a0107_1" xmlnodename="user_sex3" des="性别"></field_ref>
	 *				<field_ref hrfield="A3003_2" xmlnodename="tran_date" des="异动日期"></field_ref>	 	 
	 *	 		</fields_ref> 
	 * 		</tab>
	 *		
	 * 		<tab id='46'   >
	 *			<!-- 公文模板标识  itemid:通过模板中指标的值确定公文模板标识  -->
	 *			<date_id itemid='A3009_2' >
	 *				<code_id value='01' >362</code_id>
	 *			  <code_id value='02' >241</code_id>
	 *			</date_id> 
	 *			<fields_ref>
	 *			<!--模板指标对应，hrfield指标名称，xmlnodename表示需要发送的指标名称，为空时表示与hrfield同名（注：如指标是变化前的：xxxx_1 指标是变化后的 yyyy_2 ），des描述-->
	 *				<field_ref hrfield="a0101_1" xmlnodename="user_name" des="姓名"></field_ref>
	 *				<field_ref hrfield="a0107_1" xmlnodename="user_sex" des="性别"></field_ref>
	 *				.......
	 *	 		</fields_ref> 
	 * 		</tab>
	 *	</tabs>	
 	 *	</sync>
	 * @throws GeneralException
	 */
	private void getXmlConfigDocument() throws GeneralException
	{
		String xmlPath="";
		String classPath = System.getProperty("java.class.path");
		// 路径分割符号
		String sep = System.getProperty("path.separator");

		String[] path = classPath.split(sep);
		File file = null;
		for (int i = 0; i < path.length; i++) {
			file = new File(path[i], "oasync.xml");
			if (file.exists()) {
				xmlPath = path[i]; 
				break;
			}
		}
		if(xmlPath.length()>0)
		{
			InputStream in = null;
			try {
				if (file != null && file.exists()) {
					in = new FileInputStream(file);
					doc = PubFunc.generateDom(in);
					
					 
					 Element element=null;
					 List list=null;
					 XPath  xPath = XPath.newInstance("/sync/server_id");
					 element= (Element) xPath.selectSingleNode(this.doc);  
					 if(element==null)
					 {
						 if(SystemConfig.getPropertyValue("clientName")!=null&& "gdzy".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim()))
							 throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
					 }
					 else
					 {
						 list=element.getAttributes();
						 for(int i=0;i<list.size();i++)
						 {
							 Attribute attri = (Attribute) list.get(i);
							 orgFlagMap.put(attri.getName(),attri.getValue());  
						 } 
					 }
					 
					 
					 
					 xPath = XPath.newInstance("/sync/tabs/tab");
					 list= xPath.selectNodes(this.doc);  
					 if(list==null)
					 {
						 throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
					 }
					 else
					 { 
						 for(int i=0;i<list.size();i++)
						 {
							 element= (Element) list.get(i);
							 String id=element.getAttributeValue("id"); 
							 String opt=element.getAttributeValue("opt");
							 if(opt==null||opt.trim().length()==0||(!"2".equals(opt.trim())&&!"1".equals(opt.trim())))
								 opt="1";
							 this.synTabids+=","+id;
							 this.tabOptMap.put(id.trim(),opt);
						 } 
						 this.synTabids+=",";
					 }
					   
				} 
			} catch (Exception e) {
				e.printStackTrace(); 
				throw GeneralExceptionHandler.Handle(e);
			}finally {
				PubFunc.closeResource(in);
			}
			
		}
		
	}
	
	
	
	
	
	public void getTabParam(String tabid) throws GeneralException
	{ 
		if(this.doc!=null)
		{
			try {

				 XPath xPath = XPath.newInstance("/sync/tabs/tab[@id='"+tabid+"']/date_id");
				 Element element= (Element) xPath.selectSingleNode(this.doc);  
				 if(element==null)
				 {
					 data_item="templateID";
					// throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
				 }
				 else
				 {
					 data_item=element.getAttributeValue("itemid");  
				 }
				
				List list=null;
				if(!"templateID".equalsIgnoreCase(data_item))
				{
					 xPath = XPath.newInstance("/sync/tabs/tab[@id='"+tabid+"']/date_id/code_id");
					 list= xPath.selectNodes(this.doc);  
					 if(element==null)
					 {
						 throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
					 }
					 else
					 {
						 for(int i=0;i<list.size();i++)
						 {
							 element= (Element) list.get(i); 
							 dataItemValue.put(element.getAttributeValue("value"), element.getValue());
						 } 
					 }
				}
			 
				 xPath = XPath.newInstance("/sync/tabs/tab[@id='"+tabid+"']/fields_ref/field_ref");
				 list= xPath.selectNodes(this.doc);  
				 if(list==null)
				 {
					 throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
				 }
				 else
				 { 
					 for(int i=0;i<list.size();i++)
					 {
						 element= (Element) list.get(i);
						 LazyDynaBean abean=new LazyDynaBean();
						 abean.set("hrfield", element.getAttributeValue("hrfield"));
						 abean.set("xmlnodename", element.getAttributeValue("xmlnodename"));
						 linkFieldList.add(abean);
					 } 
				 }
				 
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		
		}
	}
	
	
	
	/**
	 * 生成异动单据
	 * @param task_id
	 * @param conn
	 * @return
	 */
	public String getSendDataXml(String task_id,String tabid,UserView userview,Connection conn) throws GeneralException
	{
		StringBuffer xml=new StringBuffer("");
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			
			
			String srctab="templet_"+tabid;
			StringBuffer sql=new StringBuffer();
			if("0".equals(task_id))//sutemplet_xxx，以用户名有关的临时表
				srctab=userview.getUserName()+srctab;
			if("1".equals(this.selfapply))//员工通过自助平台发动申请
				srctab="g_templet_"+tabid;
			sql.append("select * from ");
			sql.append(srctab);
			if(!"0".equals(task_id))
			{ 
				//暂时不确定是否要加角色范围控制 
				sql.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
				sql.append("  and task_id="+task_id+" and tab_id="+tabid+"  ) ");
			}
			else
			{
				if("1".equals(this.selfapply))//员工通过自助平台发动申请
				{
					sql.append(" where basepre='");
					sql.append(userview.getDbname());
					sql.append("' and a0100='");
					sql.append(userview.getA0100());
					sql.append("'");
				}
				else
					sql.append(" where submitflag=1");
			}
			
			
			
			
			
		//	StringBuffer sql=new StringBuffer("select * from templet_"+tabid+" where ");
		//	sql.append("   exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum  and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id");
		//	sql.append("  and task_id="+task_id+" and tab_id="+tabid+"  ) ");
			
			
			String data_id="";
			String proc_auth=userview.getUserName();
			String server_id=""; //机构标识
			String serial_num=task_id;
			rowSet=dao.search(sql.toString());
			if("templateID".equalsIgnoreCase(data_item))
			{
				data_id=tabid;
			}
			else
			{
				if(rowSet.next())
				{
					String value=rowSet.getString(data_item);
					if(value==null)
						throw GeneralExceptionHandler.Handle(new Exception("单据内容没有指定公文模板标识!"));	 	
					if(this.dataItemValue.get(value)!=null&&((String)this.dataItemValue.get(value)).length()>0)
						data_id=(String)this.dataItemValue.get(value);
					
					String orgid=userview.getUserOrgId();
					Set set=orgFlagMap.keySet();
					for(Iterator t=set.iterator();t.hasNext();)
					{
						String key=(String)t.next();
						value=","+(String)orgFlagMap.get(key)+",";
						if(value.indexOf(","+orgid+",")!=-1)
							server_id=key;
					} 
				}
				rowSet.beforeFirst();
			}
			
			if(data_id.length()==0||proc_auth.length()==0)
				 throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
			if(server_id.length()==0&&!"templateID".equalsIgnoreCase(data_item))
				 throw GeneralExceptionHandler.Handle(new Exception("service配置文件机构标识设置有问题!"));	
			xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			xml.append("<root><baseinfo><data_id>"+data_id+"</data_id><proc_auth>"+proc_auth+"</proc_auth>");
			xml.append("<server_id>"+server_id+"</server_id><serial_num>"+serial_num+"</serial_num></baseinfo><users>");
			LazyDynaBean abean=null;
			String value="";
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next())
			{
				 xml.append("<user>");
				 for(int i=0;i<this.linkFieldList.size();i++)
				 {
					 value="";
					 abean=(LazyDynaBean)this.linkFieldList.get(i);
					 String hrfield=(String)abean.get("hrfield");
					 String xmlnodename=(String)abean.get("xmlnodename");
					 String type="A";
					 if(hrfield.length()>2)
					 {
						 String temp_name=hrfield.substring(0,hrfield.length()-2);
						 FieldItem item=DataDictionary.getFieldItem(temp_name.toLowerCase());
						 if(item!=null)
							 type=item.getItemtype();
					 }
					 if(("A".equalsIgnoreCase(type)|| "N".equalsIgnoreCase(type))&&rowSet.getString(hrfield)!=null)
					 {
						 value=rowSet.getString(hrfield);
					 }
					 else if("D".equalsIgnoreCase(type)&&rowSet.getDate(hrfield)!=null)
					 {
						 
						 value=df.format(rowSet.getDate(hrfield));
					 }
					 else if("M".equalsIgnoreCase(type))
					 {
						 value=Sql_switcher.readMemo(rowSet,hrfield);
					 }
					 
					 xml.append("<"+xmlnodename+">"+value+"</"+xmlnodename+">"); 
				 }
				 xml.append("</user>");
			}
			xml.append("</users></root>"); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally {
			try {
				if (rowSet != null){
					rowSet.close();
				}
			}catch (SQLException sql){
				 
			}
		}    
		return xml.toString();
	}
	
	
	/**
	 * 调用OA接口
	 * @param task_id
	 * @return  创建成功返回1，否则返回详细错误信息
	 */
	public String synOaService(String task_id,String tabid,UserView userview) throws GeneralException
	{
		String result="1";
		Connection conn = null; 
		try
 	    {
			if(doc==null)
				throw GeneralExceptionHandler.Handle(new Exception("没有定义OA的webservice配置文件!"));	
			getTabParam(tabid);
			conn=AdminDb.getConnection();
			String xml=getSendDataXml(task_id,tabid,userview,conn);
			log.debug("SynOaService.xml="+xml);
		 	result=sendMessage(xml);
			 
			
 	   } catch(Exception e) {
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(new Exception("发送人事异动失败，请重试！"));
		} finally {
			try {
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				 
			}
		}    
		return result;
	}
	
	
	
	
	/**
	 * 获得拆单后各单据下的人员的查询条件
	 * @return
	 */
	public ArrayList getSplitInstanceWhl(String tabid,UserView userview,Connection conn ) throws GeneralException
	{
		ArrayList list=new ArrayList();
		RowSet rowSet=null;
		try
		{
			
			getTabParam(tabid);
			ContentDAO dao=new ContentDAO(conn);
			String tablename=userview.getUserName()+"templet_"+tabid;
			String leave_dept_field="";
			String enter_dept_field="";
			
			LazyDynaBean abean=null;
			for(int i=0;i<this.linkFieldList.size();i++)
			{
				abean=(LazyDynaBean)this.linkFieldList.get(i);
				String hrfield=(String)abean.get("hrfield");
				String xmlnodename=(String)abean.get("xmlnodename");
				if("leave_dept".equalsIgnoreCase(xmlnodename))
					leave_dept_field=hrfield;
				if("enter_dept".equalsIgnoreCase(xmlnodename))
					enter_dept_field=hrfield;
			}
			
			XPath xPath = XPath.newInstance("/sync/tabs/tab[@id='"+tabid+"']/groupfield");
			Element el = (Element)xPath.selectSingleNode(this.doc);
			if (el != null) {
				leave_dept_field = el.getAttributeValue("leave_post");
				enter_dept_field = el.getAttributeValue("enter_dept");
			}
			
			if(leave_dept_field.length()<=0 || enter_dept_field.length()<=0)
				throw GeneralExceptionHandler.Handle(new Exception("service配置文件没定义离开或进入部门!"));	
			rowSet = dao.search("select * from "+tablename+" where submitflag=1 order by "+leave_dept_field);
			
			System.out.println("sql-------"+"select * from "+tablename+" where submitflag=1 order by "+leave_dept_field+"------"+enter_dept_field);
			
			HashMap groupMap = new HashMap();
			while(rowSet.next())
			{
				String value1=rowSet.getString(leave_dept_field)!=null?rowSet.getString(leave_dept_field):"";
				String value2=rowSet.getString(enter_dept_field)!=null?rowSet.getString(enter_dept_field):"";
				
				System.out.println(">>>>tabid---" + tabid + "---" + value1 + "---" + value2);
				
			//	log.debug(">>>>" + value1 + "---" + value2+">>>>tabid---" + tabid);
			//	log.error(">>>>" + value1 + "---" + value2+">>>>tabid---" + tabid);
				
				if(("3".equals(tabid) || "5".equals(tabid) || "17".equals(tabid) || "6".equals(tabid) || "16".equals(tabid)) && (value1.startsWith("0102") || value2.startsWith("0102"))) {
					if (value1.startsWith("0102")) {
						value1 = value1.substring(0, 6);
					}
					if (value2.startsWith("0102")) {
						value2 = value2.substring(0, 6);
					}
				} else {
					if (value1.startsWith("0101")) {
						value1 = value1.substring(0, 7);
					}
					
					if (value1.startsWith("0102")) {
						value1 = value1.substring(0, 9);
					}
					
					if (value2.startsWith("0101")) {
						value2 = value2.substring(0, 7);
					}
					
					if (value2.startsWith("0102")) {
						value2 = value2.substring(0, 9);
					}
					
				}
				
				System.out.println(">>>>>tabid---" + tabid + "---" + value1 + "---" + value2);
								
				String seqnum=rowSet.getString("seqnum");				
				if (groupMap.entrySet().size() == 0) 
				{
					ArrayList tmp = new ArrayList();
					tmp.add(seqnum);
					groupMap.put(value1 + "`" + value2, tmp);
					
					System.out.println("groupMap.getKey(0000)---" + value1 +"---"+ value2 + "---" + tmp);
				} 
				else 
				{					
					boolean flag = false;
					Set keySet = groupMap.keySet();
					Iterator it = keySet.iterator();
					while(it.hasNext())
					{
						String str = (String)it.next();  //键值	     					
						String[] strs = str.split("`",-1);//bug 33037 split在分割是如果是“1`”，不加第二个参数只会返回一个元素，加上会返回2个元素。
						if(strs.length>=2){
							System.out.println("groupMap.getKey()---"+str+"---" + value1 + "---" + strs[0]+"---"+ value2 + "---" + strs[1]);
						
							if (value1.equalsIgnoreCase(strs[0]) && value2.equalsIgnoreCase(strs[1])) {
								ArrayList temp = (ArrayList)groupMap.get(str);   //value值  
								temp.add(seqnum);
							
								System.out.println("groupMap.getValue()---" + temp.size() + "---" + seqnum);
							
								flag = true;
							//	break;
							}
						}
					}
					
					System.out.println("flag-------" +flag+"---groupMap:"+groupMap.size());
				//	log.debug("flag-------" +flag+"---groupMap:"+groupMap.size());
				//	log.error("flag-------" +flag+"---groupMap:"+groupMap.size());
					if (!flag) {
						ArrayList tmp = new ArrayList();
						tmp.add(seqnum);
						groupMap.put(value1 + "`" + value2, tmp);
					}
				}
				
			}
			
			Set set=groupMap.keySet();
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				ArrayList tmpList=(ArrayList)groupMap.get(key);
				String whl_str="";
				int n=0;
				for(int i=0;i<tmpList.size();i++)
				{
					if(n>4)
					{
						list.add(" and ("+whl_str.substring(3)+")");
						n=0;
						whl_str="";
					}
					
					whl_str+=" or seqnum='"+(String)tmpList.get(i)+"'";
					n++;
				}
				if(whl_str.length()>0)
					list.add(" and ("+whl_str.substring(3)+")");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		} finally {
			try {
				if (rowSet != null){
					rowSet.close();
				}
			}catch (SQLException sql){
				 
			}
		}
		
		for (int i= 0; i< list.size(); i++) {
			System.out.println("list---"+i + "-------" +list.get(i));
		//	log.debug(i + "-------" +list.get(i));
		//	log.error(i + "-------" +list.get(i));
		}
				
		return list;
	}
	
	 
	
	/**
	 * 调用OAservice接口
	 * @param xml
	 * @return
	 */
	public String sendMessage(String xml)throws GeneralException
	{
		String result="1"; 
		try
 	    {
			 String url="";
			 String namespace="";
			 String function="";
			 String paramname="";
			 String username="";
			 String password="";
			 String style="";
			 XPath xPath = XPath.newInstance("/sync/hrwebservice");
			 Element element= (Element) xPath.selectSingleNode(this.doc);  
			 if(element==null)
			 {
				 throw GeneralExceptionHandler.Handle(new Exception("service配置文件设置有问题!"));	
			 }
			 else
			 {
				 url=element.getAttributeValue("url");
				 namespace=element.getAttributeValue("namespace");
				 function=element.getAttributeValue("function");
				 paramname=element.getAttributeValue("paramname");
				 username=element.getAttributeValue("username");
				 password=element.getAttributeValue("password");
				 style=element.getAttributeValue("style");
				 
			 }
			  
			/* 
			xml="<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+"<root><baseinfo><data_id>362</data_id><proc_auth>zhangyl</proc_auth>"
				+"<server_id>zy</server_id><serial_num>1112</serial_num></baseinfo>"
				+"<users><user><user_name>zhangyl</user_name><user_sex>男</user_sex><tran_date>2012-02-02</tran_date>"
				+"<reason>原因</reason><leave_dept>离开部门</leave_dept><leave_office>离开职务</leave_office><enter_dept>进入部门</enter_dept>"
				+"<cur_position>现任职务</cur_position><wage_changetime>2012-02-02</wage_changetime><remarks>备注</remarks></user></users></root>";
			*/
			
		    Service service = new Service(); 
			Call call = (Call) service.createCall(); 			
			call.setTargetEndpointAddress(new java.net.URL(url)); 	
			call.setReturnType(XMLType.XSD_STRING);
			call.setUseSOAPAction(true);
			call.setOperationName(new QName(namespace, function));	        
			call.addParameter(new QName(namespace, paramname),XMLType.XSD_STRING,ParameterMode.IN);	
			call.setSOAPActionURI(namespace+"/"+function);
			if(username!=null&&username.length()>0)
			{
				call.getMessageContext().setUsername(username);
				call.getMessageContext().setPassword(password);
			}
			if(style!=null)
			{
				if("wrapped".equalsIgnoreCase(style))
					call.setOperationStyle(org.apache.axis.constants.Style.WRAPPED);
				
			}
			xml = new String(xml.getBytes(),"GB2312");//如果没有加这段，中文参数将会乱码
			String mess = (String) call.invoke( new Object[] {xml} );  

		
 	   } catch(Exception e) {
			e.printStackTrace(); 
			throw GeneralExceptionHandler.Handle(e);	
		}   
		return result;
	}
	
	
	
	
	/**
	 * 返回审批结果（HR系统提供，OA系统调用）
	 * @param yddid 异动单id
	 * @param result 1：成功；0：失败；
	 * @return 1：成功；0：失败；
	 */
	public String processResult(String  task_id,String result)
	{
		String  flag="1";
		Connection conn = null; 
		try
 	    {
			log.debug("SynOaService  task_id="+task_id+",result="+result);
			conn=AdminDb.getConnection();
			if(conn != null) {
				String tabid=getIdByTask(task_id,conn,1);
				String ins_id=getIdByTask(task_id,conn,2);
				UserView userView=getUserViewByTask(task_id,conn);
				TemplateTableBo tablebo=new TemplateTableBo(conn,Integer.parseInt(tabid),userView);
				tablebo.getTasklist().add(task_id);
				tablebo.setIns_id(Integer.parseInt(ins_id));
				RecordVo ins_vo=new RecordVo("t_wf_instance");
				ins_vo.setInt("ins_id",Integer.parseInt(ins_id));
				WF_Instance ins=new WF_Instance(tablebo,conn);
				ins.setObjs_sql(ins.getObjsSql(Integer.parseInt(ins_id),Integer.parseInt(task_id),3,tabid,userView,""));
				WF_Actor wf_actor=new WF_Actor(userView.getUserName(),"4");
				wf_actor.setBexchange(false);
				wf_actor.setActorname(userView.getUserFullName());	
				wf_actor.setContent("同意");  
				wf_actor.setSp_yj("01");//审批意见
				ins.setIns_id(Integer.parseInt(ins_id));
				ContentDAO dao=new ContentDAO(conn);
				dao.update("update t_wf_task_objlink set submitflag=1 where task_id="+task_id+" and tab_id="+tabid);
				if("0".equals(result))
				{
					wf_actor.setSp_yj("02");
					wf_actor.setContent("不同意");
					wf_actor.setBexchange(true);	  
					if(ins.rejectTask(ins_vo,wf_actor,Integer.parseInt(task_id),userView)); //,rejectObj))
					{
					;
					}						
				}
				else
					ins.createNextTask(ins_vo,wf_actor,Integer.parseInt(task_id),userView);
				
				 
			}
			else
				flag="0";
 	    } catch(Exception e) {
			e.printStackTrace();
			flag="0";
		} finally {
			try {
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				 
			}
		}    
		log.debug("SynOaService  processResult="+flag);
		return flag;
	}
	
	

	/**
	 * 根据 taskid 获得 处理人员的userView (只支持业务人员)
	 * @param task_id
	 * @param conn 
	 * @return
	 */
	private UserView getUserViewByTask(String task_id,Connection conn ) throws GeneralException
	{
		UserView userView=null; 
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select actorid,actor_type from t_wf_task where task_id="+task_id; 
			rowSet=dao.search(sql);
			if(rowSet.next())
			{
				String actorid=rowSet.getString("actorid");
				String actor_type=rowSet.getString("actor_type"); //4 业务用户
				if("4".equals(actor_type))
				{
					userView=new UserView(actorid, conn); 
					if(!userView.canLogin())
						throw GeneralExceptionHandler.Handle(new Exception("审批用户登录不成功!"));	
				}
				else
					throw GeneralExceptionHandler.Handle(new Exception("审批用户不是业务用户!"));	
			}
			else
				throw GeneralExceptionHandler.Handle(new Exception("任务单据丢失!"));	
		}
		catch(Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);	
		}
		finally
		{
			try {
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				
			} 
		} 
		return userView;
	}
	
	
	
	
	/**
	 * 根据 taskid 获得 模板ID | ins_id
	 * @param task_id
	 * @param conn
	 * @param flag  1:模板id  2：ins_id
	 * @return
	 */
	private String getIdByTask(String task_id,Connection conn,int flag )
	{
		String id="";
		RowSet rowSet=null;
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			String sql="select tabid from t_wf_instance where ins_id=(select ins_id from t_wf_task where task_id="+task_id+" )";
			if(flag==2) // 2：ins_id
				 sql="select ins_id from t_wf_task where task_id="+task_id;
			rowSet=dao.search(sql);
			if(rowSet.next())
				id=rowSet.getString(1);
		}
		catch(Exception e) {
				e.printStackTrace();
			
		}
		finally
		{
			try {
				if(rowSet!=null)
					rowSet.close();
			}
			catch(Exception ee)
			{
				
			}
		} 
		return id;
	}



	public String getTabids() {

	//	if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("gdzy"))
		{
			try
			{
				getXmlConfigDocument();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return this.synTabids;
	}


 

	public String getSynTabids() {
		return synTabids;
	}



	public void setSynTabids(String synTabids) {
		this.synTabids = synTabids;
	}
	
	public HashMap getTabOptMap() {
		return tabOptMap;
	}



	public void setTabOptMap(HashMap tabOptMap) {
		this.tabOptMap = tabOptMap;
	}



	public String getSelfapply() {
		return selfapply;
	}



	public void setSelfapply(String selfapply) {
		this.selfapply = selfapply;
	}
	
}
