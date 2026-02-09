export const API_ENDPOINTS = {
  BASE_URL: import.meta.env.VITE_API_BASE_URL,


  EXTERNAL: {
    POSTAL_PINCODE: (pincode) => `https://api.postalpincode.in/pincode/${pincode}`,
  },
  AUTH: {
    LOGIN: "/api/auth/login",
    ROLE: "/api/roles/dropdown",
    SIGNUP: "/api/tenants/signup",
    OTPSEND: "/api/otp/send",
    OTPVERIFY: "/api/otp/verify",
    EMAIL_OTP_SEND: "/api/email/otp/send",
    EMAIL_OTP_VERIFY: "/api/email/otp/verify",
    RESETPASSWORD: (email, newPassword) =>
  `/api/users/reset-password?email=${email}&newPassword=${newPassword}`,
    CHECK_PHONE: "/api/users/phone-exists",
    LOGOUT: "/api/auth/logout"
  },

  
  PROFILE: {
    CREATE: "/api/profile/upsert",  
  },
  
  // VENDOR LICENSE ENDPOINTS
  VENDOR_LICENSES: {
    BATCHES: "/api/vendor/licenses/batches",
    BATCH_DETAIL: (id) => `/api/vendor/licenses/batches/${id}`,
    // sending batch status
    BATCH_STATUS: "/api/vendor/licenses/batches/status",
    KEYS: "/api/vendor/licenses/keys",
    ISSUE_KEY: (id) => `/api/vendor/licenses/keys/${id}/issue`,
  // --------------------------------
  // ACTIVE PLANS
  // --------------------------------
  ACTIVE_PLANS: "/api/vendor/licenses/active/plans",

  // --------------------------------
  // PLAN PRICE
  // --------------------------------
  PLAN_PRICE: "/api/vendor/licenses/plan/price",

  // --------------------------------
  // VENDOR DISCOUNT
  // --------------------------------
VENDOR_DISCOUNT: (userId) =>
  `/api/vendor/licenses/vendor/${userId}/discount`,

  // --------------------------------
  // GST
  // --------------------------------

    KEYS: "/api/vendor/licenses/keys",
    CHECK_ELIGIBILITY: "/api/vendor/licenses/check",
    ISSUE_LICENSE: "/api/vendor/licenses/issue",
    ISSUE_KEY: (id) => `/api/vendor/licenses/keys/${id}/issue`,
    ACTIVATION_KEY_DETAIL: (id) => `/api/activation-keys/${id}`,
    ACTIVE_PLANS: "/api/vendor/licenses/active/plans", 
    PLAN_PRICE: "/api/vendor/licenses/plan/price",
    //  ADD THIS
    VENDOR_DISCOUNT: (userId) =>
      `/api/vendor/licenses/vendor/${userId}/discount`,

    // ADD THIS
    GST: {
      LATEST_RATE: "/api/gst/rate/latest",
    },
    ISSUE_KEY: (vendorBatchId) => `/api/vendor/licenses/keys/${vendorBatchId}`,
  },

  GST :{
    LATEST_RATE: "/api/gst/rate/latest",
  },
  
  // ADMIN ENDPOINTS
  PLANS: {
    CREATE: "/api/admin/plans",
    LIST: "/api/admin/plans",       
    GET_BY_ID: (id) => `/api/admin/plans/${id}` 
  },

  // ADD-ON ENDPOINTS
  ADDONS: {
    CREATE: "/api/admin/addons",
    LIST: "/api/admin/addons",
    GET_BY_ID: (id) => `/api/admin/addons/${id}`,
    GET_BY_PLAN: "/api/admin/addons/by-plan"
  },

  // VENDOR DISCOUNT ENDPOINTS
  VENDOR_DISCOUNT: {
    UPSERT: "/api/vendor-discount/upsert",
    LIST: "/api/vendor-discount/all",
    GETBYID: "/api/vendor-discount/get",
    UPDATE: "api/vendor-discount/upsert"
  },

  // REFERRAL PROGRAM ENDPOINTS
  REFERRAL_PROGRAM: {
    UPSERT: "/api/super-admin/referral-programs",
    LIST: "/api/super-admin/referral-programs"
  },

  // COUPON / OFFER ENDPOINTS
  COUPONS: {
    UPSERT: "/api/super-admin/coupons",
    LIST: "/api/super-admin/coupons",
    GETOFFRMANAGMENTBYID : "/api/super-admin/coupons/get"
  },

   CA: {
    REDEMPTION: "/api/ca/referrals/redemptions",
    REDEMPTION_PENDING: "/api/ca/referrals/redemptions",
  },

AUDIT: {	 
 	LOGS: "/api/audit/logs",	 
 	LOG_BY_ID: (id) => `/api/audit/logs/${id}`,	 
 	},
  USERS: {
    PAGINATION: "/api/users/pagination",
    INACTIVE_PAGINATION: "/api/users/inactive/pagination",
    UPDATE: (id) => `/api/users/${id}/full-details`,
    FULL_DETAILS: (id) => `/api/users/${id}/full-details`,
    GETUSERDATA: (id) => `/api/users/${id}`,
  },

  Tanent: {
    TANENTDATA: (id) => `/api/tenants/${id}`,
    ACTIVE_ACTIVE_PLANS: "api/tenants/start-trial",
    PLAN_USAGE: "/api/tenant/plan-usage",
    TANENT_USER: "/api/users/tenant-users",
    UPDATE_TANENT_USER: "/api/tenant-users/status",
    FETCH_TANENT_USER_BY_ID: "/api/users/tenant-users/details",
    GET_PLAN_DETAILS: "/api/tenant-users/usage-roles",
    GET_TRIAL_CONFIG: "/api/tenants/trial-config",
    GET_GST: "/api/gst/rate/latest",
    GET_PERTICULAR_TANENT_INFO: "api/tenants/by-user/tenantdetails"
  },

  RESET_PASSWORD: {
      VERIFT_PASSWORD: "/api/auth/verify-password",
      CHANGE_PASSWORD: "/api/auth/set-password",
  },


  // DASHBOARD ENDPOINTS
  DASHBOARD: {
    BASE: (roleId) => `/api/dashboard/${roleId}`,
  },

};


