// ======================================
// AUTH STORAGE KEYS
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
export const setAuthData = ({ accessToken, roleId, redirectUrl, userId }) => {
  if (accessToken) {
    localStorage.setItem(AUTH_KEYS.ACCESS_TOKEN, accessToken);
  }

  if (roleId) {
    localStorage.setItem(AUTH_KEYS.ROLE_ID, roleId);
  }

  if (redirectUrl) {
    localStorage.setItem(AUTH_KEYS.REDIRECT_URL, redirectUrl);
  }

  if (userId) {
    localStorage.setItem(AUTH_KEYS.USER_ID, userId);
  }
};

// ======================================
// GETTERS
// ======================================
export const getAccessToken = () =>
  localStorage.getItem(AUTH_KEYS.ACCESS_TOKEN);

// Get Role where refreshing the page would default to the view
// The roleId was being retrieved as a string from local storage after a refresh, causing the strict equality check to fail.
// so thats why we are using Number() to convert the roleId to a number.
export const getRoleId = () => {
  const roleId = localStorage.getItem(AUTH_KEYS.ROLE_ID);
  return roleId ? Number(roleId) : null;
};

export const getRedirectUrl = () =>
  localStorage.getItem(AUTH_KEYS.REDIRECT_URL);

export const getUserId = () => {
  const userId = localStorage.getItem(AUTH_KEYS.USER_ID);
  return userId ? Number(userId) : null;
};

// ======================================
// CLEAR AUTH DATA (LOGOUT)
// ======================================
export const clearAuthData = () => {
  Object.values(AUTH_KEYS).forEach((key) => {
    localStorage.removeItem(key);
  });
};
