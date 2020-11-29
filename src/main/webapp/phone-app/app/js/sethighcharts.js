var chart;
$(document).ready(function() {
	chart = new Highcharts.Chart({
		chart: {
			renderTo: 'container',
			defaultSeriesType: 'line',
			marginRight: 10,
			marginBottom: 25
		},
		title: {
			text: 'Monthly Average Temperature',
			x: -20 //center
		},
		subtitle: {
			text: 'Source: WorldClimate.com',
			x: -20
		},
		xAxis: {
			categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
				'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
		},
		yAxis: {
			title: {
				text: 'Temperature (°C)'
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
					this.x +': '+ this.y +'°C';
			}
		},
		legend: {
			layout: 'vertical',
			align: 'right',
			verticalAlign: 'top',
			x: -10,
			y: 100,
			borderWidth: 0
		},
		series: [{
			name: 'Tokyo',
			data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
		}, {
			name: 'New York',
			data: [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
		}, {
			name: 'Berlin',
			data: [-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0]
		}, {
			name: 'London',
			data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
		}]
	});
});


var chart;
$(document).ready(function() {

	// define the options
	var options = {

		chart: {
			renderTo: 'container'
		},

		title: {
			text: 'Daily visits at www.highcharts.com'
		},

		subtitle: {
			text: 'Source: Google Analytics'
		},

		xAxis: {
			type: 'datetime',
			tickInterval: 7 * 24 * 3600 * 1000, // one week
			tickWidth: 0,
			gridLineWidth: 1,
			labels: {
				align: 'left',
				x: 3,
				y: -3
			}
		},

		yAxis: [{ // left y axis
			title: {
				text: null
			},
			labels: {
				align: 'left',
				x: 3,
				y: 16,
				formatter: function() {
					return Highcharts.numberFormat(this.value, 0);
				}
			},
			showFirstLabel: false
		}, { // right y axis
			linkedTo: 0,
			gridLineWidth: 0,
			opposite: true,
			title: {
				text: null
			},
			labels: {
				align: 'right',
				x: -3,
				y: 16,
				formatter: function() {
					return Highcharts.numberFormat(this.value, 0);
				}
			},
			showFirstLabel: false
		}],

		legend: {
			align: 'left',
			verticalAlign: 'top',
			y: 20,
			floating: true,
			borderWidth: 0
		},

		tooltip: {
			shared: true,
			crosshairs: true
		},

		plotOptions: {
			series: {
				cursor: 'pointer',
				point: {
					events: {
						click: function() {
							hs.htmlExpand(null, {
								pageOrigin: {
									x: this.pageX,
									y: this.pageY
								},
								headingText: this.series.name,
								maincontentText: Highcharts.dateFormat('%A, %b %e, %Y', this.x) +':<br/> '+
									this.y +' visits',
								width: 200
							});
						}
					}
				},
				marker: {
					lineWidth: 1
				}
			}
		},

		series: [{
			name: 'All visits',
			lineWidth: 4,
			marker: {
				radius: 4
			}
		}, {
			name: 'New visitors'
		}]
	};


	// Load data asynchronously using jQuery. On success, add the data
	// to the options and initiate the chart.
	// This data is obtained by exporting a GA custom report to TSV.
	// http://api.jquery.com/jQuery.get/
	jQuery.get('analytics.tsv', null, function(tsv, state, xhr) {
		var lines = [],
			listen = false,
			date,

			// set up the two data series
			allVisits = [],
			newVisitors = [];

		// inconsistency
		if (typeof tsv !== 'string') {
			tsv = xhr.responseText;
		}

		// split the data return into lines and parse them
		tsv = tsv.split(/\n/g);
		jQuery.each(tsv, function(i, line) {

			// listen for data lines between the Graph and Table headers
			if (tsv[i - 3] == '# Graph') {
				listen = true;
			} else if (line == '' || line.charAt(0) == '#') {
				listen = false;
			}

			// all data lines start with a double quote
			if (listen) {
				line = line.split(/\t/);
				date = Date.parse(line[0] +' UTC');

				allVisits.push([
					date,
					parseInt(line[1].replace(',', ''), 10)
				]);
				newVisitors.push([
					date,
					parseInt(line[2].replace(',', ''), 10)
				]);
			}
		});

		options.series[0].data = allVisits;
		options.series[1].data = newVisitors;

		chart = new Highcharts.Chart(options);
	});
});

