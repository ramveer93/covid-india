
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
                }
            });
        });

    },
    getGenericData: function (callback) {
        var url = "/v1/tracker/genericData";
        return fetch(url,
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



