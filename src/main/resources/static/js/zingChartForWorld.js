zingChartForWorld = {
    initWorldMap: function () {

        this.getWorldJsonData().then((worldJsonData) => {
        	zingchart.loadModules('maps, maps-world-countries', function(e) {
        	    zingchart.render({
        	        id: 'worldMap',
        	        data: {
        	            shapes: [{
        	                type: 'zingchart.maps',
        	                options: {
        	                	ignore: ['IND'],
        	                    name: 'world.countries', // Note that the below
        	                    // property works with
        	                    // world.countries only.
        	                    // groups: ['NORTHAMERICA'],

        	                    style: {
        	                        items: worldJsonData
        	                    }
        	                }
        	            }],
        	            "legend":{
                            "toggle-action": "none",
                            "vertical-align": "top",
                            "align": "right",
                            "border-width":0,
                            "background-color":"none",
                            "item": {
                              "font-size":8
                            }
                          },
                          "series": [ // for legend items
                              {
                                "legend-item" :{
                                  "text":" >=1000000"
                                },
                                "legend-marker": {
                                  "background-color": "#800026",
                                }
                              },
                              {
                                "legend-item" :{
                                  "text":"500000 - 1000000"
                                },
                                "legend-marker": {
                                  "background-color": "#bd0026",
                                }
                              },
                              {
                                "legend-item" :{
                                  "text":"100000 - 500000"
                                },
                                "legend-marker": {
                                  "background-color": "#e31a1c",
                                }
                              },
                              {
                                "legend-item" :{
                                  "text":"100000 - 80000"
                                },
                                "legend-marker": {
                                  "background-color": "#fc4e2a",
                                }
                              },
                              {
                                "legend-item" :{
                                  "text":"50000 - 80000"
                                },
                                "legend-marker": {
                                  "background-color": "#fd8d3c",
                                }
                              },
                              {
                                "legend-item" :{
                                  "text":"20000 - 50000"
                                },
                                "legend-marker": {
                                  "background-color": "#feb24c",
                                }
                              },
                              {
                                  "legend-item" :{
                                    "text":"10000 - 20000"
                                  },
                                  "legend-marker": {
                                    "background-color": "#fed976",
                                  }
                                },
                                {
                                    "legend-item" :{
                                      "text":"5000 - 10000"
                                    },
                                    "legend-marker": {
                                      "background-color": "#ffeda0",
                                    }
                                  },
                                  {
                                      "legend-item" :{
                                        "text":"1000 - 5000"
                                      },
                                      "legend-marker": {
                                        "background-color": "#ffffcc",
                                      }
                                    },
                                    {
                                        "legend-item" :{
                                          "text":"500 - 1000"
                                        },
                                        "legend-marker": {
                                          "background-color": "#99d8c9",
                                        }
                                      },
                                      {
                                          "legend-item" :{
                                            "text":"<500"
                                          },
                                          "legend-marker": {
                                            "background-color": "#66c2a4",
                                          }
                                        }
                          ]
        	        },
        	        height: 400,
        	        width: '100%'
        	    });
        	});

      
         

        });






    },
//    getWorldChart: function(){
//    	
//    	zingchart.loadModules('maps, maps-world-countries', function(e) {
//    		  zingchart.render({ 
//    			  id: 'worldMap', 
//    			  data: {
//    			    shapes: [
//    			      {
//    			        type: 'zingchart.maps',
//    			        options: {
//    			          name: 'world.countries', // Note that the below
//													// property works with
//													// world.countries only.
//    			          // groups: ['NORTHAMERICA'],
//    			          
//    			          style: {
//    			            items: {
//    			               "NZL": {
//    			            	   "backgroundColor": "#1f4f7b",
//    			            	   "tooltip": {
//    			            		   "html-mode": true,
//    			            		   "text": "<table class='table-sm table-striped table-borderless table-info'><tbody><tr><th>New Zealand</th></tr><tr><th>Total</th><td class='float-right'>2236</th></tr><tr><th>Active</th><td class='float-right'>1409</th></tr><tr><th>Deaths</th><td class='float-right'>11</th></tr><tr><th>Cured</th><td class='float-right'>816</th></tr></tbody></table>"
//    			            	   },
//    			            	   "label": {
//    			            		   "visible": false
//    			            	   }
//    			               }
//    			            }
//    			          }
//    			        }
//    			      }
//    			    ]
//    			  },
//    			  height: 400, 
//    			  width: '100%'
//    		  });
//    		});
//    }
    getWorldJsonData: function (callback) {
        return fetch('/v1/tracker/worldMapData',{
        	method: 'GET',
        	headers:{
        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
        	}
        })
            .then((response) => {
                return response.json().then((data) => {
//                    console.log('worldMapData...',data);
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    }
}