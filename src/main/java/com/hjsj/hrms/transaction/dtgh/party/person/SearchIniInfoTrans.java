package com.hjsj.hrms.transaction.dtgh.party.person;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
/**
 * 
 * @author xujian
 *Jan 14, 2010
 */
public class SearchIniInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		String fieldstr="";
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String param = (String)hm.get("param");
		param = param!=null&&param.length()>0?param:"Y";//党务
		String codesetid ="64";
		if("Y".equalsIgnoreCase(param)){
			codesetid = "64";
		}
		if("V".equalsIgnoreCase(param)){//团务
			codesetid = "65";
		}
		if("W".equalsIgnoreCase(param)){//工会
			codesetid = "66";
		}
		DbWizard dbwizard=new DbWizard(this.getFrameconn());	
	    if(!dbwizard.isExistTable(param+"01",false)){
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("dtgh.party."+param),"",""));
	    }
	    this.getFormHM().put("code", codesetid);
	    String sql = "select codesetdesc from codeset where codesetid='"
			+ codesetid + "'";
	    try{
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    this.frowset = dao.search(sql);
		    if (this.frowset.next())
		    	this.getFormHM().put("codesetdesc",
					this.frowset.getString("codesetdesc"));
			fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
		    if(fieldstr!=null&&fieldstr.length()>0)
		    {
			   		
		   }else{
					fieldstr=",b0110,e0122,a0101";
		   }
		    StringBuffer cond=new StringBuffer();
	        cond.append("select pre,dbname from dbname");
	        String userbase="";
	        
	        if(!this.userView.isSuper_admin()) {
	            StringBuffer dbnamePiv = this.userView.getDbpriv();
	            if(dbnamePiv.length() > 0) {
	                cond.append(" where pre in (");
	                String[] dbnames = dbnamePiv.toString().split(",");
	                for(int i = 0; i < dbnames.length; i++){
	                    if(StringUtils.isEmpty(dbnames[i]))
	                        continue;
	                    
	                    if(StringUtils.isEmpty(userbase))
	                        userbase = dbnames[i];
	                    
	                    cond.append("'" + dbnames[i] + "',");
	                }
	                
	                cond.setLength(cond.length() - 1);
	                cond.append(")");
	            }
	        }
	        
	        if(StringUtils.isEmpty(userbase))
	            userbase="usr";
	        
	        cond.append(" order by dbid");
	        this.getFormHM().put("cond", cond.toString());
	        this.getFormHM().put("userbase",userbase);
			this.getFormHM().put("fieldstr", fieldstr);
			this.getFormHM().put("partylike", "1");
			this.getFormHM().put("querylike", "0");
			this.getFormHM().put("query", "");
			this.getFormHM().put("isShowCondition", "none");
			ArrayList fieldList=splitField(fieldstr,1);
			if(fieldList!=null)
			  this.getFormHM().put("browsefields", fieldList);
			else
			  this.getFormHM().put("browsefields", new ArrayList());
		    selectField(param);
		    selectsetup();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	}
    public void selectField(String param)
    {
    	 String constant = "SS_QUERYTEMPLATE";
    	 RecordVo vo= ConstantParamter.getRealConstantVo(constant);
    	 if(vo!=null)
         {
             String strfields=vo.getString("str_value");
             ArrayList fieldlist=splitField(strfields,0);
             this.getFormHM().put("queryfieldlist",fieldlist);            
         }
         else
         {
             this.getFormHM().put("queryfieldlist",new ArrayList());            	
         }
    }
    /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields,int flag)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();
            if("a0101".equalsIgnoreCase(fieldname)&&0==flag)
            	continue;
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item!=null)
            {
            	 FieldItem item_0=(FieldItem)item.clone();             	 
                 list.add(item_0);
            }
           
        }
        return list;
    }
    /**
     * 查询设置参数
     */
    private void selectsetup() throws GeneralException{
    	String belongparty="";
		String belongmember="";
		String belongmeet="";
		String polity = "";
		String party = "";
		String preparty="";
		String important="";
		String active="";
		String application="";
		String member="";
		String person="";
		try{
			ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
			belongparty = xml.getValue("belongparty");
			belongparty = belongparty!=null&&belongparty.length()>0?belongparty:"";
			belongmember = xml.getValue("belongmember");
			belongmember = belongmember!=null&&belongmember.length()>0?belongmember:"";
			belongmeet = xml.getValue("belongmeet");
			belongmeet = belongmeet!=null&&belongmeet.length()>0?belongmeet:"";
			polity = xml.getNodeAttributeValue("/param/polity", "column");
			polity = polity!=null&&polity.length()>0?polity:"";
			if(polity.length()>0){
					party = xml.getNodeAttributeValue("/param/polity/party","value");
					preparty=xml.getNodeAttributeValue("/param/polity/preparty","value");
					important=xml.getNodeAttributeValue("/param/polity/important","value");
					active=xml.getNodeAttributeValue("/param/polity/active","value");
					application=xml.getNodeAttributeValue("/param/polity/application","value");
					member=xml.getNodeAttributeValue("/param/polity/member","value");
					person=xml.getNodeAttributeValue("/param/polity/person","value");
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("belongparty", belongparty);
			this.getFormHM().put("belongmember", belongmember);
			this.getFormHM().put("belongmeet", belongmeet);
			this.getFormHM().put("polity", polity);
			this.getFormHM().put("party", party);
			this.getFormHM().put("preparty", preparty);
			this.getFormHM().put("important", important);
			this.getFormHM().put("active", active);
			this.getFormHM().put("application", application);
			this.getFormHM().put("member", member);
			this.getFormHM().put("person", person);
			
		}
    }
}
