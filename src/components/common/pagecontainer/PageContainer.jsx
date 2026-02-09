// In src/components/common/pagecontainer/PageContainer.jsx
import React from 'react';
import './PageContainer.css';

const PageContainer = ({ children, className = '', ...props }) => {
  return (
    // additional props to the div element if we want add anything
    <div className={`page-container ${className}`} {...props}>
      {/* Renders whatever is passed between the component tags of children */}
      {children}
    </div>
  );
};

export default PageContainer;