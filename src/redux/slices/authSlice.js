import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { loginUser } from "../../constants/endpoints";
import {
  getAccessToken,
  getRoleId,
  getRedirectUrl,
  getUserId,
  clearAuthData,
} from "../../utils/storage";

// ======================================
// ASYNC THUNKS
// ======================================
export const login = createAsyncThunk(
  "auth/login",
  async ({ username, password }, { rejectWithValue }) => {
    try {
      const response = await loginUser(username, password);
      return response;
    } catch (error) {
      return rejectWithValue(error || "Login failed");
    }
  }
);

// ======================================
// INITIAL STATE
// ======================================
const initialState = {
  accessToken: getAccessToken(),
  roleId: getRoleId(),
  userId: getUserId(),
  redirectUrl: getRedirectUrl(),
  isAuthenticated: !!getAccessToken(),
  loading: false,
  error: null,
};

// ======================================
// SLICE
// ======================================
const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    logout: (state) => {
      clearAuthData();
      state.accessToken = null;
      state.roleId = null;
      state.userId = null;
      state.redirectUrl = null;
      state.isAuthenticated = false;
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // LOGIN
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        const { accessToken, roleId, redirectUrl, userId } = action.payload;

        state.loading = false;
        state.accessToken = accessToken;
        state.roleId = roleId;
        state.userId = userId;
        state.redirectUrl = redirectUrl;
        state.isAuthenticated = true;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
        state.isAuthenticated = false;
      });
  },
});

// ======================================
// EXPORTS
// ======================================
export const { logout } = authSlice.actions;
export default authSlice.reducer;
