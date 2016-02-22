/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
/* global angular, option */

(function() {
    'use strict';
    
    /*
     * Create the module that can be imported in the main app.
     * Set bottom of page reached to false.
     */
    var app = angular.module('app-directives', []);
    var bottomReached = false;

    /*
     * fileUpload directive which adds the selected file to a JSON array on change.
     */
    app.directive('fileUpload', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                // On change, new file is added with correct name.
                // Old file is removed from the JSON array.
                element.bind('change', function (event) {
                    var file = event.target.files[0];
                    var parname = attrs.name;
                    scope.file[parname] = file;
                    scope.$apply();
                });
            }
        };
    });

    /*
     * removeSelectedFile directive can be used for buttons within the same parent
     * of the input that should be cleared.
     */
    app.directive('removeSelectedFile', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                // On click of button, both value and JSON array are being cleared.
                element.bind('click', function () {
                    var inputElement = element.parent().find('input');
                    inputElement.val(null);
                    delete scope.file[inputElement[0].name];
                    scope.$apply();
                });
            }
        };
    });

    /*
     * setFullHeight directive, sets the height for corresponding element.
     * Can be done either with or without sites header.
     */
    app.directive('setFullHeight', function($window) {
        return {
            restrict: 'A',
            scope: {},
            link: function(scope, element, attrs) {

                // When false given as parameter, exclude site header from window height.
                scope.onResize = function() {
                    if (attrs.setFullHeight === 'false') {
                        element.css('height', ($window.innerHeight - $('#hero-header').outerHeight(true)) + 'px');
                    } else {
                        element.css('height', $window.innerHeight + 'px');
                    }
                };

                // Call onResize method and resize to window for automatic resize.
                // Also resize the Jmol viewer element.
                scope.onResize();
                angular.element($window).bind('resize', function() {
                    scope.onResize();
                    $('#jmol_applet_insane_appletinfotablediv').css('height', ($('#fixeddiv').height() - $('#controldiv').outerHeight(true) - 2) +'px');

                    // When bottom of page reached, keep bottom page visible.
                    if (bottomReached === true) {
                        document.body.scrollTop = document.body.scrollHeight;
                    } 
                });
            }
        };
    });

    /*
     * lockAtBottom directive which locks the page by setting overflow hidden.
     */
    app.directive('lockAtBottom', function($window) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {

                // When scrolling, if bottom reached, prevent page from scrolling.
                $window.onscroll = function() {
                    if (($window.innerHeight + $window.scrollY) >= document.body.offsetHeight) {
                        element.css('overflow', 'hidden');
                        bottomReached = true;
                    }
                };
            }
        };
    });

    /*
     * fadeOut directive which is able to get a value from 0.1 - 1.0 lets
     * corresponding element it's opacity being faded when scrolling page.
     * Default value is 1.
     */
    app.directive('fadeOut', function($window){
        return{
            restrict: 'A',
            link: function(scope, element, attrs){
                /*
                 * Fade with default ore selected value. 0.1 means that after
                 * 10% of the window height (excluding site header), has an opacity
                 * of 0 or less when scrolling. 
                 */
                var fadelength = 1;
                if (attrs.fadeOut !== '') {
                    fadelength = attrs.fadeOut;
                }
                $(window).scroll(function(){
                    element.css("opacity", 1 - $(window).scrollTop() / (($window.innerHeight - $('#hero-header').outerHeight(true)) * fadelength));
                });
            }
        };
    });

    /*
     * configDownload directive which generates a config file containing all
     * used setting for the model. File get created in memory.
     */
    app.directive('configDownload', function() {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {
                element.bind('click', function () {
                    var blob = new Blob([JSON.stringify(scope.master)], {type: "text/plain;charset=utf-8"});
                    saveAs(blob, "config_insane_model.json");
                });
            }
        };
    });

    /*
     * configUpload directive which enables the configuration file to be
     * uploaded by drag and drop or manual upload.
     */
    app.directive('configUpload', ['$http', function($http) {
        return {
            restrict: 'A',
            link: function(scope, element, attrs) {

                // Manual upload and add values to input scope.
                if (attrs.configUpload === 'manual') {
                    element.bind('change', function() {
                        var file = element[0].files[0];
                        var reader = new FileReader();
                        reader.readAsText(file);
                        reader.onload = function (evt) {
                            angular.forEach(JSON.parse(evt.target.result), function(value, key) {
                                scope.input[key] = value;
                            });
                            scope.$apply();
                        };
                        reader.onerror = function (evt) {
                            console.log('Error reading configuration file');
                        };
                    });

                // Disable dragover and dragenter and upload on drop.
                } else if (attrs.configUpload === '') {
                    element.on('dragover', function(e) {
                        e.preventDefault();
                        e.stopPropagation();
                    });
                    element.on('dragenter', function(e) {
                        e.preventDefault();
                        e.stopPropagation();
                    });
                    element.on('drop', function(e) {
                        e.preventDefault();
                        e.stopPropagation();
                        var file = e.dataTransfer.files[0];
                        var reader = new FileReader();
                        reader.readAsText(file);
                        reader.onload = function (evt) {
                            angular.forEach(JSON.parse(evt.target.result), function(value, key) {
                                scope.input[key] = value;
                            });
                            scope.$apply();
                        };
                        reader.onerror = function (evt) {
                            console.log('Error reading configuration file');
                        };
                    });
                }
            }
        };
    }]);
})();