// src/components/common/pageheader/PageHeader.jsx
import React from 'react';
// page header css
import './PageHeader.css';

// page header component
const PageHeader = ({ title, subtitle, button }) => {
  return (
    // page header div
    <div className="page-header">
      {/* // page header content div */}
      <div className="page-header-content">
        {/* // page header text div */}
        <div className="page-header-text">
          {/* // page header title */}
          <h1 className="page-header-title">{title}</h1>
          {/* // page header subtitle */}
          <p className="page-header-subtitle">{subtitle}</p>
        </div>
        {button && (
          // page header action div
          <div className="page-header-action">
            {button}
          </div>
        )}
      </div>
      {/* // page header action div */}
    </div>
    // page header component end
  );
};

export default PageHeader;
