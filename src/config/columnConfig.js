// ======================================
// COLUMN CONFIGURATION SYSTEM
// ======================================

// Column types for different data formats
export const COLUMN_TYPES = {
  TEXT: 'text',
  NUMBER: 'integer',
  FLOAT: 'float',
  DATE: 'date',
  DATETIME: 'datetime',
  BOOLEAN: 'boolean',
  AMOUNT: 'amount',
  EMAIL: 'email',
  PHONE: 'phone',
  STATUS: 'status',
  ACTIONS: 'actions',
};

// ======================================
// LICENSE BATCH COLUMNS
// ======================================
export const LICENSE_BATCH_COLUMNS = [
  { accessorKey: 'createdAt', header: 'Date', type: 'datetime', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'planName', header: 'Plan', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'totalActivations', header: 'Batch Count', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'usedActivations', header: 'Issued Count', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'remainingActivations', header: 'Remaining Batches', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'costPrice', header: 'Cost Price', type: 'amount', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'status', header: 'Status', type: 'status', sortable: true, filterable: true, width: 100 },
];

// ======================================
// VENDOR BATCH APPROVALS COLUMNS
// ======================================
export const VENDOR_BATCH_APPROVALS_COLUMNS = [
  { accessorKey: 'id', header: 'Batch ID', type: 'integer', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'vendorName', header: 'Vendor', type: 'text', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'planName', header: 'Plan', type: 'text', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'totalActivations', header: 'Quantity', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'usedActivations', header: 'Issued Count', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'remainingActivations', header: 'Remaining Batches', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'costPrice', header: 'Amount', type: 'amount', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'paymentMode', header: 'Payment Mode', type: 'text', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'status', header: 'Status', type: 'status', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'createdAt', header: 'Date', type: 'datetime', sortable: true, filterable: true, width: 150 },
];

// ======================================
// LICENSE KEYS COLUMNS
// ======================================
export const LICENSE_KEYS_COLUMNS = [
  { accessorKey: 'plainCodeLast4', header: 'Keys', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'issuedToEmail', header: 'Tenent Mail', type: 'email', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'issuedToPhone', header: 'Tenent Phone', type: 'phone', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'status', header: 'Status', type: 'status', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'expiresAt', header: 'Expiry Date', type: 'datetime', sortable: true, filterable: true, width: 150 },
];

// ======================================
// VENDOR DISCOUNT COLUMNS
// ======================================
export const VENDOR_DISCOUNT_COLUMNS = [
  { accessorKey: 'id', header: 'ID', type: 'integer', sortable: true, filterable: true, width: 80 },
  { accessorKey: 'name', header: 'Discount Name', type: 'text', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'type', header: 'Type', type: 'text', sortable: true, filterable: true, width: 120 },
  { 
    accessorKey: 'value', 
    header: 'Value', 
    type: 'text', 
    sortable: true, 
    filterable: true, 
    width: 120,
    Cell: ({ row }) => {
      const type = row.original.type;
      const value = row.original.value;
      
      if (type === 'PERCENTAGE') {
        return `${value}%`;
      } else {
        return `₹${parseFloat(value).toFixed(2)}`;
      }
    }
  },
  { accessorKey: 'effectiveDate', header: 'Effective Date', type: 'date', sortable: true, filterable: true, width: 150 },
];

// ======================================
// REFERRAL PROGRAM COLUMNS
// ======================================
export const REFERRAL_PROGRAM_COLUMNS = [
  { accessorKey: 'id', header: 'ID', type: 'integer', sortable: true, filterable: true, width: 80 },
  { accessorKey: 'code', header: 'Referral Code', type: 'text', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'name', header: 'Referral Name', type: 'text', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'rewardType', header: 'Reward Type', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'rewardValue', header: 'Reward Value', type: 'amount', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'rewardPercentage', header: 'Reward %', type: 'float', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'maxPerReferrer', header: 'Max Per Referrer', type: 'integer', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'ownerType', header: 'Owner Type', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'status', header: 'Status', type: 'text', sortable: true, filterable: true, width: 100 },
];

// ======================================
// AUDIT LOGS COLUMNS
// ======================================
export const AUDIT_LOGS_COLUMNS = [
  { accessorKey: 'timestamp', header: 'Timestamp', type: 'datetime', sortable: true, filterable: true, width: 180 },
  { accessorKey: 'action', header: 'Action', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'performedBy', header: 'Performed By', type: 'text', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'module', header: 'Module', type: 'text', sortable: true, filterable: true, width: 150 },

];

// ======================================
// OFFER MANAGEMENT COLUMNS
// ======================================
export const OFFER_MANAGEMENT_COLUMNS = [
  { accessorKey: 'id', header: 'ID', type: 'integer', sortable: true, filterable: true, width: 80 },
  { accessorKey: 'code', header: 'Offer Code', type: 'text', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'discountType', header: 'Discount Type', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'discountValue', header: 'Discount Value', type: 'amount', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'discountPercentage', header: 'Discount %', type: 'float', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'currency', header: 'Currency', type: 'text', sortable: true, filterable: true, width: 80 },
  { accessorKey: 'validFrom', header: 'Valid From', type: 'date', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'validTo', header: 'Valid To', type: 'date', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'maxUses', header: 'Max Uses', type: 'integer', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'status', header: 'Status', type: 'text', sortable: true, filterable: true, width: 100 },
];

// ======================================
// REDEMPTION COLUMN
// ======================================
export const REDEMPTION_COLUMNS = [
  { accessorKey: 'referredTenantId', header: 'Tenant ID', type: 'integer', sortable: true, filterable: true, width: 20 },
  { accessorKey: 'referredTenantName', header: 'Referred Tenant Name', type: 'text', sortable: true, filterable: true, width: 220 },
  { accessorKey: 'rewardedAmount', header: 'Reward Amount', type: 'amount', sortable: true, filterable: true, width: 10 },
  { accessorKey: 'status', header: 'Status', type: 'status', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'createdAt', header: 'Created At', type: 'date', sortable: true, filterable: true, width: 150 },
];

// ======================================
// CA REDEMPTION APPROVALS COLUMNS
// ======================================
export const CA_REDEMPTION_APPROVALS_COLUMNS = [
  { accessorKey: 'id', header: 'Redemption ID', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'caName', header: 'CA Name', type: 'text', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'referrals', header: 'Referrals', type: 'integer', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'amount', header: 'Amount', type: 'amount', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'bankDetails', header: 'Bank Details', type: 'text', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'status', header: 'Status', type: 'status', sortable: true, filterable: true, width: 100 },
  { accessorKey: 'createdAt', header: 'Date', type: 'datetime', sortable: true, filterable: true, width: 150 },
];

// ======================================
// TENANT CA MANAGEMENT COLUMNS
// ======================================
export const TENANT_CA_MANAGEMENT_COLUMNS = [
  { accessorKey: 'name', header: 'Name', type: 'text', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'email', header: 'Email', type: 'email', sortable: true, filterable: true, width: 220 },
  { accessorKey: 'phone', header: 'Phone', type: 'phone', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'isView', header: 'Status', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'createdAt', header: 'Created At', type: 'datetime', sortable: true, filterable: true, width: 180 },
];

// ======================================
// CA TENANT REQUESTS COLUMNS
// ======================================
export const CA_TENANT_REQUESTS_COLUMNS = [
  { accessorKey: 'name', header: 'Name', type: 'text', sortable: true, filterable: true, width: 200 },
  { accessorKey: 'email', header: 'Email', type: 'email', sortable: true, filterable: true, width: 220 },
  { accessorKey: 'phone', header: 'Phone', type: 'phone', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'isView', header: 'Status', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'requestedAt', header: 'Requested At', type: 'datetime', sortable: true, filterable: true, width: 180 },
];

// ======================================
// USERS COLUMNS
// ======================================
export const USERS_COLUMNS = [
  { accessorKey: 'id', header: 'ID', type: 'integer', sortable: true, filterable: true, width: 80 },
  { accessorKey: 'name', header: 'Name', type: 'text', sortable: true, filterable: true, width: 220 },
  { accessorKey: 'email', header: 'Email', type: 'email', sortable: true, filterable: true, width: 220 },
  { accessorKey: 'phone', header: 'Phone', type: 'phone', sortable: true, filterable: true, width: 140 },
  { accessorKey: 'roleName', header: 'Role', type: 'text', sortable: true, filterable: true, width: 150 }, // Add roleName column
  { accessorKey: 'active', header: 'Active', type: 'boolean', sortable: true, filterable: true, width: 100 },
];

// ======================================
// TENANT PLAN USAGE COLUMNS
// ======================================
export const TENANT_PLAN_USAGE_COLUMNS = [
  { accessorKey: 'planCode', header: 'Code', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'planName', header: 'Plan Name', type: 'text', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'planExpiry', header: 'Expiry Date', type: 'datetime', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'activeUsers', header: 'Users', type: 'integer', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'activeCompanies', header: 'company', type: 'integer', sortable: true, filterable: true, width: 120 },
];
// ======================================
// TENANT PLAN USAGE COLUMNS
// ======================================
export const TANENT_PLAN = [
  { accessorKey: 'name', header: 'Name', type: 'text', sortable: true, filterable: true, width: 120 },
  { accessorKey: 'email', header: 'Email', type: 'text', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'phone', header: 'Phone', type: 'number', sortable: true, filterable: true, width: 150 },
  { accessorKey: 'status', header: 'Status', type: 'text', sortable: true, filterable: true, width: 120 },
  
];
//=======================================
// USERS COLUMNS
// ======================================
export const TANENT_COLUMNS = [
  { accessorKey: 'name', header: 'Name', type: 'text', sortable: true, filterable: true, width: 220 },
  { accessorKey: 'email', header: 'Email', type: 'email', sortable: true, filterable: true, width: 220 },
  { accessorKey: 'phone', header: 'Phone', type: 'phone', sortable: true, filterable: true, width: 140 },
  { accessorKey: 'tenantStatus', header: 'Tenant Status', type: 'text', sortable: false, filterable: true, width: 140 },
  { accessorKey: 'isActive', header: 'Active', type: 'boolean', sortable: true, accessorFn: (row) => row.tenantUserActive, width: 100 },
];
