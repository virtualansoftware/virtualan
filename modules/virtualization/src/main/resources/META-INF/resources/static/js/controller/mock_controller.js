'use strict';

myApp.controller('MockController', ['$scope',  '$filter', '$modal', 'MockService', function($scope, $filter,  $modal, MockService) {
    
	var self = this;
    self.mockRequest={id:'',resource:'',url:'',method:'',type:'',operationId:'',input:'',output:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockCreateRequest= {id:'',resource:'',method:'',type:'',url:'',operationId:'',input:'',output:'', contentType:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockMsgRequest={id:'',resource:'',brokerUrl:'',responseTopicOrQueueName:'',type:'',requestTopicOrQueueName:'',input:'',output:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockRequests=[];
    self.mockMsgRequests=[];
    self.mockSoapRequests=[];
    self.additionalParamKey ='';
    self.additionalParamValue ='';
    self.additionalParams ={};
    self.responseHeaderParams={};
    self.additionalParamList =[];
    self.responseHeaderParamList=[];
    self.responseHeaderParamKey ='';
    self.responseHeaderParamValue ='';
    self.mockLoadRequests=[];
    self.mockLoadRequest='';
    self.kafkaTopics =[];
    self.soapRequests =[];
    self.selectedOperationId ='';
    self.edit = edit;
    self.remove = remove;
    self.message ='';
    self.typeWarning = false;
    self.filtered = {};
    self.searchText ='';
    self.currentPage = 1;
    self.msgFiltered = {};
    self.searchMsgText ='';
    self.currentMsgPage = 1;
    self.soapFiltered = {};
    self.searchSoapText ='';
    self.currentSoapPage = 1;
    self.viewby = 5;
    self.perPage = 5;
    self.maxSize = 5;
    self.viewMsgby = 5;
    self.perMsgPage = 5;
    self.maxMsgSize = 5;
    self.viewSoapby = 5;
    self.perSoapPage = 5;
    self.maxSoapSize = 5;
    self.prettyJson = '';
    self.treeJson = '';
    self.appName = "Virtualan!!";
    getAppName();
    loadAllMockRequest();
    loadAllTopics();
    loadAllSoapRequest();
    self.tmp= null;
    self.isNotEmpty = function() {
       return (Object.keys(self.mockLoadRequests).length > 0);
    }

    self.setPage = function (pageNo) {
    	self.currentPage = pageNo;
    };


    self.setMsgPage = function (pageNo) {
    	self.currentMsgPage = pageNo;
    };


    self.setSoapPage = function (pageNo) {
    	self.currentSoapPage = pageNo;
    };

     function getAppName(){	
    	MockService.readApplicationName()
            .then(
            function(d) {
            	self.appName = d.appName ;
            },
            function(errResponse){
                console.error('Error while fetching app-name');
            }
       );
    };

    var self = this;

    self.input = "Enter the Json here to get formatted";
    self.output = "You will see the Formatted Output or Error";
    self.jsonOutputObj = JSON.parse("{  \"errorCode\": \"NOT_FOUND\" }");

    self.jsonFormatter = function() {
        try {
            self.input = JSON.stringify(JSON.parse(self.input), undefined, 4);
            self.jsonOutputObj = JSON.parse(self.input);
            self.showJSONDialog = false;
        } catch (throw_error) {
            self.error = throw_error.message;
            self.showJSONDialog = true;
        }
    }

   self.jsonObj = JSON.parse("{  \"message\": \"No Data Found\" }");
   self.groovyObj = "NO Data ";

    self.copyToClipboard = function (data) {
         angular.element('<textarea/>')
             .css({ 'opacity' : '0', 'position' : 'fixed' })
             .text(data)
             .appendTo(angular.element(window.document.body))
             .select()
             .each(function() { document.execCommand('copy') })
             .remove();
     }

    self.isEmptyNotPresent = function (value) {
      if(value === null || value === '' || value === undefined){
         return  true ;
      }
      return false;
    };

    self.loadDefaultRule = function(type, value, mockRequest) {
     if(value != null) {
        self.tmp = value
     }if(type && type.toUpperCase() === 'SCRIPT') {
       if (self.tmp == null && value == null ) {
        return  " def executeScript(mockServiceRequest, responseObject) { \n" +
            "     int age = getAge(mockServiceRequest.getInput().getBirthday()); \n" +
            "    String postalCode = mockServiceRequest.getInput().getPostalCode(); \n" +
            "    int riskFactor = computeRiskFactor(age, postalCode); \n" +
            "    responseObject.setHttpStatusCode('200'); \n" +
            "    responseObject.setOutput(String.valueOf(riskFactor)); \n" +
            "    return responseObject.builder(); \n" +
            " } \n";
          } else {
            return self.tmp;
          }
        } else if( type && type.toUpperCase() === 'RULE') {
           if (self.tmp == null && value == null ) {
              return " T(java.time.Period).between(input.dateOfBirth, T(java.time.LocalDate).now()).getYears() < 22 ";
            } else {
               return self.tmp;
            }
        } else if(type && type.toUpperCase() === 'PARAMS' && value == null) {
        }
    }

    self.loadJson = function (value) {
      try{
          if(typeof value === 'object' && value !== null ) {
            self.jsonObj = JSON.parse(JSON.stringify(value, undefined, 4));
            self.jsonStr =  JSON.stringify(value, undefined, 4);
          } else {
            self.jsonObj = JSON.parse(value);
            self.jsonStr = JSON.stringify(JSON.parse(value), undefined, 4);
          }
        }catch(e){
          self.jsonObj = "{ \"message\" : \"NO-DATA\"}";
          self.jsonStr = prettifyXml(value);
        }
    };

     function prettifyXml(sourceXml){
        var xmlDoc = new DOMParser().parseFromString(sourceXml, 'application/xml');
        var xsltDoc = new DOMParser().parseFromString([
            // describes how we want to modify the XML - indent everything
            '<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">',
            '  <xsl:strip-space elements="*"/>',
            '  <xsl:template match="para[content-style][not(text())]">', // change to just text() to strip space in text nodes
            '    <xsl:value-of select="normalize-space(.)"/>',
            '  </xsl:template>',
            '  <xsl:template match="node()|@*">',
            '    <xsl:copy><xsl:apply-templates select="node()|@*"/></xsl:copy>',
            '  </xsl:template>',
            '  <xsl:output indent="yes"/>',
            '</xsl:stylesheet>',
        ].join('\n'), 'application/xml');

        var xsltProcessor = new XSLTProcessor();
        xsltProcessor.importStylesheet(xsltDoc);
        var resultDoc = xsltProcessor.transformToDocument(xmlDoc);
        var resultXml = new XMLSerializer().serializeToString(resultDoc);
        return resultXml;
    };

    self.loadGroovy = function (value) {
      console.log(value);
      self.groovyObj = value;
    };

    $scope.$watch(self.searchText, function (term) {
      var obj = term;
      self.filterList = $filter('filter')(self.mockRequests, obj);
      self.currentPage = 1;
    });

   $scope.$watch(self.searchMsgText, function (term) {
         var obj = term;
         self.filterMsgList = $filter('filter')(self.mockMsgRequests, obj);
         self.currentMsgPage = 1;
       });

   $scope.$watch(self.searchSoapText, function (term) {
         var obj = term;
         self.filterSoapList = $filter('filter')(self.mockSoapRequests, obj);
         self.currentSoapPage = 1;
       });

    self.isDefined = function (value) {
    		return typeof value !== 'undefined';
    }

    self.keyObject =[];


    self.paramFinder = function (mockRequest) {
       var output = null;
       if(mockRequest.input.indexOf('<') && mockRequest.input.indexOf('>')) {
          var reTagCatcher = /(<.[^(><.)]+>)/g;
          var output = mockRequest.input.match(reTagCatcher);
          for ( var i = 0; i < output.length; i++) {
             output[i] = output[i].substring(1, output[i].length-1);
          }
       }
       return output;
    }


    self.addParametrizedParams = function(mockRequest, key) {
    	if(key != null) {
        var headerNames = self.paramFinder(mockRequest);
        const obj = {};
        for ( var i = 0; i < key.length; i++) {
          obj[headerNames[i]] = key[i];
        }
        if(self.isDefined(mockRequest.rule)){
          if(!self.isExist(mockRequest, obj)) {
            mockRequest.rule.push(obj);
          }
        } else {
          mockRequest.rule = new Array();
          mockRequest.rule.push(obj);
        }
    	}
    };

    self.objectsAreEqual = function(a, b) {
       for (var prop in a) {
         if (a.hasOwnProperty(prop)) {
           if (b.hasOwnProperty(prop)) {
             if (typeof a[prop] === 'object') {
               if (!self.objectsAreEqual(a[prop], b[prop])) return false;
             } else {
               if (a[prop] !== b[prop]) return false;
             }
           } else {
             return false;
           }
         }
       }
       return true;
     }

    self.isExist = function(mockRequest, key) {
        for(const index in mockRequest.rule) {
            if(self.objectsAreEqual(key, mockRequest.rule[index])){
              return true;
            }
        }
        return false;
    };


    self.addParam = function(mockRequest, key, value) {
    	if(self.isDefined(mockRequest.additionalParams)){
    		mockRequest.additionalParams[key] = value;
    	} else {
    		mockRequest.additionalParams = {};
    		mockRequest.additionalParams[key] = value;
    	}
        self.additionalParamKey ='';
        self.additionalParamValue ='';
    };
    
    self.addResponseHeaderParam = function(mockRequest, key, value) {
    	if(self.isDefined(mockRequest.responseHeaderParams)){
    		mockRequest.responseHeaderParams[key] = value;
    	} else {
    		mockRequest.responseHeaderParams = {};
    		mockRequest.responseHeaderParams[key] = value;
    	}
        self.responseHeaderParamKey ='';
        self.responseHeaderParamValue ='';
    };
    
    self.removeParam = function(item, params) {
    	delete params[item];
    };



    self.setItemsSoapPerPage = function(num) {
	    self.perSoapPage = num;
	    self.currentSoapPage = 1; //reset to first page
    };

    self.setItemsPerPage = function(num) {
	    self.perPage = num;
	    self.currentPage = 1; //reset to first page
    };

    self.setItemsMsgPerPage = function(num) {
	    self.perMsgPage = num;
	    self.currentMsgPage = 1; //reset to first page
    };
        
    self.loadData = fetchAllMockRequest();
    self.loadMsgData = fetchAllMsgMockRequest();
    self.loadSoapData = fetchAllSoapMockRequest();

    function fetchAllSoapMockRequest(){
    	MockService.fetchAllSoapMockRequest()
            .then(
            function(d) {
                self.mockSoapRequests = d;
                self.filterSoapList = self.mockSoapRequests;
            },
            function(errResponse){
                console.error('Error while fetching Soap Mocks');
            }
        );
    };


    function fetchAllMsgMockRequest(){
        	MockService.fetchAllMsgMockRequest()
                .then(
                function(d) {
                    self.mockMsgRequests = d;
                    self.filterMsgList = self.mockMsgRequests;
                },
                function(errResponse){
                    console.error('Error while fetching Mocks');
                }
            );
        };


     function filterRestObject(obj){
       return obj.filter(function (el) { return  (el.requestType == 'REST' || el.requestType == "" ) });
     }

    function fetchAllMockRequest(){
    	MockService.fetchAllMockRequest()
            .then(
            function(d) {
                self.mockRequests = filterRestObject(d);
                self.filterList = self.mockRequests;
            },
            function(errResponse){
                console.error('Error while fetching Mocks');
            }
        );
    };
    
    
    function loadAllMockRequest(){
        MockService.loadAllMockRequest()
            .then(
            function(d) {
            	self.mockLoadRequests = d;
           	 console.log("ALL API's", Object.keys(self.mockLoadRequests).length);
            },
            function(errResponse){
                console.error('Error while fetching mockLoadRequests');
            }
        );
    }

   self.isSoapNotEmpty = function() {
       return (Object.keys(self.soapRequests).length > 0);
    }

    function loadAllSoapRequest(){
            MockService.loadAllSoapRequest()
                .then(
                function(d) {
                	self.soapRequests = d;
               	 console.log("ALL soapRequests's ", Object.keys(self.soapRequests).length);
                },
                function(errResponse){
                    console.error('Error while fetching soapRequests');
                }
            );
        }



  function loadAllTopics(){
          MockService.loadAllTopics()
              .then(
              function(d) {
                self.kafkaTopics = d;
               console.log("ALL kafka's ", self.kafkaTopics.length);
              },
              function(errResponse){
                  console.error('Error while fetching kafkaTopics');
              }
          );
      }

	self.showJSONDialog = false;
	self.closeJSONAlertDialog = false;

	self.typeJSONDialog = function() {
		return "warning";
	}

	self.closeJSONAlert = function() {
		self.typeJSONWarning = false;
		self.showJSONDialog = false;
	}

	self.showJSONMessage = function() {
			return self.error;
	}


	self.showDialog = false;
	
	self.showAlert = function(operationId) {
		if(operationId === self.selectedOperationId){
			self.closeAlertDialog = true;
			return true;
		}
		return false;
	}
	
	self.typeDialog = function() { 
		if(self.typeWarning) {
			return "warning";
		} 
			return "success";
	}
	
	self.closeAlert = function(operationId) {
		self.typeWarning = false;
		self.showDialog = false;
	}
	
	self.showMessage = function(operationId) {
		if(operationId === self.selectedOperationId){
			return self.message;
		}
		return;
	}
    
    function createMockRequest(mockRequest){
		self.showDialog = false;
    	self.typeWarning = false;
    	self.selectedOperationId = mockRequest.operationId;
		MockService.createMockRequest(mockRequest)
            .then(
            		self.loadData = fetchAllMockRequest,
            function(errResponse){
                self.showDialog = true;
            	self.typeWarning = true;
            	self.message = errResponse.data.code;
                console.error('Error while creating MockRequest');
            }
        );
    	self.showDialog = true;
    	self.message = 'Mock response added successfully for the given request parameter(s)!!!!';
    }

    function createMockSoapRequest(mockRequest){
      self.showDialog = false;
      self.typeWarning = false;
      self.selectedOperationId = mockRequest.operationId;
      MockService.createMockSoapRequest(mockRequest)
        .then(
            self.loadSoapData = fetchAllSoapMockRequest,
        function(errResponse){
            self.showDialog = true;
          self.typeWarning = true;
          self.message = errResponse.data.code;
            console.error('Error while creating MockRequest');
        }
      );
      self.showDialog = true;
      self.message = 'Mock response added successfully for the given request parameter(s)!!!!';
    }

 function createMockMsgRequest(mockRequest){
		self.showDialog = false;
    	self.typeWarning = false;
    	self.selectedOperationId = mockRequest.requestTopicOrQueueName;
		MockService.createMockMsgRequest(mockRequest)
            .then(
            		self.loadMsgData = fetchAllMsgMockRequest,
            function(errResponse){
                self.showDialog = true;
            	self.typeWarning = true;
            	self.message = errResponse.data.code;
                console.error('Error while creating createMockMsgRequest');
            }
        );
    	self.showDialog = true;
    	self.message = 'Message Mock response added successfully for the given request parameter(s)!!!!';
    }


    function updateMockRequest(mockRequest, id){
        MockService.updateMockRequest(mockRequest, id)
            .then(
            		self.loadData = fetchAllMockRequest,
            function(errResponse){
                console.error('Error while updating MockRequest');
            }
        );
    }


    function deleteSoapMockRequest(id){
        MockService.deleteMockRequest(id)
            .then(
            		self.loadSoapData = fetchAllSoapMockRequest,
            function(errResponse){
                console.error('Error while deleting Msg MockRequest');
            }
        );
    }

    function deleteMsgMockRequest(id){
        MockService.deleteMockRequest(id)
            .then(
            		self.loadMsgData = fetchAllMsgMockRequest,
            function(errResponse){
                console.error('Error while deleting Msg MockRequest');
            }
        );
    }

    function deleteMockRequest(id){
        MockService.deleteMockRequest(id)
            .then(
            		fetchAllMockRequest,
            function(errResponse){
                console.error('Error while deleting MockRequest');
            }
        );
    }

    self.mergeParams = function (mockRequest){
    	
    	self.availableParamsWithAdditionalParam = [];
    	
    	angular.forEach(mockRequest.availableParams, function(data){
    		self.availableParamsWithAdditionalParam.push(data);
    	});
    	if(self.isDefined(mockRequest.additionalParams)) {
    		self.moveParams(mockRequest.additionalParams, self.availableParamsWithAdditionalParam);
    	}
    	return self.availableParamsWithAdditionalParam;
    }

    self.moveParams = function (paramMap, params){
    	if(self.isDefined(paramMap)) {
		    Object.keys(paramMap).forEach(function(key) {
			    self.value = paramMap[key];
			    params.push(self.addParamObj(key, self.value))
			});
    	}
    }
    
    
	
    self.addParamObj = function(key, value) {
    	self.additionalParam={};
        self.additionalParam['key'] =  key;
    	self.additionalParam['value'] =  value;
		return self.additionalParam;
    }
    
    self.addResParam = function(key, value) {
    	self.responseHeaderParam = {};
	    self.responseHeaderParam['key'] =  key;
		self.responseHeaderParam['value'] =  value;
		return self.responseHeaderParam;
    }
    
    self.submit= function (mockRequest) {
        console.log('Saving New mockRequest', mockRequest);
        self.responseHeaderParamList = [];
        self.moveParams(mockRequest.responseHeaderParams, self.responseHeaderParamList)
        var mockRule = mockRequest.rule;
        if(mockRequest.type === 'Params') {
          mockRule = JSON.stringify(mockRule);
        }

        self.mockCreateRequest= {id:'',
        			resource:mockRequest.resource,
        			url:mockRequest.url,
        			type:mockRequest.type,
              rule:mockRule,
              operationId:mockRequest.operationId,
        			input:mockRequest.input,
        			output:mockRequest.output,
        			excludeList:mockRequest.excludeList, 
        			httpStatusCode:mockRequest.httpStatusCode,
        			method:mockRequest.method,
        			availableParams:self.mergeParams(mockRequest),
        			headerParams:self.responseHeaderParamList};
        console.log('Saving New mockRequest', self.mockCreateRequest);
        createMockRequest(self.mockCreateRequest);

    }

    self.submitSoap= function (mockRequest) {
        console.log('Saving New Soap mock Request', mockRequest);
        self.responseHeaderParamList = [];
        self.moveParams(mockRequest.responseHeaderParams, self.responseHeaderParamList)
        self.mockCreateRequest= {id:'',
            resource:mockRequest.resource,
            url:mockRequest.url,
            type:mockRequest.type,
            rule:mockRequest.rule,
            operationId:mockRequest.operationId,
            input:mockRequest.input,
            output:mockRequest.output,
            contentType:mockRequest.contentType,
            excludeList:mockRequest.excludeList,
            httpStatusCode:mockRequest.httpStatusCode,
            method:mockRequest.method,
            availableParams:self.mergeParams(mockRequest),
            headerParams:self.responseHeaderParamList};
        console.log('Saving New mock Soap Request', self.mockCreateRequest);
        createMockSoapRequest(self.mockCreateRequest);
    }

    self.submitMessage= function (mockRequest) {
            console.log('Saving New message mock  Request', mockRequest);
            self.responseHeaderParamList = [];
            self.moveParams(mockRequest.responseHeaderParams, self.responseHeaderParamList)
            self.mockCreateRequest= {id:'',
            			resource:mockRequest.resource,
            			brokerUrl:mockRequest.url,
            			requestTopicOrQueueName:mockRequest.operationId,
            			input:mockRequest.input,
            			output:mockRequest.output,
            			excludeList:mockRequest.excludeList,
            			responseTopicOrQueueName:mockRequest.method,
            			availableParams:self.mergeParams(mockRequest),
            			headerParams:self.responseHeaderParamList};
            console.log('Saving New mock message Request', self.mockCreateRequest);
            createMockMsgRequest(self.mockCreateRequest);

        }

    function edit(id){
        console.log('id to be edited', id);
        for(var i = 0; i < self.mockRequests.length; i++){
            if(self.mockRequests[i].id === id) {
                self.mockRequest = angular.copy(self.mockRequests[i]);
                break;
            }
        }
    }

    self.removeSoap = function(id){
        console.log('id to be deleted', id);
        if(self.mockRequest.id === id) {//clean form if the mockRequest to be deleted is shown there.
            reset();
        }
        deleteSoapMockRequest(id);
    }

    self.removeMsg = function(id){
        console.log('id to be deleted', id);
        if(self.mockRequest.id === id) {//clean form if the mockRequest to be deleted is shown there.
            reset();
        }
        deleteMsgMockRequest(id);
    }

    function remove(id){
        console.log('id to be deleted', id);
        if(self.mockRequest.id === id) {//clean form if the mockRequest to be deleted is shown there.
            reset();
        }
        deleteMockRequest(id);
    }


    self.reset = function (myForm, mockRequest){
        self.message ='';
        self.classCode = '';
        if(mockRequest) {
          mockRequest.id = null;
          mockRequest.output= null;
          mockRequest.excludeList = null;
          mockRequest.httpStatusCode ='';
          self.type = null;
          for (var parm in mockRequest.availableParams) {
            mockRequest.availableParams[parm].value =  '';
          }
          if(mockRequest.input != null) {
            mockRequest.input = '';
        }
          myForm.$setPristine(); //reset Form
      }
    }

}]);

