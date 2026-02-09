import React from 'react';

// Import the CSS file
import './mainScreenContainer.css';
// MainScreenContainer component common for super admin  layout
const MainScreenContainer = ({ children, className = '' }) => {
  return (
    <div className={`main-screen-container ${className}`}>
      {children}
    </div>
  );
};

export default MainScreenContainer;