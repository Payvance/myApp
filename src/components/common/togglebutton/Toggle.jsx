import React from 'react';
import './Toggle.css';

const Toggle = ({ 
  isOn, 
  onToggle, 
  labelOn, 
  labelOff, 
  disabled = false,
  size = 'medium'
}) => {
  return (
    <div className={`toggle-container toggle-${size}`}>
      
      {/* toggle button FIRST */}
      <button
        type="button"
        className={`toggle-button ${isOn ? 'toggle-on' : 'toggle-off'} ${disabled ? 'toggle-disabled' : ''}`}
        onClick={() => !disabled && onToggle(!isOn)}
        disabled={disabled}
      >
        <span className="toggle-thumb" />
      </button>

      {/* label AFTER toggle */}
      <span className="toggle-label">
        {isOn ? labelOn : labelOff}
      </span>

    </div>
  );
};

export default Toggle;
