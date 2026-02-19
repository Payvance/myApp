import axios from "axios";
import { API_ENDPOINTS } from "./api";
import { getAccessToken, getUserId } from "./authService";

// ======================================
// AXIOS INSTANCE
// ======================================
// Public axios instance without token
const publicApi = axios.create({
  baseURL: API_ENDPOINTS.BASE_URL,
  headers: { "Content-Type": "application/json" },
});

// Authenticated axios instance with token
const api = axios.create({
  baseURL: API_ENDPOINTS.BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});
// Helper function for GET requests with query parameters
const postWithParams = (url, params) => publicApi.post(url, null, { params });
// ===============================
// REQUEST INTERCEPTOR – attach token
// ===============================
api.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// ===============================
// RESPONSE INTERCEPTOR – handle auth errors
// ===============================
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle 401 Unauthorized errors
    if (error.response?.status === 401) {
      console.log('Authentication failed - redirecting to login');
      // Clear any existing auth data
      localStorage.removeItem('accessToken');
      localStorage.removeItem('roleId');
      localStorage.removeItem('userId');
      // Redirect to login page
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ======================================
// API SERVICES
// ======================================
export const authServices = {
  login: (email, password) =>
  publicApi.post(API_ENDPOINTS.AUTH.LOGIN, { email, password }),

  getRoles: () => 
  publicApi.get(API_ENDPOINTS.AUTH.ROLE),

  signup: (data) => 
  publicApi.post(API_ENDPOINTS.AUTH.SIGNUP, data),

  otpsend: (mobile) =>
  postWithParams(API_ENDPOINTS.AUTH.OTPSEND, { mobile }),

  otpverify: ({ mobile, otp }) =>
  postWithParams(API_ENDPOINTS.AUTH.OTPVERIFY, { mobile, otp }),

  emailOtpSend: (email) => 
  publicApi.post(API_ENDPOINTS.AUTH.EMAIL_OTP_SEND, { email }),

  emailOtpVerify: (email, otp) => 
  publicApi.post(API_ENDPOINTS.AUTH.EMAIL_OTP_VERIFY, { email, otp }),  

resetPassword: ({ email, newPassword }) =>{
  const urlwithParams = API_ENDPOINTS.AUTH.RESETPASSWORD(email, newPassword);
  return publicApi.post(urlwithParams);
},

  
 checkPhoneExists: (phone) => 
    api.get(API_ENDPOINTS.AUTH.CHECK_PHONE, { params: { phone } }),
  logout: () =>
    api.post(API_ENDPOINTS.AUTH.LOGOUT),
};

export const profileServices = {
  createOrUpdateProfile: (data, roleId) =>
    publicApi.post(API_ENDPOINTS.PROFILE.CREATE, data, {
      headers: {
        ROLE_ID: roleId, // ✅ REQUIRED BY BACKEND
      },
    }),
};

// ======================================
// PLAN SERVICES
// ======================================
export const planServices = {
  createPlan: (data) =>
    api.post(API_ENDPOINTS.PLANS.CREATE, data),

  // fetch all plans
  getAllPlans: () =>
    api.get(API_ENDPOINTS.PLANS.LIST),

  // fetch plan by id
  getPlanById: (id) =>
    api.get(API_ENDPOINTS.PLANS.GET_BY_ID(id)),

  updatePlan: (id, data) =>
    api.put(API_ENDPOINTS.PLANS.GET_BY_ID(id), data),
};

// ======================================
// ADD-ON SERVICES
// ======================================
export const addonServices = {
  // create add-on
  createAddon: (data) =>
    api.post(API_ENDPOINTS.ADDONS.CREATE, data),

  // fetch all add-ons
  getAllAddons: () =>
    api.get(API_ENDPOINTS.ADDONS.LIST),

  // fetch add-on by id
  getAddonById: (id) =>
    api.get(API_ENDPOINTS.ADDONS.GET_BY_ID(id)),

  // update add-on by id
  updateAddon: (id, data) =>
    api.put(API_ENDPOINTS.ADDONS.GET_BY_ID(id), data),

  // Get addon by id
  getaddonbyplan: (id) => 
    api.post(API_ENDPOINTS.ADDONS.GET_BY_PLAN, id),

};

// ======================================
// VENDOR DISCOUNT SERVICES
// ======================================
export const vendorDiscountServices = {
  upsertDiscount: (data) =>
    api.post(API_ENDPOINTS.VENDOR_DISCOUNT.UPSERT, data),

  getAllDiscounts: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    } 
    const url = `${API_ENDPOINTS.VENDOR_DISCOUNT.LIST}?${queryParams.toString()}`;
    return api.get(url);
  },

  getById: (id) =>
    api.post(API_ENDPOINTS.VENDOR_DISCOUNT.GETBYID, id),

  updateDiscount: (data) =>
    api.post(API_ENDPOINTS.VENDOR_DISCOUNT.UPDATE, data),
};

// ======================================
// REFERRAL PROGRAM SERVICES
// ======================================
export const referralProgramServices = {
  upsertReferral: (data) =>
    api.post(API_ENDPOINTS.REFERRAL_PROGRAM.UPSERT, data),

  getAllReferrals: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size); 
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    const url = `${API_ENDPOINTS.REFERRAL_PROGRAM.LIST}?${queryParams.toString()}`;
    return api.get(url);
  },
};

// ======================================
// COUPON / OFFER SERVICES
// ======================================
export const couponServices = {
  upsertCoupon: (data) =>
    api.post(API_ENDPOINTS.COUPONS.UPSERT, data),

  getCouponsPagination: (params = {}) => {
  const queryParams = new URLSearchParams();

  if (params.page !== undefined) queryParams.append('page', params.page);
  if (params.size !== undefined) queryParams.append('size', params.size);
  if (params.sortBy && params.sortDir) {
    queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
  }
  if (params.search) queryParams.append('search', params.search);

  const url = `${API_ENDPOINTS.COUPONS.LIST}?${queryParams.toString()}`;
  return api.get(url);
},
  getoffermanagmentbyid: (data) => 
    api.post(API_ENDPOINTS.COUPONS.GETOFFRMANAGMENTBYID, data),
  
};

// ======================================
// USER SERVICES
// ======================================
export const userServices = {
  getUsersPagination: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    if (params.search) queryParams.append('search', params.search);
    if (params.status) queryParams.append('status', params.status);
    const url = `${API_ENDPOINTS.USERS.PAGINATION}?${queryParams.toString()}`;
    return api.get(url);
  },

  // Get inactive/pending users using the endpoint
  getInactiveUsersPagination: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    if (params.search) queryParams.append('search', params.search);
    const url = `${API_ENDPOINTS.USERS.INACTIVE_PAGINATION}?${queryParams.toString()}`;
    return api.get(url);
  },

  //Get Rehject Users using the endpoint
    getRejectedUsers: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    if (params.search) queryParams.append('search', params.search);
    const url = `${API_ENDPOINTS.USERS.REJECTUSERS}?${queryParams.toString()}`;
    return api.get(url);
  },
  // Update user by id 
  updateUser: (id, data) => api.put(API_ENDPOINTS.USERS.UPDATE(id), data),

  // Approve/Reject user
  approveUser: (id, params) => api.post(`/api/users/approve/${id}`, null, { params }),
  // GET user by id for the User Profile page
  getUserById: (id) => api.get(API_ENDPOINTS.USERS.FULL_DETAILS(id)),

  getUserData: (id) => api.get(API_ENDPOINTS.USERS.GETUSERDATA(id)),
};


// ======================================
// VENDOR LICENSE SERVICES
// ======================================
export const vendorLicenseServices = {
  // Search/List license batches with pagination, sorting, and filtering
  getLicenseBatches: (params = {}) => {
    const queryParams = new URLSearchParams();

    // Add query parameters if they exist
    if (params.vendorId) queryParams.append('vendorId', params.vendorId);
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    if (params.status) queryParams.append('status', params.status);
    if (params.planId) queryParams.append('planId', params.planId);
    if (params.startDate) queryParams.append('startDate', params.startDate);
    if (params.endDate) queryParams.append('endDate', params.endDate);

    const url = `${API_ENDPOINTS.VENDOR_LICENSES.BATCHES}?${queryParams.toString()}`;
    return api.get(url);
  },

  subscribePlan: (data) => 
    api.post(API_ENDPOINTS.VENDOR_LICENSES.SUBSCRIPTION, data),

  simulatePayment: (data) => 
    api.post(API_ENDPOINTS.VENDOR_LICENSES.SIMULATE_PAYMENT, data),

  getCouponDiscount: (data) => 
    api.post(API_ENDPOINTS.VENDOR_LICENSES.APPLYCOUPON, data),

  getRefralCode: (data) => 
    api.post(API_ENDPOINTS.VENDOR_LICENSES.APPLYREFRAL, data),

  // Get batch details by ID
  getBatchById: (id) =>
    api.get(API_ENDPOINTS.VENDOR_LICENSES.BATCH_DETAIL(id)),

  // Create new license batch
  createBatch: (data) =>
    api.post(API_ENDPOINTS.VENDOR_LICENSES.BATCHES, data),

  // Update license batch
  updateBatch: (id, data) =>
    api.put(API_ENDPOINTS.VENDOR_LICENSES.BATCH_DETAIL(id), data),

  // Delete license batch
  deleteBatch: (id) =>
    api.delete(API_ENDPOINTS.VENDOR_LICENSES.BATCH_DETAIL(id)),

  // Update batch status
  updateBatchStatus: (batchId, status) =>
    api.put(API_ENDPOINTS.VENDOR_LICENSES.BATCH_STATUS, {
      batchId,
      status,
      userId: localStorage.getItem("user_id"),
    }),
  // Check license eligibility
  checkEligibility: (email, phone) =>
    api.post(API_ENDPOINTS.VENDOR_LICENSES.CHECK_ELIGIBILITY, { 
      email, 
      phone,
      userId: localStorage.getItem("user_id")
    }),

  // Issue license batch
  issueLicenseBatch: (payload) =>
    api.post(API_ENDPOINTS.VENDOR_LICENSES.ISSUE_LICENSE, payload),

  // Get activation key details
  getActivationKeyDetail: (id) =>
    api.get(API_ENDPOINTS.VENDOR_LICENSES.ACTIVATION_KEY_DETAIL(id)),


  // Fetch active plans for vendor
  getActivePlans: () =>
    api.get(API_ENDPOINTS.VENDOR_LICENSES.ACTIVE_PLANS),

  getPlanPrice: (code) =>
    api.get(API_ENDPOINTS.VENDOR_LICENSES.PLAN_PRICE, {
      params: { code },
    }),


  getVendorDiscount: (userId) =>
    api.get(API_ENDPOINTS.VENDOR_LICENSES.VENDOR_DISCOUNT(userId)),

  getLatestGstRate: () =>
    api.get(API_ENDPOINTS.GST.LATEST_RATE),

  // ======================================
  // LICENSE KEYS SERVICES
  // ======================================

  // Get license keys with pagination and filtering
  getLicenseKeys: (params) => {
    const queryParams = new URLSearchParams();

    if (params.vendorBatchId) queryParams.append('vendorBatchId', params.vendorBatchId);
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    if (params.status) queryParams.append('status', params.status);
    if (params.issuedToEmail) queryParams.append('issuedToEmail', params.issuedToEmail);
    if (params.issuedToPhone) queryParams.append('issuedToPhone', params.issuedToPhone);
    if (params.plainCodeLast4) queryParams.append('plainCodeLast4', params.plainCodeLast4);

    const url = `${API_ENDPOINTS.VENDOR_LICENSES.KEYS}?${queryParams.toString()}`;
    return api.get(url);
  },

  // Issue a license key
  issueLicenseKey: (id) =>
    api.post(API_ENDPOINTS.VENDOR_LICENSES.ISSUE_KEY(id)),
};
// ======================================
// AUDIT LOG SERVICES
// ======================================
export const auditServices = {
  getAuditLogs: ({ filter = {}, pageable = {} }) => {
    const params = new URLSearchParams();

    // ---- filter params (MATCH POSTMAN) ----
    if (filter.tenantId) params.append("tenantId", filter.tenantId);
    if (filter.actorType) params.append("actorType", filter.actorType);
    if (filter.action) params.append("action", filter.action);
    if (filter.entityType) params.append("entityType", filter.entityType);
    if (filter.fromDate) params.append("fromDate", filter.fromDate);
    if (filter.toDate) params.append("toDate", filter.toDate);


    // ---- pagination params ----
    if (pageable.page !== undefined) params.append("page", pageable.page);
    if (pageable.size !== undefined) params.append("size", pageable.size);

    if (pageable.sort?.length) {
      pageable.sort.forEach((s) => params.append("sort", s));
    }

    return api.get(`${API_ENDPOINTS.AUDIT.LOGS}?${params.toString()}`);
  },

  getAuditLogById: (id) => {
    return api.get(API_ENDPOINTS.AUDIT.LOG_BY_ID(id));
  },
};


// ======================================
// VENDOR SERVICES
// ======================================
export const vendorServices = {
  getVendorByUserId: (userId) => {
    return api.get(`/api/vendor/${userId}`);
  },
};

// ======================================
// COUPON / OFFER SERVICES
// ======================================
export const caServices = {
  redemption: (params = {}) => {
    const queryParams = new URLSearchParams();

    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }

    // Filters
    if (params.status) queryParams.append('status', params.status);
    if (params.referredTenantName) queryParams.append('referredTenantName', params.referredTenantName);

    const url = `${API_ENDPOINTS.CA.REDEMPTION}?${queryParams.toString()}`;
    return api.get(url);
  },

  redemptionpending: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    if (params.status) queryParams.append('status', params.status);
    if (params.referredTenantName) queryParams.append('referredTenantName', params.referredTenantName);
    const url = `${API_ENDPOINTS.CA.REDEMPTION_PENDING}?${queryParams.toString()}`;
    return api.get(url);
  },

};

export const tenantCaManagementServices = {
  createCA: (payload) => {
    return api.post(API_ENDPOINTS.TENANT_CA_MANAGEMENT.MANAGEMENT_PROCESS, payload);
  },

  getTenantCAManagementPagination: (params = {}) => {
    const queryParams = new URLSearchParams();
    
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    
    if (params.search) queryParams.append('search', params.search);
    
    // Add userId parameter
    if (params.userId) queryParams.append('userId', params.userId);

    const url = `${API_ENDPOINTS.TENANT_CA_MANAGEMENT.PAGINATION}?${queryParams.toString()}`;
    return api.get(url);
  },
};

export const caTenantServices = {
  getTenantsForCa: (params = {}) => {
    const queryParams = new URLSearchParams();
    
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    
    // Add caUserId parameter
    if (params.caUserId) queryParams.append('caUserId', params.caUserId);

    const url = `${API_ENDPOINTS.CA.TENANTS_PAGINATION}?${queryParams.toString()}`;
    return api.get(url);
  },

  updateTenantStatus: (payload) => {
    return api.post(API_ENDPOINTS.CA.UPDATE_STATUS, payload);
  },
};


// ======================================
// TENANT SERVICES
// ======================================
export const tenantServices = {
  getTenantData: (id) => {
    return api.get(API_ENDPOINTS.Tanent.TANENTDATA(id));
  },
  startTrial: (data) => {
    return api.post(API_ENDPOINTS.Tanent.ACTIVE_ACTIVE_PLANS, data);
  },
  // Get inactive/pending users using the endpoint
  getInactiveUsersPagination: (params = {}, tenantId) => {
  const queryParams = new URLSearchParams();

  if (params.page !== undefined) queryParams.append('page', params.page);
  if (params.size !== undefined) queryParams.append('size', params.size);
  if (params.sortBy && params.sortDir) {
    queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
  }
  if (params.search) queryParams.append('search', params.search);

  const url = `${API_ENDPOINTS.Tanent.TANENT_USER}?${queryParams.toString()}`;

  return api.get(url, {
    headers: {
      'X-Tenant-Id': tenantId,
    },
  });
},
  // Update tenant user status
  updateTenantUserStatus: (data, tenantId) => {
    return api.post(API_ENDPOINTS.Tanent.UPDATE_TANENT_USER, data, {
      headers: {
        'X-Tenant-Id': tenantId,
      },
    });
  },
  // Fetch tenant user details by ID
  fetchTenantUserById: (data, tenantId) => {
    return api.post(API_ENDPOINTS.Tanent.FETCH_TANENT_USER_BY_ID, data, {
      headers: {
        'X-Tenant-Id': tenantId,
      },
    });
  },
  // Get plan details for tenant
  getPlanDetails: (tenantId) => {
    return api.get(API_ENDPOINTS.Tanent.GET_PLAN_DETAILS, {
      headers: {
        'X-Tenant-Id': tenantId,
      },
    });
  },
  getPlanUsage: (params = {}) => {
    const queryParams = new URLSearchParams();
    if (params.page !== undefined) queryParams.append('page', params.page);
    if (params.size !== undefined) queryParams.append('size', params.size);
    if (params.sortBy && params.sortDir) {
      queryParams.append('sort', `${params.sortBy},${params.sortDir}`);
    }
    const tenantId = localStorage.getItem("tenant_id");
    const url = `${API_ENDPOINTS.Tanent.PLAN_USAGE}?${queryParams.toString()}`;
    return api.get(url, {
      headers: {
        'X-Tenant-Id': tenantId
      }
    });
},
  getTrialConfig: (tenantId) => {
    return api.get(API_ENDPOINTS.Tanent.GET_TRIAL_CONFIG, {
      headers: {
        'X-Tenant-Id': tenantId,
      },
    });
  },
  getLatestGst : () => {
    return api.get(API_ENDPOINTS.Tanent.GET_GST);
  },

  getPerticularTanentInfo: (userId) =>{
    return api.get(API_ENDPOINTS.Tanent.GET_PERTICULAR_TANENT_INFO,{
      headers: {
        'X-User-Id' : userId,
      }
    })
  }
};

// ======================================
// EXTERNAL SERVICES
// ======================================

export const externalServices = {
  // Fetch postal info by pincode from external API
  getPostalInfoByPincode: (pincode) =>
    axios.get(API_ENDPOINTS.EXTERNAL.POSTAL_PINCODE(pincode)),

};


export const resetPasswordServices = {
  verifyAndResetPassword: (data) =>
    api.post(API_ENDPOINTS.RESET_PASSWORD.VERIFT_PASSWORD, data),
  setNewPassword: (data) =>
    api.post(API_ENDPOINTS.RESET_PASSWORD.CHANGE_PASSWORD, data),
};

// ======================================
// DASHBOARD SERVICES
// ======================================

export const dashboardServices = {
  // Get dashboard data by role ID and user data
  getDashboardData: (roleId, payload) => {
    return api.post(API_ENDPOINTS.DASHBOARD.BASE(roleId), payload);
  },
};

// ======================================
// DEFAULT EXPORT 
// ======================================
export default api;
