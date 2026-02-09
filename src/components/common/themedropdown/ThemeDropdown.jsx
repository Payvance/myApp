import React, { useState, useRef, useEffect } from 'react';
import './ThemeDropdown.css';

const ThemeDropdown = ({ isOpen, onClose, onThemeChange }) => {
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen, onClose]);

  const themes = [
    { name: 'Light', icon: <i className="bi bi-sun"></i>, value: 'light' },
    { name: 'Dark', icon: <i className="bi bi-moon"></i>, value: 'dark' },
    { name: 'PayVance', icon: <i className="bi bi-palette"></i>, value: 'custom' }
  ];

  if (!isOpen) return null;

  return (
    <div className="theme-dropdown" ref={dropdownRef}>
      {themes.map((theme) => (
        <button
          key={theme.value}
          className="theme-option"
          onClick={() => {
            onThemeChange(theme.value);
            onClose();
          }}
        >
          <span className="theme-icon">{theme.icon}</span>
          <span className="theme-name">{theme.name}</span>
        </button>
      ))}
    </div>
  );
};

export default ThemeDropdown;
