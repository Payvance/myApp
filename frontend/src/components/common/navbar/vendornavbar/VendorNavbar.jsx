// src/components/common/navbar/Navbar.jsx
import React, { useState } from 'react';
import { useLocation } from 'react-router-dom';
import ThemeDropdown from '../../themedropdown/ThemeDropdown';
import { useTheme } from '../../../../context/ThemeContext';
import './Navbar.css';

const Navbar = ({ isSidebarCollapsed }) => {
  const location = useLocation();
  const [isThemeDropdownOpen, setIsThemeDropdownOpen] = useState(false);
  const { theme, setTheme, isDark, isCustom } = useTheme();
  
  // Determine page title and menu items based on current path
  const getPageConfig = () => {
    if (location.pathname === '/licenseinventory') {
      return {
        menuItems: ['Index', 'Create Batch']
      };
    }
    return {
      menuItems:[]
    };
  };

  const { menuItems } = getPageConfig();

  const handleThemeChange = (selectedTheme) => {
    console.log('Theme changed to:', selectedTheme);
    // Set the theme using ThemeContext
    setTheme(selectedTheme);
  };

  return (
    <header className={`app-navbar ${isSidebarCollapsed ? 'sidebar-collapsed' : ''}`}>
      {/* Left section */}
      <div className="navbar-left">
        {menuItems.map((item, index) => (
          <button key={index} className="navbar-btn">
            {item}
          </button>
        ))}
      </div>

      {/* Right section */}
      <div className="navbar-right">
        <div className="theme-button-container">
          <button 
            className="navbar-btn theme-btn"
            onClick={() => setIsThemeDropdownOpen(!isThemeDropdownOpen)}
          >
            {isCustom ? (
              <i className="bi bi-palette-fill"></i>
            ) : isDark ? (
              <i className="bi bi-moon-fill"></i>
            ) : (
              <i className="bi bi-sun-fill"></i>
            )}
          </button>
          <ThemeDropdown 
            isOpen={isThemeDropdownOpen}
            onClose={() => setIsThemeDropdownOpen(false)}
            onThemeChange={handleThemeChange}
          />
        </div>
      </div>
    </header>
  );
};

export default Navbar;
