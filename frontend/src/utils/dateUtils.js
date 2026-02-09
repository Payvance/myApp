/**
 * Copyright: © 2024 Payvance Innovation Pvt. Ltd.
 * 
 * Organization: Payvance Innovation Pvt. Ltd.
 * 
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *  
**/

/**
 *
 * author            	version     date         change description
 * Gaurav Somji         1.0.0       17/nov/2025   component created
 * 
**/

/**
 * Simple date formatter - converts ISO timestamp to various formats
 */


/**
 * Format date with time in AM/PM format including seconds
 * Format: "14 Nov 2025, 02:22:45 PM"
 * Designed for the LastLogin 
 */
export function formatDateWithTime(input) {
  if (!input) return "Invalid Date";

  const date = new Date(input);
  if (isNaN(date)) return "Invalid Date";

  const day = String(date.getDate()).padStart(2, "0");
  const month = date.toLocaleString("en-US", { month: "short" });
  const year = date.getFullYear();

  const time = date.toLocaleTimeString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: true,
  });

  return `${day} ${month} ${year}, ${time}`;
}



/**
 * Format: "YYYY-MM-DD"
 * For input fields type="date"
 * - Specially designed to return date in the format "yyyy-MM-dd" which can be used by the InputField component.
 */
export function normalizeDate(input) {
  if (!input) return "";

  const d = new Date(input);
  if (isNaN(d)) return "";

  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");

  return `${y}-${m}-${day}`;
}


/**
 * Formats a given date input into a short, human-friendly format (e.g., "05 Jan 2025").
 * - Returns "Invalid Date" for empty, null, or unparseable values.
 * - Uses the local timezone for day and year.
 * - Month is displayed as a short English name (Jan, Feb, Mar, etc.).
 * - Specially Designed to be used for the calender section
 */
export function formatDateShort(input) {
  if (!input) return "Invalid Date";

  const date = new Date(input);
  if (isNaN(date.getTime())) return "Invalid Date";

  const day = String(date.getDate()).padStart(2, "0");
  const month = date.toLocaleString("en-US", { month: "short" });
  const year = date.getFullYear();

  return `${day} ${month} ${year}`;
}

/**
 * Format: "23 Dec 2025, 12:13 AM"
 * Designed for general datetime display
 */
export function formatDateTime(input) {
  if (!input) return "";

  const date = new Date(input);
  if (isNaN(date.getTime())) return "";

  const day = String(date.getDate()).padStart(2, "0");
  const month = date.toLocaleString("en-US", { month: "short" });
  const year = date.getFullYear();

  const time = date.toLocaleTimeString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: true,
  });

  return `${day} ${month} ${year}, ${time}`;
}
