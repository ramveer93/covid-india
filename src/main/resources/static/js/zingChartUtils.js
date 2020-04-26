zingChartUtils = {
    initIndiaMap: function () {

        this.getStateJsonData().then((stateJsonData) => {
            let chartConfig = {
                shapes: [
                    {
                        type: 'zingchart.maps',
                        options: {
                            // bbox: [67.177, 36.494, 98.403, 6.965], // get bbox from zingchart.maps.getItemInfo('world-countries','ind');
                            // ignore: ['IND'], // ignore India because we are rendering a more specific India map below
                            name: 'ind',
                            // panning: false, // turn of zooming. Doesn't work with bounding box
                            style: {
                                tooltip: {
                                    borderColor: '#000',
                                    borderWidth: '2px',
                                    fontSize: '18px'
                                },
                                controls: {
                                    visible: false // turn of zooming. Doesn't work with bounding box
                                },
                                hoverState: {
                                    alpha: .28
                                }
                            },
                            zooming: true // turn of zooming. Doesn't work with bounding box
                        }
                    },
                    {
                        type: 'zingchart.maps',
                        options: {
                            name: 'ind',
                            panning: false, // turn of zooming. Doesn't work with bounding box
                            zooming: true,
                            scrolling: false,
                            style: {
                                tooltip: {
                                    borderColor: '#000',
                                    borderWidth: '2px',
                                    fontSize: '10px'
                                },
                                borderColor: '#000',
                                borderWidth: '1px',
                                controls: {
                                    visible: false, // turn of zooming. Doesn't work with bounding box

                                },
                                hoverState: {
                                    alpha: .28
                                },
                                items: stateJsonData,
                                label: { // text displaying. Like valueBox
                                    fontSize: '15px',
                                    visible: false
                                }
                            },
                            zooming: true // turn of zooming. Doesn't work with bounding box
                        }
                    }
                ],
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
                          "text":" >=5k"
                        },
                        "legend-marker": {
                          "background-color": "#7a0177",
                        }
                      },
                      {
                        "legend-item" :{
                          "text":"4k - 5k"
                        },
                        "legend-marker": {
                          "background-color": "#ae017e",
                        }
                      },
                      {
                        "legend-item" :{
                          "text":"3k - 4k"
                        },
                        "legend-marker": {
                          "background-color": "#dd3497",
                        }
                      },
                      {
                        "legend-item" :{
                          "text":"2k - 3k"
                        },
                        "legend-marker": {
                          "background-color": "#f768a1",
                        }
                      },
                      {
                        "legend-item" :{
                          "text":"1k - 2k"
                        },
                        "legend-marker": {
                          "background-color": "#fa9fb5",
                        }
                      },
                      {
                        "legend-item" :{
                          "text":"500 - 1k"
                        },
                        "legend-marker": {
                          "background-color": "#fcc5c0",
                        }
                      },
                      {
                          "legend-item" :{
                            "text":"< 500"
                          },
                          "legend-marker": {
                            "background-color": "#fde0dd",
                          }
                        }
                  ]
            }

            zingchart.loadModules('maps,maps-ind');
            zingchart.render({
                id: 'indiaMap',
                data: chartConfig,
                height: '100%',
                width: '100%',
            });

        });






    },
    getStateJsonData: function (callback) {
        return fetch('/v1/tracker/stateData?zinkChartData=true',{
        	method: 'GET',
        	headers:{
        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
        	}
        })
            .then((response) => {
                return response.json().then((data) => {
//                    console.log('data...',data);
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    }
}