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

const apiRequestsGet = (getEndpoint: string) => {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(getEndpoint);
        // console.log("data fetched", response);
        const responseData = response.data;
        setData(responseData);
      } catch (error: any) {
        // console.error("Error fetching data:", error);
        setError(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [getEndpoint]);

  return data;
};

export { apiRequestsGet, apiRequestsDelete, apiRequestsPost };
