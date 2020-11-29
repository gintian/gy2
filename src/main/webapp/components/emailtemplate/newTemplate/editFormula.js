   //公式修改
	function editF(){
		var selecttext="";
		/**判断是否兼容模式*/
		if(isCompatibleIE()){
			var obj = Ext.getCmp('contentId').getDoc().selection.createRange(); 
			selecttext=obj.text;
		}
		if(!isCompatibleIE()){
			var selectObj = Ext.getCmp('contentId').getDoc().getSelection();
			if(selectObj.rangeCount)
				selecttext=selectObj.getRangeAt(0).toString();
		}
	    if(selecttext.indexOf("#")==-1||selecttext.length<=1||selecttext.indexOf(":")==-1)
	    {
			Ext.MessageBox.alert("提示信息","请选择公式进行修改！");
			return;
	    }
	    if(selecttext.indexOf("##")!=-1||selecttext.indexOf(":")<selecttext.indexOf("#"))
	    {
			Ext.MessageBox.alert("提示信息","请按正确方式选择公式，正确方式为选中 [#+公式序号+冒号+名称+#] 内容！");
			return;
	    }
	    var fieldtitle=selecttext.substring(selecttext.indexOf(":")+1,selecttext.length-1);
	    var templateId = Ext.getCmp('tempalteId').getValue();
	    var fieldId=selecttext.substring(selecttext.indexOf("#")+1,selecttext.indexOf(":"));//需要修改的公式的fieldid
	    if(formula_array[parseInt(fieldId)]==null || formula_array[parseInt(fieldId)].length==0){
		    var map = new HashMap();
			map.put("templateId",templateId);
			map.put("fieldid",fieldId);
			map.put("fieldtitle",trim(fieldtitle));
			Rpc( {
				functionId : 'ZP0000002346',
				success : Global.toEditFormula
			}, map);
		    
	    }else{
	    //未存入数据库的公式可以从formula_array中取值
	    	 addFormat('2',fieldId);
	    	 var arr = formula_array[parseInt(fieldId)];
		     var fieldtitle = arr[0];
		     var dateFormat = arr[5];
		     var nDec = arr[4];
		     var codeSet = arr[6];
		     var fieldLen = arr[3];
		     var fieldType = arr[2];
		     var nFlag = arr[7];
		     var fieldset = arr[8];
		     var fieldContent = arr[1];
		     Ext.getCmp('formulaTitleId').setValue(fieldtitle);
		     Ext.getCmp('formulaId').setValue(fieldContent);
		     if(fieldType=='A'){
		        // Ext.getCmp('rd1').getChecked()[0].inputValue='1';
				 Ext.getCmp('rd1').setValue({rd:['1']});
			     Ext.getCmp('numId').setValue(parseInt(fieldLen));
		     }
		      if(fieldType=='D'){
				 Ext.getCmp('rd1').setValue({rd:['2']});
			     Ext.getCmp('dateId').setValue(dateFormat);
		     }
		      if(fieldType=='N'){
				 Ext.getCmp('rd1').setValue({rd:['3']});
			     Ext.getCmp('integerId').setValue(parseInt(fieldLen));
			     Ext.getCmp('decimalId').setValue(parseInt(nDec));
		     }
	    }
	}
  
  Global.toEditFormula=function(response){
  	 var value = response.responseText;
	 var map = Ext.decode(value);
	 var fieldId = map.fieldid;
  	 addFormat('2',fieldId);
  	 var fieldtitle = map.fieldtitle;
  	 var template_id = map.template_id;
  	 var dateFormat = map.dateFormat;
  	 var nDec = map.nDec;
  	 var codeSet = map.codeSet;
  	 var fieldLen = map.fieldlen;
  	 var fieldType = map.fieldType;
  	 var nFlag = map.nFlag;
  	 var fieldContent = map.fieldContent;
  	 var fieldset = map.fieldset;
     Ext.getCmp('formulaTitleId').setValue(fieldtitle);
     Ext.getCmp('formulaId').setValue(fieldContent);
     if(fieldType=='A'){
      	 Ext.getCmp('rd1').setValue({rd:['1']});
	     Ext.getCmp('numId').setValue(parseInt(fieldLen));
     }
      if(fieldType=='D'){
      	 Ext.getCmp('rd1').setValue({rd:['2']});
	     Ext.getCmp('dateId').setValue(dateFormat);
     }
      if(fieldType=='N'){
      	 Ext.getCmp('rd1').setValue({rd:['3']});
	     Ext.getCmp('integerId').setValue(parseInt(fieldLen));
	     Ext.getCmp('decimalId').setValue(parseInt(nDec));
     }
    
  }
  
  