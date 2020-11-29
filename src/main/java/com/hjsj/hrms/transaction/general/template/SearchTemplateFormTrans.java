/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.general.template.TemplatePageBo;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;
/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 26, 20065:21:10 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchTemplateFormTrans extends IBusiness {

	/**
	 * 取得权限范围内的人员库列表
	 * @return
	 * @throws GeneralException
	 */
	private String getDbStr(String type) throws GeneralException {
		
		ArrayList dblist=this.userView.getPrivDbList();		
		if (TemplateTableBo.isJobtitleVoteModule(this.userView)){
			dblist=DataDictionary.getDbpreList();
		}
		
		if(type!=null&& "23".equals(type))
		{
			KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(),userView);
			dblist= kqUtilsClass.setKqPerList(null,"2");
		}
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		dblist=dbvo.getDbNameVoList(dblist);
		StringBuffer strdb=new StringBuffer();
		for(int i=0;i<dblist.size();i++)
		{
			RecordVo dbname=(RecordVo)dblist.get(i);
			strdb.append(dbname.getString("pre"));
			strdb.append(",");
		}
		if(strdb.length()>0)
			strdb.setLength(strdb.length()-1);
		return strdb.toString();
	}
	
	/**
	 * 人员调入模板|新增组织单元模板
	 */
	private void autoAddRecord(TemplateTableBo tablebo,String tablename)throws GeneralException
	{
		ContentDAO dao=null;
		try
		{
			if(!(tablebo.getOperationtype()==0||tablebo.getOperationtype()==5))
				return;
            dao=new ContentDAO(this.getFrameconn());
            
			StringBuffer buf=new StringBuffer();
			buf.append("select count(*) as nrec from "); 
			buf.append(tablename);
			RowSet rset=dao.search(buf.toString());
			int irow=0;
			if(rset.next())
				irow=rset.getInt("nrec");
			if(irow!=0)
				return;
			String a0100=null;
			RecordVo vo=new RecordVo(tablename);
            IDGenerator idg=new IDGenerator(2,this.getFrameconn());

			/**
			 * 查找变化前的历史记录单元格
			 * 保存时把这部分单元格的内容
			 * 过滤掉，不作处理
			 * */            
            HashMap sub_map=tablebo.getHisModeSubCell();
			a0100=  idg.getId("rsbd.a0100");
			if(tablebo.getInfor_type()==2||tablebo.getInfor_type()==3)
				a0100="B"+a0100;
			
		/*	if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				if(tablebo.getDest_base()==null||tablebo.getDest_base().length()==0)
					throw new GeneralException("人员调入业务模板未定义目标库!");
				vo.setString("basepre",tablebo.getDest_base());
			}
			else	
				vo.setString("basepre","");*/
			if(tablebo.getInfor_type()==1&&(tablebo.getDest_base()==null||tablebo.getDest_base().length()==0))
				throw new GeneralException("人员调入业务模板未定义目标库!");
			ArrayList dbList=DataDictionary.getDbpreList();
			if(tablebo.getInfor_type()==1)
			{
				vo.setString("a0100",a0100); 
				String dbpre=tablebo.getDest_base();
				for(int i=0;i<dbList.size();i++)
				{
					String pre=(String)dbList.get(i);
					if(pre.equalsIgnoreCase(tablebo.getDest_base()))
						dbpre=pre;
				}
				vo.setString("basepre",dbpre);
				if(vo.hasAttribute("a0101_2"))
				{
					vo.setString("a0101_2", "--");
				}
				if(vo.hasAttribute("a0101_1"))
				{
					vo.setString("a0101_1", "--");
				}
			}
			else
			{
				if(tablebo.getInfor_type()==2)
					vo.setString("b0110",a0100);
				if(tablebo.getInfor_type()==3)
					vo.setString("e01a1",a0100);
				
				
				if(vo.hasAttribute("codeitemdesc_2"))
				{
					vo.setString("codeitemdesc_2", "--");
				}
				if(vo.hasAttribute("codeitemdesc_1"))
				{
					vo.setString("codeitemdesc_1", "--");
				}
			}
			Iterator iterator=sub_map.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String field_name=entry.getKey().toString();
				TemplateSetBo setbo =(TemplateSetBo)entry.getValue();
				
				//根据field_name的值，判断对应的子集是否存在，若不存在抛出异常 20150824 liuzy
				String text_name=setbo.getHz();
				text_name=text_name.substring(text_name.indexOf("{")+1, text_name.lastIndexOf("}"));
				String name=field_name.substring(field_name.indexOf("_")+1, field_name.lastIndexOf("_"));   
				if(!"".equals(name)&&name!=null)
				{
				    FieldSet set = DataDictionary.getFieldSetVo(name.toLowerCase());
				    if(set==null){
				    	throw GeneralExceptionHandler.Handle(new Exception(text_name+ ResourceFactory.getProperty("system.param.sysinfosort.subset.undiscovered")));
				    }
				}
				
				TSubSetDomain setdomain=new TSubSetDomain(setbo.getXml_param());
				String xml=setdomain.outContentxml();
				vo.setString(field_name.toLowerCase(), xml);
			}
			
			this.frowset=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+tablename);
			if(this.frowset.next())
				vo.setInt("a0000", this.frowset.getInt(1));
			
			String seqnum=CreateSequence.getUUID();
			vo.setString("seqnum", seqnum);
            dao.addValueObject(vo);
            if(tablebo.getInfor_type()==1)
            {
	            this.getFormHM().put("a0100", a0100);
	            String dbpre=tablebo.getDest_base();
				for(int i=0;i<dbList.size();i++)
				{
					String pre=(String)dbList.get(i);
					if(pre.equalsIgnoreCase(tablebo.getDest_base()))
						dbpre=pre;
				}
	            this.getFormHM().put("basepre",dbpre);
            }
            else if(tablebo.getInfor_type()==2)
            	this.getFormHM().put("b0110", a0100);
            else if(tablebo.getInfor_type()==3)
            	this.getFormHM().put("e01a1", a0100);
			 if("1".equals(tablebo.getId_gen_manual())){
			            	
			            }else{
			            	tablebo.filloutSequence(a0100, tablebo.getDest_base(), tablename);     
			            }
                  	
          
  
            /**生成序号*/

	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}			
	}
	
	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		String ins_id=(String)this.getFormHM().get("ins_id");
		String sp_batch=(String)this.getFormHM().get("sp_batch");
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String objectId= (String)hm.get("objectId");
			if (objectId==null || objectId.length()<1){				
				this.getFormHM().put("objectId","");
			}
			else {
				this.getFormHM().put("objectId",objectId);
				hm.remove("objectId");
			}
			String pendingType="业务模板"; 
			if("0".equalsIgnoreCase(sp_batch))//单个审批
			{
				if(hm.get("pre_pendingID")!=null)
				{
					this.getFormHM().put("pre_pendingID",(String)hm.get("pre_pendingID"));
					PendingTask imip=new PendingTask();
					//将旧的代办信息置为已阅状态  
					imip.updatePending("T",(String)hm.get("pre_pendingID"),2,pendingType,this.userView); 
					hm.remove("pre_pendingID");
				}
			}
			String  isInitData="1"; //是否需要初始化数据
			if(hm.get("isInitData")!=null)
			{
				isInitData=(String)hm.get("isInitData");
				hm.remove("isInitData");
			}
			if(hm.get("page_num")!=null)
			{
				this.getFormHM().put("page_num",(String)hm.get("page_num"));
				hm.remove("page_num");
			}
			else
				this.getFormHM().put("page_num","1");
			this.userView.getHm().remove("filterStr"); 
			String sp_flag=(String)this.getFormHM().get("sp_flag");
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			ArrayList dbList=DataDictionary.getDbpreList();
			String returnflag=(String)hm.get("returnflag");
			if(tablebo.getInfor_type()==1)
            {
	            this.getFormHM().put("a0100", (String)this.getFormHM().get("a0100")); 
	            String dbpre=tablebo.getDest_base();
				for(int i=0;i<dbList.size();i++)
				{
					String pre=(String)dbList.get(i);
					if(pre.equalsIgnoreCase(tablebo.getDest_base()))
						dbpre=pre;
				}
	            this.getFormHM().put("basepre", dbpre);
            }
            else if(tablebo.getInfor_type()==2)
            	this.getFormHM().put("b0110",  (String)this.getFormHM().get("a0100"));
            else if(tablebo.getInfor_type()==3)
            	this.getFormHM().put("e01a1",  (String)this.getFormHM().get("a0100"));
			if("1".equals(isInitData)&&(returnflag==null||!("list".equalsIgnoreCase(returnflag)|| "listhome".equalsIgnoreCase(returnflag)|| "warnhome".equalsIgnoreCase(returnflag))))
			{
				/**创建或修临时表*/
				/**发起流程时才需要创建临时表,审批环节不用创建临时表*/
				if("0".equalsIgnoreCase(ins_id))
				{
					 if(!tablebo.isCorrect(tabid))
							throw new GeneralException(ResourceFactory.getProperty("template.operation.noResource"));
					 
					//查找模板中是否有多个相同变化后子集
					tablebo.checkIsHaveMutilSub();
					tablebo.createTempTemplateTable(this.userView.getUserName());
					String tablename=this.userView.getUserName()+"templet_"+tabid; 
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					RowSet rowSet=dao.search("select count(*) from "+tablename);
					int n=0;
					if(rowSet.next())
						n=rowSet.getInt(1);
					if(n==0)
					{
						autoAddRecord(tablebo,tablename);
					}
					/**档案中与模板中的数据进行数据同步*/
					tablebo.syncDataFromArchive();
					//解决封板包的问题，后期因注释掉
					if(tablebo.getOperationtype()==0||tablebo.getOperationtype()==5)
						updateSeqNum(tablebo.getInfor_type(),tablebo.getTabid());
					/**发起流程时将task_id=0写入到templateMap中去，否则就会出现发起流程时存在串改流程号的问题**/
					HashMap templateMap=new HashMap();
					templateMap.put("0", PubFunc.encrypt("0"));
					this.getFormHM().put("taskid", "0");
					this.userView.getHm().put("templateMap", templateMap);//将正在使用的task_id放在userview中
				}
				else
				{	
					tablebo.changeSpTableStrut();
					String showCard = hm.get("showCard")==null?"": (String)hm.get("showCard");
					hm.remove("showCard");
					if(tablebo.getOperationtype()!=0&&tablebo.getOperationtype()!=1&&tablebo.getOperationtype()!=2)
					{
					//	tablebo.setImpDataFromArchive_sub(false);
						
						/**新增任务列表,20080418*/
						Pattern pattern = Pattern.compile("[0-9]+");  //20141219 dengcan 汉口银行某种场景下传来的task_id是加密的 
						if("0".equalsIgnoreCase(sp_batch))//单个审批
						{
							/**安全平台改造，将加密的参数解密回来**/
							String task_id=(String)this.getFormHM().get("taskid");
							
						/*
							if(showCard.trim().length()<=0){
								//showCard = 1 代表从列表模式点击编辑图标进来时,这个task_id是没有加密的, 走这个交易类的有两种情况（从几个tableset进来的需要解密,从邮件进来的task_id要解密）
								//另外就是从列表模式进来    这时分两种 一种是直接点列表模式卡片（按钮）     另一种是列表模式点编辑图标
								//直接点卡片按钮时 isInitData = 0,不需要初始化数据。点编辑时需要初始化数据(这样意味着要处理单个人，数据要发生变化)，这里要处理一下
								if(!pattern.matcher(task_id).matches())
									task_id = PubFunc.decrypt(task_id);
							} */
							if(!pattern.matcher(task_id).matches())
								task_id = PubFunc.decrypt(task_id);
							
							HashMap templateMap=new HashMap();
							templateMap.put(task_id, PubFunc.encrypt(task_id));
							this.getFormHM().put("taskid", task_id);//刷新from中的值便于明文使用
							this.userView.getHm().put("templateMap", templateMap);//将正在使用的task_id放在userview中
							ArrayList inslist=new ArrayList();
							inslist.add(ins_id);
							tablebo.setInslist(inslist);
							tablebo.setIns_id(Integer.parseInt(ins_id));
							/**只有卡片模式才能使用附件（卡片中的插入的附件,包含个人附件和公共附件）**/
							HashMap cardAttachMap = new HashMap();
							cardAttachMap.put(ins_id, "1");
							this.userView.getHm().put("cardAttachMap", cardAttachMap);
							tablebo.getTasklist().add(task_id); 
						}
						else//批量审批
						{
							ArrayList tasklist=(ArrayList)this.getFormHM().get("tasklist");

							//String task_ids = (String)this.getFormHM().get("taskid");  //此时的task_id可能是从首页/我的任务 传递过来的 liuzy 20151124
							HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
							String task_ids = (String)map.get("batch_task");
							String homeflag=(String)map.get("homeflag");
							getIns_id(tasklist, task_ids,homeflag);
							//map.put("homeflag", "0");
							
							ArrayList inslist=new ArrayList();
							ArrayList temptasklist = new ArrayList();
							String startflag="0";
							LazyDynaBean dyna=null;
							String _task_id="";
							String _ins_id="";
							HashMap templateMap=new HashMap();
							HashMap cardAttachMap = new HashMap(); 
							PendingTask imip=new PendingTask();
							for(int i=0;i<tasklist.size();i++)
							{
								dyna=(LazyDynaBean)tasklist.get(i);
								_ins_id=(String)dyna.get("ins_id");
								/**将加密的参数解密回来**/
								_task_id=(String)dyna.get("task_id");
								/**只有卡片模式才能使用附件（卡片中的插入的附件,包含个人附件和公共附件）**/
								cardAttachMap.put(_ins_id, "1");
								/*
								if(showCard.trim().length()<=0){
									//showCard = 1 代表从列表模式点击编辑图标进来时,这个task_id是没有加密的, 走这个交易类的有两种情况（从几个tableset进来的需要解密）
									//另外就是从列表模式进来 这时分两种 一种是直接点列表模式卡片 另一种是列表模式点编辑图标
									//直接点卡片按钮时 isInitData = 0,不需要初始化数据。点编辑时需要初始化数据(这样意味着要处理单个人，数据要发生变化)，这里要处理一下
									_task_id=PubFunc.decrypt((String)dyna.get("task_id"));
								}*/
								if(!pattern.matcher(_task_id).matches())
									_task_id=PubFunc.decrypt(_task_id);
								
								/**下一步即将去展现page页面或者list页面，都需要明文展现，所以修改list中bean的值**/
								dyna.set("task_id", _task_id);
								templateMap.put(_task_id, PubFunc.encrypt(_task_id));
								inslist.add(_ins_id);
								temptasklist.add(_task_id);
								String pendingCode="HRMS-"+PubFunc.encrypt(_task_id); 
								imip.updatePending("T",pendingCode,2,pendingType,this.userView); 
					
							}
							
							this.userView.getHm().put("cardAttachMap", cardAttachMap);
							tablebo.setInslist(inslist);
							tablebo.setTasklist(temptasklist);
							this.getFormHM().put("tasklist", tasklist);
							this.userView.getHm().put("templateMap", templateMap);
						}
						/*//因客户的需求，当流程发起以后，无需将档案中与模板中的数据进行数据同步，故屏蔽掉 liuzy 20150907
				        if(sp_flag!=null&&sp_flag.equals("1"))
				        	tablebo.syncDataFromArchive(Integer.parseInt(ins_id),"templet_"+tabid);*/
					}else{//不处于内部调动时，存在安全改造的问题，所以添加上
						Pattern pattern = Pattern.compile("[0-9]+");  //20141219 dengcan 汉口银行某种场景下传来的task_id是加密的 
						if("0".equalsIgnoreCase(sp_batch))//单个审批
						{
							/**安全平台改造，将加密的参数解密回来**/
							String task_id=(String)this.getFormHM().get("taskid");
							
							/*
							if(showCard.trim().length()<=0){
								//showCard = 1 代表从列表模式点击编辑图标进来时,这个task_id是没有加密的, 走这个交易类的有两种情况（从几个tableset进来的需要解密）
								//另外就是从列表模式进来 这时分两种 一种是直接点列表模式卡片 另一种是列表模式点编辑图标
								//直接点卡片按钮时 isInitData = 0,不需要初始化数据。点编辑时需要初始化数据(这样意味着要处理单个人，数据要发生变化)，这里要处理一下
								task_id = PubFunc.decrypt(task_id);
							}*/
							if(!pattern.matcher(task_id).matches())
								task_id = PubFunc.decrypt(task_id);
							
							HashMap templateMap=new HashMap();
							templateMap.put(task_id, PubFunc.encrypt(task_id));
							this.getFormHM().put("taskid", task_id);//刷新from中的值便于明文使用
							this.userView.getHm().put("templateMap", templateMap);//将正在使用的task_id放在userview中
							/**只有卡片模式才能使用附件（卡片中的插入的附件,包含个人附件和公共附件）**/
							HashMap cardAttachMap = new HashMap();
							cardAttachMap.put(ins_id, "1");
							this.userView.getHm().put("cardAttachMap", cardAttachMap);
						}
						else//批量审批
						{
							ArrayList tasklist=(ArrayList)this.getFormHM().get("tasklist");

							//String task_ids = (String)this.getFormHM().get("taskid");  //此时的task_id可能是从首页/我的任务 传递过来的 liuzy 20151124
							HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
							String task_ids = (String)map.get("batch_task");
							String homeflag=(String)map.get("homeflag");
							getIns_id(tasklist, task_ids,homeflag);
							//map.put("homeflag", "0");
							
							LazyDynaBean dyna=null;
							String _task_id="";
							String _ins_id="";
							HashMap templateMap=new HashMap();
							/**只有卡片模式才能使用附件（卡片中的插入的附件,包含个人附件和公共附件）**/
							HashMap cardAttachMap = new HashMap();
							
							for(int i=0;i<tasklist.size();i++)
							{
								dyna=(LazyDynaBean)tasklist.get(i);
								/**将加密的参数解密回来**/
								_task_id =(String)dyna.get("task_id");
								_ins_id =(String)dyna.get("ins_id");
								/*
								if(showCard.trim().length()<=0){
									//showCard = 1 代表从列表模式点击编辑图标进来时,这个task_id是没有加密的, 走这个交易类的有两种情况（从几个tableset进来的需要解密）
									//另外就是从列表模式进来 这时分两种 一种是直接点列表模式卡片 另一种是列表模式点编辑图标
									//直接点卡片按钮时 isInitData = 0,不需要初始化数据。点编辑时需要初始化数据(这样意味着要处理单个人，数据要发生变化)，这里要处理一下
									_task_id=PubFunc.decrypt((String)dyna.get("task_id"));
								}*/
								if(!pattern.matcher(_task_id).matches())
									_task_id=PubFunc.decrypt(_task_id);
								/**下一步即将去展现page页面或者list页面，都需要明文展现，所以修改list中bean的值**/
								dyna.set("task_id", _task_id);
								/**将批量审批的每个ins_id加入到map中**/
								cardAttachMap.put(_ins_id, "1");
								templateMap.put(_task_id, PubFunc.encrypt(_task_id));
							}
							this.userView.getHm().put("cardAttachMap", cardAttachMap);
							this.getFormHM().put("tasklist", tasklist);
							this.userView.getHm().put("templateMap", templateMap);
						}
					}
				}
			}//以上内容都是需要初始化的,不需要初始化时,进入卡片模式时都应该是发起的流程ins_id,以及task_id都为0
				
			
			ArrayList list=tablebo.getAllTemplatePage();//得到模板页
			ArrayList outlist=new ArrayList();
			ArrayList noprintlist=new ArrayList();
			ArrayList mobileList=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				TemplatePageBo pagebo=(TemplatePageBo)list.get(i);
				//if(pagebo.isIsprint())
				//	outlist.add(pagebo);
				//else
				//   noprintlist.add(pagebo); //以前考核意见审批表,用不打印的表格来处理，这种方式有点久妥
			//#模板是否显示不打印页
				if(!pagebo.isShow())
					continue;
				//bs端如果isMobile为1,则不显示 1代表专门为手机端做的模板页
				if("1".equals(pagebo.getIsMobile())){
					mobileList.add(pagebo);
				    continue;
				}
				outlist.add(pagebo);
			}
			if (outlist.size()<1){
				/*if (mobileList.size()>0) {//有手机用的模板，取手机模板 wangrd 2015-05-27
                    outlist= mobileList;
                }
                else 
                  throw new GeneralException("此模板的页签显示设置错误！");*/
				//没有电脑页提示用户检查。
                  throw new GeneralException("您无权查看和使用模板!请检查设置 ①此页是否打印 ②此页是否适用手机 ③子集&指标权限!");
            }
			
			String type=(String)this.getFormHM().get("type");
			if(hm.get("type")!=null)
			{
				type=(String)hm.get("type");
				this.getFormHM().put("type",type);
				hm.remove("type");
			}
			if(type!=null&&type.length()>0&&type.charAt(0)=='t') //如果是个性化指定只显示某个模板的任务  &type=t48,49,41  20150411 dengcan
			{
				
			}
			else if(type==null||(tablebo.get_static()==10&&!"10".equals(type))||(tablebo.get_static()==11&&!"11".equals(type)))
					this.getFormHM().put("type",String.valueOf(tablebo.get_static()));
			this.getFormHM().put("_static",String.valueOf(tablebo.get_static()));
			this.getFormHM().put("infor_type",String.valueOf(tablebo.getInfor_type()));
			this.getFormHM().put("pagelist",outlist);
			this.getFormHM().put("name",tablebo.getName());
			this.getFormHM().put("operationtype",String.valueOf(tablebo.getOperationtype()));
			this.getFormHM().put("dbpres",getDbStr(type));
			this.getFormHM().put("noprintlist",noprintlist);
			this.getFormHM().put("num", "0");
		}
		catch(Exception ex)
		{
			 
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{ 
				String tablename="templet_"+tabid;
				if(ins_id==null|| "0".equalsIgnoreCase(ins_id))
					tablename=this.userView.getUserName()+"templet_"+tabid;
				PubFunc.resolve8060(this.getFrameconn(),tablename);
				throw GeneralExceptionHandler.Handle(new Exception("数据结构已同步，请重新进入此模块!"));
			}
			else
				throw GeneralExceptionHandler.Handle(ex);
			
			
		}
	}
	
	/**
	 * 对人员调入模板业务，需要升级，前台人员列表姓名才不为空
	 * @throws GeneralException
	 */
	private void updateSeqNum(int infor_type,int tabid)throws GeneralException
	{
		String strDesT=null;
		 
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			 
			
			strDesT=this.userView.getUserName()+"templet_"+tabid; 
			String sql="select * from "+strDesT ;
			RowSet rowSet=dao.search(sql);
			while(rowSet.next()){
				
				String	seqnum = rowSet.getString("seqnum");
				if(seqnum==null||seqnum.trim().length()==0){
					seqnum=CreateSequence.getUUID();
					if(infor_type==1) 
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where a0100='"+rowSet.getString("a0100")+"' and  lower(basepre)='"+rowSet.getString("basepre").toLowerCase()+"'");
					}
					else if(infor_type==2)
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where b0110='"+rowSet.getString("b0110")+"'");
					}
					else if(infor_type==3)
					{
						dao.update("update "+strDesT+" set seqnum='"+seqnum+"' where E01A1='"+rowSet.getString("E01A1")+"'");
					}
				}
			}
			rowSet.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	/**
	 * 根据task_id的值查询出ins_id和一些值，放入任务列表中
	 * @param tasklist 任务列表
	 * @param task_ids 任务编号，以","分割组成
	 */
	private void getIns_id(ArrayList tasklist,String task_ids,String homeflag){
		try {
			Pattern pattern = Pattern.compile("[0-9]+");  //20141219 dengcan 汉口银行某种场景下传来的task_id是加密的 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			//System.out.println(task_ids.length());
			if(task_ids!=null && task_ids.length()>0){
				if(!pattern.matcher(task_ids).matches())
					task_ids = PubFunc.decrypt(task_ids);
				if("1".equals(homeflag) && task_ids.indexOf(",")!=-1){
					if(task_ids.indexOf(",")==0){
						task_ids=task_ids.substring(1,task_ids.length());
					}
					tasklist.clear();
					String []task_idArr=task_ids.split(",");
					//System.out.println(task_id);
					for(int i=0;i<task_idArr.length;i++){
						int task_id=Integer.parseInt(task_idArr[i]);
						RecordVo vo=new RecordVo("t_wf_task");
						vo.setInt("task_id", task_id);
						vo=dao.findByPrimaryKey(vo);
						LazyDynaBean dyna=new LazyDynaBean();
						dyna.set("ins_id", vo.getString("ins_id"));
						dyna.set("task_id", PubFunc.encrypt(task_id+""));
						dyna.set("node_id", vo.getString("node_id"));
						dyna.set("a0101_1", vo.getString("a0101_1"));
						dyna.set("task_topic", vo.getString("task_topic"));
						tasklist.add(dyna);
					}
				}
			}
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
