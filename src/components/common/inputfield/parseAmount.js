export const parseAmount = (input) => {
  if (!input) return 0;
 
  const sanitizedInput = input.toString().toLowerCase();
 
  // eslint-disable-next-line security/detect-unsafe-regex
  const regex = /(\d+(?:\.\d+)?)(cr|l|k|h)?/g;
 
  let totalAmount = 0;
  let match;
 
  while ((match = regex.exec(sanitizedInput)) !== null) {
    const value = parseFloat(match[1]);
    const suffix = match[2]; // FIXED
 
    if (suffix === "h") totalAmount += value * 100;
    else if (suffix === "k") totalAmount += value * 1000;
    else if (suffix === "l") totalAmount += value * 100000;
    else if (suffix === "cr") totalAmount += value * 10000000;
    else totalAmount += value;
  }
 
  return totalAmount;
};
