import React, { useState } from 'react';
import './StateToggle.css';

const StateToggle = ({ 
  isOn, 
  onToggle, 
  labelOn, 
  labelOff, 
  disabled = false,
  size = 'medium'
}) => {
  const [isPressed, setIsPressed] = useState(false);

  const handleMouseDown = () => {
    if (!disabled) {
      setIsPressed(true);
    }
  };

  const handleMouseUp = () => {
    setIsPressed(false);
  };

  const handleMouseLeave = () => {
    setIsPressed(false);
  };

  const handleLeftClick = () => {
    if (!disabled && !isOn) {
      onToggle(true);
    }
  };

  const handleRightClick = () => {
    if (!disabled && isOn) {
      onToggle(false);
    }
  };

  return (
    <div className={`state-toggle-container state-toggle-${size}`}>
      <div className="state-toggle-wrapper">
        {/* Left side - Shared Database */}
        <button
          type="button"
          className={`state-toggle-side state-toggle-left ${isOn ? 'state-toggle-active' : 'state-toggle-inactive'} ${disabled ? 'state-toggle-disabled' : ''}`}
          onClick={handleLeftClick}
          onMouseDown={handleMouseDown}
          onMouseUp={handleMouseUp}
          onMouseLeave={handleMouseLeave}
          disabled={disabled}
        >
          <div className="state-toggle-content">
            <div className="state-toggle-icon state-toggle-left-icon">
              {isOn ? '✓' : ''}
            </div>
            <span className="state-toggle-label">
              {labelOn}
            </span>
          </div>
        </button>

        {/* Right side - Separate Database */}
        <button
          type="button"
          className={`state-toggle-side state-toggle-right ${!isOn ? 'state-toggle-active' : 'state-toggle-inactive'} ${disabled ? 'state-toggle-disabled' : ''}`}
          onClick={handleRightClick}
          onMouseDown={handleMouseDown}
          onMouseUp={handleMouseUp}
          onMouseLeave={handleMouseLeave}
          disabled={disabled}
        >
          <div className="state-toggle-content">
            <div className="state-toggle-icon state-toggle-right-icon">
              {!isOn ? '✓' : ''}
            </div>
            <span className="state-toggle-label">
              {labelOff}
            </span>
          </div>
        </button>
      </div>
    </div>
  );
};

export default StateToggle;
