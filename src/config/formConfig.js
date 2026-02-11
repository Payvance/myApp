export const formConfig = {
  signin: {
    email: {
      label: "Email ID",
    },
    password: {
      label: "Password",
    },
    confirmPassword: {
      label: "Confirm Password",
    },
    currentPassword: {
      label: "Current Password",
    },
    fullname: {
      label: "Full Name",
    },
    mobileno: {
      label: "Mobile Number",
    },
    organization: {
      label: "Organization Name",
    },
    role: {
      label: "Role",
    }, 
  },

  caprofile: {
    caRegNo: {
      label: "CA Registration Number",
    }, 
    caNo: {
      label: "CA No.",
    },
    referenceCode: {
      label: "CA Reference Code",
    },
    yearsofenrollment: {
      label: "Years of Enrollment",
    },
    membershipStatus: {
      label: "ICAI Membership Status",
    },
    practiceType: {
      label: "Type of Practice",
    },
    firmName: {
      label: "Firm Name (if applicable)",
    },
    certificateNo: {
      label: "CA Certificate / ICAI Membership Proof No",
    },
    aadhaar: {
      label: "Aadhaar No.",
    },
  },

  vendorprofile: {
    vendorname: {
      label: "Vendor / Business Name",
    },
    yearofexperience: {
      label: "Years of Experience",
    },
    vendortype: {
      label: "Type",
    },
    gstno: {
      label: "GST No.",
    },
    cinno: {
      label: "CIN No.",
    },
    panno: {
      label: "PAN No.",
    },
    tanno: {
      label: "TAN No.",
    },
    assignDiscount: {
      label : "Assigned Discount"
    }
  },

  bankdetails: {
    bankname: {
      label: "Bank Name",
    },
    branchname: {
      label: "Branch Name",
    },
    accountno: {
      label: "Account No.",
    },
    ifsccode: {
      label: "IFSC Code",
    },
  },

  address: {
    houseno: {
      label: "House / Building No.",
    },
    housename: {
      label: "House / Building Name",
    },
    roadareaplace: {
      label: "Road / Area / Place",
    },
    landmark: {
      label: "Landmark",
    },
    country: {
      label: "Country",
    },
    pincode: {
      label: "Pincode",
    },
    city: {
      label: "City",
    },
    village: {
      label: "Village",
    },
    taluka: {
      label: "Taluka",
    },
    district: {
      label: "District",
    },
    state: { 
      label: "State",
    },
    postoffice: {
      label: "Post Office",
    },
  },

  // audit view form config
  auditview: {
    logId: {
      label: "Log ID",
    },
    timestamp: {
      label: "Timestamp",
    },
    action: {
      label: "Action",
    },
    performedBy: {
      label: "Performed By",
    },
    module: {
      label: "Module",
    },
    metadata: {
      label: "Metadata",
    },
    basicInformation: {
      label: "Basic Information",
    },
    noMetadata: {
      label: "No additional metadata available",
    },
  },

  // payment form config
  payment: {
    couponCode: {
      label: "Apply Coupon",
    },
    referralCode: {
      label: "Referral Code",
    },
  },

  // Common lables for Subscription plans.
  subscriptionPlan: {
    allowedCompany: {
      label : "Allowed Companys"
    },
    allowedUsers: {
      label: "Allowed Users"
    },
    planCode: {
      label: "Plan Code",
    },
    planName: {
      label: "Plan Name"
    },    
    planPrice : {
      label : "Plan Price (INR)"
    },   
    periodType : {
      label : "Period Type"
    },   
    periodDuration: {
      label: "Duration "
    }, 
    status : {
      label : "Status"
    }
  },


  addon : {
    selectplan : {
      label : "Select Plan"
    },
    addOnCode : {
      label : "Add-on Code"
    },
    addOnName : {
      label : "Add-on Name"
    },
    periodType : {
      label : "Period Type"
    },
    UnitPrice: {
      label : "Unit Price (INR)"
    }
  },

  venderDiscount: {
    discountName : {
      label : "Discount Name"
    },
    discountType: {
      label: "Discount Type"
    },
    percentage : {
      label : "Percentage"
    },
    value : {
      label : "Value"
    }
  },

  offerManagement: {
    offerCode : {
      label : "Offer Code"
    },
    description : {
      label: "Description"
    },
    validFrom: {
      label: "Valid From"
    },
    validTo: {
      label: "Valid To"
    },
    maxUser: {
      label : "Max Users"
    }
  }
};
export default formConfig;
