package com.hjsj.hrms.redevelopment.extractexperter.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;

import javax.sql.RowSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @dept  项目研发部
 * @author zhangh
 * @time 2015-12-11
 */
public class InsertProjectInforTrans extends IBusiness {
    public static void main(String[] args) {
		String num=null;
		/**
		 * 判断数字正则表达式
		 */
		/*if(num.matches("[0-9]{2,6}")){
			System.out.println(num);
		}*/
		num+="dasdas";
		//System.out.println(num);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");   //日期格式转换
		try {
			String datee="2015-12-16 00:00:00.0";
			Date date=sdf.parse(datee.substring(0,9));
			System.out.println(sdf.format(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    public void execute() throws GeneralException {
        try {
        	String opt=(String)this.getFormHM().get("opt");
        	if("insertInfo".equalsIgnoreCase(opt)){
        	ContentDAO dao=new ContentDAO(this.frameconn);
        	StringBuffer sqlBuf=new StringBuffer();
        	sqlBuf.append("insert into N03 values(isnull(((select MAX(n0301) from N03)+1),1),");
        	String proj_name=(String)this.getFormHM().get("proj_name");  //项目名称
        	sqlBuf.append("'"+proj_name+"',");
        	String proj_content=(String)this.getFormHM().get("proj_content"); //项目内容
        	sqlBuf.append("'"+proj_content+"',");
        	String need_dept=(String)this.getFormHM().get("need_dept");   //需求部门
        	sqlBuf.append("'"+need_dept+"',");
        	String manager_dept=(String)this.getFormHM().get("manager_dept"); //管理部门
        	sqlBuf.append("'"+manager_dept+"',");
        	String comment_time=(String)this.getFormHM().get("comment_time"); //评选时间
        	sqlBuf.append("CONVERT(datetime,'"+comment_time.replace("T", " ")+"'),");
        	String comment_addr=(String)this.getFormHM().get("comment_addr"); //评选地点
        	sqlBuf.append("'"+comment_addr+"',");
        	String sup_person=(String)this.getFormHM().get("sup_person"); //抽取人
        	String sup_personid=(String)this.getFormHM().get("sup_personid");
        	sqlBuf.append("'"+sup_person+"',");
        	String sup_time=(String)this.getFormHM().get("sup_time"); //抽取时间
        	sqlBuf.append("CONVERT(datetime,'"+sup_time.replace("T", " ")+"'),");
        	String see_person=(String)this.getFormHM().get("see_person");  //监督人
        	String see_personid=(String)this.getFormHM().get("see_personid");  //监督人
        	sqlBuf.append("'"+see_person+"',");
        	sqlBuf.append("null,");
        	sqlBuf.append("null,");
        	String invite_time=(String)this.getFormHM().get("invite_time"); //招标时间
        	sqlBuf.append("CONVERT(datetime,'"+invite_time.replace("T", " ")+"'),");
        	if(this.getFormHM().get("right")!=null){
        	String right=this.getFormHM().get("right").toString();//正选人数
        		sqlBuf.append(""+right+",");
        	//}
        	}else{
        		sqlBuf.append("5,");
        	}
        	if(this.getFormHM().get("spa")!=null){
        	String spa=this.getFormHM().get("spa").toString(); //备选人数
            sqlBuf.append(""+spa+",");   
        	}else{
        	sqlBuf.append("5,");
        	}
        	String category=(String)this.getFormHM().get("category"); //所属科别
            if(category==null)
            sqlBuf.append("null,");
            else
        	sqlBuf.append("'"+category+"',");
        	String special1=(String)this.getFormHM().get("special1"); //专业一
            if(special1==null)
            sqlBuf.append("null,");
            else
        	sqlBuf.append("'"+special1+"',");
        	String special2=(String)this.getFormHM().get("special2"); //专业二
        	if(special2==null)
        	sqlBuf.append("null,");
        	else
        	sqlBuf.append("'"+special2+"',");
        	ArrayList<MorphDynaBean> list1=(ArrayList<MorphDynaBean>)this.getFormHM().get("selected1");
        	ArrayList<MorphDynaBean> list2=(ArrayList<MorphDynaBean>)this.getFormHM().get("selected2"); //所抽选的专家
        	//String experts=null;
        	if(list2!=null){
        		/**
        		 * 二次抽选的结果记录
        		 */
        		String experts="";
               Iterator<MorphDynaBean> it=list2.iterator();
               int i = 0;
               while(it.hasNext()){
            	   MorphDynaBean  bean=it.next();
            	// int projno=Integer.valueOf(numb)+1;
            	 //  id=00000595, phone=010-86342312, category=临床, a0101=谭红, flag=0, a0100=00000595, remark=备选, accept=, tel=13552017643, special=经济学、经济学, dept=党委工作部
            	 String str="2";
            	 String accept="2";
            	 if("正选".equals(bean.get("remark"))){
            		 str="1";
            	 }
            	 if(bean.get("accept")!=null)
            	 if("是".equals(bean.get("accept"))){
            		 accept="1";
            		 i++;
            		 if(i<6)
            		 experts+=bean.get("a0101")+",";
            	 }
            	 String sql="insert into N04 values(isnull(((select MAX(n0401) from N04)+1),1),isnull(((select MAX(n0301) from N03)+1),1),'usr','"+bean.get("id")+"','"+bean.get("a0101")+"'" +
            	 		",'"+bean.get("a0100")+"','"+bean.get("tel")+"','"+bean.get("phone")+"','"+bean.get("special")+"','"+bean.get("flag")+"',"+str+",'"+accept+"',2,'"+bean.get("category")+"','"+bean.get("dept")+"')";
            	// String str=""; 
            	 dao.update(sql);
               }
            	// String a0101=  (String)bean.get("a0101");
            	// experts+=a0101+","; 
            	// System.out.println(a0100);
            	 // LazyDynaBean bean=(LazyDynaBean)it.next();
            	//  System.out.println(bean.get("a0100"));
            	 /**
            	  * 第一次抽选结果记录
            	  */
            	 Iterator<MorphDynaBean> itt=list1.iterator();
                 while(itt.hasNext()){
                	 MorphDynaBean  bean=itt.next(); 
              	// int projno=Integer.valueOf(numb)+1;
              	 //  id=00000595, phone=010-86342312, category=临床, a0101=谭红, flag=0, a0100=00000595, remark=备选, accept=, tel=13552017643, special=经济学、经济学, dept=党委工作部
              	 String str2="2";
              	 String accept2="2";
              	 if("正选".equals(bean.get("remark"))){
              		 str2="1";
              	 }
              	if(bean.get("accept")!=null)
              	 if("是".equals(bean.get("accept"))){
              		 accept2="1";
              	 }
              	 String sql2="insert into N04 values(isnull(((select MAX(n0401) from N04)+1),1),isnull(((select MAX(n0301) from N03)+1),1),'usr','"+bean.get("id")+"','"+bean.get("a0101")+"'" +
              	 		",'"+bean.get("a0100")+"','"+bean.get("tel")+"','"+bean.get("phone")+"','"+bean.get("special")+"','"+bean.get("flag")+"',"+str2+",'"+accept2+"',1,'"+bean.get("category")+"','"+bean.get("dept")+"')";
              	// String str=""; 
              	 dao.update(sql2);  
               }
            //   experts.substring(0, experts.length()-3);
                 if(experts.length()>0)
                     sqlBuf.append("'"+experts.substring(0, experts.length()-1)+"')");
                     else
                     sqlBuf.append("null)");	
        	}else if(list1!=null && list2==null){
        		String experts="";
                Iterator<MorphDynaBean> it=list1.iterator();
                int i = 0;
                while(it.hasNext()){
             	   MorphDynaBean  bean=it.next();
             	// int projno=Integer.valueOf(numb)+1;
             	 //  id=00000595, phone=010-86342312, category=临床, a0101=谭红, flag=0, a0100=00000595, remark=备选, accept=, tel=13552017643, special=经济学、经济学, dept=党委工作部
             	 String str="2";
             	 String accept="2";
             	 
             	 if("正选".equals(bean.get("remark"))){
            		 str="1";
            	 }
             	if(bean.get("accept")!=null)
            	 if("是".equals(bean.get("accept"))){
            		 accept="1";
            		 i++;
            		 if(i<6)
            		 experts+=bean.get("a0101")+",";
            	 }
            	/* if(bean.get("remark").equals("正选") && bean.get("accept").equals("是")){
            		 experts+=bean.get("a0101")+","; 
            	 }*/
             	 String sql="insert into N04 values(isnull(((select MAX(n0401) from N04)+1),1),isnull(((select MAX(n0301) from N03)+1),1),'usr','"+bean.get("id")+"','"+bean.get("a0101")+"'" +
             	 		",'"+bean.get("a0100")+"','"+bean.get("tel")+"','"+bean.get("phone")+"','"+bean.get("special")+"','"+bean.get("flag")+"',"+str+",'"+accept+"',1,'"+bean.get("category")+"','"+bean.get("dept")+"')";
             	// String str=""; 
             	 dao.update(sql);
                }
                if(experts.length()>0)
                sqlBuf.append("'"+experts.substring(0, experts.length()-1)+"')");
                else
                sqlBuf.append("null)");	
        	}else{
        		sqlBuf.append("null)");
        	}
        	dao.update(sqlBuf.toString());
        	}
        	if("selexpert".equalsIgnoreCase(opt)){
        		String projectId=(String)this.getFormHM().get("projectIds");
        		/**
        		 * (select codeitemdesc from organization where codesetid='UM' and codeitemid=n0304)
        		 * 此处不转化
        		 */ 
        		String sql="select n0302,n0303,n0304," +
        				"n0305,n0306,n0307," +
        				"n0308,n0309,n0310,n0311,n0312,n0313,n0314,n0315,n0316,n0317,n0318,n0301 from N03 where n0301="+projectId+"";
        		ContentDAO dao=new ContentDAO(this.frameconn);
        		String sql1="select N0404,N0405 from N04 where N0402 = "+projectId+"  and N0410=1" ;
        		RowSet rs = dao.search(sql1);
        		List list=new ArrayList();
    			while(rs.next()){
    				list.add(rs.getString(1)+","+rs.getString(2));
    			}
            	rs= dao.search(sql);
            	//JSONArray array=new JSONArray();
            	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            	SimpleDateFormat sdfExt=new SimpleDateFormat("MM/dd/YYYY");
            	Date date = null;
            	JSONObject obj = null;
            	//ContentDAO dao = new ContentDAO(this.frameconn);
    			String sqls="select max(N0413) len from N04  where N0402="+projectId+" "; 
    			RowSet rowSet=dao.search(sqls);
    			sqls="";
    			if(rowSet.next()){
    				if(rowSet.getString(1)!=null){
    				if("2".equals(rowSet.getString(1))){
    					sqls="2";
    				}else{
    					sqls="1";
    				}}else{
    					sqls="0";
    				}
    			}
            	if(rs.next()){
            		obj=new JSONObject();
            		obj.put("proj_name", rs.getString(1));
            		obj.put("proj_content", rs.getString(2));
            		obj.put("need_dept", rs.getString(3));
            		obj.put("manager_dept", rs.getString(4));
            		if(rs.getString(5)!=null){
            			String time=rs.getString(5).substring(0,10);
            			String comment_time=sdfExt.format(sdf.parse(time));
            		//	date=sdf.parse(rs.getString(5));
            			//String  comment_time=new SimpleDateFormat(rs.getString(5));
            			obj.put("comment_time", comment_time);
            		}else{
            		obj.put("comment_time", rs.getString(5));
            		}
            		obj.put("comment_addr", rs.getString(6));	
            		obj.put("sup_person", rs.getString(7));
            		if(rs.getString(8)!=null){
            			String time=rs.getString(8).substring(0,10);
            			String sup_time=sdfExt.format(sdf.parse(time));
            		    obj.put("sup_time", sup_time);
            		}else{
            			  obj.put("sup_time", rs.getShort(8));
            		}
            		obj.put("see_person", rs.getString(9));
            		obj.put("result", rs.getString(10));
            		obj.put("money", rs.getString(11));
            		obj.put("right", rs.getString(13));
            		obj.put("spa", rs.getString(14));
            		obj.put("category", rs.getString(15));
            		obj.put("special1", rs.getString(16));
            		obj.put("special2", rs.getString(17));
            		if(rs.getString(12)!=null){
            			String time=rs.getString(12).substring(0,10);
            			String sup_time=sdfExt.format(sdf.parse(time));
            		    obj.put("invite_time", sup_time);
            		}else{
            			  obj.put("invite_time", rs.getShort(12));
            		}
            		obj.put("proj_id", projectId);
            	//	if(list)
            		obj.put("list", list);
            		if(!"".equals(sqls)){
            			obj.put("times", sqls);
            		}
            		//obj.put("spa", rs.getString(17));
            		//array.add(obj);
            	}
            	this.getFormHM().put("data", obj);
        	}
        	if("updproject".equals(opt)){

            	ContentDAO dao=new ContentDAO(this.frameconn);
            	StringBuffer sqlBuf=new StringBuffer();
            	String proj_id=(String)this.getFormHM().get("projectId");
            	sqlBuf.append("update N03 set ");
            	String proj_name=(String)this.getFormHM().get("proj_name");  //项目名称
            	sqlBuf.append("n0302='"+proj_name+"',");
            	String proj_content=(String)this.getFormHM().get("proj_content"); //项目内容
            	sqlBuf.append("n0303='"+proj_content+"',");
            	String need_dept=(String)this.getFormHM().get("need_dept");   //需求部门
            	sqlBuf.append("n0304='"+need_dept+"',");
            	String manager_dept=(String)this.getFormHM().get("manager_dept"); //管理部门
            	sqlBuf.append("n0305='"+manager_dept+"',");
            	String comment_time=(String)this.getFormHM().get("comment_time"); //评选时间
            	sqlBuf.append("n0306=CONVERT(datetime,'"+comment_time.replace("T", " ")+"'),");
            	String comment_addr=(String)this.getFormHM().get("comment_addr"); //评选地点
            	sqlBuf.append("n0307='"+comment_addr+"',");
            	String sup_person=(String)this.getFormHM().get("sup_person"); //抽取人
            	sqlBuf.append("n0308='"+sup_person+"',");
            	String sup_time=(String)this.getFormHM().get("sup_time"); //抽取时间
            	sqlBuf.append("n0309=CONVERT(datetime,'"+sup_time.replace("T", " ")+"'),");
            	String see_person=(String)this.getFormHM().get("see_person");  //监督人
            	sqlBuf.append("n0310='"+see_person+"',");
            	//sqlBuf.append("null,");
            	//sqlBuf.append("null,");
            	String right=(String)this.getFormHM().get("right");//正选人数
            	sqlBuf.append("n0314="+right+",");
            	//}
            	String spa=(String)this.getFormHM().get("spa"); //备选人数
            	sqlBuf.append("n0315="+spa+",");
            	String category=(String)this.getFormHM().get("category"); //所属科别
            	if(category==null)
            	sqlBuf.append("n0316=null,");
            	else
            	sqlBuf.append("n0316='"+category+"',");
            	String special1=(String)this.getFormHM().get("special1"); //专业一
            	if(special1==null)
            	sqlBuf.append("n0317=null,");	
            	else
            	sqlBuf.append("n0317='"+special1+"',");
            	String special2=(String)this.getFormHM().get("special2"); //专业二
            	if(special2==null)
            	sqlBuf.append("n0318=null,");
            	else
            	sqlBuf.append("n0318='"+special2+"',");
            	ArrayList<MorphDynaBean> list1=(ArrayList<MorphDynaBean>)this.getFormHM().get("selected1");
            	ArrayList<MorphDynaBean> list2=(ArrayList<MorphDynaBean>)this.getFormHM().get("selected2"); //所抽选的专家
            	//String experts=null;
            	if(list2!=null){
            		/**
            		 * 二次抽选的结果记录
            		 */
            		String clear="delete from N04 where N0402="+proj_id+"";
            		dao.update(clear);
            		String experts="";
                   Iterator<MorphDynaBean> it=list2.iterator();
                   int i = 0;
                   while(it.hasNext()){
                	  // i++;
                	   MorphDynaBean  bean=it.next();
                	// int projno=Integer.valueOf(numb)+1;
                	 //  id=00000595, phone=010-86342312, category=临床, a0101=谭红, flag=0, a0100=00000595, remark=备选, accept=, tel=13552017643, special=经济学、经济学, dept=党委工作部
                	 String str="2";
                	 String accept="2";
                	 if("正选".equals(bean.get("remark")) ){
                		 str="1";
                		 
                	 }
                	 if(bean.get("accept")!=null)
                	 if("是".equals(bean.get("accept"))){
                		 accept="1";
                		 i++;
                		 if(i<6)
                		 experts+=bean.get("a0101")+",";
                	 }
                	 String sql="insert into N04 values(isnull(((select MAX(n0401) from N04)+1),1),"+proj_id+",'usr','"+bean.get("id")+"','"+bean.get("a0101")+"'" +
                	 		",'"+bean.get("a0100")+"','"+bean.get("tel")+"','"+bean.get("phone")+"','"+bean.get("special")+"','"+bean.get("flag")+"',"+str+",'"+accept+"',2,'"+bean.get("category")+"','"+bean.get("dept")+"')";
                	// String str=""; 
                	 dao.update(sql);
                   }
                	// String a0101=  (String)bean.get("a0101");
                	// experts+=a0101+","; 
                	// System.out.println(a0100);
                	 // LazyDynaBean bean=(LazyDynaBean)it.next();
                	//  System.out.println(bean.get("a0100"));
                	 /**
                	  * 第一次抽选结果记录
                	  */
                	 Iterator<MorphDynaBean> itt=list1.iterator();
                     while(itt.hasNext()){
                    	 MorphDynaBean  bean=itt.next();
                  	// int projno=Integer.valueOf(numb)+1;
                  	 //  id=00000595, phone=010-86342312, category=临床, a0101=谭红, flag=0, a0100=00000595, remark=备选, accept=, tel=13552017643, special=经济学、经济学, dept=党委工作部
                  	 String str2="2";
                  	 String accept2="2";
                  	 if("正选".equals(bean.get("remark"))){
                  		 str2="1";
                  	 }
                  	if(bean.get("accept")!=null)
                  	 if("是".equals(bean.get("accept"))){
                  		 accept2="1";
                  	 }
                  	 String sql2="insert into N04 values(isnull(((select MAX(n0401) from N04)+1),1),"+proj_id+",'usr','"+bean.get("id")+"','"+bean.get("a0101")+"'" +
                  	 		",'"+bean.get("a0100")+"','"+bean.get("tel")+"','"+bean.get("phone")+"','"+bean.get("special")+"','"+bean.get("flag")+"',"+str2+",'"+accept2+"',1,'"+bean.get("category")+"','"+bean.get("dept")+"')";
                  	// String str=""; 
                  	 dao.update(sql2);  
                   }
                   //experts.substring(0, experts.length()-2);
                   sqlBuf.append(" N0319='"+experts.substring(0, experts.length()-1)+"',");
            	}else if(list1!=null && list2==null){
            		String clear="delete from N04 where N0402="+proj_id+"";
            		dao.update(clear);
            		String experts="";
                    Iterator<MorphDynaBean> it=list1.iterator();
                    int i=0;
                    while(it.hasNext()){
                 	   MorphDynaBean  bean=it.next();
                 	// int projno=Integer.valueOf(numb)+1;
                 	 //  id=00000595, phone=010-86342312, category=临床, a0101=谭红, flag=0, a0100=00000595, remark=备选, accept=, tel=13552017643, special=经济学、经济学, dept=党委工作部
                 	 String str="2";
                 	 String accept="2";
                 	 
                 	 if("正选".equals(bean.get("remark"))){
                 		 str="1";
                 		// experts+=bean.get("a0101")+",";
                 	 }
                 	if(bean.get("accept")!=null)
                 	 if("是".equals(bean.get("accept"))){
                 		 accept="1";
                 		  i++;
               		      if(i<6)
               		    experts+=bean.get("a0101")+",";
                 	 }
                 	 String sql="insert into N04 values(isnull(((select MAX(n0401) from N04)+1),1),"+proj_id+",'usr','"+bean.get("id")+"','"+bean.get("a0101")+"'" +
                 	 		",'"+bean.get("a0100")+"','"+bean.get("tel")+"','"+bean.get("phone")+"','"+bean.get("special")+"','"+bean.get("flag")+"',"+str+",'"+accept+"',1,'"+bean.get("category")+"','"+bean.get("dept")+"')";
                 	// String str=""; 
                 	 dao.update(sql);
                    }
                   // experts.substring(0, experts.length()-2);
                    sqlBuf.append(" N0319='"+experts.substring(0, experts.length()-1)+"',");
            	}else{
            		sqlBuf.append(" N0319=null)");
            	}
            	String invite_time=(String)this.getFormHM().get("invite_time"); //招标时间
            	sqlBuf.append("n0313=CONVERT(datetime,'"+invite_time.replace("T", " ")+"') ");
            	sqlBuf.append(" where n0301="+proj_id+"");
            	dao.update(sqlBuf.toString());
            		
        	}
        	if("save".equals(opt)){
        		ArrayList<MorphDynaBean> list=(ArrayList<MorphDynaBean>)this.getFormHM().get("saveinfo");
        		if(list!=null){
        		ContentDAO  dao=new ContentDAO(frameconn);
        		   Iterator<MorphDynaBean> it=list.iterator();
        		   while(it.hasNext()){
        		   MorphDynaBean  bean= it.next();
        		   String sql=""; 
        		   if(bean.get("rmb")==null || "".equals(bean.get("rmb"))){
        		   sql="update N03 set n0311='"+bean.get("result")+"',n0312=null  where n0301="+bean.get("proj_id")+"";   
        		   }else{
        		   sql="update N03 set n0311='"+bean.get("result")+"', n0312="+bean.get("rmb")+"  where n0301="+bean.get("proj_id")+"";
        		   }
        		    
        		   dao.update(sql);
        		}
        		}
        	}if("chargesel".equals(opt)){
        		String proj_id=(String)this.getFormHM().get("projectIds");
        		String sql="select n0311,n0312 from N03 where n0301='"+proj_id+"'";
        		boolean  flag=false;
        		ContentDAO  dao=new ContentDAO(frameconn);
        		RowSet rs=dao.search(sql);
        		if(rs.next()){
        			String sq=rs.getString(1);
        			String sd=rs.getString(2);
        			if(sq!=null){
        			if(!"".equals(sq)){
        				flag=true;
        			}
        			}
        			if(sd!=null){
        			if(!"".equals(sd)){
        				flag=true;
        			}
        			}
        		}
        		if(flag){
        			this.getFormHM().put("data", "NO");
        		}else{
        			this.getFormHM().put("data", "");
        		}
        		
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
