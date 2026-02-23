import React from 'react';
import './Button.css';

const Button = ({ children, onClick, variant = 'primary', className = '', icon }) => {
    return (
        <button
            className={`common-btn ${variant} ${className}`}
            onClick={onClick}
        >
            {icon && <i className={`bi ${icon}`}></i>}
            {children}
        </button>
    );
};

export default Button;
