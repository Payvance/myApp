import React, { useId } from "react";
import "./Checkbox.css";

// Resuable checkbox component
const Checkbox = ({ label, checked, onChange, id, disabled = false }) => {
  // Creates a unique id to link label and input properly
  const autoId = useId();
  const checkboxId = id || autoId;

  return (
    <label className={`checkbox ${disabled ? "checkbox-disabled" : ""}`} htmlFor={checkboxId}>
      <input
      // checkbox inputs
        id={checkboxId}
        type="checkbox"
        className="checkbox-input"
        checked={checked}
        onChange={onChange}
        disabled={disabled}
      />
      {/* Custom styled checkbox box (visual only) */}
      <span className="checkbox-box" aria-hidden="true" />
      {/* Label text */}
      <span className="checkbox-label">{label}</span>
    </label>
  );
};

export default Checkbox;
