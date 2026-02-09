// ======================================
// KEYS
// ======================================
export const AUTH_KEYS = {
  ACCESS_TOKEN: "accessToken",
  ROLE_ID: "roleId",
  REDIRECT_URL: "redirectUrl",
  USER_ID: "userId",
};

// ======================================
// SET AUTH DATA
// ======================================
export const setAuthData = (data) => {
  Object.entries(data).forEach(([key, value]) => {
    if (key in AUTH_KEYS && value !== undefined) {
      localStorage.setItem(AUTH_KEYS[key], value);
    }
  });
};

// ======================================
// GETTERS
// ======================================
export const getAccessToken = () => localStorage.getItem(AUTH_KEYS.ACCESS_TOKEN);
export const getRoleId = () => localStorage.getItem(AUTH_KEYS.ROLE_ID);
export const getRedirectUrl = () => localStorage.getItem(AUTH_KEYS.REDIRECT_URL);
export const getUserId = () => localStorage.getItem(AUTH_KEYS.USER_ID);

// ======================================
// AUTH UTILITIES
// ======================================
export const isAuthenticated = () => {
  const token = getAccessToken();
  return !!token; // Returns true if token exists and is not empty
};

export const requireAuth = () => {
  if (!isAuthenticated()) {
    window.location.href = '/login';
    return false;
  }
  return true;
};

export const getAuthData = () => {
  return {
    token: getAccessToken(),
    roleId: getRoleId(),
    userId: getUserId(),
    redirectUrl: getRedirectUrl()
  };
};

// ======================================
// CLEAR AUTH DATA
// ======================================
export const clearAuthData = () => {
  Object.values(AUTH_KEYS).forEach((key) => localStorage.removeItem(key));
};
