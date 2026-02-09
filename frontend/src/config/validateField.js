export const VALIDATION_PATTERNS = {
  TEXT_ONLY: {
    pattern: /^[A-Za-z\s]*$/,
    message: "Only alphabets allowed",
  },
  NUMBER_ONLY: {
    pattern: /^[0-9]*$/,
    message: "Only numbers allowed",
  },
    
  FLOAT: {
    pattern: /^\d*\.?\d*$/, // More permissive for typing
    message: "Enter a valid number",
  },

  EMAIL: {
    pattern: /^[a-zA-Z0-9._+-]+@([a-zA-Z0-9-]+\.)+[a-zA-Z]{2,}$/,
    message: "Please enter a valid email address (e.g. user@example.com)",
  },
  
  // Name validation (letters, spaces, and basic punctuation)
  NAME: {
  pattern: /^[a-zA-Z\s\-'\.]*$/,
  message:
    "Name can only contain letters, spaces, hyphens, apostrophes, and periods",
},

  
  // Mobile number validation (10 digits)
 MOBILE: {
  pattern: /^[6-9]?\d{0,9}$/,
  message: "Please enter a valid 10-digit mobile number",
},

  PHONE: {
    pattern: /^[0-9]*$/,
    message: "Invalid phone number",
  },
  PAN: {
    pattern: /^[A-Za-z]{5}[0-9]{4}[A-Za-z]$/,
    message: "Please enter a valid PAN number (e.g., ABCDE1234F)",
  },
  ALPHANUMERIC: {
    pattern: /^[A-Za-z0-9\s]*$/,
    message: "Only letters and numbers allowed",
  },
  ALL_CAPITAL: {
    // only capital letters are allowed
    pattern: /^[A-Z]*$/,
    message: "Only capital letters allowed",
  },
  LOGIN_CODE: {
    pattern: /^[A-Za-z0-9]*$/,
    message: "No special characters or spaces allowed",
  },
  PASSWORD: {
    pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&._\-])[A-Za-z\d@$!%*?&._\-]{8,32}$/,
    message:
      "Minimum 8 characters, at least one uppercase letter, one lowercase letter, one number, one special character and space not allowed",
  },
 DECIMAL: {
  pattern: /^\d*\.?\d{0,2}$/, // Allows empty, digits, optional dot, and up to 2 decimals
  message: "Only numeric values allowed (up to 2 decimal places)",
},

  AMOUNT: {
    pattern: /^\d{1,13}(\.\d{1,2})?$/,  // eslint-disable-line security/detect-unsafe-regex
    message:
      "Invalid amount. Only up to 13 digits before decimal and 2 decimal places allowed",
  },
  PERCENTAGE: {
    pattern: /^(100(\.0{1,2})?|[0-9]{1,2}(\.[0-9]{1,2})?)$/, // eslint-disable-line security/detect-unsafe-regex
    message:
      "Invalid percentage format. Must be between 0 and 100 with up to 2 decimal places",
  },
  ACCOUNT_NUMBER: {
    pattern: /^[0-9]*$/,
    message: "Account number must be between 9 and 20 digits",
  },
  ALPHABETS_AND_SPACE: {
  pattern: /^[A-Za-z\s]*$/,
  message: "Only alphabets and spaces allowed",
  },
  GRADE: {
    pattern: /^[A-F][+-]?$/,
    message:
      "Invalid grade format. Must be A, B, C, D, E, F with optional + or -",
  },
  IFSC_CODE: {
    pattern: /^[A-Z]{4}0[A-Z0-9]{6}$/,
    message: "Please enter a valid IFSC code (e.g., SBIN0001234)",
  },
  TAN: {
    pattern: /^[A-Za-z]{0,4}[0-9]{0,5}[A-Za-z]{0,1}$/,
    message: "Invalid TAN format.(e.g., ABCD12345E)",
  },
  REGISTRATION_NO: {
    pattern: /^[a-zA-Z0-9_/\\-]*$/,
    message: "Only letters, numbers, and _ / \\ - characters allowed",
  },
  EVERYTHING: {
    pattern: /^.*$/,
  },
  DATE: {
    pattern: /^\d{4}-\d{2}-\d{2}$/,
    message: "Invalid date format. Use YYYY-MM-DD",
  },
  DAY: {
    pattern: /^(?:[0-9]|[12][0-9]|3[01])$/,
    message: "Day should be between 0 to 31",
  },
  GST: {
    pattern: /^\d{2}[A-Z]{5}\d{4}[A-Z][A-Z0-9]Z[A-Z0-9]$/,
    message: "Please enter a valid GST number",
  },
  CIN: {
    pattern: /^[LU]\d{5}[A-Z]{2}\d{4}[A-Z]{3}\d{6}$/,
    message: "Please enter a valid CIN number",
  },
  UTR: {
    pattern: /^[A-Z0-9]*$/,
    message: "UTR must contain only uppercase letters and numbers (16 to 22 characters)",
  },
  ALPHA_NUMERIC_ONLY: {
    pattern: /^[A-Z0-9]*$/,
    message: "Only capital alphabets and numbers are allowed",
  },
};

export const validateField = (fieldName, value, required = false) => {
  // If the field is required but empty, return error
  if (required && (!value || value.toString().trim() === "")) {
    return "This field is required";
  }

  // Check if the field has a validation pattern defined
  if (VALIDATION_PATTERNS[fieldName]) {
    const { pattern, message } = VALIDATION_PATTERNS[fieldName];
    if (!pattern.test(value)) {
      return message;
    }
  }

  return ""; // No error
};
