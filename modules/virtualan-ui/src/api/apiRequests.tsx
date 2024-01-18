import { useEffect, useState } from "react";
import axios from "axios";

const apiRequestsPost = async (postEndpoint: string, postData: any) => {
  try {
    const response = await axios.post(postEndpoint, postData);
    return response;
  } catch (error) {
    return error;
  }
};

const apiRequestsDelete = async (deleteEndpoint: string, id: number) => {
  try {
    await axios.delete(`${deleteEndpoint}/${id}`);
    // console.log(`Data with ID ${id} deleted successfully.`);
    return null;
  } catch (deleteError: any) {
    // console.error(`Error deleting data with ID ${id}:`, deleteError);
    return deleteError;
  }
};


const apiRequestsGet = async (getEndpoint: string) => {
  try {
    const response = await axios.get(getEndpoint);
    return response.data;
  } catch (error) {
    console.error("Error fetching data:", error);
    throw error;
  }
};


export { apiRequestsGet, apiRequestsDelete, apiRequestsPost };
