package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-9:9:41:12</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class SearchFileTrans extends IBusiness {

	 /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
   
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String field_str_item = (String)this.getFormHM().get("field_str_item");
        DateStyle first_date = new DateStyle();

    	DateStyle second_date = new DateStyle();

    	DateStyle third_date = new DateStyle();
    	String selsql = "select distinct(content_type) content_type from law_base_file where base_id in ( select base_id from law_base_struct where basetype='"+this.getFormHM().get("basetype")+"') ";
    	String status=(String)hm.get("status");
    	if(status==null||status.length()<=0)
    		status="1";
        String id="";
        
        String flag="1";
        
        if(hm.get("a_id")==null)
        {
        	id="0";
        	this.getFormHM().put("flag","1");
        }
        else
        {
        	id = (String)hm.get("a_id");
        	id = PubFunc.decrypt(SafeCode.decode(id));
        	hm.remove("a_id");
        }
        if(this.getFormHM().get("flag")==null)
        {
        	flag="1";
        }
        else
        {
        	flag=(String)this.getFormHM().get("flag");
        }
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList contentlist = new ArrayList();
        try {
			this.frowset=dao.search(selsql);
			while(this.frowset.next()){
				CommonData data = new CommonData();
				data.setDataName(this.frowset.getString("content_type"));
				data.setDataValue(this.frowset.getString("content_type"));
			   contentlist.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("contentlist",contentlist);
      
		//动态字段  郭峰
		 try{
	    	   ArrayList lawBaseFileList = new ArrayList();
	    	   String[] temp = field_str_item.split(",");
				if(temp.length>0 && !"".equals(temp[0])){
					StringBuffer sb = new StringBuffer();
					sb.append("select ");
					for(int i=0;i<temp.length;i++){
						String itemid = temp[i].split("`")[0];
						if("extfile".equalsIgnoreCase(temp[i].split("`")[0]))
							continue;
						sb.append(itemid);
						if(i!=(temp.length-1))
							sb.append(",");
					}
					if(sb.toString().endsWith(","))
						sb.setLength(sb.length()-1);
					sb.append(" from law_base_file where file_id='"+id+"'");
					frowset = dao.search(sb.toString());
					
						for(int i=0;i<temp.length;i++){
							if(!"extfile".equalsIgnoreCase(temp[i].split("`")[0])&&!"viewcount".equalsIgnoreCase(temp[i].split("`")[0])){
								FieldItem item = DataDictionary.getFieldItem(temp[i].split("`")[0]);
								if(item==null)
									continue;
								String itemid = item.getItemid();
								String itemtype = item.getItemtype();
								String itemdesc = temp[i].split("`")[1];
								String decWidth = String.valueOf(item.getDecimalwidth());
								String len = String.valueOf(item.getItemlength());
								String codesetid = item.getCodesetid();
								String inputtype = String.valueOf(item.isFillable());
								String value = "";
								if(frowset.next()){
									if("D".equalsIgnoreCase(itemtype))
										value = PubFunc.FormatDate(this.getFrowset().getDate(itemid));
									else
										value = frowset.getString(itemid)==null?"":frowset.getString(itemid);
								}
								frowset.previous();	
								String viewvalue = "";
								if("A".equalsIgnoreCase(itemtype) && !"0".equals(codesetid)){
									viewvalue = AdminCode.getCodeName(codesetid,value)==null?"":AdminCode.getCodeName(codesetid,value);
								}
								LazyDynaBean bean = new LazyDynaBean();
								bean.set("value", value);
								bean.set("itemid", itemid);
								bean.set("itemtype",itemtype);
								bean.set("itemdesc", itemdesc);
								bean.set("decWidth", decWidth);
								bean.set("len", len);
								bean.set("codesetid", codesetid);
								bean.set("viewvalue", viewvalue);
								bean.set("inputtype", inputtype+"");
								lawBaseFileList.add(bean);
								
							}
							
						}
				}
				String url = "a_base_id="+(String)this.getFormHM().get("base_id")+"&status="+status;
				this.getFormHM().put("encryptParam", PubFunc.encrypt(url));
				this.getFormHM().put("lawBaseFileList", lawBaseFileList);
	       }catch(Exception e){
	    	   e.printStackTrace();
	    	   throw GeneralExceptionHandler.Handle(e);
	       }
	       
	       
        /**
         * 按新增按钮时，则不进行查询，直接退出；是否可以在这里处理增加一条记录，考虑
         * 用户的使用习惯。
         */
    	
        RecordVo vo=new RecordVo("law_base_file");
        if("1".equals(flag))
        {
        	vo.setString("valid",status);
        	this.getFormHM().put("lawFileTb",vo);
        	this.getFormHM().put("transfercodeitemid",null);
        	return;
        }
       
        try
        {
        	StringBuffer sbSql=new StringBuffer();
        	sbSql.append("select file_id,name,title,type,content_type,valid,note_num,issue_org,notes,issue_date, ");
        	sbSql.append("implement_date ,valid_date,ext,base_id,digest,b0110,keywords,viewcount,originalext from law_base_file where file_id='");
        	sbSql.append(id);
        	sbSql.append("'");
            String sql=sbSql.toString();
            vo.setString("file_id",id);
//            vo = dao.findByPrimaryKey(vo);
            this.frowset =dao.search("select viewcount from law_base_file where file_id='"+id+"'");
            int viewcount = 0;
            if(this.frowset.next()){
            	viewcount=(this.frowset.getString("viewcount")==null?0:this.frowset.getInt("viewcount"))+1;
            }
            //vo.setInt("viewcount",vo.getString("viewcount")==null?0:vo.getInt("viewcount")+1);
            String upsql = "update law_base_file set viewcount ='"+viewcount+"' where file_id='"+id+"'";
            dao.update(upsql);
            //dao.updateValueObject(vo);
	        this.frowset=dao.search(sql);
            String digest="";
            if(this.frowset.next())
            {
            	  vo.setString("name",this.frowset.getString("name"));
            	  vo.setString("title",this.frowset.getString("title"));
            	  vo.setString("type",this.frowset.getString("type"));
            	  vo.setString("content_type",this.frowset.getString("content_type"));
            	  vo.setString("valid",this.frowset.getString("valid"));
            	  vo.setString("note_num",this.frowset.getString("note_num"));
            	  vo.setString("issue_org",this.frowset.getString("issue_org"));
            	  vo.setString("notes",this.frowset.getString("notes"));
            	  digest=Sql_switcher.readMemo(this.frowset,"digest");
            	  vo.setString("digest", Sql_switcher.readMemo(this.frowset,"digest"));
            	  vo.setString("viewcount",this.frowset.getString("viewcount"));
            	 // System.out.println(this.frowset.getString("digest"));
            	  RecordVo v = (RecordVo) this.getFormHM().get("lawFileTb");
            	  v.setString("digest", Sql_switcher.readMemo(this.frowset,"digest"));
            	  first_date.setDateString(PubFunc.FormatDate(this.frowset.getDate("issue_date")));

            	  this.getFormHM().put("first_date",first_date);
                  second_date.setDateString(PubFunc.FormatDate(this.frowset.getDate("implement_date")));
                  this.getFormHM().put("second_date",second_date);
                  third_date.setDateString(PubFunc.FormatDate(this.frowset.getDate("valid_date")));
                  this.getFormHM().put("third_date",third_date);
                  
                  vo.setString("ext",this.frowset.getString("ext"));
                  vo.setString("base_id",this.frowset.getString("base_id"));
                  vo.setString("b0110",AdminCode.getCodeName("UN",this.frowset.getString("b0110")==null?"":this.frowset.getString("b0110")));
                  vo.setString("keywords",this.frowset.getString("keywords"));
                  vo.setString("originalext",PubFunc.nullToStr(frowset.getString("originalext")).toLowerCase().trim());
                  this.formHM.put("transfercodeitemid",this.frowset.getString("b0110"));
            }
                           
           if("3".equals(flag))
           {
        	   String content=vo.getString("name")==null?"":vo.getString("name");
        	   vo.setString("name",content);
           }
           this.getFormHM().put("digest",digest);
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("lawFileTb",vo);
        }
    }
}
