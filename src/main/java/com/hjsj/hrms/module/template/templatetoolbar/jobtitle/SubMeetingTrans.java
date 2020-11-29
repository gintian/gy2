/**
 * 
 */
package com.hjsj.hrms.module.template.templatetoolbar.jobtitle;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.jobtitle.configfile.transaction.DomXml;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.template.templatetoolbar.printout.businessobject.OutPutModelBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.io.File;
import java.util.*;

/**
 * <p>Title:SubMeetingTrans.java</p>
 * <p>Description>:职称评审-上会</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2016-4-18 上午09:43:34</p>
 * <p>@author:wangrd</p>
 * <p>@version: 7.0</p>
 */
public class SubMeetingTrans extends IBusiness {
	private String jobtitleCodeSetid = "";//不同职位模板的代码型不同。chent 20170413
	@Override
    public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String w0301=(String)hm.get("w0301");
		//haosl add 上会时，需要将高评委和二级单位关联的评委会关联到上会信息中
		String sub_committee_id = hm.get("sub_committee_id")==null?"":(String)hm.get("sub_committee_id");
		if(StringUtils.isNotBlank(sub_committee_id))
			sub_committee_id = PubFunc.decrypt(sub_committee_id);
		w0301 = PubFunc.decrypt(w0301);
		String tabid=(String)hm.get("tabid");
		String ins_id=(String)hm.get("ins_id");
		String taskid=(String)(String)hm.get("taskid");
		String sp_batch=(String)hm.get("sp_batch");
		String batch_task=(String)hm.get("batch_task");
        if(sp_batch==null|| "".equals(sp_batch))
            sp_batch="0";//单个任务审批
        if (batch_task==null || batch_task.length()<1){
            batch_task=taskid;
        }
        
        String module_id="";
        String infor_type = "1";
        if (hm.get("tab_id")!=null){//新版人事异动调用
            TemplateFrontProperty frontProperty = new TemplateFrontProperty(this.getFormHM());
            tabid = frontProperty.getTabId();
            taskid = frontProperty.getTaskId();
            batch_task= taskid;
            module_id = frontProperty.getModuleId();
            infor_type = frontProperty.getInforType();
            boolean bBatchApprove = frontProperty.isBatchApprove();
            sp_batch="0";
            if (bBatchApprove){
                sp_batch="1";
            }
        }
        
		try
		{
	        ArrayList tasklist=new ArrayList();//所有要处理的任务	
			ContentDAO dao=new ContentDAO(this.frameconn);
			ArrayList tmpTasklist=getTaskList(batch_task);		
			if("1".equals(sp_batch))
			{
				for(int i=0;i<tmpTasklist.size();i++)
	            {
				    RecordVo taskvo= (RecordVo)tmpTasklist.get(i);
	                taskid=taskvo.getString("task_id");
	                if (isSelectedTaskId(dao, tabid, taskid)){
	                    tasklist.add(taskvo); 
	                }
	            }    
			}
			else//单任务审批
			{
			    for(int i=0;i<tmpTasklist.size();i++)
                {
                    RecordVo taskvo= (RecordVo)tmpTasklist.get(i);
                    ins_id= taskvo.getString("ins_id");
                    tasklist.add(taskvo); 
                } 
			}
			boolean bW0535=false;//评审材料模板
			boolean bW0537=false;//送审论文模板
			DomXml	domXml = new DomXml();
			String templateId= ","+domXml.getJobtitleTemplateByType(this.frameconn, "5")+",";
			if (templateId.contains(tabid)){
				bW0537=true;
			}
			templateId= ","+domXml.getJobtitleTemplateByType(this.frameconn, "6")+",";
			if (templateId.contains(tabid)){
				bW0535=true;
			}		

			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			if(!"1".equals(sp_batch)){
				if (!tablebo.taskIsMatchedInstance(Integer.parseInt(ins_id), Integer.parseInt(taskid))){
					Category.getInstance(this.getClass()).error("存在串号：ins_id"+ins_id+" taskid："+taskid);
					throw new GeneralException("检测到浏览器异常，请关闭所有浏览器重新登录审批！");
				}
			}
			DbWizard dbw=new DbWizard(this.frameconn);
			ArrayList fieldlist=tablebo.getAllFieldItem();	
			HashMap updateMap=getUpdateFieldsMap(fieldlist);
			
			List<String> addRecordList = new ArrayList<String>();//记录新添加的记录。后面更新指标时，如果是新添加的才更新；如果是老的数据就不更新了。chent 20170801
			
			//复制记录
			String srcTab="templet_"+Integer.parseInt(tabid);		
			IDGenerator idg = new IDGenerator(2, this.frameconn);
			for(int i=0;i<tasklist.size();i++)
			{
				ins_id=((RecordVo)tasklist.get(i)).getString("ins_id");
				taskid=((RecordVo)tasklist.get(i)).getString("task_id");
				
				String addr ="/general/template/edit_form.do?b_query=link"
					+"&tabid="+tabid+"&ins_id="+ins_id+"&taskid="+PubFunc.encrypt(taskid)
					+"&sp_flag=2&returnflag=noback"
					+"&taskid_validate="+ReviewFileBo.createTaskidValidCode(taskid);
				//以后改成如下。
				//String addr ="tabid="+tabid+"&taskid="+PubFunc.encrypt(taskid);
				//新增记录
				StringBuffer sbsql = new StringBuffer("");			
				sbsql.append("select * from ");
				sbsql.append(srcTab);
				sbsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
				sbsql.append("  and task_id="+taskid+"  and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")
						+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
				sbsql.append(" and not exists (select null from w05  where upper("
						+srcTab+".basepre)=upper(w05.w0503) and "+srcTab
						+".a0100=w05.w0505 and w05.w0301= '"+w0301+"')");				
				
				RowSet rSet =dao.search(sbsql.toString(),new ArrayList());
				while (rSet.next())
				{
					String a0100= rSet.getString("a0100");
					String nbase= rSet.getString("basepre");					
					RecordVo w05Vo=new RecordVo("w05");
					String  w0501 = idg.getId("W05.W0501");
					w05Vo.setString("w0501", w0501);
					w05Vo.setString("w0505",a0100);
					if(StringUtils.isNotBlank(sub_committee_id))
						w05Vo.setString("w0561",sub_committee_id);
					w05Vo.setString("w0503",nbase);						
					w05Vo.setString("w0301",w0301);//评审会议ID
					w05Vo.setString("w0525","0000");//导入标识
					w05Vo.setInt("w0517",0);//专家人数(评委会)
					w05Vo.setInt("w0519",0);//参会人数(评委会)
					w05Vo.setInt("w0549",0);//反对人数(评委会)
					w05Vo.setInt("w0551",0);//弃权人数(评委会)
					w05Vo.setInt("w0553",0);//赞成人数(评委会)
					w05Vo.setInt("w0521",0);//专家人数(学科组)
					w05Vo.setInt("w0543",0);//反对人数(学科组)
					w05Vo.setInt("w0545",0);//弃权人数(学科组)
					w05Vo.setInt("w0547",0);//赞成人数(学科组)
					w05Vo.setInt("w0523",0);//专家人数(同行专家)
					w05Vo.setInt("w0527",0);//反对人数(同行专家)
					w05Vo.setInt("w0529",0);//弃权人数(同行专家)
					w05Vo.setInt("w0531",0);//赞成人数(同行专家)
					w05Vo.setInt("w0571",0);//专家人数(二级单位)
					w05Vo.setInt("w0563",0);//反对人数(二级单位)
					w05Vo.setInt("w0565",0);//弃权人数(二级单位)
					w05Vo.setInt("w0567",0);//赞成人数(二级单位)
					w05Vo.setDate("create_time",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
					w05Vo.setString("create_user",this.userView.getS_userName());                
					w05Vo.setString("create_fullname",this.userView.getUserFullName());  
					if (dao.addValueObject(w05Vo)>=0){
						addRecordList.add(nbase+a0100);//新添加的记录，加入list chent 20170801
						//更新 b0110 e0122 a0101
						String usrTab=nbase+"A01";
						String strJoin = "w05.w0505="+usrTab+".a0100";
						String strSet = "w05.w0507="+usrTab+".b0110"+"`"
						               +"w05.w0509="+usrTab+".e0122"+"`"
						               +"w05.w0511="+usrTab+".a0101";
						dbw.updateRecord("w05", usrTab,strJoin,strSet ,
						        "w05.w0505='"+a0100+"' and w05.w0503='"+nbase+"'",
						        usrTab+".a0100='"+a0100+"'"
						        
						        );
					}
				}

				//更新 
				sbsql.setLength(0);		
				sbsql.append("select * from ");
				sbsql.append(srcTab);
				sbsql.append(" where  exists (select null from t_wf_task_objlink where "+srcTab+".seqnum=t_wf_task_objlink.seqnum and "+srcTab+".ins_id=t_wf_task_objlink.ins_id ");
				sbsql.append("  and task_id="+taskid+"  and submitflag=1  and (state is null or  state=0 ) and ("+Sql_switcher.isnull("special_node","0")
						+"=0  or ( "+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ");
				
				rSet =dao.search(sbsql.toString(),new ArrayList());
				List<Map<String,String>> w0505List = new ArrayList<Map<String,String>>();
				HashMap<String,String> map = null;
				while (rSet.next())
				{
					String a0100= rSet.getString("a0100");
					String nbase= rSet.getString("basepre");					
					
					//if(addRecordList.contains(nbase+a0100)) {//是新添加的，才更新.曾经上会过的人就不更新 chent 20170801
						String w0535addr="";
						String w0537addr="";
						if (bW0535){
							w0535addr=addr;
						}
						if (bW0537){
							w0537addr=addr;
						}
						//更新已存在的记录
						String updateSql =getChangeUpdateSQL(srcTab,ins_id,w0301,updateMap,w0535addr,w0537addr,nbase,a0100,sub_committee_id);
						dao.update(updateSql);		
						map = new HashMap<String,String>();
						map.put("w0505", a0100);
						map.put("w0503",nbase);
						w0505List.add(map);
					//}
					
	                
				}
				//同步投票数据
				this.syncW05Data(w0301,w0505List);
				//获得公示、投票环节显示申报材料表单上传的word模板内容参数设置 将文件路径存到W0536中
				this.saveTemplateDataToW0536(tabid,tmpTasklist,module_id,infor_type,taskid,w0301);
			} 
		}

		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 获得公示、投票环节显示申报材料表单上传的word模板内容参数设置 将文件路径存到W0536中
	 * @param tabid
	 * @param tmpTasklist
	 * @param module_id
	 * @param infor_type
	 * @param taskid
	 * @param w0301
	 * @throws GeneralException
	 */
	private void saveTemplateDataToW0536(String tabid, ArrayList tmpTasklist, String module_id, String infor_type, String taskid, String w0301) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rst = null;
		try{
			ConstantXml constantXml = new ConstantXml(this.frameconn,"JOBTITLE_CONFIG");
	        String support_word= constantXml.getTextValue("/params/support_word");
	        constantXml = new ConstantXml(this.frameconn,"FILEPATH_PARAM");
	        String rootpath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
	        rootpath=rootpath.replace("\\",File.separator);          
	    	if (!rootpath.endsWith(File.separator)) 
	    		rootpath =rootpath+File.separator;
	        if("true".equalsIgnoreCase(support_word)){
	        	ArrayList paramList = new ArrayList();
	        	RecordVo vo = new RecordVo("template_table");
				vo.setInt("tabid", Integer.parseInt(tabid));
				vo=dao.findByPrimaryKey(vo);
				String tabname=vo.getString("name");
	        	OutPutModelBo opmBo = new OutPutModelBo(this.frameconn,this.userView);
	        	String selfapply = "0";
	        	if("9".equals(module_id))
	        		selfapply = "1";
	        	String insid = "";
	        	for(int j=0;j<tmpTasklist.size();j++)
	            {
	                RecordVo taskvo= (RecordVo)tmpTasklist.get(j);
	                String insid_= taskvo.getString("ins_id");
	                if(j==0)
	                	insid+=insid_;
	                else
	                	insid+=","+insid_;
	            }
	        	ArrayList fileList = opmBo.outPutModel(insid, taskid, null, null, selfapply, infor_type, tabid, "1");
	        	for(int k=0;k<fileList.size();k++){
	        		ArrayList param = new ArrayList();
	        		HashMap filemap = (HashMap)fileList.get(k);
	        		Set keySet = filemap.keySet();
					Iterator iterator = keySet.iterator();
					String filepath_ = "";
	        		String objectid = "";
					while (iterator.hasNext()) {
						objectid = iterator.next().toString();
						filepath_ = filemap.get(objectid).toString();
					}
					//查找人员对应的W0501
					ArrayList searchparam = new ArrayList();
					StringBuffer sb = new StringBuffer("");
					String W0501 = "";
					sb.append("select w0501 from w05 where lower(w0503"+Sql_switcher.concat()+"w0505)=? and w0301=?");
					searchparam.add(objectid.toLowerCase());
					searchparam.add(w0301);
					rst = dao.search(sb.toString(), searchparam);
					if(rst.next()){
						W0501 = rst.getString("w0501");
					}
	        		//\ reviewMaterial \ meeting_会议ID\ 模板名称_W0501.pdf
	        		String filepath = rootpath+"reviewMaterial"+File.separator+"meeting_"+w0301+File.separator+tabname+"_"+W0501+".pdf";
	        		FileUtils.copyFile(new File(filepath_), new File(filepath));
	        		File oldfile = new File(filepath_);
		      		  if(oldfile.exists())
		      			oldfile.delete();
	        		RecordVo recordVo=new RecordVo("w05");
		        	recordVo.setString("w0501", W0501);
		        	recordVo.setString("w0536", SafeCode.encode(PubFunc.encrypt(filepath)));
		        	dao.updateValueObject(recordVo);
	        	}
        }
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rst);
		}
	}
	/**
	 * 基于最新需求的优化：
	 * 校级评审会议在创建时仅需考虑启用学科组、高评委评审两个阶段，
	 * 但在上会材料处要参考申报人在院级评审会议中产生的二级单位评议组、同行专家投票结果数据，
	 * 程序在此处特殊处理，当会议启用了高评委即使没有选择“二级单位评议”、“同行专家”投票阶段，
	 * 在上会材料处仍显示相关环节的投票情况，此处的投票数据在上会时引自申报人同年
	 * 其它已结束的评审会议启用了该阶段的数据。
	 * @throws GeneralException 
	 */
	private void syncW05Data(String newW0301,List<Map<String,String>> personList) throws GeneralException {
		//获得当前年度
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		RowSet rs2 = null;
		try {
			if(personList.size()==0)
				return;
			//得到要更新的列，list的最后一列为导入标识（将同步标识用导入标识表示）
			List<String> fields = this.needSyncModifyFields(newW0301);
			StringBuffer sql = new StringBuffer();
			//查询 最新上会 的人员编号
			List<String> values = new ArrayList<String>();	
			List<List> updateList = new ArrayList<List>();
			for(Map<String,String> map:personList){
				String w0505 = map.get("w0505");
				String w0503 = map.get("w0503");
				
				values.clear();
				sql.setLength(0);
				//查询需要更新的申报人的w0501
				sql.append("select w0501 from w05 where w0505 = ? and w0503= ? and w0301=?");
				values.add(w0505);
				values.add(w0503);
				values.add(newW0301);
				rs = dao.search(sql.toString(),values);
				
				//根据人员编号 查询当前年度上会记录  用以同步最新上会数据
				if(rs.next()){
					String w0501 = rs.getString("w0501");
					sql.setLength(0);
					sql.append("select W05.* from W05,W03 where w0505 = ? and w0503= ? and ");
					sql.append(Sql_switcher.diffYears("w05.create_time",Sql_switcher.today())+"=0 ");//本年度
					sql.append("and w05.W0301=w03.W0301 ");
					sql.append("and W05.w0301<>? and W03.W0321='06' ");
					sql.append("order by W05.create_time desc");
					
					values.clear();
					values.add(w0505);
					values.add(w0503);
					values.add(newW0301);
					rs2 = dao.search(sql.toString(),values);
					
					List list = new ArrayList();
					if(rs2.next()){
						for(int i=0;i<fields.size();i++){
							if(i==fields.size()-1){
								list.add(fields.get(i));
							}else{
								String cName = fields.get(i);//列名
								if("w0561".equalsIgnoreCase(cName)
										|| "group_id".equals(cName))
									list.add(rs2.getString(cName));
								else
									list.add(rs2.getInt(cName));
							}
						}
						list.add(w0501);
						updateList.add(list);
					}
						
				}
			}
			//更新数据
			if(fields.size()>0){
				StringBuffer upSql = new StringBuffer("update W05 set ");
				for(int i=0;i<fields.size();i++){
					if(i==fields.size()-1){
						upSql.append("W0525=?");//导入标识
					}else{
						String cName = fields.get(i);//列名
						upSql.append(cName+"=?,");
					}
					
				}
				upSql.append(" where w0501=?");
				dao.batchUpdate(upSql.toString(), updateList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rs2);
			PubFunc.closeDbObj(rs);
		}
	}
	/**
	 * 获得需要同步的字段
	 *  	W0517：评委人数 （总）<br />
	 *		W0521：学科组人数（总）<br />
	 *		W0523：外部专家人数（总）<br />
	 *		W0571：学院任聘组人数（总）<br />
     *
 	 *		W0531：外部专家赞成人数<br />
	 *		W0527：外部专家反对人数<br />
	 *		W0529：外部专家弃权人数<br />
	 *		
	 *		W0547：学科组赞成人数<br />
	 *		W0543：学科组反对人数<br />
	 *		W0545：学科组赞成人数<br />
	 *		
	 *		W0553：评委会赞成人数<br />
	 *		W0549：评委会反对人数<br />
	 *		W0551：评委会弃权人数<br />
	 *		
	 *		W0567：二级单位赞成人数<br />
	 *		W0563：二级单位反对人数<br />
	 *		W0565：二级单位弃权人数<br />
	 * @author haosl
	 * @throws GeneralException 
	 */
	private List<String> needSyncModifyFields(String w0301) throws GeneralException{
		List<String> fields = new ArrayList<String>();
		ContentDAO dao = new ContentDAO(this.getFrameconn());//
		RowSet rs = null;
		String[] sync = {"0","0","0","0"};
		List<Integer> list = new ArrayList<Integer>();//保存没有启用的评议组
		try {
			//二级单位 和高评委
			StringBuffer sql = new StringBuffer();
			sql.append("select w0315,w0323,w0325 from w03 where w0301 ='"+w0301+"'");
			rs = dao.search(sql.toString());
			if(rs.next()){
				int w0315 = rs.getInt("w0315");//高评委
				int w0323 = rs.getInt("w0323");//二级单位
				String w0325 = rs.getString("w0325");//w0325是否启用同行阶段 1 启用  ;null| 2不启用
				if(w0315==0){//没有启用高评委
					fields.add("W0517");
					fields.add("W0553");
					fields.add("W0549");
					fields.add("W0551");
					sync[2]="1";
				}if(w0323==0){//没有启用二级单位
					fields.add("W0561");
					fields.add("W0571");
					fields.add("W0567");
					fields.add("W0563");
					fields.add("W0565");
					sync[3]="1";
				}if(!StringUtils.equals(w0325, "1")){//没有启用同行评议组
					fields.add("W0523");
					fields.add("W0531");
					fields.add("W0527");
					fields.add("W0529");
					sync[0]="1";
				}
			}
			//学科组
			sql.setLength(0);
			sql.append("select distinct(group_id) from zc_expert_user where w0301 ='"+w0301+"' and type=2");
			rs.close();
			rs = dao.search(sql.toString());
			if(!rs.next()){//没有数据 证明没有启用学科组评议
				fields.add("group_id");
				fields.add("W0521");
				fields.add("W0547");
				fields.add("W0543");
				fields.add("W0545");
				sync[1]="1";
			}
			//将导入标识作为同步标识，该标识放到list的最后一列
			String syncTemp = StringUtils.join(sync);
			if(!"0000".equals(syncTemp))
				fields.add(StringUtils.join(sync));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return fields;
	}
	/** 
	* @Title: getUpdateFieldsMap 
	* @Description: 获取需要更新的字段对应更新  放在map里面，目标字段=源字段
	* @param @param fieldlist
	* @param @return
	* @param @throws GeneralException
	* @return HashMap
	*/ 
	public HashMap getUpdateFieldsMap(ArrayList fieldlist)throws GeneralException
	{
		String w0513Fld="";//现聘职称
		String w0515Fld="";//申报职称名称
		HashMap map=new HashMap();
		try
		{
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem item=(FieldItem)fieldlist.get(i);
				String fieldname=item.getItemid();
				String codesetid = item.getCodesetid();
				if ("AJ".equalsIgnoreCase(codesetid) || "YM".equalsIgnoreCase(codesetid) || "YL".equalsIgnoreCase(codesetid) || "YN".equalsIgnoreCase(codesetid)){//不同职位模板的代码型不同，有哪个取哪个。chent 20170413
					this.jobtitleCodeSetid = item.getCodesetid();
					if (item.isChangeBefore()){
						w0513Fld=fieldname+"_1";
					}
					else if (item.isChangeAfter()){
						w0515Fld+=","+fieldname+"_2";
					}
				}else {	
					FieldItem w05Item = DataDictionary.getFieldItem(fieldname, "w05");
					if ("b0110".equalsIgnoreCase(fieldname)){
						w05Item = DataDictionary.getFieldItem("w0507", "w05");
					} 
					else if ("e0122".equalsIgnoreCase(fieldname)){
						w05Item = DataDictionary.getFieldItem("w0509", "w05");
					}  
					else if ("a0101".equalsIgnoreCase(fieldname)){
						w05Item = DataDictionary.getFieldItem("w0511", "w05");
					}  
					if (w05Item!=null){
						String w05Itemid=w05Item.getItemid();
						if (item.isChangeAfter()){// 变化后指标优先 
							map.put(w05Itemid, fieldname+"_2");
						}
						else {
							//如果有变化后指标，则不使用变化前指标w05 先查一下
							boolean bExists=false;
							Iterator it = map.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry entry = (Map.Entry) it.next();
								Object key = entry.getKey();
								if (w05Itemid.equals((String)key)){
									bExists=true;
									break;
								}
							}
							if (!bExists){//不存在变化后指标
								map.put(w05Itemid, fieldname+"_1");
							}
						}
					}
				}			
			}
			if (w0513Fld.length()>0){
				map.put("w0513", w0513Fld);
			}
			if (w0515Fld.length()>0){
				map.put("w0515", w0515Fld.substring(1));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return map;
	}
	
	
	
	/**
	 * 当模板关联AJ代码的变化后指标超过一个时，需特殊处理
	 * @param destFld 目标表指标
	 * @param srcFld   源表指标
	 * @return
	 */
	private String getSrcFld(String destFld,String srcFld,String srcTab)
	{ 
		String tempStr="";
		if("w0513".equalsIgnoreCase(destFld)){//不同职位模板的代码型不同。现聘职称和申报职称直接显示文字。chent 20170413
			tempStr = "(select codeitemdesc From codeitem where codesetid='"+this.jobtitleCodeSetid+"' and codeitemid="+srcFld+")";
			
		} else if("w0515".equalsIgnoreCase(destFld)) {
			String[] temps = srcFld.split(",");
			int temps_length = temps.length;
			if(temps_length == 1) {
				tempStr = "(select codeitemdesc From codeitem where codesetid='"+this.jobtitleCodeSetid+"' and codeitemid="+srcFld+")";
			} else {
				for(int i=0; i<temps_length; i++) {
					if(temps[i].trim().length()>0) {
						if(i == 0) {
							tempStr+=" case ";
						}
						tempStr+="  when nullif("+srcTab+"."+temps[i]+",'') is not null then  (select codeitemdesc From codeitem where codesetid='"+this.jobtitleCodeSetid+"' and codeitemid="+temps[i]+")";
					}
				} 
				tempStr+=" end ";
			}
			
		} else {
			tempStr=srcTab+"."+srcFld;
		}
		
		
		return tempStr;
	}
	
	
	
	/** 
	* @Title: getChangeUpdateSQL 
	* @Description: 获取更新sql
	* @param @param srcTab 源表
	* @param @param w0513Fld  现聘职称
	* @param @param w0515Fld 申报职称
	* @param @param ins_id
	* @param @param w0301 会议id
	* @param @return
	* @return String
	*/ 
	private String getChangeUpdateSQL(String srcTab,String ins_id,String w0301,
			HashMap updateMap,String w0535addr, String w0537addr,String w0503,String w0505,String w0561)
	{
		int db_type=Sql_switcher.searchDbServer();//数据库类型		
		String destTab="w05";
		StringBuffer strsql=new StringBuffer();
		if(db_type==2||db_type==3)
		{
		
			String srcUpdateFlds="";
			String destUpdateFlds="";
			Iterator it = updateMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String destFld = (String)entry.getKey();
				String srcFld = (String)entry.getValue();
				if (srcUpdateFlds.length()>0){
					srcUpdateFlds=srcUpdateFlds+",";
					destUpdateFlds=destUpdateFlds+",";
				}
				srcFld=getSrcFld(destFld,srcFld,srcTab);
				srcUpdateFlds=srcUpdateFlds+srcFld;
				destUpdateFlds=destUpdateFlds + destTab+"."+destFld;
			}
			if (w0535addr.length()>0){
				if (srcUpdateFlds.length()>0){
					srcUpdateFlds=srcUpdateFlds+",";
					destUpdateFlds=destUpdateFlds+",";
				}
				destUpdateFlds=destUpdateFlds+ destTab+".w0535";
				srcUpdateFlds=srcUpdateFlds+"'"+w0535addr+"'";
			}
			if (w0537addr.length()>0){
				if (srcUpdateFlds.length()>0){
					srcUpdateFlds=srcUpdateFlds+",";
					destUpdateFlds=destUpdateFlds+",";
				}
				destUpdateFlds=destUpdateFlds+ destTab+".w0537";
				srcUpdateFlds=srcUpdateFlds+"'"+w0537addr+"'";
			}
			
			strsql.append("update w05 set  (w05.w0561,");
			strsql.append(destUpdateFlds+")=(select '"+w0561+"', "+srcUpdateFlds);
			strsql.append(" from ");
			strsql.append(srcTab);
			strsql.append(" where ");
			strsql.append("upper(basepre)=upper("+destTab+".w0503)");
			strsql.append(" and a0100="+destTab+".w0505");
			strsql.append(" and  ins_id="+ins_id);
			strsql.append(") where w0301 ='"+w0301+"'");;
			strsql.append(" and  w0503 ='"+w0503+"'");
			strsql.append(" and  w0505 ='"+w0505+"'");
		}
		else
		{
			String strupdate="";
			Iterator it = updateMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String destFld = (String)entry.getKey();
				String srcFld = (String)entry.getValue();
				srcFld=getSrcFld(destFld,srcFld,srcTab);
				if (strupdate.length()>0){
					strupdate=strupdate+",";
				}
				strupdate =strupdate+destTab+"."+destFld+"="+srcFld;
			}			
			if (w0535addr.length()>0){
				if (strupdate.length()>0){
					strupdate=strupdate+",";
				}
				strupdate =strupdate+destTab+".w0535"+"="+"'"+w0535addr+"'";
			}
			if (w0537addr.length()>0){
				if (strupdate.length()>0){
					strupdate=strupdate+",";
				}
				strupdate =strupdate+destTab+".w0537"+"="+"'"+w0537addr+"'";
			}
			
			strsql.append("update w05 set w0561='"+w0561+"', ");
			strsql.append(strupdate);
			strsql.append(" from w05 ");
			strsql.append(" left join ");
			strsql.append(srcTab);
			strsql.append(" on ");
			strsql.append("w05.w0503="+srcTab+".basepre");
			strsql.append(" and w05.w0505="+srcTab+".a0100");
			strsql.append(" where ");
			strsql.append("w05.w0301 ='"+w0301+"'");
            strsql.append(" and  w05.w0503 ='"+w0503+"'");
            strsql.append(" and  w05.w0505 ='"+w0505+"'");
			strsql.append(" and "+srcTab+".ins_id=");
			strsql.append(ins_id);
			
		} 
		return strsql.toString();
	}

	
	private ArrayList getTaskList(String batch_task)throws GeneralException
	{
		ArrayList tasklist=new ArrayList();
		String[] lists=StringUtils.split(batch_task,",");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select * from t_wf_task where task_id in (");
		HashMap templateMap =(HashMap) this.userView.getHm().get("templateMap");
		for(int i=0;i<lists.length;i++)
		{
			if(i!=0)
				strsql.append(",");
			strsql.append(lists[i]);
		}
		strsql.append(")");
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
			{
				RecordVo taskvo=new RecordVo("t_wf_task");
				taskvo.setInt("task_id",rset.getInt("task_id"));
				taskvo.setInt("ins_id",rset.getInt("ins_id"));
				tasklist.add(taskvo);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return tasklist;
	}
	
	/**   
	 * @Title: isSelectedTaskId   
	 * @Description: 判断单据里面的记录是否被选中，如果没有选中的，则后续不处理   
	 * @param @param dao
	 * @param @param tabid
	 * @param @param task_id
	 * @param @return
	 * @param @throws GeneralException 
	 * @return boolean 
	 * @author:wangrd   
	 * @throws   
	*/
	private boolean isSelectedTaskId(ContentDAO dao,String tabid,String task_id)throws GeneralException
    {
	    boolean b=true;
        try
        {
            String sqlstr ="select count(*) from templet_"+tabid 
                +" where  exists (select null from t_wf_task_objlink where templet_"
                +tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id "
                +"  and task_id="+task_id+"   and submitflag=1  and (state is null or  state=0 ) and ("
                +Sql_switcher.isnull("special_node","0")+"=0  or ( "
                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' ) ) ) ) ";
            this.frowset=dao.search(sqlstr);
            if(this.frowset.next())
            {
                if(this.frowset.getInt(1)==0)
                   b=false; 
            }
                    
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return b;
    }
	
}
