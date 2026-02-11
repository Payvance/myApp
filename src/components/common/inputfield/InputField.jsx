import React, { useState, useRef, useEffect } from "react";
import { VALIDATION_PATTERNS } from "../../../config/validateField";
import "./InputField.css";
import { ToastContainer, toast } from "react-toastify";
import { parseAmount } from "./parseAmount";
import CustomDatePicker from "./calender/CustomDatePicker";
import { normalizeDate } from "../../../utils/dateUtils";
import "./calender/DatePickerFix.css";

// Format short numbers (Cr, L, K)
const formatShortAmount = (amount) => {
  if (amount >= 10000000) {
    return (amount / 10000000).toFixed(1) + "Cr";
  } else if (amount >= 100000) {
    return (amount / 100000).toFixed(1) + "L";
  } else if (amount >= 1000) {
    return (amount / 1000).toFixed(1) + "K";
  }
  return amount;
};

const InputField = ({
  onChange,
  onBlur, // Add onBlur prop
  label,
  required,
  type,
  min,
  max,
  value = "",
  name,
  disabled,
  validationType = "EVERYTHING", // Default to EVERYTHING if not provided
  inputValue,
  onFocus,
  customValidation,
  onPaste,
  classN, // ✅ NEW added for adjusting medium type inputfield
  validationErrors = {},
  setValidationErrors = () => {},
}) => {
  const [displayValue, setDisplayValue] = useState(
    value !== null ? value.toString() : ""
  );
  const [rawValue, setRawValue] = useState(value ? parseAmount(value) : 0);
  const [error, setError] = useState("");
  const inputRef = useRef(null); // Used to return focus on error
  const [showDatePicker, setShowDatePicker] = useState(false);
  const datePickerRef = useRef(null);
   const [datePickerPosition, setDatePickerPosition] = useState({ top: 0, left: 0 });

  useEffect(() => {
    setDisplayValue(value !== null ? value.toString() : "");
    setRawValue(parseAmount(value));
  }, [value]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        datePickerRef.current &&
        !datePickerRef.current.contains(event.target) &&
        inputRef.current &&
        !inputRef.current.contains(event.target)
      ) {
        setShowDatePicker(false);
      }
    };

       const handleResize = () => {
      if (showDatePicker) {
        updateDatePickerPosition();
      }
    };

    const handleScroll = () => {
      if (showDatePicker) {
       updateDatePickerPosition();
      }
    };

 
    document.addEventListener("mousedown", handleClickOutside);

    window.addEventListener("resize", handleResize);
    window.addEventListener("scroll", handleScroll, true);

 
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showDatePicker]);

  // Format amount based on the selected currency
  const formatAmount = (amount, currencyCode) => {
    if (!amount) return "";
    return new Intl.NumberFormat("en-IN", {
      style: "currency",
      currency: currencyCode,
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }).format(amount);
  };

    const updateDatePickerPosition = () => {
    if (!inputRef.current) return;
   
    const inputRect = inputRef.current.getBoundingClientRect();
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;
    const datePickerWidth = 300; // Approximate width of the date picker
    const datePickerHeight = 350; // Approximate height of the date picker
   
    // Calculate available space on each side
    const spaceBelow = viewportHeight - inputRect.bottom - 10; // 10px margin
    const spaceAbove = inputRect.top - 10;
    const spaceRight = viewportWidth - inputRect.left - 10;
    const spaceLeft = inputRect.right - 10;
   
    // Determine vertical position
    let top, bottom;
    if (spaceBelow >= datePickerHeight || spaceBelow > spaceAbove) {
      // Position below the input
      top = inputRect.bottom + window.scrollY + 5;
      bottom = 'auto';
    } else {
      // Position above the input
      bottom = viewportHeight - inputRect.top + window.scrollY + 5;
      top = 'auto';
    }
   
    // Determine horizontal position
    let left, right;
    if (spaceRight >= datePickerWidth || spaceRight > spaceLeft) {
      // Position to the right
      left = inputRect.left + window.scrollX;
      right = 'auto';
    } else {
      // Position to the left
      right = viewportWidth - inputRect.right + window.scrollX;
      left = 'auto';
    }
   
    setDatePickerPosition({
      top: top !== 'auto' ? `${top}px` : 'auto',
      bottom: bottom !== 'auto' ? `${bottom}px` : 'auto',
      left: left !== 'auto' ? `${left}px` : 'auto',
      right: right !== 'auto' ? `${right}px` : 'auto',
    });
  };
 
  const handleFocus = (e) => {
    if (onFocus) onFocus(e);
    if ((type === "date" || validationType === "DATE") && !showDatePicker) {
      updateDatePickerPosition();
      setShowDatePicker(true);
    }
  };
 
  // Handle click on the input to toggle date picker
  const handleInputClick = (e) => {
    if (disabled) return;
   
    if (type === "date" || validationType === "DATE") {
      if (!showDatePicker) {
        updateDatePickerPosition();
        setShowDatePicker(true);
      }
    }
  };
 
  // Function to enforce pattern on input change
const enforcePattern = (inputValue, validationType) => {
  if (!validationType || !VALIDATION_PATTERNS[validationType]) return inputValue;

  // ✅ Skip password, pan and amount validation
  if (validationType === "PASSWORD" || validationType === "PAN" || validationType === "AMOUNT" || validationType === "EMAIL") return inputValue;
  

  const { pattern, message } = VALIDATION_PATTERNS[validationType];

  // If input does NOT match the pattern
  if (!pattern.test(inputValue)) {
    toast.error(message || "Invalid input", { toastId: "patternError" });
    return inputValue.replace(/[^A-Za-z0-9\s]/g, ""); // ✅ FILTER
  }

  return inputValue; // Return valid input
};



  // Validation function  UPDATED to include max length and decimal place checks for AMOUNT type
  const validate = (inputValue) => {
    if (!required && !inputValue) {
      return "";
    }
 
    if (required && !inputValue) {
      return label ? `${label} is required` : "This field is required";
    }

    // AMOUNT validation 
    if (validationType === "AMOUNT") {
      if (!/^\d*\.?\d*$/.test(inputValue)) {
        return "Only numbers are allowed";
      }

      const numericValue = parseFloat(inputValue);
      if (isNaN(numericValue)) return "Invalid amount";

      // ADDED: Validate maximum length (13 digits + 2 decimal places)
      const stringValue = numericValue.toString();
      const [wholePart, decimalPart] = stringValue.split(".");

      // Check if whole number part exceeds 13 digits
      if (wholePart.length > 13) {
        return "Amount cannot exceed 13 digits";
      }

      // Check if decimal part exceeds 2 places
      if (decimalPart && decimalPart.length > 2) {
        return "Amount cannot have more than 2 decimal places";
      }

      if (min && numericValue < min) return `Minimum amount is ₹${min}`;
      if (max && numericValue > max) return `Maximum amount is ₹${max}`;

      setRawValue(numericValue);
    } else {
      if (min && inputValue.length < min) {
        return `Minimum ${min} value required`;
      }

 
      if (max && inputValue.length > max) {
        return `Maximum ${max} value allowed`;
      }
 
      if (type === "date" || validationType === "DATE") {
        if (min && inputValue < min) {
          return `Date must be after ${min}`;
        }
 
        if (max && inputValue > max) {
          return `Date must be before ${max}`;
        }
      }
 
      if (validationType && VALIDATION_PATTERNS[validationType]) {

        const { pattern, message } = VALIDATION_PATTERNS[validationType];
        if (!pattern.test(inputValue)) {

          return message;
        }
      }
    }
 
    if (customValidation) {
      const customError = customValidation(inputValue);
      if (customError) return customError;
    }
 
    return "";
  };
 
  // Format number with thousand separators
const formatWithCommas = (value) => {
  // Remove existing commas and non-numeric characters except decimal point
  const numericValue = value.replace(/[^0-9.]/g, '');
  
  if (numericValue === '') return '';
  
  const parts = numericValue.split('.');
  let integerPart = parts[0];
  const decimalPart = parts[1] ? '.' + parts[1] : '';
  
  // Add commas to integer part
  integerPart = integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  
  return integerPart + decimalPart;
};

// Handle input changes with enhanced validation for AMOUNT type
  const handleChange = (e) => {
    let newValue = e.target.value;

     // First, enforce pattern if applicable
  if (validationType && VALIDATION_PATTERNS[validationType]) {
    newValue = enforcePattern(newValue, validationType);
  }

  // Special handling for AMOUNT fields to add comma formatting
  if (validationType === "AMOUNT") {
    // Format display value with commas
    const formattedValue = formatWithCommas(newValue);
    setDisplayValue(formattedValue);
    
    // Remove commas for processing
    let cleanValue = newValue.replace(/,/g, '');
    
    // Remove any characters that are not digits or decimal point
    cleanValue = cleanValue.replace(/[^0-9.]/g, "");

    // Ensure only one decimal point
    const decimalCount = (cleanValue.match(/\./g) || []).length;
    if (decimalCount > 1) {
      newValue = cleanValue.replace(/\.+$/, "").replace(/(.*\..*)\./, "$1");
    } else {
      newValue = cleanValue;
    }

    // Limit decimal places to 2
    if (newValue.includes(".")) {
      const parts = newValue.split(".");
      if (parts[1].length > 2) {
        newValue = parts[0] + "." + parts[1].substring(0, 2);
      }
    }

    // Limit total digits before decimal to 13
    const numericPart = newValue.split(".")[0].replace(/[^0-9]/g, "");
    if (numericPart.length > 13) {
      newValue = numericPart.substring(0, 13) + (newValue.includes(".") ? "." + newValue.split(".")[1] : "");
    }
    
    // Update formatted display after processing
    const finalFormattedValue = formatWithCommas(newValue);
    setDisplayValue(finalFormattedValue);
  } else {
    setDisplayValue(newValue);

    if (max && newValue.length > max) {
      newValue = newValue.slice(0, max);
    }
      const { pattern } = VALIDATION_PATTERNS[validationType] || {}; // Added || {} for safety
      // If the pattern has a global flag, assume it is already a filtering pattern
      if (validationType === "PAN") {
        // Process each segment separately
        const part1 = newValue.substring(0, 5).replace(/[^A-Za-z]/g, ""); // First 5: letters only
        const part2 = newValue.substring(5, 9).replace(/[^0-9]/g, ""); // Next 4: digits only
        const part3 = newValue.substring(9, 10).replace(/[^A-Za-z]/g, ""); // Last: letter only
        newValue = part1 + part2 + part3;
      
      } else if (pattern.global) {
        newValue = newValue.replace(pattern, "");
      } else {
        // Try to extract allowed characters from patterns defined as /^[allowed_chars]*$/
        const match = pattern.source.match(/^\^\[([^\]]+)\]\*\$$/);
        if (match && match[1]) {
          const allowed = match[1];
          // eslint-disable-next-line security/detect-non-literal-regexp
          const filterRegex = new RegExp(`[^${allowed}]`, "g");
          newValue = newValue.replace(filterRegex, "");
        }
      }
    }
 
    const newError = validate(newValue);
    setError(newError); // Update local error
    if (setValidationErrors) {
      setValidationErrors((prev) => ({
        ...prev,
        [name]: newError || "",
      }));
    }
    
    if (validationType === "AMOUNT") {
      const numericValue = parseAmount(newValue);
      setRawValue(numericValue);
      onChange({ target: { name, value: newValue, error: "" } });
    } else {
      setDisplayValue(newValue);
      onChange({ target: { name, value: newValue, error: "" } });
    }
  };

  const handleDateSelect = (date) => {
    const newError = validate(date);

    setError(newError);

    if (setValidationErrors) {
      setValidationErrors((prev) => ({
        ...prev,

        [name]: newError || "",
      }));
    }

    setDisplayValue(date);

    onChange({ target: { name, value: date, error: "" } });

    setShowDatePicker(false);
  };

  // Handle blur event to validate and format amount if needed
  const handleBlur = (e) => {
  const newValue = e.target.value;
  const errorMessage = validate(newValue);
  setError(errorMessage); // Set local error
  if (setValidationErrors) {
    setValidationErrors((prev) => ({
      ...prev,
      [name]: errorMessage || "",
    }));
  }
  if (errorMessage) {
    setError(errorMessage);
    // Show toast only if it hasn't been shown before
    if (!toast.isActive("errorToast")) {
      toast.error(errorMessage, { toastId: "errorToast" });
    }
    return;
  }
  setError("");
  toast.dismiss("errorToast"); // Remove existing error toast when resolved
  // Format amount when user leaves the input field
  if (validationType === "AMOUNT") {
    const numericValue = parseAmount(newValue);
    
    // ✅ ADDED: Ensure the value doesn't exceed 13 digits with 2 decimal places
    let formattedValue = numericValue.toFixed(2);
    const [wholePart, decimalPart] = formattedValue.split('.');
    
    // If whole part exceeds 13 digits, truncate it
    if (wholePart.length > 13) {
      formattedValue = wholePart.substring(0, 13) + '.' + decimalPart;
      // Re-parse to ensure it's still a valid number
      const truncatedValue = parseFloat(formattedValue);
      formattedValue = truncatedValue.toFixed(2);
    }
    
    setRawValue(parseFloat(formattedValue));
    setDisplayValue(formattedValue);
    if (onBlur) {
      onBlur({
        target: { name, value: formattedValue, error: errorMessage },
      });
    }
  } else {
    if (onBlur) {
      onBlur({ target: { name, value: newValue, error: errorMessage } });
    }
  }
};

  const handleKeyDown = (e) => {
    if (e.key === " " && (type === "password" || validationType === "PASSWORD")) {
    e.preventDefault();
    return; 
  }
    if (e.key === "Tab" && error) {
      e.preventDefault(); // Prevent moving to the next field
      if (!toast.isActive("errorToast")) {
        toast.error(error, { toastId: "errorToast" });
      }
    }
  };

  const inputId = useRef(`input-${name}-${Math.random().toString(36).substr(2, 5)}`);

  return (
     <div
      className={`input-container ${classN || ""} ${
        inputValue === "Pass" ? "wide-input" : ""
      }`}
      style={{
        width:
        // classn large size should be full width 
          classN === "large"
            ? "100%"
            : classN === "medium"
            ? "60%"
            : classN === "small"
            ? "22.2%"
            : "22.2%" // default full width
      }}
    >

      {!error && validationErrors?.[name] && (
        <span className="error-message">{validationErrors[name]}</span>
          )}

          <input
            //autoFocus
            type={type || "text"}
            min={min}
            max={max}
            onChange={handleChange}
            onBlur={onBlur} // Attach onBlur event
            onKeyDown={handleKeyDown}
            onMouseEnter={() => inputRef.current}
            onClick={handleInputClick}
            value={validationType === "AMOUNT" ? displayValue : (type?.toLowerCase() === "date" ? normalizeDate(value) : value)} // checks whether the type is date if the type is date then the coverter will be applied otherwise it will show the normal value 
            name={name}
            onPaste={onPaste}
            onFocus={onFocus}
            required={required}
            disabled={disabled}
            ref={inputRef} // Ensure focus control
            id={inputId.current}
            className="InputField-input-field"
            autoComplete="off"
            placeholder=" " // Required for floating label behavior
          />
          {(type === "date" || validationType === "DATE") && showDatePicker && (
              <div
        ref={datePickerRef}
        style={{
          position: 'fixed',
          top: datePickerPosition.top !== 'auto' ? datePickerPosition.top : 'auto',
          bottom: datePickerPosition.bottom !== 'auto' ? datePickerPosition.bottom : 'auto',
          left: datePickerPosition.left !== 'auto' ? datePickerPosition.left : 'auto',
          right: datePickerPosition.right !== 'auto' ? datePickerPosition.right : 'auto',
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 4px 20px rgba(0, 0, 0, 0.2)',
          zIndex: 9999,
        }}
      >
            <CustomDatePicker
              selectedDate={value ? value : max ? max : min}
              onDateChange={handleDateSelect}
              onClose={() => setShowDatePicker(false)}
              minDate={min}
              maxDate={max}
            />
          </div>
        )}
        <label htmlFor={inputId.current}>
          {label}
          {required && <span className="required">*</span>}
        </label>
      <ToastContainer />
    </div>
  );
};

export default InputField;
