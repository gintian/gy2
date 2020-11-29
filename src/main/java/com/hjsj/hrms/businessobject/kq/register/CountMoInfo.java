package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 对月汇总进行考勤计算
 * <p>Title:CountMoInfo.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Feb 28, 2007 6:45:25 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class CountMoInfo {

	private UserView userView=null;
	private Connection conn=null;
	
	private String code = "";
	private String kind = "";
	
	public CountMoInfo(){}
	public CountMoInfo(UserView userView,Connection conn)
	{
		this.userView=userView;
		this.conn=conn;
	}
	public void countKQInfo(String kq_duration)throws GeneralException
	{
		    CountInfo countInfo=new CountInfo(this.userView,this.conn);
		    ArrayList kq_target_list=countInfo.getTargetList();
		    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    	
	    	ArrayList columnlist= new ArrayList();
	    	for(int i=0;i<fielditemlist.size();i++)
	    	{
	   	       FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	   	      /* if("N".equals(fielditem.getItemtype()))
	   	       {   		   
	   		      columnlist.add(fielditem);
	   		   }*/	
	   	    if(!("i9999").equalsIgnoreCase(fielditem.getItemid()))
	   	       {
	   	    	 columnlist.add(fielditem);
	   	       }
	   	    }
		    HashMap hash=new HashMap();
	        //		判断是否已经报批	
	 	    for(int r=0;r<this.userView.getPrivDbList().size();r++)
	 	    {
	 	        String userbase=this.userView.getPrivDbList().get(r).toString();
	 	    
	 	        String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,userbase);	
	 	        if(code != null && !"".equals(code)){
    	 	        String kindField = "";
                    if ("1".equals(kind)) {
                        kindField = "e0122";
                    } else if ("0".equals(kind)) {
                        kindField = "e01a1";
                    } else if ("-1".equals(kind)) {
                        kindField = "a0100";
                    } else {
                        kindField = "b0110";
                    }
                    if (whereIN.contains("WHERE") || whereIN.contains("where")) {
                        whereIN = whereIN + " and " + kindField + " like '" + code + "%'";
                    } else {
                        whereIN = whereIN + " where " + kindField + " like '" + code + "%'";
                    }
	 	        }
		        
			   /***一个考勤期间的开始****/		    	
	                //公休日
		   	    if(!userView.isSuper_admin())
				{   
		   	    	String whereB0110=RegisterInitInfoData.selcet_OrgId(userbase,"b0110",whereIN);
					 ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.conn,whereB0110,"b0110");
					 for(int t=0;t<orgidb0110List.size();t++)
					 {
						 String b0110_one=orgidb0110List.get(t).toString();			
						 String nbase=RegisterInitInfoData.getOneB0110Dase(hash,this.userView,userbase,b0110_one,this.conn);
						 /********按照该单位的人员库的操作*********/
						 if(nbase!=null&&nbase.length()>0)
						 {
							 oneCountKQ(kq_duration,whereIN,userbase,b0110_one,"2",kq_target_list,columnlist);	 
						 }											
					 }
				}else
				{  
					ArrayList b0100list=RegisterInitInfoData.getAllBaseOrgid(userbase,"b0110",whereIN,this.conn);
					 for(int n=0;n<b0100list.size();n++)
					 {
						 String b0110_one=b0100list.get(n).toString();
						 String nbase=RegisterInitInfoData.getOneB0110Dase(hash,this.userView,userbase,b0110_one,this.conn);
						 /********按照该单位的人员库的操作*********/
						 if(nbase!=null&&nbase.length()>0)
						 {
							oneCountKQ(kq_duration,whereIN,userbase,b0110_one,"2",kq_target_list,columnlist);	
						 }
						 /***************/
						 				  
				   }
			   } 	  	   
		   	}
	}
	/**
	 * 
	 * @param datelist  时间
	 * @param whereIN  操作人员权限
	 * @param e0122  部门
	 * @param kq_target_list  考勤项目指标里面是个HashMap,HashMap存了两个属性 fielditemid=指标项;c_expr=公式;
	 * @param filedlist  指标项集
	 * @throws GeneralException
	 */
	    public void oneCountKQ(String kq_duration, String whereIN,String userbase,String code,String kind,ArrayList kq_target_list,ArrayList filedlist)throws GeneralException
	    {
	    	
	    	    CountInfo countInfo=new CountInfo(this.userView,this.conn);
	        	ContentDAO dao = new ContentDAO(this.conn);
	        	String curKqItemName = "";
	        	try
	            {
	        	    for(int i=0;i<kq_target_list.size();i++)
	                {
	                	
	                	HashMap onemap=(HashMap)kq_target_list.get(i);
	                	curKqItemName = onemap.get("item_name").toString();
	                	String fielditemid=onemap.get("fielditemid").toString();
	                	String c_expr=onemap.get("c_expr").toString();	
	                	if(c_expr==null||c_expr.length()<=0) {
                            c_expr="";
                        }
	                	int s=c_expr.indexOf("^");
	                	if(s!=-1)
	                	{
	                		c_expr=c_expr.substring(s+1);
	                	}else
	                	{
	                		continue;
	                	}
	                	if(fielditemid==null||fielditemid.length()<=0)
	                	{
	                		continue;
	                	}else if(c_expr==null||c_expr.length()<=0)
	                	{
	                		continue;
	                	}else
	                	{
	                		int lxtype=countInfo.getLXtype(filedlist,fielditemid);    
	                        //  解析公式
	                    	YksjParser yp = new YksjParser(
	                    			this.userView//Trans交易类子类中可以直接获取userView
	                    			,filedlist
	                    			,YksjParser.forNormal
	                    			,lxtype//此处需要调用者知道该公式的数据类型
	                    			,YksjParser.forPerson
	                    			,"","");
	                    	
	                    	//linbz 32327 若有公式包含岗位等主集信息时，需有月汇总对应的指标数据，这些在相应的临时表不存在，故需增加Q05表名条件
                            yp.setTempTableName("Q05");
                            yp.setStdTmpTable("Q05");
                            
	                    	c_expr=c_expr.trim();
	                    	yp.run(c_expr);                	
	                    	String FSQL=yp.getSQL();
	                    	StringBuffer updateSQL=new StringBuffer();
	            	    	updateSQL.append("update Q05 set "+fielditemid+"="+FSQL);    	
	            	    	updateSQL.append(" where nbase='"+userbase+"'");
	                      	if("1".equals(kind))
	                  		{
	                      		updateSQL.append(" and e0122 like '"+code+"%'");
	                  		}else if("0".equals(kind))
	                  		{
	                  			updateSQL.append(" and e01a1 like '"+code+"%'");	
	                  	    }else if("-1".equals(kind))
	                  		{
	                  	    	String part1 = code.substring(0, 3);
	            	 	    	String part2 = code.substring(3);
	            	 	    	updateSQL.append(" and a0100 = '" + part2 + "'");
	            	 	    	updateSQL.append(" and nbase = '" + part1 + "'");
	                  		}else
	                  		{
	                  	    	updateSQL.append(" and b0110 like '"+code+"%'");	
	                  		}
	            	    	updateSQL.append(" and Q03Z0 ='"+kq_duration+"'");	    		    	
	            	    	updateSQL.append(" and q03z5 in('01','07')"); 	            	    	
	            	    	if(whereIN!=null&&whereIN.length()>0&&!"-1".equals(kind)) {
                                updateSQL.append(" and a0100 in(select a0100 "+whereIN+")");
                            }
	            	    	
	            	    	dao.update(updateSQL.toString());
	                	}        	
	                }  
	            }catch(Exception e)
	            {
	                e.printStackTrace();
	                String errItemInfo = "";
                    if (StringUtils.isNotBlank(curKqItemName)) {
                        errItemInfo = "在执行考勤规则" + curKqItemName + "的月汇总计算公式时，发生异常:<br>";
                    }
                    throw new GeneralException(errItemInfo + e.getMessage());
	            } 
	    }
	    /**
	     * 对当前考勤数据进行计算
	     * @param start_date
	     * @param end_date
	     * @param kq_dbase_list
	     * @param code
	     * @param kind
	     * @param kq_target_list
	     * @param columnlist
	     * @throws GeneralException
	     */	    
	    public String getUpdateSQL(String fielditemid,String FSQL,String kind,String whereIN)
	    {
	    	StringBuffer updateSQL=new StringBuffer();
	    	updateSQL.append("update Q05 set "+fielditemid+"="+FSQL);    	
	    	updateSQL.append(" where nbase=?");
	    	if("1".equals(kind))
			{
	    		updateSQL.append(" and e0122 like ?");
			}else if("0".equals(kind))
			{
				updateSQL.append(" and e01a1 like ?");	
		    }else
			{
				updateSQL.append(" and b0110 like ?");	
			}
	    	updateSQL.append(" and Q03Z0 =?");	    		    	
	    	updateSQL.append(" and q03z5 in(?,?)"); 
	    	updateSQL.append(" and a0100 in(select a0100 "+whereIN+")");	    	
	    	return updateSQL.toString();
	    }
	    public void singCountKQInfo(String kq_duration,String nbase,String a0100)throws GeneralException
		{
			    CountInfo countInfo=new CountInfo(this.userView,this.conn);
			    ArrayList kq_target_list=countInfo.getTargetList();
			    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    	
		    	ArrayList columnlist= new ArrayList();
		    	for(int i=0;i<fielditemlist.size();i++)
		    	{
		   	       FieldItem fielditem=(FieldItem)fielditemlist.get(i);
		   	      /* if("N".equals(fielditem.getItemtype()))
		   	       {   		   
		   		      columnlist.add(fielditem);
		   		   }*/	
		   	    if(!("i9999").equalsIgnoreCase(fielditem.getItemid()))
		   	       {
		   	    	 columnlist.add(fielditem);
		   	       }
		   	    }
		    	oneCountKQ(kq_duration,"",nbase,a0100,"EP",kq_target_list,columnlist);
		}
	    
	    /**
	     * 计算考勤部门日汇总、月汇总（暂仅固定支持计算“出勤率”）
	     * @Title: countOrgKqInfo   
	     * @Description:    
	     * @param kq_duration
	     * @throws GeneralException
	     */
	    public void countOrgKqInfo(String destTab, String orgId, String startDate, String endDate)throws GeneralException
	    {
            CountInfo countInfo = new CountInfo(this.userView,this.conn);
            ArrayList kq_target_list = countInfo.getTargetList();
            
            ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);   
            
            String  attendanceRateItem = "";
            ArrayList columnlist = new ArrayList();
            for(int i=0; i<fielditemlist.size(); i++)
            {
                FieldItem fielditem = (FieldItem)fielditemlist.get(i);
                
                if("出勤率".equalsIgnoreCase(fielditem.getItemdesc())) {
                    attendanceRateItem = fielditem.getItemid();
                }
                
                if(!("i9999").equalsIgnoreCase(fielditem.getItemid())) {
                    columnlist.add(fielditem);
                }
            }
            
            //没有出勤率指标就不用计算了
            if("".equalsIgnoreCase(attendanceRateItem)) {
                return;
            }
            
            ContentDAO dao = new ContentDAO(this.conn);
            try
            {
                for(int i=0;i<kq_target_list.size();i++)
                {
                    HashMap onemap = (HashMap)kq_target_list.get(i);
                    String fielditemid = onemap.get("fielditemid").toString();
                    if(fielditemid == null||fielditemid.length()<=0) {
                        continue;
                    }
                    
                    if(!attendanceRateItem.equalsIgnoreCase(fielditemid)) {
                        continue;
                    }
                    
                    String c_expr = onemap.get("c_expr").toString();  
                    if(c_expr==null || c_expr.length()<=0) {
                        continue;
                    }
                    
                    int s = c_expr.indexOf("^");
                    if(s < 0) {
                        continue;
                    }
                    
                    c_expr = c_expr.substring(s+1);
                    
                    if(c_expr==null || c_expr.length()<=0) {
                        continue;
                    }
                    
                    int lxtype = countInfo.getLXtype(columnlist, fielditemid);    
                    //  解析公式
                    YksjParser yp = new YksjParser(
                            this.userView//Trans交易类子类中可以直接获取userView
                            ,columnlist
                            ,YksjParser.forNormal
                            ,lxtype//此处需要调用者知道该公式的数据类型
                            ,YksjParser.forPerson
                            ,"","");
                    c_expr = c_expr.trim();
                    //System.out.println("c_expr----->"+c_expr);                        
                    yp.run(c_expr);                 
                    String FSQL = yp.getSQL();
                    
                    StringBuffer updateSQL = new StringBuffer();
                    updateSQL.append("update ").append(destTab).append(" set "+fielditemid+"="+FSQL);       
                    updateSQL.append(" where b0110='").append(orgId).append("'");
                    
                    if("Q07".equalsIgnoreCase(destTab)) {
                      updateSQL.append(" and Q03Z0>='").append(startDate).append("'");   
                      updateSQL.append(" and Q03Z0<='").append(endDate).append("'"); 
                    } else {
                        updateSQL.append(" and Q03Z0='").append(startDate).append("'");
                    }
                    dao.update(updateSQL.toString());
                }  
            }catch(Exception e)
            {
                  e.printStackTrace();                   
                throw GeneralExceptionHandler.Handle(e);
            } 
	    }
	    
        public void setCode(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }
        public void setKind(String kind) {
            this.kind = kind;
        }
        public String getKind() {
            return kind;
        }
}
