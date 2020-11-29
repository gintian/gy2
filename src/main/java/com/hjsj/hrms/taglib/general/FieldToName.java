package com.hjsj.hrms.taglib.general;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.taglib.CommonData;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
/**
 * <p>Title:FieldToName</p>
 * <p>Description:字典中的指标转换成汉字描述</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 26, 2005:9:45:24 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class FieldToName extends TagSupport {
    private String name;//属性名
    private String scope; //作用域    
    private String fieldname; //值xxx.yyy
    private String fielditem;//指标对象
    /**
     * @return Returns the fielditem.
     */
    public String getFielditem() {
        return fielditem;
    }
    /**
     * @param fielditem The fielditem to set.
     */
    public void setFielditem(String fielditem) {
        this.fielditem = fielditem;
    }
    /**
     * @return Returns the fieldname.
     */
    public String getFieldname() {
        return fieldname;
    }
    /**
     * @param fieldname The fieldname to set.
     */
    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }
    /* 
     * @see javax.servlet.jsp.tagext.Tag#doEndTag()
     */
    public int doEndTag() throws JspException {
        // TODO Auto-generated method stub
        return super.doEndTag();
    }
    /* 
     * @see javax.servlet.jsp.tagext.Tag#doStartTag()
     */
    public int doStartTag() throws JspException {
        CommonData item=null;
        FieldItem fieldvo=null;
        try
        {
          /**
           * 找到对应对象的值
           */
          item=new CommonData();
          Object value=null;
          try
          {
        	  value = TagUtils.getInstance().lookup(pageContext,name,fieldname,scope);
          }
          catch(Exception ee)
          {
        	  value=fieldname;
          }
          //Object value=TagUtility.getClassValue(pageContext,fieldname);
          //System.out.println("value="+(String)value);
          if (value != null)
          {
            fieldvo=DataDictionary.getFieldItem((String)value);
           
            if(fieldvo!=null)
            {
                item.setDataName(fieldvo.getItemid());
                item.setDataValue(fieldvo.getItemdesc());
            }
          }

          pageContext.setAttribute(fielditem, item);
        }
        catch (Exception ee)
        {
          ee.printStackTrace();
        }
        return super.doStartTag();

    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the scope.
     */
    public String getScope() {
        return scope;
    }
    /**
     * @param scope The scope to set.
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

}
