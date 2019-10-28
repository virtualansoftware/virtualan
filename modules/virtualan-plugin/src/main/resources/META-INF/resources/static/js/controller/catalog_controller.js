'use strict';
myApp.controller('CatalogController', [ '$scope', '$window','MockService', 
		function($scope, $window, MockService) {
			var self = this;
			self.enableCatalog = false;
			self.catalogs =[]; 
			self.fileNames =[]; 
			self.catalogName = '';
			self.contextPath = '';
			loadCatalog();

			function loadCatalog() {
				MockService.loadCatalogNames().then(function(d) {
					self.catalogs = d;
					if(self.catalogs.length > 0){
						self.enableCatalog = true;
					}
					
				}, function(errResponse) {
					console.error('Error while fetching catalog files');
				});
			};
			
			self.loadFiles = function (name) {
				MockService.loadCatalogFiles(name).then(function(d) {
					self.catalogName = name;
					self.fileNames = d;
					self.contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2));
					//window.swaggerUi.api.setBasePath(self.contextPath);
					console.info(self.contextPath);
				}, function(errResponse) {
					console.error('Error while fetching load filenames');
				});
			};
			
			function loadUrl(url) {
				$window.open(url, "_blank", "location=no,toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=1000,height=600");
			}
		} ]);
