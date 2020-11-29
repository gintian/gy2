package com.hjsj.hrms.transaction.lawbase;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveFileIndexTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
        String fields=(String)this.getFormHM().get("fields");
        fields = ",".equals(fields) ? "" : fields;
        String fieldsname = (String)this.getFormHM().get("fieldsname");
        fieldsname = "、".equals(fieldsname) ? "" : fieldsname;
        
        String field = (String)this.getFormHM().get("s");
        String fieldname = (String)this.getFormHM().get("b");
        fieldname = SafeCode.decode(fieldname);
        String flag = (String)this.getFormHM().get("save_flag");
        String s = "";
        String b = "";
        if ("left".equalsIgnoreCase(flag)) {
        	if (fields != null && !"".equals(fields)) {
        		String[] arr1 =  fields.split(",");
        		String[] arr2 = fieldsname.split("、");
        		String[] arr3 = field.split(",");
        		String[] arr4 = fieldname.split("、");
        		
        		for (int i = 0; i < arr3.length; i++) {
        			if (fields.indexOf(arr3[i]) != -1) {
        				s = s + arr3[i] + ",";
        				String str = this.getFieldName(arr3[i], arr2, arr1);
        				if (str.equals(arr4[i])) {
        					b = b + arr4[i] + "、";
        				}else {//指标重命名
        					for (int j = 0; j < arr1.length; j++) {
        						if (arr1[j].equals(arr3[i])) {
        							b = b + arr2[j] + "、";
        						}
        					}
        				}
        			}
        		}
			}
		}else{
			if (fields != null && !"".equals(fields)) {
        		String[] arr1 =  fields.split(",");
        		String[] arr2 = fieldsname.split("、");
        		String[] arr3 = field.split(",");
        		String[] arr4 = fieldname.split("、");
        		
        		for (int i = 0; i < arr3.length; i++) {
        			
        				s = s + arr3[i] + ",";
        				String str = this.getFieldName(arr3[i], arr2, arr1);
        				if (str.equals(arr4[i])) {
        					b = b + arr4[i] + "、";
        				}else {//指标重命名
        					boolean ischange = false;
        					for (int j = 0; j < arr1.length; j++) {
        						if (arr1[j].equals(arr3[i])) {
        							b = b + arr2[j] + "、";
        							ischange = true;
        						}
        					}
        					if(!ischange)
        						b = b + arr4[i] + "、";
        				}
        			
        		}
			}else{
				s = field;
				b = fieldname;
			}
		}
        this.getFormHM().put("file_index_fields", fields);
        this.getFormHM().put("file_index_value", fieldsname);
        this.getFormHM().put("right_fields", s);
        this.getFormHM().put("right_value", b);

    }

    private String getFieldName(String fieldId,String[] fieldsName,String[] fieldsId){
    	String fieldNameString = "";
    	int i = 0;
    	for (int j = 0; j < fieldsId.length; j++) 
		{
			if (fieldId.equals(fieldsId[j])) 
			{
				i = j;
			}
		}
    	fieldNameString = fieldsName[i];
    	return fieldNameString;
    }
}
