package com.hjsj.hrms.module.template.templatetoolbar.apply.businessobject;

import com.hjsj.hrms.businessobject.general.template.TSubsetCtrl;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.*;
import com.hjsj.hrms.businessobject.infor.BaseInfoBo;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.AsyncEmailBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.service.SynOaService;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("all")
public class TemplateApplyBo {

	private UserView userView;
	private Connection conn=null;
	
	private TemplateFrontProperty frontProperty;
	private TemplateParam paramBo = null;
	private ContentDAO dao;
	private String applyFlag = "1"; //默认1 报批 2 驳回
	
	public String getApplyFlag() {
		return applyFlag;
	}


	public void setApplyFlag(String applyFlag) {
		this.applyFlag = applyFlag;
	}
	
	public TemplateApplyBo(Connection conn,UserView userview,TemplateParam paramBo,TemplateFrontProperty frontProperty){
		this.conn = conn;
		this.userView=userview;
		this.frontProperty=frontProperty; 
        this.paramBo = paramBo;
        dao = new ContentDAO(conn);   
        
	}
	
	   
    /**
     * flag =1报批，=2驳回
     * @throws GeneralException 
     * 
     */
    public void submitAttachmentFile(String ins_id ,TemplateTableBo tablebo,String flag,String tabid) throws GeneralException{

    	RowSet rset = null;
        RowSet rowSet = null;
		HashMap delMap=new HashMap();
		String archive_attach_to = paramBo.getArchive_attach_to();	
		Boolean attach_history=paramBo.isAttach_history();
		ContentDAO dao=new ContentDAO(this.conn);
		if("".equals(archive_attach_to)){//bug 49689 单位、岗位个人附件提交后重复
			if(tablebo.getInfor_type() == 1){
				archive_attach_to = "A00";
			}else if(tablebo.getInfor_type() == 2){
				archive_attach_to = "B00";
			}else if(tablebo.getInfor_type() == 3){
				archive_attach_to = "K00";
			}
		}
        try
        {
    		boolean isSavaAttachToMinSet = false;
    		FieldSet a01Set = DataDictionary.getFieldSetVo("A01");
    		if(paramBo.isArchiveAttachToMainSet() && "1".equals(a01Set.getMultimedia_file_flag())){
				isSavaAttachToMinSet = true;
    		}
            if (this.hasFunction(isSavaAttachToMinSet,archive_attach_to)) {
            	ArrayList paramList=new ArrayList();
	            	String task_id = "";
	        		StringBuffer sb = new StringBuffer("");
	        		StringBuffer sbfortask = new StringBuffer("");
	        		sbfortask.append("select max(task_id) task_id from t_wf_task where ins_id="+ins_id);
	        		if(tablebo.getInfor_type() == 1){//查询出人下面的附件
		        		sb.append("select * from t_wf_file where ins_id=? and tabid=? and attachmenttype=1 order by file_id");// 个人附件
		        		paramList=new ArrayList();
		        		paramList.add(ins_id);
		        		paramList.add(tabid);
	        		}else if(tablebo.getInfor_type() == 2){
		        		sb.append("select * from t_wf_file where ins_id=? and tabid=? and attachmenttype=1  order by file_id");// 个人附件
		        		paramList=new ArrayList();
		        		paramList.add(ins_id);
		        		paramList.add(tabid);
	        		}else if(tablebo.getInfor_type() == 3){
		        		sb.append("select * from t_wf_file where ins_id=? and tabid=? and attachmenttype=1  order by file_id");// 个人附件
		        		paramList=new ArrayList();
		        		paramList.add(ins_id);
		        		paramList.add(tabid);
	        		}
	        		//个人附件归档时不需要判断 lis 20160820
	        		/*if (!this.userView.isAdmin() && !this.userView.getGroupId().equals("1") && !this.getIsIgnorePriv()) {
	        			sb.append(" and filetype in (select id from mediasort where flag in (" + this.getMediaPriv() + "))");
	        		}*/
	        		//查询对应的最后一个task_id
	        		rowSet = dao.search(sbfortask.toString());
	        		if(rowSet.next())
	        			task_id =rowSet.getInt("task_id")+"";
	        		rowSet = dao.search(sb.toString(),paramList);
	        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        		String time = sdf.format(new Date());// 得到当前日期
	        		switch (Sql_switcher.searchDbServer()) {
	        		case Constant.ORACEL : {
	        			time = "to_date('" + time + "','yyyy-mm-dd')";
	        			break;
	        		}
	        		case Constant.MSSQL : {
	        			time = "'" + time + "'";
	        			break;
	        		}
	        		}
	        		HashMap destA0100Map = new HashMap();//用于是否清除目标主集附件。 
	        		HashMap map = tablebo.getDestination_a0100();
	        		while (rowSet.next()) {
	        			Boolean isNeedInsert=false;
	        			sb.setLength(0);
	        			int flagid = rowSet.getInt("filetype");// 和mediasort表的id相关联
	        			int file_id = rowSet.getInt("file_id");
	        			int state = rowSet.getInt("state");
	        			String mediaId = StringUtils.isBlank(rowSet.getString("i9999"))?"-1":rowSet.getString("i9999");
	        			int recid = 0;
	        			String tableName = null;
	        			RecordVo updatevo = null;
	        			Blob blob = null;
	        			String filepath = rowSet.getString("filepath");
	        			//liuyz bug25774 兼容6.3程序上传的附件，6.3程序上传附件存入t_wf_file表中无filepath，需要将content中二进制数据转换成文件，文件路径重新保存到t_wf_file的filepath中
	        			AttachmentBo attachmentBo = new AttachmentBo(userView, conn, tabid);
	        			if(filepath==null)
	        			{
	        				 InputStream in = null;
	        				 HashMap fileMap =attachmentBo.downloadFile(String.valueOf(file_id));
	        				 filepath = (String)fileMap.get("filepath");
	        				in = (InputStream)fileMap.get("ole");
	        				String ext = attachmentBo.getExt().toLowerCase();
	        				//保存到指定目录(路径)按照子集附件保存路径存储
	        				OutputStream output = null;
	        				attachmentBo.initParam(true);
	        			    String middlepath = "";
	        				if("\\".equals(File.separator)){//证明是windows
	        					middlepath = "subdomain\\template_";
	        				}else if("/".equals(File.separator)){//证明是linux
	        					middlepath = "subdomain/template_";
	        				}
	        				UUID uuid = UUID.randomUUID();
	        			    String fileuuidname = uuid.toString();
	        			    filepath = attachmentBo.getAbsoluteDir(fileuuidname,middlepath)+File.separator+fileuuidname + ext;
	        			    // 保存文件
	        			    String absoluteDir = attachmentBo.getAbsoluteDir(fileuuidname,middlepath);
	        			    if(!absoluteDir.startsWith(attachmentBo.getRootDir())) {
	        			    	absoluteDir = attachmentBo.getRootDir()+absoluteDir;
	        			    }
	        				File file = new File(absoluteDir, fileuuidname + ext);
	        				output = new FileOutputStream(file);
	        				byte[] bt = new byte[1024];
	        				int read = 0;
	        				while ((read = in.read(bt)) != -1) {
	        					output.write(bt, 0, read);
	        				}
	        				//将filepath存储到对应记录
	        				StringBuffer sbin = new StringBuffer();
	        				sbin.append("update t_wf_file set filepath='"+filepath+"',content='' where file_id ="+file_id);
	        				dao.update(sbin.toString());
	        			}
	        			////liuyz bug25774 end
	        			/*filepath = filepath.replace("\\", File.separator).replace("/", File.separator);//liunx和window盘符不同
	        			File file = null;
	        			if(StringUtils.isNotBlank(filepath)) {
	        				attachmentBo.initParam(false);
	        				if(!filepath.startsWith(attachmentBo.getRootDir())) {
	        					filepath = attachmentBo.getRootDir()+filepath;
	        				}
	        				file = new File(filepath);
	        			}*/
	        			//vfs改造 无需校验文件是否存在
	        			if(true/*file.exists()||state==1*/){//如果文件存在说明是没删除的，state=1是需要删除的
		        			if (tablebo.getInfor_type() == 1) {
		        				String basepre = "";// 要归档到的人员库
		        				String a0100 = "";// 最终的人员编号
		        				if (tablebo.getOperationtype() == 0 || tablebo.getOperationtype() == 1 || tablebo.getOperationtype() == 2) {
		        					basepre = tablebo.getDestBase();
		        					String sourcea0100 =  rowSet.getString("objectid");
		        					a0100 = (String) tablebo.getDestination_a0100().get(sourcea0100);
		        				} else {
		        					basepre = rowSet.getString("basepre");
		        					a0100 = rowSet.getString("objectid");
		        				}
		        				if ("1".equals(this.userView.getHm().get("fillInfo")))
		        	            {
		        	              ArrayList list = new ArrayList();
		        	              String sql = "update t_wf_file set basepre=? where file_id=?";
		        	              list.add(basepre);
		        	              list.add(Integer.valueOf(file_id));
		        	              dao.update(sql, list);
		        	            }
		        				boolean isRemoveAtta = false;
		        				if(isSavaAttachToMinSet){
		        					isRemoveAtta = attach_history;
		        					this.saveMultimediaToFile(basepre, a0100, "A01",destA0100Map, flagid, rowSet, filepath, mediaId,isRemoveAtta,state);
		                    	}else{
		                    		if("A00".equalsIgnoreCase(archive_attach_to)){
			                    		tableName =  basepre + "A00";
			                    		if(state==1){
			                    			if(attach_history&&StringUtils.isNotBlank(mediaId)){
					                    		String sql="update "+tableName+" set state=9 where a0100=? and lower(flag)<>'p'  and i9999=? ";//先把多媒体数据的state置为9标记为待删除
					                    		ArrayList  list=new ArrayList();
					                    		list.add(a0100);
					                    		list.add(mediaId);
					                    		dao.update(sql, list);
					                    		delMap.put(basepre+"`"+a0100, tableName);
			                    			}
			                    		}
			                    		if(state==0){
				                    		String sql="select 1 from  "+tableName+" where a0100=? and lower(flag)<>'p'  and i9999=? ";//如果多媒体或者主集中有这个附件，就不再插入。只有新增的才插入
				                    		ArrayList  list=new ArrayList();
				                    		list.add(a0100);
				                    		list.add(mediaId);
				                    		rset=dao.search(sql, list);
				                    		if(!rset.next()||"-1".equalsIgnoreCase(mediaId)){//mediaId=-1说明是新插入的rset是空说明档案库中没有
				                    			isNeedInsert=true;
					                    		recid = Integer.parseInt(new StructureExecSqlString().getUserI9999(basepre + "a00",a0100,"A0100",this.conn));
					                    		updatevo=new RecordVo(tableName);
					                    		updatevo.setString("a0100",a0100);
					                    		updatevo.setInt("i9999",recid);
					                    		sb.append("insert into " + tableName + " (id,a0100,i9999,title,flag,ext,createusername,modusername,state,createtime,modtime)");
					                    		sb.append(" select null,'" + a0100 + "'," + recid + " ,name,(select flag from mediasort where id=" + flagid + ") flag," );
					                    		if(Sql_switcher.searchDbServer()==Constant.ORACEL){
					                    			sb.append("case when instr(ext,'.')=1 then ext when instr(ext,'.')=0 then '.'" + Sql_switcher.concat() + "ext end ext");
					                    		}else{
					                    			sb.append("case when charindex('.',ext)=1 then ext when charindex('.',ext)=0 then '.'" + Sql_switcher.concat() + "ext end ext");
					                    		}
					                    		sb.append(",create_user,create_user,3,(select " + time + " from t_wf_file where file_id=" + file_id + ") createtime,(select " + time + " from t_wf_file where file_id=" + file_id + ") modtime from t_wf_file where file_id=" + file_id);//liuyz 通过人事异动审批的多媒体附件都是审批状态。 
				                    		}
				                    	}
		                    		}else{//归档到子集
			                    		int i9999 = 1;
										BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userView,1);
			                    		ArrayList subUpdateList = paramBo.getSubUpdateList();
			                    		int updatetype = 0;
			                    		TSubsetCtrl tsubsetCtrl_ = null;
			                    		YksjParser yp=null;
			                    		for(int i=0;i<subUpdateList.size();i++){
			                    			TSubsetCtrl tsubsetCtrl = (TSubsetCtrl)subUpdateList.get(i);
			                    			String name = tsubsetCtrl.getSetcode();
			                    			String submenu = tsubsetCtrl.getSubMenu();
			                    			int type = tsubsetCtrl.getUpdatetype();
			                    			if(name.equalsIgnoreCase(archive_attach_to)&&"false".equalsIgnoreCase(submenu)){
			                    				updatetype = type;
			                    				tsubsetCtrl_ = tsubsetCtrl;
			                    			}
			                    		}
			                    		i9999=infobo.getMaxI9999(basepre,archive_attach_to,a0100);
			                    		if(updatetype==1||updatetype==0){//新增记录
			                    			isRemoveAtta = false;
			                    			if(i9999>1)
				                    			this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, filepath, i9999-1,isRemoveAtta,state);
			                    		}
			                    		else if(updatetype==2){//更新当前
			                    			isRemoveAtta = false;    
			                    			if(i9999>1)
				                    			this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, filepath, i9999-1,isRemoveAtta,state);
										}
			                    		else if(updatetype==3){//条件更新
			                    			isRemoveAtta = false;
			                    			//查出条件对应的i9999
			                    			String destab=basepre+archive_attach_to;
			                    			String srctab = "templet_"+tabid;
			                    			String i9999s = "";
			                    			String cond_str="";
			    							String condFormula=tsubsetCtrl_.getCondFormula();
			    							if(condFormula==null||condFormula.trim().length()==0){
			    								cond_str=" and ( 1=1 ) ";	
			    							}else{
			    								yp = new YksjParser( this.userView ,getCondUpdateFieldList(archive_attach_to,tabid),
			    										YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
			    								
			    								yp.run_where(condFormula);
			    								String strfilter=yp.getSQL();
			    								if(strfilter.length()>0)
			    									cond_str=" and ("+strfilter+") ";
			    							}
			    							StringBuffer strSubCondition=new StringBuffer(""); 
			    							strSubCondition.append(" where  exists (select null from t_wf_task_objlink where templet_"+tabid+".seqnum=t_wf_task_objlink.seqnum and templet_"+tabid+".ins_id=t_wf_task_objlink.ins_id ");
			    							strSubCondition.append("  and task_id="+task_id+" and tab_id="+tabid+" and state=1 ) ");
			    							strSubCondition.append(" and lower(basepre)='"+basepre.toLowerCase()+"'"); 
			    							String sql = "select i9999 from "+destab+","+srctab+" where "+destab+".a0100="+srctab+".a0100 and "+strSubCondition.substring(7)+" "+cond_str;
			    							rset = dao.search(sql);
			    							while(rset.next()) {
			    								String i9999_ = rset.getInt("i9999")+"";
			    								i9999s+=i9999_+",";
			    							}
			    							if(i9999s.length()>0) {
			    								i9999s = i9999s.substring(0, i9999s.length()-1);
			    								this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, filepath, i9999s,isRemoveAtta,state);
			    							}else{//如果没有符合条件的，更新到最后一条中
			    								isRemoveAtta = false;    
				                    			if(i9999>1) {
				                    				this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, filepath, i9999-1,isRemoveAtta,state);
				                    			}
			    							}
										}
			                    	}
		                    	}
		        			} else if (tablebo.getInfor_type() == 2) {
		        				String b0110 = rowSet.getString("objectid");
		        				//lis add 20160719
		        				if(map!=null){
		        					String objectid = (String) map.get(b0110);
		        					if(objectid!=null&&!objectid.equals(b0110)){
		        						b0110 = objectid;
		        					}
		        				}
		        				//lis end 20160719   
		        				recid = Integer.parseInt(new StructureExecSqlString().getUserI9999("B00",b0110,"b0110",this.conn));
		        				tableName = "B00";
		        				if(state==1){
			        				if(attach_history&&StringUtils.isNotBlank(mediaId)){
			                    		String sql="update "+tableName+" set state=9  where b0110=? and i9999=? ";
			                    		ArrayList  list=new ArrayList();
			                    		list.add(b0110);
			                    		list.add(mediaId);
			                    		dao.update(sql, list);
			                    		delMap.put(b0110, tableName);
			        				}
		        				}
			        			if(state==0){
			        				String sql="select 1 from  "+tableName+" where b0110=? and i9999=? ";
		                    		ArrayList  list=new ArrayList();
		                    		list.add(b0110);
		                    		list.add(mediaId);
		                    		rset=dao.search(sql, list);
		                    		if(!rset.next()||"-1".equalsIgnoreCase(mediaId)){
		                    			isNeedInsert=true;
				        				updatevo=new RecordVo(tableName);
				        				updatevo.setString("b0110",b0110);
				        				updatevo.setInt("i9999",recid);
				        				sb.append("insert into B00 (b0110,i9999,title,flag,ext,createusername,modusername,state,createtime,modtime)");
				        				sb.append(" select '" + b0110 + "',(select " + Sql_switcher.isnull("max(i9999)", "0") + "+1 from B00 where b0110='" + b0110 + "') i9999,name,(select flag from mediasort where id=" + flagid + ") flag," );
				        				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				                			sb.append("case when instr(ext,'.')=1 then ext when instr(ext,'.')=0 then '.'" + Sql_switcher.concat() + "ext end ext");
				                		}else{
				                			sb.append("case when charindex('.',ext)=1 then ext when charindex('.',ext)=0 then '.'" + Sql_switcher.concat() + "ext end ext");
				                		} 
				        				sb.append(",create_user,create_user,3,(select " + time + " from t_wf_file where file_id=" + file_id + ") createtime,(select " + time + " from t_wf_file where file_id=" + file_id + ") modtime from t_wf_file where file_id=" + file_id);//liuyz 通过人事异动审批的多媒体附件都是审批状态。
		                    		}
		        				}
		        			} else if (tablebo.getInfor_type() == 3) {
		        				String e01a1 = rowSet.getString("objectid");
		        				//lis add 20160719
		        				if(map!=null){
		        					String objectid = (String) map.get(e01a1);
		        					if(objectid!=null&&!objectid.equals(e01a1)){
		        						e01a1 = objectid;
		        					}
		        				}
		        				//lis end 20160719
		        				tableName = "K00";
		        				if(state==1){
			        				if(attach_history&&StringUtils.isNotBlank(mediaId)){
			                    		String sql="update "+tableName+" set state=9 where e01a1=? and i9999=? ";
			                    		ArrayList  list=new ArrayList();
			                    		list.add(e01a1);
			                    		list.add(mediaId);
			                    		dao.update(sql, list);
			                    		delMap.put(e01a1, tableName);
			        				}
		        				}
		        				if(state==0){
		        					String sql="select 1 from  "+tableName+" where e01a1=? and i9999=? ";
		                    		ArrayList  list=new ArrayList();
		                    		list.add(e01a1);
		                    		list.add(mediaId);
		                    		rset=dao.search(sql, list);
		                    		if(!rset.next()||"-1".equalsIgnoreCase(mediaId)){
		                    			isNeedInsert=true;
				        				recid = Integer.parseInt(new StructureExecSqlString().getUserI9999("K00",e01a1,"e01a1",this.conn));
				        				updatevo=new RecordVo(tableName);
				        				updatevo.setString("e01a1",e01a1);
				        				updatevo.setInt("i9999",recid);
				        				sb.append("insert into K00 (e01a1,i9999,title,flag,ext,createusername,modusername,state,createtime,modtime)");
				        				sb.append(" select '" + e01a1 + "',(select " + Sql_switcher.isnull("max(i9999)", "0") + "+1 from K00 where e01a1='" + e01a1 + "') i9999,name,(select flag from mediasort where id=" + flagid + ") flag,");//云南能投批准报e01a1不存在
				        				if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				                			sb.append("case when instr(ext,'.')=1 then ext when instr(ext,'.')=0 then '.'" + Sql_switcher.concat() + "ext end ext");
				                		}else{
				                			sb.append("case when charindex('.',ext)=1 then ext when charindex('.',ext)=0 then '.'" + Sql_switcher.concat() + "ext end ext");
				                		}
				        				sb.append(",create_user,create_user,3,(select " + time + " from t_wf_file where file_id=" + file_id + ") createtime,(select " + time + " from t_wf_file where file_id=" + file_id + ") modtime from t_wf_file where file_id=" + file_id);//liuyz 通过人事异动审批的多媒体附件都是审批状态。 
		                    		}
		        				}
		        			}
		        			//AttachmentBo attachmentBo = new AttachmentBo(userView, conn, tabid);
		        			if("A00".equalsIgnoreCase(archive_attach_to)||"B00".equalsIgnoreCase(archive_attach_to)||"K00".equalsIgnoreCase(archive_attach_to)){
			        			if(state==0&&isNeedInsert){//如果state=0且需要插入，才更新
			        				dao.update(sb.toString(),new ArrayList());
		        					updatevo.setObject("fileid",filepath);	
		        					//提交时修改附件tag类型为员工类型
		        					VfsService.updateFileTag(filepath, VfsModulesEnum.YG.toString());
		        					dao.updateValueObject(updatevo);
			        				/*switch (Sql_switcher.searchDbServer()) {
				        				case Constant.ORACEL : {
				        					dao.update(sb.toString(),Arrays.asList(""));
				        					//保存附件
				        					blob = this.getOracleBlob(updatevo, file,tablebo.getInfor_type());
				        					updatevo.setObject("ole",blob);	
				        					dao.updateValueObject(updatevo);
				        					break;
				        				}
				        				case Constant.MSSQL : {
				        					dao.update(sb.toString(),Arrays.asList(attachmentBo.getBytes(file)));
				        					break;
				        				}
			        				}*/
			        			}
		        		    }
	        		} // while 遍历结束
	            } // flag=1 结束
            } // try
        }
        catch(Exception e)
        {
        	try{
	        	Iterator iterator = delMap.entrySet().iterator();
	    		while(iterator.hasNext()){
	    			Entry next = (Entry) iterator.next();
	    			String key = (String) next.getKey();
	    			String value = (String) next.getValue();
	    			String sql="";
	    			String updatesql="";
	    			ArrayList list=new ArrayList();
	    			if("A00".equalsIgnoreCase(archive_attach_to)){
	    				updatesql="update "+value+" set state=3 where a0100=? and state=9";
	    				String a0100=key.split("`")[1];
	    				list.add(a0100);
	    			}else if("B00".equalsIgnoreCase(archive_attach_to)){
	    				updatesql="update "+value+" set state=3 where B0110=? and state=9";
	    				list.add(key);
	    			}else if("K00".equalsIgnoreCase(archive_attach_to)){
	    				updatesql="update "+value+" set state=3 where E01a1=? and state=9";
	    				list.add(key);
	    			}
	    			dao.update(updatesql, list);
	    		}
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally {
        	try{
	        	Iterator iterator = delMap.entrySet().iterator();
	    		while(iterator.hasNext()){
	    			Entry next = (Entry) iterator.next();
	    			String key = (String) next.getKey();
	    			String value = (String) next.getValue();
	    			String sql="";
	    			ArrayList list=new ArrayList();
	    			if("A00".equalsIgnoreCase(archive_attach_to)){
	    				sql="delete from "+value+" where a0100=? and state=9";
	    				String a0100=key.split("`")[1];
	    				list.add(a0100);
	    			}else if("B00".equalsIgnoreCase(archive_attach_to)){
	    				sql="delete from "+value+" where B0110=? and state=9";
	    				list.add(key);
	    			}else if("K00".equalsIgnoreCase(archive_attach_to)){
	    				sql="delete from "+value+" where e01a1=? and state=9";
	    				list.add(key);
	    			}
    				dao.delete(sql, list);
	    		}
    		}catch(Exception ex){
				ex.printStackTrace();
			}
        	PubFunc.closeDbObj(rowSet);
        	PubFunc.closeDbObj(rset);
        }
    }
    /**
     * 条件更新时的附件归档
     * @param basepre
     * @param a0100
     * @param archive_attach_to
     * @param destA0100Map
     * @param flagid
     * @param rowSet
     * @param file
     * @param i9999s
     * @param isRemoveAtta
     * @throws GeneralException
     */
    private void saveMultimediaToFile(String basepre, String a0100, String archive_attach_to, HashMap destA0100Map,
			int flagid, RowSet rowSet, File file, String i9999s, boolean isRemoveAtta,int state) throws GeneralException{
    	try {
    		if(StringUtils.isBlank(i9999s)){//bug 51078
    			i9999s="-1";
    		}
			String[] i9999arr = i9999s.split(",");
			for(int i=0;i<i9999arr.length;i++) {
				int i9999 = Integer.parseInt(i9999arr[i]);
				this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, file, i9999,isRemoveAtta,state);
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
		}
	}
    
    private void saveMultimediaToFile(String basepre, String a0100, String archive_attach_to, HashMap destA0100Map,
			int flagid, RowSet rowSet, String fileid, String i9999s, boolean isRemoveAtta,int state) throws GeneralException{
    	try {
    		if(StringUtils.isBlank(i9999s)){//bug 51078
    			i9999s="-1";
    		}
			String[] i9999arr = i9999s.split(",");
			for(int i=0;i<i9999arr.length;i++) {
				int i9999 = Integer.parseInt(i9999arr[i]);
				this.saveMultimediaToFile(basepre, a0100, archive_attach_to,destA0100Map, flagid, rowSet, fileid, i9999,isRemoveAtta,state);
			}
    	}catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
     * 主集或者子集附件归档
     * @param basepre
     * @param a0100
     * @param setid
     * @param destA0100Map
     * @param flagid
     * @param rowSet
     * @param file
     * @param i9999
     * @param isRemoveAtta
     * @throws GeneralException
     */
    private void saveMultimediaToFile(String basepre, String a0100, String setid, HashMap destA0100Map,int flagid,RowSet rowSet,File file,int i9999, boolean isRemoveAtta,int state)throws GeneralException{
    	try{
    		MultiMediaBo multimediabo = new MultiMediaBo(this.conn, this.userView, "A",basepre,setid, a0100, i9999);
			String keyValue= basepre+a0100;
			if (isRemoveAtta&&state==1&&(i9999!=0&&i9999!=-1)){//state=1标识删除，i9999不是0或者-1说明是从档案库同步过来的
				//清除附件
				multimediabo.deleteMultimediaFileByA0100("A", setid, basepre, a0100, i9999);
				destA0100Map.put(keyValue, keyValue);
			}
			if(state==0&&(((i9999==0||i9999==-1)&&"A01".equalsIgnoreCase(setid))||!"A01".equalsIgnoreCase(setid))){//i9999==0说明是优化前新增的附件，i9999=-1说明是优化后用户新增的。state=0不是要删除的
				RowSet set = dao.search("select flag from mediasort where id=?", Arrays.asList(flagid));
				HashMap destMap = new HashMap();
				destMap.put("mainguid", multimediabo.getMainGuid());//主集
		        destMap.put("childguid", multimediabo.getChildGuid());          
		        destMap.put("nbase", basepre);
		        destMap.put("a0100", a0100);
		        if(set.next())
		        	destMap.put("filetype", set.getString("flag"));//多媒体文件目录
		        destMap.put("filetitle", rowSet.getString("name"));//多媒体文件目录
				multimediabo.saveMultimediaFile(destMap, file, true);
				PubFunc.closeResource(set);
			}
    	}catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    private void saveMultimediaToFile(String basepre, String a0100, String setid, HashMap destA0100Map,int flagid,RowSet rowSet,String fileid,int i9999, boolean isRemoveAtta,int state)throws GeneralException{
    	try{
    		MultiMediaBo multimediabo = new MultiMediaBo(this.conn, this.userView, "A",basepre,setid, a0100, i9999);
			String keyValue= basepre+a0100;
			if (isRemoveAtta&&state==1&&(i9999!=0&&i9999!=-1)){//state=1标识删除，i9999不是0或者-1说明是从档案库同步过来的
				//清除附件
				multimediabo.deleteMultimediaFileByA0100("A", setid, basepre, a0100, i9999);
				destA0100Map.put(keyValue, keyValue);
			}
			// 主集附件与子集附件归档都需要判断state是否为0  注释掉i9999的校验 否则子集附件无法新增
			if(state==0/*&&(i9999==0||i9999==-1)*//*&&"A01".equalsIgnoreCase(setid)||!"A01".equalsIgnoreCase(setid)*/){//i9999==0说明是优化前新增的附件，i9999=-1说明是优化后用户新增的。state=0不是要删除的
				RowSet set = dao.search("select flag from mediasort where id=?", Arrays.asList(flagid));
				HashMap destMap = new HashMap();
				destMap.put("mainguid", multimediabo.getMainGuid());//主集
		        destMap.put("childguid", multimediabo.getChildGuid());          
		        destMap.put("nbase", basepre);
		        destMap.put("a0100", a0100);
		        if(set.next())
		        	destMap.put("filetype", set.getString("flag"));//多媒体文件目录
		        destMap.put("filetitle", rowSet.getString("name"));//多媒体文件目录
		        VfsFileEntity enty=VfsService.getFileEntity(fileid);
		        String name=enty.getName();
		        destMap.put("path", fileid);
		        destMap.put("srcfilename", name);
		        destMap.put("ext",name.substring(name.lastIndexOf(".")));
		        //提交时修改文件类型为员工管理附件类型 用于区分引入附件时 附件来源于人事异动还是员工管理
		        VfsService.updateFileTag(fileid, VfsModulesEnum.YG.toString());
				multimediabo.saveMultimediaFile(destMap, true);
				PubFunc.closeResource(set);
			}
    	}catch(Exception e){
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**
     * @author lis
     * @Description: 得到oracle字段数据
     * @date 2016-5-25
     * @param vo
     * @param file
     * @param info_type
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
	private Blob getOracleBlob(RecordVo vo, File file,int info_type) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		StringBuffer strInsert=new StringBuffer();
		if(info_type == 1){
			String tableName = vo.getModelName();
			String a0100 = vo.getString("a0100");
			String recid = vo.getString("i9999");
			strSearch.append("select ole from ");
			strSearch.append(tableName);
			strSearch.append(" where a0100='");
			strSearch.append(a0100);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append(tableName);
			strInsert.append(" set ole=EMPTY_BLOB() where a0100='");
			strInsert.append(a0100);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}else if(info_type == 2){
			String tableName = vo.getModelName();
			String b0110 = vo.getString("b0110");//云南能投批准报b0100不存在
			String recid = vo.getString("i9999");
			strSearch.append("select ole from ");
			strSearch.append(tableName);
			strSearch.append(" where b0110='");
			strSearch.append(b0110);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append(tableName);
			strInsert.append(" set ole=EMPTY_BLOB() where b0110='");
			strInsert.append(b0110);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}else if(info_type == 3){
			String tableName = vo.getModelName();
			String e01a1 = vo.getString("e01a1");
			String recid = vo.getString("i9999");
			strSearch.append("select ole from ");
			strSearch.append(tableName);
			strSearch.append(" where e01a1='");
			strSearch.append(e01a1);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			
			strInsert.append("update  ");
			strInsert.append(tableName);
			strInsert.append(" set ole=EMPTY_BLOB() where e01a1='");
			strInsert.append(e01a1);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}
		InputStream in = new FileInputStream(file);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),in); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
	
    public void SendEmailToBeginUser(TemplateTableBo tablebo,RecordVo ins_vo,ContentDAO dao,WF_Instance ins,String tabid) throws GeneralException{
        try{
         	if (!"true".equalsIgnoreCase(tablebo.getNotice_initiator())){//发送邮件并且在单据提交入库时要通知到流程发起人
	    		return;
	    	}
	    	if (!(tablebo.isBemail() || tablebo.isBsms())){
	    		return;
	    	}
            
            ins_vo=dao.findByPrimaryKey(ins_vo);
            if(ins_vo==null){
                throw new GeneralException(ResourceFactory.getProperty("error.wf.notins_id"));
            }

            int actorType=ins_vo.getInt("actor_type");
            String tempactorid=ins_vo.getString("actorid");
            String Titlename="";
            String context="";
            String a0100="";
            String nbase="";
            String sql="";
            //EMailBo bo=null;

            String objectId = "";
            String toAddr = "";
            if(actorType==4){//说明是业务用户
                sql="select * from operuser where userName='"+tempactorid+"'";
                RowSet frowset=dao.search(sql);
                String email = "";
                if(frowset.next()){
                    a0100=frowset.getString("a0100");
                    nbase=frowset.getString("nbase");
                    email = frowset.getString("email");
                }
                if(StringUtils.isNotBlank(email)) {
                	toAddr = email;
                }else {
                	if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(a0100)) {
                		objectId = nbase+a0100;
                    }
                }
                
            }else{//自助用户
                if(tempactorid.length()>3){
                    nbase=tempactorid.substring(0,3);
                    a0100=tempactorid.substring(3);
                    objectId = nbase+a0100;
                }
            }
            
            ArrayList attachList = null;
            Titlename=tablebo.getTable_vo().getString("name");//得到模版的名称 也就是邮件头
            context=Titlename;
            String template_initiator="";
            SendMessageBo sendBo=null;
            template_initiator=(String)tablebo.getTemplate_initiator();
            sendBo=new SendMessageBo(this.conn,tablebo.getUserview());
            if(ins.getTask_vo().getInt("task_id")!=0)
                sendBo.setTask_id(String.valueOf(ins.getTask_vo().getInt("task_id")));
            sendBo.setIns_id(String.valueOf(ins_vo.getInt("ins_id")));
            sendBo.setSp_flag("0");
            if(template_initiator!=null&&template_initiator.trim().length()>0) //获得审批模板
            {
                LazyDynaBean mailInfo=sendBo.getTemplateMailInfo(template_initiator);
                context=(String)mailInfo.get("content");
                //zxj 20141023 邮件模板附件
                attachList = (ArrayList)mailInfo.get("attach"); 
            }
            
            String _context = context.replace("\r\n","<br>").replace("\n","<br>").replace("\r","<br>");
            LazyDynaBean _bean = null;
            //这地方好像颠倒了 先改过来 wangrd 2015-05-27 
            if(template_initiator==null||template_initiator.trim().length()==0){
                _bean = sendBo.getTileAndContent("3", Titlename, _context, tempactorid, tablebo.getUserview(), tabid, ins.getObjs_sql(), null,tablebo.getInfor_type());//单据结束通知发起人,opt传3,让信息中的提示得到的是已被批准
            }else{
                _bean = sendBo.getEmailBean("2",null,Titlename,_context,tempactorid,tablebo.getUserview(),tabid,ins.getObjs_sql());
            }
            if(StringUtils.isNotBlank(objectId)) {
            	_bean.set("objectId", objectId);
            }
            if(StringUtils.isNotBlank(toAddr)) {
            	_bean.set("toAddr", toAddr);
            }
            if(attachList!=null&&attachList.size()>0){
                _bean.set("attachList", attachList);
            }
            //_context = (String)_bean.get("context");
            //context=_context;
            if (tablebo.isBemail() ){ //发送邮件
                AsyncEmailBo newEmailBo = null;
                try{
                    //bo=new EMailBo(this.getFrameconn(),true,"");
                    newEmailBo = new AsyncEmailBo(this.conn, this.userView);
                }
                catch(Exception e){
                    throw new GeneralException(ResourceFactory.getProperty("邮箱服务器配置错误，通知发起人失败"));
                }
            	newEmailBo.send(_bean);
            	//发送微信信息
            	new com.hjsj.hrms.businessobject.general.template.TemplateUtilBo(this.conn,this.userView).sendWeixinMessageFromEmail(_bean);
            }
            
            if (tablebo.isBsms() ){ //发送短信
				SmsBo smsbo=new SmsBo(this.conn);
				smsbo.sendMessage(this.userView,objectId,_context);
            }
                
        }catch(Exception e){
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    
    }
    
 
    
       /**
     * 从系统邮件服务器设置中得到发送邮件的地址
     * @return
     */
    public String getFromAddr() throws GeneralException 
    {
        String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null)
            return "";
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param))
            return "";
        try
        {
            Document doc = PubFunc.generateDom(param);
            Element root = doc.getRootElement();
            Element stmp=root.getChild("stmp"); 
            str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
    }
    

    /**   
     * @param personlist 
     * @Title: transAttachmentFile   
     * @Description: 将附件提交到审批表    此方法未考虑提交拆单情况
     * @param @param srcTab
     * @param @param ins_id
     * @param @throws GeneralException 
     * @return void 
     * @throws   
    */
    public void transAttachmentFile(String srcTab,String ins_id, ArrayList personlist) throws GeneralException{
        try
        {
        	 RowSet frowset=dao.search("select * from id_factory where sequence_name='t_wf_file.file_id'");
             if(!frowset.next())
             {//这个语句是向id_factory添加主键自动生成的功能
                 StringBuffer insertSQL=new StringBuffer();
                 insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue,auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
                 insertSQL.append(" values ('t_wf_file.file_id', '附件号', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
                 ArrayList list=new ArrayList();
                 dao.insert(insertSQL.toString(),list);
             }
             //提交
             String create_user=this.userView.getUserName();
 			//如果是通过自助申请，且是业务用户关联自助用户，就把上传附件人的帐号填入自助的帐号，否则用微信打开看不到。
            if(this.frontProperty.isSelfApply()&&this.userView.getStatus()==0){
             	DbNameBo db = new DbNameBo(this.conn);
 				String loginNameField = db.getLogonUserNameField();
 				String usernameSele=this.userView.getUserName();
 				if(StringUtils.isNotBlank(loginNameField)) {
 					loginNameField = loginNameField.toLowerCase();
 					String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
 					frowset=dao.search(sql);
 					while(frowset.next()){
 						usernameSele=frowset.getString("username");
 					}
 					if(StringUtils.isNotBlank(usernameSele)){
 						create_user=usernameSele;
 					}
 				}
             }
             String sql_file=" select * from t_wf_file where ins_id='"+ins_id+"' and tabid= "+this.frontProperty.getTabId()+" and create_user='"+create_user+"' ";
             String sql=" select * from t_wf_file where ins_id=0 and tabid="
                 +this.frontProperty.getTabId()+" and create_user='"+create_user+"' ";
                 if(this.paramBo.getInfor_type()==1){//人员
                 	String personarr = "";
                 	String sqlStr="";
                 	//liuyz bug27262 选人较多时，in后面超过1000,报ORA-01795
                 	switch (Sql_switcher.searchDbServer()) {
 					case Constant.MSSQL:
 						sqlStr+=" (basepre+objectid) " ;
 						 break;
 					default:
 						sqlStr+=" concat(basepre,objectid) " ;
 						 break;
                 	}
                 	for(int i=0;i<personlist.size();i++){
                 		ArrayList list = (ArrayList)personlist.get(i);
                 		String basepre = (String)list.get(0);
                 		String a0100 = (String)list.get(1);
                 		if(i==0)
                 			personarr += " in('"+basepre+a0100+"'";
                 		else if(i%990==0&&personlist.size()%990>0)
                 		{
                 			personarr+=") or "+sqlStr+" in('"+basepre+a0100+"'";
                 		}
                 		else{
                 			personarr += ",'"+basepre+a0100+"'";
                 		}
                 		if(i==personlist.size()-1)
                 		{
                 			personarr +=")";
                 		}
                 	}
                 	if("".equals(personarr))
                 		personarr = "in ('')";
                 	sql+="and (("+sqlStr+personarr+" and attachmenttype=1) or (attachmenttype=0)) ";
                 	sql_file+="and (("+sqlStr+personarr+" and attachmenttype=1) or (attachmenttype=0)) ";
                 }else{//b0110,e01a1
                 	String personarr = "";
                 	for(int i=0;i<personlist.size();i++){
                 		ArrayList list = (ArrayList)personlist.get(i);
                 		String a0100 = (String)list.get(0);
                 		if(i==0)
                 			personarr += "'"+a0100+"'";
                 		else
                 			personarr += ",'"+a0100+"'";
                 	}
                 	if("".equals(personarr))
                 		personarr = "''";
 					sql+=" and ((objectid in ("+personarr+") and attachmenttype=1) or (attachmenttype=0)) " ;
 					sql_file+=" and ((objectid in ("+personarr+") and attachmenttype=1) or (attachmenttype=0)) " ;
                 }
             sql+= " order by file_id";
             frowset = dao.search(sql_file);
             //报批后插入附件前判断附件表中是否存在相同记录 如果存在则取消插入 防止出现重复记录
             if(frowset.next()) {
                 return ;
             }
             frowset = dao.search(sql);
             String sqlstrs = "";
             while(frowset.next()){
                 String file_id = frowset.getString("file_id");
                 IDGenerator idg = new IDGenerator(2, this.conn);
                 String file_id2 = idg.getId("t_wf_file.file_id");
                 //liuyz bug25774 兼容6.3上传的附件，须将content字段内容也保留
                 sqlstrs=" insert into t_wf_file(file_id,filepath,filetype,objectid,basepre,attachmenttype,ins_id,tabid,ext,name,"
                     +"create_user,create_time,fullname,content,i9999,state) "
                     +"select "+file_id2+",filepath,filetype,objectid,basepre,attachmenttype,"+ins_id
                     +",tabid,ext,name,create_user,create_time,fullname,content,i9999,state from t_wf_file where file_id="+file_id+"  ";
                 dao.update(sqlstrs);
             } 
        } //try
        catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    /**   
     * @Title: deleteAttachmentFile   
     * @Description: 删除附件   
     * @param @param srcTab
     * @param @param personlist
     * @param @throws GeneralException 
     * @return void 
     * @throws   
    */
    public void deleteAttachmentFile(String srcTab,ArrayList personlist) throws GeneralException{
        try
        {
            //把原公共附件清空（不能直接清空。当发起人选了4个人，只报批了三个的时候，就不能清空）
            RowSet frowset=dao.search("select count(*) from "+srcTab+" where submitflag=0");
            boolean deleteFlag=true;
            if(frowset.next()){
                if(frowset.getInt(1)!=0){
                    deleteFlag=false;
                }
            }
            if(deleteFlag){
                dao.update(" delete from t_wf_file where ins_id=0 and tabid="+this.frontProperty.getTabId()+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype is null or attachmenttype=0)");
            }
           //再清空个人附件
            StringBuffer sb = new StringBuffer("");
            if(this.frontProperty.isSelfApply()){//如果是员工自助申请，那么直接删除
            	String create_user=this.userView.getUserName();
            	if(this.userView.getStatus()==0){
	             	DbNameBo db = new DbNameBo(this.conn);
	 				String loginNameField = db.getLogonUserNameField();
	 				String usernameSele=this.userView.getUserName();
	 				if(StringUtils.isNotBlank(loginNameField)) {
	 					loginNameField = loginNameField.toLowerCase();
	 					String sql="select "+loginNameField+" as username from "+this.userView.getDbname()+"A01 where a0100='"+this.userView.getA0100()+"' ";
	 					frowset=dao.search(sql);
	 					while(frowset.next()){
	 						usernameSele=frowset.getString("username");
	 					}
	 					if(StringUtils.isNotBlank(usernameSele)){
	 						create_user=usernameSele;
	 					}
	 				}
            	}
            
               dao.update(" delete from t_wf_file where ins_id=0 and tabid="+this.frontProperty.getTabId()+" and create_user='"+create_user+"' and (attachmenttype=1) and objectid='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
            }else{
               sb.setLength(0);
               if(this.paramBo.getInfor_type()==1){
                   sb.append("delete from t_wf_file where ins_id=0 and tabid="+this.frontProperty.getTabId()+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and basepre=? and objectid=?");
               }else{
                   sb.append("delete from t_wf_file where ins_id=0 and tabid="+this.frontProperty.getTabId()+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid=?");
               }
               dao.batchUpdate(sb.toString(),personlist);
           }
        } //try
        catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    
    
    
    public void sendOA(String ins_id,String task_id,String selfapply) throws GeneralException{
        try
        {
            boolean isSend=true;
            if(SystemConfig.getPropertyValue("clientName")!=null&& "gdzy".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())&& "1".equalsIgnoreCase(selfapply))
                isSend=false;
             //将单据信息发送至外部系统
    //       if(!selfapply.equalsIgnoreCase("1"))
             if(isSend)
             {
                 SynOaService sos=new SynOaService();
                 String tab_ids=sos.getTabids();
                 if(tab_ids.indexOf(","+this.frontProperty.getTabId()+",")!=-1)
                 {
                    if("1".equals((String)sos.getTabOptMap().get(this.frontProperty.getTabId().trim())))
                    {
                        String _info=sos.synOaService(String.valueOf(task_id),
                                this.frontProperty.getTabId(),this.userView);  //创建成功返回1，否则返回详细错误信息
                        if(!"1".equals(_info))
                            throw GeneralExceptionHandler.Handle(new Exception(_info)); 
                    }
                 }
             }
            
            
            
            
        } //try
        catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    public void deletePublicAttach(TemplateTableBo tablebo ,String srcTab,String task_id,String selfapply) throws GeneralException{
        try
        {
            String tabid= this.frontProperty.getTabId();
            RowSet frowset=dao.search("select count(*) from "+srcTab+" where submitflag=0");
            boolean deleteFlag=true;
            if(frowset.next()){
                if(frowset.getInt(1)!=0){
                    deleteFlag=false;
                }
            }
            //把原公共附件清空（不能直接清空。当发起人选了4个人，只报批了三个的时候，就不能清空）
            //if("1".equals(selectAll)){//如果全选,xcs 2014-05-20 这种做法是不对的 只有全选checkbox打勾时，这个selectAll才为1
            if(deleteFlag){
                dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype is null or attachmenttype=0)");
            }
           //再清空个人附件
            StringBuffer sb = new StringBuffer("");
            if("1".equals(selfapply)){//如果是员工自助申请，那么直接删除
                   dao.update(" delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'");
            }else{
               sb.setLength(0);
               if(tablebo.getInfor_type()==1){
                   sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and basepre=? and objectid=?");
               }else{
                   sb.append("delete from t_wf_file where ins_id=0 and tabid="+tabid+" and create_user='"+this.userView.getUserName()+"' and (attachmenttype=1) and objectid=?");
               }
               ArrayList personlist = new ArrayList();
               personlist = tablebo.getPersonlist(tablebo.getInfor_type(),this.userView.getUserName()+"templet_"+tabid);
               dao.batchUpdate(sb.toString(),personlist);
           }
            
            
            
        } //try
        catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    public void saveSubmitTemplateData(TemplateTableBo tablebo ,RecordVo ins_vo,WF_Actor wf_actor,String whl,String selfapply) throws GeneralException{
        try
        {
            String tabid= this.frontProperty.getTabId();
            boolean isOriData=false; // 表单数据没到临时表
            String sql="select count(*) as nrec from ";
            if("1".equalsIgnoreCase(selfapply))//员工通过自助平台发动申请
                sql+=" g_templet_"+tabid+" where a0100='"+this.userView.getA0100()+"' and lower(basepre)='"+this.userView.getDbname().toLowerCase()+"'";
            else
            {
                sql+=this.userView.getUserName()+"templet_"+tabid+" where submitflag=1"+whl;
            
            }
            RowSet frowset=dao.search(sql);
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
                    tablebo.saveSubmitTemplateData(this.userView.getUserName(),ins_vo.getInt("ins_id"),whl);
            }
            
            
            
            
        } //try
        catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**得到basepre和a1000的list 用于删除个人附件*/
    public ArrayList getPersonlist(int infor_type,String tablename){
        ArrayList list = new ArrayList();
        try{
            ContentDAO dao = new ContentDAO(this.conn);
            RowSet rs = null;
            StringBuffer sb = new StringBuffer("");
            if(infor_type==1){ 
                if(this.frontProperty.isSelfApply())
                {
                    ArrayList templist = new ArrayList();
                    String basepre =this.userView.getDbname();
                    String a0100 =this.userView.getA0100();
                    templist.add(basepre);
                    templist.add(a0100);
                    list.add(templist);
                }
                else
                {
                    sb.append("select basepre,a0100 from "+tablename+" where submitflag=1");
                    rs = dao.search(sb.toString());
                    while(rs.next()){
                        ArrayList templist = new ArrayList();
                        String basepre = rs.getString("basepre");
                        String a0100 = rs.getString("a0100");
                        templist.add(basepre);
                        templist.add(a0100);
                        list.add(templist);
                    }
                }
            }else if(infor_type==2){
                sb.append("select b0110 from "+tablename+" where submitflag=1");
                rs = dao.search(sb.toString());
                while(rs.next()){
                    ArrayList templist = new ArrayList();
                    String b0110 = rs.getString("b0110");
                    templist.add(b0110);
                    list.add(templist);
                }
            }else if(infor_type==3){
                sb.append("select e01a1 from "+tablename+" where submitflag=1");
                rs = dao.search(sb.toString());
                while(rs.next()){
                    ArrayList templist = new ArrayList();
                    String e01a1 = rs.getString("e01a1");
                    templist.add(e01a1);
                    list.add(templist);
                }
            }
            PubFunc.closeDbObj(rs);
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    
	/*是否勾选了 提交入库时不判断子集和指标的权限**/
	public boolean getIsIgnorePriv(){
		if("1".equals(paramBo.getUnrestrictedMenuPriv()))//=1 不判断
			return true;//=true 提交入库时不判断子集和指标权限  =false 判断
		return false;
	}
	
	/**检查是否有归档附件的权限
	 * @param isSavaAttachToMinSet 是否归档到主集
	 * */
	public boolean hasFunction( boolean isSavaAttachToMinSet, String archive_attach_to){
		boolean bool = false;
		boolean issubatt = false;
		FieldSet fieldSet = null;
		String tablename = "";
		if(paramBo.getInfor_type()==1){
			if(isSavaAttachToMinSet){ //个人附件保存到主集
				tablename="A01";
				issubatt = true;
			}else{
				if(!"".equals(archive_attach_to)&&archive_attach_to.length()>0&&!"old".equals(archive_attach_to)){
					tablename  = archive_attach_to;
					issubatt = true;
					if(!"A00".equalsIgnoreCase(tablename)){
						fieldSet = DataDictionary.getFieldSetVo(tablename);
						issubatt = "1".equals(fieldSet.getMultimedia_file_flag());
					}
				}else{
					tablename="A00";
					issubatt = true;
				}
			}
		}else if(paramBo.getInfor_type()==2){
			if(isSavaAttachToMinSet) //个人附件保存到主集
				tablename="B01";
			else
				tablename="B00";
			issubatt = true;		
		}else if(paramBo.getInfor_type()==3){
			if(isSavaAttachToMinSet) //个人附件保存到主集
				tablename="K01";
			else
				tablename="K00";
			issubatt = true;
		}

		if(issubatt&&"2".equals(this.userView.analyseTablePriv(tablename)) && (this.getMediaPriv().length()>0 || this.userView.isAdmin() || "1".equals(this.userView.getGroupId()))){
			bool = true;
		}
		if(!bool){
			bool = getIsIgnorePriv();
		}
		return bool;
	}
	
	/**将多媒体分类的权限组装成sql语句的形式*/
	public String getMediaPriv(){
		String mediaPriv = "";
		StringBuffer sb = new StringBuffer(",'-1'");
		String priv = this.userView.getMediapriv().toString();//我在上层已经用hasFunction控制了。所以priv一定有值。没值则不会调用这个函数
		String[] array = priv.split(",");
		for(int i=0;i<array.length;i++){
			if("".equals(array[i])){
				continue;
			}
			sb.append(",'" + array[i] + "'");
		}
		mediaPriv = sb.substring(1);
		return mediaPriv;
	}
	
	/**
	 * 报批过程出错，还原表单数据
	 * @param userView
	 * @param ins
	 * @param ins_id 实例ID
	 * @param tabId  模板ID
	 * @param bSelfApply 是否业务申请
	 */
	public void restoreTemplateData(UserView userView,WF_Instance ins,int ins_id,String tabId,boolean bSelfApply)throws GeneralException
	{
		try
		{ 
			ContentDAO dao=new ContentDAO(this.conn);
			//把业务数据返回至临时表中
			saveTemplateDataToSrcTab(userView.getUserName(),ins_id,tabId,bSelfApply); 
			dao.delete("delete from t_wf_instance where ins_id=?" , Arrays.asList(ins_id));
			dao.delete("delete from t_wf_task where ins_id=?" , Arrays.asList(ins_id));
			dao.delete("delete from t_wf_task_objlink where ins_id=?" , Arrays.asList(ins_id));
		
            String _sql="select * from templet_"+tabId+" where ins_id="+ins_id;
            String _delStr="delete from templet_"+tabId+" where ins_id="+ins_id;
            ins.insertKqApplyTable(_sql,tabId,"","10","templet_"+tabId); 
            dao.delete( _delStr,new ArrayList()); 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	 
	
	
	/**
	 * 把业务数据返回至临时表中
	 * @param username 用户名
	 * @param ins_id 实例号 
	 * @return
	 */
	private boolean saveTemplateDataToSrcTab(String username,int ins_id,String tabid,boolean bSelfApply)throws GeneralException 
	{
		boolean bflag=true;
		String srcTab=username+"templet_"+tabid;
		if(bSelfApply)//员工通过自助平台发动申请
			srcTab="g_templet_"+tabid;
		
		String destTab="templet_"+tabid;
		RowSet rset=null;
		try
		{
			StringBuffer strsql=new StringBuffer();
			ContentDAO dao=new ContentDAO(this.conn);
			strsql.append("select * from "+srcTab+" where 1=2");
			RowSet rowSet=dao.search(strsql.toString());
			ResultSetMetaData mt=rowSet.getMetaData();
			HashMap srcTabMap=new HashMap(); //原表结构
			for(int i=1;i<=mt.getColumnCount();i++)
			{
				srcTabMap.put(mt.getColumnName(i).toLowerCase(),mt.getColumnType(i)+"");
			}
			rowSet=dao.search("select * from "+destTab+" where 1=2");
			rowSet=dao.search(strsql.toString());
			mt=rowSet.getMetaData();
			HashMap desTabMap=new HashMap(); //原表结构
			for(int i=1;i<=mt.getColumnCount();i++)
			{
				desTabMap.put(mt.getColumnName(i).toLowerCase(),mt.getColumnType(i)+"");
			}
			Set  keySet=srcTabMap.keySet();
			StringBuffer selectItem=new StringBuffer("");
			for(Iterator t=keySet.iterator();t.hasNext();)
			{
				String key=(String)t.next();
				if(desTabMap.get(key.toLowerCase())!=null&&((String)desTabMap.get(key.toLowerCase())).equals(((String)srcTabMap.get(key.toLowerCase())) ))
				{
					selectItem.append(","+key);
				}
			}
			if(selectItem.length()>0)
			{
				dao.update("insert into "+srcTab+"("+selectItem.substring(1)+") select "+selectItem.substring(1)+" from "+destTab+" where ins_id="+ins_id);
			}
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bflag=false;
		}
		return bflag;
	}
	
	private ArrayList getCondUpdateFieldList(String setid, String tabid)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			list.addAll((ArrayList)DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET).clone());
			String sql="select * from template_set  where tabid="+tabid+" and field_name is not null and field_type is not null  and subflag='0'";
			RowSet rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String flag=rowSet.getString("flag");
				String field_type=rowSet.getString("field_type");
				int chgstate=rowSet.getInt("chgstate");
				if(flag==null|| "".equals(flag))
					continue;
				if("A".equalsIgnoreCase(flag)|| "B".equalsIgnoreCase(flag)|| "K".equalsIgnoreCase(flag))
				{
					if(field_type!=null&&field_type.trim().length()>0)
					{
						if(!("A".equalsIgnoreCase(field_type)|| "N".equalsIgnoreCase(field_type)|| "D".equalsIgnoreCase(field_type)|| "M".equalsIgnoreCase(field_type)))
							continue;
					}
					String field_name=rowSet.getString("field_name"); 
					FieldItem item=DataDictionary.getFieldItem(field_name.toLowerCase());
					if(item!=null)
					{
						/**可以增加模板指标与字典表指标进行校验*/
						FieldItem tempitem=(FieldItem)item.cloneItem();
						if(chgstate==2)
						{
							tempitem.setNChgstate(2); 
							tempitem.setItemid(rowSet.getString("field_name")+"_2");
							tempitem.setItemdesc("拟"+rowSet.getString("field_hz"));
						}
						else
						{
							tempitem.setNChgstate(1); 
							tempitem.setItemid(rowSet.getString("field_name")+"_1");
							tempitem.setItemdesc("现"+rowSet.getString("field_hz"));
						}
						list.add(tempitem);
					}
				}
				
			}
			
			ArrayList fieldlist=getMidVariableList(tabid);//临时变量
			list.addAll(fieldlist);
			PubFunc.closeDbObj(rowSet);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @param tabid 
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	public ArrayList getMidVariableList(String tabid)throws GeneralException
	{
		ArrayList fieldlist=new ArrayList();
		try
		{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
			buf.append(" midvariable where  nflag=0 and templetId <> 0 ");  //人事异动的nflag=0 
			//计算本模板中的临时变量和模板指标中引入的共享的临时变量。 20150929 liuzy
			buf.append(" and (templetId ="+tabid+" or (cstate ='1' and cname in (select field_name from template_set where tabid="+tabid+" and nullif(field_name,'') is not null ))) ");
			
			buf.append(" order by sorting"); 
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid(""/*"A01"*/);//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setCodesetid(rset.getString("codesetid")==null?"0":rset.getString("codesetid"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4:					
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
/**
		 * 审批之前判断是不是同一个单据的  如果是true 需要复制流程
		 * @param applyFlag
		 * @param tablebo
		 * @param taskId
		 * @return
		 */
		public String validateIsFromOne(String taskId) {
			RowSet rSet = null;
			String task_id="";
			TemplateUtilBo utilBo= new TemplateUtilBo(conn,this.userView);            
	        String tableName=utilBo.getTableName(this.frontProperty.getModuleId(),
	                Integer.parseInt(this.frontProperty.getTabId()), this.frontProperty.getTaskId());
			String sqlstr ="";
			try
	        {
		        if(this.frontProperty.isBatchApprove())//如果是批量处理
		        { 
		            task_id = validateBatchTasks(tableName);
		        }
		        else
		        {
		        	String taskid = "";
		        	String a0101="a0101_1";
		        	String a0100 = "a0100";
		        	RecordVo vo=new RecordVo(tableName);
		        	if(this.paramBo.getOperationType()==0&&vo.hasAttribute("a0101_2"))//调入
					{
						a0101="a0101_2";
					}
		            if(this.paramBo.getInfor_type()==2 || this.paramBo.getInfor_type()==3){//如果是单位管理机构调整 或 岗位管理机构调整
						a0101="codeitemdesc_1";
						if(this.paramBo.getOperationType()==5)
							a0101="codeitemdesc_2";
					}
		        	if(this.paramBo.getInfor_type()==2) {//单位
		        		a0100 = "b0110";
		        	}else if(this.paramBo.getInfor_type()==3)//岗位
		        		a0100 = "e01a1";
		        	boolean bHave=false;//有未选中记录
		        	boolean bHave_reject = false;
		            String sql="";
		            sql = "select * from "+tableName +" where exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum and "+tableName+".ins_id=t_wf_task_objlink.ins_id "
		            		+ "and tab_id="+this.frontProperty.getTabId()+" and task_id="+this.frontProperty.getTaskId()+" and "+Sql_switcher.isnull("submitflag", "0")+"=0 ";
		            sql+=" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+
		            		this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) )";
		            rSet=dao.search(sql);
		            if(rSet.next())
		            {
                        bHave =true;
		            }
		            if("2".equals(this.applyFlag)&&!bHave) {//驳回
			            sql = "";
		            	RecordVo taskVo=new RecordVo("t_wf_task");
		            	taskVo.setInt("task_id", Integer.parseInt(this.frontProperty.getTaskId()));
		            	taskVo = dao.findByPrimaryKey(taskVo);
		            	int ins_id = taskVo.getInt("ins_id");
		            	//查询流程下未结束的单据人员数
		            	sql = "select count(1) num from t_wf_task a,t_wf_task_objlink b where a.ins_id=b.ins_id and a.task_id=b.task_id and a.task_state in('2','3') and a.ins_id="+ins_id;
		            	sql += " and  "+Sql_switcher.isnull("b.state","0")+"!=3   and ( "+Sql_switcher.isnull("b.special_node","0")+"=0 or ("+Sql_switcher.isnull("b.special_node","0")+"=1 and (lower(b.username)='"+
			            		this.userView.getUserName().toLowerCase()+"' or lower(b.username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
		            	rSet = dao.search(sql);
		            	int num = 0;
		            	if(rSet.next()) {
		            		num = rSet.getInt("num");
		            	}
		            	sql = "";
		            	sql = "select count(1) num from "+tableName +" where exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum and "+tableName+".ins_id=t_wf_task_objlink.ins_id "
			            		+ "and tab_id="+this.frontProperty.getTabId()+" and task_id="+this.frontProperty.getTaskId();
			            sql+=" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and "
			            		+ "(lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
			            sql+=" and "+Sql_switcher.isnull("submitflag", "0")+"=1 )";
			            rSet = dao.search(sql);
		            	int realNum = 0;
		            	if(rSet.next()) {
		            		realNum = rSet.getInt("num");
		            	}
		                if(realNum<num) {
		                	bHave_reject =true;
		                	bHave = true;
		                }
		            }
		            sqlstr ="select * from "+tableName +" where exists (select null from t_wf_task_objlink where "+tableName+".seqnum=t_wf_task_objlink.seqnum and "+tableName+".ins_id=t_wf_task_objlink.ins_id "+
		            		" and tab_id="+this.frontProperty.getTabId()+" and task_id="+this.frontProperty.getTaskId()+" ";
		            sqlstr+=" and  "+Sql_switcher.isnull("state","0")+"!=3   and ( "+Sql_switcher.isnull("special_node","0")+"=0 or ("+Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )   )  ) ";
		            sqlstr+=" and "+Sql_switcher.isnull("submitflag", "0")+"=1 )";
		            String a0101s="";
		            String a0101s_="";
	                String a0100forsql="";
	                int num = 0;
	                rSet=dao.search(sqlstr);
	                ArrayList a0100list = new ArrayList();//记录选中的人员姓名
	                String ins_id = "";
	                while (rSet.next()){
	                    String a0101Value=rSet.getString(a0101);
	                    String a0100Value=rSet.getString(a0100);
	                    String nbaseValue = "";
	                    if(this.paramBo.getInfor_type()==1) {
	                    	nbaseValue = rSet.getString("basepre").toLowerCase();
	                    	a0100Value = nbaseValue+a0100Value;
	                    }
	                    ins_id = rSet.getString("ins_id");
	                    a0100list.add(a0101Value);
	                    if (a0101s.length()>0 ) {
	                    	a0101s=a0101s+",";
	                    	if(num<=4)
	                    		a0101s_ = a0101s_+",";
	                    	a0100forsql = a0100forsql+",";
	                    }
	                    a0101s=a0101s+a0101Value;
	                    if(num<4)
	                		a0101s_ = a0101s_+a0101Value;
	                    a0100forsql = a0100forsql+"'"+a0100Value+"'";
	                    num++;
	                }
	                if(!"".equals(a0101s_)&&!a0101s_.endsWith(",")) {
	                	a0101s_ = a0101s_+",";
	                }
	                if(bHave) {
	                	task_id = this.copyRecord(ins_id,a0100forsql,a0101s,num,taskid,taskId,a0101s_,bHave_reject);
	                }else {
	                	task_id = taskId;
	                }
		        }
	        }catch (Exception e) {
				e.printStackTrace();
	        }finally {
				PubFunc.closeDbObj(rSet);
			}
			return task_id;
		}
		/**
		 * 判断当前节点是不是与发散过来的
		 * @param taskid
		 */
		private boolean checkIsFromYu(String taskid) {
			RowSet rSet = null;
			boolean isFromYu = false;
			try {
				String[] taskarr =taskid.split(",");
				String sql = "select nodetype from t_wf_node where node_id=(select pre_nodeid from t_wf_transition  "
						+ "where next_nodeid=(select node_id from t_wf_task where task_id="+taskarr[0]+"))";
				rSet=dao.search(sql);
				int num = 0;
				String nodetype = "";
				while(rSet.next()) {
					nodetype = rSet.getString("nodetype");
					num++;
				}
				if(num==1&&"4".equals(nodetype)) {
					isFromYu = true;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rSet);
			}
			return isFromYu;
		}
		/**
		 * 批量处理复制流程
		 * @param tabname
		 * @param applyFlag
		 * @return
		 * @throws GeneralException
		 */
		private String validateBatchTasks(String tabname)throws GeneralException
	    {
			RowSet rSet = null;
	        String taskid= "";
	        try
	        {
	        	String a0101="a0101_1";
	        	String a0100 = "a0100";
	        	RecordVo vo=new RecordVo(tabname);
	        	if(this.paramBo.getOperationType()==0&&vo.hasAttribute("a0101_2"))//调入
				{
					a0101="a0101_2";
				}
	            if(this.paramBo.getInfor_type()==2 || this.paramBo.getInfor_type()==3){//如果是单位管理机构调整 或 岗位管理机构调整
					a0101="codeitemdesc_1";
					if(this.paramBo.getOperationType()==5)
						a0101="codeitemdesc_2";
				}
	        	if(this.paramBo.getInfor_type()==2) {//单位
	        		a0100 = "b0110";
	        	}else if(this.paramBo.getInfor_type()==3)//岗位
	        		a0100 = "e01a1";
	            String[] temps=this.frontProperty.getTaskId().split(",");
	            StringBuffer ss=new StringBuffer("");
	            for(int i=0;i<temps.length;i++)
	            {
	                if(temps[i]!=null&&temps[i].trim().length()>0)
	                    ss.append(","+temps[i]);
	            }
	            //批量审批，多个单据没必要同时提交，只要来自同一单据的人员被全选中了即可。
	            String sql="select distinct task_id from t_wf_task_objlink where tab_id="
	                +this.frontProperty.getTabId()+" and  "+Sql_switcher.isnull("state","0")+"!=3  and task_id in ("+ss.substring(1)
	                +") and "+Sql_switcher.isnull("submitflag", "0")+"=1  and ( "
	                +Sql_switcher.isnull("special_node","0")+"=0 or ("
	                +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )))";
	            ArrayList selectTaskList= dao.searchDynaList(sql);
	            //if(!"10".equals(applyFlag)){//提交或者报批的时候才验证批量必须全选
		            for (int i=0;i<selectTaskList.size();i++){//分析选中的记录
		            	boolean bHave=false;//有未选中记录
		                LazyDynaBean bean = (LazyDynaBean)selectTaskList.get(i);
		                String task_id= (String)bean.get("task_id");
		                
		                RecordVo task_vo=new RecordVo("t_wf_task");
		                task_vo.setInt("task_id",Integer.parseInt(task_id));
		                task_vo=dao.findByPrimaryKey(task_vo);
		                if(task_vo==null)
		                    throw new GeneralException(ResourceFactory.getProperty("error.wf_nottaskid"));  
		                String task_topic= task_vo.getString("task_topic");
		                String ins_id = task_vo.getString("ins_id");
		                //未选中人员                
		                sql="select * from "+tabname +" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and "+tabname+".ins_id=t_wf_task_objlink.ins_id"
		                    +" and tab_id="
		                    +this.frontProperty.getTabId()+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id ="+task_id
		                    +" and "+Sql_switcher.isnull("submitflag", "0")+"=0  and ( "
		                    +Sql_switcher.isnull("special_node","0")+"=0 or ("
		                    +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )))"
		                    +")";
		                rSet=dao.search(sql);
		                if (rSet.next()){
		                    bHave=true;
		                }
		                //选中的人员
		                sql="select * from "+tabname +" where exists (select null from t_wf_task_objlink where "+tabname+".seqnum=t_wf_task_objlink.seqnum and "+tabname+".ins_id=t_wf_task_objlink.ins_id"
		                        +" and tab_id="
		                        +this.frontProperty.getTabId()+" and  "+Sql_switcher.isnull("state","0")+"!=3   and task_id ="+task_id
		                        +" and "+Sql_switcher.isnull("submitflag", "0")+"=1  and ( "
		                        +Sql_switcher.isnull("special_node","0")+"=0 or ("
		                        +Sql_switcher.isnull("special_node","0")+"=1 and (lower(username)='"+this.userView.getUserName().toLowerCase()+"' or lower(username)='"+this.userView.getDbname().toLowerCase()+this.userView.getA0100()+"' )))"
		                        +")";   
	                    String a0101s="";
	                    String a0101s_ = "";
	                    String a0100forsql="";
	                    int num = 0;
	                    rSet=dao.search(sql);
	                    ArrayList a0100list = new ArrayList();//记录选中的人员姓名
	                    while (rSet.next()){
	                        String a0101Value=rSet.getString(a0101);
	                        String a0100Value=rSet.getString(a0100);
	                        String nbaseValue = "";
	                        if(this.paramBo.getInfor_type()==1) {
	                        	nbaseValue = rSet.getString("basepre").toLowerCase();
	                        	a0100Value = nbaseValue+a0100Value;
	                        }
	                        a0100list.add(a0101Value);
	                        if (a0101s.length()>0 ) {
	                        	a0101s=a0101s+",";
	                        	if(num<=4)
	                        		a0101s_ = a0101s_+",";
	                        	a0100forsql = a0100forsql+",";
	                        }
	                        a0101s=a0101s+a0101Value;
	                        if(num<4)
	                        	a0101s_=a0101s_+a0101Value;
	                        a0100forsql = a0100forsql+"'"+a0100Value+"'";
	                        num++;
	                    }
	                    if(!"".equals(a0101s_)&&!a0101s_.endsWith(",")) {
	                    	a0101s_ = a0101s_+",";
	                    }
	                    if(bHave) {
	                    	taskid= this.copyRecord(ins_id,a0100forsql,a0101s,num,taskid,task_id,a0101s_,false);
	                    }else{
	                    	taskid += task_id+",";
	                    }
		            }
	            //}
	        }
	        catch(Exception ex)
	        {
	            ex.printStackTrace();
	            throw GeneralExceptionHandler.Handle(ex);
	        }finally {
	        	PubFunc.closeDbObj(rSet);
	        }
	        return taskid;
	    }

		/**
		 * 复制流程以及任务
		 * @param ins_id
		 * @param a0100forsql
		 * @param a0101s
		 * @param num
		 * @param taskid
		 * @param task_id
		 * @param a0101s_ 
		 * @param bHave_reject 
		 * @return
		 */
		private String copyRecord(String ins_id, String a0100forsql, String a0101s, int num, String taskid, String task_id, String a0101s_, boolean bHave_reject) {
			RowSet rSet = null;
			RowSet rSet1 = null;
			try {
				IDGenerator idg=new IDGenerator(2,this.conn);
		    	//复制当前taskid之前的流程任务
		    	String sqlWithIns = "select * from t_wf_task where ins_id="+ins_id +" order by task_id";
		    	String sqlWithInsObjlink = "select two.* from t_wf_task_objlink two,templet_"+this.frontProperty.getTabId()+" t  "
		    			+ "where two.ins_id=t.ins_id and two.seqnum=t.seqnum and t.ins_id="+ins_id ;
		    			
		    	if(this.paramBo.getInfor_type()==1) {
		    		sqlWithInsObjlink+=" and lower(t.basepre"+Sql_switcher.concat()+"t.a0100) in("+a0100forsql.toLowerCase()+")";
		    	}else if(this.paramBo.getInfor_type()==2)
		    		sqlWithInsObjlink+=" and lower(t.b0110) in("+a0100forsql.toLowerCase()+")";
		    	else if(this.paramBo.getInfor_type()==3)
		    		sqlWithInsObjlink+=" and lower(t.e01a1) in("+a0100forsql.toLowerCase()+")";
		    			
		    	ArrayList<LazyDynaBean> dynaList = dao.searchDynaList(sqlWithInsObjlink);
		    	ArrayList task_list=new ArrayList();
		    	//发起节点 特殊处理记录下发起节点的task_id
		    	ArrayList<LazyDynaBean> startTaskList=dao.searchDynaList(sqlWithIns);
		    	LazyDynaBean startBean=startTaskList.get(0);
		    	task_list.add(startBean.get("task_id")+"");
		    	for(LazyDynaBean bean:dynaList) {
		    	    task_list.add(bean.get("task_id")+"");
		    	}
		    	//先复制流程实例
		    	String sql_ins = "select * from t_wf_instance where ins_id="+ins_id;
		    	rSet = dao.search(sql_ins);
		    	RecordVo insvo = null;
		    	int insid = 0;
		    	String stopic = "人";
		    	if(paramBo.getInfor_type()==2 || paramBo.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
		    		stopic="条记录";
		    	if(rSet.next()) {
		    		insvo = new RecordVo("t_wf_instance");
		    		insid = Integer.parseInt(idg.getId("wf_instance.ins_id"));
		    		insvo.setInt("ins_id", insid);
		    		insvo.setString("name", paramBo.getTable_vo().getString("name")+"("+a0101s_+"共"+num+stopic+")");
		    		
		    		insvo.setDate("start_date",rSet.getTimestamp("start_date"));
		    		insvo.setDate("end_date",rSet.getTimestamp("end_date"));
		    		if(rSet.getString("finished")!=null&&!"".equals(rSet.getString("finished"))) 
		    			insvo.setString("finished",rSet.getString("finished"));
		    		insvo.setInt("tabid", rSet.getInt("tabid"));
		    		if(rSet.getString("template_type")!=null&&!"".equals(rSet.getString("template_type"))) 
		    			insvo.setInt("template_type", rSet.getInt("template_type"));
		    		if(rSet.getString("bfile")!=null&&!"".equals(rSet.getString("bfile"))) 
		    			insvo.setInt("bfile", rSet.getInt("bfile"));
		    		insvo.setInt("actor_type", rSet.getInt("actor_type"));
		    		insvo.setString("actorid",rSet.getString("actorid"));
		    		insvo.setString("actorname",rSet.getString("actorname"));
		    		insvo.setString("b0110",rSet.getString("b0110"));
		    		dao.addValueObject(insvo);
		    	}
		    	
		    	rSet=dao.search(sqlWithIns);
		    	RecordVo taskvo = null;
		    	RecordVo taskobjlinkvo = null;
		    	ArrayList taskList = new ArrayList();
		    	ArrayList taskobjlinkList = new ArrayList();
		    	HashMap seqnumMap = new HashMap();
		    	String taskid_new = "";
		    	String updateSql = "update t_wf_task_objlink set ins_id=?,task_id=? where seqnum=? and ins_id=? and task_id=? and node_id=? and tab_id=?";
		    	int num_ = 0;
		    	while(rSet.next()) {
		    	    //复制流程时过滤掉不属于选中数据的流程
		    	    if(!task_list.contains(rSet.getInt("task_id")+"")) {
		    	        continue;
		    	    }
		    		taskvo=new RecordVo("t_wf_task");
		            String taskid_ = rSet.getString("task_id");
		            int newtaskid = 0;
		            if(task_id.equals(taskid_)) {
		            	taskid_new = idg.getId("wf_task.task_id");
		            	newtaskid = Integer.parseInt(taskid_new);
		            }else
		            	newtaskid = Integer.parseInt(idg.getId("wf_task.task_id"));
		            if(num_==0) {
		    	    	String sqlforfile = "select 1 from t_wf_file where ins_id="+ins_id;
		    	    	String sqlforfileupd = "update t_wf_file set ins_id="+insid+" where ins_id="+ins_id;
		    	    	String sqlforfile_b = "";
		            	//修改临时表数据将对应人的ins_id和task_id 改成新的
		    	    	String sqlfortem = "update templet_"+this.frontProperty.getTabId()+" set ins_id="+insid+",task_id="+newtaskid+" where ins_id="+ins_id;
		    	    	if(this.paramBo.getInfor_type()==1) {
		    	    		sqlfortem+=" and lower(basepre"+Sql_switcher.concat()+"a0100) in("+a0100forsql.toLowerCase()+")";
		    	    		sqlforfile_b+=" and lower(basepre"+Sql_switcher.concat()+"objectid) in("+a0100forsql.toLowerCase()+")";
		    	    	}else if(this.paramBo.getInfor_type()==2) {
		    	    		sqlfortem+=" and lower(b0110) in("+a0100forsql.toLowerCase()+")";
		    	    		sqlforfile_b+=" and lower(objectid) in("+a0100forsql.toLowerCase()+")";
		    	    	}else if(this.paramBo.getInfor_type()==3) {
		    	    		sqlfortem+=" and lower(e01a1) in("+a0100forsql.toLowerCase()+")";
		    	    		sqlforfile_b+=" and lower(objectid) in("+a0100forsql.toLowerCase()+")";
		    	    	}
		    	    	dao.update(sqlfortem);
		    	    	//修改临时表对应的人的附件对应的数据的ins_id（t_wf_file）
		    	    	rSet1 = dao.search(sqlforfile+sqlforfile_b);
		    	    	if(rSet1.next()) {
		    	    		dao.update(sqlforfileupd+sqlforfile_b);
		    	    	}
		            }
		            
		            taskvo.setInt("task_id",newtaskid);
		            String task_topic = rSet.getString("task_topic");
		            if(task_topic.indexOf("共0")==-1)
		            	task_topic = paramBo.getTable_vo().getString("name")+"("+a0101s_+"共"+num+stopic+")";
		            taskvo.setString("task_topic",task_topic);
		            taskvo.setInt("node_id",rSet.getInt("node_id"));
		            taskvo.setString("actorid",rSet.getString("actorid"));
		            taskvo.setString("actor_type",rSet.getString("actor_type"));
		            taskvo.setString("actorname",rSet.getString("actorname"));
		            taskvo.setInt("ins_id",insid);
		            taskvo.setDate("start_date",rSet.getTimestamp("start_date"));
		            taskvo.setDate("end_date",rSet.getTimestamp("end_date"));
		            taskvo.setString("task_type",rSet.getString("task_type"));
		            taskvo.setString("task_pri",rSet.getString("task_pri"));
		            taskvo.setString("bs_flag",rSet.getString("bs_flag"));
		            if(rSet.getString("bread")!=null&&!"".equals(rSet.getString("bread"))) 
		            	taskvo.setInt("bread",rSet.getInt("bread"));
		            taskvo.setString("a0100",rSet.getString("a0100"));
		            taskvo.setString("a0101",rSet.getString("a0101"));
		            taskvo.setString("a0100_1",rSet.getString("a0100_1"));
		            taskvo.setString("a0101_1",rSet.getString("a0101_1"));
		            taskvo.setString("sp_yj",rSet.getString("sp_yj"));
		            taskvo.setString("content",rSet.getString("content"));
		            taskvo.setString("state",rSet.getString("state"));
		            taskvo.setString("task_state",rSet.getString("task_state"));
		            taskvo.setString("url_addr",rSet.getString("url_addr"));
		            taskvo.setString("params",rSet.getString("params"));
		            taskvo.setString("appuser",rSet.getString("appuser"));
		            if(rSet.getString("flag")!=null&&!"".equals(rSet.getString("flag"))) 
	                	taskobjlinkvo.setInt("flag",rSet.getInt("flag"));
		            /**
		             * 如何处理之前的task_id
		             */
		            if(rSet.getString("pri_task_id")!=null&&!"".equals(rSet.getString("pri_task_id"))) { 
		            	int pri_task_id = rSet.getInt("pri_task_id");
		            	int cha = Integer.parseInt(taskid_)-pri_task_id;
		            	//依据复制数据的规律生成pri_task_id
		            	int pri_task_id_new = newtaskid-cha;
		            	taskvo.setInt("pri_task_id",pri_task_id_new);
		            }
		            if(rSet.getString("task_id_pro")!=null&&!"".equals(rSet.getString("task_id_pro"))) { 
		            	String task_id_pro = rSet.getString("task_id_pro");
		            	String[] taskidproarr =  task_id_pro.split(",");
		            	String task_id_pro_new = "";
		            	for(int i=0;i<taskidproarr.length;i++) {
		            		String id =  taskidproarr[i];
		            		if(StringUtils.isNotBlank(id)) {
		            			int cha = Integer.parseInt(taskid_)-Integer.parseInt(id);
		            			task_id_pro_new += ","+(newtaskid-cha);
		            		}
		            	}
		            	taskvo.setString("task_id_pro",task_id_pro_new);
		            }
		            taskvo.setString("originate_id",taskid_);
		            taskList.add(taskvo);
		            for(int j=0;j<dynaList.size();j++) {
		            	LazyDynaBean dyna = (LazyDynaBean) dynaList.get(j);
		            	if(taskid_.equals(dyna.get("task_id"))) {
		            		ArrayList paramlist = new ArrayList();
		            		paramlist.add(insid);
		            		paramlist.add(newtaskid);
		            		paramlist.add((String)dyna.get("seqnum"));
		            		paramlist.add(ins_id);
		            		paramlist.add(dyna.get("task_id"));
		            		paramlist.add(Integer.parseInt((String)dyna.get("node_id")));
		            		paramlist.add(Integer.parseInt((String)dyna.get("tab_id")));
		            		taskobjlinkList.add(paramlist);
		            	}
		            }
		            num_++;
		    	}
		    	dao.addValueObject(taskList);
		    	dao.batchUpdate(updateSql, taskobjlinkList);
		    	taskid+=Integer.parseInt(taskid_new)+",";
		    	String[] a0101arr = a0101s.split(",");
		    	if("2".equals(this.applyFlag)&&a0101arr.length==num&&bHave_reject) {
		    		ArrayList tempList=new ArrayList();
					Timestamp dateTime = new Timestamp((new Date()).getTime());
					tempList.add(dateTime);
					tempList.add(task_id);
		    		dao.update("update t_wf_task set end_date=?,task_state='5' where task_id=?",tempList);
		    		dao.update("update t_wf_task_objlink set state=1 where task_id="+task_id);
		    	}
		    	//修改原始标题
	    		this.updateOldTopic(ins_id,a0101s,task_id,num,task_list,bHave_reject);
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rSet);
				PubFunc.closeDbObj(rSet1);
			}
			return taskid;
		}

		/**
		 * 修改原始标题
		 * @param ins_id
		 * @param a0101s
		 * @param taskId 
		 * @param num
		 * @param task_list 
		 * @param bHave_reject
		 */
		private void updateOldTopic(String ins_id, String a0101s,String taskId, int num, ArrayList task_list, boolean bHave_reject) {
			RowSet rSet = null;
			try {
				String stopic = "人";
		    	if(paramBo.getInfor_type()==2 || paramBo.getInfor_type()==3)//如果是单位管理机构调整 或 岗位管理机构调整
		    		stopic="条记录";
				String sql = "select name from t_wf_instance where ins_id="+ins_id;
				rSet=dao.search(sql);
				String[] a0101arr = a0101s.split(",");
				if(rSet.next()) {
					String name = rSet.getString("name");
					for(int i=0;i<a0101arr.length;i++) {
						String a0101 = a0101arr[i];
						if(name.indexOf(a0101)!=-1) {
							name=name.replace(a0101+",","");
						}
					}
					int index = name.lastIndexOf("共");
					int index2 = name.lastIndexOf(stopic);
					String n = name.substring(index+1,index2);
					if(index!=-1) {
						name=name.substring(0,index+1)+(Integer.parseInt(n)-a0101arr.length)+stopic+")";
					}
					dao.update("update t_wf_instance set name='"+name+"' where ins_id="+ins_id);
				}
				sql = "select task_topic,task_id,node_id from t_wf_task where ins_id="+ins_id;
				rSet=dao.search(sql);
				while(rSet.next()) {
				    //去除非当前流程涉及到的节点不修改topic
				    if(!task_list.contains(rSet.getInt("task_id")+"")) {
				        continue;
				    }
					String task_topic = rSet.getString("task_topic");
					String task_id = rSet.getString("task_id");
					if(task_topic.indexOf("共0"+stopic)==-1) {
						int replaceNum = 0;
						for(int i=0;i<a0101arr.length;i++) {
							String a0101 = a0101arr[i];
							if(task_topic.indexOf(a0101)!=-1) {
								task_topic=task_topic.replace(a0101+",","");
								replaceNum++;
							}
						}
						int index = task_topic.lastIndexOf("共");
						int index2 = task_topic.lastIndexOf(stopic);
						String n = task_topic.substring(index+1,index2);
						if(index!=-1) {
							task_topic=task_topic.substring(0,index+1)+(Integer.parseInt(n)-replaceNum)+stopic+")";
						}
						if("2".equals(this.applyFlag)&&a0101arr.length==num&&taskId.equals(task_id)&&bHave_reject) {
							continue;
						}
						dao.update("update t_wf_task set task_topic='"+task_topic+"' where task_id="+task_id);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeDbObj(rSet);
			}
		}
		
		
		
		
		
		

		
		/**
		 * 判断流程某节点下的角色是否是自己审批
		 * @param tabId 模板ID
		 * @param nodeid 节点ID
		 * @param task_id 任务ID
		 * @param dao
		 * @param actorid 审批人
		 * @param ins_id 实例ID
		 * @param tablebo
		 * @return
		 */
		 private boolean  roleIsSelf(int tabId,String nodeid,int task_id,ContentDAO dao,String actorid,int ins_id,TemplateTableBo tablebo )
		    {
		    	boolean isSelf=false;
		        String userStr="";
		        RowSet rowSet2=null;
		        try
		        { 
		        	String scope_field="";
					String containUnderOrg="0"; //包含下属机构
					ArrayList valueList=new ArrayList();
					valueList.add(new Integer(tabId));
					valueList.add(new Integer(nodeid));
					rowSet2=dao.search("select * from t_wf_node where tabid=? and node_id=?",valueList);
					String ext_param="";
					if(rowSet2.next())
							ext_param=Sql_switcher.readMemo(rowSet2,"ext_param"); 
					if(ext_param!=null&&ext_param.trim().length()>0)
					{
							Document doc=PubFunc.generateDom(ext_param); 
							String xpath="/params/scope_field";
							XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
							List childlist=findPath.selectNodes(doc);
							if(childlist.size()==0){
								xpath="/param/scope_field";
								 findPath = XPath.newInstance(xpath);// 取得符合条件的节点
								 childlist=findPath.selectNodes(doc);
							}
							if(childlist!=null&&childlist.size()>0)
							{
								for(int j=0;j<childlist.size();j++)
								{
									Element element=(Element)childlist.get(j);
									if(element!=null&&element.getText()!=null&&element.getText().trim().length()>0)
									{
										scope_field=element.getText().trim();
										if(element.getAttribute("flag")!=null&& "1".equals(element.getAttributeValue("flag").trim()))
											containUnderOrg="1";
									}
								}
							}
					}
					if(scope_field==null)
							scope_field=""; 
		            RowSet rowSet=dao.search("select * from t_sys_role where role_id=?",Arrays.asList(new Object[] {actorid}));
		            if(rowSet.next())
		            {
		                    int role_property=rowSet.getInt("role_property");
		                    if(role_property==9||role_property==10||role_property==11||role_property==12||role_property==13)
		                    {
		                            WorkflowBo workflowBo=new WorkflowBo(conn,tabId,this.userView);
		                         
		                            LazyDynaBean bean=workflowBo.getFromNodeid_role(ext_param,ins_id,dao,task_id,"0","");
		                            String sql=""; 
		                            HashMap a_map=workflowBo.getSuperSql(role_property,tablebo.getRelation_id(),bean);
		                            if(a_map.size()==0)
		                                        throw new GeneralException(ResourceFactory.getProperty("general.template.wf_node.nosprelation"));
		                             sql=(String)a_map.get("sql");  
		                             rowSet=dao.search(sql);
		                             if(rowSet.next()){
		                                    if("1".equals(rowSet.getString("actor_type")))  //自助
		                                    {
		                                        if((this.userView.getDbname()+this.userView.getA0100()).equalsIgnoreCase(rowSet.getString("mainbody_id")))
		                                        	isSelf=true;
		                                    }else{ //业务用户
		                                        if(this.userView.getUserName().equalsIgnoreCase(rowSet.getString("mainbody_id")))
		                                        	isSelf=true;
		                                    }
		                                    
		                              }
		                    }
		                    else //非特殊角色
		                    {
		                    	String operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
		                    	if ("1".equals(role_property)){//部门领导
		    						String e0122=this.userView.getUserDeptId();
		    						if (e0122!=null &&e0122.length()>0){
		    							//operOrg="UN"+e0122;//不知道为什么要写成UN
		    							operOrg="UM"+e0122;//改成UM，应该是对的 20170930
		    						}
		    						else {
		    							operOrg="";
		    						}
		    					}
		    					else if ("6".equals(role_property)){//单位领导
		    						String b0110=this.userView.getUserOrgId();
		    						if (b0110!=null &&b0110.length()>0){
		    							operOrg="UN"+b0110;
		    						}
		    						else {
		    							operOrg="";
		    						}
		    					}
		                    	
		                    	if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1||"parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)) //单独处理
		        				{
		                    		if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
		                    			  isSelf=true;
		        				}
		                    	else
		                    	{
		                    		if("UN`".equalsIgnoreCase(operOrg)||scope_field.trim().length()==0)
		                    		{
		                    			isSelf=true;
		                    			ArrayList tempList=new ArrayList();
										Timestamp dateTime = new Timestamp((new Date()).getTime());
										tempList.add(dateTime);
										tempList.add(userView.getUserName()); 
										tempList.add(task_id);
		                    			dao.update("update t_wf_task_objlink set locked_time=?,username=? where   task_id=?",tempList );
		                    		}
		                    		else
		                    		{
		                    			String from_where_sql="";
		                    			String[] temps=scope_field.split("_");
		    							String itemid=temps[0].toLowerCase(); 
		    							FieldItem _item=DataDictionary.getFieldItem(itemid);
		    							String codesetid=_item.getCodesetid();
		    							if(operOrg!=null && operOrg.length() > 3)
		    							{
		    								StringBuffer tempSql = new StringBuffer(""); 
		    								String[] temp = operOrg.split("`");
		    								for (int i = 0; i < temp.length; i++) {
		    									if("1".equals(containUnderOrg))
		    									{
		    										tempSql.append(" or t."+scope_field+" like '" + temp[i].substring(2)+ "%'");		
		    									}
		    									else
		    									{
		    										if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2)))
		    											tempSql.append(" or t."+scope_field+"='" + temp[i].substring(2)+ "'");
		    										else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2)))
		    											tempSql.append(" or t."+scope_field+" like '" + temp[i].substring(2)+ "%'");				
		    									}
		    								}
		    								
		    								if(tempSql.length()==0)
		    								{
		    									if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制 2014-04-01 dengcan
		    									{
		    										if("UN".equalsIgnoreCase(codesetid))
		    										{
		    											if("1".equals(containUnderOrg))
		    												tempSql.append(" or t."+scope_field+" like '"+userView.getUserOrgId()+"%'");
		    											else
		    												tempSql.append(" or t."+scope_field+"='"+userView.getUserOrgId()+"'");
		    										}
		    										else if ("UM".equalsIgnoreCase(codesetid)){
		    										    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
		    										        tempSql.append(" or t."+scope_field+" like '"+userView.getUserDeptId()+"%'");
		    										    }else{
		    										        tempSql.append(" or 1=2 ");
		    										    }
		    											
		    										}
		    									}
		    								}
		    								
		    								if(tempSql.toString().trim().length()==0)
		    									tempSql.append(" or 1=2 ");
		    								
		    								from_where_sql+=" and ( " + tempSql.substring(3) + " ) ";
		    							}
		    							else
		    							{
		    								if(userView.getA0100()!=null&&userView.getA0100().trim().length()>0) // 2014-04-01 dengcan
		    								{
		    									if("UN".equalsIgnoreCase(codesetid))
		    									{
		    										if("1".equals(containUnderOrg))
		    											from_where_sql+=" and t."+scope_field+" like '"+userView.getUserOrgId()+"%'";
		    										else
		    											from_where_sql+=" and t."+scope_field+"='"+userView.getUserOrgId()+"'";
		    									}
		    									else if ("UM".equalsIgnoreCase(codesetid)){
		    									    if(userView.getUserDeptId()!=null&&userView.getUserDeptId().trim().length()>0){
		    									    	from_where_sql+=" and t."+scope_field+" like '"+userView.getUserDeptId()+"%'";
		    									    }else{
		    									    	from_where_sql+=" and 1=2 ";
		    									    }
		    									}
		    								}
		    								else
		    									from_where_sql+=" and 1=2 ";
		    							}
		    							
		    							rowSet=dao.search("select twt.* from templet_"+tabId+" t,t_wf_task_objlink twt where   twt.seqnum=t.seqnum and twt.ins_id=t.ins_id     and t.ins_id=? and twt.task_id=? and "+Sql_switcher.isnull("twt.state","0")+"=0   "+from_where_sql,Arrays.asList(new Object[] {new Integer(ins_id),new Integer(task_id)}));
		    							int n=0;
		    							HashMap dataMap=new HashMap();
		    							ArrayList updList=new ArrayList();
		    							while(rowSet.next())
		    							{
		    								n++;
		    								/*
		    								int num=rowSet.getInt(1);
		    								if(num>0)
		    									 isSelf=true;
		    									 */
		    								int node_id=rowSet.getInt("node_id");
		    								if(dataMap.get(node_id+task_id)==null)
		    								{
		    									dao.update("update t_wf_task_objlink set special_node=1 where task_id="+task_id+" and node_id="+node_id);
		    									dataMap.put(node_id+task_id,"1");
		    								}
		    								
		    								ArrayList tempList=new ArrayList();
											Timestamp dateTime = new Timestamp((new Date()).getTime());
											tempList.add(dateTime);
											tempList.add(userView.getUserName());
											tempList.add(rowSet.getString("seqnum"));
											tempList.add(new Integer(rowSet.getString("task_id")));
											updList.add(tempList);
		    							} 
		    							if(n>0)
		    							{
		    								if(updList.size()>0)
		    								{ 
		    									dao.batchUpdate("update t_wf_task_objlink set locked_time=?,username=? where seqnum=? and task_id=?",updList );
		    								}
		    								isSelf=true;
		    							}
		    							
		    							
		    							
		                    		} 
		                    	} 
		                    }
		                     
		                }
		                if(rowSet!=null)
		                    rowSet.close();
		            
		        }
		        catch(Exception e)
		        {
		            e.printStackTrace();
		        }
		        finally
		        {
		        	PubFunc.closeDbObj(rowSet2);
		        }
		        return isSelf;
		    }
		
		 
		 
		 	/**
		 	 * 相邻节点如是自己审批，
		 	 * 限定范围：自动执行批准操作。 如下一节点是与或发散里的某一节点是自己暂不支持自动审批
		 	 * @param ins
		 	 * @param ins_vo
		 	 * @param content 审批内容
		 	 * @param pri  优先级
		 	 * @param sp_yj 审批意见（同意 | 不同意）
		 	 * @param actorName 审批人ID
		 	 * @param otherParaMap
		 	 * @param tablebo
		 	 */
		 	public RecordVo autoApplyTask(WF_Instance ins, RecordVo ins_vo,String content,String pri,String sp_yj,String actorName,HashMap otherParaMap,TemplateTableBo tablebo)
		 	{
		 		WF_Actor wf_actor=null; 
		 		try
		 		{
		 		
		            //获得下一个流程节点任务
		            RecordVo nextTaskVo=ins.getTask_vo();  
		            if(nextTaskVo==null)
		            	return ins_vo;
		            

		            ArrayList nextTaskVoList=ins.getNextTaskVoList();
		            if(nextTaskVoList.size()==0||nextTaskVoList.size()==1)
		            {
		            	nextTaskVoList=new ArrayList();
		            	nextTaskVoList.add(nextTaskVo);
		            }
		            for(int i=0;i<nextTaskVoList.size();i++)
		            {
		            	nextTaskVo=(RecordVo)nextTaskVoList.get(i);
		            
		            
			            int whileNum=0;
			            int ins_id=ins_vo.getInt("ins_id");
			            int task_id=nextTaskVo.getInt("task_id");
			            int tabId=ins_vo.getInt("tabid");
			            //校验必填项 基于下面是个循环，故保留该记录。
			            boolean isMustFill = this.checkMustFill(tablebo,task_id);
			            if(isMustFill) {
			            	break;
			            }
			            while(true)
			            {
			            	whileNum++;
			            	if(whileNum>10)//最多连续10个自动提交，防止死循环
			            		break;
			            	if(nextTaskVo==null)
			            		break;
			            	int _task_id=nextTaskVo.getInt("task_id");
			            	if(task_id==_task_id&&whileNum>1)
			            		break;
			            	String actor_type=nextTaskVo.getString("actor_type");
			            	String actorid=nextTaskVo.getString("actorid"); 
			        		ArrayList rolelist= userView.getRolelist();//角色列表
			        		boolean isSelf=false; //判断下一个节点是否是自己
			        		
			            	if("2".equals(actor_type))
			            	{
			            		                       		
			            		if(rolelist.contains(actorid))
			            		{ 
			            			 isSelf=roleIsSelf(tabId,nextTaskVo.getInt("node_id")+"",_task_id,dao,actorid,ins_id,tablebo ); 
			            		} 
			            	}
			            	else if("1".equals(actor_type)&&actorid.equalsIgnoreCase(this.userView.getDbname()+this.userView.getA0100()))
			            		isSelf=true;
			            	else if("4".equals(actor_type)&&(actorid.equalsIgnoreCase(this.userView.getUserName())||actorid.equalsIgnoreCase(this.userView.getS_userName())))
			            		isSelf=true;
			            	
			            	if(isSelf)
			    			{
			    				 wf_actor = new WF_Actor(actorid, nextTaskVo.getString("actor_type"));
			    		         wf_actor.setContent("【自动审批】 "+content);
			    		         wf_actor.setEmergency(pri);
			    		         wf_actor.setSp_yj(sp_yj);
			    		         wf_actor.setActorname(actorName);
			    		         wf_actor.setBexchange(false);
			    		        
			    		         dao.update("update t_wf_task_objlink set submitflag=1 where task_id="+_task_id); 
			    		         
			    		         //校验必填项
			    		         /**syl 20190921 bug 53129 深圳报业集团:发起人与第一个审批人是同一个人员时进行了自动审批 
			    		          * 原因：校验时，经过对比：select a0101_1,a0100,basepre ,ins_id ,b0110_2,e0122_2,e01a1_2,a0304_2,a0306_2 from templet_44 where  exists (select null from t_wf_task_objlink where templet_44.seqnum=t_wf_task_objlink.seqnum and templet_44.ins_id=t_wf_task_objlink.ins_id   and task_id in (2390) and tab_id=44  and submitflag=1  and (state is null or  state=0 )  and (ISNULL(special_node,0)=0  or ( ISNULL(special_node,0)=1 and (lower(username)='ces' or lower(username)='' ) ) )) 
			    		          * 该sql查询时，其submitflag 不为1 所以查询不到必填记录。故当发起节点-或发散（开始分支）-节点A或者节点B 这种情况下的相邻节点需要在此之后才可以提交*/
						         isMustFill = this.checkMustFill(tablebo,task_id);
						         if(isMustFill) {
						        	 break;
						         }
			    		         // 待办信息 
			                     String _pendingCode = "HRMS-" + PubFunc.encrypt(_task_id+"");
			                     otherParaMap.put("pre_pendingID", _pendingCode);
			                     ins.setOtherParaMap(otherParaMap);
			                     ins.setObjs_sql(ins.getObjsSql(ins_id, _task_id, 3, ""+tabId, this.userView, ""));// 作用是 
			                     ins.setIns_id(ins_id);       
			    		         
			    				ins.createNextTask(ins_vo, wf_actor,nextTaskVo.getInt("task_id"), this.userView);// 在这个函数里面执行了expDataIntoArchive()
			                    ins_vo =dao.findByPrimaryKey(ins_vo);//重新获取vo lis 21060825
			                    //更新当前任务的bread
			                    dao.update("update t_wf_task set bread=1 where task_id="+_task_id); 
			                    nextTaskVo=ins.getTask_vo();
			                   
			                    if(ins_vo.getString("finished").equalsIgnoreCase(String.valueOf(NodeType.TASK_FINISHED))){//下一个流程结点是结束结点
			                    	break; 
			                    }
			                    else
			                    	 ins.updateApproveOpinion(ins_vo, wf_actor, this.userView,_task_id);
			    			}
			    			else 
			    				break; 
			            	
			            }
		            }
		          
		 		}
		 		catch(Exception e)
		 		{
		 			e.printStackTrace();
		 		}
		 		return ins_vo;
		 		
		 	}

		 	/**
		 	 * 检验必填，如果有必填项没有填写，则直接跳过不在自动审批
		 	 * @param tablebo
		 	 * @param task_id
		 	 * @return
		 	 */
			private boolean checkMustFill(TemplateTableBo tablebo, int task_id) {
				boolean isMustFill = false;
				try {          
			        String tableName="templet_"+this.frontProperty.getTabId();
					ArrayList fieldlist = tablebo.getAllFieldItem("0");
					tablebo.checkMustFillItem(tableName, fieldlist,task_id);
				} catch (Exception e) {
					String message=e.toString();
					if(message.indexOf("指标未填写")!=-1){
						isMustFill = true;
					}
				}
				return isMustFill;
			}

			/**
			 * 通过whl获得人员
			 * @param infor_type
			 * @param ins_id
			 * @param whl
			 * @return
			 */
			public ArrayList getPersonlist(int infor_type, int ins_id, String tabId, String whl) {
		        ArrayList list = new ArrayList();
		        RowSet rs = null;
		        try{
		            ContentDAO dao = new ContentDAO(this.conn);
		            StringBuffer buf = new StringBuffer("");
		            String tableName = "templet_"+tabId;
		            ArrayList paramList = new ArrayList();
		            String searchcode = "basepre,a0100";
		            if(infor_type==2)
		            	searchcode = "b0110";
		            else if(infor_type==3) 
		            	searchcode = "e01a1";
		            buf.append("select ");
		            buf.append(searchcode);
		            buf.append(" from ");
		            
		            buf.append(tableName);
                    buf.append(" where 1=1 ");
                    buf.append(" and exists (select null from t_wf_task_objlink where " + tableName + ".seqnum=t_wf_task_objlink.seqnum and " + tableName + ".ins_id=t_wf_task_objlink.ins_id  ");
                    buf.append(" and  submitflag=1 and  ins_id = ?");
                    buf.append(" and (state is null or state<>3) ) ");
                    buf.append(whl);
                    paramList.add(ins_id);
                    rs = dao.search(buf.toString(),paramList);
                    while(rs.next()){
                        ArrayList templist = new ArrayList();
                        if(infor_type==1){
	                        String basepre = rs.getString("basepre");
	                        String a0100 = rs.getString("a0100");
	                        templist.add(basepre);
	                        templist.add(a0100);
                        }
                        else if(infor_type==2){
                        	String b0110 = rs.getString("b0110");
		                    templist.add(b0110);
                        }
                        else if(infor_type==3){
                        	String e01a1 = rs.getString("e01a1");
		                    templist.add(e01a1);
                        }
                        list.add(templist);
                    }
		        }catch(Exception e){
		            e.printStackTrace();
		        }finally {
		        	PubFunc.closeDbObj(rs);
		        }
		        return list;
			}
		
}
