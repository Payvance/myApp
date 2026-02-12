package com.payvance.erp_saas.security.permissions;

public final class ApiPermissions {

        private ApiPermissions() {
        }

        /* ===================== PUBLIC ===================== */
        public static final String[] PUBLIC_APIS = {
                        "/api/tenants/signup",
                        "/api/tenants/trial",
                        "/api/roles/dropdown", // to get roles for dropdown
                        "/api/auth/login",
                        "/api/auth/logout",
                        "/api/otp/send",
                        "/api/otp/verify",
                        "/api/auth/verify-email",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/error",
                        "/api/tenants/start-trial",
                        "/api/tenant/logout",
                        "/api/company-config/details/list",
                        "/api/tally/sync-settings",
                        "/api/gst/**",
                        "/api/vendor/licenses/batches",
                        "/api/profile/upsert", // Profile management endpoints
                        "/api/profile/fetch", // Profile management endpoints
                        "/api/vendor/licenses/batches/{id}",
                        "/api/email/otp/**",
                        "/api/users/reset-password",
        };

        /* ===================== SUPER ADMIN ===================== */
        public static final String[] SUPER_ADMIN_APIS = {
                        "/api/admin/**",
                        "/api/audit/logs/**", // Audit logs access for super admin
                        "/api/vendor/licenses/batches/status",
                        // "/api/users/**",
                        "/api/vendor/licenses/vendor/{userId}/discount",
                        "/api/vendor/licenses/plan/price"
        };

        /* ===================== TENANT ADMIN ===================== */
        public static final String[] TENANT_ADMIN_APIS = {
                        "/api/tenants/**",
                        "/api/users/**",
                        "/api/users/tenant-users",
                        "/api/tally/sync-settings/**",
                        "/api/tenant-users/**",
                        "/api/wallet/**",
                        "/api/ca/referrals/redemptions/referral/details",
                        "/api/ca/referrals/redemptions/referral/details",
                        "/api/v1/sync/writeback/job",
                        "/api/v1/sync/writeback/**",
                        "/api/mobile/**"
        };

        /* ===================== TENANT USER ===================== */
        public static final String[] TENANT_USER_APIS = {
                        "/api/tenants/trial",
                        "/api/tenants/{id}",
                        "/api/users/**",
                        "/api/tenant-users/**",
                        "/api/tenant-ca-management/**"

        };

        /* ===================== TALLY ===================== */
        public static final String[] TALLY_APIS = {
                        "/api/tenants/tally/**"

        };

        /* ===================== VENDOR ===================== */
        public static final String[] VENDOR_APIS = {
                        "/api/vendor/**",
                        "/api/activation-keys/**"

        };

        /* ===================== CA ===================== */
        public static final String[] CA_APIS = {
            "/api/ca/**",
            "/api/ca-tenants/**"
        };
}
