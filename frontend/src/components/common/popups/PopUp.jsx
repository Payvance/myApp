// src/components/common/popups/PopUp.jsx

import React from 'react';
import './PopUp.css';

/**
 * Reusable Popup Component
 *
 * Props:
 * - isOpen (boolean): Controls popup visibility
 * - onClose (function): Callback fired when popup closes
 * - children (node): Popup content/body
 * - title (string): Optional popup header title
 * - subtitle (string): Optional popup header subtitle
 * - showCloseButton (boolean): Toggles header close button
 * - closeOnBackdropClick (boolean): Enables closing when clicking outside popup
 * - size (string): Size variant - 'small' | 'medium' | 'large'
 */
const PopUp = ({ 
  isOpen, 
  onClose, 
  children,
  title,
  subtitle,
  showCloseButton = true,
  closeOnBackdropClick = true,
  size = 'medium' // default size
}) => {

  // If popup is closed, don't render anything
  if (!isOpen) return null;

  /**
   * Handles click events on the overlay
   * Ensures popup only closes when clicking the backdrop,
   * NOT when clicking inside the popup content area
   */
  const handleBackdropClick = (e) => {
    if (closeOnBackdropClick && e.target === e.currentTarget) {
      onClose();
    }
  };

  return (
    // Overlay background wrapper
    <div className="popup-overlay" onClick={handleBackdropClick}>

      {/* Popup container with dynamic size class */}
      <div className={`popup-container popup-${size}`}>

        {/* Optional Header Section */}
        {title && (
          <div className="popup-header">

            {/* Title and Subtitle Container */}
            <div className="popup-title-container">
              {/* Title Text */}
              <h3 className="popup-title">{title}</h3>
              
              {/* Subtitle Text (optional) */}
              {subtitle && (
                <p className="popup-subtitle">{subtitle}</p>
              )}
            </div>
            
            {/* Close Button (optional) */}
            {showCloseButton && (
              <button 
                className="popup-close-btn" 
                onClick={onClose}
                aria-label="Close popup" // accessibility support
              >
                ×
              </button>
            )}
          </div>
          // <POPUP-HEADER/>
        )}
        
        {/* Popup Content Area */}
        <div className="popup-content">
          {children}
        </div>
      {/*  popup-content*/}
      </div> 
      {/*  popup-overlay*/}
    </div>
    
  );
};

export default PopUp;
