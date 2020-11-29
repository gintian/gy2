package com.hjsj.hrms.transaction.hire.zp_options.cond;

import com.hjsj.hrms.businessobject.hire.zp_options.ZpCondTemplateXMLBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowZpCondFieldsListTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			ArrayList list = new ArrayList();
			ArrayList selectList = new ArrayList();
			ArrayList nowList = new ArrayList();
			ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.getFrameconn());
			String fieldsId = (String)hm.get("ids");
			String type=(String)this.getFormHM().get("zp_cond_template_type");
			selectList = bo.getAttributeValues(type);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
          if(fieldsId == null || "".equals(fieldsId)){
				
			}else{
			   HashMap map = new HashMap();
			   String[] fieldsIdsArray = fieldsId.split(",");
			   StringBuffer yx = new StringBuffer();
			   StringBuffer wx = new StringBuffer();
			   for(int k=0;k<selectList.size();k++){
				   LazyDynaBean ldb=(LazyDynaBean)selectList.get(k);
				   if(fieldsId.indexOf((String)ldb.get("name")) != -1){
					   yx.append(',');
					   yx.append((String)ldb.get("name"));
					   /*为了调序，本调序不改变数据库中的显示顺序*/
					   map.put(((String)ldb.get("name")).toLowerCase(),ldb);
					   //list.add(ldb);
				   }
			   }
			   for(int n=0;n<fieldsIdsArray.length;n++){
				   if(yx.toString().indexOf(fieldsIdsArray[n]) == -1){
					   wx.append(",");
					   wx.append(fieldsIdsArray[n]);
				   }
				   if(map.get(fieldsIdsArray[n].toLowerCase()) != null)
				       list.add(map.get(fieldsIdsArray[n].toLowerCase()));
			   }
			   if(yx != null && yx.toString().trim().length()>0){
			       for(int i=0;i<list.size();i++){
			    	   DynaBean bean = new LazyDynaBean();
			    	   LazyDynaBean yx_bean = (LazyDynaBean)list.get(i);
			    		      String id=(String)yx_bean.get("name");
			    		      if("createtime".equalsIgnoreCase(id))
			    		      {
			    		    	  bean.set("itemdesc","简历入库时间");
			    		    	  bean.set("itemtype","D");
			    		    	  bean.set("itemlength","10");
			    		    	  bean.set("codesetid","0");
			    		    	  bean.set("decimalwidth","0");
			    		    	  bean.set("itemid","createtime");
			    		    	  String e_value = (String)yx_bean.get("e_value");
							      String s_value = (String)yx_bean.get("s_value");
							      bean.set("s_value",s_value);
							      bean.set("e_value",e_value);
							      bean.set("flag",yx_bean.get("flag"));
							      nowList.add(bean);
							      continue;
			    		      }
					          String codesetid = "";
					          String itemtype="";
						      String e_value = (String)yx_bean.get("e_value");
						      String s_value = (String)yx_bean.get("s_value");
						      this.frowset = dao.search("select itemdesc,itemid,itemtype,codesetid,itemlength,decimalwidth from fielditem where itemid ='"+id+"'");
						      while(this.frowset.next()){
						    	      bean.set("itemdesc",this.frowset.getString("itemdesc"));
						    	      bean.set("itemtype",this.frowset.getString("itemtype"));
						    	      bean.set("itemid",this.frowset.getString("itemid"));
						    	      bean.set("codesetid",this.frowset.getString("codesetid"));
						    	      bean.set("itemlength",this.frowset.getString("itemlength"));
						    	      bean.set("decimalwidth",this.frowset.getString("decimalwidth"));
						    	      codesetid=this.frowset.getString("codesetid");
						    	      itemtype = this.frowset.getString("itemtype");
						      }
						      if("A".equals(itemtype)&&!"0".equals(codesetid)){
						    	  if(e_value !=null && !"".equals(e_value)){
						    		  
								    	  bean.set("view_e_value",AdminCode.getCodeName(codesetid,e_value));
								     
								      bean.set("e_value",e_value);
						    	  }
						    	  
							      bean.set("view_s_value",AdminCode.getCodeName(codesetid,s_value));
							   
							      bean.set("s_value",s_value);
						      }else{
						    	  bean.set("s_value",s_value);
						    	  bean.set("e_value",e_value);
						      }
						      bean.set("flag",yx_bean.get("flag"));
			    		   nowList.add(bean);
			    	   //}
			    	   
			    	   
			       }
			       
			   }
			   if(wx != null && wx.toString().trim().length()>0 ){
			         String[] wx_Arr = wx.toString().substring(1).split(",");
			         for(int j=0;j<wx_Arr.length;j++){
			        	 DynaBean bean = new LazyDynaBean();
			        	 if("createtime".equalsIgnoreCase(wx_Arr[j]))
			        	 {
			        		 bean.set("itemtype","D");
			        		 bean.set("codesetid","0");
			        		 bean.set("itemdesc","简历入库时间");
			        		 bean.set("itemlength","10");
			        		 bean.set("decimalwidth","0");
			        		 bean.set("itemid","createtime");
			        		 bean.set("s_value","");
			        		 bean.set("e_value","");
			        		 bean.set("flag","true");
			        		 nowList.add(bean);
			        		 continue;
			        	 }
			        	 this.frowset=dao.search("select itemtype,codesetid ,itemdesc,itemlength,decimalwidth from fielditem where itemid ='"+wx_Arr[j]+"'");
				            while(this.frowset.next()){
					              bean.set("itemtype",this.frowset.getString("itemtype"));
					              bean.set("codesetid",this.frowset.getString("codesetid"));
					              bean.set("itemdesc",this.frowset.getString("itemdesc"));
					              bean.set("itemlength",this.frowset.getString("itemlength"));
					              bean.set("decimalwidth",this.frowset.getString("decimalwidth"));
					             // codesetid=this.frowset.getString("codesetid");
					    	     // itemtype = this.frowset.getString("itemtype");
				            }
				           
				            	
				            bean.set("s_value","");
						    bean.set("e_value","");
				            
				            bean.set("itemid",wx_Arr[j]);
					        bean.set("flag","true");
			        	    nowList.add(bean);
			         }
			   }
			}
			this.getFormHM().put("fieldSetList",nowList);
			this.getFormHM().put("zp_cond_template_type",type);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
}
