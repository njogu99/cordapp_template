"use strict";

// Define your client-side logic here.
const app = angular.module('demoAppModule', ['ui.bootstrap']);

app.controller('DemoAppController', function($http, $location) {
    const demoApp = this;

    const apiBaseURL = "/";

    $http.get(apiBaseURL + "me").then((response) => demoApp.thisNode = response.data.me);

    demoApp.getTokens = () => $http.get(apiBaseURL + "token")
        .then((response) => demoApp.tokens = Object.keys(response.data)
            .map((key) => response.data[key])
            .reverse());


    demoApp.getTokens();
});