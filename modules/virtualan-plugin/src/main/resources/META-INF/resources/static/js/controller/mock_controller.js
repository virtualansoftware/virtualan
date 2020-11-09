'use strict';

myApp.controller('MockController', ['$scope',  '$filter', '$modal', 'MockService', function($scope, $filter,  $modal, MockService) {

	var self = this;
    self.mockRequest={id:'',resource:'',url:'',method:'',type:'',operationId:'',input:'',output:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockCreateRequest= {id:'',resource:'',method:'',type:'',url:'',operationId:'',input:'',output:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockMsgRequest={id:'',resource:'',brokerUrl:'',responseTopicOrQueueName:'',type:'',requestTopicOrQueueName:'',input:'',output:'',excludeList:'', httpStatus:'',availableParams:[], headerParams:[]};
    self.mockRequests=[];
    self.mockMsgRequests=[];
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
    self.viewby = 5;
    self.perPage = 5;
    self.maxSize = 5;
    self.prettyJson = '';
    self.treeJson = '';
    self.appName = "Virtualan!!";
    getAppName();
    loadAllMockRequest();

    self.isNotEmpty = function() {
       return (Object.keys(self.mockLoadRequests).length > 0);
    }

    self.setPage = function (pageNo) {
    	self.currentPage = pageNo;
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


   self.jsonObj = JSON.parse("{  \"message\": \"____NOT_FOUND\" }");
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

    self.loadDefaultRule = function(type, value) {
     if(type.toUpperCase() === 'SCRIPT') {
       if (value == null ) {
        return  " def executeScript(mockServiceRequest, responseObject) { \n" +
            "     int age = getAge(mockServiceRequest.getInput().getBirthday()); \n" +
            "    String postalCode = mockServiceRequest.getInput().getPostalCode(); \n" +
            "    int riskFactor = computeRiskFactor(age, postalCode); \n" +
            "    responseObject.setHttpStatusCode('200'); \n" +
            "    responseObject.setOutput(String.valueOf(riskFactor)); \n" +
            "    return responseObject.builder(); \n" +
            " } \n";
          }
        } else if(type.toUpperCase() === 'RULE') {
           if (value == null ) {
              return " T(java.time.Period).between(input.dateOfBirth, T(java.time.LocalDate).now()).getYears() < 22 ";
           }
        }
    }


    self.loadParameterized = function (value) {
      console.log(value);
      self.parameterized = value;
    };

    self.getKeys = function() {
      return  self.parameterized && self.parameterized.length > 0 && Object.keys(JSON.parse(self.parameterized)[0]);
    };

    self.getParameterized = function() {
      try{
        return self.parameterized  && JSON.parse(self.parameterized);
      }catch{
      }
    };

  self.parameterizedTable = [];
  self.parameterizedKeys = [];

  self.loadParameterizedFromTable = function (value) {
    console.log(value);
    self.parameterizedTable = JSON.parse(value);
    self.parameterizedKeys = Object.keys(self.parameterizedTable[0]);
  };

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
          self.jsonStr = value;
        }
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


  self.isDefined = function (value) {
    		return typeof value !== 'undefined' && value;
    };

    self.isArrayDefined = function (value) {
    		return typeof value !== 'undefined' && value && value.length > 0;
    };

    self.keyObject =[];

    self.getRexEx  = function(contentType){
      var reTagCatcher = /(<.[^(><.)]+>)/g;
      if(contentType === 'XML'){
         reTagCatcher = /(\{.[^(\}\{.)]+\})/g;
      }
      return reTagCatcher;
    };


  self.getObjectParams = function(input, contentType){
      if(self.isDefined(input)) {
        var outputAll = new Array();
        var output;
        for (var key in input) {
          var reTagCatcher = self.getRexEx(contentType);
          output = input[key].match(reTagCatcher);
          if(self.isArrayDefined(output)) {
            for ( var i = 0; i < output.length; i++) {
               outputAll.push(output[i].substring(1, output[i].length-1));
            }
          }
       }
        return outputAll;
    }
  };

   self.getParameterizedFormat = function(input, contentType) {
     if('XML' ===   contentType) {
        return input.indexOf('{') && input.indexOf('}');
     } else {
        return input.indexOf('<') && input.indexOf('>');
     }
   };

   self.getParams = function(input, contentType){
      if(self.isDefined(input) && self.getParameterizedFormat(input, contentType)) {
         var outputAll = new Array();
         var reTagCatcher = self.getRexEx(contentType);
          var output = input.match(reTagCatcher);
          if(self.isArrayDefined(output)) {
            for ( var i = 0; i < output.length; i++) {
               outputAll.push(output[i].substring(1, output[i].length-1));
            }
          }
        return outputAll;
       }
    };

   self.getParamsMap = function(inputMap, contentType){
      if(self.isArrayDefined(inputMap)) {
         var outputAll = new Array();
         var reTagCatcher = self.getRexEx(contentType);
         for ( var index in inputMap) {
            var resValue  =  inputMap[index]['value'];
            if(self.isDefined(resValue)){
              var output = resValue.match(reTagCatcher);
              if(self.isArrayDefined(output)){
                outputAll.push(resValue.substring(1, resValue.length-1));
              }
            }
         }
         return outputAll;
       }
    };

    self.paramHeaderMapper = {};
    self.paramFinder =  function(mockRequest) {
          var keyId = mockRequest.method
          keyId = keyId.concat("-").concat(mockRequest.operationId)
      return self.paramHeaderMapper[keyId]
    };
    self.paramMapper = function (mockRequest) {
       var outputAll = new Array();

       var availableParams = self.getParamsMap(mockRequest.availableParams, mockRequest.contentType);
       if(self.isArrayDefined(availableParams)) {
           Array.prototype.push.apply(outputAll, availableParams);
       }

       var additionalParams = self.getObjectParams(mockRequest.additionalParams, mockRequest.contentType)
       if(self.isArrayDefined(additionalParams)) {
           Array.prototype.push.apply(outputAll, additionalParams);
       }

       var responseHeaderParams = self.getObjectParams(mockRequest.responseHeaderParams, mockRequest.contentType);
       if(self.isArrayDefined(responseHeaderParams)) {
           Array.prototype.push.apply(outputAll, responseHeaderParams);
       }

       var input = self.getParams(mockRequest.input, mockRequest.contentType);
       if(self.isArrayDefined(input)) {
           Array.prototype.push.apply(outputAll, input);
       }
       var output = self.getParams(mockRequest.output, mockRequest.contentType);
       if(self.isArrayDefined(output)) {
           Array.prototype.push.apply(outputAll, output);
       }

       if(self.isArrayDefined(outputAll)) {
          var keyId = mockRequest.method
          keyId = keyId.concat("-").concat(mockRequest.operationId)
          self.paramHeaderMapper[keyId] = outputAll.reduce((unique, item) => (unique.includes(item) ? unique : [...unique, item]), [],);;
       }

    };


    self.addParametrizedParams = function(mockRequest, keyObject) {
    	if(keyObject != null) {
        const obj  = {}
        for(var k in keyObject) {
          if(keyObject.hasOwnProperty(k) &&
                k.substring(0, k.lastIndexOf('-'))  ===
                ('params-'+mockRequest.method+'-'+mockRequest.operationId)) {
            obj[k.substring(k.lastIndexOf('-')+1)] = keyObject[k];
            keyObject[k] ='';
           }
        }
        if(self.isDefined(mockRequest.rule)){
          if(!self.isExist(mockRequest.rule, obj)) {
            mockRequest.rule.push(obj);
          }
        } else {
          mockRequest.rule = new Array();
          mockRequest.rule.push(obj);
        }
        keyObject = {};
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
     };

    self.isExist = function(rule, key) {
        for(const index in rule) {
            if(self.objectsAreEqual(key, rule[index])){
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
    	self.paramMapper(mockRequest);
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
    	self.paramMapper(mockRequest);
      self.responseHeaderParamKey ='';
      self.responseHeaderParamValue ='';
    };

    self.removeParam = function(mockRequest, item, params) {
    	delete params[item];
    	self.paramMapper(mockRequest);
    };

    self.removeParameterized = function(mockRequest, item) {
        mockRequest.rule = mockRequest.rule.filter(key => !self.objectsAreEqual(key, item));
    };

    self.setItemsPerPage = function(num) {
	    self.perPage = num;
	    self.currentPage = 1; //reset to first page
    };

    self.loadData = fetchAllMockRequest();

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

    function fetchAllMockRequest(){
    	MockService.fetchAllMockRequest()
            .then(
            function(d) {
                self.mockRequests = d;
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
            		fetchAllMockRequest,
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
            		fetchAllMsgMockRequest,
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
            		fetchAllMockRequest,
            function(errResponse){
                console.error('Error while updating MockRequest');
            }
        );
    }

    function deleteMsgMockRequest(id){
        MockService.deleteMockRequest(id)
            .then(
            		fetchAllMsgMockRequest,
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
            mockRule = angular.toJson(mockRule);
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

    function edit(id){
        console.log('id to be edited', id);
        for(var i = 0; i < self.mockRequests.length; i++){
            if(self.mockRequests[i].id === id) {
                self.mockRequest = angular.copy(self.mockRequests[i]);
                break;
            }
        }
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

}]);

