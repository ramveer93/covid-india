newsFeed = {
    getNewsData: function () {
        this.getNewsFeedData().then((newsFeedData) => {
             var totalArticles = Object.keys(newsFeedData.articles).length;
            // // console.log('------------------hhhher--totalArticles-----', totalArticles);
            // // if (totalArticles >= 10) {
            // //     console.log('there are more then 10 articles so resizing the news feed for performance to 10 articles only');
            // //     newsFeedData.articles.splice(10, totalArticles - 10);
            // // }
            // // console.log('------------------after--totalArticles-----', newsFeedData);
            // // var totalResizedArticles = Object.keys(newsFeedData.articles).length;
            // for (var i = 0; i < totalArticles; i++) {
            //     if (i == 0) {
            //         newsFeedData.articles[i].active = 'carousel-item active';
            //     } else {
            //         newsFeedData.articles[i].active = 'carousel-item';
            //     }
            // }
//            console.log('------------------after--kkkklklkll-----', JSON.stringify(newsFeedData));
            var vm = new Vue({
                el: '#newsFeed',
                data: newsFeedData
            });
        });


    },
    getNewsFeedData: function (callback) {
        var query = "India-corona";
        var endDate = new Date().toISOString().slice(0, 10);
        var currentDate = new Date();
        currentDate.setDate(currentDate.getDate() - 1);
        var startDate = currentDate.getFullYear() + '-' + (currentDate.getMonth() + 1) + '-' + currentDate.getDate();
         var url = "/v1/tracker/news?q=" + query + "&from=" + startDate + "&to=" + endDate;
        // var url = "http://newsapi.org/v2/everything?q=India-corona&from=2020-4-11&to=2020-04-12&sortBy=popularity&apiKey=8e9e8a6144b04f17a6b935c8fa00789f"
//        console.log('url to be hit....', url);
        return fetch(url,{
        	method: 'GET',
        	headers:{
        		'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW12ZWVyQWRtaW45MzIiLCJleHAiOjE2NTkyOTg1ODEsImlhdCI6MTU4NzI5ODU4MX0.N75CGJchg4J3uR6k9Y2JnjALlScK4mp3TOOPcTXBG8g'
        	}
        })
            .then((response) => {
                return response.json().then((data) => {
//                    console.log('news feed data.kjkjjkkj..', data);
                    return data;
                }).catch((err) => {
                    console.log(err);
                })
            });
    }


}



