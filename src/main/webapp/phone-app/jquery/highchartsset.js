// Set the theme
Highcharts.setOptions({
colors: ["#7798BF", "#FFCC00", "#55BF3B", "#CC3300", "#aaeeee", "#ff0066", "#eeaaee", 
		"#66443B", "#DF5353", "#FF0000", "#99CCFF"],
	chart: {
		backgroundColor: null,
		borderColor: '#353028',
		borderWidth: 1,
		borderRadius: 15,
		plotBackgroundColor: null,
		plotShadow: false,
		plotBorderWidth: 0,
		margin: [30, 7, 30, 30],
		style: {
			zIndex: 1
		}
	},
	title: {
		style: { 
			color: '#FFF',
			font: '14px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif',
			textShadow: 'none'
		}
	},
	subtitle: {
		style: { 
			color: '#DDD',
			font: '12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif',
			textShadow: 'none'
		}
	},
	xAxis: {
		gridLineWidth: 0,
		lineColor: '#999',
		tickColor: '#999',
		labels: {
			style: {
				color: '#999',
				fontWeight: 'bold'
			}
		},
		title: {
			style: {
				color: '#AAA',
				font: 'bold 12px Lucida Grande, Lucida Sans Unicode, Verdana, Arial, Helvetica, sans-serif'
			}				
		}
	},
	yAxis: {
		alternateGridColor: null,
		minorTickInterval: null,		
		lineWidth: 0,
		tickWidth: 0,
		labels: {
			style: {
				color: '#999',
				fontWeight: 'bold'
			}
		},
		title: {
			text: null			
		}
	},
	legend: {
		//layout: 'vertical',
		style: {
			left: '40px',
			bottom: 'auto',
			top: '10px'
		},
		borderWidth: 1,
		backgroundColor: '#625E56',
		shadow: false,
		symbolWidth: 16,
		itemStyle: {
			color: '#CCC'
		},
		itemHoverStyle: {
			color: '#FFF'
		},
		itemHiddenStyle: {
			color: '#333'
		}
	},
	credits: {
		enabled: false
	},
	labels: {
		style: {
			color: '#CCC'
		}
	},
	tooltip: {
		backgroundColor: {
			linearGradient: [0, 0, 0, 50],
			stops: [
				[0, 'rgba(66, 60, 50, .9)'],
				[1, 'rgba(29, 27, 21, .8)']
			]
		},
		borderWidth: 0,
		style: {
			color: '#FFF'
		}
	},
	
	
	plotOptions: {
		line: {
			dataLabels: {
				color: '#CCC'
			},
			marker: {
				lineColor: '#333'
			}
		},
		spline: {
			marker: {
				lineColor: '#333'
			}
		},
		scatter: {
			marker: {
				lineColor: '#333'
			}
		}
	},
	
	toolbar: {
		itemStyle: {
			color: '#CCC'
		}
	}
});
function getColumnChart(arrayList,titleStr)
{
   var options = {
		chart: {
		   renderTo: 'container',
		   defaultSeriesType: 'column'
	    },
	    title: {
		   text: titleStr
	    },
	    xAxis: {
		},	   
		yAxis: {
			title: {
				text: ''
			},
			plotLines: [{
				value: 0,
				width: 1,
				color: '#808080'
			}]
		},
	    tooltip: {
		   formatter: function() {
			  return '<b>'+ this.series.name +'</b><br/>'+
			  	this.x  +': '+ this.y;
		   }
	    },
	    legend: {
	       enabled:false
	    }
	    	    
	};
	options.xAxis.categories = [];		
	for(var i=0;i<arrayList.length;i++)
	{
	   var object =arrayList[i];
	   var name=object.legend;
	   options.xAxis.categories.push(name);
	}
	options.series = [];
	options.series[0] = { 
		   name: titleStr,
		   data: []	   
	};
	for(var i=0;i<arrayList.length;i++)
	{
	    var object =arrayList[i];
	    var value=object.count;
	    options.series[0].data.push(parseFloat(value));
	}
	return options;
}
function getPieChart(arrayList,titleStr)
{
  var  options = {
        chart: {
		   renderTo: 'container',
		   plotBackgroundColor: null,
		   plotBorderWidth: null,
		   plotShadow: false
	    },
	   title: {
		text: titleStr
	   },
	   tooltip: {
		  //enabled: false,
		   formatter: function() {
			  return '<b>'+ this.point.name +'</b>:<br/>'+ 
				this.y +'<br/>'+//fruit items
				'('+ Highcharts.numberFormat(this.percentage, 1) +'%)';
	      }
	   },
	   legend: {
		enabled: false
	   },
	   plotOptions: {
			pie: {
				allowPointSelect: true,
				cursor: 'pointer',
			    dataLabels: {
				enabled: false
			},
			showInLegend: true
		  }
	   },
	   series: [{
		  type: 'pie',
		  name: titleStr,
		  allowPointSelect: true,
		  cursor: 'pointer',
		  center: ['50%', '48%'],
		  size: '50%',		  
		  showInLegend: false,
		  dataLabels: {
			  enabled: true,			  
			  formatter: function() {
				return this.point.name +':<br/>'+ this.y;
			  },
			  y: -2,
			  color: '#333',
			  style: {
				font: '12px Trebuchet MS, Verdana,sans-serif,bold'
			  }
		  }
	    }]	    
    };
    var da=new Array();
    for(var i=0;i<arrayList.length;i++)
	{
	    var object =arrayList[i];
	    var value=object.count;
	    var n=object.legend;
	    var vo=new Object();
	    vo.name=n;
	    vo.y=parseFloat(value);	    
	    da.push(vo);   
	} 
	options.series[0].data=da;
    return options;
}
function getLineChart(arrayList,titleStr)
{
    var options = {
         chart: {
		    renderTo: 'container',
			defaultSeriesType: 'line',						
			marginBottom: 25
		},
		title: {
		   text: titleStr
	    },
		xAxis: {
		},			
		yAxis: {
			title: {
		   		text: ''
	    	},
			plotLines: [{
				value: 0,
				width: 1,
				color: '#808080'
			}]
		},	
		tooltip: {
			formatter: function() {
				   return '<b>'+ this.series.name +'</b><br/>'+
				   this.x +': '+ this.y +'';
			}
		},
	    legend: {
			layout: 'vertical',
			align: 'right',
			verticalAlign: 'top',
			x: -10,
			y: 100,
			borderWidth: 0,
			enabled:false
		}
    };
    options.xAxis.categories = [];		
	for(var i=0;i<arrayList.length;i++)
	{
	   var object =arrayList[i];
	   var name=object.legend;
	   options.xAxis.categories.push(name);
	}
	options.series = [];
	options.series[0] = { 
		   name: titleStr,
		   data: []	   
	};
	for(var i=0;i<arrayList.length;i++)
	{
	    var object =arrayList[i];
	    var value=object.count;
	    options.series[0].data.push(parseFloat(value));
	}
    return options;
}