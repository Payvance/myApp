import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRoleId } from '../../services/authService';
import DashboardLayout from './DashboardLayout';
import DashboardCard from './DashboardCard';
import PieChartComponent from './PieChartComponent';
import BarChartComponent from './BarChartComponent';
import DataViewComponent from './DataViewComponent';
import ReferralCode from './ReferralCode';
import TransactionHistory from './TransactionHistory';
import {
  useDashboardCards,
  useDashboardPieCharts,
  useDashboardBarCharts,
  useDashboardDataViews,
  useReferralCode,
  useTransactionHistory
} from './hooks/useDashboardData';
import './CommonDashboard.css';
import { applyHighchartsTheme } from '../../theme/highchartsTheme';
import RecentUsersTable from './hooks/RecentUsersTable';
import DashboardDetailModal from './hooks/DashboardDetailModal';


const CommonDashboard = () => {
  const navigate = useNavigate();

  useEffect(() => {
    applyHighchartsTheme();
  }, []);


  // Get roleId from authService
  const roleId = getRoleId();

  // State for selected year range
  const [yearRange, setYearRange] = useState({
    startYear: new Date().getFullYear() - 1, // Default: 2025
    endYear: new Date().getFullYear() // Default: 2026
  });

  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedDetail, setSelectedDetail] = useState(null);
  const [detailType, setDetailType] = useState(null);

  const handleCardClick = (card) => {
    const roleIdNum = Number(roleId);

    // Comprehensive route map by role
    const routeMatrix = {
      // SuperAdmin (Role 1)
      1: {
        'total_tenants': '/tenantmanagement',
        'total_vendors': '/partners',
        'total_cas': '/partners',
        'vendor_approval_requests': '/users/pending',
        'ca_approval_requests': '/users/pending',
        'vendor_batches_pending_approval': '/licenseinventory',
        'active_plans': '/subscriptionplan',
        'total_revenue_generated': '/audits',
        'guest_users': '/users/pending',
        'sale_vs_renewal': '/audits'
      },
      // Tenant (Role 2)
      2: {
        'total_users': '/usermanagement',
        'active_users': '/usermanagement',
        'inactive_users': '/usermanagement',
        'plan': '/tenantplanss',
        'companies_allowed': '/BuyPlan'
      },
      // Vendor (Role 3)
      4: {
        'total_activations': '/licenseinventory',
        'used_activations': '/licenseinventory',
        'remaining_activations': '/licenseinventory',
        'batch_approval_requests': '/licenseinventory',
        'approved_batches': '/licenseinventory',
        'rejected_batches': '/licenseinventory',
        'total_profit': '/audits'
      },
      // CA (Role 5)
      5: {
        'tenants_referred': '/tenantrequests',
        'wallet_balance': '/redemption',
        'total_earnings': '/redemption',
        'enchashment_requests': '/redemption/pending',
        'total_creditpoints': '/redemption'
      }
    };

    const roleMap = routeMatrix[roleIdNum] || {};
    const targetRoute = roleMap[card.id];

    if (targetRoute) {
      navigate(targetRoute);
    }
  };

  const handleDetailViewClick = (data, type) => {
    setSelectedDetail(data);
    setDetailType(type);
    setDetailModalOpen(true);
  };

  // Hooks now use getRoleId() internally, no need to pass roleId
  const { cards, loading: cardsLoading, error: cardsError } = useDashboardCards(yearRange);
  const { pieCharts, loading: pieLoading, error: pieError } = useDashboardPieCharts(yearRange);
  const { barCharts, loading: barLoading, error: barError } = useDashboardBarCharts(yearRange);
  const { dataViews, loading: dataLoading, error: dataError } = useDashboardDataViews(yearRange);
  const { referralCode, loading: referralLoading, error: referralError } = useReferralCode(yearRange);
  const { transactionHistory, loading: transactionLoading, error: transactionError } = useTransactionHistory(yearRange);

  const isLoading = cardsLoading || pieLoading || barLoading || dataLoading || referralLoading || transactionLoading;
  const hasError = cardsError || pieError || barError || dataError || referralError || transactionError;

  // Handle year range change for bar charts
  const handleYearChange = (range) => {
    setYearRange(range);
    console.log('Year range changed:', range);
    // TODO: Call dashboard API with year range and user ID
  };

  if (hasError) {
    return (
      <DashboardLayout>
        <div className="dashboard-error">
          <div className="dashboard-error__icon">
            <i className="bi bi-exclamation-triangle"></i>
          </div>
          <h3>Dashboard Error</h3>
          <p>{cardsError || pieError || barError || dataError || referralError || transactionError}</p>
        </div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout loading={isLoading}>
      {/* Row 1: Cards Section */}
      <div className="dashboard-section">
        <div className="dashboard-cards-grid">
          {cards.map((card) => (
            <DashboardCard
              key={card.id}
              {...card}
              onCardClick={handleCardClick}
            />
          ))}
        </div>
      </div>

      {/* Row 2: Charts Section */}
      <div className="dashboard-section">
        <div className="dashboard-charts-section">
          <div className="charts-left">
            {pieCharts.map((chart) => (
              <PieChartComponent
                key={chart.id}
                {...chart}
                loading={pieLoading}
              />
            ))}
          </div>
          <div className="charts-right">
            {barCharts.map((chart) => (
              <BarChartComponent
                key={chart.id}
                {...chart}
                loading={barLoading}
                onYearChange={handleYearChange}
              />
            ))}
          </div>
        </div>
      </div>

      {/* Row 3: Data Views Section */}
      <div className="dashboard-section">
        {Number(roleId) === 5 ? (
          <div className="dashboard-ca-section">
            <ReferralCode
              data={referralCode}
              loading={referralLoading}
            />
            <TransactionHistory
              data={transactionHistory}
              loading={transactionLoading}
            />
          </div>
        ) : (
          <div className="dashboard-ca-section">
            {dataViews.map((view) => {
              if (view.id === "recent_users") {
                return (
                  <RecentUsersTable
                    key={view.id}
                    title={view.title}
                    data={view.data}
                    loading={dataLoading}
                  />
                );
              } else if (view.id === "recent_batches") {
                return (
                  <RecentUsersTable
                    key={view.id}
                    title={view.title}
                    data={view.data}
                    type="batches"
                    loading={dataLoading}
                  />
                );
              } else if (view.id === "vendor_batch_tenants") {
                return (
                  <RecentUsersTable
                    key={view.id}
                    title={view.title}
                    data={view.data}
                    type="vendor_batch_tenants"
                    loading={dataLoading}
                    onViewClick={(data) => handleDetailViewClick(data, 'batch')}
                  />
                );
              } else if (view.id === "detailed_tenants" && Number(roleId) === 1) {
                return (
                  <RecentUsersTable
                    key={view.id}
                    title={view.title}
                    data={view.data}
                    type="detailed_tenants"
                    loading={dataLoading}
                    onViewClick={(data) => handleDetailViewClick(data, 'tenant')}
                  />
                );
              } else {
                return (
                  <DataViewComponent
                    key={view.id}
                    {...view}
                    loading={dataLoading}
                    isTenant={Number(roleId) === 2}
                  />
                );
              }
            })}
          </div>
        )}
      </div>
      {detailModalOpen && selectedDetail && (
        <DashboardDetailModal
          show={detailModalOpen}
          data={selectedDetail}
          type={detailType}
          onClose={() => setDetailModalOpen(false)}
        />
      )}
    </DashboardLayout>
  );
};

export default CommonDashboard;
