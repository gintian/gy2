package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.sys.param.Sys_Infom_Parameter;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 批量导入照片前准备工作
 * 主要查询主集A01的所有字符串指标
 * @author tianye
 * @date 2013-5-29
 */
public class BatchPOMImportBefore extends IBusiness{

	public void execute() throws GeneralException {
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList setList = new ArrayList();
			ArrayList itemList = new ArrayList();
		
			String fieldSetId = (String)this.getFormHM().get("selectSetid");	
			
			if(StringUtils.isNotEmpty(fieldSetId)) {
			    String itemsql = "select itemid,itemdesc  from fielditem where useflag <> 0 and fieldsetid ='"+fieldSetId+"'";
			    try {
			        this.frowset = dao.search(itemsql);
			        while(this.frowset.next()){
			            String itemid = this.frowset.getString("itemid");
			            String itemdesc = this.frowset.getString("itemdesc");
			            CommonData dataobj = new CommonData();
			            dataobj = new CommonData(itemid,itemdesc);
			            itemList.add(dataobj);
			        }
			        this.getFormHM().put("mulFileItemlist",itemList);
			    } catch (SQLException e) {
			        e.printStackTrace();
			    }
			} else {
				String setsql = "select fieldsetid,fieldsetdesc,changeflag,customdesc,multimedia_file_flag from fieldset where useflag <> 0 and fieldsetid like 'A%' order by "+com.hrms.hjsj.utils.Sql_switcher.substr("fieldsetid", "1", "1")+",Displayorder";
				String firstSetId = "";
				boolean bool = false;
				try {
					this.frowset = dao.search(setsql);
					while(this.frowset.next()){
						
						if(this.frowset.getString("multimedia_file_flag")==null || !"1".equals(this.frowset.getString("multimedia_file_flag")))
							continue;
						if("0".equals(this.userView.analyseTablePriv(this.frowset.getString("fieldsetid"))))
					        continue;
						if(!bool){
							firstSetId = this.frowset.getString("fieldsetid");
							bool = true;
						}
						
						CommonData dataobj = new CommonData();
						String setid = this.getFrowset().getString("fieldsetid");
						String setdesc ="";
						if(this.getFrowset().getString("customdesc")!=null&&this.getFrowset().getString("customdesc").length()>0)
							setdesc= this.getFrowset().getString("customdesc");
						else
							setdesc= this.getFrowset().getString("fieldsetdesc");
						dataobj = new CommonData(setid,setdesc);
						setList.add(dataobj);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				ArrayList fielditemlist=DataDictionary.getFieldList(firstSetId,Constant.USED_FIELD_SET);
				if(fielditemlist!=null) {
					for(int i=0;i<fielditemlist.size();i++) {
				      FieldItem fielditem=(FieldItem)fielditemlist.get(i);
				      if("M".equals(fielditem.getItemtype()))
					    	continue;
					  if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid())))
					        continue;
					  CommonData dataobj = new CommonData();
					  dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					  itemList.add(dataobj);
				   }
			   }
				
				this.getFormHM().put("multimediaFilelist",setList);
				this.getFormHM().put("mulFileItemlist",itemList);
				
				//判断子集是否支持附件功能  1 表示支持
				if(setList!=null && setList.size()>0){
				    this.getFormHM().put("multimedia_file_flag","1");
				}
				
				
				//获取控制上传照片和多媒体大小配置信息
				Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
				String photo_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO,"MaxSize");
				photo_maxsize=photo_maxsize!=null&&photo_maxsize.length()>0?photo_maxsize:"0";
				if(Long.parseLong(photo_maxsize)<0){
				    photo_maxsize="0";
				}
				String multimedia_maxsize=sys_Infom_Parameter.getValue(Sys_Infom_Parameter.MULTIMEDIA,"MultimediaMaxSize");
				multimedia_maxsize=multimedia_maxsize!=null&&multimedia_maxsize.length()>0?multimedia_maxsize:"0";
				if(Long.parseLong(multimedia_maxsize)<0){
				    multimedia_maxsize="0";
				}
				
				String batchImportType = (String)((HashMap)this.getFormHM().get("requestPamaHM")).get("batchImportType");
				if(batchImportType==null|| "".equals(batchImportType)){
				    throw GeneralExceptionHandler.Handle(new Exception("不能确定批量导入的类型！"));
				}
				
				
				String sql = "";
				ArrayList infoSetList = null;//暂时使用infoSetList存放文件分类信息
				ArrayList infoFieldList = new ArrayList();//暂时使用infofieldlist存放命名规则指标信息
				if("multimedia".equals(batchImportType)){
				    sql="select flag,sortname from MediaSort where dbflag = '1'";
				    this.frowset=dao.search(sql);
				    infoSetList = new ArrayList();
				    while(this.frowset.next()){
				        String flag = this.frowset.getString("flag");
				        if(this.userView.isSuper_admin())
				        {
				            String datavalue = this.frowset.getString("sortname");
				            CommonData cd = new CommonData(flag,datavalue);
				            infoSetList.add(cd);
				        }else{
				            if(this.userView.hasTheMediaSet(flag))
				            {
				                String datavalue = this.frowset.getString("sortname");
				                CommonData cd = new CommonData(flag,datavalue);
				                infoSetList.add(cd);
				            }
				        }
				    }
				    if(infoSetList.size()==0)
				    {
				        throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.multimedia.noclassification")));
				    }
				}
				//获取唯一性指标
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
				if(onlyname != null){
				    sql = "select itemid from fielditem where itemid = '"+onlyname+"'";
				    this.frowset=dao.search(sql);
				    while(this.frowset.next()){
				        //如果有唯一性指标则默认显示该指标
				        this.getFormHM().put("ruleItemid", this.frowset.getString("itemid"));
				    }
				}
				//获取主集A01的所有字符串指标
				sql=" select itemid,itemdesc from fielditem where fieldsetid = 'A01' and useflag = 1 and itemtype = 'A'  and (codesetid = '0' or codesetid is null)";
				this.frowset=dao.search(sql);
				
				while(this.frowset.next()){
				    CommonData infoField = new CommonData(this.frowset.getString("itemid"),this.frowset.getString("itemdesc"));
				    infoFieldList.add(infoField);
				}
				
				this.getFormHM().put("infofieldlist", infoFieldList);//暂时使用infofieldlist存放命名规则指标信息
				if(infoSetList!=null){
				    this.getFormHM().put("infosetlist", infoSetList);
				}
				this.getFormHM().put("photo_maxsize", photo_maxsize);
				this.getFormHM().put("multimedia_maxsize", multimedia_maxsize);
				this.getFormHM().put("batchImportType", batchImportType);
				this.getFormHM().put("mulSetid", firstSetId);
			
			}
			
		}catch (SQLException e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		    
	}

}
