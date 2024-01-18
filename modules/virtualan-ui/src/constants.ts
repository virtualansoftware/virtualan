
let ENV_URL = "http://localhost:8080"; 
// let ENV_URL = "https://live.virtualandemo.com";

if (process.env.NODE_ENV !== 'development') {
    ENV_URL = "";
}

export const API_GET_ENDPOINT_ADD = ENV_URL + "/virtualservices/load";
export const API_GET_ENDPOINT_LOAD = ENV_URL + "/virtualservices";
export const API_DELETE_ENDPOINT = ENV_URL + "/virtualservices";
export const API_POST_ENDPOINT = ENV_URL + "/virtualservices";
export const API_GET_CATALOGS = ENV_URL + "/api-catalogs";
