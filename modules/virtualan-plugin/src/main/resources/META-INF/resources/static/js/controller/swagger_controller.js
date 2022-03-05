'use strict';
myApp.controller('SwaggerController', [ '$scope', '$window','MockService', 
		function($scope, $window, MockService) {
			var self = this;
			self.enableCatalog = false;
			self.catalogs =[]; 
			//self.fileNames =[]; 
			self.catalogName = '';
			
			self.openSwaggerCatalog = function() {
				loadUrl("swagger-ui/index.html");
			};
			self.openSwaggerUI = function() {
				loadUrl("swagger-ui/index.html");
			};
			self.openSwaggerEUI = function() {
				loadUrl("swagger-ui/swaggerex-ui.html");
			};
			self.openSwaggerEditor = function() {
				loadUrl("swagger-editor/index.html")
			};
			
			
			function loadUrl(url) {
				$window.open(url, "_blank", "location=no,toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=1000,height=600");
			}
		} ]);
