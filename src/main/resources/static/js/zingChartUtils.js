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
        return fetch('/v1/tracker/stateData?zinkChartData=true')
            .then((response) => {
                return response.json().then((data) => {
                    console.log('data...',data);
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    }
}