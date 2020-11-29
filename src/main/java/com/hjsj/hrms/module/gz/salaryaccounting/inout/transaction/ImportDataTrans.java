package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.io.InputStream;
import java.util.ArrayList;
/**
*原来的验证与导出是分开的，现在合并在一起。
* @ClassName: ValidateDataTrans 
* @Description: TODO(薪资发放导入数据) 
* @author lis 
* @date 2015-7-14 下午03:32:21
 */
public class ImportDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try
		{
			ArrayList<MorphDynaBean> oppositeItem1 = (ArrayList<MorphDynaBean>)this.getFormHM().get("oppositeItem");//对应指标
			ArrayList<MorphDynaBean> relationItem1 = (ArrayList<MorphDynaBean>)this.getFormHM().get("relationItem");  //关联指标
			ArrayList<String> oppositeItem = new ArrayList<String>();//对应指标
			ArrayList<String> relationItem = new ArrayList<String>();  //关联指标

			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//是否定义唯一性指标 0：没定义
			String onlyName = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标值
//唯一性指标改为从对应关系中取得。若对应关系中未设置唯一性指标，则不进行人员引入。 zhanghua 2017-6-24
			//String onlyNameText=(String)this.getFormHM().get("onlyname");//唯一性指标
//			if(onlyNameText.equalsIgnoreCase("null")||onlyNameText.trim().length()==0||uniquenessvalid.equals("0")){
//				onlyName="";
//			}
			String onlyNameText="";//唯一性指标
			
			String salaryid=(String)this.getFormHM().get("salaryid");//当前薪资id
			salaryid=SafeCode.decode(salaryid); //解码
			salaryid =PubFunc.decrypt(salaryid); //解密
			String appdate=(String)this.getFormHM().get("appdate");//当前薪资id
			appdate=PubFunc.decrypt(SafeCode.decode(appdate));
			
			String inputType=(String)this.getFormHM().get("inputType");
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String tableName=gzbo.getGz_tablename();
			
			SalaryInOutBo inOutBo = new SalaryInOutBo(this.getFrameconn(), Integer.valueOf(salaryid), this.userView);
			
			/**=============================解析对应关系======================================*/
			int z=0;
			for(MorphDynaBean bean:oppositeItem1){
				oppositeItem.add(bean.get("itemid") + "=" + bean.get("itemid1")+"="+bean.get("itemid1")+"_o"+z);//依次为 名称=代码=临时表中列名(代码+_0+下标)以此保证列名唯一
				
				z++;
			}
			z=0;
			for(MorphDynaBean bean:relationItem1){
				relationItem.add(bean.get("itemid") + "=" + bean.get("itemid2")+"="+bean.get("itemid2")+"_r"+z);//依次为 名称=代码=临时表中列名(代码+_r+下标)以此保证列名唯一
				if(!"0".equals(uniquenessvalid)&&bean.get("itemid2").toString().equalsIgnoreCase(onlyName)){
					onlyNameText=bean.get("itemid").toString();
				}
				z++;
			}
			if(onlyNameText.trim().length()!=0)//添加唯一性指标列
				relationItem.add(onlyNameText+"="+onlyName+"="+onlyName+"_only");
			else
				onlyName="";
			/**============================================================================*/
			if("1".equals(inputType)){//首次导入
				// 上传组件 vfs改造 
	            String fileid = (String)this.getFormHM().get("fileid");
	            InputStream input = VfsService.getFile(fileid);
				
				ArrayList<String> list=new ArrayList<String>();
				/* 取得导入文件中列指标列表  */
				ArrayList<CommonData> originalHeadList=inOutBo.getOriginalDataFiledList2(input);
				String msg="";
				ArrayList updateDateList=new ArrayList();    
				
				for(MorphDynaBean oppbean:oppositeItem1)
				{
					//判断数据对应指标中目标数据不能重复
					if(list.contains((String)oppbean.get("itemid1"))){
						FieldItem item1=DataDictionary.getFieldItem((String)oppbean.get("itemid1"));
						//不能重复作为目标数据
						throw GeneralExceptionHandler.Handle(new Exception(item1.getItemdesc()+ResourceFactory.getProperty("gz_new.gz_accounting.repeatdata")));
					}else
						list.add((String)oppbean.get("itemid1"));
					
					//只读权限的指标不能修改
					if("1".equalsIgnoreCase(this.userView.analyseFieldPriv((String)oppbean.get("itemid1"))))
					{
						FieldItem item=DataDictionary.getFieldItem((String)oppbean.get("itemid1"));
						msg+= ResourceFactory.getProperty("gz_new.gz_relationItme")+(String)oppbean.get("itemid")+"="+item.getItemdesc()+") 中 "+item.getItemdesc()+ResourceFactory.getProperty("gz_new.gz_onlyRead");
					}
				}
				if(msg.trim().length()>0)
					throw GeneralExceptionHandler.Handle(new Throwable(msg));
				
				
				/**=============================创建临时表======================================*/
				ArrayList<String> fieldList=new ArrayList<String>();//存储临时表列名
				fieldList.addAll(relationItem);
				fieldList.addAll(oppositeItem);
				
				
				ArrayList<String> tempTableList=new ArrayList<String>();//临时表包含的所有列
				ArrayList<String> uselessList=new ArrayList<String>();//未设置关联或更新的列
				String strhead="";
				int i=0;
				/** 将excel文件所有列都添加到临时表。如果已经选择对应或者关联，则以对应列id作为临时表列名，否则以f_useless+i 命名。
				 * 依照excel列顺序插入，导出时则按照此顺序导出。若同时设置了更新关系和对应关系。则导出时还原数据以对应关系为准。即出现重复列以第一个为准。
				 * */
				//tempTableList.addAll(fieldList);
				for(CommonData cd:originalHeadList){
					ArrayList<String> tlist=new ArrayList<String>();
					strhead="";
					for(String str:fieldList){
						if(str.split("=")[0].equals(cd.getDataName())){
							tlist.add(str);
						}
					}
					if(tlist.size()==0){
						tempTableList.add(cd.getDataName()+"=f_useless"+i+"=f_useless"+i);
						uselessList.add(cd.getDataName()+"=f_useless"+i+"=f_useless"+i);
					}
					else
						tempTableList.addAll(tlist);
					
					i++;
				}
				String  [] vfeild={"Nbase","A0100","A0000","B0110","E0122","A0101","A01Z0"};//默认字段。将其加入临时表列中以备添加使用。
				
				for(String strF:vfeild)
					tempTableList.add("temp_"+strF+"="+strF+"="+strF);

				
				String tempTableName=inOutBo.creatTempTable(tempTableList);//创建临时表
				/**===========================================================================*/
				// 由于现在vfs返回的流只能读一次 故这里再获取一遍
				input = VfsService.getFile(fileid);
				//插入临时表数据
				inOutBo.setExcelData(fieldList,uselessList,input,originalHeadList,Integer.valueOf(salaryid),tempTableName, "".equals(onlyName.trim())?"":onlyName+"_only");//excel数据插入临时表
				inOutBo.checkTempDate(tableName, tempTableName, relationItem, onlyName,appdate);
				String[] inputDataMsg =inOutBo.getInputDataMsg(tempTableName);//获取导入数据校验信息
				
				String errNum=String.valueOf(Integer.parseInt(inputDataMsg[0])+Integer.parseInt(inputDataMsg[3])+Integer.parseInt(inputDataMsg[4])+Integer.parseInt(inputDataMsg[5])+Integer.parseInt(inputDataMsg[6]));
				this.getFormHM().put("inputDataMsg",inputDataMsg);
				this.getFormHM().put("tempTableList",tempTableList);
				
				this.getFormHM().put("errNum", errNum);//0 问题
				this.getFormHM().put("updateNum", inputDataMsg[1]);//1更新 
				this.getFormHM().put("insertNum", inputDataMsg[2]);//2插入 
				//this.getFormHM().put("unNum", inputDataMsg[3]);//3无法匹配
				
				this.getFormHM().put("tempTableName", SafeCode.encode(PubFunc.encrypt(tempTableName)));
				// 关闭InputStream对象
				PubFunc.closeIoResource(input);
			}else if("2".equals(inputType)){//继续导入
				
				String Manager=gzbo.getManager();

				String tempTableName=(String)this.getFormHM().get("tempTableName");
				tempTableName=PubFunc.decrypt(SafeCode.decode(tempTableName));
				
				String count=(String)this.getFormHM().get("count");//当前薪资id
				count=PubFunc.decrypt(SafeCode.decode(count));
				
//				for(MorphDynaBean bean:oppositeItem1){
//					oppositeItem.add(bean.get("itemid1").toString());
//				}
//				for(MorphDynaBean bean:relationItem1){
//					String itemid2=bean.get("itemid2").toString();
//					relationItem.add( itemid2);
//				}
//				if(onlyName.trim().length()!=0){
//					relationItem.add(onlyName+"="+);
//				}
				int upNum=0;
				int inNum=0;
				try{
					if(oppositeItem.size()!=0)
						upNum=inOutBo.doUpdateImportData(oppositeItem, relationItem, tempTableName, tableName);//执行更新操作
					if(!"".equals(onlyName))
						inNum=inOutBo.doInsertImportData(oppositeItem, tempTableName, tableName, onlyName, Manager,appdate,count);//执行新增操作
				}catch(Exception e){
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}finally{
					inOutBo.dropTempTable(tempTableName);
				}
				
				String msg="导入完成，更新"+upNum+"条，新增"+inNum+"条！";
				this.getFormHM().put("msg",msg);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
