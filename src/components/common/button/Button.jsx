import React from "react";
import "./Button.css";
import { useNavigate } from "react-router-dom";

// reusable button component
const Button = ({ text, onClick, type = "button", variant = "primary", disabled = false,isBack = false }) => {
  const navigate = useNavigate();

  // If isBack is true, it uses history logic; otherwise, it uses your onClick prop
  const handleClick = (e) => {
    if (isBack) {
      navigate(-1);
    } else if (onClick) {
      onClick(e);
    }
  };
  return (
    <button
      className={`btn btn-${variant} ${isBack ? "btn-back" : ""}`}
      type={type}
      onClick={handleClick}
      disabled={disabled}
    >
      {isBack ? <i className="bi bi-chevron-left"></i> : text}
    </button>
  );
};

export default Button;
