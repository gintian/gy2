/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TSubSetDomain;
import com.hjsj.hrms.businessobject.general.template.TemplateSetBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.CreateSequence;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *<p>Title:人员调入模板新增人员</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2008-2-13:上午10:29:12</p> 
 *@author cmq
 *@version 4.0
 */
public class ImpNewObjToTempletTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("setname");
		String codeid=(String)hm.get("codeid");
		cat.debug("table name="+name);
		int idx=name.lastIndexOf("_");
		String tab_id=name.substring(idx+1);		
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
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tab_id),this.userView);
            HashMap sub_map=tablebo.getHisModeSubCell();
			a0100=  idg.getId("rsbd.a0100");
			if(tablebo.getInfor_type()==2||tablebo.getInfor_type()==3)
				a0100="B"+a0100;
			
			if(tablebo.getInfor_type()==1&&(tablebo.getDest_base()==null||tablebo.getDest_base().length()==0))
				throw new GeneralException("人员调入业务模板未定义目标库!");
			
			
			
			if(tablebo.getInfor_type()==1)
			{
				
				ArrayList dbList=DataDictionary.getDbpreList();
				String dbpre=tablebo.getDest_base();
				for(int i=0;i<dbList.size();i++)
				{
					String pre=(String)dbList.get(i);
					if(pre.equalsIgnoreCase(tablebo.getDest_base()))
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
				
				if(codeid!=null&&codeid.length()>0)
				{
					String codeSet=codeid.substring(0,2);
					String value="";
					if(codeid.length()>0)
						value=codeid.substring(2);
					if("UN".equalsIgnoreCase(codeSet)&&value.length()>0&&vo.hasAttribute("b0110_2"))
					{
						vo.setString("b0110_2",value);
					}
					if("UM".equalsIgnoreCase(codeSet))
					{
						if(value.length()>0&&vo.hasAttribute("e0122_2"))
						{
							vo.setString("e0122_2",value);
						}
						if(value.length()>0&&vo.hasAttribute("b0110_2"))
						{
							vo.setString("b0110_2",tablebo.getB0110(value));
						}
						
					}
					
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
				
				
				if(tablebo.getOperationtype()==5)
				{
					if(vo.hasAttribute("parentid_2"))
					{
						if(codeid!=null&&codeid.length()>0)
						{
							String codeSet=codeid.substring(0,2);
							String value="";
							if(codeid.length()>0)
								value=codeid.substring(2);
							if(value.length()>0)
								vo.setString("parentid_2", value);
						}
					}
					 
				}
				
				
				
			}
			
			
			Iterator iterator=sub_map.entrySet().iterator();
			while(iterator.hasNext())
			{
				Entry entry=(Entry)iterator.next();
				String field_name=entry.getKey().toString();
				TemplateSetBo setbo =(TemplateSetBo)entry.getValue();
				TSubSetDomain setdomain=new TSubSetDomain(setbo.getXml_param());
				String xml=setdomain.outContentxml();
				vo.setString(field_name.toLowerCase(), xml);
			}
			
			this.frowset=dao.search("select "+Sql_switcher.isnull("max(a0000)","0")+"+1 from "+name);
			if(this.frowset.next())
				vo.setInt("a0000", this.frowset.getInt(1));
			vo.setString("seqnum", CreateSequence.getUUID());
            dao.addValueObject(vo);
          
            this.getFormHM().put("a0100", a0100);
            
            if(tablebo.getInfor_type()==1)
            {
	            this.getFormHM().put("a0100", a0100);
	            this.getFormHM().put("basepre", tablebo.getDest_base());
            }
            else if(tablebo.getInfor_type()==2)
            	this.getFormHM().put("b0110", a0100);
            else if(tablebo.getInfor_type()==2)
            	this.getFormHM().put("e01a1", a0100);
            if(Sql_switcher.searchDbServer()==Constant.ORACEL)
            {
            	 if("1".equals(tablebo.getId_gen_manual())){
                 	
                 }else{
                	 tablebo.filloutSequence(a0100, tablebo.getDest_base(), name);   
                 }
                     	
            }
           	else	
           	{
           	 if("1".equals(tablebo.getId_gen_manual())){
              	
             }else{
            	 tablebo.filloutSequence(a0100, tablebo.getDest_base(), name);         
             }
                  	
           	}
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




