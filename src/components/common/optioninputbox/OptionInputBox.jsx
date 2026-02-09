/**
 * OptionInputBox Component
 * Props:
 * - label (string): Label text to display above the dropdown.
 * - name (string): Name attribute for identifying the field in form data.
 * - options (array): Array of objects with `code` and `value` properties.
 * - onChange (function): Callback to handle changes in dropdown selection.
 * - classname (string): Custom class for styling.
 * - required (boolean): Marks field as required.
 * - value (string | number): Currently selected value.
 * - disabled (boolean): Disables the dropdown.
 * - size (string): Size modifier (e.g., "small").
 */

import { useState, useRef, useEffect } from "react";
import "./OptionInputBox.css";
import { VALIDATION_PATTERNS } from "../../../config/validateField.js";
import { ToastContainer, toast } from "react-toastify";

const OptionInputBox = ({
  label,
  name,
  placeholder,
  classN,
  onChange,
  onBlur,
  required,
  value = "",
  disabled,
  validationType,
  customValidation,
  setValidationErrors = () => {},
  options = [],
  autoFocus,
}) => {
  const [inputValue, setInputValue] = useState(value);
  const [error, setError] = useState("");
  const inputRef = useRef(null);

  useEffect(() => {
    setInputValue(value);
  }, [value]);

  const validate = (val) => {
    if (!required && !val) return "";
    if (required && !val) {
      return label ? `${label} is required` : "This field is required";
    }
    if (validationType && VALIDATION_PATTERNS[validationType]) {
      const { pattern, message } = VALIDATION_PATTERNS[validationType];
      if (!pattern.test(val)) return message;
    }
    if (customValidation) {
      const customError = customValidation(val);
      if (customError) return customError;
    }
    return "";
  };

  const handleBlur = (e) => {
    const val = e.target.value;
    const errorMessage = validate(val);
    setError(errorMessage);
    setValidationErrors((prev) => ({ ...prev, [name]: errorMessage || "" }));
    if (errorMessage) {
      toast.error(errorMessage, { toastId: `${name}-error` });
      return;
    }
    toast.dismiss(`${name}-error`);
    if (onBlur) onBlur({ target: { name, value: val } });
  };

  return (
    <div
      className={`select-container ${value ? "filled" : ""} ${classN}`}
      style={{
        width:
          classN === "large" ? "100%" : classN === "medium" ? "60%" : "22.2%",
      }}
    >
      {options && options.length > 0 ? (
        <select
          name={name}
          value={inputValue || ""}
          required={required}
          disabled={disabled}
          autoFocus={autoFocus}
          onChange={(e) => {
            setInputValue(e.target.value);
            onChange({ target: { name, value: e.target.value } });
          }}
          onBlur={handleBlur}
        >
          <option value="" disabled hidden>
            {placeholder}
          </option>
          {options.map((option, index) => (
            <option key={index} value={option.code} disabled={option.disabled}>
              {option.value}
            </option>
          ))}
        </select>
      ) : (
        <select
          name={name}
          value={inputValue || ""}
          required={required}
          disabled={disabled}
          onChange={(e) => {
            setInputValue(e.target.value);
            onChange({ target: { name, value: e.target.value } });
          }}
          onBlur={handleBlur}
        >
          <option value="" disabled hidden>
            {" "}
          </option>
        </select>
      )}
      <label>
        {label} {required && <span className="required">*</span>}
      </label>
      
      <ToastContainer />
    </div>
  );
};

export default OptionInputBox;
