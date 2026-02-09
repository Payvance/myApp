import axios from "axios";
import { API_ENDPOINTS } from "../services/api";

const api = axios.create({
  baseURL: API_ENDPOINTS.BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;
