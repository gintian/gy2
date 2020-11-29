package com.hjsj.hrms.transaction.hire.innerEmployNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.hire.InnerHireBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class InitAttachTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String clientName=SystemConfig.getPropertyValue("clientName");//得到一个专有的客户名称
            String email="";
            RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL");
            String itemid=vo.getString("str_value");
            RecordVo vo2 = ConstantParamter.getConstantVo("ZP_DBNAME");
            String dbname = vo2.getString("str_value");
            InnerHireBo bo=new InnerHireBo(this.getFrameconn());
            
            if(clientName!=null&& "hkyh".equalsIgnoreCase(clientName)){//专门为汉口银行做专版
                /**得到系统的唯一性指标**/
                Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
                String onlyFlag = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
                itemid = onlyFlag;
                if(onlyFlag==null||onlyFlag.trim().length()<=0){
                    throw GeneralExceptionHandler.Handle(new Exception("请配置系统的唯一性指标!"));
                }
               
                String onlyName = bo.getEmailAddress(onlyFlag,this.getUserView());//这里得到的是人员的唯一性标识
                
                if(onlyName==null||onlyName.length()==0)
                    throw GeneralExceptionHandler.Handle(new Exception("您没有设置您的唯一性标识指标，不能申请岗位!"));
                
                if(bo.getSameEmailCount(onlyFlag,onlyName,this.userView)>1)
                    throw GeneralExceptionHandler.Handle(new Exception("您的唯一性指标与库中其他人的唯一性指标相同，不能申请岗位,请修改!"));
                email = onlyName;
                
            }else{    
                
                
                if ("#".equals(itemid))
                    throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标!"));
                email = bo.getEmailAddress(itemid, this.getUserView());
                if (email == null || email.length() == 0)
                    throw GeneralExceptionHandler.Handle(new Exception("您没有设置电子邮件地址!"));
    
                if (bo.getSameEmailCount(itemid, email, this.userView) > 1)
                    throw GeneralExceptionHandler.Handle(new Exception("您的电子邮件与库中其他人的邮件地址相同,请修改!"));
            }
            String ya0100 = this.userView.getA0100();
            if (ya0100 != null && ya0100.trim().length() > 0) {
                String a0100 = bo.getZpkA0100(email, itemid);
                ArrayList attachList = new ArrayList();
                ParameterXMLBo bo2 = new ParameterXMLBo(this.getFrameconn(), "1");
                HashMap map = bo2.getAttributeValues();
                EmployNetPortalBo employNetPortalBo = new EmployNetPortalBo(this.getFrameconn());
                ContentDAO dao = new ContentDAO(this.getFrameconn());
                String alertMessage = "";
                String flag = "1";
                if (a0100 != null && a0100.trim().length() > 0) {
                    attachList = employNetPortalBo.getUploadFileList(a0100, dbname);
                } else {
                    ArrayList zpFieldlist = employNetPortalBo.getZpFieldList();
                    /**内部招聘按社会招聘处理，检验必填项是否全部填写，未填写，不能够应聘职位*/
                    zpFieldlist = employNetPortalBo.getSetByWorkExprience("02");

                    //{A19=[A1905, A1910, A1915, A1920, A1925], A01=[A0101, A0177, A0111, A0107, C0101, A0121, A0141, A0124, A0127, C0104, C0102, C01SS, A0171, A0114, E0104, C0106]}
                    HashMap fieldMap = (HashMap) zpFieldlist.get(1);
                    //{a19={a1925=1#0, a1905=1#1, a1920=1#0, a1910=1#1, a1915=1#0}, a01={a0141=1#0, a0101=1#0, a0177=1#0, a0124=1#0, c0102=1#1, c0106=1#1, c0104=1#1, c0101=1#0, a0127=1#0, a0111=1#0, a0171=1#0, c01ss=1#1, a0107=1#0, a0114=1#0, e0104=1#1, a0121=1#0}}
                    HashMap fieldSetMap = (HashMap) zpFieldlist.get(2);
                    //{a0141=1#0, a0101=1#0, a0177=1#0, a0124=1#0, c0102=1#1, c0106=1#1, c0104=1#1, c0101=1#0, a0127=1#0, a0111=1#0, a0171=1#0, c01ss=1#1, a0107=1#0, a0114=1#0, e0104=1#1, a0121=1#0}

                    ArrayList list = (ArrayList) zpFieldlist.get(0);
                    Set set = fieldMap.keySet();
                    StringBuffer noDataSet = new StringBuffer("");
                    /**所有子集全部检查是否有必填项未填*/
                    //		    		for(int i=0;i<list.size();i++)
                    //		    		{
                    //		     			LazyDynaBean bean = (LazyDynaBean)list.get(i);
                    //		    			String key=(String)bean.get("fieldSetId");
                    //		    			HashMap fieldExtendMap=(HashMap)fieldSetMap.get(key.toLowerCase());
                    //		    	    	ArrayList fieldList=(ArrayList)fieldMap.get(key.toLowerCase());
                    //		        		if(fieldList==null)
                    //		           			fieldList=(ArrayList)fieldMap.get(key.toUpperCase());
                    //		    	    	StringBuffer whl=new StringBuffer("");
                    //		     	    	StringBuffer noDataField = new StringBuffer("");
                    //		     	    	int checkFlag=0;
                    //			         	for(Iterator t=fieldList.iterator();t.hasNext();)
                    //		    	    	{
                    //			        		String itemid=(String)t.next();
                    //			        		String temp=(String)fieldExtendMap.get(itemid.toLowerCase());
                    //			        		if(temp==null)
                    //			        			temp=(String)fieldExtendMap.get(itemid.toUpperCase());
                    //			        		String[] temps=temp.split("#");
                    //			         		if(temps[1].equals("1"))
                    //			        		{
                    //			        			if(checkFlag!=1&&checkFlag!=2)
                    //			    	    		{
                    //			    	    			checkFlag=1;
                    //			    	    			this.frowset=dao.search("select a0100 from "+this.userView.getDbname()+key+" where a0100='"+this.userView.getA0100()+"'");
                    //			    	    			if(this.frowset.next())
                    //			    	     			{
                    //			    	    				checkFlag=2;
                    //			     	    			}
                    //			        			}
                    //			         			if(checkFlag==1)
                    //			        			{
                    //			         				FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
                    //			        				if(item!=null)
                    //			        					noDataField.append(item.getItemdesc()+",");
                    //			        				else
                    //			        					noDataField.append(itemid.toUpperCase()+",");
                    //			         			}
                    //			        			else
                    //			         			{
                    //			        			   this.frowset=dao.search("select a0100 from "+this.userView.getDbname()+key +" where a0100='"+this.userView.getA0100()+"' and ("+itemid+" is null or "+Sql_switcher.length(itemid)+"=0)");
                    //			        			   if(this.frowset.next())
                    //			        			   {
                    //			    	     			   FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
                    //				        				if(item!=null)
                    //				        					noDataField.append(item.getItemdesc()+",");
                    //				        				else
                    //				         					noDataField.append(itemid.toUpperCase()+",");
                    //			    	    		   }
                    //			        			}
                    //			        		}
                    //			        	}
                    //			        	if(noDataField.toString().length()>0)
                    //		    	    	{
                    //			        		FieldSet fieldSet = DataDictionary.getFieldSetVo(key.toLowerCase());
                    //    			    		if(fieldSet!=null)
                    //    			        		noDataSet.append(fieldSet.getCustomdesc());
                    //    			    		else
                    //    			     			noDataSet.append(key.toUpperCase());
                    //    			    		noDataField.setLength(noDataField.length()-1);
                    //    		  	    		noDataSet.append("("+noDataField.toString()+")\r\n");
                    //			        	}
                    //		    		}
                    String isUpPhoto = "0"; //是否必须上传照片
                    if (map.get("photo") != null && ((String) map.get("photo")).length() > 0)
                        isUpPhoto = (String) map.get("photo");
                    
                    if (noDataSet.length() > 0) {
                        noDataSet.append("以上子集中，括号内的指标为必填项！");
                    }
                    
                    if ("1".equals(isUpPhoto)) {
                        String sql = "select a0100 from " + this.userView.getDbname() + "a00" 
                                   + " where a0100='" + this.userView.getA0100() + "' and flag='P'";
                        this.frowset = dao.search(sql);
                        if (!this.frowset.next()) {
                            if (noDataSet.length() > 0) {
                                noDataSet.append("\r\n个人照片必须上传！");
                            } else {
                                noDataSet.append("个人照片必须上传！");
                            }
                        }
                    }
                    
                    if (noDataSet.toString().length() > 0) {
                        flag = "7";
                        alertMessage = noDataSet.append("\r\n请先维护以上信息！").toString();
                    } else {
                        boolean aflag = true;
                        //bo.getMustItemInfo(this.getUserView().getA0100(),this.getUserView().getDbname());
                        a0100 = bo.copyInfoToZpInner(this.getUserView().getA0100(), this.getUserView().getDbname(), dbname,
                                email, itemid, aflag);

                    }
                }
                this.getFormHM().put("flag", flag); //1:可以上传附件，7要先维护子集，
                this.getFormHM().put("alertMessage", SafeCode.encode(alertMessage));
                this.getFormHM().put("attachList", attachList);
                this.getFormHM().put("isSelfUser", "1");
                this.getFormHM().put("zpkA0100", PubFunc.encrypt(a0100));
                this.getFormHM().put("dbname", PubFunc.encrypt(dbname));
            } else {
                this.getFormHM().put("isSelfUser", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

    }

}
