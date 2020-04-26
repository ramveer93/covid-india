
genericDataWorld = {
    getData: function () {
        this.getGenericDataForWorld().then((genericData) => {
            $("#worldActiveSpinner").hide();
            $("#worldRecoveredSpinner").hide();
            $("#worldDeathsSpinner").hide();
            
            
            var vm = new Vue({
                el: '#genericData',
                data: genericData
            });
            var testvm = new Vue({
                el: '#navId',
                data: genericData,
                methods: {
                	refreshWorldGenericData: function () {
                        $('#refreshSpinner').css('display', '');
                        fetch("/v1/tracker/refreshCompleteWorld",{
                        	method: 'GET',
                        	headers:{
                        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
                        	}
                        })
                            .then((response) => {
                                return response.json().then((data) => {
//                                    console.log('refresh data..', data);
                                    $("#refreshSpinner").hide();
//                                    console.log('reloading the page');
                                    location.reload(true);
//                                    console.log('reloading done');
                                    return data;
                                }).catch((err) => {
                                    $("#refreshSpinner").hide();
                                    console.log(err);
                                })
                            });
                    }
                }

            });
//            this.getDistrictAndStateData();

        });
    },
    getGenericDataForWorld: function (callback) {
        var url = "/v1/tracker/worldGenericData?countryId=earth";
//        console.log('url for generic data world..', url);
        return fetch(url,{
        	method: 'GET',
        	headers:{
        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
        	}
        })
            .then((response) => {
                return response.json().then((data) => {
//                    console.log('generic data world..', data);
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    },
//    refreshData: function (callback) {
//        var url = "/v1/tracker/refresh";
//        console.log('url to be refresh....', url);
//        return fetch("/v1/tracker/refresh")
//            .then((response) => {
//                return response.json().then((data) => {
//                    console.log('refresh data..', data);
//                    return data;
//                }).catch((err) => {
//                    console.log(err);
//                })
//            });
//    }


}



