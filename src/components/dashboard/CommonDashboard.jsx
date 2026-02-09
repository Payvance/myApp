import React from 'react';
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

const CommonDashboard = () => {
  // Get roleId from authService
  const roleId = getRoleId();
  
  // Hooks now use getRoleId() internally, no need to pass roleId
  const { cards, loading: cardsLoading, error: cardsError } = useDashboardCards();
  const { pieCharts, loading: pieLoading, error: pieError } = useDashboardPieCharts();
  const { barCharts, loading: barLoading, error: barError } = useDashboardBarCharts();
  const { dataViews, loading: dataLoading, error: dataError } = useDashboardDataViews();
  const { referralCode, loading: referralLoading, error: referralError } = useReferralCode();
  const { transactionHistory, loading: transactionLoading, error: transactionError } = useTransactionHistory();

  const isLoading = cardsLoading || pieLoading || barLoading || dataLoading || referralLoading || transactionLoading;
  const hasError = cardsError || pieError || barError || dataError || referralError || transactionError;

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
            <DashboardCard key={card.id} {...card} />
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
              />
            ))}
          </div>
        </div>
      </div>

      {/* Row 3: Data Views Section */}
      <div className="dashboard-section">
        {roleId === 5 ? (
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
          <div className="dashboard-data-grid">
            {dataViews.map((view) => (
              <DataViewComponent 
                key={view.id} 
                {...view} 
                loading={dataLoading}
              />
            ))}
          </div>
        )}
      </div>
    </DashboardLayout>
  );
};

export default CommonDashboard;
