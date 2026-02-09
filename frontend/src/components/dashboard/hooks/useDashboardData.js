import { useState, useEffect } from 'react';
import { dashboardServices } from '../../../services/apiService';
import { getRoleId } from '../../../services/authService';

// Single API call hook to fetch all dashboard data
export const useDashboardData = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        const roleId = getRoleId();
        
        if (!roleId) {
          throw new Error('No role ID found');
        }

        const response = await dashboardServices.getDashboardData(roleId);
        setData(response.data);
        setError(null);
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError(err.message || 'Failed to fetch dashboard data');
        setData(null);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  return { data, loading, error };
};

// Individual hooks that use the single data source
export const useDashboardCards = () => {
  const { data, loading, error } = useDashboardData();
  return { 
    cards: data?.cards || [], 
    loading, 
    error 
  };
};

export const useDashboardPieCharts = () => {
  const { data, loading, error } = useDashboardData();
  return { 
    pieCharts: data?.pieCharts || [], 
    loading, 
    error 
  };
};

export const useDashboardBarCharts = () => {
  const { data, loading, error } = useDashboardData();
  return { 
    barCharts: data?.barCharts || [], 
    loading, 
    error 
  };
};

export const useDashboardDataViews = () => {
  const { data, loading, error } = useDashboardData();
  return { 
    dataViews: data?.dataViews || [], 
    loading, 
    error 
  };
};

export const useReferralCode = () => {
  const { data, loading, error } = useDashboardData();
  return { 
    referralCode: data?.referralCode || null, 
    loading, 
    error 
  };
};

export const useTransactionHistory = () => {
  const { data, loading, error } = useDashboardData();
  return { 
    transactionHistory: data?.transactionHistory || [], 
    loading, 
    error 
  };
};
