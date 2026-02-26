import { useState, useEffect } from 'react';
import { dashboardServices } from '../../../services/apiService';
import { getRoleId } from '../../../services/authService';

let globalCache = null;
let activePromise = null;

// Single API call hook to fetch all dashboard data intelligently
export const useDashboardData = (yearRange = null) => {
  const [data, setData] = useState(globalCache);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      try {
        const roleId = getRoleId();
        const userId = localStorage.getItem('user_id');

        if (!roleId) {
          throw new Error('No role ID found');
        }

        const payload = {
          userId
        };

        // Add year range to payload if provided
        if (yearRange && yearRange.startYear && yearRange.endYear) {
          payload.startYear = yearRange.startYear;
          payload.endYear = yearRange.endYear;
        }

        // Return existing promise if already fetching to prevent duplicate API hits
        if (!activePromise || (yearRange && yearRange.startYear)) {
          activePromise = dashboardServices.getDashboardData(roleId, payload);
        }

        const response = await activePromise;
        globalCache = response.data;
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
  }, [yearRange?.startYear, yearRange?.endYear]); // Track explicit changes in range

  return { data, loading, error };
};

// Individual hooks that use single data source
export const useDashboardCards = (yearRange = null) => {
  const { data, loading, error } = useDashboardData(yearRange);
  return {
    cards: data?.cards || [],
    loading,
    error
  };
};

export const useDashboardPieCharts = (yearRange = null) => {
  const { data, loading, error } = useDashboardData(yearRange);
  return {
    pieCharts: data?.pieCharts || [],
    loading,
    error
  };
};

export const useDashboardBarCharts = (yearRange = null) => {
  const { data, loading, error } = useDashboardData(yearRange);
  return {
    barCharts: data?.barCharts || [],
    loading,
    error
  };
};

export const useDashboardDataViews = (yearRange = null) => {
  const { data, loading, error } = useDashboardData(yearRange);
  return {
    dataViews: data?.dataViews || [],
    loading,
    error
  };
};

export const useReferralCode = (yearRange = null) => {
  const { data, loading, error } = useDashboardData(yearRange);
  return {
    referralCode: data?.referralCode || null,
    loading,
    error
  };
};

export const useTransactionHistory = (yearRange = null) => {
  const { data, loading, error } = useDashboardData(yearRange);
  return {
    transactionHistory: data?.transactionHistory || [],
    loading,
    error
  };
};
