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
     */
    var app = angular.module('app-directives', []);

    /*
     * fileUpload directive which adds the selected file to a JSON array on change.
     */
    app.directive('fileUpload', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                /*
                 * On change, new file is added with correct name.
                 * Old file is removed from the JSON array.
                 */
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
                /*
                 * On click of button, both value and JSON array are being cleared.
                 */
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
    app.directive('setFullHeight', function($window){
        return{
            restrict: 'A',
            scope: {},
            link: function(scope, element, attrs){

                /*
                 * When false given as parameter, exclude site header from window height.
                 */
                scope.onResize = function() {
                    if (attrs.setFullHeight === 'false') {
                        element.css('height', ($window.innerHeight - $('#hero-header').outerHeight(true)) + 'px');
                    } else {
                        element.css('height', $window.innerHeight + 'px');
                    }
                };

                /*
                 * Call onResize method and resize to window for automatic resize.
                 * Also resize the Jmol viewer element.
                 */
                scope.onResize();
                angular.element($window).bind('resize', function() {
                    scope.onResize();
                    $('#jmol_applet_insane_appletinfotablediv').css('height', ($('#fixeddiv').height() - $('#controldiv').outerHeight(true) - 2) +'px');
                });
            }
        };
    });

    /*
     * fadeOut directive which is able to get a value from 0.1 - 1.0 lets
     * corresponding element it's opacity being faded when scrolling page.
     * Defeult value is 1.
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
})();