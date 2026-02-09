import api from "../utils/api";
import { API_ENDPOINTS } from "../services/api";
import { setAuthData } from "../utils/storage";

export const loginUser = async (email, password) => {
  try {
    const response = await api.post(API_ENDPOINTS.AUTH.LOGIN, {
      email,
      password,
    });

    const { accessToken, roleId, redirectUrl } = response.data;

    setAuthData({ accessToken, roleId, redirectUrl });

    return response.data;
  } catch (error) {
    throw error?.response?.data || error;
  }
};
