import axios from 'axios';
import type { AxiosError, AxiosInstance, AxiosRequestConfig } from 'axios';
import { toast } from 'react-toastify';

export interface ApiResponse<T> {
  status?: boolean;
  statusCode?: string | number;
  data: T;
  message: string;
}

interface ApiErrorResponse {
  message?: string;
}

const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

const handleSuccess = <T>(response: ApiResponse<T>): ApiResponse<T> => {
  // If explicitly status: false (legacy)
  if (response.status === false) {
    toast.error(response.message);
    throw new Error(response.message);
  }

  // If there's a statusCode but it represents an error state
  if (
    response.statusCode &&
    typeof response.statusCode === 'string' &&
    !['ACCEPTED', 'OK', 'CREATED', 'SUCCESS'].includes(response.statusCode)
  ) {
    toast.error(response.message || 'An error occurred');
    throw new Error(response.message || 'An error occurred');
  }

  if (response.message) {
    toast.success(response.message);
  }
  console.log(JSON.stringify(response));
  return response;
};

const handleError = <T>(error: AxiosError<ApiErrorResponse>): ApiResponse<T> => {
  const message = error.response?.data?.message ?? error.message ?? 'Something went wrong';

  toast.error(message);

  console.log(error.response?.data);
  return error.response?.data as ApiResponse<T>;
};

export const apiHelper = {
  async get<T>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T> | ApiErrorResponse> {
    try {
      const { data } = await api.get<ApiResponse<T>>(url, config);
      return handleSuccess(data);
    } catch (error) {
      return handleError(error as AxiosError<ApiErrorResponse>);
    }
  },

  async post<T>(
    url: string,
    body?: unknown,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T> | ApiErrorResponse> {
    try {
      const { data } = await api.post<ApiResponse<T>>(url, body, config);
      return handleSuccess(data);
    } catch (error) {
      return handleError(error as AxiosError<ApiErrorResponse>);
    }
  },

  async put<T>(
    url: string,
    body?: unknown,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T> | ApiErrorResponse> {
    try {
      const { data } = await api.put<ApiResponse<T>>(url, body, config);
      return handleSuccess(data);
    } catch (error) {
      return handleError(error as AxiosError<ApiErrorResponse>);
    }
  },

  async delete<T>(
    url: string,
    config?: AxiosRequestConfig
  ): Promise<ApiResponse<T> | ApiErrorResponse> {
    try {
      const { data } = await api.delete<ApiResponse<T>>(url, config);
      return handleSuccess(data);
    } catch (error) {
      return handleError(error as AxiosError<ApiErrorResponse>);
    }
  },
};

export default apiHelper;
