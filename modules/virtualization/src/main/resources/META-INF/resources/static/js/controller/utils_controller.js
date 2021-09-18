'use strict';
myApp.controller('UtilsController', [ '$scope', '$filter', '$window','MockService','$sce',
		function($scope, $filter, $window, MockService, $sce ) {

			var self = this;
			self.input = "Enter the Json here to get formatted";
			self.output = "You will see the Formatted Output or Error";
			self.jsonFormatter = function() {
				try {
					self.output = JSON.parse(self.input);
				} catch (throw_error) {
					self.output = throw_error.message;
				}
			}


            self.pageUrl = null;

            self.iframeUrl = function(){
                return $sce.trustAsResourceUrl(self.pageUrl);
            };

            self.loadTutorialsPage= function(url){
                MockService.checkUrl(url).then(
                   function(d) {
                     self.pageUrl = url;
                   },
                   function(errResponse){
                     self.pageUrl = "notutorials.html";
                        console.error('Error while fetching Soap Mocks');
                     }
                   );
             };
		} ]);
