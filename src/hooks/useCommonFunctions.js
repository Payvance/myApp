// ======================================
// COMMON UTILITY FUNCTIONS
// ======================================

/**
 * Capitalizes each word in a text string
 * @param {string} text - The text to capitalize
 * @returns {string} - The capitalized text
 */
export function capitalizeEachWord(text) {
  if (!text) return "";

  return text
    .toString()
    .trim()
    .toLowerCase()
    .split(/\s+/)
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}
