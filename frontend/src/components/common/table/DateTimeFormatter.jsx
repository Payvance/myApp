import React from 'react';
import { formatDateTime, formatDateShort } from '../../../utils/dateUtils';

/**
 * Common component for displaying formatted date and time
 * Format: "23 Dec 2025" or "23 Dec 2025, 12:13 AM"
 */
const DateTimeFormatter = ({ value, showTime = true, className = '' }) => {
    if (!value) return null;

    return (
        <span className={`date-time-display ${className}`}>
            {showTime ? formatDateTime(value) : formatDateShort(value)}
        </span>
    );
};

export default DateTimeFormatter;
