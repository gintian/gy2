package com.hjsj.hrms.redevelopment.extractexperter.transaction;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.sql.RowSet;

/**
 * 
 * @author zhangh
 *
 */
public class AddProjectInforTrans extends IBusiness {
    public static void main(String[] args) {
		for(int i=0;i<10;i++){
			System.out.println();
		}
	}
    public void execute() throws GeneralException {
        try {
        	
        	String req = (String) this.getFormHM().get("req");   
        	String opt = (String)this.getFormHM().get("opt");
            String proj_id=(String)this.getFormHM().get("projectId");  //项目编号
            
        	//String sel = (String) this.getFormHM().get("bosdate");
        	if("dept".equals(req)){
        		String sel=(String)this.getFormHM().get("selitem");
            String sql = "select codeitemdesc,codeitemid from organization where codesetid='UN'";
            if(sel!=null){sql+=" and codeitemid!='"+sel+"'";}
            ContentDAO dao = new ContentDAO(this.frameconn);
            RowSet rs=dao.search(sql);
          //  StringBuffer buf=new StringBuffer(); 
            JSONArray array=new JSONArray();
            while(rs.next()){
            	JSONObject  obj=new JSONObject();
                obj.put("deptdesc", rs.getString(1));
                obj.put("deptcode", rs.getString(2));
                array.add(obj);
            }
            this.getFormHM().put("deptstr", array);
        	}else if("name".equals(req)){

        		//String sel=(String)this.getFormHM().get("selitem");
            String sql = "select a0100,a0101 from usra01";
            ContentDAO dao = new ContentDAO(this.frameconn);
            RowSet rs=dao.search(sql);
          //  StringBuffer buf=new StringBuffer(); 
            JSONArray array=new JSONArray();
            while(rs.next()){
            	JSONObject  obj=new JSONObject();
                obj.put("namedesc", rs.getString(2));
                obj.put("namecode", rs.getString(1));
                array.add(obj);
            }
            this.getFormHM().put("namestr", array);
        	
        	}else if("catagory".equals(req)){
        	    String sql = "select codeitemdesc,codeitemid from codeitem where codesetid='LA'";
                ContentDAO dao = new ContentDAO(this.frameconn);
                RowSet rs=dao.search(sql);
                StringBuffer buf=new StringBuffer();
                JSONArray array=new JSONArray();
                while(rs.next()){
                	JSONObject  obj=new JSONObject();
                    obj.put("catagorydesc", rs.getString(1));
                    obj.put("catagorycode", rs.getString(2));
                    array.add(obj);
                }
                this.getFormHM().put("categorystr", array);	
        	}else if("special".equals(req)){
        	String	sql="select codeitemdesc,codeitemid from codeitem where codesetid='AI'";
        	ContentDAO dao = new ContentDAO(this.frameconn);
            RowSet rs=dao.search(sql);
           // StringBuffer buf=new StringBuffer();
            JSONArray array=new JSONArray();
            while(rs.next()){
            	JSONObject  obj=new JSONObject();
                obj.put("specialdesc", rs.getString(1));
                obj.put("specialcode", rs.getString(2));
                array.add(obj);
            }
            this.getFormHM().put("specialstr", array);	
        	}else if("selexpert".equals(req)){
        		/**
        		 * 查询符合条件的专家
        		 */
        		ContentDAO dao = new ContentDAO(this.frameconn);
        		if(opt!=null){
        			/**
        			 * 抽选专家
        			 */
                	String category=(String) this.getFormHM().get("category");
                	String special1=(String) this.getFormHM().get("special1");
                	String special2=(String) this.getFormHM().get("special2");
                    if(category!=null && special1!=null && special2!=null){
                    	StringBuffer buf=new StringBuffer();
                    	if((category==null || "".equals(category)) && (special1==null || "".equals(special1)) && (special2==null || "".equals(special2))){
                    		System.out.println("3123");
                    	}else{
                        	buf.append("select * from (select a.A0100,a.A0101,h.I9999,(select codeitemdesc from " +
                        			"codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                        			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                        			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                        			"left join usraah h on a.A0100=h.a0100  where a.A01AL = '1' and a.A01AF=");
                        	if(!"".equals(category)){
                        	buf.append("'"+category+"'");	
                        	}else{
                        	buf.append("a.A01AF ");	
                        	}
                        	if(!"".equals(special1) && !"".equals(special2)){
                        	buf.append("and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')");	
                        	}else if("".equals(special1) && !"".equals(special2)){
                        	buf.append("and h.aahaa='"+special2+"'");	
                        	}else if(!"".equals(special1) && "".equals(special2)){
                            buf.append("and h.aahaa='"+special1+"'");	
                            }else{
                            buf.append("");	
                            }
                        	buf.append(") g ");
                        	buf.append("where not exists (select 1 from (select a.A0100,a.A0101,h.I9999," +
                        			"(select codeitemdesc from codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                        			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                        			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                        			"left join usraah h on a.A0100=h.a0100  where a.A01AL = '1' and a.A01AF=");
                          	if(!"".equals(category)){
                            	buf.append("'"+category+"'");	
                            	}else{
                            	buf.append("a.A01AF ");	
                            	}
                            	if(!"".equals(special1) && !"".equals(special2)){
                            	buf.append("and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')");	
                            	}else if("".equals(special1) && !"".equals(special2)){
                            	buf.append("and h.aahaa='"+special2+"'");	
                            	}else if(!"".equals(special1) && "".equals(special2)){
                            	buf.append("and h.aahaa='"+special1+"'");	
                            	}else{
                            	buf.append("");	
                            	}
                            	buf.append(") b ");
                            	buf.append("where b.A0100=g.A0100 and b.i9999 >g.i9999)");
                        	//ContentDAO dao = new ContentDAO(this.frameconn);
                            RowSet rs=dao.search(buf.toString());
                         //   StringBuffer buf=new StringBuffer();
                            JSONArray array=new JSONArray();
                            while(rs.next()){
                            	JSONObject  obj=new JSONObject();
                                obj.put("id", rs.getString(1));
                                obj.put("a0101", rs.getString(2));
                                obj.put("category", rs.getString(4));
                                if(rs.getString(5)!=null)
                                    obj.put("dept", rs.getString(5));
                                    else
                                    obj.put("dept", "");
                                String sql1="select (select codeitemdesc from codeitem where codeitemid=aahaa and codesetid='ai') special from usraah where a0100='"+rs.getString(1)+"'";
                                RowSet rs1=dao.search(sql1);
                                String specials="";
                                while(rs1.next()){
                                	specials+=rs1.getString(1)+"、";
                                }
                                ;
                                if(specials!=null && !"".equals(specials)){
                                obj.put("special", specials.substring(0, specials.length()-1));
                                }else{
                                 obj.put("special", "");   
                                }
                                obj.put("a0100", rs.getString(7));
                                obj.put("tel", rs.getString(8));
                                obj.put("flag", rs.getString(9));
                                obj.put("phone", rs.getString(10));
                                array.add(obj);
                            }
                            this.getFormHM().put("expert", array);
                            }
                    }else{
            	 	String str="select N0316,N0317,N0318 from N03 where N0301="+proj_id+"";
            	 	RowSet rs = dao.search(str);
                	if(rs.next()){
                		category=rs.getString(1);
                		special1=rs.getString(2);
                		special2=rs.getString(3);
                	}
                	StringBuffer buf=new StringBuffer();
                	if((category==null || "".equals(category)) && (special1==null || "".equals(special1)) && (special2==null || "".equals(special2))){
                		
                	}else{
                    
                    	
                    	buf.append("select * from (select a.A0100,a.A0101,h.I9999,(select codeitemdesc from " +
                    			"codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                    			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                    			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                    			"left join usraah h on a.A0100=h.a0100  where a.A01AL = '1' and a.A01AF=");
                    	if(!"".equals(category)){
                    	buf.append("'"+category+"'");	
                    	}else{
                    	buf.append("a.A01AF ");	
                    	}
                    	if( special1!=null && !"".equals(special1)  && special2!=null && !"".equals(special2)){
                    	buf.append("and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')");	
                    	}else if((special1==null || "".equals(special1)) && (special2!=null && !"".equals(special2))){
                    	buf.append("and h.aahaa='"+special2+"'");	
                    	}else if(special1!=null && (!"".equals(special1)) && (special2==null || "".equals(special2))){
                        buf.append("and h.aahaa='"+special1+"'");	
                        }else{
                        buf.append("");	
                        }
                    	buf.append(") g ");
                    	buf.append("where not exists (select 1 from (select a.A0100,a.A0101,h.I9999," +
                    			"(select codeitemdesc from codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                    			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                    			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                    			"left join usraah h on a.A0100=h.a0100  where a.A01AL = '1' and a.A01AF=");
                      	if(!"".equals(category)){
                        	buf.append("'"+category+"'");	
                        	}else{
                        	buf.append("a.A01AF ");	
                        	}
                      	if( special1!=null && !"".equals(special1)  && special2!=null && !"".equals(special2)){
                        	buf.append("and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')");	
                        	}else if((special1==null || "".equals(special1)) && (special2!=null && !"".equals(special2))){
                        	buf.append("and h.aahaa='"+special2+"'");	
                        	}else if(special1!=null && (!"".equals(special1)) && (special2==null || "".equals(special2))){
                            buf.append("and h.aahaa='"+special1+"'");	
                            }else{
                            buf.append("");	
                            }
                        	buf.append(") b ");
                        	buf.append("where b.A0100=g.A0100 and b.i9999 >g.i9999)");
                    	//ContentDAO dao = new ContentDAO(this.frameconn);
                          rs=dao.search(buf.toString());
                     //   StringBuffer buf=new StringBuffer();
                        JSONArray array=new JSONArray();
                        while(rs.next()){
                        	JSONObject  obj=new JSONObject();
                            obj.put("id", rs.getString(1));
                            obj.put("a0101", rs.getString(2));
                            obj.put("category", rs.getString(4));
                            if(rs.getString(5)!=null)
                                obj.put("dept", rs.getString(5));
                            else
                                obj.put("dept", "");
                            String sql1="select (select codeitemdesc from codeitem where codeitemid=aahaa and codesetid='ai') special from usraah where a0100='"+rs.getString(1)+"'";
                            RowSet rs1=dao.search(sql1);
                            String specials="";
                            while(rs1.next()){
                            	specials+=rs1.getString(1)+"、";
                            }
                            ;
                            if(specials!=null && !"".equals(specials)){
                            obj.put("special", specials.substring(0, specials.length()-1));
                            }else{
                             obj.put("special", "");   
                            }
                            obj.put("a0100", rs.getString(7));
                            obj.put("tel", rs.getString(8));
                            obj.put("flag", rs.getString(9));
                            obj.put("phone", rs.getString(10));
                            array.add(obj);
                        }
                        this.getFormHM().put("expert", array);
                        }
                	
            	}
        		}else{
        			/**
        			 * 创建项目  
        			 */
            	String category=(String) this.getFormHM().get("category");
            	String special1=(String) this.getFormHM().get("special1");
            	String special2=(String) this.getFormHM().get("special2");
            	StringBuffer buf=new StringBuffer();
            	if((category==null || "".equals(category)) && (special1==null || "".equals(special1)) && (special2==null || "".equals(special2))){
            		
            	}else{
                
   /*             	String sql="select * from (select a.A0100,a.A0101,h.I9999,(select codeitemdesc from " +
                			"codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                			"left join usraah h on a.A0100=h.a0100  where a.A01AF='"+category+"' and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')) g  " +
                			"where not exists (select 1 from (select a.A0100,a.A0101,h.I9999," +
                			"(select codeitemdesc from codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                			"left join usraah h on a.A0100=h.a0100  where a.A01AF='"+category+"' and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')) b " +
                			"where b.A0100=g.A0100 and b.i9999 >g.i9999)";*/
                	buf.append("select * from (select a.A0100,a.A0101,h.I9999,(select codeitemdesc from " +
                			"codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                			"left join usraah h on a.A0100=h.a0100  where a.A01AL = '1' and  a.A01AF=");
                	if(!"".equals(category)){
                	buf.append("'"+category+"'");	
                	}else{ 
                	buf.append("a.A01AF ");	
                	}
                	if(!"".equals(special1) && !"".equals(special2)){
                	buf.append("and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')");	
                	}else if("".equals(special1) && !"".equals(special2)){
                	buf.append("and h.aahaa='"+special2+"'");	
                	}else if(!"".equals(special1) && "".equals(special2)){
                    buf.append("and h.aahaa='"+special1+"'");	
                    }else{
                    buf.append("");	
                    }
                	buf.append(") g ");
                	buf.append("where not exists (select 1 from (select a.A0100,a.A0101,h.I9999," +
                			"(select codeitemdesc from codeitem where codeitemid=a.A01AF and codesetid='LA')category," +
                			"(select codeitemdesc from organization where codeitemid=a.B0110)dept," +
                			"(select codeitemdesc from codeitem where codeitemid=h.aahaa and codesetid='ai')special,A0144, A0148 ,0 as flag,A01AM from UsrA01 a " +
                			"left join usraah h on a.A0100=h.a0100  where a.A01AL = '1' and a.A01AF=");
                  	if(!"".equals(category)){
                    	buf.append("'"+category+"'");	
                    	}else{
                    	buf.append("a.A01AF ");	
                    	}
                    	if(!"".equals(special1) && !"".equals(special2)){
                    	buf.append("and (h.aahaa='"+special1+"' or  h.aahaa='"+special2+"')");	
                    	}else if("".equals(special1) && !"".equals(special2)){
                    	buf.append("and h.aahaa='"+special2+"'");	
                    	}else if(!"".equals(special1) && "".equals(special2)){
                    	buf.append("and h.aahaa='"+special1+"'");	
                    	}else{
                    	buf.append("");	
                    	}
                    	buf.append(") b ");
                    	buf.append("where b.A0100=g.A0100 and b.i9999 >g.i9999)");
                	//ContentDAO dao = new ContentDAO(this.frameconn);
                    RowSet rs=dao.search(buf.toString());
                 //   StringBuffer buf=new StringBuffer();
                    JSONArray array=new JSONArray();
                    while(rs.next()){
                    	JSONObject  obj=new JSONObject();
                        obj.put("id", rs.getString(1));
                        obj.put("a0101", rs.getString(2));
                        obj.put("category", rs.getString(4));
                        if(rs.getString(5)!=null)
                        obj.put("dept", rs.getString(5));
                        else
                        obj.put("dept", "");
                        String sql1="select (select codeitemdesc from codeitem where codeitemid=aahaa and codesetid='ai') special from usraah where a0100='"+rs.getString(1)+"'";
                        RowSet rs1=dao.search(sql1);
                        String specials="";
                        while(rs1.next()){
                        	specials+=rs1.getString(1)+"、";
                        }
                        ;
                        if(specials!=null && !"".equals(specials)){
                        obj.put("special", specials.substring(0, specials.length()-1));
                        }else{
                         obj.put("special", "");   
                        }
                        obj.put("a0100", rs.getString(7));
                        obj.put("tel", rs.getString(8));
                        obj.put("flag", rs.getString(9));
                        obj.put("phone", rs.getString(10));
                        array.add(obj);
                    }
                    this.getFormHM().put("expert", array);
                    }
            	
                }
        		}
        	else if("isselexpert".equals(req)){
        		if(opt!=null){
        		if("sel1".equals(opt)){
        			/**
        			 * 查询第一次选中的专家
        			 */
        			ContentDAO dao = new ContentDAO(this.frameconn);
        			String sql="select N0404,N0405,N0409,N0406,N0407,N0410,N0411,N0412,N0414,N0415,N0408 from N04 where N0402 = "+proj_id+" and N0413='1' order by N0411" ;
        			 RowSet rs=dao.search(sql);
                     //   StringBuffer buf=new StringBuffer();
                        JSONArray array=new JSONArray();
                      //  ArrayList list=new ArrayList();
                        while(rs.next()){
                        	JSONObject  obj=new JSONObject();
                            obj.put("id", rs.getString(1));
                            //list.add(e)
                            obj.put("a0101", rs.getString(2));
                            obj.put("category", rs.getString(9));
                            obj.put("dept", rs.getString(10));
                            obj.put("special", rs.getString(3));
                            obj.put("a0100", rs.getString(4));
                            obj.put("tel", rs.getString(5));
                            obj.put("flag", rs.getString(6));	
                            if("1".equals(rs.getString(7))){
                                obj.put("remark", "正选");
                                }else{
                                	obj.put("remark", "备选");	
                                }
                            if("1".equals(rs.getString(8))){
                                obj.put("accept", "是");
                                }else{
                                	obj.put("accept", "否");	
                                }
                            obj.put("phone", rs.getString(11));
                            array.add(obj);
                        }
                        this.getFormHM().put("selexpert", array);
        			
        		}
        		if("sel2".equals(opt)){
        			/**
        			 * 查询第二次选中的专家
        			 */
        			ContentDAO dao = new ContentDAO(this.frameconn);
        			String sql="select N0404,N0405,N0409,N0406,N0407,N0410,N0411,N0412,N0414,N0415,N0408 from N04 where N0402 = "+proj_id+" and N0413='2' order by N0411" ;
        			 RowSet rs=dao.search(sql);
                     //   StringBuffer buf=new StringBuffer();
                        JSONArray array=new JSONArray();
                      //  ArrayList list=new ArrayList();
                        while(rs.next()){
                        	JSONObject  obj=new JSONObject();
                            obj.put("id", rs.getString(1));
                            //list.add(e)
                            obj.put("a0101", rs.getString(2));
                            obj.put("category", rs.getString(9));
                            obj.put("dept", rs.getString(10));
                            obj.put("special", rs.getString(3));
                            obj.put("a0100", rs.getString(4));
                            obj.put("tel", rs.getString(5));
                            obj.put("flag", rs.getString(6));	
                            if("1".equals(rs.getString(7))){
                                obj.put("remark", "正选");
                                }else{
                                	obj.put("remark", "备选");	
                                }
                            if("1".equals(rs.getString(8))){
                                obj.put("accept", "是");
                                }else{
                                	obj.put("accept", "否");	
                                }
                            obj.put("phone", rs.getString(11));
                            array.add(obj);
                        }
                        this.getFormHM().put("selexpert", array);	
                        }
        	}
        	}else if("all".equals(req)){
        		
        		String value=(String)this.getFormHM().get("value");
        		String realvalue="";
        		//value;
        		if(value!=null){
        			realvalue=SafeCode.decode(value) ;	
        		}
         		//String d= 
        		ContentDAO  dao=new ContentDAO(this.frameconn);
        		StringBuffer buf=new StringBuffer();
        		buf.append("select a0100,a0101,isnull((select codeitemdesc from " +
        				  "codeitem where codeitemid=A01AF and codesetid='LA'),'')category,isnull((select codeitemdesc from organization where codeitemid=B0110  ),'')dept," +
        				  "A0144, A0148 ,0 as flag,A01AM from usra01 where A01AL='1'");
        		if(value!=null && !"".equals(value)){
        			buf.append(" and a0101 like '%"+realvalue+"%'"); 
        		}
        		/*String sql="select a0100,a0101,(select codeitemdesc from " +
        				  "codeitem where codeitemid=A01AF and codesetid='LA')category,(select codeitemdesc from organization where codeitemid=B0110)dept," +
        				  "A0144, A0148 ,0 as flag,A01AM from usra01 where A01AL='1'";*/
        		System.out.println(buf.toString());
        		RowSet rs=dao.search(buf.toString());
                //   StringBuffer buf=new StringBuffer();
                   JSONArray array=new JSONArray();
                   while(rs.next()){
                   	JSONObject  obj=new JSONObject();
                       obj.put("id", rs.getString(1));
                       obj.put("a0101", rs.getString(2));
                       obj.put("category", rs.getString(3));
                       obj.put("dept", rs.getString(4));
                       String sql1="select (select codeitemdesc from codeitem where codeitemid=aahaa and codesetid='ai') special from usraah where a0100='"+rs.getString(1)+"'";
                       RowSet rs1=dao.search(sql1);
                       String specials="";
                       while(rs1.next()){
                       	specials+=rs1.getString(1)+"、";
                       }
                       ;
                       if(specials!=null && !"".equals(specials)){
                       obj.put("special", specials.substring(0, specials.length()-1));
                       }else{
                        obj.put("special", "");   
                       }
                       obj.put("a0100", rs.getString(5));
                       obj.put("tel", rs.getString(6));
                       obj.put("flag", rs.getString(7));	
                       obj.put("phone", rs.getString(8));
                       array.add(obj);
                   }
                   this.getFormHM().put("expert", array);
        	}
        	else{
        		if(opt!=null){
        			/**
        			 * 查询指定专家
        			 */
        			ContentDAO dao = new ContentDAO(this.frameconn);
        			String sql="select N0404,N0405,N0409,N0406,N0407,N0410,N0411,N0412,N0414,N0415,N0408 from N04 where N0402 = "+proj_id+" and N0410=1 and N0413='1'" ;
        			 RowSet rs=dao.search(sql);
                     //   StringBuffer buf=new StringBuffer();
                        JSONArray array=new JSONArray();
                        while(rs.next()){
                        	JSONObject  obj=new JSONObject();
                            obj.put("id", rs.getString(1));
                            //list.add(e)
                            obj.put("a0101", rs.getString(2));
                            obj.put("category", rs.getString(9));
                            obj.put("dept", rs.getString(10));
                            obj.put("special", rs.getString(3));
                            obj.put("a0100", rs.getString(4));
                            obj.put("tel", rs.getString(5));
                            obj.put("flag", rs.getString(6));	
                            if("1".equals(rs.getString(7))){
                                obj.put("remark", "正选");
                                }else{
                                	obj.put("remark", "备选");	
                                }
                            if("1".equals(rs.getString(8))){
                                obj.put("accept", "是");
                                }else{
                                	obj.put("accept", "否");	
                                }
                            obj.put("phone", rs.getString(9));
                            array.add(obj);
                        }
                        this.getFormHM().put("sedexpert", array);
        		}
        	}
        	this.getFormHM().remove("req");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	private String URLDecoder(String value, String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
