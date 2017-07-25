jQuery(document).ready(function () {

    var myMap;

    //The infocards.
    function populateInfocards() {
        $.ajax({
            type: 'post',
            url: '/cocotemp/dashboard/data.json',
            success: function (data) {
                $('#site-count').text(data.siteCount);
                $('#record-count').text(data.recordCount);
                $('#upload-count').text(data.uploadCount);
                $('#device-count').text(data.deviceCount)
            },
            error: function (results) {

            }
        });
    }

    function populateMap() {
        myMap = L.map('map').setView([51.505, -0.09], 13);
        L.tileLayer('https://api.mapbox.com/styles/v1/cjsumner/ciu0aibyr002p2iqd51spbo9p/tiles/256/{z}/{x}/{y}?access_token=pk.eyJ1IjoiY2pzdW1uZXIiLCJhIjoiY2lmeDhkMDB3M3NpcHUxbTBlZnoycXdyYyJ9.NKtr-pvthf3saPDsRDGTmw', {
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="http://mapbox.com">Mapbox</a>',
            maxZoom: 18,
            id: 'your.mapbox.project.id',
            accessToken: 'your.mapbox.public.access.token'
        }).addTo(myMap);
    }

    function populateSites() {
        var siteMarkers = [];

        $.ajax({
            type: 'post',
            url: '/cocotemp/sites.json',
            success: function (data) {
                if (data.length == 0) {
                    return;
                }

                for (var i = 0; i < data.length; i++) {
                    //Add the station locations to the map.
                    var myMarker = L.marker([data[i].siteLatitude, data[i].siteLongitude]).addTo(myMap);
                    myMarker.bindPopup("<p>" + data[i].siteName + "</p>");
                    siteMarkers.push(myMarker);
                }

                //Fit to show all markers on the map.
                var myGroup = new L.featureGroup(siteMarkers);
                myMap.fitBounds(myGroup.getBounds())
            },
            error: function (results) {

            }
        });
    }

    _.defer(populateMap);
    _.defer(populateInfocards);
    _.defer(populateSites);
});