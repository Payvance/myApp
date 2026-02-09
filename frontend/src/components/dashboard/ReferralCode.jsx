import React, { useState } from 'react';
import './ReferralCode.css';

const ReferralCode = ({ data }) => {
  const [copied, setCopied] = useState(false);
  
  if (!data) return null;

  const { code } = data;

  const handleCopyCode = async () => {
    try {
      await navigator.clipboard.writeText(code);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy:', err);
    }
  };

  return (
    <div className="referral-code-card">
      <div className="referral-code__header">
        <h3 className="referral-code__title">Your Referral Code</h3>
        <div className="referral-code__icon">
          <i className="bi bi-gift"></i>
        </div>
      </div>
      
      <div className="referral-code__content">
        <div className="referral-code__code-display">
          <div className="referral-code__code-label">Share this code</div>
          <div className="referral-code__code-wrapper">
            <span className="referral-code__code">{code}</span>
            <button 
              className={`referral-code__copy-btn ${copied ? 'copied' : ''}`}
              onClick={handleCopyCode}
              title={copied ? 'Copied!' : 'Copy code'}
            >
              {copied ? (
                <i className="bi bi-check-lg"></i>
              ) : (
                <i className="bi bi-clipboard"></i>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReferralCode;
