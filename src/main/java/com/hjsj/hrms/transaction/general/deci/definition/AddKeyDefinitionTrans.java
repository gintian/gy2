package com.hjsj.hrms.transaction.general.deci.definition;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class AddKeyDefinitionTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		
		String name = (String)this.getFormHM().get("name");
		String desc = (String)this.getFormHM().get("desc");
		String standartValue = (String)this.getFormHM().get("standartValue");
		String controlValue = (String)this.getFormHM().get("controlValue");
		String fieldName = (String)this.getFormHM().get("fieldName");
		String codeItemValues = (String)this.getFormHM().get("codeItemValues");
		String staticMethod = (String)this.getFormHM().get("staticMethod");
		String box = (String)this.getFormHM().get("box");
		String oneFieldItemValue = (String)this.getFormHM().get("oneFieldItemValue");
		String twoFieldItemValue = (String)this.getFormHM().get("twoFieldItemValue");
		
		System.out.println("*****************************");
		System.out.println("name=" + name);
		System.out.println("desc=" + desc);
		System.out.println("standartValue=" + standartValue);
		System.out.println("controlValue=" + controlValue);
		System.out.println("fieldName=" + fieldName);
		System.out.println("codeItemValues=" + codeItemValues);
		System.out.println("staticMethod=" + staticMethod);
		System.out.println("box=" + box);
		System.out.println("oneFieldItemValue=" + oneFieldItemValue);
		System.out.println("twoFieldItemValue=" + twoFieldItemValue);
		System.out.println("***************************************");
		
		
		
		
		/*// TODO Auto-generated method stub
		RecordVo rv = (RecordVo)this.getFormHM().get("factor");
        if(rv==null)
            return; 
        String fid=(String)this.getFormHM().get("keyid");
        String sel=(String)this.getFormHM().get("sel");
        String codeItemValue=(String)this.getFormHM().get("codeItemValue");
        String seb=(String)this.getFormHM().get("seb");
        String one=(String)this.getFormHM().get("one");
        String two=(String)this.getFormHM().get("two");
        String nam=(String)this.getFormHM().get("nam");
        String dialog=(String)this.getFormHM().get("dialog");
        String obj=(String)this.getFormHM().get("object");
       // String box=(String)this.getFormHM().get("box");
        
        String deri=rv.getString("description");
        
        if(nam==null||nam.equals(""))
        	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.static.definition"),"",""));
        if(deri==null||deri.equals(""))
        	throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("error.definition.unull")));
	     String typ=null;
	     StringBuffer sql=new StringBuffer();
	   //  String sell=DataDictionary.getFieldItem(sel).getItemdesc();
        ContentDAO dao=new ContentDAO(this.getFrameconn());  
        try
        {   
        	if(sel!=null||!sel.equals(""))
    		{
    			sql.append("select codeitemdesc from codeitem where codesetid='");
    			sql.append(dialog);
    			sql.append("' and codeitemid='");
    			sql.append(seb);
    			sql.append("'");
    			
    			this.frowset = dao.search(sql.toString());
    			if(this.frowset.next())
    			  typ=this.frowset.getString("codeitemdesc");
 
    		}

           if(fid==null||fid.equals(""))
		   {
         	    IDGenerator idg=new IDGenerator(2,this.getFrameconn());
         	    String tid=idg.getId("ds_key_factor.factorid");
         	     rv.setString("factorid",tid);
         	     rv.setString("field_name",sel);
         	     rv.setString("codeitem_value",codeItemValue);
         	     rv.setString("typeid",nam);
         	     rv.setString("flag",obj);
         	     rv.setString("gformula",null);
         	     if(box==null||box.equals(""))
         	     {
         	    	rv.setString("formula",one);
         	     }else{
         	         rv.setString("formula",one+"/"+two);
         	     }        	   
	             dao.addValueObject(rv);
		    }else{
			   
			   rv.setString("factorid",fid);
       	       rv.setString("field_name",sel);      	       
       	       rv.setString("codeitem_value",codeItemValue);
       	       rv.setString("typeid",nam);
       	       rv.setString("flag",obj);
       	       rv.setString("gformula",null);
       	      if(box==null||box.equals(""))
    	      {
    	    	  rv.setString("formula",one);
    	      }else{
    	    	  if(one.equals(""))
    	    		  rv.setString("formula",one);
    	    	  else
    	    		  rv.setString("formula",one+"/"+two);
    	      }
       	       
	          dao.updateValueObject(rv);
         }

          }catch(Exception exx)
          {
   	         exx.printStackTrace();
   	         throw GeneralExceptionHandler.Handle(exx);
   	      }
          finally
          {
        	  
        	  this.getFormHM().remove("keyid");
        	  this.getFormHM().put("one","");
        	  this.getFormHM().put("two","");
        	  this.getFormHM().put("seb","");
          }
*/
	}

}
