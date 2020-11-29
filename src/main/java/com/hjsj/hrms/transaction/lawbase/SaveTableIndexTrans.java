package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 保存文档指标设置
 * @author Administrator
 *
 */
public class SaveTableIndexTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	 try
         {
        String fields=(String)this.getFormHM().get("fields");//可选指标
        String fieldsname = (String)this.getFormHM().get("fieldsname");
        String field=(String)this.getFormHM().get("field");//列表指标
        String fieldname = (String)this.getFormHM().get("fieldname");
        String base_id = (String)this.getFormHM().get("base_id");
        
        String [] fieldsArr = fields.split(",");
        String [] fieldsnameArr = fieldsname.split("、");
        String [] fieldArr = field.split(",");
        String [] fieldnameArr = fieldname.split("、");
        
        String isok = "0";
        
        ArchiveXml xml = new ArchiveXml();
        String item_value = "";
        for (int i = 0; i < fieldsArr.length; i++)
        {
            if (fieldsArr.length == 1 && "".equals(fieldsArr[0]))
            {
                break;
            }
            item_value = item_value + fieldsArr[i] + "`" + fieldsnameArr[i] + ",";
        }
        String listing_value = "";
        for (int i = 0; i < fieldArr.length; i++)
        {
            if (fieldArr.length ==1 && "".equals(fieldArr[0]))
            {
               break; 
            }
            listing_value = listing_value + fieldArr[i] + "`" + fieldnameArr[i] + ",";
        }
        String result = xml.strToXml(item_value, listing_value);
        StringBuffer strsql=new StringBuffer();
        String buf = "";
        if ("".equals(item_value) && "".equals(listing_value))
        {
            buf = null;
        }else {
            XMLOutputter outputter=new XMLOutputter();
            Format format=Format.getPrettyFormat();
            format.setEncoding("UTF-8");
            outputter.setFormat(format);
            buf = result;
        }
        strsql.append("update law_base_struct set field_str = ? where base_id = ?");
        ArrayList one = new ArrayList();
        ArrayList sql = new ArrayList();
        one.add(buf);
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(base_id);
		if(!isNum.matches()&&!"root".equals(base_id)&&!"null".equals(base_id)&&base_id!=null&&base_id.length()>0){
			one.add(PubFunc.decrypt(SafeCode.decode(base_id)));
		}else{
			one.add(base_id);
		}
        sql.add(one);
        ContentDAO dao = new ContentDAO(this.getFrameconn());
       
            dao.batchUpdate(strsql.toString(),sql);
            isok = "1";
            
            this.getFormHM().put("isok", isok);
            this.getFormHM().put("file_index_fields", fields);
            this.getFormHM().put("file_index_value", fieldsname);
            this.getFormHM().put("base_id", base_id);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }  
        
        
    }

}
