
dataBindingVue = {
    getData: function () {
        this.getGenericData().then((genericData) => {
            $("#totalActive").hide();
// $("#totalCured").hide();
// $("#totalDeaths").hide();
            
            
            var vm = new Vue({
                el: '#genericData',
                data: genericData
            });
            var testvm = new Vue({
                el: '#navId',
                data: genericData,
                methods: {
                    refresh: function () {
                        $('#spinner1').css('display', '');
                        fetch("/v1/tracker/refresh",{
                        	method: 'GET',
                        	headers:{
                        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
                        	}
                        })
                            .then((response) => {
                                return response.json().then((data) => {
         console.log('refresh data..', data);
                                    $("#spinner1").hide();
// console.log('reloading the page');
                                    location.reload(true);
// console.log('reloading done');

                                    return data;
                                }).catch((err) => {
                                    $("#spinner1").hide();
                                    console.log(err);
                                })
                            });

                    }
                }

            });
            this.getDistrictAndStateData();

        });
    },
    getDistrictAndStateData: function () {
        this.getStateDistrictData().then((district) => {

            var vm = new Vue({
                el: '#districtData',
                data: {
                    totalActiveq: 5600,
                    items: district

                    // [
                    // {
                    // "state": "Rajasthan",
                    // "activeCases": 120,
                    // "cured": 100,
                    // "deaths": 10,
                    // "total": 230,
                    // "classx": "Rajasthan",
                    // "district": [
                    // {
                    // "ActiveCases": 10,
                    // "DistrictName": "Bharatpur"
                    // },
                    // {

                    // "ActiveCases": 12,
                    // "DistrictName": "Dholpur"
                    // },
                    // {
                    // "ActiveCases": 40,
                    // "DistrictName": "Sawai Madhopur"

                    // }
                    // ]
                    // },
                    // {
                    // "state": "Karnataka",
                    // "activeCases": 200,
                    // "cured": 300,
                    // "deaths": 40,
                    // "total": 540,
                    // "classx": "Karnataka",
                    // "district": [
                    // {
                    // "DistrictName": "Bangalore",
                    // "ActiveCases": 400
                    // },
                    // {
                    // "DistrictName": "Tumkur",
                    // "ActiveCases": 90
                    // },
                    // {
                    // "DistrictName": "Mysure",
                    // "ActiveCases": 80
                    // },
                    // {
                    // "DistrictName": "Hassan",
                    // "ActiveCases": 80,
                    // }
                    // ]
                    // },
                    // {
                    // "state": "Madhya Pradesh",
                    // "activeCases": 500,
                    // "cured": 100,
                    // "deaths": 90,
                    // "total": 690,
                    // "classx": "MadhyaPradesh",
                    // "district": [
                    // {
                    // "DistrictName": "Vidisha",
                    // "ActiveCases": 40
                    // },
                    // {
                    // "DistrictName": "Bhopal",
                    // "ActiveCases": 150
                    // },
                    // {
                    // "DistrictName": "Indore",
                    // "ActiveCases": 200
                    // },
                    // {
                    // "DistrictName": "Khandawa",
                    // "ActiveCases": 80,
                    // }
                    // ]
                    // },
                    // {
                    // "state": "Uttar Pradesh",
                    // "activeCases": 500,
                    // "cured": 100,
                    // "deaths": 90,
                    // "total": 690,
                    // "classx": "UtterPradesh",
                    // "district": [
                    // {
                    // "DistrictName": "Agra",
                    // "ActiveCases": 40
                    // },
                    // {
                    // "DistrictName": "Lucknow",
                    // "ActiveCases": 150
                    // },
                    // {
                    // "DistrictName": "Azamghar",
                    // "ActiveCases": 200
                    // },
                    // {
                    // "DistrictName": "Barely",
                    // "ActiveCases": 80,
                    // }
                    // ]
                    // },
                    // {
                    // "state": "Uttaranchal",
                    // "activeCases": 500,
                    // "cured": 100,
                    // "deaths": 90,
                    // "total": 690,
                    // "classx": "Uttaranchal",
                    // "district": [
                    // {
                    // "DistrictName": "Agra",
                    // "ActiveCases": 40
                    // },
                    // {
                    // "DistrictName": "Lucknow",
                    // "ActiveCases": 150
                    // },
                    // {
                    // "DistrictName": "Azamghar",
                    // "ActiveCases": 200
                    // },
                    // {
                    // "DistrictName": "Barely",
                    // "ActiveCases": 80,
                    // }
                    // ]
                    // }

                    // ]
                }
            });
        });

    },
    getGenericData: function (callback) {
        var url = "/v1/tracker/genericData";
        return fetch(url,{
        	method: 'GET',
        	headers:{
        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
        	}
        })
            .then((response) => {
                return response.json().then((data) => {
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    },
    getStateDistrictData: function (callback) {
        var url = "/v1/tracker/districtData";
// console.log('url to be getDistrictData....', url);
        return fetch(url,{
        	method: 'GET',
        	headers:{
        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
        	}
        })
            .then((response) => {
                return response.json().then((data) => {
// console.log('generic data..', data);
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    },
    refreshData: function (callback) {
        var url = "/v1/tracker/refresh";
// console.log('url to be refresh....', url);
        return fetch("/v1/tracker/refresh",{
        	method: 'GET',
        	headers:{
        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
        	}
        }
        		)
            .then((response) => {
                return response.json().then((data) => {
// console.log('refresh data..', data);
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    }


}



