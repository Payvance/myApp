import React, { useState, useEffect } from 'react';
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
import DashboardModal from './hooks/DashboardModal';


const CommonDashboard = () => {

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

  const [modalOpen, setModalOpen] = useState(false);
  const [selectedCard, setSelectedCard] = useState(null);

  const handleCardClick = (card) => {
    setSelectedCard(card);
    setModalOpen(true);
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
            {dataViews.map((view) => (
              view.id === "recent_users" ? (
                <RecentUsersTable
                  key={view.id}
                  title={view.title}
                  data={view.data}
                  loading={dataLoading}
                />
              ) : (
                <DataViewComponent
                  key={view.id}
                  {...view}
                  loading={dataLoading}
                />
              )
            ))}
          </div>
        )}
      </div>
      {modalOpen && selectedCard && (
        <DashboardModal
          show={modalOpen}
          data={selectedCard}
          onClose={() => setModalOpen(false)}
        />
      )}
    </DashboardLayout>
  );
};

export default CommonDashboard;
