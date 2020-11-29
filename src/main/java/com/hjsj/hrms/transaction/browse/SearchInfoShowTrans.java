/*
 * Created on 2005-6-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.browse;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchInfoShowTrans extends IBusiness {
	public void execute() throws GeneralException {
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			
			String flag=(String)hm.get("flag");    	
			String userpriv="";
			String type="1";//=1是我的信息.=2是员工信息
			if("infoself".equalsIgnoreCase(flag)) {
				userpriv="selfinfo";
				type="1";
			} else {
				type="2";
			}
			
			String isUserEmploy = (String)hm.get("isUserEmploy");
			
			if(StringUtils.isEmpty(isUserEmploy)){
				if("1".equals(type))
					isUserEmploy = "1"; 
				else
					isUserEmploy = "0"; 
				
			}
			
			this.getFormHM().put("isUserEmploy", isUserEmploy); 
			this.getFormHM().put("userpriv", userpriv);
			String userbase = "";
			String A0100 = "";
			if(!("infoself".equalsIgnoreCase(flag) && userView.getStatus()!=4))
			{
				userbase=(String)this.getFormHM().get("userbase"); //人员库
				String tablename=userbase + "A01";                        //表的名称
				A0100=(String)this.getFormHM().get("a0100");       //获得人员ID
				String returnvalue = (String)hm.get("returnvalue");
				if("train_post".equals(returnvalue) || "train_no_post".equals(returnvalue)){
					A0100 = PubFunc.decrypt(A0100);				
				}
				
				//v7版本  机构图进来时带前缀“RY”
				if(hm.containsKey("personid")){
					A0100 = hm.get("personid").toString();
					A0100 = SafeCode.decode(A0100);
					hm.remove("personid");
					userbase = PubFunc.convert64BaseToString(hm.get("dbname").toString());
				}
				
				if(A0100!=null&&A0100.trim().length()>0&& "~".equalsIgnoreCase(A0100.substring(0,1))) //dengcan 2012-2-10 如果是通过转码传过来的需解码
				{ 
					String _temp=A0100.substring(1); 
					A0100=PubFunc.convert64BaseToString(SafeCode.decode(_temp));
				}
				
				if("A0100".equals(A0100))
					A0100=userView.getUserId();
				else{
					if(!"infoself".equalsIgnoreCase(flag)){
						if(!"relation".equalsIgnoreCase(returnvalue)/*&&!"100000".equals(returnvalue)*/){// 100000是机构图和汇报关系使用的，需要检查
							CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
							userbase=checkPrivSafeBo.checkDb(userbase);
							A0100=checkPrivSafeBo.checkA0100("", userbase, A0100, "");
						}
					}
				}
				if(A0100.trim().length() > 8)//bug 49497 通用统计 A0100 未正常解密后，长度超过8位    解密处理    wangb 20190624
					A0100 = PubFunc.decrypt(A0100);
				searchMessage(userbase,A0100,flag,type);
				this.isAble(userbase, A0100);			
			}		
			else
			{
				if(this.userView.getA0100()!=null&&this.userView.getA0100().length()>0)
				{
					userbase=this.userView.getDbname(); //人员库
					String tablename=userbase + "A01";                        //表的名称
					A0100=userView.getA0100();
					searchMessage(userbase,A0100,flag,type);
				}else
					throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("employ.no.use.model"))) ;
			}
			InfoUtils InfoUtils=new InfoUtils();
			String a01desc=InfoUtils.getFieldSetCustomdesc(this.getFrameconn(), "A01");
			this.getFormHM().put("emp_cardId",searchCard("1"));
			this.getFormHM().put("pos_cardId",searchCard("3"));		
			this.getFormHM().put("a01desc", a01desc);
			
			if("1".equalsIgnoreCase(type)) {
				FieldSet a01 = DataDictionary.getFieldSetVo("A01");
				String multimedia_file_flag = a01.getMultimedia_file_flag();
				this.getFormHM().put("multimedia_file_flag", multimedia_file_flag);
			}
			
			if(StringUtils.isNotEmpty(userbase) && StringUtils.isNotEmpty(A0100)) {
				StringBuffer sql = new StringBuffer();
				sql.append("select fileid from ");
				sql.append(userbase);
				sql.append("a00 where a0100=?");
				sql.append(" and flag='P'");
				ArrayList<String> paramList = new ArrayList<String>();
				paramList.add(A0100);
				ContentDAO dao = new ContentDAO(this.frameconn);
				try {
					this.frowset = dao.search(sql.toString(), paramList);
					if (this.frowset.next()) {
						String fileid = this.frowset.getString("fileid");
						this.getFormHM().put("photoId", fileid);
					} else {
						this.getFormHM().put("photoId", "");
					}
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
				}
			}

  }
	private void searchMessage(String userbase,String A0100,String flag,String type)throws GeneralException
	{
		List rs=null;
		List infoFieldList=null;
		List infoSetList=null;
		String tablename=userbase + "A01";  
		String personsort=(String)this.getFormHM().get("personsort");
		String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
		String fenlei_priv=(String)this.getFormHM().get("fenlei_priv");
		InfoUtils infoUtils=new InfoUtils();
		ContentDAO dao=new ContentDAO(this.getFrameconn());			
		String sub_type=infoUtils.getOneselfFenleiType(userbase, A0100, fenlei_priv, dao);//人员分类
		if("infoself".equalsIgnoreCase(flag)) {
		    /*
             * 按分类授权获取到的子集/指标没有区分是不是员工角色特征下的，因此分类授权只能在业务上实现；
             * 自助服务员工信息要使用分类授权的话，分类授权只能在员工角色特征下的角色授权，
             * 其它地方的不能进行子集/指标的分类授权否则会显示全部的分类授权的子集/指标
             */
		    if(sub_type!=null&&sub_type.length()>0) {
                infoFieldList=infoUtils.getSubPrivFieldList(this.userView,"A01",sub_type, 1);
                //得到分类授权子集
                infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET, 1);
                //如果分类中得不到指标则用默认权限的
                if(infoFieldList==null||infoFieldList.size()<=0)
                    //获得当前子集的所有属性
                    infoFieldList = userView.getPrivFieldList("A01", 0);
                //获得所有权限的子集
                if(infoSetList==null||infoSetList.size()<=0)
                    infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
                    
            } else {
                // 获得当前子集的所有属性
                infoFieldList = userView.getPrivFieldList("A01", 0);
                // 获得所有权限的子集
                infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
            }
		    
		} else {	
	    	if(sub_type!=null&&sub_type.length()>0)
			{
				infoFieldList=infoUtils.getSubPrivFieldList(this.userView,"A01",sub_type);
				//得到分类授权子集
				infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET);
				if(infoFieldList==null||infoFieldList.size()<=0)//如果分类中得不到指标则用默认权限的
					infoFieldList=userView.getPrivFieldList("A01");   //获得当前子集的所有属性
				if(infoSetList==null||infoSetList.size()<=0)
					infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
					
			}else
			{
				infoFieldList=userView.getPrivFieldList("A01");      //获得当前子集的所有属性
			  infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
			}
		  
	    }
		
		List infoFieldViewList=new ArrayList();               //保存处理后的属性
		String filename="";
		String state="";
		try
		{
			boolean isExistData=false;
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from ");
			strsql.append(tablename);
			strsql.append(" where A0100='");
			strsql.append(A0100);
			strsql.append("'");
			rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn(),true);
			isExistData=!rs.isEmpty();
			/**有可能会出错*/
            if(!isExistData)
            	return;
			LazyDynaBean rec=(LazyDynaBean)rs.get(0);
			state=(String)rec.get("state");
			
			// WJH　２０１３－４－２５　 2013-4-26, 机构组织维护中浏览也增加处理 personsort=null
			// if("all".equalsIgnoreCase(personsort) &&  personsortfield!=null && !"infoself".equalsIgnoreCase(flag))
			if( (personsort==null || "all".equalsIgnoreCase(personsort)) && personsortfield!=null && !"infoself".equalsIgnoreCase(flag) )
			     personsort=rec.get(personsortfield.toLowerCase())!=null?rec.get(personsortfield.toLowerCase()).toString():null;
			//--------------
			     
			 if(!"infoself".equalsIgnoreCase(flag))
			 {
				 
				  infoSetList=new SortFilter().getSortPersonFilterSet(infoSetList,personsort,this.getFrameconn());
				  infoFieldList=new SortFilter().getSortPersonFilterField(infoFieldList,personsort,this.getFrameconn());
				  String nbase = getNbase(userbase);
				  infoSetList=new SortFilter().getPersonDBFilterSet(infoSetList, nbase,this.getFrameconn());
				  infoFieldList=new SortFilter().getPersonDBFilterField(infoFieldList, nbase,this.getFrameconn());
				 
			 }
			 
	        String HIDESET=SystemConfig.getPropertyValue("HIDESET_INFOM");          
            if (HIDESET==null) HIDESET="";
            if ((!"".equals(HIDESET)) &&(!"infoself".equalsIgnoreCase(flag))){
                HIDESET =(","+HIDESET+",").toUpperCase();
                ArrayList list = new ArrayList();
                for(int i = 0; i < infoSetList.size(); i++)
                {
                    FieldSet fieldset = ((FieldSet)infoSetList.get(i)).cloneItem();
                    if (HIDESET.indexOf(fieldset.getFieldsetid().toUpperCase())<0){     
                        list.add(fieldset) ;  
                    }
                
                }
                infoSetList =list;
                if(!"infoself".equalsIgnoreCase(flag))
                {                  
                 
                     infoFieldList=new SortFilter().getSortPersonFilterField(infoFieldList,personsort,this.getFrameconn());
                     String nbase = getNbase(userbase);              
                     infoFieldList=new SortFilter().getPersonDBFilterField(infoFieldList, nbase,this.getFrameconn());
                    
                } 
            }
			 
			
			 if(!infoFieldList.isEmpty())
		    {
				GzDataMaintBo gzDataMaintBo=new GzDataMaintBo(this.getFrameconn());
				for(int i=0;i<infoFieldList.size();i++)                //字段的集合
				{
				    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);	
				    if("b0110".equalsIgnoreCase(fieldItem.getItemid()))
				    {
				    	String UNIT_LEN=gzDataMaintBo.getValues("UNIT_LEN");
				    	if(UNIT_LEN!=null&& "0".equals(UNIT_LEN))
				    		fieldItem.setVisible(false);
				    	else
				    		fieldItem.setVisible(true);
				    }else if("e01a1".equalsIgnoreCase(fieldItem.getItemid()))
				    {
				    	String POS_LEN_str =gzDataMaintBo.getValues("POS_LEN");
				    	if(POS_LEN_str!=null&& "0".equals(POS_LEN_str))
				    		fieldItem.setVisible(false);
				    	else
				    		fieldItem.setVisible(true);
				    }
					if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
					{
						FieldItemView fieldItemView=new FieldItemView();
						fieldItemView.setVisible(fieldItem.isVisible());
						fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
						fieldItemView.setCodesetid(fieldItem.getCodesetid());
						fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());							
						fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView.setPriv_status(fieldItem.getPriv_status());
				        //在struts用来表示换行的变量
					    fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
						if(isExistData)
						{
							if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
							{
								if(!"0".equals(fieldItem.getCodesetid()))
								{
									String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
									if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
									{
										//tianye update start
										//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
										String name = "";
										if(!"e0122".equalsIgnoreCase(fieldItem.getItemid())){
											CodeItem codeItem = InfoUtils.getUMOrUN(fieldItem.getCodesetid(),codevalue);
											name = (codeItem!=null ? codeItem.getCodename(): "");
										}else{
											name = AdminCode.getCodeName(fieldItem.getCodesetid(), codevalue);
										}
										fieldItemView.setFieldvalue(name);
										//end
									}
										else
								       fieldItemView.setFieldvalue("");
									fieldItemView.setViewvalue(codevalue);										
								}
								else
								{
									String fieldvalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString().replaceAll("\n","<br>"):"";
									fieldvalue=fieldvalue.replaceAll("<br>","");										
									fieldItemView.setFieldvalue(fieldvalue);

								}
							}else if("D".equals(fieldItem.getItemtype()))        //日期型有待格式化处理
							{
                                int itemlen =  fieldItem.getItemlength();
                                String value =rec.get(fieldItem.getItemid()).toString();
                                if ((value !=null) && (value.length()>=itemlen)){
                                    fieldItemView.setFieldvalue(
                                            new FormatValue().format(fieldItem,
                                         value));
                                }
                                else {                                      
                                    fieldItemView.setFieldvalue("");  
                                }
						/*			if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==10)
									{
										fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
									}else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
									{
										fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
									}
									else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
									{
										fieldItemView.setFieldvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
									}
									else
		                            {
		                            	fieldItemView.setFieldvalue("");
		                            }*/
							}
							else                                              //数值类型的有待格式化处理
							{
                             	fieldItemView.setFieldvalue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()).toString(),fieldItem.getDecimalwidth()));
							}
						}		
						fieldItemView.setRowindex(String.valueOf(i));
						infoFieldViewList.add(fieldItemView);
					}
				}
			}
	
		}
		catch(Exception e)
		{
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}finally{
		   this.getFormHM().put("a0100",A0100);
		   this.getFormHM().put("userbase",userbase);		   
	       this.getFormHM().put("infofieldlist",infoFieldViewList);          //压回页面
	       this.getFormHM().put("infosetlist",infoSetList);
	       this.getFormHM().put("photoname",filename);		
	       this.getFormHM().put("e01a1",this.userView.getUserPosId());
	       this.getFormHM().put("type", type);
	       this.getFormHM().put("state", state);
       }	
	    String infosort=(String)this.getFormHM().get("infosort");
		if(infosort!=null&& "1".equals(infosort))
		{
			infoSort(infoFieldViewList,infoSetList);
		}else
		{
			this.getFormHM().put("mainsort", "");
			this.getFormHM().put("infosort", "");
		}
	}
	 /**
     * 根据信息群类别，查询定义的登记表格号
     * @param infortype =1人员 =2单位 3=职位 
     * @return
     */
    private String searchCard(String infortype)
    {
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		 String cardid="-1";
		 try
		 {
			 if("1".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"emp");
			 }
			 if("2".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"org");
			 }
			 if("3".equalsIgnoreCase(infortype))
			 {
				 cardid=sysbo.getValue(Sys_Oth_Parameter.BOROWSE_CARD,"pos");
			 }
			 if(cardid==null|| "".equalsIgnoreCase(cardid)|| "#".equalsIgnoreCase(cardid))
				 cardid="-1";
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
		 }
		 return cardid;
    }
    private void infoSort(List infoFieldViewList,List infoSetList)
    {
    	SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());		
		ArrayList subsort_list=infoxml.getView_tag("A01");//主集分类		
		ArrayList set_list=infoxml.getView_tag("SET_A");
		if(set_list==null||set_list.size()<=0)
		{
			this.getFormHM().put("infosort", "");
		}
		if(subsort_list==null||subsort_list.size()<=0)
		{
			this.getFormHM().put("mainsort", "");
		}else
		{
			this.getFormHM().put("mainsort", "1");
		}
		/**********主集************/
        /*if(subsort_list!=null&&subsort_list.size()>0)
        	subsort_list.add("未分类指标"); */
        List infolist=null;
        HashMap hm=new HashMap();
        if(subsort_list!=null&&subsort_list.size()>0)
        {
        	String sortName="";
        	for(int i=0;i<subsort_list.size();i++)
        	{
        		infolist=new ArrayList();
        		sortName=subsort_list.get(i).toString();
        		if(sortName!=null&& "未分类指标".equals(sortName))
            	{
            		StringBuffer infoFielditem=new StringBuffer();
            		String  iSortName="";
            		for(int n=0;n<subsort_list.size();n++)
            		{
            			    iSortName=subsort_list.get(n)!=null?subsort_list.get(n).toString():"";
            				infoFielditem.append(infoxml.getView_value("A01", iSortName)+",");;
            		}        		
            		infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem.toString(),false);
            		
            	}else if(i==subsort_list.size()-1)
            	{
            		String infoFielditem=infoxml.getView_value("A01", sortName);
            		infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem,true);
            		StringBuffer infoFielditems=new StringBuffer();
            		String  iSortName="";
            		infoFielditems.append(infoFielditem+",");
            		for(int n=0;n<subsort_list.size();n++)
            		{
            			    iSortName=subsort_list.get(n)!=null?subsort_list.get(n).toString():"";
            				infoFielditems.append(infoxml.getView_value("A01", iSortName)+",");;
            		}        		
            		List no_infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditems.toString(),false);
            		for(int s=0;s<no_infolist.size();s++)
            		{
            			infolist.add(no_infolist.get(s));
            		}
            		infolist=reOrderinfoList(infoFieldViewList,infolist);
            	}
        		else
            	{
            		
            		String infoFielditem=infoxml.getView_value("A01", sortName);
            		infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem,true);
            	}
        		hm.put(sortName, infolist);
        	}
        	
        }
        /**********子集*********/
		this.getFormHM().put("subsort_list", subsort_list);
		this.getFormHM().put("infoMap", hm);
		
    }
    private void isAble(String userbase,String a0100) {
    	String sql = "select 1 from t_hr_mydata_chg where nbase='"+userbase+"' and a0100='"+a0100+"' and sp_flag='02'";
    	ContentDAO dao  = new ContentDAO(this.frameconn);
    	try {
			this.frowset = dao.search(sql);
			if (this.frowset != null &&this.frowset .next()) {
				this.formHM.put("isAble", "0");
			} else {
				this.formHM.put("isAble", "1");
			}
			MyselfDataApprove self = new MyselfDataApprove(this.getFrameconn(), this.userView, userbase,
					a0100);
			if(self.queryMainPeal("A01", a0100, "02")) {
				this.formHM.put("isMainPeal", "1");
			}  else {
				this.formHM.put("isMainPeal", "0");
			}			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private String getNbase(String userbase){
    	String nbase=userbase;
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	try {
			this.frowset = dao.search("select pre from dbname where Upper(pre)='"+userbase.toUpperCase()+"'");
			if(this.frowset.next()){
				nbase=this.frowset.getString("pre");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return nbase;
    }
    private List reOrderinfoList(List infoFieldViewList,List infolist)
    {
    	List infoFieldList=new ArrayList();   
    	if(infolist==null||infolist.size()<=0)
			return infoFieldList;		
    	for(int i=0;i<infoFieldViewList.size();i++)
		{
			FieldItemView fieldItemView=(FieldItemView)infoFieldViewList.get(i);
			for(int r=0;r<infolist.size();r++)
			{
				FieldItemView fieldItem=(FieldItemView)infolist.get(r);
				if(fieldItem.getItemid().equals(fieldItemView.getItemid()))
				{
					infoFieldList.add(fieldItemView.clone());
				}
			}
		}
    	return infoFieldList;
    }
}
