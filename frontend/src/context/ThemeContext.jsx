import React, { createContext, useContext, useState, useEffect } from 'react';
// Import theme CSS files directly
import '../theme/LightTheme.css';
import '../theme/Darktheme.css';
import '../theme/CustomTheme.css';

// Create the theme context
const ThemeContext = createContext();

// Custom hook to use the theme context
export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

// Theme provider component
export const ThemeProvider = ({ children }) => {
  // Get initial theme from localStorage or default to 'light'
  const [theme, setTheme] = useState(() => {
    const savedTheme = localStorage.getItem('theme');
    return savedTheme || 'light';
  });

  // Set theme function (for direct theme setting)
  const setThemeWithPersistence = (newTheme) => {
    setTheme(newTheme);
    // Save to localStorage
    localStorage.setItem('theme', newTheme);
  };

  // Toggle theme function
  const toggleTheme = () => {
    setTheme(prevTheme => {
      const newTheme = prevTheme === 'light' ? 'dark' : 'light';
      // Save to localStorage
      localStorage.setItem('theme', newTheme);
      return newTheme;
    });
  };

  // Set theme on document element when theme changes
  useEffect(() => {
    const root = document.documentElement;
    
    // Set the data-theme attribute
    if (theme === 'dark') {
      root.setAttribute('data-theme', 'dark');
    } else if (theme === 'custom') {
      root.setAttribute('data-theme', 'custom');
    } else {
      root.removeAttribute('data-theme');
    }
    
    // Update meta theme-color for mobile browsers
    let metaThemeColor = document.querySelector('meta[name="theme-color"]');
    if (!metaThemeColor) {
      metaThemeColor = document.createElement('meta');
      metaThemeColor.name = 'theme-color';
      document.head.appendChild(metaThemeColor);
    }
    
    if (theme === 'dark') {
      metaThemeColor.content = '#111827';
    } else if (theme === 'custom') {
      metaThemeColor.content = '#fefbf8';
    } else {
      metaThemeColor.content = '#ffffff';
    }
    
    // Update body class for additional theme targeting
    if (theme === 'dark') {
      document.body.className = 'dark-mode';
    } else if (theme === 'custom') {
      document.body.className = 'custom-mode';
    } else {
      document.body.className = 'light-mode';
    }
  }, [theme]);

  // Theme context value
  const value = {
    theme,
    setTheme: setThemeWithPersistence,
    toggleTheme,
    isLight: theme === 'light',
    isDark: theme === 'dark',
    isCustom: theme === 'custom',
    isDarkMode: theme === 'dark',
    isLightMode: theme === 'light',
    isCustomMode: theme === 'custom'
  };

  return (
    <ThemeContext.Provider value={value}>
      {children}
    </ThemeContext.Provider>
  );
};

export default ThemeContext;