import React from 'react';
import { formatDateShort } from '../../../utils/dateUtils';

/**
 * Common component for displaying formatted date and time
 * Format: "23 Dec 2025" or "23 Dec 2025"
 */
const DateTimeFormatter = ({ value, className = '' }) => {
    if (!value) return null;

    return (
        <span className={`date-time-display ${className}`}>
            {formatDateShort(value)}
        </span>
    );
};

export default DateTimeFormatter;
