/**
 * Copyright: © 2025 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * author                version        date        change description
 * Neha Tembhe           1.0.0         10/01/2026   Password model creation
 *
 **/
import { useRef, useState } from "react";
import "./PaswordInputBox.css";
import { formConfig } from "../../../config/formConfig.js";

const PaswordInputBox = ({ label, value, onChange, onValidationChange, type = "password", classN = "" ,showCaseInfo}) => {
  const inputRef = useRef(null);
  const [showBox, setShowBox] = useState(false);
  const [boxStyle, setBoxStyle] = useState({});
  const [validation, setValidation] = useState({
    length: false,
    uppercase: false,
    lowercase: false,
    number: false,
    special: false,
  });
   
  const validatePassword = (val) => {
    onChange(val);
    const newState = {
      length: val.length >= 8,
      uppercase: /[A-Z]/.test(val),
      lowercase: /[a-z]/.test(val),
      number: /[0-9]/.test(val),
      special: /[\W_]/.test(val),
    };
    setValidation(newState);

    // Check if ALL criteria are met
    const isValid = Object.values(newState).every(Boolean);
    if (onValidationChange) {
      onValidationChange(isValid);
    }
  };
  
  const handleFocus = () => {
    if (!inputRef.current) return;

    const rect = inputRef.current.getBoundingClientRect();
    const boxWidth = 220;
    const gap = 12;

    let left = rect.right + gap;
    if (left + boxWidth > window.innerWidth) {
      left = rect.left - boxWidth - gap;
    }

    let top = rect.top;
    if (top + 180 > window.innerHeight) {
      top = window.innerHeight - 200;
    }

    setBoxStyle({ position: "fixed", top, left, zIndex: 999999 });
    setShowBox(true);
  };

  return (
    <div className={`password-wrapper ${classN || ""}`} style={classN === "large" ? { width: "100%" } : {}}>
      {showBox && (
        <div className="validation-box" style={boxStyle}>
          <ul>
            <li className={validation.lowercase ? "valid" : ""}>
              {validation.lowercase ? "✔" : "⚪"} Lower-case
            </li>
            <li className={validation.uppercase ? "valid" : ""}>
              {validation.uppercase ? "✔" : "⚪"} Upper-case
            </li>
            <li className={validation.number ? "valid" : ""}>
              {validation.number ? "✔" : "⚪"} Number
            </li>
            <li className={validation.special ? "valid" : ""}>
              {validation.special ? "✔" : "⚪"} Special character
            </li>
            <li className={validation.length ? "valid" : ""}>
              {validation.length ? "✔" : "⚪"} More than 8 characters
            </li>
          </ul>
        </div>
      )}

      {/* EXACT SAME STRUCTURE AS InputField */}
      <div className={`input-container ${classN || ""}`} style={classN === "large" ? { width: "100%" } : {}}>
        <input
          ref={inputRef}
          type={type}
          value={value}
          placeholder=" "             // ✅ REQUIRED
          maxLength={16}
          onChange={(e) => validatePassword(e.target.value.slice(0, 16))}
          onFocus={handleFocus}
          onBlur={() => setTimeout(() => setShowBox(false), 200)}
          autoComplete="new-password"
          onKeyDown={(e) => {
    if (e.key === " ") {
      e.preventDefault();
    }
  }}
        />
        <label>
          {label || formConfig.signin.password.label}
          {showCaseInfo && (
            <span className="info-wrapper">
            <i
              className="bi bi-info-circle ms-2"
              style={{ marginLeft: "5px"}}
            ></i>
            <span className="info-tooltip">
             Password is case sensitive
            </span>
            </span>
          )}
        </label>
      </div>
    </div>
  );
};

export default PaswordInputBox;
