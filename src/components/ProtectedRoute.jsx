import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { getAccessToken } from '../services/authService';

const ProtectedRoute = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const checkAuthentication = () => {
      const token = getAccessToken();
      
      // If no token, redirect to login
      if (!token) {
        navigate('/login', { 
          state: { from: location.pathname },
          replace: true 
        });
        return;
      }
    };

    checkAuthentication();
  }, [navigate, location]);

  // Check token on render as well
  const token = getAccessToken();
  if (!token) {
    return null; // Will redirect via useEffect
  }

  return children;
};

export default ProtectedRoute;
