chartJsUtilsForWorld = {
  getRandomColor: function () {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++) {
      color += letters[Math.floor(Math.random() * 16)];
    }
//    console.log('random color is .....' + color);
    return color;
  },

  initChartsPages: function (country,flag) {
//   console.log('-----------country-----',country);
    this.getChartData(country,flag).then((chartData) => {
    	$("#dailyTrendSpinner").hide();
    	$('#worldTrendSpinnerDropDown').hide();
//    	$('#worldTopTenCountriesActiveSpinner').hide();
//    	$('#worldTopTenCountriesCuredSpinner').hide();
//    	$('#worldTopTenCountriesDeathsSpinner').hide();
    	
//    console.log('chart data........',chartData);
      chartColor = "#FFFFFF";

      var dailyTrendChart = document.getElementById("dailyTrendChart");
      var chartOptions = {
        legend: {
          display: true,
          position: 'top'
        }
      };

      var lineChart = new Chart(dailyTrendChart, {
        type: 'line',
        hover: false,
        data: chartData.dailyTrend,
        options: chartOptions
      });
      var barChartOptions = {
        scales: {
          xAxes: [{
            gridLines: {
              offsetGridLines: true
            }
          }]
        }
      };
      var barData = {
        labels: ["Rajasthan", "UP", "MAdhyapradesh", "Karnataka", "Sikkim", "Kerla", "Mahrastra", "Delhi", "Jammu & Kashmir", "Manipur"],
        datasets: [{
          label: 'Active Cases',
          backgroundColor: ["#3498DB", "#ABEBC6", "orange", "#F8C471", "#BB8FCE", "#73C6B6", "#80b6f4", "#f49080", "#fad874", "#94d973"],
          data: [10, 20, 30, 40, 50, 60, 70, 89, 120, 123]
        }]
      };
      ctx = document.getElementById("topTenCountriesActiveCases").getContext("2d");
      myBarChart = new Chart(ctx, {
        type: 'bar',
        data: chartData.topTenCountryBar.Active,
        options: barChartOptions
      });

      ctx = document.getElementById("topTenCountriesCasesInCured").getContext("2d");
      myBarChart = new Chart(ctx, {
        type: 'bar',
        data: chartData.topTenCountryBar.Cured,
        options: barChartOptions
      });

      ctx = document.getElementById("topTenCountriesCasesInDeath").getContext("2d");
      myBarChart = new Chart(ctx, {
        type: 'bar',
        data: chartData.topTenCountryBar.Deaths,
        options: barChartOptions
      });

//      ctx = document.getElementById("topTenDistrictsActiveCases").getContext("2d");
//      myBarChart = new Chart(ctx, {
//        type: 'bar',
//        data: chartData.topTenDistrictBar.Active,
//        options: barChartOptions
//      });
    });





  },

  initGoogleMaps: function () {
    var myLatlng = new google.maps.LatLng(40.748817, -73.985428);
    var mapOptions = {
      zoom: 13,
      center: myLatlng,
      scrollwheel: false, //we disable de scroll over the map, it is a really annoing when you scroll through page
      styles: [{
        "featureType": "water",
        "stylers": [{
          "saturation": 43
        }, {
          "lightness": -11
        }, {
          "hue": "#0088ff"
        }]
      }, {
        "featureType": "road",
        "elementType": "geometry.fill",
        "stylers": [{
          "hue": "#ff0000"
        }, {
          "saturation": -100
        }, {
          "lightness": 99
        }]
      }, {
        "featureType": "road",
        "elementType": "geometry.stroke",
        "stylers": [{
          "color": "#808080"
        }, {
          "lightness": 54
        }]
      }, {
        "featureType": "landscape.man_made",
        "elementType": "geometry.fill",
        "stylers": [{
          "color": "#ece2d9"
        }]
      }, {
        "featureType": "poi.park",
        "elementType": "geometry.fill",
        "stylers": [{
          "color": "#ccdca1"
        }]
      }, {
        "featureType": "road",
        "elementType": "labels.text.fill",
        "stylers": [{
          "color": "#767676"
        }]
      }, {
        "featureType": "road",
        "elementType": "labels.text.stroke",
        "stylers": [{
          "color": "#ffffff"
        }]
      }, {
        "featureType": "poi",
        "stylers": [{
          "visibility": "off"
        }]
      }, {
        "featureType": "landscape.natural",
        "elementType": "geometry.fill",
        "stylers": [{
          "visibility": "on"
        }, {
          "color": "#b8cb93"
        }]
      }, {
        "featureType": "poi.park",
        "stylers": [{
          "visibility": "on"
        }]
      }, {
        "featureType": "poi.sports_complex",
        "stylers": [{
          "visibility": "on"
        }]
      }, {
        "featureType": "poi.medical",
        "stylers": [{
          "visibility": "on"
        }]
      }, {
        "featureType": "poi.business",
        "stylers": [{
          "visibility": "simplified"
        }]
      }]

    }
    var map = new google.maps.Map(document.getElementById("map"), mapOptions);

    var marker = new google.maps.Marker({
      position: myLatlng,
      title: "Hello World!"
    });

    // To add the marker to the map, call setMap();
    marker.setMap(map);
  },

  showNotification: function (from, align) {
    color = 'primary';

    $.notify({
      icon: "nc-icon nc-bell-55",
      message: "Welcome to <b>Paper Dashboard</b> - a beautiful bootstrap dashboard for every web developer."

    }, {
      type: color,
      timer: 8000,
      placement: {
        from: from,
        align: align
      }
    });
  },
  barDataForTopTenStatesActiveCases: function () {
    var barData = {
      labels: ["Rajasthan", "UP", "MAdhyapradesh", "Karnataka", "Sikkim", "Kerla", "Mahrastra", "Delhi", "Jammu & Kashmir", "Manipur"],
      datasets: [{
        label: 'Active Cases',
        backgroundColor: ["#3498DB", "#ABEBC6", "orange", "#F8C471", "#BB8FCE", "#73C6B6", "#80b6f4", "#f49080", "#fad874", "#94d973"],
        data: [10, 20, 30, 40, 50, 60, 70, 89, 120, 123]
      }]
    };
    return barData;
  },
  barDataForTopTenStatesWithMaxDeaths: function () {
    var barData = {
      labels: ["Rajasthan", "UP", "MAdhyapradesh", "Karnataka", "Sikkim", "Kerla", "Mahrastra", "Delhi", "Jammu & Kashmir", "Manipur"],
      datasets: [{
        label: 'Deaths',
        backgroundColor: ["#3498DB", "#ABEBC6", "orange", "#F8C471", "#BB8FCE", "#73C6B6", "#80b6f4", "#f49080", "#fad874", "#94d973"],
        data: [100, 120, 300, 640, 750, 60, 70, 89, 120, 123]
      }]
    };
    return barData;
  },
  barDataForTopTenStatesWithMaxCured: function () {
    var barData = {
      labels: ["Rajasthan", "UP", "MAdhyapradesh", "Karnataka", "Sikkim", "Kerla", "Mahrastra", "Delhi", "Jammu & Kashmir", "Manipur"],
      datasets: [{
        label: 'Cured',
        backgroundColor: ["#3498DB", "#ABEBC6", "orange", "#F8C471", "#BB8FCE", "#73C6B6", "#80b6f4", "#f49080", "#fad874", "#94d973"],
        data: [10, 200, 30, 940, 250, 560, 170, 89, 120, 123]
      }]
    };
    return barData;
  },
  getDailyTrendForDeaths: function () {
    var deathDataForDailyTrend = {
      data: [0, 5, 12, 24, 30, 35, 39, 40, 45, 49, 50, 200],
      fill: false,
      label: "Deaths",
      borderColor: '#ff4444',
      backgroundColor: '#ff4444',
      pointBorderColor: '#ff4444',
      pointRadius: 4,
      pointHoverRadius: 4,
      pointBorderWidth: 8
    };
    return deathDataForDailyTrend;
  },
  getDailyTrendForActiveCases: function () {
    var activeCases = {
      data: [0, 5, 10, 12, 20, 27, 30, 34, 42, 45, 55, 63],
      fill: false,
      label: "Active Cases",
      borderColor: '#4B515D',
      backgroundColor: '#4B515D',
      pointBorderColor: '#4B515D',
      pointRadius: 4,
      pointHoverRadius: 4,
      pointBorderWidth: 8
    };
    return activeCases;
  },
  getDailyTrendForCured: function () {
    var curedData = {
      data: [0, 19, 15, 20, 30, 40, 40, 50, 25, 30, 50, 70],
      fill: false,
      label: "Cured",
      borderColor: '#00C851',
      backgroundColor: '#00C851',
      pointBorderColor: '#00C851',
      pointRadius: 4,
      pointHoverRadius: 4,
      pointBorderWidth: 8,
    };
    return curedData;
  },
  getDailyTrendData: function () {
    var labelsData = ["23 April 2020", "02 April 2020", "03 April 2020", "04 April 2020", "05 April 2020", "06 April 2020", "07 April 2020", "08 April 2020"
      , "09 April 2020", "10 April 2020", "11 April 2020", "12 April 2020"];
    var speedData = {
      labels: labelsData,
      datasets: [this.getDailyTrendForDeaths(), this.getDailyTrendForActiveCases(), this.getDailyTrendForCured()]
    };
    return speedData;
  },
  barDataForTopTenDistricts: function () {
    var barData = {
      labels: ["Bharatpur", "Bangalore", "Agra", "Bhopal", "South Delhi", "Mumbai", "Ernakulam", "Jaipur", "Jammu", "24 pargama"],
      datasets: [{
        label: 'Active cases',
        backgroundColor: ["#3498DB", "#ABEBC6", "orange", "#F8C471", "#BB8FCE", "#73C6B6", "#80b6f4", "#f49080", "#fad874", "#94d973"],
        data: [100, 120, 300, 640, 750, 60, 70, 89, 120, 123]
      }]
    };
    return barData;
  },

  getChartData: function (country,flag, callback) {
	var url = "/v1/tracker/lineAndBarDataForCountry?countryId="+country+"&dropDownValue="+flag;;
//    console.log('lineAndBarDataForCountry..', url);
    return fetch(url,{
    	method: 'GET',
    	headers:{
    		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
    	}
    })
      .then((response) => {
        return response.json().then((data) => {
//          console.log('news feed data...', data);
          return data;
        }).catch((err) => {
          console.log(err);
        })
      });
  }
};