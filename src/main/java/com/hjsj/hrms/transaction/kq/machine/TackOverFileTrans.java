package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 接收规则文件
 * <p>Title:TackOverFileTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 5, 2007 9:35:09 AM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class TackOverFileTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
	    KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, "", this.getFrameconn()); 
        String cardno_field=kq_paramter.getCardno();
        
        if(cardno_field==null || cardno_field.length()<=0)
        {
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.card.nocreate.card_no"),"",""));
        }   
        
		String file_num=(String)this.getFormHM().get("file_num");		
		FormFile file = (FormFile) this.getFormHM().get("file");
		
		KqCardData kqCardData = new KqCardData(this.userView,this.getFrameconn());
		HashMap hashM = kqCardData.getFile_Rule(file_num);
		if(hashM!=null)
		{
			InputStream is = null;	
			BufferedReader br=null;
			ArrayList filelist =new ArrayList();
			String machine_no="";
			String line = "";
			try {
				is = file.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
				line = br.readLine(); 
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
			
			String status=(String)hashM.get("status");
			int i=0;
			while(line != null) 
			{ 
				try
				{
					line=line.trim();
					if(i==0)
				    {
					   if(status!=null&& "1".equals(status))
					   {
						   machine_no=line;
					   }else
					   {
						   if(line.length()<=0)
							{
								line = br.readLine(); 
								continue;
							}
						    
						    if(!kqCardData.CheckCardData(line, hashM))
							{
								line = br.readLine(); 
	                            continue;
							}
						    
						    filelist.add(kqCardData.getFileValue(line,hashM,machine_no));   
					   }
				   }else
				   {
					   if(line.length()<=0)
						{
							line = br.readLine(); 
							continue;
						}
					   
					    if(!kqCardData.CheckCardData(line, hashM))
						{
							line = br.readLine(); 
	                        continue;
						}
					    
					    filelist.add(kqCardData.getFileValue(line,hashM,machine_no));    
				   }
				   i++;
				   line = br.readLine(); 				  
				}catch(Exception e)
				{
					i++;
					try {
						line = br.readLine();
					} catch (IOException e1) {
						e1.printStackTrace();
					} 				  
				}
			} 
			
			try
			{
				if(br!=null)
					br.close();
				
				if(is!=null)
					is.close();
				
			}catch(Exception e)
			{
				e.printStackTrace();
			}
				
			StringBuffer notInPrivCards = new StringBuffer();
			int notInPrivCount = 0;
			int allCount = filelist.size();
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(),this.userView);
			ArrayList  dblist = kqUtilsClass.getKqPreList();	
				
			for (i = filelist.size() - 1; i >= 0; i--)
			{	 
			    boolean inPriv = false;
			    String cardno = (String)((ArrayList)filelist.get(i)).get(3);
				for (int k = 0; k < dblist.size(); k++)
				{
					inPriv = CardInPriv(dao, dblist.get(k).toString(), cardno, cardno_field);
					if (inPriv)
						break;
				}
				
				if(!inPriv){
				    if (!notInPrivCards.toString().contains(cardno))
				        notInPrivCards.append(cardno + ",");
				    
				    notInPrivCount++;
				    filelist.remove(i);
				}
			}
				
		    kqCardData.insert_kq_originality_data(filelist,dblist,cardno_field);
		    
		    String msg = "导入完成！<br><br>共导入" + (allCount-notInPrivCount) + "条记录！";
		    if (notInPrivCards.length() > 0)
		        msg = msg + "<br>另有" + notInPrivCount + "条记录由于卡号不正确或无该人员权限没有被导入！";
		        throw new GeneralException(msg) ;
		}
	}	
	
	/**
	 * 判断权限范围内有无某卡号持卡人
	 * @Title: getEmpA01   
	 * @Description:    
	 * @param dao
	 * @param nbase
	 * @param cardno
	 * @param cardnoField
	 * @return
	 */
    private boolean CardInPriv(ContentDAO dao, String nbase, String cardno, String cardnoField) {
        boolean inPriv = false;
        
        RowSet rs = null;
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("select 1 from " + nbase + "A01");
            sb.append(" where " + cardnoField + "= '" + cardno + "'");
            if (!this.userView.isSuper_admin()) {
                // 要控制人员范围
                String whereIn = RegisterInitInfoData.getWhereINSql(userView, nbase);
                sb.append(" and a0100 in (");
                sb.append(" select a0100 " + whereIn + ")");
            }

            rs = dao.search(sb.toString());
            inPriv = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return inPriv;
    }
}
