/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
/* global angular, option */

(function() {
    'use strict';

    /*
     * Create app and include seperate modules.
     */
    var app = angular.module('insaneVizualisation', ['ui.bootstrap', 'app-directives']);

    /*
     * Create main controller, with default parameters and functions.
     */
    app.controller('PageController', ['$http', '$scope', function($http, $scope) {
        var option = this;
        $scope.option.general = [];
        $scope.option.settings = [];
        $scope.master = {};
        $scope.file = {};
        $scope.outFilePath = "";
        $scope.warningMessages = [];
        
        $scope.oneAtATime = false;
        $scope.isFirstOpen = false;
        $scope.advancedDisabled = true;
        $scope.radio = false;
        $scope.submitDisabled = false;
        $scope.downloadButton = false;

        /*
         * Retrieve the date from our external JSON file containing all settings.
         */
        $http.get('json/options.json').success(function(data) {
            $scope.option.general = data[0];
            $scope.option.settings = data[1];
            
            /*
             * Put all settings in the master array (array containing al values
             * that will be send to the servlet) and copy it's contents to the
             * input array (array containing all user input from site's form).
             */
            angular.forEach($scope.option.settings, function(i){
                angular.forEach(i.inputs, function(j){
                    if (j.default !== null) {
                        $scope.master[j.id] = j.default;
                    }
                });
            });
            $scope.input = angular.copy($scope.master);
        });

        /*
         * update function that copies all user input to the master array, disables
         * the submit button and calls creatView function.
         */
        $scope.update = function(input) {
            $scope.master = angular.copy(input);
            console.log("Update called");
            /*
             * Disable the update button temporarily.
             */
            $scope.submitDisabled = true;
            $scope.option.general.submit = "Initializing ...";
            $scope.createView();
        };

        /*
         * Add another input field for multiselect field like solvents.
         */
        $scope.duplicateSelectTag = function(input, value, emptyOption) {
            //$scope.default = [[null, null, "1:1"], ["", "", "", "1:1"]];
            input[value].push(angular.copy(emptyOption));
	};

        /*
         * Remove the corresponding input field created with duplicateSelectTag
         * function.
         */
        $scope.removeSelectTag = function(input, value, index) {
            input[value].splice(index, 1);
        };

        /*
         * createView function, called by update function. This function sends
         * master array with protein upload file to servlet.
         */
        $scope.createView = function() {
            $http({
                method: 'POST',
                url: 'InsaneModelServlet',
                headers: {'Content-Type': undefined},
                /*
                 * This method will change how the data is sent to the server.
                 * The model date gets encapsulate in 'FormData'.
                 */
                transformRequest: function (data) {
                    /*
                     * Fix voor 'not well-formed' error in firefox?
                     */
                    $.ajaxSetup({ beforeSend: function(xhr) {
                        if (xhr.overrideMimeType) {
                            xhr.overrideMimeType("application/json");
                        }
                    }});

                    var formData = new FormData();
                    /*
                     * Append the string version of the JSON master array
                     */
                    formData.append("master", JSON.stringify(data.master));
                    
                    /*
                     * Add boolean value whether there was a file given.
                     * This is very hard to check inside the servlet.
                     */
                    formData.append("wasFileGiven", data.proteinFile !== undefined);

                    /*
                     * Add the assigned protein file
                     */
                    formData.append("file", data.proteinFile);

                    return formData;
                },
                /*
                 * Create an object that contains the model and files which will
                 * be transformed in the above transformRequest method.
                 */
                data: { master: $scope.master, proteinFile: $scope.file.parameter_f }
            }).
            success(function (data, status, headers, config) {
                /*
                 * Succesfully obtained date from servlet, load into Jmol script.
                 */
                console.log("Successfully obtained data from server.");
                if (data.display){
                    jmol_applet_insane._loadFile(data.outfile);
                    Jmol.script(jmol_applet_insane, 'moveto 0.0 bottom');
                } else {
                    jmol_applet_insane._loadFile("no_output_available");
                }
                

                if (data.download){
                    $scope.downloadButton = true;
                    $scope.outFilePath = data.outfile;
                } else{
                    $scope.downloadButton = false;
                }
                
                $scope.warningMessages = JSON.parse(data.errorMessages);
            }).error(function (data, status, headers, config) {
                /*
                 * Something went wrong, no data.
                 */
                console.log("Failed to obtain data from server");
                $scope.warningMessages = ["Failed to obtain data from the server, please try again."];
            }).finally(function (data, status, headers, config) {
                /*
                 * Always enable the submit button. Users are now able to submit
                 * new request.
                 */
                $scope.submitDisabled = false;
                $scope.option.general.submit = "Update View";
            });
        };
    }]);
})();
