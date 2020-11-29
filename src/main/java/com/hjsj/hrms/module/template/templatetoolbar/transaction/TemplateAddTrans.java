package com.hjsj.hrms.module.template.templatetoolbar.transaction;

import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.module.template.utils.TemplateBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateItem;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.mortbay.util.ajax.JSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class TemplateAddTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		HashMap hm=this.getFormHM();
		String tab_id=(String)hm.get("tabid");
		String name=this.userView.getUserName()+"templet_"+tab_id;  		
		ContentDAO dao=null;
		try
		{
			String a0100=null;
			RecordVo vo=new RecordVo(name);
            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
            dao=new ContentDAO(this.getFrameconn());
			/**
			 * 查找变化前的历史记录单元格
			 * 保存时把这部分单元格的内容
			 * 过滤掉，不作处理
			 * */       
            TemplateParam param=new TemplateParam(this.getFrameconn(),this.userView,Integer.parseInt(tab_id));
			TemplateBo tablebo=new TemplateBo(this.getFrameconn(),this.userView,param);
			
            HashMap sub_map=tablebo.getHisModeSubCell();
			a0100=  idg.getId("rsbd.a0100");
			if(param.getInfor_type()==2||param.getInfor_type()==3)
				a0100="B"+a0100;
			if(param.getInfor_type()==1&&(param.getDest_base()==null||param.getDest_base().length()==0))
				throw new GeneralException("人员调入业务模板未定义目标库!");
			 HashMap lazyVoMap = new HashMap();
			if(param.getInfor_type()==1)
			{
				ArrayList dbList=DataDictionary.getDbpreList();
				String dbpre=param.getDest_base();
				for(int i=0;i<dbList.size();i++)
				{
					String pre=(String)dbList.get(i);
					if(pre.equalsIgnoreCase(param.getDest_base()))
						dbpre=pre;
				}
				vo.setString("a0100",a0100);
				vo.setString("basepre",dbpre);
				if(vo.hasAttribute("a0101_2"))
				{
					vo.setString("a0101_2", "--");
				}
				if(vo.hasAttribute("a0101_1"))
				{
					vo.setString("a0101_1", "--");
				}
				
				lazyVoMap.put("a0101_2", "--");
				lazyVoMap.put("name", "--");
				lazyVoMap.put("objectid_e", PubFunc.encrypt(dbpre+"`"+a0100));
				lazyVoMap.put("basepre", dbpre);
				lazyVoMap.put("a0100", a0100);
			}
			else
			{
				if(param.getInfor_type()==2)
					vo.setString("b0110",a0100);
				if(param.getInfor_type()==3)
					vo.setString("e01a1",a0100);
				if(vo.hasAttribute("codeitemdesc_2"))
				{
					vo.setString("codeitemdesc_2", "--");
					lazyVoMap.put("codeitemdesc_2", "--");
				}
				if(vo.hasAttribute("codeitemdesc_1"))
				{
					vo.setString("codeitemdesc_1", "--");
					lazyVoMap.put("codeitemdesc_1", "--");
				}
				if(param.getOperationType()==5)
				{
					if(vo.hasAttribute("parentid_2"))
					{
						String value="";
						vo.setString("parentid_2", value);
						lazyVoMap.put("parentid_2", value);
					}
				}	
                
				lazyVoMap.put("objectid_e", PubFunc.encrypt(a0100));
				
			}
			 ArrayList allTemplateItem = tablebo.getAllTemplateItem();
	            for(int i=0;i<allTemplateItem.size();i++){
	            	TemplateItem item = (TemplateItem) allTemplateItem.get(i);
	            	TemplateSet setBo = item.getCellBo();
	            	if(StringUtils.isNotBlank(setBo.getDefaultValue())&&!setBo.isSubflag()&&setBo.getChgstate()==2){
	            		String id = setBo.getSub_domain_id();
						if(id!=null&&id.trim().length()>0){
							id = "_"+id;
						}else{
							id="";
						}
						String defaultValue= setBo.getDefaultValue();
						if("D".equalsIgnoreCase(setBo.getOld_fieldType())){
							Date date=null;
							if(StringUtils.isNotBlank(defaultValue)){
								if("SYSTIME".equalsIgnoreCase(setBo.getDefaultValue())){
									 date=new Date();
								}else{
									defaultValue=defaultValue.replace("\\", "-").replace("/", "-").replace(".", "-").replace("年", "-").replace("月", "-").replace("日", " ").replace("时", ":").replace("分", ":").replace("秒", "");
									String format="yyyy-MM-dd";
									if(defaultValue!=null&&defaultValue.indexOf(":")>-1){
										format="yyyy-MM-dd HH:mm:ss";
									}
									date=DateUtils.getSqlDate(defaultValue,format);
								}
								vo.setDate(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),date);
								lazyVoMap.put(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(), defaultValue);
							}
	            		}else if("N".equalsIgnoreCase(setBo.getOld_fieldType())){
	            			int ndec=item.getFieldItem().getDecimalwidth();//小数点位数  
	    					String value=PubFunc.DoFormatDecimal(defaultValue,ndec);
	            			vo.setString(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),  value);
	            			lazyVoMap.put(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(), value);
	            		}else{
	            			if(param.getInfor_type()!=1&&param.getOperationType()==5
	            					&&"defMngOrg".equalsIgnoreCase(defaultValue)) {//默认值是管理机构，需要得到权限下的机构，按顺序取最大范围那个
	            				String orgCode = this.userView.getUnitIdByBusi("4");
	            				String orgid = "";
	            				if("UN`".equalsIgnoreCase(orgCode)){//UN`=全部 需要查到最顶层的UN节点
	            					String sql = "select codeitemid from organization where grade=1 and codesetid='UN'";
	            					this.frowset = dao.search(sql);
	            					while(this.frowset.next()) {
	            						String codeitemid = this.frowset.getString("codeitemid");
	            						orgid += "UN"+codeitemid+"`";
	            					}
	            					orgCode = orgid;
	            				}
	            				String[] orgArr = StringUtils.split(orgCode,"`");
	            				String value = "";
	            				for(int j=0;j<orgArr.length;j++) {
	            					String value_ = orgArr[j].substring(2);
	            					if(j==0) {
	            						value = value_;
	            					}
	            					if(value.length()>value_.length()) {
	            						value = value_;
	            					}
	            				}
	            				vo.setString(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),  value);
	            				lazyVoMap.put(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(), value);
	            			}else {
	            				vo.setString(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(),  defaultValue);
	            				lazyVoMap.put(setBo.getField_name().toLowerCase()+id+"_"+setBo.getChgstate(), defaultValue);
	            			}
	            		}
					}
	            }
			lazyVoMap.put("realtask_id_e", "0");
			lazyVoMap.put("ins_id", "0");
			if(param.getOperationType() == 0 || param.getOperationType() == 5)//是调入模板时是选中 lis 20160809
				lazyVoMap.put("submitflag2", "1");
			else
				lazyVoMap.put("submitflag2", "0");
			
			Iterator iterator=sub_map.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String field_name=entry.getKey().toString();
				TemplateSet setbo =(TemplateSet)entry.getValue();
				TSubSetDomain setdomain=new TSubSetDomain(setbo.getXml_param());
				String xml=setdomain.outContentxml();
				vo.setString(field_name.toLowerCase(), xml);
			}
			
			this.frowset=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+name);
			if(this.frowset.next())
				vo.setInt("a0000", this.frowset.getInt(1));
			vo.setString("seqnum", CreateSequence.getUUID());
			vo.setInt("submitflag", 1);
            dao.addValueObject(vo);
          
            this.getFormHM().put("a0100", a0100);
            
            if(param.getInfor_type()==1)
            {
	            this.getFormHM().put("a0100", a0100);
	            this.getFormHM().put("basepre", param.getDest_base());
            }
            else if(param.getInfor_type()==2)
            	this.getFormHM().put("b0110", a0100);
            else if(param.getInfor_type()==2)
            	this.getFormHM().put("e01a1", a0100);
            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
            {
            	 if("1".equals(param.getId_gen_manual())){
                 	
                 }else{
                	 tablebo.filloutSequence(a0100, param.getDest_base(), name,"0");   
                 }
                     	
            }
           	else	
           	{
           	 if("1".equals(param.getId_gen_manual())){
              	
             }else{
            	 tablebo.filloutSequence(a0100, param.getDest_base(), name,"0");         
             }
                  	
           	}
            //if ("card".equals(viewType)){
           
            	String record = JSON.toString(lazyVoMap);
            	this.getFormHM().put("record",SafeCode.encode(record));            	
            //}
            
            /**生成序号*/

	    }
		catch(Exception ex)
		{
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{  
				PubFunc.resolve8060(this.getFrameconn(),name);
				throw GeneralExceptionHandler.Handle(new Exception("请重新操作!"));
			}
			else
				throw GeneralExceptionHandler.Handle(ex);
		}		

	}
}
