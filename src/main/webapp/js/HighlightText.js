function highlightstring(text,key)
{
  var html=text.nodeValue;
  var ss = html.split(key);
  key = key.fontcolor("red");
  html="";
  for(var j=0;j<ss.length;j++)
  {
     if(j>0)
       html+= key;
     html+=ss[j];
  }
  var node = document.createElement("font");
  html = html.replace(/\r/g, "</br>");
  node.innerHTML = html;
  return node;
}

function highlighttext(text,keys,index)
{
  var node = highlightstring(text,keys[index]);
  for(var i=index+1;i<keys.length;i++)
  {
     highlightobject(node,keys,index+1);
  }
  return node;
}

function highlightobject(object,keys,index)
{
  var nodes = object.childNodes;
  for(var i=0;i<nodes.length;i++)
  {
    if(nodes[i].nodeType==3)
    {
       var mynode = highlighttext(nodes[i],keys,index);
       object.replaceChild(mynode,nodes[i]);
    }
    else
    {
       highlightobject(nodes[i],keys,index);
    }
  }
}

function highlightdocument(key)
{
  var s = location.href;
  var index = s.indexOf("?");
  if(index>=0)
  {
     s = s.substr(index+1);
     index = s.indexOf(key + "=");
     if(index>=0)
     {
       s = s.substr(key.length + 1);
       index = s.indexOf("&");
       if(index>=0)
         s = s.substr(0,index);
       keys = s.split("::");
       var aaa=clearBlank(keys);
       if(aaa.length>0) {
         highlightobject(document.body,aaa,0);
       }
     }
  }
}
function clearBlank(arr)
{
  var j=0;
  var toArr=new Array();
  if(arr!=null)
  {
          for(var i=0;i<arr.length;i++)
          {
                  if(arr[i]!=null && arr[i]!="")
                  {
                          toArr[j]=arr[i];
                          j++;
                  }
          }
  }
  return toArr;
}
    function wordHighlight(wordUrl,searchText) {
		var word; 
		word = new ActiveXObject("Word.Application");
		var range = word.Range;
		word.Visible = true;
		var path = wordUrl;
		word.Documents.Open(path);
		/*for (var i = 0; i < searchText.length; i ++) {
	     	
			do{
				word.Selection.Find.ClearFormatting();
				select_start = word.Selection.Range.Start;
				select_end = word.Selection.Range.End;
				word.Selection.Find.Text = searchText[i];
				word.Selection.Find.Execute();
				word.Selection.Font.Color = 1000;
				}
			while(select_start!=word.Selection.Range.Start && select_end != word.Selection.Range.End)
	    }*/
        window.close();
    }
    function excelHighlight(wordUrl,searchText) {
		AppExcel = new ActiveXObject("Excel.Application");
		AppExcel.Visible = true;
		AppExcel.WorkBooks.Open(wordUrl);
		for (var i = 0; i < searchText.length; i ++) {
    		AppExcel.Cells.Find(searchText,AppExcel.ActiveCell).Activate;
	    	AppExcel.Selection.Font.ColorIndex = 3;
		    select_start = AppExcel.Selection.Row;
	     	select_end = AppExcel.Selection.Column;
		    do{
		         AppExcel.Cells.FindNext(AppExcel.ActiveCell).Activate;
			     AppExcel.Selection.Font.ColorIndex = 3;    
			} while(select_start!=AppExcel.Selection.Row || select_end != AppExcel.Selection.Column)
	    }
	    window.close();
    }